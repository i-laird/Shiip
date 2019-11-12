/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Message;
import jack.serialization.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Donahoo
 */
@DisplayName("Simple ResponseTest")
public class SimpleResponseTester {

    // the charset
    private static final Charset ENC = StandardCharsets.US_ASCII;

    /**
     * encode
     */
    @DisplayName("encode")
    @Test
    void testEncode() {
        var m = new Response();
        m.addService("b", 3);
        m.addService("a", 5);
        m.addService("b", 3);
        byte[] expEnc = "R a:5 b:3 ".getBytes(ENC);
        assertArrayEquals(expEnc, m.encode());
    }

    /**
     * decode
     * @throws IOException for a network error
     */
    @DisplayName("decode")
    @Test
    void testDecode() throws IOException {
        byte[] enc = "R b:3 a:5 b:3 ".getBytes(ENC);
        Response m = (Response) Message.decode(enc);
        List expSvc = Arrays.asList("a:5", "b:3");
        assertIterableEquals(expSvc, m.getServiceList());
    }
}