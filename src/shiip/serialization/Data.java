/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/


package shiip.serialization;

/**
 * Data message
 *
 * @version 1.0
 * @author Ian laird
 */
public class Data extends Message {

    /**
     * Creates Data message from given values
     *
     * @param streamID stream ID
     * @param isEnd true if last data message
     * @param data bytes of application data
     * @throws BadAttributeException if attribute invalid (set protocol spec)
     */
    public Data(int streamID, boolean isEnd, byte[] data) throws BadAttributeException{

    }

    /**
     *
     * @param obj
     * @return
     */
    public boolean equals​(java.lang.Object obj){

    }

    /**
     *
     * @return
     */
    public byte [] getData(){

    }

    @Override
    public int hashCode(){

    }

    @Override
    public boolean equald(Object obj){

    }

    /**
     * Return end value
     * @return end value
     */
    public boolean isEnd(){

    }

    /**
     * Set data
     * @param data data to set
     * @throws BadAttributeException if invalid
     */
    public void setData(byte [] data) throws BadAttributeException{

    }

    /**
     * Set end value
     * @param end end value
     */
    public void setEnd(boolean end){

    }

    /**
     * Returns string of the form
     * Data: StreamID=<streamid> isEnd=<end> data=<length>
     *
     * For example:
     *
     * Data: StreamID=5 isEnd=true data: 5
     * @return
     */
    @Override
    public java.lang.String toString(){

    }
}
