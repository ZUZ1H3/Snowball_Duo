import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameScreenPanel extends JPanel {

    public GameWaitPanel gameWaitPanel;
    private GamePlayPanel gamePlayPanel = null;
    private ChatPanel gameInfoPane;
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
            gameWaitPanel.addGameStartBtn();
        }
        repaint();
    }
    public void changeToPlaypanel() {
        remove(gameWaitPanel);
        if(gameWaitPanel == null) {
            gamePlayPanel = new GamePlayPanel();
            gamePlayPanel.setBounds(0, 0, gamePlayPanel.getWidth(), gamePlayPanel.getHeight());
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


        this.repaint();
    }

}