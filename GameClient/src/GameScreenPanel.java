import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameScreenPanel extends JPanel {

    public GameWaitPanel gameWaitPanel;
    private GamePlayPanel gamePlayPanel = null;
    private ChatPanel chatPanel;

    public GameScreenPanel(GameClientFrame gameClientFrame, String userName) {
        setLayout(null);
        setSize(1000, 600);
        setVisible(true);

        gameWaitPanel = new GameWaitPanel(gameClientFrame);
        gameWaitPanel.setBounds(0, 0, 1000, 600); // 위치 및 크기 설정

        add(gameWaitPanel);
        changeWaitPlayerNum();

        gameWaitPanel.setBackground(Color.BLACK);
        chatPanel = new ChatPanel(userName);

    }

    public void changeWaitPlayerNum() {
        gameWaitPanel.changePlayerNum(GameClientFrame.waitingPlayerNum); // gameRoom에 입장한 플레이어 수
        if (GameClientFrame.waitingPlayerNum == 2) {
            gameWaitPanel.addGameStartBtn();
        }
        repaint();
    }

    public void changeToPlaypanel() {
        remove(gameWaitPanel);
        if (gamePlayPanel == null) {
            gamePlayPanel = new GamePlayPanel();
            gamePlayPanel.setBounds(0, 0, 800, 600);
            add(gamePlayPanel);
            //addKeyListener(gamePlayPanel.testKey);
            gamePlayPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("mouse click");
                    requestFocus();
                    setFocusable(true);
                }
            });
            chatPanel.setBounds(800, 0, 200, 600);
            chatPanel.changePlayerList(GameClientFrame.playerNames);
            add(chatPanel);
        }
        this.repaint();
    }


}