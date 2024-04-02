package Main.Display;

import Main.Database.DB;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.ArrayList;

public class SuperAdminInterface {
    private final DB db;
    private ArrayList<RoundRectangle2D.Float> userBorders;

    public SuperAdminInterface(DB db) {
        this.db = db;
        userBorders = new ArrayList<>();
    }

    private int getTotalUsers() {
        int users = 0;
        try {
            File[] files = db.getCurrentUser().getParentFile().listFiles();
            if (files != null)
                for (File f : files)
                    if (f.isDirectory())
                        users++;
            return users;
        } catch (Exception ignored) {
        }
        return users;
    }

    public void drawInterface(Graphics2D g2) {
        Font font = new Font("Book Antiqua", Font.PLAIN, 20);
        g2.setFont(font);
        String info = "Total Users: ";
        int x = 20, y = 30;
        RoundRectangle2D border = new RoundRectangle2D.Float(x, y,
                MainFrame.SCREEN_SIZE.width - 60, MainFrame.SCREEN_SIZE.height - 90, 60, 60);
        g2.setColor(new Color(0xFF4F5054, true));
        g2.fill(border);
        g2.setStroke(new BasicStroke(6));
        g2.setColor(new Color(0x284138));
        g2.draw(border);
        System.out.println(getTotalUsers());
    }
}
