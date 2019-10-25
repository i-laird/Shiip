/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Error;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ian Laird and Andrew Walker
 */
public class ErrorTester {

    // an example error message
    private static final String errorStr = "error";

    /**
     * null for error msg
     */
    @Test
    @DisplayName("Null error message")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> new Error(null));
    }

    /**
     * equality testing
     */
    @Nested
    @DisplayName("equality")
    public class TestEquality {

        /**
         * equal obj
         */
        @Test
        @DisplayName("equals on equal obj")
        public void testEqualsEqualObj() {
            Error q1 = new Error(errorStr);
            Error q2 = new Error(errorStr);
            assertEquals(q1, q2);
        }

        /**
         * unequal obj
         */
        @Test
        @DisplayName("equals on unequal obj")
        public void testEqualsUnequalObj() {
            Error q1 = new Error(errorStr);
            Error q2 = new Error(errorStr + errorStr);
            assertNotEquals(q1, q2);
        }
    }

    /**
     * test hashcode
     */
    @Test
    @DisplayName("hashcode")
    public void testHashcode(){
        Error q1 = new Error(errorStr);
        Error q2 = new Error(errorStr);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    /**
     * test tostring
     */
    @Test
    @DisplayName("tostring")
    public void testToString(){
        Error error = new Error(errorStr);
        assertEquals("ERROR " + errorStr, error.toString());
    }
}
