package fr.ul.acl.tests;

import fr.ul.acl.model.Player;

public class Test {
    public static void main(String[] args) {
        Player player = new Player(200, 1000, 64);
        System.out.println("PV de départ : " + player.getHp());
        player.takeDamage(15);
        System.out.println("PV après coup : " + player.getHp());
        player.switchWeapon();
        System.out.println("Arme actuelle : " + player.getCurrentWeapon());
    }
}