/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

/**
 * Represents a SHiiP message
 *
 * @version 1.0
 * @author Ian laird
 */
public class Message {

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
    public static Message decode​(byte[] msgBytes, com.twitter.hpack.Decoder decoder)  throws BadAttributeException{

    }
    public byte [] encode(com.twitter.hpack.Encoder encoder){

    }

    /**
     * Returns type code for message
     *
     * @return type code
     */
    public byte getCode(){

    }

    /**
     * Returns the stream ID
     *
     * @return stream ID
     */
    public int getStreamId(){

    }

    /**
     * Sets the stream ID in the frame.
     *     Stream ID validation depends on specific message type
     *
     * @param streamId new stream id value
     * @throws BadAttributeException
     */
    public void setStreamId( int streamId) throws BadAttributeException{

    }
}
