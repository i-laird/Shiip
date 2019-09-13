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
        this.setStreamId(REQUIRED_SETTINGS_STREAM_ID);
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
        return "StreamID=" + Integer.toString(this.streamId);
    }

    @Override
    protected void ensureValidStreamId(int streamId) throws BadAttributeException {
        if(streamId != Message.REQUIRED_SETTINGS_STREAM_ID)
            throw new BadAttributeException("only 0x0 allowed as " +
                    "stream identifier for settings frame", "streamId");
    }

    @Override
    public byte [] encode(com.twitter.hpack.Encoder encoder){
        ByteBuffer createByteArray = ByteBuffer.allocate(HEADER_SIZE);
        createByteArray.put(SETTINGS_TYPE);
        createByteArray.put(REQUIRED_SETTINGS_FLAGS);
        createByteArray.putInt(this.getStreamId());
        return createByteArray.array();
    }

    @Override
    public byte getCode() {
        return Message.SETTINGS_TYPE;
    }

}
