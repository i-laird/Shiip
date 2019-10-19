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
 * query message
 */
public class Query extends Message{

    private String searchString = null;

    /**
     * Creates a Query message from given values
     * @param searchString search String for query
     * @throws IllegalArgumentException if any validation problem with searchString, including null, etc.
     */
    public Query(String searchString) throws IllegalArgumentException{
        this.setSearchString​(searchString);
    }

    /**
     * gets the search string
     * @return the search string
     */
    public String getSearchString(){
        return this.getSearchString();
    }

    /**
     * sets the search string
     * @param searchString the string to search for
     * @throws IllegalArgumentException if search string fails validation, including null
     */
    public final void setSearchString​(String searchString) throws IllegalArgumentException{
        if(Objects.isNull(searchString)){
            throw new IllegalArgumentException("search string cannot be null", new NullPointerException("search string cannot be null"));
        }
        this.searchString = searchString;
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
        return this.getSearchString();
    }
}
