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
import shiip.transmission.MessageReceiver;
import shiip.transmission.MessageSender;
import shiip.util.*;
import tls.TLSFactory;

//use many constants from here
import static shiip.serialization.Headers.*;

import static shiip.serialization.Data.DATA_TYPE;

// all error return nums
import static shiip.util.ErrorCodes.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Laird
 * Shiip TCP client
 */
public class Client {

    // command line args *************************************************

    // there must be a server a port, and at least one path
    private static final int MINIMUM_COMMAND_LINE_PARAMS_CLIENt = 3;

    // the arg pos of the server
    private static final int SERVER_URL_ARG_POS = 0;

    // the arg pos of the port
    private static final int PORT_ARG_POS = 1;

    // the arg pos that srarts the list of paths
    private static final int PATH_START_POS = 2;

    //stream id increment
    private static final int STREAM_ID_INCREMENT = 2;

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

    // send by the client to initialize the connection
    public static final byte [] CLIENT_CONNECTION_PREFACE =
            {0x50, 0x52, 0x49, 0x20, 0x2a, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f,
             0x32, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x53, 0x4d, 0x0d, 0x0a,
             0x0d, 0x0a};

    // the length of the SERVER connection preface excluding settings frame
    private static final int SERVER_CONNECTION_PREFACE_SIZE = 24;

	private static final int STREAM_ID_FOR_SESSION = 0;

    // local variables ***************************************************

    // the socket associated with the shiip connection
    private Socket socket = null;

    //the next stream id that will be used
    private int currentStreamId = 1;

    // all of the paths that the client is going to send to the server
    private List<String> paths = null;

    // maps a streamId to its corresponding ClientStream object
    private Map<Integer, ClientStream> streams = new HashMap<>();

	// the active streams in the session
    private Set<Integer> activeStreams = new HashSet<>();

    // the server
    private String server;

    // used to receive messages
    private MessageReceiver messageReceiver = null;

    // used to send messages
    private MessageSender messageSender = null;

    // the output stream for the Session
    private OutputStream out = null;

    // static methods *********************************************************

