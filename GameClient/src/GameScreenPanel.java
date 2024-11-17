import javax.swing.*;
import java.awt.*;

public class GameScreenPanel extends JPanel {

    public GameWaitPanel gameWaitPanel;

    public GameScreenPanel(GameClientFrame gameClientFrame, String userName) {
        setLayout(null);
        setSize(1000, 600);
        setVisible(true);

        gameWaitPanel = new GameWaitPanel(gameClientFrame);
        gameWaitPanel.setBounds(0, 0, 1000, 600); // 위치 및 크기 설정


        add(gameWaitPanel);
        changeWaitPlayerNum();

        gameWaitPanel.setBackground(Color.BLACK);
    }

    public void changeWaitPlayerNum() {
        gameWaitPanel.changePlayerNum(GameClientFrame.waitingPlayerNum); // gameRoom에 입장한 플레이어 수
        if (GameClientFrame.waitingPlayerNum == 2) {
            //gameWaitPanel.addGameStartBtn();
        }
        repaint();
    }

}