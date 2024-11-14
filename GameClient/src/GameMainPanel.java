import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameMainPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("GameClient/image/background.png").getImage();
    private Image titleImage = new ImageIcon("GameClient/image/title.png").getImage();
    private Image penguinImage = new ImageIcon("GameClient/image/penguin.png").getImage();
    private Image sealImage = new ImageIcon("GameClient/image/seal.png").getImage();

    private ImageIcon gameStartImage = new ImageIcon("GameClient/image/game_start.png");
    private ImageIcon gameRulesImage = new ImageIcon("GameClient/image/game_rules.png");

    private JButton gameStartButton = createImageButton(gameStartImage, 709, 377, 214, 30);
    private JButton gameRulesButton = createImageButton(gameRulesImage, 709, 440, 203, 30);

    private ImageIcon gameStartHoverImage = new ImageIcon(applyColorFilter(gameStartImage, Color.decode("#B4FDFF")));
    private ImageIcon gameRulesHoverImage = new ImageIcon(applyColorFilter(gameRulesImage, Color.decode("#B4FDFF")));

    private GameClientFrame frame;

    public GameMainPanel() {
        setLayout(null);

        add(gameStartButton);  // 버튼 패널에 추가
        add(gameRulesButton);

        // 스타트 버튼 클릭 리스너 추가
        gameStartButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            public void mouseEntered(MouseEvent e){
                gameStartButton.setIcon(gameStartHoverImage);
            }

            public void mouseExited(MouseEvent e){
                gameStartButton.setIcon(gameStartImage);
            }
        });

        // 스타트 버튼 클릭 리스너 추가
        gameRulesButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            public void mouseEntered(MouseEvent e){
                gameRulesButton.setIcon(gameRulesHoverImage);
            }

            public void mouseExited(MouseEvent e){
                gameRulesButton.setIcon(gameRulesImage);
            }
        });
    }

    private JButton createImageButton(ImageIcon image, int x, int y, int width, int height) {
        JButton button = new JButton(image);
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);  // 배경 없애기
        return button;
    }

    // 기본 이미지를 BufferedImage로 변환
    private BufferedImage applyColorFilter(ImageIcon icon, Color filterColor) {
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        // Graphics2D로 기본 이미지 위에 색상 필터를 적용
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(icon.getImage(), 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcAtop.derive(0.5f)); // 투명도 조정
        g2d.setColor(filterColor);
        g2d.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
        g2d.dispose();

        return bufferedImage;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(titleImage, 177, 129, 641, 74, this);
        g.drawImage(penguinImage, 41, 305, 280, 280, this);
        g.drawImage(sealImage, 269, 336, 260, 260, this);
    }
}
