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
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import shiip.serialization.*;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static shiip.serialization.test.TestingConstants.*;


/**
 * Performs testing for the {@link Message}.
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
public class MessageTester {

    // the encoder instances for the tests
    private static Encoder encoder = null, encoder2 = null;

    // the decoder instance for the tests
    private static Decoder decoder = null;


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

    // max header size for encoder and decoder
    private static int MAX_HEADER_SIZE = 1024;

    // max header table size
    private static int MAX_HEADER_TABLE_SIZE = 1024;

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
     * the stream identifier is max value
     * the payload is 4 octets and the R bit is set and increment is max value
     */
    private static byte [] GOOD_WINDOW_UPDATE_THREE =
            {0x08, 0x00, (byte)0xff, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF };

    /*
     * an example window update frame
     * the type is 8
     * the flags are 0
     * the stream identifier is one
     * the payload is 3 octets BAD!!! and contains 1
     */
    private static byte [] BAD_WINDOW_UPDATE_ONE =
            {0x08, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x01};

    /*
     * an example data header
     * the type is headers (1)
     * HDR is set
     * the stream identifier is one
     */
    private static byte [] GOOD_HEADERS_HEADER_ONE = {0x01, 0x04, 0x00, 0x00, 0x00, 0x01};

    /*
     * an example data header
     * the type is headers (1)
     * HDR is set and end stream
     * the stream identifier is three
     */
    private static byte [] GOOD_HEADERS_HEADER_TWO = {0x01, 0x05, 0x00, 0x00, 0x00, 0x03};

    /**
     * init the static objects
     */
    @BeforeAll
    public static void initialize(){
        assertDoesNotThrow(() -> {
            CORRECT_DATA_ONE = new Data(1,false,
                    new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05});
            CORRECT_SETTINGS_ONE = new Settings();
            CORRECT_WINDOW_UPDATE_ONE = new Window_Update(1,1);

            CORRECT_DATA_ONE_ENCODED =
                    CORRECT_DATA_ONE.encode(null);
            CORRECT_SETTINGS_ENCODED =
                    CORRECT_SETTINGS_ONE.encode(null);
            CORRECT_WINDOw_UPDATE_ENCODED =
                    CORRECT_WINDOW_UPDATE_ONE.encode(null);

            // encode header list into header block
            encoder = new Encoder(MAX_HEADER_TABLE_SIZE);
            encoder2 = new Encoder(MAX_HEADER_TABLE_SIZE);
            decoder = new Decoder(MAX_HEADER_SIZE, MAX_HEADER_TABLE_SIZE);

        });
    }

    /**
     * Performs decoding a {@link Message}.
     *
     * @version 1.0
     * @author Ian Laird, Andrew Walker
     */
    @Nested
    @DisplayName("decode")
    public class Decode {

        /**
         * null msg
         */
        @DisplayName("Null")
        @Test
        void testNullMsgBytes() {
            assertThrows(NullPointerException.class, () -> {
                Message.decode(null, decoder);
            });
        }

        /**
         * invalid message type
         */
        @DisplayName("Invalid Type")
        @Test
        void testInvalidType() {
            assertThrows(BadAttributeException.class,() -> {
                Message.decode(TEST_HEADER_BAD_TYPE, decoder);
            });
        }

        /**
         *
         */
        @Nested
        @DisplayName("Data")
        public class DataType{

            /**
             * Data Type
             */
            @DisplayName("Valid Type")
            @Test
            void testDataFrameRecognized() {
                assertDoesNotThrow(() -> {
                    Message message = Message.decode(GOOD_DATA_ONE, decoder);
                    assertNotNull(message);
                    assertEquals(message.getCode(), DATA_TYPE);
                });
            }

            /**
             * Valid Data frame
             */
            @DisplayName("Valid Frame")
            @Test
            void testDataFrameReadIn() {
                assertDoesNotThrow(() -> {
                    Message message = Message.decode(GOOD_DATA_ONE, decoder);
                    Data data = (Data) message;
                    assertEquals(data, CORRECT_DATA_ONE);
                });
            }

            /**
             * Data with bad bit set
             */
            @DisplayName("Bad Bit Set")
            @Test
            void testDataFrameBadBit() {
                assertThrows(BadAttributeException.class, () -> {
                    Message.decode(BAD_DATA_ONE, decoder);
                });
            }

            /**
             * Data with streamID of 0
             */
            @DisplayName("Bad streamID")
            @Test
            void testDataFrameStreamIdentifierZero() {
                assertThrows(BadAttributeException.class, () -> {
                    Message.decode(BAD_DATA_TWO, decoder);
                });
            }
        }

        @Nested
        @DisplayName("Settings")
        public class SettingsType {
            /**
             * Settings Type
             */
            @DisplayName("Valid Type")
            @Test
            void testSettingsFrameRecognized() {
                assertDoesNotThrow(() -> {
                    Message message = Message.decode(GOOD_SETTINGS_ONE, decoder);
                    assertNotNull(message);
                    assertEquals(message.getCode(), SETTINGS_TYPE);
                });
            }

            /**
             * Valid Settings with payload
             */
            @DisplayName("Valid with Payload")
            @Test
            void testSettingsFramePayload() {
                assertDoesNotThrow(() -> {
                    Message.decode(GOOD_SETTINGS_TWO, decoder);
                });
            }

            /**
             * Invalid streamID
             */
            @DisplayName("Bad StreamID")
            @Test
            void testSettingsFrameBadStreamIdentifier() {
                assertThrows(BadAttributeException.class, () -> {
                    Message.decode(BAD_SETTINGS_ONE, decoder);
                });
            }

            /**
             * Invalid Flags
             */
            @DisplayName("Bad Flags")
            @Test
            void testSettingsFrameBadFlags() {
                assertDoesNotThrow(() -> {
                    Message.decode(BAD_SETTINGS_TWO, decoder);
                });
            }
        }

        /**
         *
         */
        @Nested
        @DisplayName("Window_Update")
        public class WindowUpdateType {

            /**
             * Window_Update Type
             */
            @DisplayName("Valid Type")
            @Test
            void testWindowUpdateFrameRecognized() {
                assertDoesNotThrow(() -> {
                    Message message =
                            Message.decode(GOOD_WINDOW_UPDATE_ONE, decoder);
                    assertEquals(message.getCode(), WINDOW_UPDATE_TYPE);
                });
            }

            /**
             * R bit
             */
            @DisplayName("R Bit")
            @Test
            void testWindowsUpdateRPaylaod() {
                assertDoesNotThrow(() -> {
                    Message.decode(GOOD_WINDOW_UPDATE_TWO, decoder);
                });
            }

            /**
             * R bit and max increment size
             */
            @DisplayName("Big Increment")
            @Test
            void testWindowsUpdateMaxIncrement() {
                assertDoesNotThrow(() -> {
                    Window_Update wu = (Window_Update) Message.decode(GOOD_WINDOW_UPDATE_THREE, decoder);
                    assertEquals(wu.getIncrement(), LARGEST_INT);
                    assertEquals(wu.getStreamID(), LARGEST_INT);
                });
            }

            /**
             * Payload too short
             */
            @DisplayName("Missing Payload")
            @Test
            void testWindowsUpdateFrameShort() {
                assertThrows(BadAttributeException.class, () -> {
                    Message.decode(BAD_WINDOW_UPDATE_ONE, decoder);
                });
            }
        }
    }

    /**
     * Performs encoding a {@link Message}.
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
            @DisplayName("Data Flags Unset")
            @Test
            public void testDataFlagsUnset(){
                assertEquals(NO_FLAGS,
                        CORRECT_DATA_ONE_ENCODED[FLAG_POS_IN_HEADER]);
            }

            /**
             * settings flags unset
             */
            @DisplayName("Settings Flags Unset")
            @Test
            public void testSettingsFlagsUnset(){
                assertEquals(REQUIRED_SETTINGS_FLAGS_SERIALIZATION,
                        CORRECT_SETTINGS_ENCODED[FLAG_POS_IN_HEADER]);
            }

            /**
             * window update flags unset
             */
            @DisplayName("Window_Update Flags Unset")
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
        @DisplayName("Null Encoder")
        public void testNullEncoder(){
            assertDoesNotThrow(()->CORRECT_DATA_ONE.encode(null));
        }

        /**
         * testing the r bit
         */
        @DisplayName("R Bit Unset")
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
        @DisplayName("R Bit Unset - Window_Update")
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
        @DisplayName("Valid Data Encoding")
        @Test
        public void testDataEncoding(){
            assertArrayEquals(GOOD_DATA_ONE, CORRECT_DATA_ONE_ENCODED);
        }

        /**
         * settings encoding
         */
        @DisplayName("Valid Settings Encoding")
        @Test
        public void testSettingsEncoding(){
            assertArrayEquals(GOOD_SETTINGS_ONE, CORRECT_SETTINGS_ENCODED);
        }

        /**
         * window update encoding
         */
        @DisplayName("Valid Window_Update Encoding")
        @Test
        public void testWindowUpdateEncoding(){
            assertArrayEquals(GOOD_WINDOW_UPDATE_ONE,
                    CORRECT_WINDOw_UPDATE_ENCODED);
        }

        /**
         * @author Ian laird, Andrew Walker
         * tests headers
         */
        @Nested
        @DisplayName("Headers tester")
        public class HeaderTester{

            /**
             * tests headers source
             * @param streamId the stream id to test
             * @param options the name and value pairs
             * @param headerPlusPayload the encoded expectation
             */
            @ParameterizedTest(name = "streamID = {0}, encoded = {2}")
            @DisplayName("Encoding Tests")
            @ArgumentsSource(MessageArgs.class)
            public void testHeadersEncoding(int streamId, Map<String, String> options, byte [] headerPlusPayload){
                assertDoesNotThrow(() -> {
                    Headers h = new Headers(streamId, false);
                    for (Map.Entry<String, String> entry : options.entrySet()) {
                        h.addValue(entry.getKey(), entry.getValue());
                    }
                    byte [] generated = h.encode(encoder2);
                    Headers reGenerated = (Headers) Message.decode(generated, decoder);
                    assertEquals(h, reGenerated);
                });
            }
        }

        /**
         * window update encoding
         */
        @DisplayName("Valid Window_Update Encoding")
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4, 5})
        public void testMalformedHeader(int headerSize){
            assertThrows(BadAttributeException.class, () -> {
                Message.decode(new byte[headerSize], null);
            });
        }

    }

    /**
     * @author Ian Laird, Andrew Walker
     * provides arguments for Message tests
     */
    static class MessageArgs implements ArgumentsProvider {

        /**
         * provide arguments
         */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            List<Integer> validStreamIDs = Arrays.asList( 1, 2876);
            List<Map<String, String>> validPayloads = Arrays.asList(
                    new TreeMap<>(),
                    Map.of(":method", "GET"),
                    Map.of(":method", "POST", ":version", "HTTP/2.0"),
                    Map.of(":host", "duckduckgo.com", ":method", "PUT", ":scheme", "https")
            );

            return validStreamIDs
                    .stream()
                    .flatMap(streamID ->

                            validPayloads
                                    .stream()
                                    .map(( payload) -> {
                                        return Arguments.of(streamID, payload, mergeTwoArrays(expectedHeader(streamID), compress(payload)));
                                    })

                    );
        }

        /**
         * gives the expected header for the given stream id
         * @param streamid stream id
         * @return the expected header
         */
        private byte [] expectedHeader(int streamid){
            return ByteBuffer.allocate(HEADER_SIZE).put(HEADERS_TYPE).put((byte)0x04).putInt(streamid).array();
        }

        /**
         * compresses a map into a header block
         * @param map the map of name and value pairs
         * @return the header block
         */
        private byte [] compress(Map<String, String> map){
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    encoder.encodeHeader(out, entry.getKey().getBytes(StandardCharsets.US_ASCII), entry.getValue().getBytes(StandardCharsets.US_ASCII), false);
                }
                return out.toByteArray();
            }catch(Exception e){}
            return null;
        }

        /**
         * concats two arrays
         * @param first first array
         * @param second second array
         * @return first plus second
         */
        private byte [] mergeTwoArrays(byte [] first, byte [] second){
            byte [] toReturn = new byte [first.length + second.length];
            int count = 0;
            for(int  i = 0; i < first.length; i++){
                toReturn[count++] = first[i];
            }
            for(int  i = 0; i < second.length; i++){
                toReturn[count++] = second[i];
            }
            return toReturn;
        }
    }
}

