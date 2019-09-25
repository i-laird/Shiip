package shiip.client;

import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Client {

    // there must be a server a port, and at least one path
    private static final int MINIMUM_COMMAND_LINE_PARAMS_CLIENt = 3;

    // ERROR CODES

    // invalid number of params error
    private static final int INVALID_PARAM_NUMBER_ERROR = 1;

    // invalid url error
    private static final int BAD_URL_ERROR = 2;

    //invalid port error
    private static final int BAD_PORT_ERROR = 3;

    private static final int SERVER_URL_ARG_POS = 0;
    private static final int PORT_ARG_POS = 1;
    private static final int PATH_START_POS = 2;

    private static Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String [] args){
        if(args.length < MINIMUM_COMMAND_LINE_PARAMS_CLIENt){
            logger.severe("Usage: <server> <port> [<path> ...]");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        InetAddress ipAddr = getIpAddress(args[SERVER_URL_ARG_POS]);

        int port = getPort(args[PORT_ARG_POS]);

        List<String> paths = Arrays.asList(Arrays.copyOfRange(args, PATH_START_POS, args.length));
    }

    private static InetAddress getIpAddress (String server){
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
                logger.severe("Error: Unable to parse the server");
                System.exit(BAD_URL_ERROR);
            }
        }

        return ipAddress;
    }

    private static int getPort(String port){
        try {
            int toReturn = Integer.parseInt(port);
            if(toReturn < 0){
                logger.severe("Error: port cannot be negative");
                System.exit(BAD_PORT_ERROR);
            }
        }catch(NumberFormatException e){
            logger.severe("Invalid port");
            logger.severe(e.getMessage());
            System.exit(BAD_PORT_ERROR);
        }

        // will not ever be reached
        return -1;
    }

    private static Socket initializeConnection(InetAddress ipAddress, int portNum){
        return null;
    }
}
