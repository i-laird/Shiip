/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ian Laird and Andrew Walker
 */
public class ResponseTester {

    // a valid port
    public static int VALID_PORT = 1;

    // not an allowed port num
    public static int PORT_TOO_LOW = 0;

    // not an allowed port num
    public static int PORT_TOO_HIGH = 65536;

    /**
     * tests a null host
     */
    @Test
    @DisplayName("Null host")
    public void testNullHost(){
        Response response = new Response();
        assertThrows(IllegalArgumentException.class, () -> response.addService(null, VALID_PORT));
    }

    /**
     * bad port vals
     */
    @Test
    @DisplayName("Bad port vals")
    public void testBadPorts(){
        Response response = new Response();
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> response.addService("google", PORT_TOO_LOW)),
                () -> assertThrows(IllegalArgumentException.class, () -> response.addService("google", PORT_TOO_HIGH))
        );
    }

    /**
     * service list one elem
     */
    @Test
    @DisplayName("test service list one element")
    public void testServiceList(){
        Response response = new Response();
        response.addService("localhost", VALID_PORT);
        assertEquals(1, response.getServiceList().size());
    }

    /**
     * test service list dup elem
     */
    @Test
    @DisplayName("test service list dup element")
    public void testServiceListDup(){
        Response response = new Response();
        response.addService("localhost", VALID_PORT);
        response.addService("localhost", VALID_PORT);
        assertEquals(1, response.getServiceList().size());
    }

    /**
     * to string
     */
    @Nested
    @DisplayName("to string")
    public class testToString{

        /**
         * no elem
         */
        @Test
        @DisplayName("no elem")
        public void testToStringEmpty(){
            Response response = new Response();
            String expected = "RESPONSE ";
            assertEquals(expected, response.toString());
        }

        /**
         * one elem
         */
        @Test
        @DisplayName("one elem")
        public void testToStringOne(){
            Response response = new Response();
            response.addService("localhost", VALID_PORT);
            String expected = "RESPONSE [localhost:" + VALID_PORT + "]";
            assertEquals(expected, response.toString());
        }

        /**
         * two elem non alphabetically
         */
        @Test
        @DisplayName("two elem (entered non alphabetical)")
        public void testToStringTwo(){
            Response response = new Response();
            response.addService("localhost", VALID_PORT);
            response.addService("aaa", VALID_PORT);
            String port = Integer.toString(VALID_PORT);
            String expected = "RESPONSE [aaa:" + port + "][localhost:" + port + "]";
            assertEquals(expected, response.toString());
        }

        /**
         * two elem entered alphabetically
         */
        @Test
        @DisplayName("two elem (entered alphabetical)")
        public void testToStringTwoAlphabetical(){
            Response response = new Response();
            response.addService("aaa", VALID_PORT);
            response.addService("localhost", VALID_PORT);
            String port = Integer.toString(VALID_PORT);
            String expected = "RESPONSE [aaa:" + port + "][localhost:" + port + "]";
            assertEquals(expected, response.toString());
        }

    }

    /**
     * equality
     */
    @Nested
    @DisplayName("equality")
    public class TestEquality{

        /**
         * empty response
         */
        @Test
        @DisplayName("empty response")
        public void testEmptyResponse(){
            Response r1 = new Response();
            Response r2 = new Response();
            assertEquals(r1, r2);
        }

        /**
         * equal with services
         */
        @Test
        @DisplayName("equal with services")
        public void testEqualServices(){
            Response r1 = new Response();
            Response r2 = new Response();
            r1.addService("aaa", VALID_PORT);
            r2.addService("aaa", VALID_PORT);
            assertEquals(r1, r2);
        }

        /**
         * unequal with services
         */
        @Test
        @DisplayName("unequal with services")
        public void testUnequalServices(){
            Response r1 = new Response();
            Response r2 = new Response();
            r1.addService("aaa", VALID_PORT);
            assertNotEquals(r1, r2);
        }
    }

    /**
     * hash code
     */
    @Nested
    @DisplayName("hash code")
    public class TestHashcode {

        /**
         * hashcode empty response
         */
        @Test
        @DisplayName("hashcode empty response")
        public void testHashCode() {
            Response r1 = new Response();
            Response r2 = new Response();
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        /**
         * hashcode with services
         */
        @Test
        @DisplayName("hashcode with services")
        public void testEqualServices(){
            Response r1 = new Response();
            Response r2 = new Response();
            r1.addService("aaa", VALID_PORT);
            r2.addService("aaa", VALID_PORT);
            assertEquals(r1.hashCode(), r2.hashCode());
        }
    }

    /**
     * oversized payload
     */
    @Test
    public void testOversizedPayload(){
        Response response = new Response();
        // 65,507 - 2 = 65,505
        for(int i = 0; i < 6550; i++){
            String host = String.valueOf(i);
            int originalLength = host.length();
            for(int j = 0; j < 7 - originalLength; j++){
                host = host.concat("A");
            }
            response.addService(host, 1);
        }

        assertThrows(IllegalArgumentException.class, () -> {
            response.addService("AAAA", 1);
        });
    }
}
