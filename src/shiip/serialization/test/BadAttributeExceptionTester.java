package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shiip.serialization.BadAttributeException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Performs testing for the {@link shiip.serialization.BadAttributeException}.
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
@DisplayName("Bad Attribute Exception Tester")
public class BadAttributeExceptionTester {

    private static final String testMessage = "test message",
                                testAttribute = "test attribute";

    private class testException extends Throwable{
        public testException(){
            super();
        }
        public testException(String message){
            super(message);
        }

        @Override
        public boolean equals(Object o) {
            return this.getMessage().equals(((testException)o).getMessage());
        }
    }

    @DisplayName("testing two parameter constructor")
    @Test
    public void testTwoParamConstructor(){
        BadAttributeException exception = new BadAttributeException(
                testMessage, testAttribute);
        assertAll(
                () -> assertEquals(testAttribute, exception.getAttribute()),
                () -> assertEquals(testMessage, exception.getMessage())
        );
    }

    @DisplayName("testing three param")
    @Test
    public void testThreeParamConstructor(){
        Throwable cause = new testException("test thrown exception");
        BadAttributeException exception = new BadAttributeException(
                testMessage, testAttribute, cause);
        assertAll(
                () -> assertEquals(testAttribute, exception.getAttribute()),
                () -> assertEquals(testMessage, exception.getMessage()),
                () -> assertEquals(cause, exception.getCause())
        );
    }

    @DisplayName("testing null for 3 param constructor (nulls allowed")
    @Test
    public void testNullInThreeParamConstructor(){
        Throwable cause = new testException("test thrown exception");
        assertAll(
                () -> assertDoesNotThrow(
                        () -> (new BadAttributeException(
                                testMessage, testAttribute, null))),
                () -> assertDoesNotThrow(
                        () -> (new BadAttributeException(
                                testMessage, null, cause))),
                () -> assertDoesNotThrow(
                        () -> (new BadAttributeException(
                                null, testAttribute, cause))),
                () -> assertDoesNotThrow(
                        () -> (new BadAttributeException(
                                null, null, null)))
        );
    }

    @DisplayName("testing null for 2 param constructor (nulls allowed")
    @Test
    public void testNullInTwoParamConstructor(){
        assertAll(
            () -> assertDoesNotThrow(
                    () -> new BadAttributeException(testMessage, null)),
            () -> assertDoesNotThrow(
                    () -> new BadAttributeException(null, testAttribute)),
            () -> assertDoesNotThrow(
                    () -> new BadAttributeException(null, null))
        );
    }
}
