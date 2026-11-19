package fr.ul.acl.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameWorld {
    private BufferedImage bgImage;
    private ArrayList<Rectangle> obstacles = new ArrayList<>();
    private ArrayList<Obstacle> customObjects = new ArrayList<>();
    private final int mapWidth, mapHeight;
    private int currentMap = 1;

    public GameWorld(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        loadMap(1);
    }

    public void loadMap(int mapId) {
        this.currentMap = mapId;
        obstacles.clear();
        customObjects.clear();

        String mapPath = (mapId == 2) ? "/imagesSons/map2.png" : "/imagesSons/grand_map.png";

        try {
            java.net.URL imgURL = getClass().getResource(mapPath);
            if (imgURL == null) {
                mapPath = "/imagesSons/grand_map.png";
                imgURL = getClass().getResource(mapPath);
            }

            if (imgURL != null) {
                bgImage = ImageIO.read(imgURL);
                int largeur = bgImage.getWidth();
                int hauteur = bgImage.getHeight();

                double scaleX = (double) mapWidth / largeur;
                double scaleY = (double) mapHeight / hauteur;
                int stepX = Math.max(1, largeur / 300);
                int stepY = Math.max(1, hauteur / 300);

                for (int y = 0; y < hauteur; y += stepY) {
                    for (int x = 0; x < largeur; x += stepX) {
                        int couleurPixel = bgImage.getRGB(x, y);
                        if ((couleurPixel & 0x00FFFFFF) == 0) {
                            obstacles.add(new Rectangle(
                                (int) (x * scaleX),
                                (int) (y * scaleY),
                                (int) Math.ceil(stepX * scaleX),
                                (int) Math.ceil(stepY * scaleY)
                            ));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- AJOUT DE TES OBJETS D'OBSTACLES (PAS BESOIN DE NOIR) ---
        if (mapId == 1) {
            // Exemple avec les images déjà présentes dans votre dossier imagesSons :
            customObjects.add(new Obstacle(300, 900, 80, 80, "/imagesSons/obstacle.png"));
            customObjects.add(new Obstacle(550, 1050, 96, 96, "/imagesSons/obstacle_vrai.png"));
        } else if (mapId == 2) {
            // Objets sur la Map 2 (change les chemins avec tes propres PNG) :
            customObjects.add(new Obstacle(400, 450, 90, 90, "/imagesSons/obstacle.png"));
            customObjects.add(new Obstacle(700, 350, 100, 100, "/imagesSons/obstacle_vrai.png"));
            customObjects.add(new Obstacle(1000, 1000, 150, 150, "/imagesSons/map2_rocher1.png"));
            customObjects.add(new Obstacle(2000, 1000, 150, 150, "/imagesSons/map2_palmier1.png"));
        }
    }

    public int getCurrentMap() { return currentMap; }

    public void draw(Graphics2D g2, JPanel observer) {
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, mapWidth, mapHeight, observer);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, mapWidth, mapHeight);
        }

        // Dessin de tous tes objets par-dessus la carte
        for (Obstacle obj : customObjects) {
            obj.draw(g2, observer);
        }
    }

    public boolean checkCollision(Rectangle hitbox) {
        // 1. Collisions avec les pixels noirs de la map
        for (Rectangle obstacle : obstacles) {
            if (hitbox.intersects(obstacle)) {
                return true;
            }
        }
        // 2. Collisions avec tes nouveaux objets
        for (Obstacle obj : customObjects) {
            if (hitbox.intersects(obj.getBounds())) {
                return true;
            }
        }
        return false;
    }
}