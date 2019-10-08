/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.util.CommandLineParser;
import shiip.util.MessageReceiver;
import shiip.util.MessageSender;
import shiip.util.TLS_Factory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static shiip.util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
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
    public static Logger logger = Logger.getLogger("SHiip Server");

    // uses TLS to communicate to the client
    private Socket socket = null;

    // used to receive messages
    private MessageReceiver messageReceiver = null;

    // used to send messages
    private MessageSender messageSender = null;

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

        //TODO set the document root

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        ServerSocket ss = TLS_Factory.create_TLS_Server(port);

        // run forever accepting connections to the server
        while(true){
            Socket conn = null;
            try{
                conn = ss.accept();
            }catch(IOException e){
                logger.severe("Unable to create Socket");
                executorService.shutdown();
                System.exit(SOCKET_CREATION_ERROR);
            }
            Server s = new Server(conn);

            // run this task in the thread pool
            executorService.submit(s);
        }
    }

    /**
     * constructor
     * @param socket the socket to be associated with the connection
     */
    public Server(Socket socket) {
        this.socket = socket;
        //this.messageReceiver = new MessageReceiver()
    }

    /**
     * each thread has this method called first
     */
    public void run(){

    }
}
