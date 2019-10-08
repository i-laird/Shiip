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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static shiip.serialization.Message.*;
import static shiip.util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static shiip.util.ErrorCodes.NETWORK_ERROR;
import static shiip.util.ErrorCodes.SOCKET_CREATION_ERROR;

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

    // the number of arguments for the shiip server
    private static final int SERVER_ARG_COUNT = 3;

    // the position of the port number in the arg pos
    private static final int SERVER_ARG_PORT_POS = 0;

    // the position of the thread num in the arg pos
    private static final int SERVER_ARG_THREAD_NUM_POS = 1;

    // the position of the document root in the args
    private static final int SERVER_ARG_DOC_ROOT_POS = 2;

    // used for logging the server
    private static Logger logger = Logger.getLogger("SHiip Server");

    // unexpected message
    private static String UNEXPECTED_MESSAGE = "Unexpected message: ";

    // received message
    private static String RECEIVED_MESSAGE = "Received message: ";

    // cannot open the requested file
    private static String UNABLE_TO_OPEN_FILE = "Unable to open file: ";

    // cannot access the requested directory
    private static String CANNOT_REQUEST_DIRECTORY = "Cannot request directory: ";

    // no path in the Headers frame
    private static String NO_PATH_SPECIFIED = "No path specified";

    // duplicate Stream id
    private static String DUPLICATE_STREAM_ID = "Duplicate request: ";

    // illegal stream id
    private static String ILLEGAL_STREAM_ID = "Illegal stream ID: ";

    // uses TLS to communicate to the client
    private Socket socket = null;

    // used to receive messages
    private MessageReceiver messageReceiver = null;

    // used to send messages
    private MessageSender messageSender = null;

    // the input stream from the remote socket
    private InputStream in = null;

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
    private void handleDataFrame(Data d){
        logger.info("Unexpected message");
    }

    private void handleSettingsFrame(Settings s){

    }

    private void handleHeadersFrame(Headers h){

    }

    private void handleWindowUpdateFrame(Window_Update w){

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
}
