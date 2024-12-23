import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.awt.event.ActionEvent;

public class ServerFrame extends JFrame {
    private Image backgroundImage = new ImageIcon("GameClient/image/background/background_mini.png").getImage();
    private ImageIcon portImage = new ImageIcon("GameClient/image/port.png");
    private ImageIcon serverStartImage = new ImageIcon("GameClient/image/server_start.png");
    private ImageIcon serverRunningImage = new ImageIcon("GameClient/image/server_running.png");
    private JButton serverStartButton = new JButton(serverStartImage);

    private static final long serialVersionUID = 1L;
    private int port;
    private JPanel contentPane;
    JTextArea textArea; // 로그를 표시할 텍스트 영역
    private JTextField txtPortNumber;
    private ServerSocket socket; // 서버소켓
    private Socket client_socket; // accept() 에서 생성된 client 소켓
    private Room room; //게임 룸

    private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
    private static final String ALLOW_LOGIN_MSG = "ALLOW";
    private static final String DENY_LOGIN_MSG = "DENY";
    private Font font_bold = new Font("Galmuri11 Bold", Font.PLAIN, 16);
    private Font font_regular = new Font("Galmuri9 Regular", Font.PLAIN, 12);

    public ServerFrame(int port) {
        this.port = port;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        //전체
        contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        contentPane.setPreferredSize(new Dimension(360, 450));
        pack();

        //스크롤팬
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 56, 340, 370);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        contentPane.add(scrollPane);

        //port
        JLabel lblNewLabel = new JLabel(portImage);
        lblNewLabel.setBounds(18, 23, 47, 15);
        contentPane.add(lblNewLabel);

        //메시지 뜨는 곳
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(font_regular);
        scrollPane.setViewportView(textArea);

        txtPortNumber = new JTextField();
        txtPortNumber.setEditable(false);
        txtPortNumber.setText(Integer.toString(port));
        txtPortNumber.setBackground(new Color(0, 0, 0, 0)); // 투명 배경 설정
        txtPortNumber.setForeground(Color.WHITE); // 텍스트 색을 하얀색으로 설정
        txtPortNumber.setBounds(73, 23, 43, 20);
        contentPane.add(txtPortNumber);
        txtPortNumber.setColumns(10);
        txtPortNumber.setBorder(null); // 테두리 제거
        txtPortNumber.setFont(font_bold); // 텍스트 크기 설정

        contentPane.add(serverStartButton);
        serverStartButton.setBounds(237, 21, 109, 15);
        serverStartButton.setBorderPainted(false);
        serverStartButton.setContentAreaFilled(false);  // 배경 없애기

