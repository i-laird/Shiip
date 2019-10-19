/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

/**
 * @author Ian Laird
 * a jack message
 */
public abstract class Message {

    /**
     * deserialize message from given bytes
     * @param msgBytes the message bytes
     * @return the specific message
     * @throws java.lang.IllegalArgumentException
     *     if validation fails, including null msgBytes
     */
    public static Message decode(byte [] msgBytes)  throws IllegalArgumentException{
        return null;
    }

    /**
     * serializes the message
     * @return the serialized message
     */
    public abstract byte[] encode();

    /**
     * gets the operation
     * @return the operation
     */
    public abstract String getOperation();
}
