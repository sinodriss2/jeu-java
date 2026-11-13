package fr.ul.acl.start;
import java.net.URL;
import javax.swing.*;
import fr.ul.acl.model.MonJeu; // 👈 On importe MonJeu et non ImagePanel

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Mon Jeu - Vue du dessus");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            
            URL iconURL = Main.class.getResource("/bruh/couverture.png");
            
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                frame.setIconImage(icon.getImage());
                System.out.println("✅ SUCCÈS : L'icône a été trouvée et chargée !");
            } else {
                System.err.println("❌ ERREUR : Toujours introuvable !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        frame.add(new MonJeu()); 
        
        frame.pack(); 
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}