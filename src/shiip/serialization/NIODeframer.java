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

    // the connection preface
    public static final byte [] CLIENT_CONNECTION_PREFACE =
            {0x50, 0x52, 0x49, 0x20, 0x2a, 0x20, 0x48, 0x54, 0x54, 0x50, 0x2f,
                    0x32, 0x2e, 0x30, 0x0d, 0x0a, 0x0d, 0x0a, 0x53, 0x4d, 0x0d, 0x0a,
                    0x0d, 0x0a};

    // the size of an int in bytes
    private static final int INT_SIZE_BYTES = 4;

    private byte [] receiveBuffer;
    private int numBytesInReceiveBuffer;
    private byte [] lengthBytes;
    private int numLengthBytesRead;
    private boolean connectionPrefaceActive;
    private byte [] previousBufferContents;

    public NIODeframer(){
        receiveBuffer = null;
        numLengthBytesRead = 1;
        connectionPrefaceActive = false;
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

        // allocate the receive buffer for the message
        if(Objects.isNull(receiveBuffer)){
            int len = ByteBuffer.wrap(lengthBytes).getInt();
            if(len > MAXIMUM_PAYLOAD_SIZE){
                throw new IllegalArgumentException(
                        "maximum greater than the allowed payload size");
            }
            receiveBuffer = new byte [ len];
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

        return receiveBuffer;
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
    private static byte [] concatArrays(byte [] arrayOne, byte [] arrayTwo, int startOne, int endOne, int startTwo, int endTwo){
        int bytesInOne = endOne = startOne;
        int bytesInTwo = endTwo = startTwo;

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
