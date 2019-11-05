/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jack.serialization.test.ResponseTester.VALID_PORT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ian Laird and Andrew Walker
 * @version 1.0
 */
public abstract class HostPortTester {

    // a valid host
    private static final String testString = "localhost";

    // the size of a maximum string for testing
    private static final int BIG_STRING_SIZE = 65503;

    // the big string that will be used for max size testing
    private static String HOST_THREE_FROM_MAX = null;

    private static String HOST_TWO_FROM_MAX = null;

    static{
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < BIG_STRING_SIZE; i++) {
            builder.append("A");
        }
        HOST_THREE_FROM_MAX = builder.toString();
        HOST_TWO_FROM_MAX = HOST_THREE_FROM_MAX + "A";
    }

    /**
     * tests null host
     */
    @Test
    @DisplayName("Null host")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> getNewObject(null, VALID_PORT ));
    }

    /**
     * tests the setter for host
     */
    @Test
    @DisplayName("host setter")
    public void testHostSetter(){
        String testString = "localhost";
        assertEquals(testHostSetter(testString), testString);
    }

    /**
     * tests the setter for port
     */
    @Test
    @DisplayName("port setter")
    public void testPortSetter(){
        assertEquals(testPortSetter("localhost", VALID_PORT), VALID_PORT);
    }

    /**
     * tests tostring
     */
    @Test
    @DisplayName("to string")
    public void testToString(){
        Message m = getNewObject(testString, VALID_PORT);
        String expectedString = getMessageType() + " [" + testString + ":" + VALID_PORT + "]";
        assertEquals(expectedString, m.toString());
    }

    /**
     * tests equals on equal objects
     */
    @Test
    @DisplayName("equals on equal objects")
    public void testEqualsEqualObjects(){
        Message m1 = getNewObject(testString, VALID_PORT);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertEquals(m1, m2);
    }

    /**
     * tests equal when unequal port
     */
    @Test
    @DisplayName("equals on unequal port")
    public void testEqualsUnequalPort(){
        Message m1 = getNewObject(testString, VALID_PORT + 1);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertNotEquals(m1, m2);
    }

    /**
     * tests equal when unequal host
     */
    @Test
    @DisplayName("equals on unequal host")
    public void testEqualsUnequalHost(){
        Message m1 = getNewObject(testString + testString, VALID_PORT);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertNotEquals(m1, m2);
    }

    /**
     * tests hashcode on equal objects
     */
    @Test
    @DisplayName("hashcode")
    public void testHashcodeEqualObjects(){
        Message m1 = getNewObject(testString, VALID_PORT);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    /**
     * oversized host
     */
    @Test
    public void testOversizedHostConstructor(){
        assertThrows(IllegalArgumentException.class, () -> {
            getNewObject(HOST_TWO_FROM_MAX, 1);
        });
    }

    /**
     * oversized host
     */
    @Test
    public void testOversizedHostSetter(){
        assertThrows(IllegalArgumentException.class, () -> {
            testHostSetter(HOST_TWO_FROM_MAX);
        });
    }

    /**
     * oversized port
     */
    @Test
    public void testOversizedPortConstructor(){
        int port = 12;
        assertThrows(IllegalArgumentException.class, () -> {
            getNewObject(HOST_THREE_FROM_MAX, port);
        });
    }

    /**
     * oversized port
     */
    @Test
    public void testOversizedPortSetter(){
        assertThrows(IllegalArgumentException.class, () -> {
            int newPort = 12;
            testPortSetter( HOST_THREE_FROM_MAX, newPort);
        });
    }

    /**
     * gets an object of the specific type being tested
     * @param host the host
     * @param port the port
     * @return the object of specific type
     */
    protected abstract Message getNewObject(String host, int port);

    /**
     * sets and return the string retrieved
     * @param s the string to set
     * @return the retrieved string
     */
    protected abstract String testHostSetter(String s);

    /**
     * sets and return the port
     * @param host the host name
     * @param port the port to set
     * @return the port retrieved
     */
    protected abstract int testPortSetter(String host, int port);

    /**
     * gets the full OP for the message type
     * @return the full OP
     */
    protected abstract String getMessageType();
}
