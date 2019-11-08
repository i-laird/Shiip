/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server;

// all strings needed for the server
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static shiip.util.ServerStrings.*;
import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;

public class ServerAIO {

    // public constants as defined in the handout ****************************

    // the max data size of the server
    public static final int MAXDATASIZE = 100;

    // the min data interval of the server
    public static final int MINDATAINTERVAL = 5;

    // private constants ****************************************************

    // the number of arguments for the aio shiip server
    private static final int AIO_SERVER_ARG_COUNT = 2;

    // the position of the port number in the arg pos
    private static final int AIO_SERVER_ARG_PORT_POS = 0;

    // the position of the document root in the args
    private static final int AIO_SERVER_ARG_DOC_ROOT_POS = 1;

    // the wait if the client is inactive
    private static final int CLIENT_INACTIVE_TIMEOUT = 1000 * 20;

    // used for logging the server
    private static final Logger logger = Logger.getLogger("SHiip AIO Server");

    // the charset that is being used
    private static final Charset ENC = StandardCharsets.US_ASCII;

    // the base directory
    private static File directory_base = null;

    public static void main(String[] args) {
        if(args.length != AIO_SERVER_ARG_COUNT){
            System.err.println("Usage: <port> <doc root>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }
    }
}
