/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import java.util.Arrays;
import java.util.Objects;

/**
 * Data message
 *
 * @version 1.0
 * @author Ian laird
 */
public class Data extends Message {

    private byte [] data;
    private boolean isEnd = false;

    /**
     * Creates Data message from given values
     *
     * @param streamID stream ID
     * @param isEnd true if last data message
     * @param data bytes of application data
     * @throws BadAttributeException if attribute invalid (set protocol spec)
     */
    public Data(int streamID, boolean isEnd, byte[] data) throws BadAttributeException{
        this.setStreamID(streamID);
        this.setEnd(isEnd);
        this.setData(data);
    }

    /**
     * get data
     * @return the data of a data frame
     */
    public byte [] getData(){
        return this.data;
    }

    /**
     * Return end value
     * @return end value
     */
    public boolean isEnd(){
        return this.isEnd;
    }

    /**
     * Set data
     * @param data data to set
     * @throws BadAttributeException if invalid
     */
    public void setData(byte [] data) throws BadAttributeException{
        if(Objects.isNull(data)){
            throw new BadAttributeException("data cannot be null", "data", new NullPointerException());
        }
        this.data = data.clone();
    }

    /**
     * Set end value
     * @param end end value
     */
    public void setEnd(boolean end){
        this.isEnd = end;
    }

    /**
     * Returns string of the form
     * Data: StreamID=<streamid> isEnd=<end> data=<length>
     *
     * For example:
     *
     * Data: StreamID=5 isEnd=true data: 5
     * @return
     */
    @Override
    public java.lang.String toString(){
        return "Data: StreamID=" + Integer.toString(this.streamId)
                + " isEnd=" + (this.isEnd ? "true" : "false")
                + " data=" + Integer.toString(data.length);
    }

    /**
     * tests if the stream id is valid for the Data type
     * @param streamId the stream id to be verified
     * @throws BadAttributeException if the stream id is 0x0
     */
    @Override
    protected void ensureValidStreamId(int streamId) throws BadAttributeException {
        if(streamId == Message.REQUIRED_SETTINGS_STREAM_ID)
            throw new BadAttributeException("0x0 not allowed as " +
                    "stream identifier for data frame", "streamID");
    }

    /**
     * returns the code for a settings message
     * @return 0x0
     */
    @Override
    public byte getCode() {
        return Message.DATA_TYPE;
    }

    /**
     * Tests for equality between two data messages
     * @param o the object to be compared with this
     * @return true iff o and this are equal
     *  tests streamId values, isEnd values, and data arrays
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data1 = (Data) o;
        return isEnd == data1.isEnd &&
                Arrays.equals(data, data1.data) &&
                streamId == data1.streamId;
    }

    /**
     * returns a hashcode for a {@link Data}
     * @return hashcode of {@link Data}
     */
    @Override
    public int hashCode() {

        int result = Objects.hash(isEnd, streamId);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    /**
     *
     * @return if isEnd is set the end of stream flag is set when returned
     */
    @Override
    protected byte getEncodeFlags(){
        return this.isEnd ? DATA_END_STREAM : NO_FLAGS;
    }

    /**
     * the data array
     * @param encoder can be null by default
     * @return the data array
     */
    @Override
    protected byte []  getEncodedPayload(Encoder encoder){
        return this.data;
    }

    @Override
    protected Message performDecode(HeaderParts parsed, byte [] payload, Decoder decoder) throws BadAttributeException{
        if(checkBitSet(parsed.getFlags(), DATA_BAD_FLAG)){
            throw new BadAttributeException("The Bad flag was set (0x8)", "flags");
        }
        this.setData(payload);
        this.setStreamID(parsed.streamId);
        this.setEnd(checkBitSet(parsed.flags, DATA_END_STREAM));
        return this;
    }
}
