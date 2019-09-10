/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

/**
 * Window_Update frame
 *
 * @version 1.0
 * @author Ian laird
 */
public class Window_Update extends Message {

    /**
     * Creates Window_Update message from given values
     *
     * @param streamID stream ID
     * @param increment true if last data message
     * @throws BadAttributeException if attribute invalid (set protocol spec)
     */
    public Window_Update(int streamID, int increment) throws BadAttributeException{

    }

    /**
     * Get increment value
     *
     * @return increment value
     */
    public int getIncrement(){

    }

    /**
     * Set increment value
     *
     * @param increment increment value
     * @throws BadAttributeException if invalid
     */
    public void setIncrement(int increment) throws BadAttributeException{

    }

    /**
     * Returns string of the form
     * Window_Update: StreamID=<streamid> increment=<inc>
     *     For example :
     * Window_Update: StreamID=5 increment=1024
     *
     * @return the string of the correct form
     */
    @Override
    public java.lang.String toString(){

    }
}
