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
public class ACK extends HostPortMessage{

    /**
     * Create an ACK message from given values
     * @param host host ID
     * @param port port
     * @throws IllegalArgumentException if any validation problem with host and/or port, including null, etc.
     */
    public ACK(String host, int port) throws IllegalArgumentException{
        super(host, port);
    }

    /**
     * creates an ACK from a New
     * @param n the new to use
     */
    public ACK(New n){
        super(n.getHost(), n.getPort());
    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getOperation(){
        return ACK_OP;
    }
}
