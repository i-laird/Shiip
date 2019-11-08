/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.serialization;

import java.util.Objects;

/**
 * Non-blocking frame deserialization from given buffers
 * @author Laird
 * @version 1.0
 */
public class NIODeframer {

    public NIODeframer(){

    }

    /**
     * Gets the next frame(if available)
     * @param buffer the next bytes of data
     * @return the next frame
     * @throws NullPointerException if the buffer is null
     * @throws IllegalArgumentException if the input value is bad
     */
    public byte [] getFrame(byte [] buffer) throws NullPointerException, IllegalArgumentException{
        Objects.requireNonNull(buffer, "buffer cannot be null");
        return null;
    }
}
