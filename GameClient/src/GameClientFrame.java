import javax.swing.*;
import java.awt.*;

public class GameClientFrame extends JFrame {

    public static boolean isGameMainScreen, isGameStartScreen, isGameRulesScreen;  // 게임 규칙 화면 상태

    public GameClientFrame() {
        setTitle("Snowball Duo - Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();  // 초기화 메서드 호출

        //GameStartPanel mainPanel = new GameStartPanel();
        GameMainPanel mainPanel = new GameMainPanel(this);
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        setContentPane(mainPanel);
        pack();
        setVisible(true);
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
        }
        revalidate();
        repaint();
    }
}
