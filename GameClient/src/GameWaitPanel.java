import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameWaitPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("GameClient/image/background_dark.png").getImage();
    private ImageIcon backImage = new ImageIcon("GameClient/image/back.png");
    private ImageIcon backHoverImage = new ImageIcon(applyColorFilter(backImage, Color.decode("#B4FDFF")));
    private ImageIcon startImage = new ImageIcon("GameClient/image/start.png");

    private ImageIcon penguinImage = new ImageIcon("GameClient/image/penguin_medium.png");
    private ImageIcon sealImage = new ImageIcon("GameClient/image/seal_medium.png");
    private ImageIcon[] loadingImage = {
            new ImageIcon("GameClient/image/loading.png"),
            new ImageIcon("GameClient/image/loading1.png"),
            new ImageIcon("GameClient/image/loading2.png"),
            new ImageIcon("GameClient/image/loading3.png")
    };
    private ImageIcon player1Image = new ImageIcon("GameClient/image/1.png");
    private ImageIcon player2Image = new ImageIcon("GameClient/image/2.png");

    private JLabel penguinLabel = new JLabel(penguinImage);
    private JLabel sealLabel = new JLabel(sealImage);
    private JLabel loadingLabel = new JLabel(loadingImage[0]);
    private JLabel playerLabel = new JLabel(player1Image);


    private JButton backButton = createImageButton(backImage, 39, 45, 43, 30);
    private JButton startButton = createImageButton(startImage, 445, 417, 110, 29);
    private ImageIcon startHoverImage = new ImageIcon(applyColorFilter(startImage, Color.decode("#B4FDFF")));

    private Timer jumpTimer, loadingTimer;
    private int penguinJumpHeight = 0, sealJumpHeight = 20;
    private int penguinJumpDirection = 1, sealJumpDirection = -1;
    private final int MAX_JUMP_HEIGHT = 20; // 최대 점프 높이

    public GameWaitPanel(GameClientFrame frame) {
        setLayout(null);

        add(backButton);
        add(penguinLabel);
        add(sealLabel);
        add(loadingLabel);
        changeWaitPlayerNum();
        penguinLabel.setBounds(425, 197, 80, 80);
        sealLabel.setBounds(505, 186, 70, 70);
        loadingLabel.setBounds(434, 282, loadingImage[1].getIconWidth(), loadingImage[1].getIconHeight());  // 위치와 크기 설정
        startJumpTimer();
        startLoadingTimer();
        changePlayerNum(GameClientFrame.waitingPlayerNum);

        backButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                GameClientFrame.isGameMainScreen = true;  // 게임 규칙 화면으로 상태 변경
                frame.updateScreen();  // 화면 전환 메서드 호출
            }

            public void mouseEntered(MouseEvent e) {
                backButton.setIcon(backHoverImage);
            }

            public void mouseExited(MouseEvent e) {
                backButton.setIcon(backImage);
            }
        });

    }


    // 로딩 아이콘을 1초마다 변경하는 타이머
    private void startLoadingTimer() {
        loadingTimer = new Timer(500, e -> changeLoadingIcon());
        loadingTimer.start();
    }


    private void changeLoadingIcon() {
        int currentIconIndex = (java.util.Arrays.asList(loadingImage).indexOf(loadingLabel.getIcon()) + 1) % loadingImage.length;
        loadingLabel.setIcon(loadingImage[currentIconIndex]);
        loadingLabel.setBounds(434, 282, loadingImage[currentIconIndex].getIconWidth(), loadingImage[currentIconIndex].getIconHeight());
    }

    // 타이머를 시작하여 1초마다 펭귄과 물범의 y 좌표를 변경
    private void startJumpTimer() {
        jumpTimer = new Timer(100, e -> makeJump()); // 100ms마다 점프 애니메이션 실행
        jumpTimer.start();  // 타이머 시작
    }

    // 펭귄과 물범의 점프 애니메이션을 실행하는 메서드
    private void makeJump() {
        if (penguinJumpDirection == 1) {
            penguinJumpHeight += 4; // 올라갈 때 y 좌표 증가
            if (penguinJumpHeight >= MAX_JUMP_HEIGHT) {
                penguinJumpDirection = -1; // 최고 높이에 도달하면 내려가도록 설정
            }
        } else {
            penguinJumpHeight -= 4; // 내려갈 때 y 좌표 감소
            if (penguinJumpHeight <= 0) {
                penguinJumpDirection = 1; // 바닥에 도달하면 올라가도록 설정
            }
        }

        // 물범의 점프 처리
        if (sealJumpDirection == 1) {
            sealJumpHeight += 4; // 올라갈 때 y 좌표 증가
            if (sealJumpHeight >= MAX_JUMP_HEIGHT) {
                sealJumpDirection = -1; // 최고 높이에 도달하면 내려가도록 설정
            }
        } else {
            sealJumpHeight -= 4; // 내려갈 때 y 좌표 감소
            if (sealJumpHeight <= 0) {
                sealJumpDirection = 1; // 바닥에 도달하면 올라가도록 설정
            }
        }

        // 점프한 후의 새로운 위치 계산하여 설정
        penguinLabel.setBounds(426, 197 - penguinJumpHeight, 80, 80);
        sealLabel.setBounds(506, 207 - sealJumpHeight, 70, 70);
    }

    public void changeWaitPlayerNum() {
        changePlayerNum(GameClientFrame.waitingPlayerNum); // gameRoom에 입장한 플레이어 수
        if (GameClientFrame.waitingPlayerNum == 2) {
            addGameStartBtn();
        }
        this.repaint();
    }

    public void changePlayerNum(int waitingPlayerNum) { // 참여한 플레이어 수 이미지 변경
        switch (waitingPlayerNum) {
            case 1: {
                playerLabel.setIcon(player1Image);
                playerLabel.setBounds(467, 333, 82, 19);
                add(playerLabel);
                break;
            }
            case 2:
                playerLabel.setIcon(player2Image);
                playerLabel.setBounds(467, 333, 82, 19);
                add(playerLabel);
                break;
        }
        repaint();
    }

    public void addGameStartBtn() {
        if (GameClientFrame.userNum == 1) {
            startButton.addMouseListener(new MouseAdapter() { // game start 버튼 눌렀을 때 동작
                public void mousePressed(MouseEvent e) {
                    ChatMsg obcm = new ChatMsg(GameClientFrame.userName, "300", "★Game Start!★"); // gameRoom 입장 시도
                    ListenNetwork.SendObject(obcm);
                    startButton.setEnabled(false);
                }

                public void mouseEntered(MouseEvent e) {
                    startButton.setIcon(startHoverImage);
                }

                public void mouseExited(MouseEvent e) {
                    startButton.setIcon(startImage);
                }

            });
            add(startButton);
        }
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
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
