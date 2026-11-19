package fr.ul.acl.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player {
    private double x, y;
    private final int size;
    
    private int hp = 100;
    private int maxHp = 100;
    private int invulnerabilityTimer = 0;
    
    // Armes et Sprites
    public enum WeaponType { MAINS_NUES, COUTEAU }
    private WeaponType currentWeapon = WeaponType.MAINS_NUES;

    // Sprites Mains Nues
    private BufferedImage[] standFrames = new BufferedImage[2];
    private BufferedImage[] walkFrames = new BufferedImage[3];
    
    // Sprites Couteau (Exactement le même nombre de frames !)
    private BufferedImage[] knifeStandFrames = new BufferedImage[2];
    private BufferedImage[] knifeWalkFrames = new BufferedImage[3];
    private BufferedImage[] knifeAttackFrames = new BufferedImage[3]; // Ex: 3 frames pour l'attaque au couteau

    private final int[] walkSequence = {0, 1, 2, 1};
    private int animTick = 0;
    private int currentAnimIndex = 0;

    // État de l'attaque
    private boolean isAttacking = false;
    private int attackAnimTimer = 0;

    // Dash (ton code précédent)
    private boolean isDashing = false;
    private int dashDurationTimer = 0;   
    private int dashCooldownTimer = 0;   
    private double dashDx = 0, dashDy = 0;

    public Player(double startX, double startY, int size) {
        this.x = startX;
        this.y = startY;
        this.size = size;
        loadAssets();
    }

    private void loadAssets() {
        try {
            // Chargement Mains Nues
            standFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/man_standing_01.png"));
            standFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/man_standing_02.png"));
            walkFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/man_walking_0001.png"));
            walkFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/man_walking_0002.png"));
            walkFrames[2] = ImageIO.read(getClass().getResource("/imagesSons/man_walking_0003.png"));

            // Chargement Couteau (adapte les noms de tes fichiers selon tes dossiers !)
            knifeStandFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/knife_stand_01.png"));
            knifeStandFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/knife_stand_02.png"));
            knifeWalkFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/knife_walk_0001.png"));
            knifeWalkFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/knife_walk_0002.png"));
            knifeWalkFrames[2] = ImageIO.read(getClass().getResource("/imagesSons/knife_walk_0003.png"));
            
            // Si tu as des frames d'attaque spécifiques :
            knifeAttackFrames[0] = ImageIO.read(getClass().getResource("/imagesSons/knife_attack_01.png"));
            knifeAttackFrames[1] = ImageIO.read(getClass().getResource("/imagesSons/knife_attack_02.png"));
            knifeAttackFrames[2] = ImageIO.read(getClass().getResource("/imagesSons/knife_attack_03.png"));

            System.out.println("✅ Sprites et armes chargés avec succès !");
        } catch (Exception e) {
            System.err.println("⚠️ Attention : Certains sprites d'armes manquent ou ont un nom différent.");
        }
    }

    // Méthode pour changer d'arme (ex: touche 'E' ou molette)
    public void switchWeapon() {
        if (currentWeapon == WeaponType.MAINS_NUES) {
            currentWeapon = WeaponType.COUTEAU;
            System.out.println("🗡️ Arme équipée : Couteau");
        } else {
            currentWeapon = WeaponType.MAINS_NUES;
            System.out.println("✊ Arme équipée : Mains nues");
        }
    }

    // Déclenchement de l'attaque au clic gauche
    public void attack() {
        if (!isAttacking && currentWeapon == WeaponType.COUTEAU) {
            isAttacking = true;
            attackAnimTimer = 9; // Durée de l'animation d'attaque en frames (~1/4 de seconde)
            System.out.println("🔪 COUP DE COUTEAU !");
        }
    }

    // Gestion du Dash (ton code)
    public void startDash(double currentDx, double currentDy) {
        if (dashCooldownTimer == 0 && !isDashing) {
            if (currentDx == 0 && currentDy == 0) {
                dashDx = 0; dashDy = -1;
            } else {
                double length = Math.sqrt(currentDx * currentDx + currentDy * currentDy);
                dashDx = currentDx / length;
                dashDy = currentDy / length;
            }
            isDashing = true;
            dashDurationTimer = 12; 
            dashCooldownTimer = 45; 
        }
    }

    public void updateAnimation(boolean isMoving) {
    	if (invulnerabilityTimer > 0) invulnerabilityTimer--;
    	
        if (isDashing) {
            dashDurationTimer--;
            if (dashDurationTimer <= 0) isDashing = false;
        }
        if (dashCooldownTimer > 0) dashCooldownTimer--;

        if (isAttacking) {
            attackAnimTimer--;
            if (attackAnimTimer <= 0) isAttacking = false;
        }

        animTick++;
        if (animTick >= 10) {
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
        BufferedImage currentFrameImage = null;

        // Choix de l'image selon l'arme et l'action (Attaque, Marche, Standing)
        if (isAttacking && currentWeapon == WeaponType.COUTEAU) {
            int attackIndex = (9 - attackAnimTimer) / 3; // Divise les 15 frames en 3 étapes d'animation
            if (attackIndex > 2) attackIndex = 2;
            currentFrameImage = knifeAttackFrames[attackIndex];
        } else if (currentWeapon == WeaponType.COUTEAU) {
            if (isMoving) {
                int frameIndex = walkSequence[currentAnimIndex % walkSequence.length];
                currentFrameImage = knifeWalkFrames[frameIndex];
            } else {
                int frameIndex = currentAnimIndex % knifeStandFrames.length;
                currentFrameImage = knifeStandFrames[frameIndex];
            }
        } else { // Mains nues
            if (isMoving) {
                int frameIndex = walkSequence[currentAnimIndex % walkSequence.length];
                currentFrameImage = walkFrames[frameIndex];
            } else {
                int frameIndex = currentAnimIndex % standFrames.length;
                currentFrameImage = standFrames[frameIndex];
            }
        }

        // Calcul de l'angle vers la souris (toujours orienté vers le curseur !)
        double centerX = windowWidth / 2.0;
        double centerY = windowHeight / 2.0;
        double angle = Math.atan2(mouseY - centerY, mouseX - centerX) + Math.PI / 2;

        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.translate(x, y);
        g2d.rotate(angle);

        if (isDashing) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        }

        if (currentFrameImage != null) {
            g2d.drawImage(currentFrameImage, -size / 2, -size / 2, size, size, null);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillRect(-size / 2, -size / 2, size, size);
        }
        g2d.dispose();
    }

    public boolean isDashing() { return isDashing; }
    public double getDashDx() { return dashDx; }
    public double getDashDy() { return dashDy; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public Rectangle getBounds() {
        return new Rectangle((int)(x - size / 2), (int)(y - size / 2), size, size);
    }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public void takeDamage(int amount) {
        if (invulnerabilityTimer == 0 && !isDashing) {
            this.hp = Math.max(0, this.hp - amount);
            this.invulnerabilityTimer = 45;
        }
    }

    public void resetHp() {
        this.hp = maxHp;
        this.invulnerabilityTimer = 0;
    }

    public WeaponType getCurrentWeapon() { return currentWeapon; }
    public void heal(int amount) { this.hp = Math.min(maxHp, this.hp + amount); }
    private int lives = 3;

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        this.lives = Math.max(0, this.lives - 1);
    }

    public void resetHp() {
        this.hp = this.maxHp;
        this.invulnerabilityTimer = 0;
    }

    public void resetAll() {
        this.lives = 3;
        this.hp = this.maxHp;
        this.invulnerabilityTimer = 0;
    }
}