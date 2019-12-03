/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 7
 * Class: Data Comm
 *******************************************************/

package jack.client;

import jack.serialization.Message;
import util.CommandLineParser;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

import static jack.serialization.Message.MESSAGE_MAXIMUM_SIZE;
import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;
import static util.ErrorCodes.NETWORK_ERROR;

/**
 * @author Ian laird
 * The Jack multicast client
 */
public class MulticastClient {

    // the expected number of args
    private static final int EXPECTED_ARG_NUM = 2;

    // the position of the server in the args
    private static final int SERVER_POS = 0;

    // the pos of the server port
    private static final int SERVER_PORT_POS = 1;

    // the amount of time to check if interrupted
    private static final int CHECK_FOR_KILLED = 100;

    // the multicast socket
    private MulticastSocket sock;

    // the inet address of the server
    private InetAddress server;


    /**
     * runs the multicast client
     * @param args
     *    0 multicast address
     *    1 port
     */
    public static void main(String[] args) {
        if(args.length != EXPECTED_ARG_NUM){
            System.err.println("Usage: MulticastClient <server> <port>");
            System.exit(INVALID_PARAM_NUMBER_ERROR);
        }
        InetAddress server = CommandLineParser.getIpAddress(args[SERVER_POS]);
        int port = CommandLineParser.getPort(args[SERVER_PORT_POS]);
        MulticastSocket sock = null;

        MulticastClient client = null;
        try {
            client = new MulticastClient(server, port);

        }catch(IOException e){
            System.err.println("Unable to create multicast client: " + e.getMessage());
            System.exit(NETWORK_ERROR);
        }
        client.run();
    }

    /**
     * the multicast jack client
     * @param server the server
     * @param port the port
     * @throws IOException if io error occurs
     */
    public MulticastClient(InetAddress server, int port) throws IOException{
        this.sock = new MulticastSocket();
        sock.joinGroup(server);
        this.server = server;
    }

    /**
     * runs the client
     */
    public void run() {
        byte[] buffer = new byte[MESSAGE_MAXIMUM_SIZE];
        Scanner in = new Scanner(System.in);

        while (true) {
            try {
                while(in.hasNext()) {
                    String readLine = in.next();
                    if (readLine.matches("quit")) {
                        sock.leaveGroup(server);
                        sock.close();
                        return;
                    }
                }

                sock.setSoTimeout(CHECK_FOR_KILLED);
                DatagramPacket toReceive = new DatagramPacket(buffer, MESSAGE_MAXIMUM_SIZE);
                sock.receive(toReceive);

                byte[] receivedBytes = Arrays.copyOfRange(toReceive.getData(), 0, toReceive.getLength());
                Message m = Message.decode(receivedBytes);
                System.out.println(m.toString());
            } catch (IOException e) {
                if (!(e instanceof SocketTimeoutException)) {
                    try {
                        sock.leaveGroup(server);
                        sock.close();
                        return;
                    } catch (IOException e2) { }
                }
            }
        }
    }
}
