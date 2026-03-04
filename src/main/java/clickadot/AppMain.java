package clickadot;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/** Entry point and UI wiring for the Click-a-Dot application. */
public final class AppMain {

    private static final String WINDOW_TITLE = "Click-a-Dot";
    private static final String SCORE_PREFIX = "Score: ";
    private static final String START_TEXT = "Start";
    private static final String RESTART_TEXT = "Restart";
    private static final String PLAY_AGAIN_TEXT = "Play Again";

    private static final int SCORE_FONT_SIZE = 24;
    private static final int BUTTON_FONT_SIZE = 20;
    private static final int PANEL_TITLE_FONT_SIZE = 16;

    private static final int MIN_RADIUS = 1;
    private static final int MAX_RADIUS = 50;
    private static final int MIN_SPEED_MS = 250;
    private static final int MAX_SPEED_MS = 2000;

    private AppMain() {
    }

    /** Starts the Swing application on the EDT. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppMain::createAndShowGui);
    }

    /** Creates the game window and connects UI controls to game state. */
    static void createAndShowGui() {
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GameComponent game = new GameComponent();
        frame.add(game);

        JLabel scoreLabel = new JLabel(SCORE_PREFIX + game.getScore(), SwingConstants.CENTER);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont((float) SCORE_FONT_SIZE));
        frame.add(scoreLabel, BorderLayout.NORTH);

        JButton startButton = new JButton(START_TEXT);
        startButton.setFont(startButton.getFont().deriveFont((float) BUTTON_FONT_SIZE));
        frame.add(startButton, BorderLayout.SOUTH);

        JSlider sizeSlider = new JSlider(JSlider.VERTICAL, MIN_RADIUS, MAX_RADIUS, game.getTargetRadius());
        addSliderLabels(sizeSlider, "Small", "Large");
        frame.add(makeSliderPanel(sizeSlider, "Size"), BorderLayout.WEST);

        JSlider speedSlider = new JSlider(
                JSlider.VERTICAL,
                MIN_SPEED_MS,
                MAX_SPEED_MS,
                game.getTargetTimeMillis());
        addSliderLabels(speedSlider, "Fast", "Slow");
        speedSlider.setInverted(true);
        frame.add(makeSliderPanel(speedSlider, "Speed"), BorderLayout.EAST);

        JMenuItem saveItem = new JMenuItem("Save score");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        startButton.addActionListener((ActionEvent e) -> game.startGame());
        game.addPropertyChangeListener(
                GameComponent.SCORE_PROPERTY,
                event -> scoreLabel.setText(SCORE_PREFIX + event.getNewValue()));
        game.addPropertyChangeListener(
                GameComponent.ACTIVE_PROPERTY,
                event -> startButton.setText((Boolean) event.getNewValue() ? RESTART_TEXT : PLAY_AGAIN_TEXT));

        sizeSlider.addChangeListener(event -> game.setTargetRadius(sizeSlider.getValue()));
        speedSlider.addChangeListener(event -> game.setTargetTimeMillis(speedSlider.getValue()));

        saveItem.addActionListener((ActionEvent e) -> saveScore(frame, game.getScore()));
        exitItem.addActionListener((ActionEvent e) -> frame.dispose());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                game.stopGame();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private static void addSliderLabels(JSlider slider, String minLabel, String maxLabel) {
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(slider.getMinimum(), new JLabel(minLabel));
        labels.put(slider.getMaximum(), new JLabel(maxLabel));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
    }

    private static JComponent makeSliderPanel(JSlider slider, String title) {
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont((float) PANEL_TITLE_FONT_SIZE));
        sliderPanel.add(slider);
        sliderPanel.add(titleLabel, BorderLayout.NORTH);
        return sliderPanel;
    }

    private static void saveScore(JFrame frame, int score) {
        final JFileChooser chooser = new JFileChooser();
        int returnValue = chooser.showSaveDialog(frame);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            out.println(score);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    e.getClass().getName() + ": " + e.getLocalizedMessage(),
                    e.getClass().getName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
