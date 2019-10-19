/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

/**
 * @author Ian Laird
 * ACK message
 */
public class ACK {

    /**
     * Create an ACK message from given values
     * @param host host ID
     * @param port port
     * @throws IllegalArgumentException if any validation problem with host and/or port, including null, etc.
     */
    public ACK(String host, int port) throws IllegalArgumentException{

    }

    /**
     * returns string of the following form:
     * ACK [&lt;name&gt;:&lt;port&gt;]
     * For example
     *
     * ACK [google.com:8080]
     * @return the string as defined above
     */
    public String toString(){
        return null;
    }

    /**
     * gets the host
     * @return host
     */
    public String getHost(){
        return null;
    }

    /**
     * sets the host
     * @param host the host
     * @throws IllegalArgumentException if validation failure, including null host
     */
    public final void setHost​(String host) throws IllegalArgumentException{

    }

    /**
     * sets the port
     * @param port new port
     * @throws IllegalArgumentException if validation fails
     */
    public final void setPort(int port) throws IllegalArgumentException{

    }

    /**
     * gets the port num
     * @return the port num
     * @throws IllegalArgumentException if validation fails
     */
    public int getPort() throws IllegalArgumentException{
        return 0;
    }
}
