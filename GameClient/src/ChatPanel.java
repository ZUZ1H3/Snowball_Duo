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
    private JButton sendBtn;
    private JPanel chatInputPane;

    private JLabel playerListLabel = new JLabel("[ 참여자 목록 ]");
    private JLabel penguinLabel = new JLabel("펭펭 : ");
    private JLabel sealLabel = new JLabel("하푸 : ");
    private JLabel player1Label;
    private JLabel player2Label;

    private JLabel timerLabel;
    private Timer timer;
    private int seconds = 0;
    private Font font_bold = new Font("Galmuri11 Bold", Font.PLAIN, 40);
    private Font font_regular12 = new Font("Galmuri9 Regular", Font.PLAIN, 12);
    private Font font_regular10 = new Font("Galmuri9 Regular", Font.PLAIN, 10);
    public ChatPanel() {
        setSize(200, 600);
        setLayout(null);
        setChatHistory();
        setChatInput();
        setPlayerList();
        setTimer();

        //채팅 전송 버튼
        sendBtn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                String text = chatInput.getText();
                if (ClientFrame.waitingPlayerNum == 2) { //접속한 사용자가 있을 때
                    ChatMsg msg = new ChatMsg(ClientFrame.userName, "200", text);
                    ListenNetwork.SendObject(msg);
                }

                appendText(ClientFrame.userName, text); //본인 채팅 히스토리에 추가
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

    // 채팅 입력창과 전송 버튼 설정
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

    // 참여자 목록에 대한 레이블 설정
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

    // 타이머 설정 메서드
    public void setTimer() {
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(font_bold);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(42, 33, 160, 53); // 위치 설정
        add(timerLabel);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seconds++;
                int minute = seconds / 60;
                int second = seconds % 60;
                timerLabel.setText(String.format("%02d:%02d", minute, second));
            }
        });
        timer.start();
    }

    public void changePlayerList(ArrayList<String> playerNames) {
        for (int i = 0; i < playerNames.size(); i++) {
            addPlayerLabel(i + 1, playerNames.get(i));
        }
        this.repaint();
    }

    // 채팅 내용 추가하는 메서드
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
