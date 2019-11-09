/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import shiip.server.exception.ConnectionPrefaceException;
import util.CommandLineParser;
import shiip.util.EncoderDecoderWrapper;
import shiip.client.Client;

// used for all message types
import shiip.serialization.*;
import tls.TLSFactory;
import shiip.transmission.MessageReceiver;
import shiip.transmission.MessageSender;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

// used for file, inputstream, fileHandler, and IoException
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// used for Arrays, HashMap, Map, and Objects
import java.util.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.Logger;

import static util.ErrorCodes.*;

// all strings needed for the server
import static shiip.util.ServerStrings.*;

/**
 * Shiip Server
 * @author Ian Laird
 */
public class Server extends Thread{

    // public constants as defined in the handout ****************************

    // the max data size of the server
    public static final int MAXDATASIZE = 100;

    // the min data interval of the server
    public static final int MINDATAINTERVAL = 5;

    // private constants ****************************************************

    // the number of arguments for the shiip server
    private static final int SERVER_ARG_COUNT = 3;

    // the position of the port number in the arg pos
    private static final int SERVER_ARG_PORT_POS = 0;

    // the position of the thread num in the arg pos
    private static final int SERVER_ARG_THREAD_NUM_POS = 1;

    // the position of the document root in the args
    private static final int SERVER_ARG_DOC_ROOT_POS = 2;

    // the wait if the client is inactive
    private static final int CLIENT_INACTIVE_TIMEOUT = 1000 * 20;

    // MISC *****************************************************************

    // used for logging the server
    private static final Logger logger = Logger.getLogger("SHiip Server");

    // the charset that is being used
    private static final Charset ENC = StandardCharsets.US_ASCII;

    // the base directory
    private static File directory_base = null;

    // instance variables ***************************************************

    // uses TLS to communicate to the client
    private Socket socket = null;

    // used to receive messages
    private final MessageReceiver messageReceiver;

    // used to send messages
    private final MessageSender messageSender;

    // the input stream from the remote socket
    private InputStream in = null;

    // maps a streamId to its corresponding ServerStream object
    private Map<Integer, ServerStream> streams = null;

    // the last encountered stream id in this server
    private int lastEncounteredStreamId = 0;

    /**
     * main method of the server
     * @param args
     *     0: port
     *     1: number of threads
     *     2: document root
     */
    public static void main(String [] args){
        if(args.length != SERVER_ARG_COUNT){
            System.err.println("Usage: <port> <threadNum> <doc root>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        // make sure that the public constants are valid numbers
        CommandLineParser.ensureServerConstants(MAXDATASIZE, MINDATAINTERVAL);

        // setup the logger
        util.LoggerConfig.setupLogger(logger, "connections.log");

        int port = CommandLineParser.getPort(args[SERVER_ARG_PORT_POS]);
        int threadNum = CommandLineParser.getThreadNum(args[SERVER_ARG_THREAD_NUM_POS]);

        directory_base = CommandLineParser.getDirectoryBase(args[SERVER_ARG_DOC_ROOT_POS]);

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        ServerSocket ss = CommandLineParser.openTLSServerSocket(logger, port, "mykeystore", "secret");

        // run forever accepting connections to the server
        while(true){
            Socket conn = null;
            try{
                conn = TLSFactory.getServerConnectedSocket(ss);
                Server s = new Server(conn, EncoderDecoderWrapper.getDecoder(),
                        EncoderDecoderWrapper.getEncoder());

                // run this task in the thread pool
                executorService.submit(s);
            }catch(IOException e){
                logger.severe("Unable to create Socket");
            }
        }
    }

    /**
     * sees if a stream id of a headers received from a client is valid
     * @param streamId the stream id to test
     * @return TRUE means that it is a valid stream id (positive and odd and bigger than the last one
     */
    private boolean testValidStreamId(int streamId){
        return streamId >= this.lastEncounteredStreamId && ((streamId % 2) == 1);
    }

    /**
     * constructor
     * @param socket the socket to be associated with the connection
     */
    public Server(Socket socket, Decoder decoder, Encoder encoder) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
        this.messageReceiver = new MessageReceiver(socket.getInputStream(), decoder);
        this.messageSender = new MessageSender(socket.getOutputStream(), encoder);
        this.streams = new HashMap<>();
    }

    /**
     * each thread has this method called first
     */
    public void run(){
        try{
            this.setupConnection();
        }catch(IOException | BadAttributeException e){
            logger.severe("Unable to establish the session: " + e.getMessage());
            return;
        }catch(ConnectionPrefaceException e2){
            logger.severe(CONNECTION_PREFACE_ERROR + e2.getReceivedString());
            return;
        }
        try {
            while (true) {
                Message m = null;
                this.socket.setSoTimeout(CLIENT_INACTIVE_TIMEOUT);
                try {
                    m = this.messageReceiver.receiveMessage();
                    ServerMessageHandler.handleMessage(true, logger, m, streams, directory_base, messageSender, lastEncounteredStreamId);
                }catch(BadAttributeException e){
                    logger.info(INVALID_MESSAGE + e.getMessage());
                }catch(EOFException e2){

                    // means that the client closed the stream
                    this.removeFinishedStreams();
                    if(this.streams.isEmpty()){
                        break;
                    }
                }catch(IllegalArgumentException e4){
                    logger.info(UNABLE_TO_PARSE + e4.getMessage());
                }
                catch(SocketTimeoutException e3){

                    // if we timed out close the connection
                    break;
                }

                // remove the streams that are done
                this.removeFinishedStreams();
            }
        }catch(IOException e) {
            logger.severe("Error during communication: " + e.getMessage());
        }
        this.terminateSession();
    }

    /**
     * removes all terminates streams
     */
    private void removeFinishedStreams(){
        this.streams.entrySet().removeIf(x -> x.getValue().isDone());
    }

    /**
     * sets up the connection to the client
     */
    private void setupConnection()
            throws IOException, BadAttributeException, ConnectionPrefaceException{

        // read in the connection preface octets
        byte [] clientConnectionPreface = new byte [Client.CLIENT_CONNECTION_PREFACE.length];

        // does not need to be synchronized because no other threads have been spun off yet
        int numRead = in.readNBytes
                (clientConnectionPreface, 0, Client.CLIENT_CONNECTION_PREFACE.length);
        if((numRead != Client.CLIENT_CONNECTION_PREFACE.length)
                || (!Arrays.equals(clientConnectionPreface,
                    Client.CLIENT_CONNECTION_PREFACE))){

            //get the bytes that were read
            byte [] readBytes = Arrays.copyOfRange(clientConnectionPreface, 0, numRead);
            throw new ConnectionPrefaceException(CONNECTION_PREFACE_ERROR, new String(readBytes, ENC) );
        }

        // now read in the settings frame
        Message m = this.messageReceiver.receiveMessage();
        if(m.getCode() != Settings.SETTINGS_TYPE){
            throw new IOException(ERROR_NO_CONNECTION_SETTINGS_FRAME);
        }
    }

    /**
     * closes the socket
     */
    private void terminateSession(){
        try {
            this.socket.getInputStream().close();
            this.socket.getOutputStream().close();
            this.socket.close();
        }catch(IOException e){

            //no need to do anything because it will stop automatically
        }
    }
}
