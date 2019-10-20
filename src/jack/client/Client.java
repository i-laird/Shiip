/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.client;

import jack.serialization.*;
import jack.serialization.Error;
import shiip.util.CommandLineParser;

import java.io.IOException;
import java.net.*;

import static shiip.util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static shiip.util.ErrorCodes.NETWORK_ERROR;
import static shiip.util.ErrorCodes.ERROR_MESSAGE_RECEIVED;
import static shiip.util.ErrorCodes.ERROR_OP_SPECIFIED;

/**
 * jack Client
 * @author Ian Laird
 */
public class Client {

    // only one parameter should be passed to jack server
    private static final int JACK_CLIENT_ARG_COUNT = 4;

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
    private static final int TOTAL_NUMBER_ATTEMPT_TRANSMISSIONS = 3;

    // the size of the receive buffer
    private static final int RECEIVE_BUFFER_SIZE = 1500;

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
    private static final String INVALID_MESSAGE = "Invalid message: ";

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
    // TODO rn this always resends, but we want it to wait for three seconds always
    private boolean repeat;

    /**
     * jack client
     * @param args
     *     server (name or ip address)
     *     port
     *     Op
     *     payload
     */
    public static void main(String[] args) {
        if(args.length != JACK_CLIENT_ARG_COUNT){
            System.err.println("Usage: <server (name or Ip address)> <port> <Op> <Payload>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }

        // will make sure that it is a valid port number
        int server_port = CommandLineParser.getPort(args[JACK_CLIENT_PORT_ARG_POS]);

        // make sure that the server is valid too
        InetAddress server_ipAddr = CommandLineParser.getIpAddress(args[JACK_CLIENT_SERVER_ARG_POS]);

        Client client = new Client(server_ipAddr, server_port, args[JACK_CLIENT_OP_ARG_POS], args[JACK_CLIENT_PAYLOAD_ARG_POS]);
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
        this.repeat = false;
    }

    /**
     * runner for a client
     */
    public void go(){

        //make the datagram socket
        DatagramSocket sock = null;
        try {
            sock = new DatagramSocket();
        }catch(SocketException e){
            System.err.println(COMMUNICATION_PROBLEM + "unable to create datagram socket");
            System.exit(NETWORK_ERROR);
        }

        // get the encoded message that is to be sent to the server
        byte [] bytes = getMessageToSend();

        // make the datagram packet that is to be sent
        DatagramPacket toSend = new DatagramPacket(bytes, 0, bytes.length, this.server_ip, this.server_port);

        // now send the packet to the server
        int counter = 0;
        try {
            while(this.repeat && counter < TOTAL_NUMBER_ATTEMPT_TRANSMISSIONS ) {
                byte [] receiveBuffer = new byte [RECEIVE_BUFFER_SIZE];
                sock.setSoTimeout(TOTAL_TIME_WAIT_FOR_REPLY);
                sock.send(toSend);
                DatagramPacket received = new DatagramPacket(receiveBuffer, RECEIVE_BUFFER_SIZE);
                try {
                    sock.receive(received);
                    try {
                        //TODO only 1500 bytes?
                        this.handleMessage(received);
                    }catch(IllegalArgumentException e2){
                        System.err.println(INVALID_MESSAGE + e2.getMessage());
                        this.repeat = true;
                    }
                }catch(SocketTimeoutException e){
                    this.repeat = true;
                }
                counter += 1;
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
     * gets the message to send from the op and payload
     * @return byte array for the message
     */
    private byte [] getMessageToSend(){
        switch(this.op.toLowerCase().charAt(0)){
            case 'q':
                qSent = true;
                return new Query(this.payload).encode();
            case 'n':
                nSent = true;
                //TODO fix this how to get payload for a New
                //this.optionallySent = new New(this.payload);
                return this.optionallySent.encode();
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
        // the packet contains the senders ip and port
        if(!(packet.getAddress().equals(this.server_ip) && packet.getPort() != this.server_port)){
            System.err.println(UNEXPECTED_MESSAGE_SOURCE);
            System.err.println("Expected source: " + this.server_ip.toString() + ":" + Integer.toString(this.server_port));
            System.err.println("Actual source: " + packet.getAddress().toString() + ":" + Integer.toString(packet.getPort()));
            this.repeat = true;
            return;
        }

        Message m = Message.decode(packet.getData());

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

    /**
     * handles a {@link Query} message
     * @param q the query message
     * @return true means that this is not the expected message
     */
    private void handleQ(Query q){
        System.err.println(UNEXPECTED_MESSAGE_TYPE);
        this.repeat = true;
    }

    /**
     * handles a {@link New} message
     * @param n the New message
     * @return true means not the expected message
     */
    private void handleN(New n){
        System.err.println(UNEXPECTED_MESSAGE_TYPE);
        this.repeat = true;
    }

    /**
     * handles an {@link Error} message
     * @param e the Error Message
     * @return terminates the client
     */
    private void handleE(jack.serialization.Error e){
        System.err.println(e.getErrorMessage());
        System.exit(ERROR_MESSAGE_RECEIVED);
        this.repeat = true;
    }

    /**
     * handles a {@link Response} message
     * @param r the response message
     * @return true means that this was not an expected message
     */
    private void handleR(Response r){
        if(this.qSent){
            System.out.println(r.getServiceList());
            System.exit(0);
        }
        System.err.println(UNEXPECTED_RESPONSE);
        this.repeat = true;
    }

    /**
     * handles a {@link ACK} message
     * @param a the ACK message
     * @return true means that the ACK was not expected by the client
     */
    private void handleA(ACK a ){
        if(!this.nSent){
            System.err.println(UNEXPECTED_ACK);
            this.repeat = true;
            return;
        }

        // see if they are the same
        if(((HostPortMessage)a).equals(this.optionallySent)){
            System.out.println(a.toString());
            System.exit(0);
        }

        // if they are not the same
        System.err.println(UNEXPECTED_ACK);
        this.repeat = true;
    }
}
