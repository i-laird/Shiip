/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 0
 * Class: Data Comm
 *******************************************************/


package shiip.serialization.test;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import shiip.serialization.Deframer;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performs testing for the {@link Deframer}.
 *
 * @version 1.0
 * @author Ian Laird
 */
@DisplayName("Deframer Tester")
public class DeframerTester {

    public static final int PREFIX_SIZE = 3;
    public static final int HEADER_SIZE = 6;

    // public static final String TEST_STRING_1 = "how are you?";
    // public static byte [] TEST_BYTE_ARRAY_1 = null;
    public static final String [] TEST_STRINGS =
            {"how are you",
             "hi \n\n\n\n\n ????",
             "abcdef"};
    public static byte [] [] BYTE_REPRESENTATIONS_OF_STRINGS
                             = new byte [TEST_STRINGS.length] [];

    public static final byte MAX_BYTE = (byte)0xFF;

    /**
     * Used to test that Io Exceptions are properly handled.
     *
     * @see InputStream
     * @version 1.0
     * @author Ian laird
     */
    private class BrokenInputStream extends InputStream{
        /* An IOException is thrown whenever a read is attempted */

        /**
         * Default Constructor. Simply calls the default constructor
         *     for a {@link InputStream}
         */
        BrokenInputStream(){
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @throws IOException always thrown
         */
        @Override
        public synchronized int read() throws IOException{
            throw new IOException("testing if IO exception is accounted for");
        }

        /**
         * {@inheritDoc}
         *
         * @throws IOException always thrown
         */
        @Override
        public synchronized int
                read(byte[] bytes, int i, int i1) throws IOException{
            throw new IOException("testing if IO exception is accounted for");
        }
    }

    /**
     * Creates the byte representations of all test strings.
     */
    @BeforeAll
    public static void initializeTestStrings(){
        int count = 0;
        for(String toConvert : TEST_STRINGS) {
            byte[] byteArrayOfLength =
                    ByteBuffer.allocate(4).putInt(toConvert.length() - HEADER_SIZE).array();
            byte[] stringAsBytes = toConvert.getBytes();
            byte[] builtMessage = new byte[stringAsBytes.length + PREFIX_SIZE];
            for (int i = 1; i < PREFIX_SIZE; i++) {
                builtMessage[i] = byteArrayOfLength[i + 1];
            }
            for (int i = 0; i < stringAsBytes.length; i++) {
                builtMessage[i + PREFIX_SIZE] = stringAsBytes[i];
            }
            BYTE_REPRESENTATIONS_OF_STRINGS[count] = builtMessage;
            count++;
        }
    }

    /**
     * Tests passing null to the default constructor.
     */
    @DisplayName("Passing null to the constructor")
    @Test
    public void testConstructorNull(){
        assertThrows(
                NullPointerException.class,  () -> new Deframer(null));
    }

    /**
     * tests that messages can be correctly deframed
     *
     * @see Deframer
     * @param testString the String that is being tested
     * @param byteRepresentationOfStrings framed testString
     * @param displayName the name to be displayed for the test
     */
    @DisplayName("correct usage")
    @ParameterizedTest(name = "{2}")
    @MethodSource("testsSource")
    public void testCorrectUsage(String testString,
                byte [] byteRepresentationOfStrings, String displayName){
        Deframer deframer = new Deframer(
                new ByteArrayInputStream(byteRepresentationOfStrings));
        try {
            byte[] deframedMessage = deframer.getFrame();
            assertArrayEquals(deframedMessage, testString.getBytes());
        }catch(IOException e){
            fail(e.getMessage());
        }
    }

    /**
     * Tests that a {@link EOFException} is thrown when the input stream has
     * an early EOF.
     */
    @DisplayName("Early EOF")
    @Test
    void testEarlyEOF(){

        //take one of the strings and intentionally make the count too big
        byte [] badBytes = BYTE_REPRESENTATIONS_OF_STRINGS[0].clone();
        badBytes[2] += 1;
        Deframer deframer = new Deframer(new ByteArrayInputStream(badBytes));
        assertThrows(EOFException.class, deframer::getFrame);
    }

    /**
     * Tests that a {@link EOFException} is thrown when the input stream has too short of a header
     */
    @DisplayName("5 byte header")
    @Test
    void testShortHeader(){
        byte [] shortHeader = new byte[]{0,0,0,1,2,3,4,5};
        Deframer deframer = new Deframer(new ByteArrayInputStream(shortHeader));
        assertThrows(EOFException.class, deframer::getFrame);
    }

    /**
     * Tests that a {@link EOFException} is thrown when the input stream is
     * empty.
     */
    @DisplayName("Empty Input Stream")
    @Test
    void testEmptyInputStream(){
        Deframer deframer =
                new Deframer(new ByteArrayInputStream(new byte[0]));
        assertThrows(EOFException.class, deframer::getFrame);
    }

    /**
     * Tests that a {@link IOException} is thrown when there is a i/o error.
     */
    @DisplayName("IO Exception on read")
    @Test
    void testIOException(){
        Deframer deframer = new Deframer(new BrokenInputStream());
        assertThrows(IOException.class, deframer::getFrame);
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown when the length
     * of the payload is given to be too long
     */
    @DisplayName("Too long of payload")
    @Test
    void testIllegalArgumentException(){
        byte [] tooLongOfPayload = new byte [3000];
        tooLongOfPayload[0] = MAX_BYTE;
        tooLongOfPayload[1] = MAX_BYTE;
        tooLongOfPayload[2] = MAX_BYTE;
        Deframer deframer = new Deframer(new ByteArrayInputStream(tooLongOfPayload));
        assertThrows(IllegalArgumentException.class, () -> deframer.getFrame());
    }

    /**
     * Creates arguments for the successful execution test
     * @return arguments consisting of a byte array
     *         and String description of the test
     */
    private static Stream<Arguments> testsSource(){
        return Stream.of(
                //first test the polynomials
                Arguments.of(TEST_STRINGS[0],
                        BYTE_REPRESENTATIONS_OF_STRINGS[0], "greeting"),
                Arguments.of(TEST_STRINGS[1],
                        BYTE_REPRESENTATIONS_OF_STRINGS[1], "has newlines"),
                Arguments.of(TEST_STRINGS[2],
                        BYTE_REPRESENTATIONS_OF_STRINGS[2], "minimum length")
        );
    }

}
