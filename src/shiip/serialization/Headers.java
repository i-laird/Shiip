/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 2
 * Class: Data Comm
 *******************************************************/

package shiip.serialization;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Headers message
 *
 * the decode() and encode() methods need a Decoder/Encoder respectively
 */
public class Headers extends Message {

    // header value for the name of the method
    public static final String NAME_METHOD = ":method";

    // header value for making a GET request
    public static final String GET_REQUEST = "GET";

    // header value for specifying the path
    public static final String NAME_PATH = ":path";

    // header value for the version
    public static final String NAME_VERSION = ":version";

    // header value to specify the host
    public static final String NAME_HOST = ":host";

    // header value to specify the scheme
    public static final String NAME_SCHEME = ":scheme";

    // all valid http methods
    private static final String [] HTTP_METHODS_ARRAYS = {"GET", "POST", "PUT", "HEAD", "INSERT", "DELETE"};

    // all valid http methods
    private static final List<String> HTTP_METHODS = Arrays.asList(HTTP_METHODS_ARRAYS);

    // the current version of http
    public static final String HTTP_VERSION = "HTTP/2.0";

    // the scheme of http being used
    public static final String HTTP_SCHEME = "https";

    // header value for status
    public static final String STATUS = ":status";

    // header value for accpepting encoding
    public static final String ACCEPT_ENCODING = "accept-encoding";

    // local variable storing if it is the end
    private boolean isEnd;

    // all header name/value pairs of the Headers message
    private SortedMap<String, String> nameValuePairs = new TreeMap<>();

    // for testing if name/value pairs are to be tested for encoding purposes
    private static final boolean ENCODE_MODE = true;

    // for when it is in decode mode
    private static final boolean DECODE_MODE = false;

    // the ascii encoding of a forward slash
    private static final byte FORWARD_SLASH_ASCII = 0x5c;

    // specifying if we are using sensitive encoding
    private static final boolean ENCODING_SENSITIVE = false;

    // the headers waiting to be processed
    private Map<byte [], byte []> toProcess = new HashMap<>();

    /**
     * @author Ian Laird
     *
     * used to check if byte arrays are valid names or values respectively
     */
    private static class NameValueValidityCheckerAscii {
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
     * Checks if a name and value pair are valid
     * @param name the name to check
     * @param value the value to check
     * @throws BadAttributeException if they are invalid
     *    no exception being thrown does not indicate they are valid
     */
    public static void checkValidNameValueString(String name, String value) throws BadAttributeException{
        if(NAME_METHOD.equals(name)){
            if(!HTTP_METHODS.contains(value)){
                throw new BadAttributeException("unknown http method", "value");
            }
        }
        else if(name.equals(NAME_PATH)){
            if(value.getBytes(Charset.forName("ascii"))[0] != FORWARD_SLASH_ASCII){
                throw new BadAttributeException("Must prefix a forward slash on path value", "value");
            }
        }
        else if(name.equals(NAME_VERSION)){
            if(!value.equals(HTTP_VERSION)){
                throw new BadAttributeException("The version must be HTTP/2.0", "value");
            }
        }
        else if(name.equals(NAME_HOST)){
            //TODO
        }
        else if(name.equals(NAME_SCHEME)){
            if(!value.equals(HTTP_SCHEME)){
                throw new BadAttributeException("The scheme must be https", "value");
            }
        }

        //if it is not one of these do not throw anything
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
        this.isEnd = end;
    }

    /**
     * Returns string of the form
     * Headers: StreamID=&lt;streamid&gt; isEnd=&lt;end&gt; ([&lt;name&gt;
     * = &lt;value&gt;]...[lt;name> = &lt;value&gt;])
     * For example
     *
     * Headers: StreamID=5 isEnd=false ([method=GET][color=blue])
     *
     * @return the generated string
     */
    public String toString(){
        StringBuilder buildString = new StringBuilder();
        buildString.append("Headers: StreamID=")
                .append(this.getStreamID())
                .append(" isEnd=")
                .append(this.isEnd() ? "true" : "false")
                .append(" (");
        for (Map.Entry<String, String> entry : this.nameValuePairs.entrySet()) {
            buildString.append("[")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("]");
        }
        buildString.append(")");
        return buildString.toString();
    }

