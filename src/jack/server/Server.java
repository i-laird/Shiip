/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 5
 * Class: Data Comm
 *******************************************************/

package jack.server;

import util.CommandLineParser;

import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;

/**
 * jack server
 * @author Ian Laird
 */
public class Server {

    // only one parameter should be passed to jack server
    private static final int JACK_SERVER_ARG_COUNT = 1;

    // the port number should be the first arg
    private static final int JACK_SERVER_ARG_PORT_POS = 0;

    /**
     * main method for jack server
     * @param args
     *     should be port number
     */
    public static void main(String[] args) {

        // only parameter should be the port number
        if(args.length != JACK_SERVER_ARG_COUNT){
            System.err.println("Usage: <port>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        // will make sure that it is a valid port number
        int port = CommandLineParser.getPort(args[JACK_SERVER_ARG_PORT_POS]);

    }
}
