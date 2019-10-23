/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import jack.util.HostPortPair;

/**
 * @author Ian Laird
 * @version  1.0
 * New Message
 */
public class New extends HostPortMessage{

    /**
     * Creates a New message from given values
     * @param host the host ID
     * @param port the port
     * @throws IllegalArgumentException if any validation problem with host and/or port, including null, etc.
     */
    public New(String host, int port) throws IllegalArgumentException{
        super(host, port);
    }

    public New(HostPortPair m){
        super(m.getHost(), m.getPort());
    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getOperation(){
        return NEW_OP;
    }
}
