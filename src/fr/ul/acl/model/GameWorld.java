package fr.ul.acl.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameWorld {
    private Image bgImage;
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private final int mapWidth, mapHeight;

    public GameWorld(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        loadWorld();
    }

    private void loadWorld() {
        try {
            // Chargement de l'image de fond
            java.net.URL imgURL = getClass().getResource("/imagesSons/grand_map.png");
            if (imgURL != null) {
                bgImage = new ImageIcon(imgURL).getImage();
            }

            // Chargement des collisions pixel par pixel
            java.net.URL collisionUrl = getClass().getResource("/imagesSons/grand_map.png");
            if (collisionUrl != null) {
                BufferedImage collisionImage = ImageIO.read(collisionUrl);
                int largeur = collisionImage.getWidth();
                int hauteur = collisionImage.getHeight();

                for (int y = 0; y < hauteur; y++) {
                    for (int x = 0; x < largeur; x++) {
                        int couleurPixel = collisionImage.getRGB(x, y);
                        // Si le pixel est noir (obstacle)
                        if ((couleurPixel & 0x00FFFFFF) == 0) {
                            obstacles.add(new Rectangle(x, y, 1, 1));
                        }
                    }
                }
                System.out.println("🧱 Murs chargés avec succès, Nombre de blocs : " + obstacles.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2, JPanel observer) {
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, mapWidth, mapHeight, observer);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, mapWidth, mapHeight);
        }
    }

    public boolean checkCollision(Rectangle hitbox) {
        for (Rectangle obstacle : obstacles) {
            if (hitbox.intersects(obstacle)) {
                return true;
            }
        }
        return false;
    }
}