package Map;

import javax.swing.*;
import java.awt.*;

public class Block {
    Image blockImage = new ImageIcon("GameClient/image/map/block.png").getImage();
    Rectangle rectBlock;
    int x, y, width, height;

    public static final int BLOCK_WIDTH = 34;
    public static final int BLOCK_HEIGHT = 34;

    public Block() {}

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = BLOCK_WIDTH;
        this.height = BLOCK_HEIGHT;
        this.rectBlock = new Rectangle(x, y, width, height);
    }

    public Image getBlockImage() {
        return blockImage;
    }

    public Rectangle getRectBlock() {
        return rectBlock;
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
}
