/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import java.nio.ByteBuffer;

/**
 * Settings message
 *
 * @version 1.0
 * @author Ian laird
 */
public class Settings extends Message{

    /**
     * Creates Settings message
     * @throws BadAttributeException if attribute invalid
     *     (not thrown in this case)
     */
    public Settings() throws BadAttributeException{
        this.setStreamID(REQUIRED_SETTINGS_STREAM_ID);
    }

    /**
     * Returns string of the form
     * Settings: StreamID=0
     * For example
     * Settings: StreamID=0
     *
     * @return Settings: StreamID=0
     */
    @Override
    public java.lang.String toString(){
        return "Settings: StreamID=" + Integer.toString(this.streamId);
    }

    /**
     * tests if the stream id is valid for Settings Frame
     * @param streamId the stream id to be verified
     * @throws BadAttributeException if the stream id is not 0x0
     */
    @Override
    protected void ensureValidStreamId(int streamId) throws BadAttributeException {
        if(streamId != Message.REQUIRED_SETTINGS_STREAM_ID)
            throw new BadAttributeException("only 0x0 allowed as " +
                    "stream identifier for settings frame", "streamID");
    }

    /**
     * converts a settings message into a stream of bytes
     * @param encoder can be null for this message type
     * @return the byte array representation of a settings messagell
     */
    @Override
    public byte [] encode(com.twitter.hpack.Encoder encoder){
        ByteBuffer createByteArray = ByteBuffer.allocate(HEADER_SIZE);
        createByteArray.put(SETTINGS_TYPE);
        createByteArray.put(REQUIRED_SETTINGS_FLAGS);
        createByteArray.putInt(this.getStreamID());
        return createByteArray.array();
    }

    /**
     * returns the code for a settings message
     * @return 0x4
     */
    @Override
    public byte getCode() {
        return Message.SETTINGS_TYPE;
    }

}
