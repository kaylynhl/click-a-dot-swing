# Click-a-Dot

Click-a-Dot is a small Java Swing reflex game where you click moving targets before they disappear. This repository is the cleaned and documented public version of an older coursework project.

## Run

Prerequisites:
- Java 17+
- Internet access on first run (Gradle may download its wrapper and dependencies)

Commands:
- `./gradlew run` to launch the game
- `./gradlew test` to run tests

## How to Play

- Press **Start** to begin a round.
- Each round shows **10 targets** total, so the max score per round is **10**.
- Click each blue dot before the next one appears.
- A successful click turns the dot red and increments the score.
- While a round is active, the button shows **Restart**; after a round ends, it shows **Play Again**.
- Use the **Size** and **Speed** sliders to adjust difficulty.
- Use **File -> Save score** to append your score to a file.

## Technical Notes

- Uses `javax.swing.Timer` for interval-based target spawning.
- Uses event-driven UI wiring through Swing listeners.
- Uses property change events (`GameScore`) so UI labels observe score updates.

## Project History

Originally written for coursework; later refactored and documented for portfolio use.
