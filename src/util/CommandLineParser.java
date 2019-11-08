/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package util;

import tls.TLSFactory;

import java.io.File;
import java.net.*;
import java.util.logging.Logger;

import static shiip.serialization.Framer.MAXIMUM_PAYLOAD_SIZE;
import static util.ErrorCodes.*;
import static util.ErrorCodes.BAD_PUBLIC_VALUE;


/**
 * Used to get needed values from command line strings
 * @author Ian laird
 */
public final class CommandLineParser {

    /**
     * Gets IpAddress from a server string
     * @param server the server
     * @return the ipAddress
     * @see InetAddress
     */
    public static InetAddress getIpAddress (String server){
        boolean isEncodedIp = true;

        // need to parse the server
        // first see if it is an ip address directly
        URL serverUrl = null;
        InetAddress ipAddress = null;

        try {
            ipAddress = InetAddress.getByName(server);
        }catch(UnknownHostException e){
            isEncodedIp = false;
        }

        if(!isEncodedIp) {
            try {
                serverUrl = new URL(server);
                serverUrl.toURI();
                ipAddress = InetAddress.getByName(serverUrl.getHost());
            } catch (MalformedURLException | URISyntaxException | UnknownHostException e) {
                System.err.println("Error: Unable to parse the server");
                System.exit(BAD_URL_ERROR);
            }
        }

        return ipAddress;
    }

    /**
     * get the port number from a string
     * @param port the string to parse
     * @return the port number
     */
    public static final int getPort(String port){
        return getPosInt(port, "Bad parameters: port", BAD_PORT_ERROR);
    }

    /**
     * get thread num from a string
     * @param threadNum the number of threads
     * @return the int representation of the thread num
     */
    public static final int getThreadNum(String threadNum){
        return getPosInt(threadNum, "thread num", BAD_PORT_NUM_ERROR);
    }

    /**
     * ensures values are valid
     * @param maxDataSize the maximum size of the data
     * @param minDataInterval the minimum size of the data
     */
    public static void ensureServerConstants(final int maxDataSize, final int minDataInterval){
        if(maxDataSize <= 0 || maxDataSize > MAXIMUM_PAYLOAD_SIZE){
            System.err.println("ERROR: Invalid MAXDATASIZE size");
            System.exit(BAD_PUBLIC_VALUE);
        }
        if(minDataInterval < 0){
            System.err.println("ERROR: Invalid MINDATAINTERVAL");
            System.exit(BAD_PUBLIC_VALUE);
        }
    }

    /**
     * creates TLS Server Socket
     * @param logger the logger
     * @param port the port to listen on
     * @param keyStore the key store for TLS
     * @param password the keystore password
     * @return the server socket with TLS
     */
    public static ServerSocket openTLSServerSocket(Logger logger,
            int port, String keyStore, String password){
        try {
            return TLSFactory.getServerListeningSocket
                    (port, keyStore, password);
        }catch(Exception e){
            logger.severe("Unable to create the Server Socket");
            logger.severe(e.getMessage());
            System.exit(SOCKET_CREATION_ERROR);
        }

        // unreachable
        return null;
    }

    /**
     * get the directory base
     * @param documentRoot the directory base
     * @return the directory base
     */
    public static File getDirectoryBase(String documentRoot){
        File directory_base= new File(documentRoot);
        if(!directory_base.exists()){
            System.err.println("Error: Doc root does not exist");
            System.exit(ERROR_DOC_ROOT);
        }
        return directory_base;
    }

    /**
     * Gets a numToChange number from its String representation
     * @param numToChange the number to Change
     * @param toDisplay the name of the attribute being parsed
     * @param errorCode the error code to display on failure
     * @return numToChange as an int
     */
    private static int getPosInt(String numToChange, String toDisplay, int errorCode){
        int toReturn = 0;
        try {
            toReturn = Integer.parseInt(numToChange);
            if(toReturn < 0){
                System.err.println(toDisplay + " cannot be negative");
                System.exit(errorCode);
            }
        }catch(NumberFormatException e){
            System.err.println(toDisplay + " is an improperly formatted number");
            System.err.println(e.getMessage());
            System.exit(errorCode);
        }

        return toReturn;
    }
}
