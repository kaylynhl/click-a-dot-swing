package clickadot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameComponentTargetTest {

    @Test
    void checkHitReturnsTrueOnceForPointInsideCircle() {
        GameComponent.Target target = new GameComponent.Target(10);
        target.x = 100;
        target.y = 120;

        assertTrue(target.checkHit(105, 125));
        assertFalse(target.checkHit(105, 125));
    }

    @Test
    void clipRestrictsCoordinateToVisibleRangeWhenPossible() {
        GameComponent.Target target = new GameComponent.Target(10);

        assertEquals(10, target.clip(-3, 100));
        assertEquals(90, target.clip(93, 100));
        assertEquals(55, target.clip(55, 100));
    }

    @Test
    void clipReturnsCoordinateUnchangedWhenTargetCannotFitInBounds() {
        GameComponent.Target target = new GameComponent.Target(60);

        assertEquals(8, target.clip(8, 100));
    }
}
