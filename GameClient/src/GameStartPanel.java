import javax.swing.*;
import java.awt.*;

public class GameStartPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("GameClient/image/background.png").getImage();
    private Image penguinImage = new ImageIcon("GameClient/image/penguin.png").getImage();
    private Image sealImage = new ImageIcon("GameClient/image/seal.png").getImage();
    private Image nameImage = new ImageIcon("GameClient/image/name.png").getImage();
    private Image serverImage = new ImageIcon("GameClient/image/server.png").getImage();
    private Image rectangleImage = new ImageIcon("GameClient/image/rectangle.png").getImage();

    private ImageIcon startImage = new ImageIcon("GameClient/image/start.png");

    private JButton startButton = createImageButton(startImage, 445, 328,  110, 29);

    public GameStartPanel() {
        setLayout(null);

        add(startButton);  // 버튼 패널에 추가
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
        g.drawImage(penguinImage, 41, 305, 280, 280, this);
        g.drawImage(sealImage, 269, 336, 260, 260, this);
        g.drawImage(nameImage, 354, 205, 66, 22, this);
        g.drawImage(serverImage, 327, 259, 92, 22, this);
        g.drawImage(rectangleImage, 436, 197, 237, 39, this);
        g.drawImage(rectangleImage, 436, 251, 237, 39, this);



    }
}
