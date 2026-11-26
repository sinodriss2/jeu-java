package fr.ul.acl.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameWorld {
    private static final int MIN_MAP_ID = 1;
    private static final int MAX_MAP_ID = 5;

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
        if (mapId < MIN_MAP_ID || mapId > MAX_MAP_ID) {
            mapId = MIN_MAP_ID;
        }

        this.currentMap = mapId;
        obstacles.clear();
        customObjects.clear();

        String mapPath = switch (mapId) {
            case 2 -> "/imagesSons/map2.png";
            case 3 -> "/imagesSons/map3.png";
            case 4 -> "/imagesSons/map4.png";
            case 5 -> "/imagesSons/map5.png";
            default -> "/imagesSons/grand_map.png";
        };

        try {
            java.net.URL imgURL = getClass().getResource(mapPath);
            if (imgURL == null) {
                imgURL = getClass().getResource("/imagesSons/grand_map.png");
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

        // --- OBJETS D'OBSTACLES PAR CARTE (1 A 5) ---
        switch (mapId) {
            case 1 -> {
                customObjects.add(new Obstacle(300, 900, 80, 80, "/imagesSons/obstacle.png"));
                customObjects.add(new Obstacle(550, 1050, 96, 96, "/imagesSons/obstacle_vrai.png"));
            }
            case 2 -> {
                customObjects.add(new Obstacle(400, 450, 90, 90, "/imagesSons/obstacle.png"));
                customObjects.add(new Obstacle(700, 350, 100, 100, "/imagesSons/obstacle_vrai.png"));
                customObjects.add(new Obstacle(1000, 1000, 150, 150, "/imagesSons/map2_rocher1.png"));
                customObjects.add(new Obstacle(2000, 1000, 150, 150, "/imagesSons/map2_palmier1.png"));
            }
            case 3 -> {
                customObjects.add(new Obstacle(500, 600, 96, 96, "/imagesSons/obstacle.png"));
                customObjects.add(new Obstacle(1200, 800, 120, 120, "/imagesSons/obstacle_vrai.png"));
            }
            case 4 -> {
                customObjects.add(new Obstacle(450, 500, 100, 100, "/imagesSons/obstacle_vrai.png"));
                customObjects.add(new Obstacle(1400, 900, 140, 140, "/imagesSons/map2_rocher1.png"));
            }
            case 5 -> {
                customObjects.add(new Obstacle(600, 500, 110, 110, "/imagesSons/obstacle.png"));
                customObjects.add(new Obstacle(1500, 1100, 150, 150, "/imagesSons/map2_palmier1.png"));
            }
            default -> {
            }
        }
    }

    public int getCurrentMap() { return currentMap; }
    public int getMaxMapId() { return MAX_MAP_ID; }

    public void draw(Graphics2D g2, JPanel observer) {
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, mapWidth, mapHeight, observer);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, mapWidth, mapHeight);
        }

        for (Obstacle obj : customObjects) {
            obj.draw(g2, observer);
        }
    }

    public boolean checkCollision(Rectangle hitbox) {
        for (Rectangle obstacle : obstacles) {
            if (hitbox.intersects(obstacle)) {
                return true;
            }
        }
        for (Obstacle obj : customObjects) {
            if (hitbox.intersects(obj.getBounds())) {
                return true;
            }
        }
        return false;
    }
}