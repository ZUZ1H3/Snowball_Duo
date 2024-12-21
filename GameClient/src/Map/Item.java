package Map;

import javax.swing.*;
import java.awt.*;

public class Item {
    public static final int ITEM_WIDTH = 25;
    private int mapNumber;
    Image itemImage;

    int x, y, width, height;

    public Item() {

    }

    public Item(int x, int y, int mapNumber) {
        this.x= x;
        this.y= y;
        this.width = ITEM_WIDTH;
        this.height = ITEM_WIDTH;
        this.mapNumber = mapNumber;
        setItemImage();
    }

    public void setItemImage() {
        if(mapNumber%2 == 0) { //물고기
            itemImage = resizeImage(new ImageIcon("GameClient/image/map/harp_item.png"), ITEM_WIDTH, ITEM_WIDTH).getImage();
        }
        else {
            itemImage = resizeImage(new ImageIcon("GameClient/image/map/peng_item.png"), ITEM_WIDTH, ITEM_WIDTH).getImage();
        }
    }

    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public Image getItemImage() {
        return itemImage;
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
