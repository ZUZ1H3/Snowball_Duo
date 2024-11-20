import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ChatPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.decode("#ECFAFF");
    private Image backgroundImage = new ImageIcon("GameClient/image/background/background_chat.png").getImage();
    private JScrollPane chatHistoryScroll = new JScrollPane();
    public static JTextPane chatHistory;
    private JTextArea chatInput;
    private String name; // player 닉네임
    private JButton sendBtn;
    private JPanel chatInputPane;

    private JLabel playerListLabel = new JLabel("[ 참여자 목록 ]");
    private JLabel penguinLabel = new JLabel("펭펭 : ");
    private JLabel sealLabel = new JLabel("하푸 : ");
    private JLabel player1Label;
    private JLabel player2Label;

    private JLabel timerLabel; // 스톱워치 라벨
    private Timer timer;  // 스톱워치 타이머
    private int seconds = 0; // 경과 시간 (초)
    private Font font_bold = new Font("Galmuri11 Bold", Font.PLAIN, 40);
    private Font font_regular12 = new Font("Galmuri9 Regular", Font.PLAIN, 12);
    private Font font_regular10 = new Font("Galmuri9 Regular", Font.PLAIN, 10);
    public ChatPanel(String name) {
        this.name = name; // 초기화 추가
        setSize(200, 600);
        setLayout(null);
        setChatHistory();
        setChatInput();
        setPlayerList();
        setTimer();

        sendBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                String text = chatInput.getText();
                if (GameClientFrame.waitingPlayerNum == 2) { //접속한 사용자가 있을 때 - 네트워크 전송
                    ChatMsg msg = new ChatMsg(GameClientFrame.userName, "200", text);
                    ListenNetwork.SendObject(msg);
                }

                appendText(GameClientFrame.userName, text); //본인 채팅팬에 넣기
                chatInput.setText("");
            }
        });
    }

    public void setChatHistory() {
        chatHistoryScroll.setBounds(6, 166, 188, 340);
        chatHistoryScroll.getViewport().setBackground(Color.WHITE);
        chatHistoryScroll.setBorder(null);
        add(chatHistoryScroll);

        chatHistory = new JTextPane();
        chatHistory.setEditable(true);
        chatHistory.setFocusable(false);
        chatHistory.setFont(font_regular12);
        chatHistoryScroll.setViewportView(chatHistory);
        chatHistory.setBackground(BACKGROUND_COLOR);
    }

    public void setChatInput() {
        chatInputPane = new JPanel();
        chatInputPane.setBounds(6, 516, 188, 74);
        chatInputPane.setLayout(null);
        chatInputPane.setBackground(BACKGROUND_COLOR);

        chatInput = new JTextArea();
        chatInput.setLineWrap(true); // 자동 줄바꿈
        JScrollPane chattingInputScroll = new JScrollPane(chatInput);
        chattingInputScroll.setBounds(5, 5, 180, 40);
        chattingInputScroll.getViewport().setBackground(Color.WHITE);
        chattingInputScroll.setBorder(null);
        chatInput.setBackground(BACKGROUND_COLOR);

        chatInput.setBackground(BACKGROUND_COLOR);
        chatInput.setFont(font_regular12);
        chatInput.setCaretPosition(chatInput.getDocument().getLength());
        chatInputPane.add(chattingInputScroll);
        add(chatInputPane);

        sendBtn = new JButton("[ 전송 ]");
        sendBtn.setBounds(116, 50, 80, 20);
        sendBtn.setFont(font_regular10);
        chatInput.setFont(font_regular12);
        sendBtn.setBorderPainted(false);
        sendBtn.setContentAreaFilled(false);
        sendBtn.setOpaque(false);
        chatInputPane.add(sendBtn);
    }

    public void setPlayerList() {
        playerListLabel.setForeground(Color.WHITE);
        playerListLabel.setBounds(58, 90, 188, 17);
        playerListLabel.setFont(font_regular12);
        add(playerListLabel);
        penguinLabel.setForeground(Color.WHITE);
        penguinLabel.setBounds(15, 117, 58, 20);
        penguinLabel.setFont(font_regular12);
        add(penguinLabel);
        sealLabel.setForeground(Color.WHITE);
        sealLabel.setBounds(15, 137, 68, 20);
        sealLabel.setFont(font_regular12);
        add(sealLabel);
    }

    public void addPlayerLabel(int idx, String name) {
        switch (idx) {
            case 1:
                if (player1Label == null) {
                    player1Label = new JLabel(name);
                    player1Label.setForeground(Color.WHITE);
                    player1Label.setBounds(56, 117, 161, 20);
                    player1Label.setFont(font_regular12);
                    add(player1Label);
                }
                break;
            case 2:
                player2Label = new JLabel(name);
                player2Label.setForeground(Color.WHITE);
                player2Label.setBounds(56, 137, 154, 20);
                player2Label.setFont(font_regular12);
                add(player2Label);
                break;
        }
    }
    public void setTimer() {
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(font_bold);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(42, 33, 120, 53); // 위치 설정
        add(timerLabel);

        // 타이머 설정
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                int minute = seconds / 60;
                int second = seconds % 60;
                timerLabel.setText(String.format("%02d:%02d", minute, second));
            }
        });
        timer.start(); // 타이머 시작
    }

    public void changePlayerList(ArrayList<String> playerNames) {
        for (int i = 0; i < playerNames.size(); i++) {
            addPlayerLabel(i + 1, playerNames.get(i));
        }
        this.repaint();
    }

    public static void appendText(String userName, String msg) {
        msg = msg.trim();
        int len = chatHistory.getDocument().getLength();
        chatHistory.setCaretPosition(len);
        chatHistory.replaceSelection("[" + userName + "]:" + msg + "\n");
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
