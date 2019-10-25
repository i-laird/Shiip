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

/**
 * @author Ian Laird and Andrew Walker
 */
public class QueryTester {

    // an example query for all
    private static final String queryStr = "*";

    // an example query for google substr
    private static final String queryStr2 = "google";

    /**
     * null search string
     */
    @Test
    @DisplayName("Null search string")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> new Query(null));
    }

    /**
     * equal on equal obj
     */
    @Test
    @DisplayName("equals on equal obj")
    public void testEqualsEqualObj(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr);
        assertEquals(q1, q2);
    }

    /**
     * equals on unequal obj
     */
    @Test
    @DisplayName("equals on unequal obj")
    public void testEqualsUnequalObj(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr2);
        assertNotEquals(q1, q2);
    }

    /**
     * hashcode
     */
    @Test
    @DisplayName("hashcode")
    public void testHashcode(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    /**
     * to string
     */
    @Test
    @DisplayName("tostring")
    public void testToString(){
        Query q1 = new Query(queryStr);
        Query q2 = new Query(queryStr2);
        assertAll(
                () -> assertEquals("QUERY " + queryStr, q1.toString()),
                () -> assertEquals("QUERY " + queryStr2, q2.toString())
        );

    }
}
