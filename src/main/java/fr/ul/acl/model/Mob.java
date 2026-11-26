package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Mob {
    private double x, y;
    private double targetX, targetY;
    private double speed = 1.7;
    private int changeDirectionCooldown = 0;
    private boolean facingLeft = false;
    private double aimAngle = 0.0;

    private final int size = 64;
    private int maxHp;
    private int hp;
    private int hurtFlashTimer = 0;

    private ArrayList<BufferedImage> walkSprites = new ArrayList<>();
    private ArrayList<BufferedImage> attackSprites = new ArrayList<>();
    private boolean isAttacking = false;
    private int animIndex = 0;
    private int animTimer = 0;

    public Mob(double startX, double startY) {
        this(startX, startY, 60, 1.7);
    }

    public Mob(double startX, double startY, int maxHp, double speed) {
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.speed = speed;
        loadSprites();
    }

    private void loadSprites() {
        String[] walkFiles = {
            "/imagesSons/zombie_walk_01.png",
            "/imagesSons/zombie_walk_02.png",
            "/imagesSons/zombie_walk_03.png"
        };
        String[] attackFiles = {
            "/imagesSons/zombie_attack_01.png",
            "/imagesSons/zombie_attack_02.png",
            "/imagesSons/zombie_attack_03.png"
        };

        for (String path : walkFiles) {
            try {
                java.net.URL url = getClass().getResource(path);
                if (url != null) {
                    walkSprites.add(makeWhiteTransparent(ImageIO.read(url)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String path : attackFiles) {
            try {
                java.net.URL url = getClass().getResource(path);
                if (url != null) {
                    attackSprites.add(makeWhiteTransparent(ImageIO.read(url)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        // Oriente toujours le zombie vers le joueur (ou remplace par `if (distToPlayer < aggroRange)` pour ne pivoter qu'en poursuite)
 
        double angleToPlayer = Math.atan2(playerY - y, playerX - x);
        aimAngle = angleToPlayer;

        if (distToPlayer < aggroRange) {
            dx = Math.cos(angleToPlayer) * (speed * 1.4);
            dy = Math.sin(angleToPlayer) * (speed * 1.4);
        }
        else {
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

        if (dx < -0.1) facingLeft = true;
        else if (dx > 0.1) facingLeft = false;

        // Animation d'attaque quand le zombie est proche du joueur, sinon marche
        isAttacking = (distToPlayer < 75 && !attackSprites.isEmpty());
        ArrayList<BufferedImage> activeList = isAttacking ? attackSprites : walkSprites;

        if (activeList.size() > 1 && (isAttacking || Math.abs(dx) > 0.1 || Math.abs(dy) > 0.1)) {
            animTimer++;
            if (animTimer >= (isAttacking ? 8 : 12)) {
                animIndex = (animIndex + 1) % activeList.size();
                animTimer = 0;
            }
        } else {
            animIndex = 0;
        }

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
        double angle = Math.atan2(y - attackerY, x - attackerX);
        x += Math.cos(angle) * 25;
        y += Math.sin(angle) * 25;
    }

    public void draw(Graphics2D g2) {
        int drawX = (int)(x - size / 2);
        int drawY = (int)(y - size / 2);

        ArrayList<BufferedImage> activeList = isAttacking ? attackSprites : walkSprites;

        // Si tes sprites regardent déjà vers la droite à 0 rad, utilise `aimAngle`.
        // S'ils regardent vers le haut (vue du dessus type top-down), remplace `aimAngle` par `aimAngle + Math.PI / 2`.
        
        AffineTransform oldTransform = g2.getTransform();
     
        g2.rotate(aimAngle + Math.PI / 2, x, y);

        if (!activeList.isEmpty()) {
            BufferedImage current = activeList.get(animIndex % activeList.size());
            g2.drawImage(current, drawX, drawY, size, size, null);
            if (hurtFlashTimer > 0) {
                g2.setColor(new Color(255, 50, 50, 120));
                g2.fillOval(drawX + 8, drawY + 8, size - 16, size - 16);
            }
        } else {
            g2.setColor(hurtFlashTimer > 0 ? Color.WHITE : new Color(46, 125, 50));
            g2.fillOval(drawX + 8, drawY + 8, size - 16, size - 16);
            g2.setColor(Color.RED);
            g2.fillOval(drawX + 20, drawY + 22, 7, 7);
            g2.fillOval(drawX + 37, drawY + 22, 7, 7);
        }

        // Restaure la transformation avant de dessiner la barre de vie (pour qu'elle reste horizontale)
        g2.setTransform(oldTransform);

        // Barre de vie dynamique au-dessus du zombie
        double ratio = (double) hp / maxHp;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(drawX + 6, drawY - 10, size - 12, 6);
        g2.setColor(ratio > 0.5 ? Color.GREEN : (ratio > 0.25 ? Color.ORANGE : Color.RED));
        int hpWidth = (int)((size - 12) * ratio);
        g2.fillRect(drawX + 6, drawY - 10, hpWidth, 6);
    }

    public boolean isDead() { return hp <= 0; }
    public Rectangle getBounds() {
        return new Rectangle((int)(x - size / 2), (int)(y - size / 2), size, size);
    }
}