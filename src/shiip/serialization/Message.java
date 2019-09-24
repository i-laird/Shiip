/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import com.twitter.hpack.Encoder;

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

    // for when a message does not have a type set
    protected static final byte TYPE_NOT_SET = (byte)0xFF;

    //type for a data message
    protected static final byte DATA_TYPE = (byte)0x0;

    // type for a settings message
    protected static final byte SETTINGS_TYPE = (byte)0x4;

    // type for a windows update message
    protected static final byte WINDOW_UPDATE_TYPE = (byte)0x8;

    //type for a headers message
    protected static final byte HEADER_TYPE = (byte)0x1;

    //the stream id of a settings frame
    protected static final int REQUIRED_SETTINGS_STREAM_ID = 0X0;

    //an unallowed flag for a settings frame
    protected static final int DATA_BAD_FLAG = 0x8;

    // bit to see if the end of stream is set for a data message
    protected static final int DATA_END_STREAM = 0x1;

    // the size of the header of a SHiip message
    protected static final int HEADER_SIZE = 6;

    // the increment size of a window update frame
    protected static final int WINDOW_UPDATE_INCREMENT_SIZE = 4;

    //indicates that no flags are set
    protected static final byte NO_FLAGS = 0x0;

    // test stream id for a window update frame
    protected static final int WINDOW_UPDATE_STREAM_IDENTIFIER = 4;

    // all bits but the r bit
    protected static final int CLEAR_ALL_BUT_R_BIT = 0x7F;

    // the required flags for a settings frame
    protected static final byte REQUIRED_SETTINGS_FLAGS = 0x1;

    // end of stream flag for a headers frame
    protected static final byte HEADERS_END_STREAM_FLAG = 0x1;

    // end hdr flag for a headers frame
    protected static final byte HEADERS_END_HDR_FLAG = 0x4;

    // bad flag one for a headers frame
    protected static final byte HEADERS_BAD_FLAG_ONE = 0X8;

    // bad flag two for a headers frame
    protected static final byte HEADERS_BAD_FLAG_TWO = 0x20;

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
    public static Message decode(byte[] msgBytes,
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
    public byte [] encode(Encoder encoder){
        return this.getEncoded(this.getCode(), this.getEncodeFlags(), this.getStreamID(), this.getEncodedPayload(encoder));
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

    /**
     * Creates the encoding of the header and payload.
     * @param type the type of the message
     * @param flags the flags for the message
     * @param streamId the stream id of the message
     * @param payload the payload of the message
     * @return the frame
     */
    protected byte [] getEncoded(byte type, byte flags, int streamId, byte [] payload){
        if(Objects.isNull(payload)){
            payload = new byte [0];
        }
        ByteBuffer createByteArray = ByteBuffer.allocate(HEADER_SIZE + payload.length);
        createByteArray.put(type);
        createByteArray.put(flags);
        createByteArray.putInt(streamId);
        if(payload.length > 0){
            createByteArray.put(payload);
        }
        return createByteArray.array();
    }

    /**
     * a message has no flags by default
     * @return 0x0
     */
    protected byte getEncodeFlags(){
        return (byte)0x0;
    }

    /**
     * a message has no payload by default
     * @param encoder can be null by default
     * @return null
     */
    protected byte []  getEncodedPayload(Encoder encoder){
        return null;
    }
}
