/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 5
 * Class: Data Comm
 *******************************************************/

package jack.server;

import jack.serialization.*;
import jack.serialization.Error;
import jack.util.HostPortPair;
import util.CommandLineParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static util.ErrorCodes.NETWORK_ERROR;
import static jack.serialization.Message.MESSAGE_MAXIMUM_SIZE;


/**
 * jack server
 * @author Ian Laird
 */
public class Server {

    // static *****************************************************************

    // only one parameter should be passed to jack server
    private static final int JACK_SERVER_ARG_COUNT = 1;

    // the port number should be the first arg
    private static final int JACK_SERVER_ARG_PORT_POS = 0;

    // the logger for the server
    private static final Logger logger = Logger.getLogger("jack server");

    // for a communication problem
    private static final String COMMUNICATION_PROBLEM = "Communication problem: ";

    // for an invalid message
    private static final String INVALID_MESSAGE = "Invalid message: ";

    // the charset that is being used
    private static final Charset ENC = StandardCharsets.US_ASCII;

    // instance ***************************************************************

    // the port that this server is to run on
    private int port;

    // the datagram socket for the server
    private DatagramSocket sock;

    // all of the encountered host:port pairs
    private Set<HostPortPair> serverList = new HashSet<>();

    /**
     * main method for jack server
     * @param args
     *     should be port number
     */
    public static void main(String[] args) {
        // only parameter should be the port number
        if(args.length != JACK_SERVER_ARG_COUNT){
            System.err.println("Usage: <port>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        // will make sure that it is a valid port number
        int port = CommandLineParser.getPort(args[JACK_SERVER_ARG_PORT_POS]);
        Server server = null;

        util.LoggerConfig.setupLogger(logger, "jack.log");

        try {
            server = new Server(port);
        }catch(SocketException e){
            logger.severe("Unable to create the datagram socket");
            System.exit(NETWORK_ERROR);
        }
        server.go();
    }

    /**
     * creates a server
     * @param port the port that the server is to run on
     */
    public Server(int port) throws SocketException{
        this.port = port;
        this.sock = new DatagramSocket(port);
    }

    /**
     * runs the server
     */
    public void go(){
        // create the buffer
        byte [] buffer = new byte[MESSAGE_MAXIMUM_SIZE];

        // run forever
        while(true){
            try {

                // create the datagram packet
                DatagramPacket toReceive = new DatagramPacket(buffer, MESSAGE_MAXIMUM_SIZE);
                this.sock.receive(toReceive);
                byte [] receivedBytes = Arrays.copyOfRange(toReceive.getData(), 0, toReceive.getLength());
                Message m = Message.decode(receivedBytes);
                this.handleMessage(m, toReceive);
            }catch(IOException e){
                logger.severe(COMMUNICATION_PROBLEM + e.getMessage());
            }catch(IllegalArgumentException e2){
                logger.severe(INVALID_MESSAGE + e2.getMessage());
            }
        }
    }

    /**
     * handles the message
     * @param m the message to handle
     * @param lastReceived packet to send back to the client
     * @throws IOException if network error occurs
     */
    public void handleMessage(Message m, DatagramPacket lastReceived) throws IOException{
        Message response = null;
        switch(m.getOperation().toUpperCase()){
            case "QUERY":
                response = this.handleQ((Query)m);
                break;
            case "NEW":
                response = this.handleN((New)m);
                break;
            case "RESPONSE":
            case "ACK":
            case "ERROR":
                response = this.handleRAE(m);
                break;
        }
        lastReceived.setData(response.encode());
        this.sock.send(lastReceived);
    }

    /**
     * handles a {@link Query} message
     * @param q the New message
     */
    private Message handleQ(Query q){

        // if it is empty just treat it like a wildcard
        if(q.getSearchString().isEmpty()){
            q.setSearchString("*");
        }
        String receipt = q.toString();
        logger.info(receipt);

        String stringToMatch = q.getSearchString();

        // create an empty response
        Response resultsOfQuery = new Response();

        // put any services that match the regex into the response
        this.serverList.stream().filter(x -> {
            if(stringToMatch.equals("*")){
                return true;
            }
            return x.getHost().contains(stringToMatch);
        }).forEach( x -> resultsOfQuery.addService(x.getHost(), x.getPort()));

        return resultsOfQuery;
    }

    /**
     * handles a {@link New} message
     * @param n the New message
     */
    private Message handleN(New n){
        String receipt = n.toString();
        logger.info(receipt);

        this.serverList.add(new HostPortPair(n.getHost(), n.getPort()));
        ACK acknlowdgment = new ACK(n);

        return acknlowdgment;
    }

    /**
     * handles a {@link Response} a {@link ACK} and a {@link jack.serialization.Error}
     * @param m the message to handle
     */
    private Message handleRAE(Message m){

        String errorMessage = INVALID_MESSAGE + m.toString();

        // log the invalid message
        logger.severe(errorMessage);

        return new Error(errorMessage);
    }
}
