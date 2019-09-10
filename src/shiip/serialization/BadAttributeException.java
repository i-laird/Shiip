/*******************************************************
 * Author: Ian Laird
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/

package shiip.serialization;

import java.io.Serializable;
import java.util.Objects;

/**
 * Thrown if problem with attribute
 *
 * @version 1.0
 * @author Ian laird
 */
public class BadAttributeException extends Exception implements Serializable {

    private String attribute = null;

    /**
     * Constructs a BadAttributeException with given message, attribute,
     *     and cause
     *
     * @param message detail message (null permitted)
     * @param attribute attribute related to problem (null permitted)
     * @param cause underlying cause (null is permitted and indicates
     *             no or unknown cause)
     */
    public BadAttributeException(java.lang.String message,
                                 java.lang.String attribute,
                                 java.lang.Throwable cause){
        super(message, cause);
        this.attribute = attribute;
    }

    /**
     * Constructs a BadAttributeException with given message
     * and attribute with no given cause
     *
     * @param message detail message
     * @param attribute attribute related to problem
     */
    public BadAttributeException(java.lang.String message,
                                 java.lang.String attribute){
        super(message);
        this.attribute = attribute;
    }

    /**
     * Return attribute related to problem
     *
     * @return attribute name
     */
    public java.lang.String getAttribute(){
        return this.attribute;
    }
}
