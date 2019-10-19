/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 4
 * Class: Data Comm
 *******************************************************/

package jack.serialization;

/**
 * @author Ian Laird
 * @version 1.0
 * query message
 */
public class Query extends Message{

    /**
     * Creates a Query message from given values
     * @param searchString search String for query
     * @throws IllegalArgumentException if any validation problem with searchString, including null, etc.
     */
    public Query(String searchString) throws IllegalArgumentException{

    }

    /**
     * gets the search string
     * @return the search string
     */
    public String getSearchString(){
        return null;
    }

    /**
     * sets the search string
     * @param searchString the string to search for
     * @throws IllegalArgumentException if search string fails validation, including null
     */
    public final void setSearchString​(String searchString) throws IllegalArgumentException{

    }

    /**
     * gets the operation
     * @return the operation
     */
    @Override
    public String getOperation(){
        return QUERY_OP;
    }

    /**
     * gets the payload of a message
     *  QUERY &lt;query&gt;
     * For example
     *
     * QUERY win
     * @return the payload
     */
    @Override
    public String getPayload(){
        return null;
    }
}
