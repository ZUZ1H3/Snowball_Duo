package Map;

import javax.swing.*;
import java.awt.*;

public class ButtonBlock {
    public static final int BUTTON_WIDTH = 102;
    public static final int BUTTON_HEIGHT = 16;

    Image buttonBlockImage;
    Rectangle rectButtonBlock;

    int x, y, width, height;
    private boolean isVisible;
    private int originalY; // 원래 y좌표 저장용 변수 추가

    public ButtonBlock() {

    }

    public ButtonBlock(int x, int y) {
        this.x = x;
        this.y= y;
        this.originalY = y;
        this.width = BUTTON_WIDTH;
        this.height = BUTTON_HEIGHT;
        this.rectButtonBlock = new Rectangle(x,y,width,height);
        setButtonBlockImage();
    }

    public void moveDown() {
        if (isVisible) {
            y += 10;  // 버튼 블록을 아래로 10만큼 이동
        }
    }

    public void moveUp() {
        this.y = this.originalY; // 원래 y좌표로 복귀
        rectButtonBlock.setLocation(x, y);
    }

    public void setButtonBlockImage() {
        buttonBlockImage = resizeImage(new ImageIcon("GameClient/image/map/button_block.png"), BUTTON_WIDTH, BUTTON_HEIGHT).getImage();
    }

    public ImageIcon resizeImage(ImageIcon icon, int width, int height) {
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public void setVisible(Boolean value) {isVisible = value;}
    public boolean getIsVisible() {return isVisible;}

    public Image getButtonBlockImage() {
        return buttonBlockImage;
    }

    public Rectangle getRectButtonBlock() {
        return new Rectangle(x, y, width, height);
    }

    public int getOriginalY() {
        return originalY;
    }

    public void setY(int newY) {
        this.y = newY;
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
