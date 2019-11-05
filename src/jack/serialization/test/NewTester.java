/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Message;
import jack.serialization.New;

import static jack.serialization.test.ResponseTester.VALID_PORT;

/**
 * @author Ian Laird and Andrew Walker
 */
public class NewTester extends HostPortTester {

    /**
     * gets a New for the vals
     * @param host the host
     * @param port the port
     * @return a New
     */
    protected Message getNewObject(String host, int port){
        return new New(host, port);
    }

    /**
     * tests setting host
     * @param s the string to set
     * @return the get host
     */
    protected String testHostSetter(String s){
        New toTest = new New("toChange", VALID_PORT);
        toTest.setHost(s);
        return toTest.getHost();
    }

    /**
     * tests setting port
     * @param port the port to set
     * @return the get port
     */
    protected int testPortSetter(int port){
        New toTest = new New("toChange", port + 1);
        toTest.setPort(port);
        return toTest.getPort();
    }

    /**
     * NEW
     * @return NEW
     */
    protected String getMessageType(){
        return "NEW";
    }
}
