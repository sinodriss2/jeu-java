package fr.ul.acl.test;

import fr.ul.acl.model.Player;

public class Test {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("       TEST COMPLET DE LA CLASSE PLAYER     ");
        System.out.println("==========================================\n");

        Player player = new Player(200, 1000, 64);

        // 1. État initial
        printSection("1. État initial");
        afficherEtat(player);

        // 2. Cycle complet de changement d'arme (aller-retour)
        printSection("2. Cycle complet d'armes");
        player.switchWeapon();
        System.out.println("Après 1er switch -> Arme : " + player.getCurrentWeapon());

        player.switchWeapon();
        System.out.println("Après 2e switch -> Arme : " + player.getCurrentWeapon());

        player.switchWeapon();
        System.out.println("Après 3e switch -> Arme : " + player.getCurrentWeapon());

        // 3. Dégâts, invulnérabilité temporaire et plancher à 0 PV
        printSection("3. Dégâts et invulnérabilité");
        player.takeDamage(25);
        System.out.println("Après 25 dégâts -> PV : " + player.getHp() + "/" + player.getMaxHp());

        // Dégâts immédiats pendant une éventuelle période d'invulnérabilité
        player.takeDamage(30);
        System.out.println("Après tentative de 30 dégâts juste après -> PV : " + player.getHp() + "/" + player.getMaxHp());

        // Avance des ticks d'animation pour laisser passer l'invulnérabilité
        for (int i = 0; i < 120; i++) {
            player.updateAnimation(false);
        }

        // Dégâts massifs pour vérifier le plancher à 0
        player.takeDamage(999);
        System.out.println("Après 999 dégâts -> PV : " + player.getHp() + "/" + player.getMaxHp());

        // 4. Dash et mise à jour de l'animation
        printSection("4. Dash et animation");
        double xAvant = player.getX();
        double yAvant = player.getY();

        player.startDash(1, 0);
        System.out.println("Dash activé (isDashing) : " + player.isDashing());

        for (int i = 0; i < 15; i++) {
            player.updateAnimation(true);
        }

        System.out.println("Position avant dash : (" + xAvant + ", " + yAvant + ")");
        System.out.println("Position après ticks  : (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("En dash après ticks   : " + player.isDashing());

        System.out.println("\n==========================================");
        System.out.println("               FIN DES TESTS              ");
        System.out.println("==========================================");
    }

    private static void afficherEtat(Player player) {
        System.out.println("PV       : " + player.getHp() + "/" + player.getMaxHp());
        System.out.println("Arme     : " + player.getCurrentWeapon());
        System.out.println("Position : (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("Dash     : " + player.isDashing());
    }

    private static void printSection(String titre) {
        System.out.println("\n--- " + titre + " ---");
    }
}