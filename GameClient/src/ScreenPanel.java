import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScreenPanel extends JPanel {

    public WaitingPanel waitingPanel;
    private PlayPanel playPanel = null;
    private OverPanel overPanel = null;
    private ChatPanel chatPanel;
    private ClearPanel clearPanel;
    private String userName;

    public ScreenPanel(ClientFrame clientFrame, String userName) {
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

    // 다음 스테이지로 넘어감
    public void changeToNextStage() {
        remove(clearPanel);
        if (playPanel == null) {
            playPanel = new PlayPanel();
            playPanel.setBounds(0, 0, 800, 600);
            add(playPanel);
            addKeyListener(playPanel.testKey);
            playPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("mouse click");
                    requestFocus();
                    setFocusable(true);
                }
            });
        } else {
            playPanel.setBounds(0, 0, 800, 600);
            add(playPanel);
            addKeyListener(playPanel.testKey);
            playPanel.addMouseListener(new MouseAdapter() {
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

    //실제 플레이 패널로 전환
    public void changeToPlaypanel() {
        remove(waitingPanel);
        if (playPanel == null) {
            playPanel = new PlayPanel();
            playPanel.setBounds(0, 0, 800, 600);
            add(playPanel);
            addKeyListener(playPanel.testKey);
            playPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("mouse click");
                    requestFocus();
                    setFocusable(true);
                }
            });
        }

        if (chatPanel == null) { // chatPanel을 처음 생성하는 부분
            chatPanel = new ChatPanel(); // 저장한 userName을 넘겨줌
            chatPanel.setBounds(800, 0, 200, 600);
            chatPanel.changePlayerList(ClientFrame.playerNames);
            add(chatPanel);
        }
        this.repaint();
    }

    // 재시작 화면으로 변경하는 메서드
    public void changeToRePlay() {
        remove(overPanel);  // 게임 오버 화면 제거
        remove(chatPanel);

        playPanel.setVisible(true);  // 기존 게임 플레이 화면 보이기

        addKeyListener(playPanel.testKey);
        playPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("mouse click");
                requestFocus();
                setFocusable(true);
            }
        });

        chatPanel = new ChatPanel();
        chatPanel.setBounds(800, 0, 200, 600);  // 위치 설정
        chatPanel.changePlayerList(ClientFrame.playerNames);  // 플레이어 리스트 업데이트
        add(chatPanel);  // 채팅 패널 추가

        this.repaint();
    }

    // 게임 오버 화면으로 변경하는 메서드
    public void changeToGameOverPanel() {
        playPanel.setVisible(false);  // 게임 플레이 화면 숨기기
        if (overPanel == null) {
            overPanel = new OverPanel();
            overPanel.setBounds(0, 0, 800, 600);
            add(overPanel);
        }
        this.repaint();
    }

    // 게임 클리어 화면으로 변경하는 메서드
    public void changeToGameClearPanel(int fishCount, int shellCount) {
        remove(playPanel);
        if (clearPanel == null) {
            clearPanel = new ClearPanel(fishCount, shellCount);
            clearPanel.setBounds(0, 0, 800, 600);
            add(clearPanel);
        }
        this.repaint();
    }

    // 캐릭터 이동 정보 설정
    public void setMovingInfo(int x, int y, State type) {
        if (playPanel != null)
            playPanel.setMoving(x, y, type);
    }

    // 캐릭터 사망 이미지 설정
    public void setDieImage() {
        playPanel.setDieImage();
    }
}