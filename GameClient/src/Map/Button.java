package Map;

import javax.swing.*;
import java.awt.*;

public class Button {
    public static final int BUTTON_WIDTH = 26;
    public static final int BUTTON_HEIGHT = 9;

    Image buttonImage = new ImageIcon("GameClient/image/map/button.png").getImage();
    Rectangle rectButton;
    int x, y, width, height;
    private ButtonBlock manageBlock;
    private Boolean isSwitchOn = false;

    public Button() {

    }

    public Button(int x, int y) {
        this.x= x;
        this.y= y;
        this.width = BUTTON_WIDTH;
        this.height = BUTTON_HEIGHT;
        this.rectButton = new Rectangle(x,y,width, height);
        setButtonImage();
    }

    public void setButtonImage() {
        buttonImage = resizeImage(new ImageIcon("GameClient/image/map/button.png"),BUTTON_WIDTH, BUTTON_HEIGHT).getImage();
    }

    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public void setManageBlock(ButtonBlock manageBlock) {
        this.manageBlock = manageBlock;
    }

    public void setSwitchState(Boolean value) {
        this.isSwitchOn = value;
        manage();
    }

    public void manage() {
        if (isSwitchOn) {
            manageBlock.setVisible(false);
        }
        else {
            manageBlock.setVisible(true);
        }
    }

    public Image getButtonImage() {
        return buttonImage;
    }

    public Rectangle getRectButton() {
        return rectButton;
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
