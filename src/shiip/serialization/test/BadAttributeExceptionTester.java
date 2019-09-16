/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import shiip.serialization.BadAttributeException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


/**
 * Performs testing for the {@link BadAttributeException}.
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
public class BadAttributeExceptionTester {

    private static final String message = "message";
    private static final String attribute = "attribute";

    /**
     * Performs tests on the two param constructor of BadAttributeException
     *
     * @version 1.0
     * @author Ian Laird, Andrew Walker
     */
    @Nested
    @DisplayName("Two Param Constructor")
    public class TwoParamConstructor {
        /**
         * Testing valid values
         */
        @DisplayName("Valid values")
        @Test
        public void testTwoParamConstructor(){
            BadAttributeException exception = new BadAttributeException(
                    message, attribute);
            assertAll(
                () -> assertEquals(attribute, exception.getAttribute()),
                () -> assertEquals(message, exception.getMessage())
            );
        }

        /**
         * Testing null values
         */
        @DisplayName("Null values")
        @Test
        public void testNullInTwoParamConstructor(){
            assertAll(
                () -> assertDoesNotThrow(
                    () -> new BadAttributeException(message, null)),
                () -> assertDoesNotThrow(
                    () -> new BadAttributeException(null, attribute)),
                () -> assertDoesNotThrow(
                    () -> new BadAttributeException(null, null))
            );
        }
    }


    @Nested
    @DisplayName("Three Param Constructor")
    public class ThreeParamConstructor {
        /**
         * Testing valid values
         */
        @DisplayName("Valid values")
        @Test
        public void testThreeParamConstructor(){
            Throwable cause = new Throwable();
            BadAttributeException exception = new BadAttributeException(
                    message, attribute, cause);
            assertAll(
                () -> assertEquals(attribute, exception.getAttribute()),
                () -> assertEquals(message, exception.getMessage()),
                () -> assertEquals(cause, exception.getCause())
            );
        }

        /**
         * Testing null values
         */
        @DisplayName("Null values")
        @Test
        public void testNullInThreeParamConstructor(){
            Throwable cause = new Throwable();
            assertAll(
                () -> assertDoesNotThrow(
                    () -> (new BadAttributeException(message, attribute, null))),
                () -> assertDoesNotThrow(
                    () -> (new BadAttributeException(message, null, cause))),
                () -> assertDoesNotThrow(
                    () -> (new BadAttributeException(null, attribute, cause))),
                () -> assertDoesNotThrow(
                    () -> (new BadAttributeException(null, null, null)))
            );
        }
    }
}
