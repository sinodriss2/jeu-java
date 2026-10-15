package JEU;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Obstacle {
    private Rectangle bounds;

    public Obstacle(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
