/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import java.util.*;

/**
 * @author Ian Laird
 * @version 1.0
 * The list of services from any method (e.g., toString, encode, etc.)
 * must be sorted by Java's default String order for the String
 * representation of a service (e.g., &lt;name&gt;:&lt;port&gt;)
 */
public class Response extends Message {

    // the host and port pairs stored as a string
    private Set<String> hostPort;

    /**
     * Construct response with empty host:port list
     */
    public Response(){
        this.hostPort = new TreeSet<>();
    }

    /**
     * Get the service (string representation) list where each service
     * is represented as &lt;name&gt;:&lt;port&gt;&lt;space&gt;
     * (e.g., google:8000)
     * @return service list
     */
    public List<String> getServiceList(){
        List<String> toReturn = new LinkedList<>();
        for(String s: this.hostPort){
            toReturn.add(s);
        }
        return toReturn;
    }

    /**
     * Add service to list The list of services must be sorted by Java's
     * default String order for the String representation of a service
     * (e.g., &lt;name&gt;:&lt;port&gt;)
     *
     * @param host new service host
     * @param port new service port
     * @throws IllegalArgumentException  if validation fails, including null host
     */
    public final void addService​(String host, int port) throws IllegalArgumentException{
        if(Objects.isNull(host)){
            throw new IllegalArgumentException("host cannot be null", new NullPointerException("host cannot be null"));
        }
        portValidator(port);
        //TODO ask about the space
        this.hostPort.add(host + Integer.toString(port) + " ");
    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getOperation(){
        return RESPONSE_OP;
    }

    /**
     * gets the payload of a message
     * RESPONSE [&lt;name&gt;:&lt;port&gt;&lt;space&gt;]*
     * For example
     *
     * RESPONSE [wind:8000][fire:7000]
     * @return the payload
     */
    @Override
    public String getPayload(){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: this.hostPort){
            stringBuilder.append("[").append(s).append("]");
        }
        return stringBuilder.toString();
    }
}
