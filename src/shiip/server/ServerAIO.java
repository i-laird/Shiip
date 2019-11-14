/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server;

// all strings needed for the server
import shiip.server.attachment.ConnectionAttachment;
import shiip.server.completionHandlers.ConnectionHandler;
import util.CommandLineParser;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;

/**
 * Asynchronous SHiip server
 * @author Ian Laird
 */
public class ServerAIO {

    // public constants as defined in the handout ****************************

    // the max data size of the server
    public static final int MAXDATASIZE = 105;

    // the min data interval of the server
    public static final int MINDATAINTERVAL = 5;

    // the size of the byte buffer
    public static final int BUFFER_SIZE = 256;

    // the connection preface
    public static final byte [] CLIENT_CONNECTION_PREFACE =
            {0x50, 0x52, 0x49, 0x20, 0x2a, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f,
                    0x32, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x53, 0x4d, 0x0d, 0x0a,
                    0x0d, 0x0a};

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

    /**
     * runs the server
     * @param args
     *     0 : the port number
     *     1 : the document root
     */
    public static void main(String[] args) {
        if(args.length != AIO_SERVER_ARG_COUNT){
            System.err.println("Usage: <port> <doc root>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        // make sure that the public constants are valid numbers
        CommandLineParser.ensureServerConstants(MAXDATASIZE, MINDATAINTERVAL);

        // setup the logger
        util.LoggerConfig.setupLogger(logger, "connections.log");

        int port = CommandLineParser.getPort(args[AIO_SERVER_ARG_PORT_POS]);
        directory_base = CommandLineParser.getDirectoryBase(args[AIO_SERVER_ARG_DOC_ROOT_POS]);

        try (AsynchronousServerSocketChannel listenChannel = AsynchronousServerSocketChannel.open()) {
            // Bind local port
            listenChannel.bind(new InetSocketAddress(Integer.parseInt(args[0])));

            // create the attachment for the connection
            ConnectionAttachment connectionAttachment = new ConnectionAttachment(listenChannel, logger, directory_base);

            // accept a connection
            listenChannel.accept(connectionAttachment, new ConnectionHandler());

            // Block until current thread dies
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Server Interrupted", e);
        }catch(IOException e2){
            logger.log(Level.WARNING, "Unable to create Socket", e2);
        }catch(AcceptPendingException e3){
            //TODO do I need this
        }

    }
}
