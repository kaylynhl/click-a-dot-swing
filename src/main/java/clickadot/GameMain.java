package clickadot;

/**
 * Backward-compatible entrypoint.
 *
 * <p>Use {@link AppMain} for the canonical application entrypoint.
 */
@Deprecated
public final class GameMain {
    private GameMain() {
    }

    public static void main(String[] args) {
        AppMain.main(args);
    }
}
