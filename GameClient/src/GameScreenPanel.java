import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameScreenPanel extends JPanel {

    public WaitingPanel waitingPanel;
    private GamePlayPanel gamePlayPanel = null;
    private GameOverPanel gameOverPanel = null;
    private ChatPanel chatPanel;
    private String userName;

    public GameScreenPanel(ClientFrame clientFrame, String userName) {
        this.userName = userName;
        setLayout(null);
        setSize(1000, 600);
        setVisible(true);

        waitingPanel = new WaitingPanel(clientFrame);
        waitingPanel.setBounds(0, 0, 1000, 600); // 위치 및 크기 설정
        waitingPanel.setBackground(Color.BLACK);
        add(waitingPanel);
        changeWaitPlayerNum();
    }

    public void changeWaitPlayerNum() {
        waitingPanel.changePlayerNum(ClientFrame.waitingPlayerNum); // gameRoom에 입장한 플레이어 수
        if (ClientFrame.waitingPlayerNum == 2) {
            waitingPanel.addGameStartBtn();
        }
        repaint();
    }

    public void changeToPlaypanel() {
        remove(waitingPanel);
        if (gamePlayPanel == null) {
            gamePlayPanel = new GamePlayPanel();
            gamePlayPanel.setBounds(0, 0, 800, 600);
            add(gamePlayPanel);
            addKeyListener(gamePlayPanel.testKey);
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
            chatPanel.changePlayerList(ClientFrame.playerNames);
            add(chatPanel);
        }
        this.repaint();
    }

    public void changeToGameOverPanel() {
        remove(gamePlayPanel);
        if(gameOverPanel == null) {
            gameOverPanel = new GameOverPanel();
            gameOverPanel.setBounds(0, 0, 800, 600);
            add(gameOverPanel);
        }
        this.repaint();
    }

    public void setMovingInfo(int x, int y, State type) {
        if(gamePlayPanel!=null)
            gamePlayPanel.setMoving(x, y, type);
    }

    public void setDieImage() {
        gamePlayPanel.setDieImage();
    }

}