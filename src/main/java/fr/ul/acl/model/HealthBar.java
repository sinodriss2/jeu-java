package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class HealthBar {
    private int x, y, width, height;

    public HealthBar(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D g2, int currentHp, int maxHp) {
        int safeHp = Math.max(0, Math.min(currentHp, maxHp));
        int currentWidth = (int) (width * ((double) safeHp / maxHp));

        g2.setColor(new Color(40, 40, 40, 200));
        g2.fillRect(x, y, width, height);

        if (safeHp > maxHp * 0.5) {
            g2.setColor(new Color(46, 204, 113));
        } else if (safeHp > maxHp * 0.25) {
            g2.setColor(new Color(241, 196, 15));
        } else {
            g2.setColor(new Color(231, 76, 60));
        }
        g2.fillRect(x, y, currentWidth, height);

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, width, height);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("PV : " + safeHp + " / " + maxHp, x + 10, y + height - 6);
    }
}