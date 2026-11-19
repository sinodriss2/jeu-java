package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class GameMenu {
    private boolean active = true;
    private int selectedOption = 0;

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public void handleKeyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_Z ||
            keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
            selectedOption = (selectedOption == 0) ? 1 : 0;
        } else if (keyCode == KeyEvent.VK_ENTER) {
            if (selectedOption == 0) {
                active = false;
            } else {
                System.exit(0);
            }
        }
    }

    public void draw(Graphics2D g2, int width, int height) {
        g2.setColor(new Color(20, 24, 35));
        g2.fillRect(0, 0, width, height);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 38));
        g2.drawString("MON JEU", width / 2 - 85, height / 3);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.setColor(selectedOption == 0 ? Color.YELLOW : Color.LIGHT_GRAY);
        g2.drawString((selectedOption == 0 ? "> " : "  ") + "JOUER", width / 2 - 55, height / 2 + 10);

        g2.setColor(selectedOption == 1 ? Color.YELLOW : Color.LIGHT_GRAY);
        g2.drawString((selectedOption == 1 ? "> " : "  ") + "QUITTER", width / 2 - 55, height / 2 + 50);

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Z/S ou Flèches pour choisir, ENTRÉE pour valider (ÉCHAP = Pause)", 95, height - 25);
    }
}