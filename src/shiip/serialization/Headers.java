/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package shiip.serialization;

import com.twitter.hpack.Encoder;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Headers message
 *
 * the decode() and encode() methods need a Decoder/Encoder respectively
 */
public class Headers extends Message {

    private boolean isEnd;

    private SortedMap<String, String> nameValuePairs = new TreeMap<>();

    // for testing if name/value pairs are to be tested for encoding purposes
    private static boolean ENCODE_MODE = true;

    /**
     * @author Ian Laird
     *
     * used to check if byte arrays are valid names or values respectively
     */
    private static class NameValueValidityChecker{
        // the lowest visible ascii character
        private static byte VISCHAR_LOWER_BOUND = 0x21;

        // the highest visible ascii character
        private static byte VISCHAR_UPPER_BOUND = 0x7E;

        // the SP character
        private static byte SP = 0x20;

        // the HTAB character
        private static byte HTAB = 0x9;

        // the minimum length for a name in bytes
        private static int MINIMUM_NAME_LENGTH = 1;

        //the minimum length for a value in bytes
        private static int MINIMUM_VALUE_LENGTH = 1;

        // all of the delimeters
        private static String DELIMS = "(),/;<=>?@[\\]{}";

        /**
         * Checks if a string and value pair are valid
         * @param name the name to be checked
         * @param value the value to be checked
         * @param encodeMode the way that name/value are checked
         *                  depends on decoding or encoding
         * @throws BadAttributeException thrown if invalid name or value
         */
        public static void checkValid(byte [] name, byte [] value, boolean encodeMode) throws BadAttributeException{
            //if in encode mode make sure that the string are valid for sending
            isValidName(name);
            isValidValue(value);
        }

        /**
         * checks if a name is valid
         * @param name the name to be checked
         * @throws BadAttributeException if the name is invalid
         */
        private static void isValidName(byte [] name) throws BadAttributeException{
            if(name.length < MINIMUM_NAME_LENGTH){
                throw new BadAttributeException("Invalid name: too short", "name");
            }
            for (byte b : name){
                if(!isNCHAR(b)){
                    throw new BadAttributeException("Invalid name: not an nchar", "name");
                }
            }
        }

        /**
         * checks if a value is valid
         * @param value the value to be checked
         * @throws BadAttributeException if the value is invalid
         */
        private static void isValidValue(byte [] value) throws BadAttributeException{
            if(value.length < MINIMUM_VALUE_LENGTH){
                throw new BadAttributeException("Invalid value: too short", "name");
            }
            for (byte b : value){
                if(!isVCHAR(b)){
                    throw new BadAttributeException("Invalid name: not all vchar", "value");
                }
            }
        }

        /**
         * sees if is a Visible character
         * @param c the ascii character to check
         * @return true indicates is a visible character
         */
        private static  boolean isVISCHAR( byte c){
            return (c >= VISCHAR_LOWER_BOUND) && (c <= VISCHAR_UPPER_BOUND);
        }

        /**
         * sees if is a delimiter
         * @param c the ascii char to test
         * @return true indicates is a delim
         */
        private static boolean isDelim(byte c){
            return DELIMS.contains(String.valueOf(c));
        }

        /**
         * sees if is a visible char or SP or HTAB
         * @param c the ascii char to test
         * @return true is a VCHAR
         */
        private static boolean isVCHAR(byte c){
            return isVISCHAR(c) || (c == SP) || (c == HTAB);
        }

        /**
         * sees if is a visible character that is not a delimiter
         * @param c the ascii character to test
         * @return true if is a NCHAR
         */
        private static boolean isNCHAR(byte c){
            return isVISCHAR(c) && !isDelim(c);
        }
    }

    /**
     * Creates Headers message from given values
     *
     * @param streamID stream ID
     * @param isEnd true if last header
     * @throws shiip.serialization.BadAttributeException if attribute invalid
     */
    public Headers(int streamID, boolean isEnd) throws BadAttributeException{
        this.setStreamID(streamID);
        this.setEnd(isEnd);
    }

    /**
     * returns end value
     * @return end value
     */
    public boolean isEnd(){
        return this.isEnd;
    }

    /**
     * set end value
     * @param end end value
     */
    public void setEnd(boolean end){
        this.setEnd(end);
    }

    /**
     * Returns string of the form
     * Headers: StreamID=<streamid> isEnd=<end> ([<name> = <value>]...[lt;name> = <value>])
     * For example
     *
     * Headers: StreamID=5 isEnd=false ([method=GET][color=blue])
     *
     * @param name
     * @return
     */
    public String toString(String name){
        return null;
    }

    /**
     * @param name the name
     * @return the value
     */
    public String getValue(String name){
        return null;
    }

    /**
     * get set of names in headers
     * @return set of names
     */
    public SortedSet<String> getNames(){
        return null;
    }

    /**
     * Add name/value pair to header. If the name is already contained in the header, the corresponding value is replaced by the new value.
     * @param name name to add
     * @param value value to add/replace
     * @throws BadAttributeException if invalid name or value
     */
    public void addValue(String name, String value) throws BadAttributeException{

    }


    /**
     * tests if the stream id is valid for the Headers type
     * @param streamId the stream id to be verified
     * @throws BadAttributeException if the stream id is 0x0
     */
    @Override
    protected void ensureValidStreamId(int streamId) throws BadAttributeException {
        if(streamId == Message.REQUIRED_SETTINGS_STREAM_ID)
            throw new BadAttributeException("0x0 not allowed as " +
                    "stream identifier for headers frame", "streamID");
    }

    /**
     * returns the code for a Headers message
     * @return 0x1
     */
    @Override
    public byte getCode() {
        return Message.HEADER_TYPE;
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
        Headers h1 = (Headers) o;
        return isEnd == h1.isEnd &&
                streamId == h1.streamId;
    }

    /**
     * returns a hashcode for a {@link Data}
     * @return hashcode of {@link Data}
     */
    @Override
    public int hashCode() {
        return Objects.hash(isEnd, streamId);
    }

    /**
     * 0x1 is set if it is the end header, and 0x4 is always set.
     * @return the encoding flags for a header message
     */
    @Override
    protected byte getEncodeFlags(){
        byte toReturn = (this.isEnd ? HEADERS_END_STREAM_FLAG : 0x0);
        toReturn |= HEADERS_END_HDR_FLAG; //error if this bit is not set
        return toReturn;
    }

    /**
     * encodes the payload of a headers message
     *
     * @param encoder cannot be null and is used to encode the headers payload
     * @return the encoded payload
     * @throws NullPointerException if encoder is null
     */
    @Override
    protected byte [] getEncodedPayload(Encoder encoder){
        Objects.requireNonNull(encoder, "The encoder cannot be null for a Headers message");
        return null;
    }

    protected void addValue(byte [] name, byte [] value, boolean sensitive) throws BadAttributeException{
        NameValueValidityChecker.checkValid(name, value, ENCODE_MODE);
        String n = byteArrayToString(name);
        String v = byteArrayToString(value);
        this.addValue(n,v);
    }
    protected static String byteArrayToString(byte [] b){
        return Base64.getEncoder().encodeToString(b);
    }
}
