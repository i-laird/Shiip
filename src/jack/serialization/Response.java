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
    private Map<String, Set<Integer>> hostPort;

    /**
     * Construct response with empty host:port list
     */
    public Response(){
        this.hostPort = new TreeMap<>();
    }

    /**
     * Get the service (string representation) list where each service
     * is represented as &lt;name&gt;:&lt;port&gt;&lt;space&gt;
     * (e.g., google:8000)
     * @return service list
     */
    public List<String> getServiceList(){
        List<String> toReturn = new LinkedList<>();
        this.hostPort.entrySet().stream().forEach( x -> x.getValue().forEach(y -> toReturn.add(x.getKey() + ":" + y.toString())));
        return Collections.unmodifiableList(toReturn);
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
        nameValidator(host);
        portValidator(port);

        // see if this host already exists
        Set<Integer> ports = hostPort.get(host);

        if(Objects.isNull(ports)){
            ports = new TreeSet<Integer>();
            hostPort.put(host, ports);
        }

        ports.add(port);

        // make sure that the maximum size of a message has not been exceeded
        this.testPayloadLength();
    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getFullOperation(){
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
        for(String s: this.getServiceList()){
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
        for(String s: this.getServiceList()){
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

        payload = payload.trim();
        Response toReturn = new Response();
        String [] pairs = payload.split(" ");
        Arrays.stream(pairs).forEach( x -> {
            HostPortPair hp = HostPortPair.getFromString(x);
            toReturn.addService(hp.getHost(), hp.getPort());
        });
        return toReturn;
    }

    /**
     * see if two Response are equal
     * @param o the other response
     * @return true means that they are equal
     */
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

    /**
     * two equal obj will have same hashcode
     * @return the hashcode of the response
     */
    @Override
    public int hashCode() {
        return Objects.hash(hostPort);
    }
}
