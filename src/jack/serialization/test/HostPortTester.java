/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.ACK;
import jack.serialization.Message;
import jack.serialization.New;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jack.serialization.test.ResponseTester.VALID_PORT;
import static org.junit.jupiter.api.Assertions.*;

public abstract class HostPortTester {

    private static final String testString = "localhost";

    @Test
    @DisplayName("Null host")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> getNewObject(null, VALID_PORT ));
    }

    @Test
    @DisplayName("host setter")
    public void testHostSetter(){
        String testString = "localhost";
        assertEquals(testHostSetter(testString), testString);
    }

    @Test
    @DisplayName("port setter")
    public void testPortSetter(){
        assertEquals(testPortSetter(VALID_PORT), VALID_PORT);
    }

    @Test
    @DisplayName("to string")
    public void testToString(){
        Message m = getNewObject(testString, VALID_PORT);
        String expectedString = getMessageType() + " [" + testString + ":" + VALID_PORT + "]";
        assertEquals(expectedString, m.toString());
    }

    @Test
    @DisplayName("equals on equal objects")
    public void testEqualsEqualObjects(){
        Message m1 = getNewObject(testString, VALID_PORT);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertEquals(m1, m2);
    }

    @Test
    @DisplayName("equals on unequal port")
    public void testEqualsUnequalPort(){
        Message m1 = getNewObject(testString, VALID_PORT + 1);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertNotEquals(m1, m2);
    }

    @Test
    @DisplayName("equals on unequal host")
    public void testEqualsUnequalHost(){
        Message m1 = getNewObject(testString + testString, VALID_PORT);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertNotEquals(m1, m2);
    }

    @Test
    @DisplayName("hashcode")
    public void testHashcodeEqualObjects(){
        Message m1 = getNewObject(testString, VALID_PORT);
        Message m2 = getNewObject(testString, VALID_PORT);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    protected abstract Message getNewObject(String host, int port);

    protected abstract String testHostSetter(String s);

    protected abstract int testPortSetter(int port);

    protected abstract String getMessageType();
}
