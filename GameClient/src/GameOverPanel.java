import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameOverPanel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private Image backgroundImage = new ImageIcon("GameClient/image/background/background_ingame_dark.png").getImage();
    private JLabel gameOverLabel = new JLabel(new ImageIcon("GameClient/image/game_over.png"));
    private JLabel continueLabel = new JLabel(new ImageIcon("GameClient/image/continue.png"));
    private ImageIcon yesImage = new ImageIcon("GameClient/image/yes.png");
    private ImageIcon noImage = new ImageIcon("GameClient/image/no.png");
    private ImageIcon yesHoverImage = new ImageIcon(applyColorFilter(yesImage, Color.decode("#B4FDFF")));
    private ImageIcon noHoverImage = new ImageIcon(applyColorFilter(noImage, Color.decode("#B4FDFF")));

    private JButton yesButton = new JButton(yesImage);
    private JButton noButton = new JButton(noImage);

    public GameOverPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setVisible(true);
        gameOverLabel.setBounds(254, 248, 315, 50);
        continueLabel.setBounds(334, 333, 159, 18);
        yesButton.setBounds(334, 369, 37, 15);
        noButton.setBounds(457, 369, 31, 15);

        yesButton.setBorderPainted(false);
        yesButton.setContentAreaFilled(false);  // 배경 없애기
        noButton.setBorderPainted(false);
        noButton.setContentAreaFilled(false);  // 배경 없애기

        add(gameOverLabel);
        add(continueLabel);
        add(yesButton);
        add(noButton);


        yesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //yesButton을 눌렀을때
                ClientFrame.isChanged = true;
                ClientFrame.isRetry = true;
            }

            public void mouseEntered(MouseEvent e) {
                yesButton.setIcon(yesHoverImage);
            }

            public void mouseExited(MouseEvent e) {
                yesButton.setIcon(yesHoverImage);
            }
        });

        noButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            public void mouseEntered(MouseEvent e) {
                noButton.setIcon(noHoverImage);
            }

            public void mouseExited(MouseEvent e) {
                noButton.setIcon(noImage);
            }
        });



    }

    private BufferedImage applyColorFilter(ImageIcon icon, Color filterColor) {
        BufferedImage bufferedImage = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
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
    }
}
