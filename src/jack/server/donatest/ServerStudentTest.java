package jack.server.donatest;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test for Jack server
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerStudentTest {

    /**
     * Default server port
     */
    private static final int SRVRPORT = 3000;
    /**
     * Maximum packet size
     */
    private static final int PKTMAX = 65507;
    /**
     * Default character encoding
     */
    private static final String CHARENCODING = "ASCII";
    private static final long DECODETIMEOUT = 2000;

    /**
     * Socket to connect
     */
    private DatagramSocket socket;
    /**
     * Server port
     */
    private int port;
    /**
     * Server identity
     */
    private InetAddress server;

    /**
     * Prepare server test
     * 
     * @throws IOException if problem
     */
    @BeforeEach
    public void setup() throws IOException {
        // Determine server identity and port
        port = System.getProperty("port") == null ? SRVRPORT : Integer.parseInt(System.getProperty("port"));
        server = (InetAddress) InetAddress
                .getByName(System.getProperty("server") == null ? "localhost" : System.getProperty("server"));
        // Create and connect socket
        socket = new DatagramSocket();
        socket.connect(server, port);
        // Wait until server is ready
        //System.out.println("Restart server.  Hit return to continue.");
        //new Scanner(System.in).nextLine();
    }

    /**
     * Teardown test
     * 
     * @throws IOException if problem
     */
    @AfterEach
    public void teardown() throws IOException {
        socket.close();
    }
    
    /**
     * Test query empty list
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(1)
    public void testEmptyList() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            testSend("Q cnn.com", "R ");
        });
    }
    
    /**
     * Test basic list operations
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(2)
    public void testList() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            // Add cnn.com
            testSend("N cnn.com:50", "A cnn.com:50");

            String r = "R ";
            // Query for non-existing host
            testSend("Q cmm.com", r);

            // Query for non-existing host
            testSend("Q x", r);

            // Query for existing host
            r += "cnn.com:50 ";
            testSend("Q nn.com", r);

            // Add new host
            testSend("N www.cnn.com:5000", "A www.cnn.com:5000");
            r += "www.cnn.com:5000 ";
            testSend("Q cnn.com", r);
        });
    }

    /**
     * Test server handling duplication
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(3)
    public void testDuplicate() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            // Add foo.com
            testSend("N foo.com:50", "A foo.com:50");
            testSend("N foo.com:50", "A foo.com:50");

            // Query for existing host
            String r = "R foo.com:50 ";
            testSend("Q foo.com", r);

            // Add new host
            testSend("N foo.com:5000", "A foo.com:5000");
            r += "foo.com:5000 ";
            testSend("Q foo.com", r);
        });
    }

    /**
     * Test malformed message
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(4)
    public void testBadMessage() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            String r = "X foo.com";
            sendPacket(r);
            String msg = receivePacket();
            assertTrue(msg.contains("Invalid message"));
        });
    }

    /**
     * Test bad payload name
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(5)
    public void testBadPayloadName() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            String r = "N X^x:4000";
            sendPacket(r);
            String msg = receivePacket();
            assertTrue(msg.contains("Invalid message"));
        });
    }
    
    /**
     * Test bad payload separator
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(6)
    public void testBadPayloadSeparator() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            String r = "N foo.com-4000";
            sendPacket(r);
            String msg = receivePacket();
            assertTrue(msg.contains("Invalid message"));
        });
    }

    /**
     * Test unexpected operation
     * 
     * @throws IOException if problem
     */
    @Test
    @Order(7)
    public void testUnexpectedType() throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            String r = "R ";
            sendPacket(r);
            String msg = receivePacket();
            assertTrue(msg.contains("Unexpected message type"));
        });
    }
    
    /**
     * Send message and test for expected response
     * 
     * @param send   message to send
     * @param expMsg expected message
     * 
     * @throws IOException if problem
     */
    private void testSend(String send, String expMsg) throws IOException {
        assertTimeoutPreemptively(ofMillis(DECODETIMEOUT), () -> {
            sendPacket(send);
            String rcvMsg = receivePacket();
            assertEquals(expMsg.trim(), rcvMsg.trim());
        });
    }

    /**
     * Send message
     * 
     * @param send message to send
     * 
     * @throws UnsupportedEncodingException if default encoding not available
     */
    protected void sendPacket(String send) throws UnsupportedEncodingException {
        if (send != null) {
            byte[] sndBuffer = send.getBytes(CHARENCODING);
            DatagramPacket sndDatagram = new DatagramPacket(sndBuffer, sndBuffer.length);
            try {
                socket.send(sndDatagram);
            } catch (IOException e) {
                System.err.println("Unable to send: " + e.getMessage());
            }
        }
    }

    /**
     * Receive message and return as string
     * 
     * @return message as string
     * 
     * @throws IOException if problem
     */
    protected String receivePacket() throws IOException {
        DatagramPacket rcvDatagram = new DatagramPacket(new byte[PKTMAX], PKTMAX);
        socket.receive(rcvDatagram); // Receive packet from client

        // Copy valid subset of buffer bytes and decode
        return new String(Arrays.copyOf(rcvDatagram.getData(), rcvDatagram.getLength()), CHARENCODING);
    }
}
