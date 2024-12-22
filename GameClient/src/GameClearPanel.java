import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameClearPanel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private Image backgroundImage = new ImageIcon("GameClient/image/background/background_ingame_dark.png").getImage();
    private JLabel gameClearLabel = new JLabel(new ImageIcon("GameClient/image/game_clear.png"));
    private JLabel continueLabel = new JLabel(new ImageIcon("GameClient/image/continue.png"));
    private JLabel fishLabel = new JLabel(new ImageIcon("GameClient/image/map/peng_item.png"));
    private JLabel shellLabel = new JLabel(new ImageIcon("GameClient/image/map/harp_item.png"));

    private JLabel starLabel1 = new JLabel();
    private JLabel starLabel2 = new JLabel();
    private JLabel starLabel3 = new JLabel();
    private ImageIcon emptyStar = new ImageIcon("GameClient/image/star_empty.png");
    private ImageIcon fullStar = new ImageIcon("GameClient/image/star_full.png");

    private ImageIcon yesImage = new ImageIcon("GameClient/image/yes.png");
    private ImageIcon noImage = new ImageIcon("GameClient/image/no.png");
    private ImageIcon yesHoverImage = new ImageIcon(applyColorFilter(yesImage, Color.decode("#B4FDFF")));
    private ImageIcon noHoverImage = new ImageIcon(applyColorFilter(noImage, Color.decode("#B4FDFF")));

    private JButton yesButton = new JButton(yesImage);
    private JButton noButton = new JButton(noImage);

    private JLabel fishCountLabel = new JLabel();
    private JLabel shellCountLabel = new JLabel();
    private Font font_regular20 = new Font("Galmuri9 Regular", Font.PLAIN, 20);

    public GameClearPanel(int fishCount, int shellCount){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setVisible(true);
        starLabel1.setBounds(301, 143, 50, 50);
        starLabel2.setBounds(389, 120, 50, 50);
        starLabel3.setBounds(478, 143, 50, 50);
        gameClearLabel.setBounds(239, 228, 347, 50);
        fishLabel.setBounds(344, 298, 40, 40);
        shellLabel.setBounds(344, 341, 40, 40);
        fishCountLabel.setBounds(409, 311, 200, 20);
        shellCountLabel.setBounds(409, 353, 200, 20);
        continueLabel.setBounds(334, 405, 159, 18);
        yesButton.setBounds(334, 441, 37, 15);
        noButton.setBounds(457, 441, 31, 15);

        yesButton.setBorderPainted(false);
        yesButton.setContentAreaFilled(false);
        noButton.setBorderPainted(false);
        noButton.setContentAreaFilled(false);

        fishCountLabel.setText("X   " + fishCount);
        shellCountLabel.setText("X   " + shellCount);
        fishCountLabel.setFont(font_regular20);
        fishCountLabel.setForeground(Color.WHITE);
        shellCountLabel.setFont(font_regular20);
        shellCountLabel.setForeground(Color.WHITE);

        updateStars(fishCount, shellCount);

        add(gameClearLabel);
        add(starLabel1);
        add(starLabel2);
        add(starLabel3);
        add(fishLabel);
        add(shellLabel);
        add(fishCountLabel);
        add(shellCountLabel);
        add(continueLabel);
        add(yesButton);
        add(noButton);

        yesButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

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

    // 별 개수를 업데이트하는 메서드
    public void updateStars(int fishCount, int shellCount) {
        int total = fishCount + shellCount;

        if (total >= 10) {
            starLabel1.setIcon(fullStar);
            starLabel2.setIcon(fullStar);
            starLabel3.setIcon(fullStar);
        } else if (total >= 6) {
            starLabel1.setIcon(fullStar);
            starLabel2.setIcon(fullStar);
            starLabel3.setIcon(emptyStar);
        } else {
            starLabel1.setIcon(fullStar);
            starLabel2.setIcon(emptyStar);
            starLabel3.setIcon(emptyStar);
        }
    }
}
