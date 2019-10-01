/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package shiip.client;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

// import socket, url, and exceptions
import java.net.*;

// used for data structures
import java.util.*;

// use Framer, Deframer, and all message types
import shiip.serialization.*;

//use many constants from here
import static shiip.serialization.Headers.*;

import static shiip.serialization.Data.DATA_TYPE;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.logging.Logger;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Laird
 * Shiip TCP client
 */
public class Client {

    // ERROR CODES ***********************************************

    // invalid number of params error
    private static final int INVALID_PARAM_NUMBER_ERROR = 1;

    // invalid url error
    private static final int BAD_URL_ERROR = 2;

    //invalid port error
    private static final int BAD_PORT_ERROR = 3;

    //unable to create socket error
    private static final int SOCKET_CREATION_ERROR = 4;

    // for when there is an error writing to a file
    private static final int ERROR_WRITING_TO_FILE = 5;

    // for when there is an error in receiving the connection preface
    private static final int ERROR_SENDING_REQUEST_TO_SERVER = 6;

    // for when there is an error sending the connection preface
    private static final int ERROR_SENDING_CONNECTION_PREFACE = 7;

    // for when there is a communication error with the network
    private static final int NETWORK_ERROR = 8;

    // error getting socket io streams
    private static final int ERROR_SOCKET_GET_IO = 8;

    // command line args *************************************************

    // there must be a server a port, and at least one path
    private static final int MINIMUM_COMMAND_LINE_PARAMS_CLIENt = 3;

    // the arg pos of the server
    private static final int SERVER_URL_ARG_POS = 0;

    // the arg pos of the port
    private static final int PORT_ARG_POS = 1;

    // the arg pos that srarts the list of paths
    private static final int PATH_START_POS = 2;

    // Strings ***********************************************************

    // the logger message for received message
    private static final String RECEIVED_MESSAGE = "Received message: ";

    //the logger message for an invalid message
    private static final String INVALID_MESSAGE = "Invalid message: ";

    // the logger message for an unexpected stream id
    private static final String UNEXPECTED_STREAM_ID = "Unexpected stream ID: ";

    // the logger message for being unable to parse
    private static final String UNABLE_TO_PARSE = "Unable to parse: ";

    // the logger message for a bad status occuring
    private static final String BAD_STATUS = "Bad status: ";

    // MISC **************************************************************

    // the logger for the Shiip TCP client
    private static Logger logger = Logger.getLogger(Client.class.getName());

    // send by the client to initialize the connection
    private static final byte [] CLIENT_CONNECTION_PREFACE =
            {0x50, 0x52, 0x49, 0x20, 0x2a, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f,
             0x32, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x53, 0x4d, 0x0d, 0x0a,
             0x0d, 0x0a};

    // the length of the SERVER connection preface excluding settings frame
    private static final int SERVER_CONNECTION_PREFACE_SIZE = 24;

    // local variables ***************************************************

    // the socket associated with the shiip connection
    private Socket socket = null;

    //the next stream id that will be used
    private int currentStreamId = 1;

    // the framer that is to be used in this connection
    private Framer framer = null;

    //the deframer that is to be used in this connection
    private Deframer deframer = null;

    //the encoder for this connection
    private Encoder encoder = null;

    //the decoder that is to be used by this connection
    private Decoder decoder = null;

    // all of the paths that the client is going to send to the server
    private List<String> paths = null;

    // maps a streamId to its corresponding Stream object
    private Map<Integer, Stream> streams = new HashMap<>();

    private Set<Integer> activeStreams = new HashSet<>();

    // the server
    private String server;

    // functions *********************************************************

