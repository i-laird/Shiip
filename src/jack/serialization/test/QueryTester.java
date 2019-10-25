/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class QueryTester {

    private static final String queryStr = "*";
    private static final String queryStr2 = "google";

    @Test
    @DisplayName("Null search string")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> new Query(null));
    }

    @Test
    @DisplayName("equals on equal obj")
    public void testEqualsEqualObj(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr);
        assertEquals(q1, q2);
    }

    @Test
    @DisplayName("equals on unequal obj")
    public void testEqualsUnequalObj(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr2);
        assertNotEquals(q1, q2);
    }

    @Test
    @DisplayName("hashcode")
    public void testHashcode(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr);
        assertEquals(q1.hashCode(), q2.hashCode());
    }
}
