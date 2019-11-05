/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.client;

import jack.serialization.*;
import jack.serialization.Error;
import jack.util.HostPortPair;
import util.CommandLineParser;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static util.ErrorCodes.NETWORK_ERROR;
import static util.ErrorCodes.ERROR_MESSAGE_RECEIVED;
import static util.ErrorCodes.ERROR_OP_SPECIFIED;
import static jack.serialization.Message.MESSAGE_MAXIMUM_SIZE;

/**
 * jack Client
 * @author Ian Laird
 */
public class Client {

    // only one parameter should be passed to jack server
    private static final int JACK_CLIENT_ARG_COUNT = 4;

    // if no payload is present
    private static final int JACK_CLIENT_ARG_COUNT_NO_PAYLOAD = 3;

    // the server is the first command line param
    private static final int JACK_CLIENT_SERVER_ARG_POS = 0;

    // the port number should be the first arg
    private static final int JACK_CLIENT_PORT_ARG_POS = 1;

    // the op is the third command line param
    private static final int JACK_CLIENT_OP_ARG_POS = 2;

    // the payload is the fourth command line param
    private static final int JACK_CLIENT_PAYLOAD_ARG_POS = 3;

    // wait for 3 seconds for the expected reply
    private static final int TOTAL_TIME_WAIT_FOR_REPLY = 3000;

    // the total number of times to send a message before giving up
    private static final int TOTAL_NUMBER_ATTEMPT_TRANSMISSIONS = 4;

    // for a communication problem
    private static final String COMMUNICATION_PROBLEM = "Communication problem: ";

    // for unexpected message source
    private static final String UNEXPECTED_MESSAGE_SOURCE = "Unexpected message source: ";

    // for an unexpected message type
    private static final String UNEXPECTED_MESSAGE_TYPE = "Unexpected message type";

    // for an unexpected response
    private static final String UNEXPECTED_RESPONSE = "Unexpected Response";

    // for an unexpected ACK
    private static final String UNEXPECTED_ACK = "Unexpected ACK";

    // for an invalid message
    public static final String INVALID_MESSAGE = "Invalid message: ";

    // message for if bad parameters are passed
    private static final String BAD_PARAMETERS = "Bad parameters: ";

    // ***********************************************

    // the address of the server
    private InetAddress server_ip;

    // the port number the UDP server is running on
    private int server_port;

    // if the message sent was a query
    private boolean qSent;

    // if the message sent was a new message
    private boolean nSent;

    // if a new message was sent a record of it is kept here
    private New optionallySent;

    // the op for the client
    private String op;

    // the payload of the client
    private String payload;

    // if client needs to keep trying to receive the response
    private boolean repeat;

    // the socket for the client
    private DatagramSocket sock;

    // the bytes that are to be sent across the network
    private byte [] bytes = null;

    // the datagram that is to be sent by the server
    private DatagramPacket toSend = null;

    // the receive buffer
    byte [] receiveBuffer = null;

    // the amount of time left for the client to receive the valid message
    private int timeoutRemaining;

    // the time at which the mesage was sent
    private long timeMessageSent;

    // the time by which a response is expected
    private long timeMessageExpected;

    // if a timeout has occurred
    private boolean timeoutOccurred;

    private int messagesSent;

    /**
     * jack client
     * @param args
     *     server (name or ip address)
     *     port
     *     Op
     *     payload
     */
    public static void main(String[] args) {
        String payload = null;
        if(args.length != JACK_CLIENT_ARG_COUNT){
            if(args.length != JACK_CLIENT_ARG_COUNT_NO_PAYLOAD) {
                System.err.println("Usage: <server (name or Ip address)> <port> <Op> <Payload>");
                System.exit(INVALID_PARAM_NUMBER_ERROR);
            }
            payload = "";
        }
        else{
            payload = args[JACK_CLIENT_PAYLOAD_ARG_POS];
        }

        // will make sure that it is a valid port number
        int server_port = CommandLineParser.getPort(args[JACK_CLIENT_PORT_ARG_POS]);

        // make sure that the server is valid too
        InetAddress server_ipAddr = CommandLineParser.getIpAddress(args[JACK_CLIENT_SERVER_ARG_POS]);

        Client client = new Client(server_ipAddr, server_port, args[JACK_CLIENT_OP_ARG_POS], payload);
        client.go();

    }

    public Client(InetAddress server_ip, int server_port, String op, String payload) {
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.op = op;
        this.payload = payload;
        this.nSent = false;
        this.qSent = false;
        this.optionallySent = null;
        this.repeat = true;

        // init the socket
        try {
            this.sock = new DatagramSocket();
        }catch(SocketException e){
            System.err.println(COMMUNICATION_PROBLEM + "unable to create datagram socket");
            System.exit(NETWORK_ERROR);
        }

        // get the encoded message that is to be sent to the server
        try {
            this.bytes = getMessageToSend();
        }catch(IllegalArgumentException e){
            System.err.println(BAD_PARAMETERS + e.getMessage());
            System.exit(ERROR_OP_SPECIFIED);
        }

        // make the datagram packet that is to be sent
        this.toSend = new DatagramPacket(bytes, 0, bytes.length, this.server_ip, this.server_port);

        this.receiveBuffer = new byte [MESSAGE_MAXIMUM_SIZE];

        this.messagesSent = 0;
        this.timeoutOccurred = true;
        this.timeoutRemaining = TOTAL_TIME_WAIT_FOR_REPLY;
    }

