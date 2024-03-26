package Main.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class DateTimeInterface extends MouseAdapter {
    public final int h = 110;
    public int w;
    public RoundRectangle2D.Float mainContainer;
    private String text;
    private FontMetrics fm;

    public void drawDateInterface(Graphics2D g2, int x, int y, JTextField year, JTextField month, JTextField day) {
        w = 250;
        Font font = new Font("Bell MT", Font.BOLD, 30);
        setPropertiesForCalendar(year, month, day, font);

        text = "Select date";

        //Draw main container
        mainContainer = new RoundRectangle2D.Float(x, y, w, h, 20, 20);
        g2.setColor(new Color(0x35464B));
        g2.fill(mainContainer);
        g2.setColor(Color.WHITE);
        g2.draw(mainContainer);
        g2.setColor(Color.BLACK);
        font = font.deriveFont(Font.PLAIN, 15f);
        g2.setFont(font);
        g2.drawString(text, (int) (x + mainContainer.getArcWidth() / 2), (int) (y + mainContainer.getArcHeight()));

        //Draw day container
        fm = g2.getFontMetrics(font.deriveFont(Font.BOLD, 30f));
        int xPos, yPos;
        xPos = (int) (x + mainContainer.getArcWidth() / 2) + 15;
        yPos = (int) (mainContainer.getBounds().getCenterY() - fm.getHeight() / 2);
        day.setBounds(xPos, yPos, fm.stringWidth("00"), fm.getHeight());
        day.setVisible(true);

        g2.setStroke(new BasicStroke(4));
        RoundRectangle2D.Float dayContainer = new RoundRectangle2D.Float(day.getBounds().x - 5, day.getBounds().y - 5,
                day.getWidth() + 10, day.getHeight() + 10, 15, 15);
        g2.setColor(new Color(0x12A3D2));
        g2.fill(dayContainer);
        g2.setColor(new Color(0xFF9900));
        if (day.isFocusOwner())
            g2.draw(dayContainer);

        Ellipse2D dot = new Ellipse2D.Float(dayContainer.x + dayContainer.width + 15, dayContainer.y + 5, 10, 10);
        g2.fill(dot);
        dot = new Ellipse2D.Float(dot.getBounds().x, dayContainer.getBounds().y + dayContainer.height - 15, 10, 10);
        g2.fill(dot);

        xPos = dot.getBounds().x + 30;
        month.setBounds(xPos, yPos, fm.stringWidth("00"), fm.getHeight());
        month.setVisible(true);

        RoundRectangle2D.Float monthContainer = new RoundRectangle2D.Float(month.getBounds().x - 5, month.getBounds().y - 5,
                month.getWidth() + 10, month.getHeight() + 10, 15, 15);
        g2.setColor(new Color(0x12A3D2));
        g2.fill(monthContainer);
        g2.setColor(new Color(0xFF9900));
        if (month.isFocusOwner())
            g2.draw(monthContainer);

        dot = new Ellipse2D.Float(monthContainer.x + monthContainer.width + 15, monthContainer.y + 5, 10, 10);
        g2.fill(dot);
        dot = new Ellipse2D.Float(dot.getBounds().x, monthContainer.getBounds().y + monthContainer.height - 15, 10, 10);
        g2.fill(dot);

        xPos = dot.getBounds().x + 30;
        year.setBounds(xPos, yPos, fm.stringWidth("00"), fm.getHeight());
        year.setVisible(true);

        RoundRectangle2D.Float yearContainer = new RoundRectangle2D.Float(year.getBounds().x - 5,
                year.getBounds().y - 5, year.getWidth() + 10, year.getHeight() + 10, 15, 15);
        g2.setColor(new Color(0x12A3D2));
        g2.fill(yearContainer);
        g2.setColor(new Color(0xFF9900));
        if (year.isFocusOwner())
            g2.draw(yearContainer);

        g2.setColor(Color.black);
        text = "Day";
        g2.drawString(text, dayContainer.x + dayContainer.width / 2 - g2.getFontMetrics().stringWidth(text) / 2f,
                dayContainer.y + dayContainer.width + g2.getFontMetrics().getHeight() + 5);
        text = "Month";
        g2.drawString(text, monthContainer.x + monthContainer.width / 2 - g2.getFontMetrics().stringWidth(text) / 2f,
                monthContainer.y + monthContainer.width + g2.getFontMetrics().getHeight() + 5);
        text = "Year";
        g2.drawString(text, yearContainer.x + yearContainer.width / 2 - g2.getFontMetrics().stringWidth(text) / 2f,
                yearContainer.y + yearContainer.width + g2.getFontMetrics().getHeight() + 5);
    }

    public void drawTimeInterface(Graphics2D g2, int x, int y, JTextField hour, JTextField min) {
        w = 165;
        Font font = new Font("Bell MT", Font.BOLD, 30);
        setPropertiesForTime(hour, min, font);

        text = "Select time";

        //Draw main container
        mainContainer = new RoundRectangle2D.Float(x, y, w, h, 20, 20);
        g2.setColor(new Color(0x35464B));
        g2.fill(mainContainer);
        g2.setColor(Color.WHITE);
        g2.draw(mainContainer);
        g2.setColor(Color.BLACK);
        font = font.deriveFont(Font.PLAIN, 15f);
        g2.setFont(font);
        g2.drawString(text, (int) (x + mainContainer.getArcWidth() / 2), (int) (y + mainContainer.getArcHeight()));

        //Draw hour container
        fm = g2.getFontMetrics(font.deriveFont(Font.BOLD, 30f));
        int xPos, yPos;
        xPos = (int) (x + mainContainer.getArcWidth() / 2) + 15;
        yPos = (int) (mainContainer.getBounds().getCenterY() - fm.getHeight() / 2);
        hour.setBounds(xPos, yPos, fm.stringWidth("00"), fm.getHeight());
        hour.setVisible(true);

        g2.setStroke(new BasicStroke(4));
        RoundRectangle2D.Float hourContainer = new RoundRectangle2D.Float(hour.getBounds().x - 5, hour.getBounds().y - 5,
                hour.getWidth() + 10, hour.getHeight() + 10, 15, 15);
        g2.setColor(new Color(0x12A3D2));
        g2.fill(hourContainer);
        g2.setColor(new Color(0xFF9900));
        if (hour.isFocusOwner())
            g2.draw(hourContainer);

        Ellipse2D dot = new Ellipse2D.Float(hourContainer.x + hourContainer.width + 15, hourContainer.y + 5, 10, 10);
        g2.fill(dot);
        dot = new Ellipse2D.Float(dot.getBounds().x, hourContainer.getBounds().y + hourContainer.height - 15, 10, 10);
        g2.fill(dot);

        xPos = dot.getBounds().x + 30;
        min.setBounds(xPos, yPos, fm.stringWidth("00"), fm.getHeight());
        min.setVisible(true);

        RoundRectangle2D.Float minContainer = new RoundRectangle2D.Float(min.getBounds().x - 5, min.getBounds().y - 5,
                min.getWidth() + 10, min.getHeight() + 10, 15, 15);
        g2.setColor(new Color(0x12A3D2));
        g2.fill(minContainer);
        g2.setColor(new Color(0xFF9900));
        if (min.isFocusOwner())
            g2.draw(minContainer);

        g2.setColor(Color.black);
        text = "Hour";
        g2.drawString(text, hourContainer.x + hourContainer.width / 2 - g2.getFontMetrics().stringWidth(text) / 2f,
                hourContainer.y + hourContainer.width + g2.getFontMetrics().getHeight() + 5);
        text = "Min";
        g2.drawString(text, minContainer.x + minContainer.width / 2 - g2.getFontMetrics().stringWidth(text) / 2f,
                minContainer.y + minContainer.width + g2.getFontMetrics().getHeight() + 5);
    }

    private void setPropertiesForTime(JTextField hour, JTextField min, Font font) {
        hour.setFont(font);
        hour.setBackground(new Color(0x12A3D2));
        hour.setCaretColor(Color.black);
        hour.setForeground(Color.WHITE);
        hour.setBorder(null);
        min.setFont(font);
        min.setBackground(new Color(0x12A3D2));
        min.setCaretColor(Color.black);
        min.setForeground(Color.WHITE);
        min.setBorder(null);
    }

    private void setPropertiesForCalendar(JTextField year, JTextField mon, JTextField day, Font font) {
        year.setFont(font);
        year.setBackground(new Color(0x12A3D2));
        year.setCaretColor(Color.black);
        year.setForeground(Color.WHITE);
        year.setBorder(null);
        mon.setFont(font);
        mon.setBackground(new Color(0x12A3D2));
        mon.setCaretColor(Color.black);
        mon.setForeground(Color.WHITE);
        mon.setBorder(null);
        day.setFont(font);
        day.setBackground(new Color(0x12A3D2));
        day.setCaretColor(Color.black);
        day.setForeground(Color.WHITE);
        day.setBorder(null);
    }
}
