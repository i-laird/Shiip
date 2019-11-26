/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.NIODeframer;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

/*the maximum allowed size of a payload*/
import static shiip.serialization.Framer.MAXIMUM_PAYLOAD_SIZE;

/*the size of a shiip header*/
import static shiip.serialization.Framer.HEADER_SIZE;

/**
 * @author Ian Laird
 * tests
 */
public class NIODeframerTester {

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
        byte [] tooLongOfPayload = new byte [MAXIMUM_PAYLOAD_SIZE + HEADER_SIZE + 3 + 1];
        ByteBuffer intConverter = ByteBuffer.wrap(new byte [4]).putInt(MAXIMUM_PAYLOAD_SIZE + 1);
        byte [] size = intConverter.array();
        tooLongOfPayload[0] = size[1];
        tooLongOfPayload[1] = size[2];
        tooLongOfPayload[2] = size[3];
        NIODeframer deframer = new NIODeframer();
        assertThrows(IllegalArgumentException.class, () -> deframer.getFrame(tooLongOfPayload));
    }

    /**
     * Tests that a {@link IllegalArgumentException} is thrown when the length
     * of the payload is given to be too long
     */
    @DisplayName("Too long of payload followed by valid message")
    @Test
    void testBadFollowedByGood(){
        byte [] tooLongOfPayload = new byte [MAXIMUM_PAYLOAD_SIZE + HEADER_SIZE + 3 + 1];
        ByteBuffer intConverter = ByteBuffer.wrap(new byte [4]).putInt(MAXIMUM_PAYLOAD_SIZE + 1);
        byte [] size = intConverter.array();
        tooLongOfPayload[0] = size[1];
        tooLongOfPayload[1] = size[2];
        tooLongOfPayload[2] = size[3];
        NIODeframer deframer = new NIODeframer();
        assertThrows(IllegalArgumentException.class, () -> deframer.getFrame(tooLongOfPayload));
        assertArrayEquals(deframer.getFrame(new byte[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 'a' }), new byte [] {0, 0, 0, 0, 0, 0, 'a' });
    }
}
