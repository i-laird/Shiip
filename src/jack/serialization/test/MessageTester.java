/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/


package jack.serialization.test;

import jack.serialization.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Ian Laird and Andrew Walker
 */
public class MessageTester {

    // the charset that is being used
    private static final Charset ENC = StandardCharsets.US_ASCII;

    // a valid ack message
    private static final String VALID_ACK_MESSAGE      = "A www.google.com:4987";

    // a valid error message
    private static final String VALID_ERROR_MESSAGE    = "E error message";

    // a valid new message
    private static final String VALID_NEW_MESSAGE      = "N localhost:3500";

    // a valid query
    private static final String VALID_QUERY_MESSAGE    = "Q *";

    // a valid response
    private static final String VALID_RESPONSE_MESSAGE = "R [localhost:3500][www.google.com:4987]";

    // a valid ack
    private static final String VALID_ACK_MESSAGE_TWO      = "A 0:1";

    // a valid error
    private static final String VALID_ERROR_MESSAGE_TWO    = "E 9";

    // a valid new message
    private static final String VALID_NEW_MESSAGE_TWO      = "N Z:65535";

    // a valid query
    private static final String VALID_QUERY_MESSAGE_TWO    = "Q Z";

    // a valid response
    private static final String VALID_RESPONSE_MESSAGE_TWO = "R [Z:65535]";

    // this is valid because there could be no response
    private static final String VALID_RESPONSE_MESSAGE_THREE = "R ";

    // 0 cannot be the port #
    private static final String INVALID_ACK_MESSAGE      = "A 0:0";

    // empty error message
    private static final String INVALID_ERROR_MESSAGE    = "E ";

    // need host to be specified
    private static final String INVALID_NEW_MESSAGE      = "N :4000";

    // double asterisk not allowed
    private static final String INVALID_QUERY_MESSAGE    = "Q **";

    //invalid because missing opening square bracket
    private static final String INVALID_RESPONSE_MESSAGE = "R [www.google.com:4987]localhost:3500]";

    //invalid because there is no space
    private static final String INVALID_RESPONSE_MESSAGE_TWO = "R";

    // only one char is allowed for the OP
    private static final String INVALID_MESSAGE_ONE = "QUERY *";

    // Z is not a valid OP
    private static final String INVALID_MESSAGE_TWO = "Z localhost:3000";

    // q should be capitalized
    private static final String INVALID_MESSAGE_THREE = "q *";

    // port needs to be a number
    private static final String INVALID_MESSAGE_FOUR = "N host:port";

    // all of the valid strings in a list
    private static List<String> validMessages = new LinkedList<>();

    // all of the invalid strings in a list
    private static List<String> invalidMessages = new LinkedList<>();

    // add all of the valid strings to the list
    static{
        validMessages.add(VALID_ACK_MESSAGE);
        validMessages.add(VALID_ERROR_MESSAGE);
        validMessages.add(VALID_NEW_MESSAGE);
        validMessages.add(VALID_QUERY_MESSAGE);
        validMessages.add(VALID_RESPONSE_MESSAGE);

        validMessages.add(VALID_ACK_MESSAGE_TWO);
        validMessages.add(VALID_ERROR_MESSAGE_TWO);
        validMessages.add(VALID_NEW_MESSAGE_TWO);
        validMessages.add(VALID_QUERY_MESSAGE_TWO);
        validMessages.add(VALID_RESPONSE_MESSAGE_TWO);

        validMessages.add(VALID_RESPONSE_MESSAGE_THREE);

        invalidMessages.add(INVALID_ACK_MESSAGE);
        invalidMessages.add(INVALID_ERROR_MESSAGE);
        invalidMessages.add(INVALID_NEW_MESSAGE);
        invalidMessages.add(INVALID_QUERY_MESSAGE);
        invalidMessages.add(INVALID_RESPONSE_MESSAGE);

        invalidMessages.add(INVALID_RESPONSE_MESSAGE_TWO);

        invalidMessages.add(INVALID_MESSAGE_ONE);
        invalidMessages.add(INVALID_MESSAGE_TWO);
    }

    /**
     * gets the ascii of a string
     * @param s the string
     * @return the  ascii chars
     */
    private static byte [] toAscii(String s){
        return s.getBytes(ENC);
    }

    /**
     * gets all valid byte arrays of messages
     * @return valid messages
     */
    public static Stream<String> validMessages(){
        return validMessages.stream();
    }

    /**
     * gets all invalid byte arrays of messages
     * @return invalid messages
     */
    public static Stream<String> invalidMessages(){
        return invalidMessages.stream();
    }

    /**
     * tests for valid messages
     * @param msg the message to decode and then encode
     */
    @ParameterizedTest(name = "bytes = {0}")
    @MethodSource("validMessages")
    @DisplayName("Valid Messages")
    public void testValidString(String msg){

        byte [] validMessage = toAscii(msg);

        // get the Message that corresponds to this byte array
        Message m = Message.decode(validMessage);

        // now encode the message again
        byte [] encoded = m.encode();

        // make sure that these byte arrays are equal
        assertArrayEquals(validMessage, encoded);

    }

    /**
     * tests invalid messages
     * @param msg the invalid message to test
     */
    @ParameterizedTest(name = "bytes = {0}")
    @MethodSource("invalidMessages")
    @DisplayName("Invalid Messages")
    public void testInvalidString(String msg){
        byte[] invalidMessage = toAscii(msg);
        assertThrows(IllegalArgumentException.class, () ->{
            Message.decode(invalidMessage);
        });
    }

    /**
     * tests null for bytes
     */
    @Test
    @DisplayName("Null msgBytes")
    public void testNullMsgBytes(){
        assertThrows(IllegalArgumentException.class, () ->{
            Message.decode(null);
        });
    }

    /**
     * tests empty bytes for message
     */
    @Test
    @DisplayName("Empty msgBytes")
    public void testEmptyMsgBytes(){
        assertThrows(IllegalArgumentException.class, () ->{
            byte[] invalidMessage = new byte[0];
            Message.decode(invalidMessage);
        });
    }
}
