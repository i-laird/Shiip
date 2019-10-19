/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.client;

import jack.serialization.*;
import jack.serialization.Error;
import util.CommandLineParser;

import java.io.IOException;
import java.net.*;

import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static util.ErrorCodes.NETWORK_ERROR;
import static util.ErrorCodes.ERROR_MESSAGE_RECEIVED;


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

        //make the datagram socket
        DatagramSocket sock = null;
        try {
            sock = new DatagramSocket();
        }catch(SocketException e){
            //TODO
        }

        // get the encoded message that is to be sent to the server
        byte [] bytes = getMessageToSend(args[JACK_CLIENT_OP_ARG_POS], args[JACK_CLIENT_PAYLOAD_ARG_POS]);

        // make the datagram packet that is to be sent
        DatagramPacket toSend = new DatagramPacket(bytes, 0, bytes.length, server_ipAddr, server_port);

        // now send the packet to the server
        boolean repeat = false;
        int counter = 0;
        try {
            while(repeat && counter < TOTAL_NUMBER_ATTEMPT_TRANSMISSIONS ) {
                byte [] receiveBuffer = new byte [RECEIVE_BUFFER_SIZE];
                sock.setSoTimeout(TOTAL_TIME_WAIT_FOR_REPLY);
                sock.send(toSend);
                DatagramPacket received = new DatagramPacket(receiveBuffer, RECEIVE_BUFFER_SIZE);
                try {
                    sock.receive(received);
                    try {
                        //TODO change this
                        repeat = handleMessage(received, server_ipAddr, server_port, true);
                    }catch(IllegalArgumentException e2){
                        System.err.println(INVALID_MESSAGE + e2.getMessage());
                        repeat = true;
                    }
                }catch(SocketTimeoutException e){
                    repeat = true;
                }
                counter += 1;
            }

            // if the expected response was never received it was an error
            if(repeat){
                System.err.println("Error: Expected Response never received");
            }
        }catch(IOException e){
            System.err.println(COMMUNICATION_PROBLEM + e.getMessage());
            System.exit(NETWORK_ERROR);
        }

    }

    private static byte [] getMessageToSend(String op, String payload){
        return null;
    }

    private static boolean handleMessage(DatagramPacket packet, InetAddress server_address, int serverPort, boolean qSent){

        // make sure that the message is from the correct server
        // the packet contains the senders ip and port
        if(!(packet.getAddress().equals(server_address) && packet.getPort() != serverPort)){
            System.err.println(UNEXPECTED_MESSAGE_SOURCE);
            System.err.println("Expected source: " + server_address.toString() + ":" + Integer.toString(serverPort));
            System.err.println("Actual source: " + packet.getAddress().toString() + ":" + Integer.toString(packet.getPort()));
            return true;
        }

        Message m = Message.decode(packet.getData());

        switch(m.getOperation().toUpperCase()){
            case "QUERY":
                handleQ((Query)m);
                break;
            case "RESPONSE":
                handleR((Response)m, qSent);
                break;
            case "NEW":
                break;
            case "ACK":
                break;
            case "ERROR":
                break;
        }

    }

    /**
     * handles a {@link Query} message
     * @param q the query message
     * @return true means that this is not the expected message
     */
    private static boolean handleQ(Query q){
        System.err.println(UNEXPECTED_MESSAGE_TYPE);
        return true;
    }

    /**
     * handles a {@link New} message
     * @param n the New message
     * @return true means not the expected message
     */
    private static boolean handleN(New n){
        System.err.println(UNEXPECTED_MESSAGE_TYPE);
        return true;
    }

    /**
     * handles an {@link Error} message
     * @param e the Error Message
     * @return terminates the client
     */
    private static boolean handleE(Error e){
        System.err.println(e.getErrorMessage());
        System.exit(ERROR_MESSAGE_RECEIVED);
        return true;
    }

    /**
     * handles a {@link Response} message
     * @param r the response message
     * @return true means that this was not an expected message
     */
    private static boolean handleR(Response r, boolean qSent){
        if(qSent){
            System.out.println(r.getServiceList());
            System.exit(0);
        }
        System.err.println(UNEXPECTED_RESPONSE);
        return true;
    }

    /**
     * handles a {@link ACK} message
     * @param a the ACK message
     * @return true means that the ACK was not expected by the client
     */
    private static boolean handleA(ACK a, boolean nSent, New optionallyPresent ){
        if(!nSent){
            System.err.println(UNEXPECTED_ACK);
            return true;
        }

        // see if they are the same
        if(((HostPortMessage)a).equals(optionallyPresent)){
            System.out.println(a.toString());
            System.exit(0);
        }

        // if they are not the same
        System.err.println(UNEXPECTED_ACK);
        return true;
    }
}
