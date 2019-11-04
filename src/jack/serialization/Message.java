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
    protected static final Charset ENC = StandardCharsets.US_ASCII;

    // the op for query message
    protected static final String QUERY_OP    = "QUERY";

    // the op for response message
    protected static final String RESPONSE_OP = "RESPONSE";

    // the op for new message
    protected static final String NEW_OP      = "NEW";

    // the op for ack message
    protected static final String ACK_OP      = "ACK";

    // the op for error message
    protected static final String ERROR_OP    = "ERROR";

    // if unable to parse the message
    private static final String DECODE_ISSUE = Client.INVALID_MESSAGE + " expected <Op><sp><Payload>";

    private static List<String> acceptableOP = new LinkedList<>();

    static {
        acceptableOP.add(QUERY_OP);
        acceptableOP.add(RESPONSE_OP);
        acceptableOP.add(NEW_OP);
        acceptableOP.add(ACK_OP);
        acceptableOP.add(ERROR_OP);
    }

    // the lowest valid port
    private static final int PORT_LOWER_BOUND = 1;

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

        // if the message is of length 3 it must be a response
        if(msgBytes.length == SHORTEST_POSSIBLE_LENGTH){

            // see if it is an invalid message
            if(msgString.charAt(0) != 'R'){
                throw new IllegalArgumentException(DECODE_ISSUE);
            }

            // an empty response
            return new Response();
        }

        // now get the code
        char OP_char = msgString.charAt(0);

        // get the payload
        String payload = msgString.substring(2);

        HostPortPair parsed = null;
        switch(OP_char){
            case 'Q':
                return new Query(payload);
            case 'R':
                return Response.decodeResponse(payload);
            case 'N':
                parsed = HostPortPair.getFromString(payload);
                return new New(parsed.getHost(), parsed.getPort());
            case 'A':
                parsed = HostPortPair.getFromString(payload);
                return new ACK(parsed.getHost(), parsed.getPort());
            case 'E':
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
        // TODO check about the one letter or many
        String message = this.getOperation().toUpperCase().charAt(0) + " " + this.getPayload();
        return message.getBytes(ENC);
    }

    /**
     * gets the operation
     * @return the operation
     */
    public abstract String getOperation();

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
        return this.getOperation() + " " + this.getToStringPayload();
    }

    /**
     * can be used to change how tostring is represented
     * @return the payload of the string
     */
    public String getToStringPayload(){
        return this.getPayload();
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
        if(query.equals("*")){
            return;
        }

        // if not all then check for name match
        nameValidator(query);
    }

}
