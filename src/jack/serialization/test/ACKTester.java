/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.ACK;
import jack.serialization.Message;

import static jack.serialization.test.ResponseTester.VALID_PORT;

/**
 * @author Ian Laird and Andrew Walker
 */
public class ACKTester extends HostPortTester{

    /**
     * gets an Ack object
     * @param host the host
     * @param port the port
     * @return an Ack
     */
    protected Message getNewObject(String host, int port){
        return new ACK(host, port);
    }

    /**
     * sets the host
     * @param s the string to set
     * @return the retrieved host
     */
    protected String testHostSetter(String s){
        ACK toTest = new ACK("toChange", VALID_PORT);
        toTest.setHost(s);
        return toTest.getHost();
    }

    /**
     * tests setting port
     * @param host the host name
     * @param port the port to set
     * @return the get port
     */
    @Override
    protected int testPortSetter(String host, int port){
        ACK toTest = new ACK(host, 1);
        toTest.setPort(port);
        return toTest.getPort();
    }

    /**
     * ACK
     * @return ACK
     */
    protected String getMessageType(){
        return "ACK";
    }
}
