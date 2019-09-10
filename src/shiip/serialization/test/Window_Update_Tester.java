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
 * @author Ian Laird
 */
@DisplayName("Window_Update Tester")
public class Window_Update_Tester {
    public static final int WINDOW_UPDATE_TYPE = 0x8;

    @DisplayName("Testing making increment the number: {0}")
    @ParameterizedTest
    @ValueSource(ints = {1, 1000, 128384, Integer.MAX_VALUE})
    public void testSetIncrement(int increment){
        assertAll(
                () -> assertDoesNotThrow(() -> new Window_Update(1, increment)),
                () -> assertDoesNotThrow(() -> {
                    Window_Update window_update = new Window_Update(1,1);
                    window_update.setIncrement(increment);
                })
        );
    }

    @DisplayName("Testing making increment the number: {0}")
    @ParameterizedTest
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
                () -> assertThrows(BadAttributeException.class, () -> new Window_Update(1, increment)),
                () -> assertThrows(BadAttributeException.class, () -> finalGoodWindow.setIncrement(increment))
        );
    }

    @DisplayName("Testing toString for stream id: {0} and increment: {1}")
    @ParameterizedTest
    @CsvSource(value = {"5;1;Window_Update: StreamID=5 increment=1",
                        "3;2;Window_Update: StreamID=3 increment=2",
                        "10000;10000;WindowUpdate: StreamID=10000" +
                                " increment=10000"}, delimiter = ';')
    public void testToString(int streamID, int increment, String expectedString ){
        try {
            Window_Update window_update = new Window_Update(streamID, increment);
            assertEquals(expectedString, window_update.toString());

        }catch(BadAttributeException e){
            fail(e.getMessage());
        }
    }
}
