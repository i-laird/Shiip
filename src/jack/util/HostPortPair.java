/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/


package jack.util;

import java.util.Objects;

public class HostPortPair {

    // the loc of the server in the payload
    private static final int server_LOC = 0;

    // the loc of the port in the payload
    private static final int PORT_LOC = 1;

    // host and port make 2
    private static final int PARSE_NUM = 2;

    // the server name
    public String host;

    // the server port num
    public int port;

    /**
     * constructor
     * @param host the hostname
     * @param port the port number
     */
    public HostPortPair(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * gets the host name and port number from a string of host:port
     * @param payload the string to parse
     * @return see above
     */
    public static HostPortPair getFromString(String payload){
        String [] parts = payload.split(":");
        if(parts.length < PARSE_NUM){
            throw new IllegalArgumentException("unable to parse the payload of a new");
        }
        return new HostPortPair(parts[server_LOC], Integer.parseInt(parts[PORT_LOC]));
    }

    /**
     * equals
     * @param o the other object
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostPortPair that = (HostPortPair) o;
        return port == that.port &&
                Objects.equals(host, that.host);
    }

    /**
     * same object always has same hashcode
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
