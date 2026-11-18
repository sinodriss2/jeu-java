package fr.ul.acl.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class MonJeu extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 400; 
    private final int MAP_WIDTH = 3000, MAP_HEIGHT = 3000;    
    private final int SIZE = 64;    
    private final double speed = 4 * 4.0;

    // Nos objets modularisés
    private Player player;
    private GameWorld world;

    // Entrées et souris
    private int mouseX = 0;
    private int mouseY = 0;
    private final Set<Integer> pressedKeys = new HashSet<>();

    // Audio
    private javax.sound.sampled.Clip walkSoundClip;
    private boolean isWalkingSoundPlaying = false;

    public MonJeu() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
     // Dans le constructeur de MonJeu, ajoute l'écouteur de clic de souris :
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Clic gauche
                    player.attack();
                }
            }
        });

        // Initialisation du monde et du joueur
        world = new GameWorld(MAP_WIDTH, MAP_HEIGHT);
        player = new Player(200, 1000, SIZE);

        // Chargement du son de pas
        loadWalkSound();
        // Chargement de la musique de fond
        loadBackgroundMusic();

        // Écouteur de souris
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });

        // Écouteur clavier
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { pressedKeys.add(e.getKeyCode()); 
            if (e.getKeyCode() == KeyEvent.VK_A) {
                player.switchWeapon();
            }
            }
            @Override
            public void keyReleased(KeyEvent e) { pressedKeys.remove(e.getKeyCode()); }
        });

        // Boucle de jeu à 60 FPS
        Timer timer = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        timer.start();
    }

    private void loadWalkSound() {
        try {
            java.net.URL walkSoundUrl = getClass().getResource("/imagesSons/grass1.wav");
            if (walkSoundUrl != null) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(walkSoundUrl);
                walkSoundClip = javax.sound.sampled.AudioSystem.getClip();
                walkSoundClip.open(audioIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBackgroundMusic() {
        try {
            java.net.URL musicUrl = getClass().getResource("/imagesSons/Strike1.wav");
            if (musicUrl != null) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(musicUrl);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(audioIn);

                // Réduction du volume (Gain en dB)
                try {
                    javax.sound.sampled.FloatControl gainControl = 
                        (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-15.0f); // Volume réduit
                } catch (Exception ex) {
                    System.err.println("Contrôle de volume non supporté.");
                }

                clip.start();
                clip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
                System.out.println("🎵 Musique de fond activée avec volume réduit.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    private void updateGame() {
        double dx = 0;
        double dy = 0;

        if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_Z)) { dy -= speed; }
        if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) { dy += speed; }
        if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_Q)) { dx -= speed; }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) { dx += speed; }

        // Normalisation diagonale normale
        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        // Si le joueur appuie sur Espace, on déclenche le dash en lui passant la direction actuelle
        if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
            player.startDash(dx, dy);
        }

        // --- GESTION DE LA VITESSE SI LE DASH EST ACTIF ---
        if (player.isDashing()) {
            double dashSpeed = speed * 3.5; // Vitesse fulgurante !
            dx = player.getDashDx() * dashSpeed;
            dy = player.getDashDy() * dashSpeed;
        }

        boolean isMoving = (dx != 0 || dy != 0);
        player.updateAnimation(isMoving);

        // Gestion du son de pas (on coupe le son pendant le dash si on veut, ou on le laisse)
        if (isMoving && !player.isDashing()) {
            if (walkSoundClip != null && !isWalkingSoundPlaying) {
                walkSoundClip.setFramePosition(0);
                walkSoundClip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
                isWalkingSoundPlaying = true;
            }
        } else {
            if (walkSoundClip != null && isWalkingSoundPlaying) {
                walkSoundClip.stop();
                isWalkingSoundPlaying = false;
            }
        }

        // --- COLLISIONS X et Y ---
        double futurX = Math.max(SIZE / 2, Math.min(player.getX() + dx, MAP_WIDTH - SIZE / 2));
        Rectangle hitboxX = new Rectangle((int)(futurX - SIZE / 2), (int)(player.getY() - SIZE / 2), SIZE, SIZE);
        if (!world.checkCollision(hitboxX)) {
            player.setX(futurX);
        }

        double futurY = Math.max(SIZE / 2, Math.min(player.getY() + dy, MAP_HEIGHT - SIZE / 2));
        Rectangle hitboxY = new Rectangle((int)(player.getX() - SIZE / 2), (int)(futurY - SIZE / 2), SIZE, SIZE);
        if (!world.checkCollision(hitboxY)) {
            player.setY(futurY);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Caméra centrée sur le joueur
        double cameraX = (WINDOW_WIDTH / 2.0) - player.getX();
        double cameraY = (WINDOW_HEIGHT / 2.0) - player.getY();
        g2.translate(cameraX, cameraY);

        // Dessin du monde
        world.draw(g2, this);

        // Dessin du joueur
        boolean isMoving = (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_Z) ||
                            pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S) ||
                            pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_Q) ||
                            pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D));
        
        player.draw(g2, isMoving, mouseX, mouseY, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
}