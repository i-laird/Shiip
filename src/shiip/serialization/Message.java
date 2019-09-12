/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import java.util.Arrays;

/**
 * Represents a SHiiP message
 *
 * @version 1.0
 * @author Ian laird
 */
public class Message {
    protected int streamId;
    protected byte [] ALLOWED_TYPE_CODES = new byte [] {(byte)0x0, (byte)0x4, (byte)0x8};

    protected static final int SETTINGS_STREAM_IDENTIFIER = 0x0;
    protected static final byte TYPE_NOT_SET = 0xFF;
    protected static final byte DATA_TYPE = 0x0;
    protected static final byte SETTINGS_TYPE = 0x4;
    protected static final byte WINDOW_UPDATE_TYPE = 0x8;
    /**
     * Deserializes message from given bytes
     * @param msgBytes message bytes
     * @param decoder  decoder for deserialization. This is ignored
     *                (so can be null) if not needed (in which case
     *                 it can be null). Whether it is need is determined
     *                 by and specified in specific message type.
     * @return specific Message resulting from deserialization
     * @throws NullPointerException  if msg is null or if decoder
     * is null + needed.
     * @throws BadAttributeException if validation failure
     */
    public static Message decode​(byte[] msgBytes,
            com.twitter.hpack.Decoder decoder)
            throws BadAttributeException{

    }
    public byte [] encode(com.twitter.hpack.Encoder encoder){

    }

    /**
     * Returns type code for message
     *
     * @return type code
     */
    public byte getCode(){
        return Message.TYPE_NOT_SET;
    }

    /**
     * Returns the stream ID
     *
     * @return stream ID
     */
    public int getStreamId(){
        return this.streamId;
    }

    /**
     * Sets the stream ID in the frame.
     *     Stream ID validation depends on specific message type
     *
     * @param streamId new stream id value
     * @throws BadAttributeException
     */
    public void setStreamId( int streamId) throws BadAttributeException{
        this.ensureValidStreamId(streamId);
        this.streamId = streamId;
    }

    protected void ensureValidStreamId( int streamId) throws BadAttributeException{
        //for just a message by itself there are no requirments for the stream id
    }
}
