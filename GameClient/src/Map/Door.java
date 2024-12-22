package Map;

import javax.swing.*;
import java.awt.*;

public class Door {
    public static final int DOOR_WIDTH = 68;
    public static final int DOOR_HEIGHT = 68;
    private int mapNumber; //문 2개를 구별해주는 int변수
    int x, y, width, height;
    Image doorImage;

    public Door() {}

    public Door(int x, int y, int mapNumber) {
        this.x = x;
        this.y = y;
        this.width = DOOR_WIDTH;
        this.height = DOOR_HEIGHT;
        this.mapNumber = mapNumber;
        setDoorImage();
    }

    public void setDoorImage() {
        if(mapNumber%2 == 0) { //6=물범문
            doorImage = resizeImage(new ImageIcon("GameClient/image/map/harp_door.png"), DOOR_WIDTH, DOOR_HEIGHT).getImage();
        }
        else { //펭귄문
            doorImage = resizeImage(new ImageIcon("GameClient/image/map/peng_door.png"), DOOR_WIDTH, DOOR_HEIGHT).getImage();
        }
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

    public Image getDoorImage() {
        return doorImage;
    }

    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}
