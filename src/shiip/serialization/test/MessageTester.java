package shiip.serialization.test;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.BadAttributeException;
import shiip.serialization.Message;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Performs testing for the {@link shiip.serialization.Message}.
 *
 * @version 1.0
 * @author Ian Laird
 */
@DisplayName("Message Tester")
public class MessageTester {

    private static final int MAX_HEADER_TABLE_SIZE = 4096,
                             MAX_HEADER_SIZE = 6;
    private static Decoder decoder = null;
    //private static Encoder encoder = null;

    private static byte [] TEST_HEADER_1 = new byte[]{0x0,0x0,0x0,0x0,0x0,0x1,0x0,0x0,0x0,0x0},
                           TEST_HEADER_BAD_TYPE  = new byte[]{(byte)0xEE,0x0,0x0,0x0,0x0,0x1};

    @BeforeAll
    static void initialize(){
        decoder = new Decoder(MAX_HEADER_SIZE, MAX_HEADER_TABLE_SIZE);
       // encoder = new Encoder(MAX_HEADER_TABLE_SIZE);
    }

    @DisplayName("testing null msg")
    @Test
    void testNullMsgBytes(){
        assertThrows(BadAttributeException.class,
                ()-> Message.decode​(null, decoder));
    }

    @DisplayName("Decoder null and needed")
    @Test
    void testEncoderNullAndNeeded(){
        assertThrows(NullPointerException.class, () -> Message.decode​(TEST_HEADER_1, decoder));
    }

    @DisplayName("Invalid Type")
    void testInvalidType(){
        assertThrows(BadAttributeException.class, () -> Message.decode​(TEST_HEADER_BAD_TYPE, decoder));
    }
}
