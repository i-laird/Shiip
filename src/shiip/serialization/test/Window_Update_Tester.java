package shiip.serialization.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import shiip.serialization.BadAttributeException;
import shiip.serialization.Window_Update;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Performs testing for the {@link shiip.serialization.Window_Update}.
 *
 * @version 1.0
 * @author Ian Laird, Andrew Walker
 */
@DisplayName("Window_Update Tester")
public class Window_Update_Tester extends MessageTester{
    static final int WINDOW_UPDATE_TYPE = 0x8;

    @DisplayName("Testing increment for valid numbers")
    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = {1, 1000, 128384, Integer.MAX_VALUE})
    public void testSetIncrement(int increment){
        assertAll(
                () -> assertDoesNotThrow(
                        () -> new Window_Update(1, increment)),
                () -> assertDoesNotThrow(
                        () -> {
                    Window_Update window_update =
                            new Window_Update(1,1);
                    window_update.setIncrement(increment);
                })
        );
    }

    @DisplayName("Testing increment for invalid numbers")
    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = {0, -1, -10203, Integer.MIN_VALUE})
    public void testSetIncrementBadValues(int increment){
        Window_Update goodWindow = null;
        try {
            goodWindow = new Window_Update(1, 1);
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
        final Window_Update finalGoodWindow = goodWindow;
        assertAll(
                () -> assertThrows(BadAttributeException.class,
                        () -> new Window_Update(1, increment)),
                () -> assertThrows(BadAttributeException.class,
                        () -> finalGoodWindow.setIncrement(increment))
        );
    }

    @DisplayName("Testing toString")
    @ParameterizedTest(name = "stream id: {0} and increment: {1}")
    @CsvSource(value = {"5;1;Window_Update: StreamID=5 increment=1",
                        "3;2;Window_Update: StreamID=3 increment=2",
                        "10000;10000;Window_Update: StreamID=10000" +
                                " increment=10000"}, delimiter = ';')
    public void testToString
            (int streamID, int increment, String expectedString ){
        try {
            Window_Update window_update = new Window_Update(streamID, increment);
            assertEquals(expectedString, window_update.toString());

        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }

    @DisplayName("Testing Window Update type")
    public void testType(){
        try {
            Window_Update window_update = new Window_Update(1, 1);
            assertEquals(window_update.getCode(), WINDOW_UPDATE_TYPE);
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }

    @DisplayName("Testing equal when streamId and increment are both equal")
    @Test
    public void testAreEqual(){
        try {
            Window_Update window_update_one =
                    new Window_Update(1, 1);
            Window_Update window_update_two =
                    new Window_Update(1, 1);
            assertEquals(window_update_one, window_update_two);
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }

    @DisplayName("Testing equal when streamId are same but not increment")
    @Test
    public void testNotEqualIncrement(){
        try {
            Window_Update window_update_one =
                    new Window_Update(1, 2);
            Window_Update window_update_two =
                    new Window_Update(1, 1);
            assertNotEquals(window_update_one, window_update_two);
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }

    @DisplayName("Testing equal when streamId are different but increment is the same")
    @Test
    public void testNotEqualStreamId(){
        try {
            Window_Update window_update_one =
                    new Window_Update(1, 1);
            Window_Update window_update_two =
                    new Window_Update(2, 1);
            assertNotEquals(window_update_one, window_update_two);
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }

    @DisplayName("Testing equal when streamId and increment are different")
    @Test
    public void testNotEqual(){
        try {
            Window_Update window_update_one =
                    new Window_Update(1, 2);
            Window_Update window_update_two =
                    new Window_Update(2, 1);
            assertNotEquals(window_update_one, window_update_two);
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }

    @DisplayName("Testing hashcode being equal when the window_update are equal")
    @Test
    public void testHashcodeEqual(){
        try {
            Window_Update window_update_one =
                    new Window_Update(1, 1);
            Window_Update window_update_two =
                    new Window_Update(1, 1);
            assertEquals(window_update_one.hashCode(),
                    window_update_two.hashCode());
        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }
}
