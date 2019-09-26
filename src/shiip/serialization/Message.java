/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a SHiiP message
 *
 * @version 1.0
 * @author Ian laird
 */
public abstract class Message {

    /**
     * @author Ian laird
     *
     * holds the fields of a parsed header
     */
    protected static class HeaderParts{

        //the flags of the header
        byte flags;

        // the stream id in the header
        int streamId;

        // the type of the message
        byte code;

        /**
         * @param flags flags
         * @param streamId stream id
         * @param code code
         */
        public HeaderParts(byte flags, int streamId, byte code) {
            this.flags = flags;
            this.streamId = streamId;
            this.code = code;
        }

        /**
         *
         * @return flags of the header
         */
        public byte getFlags() {
            return flags;
        }

        /**
         * set the flags
         * @param flags flags
         */
        public void setFlags(byte flags) {
            this.flags = flags;
        }

        /**
         *
         * @return stream id
         */
        public int getStreamId() {
            return streamId;
        }

        /**
         * set the stream id
         * @param streamId the stream id to set
         */
        public void setStreamId(int streamId) {
            this.streamId = streamId;
        }

        /**
         *
         * @return message type
         */
        public byte getCode() {
            return code;
        }

        /**
         * the code to set
         * @param code code
         */
        public void setCode(byte code) {
            this.code = code;
        }
    }

    // the stream id that is associated with the message
    protected int streamId;

    //type for a data message
    public static final byte DATA_TYPE = (byte)0x0;

    // type for a settings message
    public static final byte SETTINGS_TYPE = (byte)0x4;

    // type for a windows update message
    public static final byte WINDOW_UPDATE_TYPE = (byte)0x8;

    //type for a headers message
    public static final byte HEADER_TYPE = (byte)0x1;

    //the stream id of a settings frame
    protected static final int REQUIRED_SETTINGS_STREAM_ID = 0X0;

    //an unallowed flag for a settings frame
    protected static final byte DATA_BAD_FLAG = 0x8;

    // bit to see if the end of stream is set for a data message
    protected static final byte DATA_END_STREAM = 0x1;

    // the size of the header of a SHiip message
    protected static final int HEADER_SIZE = 6;

    // the increment size of a window update frame
    protected static final int WINDOW_UPDATE_INCREMENT_SIZE = 4;

    //indicates that no flags are set
    protected static final byte NO_FLAGS = 0x0;

    // test stream id for a window update frame
    protected static final int WINDOW_UPDATE_STREAM_IDENTIFIER = 4;

    // all bits but the r bit
    protected static final byte CLEAR_ALL_BUT_R_BIT = 0x7F;

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

    protected static final byte STREAM_ID_START_BYTE = 0X2;

    /**
     * sees if a specific boolean is set in a byte
     * @param flag the position to check
     * @param bit the byte to check against
     * @return true indicates that the flag is set
     */
    protected static boolean checkBitSet(byte flag, byte bit){
        return ((flag & bit) != (byte)0);
    }

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
        int length = msgBytes.length;
        msgBytes = Objects.requireNonNull(msgBytes, "The message cannot be null");
        if(msgBytes.length < HEADER_SIZE){
            throw new BadAttributeException("msgBytes too short...must be at least 6 bytes", "msgBytes");
        }
        HeaderParts parsed = parseHeader(msgBytes);
        byte [] payload = Arrays.copyOfRange(msgBytes, HEADER_SIZE, length);

        Message newMessage = null;

        switch(parsed.getCode()){
            case DATA_TYPE:
                newMessage = new Data(0x1,false, new byte [0]);
                break;
            case SETTINGS_TYPE:
                newMessage = new Settings();
                break;
            case WINDOW_UPDATE_TYPE:
                newMessage = new Window_Update(0x1, 1);
                break;
            case HEADER_TYPE:
                newMessage = new Headers(0x1, false);
                break;
            /* This is if the type of the packet is not recognized */
            default:
                throw new BadAttributeException("Unrecognized packet type: " + Byte.toString(parsed.getCode()), "type");
        }
        return newMessage.performDecode(parsed, payload, decoder);
    }

    /**
     * Performs the actual decode of the message
     * @param parsed the contents of the header of the message
     * @param payload the payload of the message
     * @param decoder the decoder
     * @return the decoded Message
     * @throws BadAttributeException if validation failure
     */
    protected abstract Message performDecode(HeaderParts parsed, byte [] payload, Decoder decoder) throws BadAttributeException;

    /**
     * creates a {@link HeaderParts} from the header bytes
     * @param msgBytes the header bytes
     * @return a newly created HeaderParts
     * @see HeaderParts
     */
    static HeaderParts parseHeader(byte [] msgBytes){

        /* clear out the R bit so that it is 0 (the R bit is the
                 most significant bit*/
        msgBytes[STREAM_ID_START_BYTE] &= CLEAR_ALL_BUT_R_BIT;

        ByteBuffer bb = ByteBuffer.wrap(msgBytes, 0, HEADER_SIZE);
        byte type = bb.get();
        byte flags = bb.get();
        int streamId = bb.getInt();
        return new HeaderParts(flags, streamId, type);
    }

    /**
     * @param headers the Headers Message to add header fields to
     * @param payload the headers block
     * @param decoder the dccoder
     * @throws BadAttributeException if unable to parse the headers
     */
    public static void addHeaderFieldsToHeader(Headers headers, byte [] payload, Decoder decoder) throws BadAttributeException {
        try {
            ByteArrayInputStream payloadStream = new ByteArrayInputStream(payload);
            decoder.decode(payloadStream,
                    (byte[] name, byte[] value, boolean sensitive) -> headers.addValue(name, value, sensitive));
            headers.processAllNameValues();
        }catch(IOException e){
            throw new BadAttributeException("Unable to decode the headers", "headers", e);
        }

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
    public abstract byte getCode();

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
        if(streamId < 0){
            throw new BadAttributeException("Stream id cannot be negative", "streamID");
        }
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
    protected byte [] getEncodedPayload(Encoder encoder){
        return null;
    }
}
