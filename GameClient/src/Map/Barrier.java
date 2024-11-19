package Map;

import javax.swing.*;
import java.awt.*;

public class Barrier {
    public static final int BARRIER_WIDTH = 34;
    Image barrierImage;
    Rectangle barrierRect;
    private int mapNumber;
    int x, y, width, height;

    public Barrier() {}

    public Barrier(int x, int y, int mapNumber) {
        this.x = x;
        this.y = y;
        this.width = BARRIER_WIDTH;
        this.height = BARRIER_WIDTH;
        this.mapNumber = mapNumber;
        this.barrierRect = new Rectangle(x, y, width, height);
        setBarrierImage();
    }

    public void setBarrierImage() {
        if (mapNumber < 0) {
            barrierImage = resizeImage(new ImageIcon("GameClient/image/map/both_barrier.png"), BARRIER_WIDTH, BARRIER_WIDTH).getImage();
        }
        else if(mapNumber%2 == 0) { //딱딱얼음-펭귄만갈수있는
            barrierImage = resizeImage(new ImageIcon("GameClient/image/map/harp_barrier.png"), BARRIER_WIDTH, BARRIER_WIDTH).getImage();
        }
        else {
            barrierImage = resizeImage(new ImageIcon("GameClient/image/map/peng_barrier.png"), BARRIER_WIDTH, BARRIER_WIDTH).getImage();
        }
    }

    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public Image getBarrierImage() {
        return barrierImage;
    }

    public Rectangle getBarrierRect() {
        return barrierRect;
    }
}
