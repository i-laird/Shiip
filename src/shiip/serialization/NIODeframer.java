/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.serialization;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/*the maximum allowed size of a payload*/
import static shiip.serialization.Framer.MAXIMUM_PAYLOAD_SIZE;

/*the size of a shiip header*/
import static shiip.serialization.Framer.HEADER_SIZE;

/**
 * Non-blocking frame deserialization from given buffers
 * @author Laird
 * @version 1.0
 */
public class NIODeframer {

    // the size of an int in bytes
    private static final int INT_SIZE_BYTES = 4;

    // the receive buffer
    private byte [] receiveBuffer;

    // the number of bytes in the receive buffer
    private int numBytesInReceiveBuffer;

    // the length bytes
    private byte [] lengthBytes;

    // the number of length bytes read
    private int numLengthBytesRead;

    // the previous contents of the buffer
    private byte [] previousBufferContents;

    /**
     * the default constructor
     */
    public NIODeframer(){
        receiveBuffer = null;
        numLengthBytesRead = 1;
        lengthBytes = new byte[INT_SIZE_BYTES];
        lengthBytes[0] = (byte)0;
        numBytesInReceiveBuffer = 0;
        previousBufferContents = new byte [0];
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

        // add the old bytes to the front of the buffer
        byte [] expandedBuffer = concatArrays(previousBufferContents, buffer, 0, previousBufferContents.length, 0, buffer.length );

        int offset = 0;
        while((numLengthBytesRead < INT_SIZE_BYTES) && (offset < expandedBuffer.length)){
            lengthBytes[numLengthBytesRead] = expandedBuffer[offset];
            ++numLengthBytesRead;
            ++offset;
        }

        //if length has not been read in yet just return
        if(numLengthBytesRead < INT_SIZE_BYTES){
            return null;
        }

        boolean lengthTooLong = false;

        // allocate the receive buffer for the message
        if(Objects.isNull(receiveBuffer)){
            int len = ByteBuffer.wrap(lengthBytes).getInt();
            if(len > MAXIMUM_PAYLOAD_SIZE){
                lengthTooLong = true;
            }
            len += HEADER_SIZE;
            receiveBuffer = new byte [len];
        }

        // now write the buffer into the receive buffer
        while((offset < expandedBuffer.length) && (numBytesInReceiveBuffer < receiveBuffer.length)){
            receiveBuffer[numBytesInReceiveBuffer] = expandedBuffer[offset];
            ++numBytesInReceiveBuffer;
            ++offset;
        }

        //see if not a whole message has been read in
        if(numBytesInReceiveBuffer < receiveBuffer.length){
            return null;
        }

        // if a whole message has been read in put the left overs in the record
        previousBufferContents = Arrays.copyOfRange(expandedBuffer, offset, expandedBuffer.length);
        numLengthBytesRead = 1;
        numBytesInReceiveBuffer = 0;
        byte [] toReturn = receiveBuffer;
        receiveBuffer = null;

        // now actually return the message
        // note that if the message has an invalid length now an exception will be thrown
        if(lengthTooLong){
            lengthTooLong = false;
            throw new IllegalArgumentException(
                    "maximum greater than the allowed payload size");
        }
        return toReturn;
    }

    /**
     * concats two arrays
     * @param arrayOne the first array
     * @param arrayTwo the second array
     * @param startOne the start index of first array inclusive
     * @param endOne the end index of first array exclusive
     * @param startTwo the start index of the second array inclusive
     * @param endTwo the end index of the second array exclusive
     * @return the concat of the arrays
     */
    public static byte [] concatArrays(byte [] arrayOne, byte [] arrayTwo, int startOne, int endOne, int startTwo, int endTwo){
        int bytesInOne = endOne - startOne;
        int bytesInTwo = endTwo - startTwo;

        byte [] toReturn = new byte [bytesInOne + bytesInTwo];

        // move over the old contents first
        for(int i = 0; i < bytesInOne; i++){
            toReturn[i] = arrayOne[i + startOne];
        }

        // then move over the new ones
        for(int j = 0; j < bytesInTwo; j++){
            toReturn[bytesInOne + j] = arrayTwo[j + startTwo];
        }

        return toReturn;
    }
}
