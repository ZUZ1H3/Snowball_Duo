import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameScreenPanel extends JPanel {

    public GameWaitPanel gameWaitPanel;
    private GamePlayPanel gamePlayPanel = null;
    private ChatPanel chatPanel;
    private String userName; // userName을 필드로 추가

    public GameScreenPanel(GameClientFrame gameClientFrame, String userName) {
        this.userName = userName;
        setLayout(null);
        setSize(1000, 600);
        setVisible(true);

        gameWaitPanel = new GameWaitPanel(gameClientFrame);
        gameWaitPanel.setBounds(0, 0, 1000, 600); // 위치 및 크기 설정
        gameWaitPanel.setBackground(Color.BLACK);
        add(gameWaitPanel);
        changeWaitPlayerNum();
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
        }

        if (chatPanel == null) { // chatPanel을 처음 생성하는 부분
            chatPanel = new ChatPanel(userName); // 저장한 userName을 넘겨줌
            chatPanel.setBounds(800, 0, 200, 600);
            chatPanel.changePlayerList(GameClientFrame.playerNames);
            add(chatPanel);
        }
        this.repaint();
    }
}