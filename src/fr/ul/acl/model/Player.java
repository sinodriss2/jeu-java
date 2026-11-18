package fr.ul.acl.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {
    private double x, y;
    private final int size;
    
    // Animations
    private BufferedImage[] standFrames = new BufferedImage[2];
    private BufferedImage[] walkFrames = new BufferedImage[3];
    private final int[] walkSequence = {0, 1, 2, 1};
    private int animTick = 0;
    private int currentAnimIndex = 0;

    public Player(double startX, double startY, int size) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        loadAssets();
    }

    private void loadAssets() {
        try {
            standFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/man_standing_01.png"));
            standFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/man_standing_02.png"));
            
            walkFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/man_walking_0001.png"));
            walkFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/man_walking_0002.png"));
            walkFrames[2] = ImageIO.read(getClass().getResource("/imagesSons/man_walking_0003.png"));
            System.out.println("✅ Animations du joueur chargées avec succès !");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des sprites du joueur :");
            e.printStackTrace();
        }
    }

    public void updateAnimation(boolean isMoving) {
        animTick++;
        if (animTick >= 8) { // Rythme de l'animation
            animTick = 0;
            currentAnimIndex++;
        }
        
        if (isMoving) {
            currentAnimIndex %= walkSequence.length;
        } else {
            currentAnimIndex %= standFrames.length;
        }
    }

    public void draw(Graphics2D g2, boolean isMoving, int mouseX, int mouseY, int windowWidth, int windowHeight) {
        // Sélection de la frame actuelle
        BufferedImage currentFrameImage;
        if (isMoving) {
            int frameIndex = walkSequence[currentAnimIndex % walkSequence.length];
            currentFrameImage = walkFrames[frameIndex];
        } else {
            int frameIndex = currentAnimIndex % standFrames.length;
            currentFrameImage = standFrames[frameIndex];
        }

        // Calcul de l'angle vers la souris
        double centerX = windowWidth / 2.0;
        double centerY = windowHeight / 2.0;
        double angle = Math.atan2(mouseY - centerY, mouseX - centerX) + Math.PI / 2;

        // Rotation et affichage du joueur
        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.translate(x, y);
        g2d.rotate(angle);

        if (currentFrameImage != null) {
            g2d.drawImage(currentFrameImage, -size / 2, -size / 2, size, size, null);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(-size / 2, -size / 2, size, size);
        }
        g2d.dispose();
    }

    // Getters et Setters pour la position et les collisions
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    
    public Rectangle getBounds() {
        return new Rectangle((int)(x - size / 2), (int)(y - size / 2), size, size);
    }
}