package clickadot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class GameComponentActivePropertyTest {

    @Test
    void startAndStopGamePublishActiveStateChanges() {
        GameComponent component = new GameComponent();
        List<Boolean> activeEvents = new ArrayList<>();

        component.addPropertyChangeListener(
                GameComponent.ACTIVE_PROPERTY,
                event -> activeEvents.add((Boolean) event.getNewValue()));

        component.startGame();
        component.stopGame();

        assertEquals(List.of(true, false), activeEvents);
    }
}
