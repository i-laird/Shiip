/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.client;

import util.CommandLineParser;

import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;


/**
 * jack Client
 * @author Ian Laird
 */
public class Client {

    // only one parameter should be passed to jack server
    private static final int JACK_CLIENT_ARG_COUNT = 4;

    // the server is the first command line param
    private static final int JACK_CLIENT_SERVER_ARG_POS = 0;

    // the port number should be the first arg
    private static final int JACK_CLIENT_PORT_ARG_POS = 1;

    // the op is the third command line param
    private static final int JACK_CLIENT_OP_ARG_POS = 2;

    // the payload is the fourth command line param
    private static final int JACK_CLIENT_PAYLOAD_ARG_POS = 3;

    /**
     * jack client
     * @param args
     *     server (name or ip address)
     *     port
     *     Op
     *     payload
     */
    public static void main(String[] args) {
        if(args.length != JACK_CLIENT_ARG_COUNT){
            System.err.println("Usage: <server (name or Ip address)> <port> <Op> <Payload>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        // will make sure that it is a valid port number
        int port = CommandLineParser.getPort(args[JACK_CLIENT_PORT_ARG_POS]);
    }
}
