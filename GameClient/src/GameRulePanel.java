import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameRulePanel extends JPanel {
    private Image gameRuleBackgroundImage = new ImageIcon("GameClient/image/gamerule_background.png").getImage();

    private ImageIcon gameStartImage = new ImageIcon("GameClient/image/game_start.png");
    private ImageIcon backImage = new ImageIcon("GameClient/image/back.png");

    private JButton gameStartButton = createImageButton(gameStartImage, 729, 521, 214, 30);
    private JButton backButton = createImageButton(backImage, 39, 45, 43, 30);

    private ImageIcon gameStartHoverImage = new ImageIcon(applyColorFilter(gameStartImage, Color.decode("#B4FDFF")));
    private ImageIcon backHoverImage = new ImageIcon(applyColorFilter(backImage, Color.decode("#B4FDFF")));

    public GameRulePanel(GameClientFrame frame) {
        setLayout(null);

        add(gameStartButton);  // 버튼 패널에 추가
        add(backButton);

        // 스타트 버튼 클릭 리스너 추가
        gameStartButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                GameClientFrame.isGameStartScreen = true;  // 게임 규칙 화면으로 상태 변경
                frame.selectScreen();  // 화면 전환 메서드 호출
            }

            public void mouseEntered(MouseEvent e) {
                gameStartButton.setIcon(gameStartHoverImage);
            }

            public void mouseExited(MouseEvent e) {
                gameStartButton.setIcon(gameStartImage);
            }
        });

        // back 버튼 클릭 리스너 추가
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                GameClientFrame.isGameMainScreen = true;  // 게임 규칙 화면으로 상태 변경
                frame.selectScreen();  // 화면 전환 메서드 호출
            }

            public void mouseEntered(MouseEvent e) {
                backButton.setIcon(backHoverImage);
            }

            public void mouseExited(MouseEvent e) {
                backButton.setIcon(backHoverImage);
            }
        });
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

    private JButton createImageButton(ImageIcon image, int x, int y, int width, int height) {
        JButton button = new JButton(image);
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);  // 배경 없애기
        return button;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(gameRuleBackgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
