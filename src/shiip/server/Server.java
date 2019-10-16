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
import shiip.tls.TLSFactory;
import shiip.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static shiip.serialization.Message.*;
import static shiip.serialization.Headers.STATUS;
import static shiip.serialization.Framer.MAXIMUM_PAYLOAD_SIZE;
import static shiip.serialization.Headers.NAME_PATH;
import static shiip.util.ErrorCodes.*;

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

    // private strings ******************************************************

    // for a parsing error that is encountered
    private static final String UNABLE_TO_PARSE = "Unable to parse: ";

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

    // duplicate ClientStream id
    private static final String DUPLICATE_STREAM_ID = "Duplicate request: ";

    // illegal stream id
    private static final String ILLEGAL_STREAM_ID = "Illegal stream ID: ";

    // 404 error for file not found
    private static final String ERROR_404_FILE = "404 File not found";

    // 404 error for directory not found
    private static final String ERROR_404_DIRECTORY = "404 Cannot request directory";

    // if bad connection preface
    private static final String ERROR_CONNECTION_PREFACE = "Error establishing connection...BAD connection preface";

    // if settings frame not sent during the startup
    private static final String ERROR_NO_CONNECTION_SETTINGS_FRAME = "Error Did not receive Settings Frame";

    // MISC *****************************************************************

    // used for logging the server
    private static final Logger logger = Logger.getLogger("SHiip Server");

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
        int port = CommandLineParser.getPort(args[SERVER_ARG_PORT_POS]);
        int threadNum = CommandLineParser.getThreadNum(args[SERVER_ARG_THREAD_NUM_POS]);
        String documentRoot = args[SERVER_ARG_DOC_ROOT_POS];

        // do not log to the console
        logger.setUseParentHandlers(false);

        // setup the logger
        try {
            FileHandler logFile = new FileHandler("connections.log");
            SimpleFormatter formatter = new SimpleFormatter();
            logFile.setFormatter(formatter);
            logger.addHandler(logFile);
        }catch(IOException e){
            System.err.println("Error: Unable to create fileHandler for connections.log");
            System.exit(LOGGER_PROBLEM);
        }

        directory_base= new File(documentRoot);
        if(!directory_base.exists()){
            System.err.println("Error: Doc root does not exist");
            System.exit(ERROR_DOC_ROOT);
        }

        // make sure that the public constants are valid numbers
        if(MAXDATASIZE <= 0 || MAXDATASIZE > MAXIMUM_PAYLOAD_SIZE){
            System.err.println("ERROR: Invalid MAXPAYLOAD size");
            System.exit(BAD_PUBLIC_VALUE);
        }
        if(MINDATAINTERVAL < 0){
            System.err.println("ERROR: Invalid MINDATAINTERVAL");
            System.exit(BAD_PUBLIC_VALUE);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        ServerSocket ss = null;
        //TODO fix this
        try {
            ss = TLSFactory.getServerListeningSocket(port, "mykeystore", "secret");
        }catch(Exception e){
            logger.severe("Unable to create the Server Socket");
            logger.severe(e.getMessage());
            System.exit(SOCKET_CREATION_ERROR);
        }

        // run forever accepting connections to the server
        while(true){
            Socket conn = null;
            try{
                conn = TLSFactory.getServerConnectedSocket(ss);
                Server s = new Server(conn, EncoderDecoderSingleton.getDecoder(), EncoderDecoderSingleton.getEncoder());

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
        }
        try {
            while (true) {
                Message m = null;
                this.socket.setSoTimeout(CLIENT_INACTIVE_TIMEOUT);
                try {
                    m = this.messageReceiver.receiveMessage();
                    this.handleMessage(m);
                }catch(BadAttributeException e){
                    logger.info(UNEXPECTED_MESSAGE + e.getMessage());
                    continue;
                }catch(EOFException e2){

                    // means that the client closed the stream
                    if(this.streams.isEmpty()){
                        break;
                    }
                }catch(IllegalArgumentException e4){
                    logger.info(UNABLE_TO_PARSE + e4.getMessage());
                }
                catch(SocketTimeoutException e3){

                    // if we timed out and there are no active streams then close the connection
                    if(this.streams.keySet().isEmpty()){
                        break;
                    }

                    // if there are active streams then process them
                }

                // remove the streams that are done
                this.streams.entrySet().removeIf(x -> x.getValue().isDone());
            }
        }catch(IOException e) {
            logger.severe("Error during communication: " + e.getMessage());
        }
        this.terminateSession();
    }

    /**
     * handles a message but calling the correct subroutine
     * @param m the message to handle
     */
    private void handleMessage(Message m) throws IOException{
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

        // see if the stream id has already been encountered
        if(streams.containsKey(h.getStreamID())){
            logger.info(DUPLICATE_STREAM_ID + h.toString());
            return;
        }

        // see if there is a path specified
        if(Objects.isNull(path)){
            logger.severe(NO_PATH_SPECIFIED);
            this.send404File(h.getStreamID(), ERROR_404_FILE);
            this.terminateStream(h.getStreamID());
            return;
        }

        // now make sure that the stream id is valid
        if(!testValidStreamId(h.getStreamID())){
            logger.info(ILLEGAL_STREAM_ID + h.toString());
            return;
        }

        // see if the file exists and has correct permissions
        String fileName = h.getValue(NAME_PATH);
        String slashPrepender = fileName.startsWith("/") ? "" : "/";
        String filePath = directory_base.getCanonicalPath() + slashPrepender + fileName;
        File file = new File(filePath);
        if(!file.exists() || (file.isFile() && !file.canRead())){
            logger.severe(UNABLE_TO_OPEN_FILE + filePath);
            this.send404File(h.getStreamID(), ERROR_404_FILE);
            this.terminateStream(h.getStreamID());
            return;
        }

        if(file.isDirectory()){
            logger.severe(CANNOT_REQUEST_DIRECTORY + filePath);
            this.send404File(h.getStreamID(), ERROR_404_DIRECTORY);
            this.terminateStream(h.getStreamID());
            return;
        }

        // send file
        this.lastEncounteredStreamId = h.getStreamID();
        ServerStream serverStream = new ServerStream(h.getStreamID(), new FileInputStream(file), this.messageSender, (int)file.length());
        this.streams.put(h.getStreamID(), serverStream);
        serverStream.start();
    }

    /**
     * sets up the connection to the client
     */
    private void setupConnection() throws IOException, BadAttributeException{

        // read in the connection preface octets
        byte [] clientConnectionPreface = new byte [Client.CLIENT_CONNECTION_PREFACE.length];
        synchronized (this.messageReceiver) {
            in.readNBytes(clientConnectionPreface, 0, Client.CLIENT_CONNECTION_PREFACE.length);
        }
        if(!Arrays.equals(clientConnectionPreface, Client.CLIENT_CONNECTION_PREFACE)){
            throw new IOException(ERROR_CONNECTION_PREFACE);
        }

        // now read in the settings frame
        Message m = this.messageReceiver.receiveMessage();
        if(m.getCode() != Settings.SETTINGS_TYPE){
            throw new IOException(ERROR_NO_CONNECTION_SETTINGS_FRAME);
        }
    }

    /**
     * sends a 404 message to the client
     * @param streamId the is of the stream that has 404
     * @param message404 the specific message to send
     */
    private void send404File(int streamId, String message404){
        try {
            Headers toSend = new Headers(streamId, true);
            toSend.addValue(STATUS, message404);
            this.messageSender.sendFrame(toSend);
        }catch(BadAttributeException | IOException e){
            logger.severe("Unable to send 404 message");
        }
    }

    /**
     * terminates the specified stream
     * @param streamId the id of the stream to terminate
     */
    private void terminateStream(int streamId){
        this.streams.get(streamId).interrupt();
        this.streams.remove(streamId);
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
