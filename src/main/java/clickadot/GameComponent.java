package clickadot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Swing game board for Click-a-Dot.
 *
 * <p>Timing model:
 * <ul>
 * <li>The timer uses {@code setInitialDelay(0)} so a target appears immediately after start.</li>
 * <li>The timer uses coalescing to avoid backlog bursts after EDT stalls.</li>
 * <li>Changing target duration updates subsequent timer firings only.</li>
 * </ul>
 */
public class GameComponent extends JPanel implements MouseListener {
    private static final long serialVersionUID = 1L;

    /** Property name fired when score changes. */
    public static final String SCORE_PROPERTY = "GameScore";
    /** Property name fired when active state changes. */
    public static final String ACTIVE_PROPERTY = "GameActive";

    private static final int MAX_TARGETS_PER_ROUND = 10;
    private static final int DEFAULT_TARGET_MILLIS = 1500;
    private static final int DEFAULT_TARGET_RADIUS = 15;
    private static final int PREF_WIDTH = 480;
    private static final int PREF_HEIGHT = 360;

    /** Duration each target remains visible in milliseconds. */
    private int targetTimeMillis = DEFAULT_TARGET_MILLIS;

    /** Current target state. */
    private final Target target = new Target(DEFAULT_TARGET_RADIUS);

    /** Timer that advances target lifecycle. */
    private final Timer timer;

    /** Whether a game is currently active. */
    private boolean isActive;

    /** Number of targets shown in current (or most recent) game. */
    private int targetCount;

    /** Number of successful hits in current (or most recent) game. */
    private int score;

    /** Creates a game board with default settings. */
    public GameComponent() {
        timer = new Timer(targetTimeMillis, e -> timeout());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        addMouseListener(this);
        setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
    }

    /**
     * Starts a new game using current settings, clearing in-progress or previous score/target count.
     */
    public void startGame() {
        targetCount = 0;
        setScore(0);
        setActive(true);
        timer.restart();
        repaint();
    }

    /** Stops the current game and cancels active timer callbacks. */
    public void stopGame() {
        timer.stop();
        setActive(false);
        repaint();
    }

    /** Sets active state and emits a property change event when it changes. */
    private void setActive(boolean newActive) {
        if (isActive == newActive) {
            return;
        }
        boolean oldActive = isActive;
        isActive = newActive;
        firePropertyChange(ACTIVE_PROPERTY, oldActive, newActive);
    }

    /** Advances one target interval if the game is active. */
    private void timeout() {
        if (!isActive) {
            return;
        }

        // Do not consume targets while the board has no drawable area.
        // This avoids rounds ending invisibly (for example during layout/minimize).
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        if (targetCount >= MAX_TARGETS_PER_ROUND) {
            stopGame();
            return;
        }

        target.respawn(width, height);
        targetCount++;
        repaint();
    }

    /** Returns the current score. */
    public int getScore() {
        return score;
    }

    /** Sets score and emits a property change event. */
    private void setScore(int newScore) {
        if (score == newScore) {
            return;
        }
        int oldScore = score;
        score = newScore;
        firePropertyChange(SCORE_PROPERTY, oldScore, newScore);
    }

    /** Returns the target radius in pixels. */
    public int getTargetRadius() {
        return target.radius;
    }

    /**
     * Updates target radius in pixels.
     *
     * @param r new radius; must be {@code > 0}
     */
    public void setTargetRadius(int r) {
        if (r <= 0) {
            throw new IllegalArgumentException("radius must be > 0");
        }
        target.radius = r;
        repaint();
    }

    /** Returns the target duration in milliseconds. */
    public int getTargetTimeMillis() {
        return targetTimeMillis;
    }

    /**
     * Updates the target interval duration for future timer firings.
     *
     * @param t delay in milliseconds; must be {@code >= 0}
     */
    public void setTargetTimeMillis(int t) {
        if (t < 0) {
            throw new IllegalArgumentException("target time must be >= 0");
        }
        targetTimeMillis = t;
        timer.setDelay(targetTimeMillis);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isActive) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        target.paintDot(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isActive && target.checkHit(e.getX(), e.getY())) {
            setScore(score + 1);
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /** Mutable target dot state and hit/position logic. */
    static final class Target {
        int x;
        int y;
        int radius;
        private boolean isHit;
        private final Random rng = new Random();

        Target(int radius) {
            this.radius = radius;
        }

        void paintDot(Graphics g) {
            g.setColor(isHit ? Color.RED : Color.BLUE);
            g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
        }

        /**
         * Clips a coordinate into {@code [radius, max - radius]} when that range exists.
         * If target diameter exceeds bounds, returns the original coordinate.
         */
        int clip(int coord, int max) {
            if (2 * radius > max) {
                return coord;
            }
            if (coord < radius) {
                return radius;
            }
            if (coord > max - radius) {
                return max - radius;
            }
            return coord;
        }

        /**
         * Moves to a new random location within current bounds and clears hit state.
         *
         * @param xMax inclusive maximum x bound
         * @param yMax inclusive maximum y bound
         */
        void respawn(int xMax, int yMax) {
            x = clip(rng.nextInt(xMax + 1), xMax);
            y = clip(rng.nextInt(yMax + 1), yMax);
            isHit = false;
        }

        /**
         * Marks and returns a successful hit exactly once per spawn.
         *
         * @return true if point is inside the circular target and target was not already hit
         */
        boolean checkHit(int cx, int cy) {
            if (isHit) {
                return false;
            }

            long dx = (long) cx - x;
            long dy = (long) cy - y;
            long distanceSquared = (dx * dx) + (dy * dy);
            long radiusSquared = (long) radius * radius;
            boolean inside = distanceSquared <= radiusSquared;
            if (inside) {
                isHit = true;
                return true;
            }
            return false;
        }
    }
}