        // 서버 시작 버튼
        serverStartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    socket = new ServerSocket(port); // 서버 소켓 열기
                } catch (NumberFormatException | IOException e1) {
                    e1.printStackTrace();
                }
                serverStartButton.setIcon(serverRunningImage);
                serverStartButton.setBounds(195, 21, 154, 18);
                serverStartButton.setEnabled(false); // 서버를 더이상 실행시키지 못 하게
                txtPortNumber.setEnabled(false); // 더이상 포트번호 수정 불가

                room = new Room(socket); //단일 서버
                room.start();
                AcceptServer accept_server = new AcceptServer(socket);
                accept_server.start();
            }
        });

        setVisible(true);
    }

    // 사용자가 접속을 기다리며 client 소켓을 받는 클래스
    class AcceptServer extends Thread {
        ServerSocket socket;
        private Vector UserVec = new Vector(); // 연결된 사용자를 저장할 벡터

        public AcceptServer(ServerSocket socket) {
            this.socket = socket;
        }

        public ServerSocket getSocket() {
            return socket;
        }

        public Vector getUserVec() {
            return UserVec;
        }

        // 서버는 사용자가 들어오면 대기하며 소켓 수신 후 처리
        public void run() {
            while (true) { // 사용자 접속을 계속해서 받기 위해 while문
                try {
                    AppendText("Waiting new clients ..." + socket.getLocalPort());
                    client_socket = socket.accept(); // 새로운 클라이언트가 연결될 때까지 대기
                    AppendText("새로운 유저 from " + client_socket);
                    UserService new_user = new UserService(client_socket, this); // 새로운 사용자 객체 생성
                    UserVec.add(new_user); // 사용자 벡터에 추가
                    new_user.start(); // 사용자 스레드 시작
                    System.out.println("현재 유저 수 " + UserVec.size());
                } catch (IOException e) {
                    AppendText("accept() error");
                }
            }
        }
    }

    // 로그 메시지를 출력하는 메서드
    public void AppendText(String str) {
        textArea.append("메시지 : " + str + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    // 서버에서 전송하는 객체의 내용을 출력하는 메서드
    public void AppendObject(ChatMsg msg) {
        textArea.append("Code : " + msg.code + "\n");
        textArea.append("ID : " + msg.UserName + "\n");
        textArea.append("Data : " + msg.data + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    // 클라이언트의 상태를 관리하는 UserService 클래스
    class UserService extends Thread {
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        private Socket client_socket;
        private ServerSocket socket;
        private Vector user_vc;
        public String UserName = "";
        public String UserStatus;

        // 클라이언트 연결 설정, 입력 및 출력 스트림 초기화
        public UserService(Socket client_socket, AcceptServer acceptServer) {
            this.client_socket = client_socket;
            this.socket = acceptServer.getSocket();
            this.user_vc = acceptServer.getUserVec();
            try {
                oos = new ObjectOutputStream(client_socket.getOutputStream());
                oos.flush();
                ois = new ObjectInputStream(client_socket.getInputStream());
            } catch (Exception e) {
                AppendText("userService error");
            }
        }

        // 로그인 시도
        public boolean Login() {
            if (room.enterRoom(UserName)) {
                room.getUserVec().add(this); // 사용자 추가
                AppendText(UserName + " 입장.");
                AppendText("유저 " + room.getUserVec().size() + " / 2");
                return true;
            } else {
                AppendText(UserName + " 입장 거절 당함.");
                return false;
            }
        }

        // 게임에 입장한 사용자 수
        public int getPlayerNum() { // gameRoom에 입장한 플레이어 수
            return room.getUserVec().size();
        }

        public void Logout() {
            System.out.println("LOGOUT 중");
            room.getUserVec().remove(this);
            System.out.println(room.getUserVec().size());
            user_vc.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
            AppendText("[" + UserName + "] 퇴장. 남은 유저 수 " + user_vc.size());
        }

        // 모든 User들에게 Object를 방송
        public void WriteAllObject(Object ob) {
            Vector gameRoomUserVec = room.getUserVec();
            int userVecSize = gameRoomUserVec.size();
            for (int i = 0; i < userVecSize; i++) {
                UserService user = (UserService) gameRoomUserVec.elementAt(i);
                if (user.UserStatus == "O")
                    user.WriteOneObject(ob);
            }
        }

        public void WriteOtherObject(Object ob) {
            Vector gameRoomUserVec = room.getUserVec();
            int userVecSize = gameRoomUserVec.size();
            for (int i = 0; i < userVecSize; i++) {
                UserService user = (UserService) gameRoomUserVec.elementAt(i);
                if (user != this) {
                    user.WriteOneObject(ob);
                }
            }
        }

        public void WriteOneObject(Object ob) {
            try {
                oos.writeObject(ob);
            } catch (IOException e) {
                AppendText("oos.writeObject(ob) error");
                try {
                    ois.close();
                    oos.close();
                    client_socket.close();
                    client_socket = null;
                    ois = null;
                    oos = null;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                Logout();
            }
        }

        public void run() {
            while (true) { // 사용자 접속을 계속해서 받기 위해 while문
                try {
                    Object obcm = null;
                    String msg = null;
                    ChatMsg cm = null;
                    MovingInfo mi = null;

                    if (socket == null)
                        break;
                    try {
                        obcm = ois.readObject();
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }
                    if (obcm == null) {
                        break;
                    }
                    if (obcm instanceof ChatMsg) {
                        cm = (ChatMsg) obcm;
                        AppendObject(cm);
                    } else if (obcm instanceof MovingInfo) {
                        mi = (MovingInfo) obcm;
                    } else {
                        continue;
                    }
                    if (cm != null) {
                        if (cm.code.matches("100")) { // login
                            System.out.println(cm.toString());
                            AppendText("로그인 요청 받음");
                            UserName = cm.UserName;
                            UserStatus = "O"; // Online 상태
                            if (Login()) { // 로그인 성공시
                                int waitingPlayerNum = getPlayerNum();
                                switch (waitingPlayerNum) {
                                    case 1:
                                        obcm = new ChatMsg("SERVER", "100", ALLOW_LOGIN_MSG + " " + waitingPlayerNum);
                                        WriteAllObject(obcm);
                                        break;
                                    case 2:
                                        obcm = new ChatMsg("SERVER", "100", ALLOW_LOGIN_MSG + " " + waitingPlayerNum + " " + getUserNames());
                                        WriteAllObject(obcm);
                                        break;
                                }

                            } else { // 로그인 실패 시
                                obcm = new ChatMsg("SERVER", "100", DENY_LOGIN_MSG);
                                oos.writeObject(obcm);
                                break; //스레드 종료
                            }
                        } else if (cm.code.matches("200")) {
                            obcm = new ChatMsg(cm.getUserName(), "200", cm.getData());
                            WriteOtherObject(obcm);
                        } else if (cm.code.matches("300")) {
                            obcm = new ChatMsg("[SERVER]", "300", "게임을 시작합니다.");
                            WriteAllObject(obcm);
                        } else if (cm.code.matches("550")) {
                            obcm = new ChatMsg("550", cm.objIdx, cm.objType);
                            WriteOtherObject(obcm);
                        } else if (cm.code.matches("301")) {
                            obcm = new ChatMsg("[SERVER]", "301", "다음 스테이지로 진행합니다.");
                            WriteAllObject(obcm);
                            obcm = new ChatMsg("[SERVER]", "301", "새로운 맵을 로딩합니다.");
                            WriteOtherObject(obcm); // 해당 명령어를 다른 클라이언트에게도 알림
                        } else if (cm.code.matches("302")) {
                            System.out.println("게임 RETRY 메시지 전송");
                            obcm = new ChatMsg("[SERVER]", "302", "게임 초기화 후 다시 진행됩니다..");
                            WriteAllObject(obcm);
                        } else if (cm.code.matches("600")) {
                            System.out.println("cm.getData = " + cm.getData());
                            obcm = new ChatMsg(cm.getUserName(), "600", cm.getData());
                            WriteOtherObject(obcm);
                        }
                    }
                    else if (mi != null) {
                        WriteOtherObject(obcm);
                    }

                } catch (IOException e) {
                    AppendText("ois.readObject() error");
                    try {
                        ois.close();
                        oos.close();
                        client_socket.close();
                        Logout(); // 에러가난 현재 객체를 벡터에서 지운다
                        break;
                    } catch (Exception ee) {
                        break;
                    }
                }
            }
        }
    }
    public String getUserNames() {
        Vector userNames = room.getUserNameVec();
        return userNames.get(0) + "//" + userNames.get(1);
    }
}