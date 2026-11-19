package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Portal {
    private Rectangle hitbox;
    private int targetMap;
    private double spawnX, spawnY;

    public Portal(int x, int y, int width, int height, int targetMap, double spawnX, double spawnY) {
        this.hitbox = new Rectangle(x, y, width, height);
        this.targetMap = targetMap;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public boolean checkCollision(Rectangle playerHitbox) {
        return hitbox.intersects(playerHitbox);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(142, 68, 173, 180));
        g2.fillOval(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        g2.setColor(Color.CYAN);
        g2.drawOval(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("MAP " + targetMap, hitbox.x + 8, hitbox.y - 5);
    }

    public int getTargetMap() { return targetMap; }
    public double getSpawnX() { return spawnX; }
    public double getSpawnY() { return spawnY; }
}