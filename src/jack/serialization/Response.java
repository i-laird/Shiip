/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

import java.util.List;

/**
 * @author Ian Laird
 * @version 1.0
 * The list of services from any method (e.g., toString, encode, etc.)
 * must be sorted by Java's default String order for the String
 * representation of a service (e.g., &lt;name&gt;:&lt;port&gt;)
 */
public class Response {

    /**
     * Construct response with empty host:port list
     */
    public Response(){

    }

    /**
     * Get the service (string representation) list where each service
     * is represented as &lt;name&gt;:&lt;port&gt;&lt;space&gt;
     * (e.g., google:8000)
     * @return service list
     */
    public List<String> getServiceList(){
        return null;
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

    }

    /**
     * Returns string of the form
     *
     * RESPONSE [&lt;name&gt;:&lt;port&gt;&lt;space&gt;]*
     * For example
     *
     * RESPONSE [wind:8000][fire:7000]
     * @return the string
     */
    @Override
    public String toString(){
        return null;
    }
}
