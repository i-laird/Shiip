/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 7
 * Class: Data Comm
 *******************************************************/

package jack.client;

import jack.serialization.Message;
import util.CommandLineParser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Scanner;

import static jack.serialization.Message.MESSAGE_MAXIMUM_SIZE;
import static util.ErrorCodes.INVALID_PARAM_NUMBER_ERROR;


/**
 * @author Ian laird
 * The Jack multicast client
 */
public class MulticastClient extends Thread {

    // the expected number of args
    private static final int EXPECTED_ARG_NUM = 2;

    // the position of the server in the args
    private static final int SERVER_POS = 0;

    // the pos of the server port
    private static final int SERVER_PORT_POS = 1;

    // the amount of time to check if interrupted
    private static final int CHECK_FOR_KILLED = 100;

    private MulticastSocket sock;

    private InetAddress server;

    private int port;


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
        Scanner in = new Scanner(System.in);
        MulticastSocket sock = null;

        try {
            MulticastClient client = new MulticastClient(server, port);

            //forever read in user input
            while(true){
                String readLine = in.next();
                if(readLine.matches("quit")){

                    client.interrupt();
                    break;
                }
            }
            client.join();
        }catch(IOException | InterruptedException e){}

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
        this.port = port;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[MESSAGE_MAXIMUM_SIZE];

        while (true) {
            try {
                sock.setSoTimeout(CHECK_FOR_KILLED);
                DatagramPacket toReceive = new DatagramPacket(buffer, MESSAGE_MAXIMUM_SIZE);
                sock.receive(toReceive);

                if(sock.getPort() == this.port) {
                    byte[] receivedBytes = Arrays.copyOfRange(toReceive.getData(), 0, toReceive.getLength());
                    Message m = Message.decode(receivedBytes);
                    System.out.println(m.toString());
                }
            } catch (IOException e) {
                if (!(e instanceof SocketTimeoutException && !this.isInterrupted())) {
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
