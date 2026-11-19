package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Mob {
    private double x, y;
    private double targetX, targetY;
    private double speed = 1.8;
    private int changeDirectionCooldown = 0;

    private final int size = 56;
    private int maxHp = 60;
    private int hp = 60;
    private int hurtFlashTimer = 0;
    private BufferedImage sprite;

    public Mob(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;
        loadSprite();
    }

    private void loadSprite() {
        try {
            // Si tu crées un mob.png dans imagesSons, il s'affichera automatiquement !
            java.net.URL url = getClass().getResource("/imagesSons/mob.png");
            if (url != null) {
                BufferedImage raw = ImageIO.read(url);
                this.sprite = makeWhiteTransparent(raw);
            }
        } catch (Exception e) {
            // Pas grave, on dessine un monstre stylisé par défaut
        }
    }

    private BufferedImage makeWhiteTransparent(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int py = 0; py < img.getHeight(); py++) {
            for (int px = 0; px < img.getWidth(); px++) {
                int argb = img.getRGB(px, py);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                result.setRGB(px, py, (r > 240 && g > 240 && b > 240) ? 0x00000000 : argb);
            }
        }
        return result;
    }

    public void update(double playerX, double playerY, int mapWidth, int mapHeight, GameWorld world) {
        if (hurtFlashTimer > 0) hurtFlashTimer--;

        double distToPlayer = Math.hypot(playerX - x, playerY - y);
        double aggroRange = 350;
        double dx = 0, dy = 0;

        if (distToPlayer < aggroRange) {
            double angle = Math.atan2(playerY - y, playerX - x);
            dx = Math.cos(angle) * (speed * 1.4);
            dy = Math.sin(angle) * (speed * 1.4);
        } else {
            changeDirectionCooldown--;
            if (changeDirectionCooldown <= 0) {
                double angle = Math.random() * 2 * Math.PI;
                double distance = 60 + Math.random() * 120;
                targetX = Math.max(size, Math.min(mapWidth - size, x + Math.cos(angle) * distance));
                targetY = Math.max(size, Math.min(mapHeight - size, y + Math.sin(angle) * distance));
                changeDirectionCooldown = 150;
            }

            double dirX = targetX - x;
            double dirY = targetY - y;
            double dist = Math.hypot(dirX, dirY);
            if (dist > 2) {
                dx = (dirX / dist) * speed;
                dy = (dirY / dist) * speed;
            }
        }

        // Déplacement avec collision contre les murs/obstacles
        double nextX = Math.max(size / 2, Math.min(x + dx, mapWidth - size / 2));
        Rectangle hitX = new Rectangle((int)(nextX - size / 2), (int)(y - size / 2), size, size);
        if (!world.checkCollision(hitX)) {
            x = nextX;
        }

        double nextY = Math.max(size / 2, Math.min(y + dy, mapHeight - size / 2));
        Rectangle hitY = new Rectangle((int)(x - size / 2), (int)(nextY - size / 2), size, size);
        if (!world.checkCollision(hitY)) {
            y = nextY;
        }
    }

    public void takeDamage(int amount, double attackerX, double attackerY) {
        hp = Math.max(0, hp - amount);
        hurtFlashTimer = 8;
        // Petit recul (knockback) quand il prend un coup
        double angle = Math.atan2(y - attackerY, x - attackerX);
        x += Math.cos(angle) * 25;
        y += Math.sin(angle) * 25;
    }

    public void draw(Graphics2D g2) {
        int drawX = (int)(x - size / 2);
        int drawY = (int)(y - size / 2);

        if (sprite != null) {
            g2.drawImage(sprite, drawX, drawY, size, size, null);
        } else {
            // Apparence par défaut (flash blanc s'il vient d'être touché)
            g2.setColor(hurtFlashTimer > 0 ? Color.WHITE : new Color(192, 57, 43));
            g2.fillOval(drawX, drawY, size, size);
            g2.setColor(Color.BLACK);
            g2.drawOval(drawX, drawY, size, size);
            // Yeux du monstre
            g2.setColor(Color.YELLOW);
            g2.fillOval(drawX + 14, drawY + 16, 8, 8);
            g2.fillOval(drawX + 34, drawY + 16, 8, 8);
        }

        // Mini barre de vie au-dessus du Mob
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(drawX, drawY - 10, size, 6);
        g2.setColor(Color.GREEN);
        int hpWidth = (int)(size * ((double) hp / maxHp));
        g2.fillRect(drawX, drawY - 10, hpWidth, 6);
    }

    public boolean isDead() { return hp <= 0; }
    public Rectangle getBounds() {
        return new Rectangle((int)(x - size / 2), (int)(y - size / 2), size, size);
    }
}