package Map;

import javax.swing.*;
import java.awt.*;

public class ButtonBlock {
    public static final int BUTTON_WIDTH = 102;
    public static final int BUTTON_HEIGHT = 16;

    Image buttonBlockImage;
    Rectangle rectButtonBlock;

    int x, y, width, height;
    private Boolean isVisible = true;

    public ButtonBlock() {

    }

    public ButtonBlock(int x, int y) {
        this.x = x;
        this.y= y;
        this.width = BUTTON_WIDTH;
        this.height = BUTTON_HEIGHT;
        this.rectButtonBlock = new Rectangle(x,y,width,height);
        setButtonBlockImage();
    }

    public void setButtonBlockImage() {
        buttonBlockImage = resizeImage(new ImageIcon("GameClient/image/map/button_block.png"), BUTTON_WIDTH, BUTTON_HEIGHT).getImage();
    }

    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public void setVisible(Boolean value) {isVisible = value;}
    public Boolean getIsVisible() {return isVisible;}

    public Image getButtonBlockImage() {
        return buttonBlockImage;
    }

    public Rectangle getRectButtonBlock() {
        return rectButtonBlock;
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
