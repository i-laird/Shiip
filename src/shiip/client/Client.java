package shiip.client;

import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.security.cert.X509Certificate;


import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    //unable to create socket error
    private static final int SOCKET_CREATION_ERROR = 4;

    private static final int SERVER_URL_ARG_POS = 0;
    private static final int PORT_ARG_POS = 1;
    private static final int PATH_START_POS = 2;

    private static Logger logger = Logger.getLogger(Client.class.getName());

    private static final byte [] CLIENT_CONNECTION_PREFACE =
            {0x50, 0x52, 0x49, 0x20, 0x2a, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f,
             0x32, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x53, 0x4d, 0x0d, 0x0a,
             0x0d, 0x0a};

    private Socket socket;

    public static void main(String [] args){
        if(args.length < MINIMUM_COMMAND_LINE_PARAMS_CLIENt){
            logger.severe("Usage: <server> <port> [<path> ...]");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        InetAddress ipAddr = getIpAddress(args[SERVER_URL_ARG_POS]);
        int port = getPort(args[PORT_ARG_POS]);
        List<String> paths = Arrays.asList(Arrays.copyOfRange(args, PATH_START_POS, args.length));
        try {
            Socket socket = createConnection(args[SERVER_URL_ARG_POS], port);
        }catch(Exception e){
            logger.severe("Error: Unable to create the socket");
            System.exit(SOCKET_CREATION_ERROR);
        }
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

    /**
     * code provided by Dr. Donahoo
     * @param server the server
     * @param portNum the port number
     * @return a socket with TLS
     * @throws Exception if unable to create the Socket
     */
    private static Socket createConnection(String server, int portNum) throws Exception {
        final SSLContext ctx = SSLContext.getInstance("TLSv1.3");
        ctx.init(null, new TrustManager[] { new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, null);
        final SSLSocketFactory ssf = ctx.getSocketFactory();
        final SSLSocket s = (SSLSocket) ssf.createSocket(server, portNum);
        s.setEnabledCipherSuites(new String[] { "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256" });
        final SSLParameters p = s.getSSLParameters();
        p.setApplicationProtocols(new String[] { "h2" });
        s.setSSLParameters(p);
        s.startHandshake();

        return s;
    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void go(){

        // first send the connection preface which is a data frame

    }
}