    /**
     * Runs the Client
     * @param args
     *    first param - server
     *    second param - port
     *    all other params are assumed to be paths
     */
    public static void main(String [] args){
        if(args.length < MINIMUM_COMMAND_LINE_PARAMS_CLIENt){
            logger.severe("Usage: <server> <port> [<path> ...]");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        InetAddress ipAddr = getIpAddress(args[SERVER_URL_ARG_POS]);
        int port = getPort(args[PORT_ARG_POS]);
        List<String> paths = Arrays.asList(Arrays.copyOfRange(args, PATH_START_POS, args.length));
        Socket socket = null;
        try {
            socket = createConnection(args[SERVER_URL_ARG_POS], port);
        }catch(Exception e){
            logger.severe("Error: Unable to create the socket");
            System.exit(SOCKET_CREATION_ERROR);
        }
        Encoder encoder = new Encoder(1024);
        Decoder decoder = new Decoder(1024, 1024);
        Client shiipConnection = new Client(socket, encoder, decoder, paths, args[SERVER_URL_ARG_POS]);
        shiipConnection.go();
    }

    /**
     * Gets IpAddress from a server string
     * @param server the server
     * @return the ipAddress
     * @see InetAddress
     */
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

    /**
     * Gets a port number from its String representation
     * @param port the port
     * @return port as an int
     */
    private static int getPort(String port){
        int toReturn = 0;
        try {
            toReturn = Integer.parseInt(port);
            if(toReturn < 0){
                logger.severe("Error: port cannot be negative");
                System.exit(BAD_PORT_ERROR);
            }
        }catch(NumberFormatException e){
            logger.severe("Invalid port");
            logger.severe(e.getMessage());
            System.exit(BAD_PORT_ERROR);
        }

        return toReturn;
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

    /**
     * creates a Client
     * @param socket the client that is connected to the Server
     * @param encoder used for hpack compression
     * @param paths all paths that are to be retrieved from the server
     */
    public Client(Socket socket, Encoder encoder, Decoder decoder, List<String> paths, String server){
        this.socket = socket;
        this.framer= new Framer(getSocketOutputStream(socket));
        this.deframer = new Deframer(getSocketInputStream(socket));
        this.encoder = encoder;
        this.decoder = decoder;
        this.paths = paths;
        this.server = server;
    }

    /**
     * runs the client until all files have been retrieved
     */
    public void go() {

        OutputStream out = getSocketOutputStream(this.socket);
        InputStream in = getSocketInputStream(this.socket);

        // send the connection preface
        try {
            out.write(CLIENT_CONNECTION_PREFACE);
            Settings connectionStartSettingsFrame = new Settings();
            this.sendFrame(connectionStartSettingsFrame);
        }catch(BadAttributeException | IOException e){
            logger.severe("Error sending connection preface: " + e.getMessage());
            System.exit(ERROR_SENDING_CONNECTION_PREFACE);
        }

        //now make all of the file requests
        int currStreamid;
        for(String path : this.paths){
            currStreamid = getNextStreamId();
            try {
                Headers header = new Headers(currStreamid, true);
                addHeaders(header, path, this.server );

                // now send the request to the server
                this.sendFrame(header);

                //create a stream for this path
                this.streams.put(currStreamid, new Stream(currStreamid, path));
                this.activeStreams.add(currStreamid);
            }catch(BadAttributeException e){
                logger.severe("Error creating the GET request");
            }catch(IOException e2){
                logger.severe("Error sending request to server");
                System.exit(ERROR_SENDING_REQUEST_TO_SERVER);
            }
        }

        //now keep reading frames from the TLS connection until all streams are done
        while(!this.activeStreams.isEmpty()){
            Message m = null;
            try {
                m = this.receiveMessage();
                switch(m.getCode()){
                    case DATA_TYPE:
                        this.handleDataFrame((Data)m);
                        break;
                    case SETTINGS_TYPE:
                        this.handleSettingsFrame((Settings)m);
                        break;
                    case HEADER_TYPE:
                        this.handleHeadersFrame((Headers)m);
                        break;
                    case WINDOW_UPDATE_TYPE:
                        this.handleWindowUpdateFrame((Window_Update)m);
                        break;
                }
            }catch(EOFException e){
                logger.severe(UNABLE_TO_PARSE + e.getMessage());
            }catch(BadAttributeException e2){
                logger.severe(INVALID_MESSAGE + e2.getMessage());
            }catch(IOException e3){
                logger.severe("Error in communication with server: " + e3.getMessage());
                System.exit(NETWORK_ERROR);
            }
        }
        for(Stream s : this.streams.values()){

            /*
             * make sure that a data frame with END_STREAM set was
             * received for this stream
             */
            if(!s.isComplete){
                //TODO check with Donahoo about this one
                logger.severe("Error never received DATA frame with" +
                        " END_STREAM set for streamID: " + s.getStreamId());
            }
            else {
                try {
                    s.writeToFile();
                } catch (IOException e) {
                    logger.severe("Error: Unable to write to file");
                    logger.severe(e.getMessage());
                    System.exit(ERROR_WRITING_TO_FILE);
                }
            }
        }
    }

    /**
     * Encodes, frames, and then sends a message over the saved output stream
     * @param m message to send
     * @throws IOException if unable to send the message
     */
    private void sendFrame(Message m) throws IOException{
        framer.putFrame(m.encode(this.encoder));
    }

    /**
     * decodes, and then deframes a message from the saved input stream
     * @return the retrieved Message
     * @throws IOException if unable to read the message
     * @throws BadAttributeException if the message has bad attributes
     */
    private Message receiveMessage() throws IOException, BadAttributeException{
        return Message.decode(deframer.getFrame(), decoder);
    }

    /**
     * retrives the next stream id
     * @return next stream id(only odd are allowed starting
     * at 1 and strictly increasing)
     */
    private int getNextStreamId(){
        int toReturn = this.currentStreamId;
        currentStreamId += 2;
        return toReturn;
    }

    /**
     * adds all headers to the {@link Headers} to perform the
     * necessary GET request
     * @param header the header that is to have headers added
     * @param path the path of the desired resource on the server
     * @throws BadAttributeException if a name value pair is unable to be added
     */
    private static void addHeaders(Headers header, String path, String host) throws BadAttributeException{
        header.addValue(NAME_METHOD, GET_REQUEST);
        header.addValue(NAME_PATH, path);
 //       header.addValue(NAME_VERSION, HTTP_VERSION);
        header.addValue(NAME_AUTHORITY, host); //TODO fix this
        header.addValue(NAME_SCHEME, HTTP_SCHEME);
        header.addValue(ACCEPT_ENCODING, "deflate");
    }

    /**
     * Handler for a Data Frame during normal operation
     * @param d the data frame to handle
     * @throws BadAttributeException should not actually be thrown
     * @throws IOException if unable to send the necessary {@link Window_Update}
     */
    private void handleDataFrame(Data d) throws BadAttributeException, IOException{
        /*
        if a data frame has an unrequested stream ID
        then print error message and be done with this packet
         */
        if(!this.streams.containsKey(d.getStreamID())){
            logger.severe(UNEXPECTED_STREAM_ID + d.toString());
            return;
        }

        // add the bytes from the data message to the stream
        Stream s = this.streams.get(d.getStreamID());

        /*
        No more data frames should be sent if we have already received the last one
         */
        if(s.isComplete){
            logger.severe(UNEXPECTED_STREAM_ID+ d.toString());
        }
        logger.info(RECEIVED_MESSAGE + d.toString());
        //TODO check this with Donahoo
        this.sendFrame(new Window_Update(d.getStreamID(), d.getData().length != 0 ? d.getData().length : 1));
        s.addBytes(d.getData());
        if(d.isEnd()){
            s.setComplete(true);
            this.activeStreams.remove(d.getStreamID());
        }
    }

    /**
     * Handler for a Settings Frame during normal operation
     * @param s the Settings Frame to handle
     */
    private void handleSettingsFrame(Settings s){
        logger.info(RECEIVED_MESSAGE + s.toString());
    }

    /**
     * Handler for a Window_Update Frame
     * @param w the window update frame
     */
    private void handleWindowUpdateFrame(Window_Update w){
        logger.info(RECEIVED_MESSAGE + w.toString());
    }

    /**
     * handler for a Headers frame
     * @param h the headers frame to handle
     */
    private void handleHeadersFrame(Headers h){
        logger.info(RECEIVED_MESSAGE + h.toString());
        if(!h.getValue(STATUS).startsWith("200")){
            logger.severe(BAD_STATUS + h.getValue(STATUS));

            //terminate the stream
            this.streams.remove(h.getStreamID());
            this.activeStreams.remove(h.getStreamID());
        }
    }

    /**
     * gete the {@link InputStream} for a {@link Socket} and
     * does exception handling
     * @param s the socket
     * @return the input stream of the Socket
     */
    private static InputStream getSocketInputStream(Socket s){
        try {
            return s.getInputStream();
        }catch(IOException e){
            logger.severe("Unable to get input stream for socket");
            System.exit(ERROR_SOCKET_GET_IO);
        }

        //unreachable statement
        return null;
    }

    /**
     * gets the {@link OutputStream} for a {@link Socket} and
     * does exception handling
     * @param s the socket
     * @return the output stream
     */
    private static OutputStream getSocketOutputStream(Socket s){
        try {
            return s.getOutputStream();
        }catch(IOException e){
            logger.severe("Unable to get input stream for socket");
            System.exit(ERROR_SOCKET_GET_IO);
        }

        //unreachable statement
        return null;
    }
}