    /**
     * runner for a client
     */
    public void go(){

        // now send the packet to the server
        try {
            while(this.repeat && this.messagesSent < TOTAL_NUMBER_ATTEMPT_TRANSMISSIONS ) {
                this.repeat = false;
                this.attemptToReceiveMessage();
            }

            // if the expected response was never received it was an error
            if(this.repeat){
                System.err.println("Error: Expected Response never received");
            }
        }catch(IOException e){
            System.err.println(COMMUNICATION_PROBLEM + e.getMessage());
            System.exit(NETWORK_ERROR);
        }
    }

    /**
     * attempts to read a valid message from the server
     * @throws IOException if io error occurs
     * if needs to be run again repeat flag is set
     */
    public void attemptToReceiveMessage() throws IOException{
        if(timeoutOccurred) {
            this.sock.send(this.toSend);
            this.timeMessageSent = System.currentTimeMillis();
            this.timeMessageExpected = this.timeMessageSent + TOTAL_TIME_WAIT_FOR_REPLY;
            this.messagesSent += 1;
            this.timeoutRemaining = TOTAL_TIME_WAIT_FOR_REPLY;
        }
        else{
            long currTime = System.currentTimeMillis();
            //if the amount of time that has elapsed is too much
            if(currTime >= this.timeMessageExpected){
                this.timeoutOccurred = true;
                this.repeat = true;
                return;
            }
            this.timeoutRemaining = (int)(this.timeMessageExpected - currTime);
        }
        this.sock.setSoTimeout(this.timeoutRemaining);
        DatagramPacket received = new DatagramPacket(receiveBuffer, MESSAGE_MAXIMUM_SIZE);
        try {
            sock.receive(received);
            try {
                this.handleMessage(received);
            }catch(IllegalArgumentException e2){
                System.err.println(INVALID_MESSAGE + e2.getMessage());
                this.repeat = true;
            }
        }catch(SocketTimeoutException e){
            this.repeat = true;
            this.timeoutOccurred = true;
        }
    }

    /**
     * gets the message to send from the op and payload
     * @return byte array for the message
     */
    private byte [] getMessageToSend(){
        switch(this.op.charAt(0)){
            case 'Q':
                qSent = true;
                return new Query(this.payload).encode();
            case 'N':
                nSent = true;
                this.optionallySent = new New(HostPortPair.getFromString(this.payload));
                return this.optionallySent.encode();
            case 'A':
                return new ACK(HostPortPair.getFromString(payload)).encode();
            case 'R':
                return Response.decodeResponse(this.payload).encode();
            case 'E':
                return new Error(this.payload).encode();
            default:
                System.err.println(BAD_PARAMETERS + "unexpected op " + this.op);
                System.exit(ERROR_OP_SPECIFIED);
        }

        // unreachable
        return new byte[0];
    }

    /**
     * handles a message
     * @param packet contains the message to handle
     */
    private void handleMessage(DatagramPacket packet){

        // make sure that the message is from the correct server
        if(!this.correctSender(packet)){
            return;
        }

        // get the bytes that were actually sent
        byte [] receivedBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());

        Message m = Message.decode(receivedBytes);

        switch(m.getOperation().toUpperCase()){
            case "QUERY":
                this.handleQ((Query)m);
                break;
            case "RESPONSE":
                this.handleR((Response)m);
                break;
            case "NEW":
                this.handleN((New)m);
                break;
            case "ACK":
                this.handleA((ACK)m);
                break;
            case "ERROR":
                this.handleE((jack.serialization.Error)m);
                break;
        }

    }

    public boolean correctSender(DatagramPacket packet){
        // the packet contains the senders ip and port
        if((packet.getPort() != this.server_port) ||
                (!packet.getAddress().getHostAddress().equals(this.server_ip.getHostAddress()))){
            System.err.println(UNEXPECTED_MESSAGE_SOURCE);
            System.err.println("Expected source: " + this.server_ip.getHostAddress() + ":" + Integer.toString(this.server_port));
            System.err.println("Actual source: " + packet.getAddress().getHostAddress() + ":" + Integer.toString(packet.getPort()));
            this.repeat = true;
            return false;
        }
        return true;
    }

    /**
     * handles a {@link Query} message
     * @param q the query message
     */
    private void handleQ(Query q){
        System.err.println(UNEXPECTED_MESSAGE_TYPE);
        this.repeat = true;
    }

    /**
     * handles a {@link New} message
     * @param n the New message
     */
    private void handleN(New n){
        System.err.println(UNEXPECTED_MESSAGE_TYPE);
        this.repeat = true;
    }

    /**
     * handles an {@link Error} message
     * @param e the Error Message
     */
    private void handleE(jack.serialization.Error e){
        System.err.println(e.getErrorMessage());
        System.exit(ERROR_MESSAGE_RECEIVED);
        this.repeat = true;
    }

    /**
     * handles a {@link Response} message
     * @param r the response message
     */
    private void handleR(Response r){
        if(this.qSent){
            System.out.println(r.toString());
            System.exit(0);
        }
        System.err.println(UNEXPECTED_RESPONSE);
        this.repeat = true;
    }

    /**
     * handles a {@link ACK} message
     * @param a the ACK message
     */
    private void handleA(ACK a ){
        if(!this.nSent){
            System.err.println(UNEXPECTED_ACK);
            this.repeat = true;
            return;
        }

        // see if they are the same
        New toTest = new New(a);
        if(toTest.equals(this.optionallySent)){
            System.out.println(a.toString());
            System.exit(0);
        }

        // if they are not the same
        System.err.println(UNEXPECTED_ACK);
        this.repeat = true;
    }
}
