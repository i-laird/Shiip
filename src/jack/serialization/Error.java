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
public class Error {

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
     * returns string of the form
     * ERROR &lt;message&gt;
     * For example
     *
     * ERROR Bad stuff
     * @return the string
     */
    @Override
    public String toString(){
        return null;
    }
}
