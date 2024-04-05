package Main.Display;

import Main.Database.DB;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

public class SuperAdminInterface {
    private final DB db;
    private final ArrayList<RoundRectangle2D.Float> userBorders;

    public SuperAdminInterface(DB db) {
        this.db = db;
        userBorders = new ArrayList<>();
    }

    private void drawTotalUsers(int xPos, int yPos, Graphics2D g2) {
        try {
            File[] files = db.getCurrentUser().getParentFile().getParentFile().listFiles();
            if (files != null) {
                String delimiter = "                  ";
                for (int i = 0; i < files.length; i++) {
                    if (!files[i].getName().equals(db.getCurrentUser().getParentFile().getName())) {
                        BufferedReader reader = new BufferedReader(new FileReader(
                                Objects.requireNonNull(files[i].listFiles())[1]));
                        String[] users = reader.readLine().split(db.getDelimiter());
                        String w = users[0] + delimiter + users[1] + delimiter + users[2] + delimiter + users[4];
                        reader.close();


                        userBorders.add(new RoundRectangle2D.Float(xPos, yPos, g2.getFontMetrics().stringWidth(w) + 30,
                                50, 20, 20));
                        g2.setColor(new Color(0x284138));
                        g2.fill(userBorders.get(i));
                        g2.setStroke(new BasicStroke(3));
                        g2.setColor(new Color(0xFF9900));
                        g2.draw(userBorders.get(i));

                        g2.setColor(Color.WHITE);
                        g2.drawString(w, 65,
                                yPos + 50 / 2 + g2.getFontMetrics().getHeight() / 3);

                        yPos = userBorders.get(i).getBounds().y + 65;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void drawInterface(Graphics2D g2) {
        int x = 20, y = 70;

        g2.setFont(new Font("Callibre", Font.PLAIN, 15));

        RoundRectangle2D border = new RoundRectangle2D.Float(x, y,
                MainFrame.SCREEN_SIZE.width - 60, MainFrame.SCREEN_SIZE.height - 130, 60, 60);
        g2.setColor(new Color(0xFF4F5054, true));
        g2.fill(border);
        g2.setStroke(new BasicStroke(6));
        g2.setColor(new Color(0x284138));
        g2.draw(border);

        drawTotalUsers((int) (x + border.getArcWidth() / 2), y + 40, g2);

        g2.setStroke(new BasicStroke(4));
        if (!userBorders.isEmpty()) {
            border = new RoundRectangle2D.Float(710, userBorders.getFirst().getBounds().y,
                    border.getBounds().width / 2f - 60,
                    border.getBounds().height - (userBorders.getFirst().x * 2) + 20, 50, 50);
            g2.setColor(new Color(0x284138));
            g2.fill(border);
            g2.setColor(Color.black);
            g2.draw(border);
        }
    }
}
