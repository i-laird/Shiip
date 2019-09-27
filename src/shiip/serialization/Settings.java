/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

/**
 * Settings message
 *
 * @version 2.0
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
     * returns the code for a settings message
     * @return 0x4
     */
    @Override
    public byte getCode() {
        return Message.SETTINGS_TYPE;
    }

    /**
     * no flags are defined for a settings frame
     * @return 0x0
     */
    @Override
    protected byte getEncodeFlags(){
        return REQUIRED_SETTINGS_FLAGS;
    }

    /**
     * settings frame has no payload
     * @param encoder can be null
     * @return null
     */
    @Override
    protected byte []  getEncodedPayload(Encoder encoder){
        return null;
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
        if(parsed.streamId != REQUIRED_SETTINGS_STREAM_ID){
            throw new BadAttributeException("Settings stream id must be 0x0", "stream id");
        }
        this.setStreamID(parsed.getStreamId());
        return this;
    }


}
