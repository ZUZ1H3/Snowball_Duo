package Map;

import javax.swing.*;
import java.awt.*;

public class Door {
    public static final int DOOR_WIDTH = 68;
    public static final int DOOR_HEIGHT = 68;
    private int state;
    int x, y, width, height;
    Image img;

    public Door() {}


    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }


}
