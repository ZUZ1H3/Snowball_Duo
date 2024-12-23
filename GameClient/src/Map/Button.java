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
    private boolean isSwitchOn;
    private boolean isVisible = true; // 추가: 버튼의 가시성을 위한 변수
    private ButtonBlock associatedButtonBlock;  // 해당 버튼과 연결된 ButtonBlock

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

    public boolean isSwitchOn() {
        return isSwitchOn;
    }

//    public void setSwitchOn(boolean switchOn) {
//        isSwitchOn = switchOn;
//        // 스위치가 눌리면 ButtonBlock 내려주기
//        if (this.associatedButtonBlock != null) {
//            this.associatedButtonBlock.moveDown();
//        } else {
//            System.out.println("Associated ButtonBlock is null.");
//        }
//        if (isSwitchOn) {
//            associatedButtonBlock.moveDown();
//        }
//    }

    public void setSwitchOn(boolean switchOn) {
        this.isSwitchOn = switchOn;
    }

    public ButtonBlock getAssociatedButtonBlock() {
        return associatedButtonBlock;
    }

    public void setAssociatedButtonBlock(ButtonBlock block) {
        this.associatedButtonBlock = block;
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
        //manage();
    }

    public void manage() {
        if (isSwitchOn) {
            manageBlock.moveDown(); // y좌표를 34만큼 내리는 메소드 호출
        } else {
            manageBlock.moveUp();   // 원래 위치로 돌아가는 메소드 호출
        }
    }

    // 버튼의 가시성을 확인하는 메소드 추가
    public boolean isVisible() {
        return isVisible;
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
