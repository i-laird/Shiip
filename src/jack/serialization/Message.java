/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import jack.client.Client;
import jack.util.HostPortPair;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Ian Laird
 * a jack message
 */
public abstract class Message {

    // the regex for matching a name
    public static final String NAME_REGEX = "([a-zA-Z0-9\\.\\-])+";

    // the regex for matching a response payload
    public static final String RESPONSE_REGEX = "(" + NAME_REGEX + ":[0-9]+ )*";

    // the charset that is being used
    public static final Charset ENC = StandardCharsets.US_ASCII;

    // the op for query message
    public static final String QUERY_OP_FULL = "QUERY";

    // the op for response message
    public static final String RESPONSE_OP_FULL = "RESPONSE";

    // the op for new message
    public static final String NEW_OP_FULL = "NEW";

    // the op for ack message
    public static final String ACK_OP_FULL = "ACK";

    // the op for error message
    public static final String ERROR_OP_FULL = "ERROR";

    // the op for a query
    public static final char QUERY_OP    = 'Q';

    // the op for a new
    public static final char NEW_OP      = 'N';

    // the op for an ack
    public static final char ACK_OP      = 'A';

    // the op for a response
    public static final char RESPONSE_OP = 'R';

    // the op for an error
    public static final char ERROR_OP    = 'E';

    // if unable to parse the message
    private static final String DECODE_ISSUE = Client.INVALID_MESSAGE + " expected <Op><sp><Payload>";

    // the maximum size of a message (65535 - 8 - 20)
    public static final int MESSAGE_MAXIMUM_SIZE = 65507;

    // the maximum size of the message payload (minus OP and a space char)
    public static final int MAXIMUM_MESSAGE_PAYLOAD_SIZE = MESSAGE_MAXIMUM_SIZE - 2;

    private static List<String> acceptableOP = new LinkedList<>();

    static {
        acceptableOP.add(QUERY_OP_FULL);
        acceptableOP.add(RESPONSE_OP_FULL);
        acceptableOP.add(NEW_OP_FULL);
        acceptableOP.add(ACK_OP_FULL);
        acceptableOP.add(ERROR_OP_FULL);
    }

    // the lowest valid port
    protected static final int PORT_LOWER_BOUND = 1;

    // the highest valid port
    private static final int PORT_UPPER_BOUND = 65535;

    // shortest possible (only an empty response)
    private static final int SHORTEST_POSSIBLE_LENGTH = 2;

    // space is the second character
    private static final int SPACE_LOC = 1;

    /**
     * deserialize message from given bytes
     * @param msgBytes the message bytes
     * @return the specific message
     * @throws java.lang.IllegalArgumentException
     *     if validation fails, including null msgBytes
     */
    public static Message decode(byte [] msgBytes)  throws IllegalArgumentException{

        if(Objects.isNull(msgBytes)){
            throw new IllegalArgumentException("msgbytes cannot be null", new NullPointerException("msgbytes"));
        }

        if(msgBytes.length > MESSAGE_MAXIMUM_SIZE){
            throw new IllegalArgumentException("Maximum length of a UDP packet exceeded");
        }

        // convert to a String
        String msgString = new String(msgBytes, ENC);

        // if less than 2 is always invalid
        if(msgBytes.length < SHORTEST_POSSIBLE_LENGTH){
            throw new IllegalArgumentException(DECODE_ISSUE);
        }

        // make sure that the second char is a space
        if (msgString.charAt(SPACE_LOC) != ' ') {
            throw new IllegalArgumentException(DECODE_ISSUE);
        }

        // if the message is of length 3 it must be a response or query
        if(msgBytes.length == SHORTEST_POSSIBLE_LENGTH){

            char firstChar = msgString.charAt(0);
            // see if it is an invalid message
            if((firstChar != RESPONSE_OP) && (firstChar != QUERY_OP)){
                throw new IllegalArgumentException(DECODE_ISSUE);
            }

            // an empty response
            if(firstChar == RESPONSE_OP){
                return new Response();
            }
            return new Query("");
        }

        // now get the code
        char OP_char = msgString.charAt(0);

        // get the payload
        String payload = msgString.substring(2);

        HostPortPair parsed = null;
        switch(OP_char){
            case QUERY_OP:
                return new Query(payload);
            case RESPONSE_OP:
                return Response.decodeResponse(payload);
            case NEW_OP:
                parsed = HostPortPair.getFromString(payload);
                return new New(parsed.getHost(), parsed.getPort());
            case ACK_OP:
                parsed = HostPortPair.getFromString(payload);
                return new ACK(parsed.getHost(), parsed.getPort());
            case ERROR_OP:
                return new Error(payload);
            default:
                throw new IllegalArgumentException(Client.INVALID_MESSAGE + "Unrecognized OP");
        }
    }

    /**
     * serializes the message
     * @return the serialized message
     */
    public byte[] encode() {
        String message = this.getFullOperation().toUpperCase().charAt(0) + " " + this.getPayload();
        return message.getBytes(ENC);
    }

    /**
     * gets the operation
     * @return the operation
     */
    public abstract String getFullOperation();

    /**
     * gets the operation
     * @return the OP
     */
    public String getOperation(){
        return getFullOperation().substring(0, 1);
    }

    /**
     * gets the payload of a message
     * @return the payload
     */
    public abstract String getPayload();

    /**
     * gets the string of the Message
     * @return the string
     */
    @Override
    public final String toString(){
        return this.getFullOperation() + " " + this.getToStringPayload();
    }

    /**
     * can be used to change how tostring is represented
     * @return the payload of the string
     */
    public String getToStringPayload(){
        return this.getPayload();
    }

    /**
     * makes sure that the payload of the message is not too long
     */
    protected void testPayloadLength(){

        // make sure that the newly created message is not too long
        String payload = this.getPayload();
        if(payload.length() > MAXIMUM_MESSAGE_PAYLOAD_SIZE){
            throw new IllegalArgumentException("Maximum size of a datagram exceeded");
        }
    }

    /**
     * validates that the port is legal
     * @param port the port ot check
     * @throws IllegalArgumentException if invalid port
     */
    protected static void portValidator(int port) throws IllegalArgumentException{
        if((port < PORT_LOWER_BOUND) || (port > PORT_UPPER_BOUND)){
            throw new IllegalArgumentException("port must be between 1 and 65535");
        }
    }

    /**
     * validates the name
     * @param name the name to validate
     * @throws IllegalArgumentException if it does not match the regular expression
     */
    protected static void nameValidator(String name) throws IllegalArgumentException{

        // check if the name is null
        if(Objects.isNull(name)){
            throw new IllegalArgumentException("name cannot be null", new NullPointerException("name cannot be null"));
        }

        if(!name.matches(NAME_REGEX)){
            throw new IllegalArgumentException("invalid name");
        }
    }

    /**
     * validates a query
     * @param query the query to validate
     * @throws IllegalArgumentException if the query is invalid
     */
    protected static void queryValidator(String query) throws IllegalArgumentException{

        // both star and empty string are valid queries
        if("*".equals(query) || query.isEmpty()){
            return;
        }

        // if not all then check for name match
        nameValidator(query);
    }

}