    /**
     * Runs the Client
     * @param args
     *    first param - server
     *    second param - port
     *    all other params are assumed to be paths
     */
    public static void main(String [] args){
		
		// ensure not too few args
        if(args.length < MINIMUM_COMMAND_LINE_PARAMS_CLIENt){
            System.err.println("Usage: <server> <port> [<path> ...]");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        InetAddress ipAddr = CommandLineParser.getIpAddress(args[SERVER_URL_ARG_POS]);
        int port = CommandLineParser.getPort(args[PORT_ARG_POS]);
        List<String> paths = Arrays.asList(Arrays.copyOfRange(args, PATH_START_POS, args.length));
        Socket socket = null;
        try {
            socket = TLSFactory.getClientSocket(args[SERVER_URL_ARG_POS], port);
        }catch(Exception e){
            System.err.println("Error: Unable to create the socket");
            System.exit(SOCKET_CREATION_ERROR);
        }
		
		// create encoder and decoder
        Encoder encoder = EncoderDecoderSingleton.getEncoder();
        Decoder decoder = EncoderDecoderSingleton.getDecoder();
        
		// create the connection
		Client shiipConnection = 
			new Client(socket, encoder, decoder, paths, args[SERVER_URL_ARG_POS]);
        
		// run the connection
		shiipConnection.go();
		shiipConnection.closeSession();
    }

	// methods *********************************************************

    /**
     * creates a Client
     * @param socket the client that is connected to the Server
     * @param encoder used for hpack compression
     * @param paths all paths that are to be retrieved from the server
     */
    public Client(Socket socket, Encoder encoder,
			Decoder decoder, List<String> paths, String server){
        this.socket = socket;
        this.paths = paths;
        this.server = server;
        this.out = ClientIOStreamGetter.getSocketOutputStream(socket);
        this.messageReceiver =
                new MessageReceiver
                        (ClientIOStreamGetter.getSocketInputStream
                                (socket), decoder);
        this.messageSender =
                new MessageSender(this.out, encoder);
    }

    /**
     * runs the client until all files have been retrieved
     */
    public void go() {

        // send the connection preface
		this.sendConnectionPreface(this.out);

        //now make all of the file requests to the server
		this.sendRequests();
		
        //now keep reading frames from the TLS connection until all streams are done
		this.receiveMessages();
		
		//write all of the requests to files
		this.writeFiles();
    }

    /**
     * retrives the next stream id
     * @return next stream id(only odd are allowed starting
     * at 1 and strictly increasing)
     */
    private int getNextStreamId(){
        int toReturn = this.currentStreamId;
        currentStreamId += STREAM_ID_INCREMENT;
        return toReturn;
    }

    /**
     * adds all headers to the {@link Headers} to perform the
     * necessary GET request
     * @param header the header that is to have headers added
     * @param path the path of the desired resource on the server
     * @throws BadAttributeException if a name value pair is unable to be added
     */
    private static void addHeaders(Headers header, String path, String host)
			throws BadAttributeException{
        header.addValue(NAME_METHOD, GET_REQUEST);
        header.addValue(NAME_PATH, path);
        header.addValue(NAME_AUTHORITY, host); //TODO fix this
        header.addValue(NAME_SCHEME, HTTP_SCHEME);
        header.addValue("user-agent", "Mozilla/5.0");
    }

    /**
     * Handler for a Data Frame during normal operation
     * @param d the data frame to handle
     * @throws BadAttributeException should not actually be thrown
     * @throws IOException if unable to send the necessary {@link Window_Update}
     */
    private void handleDataFrame(Data d) 
			throws BadAttributeException, IOException{
        /*
        if a data frame has an unrequested stream ID
        then print error message and be done with this packet
         */
        if(!this.streams.containsKey(d.getStreamID())){
            System.err.println(UNEXPECTED_STREAM_ID + d.toString());
            return;
        }

        // add the bytes from the data message to the stream
        ClientStream s = this.streams.get(d.getStreamID());

        /*
        No more data frames should be sent if we have already received the last one
         */
        if(s.isComplete){
            System.err.println(UNEXPECTED_STREAM_ID+ d.toString());
        }
        System.out.println(RECEIVED_MESSAGE + d.toString());

        // do not send a Window_update if the data was empty
        if(d.getData().length != 0) {
            this.messageSender.sendFrame(new Window_Update(STREAM_ID_FOR_SESSION,
				d.getData().length));
            this.messageSender.sendFrame(new Window_Update(d.getStreamID(),
				d.getData().length));
            s.addBytes(d.getData());
        }
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
        System.out.println(RECEIVED_MESSAGE + s.toString());
    }

    /**
     * Handler for a Window_Update Frame
     * @param w the window update frame
     */
    private void handleWindowUpdateFrame(Window_Update w){
        System.out.println(RECEIVED_MESSAGE + w.toString());
    }

    /**
     * handler for a Headers frame
     * @param h the headers frame to handle
     */
    private void handleHeadersFrame(Headers h){
        System.out.println(RECEIVED_MESSAGE + h.toString());
        if(!h.getValue(STATUS).startsWith("200")){
            System.err.println(BAD_STATUS + h.getValue(STATUS));

            //terminate the stream
            this.streams.remove(h.getStreamID());
            this.activeStreams.remove(h.getStreamID());
        }
    }

    /**
     * sends the connection preface for the client
     * @param out the output stream to write to
     */
	private void sendConnectionPreface(OutputStream out){
		try {
            out.write(CLIENT_CONNECTION_PREFACE);
            Settings connectionStartSettingsFrame = new Settings();
            this.messageSender.sendFrame(connectionStartSettingsFrame);
        }catch(BadAttributeException | IOException e){
            System.err.println(
				"Error sending connection preface: " + e.getMessage());
            System.exit(ERROR_SENDING_CONNECTION_PREFACE);
        }
	}
	
	/**
	* sends all requests to the web server
	*/
	private void sendRequests(){
		int currStreamid;
        for(String path : this.paths){
            currStreamid = getNextStreamId();
            try {
                Headers header = new Headers(currStreamid, true);
                addHeaders(header, path, this.server );

                // now send the request to the server
                this.messageSender.sendFrame(header);

                //create a stream for this path
                this.streams.put(currStreamid, new ClientStream(currStreamid, path));
                this.activeStreams.add(currStreamid);
            }catch(BadAttributeException e){
                System.err.println("Error creating the GET request");
            }catch(IOException e2){
                System.err.println("Error sending request to server");
                System.exit(ERROR_SENDING_REQUEST_TO_SERVER);
            }
        }
	}	
	
	/**
	* receives all messages from the server and handles them
	*/
	private void receiveMessages(){
		while(!this.activeStreams.isEmpty()){
            Message m = null;
            try {
                m = this.messageReceiver.receiveMessage();
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
                System.err.println(UNABLE_TO_PARSE + e.getMessage());
            }catch(BadAttributeException e2){
                System.err.println(INVALID_MESSAGE + e2.getMessage());
            }catch(IOException e3){
                System.err.println(
					"Error in communication with server: " + e3.getMessage());
                System.exit(NETWORK_ERROR);
            }
        }
	}
	
	/**
	* writes all streams to files
	*/
	private void writeFiles(){
		for(ClientStream s : this.streams.values()){

            /*
             * make sure that a data frame with END_STREAM set was
             * received for this stream
             */
            if(!s.isComplete){
                //TODO check with Donahoo about this one
                System.err.println("Error never received DATA frame with" +
                        " END_STREAM set for streamID: " + s.getStreamId());
            }
            else {
                try {
                    s.writeToFile();
                } catch (IOException e) {
                    System.err.println("Error: Unable to write to file");
                    System.err.println(e.getMessage());
                    System.exit(ERROR_WRITING_TO_FILE);
                }
            }
        }
	}
	
	/**
	* closes the session by closing the socket
	*/
	private void closeSession(){
		try {
		    this.socket.getInputStream().close();
		    this.socket.getOutputStream().close();
            this.socket.close();
        }catch(IOException e){
            System.err.println("Error: Unable to close the socket");
            System.exit(ERROR_CLOSING_SOCKET);
        }
	}
}
