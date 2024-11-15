import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameClientFrame extends JFrame {

    public static boolean isChanged, isGameMainScreen, isGameStartScreen, isGameRulesScreen, isWaitScreen;  // 게임 규칙 화면 상태
    public static ListenNetwork net = null;
    public static String userName;

    public static int userNum; // 첫번째 유저인지, 두번째 유저인지
    public static int waitingPlayerNum; // 게임룸에 몇 명이 기다리고 잇는지
    public static ArrayList<String> playerNames = new ArrayList<String>(); //유저 두명 이름

    public GameClientFrame() {
        setTitle("Snowball Duo - Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();  // 초기화 메서드 호출

        ///GameWaitPanel mainPanel = new GameWaitPanel(this);
        GameMainPanel mainPanel = new GameMainPanel(this);
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        GameThread gameThread = new GameThread();
        gameThread.start();
        setVisible(true);

        this.requestFocus();
        this.setFocusable(true);
    }
    class GameThread extends Thread{ // 게임 전반적 관리

        public void run() {
            while(true) {
                if(isChanged) { // 화면에 변화가 필요할 때
                    isChanged = false;
                    selectScreen(); // 변경될 화면 선택
                }

                try {
                    Thread.sleep(10);
                }catch(InterruptedException e) {
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

    public void selectScreen() {
        if(isGameMainScreen){
            isGameMainScreen = false;
            setContentPane(new GameMainPanel(this));
        }
        else if (isGameStartScreen) {  // 게임 시작 화면 전환
            isGameStartScreen = false;
            setContentPane(new GameStartPanel(this));
        } else if (isGameRulesScreen) {  // 게임 규칙 화면으로 전환
            isGameRulesScreen = false;  // 상태 변경
            setContentPane(new GameRulePanel(this));
        }else if(isWaitScreen){
            isWaitScreen = false;  // 상태 변경
            setContentPane(new GameWaitPanel(this));
        }
        revalidate();
        repaint();
    }

    public static void interruptNet() {
        net.interrupt();
        System.out.println("interrupt");
    }
}
