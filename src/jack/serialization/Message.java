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

    private static List<String> acceptableOP = new LinkedList<>();

    static {
        acceptableOP.add(QUERY_OP);
        acceptableOP.add(RESPONSE_OP);
        acceptableOP.add(NEW_OP);
        acceptableOP.add(ACK_OP);
        acceptableOP.add(ERROR_OP);
    }

    // the lowest valid port
    private static int PORT_LOWER_BOUND = 1;

    // the highest valid port
    private static int PORT_UPPER_BOUND = 65535;

    // there should be two tokens (one for OP and one for payload)
    private static int NUM_TOKENS_EXPECTED = 2;

    // op char is one long
    private static int OP_CHAR_LEN = 1;

    /**
     * deserialize message from given bytes
     * @param msgBytes the message bytes
     * @return the specific message
     * @throws java.lang.IllegalArgumentException
     *     if validation fails, including null msgBytes
     */
    public static Message decode(byte [] msgBytes)  throws IllegalArgumentException{

        // convert to a String
        String msgString = new String(msgBytes, ENC);

        // now tokenize it according to space character
        String [] tokenized = msgString.split("\\s");

        if(tokenized.length != NUM_TOKENS_EXPECTED){
            throw new IllegalArgumentException
                    (Client.INVALID_MESSAGE +
                            "tokenization failed expected <Op><sp><Payload>");
        }

        String OP = tokenized[0];
        String payload = tokenized[1];
        char OP_char = OP.charAt(0);

        // if it is more than one char make sure it is one of the expected ones
        if(OP.length() != OP_CHAR_LEN){
            String toCompare = OP.toUpperCase();
            boolean flag = false;
            for(String compareAgainst : acceptableOP){
                flag |= compareAgainst.equals(toCompare);
            }
            if(!flag){
                throw new IllegalArgumentException(Client.INVALID_MESSAGE + "Unrecognized OP");
            }
        }
        Message toReturn = null;
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
        String message = this.getOperation().charAt(0) + " " + this.getPayload();
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
        return this.getOperation() + " " + this.getPayload();
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

        if(!name.matches("\\([a\\-z]\\|[A\\-Z]\\|[0\\-9]\\|\\.\\|-\\)\\+")){
            throw new IllegalArgumentException("invalid name");
        }
    }

}
