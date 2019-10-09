/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.client.Client;
import shiip.serialization.*;
import shiip.util.CommandLineParser;
import shiip.util.MessageReceiver;
import shiip.util.MessageSender;
import shiip.util.TLS_Factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static shiip.serialization.Message.*;
import static shiip.util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static shiip.util.ErrorCodes.SOCKET_CREATION_ERROR;
import static shiip.serialization.Headers.STATUS;
import static shiip.serialization.Headers.NAME_PATH;

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

    // private strings ******************************************************

    // unexpected message
    private static final String UNEXPECTED_MESSAGE = "Unexpected message: ";

    // received message
    private static final String RECEIVED_MESSAGE = "Received message: ";

    // cannot open the requested file
    private static final String UNABLE_TO_OPEN_FILE = "Unable to open file: ";

    // cannot access the requested directory
    private static final String CANNOT_REQUEST_DIRECTORY = "Cannot request directory: ";

    // no path in the Headers frame
    private static final String NO_PATH_SPECIFIED = "No path specified";

    // duplicate Stream id
    private static final String DUPLICATE_STREAM_ID = "Duplicate request: ";

    // illegal stream id
    private static final String ILLEGAL_STREAM_ID = "Illegal stream ID: ";

    // 404 error for file not found
    private static final String ERROR_404_FILE = "404 File not found";

    // 404 error for directory not found
    private static final String ERROR_404_DIRECTORY = "404 Cannot request directory";

    // MISC *****************************************************************

    // used for logging the server
    private static final Logger logger = Logger.getLogger("SHiip Server");

    // instance variables ***************************************************

    // uses TLS to communicate to the client
    private Socket socket = null;

    // used to receive messages
    private MessageReceiver messageReceiver = null;

    // used to send messages
    private MessageSender messageSender = null;

    // the input stream from the remote socket
    private InputStream in = null;

    // all active stream ids for the session
    private Set<Integer> activeStreamIds = new HashSet<>();

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
        int port = CommandLineParser.getPort(args[SERVER_ARG_PORT_POS]);
        int threadNum = CommandLineParser.getThreadNum(args[SERVER_ARG_THREAD_NUM_POS]);
        String documentRoot = args[SERVER_ARG_DOC_ROOT_POS];

        // setup the logger
        try {
            FileHandler logFile = new FileHandler("connections.log");
            logger.addHandler(logFile);
        }catch(IOException e){
            System.err.println("Error: Unable to create fileHandler for connections.log");
        }

        //TODO set the document root

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        ServerSocket ss = TLS_Factory.create_TLS_Server(port);

        // run forever accepting connections to the server
        while(true){
            Socket conn = null;
            try{
                conn = ss.accept();
                // TODO fix this
                Server s = new Server(conn, null, null);

                // run this task in the thread pool
                executorService.submit(s);
            }catch(IOException e){
                logger.severe("Unable to create Socket");
                executorService.shutdown();
                System.exit(SOCKET_CREATION_ERROR);
            }
        }
    }

    /**
     * sees if a stream id of a headers received from a client is valid
     * @param streamId the stream id to test
     * @return TRUE means that it is a valid stream id (positive and odd)
     */
    private static boolean testValidStreamId(int streamId){
        return streamId >= 0 && ((streamId % 2) == 1);
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
    }

    /**
     * each thread has this method called first
     */
    public void run(){
        try {
            this.setupConnection();
            while(true){
                Message m = this.messageReceiver.receiveMessage();
                this.handleMessage(m);
            }
        }
        catch(Exception e){
            //TODO
        }
    }

    /**
     * handles a message but calling the correct subroutine
     * @param m the message to handle
     */
    private void handleMessage(Message m){
        switch(m.getCode()){
            case DATA_TYPE:
                this.handleDataFrame((Data)m);
                break;
            case SETTINGS_TYPE:
                this.handleSettingsFrame((Settings)m);
                break;
            case HEADER_TYPE:
                this.handleHeadersFrame((Headers)m);
                break;
            case WINDOW_UPDATE_TYPE:
                this.handleWindowUpdateFrame((Window_Update)m);
                break;
        }
    }

    /**
     * handles a received {@link Data} frame
     * @param d the data frame
     */
    private void handleDataFrame(Data d){
        logger.info(UNEXPECTED_MESSAGE + d.toString());
    }

    /**
     * handles a received {@link Settings} frame
     * @param s the settings frame
     */
    private void handleSettingsFrame(Settings s){
        logger.info(RECEIVED_MESSAGE + s.toString());
    }

    /**
     * handles a received {@link Window_Update} frame
     * @param w the window update frame
     */
    private void handleWindowUpdateFrame(Window_Update w){
        logger.info(RECEIVED_MESSAGE + w.toString());
    }

    /**
     * handles a received {@link Headers} frame
     * @param h the headers frame
     */
    private void handleHeadersFrame(Headers h) throws IOException{
        String path = h.getValue(Headers.NAME_PATH);

        // see if there is a path specified
        if(Objects.isNull(path)){
            logger.severe(NO_PATH_SPECIFIED);
            this.send404File(h.getStreamID(), ERROR_404_FILE);
            return;
            //TODO kill stream
        }

        // now make sure that the stream id is valid
        if(!testValidStreamId(h.getStreamID())){
            logger.info(ILLEGAL_STREAM_ID + h.toString());
            return;
        }

        // see if the stream id has already been encountered
        if(activeStreamIds.contains(h.getStreamID())){
            logger.info(DUPLICATE_STREAM_ID + h.toString());
            return;
        }

        // see if the file exists and has correct permissions
        String fileName = h.getValue(NAME_PATH);
        File file = new File(fileName);
        if(!file.exists() || !file.isFile() || !file.canRead()){
            logger.severe(UNABLE_TO_OPEN_FILE + fileName);
            this.send404File(h.getStreamID(), ERROR_404_FILE);
            return;
            //TODO kill stream
        }

        File directory = file.getParentFile();
        //TODO check with Donahoo
        if(!directory.canRead()){
            logger.severe(CANNOT_REQUEST_DIRECTORY + directory.getAbsolutePath());
            this.send404File(h.getStreamID(), ERROR_404_DIRECTORY);
        }

        this.activeStreamIds.add(h.getStreamID());
    }

    /**
     * sets up the connection to the client
     */
    private void setupConnection() throws Exception{

        // read in the connection preface octets
        byte [] clientConnectionPreface = new byte [Client.CLIENT_CONNECTION_PREFACE.length];
        in.readNBytes(clientConnectionPreface, 0, Client.CLIENT_CONNECTION_PREFACE.length);
        if(!Arrays.equals(clientConnectionPreface, Client.CLIENT_CONNECTION_PREFACE)){
            //TODO this is bad
        }

        // now read in the settings frame
        Message m = this.messageReceiver.receiveMessage();
        if(m.getCode() != Settings.SETTINGS_TYPE){
            //TODO this is bad
        }
    }

    private void send404File(int streamId, String message404){
        try {
            Headers toSend = new Headers(streamId, true);
            toSend.addValue(STATUS, message404);
            this.messageSender.sendFrame(toSend);
        }catch(BadAttributeException | IOException e){
            logger.severe("Unable to send 404 message");
            //TODO kill the stream
        }
    }
}
