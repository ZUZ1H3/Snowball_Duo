import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ClientFrame extends JFrame {
    public static boolean isChanged, isGameMainScreen, isGameStartScreen, isGameRulesScreen, isGameScreen, isWaitScreen, isPlayingScreen;  // 게임 규칙 화면 상태
    public static ListenNetwork net = null;
    public static String userName;

    public static int userNum; // 첫번째 유저인지, 두번째 유저인지
    public static int waitingPlayerNum; // 게임룸에 몇 명이 기다리고 잇는지
    public static ArrayList<String> playerNames = new ArrayList<String>(); //유저 두명 이름
    public static GameScreenPanel gameScreenPanel = null;

    public ClientFrame() {
        setTitle("Snowball Duo - Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();  // 초기화 메서드 호출

        MainPanel mainPanel = new MainPanel(this);
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        setContentPane(mainPanel);
        pack();
        GameThread gameThread = new GameThread();
        gameThread.start();
        setVisible(true);

        this.requestFocus();
        this.setFocusable(true);
    }

    class GameThread extends Thread { // 게임 전반적 관리

        public void run() {
            while (true) {
                if (isChanged) { // 화면에 변화가 필요할 때
                    isChanged = false;
                    updateScreen(); // 변경될 화면 선택
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    // 초기화 메서드
    public static void init() {
        isGameMainScreen = false;
        isGameRulesScreen = false;  // 게임 규칙 화면 초기화
        isGameStartScreen = false;
    }

    public void updateScreen() {
        if (isGameMainScreen) {
            isGameMainScreen = false;
            setContentPane(new MainPanel(this));
        } else if (isGameStartScreen) {  // 게임 시작 화면 전환
            isGameStartScreen = false;
            setContentPane(new StartPanel(this));
        } else if (isGameRulesScreen) {  // 게임 규칙 화면으로 전환
            isGameRulesScreen = false;  // 상태 변경
            setContentPane(new RulesPanel(this));
        } else if (isGameScreen) { // 대기화면
            isGameScreen = false;
            if(gameScreenPanel == null) {
                gameScreenPanel = new GameScreenPanel(this, userName);
                setContentPane(gameScreenPanel);
                gameScreenPanel.requestFocus();
                gameScreenPanel.setFocusable(true);
            }
            else {
                gameScreenPanel.changeWaitPlayerNum();
            }
        }
        else if (isPlayingScreen) {
            isPlayingScreen = false;
            gameScreenPanel.changeToPlaypanel();
        }
        revalidate();
        repaint();
    }

    public static void interruptNet() {
        net.interrupt();
        System.out.println("interrupt");
    }
}
