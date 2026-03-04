package clickadot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

class GameComponentTimeoutTest {

    @Test
    void timeoutDoesNotAdvanceRoundWhenBoardHasNoSize() throws ReflectiveOperationException {
        GameComponent component = new GameComponent();
        component.setSize(0, 0);
        component.startGame();

        Method timeout = GameComponent.class.getDeclaredMethod("timeout");
        timeout.setAccessible(true);
        timeout.invoke(component);

        Field targetCount = GameComponent.class.getDeclaredField("targetCount");
        targetCount.setAccessible(true);
        assertEquals(0, targetCount.getInt(component));

        component.stopGame();
    }
}
