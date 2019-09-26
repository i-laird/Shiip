package shiip.client;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import shiip.serialization.*;
import static shiip.serialization.Headers.*;
import static shiip.serialization.Data.DATA_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.*;
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

    private static final int ERROR_WRITING_TO_FILE = 5;

    private static final int SERVER_URL_ARG_POS = 0;
    private static final int PORT_ARG_POS = 1;
    private static final int PATH_START_POS = 2;
    private static final String RECEIVED_MESSAGE = "Received message ";
    private static final String INVALID_MESSAGE = "Invalid message: ";
    private static final String UNEXPECTED_STREAM_ID = "Unexpected stream ID: ";
    private static final String UNABLE_TO_PARSE = "Unable to parse: ";
    private static final String BAD_STATUS = "Bad status: ";


    private static Logger logger = Logger.getLogger(Client.class.getName());

    // send by the client to initialize the connection
    private static final byte [] CLIENT_CONNECTION_PREFACE =
            {0x50, 0x52, 0x49, 0x20, 0x2a, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f,
             0x32, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x53, 0x4d, 0x0d, 0x0a,
             0x0d, 0x0a};

    private static final int SERVER_CONNECTION_PREFACE_SIZE = 24;

    private Socket socket = null;
    private int currentStreamId = 1;
    private Framer framer = null;
    private Deframer deframer = null;
    private Encoder encoder = null;
    private Decoder decoder = null;
    private List<String> paths = null;
    private Map<Integer, Stream> streams = new HashMap<>();

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

    public Client(Socket socket, Encoder encoder, List<String> paths) throws IOException{
        this.socket = socket;
        this.framer = new Framer(socket.getOutputStream());
        this.deframer = new Deframer(socket.getInputStream());
        this.encoder = encoder;
        this.paths = paths;
    }

    public void go() throws Exception {
        // first send the connection preface
        OutputStream out = this.socket.getOutputStream();
        InputStream in = this.socket.getInputStream();
        out.write(CLIENT_CONNECTION_PREFACE);

        //now send a settings frame
        Settings connectionStartSettingsFrame = new Settings();
        this.sendFrame(connectionStartSettingsFrame);

        //read in the server connection preface
        byte [] serverConnectionPreface = new byte[SERVER_CONNECTION_PREFACE_SIZE];
        in.readNBytes(serverConnectionPreface, 0, SERVER_CONNECTION_PREFACE_SIZE);

        //read in the settings frame that the server will send
        Message throwAway = this.receiveMessage();

        //now make all of the file requests
        int currStreamid;
        for(String path : this.paths){
            currStreamid = getNextStreamId();
            Headers header = new Headers(currentStreamId, false);
            addHeaders(header, path);

            //create a stream for this path
            this.streams.put(currStreamid, new Stream(currentStreamId, path));
        }

        //now keep reading frames from the TLS connection until it is closed
        for(;;){
            Message m = this.receiveMessage();

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

            //TODO remove this
            break;
        }
        for(Stream s : this.streams.values()){
            if(!s.isComplete){
                //TODO this is an error
            }
            try {
                s.writeToFile();
            }catch(IOException e){
                logger.severe("Error: Unable to write to file");
                logger.severe(e.getMessage());
                System.exit(ERROR_WRITING_TO_FILE);
            }
        }
    }

    private void sendFrame(Message m) throws IOException{
        framer.putFrame(m.encode(this.encoder));
    }

    private Message receiveMessage() throws IOException, BadAttributeException{
        return Message.decode(deframer.getFrame(), decoder);
    }

    private int getNextStreamId(){
        int toReturn = this.currentStreamId;
        currentStreamId += 2;
        return toReturn;
    }

    private static void addHeaders(Headers header, String path) throws BadAttributeException{
        header.addValue(NAME_METHOD, GET_REQUEST);
        header.addValue(NAME_PATH, path);
        header.addValue(NAME_VERSION, HTTP_VERSION);
        header.addValue(NAME_HOST, ""); //TODO fix this
        header.addValue(NAME_SCHEME, HTTP_SCHEME);
    }

    private void handleDataFrame(Data d) throws BadAttributeException, IOException{
        /*
        if a data frame has an unrequested stream ID
        then print error message and CONTINUE
         */
        if(!this.streams.containsKey(d.getStreamID())){
            logger.severe(UNEXPECTED_STREAM_ID + d.toString());
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
        this.sendFrame(new Window_Update(d.getStreamID(), d.getData().length));
        s.addBytes(d.getData());
        if(d.isEnd()){
            s.setComplete(true);
        }
    }

    private void handleSettingsFrame(Settings s){
        logger.info(RECEIVED_MESSAGE + s.toString());
    }

    private void handleWindowUpdateFrame(Window_Update w){
        logger.info(RECEIVED_MESSAGE + w.toString());
    }

    private void handleHeadersFrame(Headers h){
        logger.info(RECEIVED_MESSAGE + h.toString());
        if(!h.getValue(STATUS).startsWith("200")){
            logger.severe(BAD_STATUS + h.getValue(STATUS));

            //terminate the stream
            this.streams.remove(h.getStreamID());
        }
    }
}
