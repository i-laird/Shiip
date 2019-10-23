/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import java.util.Objects;

/**
 * @author Ian Laird
 * Error Message
 */
public class Error extends Message{

    // the minimum length of an error message
    private static int ERROR_MESSAGE_MINIMUM_LENGTH = 1;

    private String errorMessage = null;

    /**
     * Create an Error message from given values
     * @param errorMessage the error message
     * @throws IllegalArgumentException if any validation problem with errorMessage, including null, etc.
     */
    public Error(String errorMessage) throws IllegalArgumentException{
        setErrorMessage​(errorMessage);
    }

    /**
     * gets the error message
     * @return the error message
     */
    public String getErrorMessage(){
        return this.errorMessage;
    }

    /**
     * sets the error message
     * @param errorMessage the error message
     * @throws IllegalArgumentException if validation fails, including null
     */
    public final void setErrorMessage​(String errorMessage) throws IllegalArgumentException{
        if(Objects.isNull(errorMessage)){
            throw new IllegalArgumentException("error message cannot be null", new NullPointerException("error message cannot be null"));
        }
        if(errorMessage.length() < ERROR_MESSAGE_MINIMUM_LENGTH){
            throw new IllegalArgumentException("Error message must be at least 1 char");
        }
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
     * &lt;message&gt;
     * For example
     *
     * ERROR Bad stuff
     * @return the payload
     */
    @Override
    public String getPayload(){
        return this.getErrorMessage();
    }
}