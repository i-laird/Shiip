/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import shiip.serialization.*;
import static shiip.serialization.test.TestingConstants.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Performs testing for the {@link shiip.serialization.Message}.
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
@DisplayName("Message Tester")
public class MessageTester {

    private static Encoder encoder = null;
    private static Decoder decoder = null;

    private static byte [] TEST_HEADER_1 =
            {0x0,0x0,0x0,0x0,0x0,0x1,0x0,0x0,0x0,0x0};
    private static byte [] TEST_HEADER_BAD_TYPE  =
            {(byte)0xEE,0x0,0x0,0x0,0x0,0x1};

    /*
     * an example data frame that has a six byte payload
     * the type is data (0)
     * the flags are 0
     * the stream identifier is one
     * the contents are 0,1,2,3,4,5
     */
    private static byte [] GOOD_DATA_ONE =
            {0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05};
    private static Data CORRECT_DATA_ONE = null;
    private static byte [] CORRECT_DATA_ONE_ENCODED = null;

    /*
     * an example data frame that has a six byte payload
     * the type is data (0)
     * the bad flag is set BAD!!
     * the stream identifier is one
     * the contents are 0,1,2,3,4,5
     */
    private static byte [] BAD_DATA_ONE =
            {0x00, 0x08, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05};

    /*
     * an example data frame that has a six byte payload
     * the type is data (0)
     * no flags are set
     * the stream identifier is zero
     * the contents are 0,1,2,3,4,5
     */
    private static byte [] BAD_DATA_TWO =
            {0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05};

    /*
     * an example settings frame that has no payload
     * the type is settings (4)
     * the flags are 1
     * the stream identifier is zero
     */
    private static byte [] GOOD_SETTINGS_ONE =
            {0x04, 0x01, 0x00, 0x00, 0x00, 0x00};
    private static Settings CORRECT_SETTINGS_ONE = null;
    private static byte [] CORRECT_SETTINGS_ENCODED = null;

