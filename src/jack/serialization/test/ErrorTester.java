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

public class ErrorTester {

    private static final String errorStr = "error";

    @Test
    @DisplayName("Null error message")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> new Error(null));
    }

    @Nested
    @DisplayName("equality")
    public class TestEquality {

        @Test
        @DisplayName("equals on equal obj")
        public void testEqualsEqualObj() {
            Error q1 = new Error(errorStr);
            Error q2 = new Error(errorStr);
            assertEquals(q1, q2);
        }

        @Test
        @DisplayName("equals on unequal obj")
        public void testEqualsUnequalObj() {
            Error q1 = new Error(errorStr);
            Error q2 = new Error(errorStr + errorStr);
            assertNotEquals(q1, q2);
        }
    }

    @Test
    @DisplayName("hashcode")
    public void testHashcode(){
        Error q1 = new Error(errorStr);
        Error q2 = new Error(errorStr);
        assertEquals(q1.hashCode(), q2.hashCode());
    }
}
