/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 0
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import shiip.serialization.Framer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static shiip.serialization.Framer.*;

/**
 * Performs testing for the {@link Framer}.
 *
 * @version 1.0
 * @author Ian Laird
 */
@DisplayName("Framer Tester")
public class FramerTester {

    public static byte [] TEST_MESSAGE_1 = "hello world :)".getBytes(),
                          TEST_MESSAGE_2 = new byte[MAXIMUM_PAYLOAD_AND_HEADER_SIZE],
                          TEST_MESSAGE_3 = getUTF16("( ͡° ͜ʖ ͡°)"),
                          SIMPLE_TEST_MESSAGE = new byte [] {1,2,3,4,5,6};
    public static byte [] TOO_SHORT_MESSAGE = "hi".getBytes();

    /**
     * Used to test that Io Exceptions are properly handled.
     *
     * @see OutputStream
     * @version 1.0
     * @author Ian laird
     */
    private class BrokenOutputStream extends OutputStream {

        /**
         * Default Constructor. Simply calls the default constructor for a {@link OutputStream}
         */
        BrokenOutputStream(){
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @throws IOException always thrown
         */
        @Override
        public void write(byte[] bytes) throws IOException {
            throw new IOException("Testing that Io Exceptions are correctly handled");
        }

        /**
         * {@inheritDoc}
         *
         * @throws IOException always thrown
         */
        @Override
        public void write(byte[] bytes, int i, int i1) throws IOException {
            throw new IOException("Testing that Io Exceptions are correctly handled");
        }

        /**
         * {@inheritDoc}
         *
         * @throws IOException always thrown
         */
        @Override
        public void write(int i) throws IOException {
            throw new IOException("Testing that Io Exceptions are correctly handled");
        }
    }

    ByteArrayOutputStream outputToBytes = new ByteArrayOutputStream();
    Framer toTest = new Framer(outputToBytes);

    /**
     * Tests that a {@link NullPointerException} is thrown when
     * passing null to the default constructor.
     */
    @DisplayName("frameConstructorNull")
    @Test
    public void frameConstructorNullTest(){
        assertThrows( NullPointerException.class,  () -> new Framer(null));
    }

    /**
     * tests that a {@link NullPointerException} is thrown when
     * the message is null.
     */
    @DisplayName("nullMessage")
    @Test
    public void nullMessageTest(){
        assertThrows( NullPointerException.class,  () -> toTest.putFrame(null));
    }

    /**
     * tests that a {@link IOException} is thrown when the payload is longer
     * than 2048 bytes
     */
    @DisplayName("framePayloadTooLong")
    @Test
    public void framePayloadTooLongTest(){
        byte [] tooLongByteArray = new byte [MAXIMUM_PAYLOAD_AND_HEADER_SIZE + 1];
        assertThrows(IOException.class, () -> toTest.putFrame(tooLongByteArray));
    }

    /**
     * tests that an {@link IOException} is thrown when a header is not included
     * in the message
     */
    @DisplayName("TooShortToContainHeader")
    @Test
    public void testTooShortToContainHeader(){
        /* if the message is not at least 6 bytes longer a header is not present */
        assertThrows(IOException.class, () -> toTest.putFrame(TOO_SHORT_MESSAGE));
    }

    /**
     * tests that incorrectly working {@link OutputStream} work properly
     */
    @DisplayName("IOException")
    @Test
    void testIOException(){
        Framer framer = new Framer(new BrokenOutputStream());
        assertThrows(IOException.class, () -> framer.putFrame(TEST_MESSAGE_1));
    }

    /**
     * Tests the successful framing of a message.
     *
     * @param message         the message to be framed
     * @param testDescription a description of the current test
     */
    @DisplayName("SuccessfulOperation")
    @ParameterizedTest(name = "{1}")
    @MethodSource("passTests")
    public void testSuccessfulOperation(byte [] message, String testDescription ){
        byte [] framedMessage = null;

        //make sure that there are no old bytes stored in the byte array output stream
        outputToBytes.reset();

        try {
            toTest.putFrame(message);
        }catch(IOException e){
            fail("this is bad");
        }

        framedMessage = outputToBytes.toByteArray();

        //make sure that the correct number of bytes have been added
        assertEquals(framedMessage.length, message.length + PREFIX_SIZE);

        //see that the intended header and payload are preserved
        byte [] newHeaderAndPayload = Arrays.copyOfRange(framedMessage, PREFIX_SIZE, PREFIX_SIZE + message.length);
        assertArrayEquals(newHeaderAndPayload, message);

        //now get the 3 byte int from the new message and make sure it is correct
        byte [] prefixLength = Arrays.copyOfRange(framedMessage, 0, PREFIX_SIZE);
        byte [] intBuffer = new byte[4];

        //zero the first byte because the given integer is unsigned
        intBuffer[0] = (byte)0;
        for(int i =1; i < 4; i++){
            intBuffer[i] = prefixLength[i - 1];
        }
        ByteBuffer byteConverter = ByteBuffer.wrap(intBuffer);
        int prefixLen = byteConverter.getInt();
        assertEquals(prefixLen, message.length - HEADER_SIZE);
    }

    /**
     * Uses a hardcoded message to ensure that lengths are being
     * encoded properly.
     */
    @DisplayName("SimpleMessage")
    @Test
    void testSimpleMessage(){
        byte [] expectedBytes = new byte[]{0,0,0,1,2,3,4,5,6};
        this.performFrame(expectedBytes, SIMPLE_TEST_MESSAGE);
    }

    /**
     * tests that the generated frame message matches what is expected
     *
     * @param expectedBytes the expected framed message
     * @param testMessage   the message that is to be framed
     * @return the framed message
     */
    byte [] performFrame(byte [] expectedBytes, byte [] testMessage){
        byte [] framedBytes = null;

        //make sure that there are no old bytes stored in the byte array output stream
        outputToBytes.reset();

        try {
            toTest.putFrame(testMessage);
        }catch(IOException e){
            fail("this is bad");
        }

        framedBytes = outputToBytes.toByteArray();
        assertArrayEquals(framedBytes, expectedBytes);
        return framedBytes;
    }

    /**
     * Creates arguments for the successful execution test
     * @return arguments consisting of a byte array
     *         and String description of the test
     */
    private static Stream<Arguments> passTests(){
        return Stream.of(
                //first test the polynomials
                Arguments.of(TEST_MESSAGE_1,         "simple_test"),
                Arguments.of(TEST_MESSAGE_2,         "maximum payload size test"),
                Arguments.of(TEST_MESSAGE_3,         "UTF-16 test")
        );
    }

    /**
     * returns the byte array of the String in UTF-16
     *
     * @param s the string to be encoded
     * @return the byte array representation of the string
     */
    public static byte [] getUTF16(String s){
        try {
            return s.getBytes("UTF_16");
        }catch(UnsupportedEncodingException e){
            fail("Encoding not found!");
            return null;
        }
    }
}
