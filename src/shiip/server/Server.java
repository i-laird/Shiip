/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 3
 * Class: Data Comm
 *******************************************************/

package shiip.server;

import shiip.util.CommandLineParser;

import java.util.logging.Logger;

import static shiip.util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;

/**
 * Shiip Server
 * @author Ian Laird
 */
public class Server {

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

    Logger logger = Logger.getLogger("SHiip Server");

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
    }
}
