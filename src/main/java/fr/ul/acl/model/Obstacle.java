package fr.ul.acl.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Obstacle {
    private BufferedImage sprite;
    private Rectangle hitbox;

    public Obstacle(int x, int y, int width, int height, String imagePath) {
        this.hitbox = new Rectangle(x, y, width, height);
        try {
            java.net.URL url = getClass().getResource(imagePath);
            if (url != null) {
                BufferedImage raw = ImageIO.read(url);
                this.sprite = makeWhiteTransparent(raw);
            } else {
                System.err.println("⚠️ Image d'obstacle introuvable : " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage makeWhiteTransparent(BufferedImage img) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int argb = img.getRGB(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                if (r > 240 && g > 240 && b > 240) {
                    result.setRGB(x, y, 0x00000000);
                } else {
                    result.setRGB(x, y, argb);
                }
            }
        }
        return result;
    }

    public void draw(Graphics2D g2, JPanel observer) {
        if (sprite != null) {
            g2.drawImage(sprite, hitbox.x, hitbox.y, hitbox.width, hitbox.height, observer);
        } else {
            g2.setColor(new Color(139, 69, 19));
            g2.fillRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }

    public Rectangle getBounds() {
        return hitbox;
    }
}