/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import jack.util.HostPortPair;

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
    public ACK(HostPortMessage n){
        super(n.getHost(), n.getPort());
    }

    /**
     * created from pair of host and port
     * @param m the pair of host and port
     */
    public ACK(HostPortPair m){
        super(m.getHost(), m.getPort());
    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getFullOperation(){
        return ACK_OP_FULL;
    }
}
