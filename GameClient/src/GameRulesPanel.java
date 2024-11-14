import javax.swing.*;
import java.awt.*;

public class GameRulesPanel extends JPanel {
    public GameRulesPanel() {
        setBackground(Color.GREEN); // 예시로 배경을 녹색으로 설정합니다.
        setLayout(new BorderLayout());
        JLabel label = new JLabel("게임 룰 화면", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setForeground(Color.WHITE);
        add(label, BorderLayout.CENTER);
    }
}
