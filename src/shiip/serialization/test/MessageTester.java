package shiip.serialization.test;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import shiip.serialization.Message;
import shiip.serialization.Settings;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Performs testing for the {@link shiip.serialization.Message}.
 *
 * @version 1.0
 * @author Ian Laird
 */
@DisplayName("Message Tester")
public class MessageTester {

    private static Encoder encoder = null;
    private static Decoder decoder = null;

    protected static final byte DATA_TYPE = (byte)0x0;
    protected static final byte SETTINGS_TYPE = (byte)0x4;
    protected static final byte WINDOW_UPDATE_TYPE = (byte)0x8;

    private static byte [] TEST_HEADER_1 = {0x0,0x0,0x0,0x0,0x0,0x1,0x0,0x0,0x0,0x0};
    private static byte [] TEST_HEADER_BAD_TYPE  = {(byte)0xEE,0x0,0x0,0x0,0x0,0x1};

    /*
     * an example data frame that has a six byte payload
     * the type is data (0)
     * the flags are 0
     * the stream identifier is one
     * the contents are 0,1,2,3,4,5
     */
    private static byte [] GOOD_DATA_ONE = {0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
    private static Data CORRECT_DATA_ONE = null;

    /*
     * an example data frame that has a six byte payload
     * the type is data (0)
     * the bad flag is set BAD!!
     * the stream identifier is one
     * the contents are 0,1,2,3,4,5
     */
    private static byte [] BAD_DATA_ONE = {0x00, 0x08, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05};

    /*
     * an example data frame that has a six byte payload
     * the type is data (0)
     * no flags are set
     * the stream identifier is zero
     * the contents are 0,1,2,3,4,5
     */
    private static byte [] BAD_DATA_TWO = {0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05};


    /*
     * an example settings frame that has no payload
     * the type is settings (4)
     * the flags are 1
     * the stream identifier is zero
     */
    private static byte [] GOOD_SETTINGS_ONE = {0x04, 0x01, 0x00, 0x00, 0x00, 0x00};
    private static Settings CORRECT_SETTINGS_ONE = null;

    /*
     * an example settings frame that has no payload
     * the type is settings (4)
     * the flags are 1
     * the stream identifier is zero
     * there is a two byte payload
     */
    private static byte [] GOOD_SETTINGS_TWO = {0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /*
     * an example data frame that has no payload
     * the type is settings (4)
     * the flags are 1
     * the stream identifier is one (error)
     */
    private static byte [] BAD_SETTINGS_ONE = {0x04, 0x01, 0x00, 0x00, 0x00, 0x01};

    /*
     * an example data frame that has no payload
     * the type is settings (4)
     * the flags are 0 (error!)
     * the stream identifier is zero
     */
    private static byte [] BAD_SETTINGS_TWO = {0x04, 0x00, 0x00, 0x00, 0x00, 0x00};

    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 4 octets and contains 1
     */
    private static byte [] GOOD_WINDOW_UPDATE_ONE = {0x04, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01};

    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 4 octets and the R bit is set
     */
    private static byte [] GOOD_WINDOW_UPDATE_TWO = {0x04, 0x00, 0x00, 0x00, 0x00, 0x01, 0x08, 0x00, 0x00, 0x01};

    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 3 octets BAD!!! and contains 1
     */
    private static byte [] BAD_WINDOW_UPDATE_ONE = {0x04, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x01};

    @BeforeAll
    public static void main(){
        try {
            CORRECT_DATA_ONE = new Data(1, false, new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05});
            CORRECT_SETTINGS_ONE = new Settings();
        }catch(BadAttributeException e){

        }
    }
    @DisplayName("Decoding Tests")
    public class DecodingTester {
        @DisplayName("testing null msg")
        @Test
        void testNullMsgBytes() {
            assertThrows(BadAttributeException.class,
                    () -> Message.decode​(null, decoder));
        }

        @DisplayName("Invalid Type")
        @Test
        void testInvalidType() {
            assertThrows(BadAttributeException.class, () -> Message.decode​(TEST_HEADER_BAD_TYPE, decoder));
        }

        @DisplayName("Make sure Data frames are properly recognized")
        @Test
        void testDataFrameRecognized() {
            try {
                Message message = Message.decode​(GOOD_DATA_ONE, decoder);
                assertEquals(message.getCode(), DATA_TYPE);
            } catch (BadAttributeException e) {
                fail(e.getMessage());
            }
        }

        @DisplayName("Make sure Data frames are properly read in")
        @Test
        void testDataFrameReadIn() {
            try {
                Message message = Message.decode​(GOOD_DATA_ONE, decoder);
                Data data = (Data) message;
                assertEquals(data, CORRECT_DATA_ONE);
            } catch (BadAttributeException e) {
                fail(e.getMessage());
            }
        }

        @DisplayName("Data Frame with the bad bit set")
        @Test
        void testDataFrameBadBit() {
            assertThrows(BadAttributeException.class, () -> Message.decode​(BAD_DATA_ONE, decoder));
        }

        @DisplayName("Data Frame with zero stream identifier")
        @Test
        void testDataFrameStreamIdentifierZero() {
            assertThrows(BadAttributeException.class, () -> Message.decode​(BAD_DATA_TWO, decoder));
        }

        @DisplayName("Make sure Settings frames are properly recognized")
        @Test
        void testSettingsFrameRecognized() {
            try {
                Message message = Message.decode​(GOOD_SETTINGS_ONE, decoder);
                assertEquals(message.getCode(), SETTINGS_TYPE);
            } catch (BadAttributeException e) {
                fail(e.getMessage());
            }
        }

        @DisplayName("Settings Frame with a payload")
        @Test
        void testSettingsFramePayload() {
            assertDoesNotThrow(() -> Message.decode​(GOOD_SETTINGS_TWO, decoder));
        }

        @DisplayName("Setting frame with bad stream identifier")
        @Test
        void testSettingsFrameBadStreamIdentifier() {
            assertThrows(BadAttributeException.class, () -> Message.decode​(BAD_SETTINGS_ONE, decoder));
        }

        @DisplayName("Setting frame with bad flags (0x0)")
        @Test
        void testSettingsFrameBadFlags() {
            assertThrows(BadAttributeException.class, () -> Message.decode​(BAD_SETTINGS_TWO, decoder));
        }

        @DisplayName("Make sure Window Update frames are properly recognized")
        @Test
        void testWindowUpdateFrameRecognized() {
            try {
                Message message = Message.decode​(GOOD_WINDOW_UPDATE_ONE, decoder);
                assertEquals(message.getCode(), WINDOW_UPDATE_TYPE);
            } catch (BadAttributeException e) {
                fail(e.getMessage());
            }
        }


        @DisplayName("Window Update R bit of payload set")
        @Test
        void testWindowsUpdateRPaylaod() {
            assertDoesNotThrow(() -> Message.decode​(GOOD_WINDOW_UPDATE_TWO, decoder));
        }

        @DisplayName("Window Update too short of payload")
        @Test
        void testWindowsUpdateFrameShort() {
            assertThrows(BadAttributeException.class, () -> Message.decode​(BAD_WINDOW_UPDATE_ONE, decoder));
        }
    }

    @DisplayName("Encoding Tests")
    public class EncodingTests{

    }
}

