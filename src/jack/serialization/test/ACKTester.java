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
     * sets and gets the port
     * @param port the port to set
     * @return the port that was retrieved
     */
    protected int testPortSetter(int port){
        ACK toTest = new ACK("toChange", port + 1);
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
