/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 6
 * Class: Data Comm
 *******************************************************/

package shiip.server.exception;

/**
 * @author Ian Laird
 * for an invalid connection preface
 */
public class ConnectionPrefaceException extends Throwable{

    // the connection preface that was received
    private String receivedString = "";

    /**
     * default constructor
     */
    public ConnectionPrefaceException(){
        super();
    }

    /**
     * constructor
     * @param message the message for the exception
     */
    public ConnectionPrefaceException(String message){
        super(message);
    }

    /**
     * custom constructor
     * @param message the message
     * @param receivedString the connection preface that was received
     */
    public ConnectionPrefaceException(String message, String receivedString){
        super(message);
        this.receivedString = receivedString;
    }

    /**
     * gets the connection preface that was received
     * @return the connection preface
     */
    public String getReceivedString() {
        return receivedString;
    }
}
