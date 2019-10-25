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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MiscTester {
    private static final String testString = "localhost";

    @Test
    @DisplayName("equal new and ack")
    public void testNewAckSameValues(){
        Message m1 = new New(testString, VALID_PORT);
        Message m2 = new ACK(testString, VALID_PORT);
        assertNotEquals(m1, m2);
    }
}