    /*
     * an example settings frame that has no payload
     * the type is settings (4)
     * the flags are 1
     * the stream identifier is zero
     * there is a two byte payload
     */
    private static byte [] GOOD_SETTINGS_TWO =
            {0x04, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    /*
     * an example data frame that has no payload
     * the type is settings (4)
     * the flags are 1
     * the stream identifier is one (error)
     */
    private static byte [] BAD_SETTINGS_ONE =
            {0x04, 0x01, 0x00, 0x00, 0x00, 0x01};

    /*
     * an example data frame that has no payload
     * the type is settings (4)
     * the flags are 0 (error!)
     * the stream identifier is zero
     */
    private static byte [] BAD_SETTINGS_TWO =
            {0x04, 0x00, 0x00, 0x00, 0x00, 0x00};

    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 4 octets and contains 1
     */
    private static byte [] GOOD_WINDOW_UPDATE_ONE =
            {0x08, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01};
    private static Window_Update CORRECT_WINDOW_UPDATE_ONE = null;
    private static byte [] CORRECT_WINDOw_UPDATE_ENCODED = null;


    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 4 octets and the R bit is set
     */
    private static byte [] GOOD_WINDOW_UPDATE_TWO =
            {0x08, 0x00, 0x00, 0x00, 0x00, 0x01, 0x08, 0x00, 0x00, 0x01};

    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 3 octets BAD!!! and contains 1
     */
    private static byte [] BAD_WINDOW_UPDATE_ONE =
            {0x08, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x01};

    /**
     * init the static objects
     */
    @BeforeAll
    public static void initialize(){
        try {
            CORRECT_DATA_ONE =
                    new Data(1,
                            false, new byte[]
                            {0x00, 0x01, 0x02, 0x03, 0x04, 0x05});
            CORRECT_SETTINGS_ONE =
                    new Settings();
            CORRECT_WINDOW_UPDATE_ONE =
                    new Window_Update(1,1);

            CORRECT_DATA_ONE_ENCODED =
                    CORRECT_DATA_ONE.encode(null);
            CORRECT_SETTINGS_ENCODED =
                    CORRECT_SETTINGS_ONE.encode(null);
            CORRECT_WINDOw_UPDATE_ENCODED =
                    CORRECT_WINDOW_UPDATE_ONE.encode(null);
        }catch(BadAttributeException e){

        }
    }

    /**
     * Performs decoding a {@link shiip.serialization.Message}.
     *
     * @version 1.0
     * @author Ian Laird, Andrew Walker
     */
    @Nested
    @DisplayName("Decoding Tests")
    public class DecodingTester {

        /**
         * null msg
         */
        @DisplayName("testing null msg")
        @Test
        void testNullMsgBytes() {
            assertThrows(NullPointerException.class,
                    () -> Message.decode​(null, decoder));
        }

        /**
         * invalid message type
         */
        @DisplayName("Invalid Type")
        @Test
        void testInvalidType() {
            assertThrows(BadAttributeException.class,
                    () -> Message.decode​(TEST_HEADER_BAD_TYPE, decoder));
        }

        /**
         * data frame type recognized
         */
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

        /**
         * data frame decoding
         */
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

        /**
         * bad bit in data frame
         */
        @DisplayName("Data Frame with the bad bit set")
        @Test
        void testDataFrameBadBit() {
            assertThrows(BadAttributeException.class,
                    () -> Message.decode​(BAD_DATA_ONE, decoder));
        }

        /**
         * data frame stream id is 0
         */
        @DisplayName("Data Frame with zero stream identifier")
        @Test
        void testDataFrameStreamIdentifierZero() {
            assertThrows(BadAttributeException.class,
                    () -> Message.decode​(BAD_DATA_TWO, decoder));
        }

        /**
         * setting frame id
         */
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

        /**
         * settings frame has a payload
         */
        @DisplayName("Settings Frame with a payload")
        @Test
        void testSettingsFramePayload() {
            assertDoesNotThrow(
                    () -> Message.decode​(GOOD_SETTINGS_TWO, decoder));
        }

        /**
         * settings frame invalid stream id
         */
        @DisplayName("Setting frame with bad stream identifier")
        @Test
        void testSettingsFrameBadStreamIdentifier() {
            assertThrows(BadAttributeException.class,
                    () -> Message.decode​(BAD_SETTINGS_ONE, decoder));
        }

        /**
         * window update id
         */
        @DisplayName("Make sure Window Update frames are properly recognized")
        @Test
        void testWindowUpdateFrameRecognized() {
            try {
                Message message =
                        Message.decode​(GOOD_WINDOW_UPDATE_ONE, decoder);
                assertEquals(message.getCode(), WINDOW_UPDATE_TYPE);
            } catch (BadAttributeException e) {
                fail(e.getMessage());
            }
        }

        /**
         * window update r bit test
         */
        @DisplayName("Window Update R bit of payload set")
        @Test
        void testWindowsUpdateRPaylaod() {
            assertDoesNotThrow(
                    () -> Message.decode​(GOOD_WINDOW_UPDATE_TWO, decoder));
        }

        /**
         * window update payload too short
         */
        @DisplayName("Window Update too short of payload")
        @Test
        void testWindowsUpdateFrameShort() {
            assertThrows(BadAttributeException.class,
                    () -> Message.decode​(BAD_WINDOW_UPDATE_ONE, decoder));
        }
    }

    /**
     * Performs encoding a {@link shiip.serialization.Message}.
     *
     * @version 1.0
     * @author Ian Laird, Andrew Walker
     */
    @Nested
    @DisplayName("Encoding Tests")
    public class EncodingTests{

        /**
         * Performs flags tests
         *
         * @version 1.0
         * @author Ian Laird, Andrew Walker
         */
        @Nested
        public class FlagsTests{

            /**
             * data flags unset
             */
            @DisplayName("Test all data flags are unset")
            @Test
            public void testDataFlagsUnset(){
                assertEquals(NO_FLAGS,
                        CORRECT_DATA_ONE_ENCODED[FLAG_POS_IN_HEADER]);
            }

            /**
             * settings flags unset
             */
            @DisplayName("Test all settings flags are unset")
            @Test
            public void testSettingsFlagsUnset(){
                assertEquals(REQUIRED_SETTINGS_FLAGS_SERIALIZATION,
                        CORRECT_SETTINGS_ENCODED[FLAG_POS_IN_HEADER]);
            }

            /**
             * window update flags unset
             */
            @DisplayName("Test all Window_Update flags are unset")
            @Test
            public void testWindowUpdateFlagsUnset(){
                assertEquals(NO_FLAGS,
                        CORRECT_WINDOw_UPDATE_ENCODED[FLAG_POS_IN_HEADER]);
            }

        }

        /**
         * null for the encoder
         */
        @Test
        public void testNullEncoder(){
            assertDoesNotThrow(()->CORRECT_DATA_ONE.encode(null));
        }

        /**
         * testing the r bit
         */
        @DisplayName("R bit stays upset when sending all message types")
        @Test
        public void testRBit(){
            assertAll(
                    () -> assertEquals(
                            (byte)(CORRECT_DATA_ONE_ENCODED
                                    [BYTE_CONTAINING_R_BIT_LOCATION]
                                    & HIGHEST_BIT_IN_BYTE),
                            (byte)0),
                    () -> assertEquals(
                            (byte)(CORRECT_SETTINGS_ENCODED
                                    [BYTE_CONTAINING_R_BIT_LOCATION]
                                    & HIGHEST_BIT_IN_BYTE),
                            (byte)0),
                    () -> assertEquals((byte)(CORRECT_WINDOw_UPDATE_ENCODED
                                    [BYTE_CONTAINING_R_BIT_LOCATION]
                                    & HIGHEST_BIT_IN_BYTE),
                            (byte)0)
            );
        }

        /**
         * second r bit in the window update
         */
        @DisplayName("Additional R bit stays unset Window_Update")
        @Test
        public void testSecondRBitWindowUpdate(){
            assertEquals((byte)(CORRECT_WINDOw_UPDATE_ENCODED
                            [BYTE_CONTAINING_SECOND_R_BIT_WINDOW_UPDATE]
                            & HIGHEST_BIT_IN_BYTE),
                    (byte)0);
        }

        /**
         * data encoding
         */
        @DisplayName("Test Data encoding is being performed properly")
        @Test
        public void testDataEncoding(){
            assertArrayEquals(GOOD_DATA_ONE, CORRECT_DATA_ONE_ENCODED);
        }

        /**
         * settings encoding
         */
        @DisplayName("Test Settings encoding is being performed properly")
        @Test
        public void testSettingsEncoding(){
            assertArrayEquals(GOOD_SETTINGS_ONE, CORRECT_SETTINGS_ENCODED);
        }

        /**
         * window update encoding
         */
        @DisplayName("Test Window_Update encoding is being performed properly")
        @Test
        public void testWindowUpdateEncoding(){
            assertArrayEquals(GOOD_WINDOW_UPDATE_ONE,
                    CORRECT_WINDOw_UPDATE_ENCODED);
        }
    }
}

