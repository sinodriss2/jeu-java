package fr.ul.acl.model;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
 


public class MonJeu extends JPanel {
    private static final long serialVersionUID = 1L;
 // Liste qui contiendra tous les obstacles du jeu
    private java.util.ArrayList<java.awt.Rectangle> obstacles = new java.util.ArrayList<>();
    private final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 600; 
    private final int MAP_WIDTH = 2000, MAP_HEIGHT = 2000;    
    private final int SIZE = 50;    
    private double x = 1200, y = 1000;       
    private final double speed = 4*4.0; // Vitesse constante du joueur
    
    private Image bgImage;
    private final Set<Integer> pressedKeys = new HashSet<>();

    public MonJeu() {
    	
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);
        try {
            // 1. On charge ton image de collision (ex: collision.png dans ton dossier bruh)
            java.net.URL collisionUrl = getClass().getResource("/imagesSons/obstacle_vrai.png");
            if (collisionUrl != null) {
                java.awt.image.BufferedImage collisionImage = javax.imageio.ImageIO.read(collisionUrl);
                
                int largeur = collisionImage.getWidth();
                int hauteur = collisionImage.getHeight();
                
                // 2. On parcourt chaque pixel de l'image
                for (int y = 0; y < hauteur; y++) {
                    for (int x = 0; x < largeur; x++) {
                        int couleurPixel = collisionImage.getRGB(x, y);
                        
                        // 3. On extrait le niveau de rouge/vert/bleu ou on regarde si c'est du noir
                        // En gros, si le pixel est noir (ou très sombre) :
                        // (Tu peux affiner la condition selon si tes murs sont du pur noir 0 ou autre)
                        if ((couleurPixel & 0x00FFFFFF) == 0) { // 0 correspond au noir
                            
                            // On crée un petit rectangle invisible à cet endroit précis !
                            // (Tu peux multiplier x et y par un facteur de zoom si ton image est petite)
                            obstacles.add(new java.awt.Rectangle(x, y, 1, 1)); // 1x1 pixel de mur
                        }
                    }
                }
                System.out.println(" Murs chargés avec succès, Nombre de blocs : " + obstacles.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Chargement du fond d'écran
        java.net.URL imgURL = getClass().getResource("/imagesSons/Test_fond_obstacle.png"); 
        if (imgURL != null) {
            bgImage = new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Fond d'écran introuvable, fond gris par défaut.");
        }
        
        try {
            java.net.URL musicUrl = getClass().getResource("/imagesSons/Strike.wav");
            if (musicUrl != null) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(musicUrl);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
                clip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
                System.out.println("Musique de fond activée ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Écouteur clavier pour gérer le maintien des touches (diagonales fluides)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        // Timer de jeu à 60 FPS (16ms)
        Timer timer = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow(); // S'assure que le panneau a le focus au démarrage
    }

    // Logique de déplacement à vitesse constante
 // Logique de déplacement avec test de collision par image
    private void updateGame() {
        double dx = 0;
        double dy = 0;

        if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_Z)) {
            dy -= speed;
        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) {
            dy += speed;
        }
        if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_Q)) {
            dx -= speed;
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) {
            dx += speed;
        }

        // Normalisation diagonale
        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        // ==========================================
        // 1. TEST DE L'AXE X (Mouvement Horizontal)
        // ==========================================
        double futurX = Math.max(SIZE / 2, Math.min(x + dx, MAP_WIDTH - SIZE / 2));
        Rectangle hitboxX = new Rectangle((int)(futurX - SIZE / 2), (int)(y - SIZE / 2), SIZE, SIZE);
        
        boolean toucheMurX = false;
        for (Rectangle obstacle : obstacles) {
            if (hitboxX.intersects(obstacle)) {
                toucheMurX = true;
                break;
            }
        }
        // Si ça ne touche pas sur X, on bouge horizontalement
        if (!toucheMurX) {
            x = futurX;
        }

        // ==========================================
        // 2. TEST DE L'AXE Y (Mouvement Vertical)
        // ==========================================
        double futurY = Math.max(SIZE / 2, Math.min(y + dy, MAP_HEIGHT - SIZE / 2));
        // Note: On utilise le 'x' (éventuellement mis à jour juste au-dessus)
        Rectangle hitboxY = new Rectangle((int)(x - SIZE / 2), (int)(futurY - SIZE / 2), SIZE, SIZE);
        
        boolean toucheMurY = false;
        for (Rectangle obstacle : obstacles) {
            if (hitboxY.intersects(obstacle)) {
                toucheMurY = true;
                break;
            }
        }
        // Si ça ne touche pas sur Y, on bouge verticalement
        if (!toucheMurY) {
            y = futurY;
        }
    } 

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Caméra centrée sur le joueur
        double cameraX = (WINDOW_WIDTH / 2.0) - x;
        double cameraY = (WINDOW_HEIGHT / 2.0) - y;
        g2.translate(cameraX, cameraY);

        // Dessin du fond d'écran
        if (bgImage != null) {
            g2.drawImage(bgImage, 0, 0, MAP_WIDTH, MAP_HEIGHT, this);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0, 0, MAP_WIDTH, MAP_HEIGHT);
        }

        // Dessin du personnage
        g2.setColor(Color.RED);
        g2.fillRect((int) x - SIZE / 2, (int) y - SIZE / 2, SIZE, SIZE);
    }
}