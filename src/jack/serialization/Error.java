/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

/**
 * @author Ian Laird
 * Error Message
 */
public class Error extends Message{

    /**
     * Create an Error message from given values
     * @param errorMessage the error message
     * @throws IllegalArgumentException if any validation problem with errorMessage, including null, etc.
     */
    public Error(String errorMessage) throws IllegalArgumentException{

    }

    /**
     * gets the error message
     * @return the error message
     */
    public String getErrorMessage(){
        return null;
    }

    /**
     * sets the error message
     * @param errorMessage the error message
     * @throws IllegalArgumentException if validation fails, including null
     */
    public final void setErrorMessage​(String errorMessage) throws IllegalArgumentException{

    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getOperation(){
        return ERROR_OP;
    }

    /**
     * gets the payload of a message
     * returns string of the form
     * ERROR &lt;message&gt;
     * For example
     *
     * ERROR Bad stuff
     * @return the payload
     */
    @Override
    public String getPayload(){
        return null;
    }
}
