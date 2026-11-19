package fr.ul.acl.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MonJeu extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final int WINDOW_WIDTH = 600, WINDOW_HEIGHT = 400; 
    private final int MAP_WIDTH = 3000, MAP_HEIGHT = 3000;    
    private final int SIZE = 64;    
    private final double speed = 4 * 4.0;

    private Player player;
    private GameWorld world;
    private GameMenu menu;
    private HealthBar healthBar;
    private Portal portal;
    private ArrayList<Mob> mobs = new ArrayList<>();

    private boolean isDying = false;
    private int deathAnimTimer = 0;
    private boolean gameOver = false;
    private boolean victory = false;

    private int mouseX = 0;
    private int mouseY = 0;
    private final Set<Integer> pressedKeys = new HashSet<>();

    private javax.sound.sampled.Clip walkSoundClip;
    private javax.sound.sampled.Clip knifeSoundClip;
    private boolean isWalkingSoundPlaying = false;

    public MonJeu() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setFocusable(true);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (!menu.isActive() && !isDying && !gameOver && !victory && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    player.attack();
                    handlePlayerAttack();
                }
            }
        });

        world = new GameWorld(MAP_WIDTH, MAP_HEIGHT);
        player = new Player(200, 1000, SIZE);
        menu = new GameMenu();
        healthBar = new HealthBar(15, 15, 180, 22);
        spawnMobsForMap(1);

        loadWalkSound();
        loadBackgroundMusic();
        loadKnifeSound();

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (menu.isActive()) {
                    menu.handleKeyPressed(e.getKeyCode());
                    return;
                }
                if ((gameOver || victory) && e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    menu.setActive(true);
                    pressedKeys.clear();
                    return;
                }

                if (isDying) return;

                pressedKeys.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    player.switchWeapon();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        Timer timer = new Timer(16, e -> {
            updateGame();
            repaint();
        });
        timer.start();
    }

    private void spawnMobsForMap(int mapId) {
        mobs.clear();
        if (mapId == 1) {
            // Map 1 : zombies à 60 PV -> 2 coups de couteau (30 dégâts) suffisent
            mobs.add(new Mob(320, 960, 60, 1.7));
            mobs.add(new Mob(340, 1060, 60, 1.7));
            mobs.add(new Mob(260, 1120, 60, 1.7));
            portal = new Portal(400, 1000, 64, 64, 2, 300, 300, false);
        } else if (mapId == 2) {
            // Map 2 : zombies plus coriaces à 120 PV -> 4 coups de couteau
            mobs.add(new Mob(420, 300, 120, 2.2));
            mobs.add(new Mob(450, 380, 120, 2.2));
            mobs.add(new Mob(350, 420, 120, 2.2));
            mobs.add(new Mob(520, 450, 120, 2.2));
            portal = new Portal(500, 300, 64, 64, 3, 0, 0, false);
        }
    }

    private void handlePlayerAttack() {
        if (knifeSoundClip != null && player.getCurrentWeapon() == Player.WeaponType.COUTEAU) {
            knifeSoundClip.setFramePosition(0);
            knifeSoundClip.start();
        }

        double angle = Math.atan2(mouseY - (WINDOW_HEIGHT / 2.0), mouseX - (WINDOW_WIDTH / 2.0));
        int reach = (player.getCurrentWeapon() == Player.WeaponType.COUTEAU) ? 75 : 55;
        int damage = (player.getCurrentWeapon() == Player.WeaponType.COUTEAU) ? 30 : 15;

        double hitCenterX = player.getX() + Math.cos(angle) * reach;
        double hitCenterY = player.getY() + Math.sin(angle) * reach;
        Rectangle attackBox = new Rectangle((int)(hitCenterX - 50), (int)(hitCenterY - 50), 100, 100);

        for (Mob mob : mobs) {
            if (attackBox.intersects(mob.getBounds())) {
                mob.takeDamage(damage, player.getX(), player.getY());
            }
        }
    }

    private void restartGame() {
        gameOver = false;
        victory = false;
        isDying = false;
        deathAnimTimer = 0;
        pressedKeys.clear();
        player.resetAll();
        player.setX(200);
        player.setY(1000);
        world.loadMap(1);
        spawnMobsForMap(1);
    }

    private void loadWalkSound() {
        try {
            java.net.URL walkSoundUrl = getClass().getResource("/imagesSons/grass1.wav");
            if (walkSoundUrl != null) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(walkSoundUrl);
                walkSoundClip = javax.sound.sampled.AudioSystem.getClip();
                walkSoundClip.open(audioIn);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadKnifeSound() {
        try {
            java.net.URL url = getClass().getResource("/imagesSons/coup_couteau.wav");
            if (url != null) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(url);
                knifeSoundClip = javax.sound.sampled.AudioSystem.getClip();
                knifeSoundClip.open(audioIn);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadBackgroundMusic() {
        try {
            java.net.URL musicUrl = getClass().getResource("/imagesSons/Strike1.wav");
            if (musicUrl != null) {
                javax.sound.sampled.AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(musicUrl);
                javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                clip.open(audioIn);
                try {
                    javax.sound.sampled.FloatControl gainControl = 
                        (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-15.0f);
                } catch (Exception ex) {}
                clip.start();
                clip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    private void updateGame() {
        if (menu.isActive() || gameOver || victory) {
            stopWalkSound();
            return;
        }

        if (isDying) {
            stopWalkSound();
            pressedKeys.clear();
            deathAnimTimer++;
            if (deathAnimTimer >= 90) {
                isDying = false;
                deathAnimTimer = 0;
                player.loseLife();

                if (player.getLives() <= 0) {
                    gameOver = true;
                } else {
                    player.resetHp();
                    if (world.getCurrentMap() == 1) {
                        player.setX(200);
                        player.setY(1000);
                    } else {
                        player.setX(300);
                        player.setY(300);
                    }
                }
            }
            return;
        }

        double dx = 0, dy = 0;
        if (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_Z)) { dy -= speed; }
        if (pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S)) { dy += speed; }
        if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_Q)) { dx -= speed; }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) { dx += speed; }

        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
            player.startDash(dx, dy);
        }

        if (player.isDashing()) {
            double dashSpeed = speed * 3.5;
            dx = player.getDashDx() * dashSpeed;
            dy = player.getDashDy() * dashSpeed;
        }

        boolean isMoving = (dx != 0 || dy != 0);
        player.updateAnimation(isMoving);

        if (isMoving && !player.isDashing()) {
            if (walkSoundClip != null && !isWalkingSoundPlaying) {
                walkSoundClip.setFramePosition(0);
                walkSoundClip.loop(javax.sound.sampled.Clip.LOOP_CONTINUOUSLY);
                isWalkingSoundPlaying = true;
            }
        } else {
            stopWalkSound();
        }

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

        Iterator<Mob> it = mobs.iterator();
        while (it.hasNext()) {
            Mob mob = it.next();
            mob.update(player.getX(), player.getY(), MAP_WIDTH, MAP_HEIGHT, world);

            if (mob.getBounds().intersects(player.getBounds())) {
                player.takeDamage(20);
            }
            if (mob.isDead()) {
                it.remove();
            }
        }

        if (player.getHp() <= 0 && !isDying) {
            isDying = true;
            deathAnimTimer = 0;
            return;
        }

        if (mobs.isEmpty() && !portal.isVisible()) {
            portal.setVisible(true);
        }

        if (portal.checkCollision(player.getBounds())) {
            if (world.getCurrentMap() == 1) {
                world.loadMap(2);
                player.setX(portal.getSpawnX());
                player.setY(portal.getSpawnY());
                spawnMobsForMap(2);
            } else {
                victory = true;
            }
        }
    }

    private void stopWalkSound() {
        if (walkSoundClip != null && isWalkingSoundPlaying) {
            walkSoundClip.stop();
            isWalkingSoundPlaying = false;
        }
    }

    private void drawSkullAnimation(Graphics2D g2, int centerX, int centerY) {
        int lift = deathAnimTimer / 2;
        int alpha = Math.max(0, 255 - (deathAnimTimer * 2));
        int sx = centerX - 20;
        int sy = centerY - 40 - lift;

        g2.setColor(new Color(231, 76, 60, Math.min(140, alpha)));
        g2.fillOval(centerX - 36, centerY - 36, 72, 72);

        g2.setColor(new Color(245, 245, 245, alpha));
        g2.fillOval(sx, sy, 40, 32);
        g2.fillRoundRect(sx + 8, sy + 24, 24, 14, 6, 6);

        g2.setColor(new Color(20, 20, 20, alpha));
        g2.fillOval(sx + 8, sy + 12, 9, 9);
        g2.fillOval(sx + 23, sy + 12, 9, 9);
        g2.fillPolygon(new int[]{sx + 20, sx + 17, sx + 23}, new int[]{sy + 20, sy + 26, sy + 26}, 3);

        g2.drawLine(sx + 14, sy + 31, sx + 14, sy + 38);
        g2.drawLine(sx + 20, sy + 31, sx + 20, sy + 38);
        g2.drawLine(sx + 26, sy + 31, sx + 26, sy + 38);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (menu.isActive()) {
            menu.draw(g2, WINDOW_WIDTH, WINDOW_HEIGHT);
            return;
        }

        double cameraX = (WINDOW_WIDTH / 2.0) - player.getX();
        double cameraY = (WINDOW_HEIGHT / 2.0) - player.getY();
        g2.translate(cameraX, cameraY);

        world.draw(g2, this);
        portal.draw(g2);

        boolean isMoving = !isDying && (pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_Z) ||
                            pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S) ||
                            pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_Q) ||
                            pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D));
        
        player.draw(g2, isMoving, mouseX, mouseY, WINDOW_WIDTH, WINDOW_HEIGHT);

        for (Mob mob : mobs) {
            mob.draw(g2);
        }

        if (isDying) {
            drawSkullAnimation(g2, (int) player.getX(), (int) player.getY());
        }

        g2.translate(-cameraX, -cameraY);
        healthBar.draw(g2, player.getHp(), player.getMaxHp());

        g2.setFont(new Font("Arial", Font.BOLD, 18));
        for (int i = 0; i < 3; i++) {
            g2.setColor(i < player.getLives() ? new Color(231, 76, 60) : Color.DARK_GRAY);
            g2.drawString("♥", 205 + (i * 22), 33);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        String objectif = mobs.isEmpty() ? "Portail ouvert !" : "Ennemis restants : " + mobs.size();
        g2.drawString(objectif, 15, 55);

        if (gameOver || victory) {
            g2.setColor(new Color(0, 0, 0, 190));
            g2.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

            g2.setFont(new Font("Arial", Font.BOLD, 38));
            g2.setColor(victory ? new Color(46, 204, 113) : new Color(231, 76, 60));
            g2.drawString(victory ? "VICTOIRE !" : "GAME OVER", WINDOW_WIDTH / 2 - 110, WINDOW_HEIGHT / 2 - 10);

            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.setColor(Color.WHITE);
            g2.drawString("Appuie sur R pour recommencer depuis le début", WINDOW_WIDTH / 2 - 165, WINDOW_HEIGHT / 2 + 30);
        }
    }
}