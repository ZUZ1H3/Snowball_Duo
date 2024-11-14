import javax.swing.*;
import java.awt.*;

public class GameMainPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("GameClient/image/background.png").getImage();
    private Image titleImage = new ImageIcon("GameClient/image/title.png").getImage();
    private Image penguinImage = new ImageIcon("GameClient/image/penguin.png").getImage();
    private Image sealImage = new ImageIcon("GameClient/image/seal.png").getImage();

    private ImageIcon gameStartImage = new ImageIcon("GameClient/image/game_start.png");
    private ImageIcon gameRulesImage = new ImageIcon("GameClient/image/game_rules.png");

    private JButton gameStartButton = createImageButton(gameStartImage, 709, 377, 214, 30);
    private JButton gameRulesButton = createImageButton(gameRulesImage, 709, 440, 203, 30);

    private GameClientFrame frame;

    public GameMainPanel() {
        setLayout(null);

        add(gameStartButton);  // 버튼 패널에 추가
        add(gameRulesButton);

        // 스타트 버튼 클릭 리스너 추가
        gameStartButton.addActionListener(e -> {
        });
    }

    private JButton createImageButton(ImageIcon image, int x, int y, int width, int height) {
        JButton button = new JButton(image);
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);  // 배경 없애기
        return button;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(titleImage, 177, 129, 641, 74, this);
        g.drawImage(penguinImage, 41, 305, 280, 280, this);
        g.drawImage(sealImage, 269, 336, 260, 260, this);
    }
}
