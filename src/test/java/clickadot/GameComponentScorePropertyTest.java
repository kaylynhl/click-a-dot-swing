package clickadot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import org.junit.jupiter.api.Test;

class GameComponentScorePropertyTest {

    @Test
    void scorePropertyFiresOnSuccessfulHit() throws ReflectiveOperationException {
        GameComponent component = new GameComponent();
        component.setSize(400, 300);
        component.startGame();
        Field timerField = GameComponent.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        ((Timer) timerField.get(component)).stop();

        Field targetField = GameComponent.class.getDeclaredField("target");
        targetField.setAccessible(true);
        Object target = targetField.get(component);

        Field xField = target.getClass().getDeclaredField("x");
        Field yField = target.getClass().getDeclaredField("y");
        xField.setAccessible(true);
        yField.setAccessible(true);
        xField.setInt(target, 50);
        yField.setInt(target, 70);

        List<Integer> newScores = new ArrayList<>();
        component.addPropertyChangeListener(GameComponent.SCORE_PROPERTY,
                event -> newScores.add((Integer) event.getNewValue()));

        MouseEvent hit = new MouseEvent(component, MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(), 0, 50, 70, 1, false);
        component.mousePressed(hit);

        assertEquals(1, component.getScore());
        assertEquals(List.of(1), newScores);

        component.stopGame();
    }
}
