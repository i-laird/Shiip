/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization.test;

import jack.serialization.Message;
import jack.serialization.New;

import static jack.serialization.test.ResponseTester.VALID_PORT;

public class NewTester extends HostPortTester{

    protected Message getNewObject(String host, int port){
        return new New(host, port);
    }

    protected String testHostSetter(String s){
        New toTest = new New("toChange", VALID_PORT);
        toTest.setHost​(s);
        return toTest.getHost();
    }

    protected int testPortSetter(int port){
        New toTest = new New("toChange", port + 1);
        toTest.setPort(port);
        return toTest.getPort();
    }
    protected String getMessageType(){
        return "NEW";
    }

}
