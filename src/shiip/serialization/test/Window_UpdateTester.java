/*******************************************************
 * Author: Ian Laird, Andrew walker
 * Assignment: Prog 1
 * Class: Data Comm
 *******************************************************/

package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import shiip.serialization.BadAttributeException;
import shiip.serialization.Window_Update;

import static org.junit.jupiter.api.Assertions.*;
import static shiip.serialization.test.TestingConstants.LARGEST_INT;
import static shiip.serialization.test.TestingConstants.WINDOW_UPDATE_TYPE;


/**
 * Performs testing for the {@link Window_Update}.
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
public class Window_UpdateTester {

    @Nested
    @DisplayName("constructor")
    public class Constructor {
        /**
         * good vals
         *
         * @param increment increment
         */
        @DisplayName("Valid increment")
        @ParameterizedTest(name = "increment = {0}")
        @ValueSource(ints = {1, 1000, 128384, LARGEST_INT})
        public void testConstructorIncrement(int increment){
            assertDoesNotThrow( () -> {
                new Window_Update(1, increment);
            });
        }

        /**
         * good vals
         *
         * @param streamID streamID
         */
        @DisplayName("Valid streamID")
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {0, 1, 1000, 128384, LARGEST_INT})
        public void testConstructorStreamID(int streamID){
            assertDoesNotThrow( () -> {
                new Window_Update(streamID, 1);
            });
        }

        /**
         * bad vals
         *
         * @param streamID streamID
         */
        @DisplayName("Invalid streamID")
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {-1, -10203, Integer.MIN_VALUE})
        public void testConstructorStreamIDBadValues(int streamID){
            assertThrows(BadAttributeException.class, () -> {
                new Window_Update(streamID, 1);
            });
        }

        /**
         * bad vals
         *
         * @param increment increment
         */
        @DisplayName("Invalid increment")
        @ParameterizedTest(name = "increment = {0}")
        @ValueSource(ints = {0, -1, -10203, Integer.MIN_VALUE})
        public void testSetIncrementBadValues(int increment){
            assertThrows(BadAttributeException.class, () -> {
                new Window_Update(1, increment);
            });
        }

        /**
         * Tests code is the correct one for Window_Update
         */
        @Test
        @DisplayName("code")
        public void testConstructorCode() {
            assertDoesNotThrow(() -> {
                Window_Update wu = new Window_Update(1,1);
                assertEquals(WINDOW_UPDATE_TYPE, wu.getCode());
            });
        }
    }

    @Nested
    @DisplayName("setIncrement")
    public class SetIncrement {
        /**
         * good vals
         *
         * @param increment increment
         */
        @DisplayName("Valid")
        @ParameterizedTest(name = "increment = {0}")
        @ValueSource(ints = {1, 1000, 128384, Integer.MAX_VALUE})
        public void testSetIncrement(int increment){
            assertDoesNotThrow( () -> {
                Window_Update wu = new Window_Update(1,1);
                wu.setIncrement(increment);
            });

        }

        /**
         * bad vals
         *
         * @param increment increment
         */
        @DisplayName("Invalid")
        @ParameterizedTest(name = "increment = {0}")
        @ValueSource(ints = {0, -1, -10203, Integer.MIN_VALUE})
        public void testSetIncrementBadValues(int increment){
            assertThrows(BadAttributeException.class, () -> {
                Window_Update wu = new Window_Update(1, 1);
                wu.setIncrement(increment);
            });
        }
    }

    /**
     * Tests the setStreamID method of Data
     */
    @Nested
    @DisplayName("setStreamID")
    public class SetStreamID{

        /**
         * Tests that BadAttributeException is thrown on invalid streamID
         * @param streamID the streamID
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {-10, -1})
        @DisplayName("Invalid")
        public void testInvalidSetStreamID(int streamID) {
            assertThrows(BadAttributeException.class, () -> {
                Window_Update wu = new Window_Update(1, 1);
                wu.setStreamID(streamID);
            });
        }

        /**
         * Tests valid streamID's
         * @param streamID valid streamID
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = { 1, 10, 20})
        @DisplayName("Valid")
        public void testValidSetStreamID(int streamID){
            assertDoesNotThrow(() -> {
                Window_Update wu = new Window_Update(1, 1);
                wu.setStreamID(streamID);
                assertEquals(streamID, wu.getStreamID());
            });
        }
    }

    /**
     * tests tostring
     * @param streamID the id of the stream
     * @param increment increment
     * @param ex expected string
     */
    @DisplayName("toString")
    @ParameterizedTest(name = "stream id: {0} and increment: {1}")
    @CsvSource(value = {"5;1;Window_Update: StreamID=5 increment=1",
            "3;2;Window_Update: StreamID=3 increment=2",
            "10000;10000;Window_Update: StreamID=10000" +
                    " increment=10000"}, delimiter = ';')
    public void testToString (int streamID, int increment, String ex ){
        assertDoesNotThrow(() -> {
            Window_Update wu = new Window_Update(streamID, increment);
            assertEquals(ex, wu.toString());
        });
    }

    @Nested
    @DisplayName("equals")
    public class Equals {
        /**
         * Equal streamID and increment
         */
        @DisplayName("Equal streamID & increment")
        @Test
        public void testAreEqual(){
            assertDoesNotThrow(() -> {
                Window_Update wu1 =  new Window_Update(1, 1);
                Window_Update wu2 = new Window_Update(1, 1);
                assertEquals(wu1, wu2);
            });
        }

        /**
         * Unequal increment
         */
        @DisplayName("Unequal increment")
        @Test
        public void testNotEqualIncrement(){
            assertDoesNotThrow(() -> {
                Window_Update wu1 = new Window_Update(1, 2);
                Window_Update wu2 = new Window_Update(1, 1);
                assertNotEquals(wu1, wu2);
            });
        }

        /**
         * Unequal streamID
         */
        @DisplayName("Unequal streamID")
        @Test
        public void testNotEqualStreamId(){
            assertDoesNotThrow(() -> {
                Window_Update wu1 = new Window_Update(1, 1);
                Window_Update wu2 = new Window_Update(2, 1);
                assertNotEquals(wu1, wu2);
            });
        }

        /**
         * Unequal streamID and increment
         */
        @DisplayName("Unequal streamID & increment")
        @Test
        public void testNotEqual(){
            assertDoesNotThrow(() -> {
                Window_Update wu1 = new Window_Update(1, 2);
                Window_Update wu2 = new Window_Update(2, 1);
                assertNotEquals(wu1, wu2);
            });
        }
    }

    @Nested
    @DisplayName("hashcode")
    public class Hashcode {

        /**
         * Hashcode is same when equal
         */
        @DisplayName("Equal")
        @Test
        public void testHashcodeEqual(){
            assertDoesNotThrow(() -> {
                Window_Update wu1 = new Window_Update(1, 1);
                Window_Update wu2 = new Window_Update(1, 1);
                assertEquals(wu1.hashCode(), wu2.hashCode());
            });
        }
    }
}
