package fr.ul.acl.model;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ImagePanel extends JPanel {
    private Image image;
    private static final long serialVersionUID = 1L;
    public ImagePanel(String imagePath) {
        URL imgURL = getClass().getResource(imagePath);
        
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            this.image = icon.getImage();
        } else {
            System.err.println("Erreur : Impossible de trouver l'image à : " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}