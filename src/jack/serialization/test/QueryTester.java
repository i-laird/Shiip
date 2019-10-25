/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class QueryTester {

    @Test
    @DisplayName("Null search string")
    public void testNull(){
        assertThrows(IllegalArgumentException.class, () -> new Query(null));
    }
}
