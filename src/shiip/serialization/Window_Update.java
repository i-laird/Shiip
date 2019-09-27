/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Window_Update frame
 *
 * @version 1.0
 * @author Ian laird
 */
public class Window_Update extends Message {

    private int increment;
    /**
     * Creates Window_Update message from given values
     *
     * @param streamID stream ID
     * @param increment true if last data message
     * @throws BadAttributeException if attribute invalid (set protocol spec)
     */
    public Window_Update(int streamID, int increment) throws BadAttributeException{
        this.setStreamID(streamID);
        this.setIncrement(increment);
    }

    /**
     * Get increment value
     *
     * @return increment value
     */
    public int getIncrement(){
        return this.increment;
    }

    /**
     * Set increment value
     *
     * @param increment increment value
     * @throws BadAttributeException if invalid
     */
    public void setIncrement(int increment) throws BadAttributeException{
        //the allowed range for increment is 1 to the maximum value of an int
        if (increment < 1)
            throw new BadAttributeException("invalid value for increment: " + Integer.toString(increment), "increment");
        this.increment = increment;
    }

    /**
     * Returns string of the form
     * Window_Update: StreamID=&lt;streamid&gt; increment=&lt;inc&gt;
     *
     *     For example :
     * Window_Update: StreamID=5 increment=1024
     *
     * @return the string of the correct form
     */
    @Override
    public java.lang.String toString(){
        return "Window_Update: StreamID=" + Integer.toString(streamId) +
                " increment=" + Integer.toString(increment);
    }

    /**
     * returns the code for a window_update message
     * @return 0x8
     */
    @Override
    public byte getCode() {
        return Message.WINDOW_UPDATE_TYPE;
    }

    /**
     * Tests for equality between two Window_Update
     * @param o the object to be compared with this
     * @return true iff o and this are equal
     *    tests increment and streamId values
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Window_Update that = (Window_Update) o;
        return increment == that.increment && this.streamId == that.streamId;
    }

    /**
     * returns a hashcode for a Window_Update
     * @return hashcode of Window_Update
     */
    @Override
    public int hashCode() {

        return Objects.hash(increment, streamId);
    }

    /**
     * no flags are to be set when encoding a Window_Update
     * @return 0x0
     */
    @Override
    protected byte getEncodeFlags(){
        return NO_FLAGS;
    }


    /**
     * the payload is 32 bits.
     * The first bit is the R bit and the next 31 are the increment
     *
     * @param encoder can be null
     * @return the payload of a Window_Update frame
     */
    @Override
    protected byte []  getEncodedPayload(Encoder encoder){
        return ByteBuffer.allocate(WINDOW_UPDATE_INCREMENT_SIZE).putInt(this.getIncrement()).array();
    }

    /**
     *
     * @param parsed the contents of the header of the message
     * @param payload the payload of the message
     * @param decoder the decoder (can be null)
     * @return this message after being modified
     * @throws BadAttributeException if validation exception
     */
    @Override
    protected Message performDecode(HeaderParts parsed, byte [] payload, Decoder decoder) throws BadAttributeException{
        /* make sure that the necessary paylaod bytes are present */
        if(payload.length != WINDOW_UPDATE_INCREMENT_SIZE){
            throw new BadAttributeException("Payload of Window_Update" +
                    " frame must be 32 bits long", "payload");
        }
        /* clear out the R bit so that it is 0 (the R bit is the
                 most significant bit*/
        payload[0] &= CLEAR_ALL_BUT_R_BIT;
        ByteBuffer bb2 = ByteBuffer.wrap(payload);
        int increment = bb2.getInt();
        this.setStreamID(parsed.getStreamId());
        this.setIncrement(increment);
        return this;
    }
}
