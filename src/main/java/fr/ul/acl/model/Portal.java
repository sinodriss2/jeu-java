package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Portal {
    private int x, y, width, height;
    private int targetMap;
    private double spawnX, spawnY;
    private boolean visible;

    public Portal(int x, int y, int width, int height, int targetMap, double spawnX, double spawnY) {
        this(x, y, width, height, targetMap, spawnX, spawnY, true);
    }

    public Portal(int x, int y, int width, int height, int targetMap, double spawnX, double spawnY, boolean visible) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.targetMap = targetMap;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.visible = visible;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean checkCollision(Rectangle playerBounds) {
        if (!visible) return false;
        return new Rectangle(x, y, width, height).intersects(playerBounds);
    }

    public void draw(Graphics2D g2) {
        if (!visible) return;
        g2.setColor(new Color(142, 68, 173, 180));
        g2.fillOval(x, y, width, height);
        g2.setColor(new Color(187, 143, 206));
        g2.drawOval(x + 6, y + 6, width - 12, height - 12);
    }

    public int getTargetMap() { return targetMap; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
}