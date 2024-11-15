import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class GameWaitPanel extends JPanel {
    private Image backgroundImage = new ImageIcon("GameClient/image/background_dark.png").getImage();
    private ImageIcon backImage = new ImageIcon("GameClient/image/back.png");
    private JButton backButton = createImageButton(backImage, 39, 45, 43, 30);
    private ImageIcon backHoverImage = new ImageIcon(applyColorFilter(backImage, Color.decode("#B4FDFF")));

    private ImageIcon penguinImage = new ImageIcon("GameClient/image/penguin_medium.png");
    private ImageIcon sealImage = new ImageIcon("GameClient/image/seal_medium.png");
    private ImageIcon loadingImage = new ImageIcon("GameClient/image/loading.png");
    private ImageIcon loading1Image = new ImageIcon("GameClient/image/loading1.png");
    private ImageIcon loading2Image = new ImageIcon("GameClient/image/loading2.png");
    private ImageIcon loading3Image = new ImageIcon("GameClient/image/loading3.png");
    private ImageIcon player1Image = new ImageIcon("GameClient/image/1.png");
    private ImageIcon player2Image = new ImageIcon("GameClient/image/2.png");

    private JLabel penguinLabel = new JLabel(penguinImage);
    private JLabel sealLabel = new JLabel(sealImage);
    private JLabel loadingLabel = new JLabel(loadingImage);
    private JLabel playerLabel = new JLabel(player1Image);

    private Timer jumpTimer, loadingTimer;
    private int penguinJumpDirection = 1; // 1이면 올라가고 -1이면 내려감
    private int sealJumpDirection = -1; // 1이면 올라가고 -1이면 내려감
    private int penguinJumpHeight = 0; // 펭귄의 현재 점프 높이
    private int sealJumpHeight = 20; // 물범의 현재 점프 높이
    private final int MAX_JUMP_HEIGHT = 20; // 최대 점프 높이

    public GameWaitPanel(GameClientFrame frame) {
        setLayout(null);

        add(backButton);
        // 펭귄 이미지
        penguinLabel.setIcon(penguinImage);
        penguinLabel.setBounds(425, 197, 80, 80);  // 위치와 크기 설정
        add(penguinLabel);

        // 물범 이미지
        sealLabel.setIcon(sealImage);
        sealLabel.setBounds(505, 186, 70, 70);  // 위치와 크기 설정
        add(sealLabel);

        loadingLabel.setIcon(loading1Image);
        loadingLabel.setBounds(434, 282, loading1Image.getIconWidth(), loading1Image.getIconHeight());  // 위치와 크기 설정
        add(loadingLabel);

        startJumpTimer();
        startLoadingTimer();

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
                backButton.setIcon(backImage);
            }
        });

    }

    // 로딩 아이콘을 1초마다 변경하는 타이머
    private void startLoadingTimer() {
        loadingTimer = new Timer(500, e -> changeLoadingIcon());
        loadingTimer.start();
    }

    // 로딩 아이콘을 순차적으로 변경하는 메서드
    private void changeLoadingIcon() {
        // 현재 로딩 아이콘을 변경
        if (loadingLabel.getIcon() == loading1Image) {
            loadingLabel.setIcon(loading2Image);
            loadingLabel.setBounds(434, 282, loading2Image.getIconWidth(), loading2Image.getIconHeight());
        } else if (loadingLabel.getIcon() == loading2Image) {
            loadingLabel.setIcon(loading3Image);
            loadingLabel.setBounds(434, 282, loading3Image.getIconWidth(), loading3Image.getIconHeight());
        } else if (loadingLabel.getIcon() == loading3Image) {
            loadingLabel.setIcon(loadingImage);
            loadingLabel.setBounds(434, 282, loadingImage.getIconWidth(), loadingImage.getIconHeight());
        } else {
            loadingLabel.setIcon(loading1Image);
            loadingLabel.setBounds(434, 282, loading1Image.getIconWidth(), loading1Image.getIconHeight());
        }
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

    /*public void changePlayerNum(int waitingPlayerNum) { // 참여한 플레이어 수 이미지 변경
        switch (waitingPlayerNum) {
            case 1: {
                waitPlayerIcon = new ImageIcon(new ImageIcon(oneImgPath).getImage().getScaledInstance(waitPlayerLabel.getWidth(), waitPlayerLabel.getHeight(), Image.SCALE_SMOOTH));
                break;
            }
            case 2:
                waitPlayerIcon = new ImageIcon(new ImageIcon(twoImgPath).getImage().getScaledInstance(waitPlayerLabel.getWidth(), waitPlayerLabel.getHeight(), Image.SCALE_SMOOTH));
                break;
        }
        waitPlayerLabel.setIcon(waitPlayerIcon);
        add(waitPlayerLabel);
    }*/

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
