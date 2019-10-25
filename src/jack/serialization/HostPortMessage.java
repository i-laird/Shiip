/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import java.util.Objects;

/**
 * @author Ian Laird
 * @version 1.0
 * abstraction of both {@link ACK} and {@link New}
 */
public abstract class HostPortMessage extends Message {

    protected String host;
    protected int port;

    /**
     * gets the host
     * @return host
     */
    public String getHost(){
        return this.host;
    }

    /**
     * Create a host or port message from given values
     * @param host host ID
     * @param port port
     * @throws IllegalArgumentException if any validation problem with host and/or port, including null, etc.
     */
    public HostPortMessage(String host, int port){
        setHost(host);
        setPort(port);
    }

    /**
     * sets the host
     * @param host the host
     * @throws IllegalArgumentException if validation failure, including null host
     */
    public final void setHost(String host) throws IllegalArgumentException{
        nameValidator(host);
        this.host = host;
    }

    /**
     * sets the port
     * @param port new port
     * @throws IllegalArgumentException if validation fails
     */
    public final void setPort(int port) throws IllegalArgumentException{
        portValidator(port);
        this.port = port;
    }

    /**
     * gets the port num
     * @return the port num
     * @throws IllegalArgumentException if validation fails
     */
    public int getPort() throws IllegalArgumentException{
        return this.port;
    }

    /**
     * gets the payload of a message
     *   returns string of the following form:
     *   [&lt;name&gt;:&lt;port&gt;]
     *   For example
     *
     *   ACK [google.com:8080]
     * @return the payload
     */
    @Override
    public String getPayload(){
        return this.getHost() + ":" + this.getPort();
    }

    /**
     * gets the payload of the tostring
     * returns string of the following form:
     *     &lt;name&gt;:&lt;port&gt;
     * @return the string
     */
    @Override
    public String getToStringPayload(){
        return "[" + this.getHost() + ":" + Integer.toString(this.getPort()) + "]";
    }

    /**
     * equals
     * @param o the object to compare against
     * @return true means equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null){
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        HostPortMessage that = (HostPortMessage) o;
        return port == that.port &&
                Objects.equals(host, that.host);
    }

    /**
     * hashcode
     * @return same values will have same hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
