/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.Deframer;
import shiip.serialization.NIODeframer;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ian Laird
 * tests
 */
public class NonBlockingDeframeTesting {

    /**
     * test provided by dr Donahoo
     */
    @Test
    @DisplayName("Provided Test")
    public void test() {
        NIODeframer framer = new NIODeframer();
        assertNull(framer.getFrame(new byte[] { 0 , 0, 1}));
        assertNull(framer.getFrame(new byte[] { 0, 0, 0, 0, 0, 0}));
        assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 'a' }, framer.getFrame(new byte[] { 'a' }));
    }

    /**
     * test if two messages are in one byte array
     */
    @Test
    @DisplayName("Three Messages in one byte array")
    public void testTwo(){
        NIODeframer framer = new NIODeframer();
        byte [] arrayOne = new byte[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 'a' };
        byte [] arrayTwo = new byte[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 'b' };
        byte [] arrayThree = new byte[] { 0, 0, 5, 0, 0, 0, 0, 0, 0, 'a', 'b', 'c', 'd', 'e' };
        byte [] concated = NIODeframer.concatArrays(arrayOne, arrayTwo, 0, arrayOne.length, 0, arrayTwo.length);
        concated = NIODeframer.concatArrays(concated, arrayThree, 0, concated.length, 0, arrayThree.length);
        byte [] returnedOne = framer.getFrame(concated);
        byte [] returnedTwo = framer.getFrame(new byte[0]);
        byte [] returnedThree = framer.getFrame(new byte[0]);
        assertArrayEquals(returnedOne, new byte [] {0, 0, 0, 0, 0, 0, 'a' });
        assertArrayEquals(returnedTwo, new byte [] {0, 0, 0, 0, 0, 0, 'b' });
        assertArrayEquals(returnedThree, new byte [] { 0, 0, 0, 0, 0, 0, 'a', 'b', 'c', 'd', 'e'});
    }

    /**
     * test a null array
     */
    @Test
    @DisplayName("Null array")
    public void testNull(){
        NIODeframer framer = new NIODeframer();
        assertThrows(NullPointerException.class, () -> framer.getFrame(null));
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown when the length
     * of the payload is given to be too long
     */
    @DisplayName("Too long of payload")
    @Test
    void testIllegalArgumentException(){
        byte [] tooLongOfPayload = new byte [3000];
        tooLongOfPayload[0] = DeframerTester.MAX_BYTE;
        tooLongOfPayload[1] = DeframerTester.MAX_BYTE;
        tooLongOfPayload[2] = DeframerTester.MAX_BYTE;
        NIODeframer deframer = new NIODeframer();
        assertThrows(IllegalArgumentException.class, () -> deframer.getFrame(tooLongOfPayload));
    }
}