    /**
     * @param name the name
     * @return the value
     */
    public String getValue(String name){
        return this.nameValuePairs.get(name);
    }

    /**
     * get set of names in headers
     * @return set of names
     */
    public SortedSet<String> getNames(){
        return new TreeSet<>(this.nameValuePairs.keySet());
    }

    /**
     * Add name/value pair to header. If the name is already contained in the header, the corresponding value is replaced by the new value.
     * @param name name to add
     * @param value value to add/replace
     * @throws BadAttributeException if invalid name or value
     */
    public void addValue(String name, String value) throws BadAttributeException{
        checkValidNameValueString(name, value);

        //make sure that the ascii is allowable as well
        NameValueValidityCheckerAscii.checkValid(name.getBytes(StandardCharsets.US_ASCII),
                value.getBytes(StandardCharsets.US_ASCII), DECODE_MODE);

        this.nameValuePairs.put(name, value);
    }


    /**
     * tests if the stream id is valid for the Headers type
     * @param streamId the stream id to be verified
     * @throws BadAttributeException if the stream id is 0x0
     */
    @Override
    protected void ensureValidStreamId(int streamId) throws BadAttributeException {
        if(streamId == Message.REQUIRED_SETTINGS_STREAM_ID) {
            throw new BadAttributeException("0x0 not allowed as " +
                    "stream identifier for headers frame", "streamID");
        }
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            for (Map.Entry<String, String> entry : this.nameValuePairs.entrySet()) {
                byte[] name = entry.getKey().getBytes(StandardCharsets.US_ASCII);
                byte[] value = entry.getValue().getBytes(StandardCharsets.US_ASCII);
                encoder.encodeHeader(out, name, value, ENCODING_SENSITIVE);
            }
        }catch(IOException e){
            return new byte [0];
        }
        return out.toByteArray();
    }



    /**
     * processes all of the names and values
     * @throws BadAttributeException if something bad happens
     */
    private void processAllNameValues() throws BadAttributeException{
        for(Map.Entry<byte [], byte []> entry: toProcess.entrySet()) {
            byte [] name = entry.getKey();
            byte [] value = entry.getValue();

            //TODO do I need this line?
            NameValueValidityCheckerAscii.checkValid(name, value, DECODE_MODE);

            String n = byteArrayToString(name);
            String v = byteArrayToString(value);
            this.addValue(n, v);
        }
        toProcess.clear();
    }

    /**
     *
     * @param name the name
     * @param value the value
     * @param sensitive false means not sensitive
     */
    private void addValue(byte [] name, byte [] value, boolean sensitive){
        this.toProcess.put(name, value);
    }

    /**
     * converts array of ascii chars to a String
     * @param b the byte array
     * @return a newly created String
     */
    private static String byteArrayToString(byte [] b){
        return Base64.getEncoder().encodeToString(b);
    }

    /**
     *
     * @param parsed the contents of the header of the message
     * @param payload the payload of the message
     * @param decoder the decoder (cannot be null)
     * @return this message after being modified
     * @throws BadAttributeException if validation exception
     */
    @Override
    protected Message performDecode(HeaderParts parsed, byte [] payload, Decoder decoder) throws BadAttributeException{
        Objects.requireNonNull(decoder,
                "The decoder may not be null for a Headers Message");
        if(checkBitSet(parsed.flags, HEADERS_BAD_FLAG_ONE)){
            throw new BadAttributeException("Bad flag 0x8 set", "flags");
        }
        if(checkBitSet(parsed.flags, HEADERS_BAD_FLAG_TWO)){
            throw new BadAttributeException("Bad flag 0x20 set", "flags");
        }
        if(!checkBitSet(parsed.flags, HEADERS_END_HDR_FLAG)){
            throw new BadAttributeException("END HDR flag not set", "flags");
        }
        this.setEnd(checkBitSet(parsed.flags, HEADERS_END_STREAM_FLAG));
        this.setStreamID(parsed.getStreamId());

        // uses the twitter library to decode the header block and adds all attributes
        addHeaderFieldsToHeader(this, payload, decoder);
        return this;
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
}
