/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

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
        this.setStreamId(streamID);
        this.setEnd(isEnd);
        this.setData(data);
    }

    /**
     *
     * @return
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

    @Override
    protected void ensureValidStreamId(int streamId) throws BadAttributeException {
        if(streamId == Message.REQUIRED_SETTINGS_STREAM_ID)
            throw new BadAttributeException("0x0 not allowed as " +
                    "stream identifier for data frame", "streamId");
    }

    @Override
    public byte getCode() {
        return Message.DATA_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data1 = (Data) o;
        return isEnd == data1.isEnd &&
                Arrays.equals(data, data1.data) &&
                streamId == data1.streamId;
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(isEnd, streamId);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
