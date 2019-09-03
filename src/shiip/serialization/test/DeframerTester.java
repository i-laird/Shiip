package shiip.serialization.test;/* shiip.serialization.test.DeframerTester.java 1.0 8/31/2019
 *
 * Copyright 2019 Ian Laird
 */


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.Deframer;

import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static shiip.serialization.Framer.HEADER_SIZE;
import static shiip.serialization.Framer.PREFIX_SIZE;

/**
 * Performs testing for the {@link Deframer}.
 *
 * @version 1.0
 * @author Ian Laird
 */
@DisplayName("Deframer Tester")
public class DeframerTester {

    // public static final String TEST_STRING_1 = "how are you?";
    // public static byte [] TEST_BYTE_ARRAY_1 = null;
    public static final String [] TEST_STRINGS = {"how are you"};
    public static byte [] [] BYTE_REPRESENTATIONS_OF_STRINGS
                             = new byte [TEST_STRINGS.length] [];

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
         * Default Constructor. Simply calls the default constructor for a {@link InputStream}
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
    @DisplayName("ConstructorNull")
    @Test
    public void ConstructorNullTest(){
        assertThrows( NullPointerException.class,  () -> new Deframer(null));
    }

    /**
     * tests that messages can be correctly deframed
     *
     * @see Deframer
     */
    @DisplayName("CorrectUsage")
    @Test
    public void testCorrectUsage(){
        Deframer deframer = new Deframer(
                new ByteArrayInputStream(BYTE_REPRESENTATIONS_OF_STRINGS[0]));
        try {
            byte[] deframedMessage = deframer.getFrame();
            assertArrayEquals(deframedMessage, TEST_STRINGS[0].getBytes());
        }catch(IOException e){
            fail(e.getMessage());
        }
    }

    /**
     * Tests that a {@link EOFException} is thrown when the input stream has
     * an early EOF.
     */
    @DisplayName("EarlyEOF")
    @Test
    void testEarlyEOF(){

        //take one of the strings and intentionally make the count too big
        byte [] badBytes = BYTE_REPRESENTATIONS_OF_STRINGS[0].clone();
        badBytes[2] += 1;
        Deframer deframer = new Deframer(new ByteArrayInputStream(badBytes));
        assertThrows(EOFException.class, deframer::getFrame);
    }

    /**
     * Tests that a {@link EOFException} is thrown when the input stream is
     * empty.
     */
    @DisplayName("EmptyInputStream")
    @Test
    void testEmptyInputStream(){
        Deframer deframer =
                new Deframer(new ByteArrayInputStream(new byte[0]));
        assertThrows(EOFException.class, deframer::getFrame);
    }

    /**
     * Tests that a {@link IOException} is thrown when there is a i/o error.
     */
    @DisplayName("IOException")
    @Test
    void TestIOException(){
        Deframer deframer = new Deframer(new BrokenInputStream());
        assertThrows(IOException.class, deframer::getFrame);
    }


}
