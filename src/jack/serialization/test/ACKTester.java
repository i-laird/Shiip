/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.ACK;
import jack.serialization.Message;

import static jack.serialization.test.ResponseTester.VALID_PORT;

public class ACKTester extends HostPortTester{

    protected Message getNewObject(String host, int port){
        return new ACK(host, port);
    }

    protected String testHostSetter(String s){
        ACK toTest = new ACK("toChange", VALID_PORT);
        toTest.setHost​(s);
        return toTest.getHost();
    }

    protected int testPortSetter(int port){
        ACK toTest = new ACK("toChange", port + 1);
        toTest.setPort(port);
        return toTest.getPort();
    }

    protected String getMessageType(){
        return "ACK";
    }
}
