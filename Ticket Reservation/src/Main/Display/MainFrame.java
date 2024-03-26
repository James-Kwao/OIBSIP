package Main.Display;

import javax.swing.*;
import java.awt.*;

public class MainFrame {

    public static Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    MainFrame() {
        JFrame frame = new JFrame("Hello");
        Display display = new Display();
        frame.setLocation(0, 0);
        frame.setPreferredSize(SCREEN_SIZE);
        frame.add(display);
        frame.setResizable(false);
        frame.requestFocus();
        frame.setAutoRequestFocus(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();

        Timer timer = new Timer(0, e -> {
            display.repaint();
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
