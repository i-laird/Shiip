/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import jack.util.HostPortPair;

import java.util.*;
import java.util.stream.Collectors;

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
        return this.hostPort.stream().map(x -> x + " ").collect(Collectors.toList());
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
    public final void addService(String host, int port) throws IllegalArgumentException{
        if(Objects.isNull(host)){
            throw new IllegalArgumentException("host cannot be null", new NullPointerException("host cannot be null"));
        }
        portValidator(port);

        // each string is host:port
        this.hostPort.add(host + ":" + Integer.toString(port));
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
            stringBuilder.append(s).append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * the tostring payload of a response message
     * @return the tostring payload
     */
    @Override
    public String getToStringPayload() {
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: this.hostPort){
            stringBuilder.append("[").append(s).append("]");
        }
        return stringBuilder.toString();
    }

    /**
     * gets a respnse
     * @param payload the payload
     * @return the response message
     */
    public static Response decodeResponse(String payload){
        if(Objects.isNull(payload) || payload.length() == 0){
            return new Response();
        }
        // first make sure that the payload is properly formatted
        if(!payload.matches(RESPONSE_REGEX)){
            throw new IllegalArgumentException("Payload not properly formatted");
        }

        Response toReturn = new Response();
        String [] pairs = payload.split(" ");
        Arrays.stream(pairs).forEach( x -> {
            HostPortPair hp = HostPortPair.getFromString(x);
            toReturn.addService(hp.getHost(), hp.getPort());
        });
        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Response response = (Response) o;
        return Objects.equals(hostPort, response.hostPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostPort);
    }
}
