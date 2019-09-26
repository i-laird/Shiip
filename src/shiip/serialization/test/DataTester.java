package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import shiip.serialization.BadAttributeException;
import shiip.serialization.Data;
import static shiip.serialization.test.TestingConstants.DATA_TYPE;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Data class
 */
public class DataTester {

    /**
     * Tests the constructor of Data
     */
    @Nested
    @DisplayName("constructor")
    public class Constructor{

        /**
         * Tests constructor with invalid streamID
         */
        @Test
        @DisplayName("Invalid streamID")
        public void testConstructorInvalidStreamID() {
            BadAttributeException ex = assertThrows(BadAttributeException.class, () -> {
                new Data(0, false, new byte[]{});
            });
            assertEquals(ex.getAttribute(), "streamID");
        }

        /**
         * Tests the constructor with null data
         */
        @Test
        @DisplayName("Invalid data")
        public void testConstructorInvalidData() {
            BadAttributeException ex = assertThrows(BadAttributeException.class, () -> {
                new Data(1, false, null);
            });
            assertEquals(ex.getAttribute(), "data");
        }

        /**
         * Tests the constructor with valid streamID's
         * @param streamID valid streamID
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {/*-1,*/ 1, 10, 20})
        @DisplayName("Valid streamID")
        public void testConstructorStreamID(int streamID){
            assertDoesNotThrow(() -> {
                Data data = new Data(streamID, false, new byte[]{});
                assertEquals(streamID, data.getStreamID());
            });
        }

        /**
         * Tests the constructor with all possible isEnd values
         * @param isEnd valid isEnd
         */
        @ParameterizedTest(name = "isEnd = {0}")
        @CsvSource({"true", "false"})
        @DisplayName("Valid isEnd")
        public void testConstructorIsEnd(boolean isEnd){
            assertDoesNotThrow(() -> {
                Data data = new Data(1, isEnd, new byte[]{});
                assertEquals(isEnd, data.isEnd());
            });
        }

        /**
         * Tests constructor with a full byte array
         */
        @Test
        @DisplayName("Valid data full")
        public void testConstructorValidDataFull() {
            byte[] byteData = new byte[]{0,1,2,3,4,5,6,7};

            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, byteData);
                assertArrayEquals(byteData, data.getData());
            });
        }

        /**
         * Tests constructor with an empty byte array
         */
        @Test
        @DisplayName("Valid data empty")
        public void testConstructorValidDataEmpty() {
            byte[] byteData = new byte[]{};

            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, byteData);
                assertArrayEquals(byteData, data.getData());
            });
        }

        /**
         * Tests code is the correct one for Data
         */
        @Test
        @DisplayName("code")
        public void testConstructorCode() {
            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, new byte[]{});
                assertEquals(DATA_TYPE, data.getCode());
            });
        }
    }

    /**
     * Tests the setData method of Data
     */
    @Nested
    @DisplayName("setData")
    public class SetData{

        /**
         * Tests that BadAttributeException is thrown when data is null
         */
        @Test
        @DisplayName("null")
        public void testSetDataInvalidData() {
            BadAttributeException ex = assertThrows(BadAttributeException.class, () -> {
                Data data = new Data(1, false, new byte[]{});
                data.setData(null);
            });
            assertEquals(ex.getAttribute(), "data");
        }

        /**
         * Tests valid empty array data
         */
        @Test
        @DisplayName("Valid data empty")
        public void testSetDataValidDataEmpty() {
            byte[] byteData = new byte[]{};

            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, new byte[]{});
                data.setData(byteData);
                assertArrayEquals(byteData, data.getData());
            });
        }

        /**
         * Tests valid full array data
         */
        @Test
        @DisplayName("Valid data full")
        public void testSetDataValidDataFull() {
            byte[] byteData = new byte[]{0,1,2,3,4,5,6,7};

            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, new byte[]{});
                data.setData(byteData);
                assertArrayEquals(byteData, data.getData());
            });
        }
    }

    /**
     * Test the setEnd method of Data
     */
    @Nested
    @DisplayName("setEnd")
    public class SetEnd{

        /**
         * Tests will all possible isEnd values
         * @param isEnd valid isEnd
         */
        @ParameterizedTest(name = "isEnd = {0}")
        @CsvSource({"true", "false"})
        @DisplayName("Valid")
        public void testSetEndIsEnd(boolean isEnd){
            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, new byte[]{});
                data.setEnd(isEnd);
                assertEquals(isEnd, data.isEnd());
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
         */
        @Test
        @DisplayName("Invalid")
        public void testConstructorInvalidData() {
            BadAttributeException ex = assertThrows(BadAttributeException.class, () -> {
                Data data = new Data(1, false, new byte[]{});
                data.setStreamID(0);
            });
            assertEquals(ex.getAttribute(), "streamID");
        }

        /**
         * Tests valid streamID's
         * @param streamID valid streamID
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {/*-1,*/ 1, 10, 20})
        @DisplayName("Valid")
        public void testConstructorStreamID(int streamID){
            assertDoesNotThrow(() -> {
                Data data = new Data(1, false, new byte[]{});
                data.setStreamID(streamID);
                assertEquals(streamID, data.getStreamID());
            });
        }
    }

    /**
     * Tests the equals method of Data
     */
    @Nested
    @DisplayName("equals")
    public class Equals{

        /**
         * Tests that Data with different streamID are unequal
         * @param streamID valid streamID
         * @throws BadAttributeException if invalid parameter
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {/*-1,*/ 10, 20})
        @DisplayName("Mismatched streamID")
        public void testEqualsInvalidStreamID(int streamID) throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{});
            assertNotEquals(new Data(streamID, false, new byte[]{}), data);
        }

        /**
         * Tests that Data with different isEnd are unequal
         * @param isEnd valid isEnd
         * @throws BadAttributeException if invalid parameter
         */
        @ParameterizedTest(name = "isEnd = {0}")
        @CsvSource({"true", "false"})
        @DisplayName("Mismatched isEnd")
        public void testEqualsInvalidIsEnd(boolean isEnd) throws BadAttributeException {
            Data data = new Data(1, !isEnd, new byte[]{});
            assertNotEquals(new Data(1, isEnd, new byte[]{}), data);
        }

        /**
         * Tests that Data with different data full are unequal
         * @throws BadAttributeException if invalid parameter
         */
        @Test
        @DisplayName("Mismatched data full")
        public void testEqualsInvalidDataFull() throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{1,2,3});
            assertNotEquals(new Data(1, false, new byte[]{3,2,1}), data);
        }

        /**
         * Tests that Data with different data empty are unequal
         * @throws BadAttributeException if invalid parameter
         */
        @Test
        @DisplayName("Mismatched data empty")
        public void testEqualsInvalidDataEmpty() throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{});
            assertNotEquals(new Data(1, false, new byte[]{1}), data);
        }

        /**
         * Tests Data with same valid streamID's are equal
         * @param streamID valid streamID
         * @throws BadAttributeException if invalid parameter
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {/*-1,*/ 1, 10, 20})
        @DisplayName("Matched streamID")
        public void testEqualsValidStreamID(int streamID) throws BadAttributeException {
            Data data = new Data(streamID, false, new byte[]{});
            assertEquals(new Data(streamID, false, new byte[]{}), data);
        }

        /**
         * Tests Data with same valid isEnd are equal
         * @param isEnd valid isEnd
         * @throws BadAttributeException if invalid parameter
         */
        @ParameterizedTest(name = "isEnd = {0}")
        @CsvSource({"true", "false"})
        @DisplayName("Matched isEnd")
        public void testEqualsValidIsEnd(boolean isEnd) throws BadAttributeException {
            Data data = new Data(1, isEnd, new byte[]{});
            assertEquals(new Data(1, isEnd, new byte[]{}), data);
        }

        /**
         * Tests Data with same full data are equal
         * @throws BadAttributeException if invalid parameter
         */
        @Test
        @DisplayName("Matched data full")
        public void testEqualsValidDataFull() throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{1,2,3});
            assertEquals(new Data(1, false, new byte[]{1,2,3}), data);
        }

        /**
         * Tests Data with same empty data are equal
         * @throws BadAttributeException if invalid parameter
         */
        @Test
        @DisplayName("Matched data empty")
        public void testEqualsValidDataEmpty() throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{});
            assertEquals(new Data(1, false, new byte[]{}), data);
        }
    }

    /**
     * Tests the hashcode method of Data
     */
    @Nested
    @DisplayName("hashcode")
    public class Hashcode{

        /**
         * Tests Data with same streamID have same hashcode
         * @param streamID valid streamID
         * @throws BadAttributeException if invalid parameter
         */
        @ParameterizedTest(name = "streamID = {0}")
        @ValueSource(ints = {/*-1,*/ 1, 10, 20})
        @DisplayName("Matched streamID")
        public void testHashcodeValidStreamID(int streamID) throws BadAttributeException {
            Data data = new Data(streamID, false, new byte[]{});
            assertEquals(new Data(streamID, false, new byte[]{}).hashCode(), data.hashCode());
        }

        /**
         * Tests Data with same isEnd have same hashcode
         * @param isEnd valid isEnd
         * @throws BadAttributeException if invalid parameter
         */
        @ParameterizedTest(name = "isEnd = {0}")
        @CsvSource({"true", "false"})
        @DisplayName("Matched isEnd")
        public void testHashcodeValidIsEnd(boolean isEnd) throws BadAttributeException {
            Data data = new Data(1, isEnd, new byte[]{});
            assertEquals(new Data(1, isEnd, new byte[]{}).hashCode(), data.hashCode());
        }

        /**
         * Tests Data with same full data have same hashcode
         * @throws BadAttributeException if invalid parameter
         */
        @Test
        @DisplayName("Matched data full")
        public void testHashcodeValidDataFull() throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{1,2,3});
            assertEquals(new Data(1, false, new byte[]{1,2,3}).hashCode(), data.hashCode());
        }

        /**
         * Tests Data with same empty data have same hashcode
         * @throws BadAttributeException if invalid parameter
         */
        @Test
        @DisplayName("Matched data empty")
        public void testHashcodeValidDataEmpty() throws BadAttributeException {
            Data data = new Data(1, false, new byte[]{});
            assertEquals(new Data(1, false, new byte[]{}).hashCode(), data.hashCode());
        }
    }

    /**
     * Tests valid toString
     */
    @ParameterizedTest(name = "streamID = {0}, isEnd = {1}, data = {2}")
    @MethodSource("provideDataParametersValid")
    @DisplayName("Valid")
    public void testToStringValid(int streamID, boolean isEnd, byte[] data) {
        assertDoesNotThrow(() -> {
            Data dataObj = new Data(streamID, isEnd, data);
            String rep = String.format("Data: StreamID=%d isEnd=%b data=%d", streamID, isEnd, data.length);
            assertEquals(rep, dataObj.toString());
        });
    }

    private static Stream<Arguments> provideDataParametersValid() {
        return Stream.of(
           // Arguments.of(-1, true, new byte[]{}),
           // Arguments.of(-1, true, new byte[]{1,2,3}),
           // Arguments.of(-1, false, new byte[]{}),
           // Arguments.of(-1, false, new byte[]{1,2,3}),
            Arguments.of(1, true, new byte[]{}),
            Arguments.of(1, true, new byte[]{1,2,3}),
            Arguments.of(1, false, new byte[]{}),
            Arguments.of(1, false, new byte[]{1,2,3}),
            Arguments.of(10, true, new byte[]{}),
            Arguments.of(10, true, new byte[]{1,2,3}),
            Arguments.of(10, false, new byte[]{}),
            Arguments.of(10, false, new byte[]{1,2,3}),
            Arguments.of(20, true, new byte[]{}),
            Arguments.of(20, true, new byte[]{1,2,3}),
            Arguments.of(20, false, new byte[]{}),
            Arguments.of(20, false, new byte[]{1,2,3})
        );
    }



}
