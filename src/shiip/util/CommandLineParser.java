/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package shiip.util;

import java.net.*;

import static shiip.util.ErrorCodes.BAD_PORT_ERROR;
import static shiip.util.ErrorCodes.BAD_URL_ERROR;
import static shiip.util.ErrorCodes.BAD_PORT_NUM_ERROR;


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
