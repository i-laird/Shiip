/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 0
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.Deframer;
import shiip.serialization.Framer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performs testing for the {@link Deframer} and the {@link Framer}.
 *
 * @version 1.0
 * @author Ian Laird
 */
@DisplayName("Combined Tester")
public class CombinedTester{
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Framer framer = new Framer(outputStream);

    /**
     * frames and then deframes a message to ensure that it
     * is identical to the original message.
     */
    @Test
    @DisplayName("frame and deframe a message and see that it is unchanged")
    void testCombinedFunctionality(){
        byte [] framedMessage = null, deframedBytes = null;
        try {
            framer.putFrame(FramerTester.TEST_MESSAGE_1);
            framedMessage = outputStream.toByteArray();

            Deframer deframer = new Deframer(
                    new ByteArrayInputStream(framedMessage));
            deframedBytes = deframer.getFrame();
            assertArrayEquals(FramerTester.TEST_MESSAGE_1, deframedBytes);
        }catch(IOException e){
            fail(e.getMessage());
        }
    }
}
