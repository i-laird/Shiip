/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a SHiiP message
 *
 * @version 1.0
 * @author Ian laird
 */
public class Message {
    protected int streamId;
    protected byte [] ALLOWED_TYPE_CODES = new byte [] {(byte)0x0, (byte)0x4, (byte)0x8};

    protected static final byte TYPE_NOT_SET = (byte)0xFF;
    protected static final byte DATA_TYPE = (byte)0x0;
    protected static final byte SETTINGS_TYPE = (byte)0x4;
    protected static final byte WINDOW_UPDATE_TYPE = (byte)0x8;

    protected static final int REQUIRED_SETTINGS_STREAM_ID = 0X0;
    protected static final int DATA_BAD_FLAG = 0x8;
    protected static final int DATA_END_STREAM = 0x1;
    protected static final int HEADER_SIZE = 6;
    protected static final int WINDOW_UPDATE_INCREMENT_SIZE = 4;
    protected static final byte NO_FLAGS = 0x0;
    protected static final int WINDOW_UPDATE_STREAM_IDENTIFIER = 4;
    protected static final int CLEAR_ALL_BUT_R_BIT = 0x7F;
    protected static final byte REQUIRED_SETTINGS_FLAGS = 0x1;

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
        msgBytes = Objects.requireNonNull(msgBytes, "The message cannot be null");
        int length = msgBytes.length;
        ByteBuffer bb = ByteBuffer.wrap(msgBytes, 0, HEADER_SIZE);
        byte type = bb.get();
        byte flags = bb.get();
        int streamId = bb.getInt();
        Message toReturn = null;
        switch(type){
            case DATA_TYPE:
                byte [] data = Arrays.copyOfRange(msgBytes, HEADER_SIZE, length);
                boolean end_stream = ((flags & DATA_END_STREAM) != (byte)0);
                boolean bad_bit = ((flags & DATA_BAD_FLAG) != (byte)0);
                if(bad_bit){
                    throw new BadAttributeException("The Bad flag was set (0x8)", "flags");
                }
                toReturn = new Data(streamId, end_stream,  data);
                break;
            case SETTINGS_TYPE:
                if(streamId != REQUIRED_SETTINGS_STREAM_ID){
                    throw new BadAttributeException("Settings stream id must be 0x0", "stream id");
                }
                toReturn = new Settings();
                break;
            case WINDOW_UPDATE_TYPE:

                /* make sure that the necessary paylaod bytes are present */
                if(msgBytes.length != (HEADER_SIZE + WINDOW_UPDATE_INCREMENT_SIZE)){
                    throw new BadAttributeException("Payload of Window_Update" +
                            " frame must be 32 bits long", "payload");
                }
                /* clear out the R bit so that it is 0 (the R bit is the
                 most significant bit*/
                msgBytes[HEADER_SIZE + WINDOW_UPDATE_INCREMENT_SIZE - 1] &= CLEAR_ALL_BUT_R_BIT;
                ByteBuffer bb2 = ByteBuffer.wrap(msgBytes, HEADER_SIZE, WINDOW_UPDATE_INCREMENT_SIZE);
                int increment = bb2.getInt();
                toReturn = new Window_Update(streamId, increment);
                break;

             /* This is if the type of the packet is not recognized */
            default:
                throw new BadAttributeException("Unrecognized packet type: " + Byte.toString(type), "type");
        }
        return toReturn;
    }

    /**
     * converts a message into a stream of bytes
     * @param encoder can optionally be null for the message types
     * @return the byte array
     */
    public byte [] encode(com.twitter.hpack.Encoder encoder){
        return null;
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
    public int getStreamID(){
        return this.streamId;
    }

    /**
     * Sets the stream ID in the frame.
     *     Stream ID validation depends on specific message type
     *
     * @param streamId new stream id value
     * @throws BadAttributeException
     */
    public void setStreamID(int streamId) throws BadAttributeException{
        this.ensureValidStreamId(streamId);
        this.streamId = streamId;
    }

    /**
     * tests if the stream id is valid for the message type
     * @param streamId the stream id to be verified
     * @throws BadAttributeException if the stream id is invalid
     */
    protected void ensureValidStreamId( int streamId) throws BadAttributeException{
        //for just a message by itself there are no requirments for the stream id
    }

    /**
     * Tests for equality between two messages
     * @param o the object to be compared with this
     * @return true iff o and this are equal
     *  tests streamId values
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return streamId == message.streamId;
    }

    /**
     * returns a hashcode for a {@link Message}
     * @return hashcode of message
     */
    @Override
    public int hashCode() {

        return Objects.hash(streamId);
    }
}
