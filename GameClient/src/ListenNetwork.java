import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

//Server Message를 수신해서 화면에 표시
public class ListenNetwork extends Thread {
    String ip_addr = "127.0.0.1";
    int port;
    int playerCharacter = 0;

    private static final String ALLOW_LOGIN_MSG = "ALLOW";
    private static final String DENY_LOGIN_MSG = "DENY";

    public static Socket socket;  // 연결소켓
    public static InputStream is;
    public static OutputStream os;
    public static ObjectInputStream ois;
    public static ObjectOutputStream oos;

    public static boolean isPlayingGame = false;
    private String userName = "";

    public ListenNetwork(String userName, int port) {
        this.userName = userName;
        this.port = port;
        System.out.println(ip_addr);
        System.out.println(port);
        try {
            socket = new Socket(ip_addr, port);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            ChatMsg obcm = new ChatMsg(userName, "100", ""); // gameRoom 입장 시도
            SendObject(obcm);
        } catch (NumberFormatException | IOException error) {
            JOptionPane.showMessageDialog(null, "Server Can't connect");
        }
    }

    public void run() {
        while (true) {
            System.out.println("메시지 대기중...");
            try {
                Object obcm = null;
                String msg = null;
                ChatMsg cm = null;
                //MovingInfo mi = null;
                try {
                    obcm = ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
                if (obcm == null) {
                    break;
                }
                if (obcm instanceof ChatMsg) {
                    cm = (ChatMsg) obcm;
                    msg = String.format("[%s] %s", cm.getUserName(), cm.getData());
                    System.out.println(msg);
                }
                if(cm != null) {
                    switch (cm.getCode()) {
                        case "100": // 서버 접속 결과 - allow,deny
                            System.out.println(cm.getData());
                            String loginResult = cm.getData().split(" ")[0];
                            System.out.println("loginResult = " + loginResult);
                            if (loginResult.equals(ALLOW_LOGIN_MSG)) {
                                GameClientFrame.isWaitScreen = true;
                                switch (cm.getData().split(" ")[1]) {
                                    case "1":
                                        if (playerCharacter == 0) // 처음 입장한 플레이어인 경우
                                            playerCharacter = 1;
                                        GameClientFrame.waitingPlayerNum = 1;
                                        GameClientFrame.playerNames.add(userName);
                                        break;
                                    case "2":
                                        String[] playerNames = cm.getData().split(" ")[2].split("//");
                                        if (playerCharacter == 0) {// 2번째로 입장한 플레이어인 경우
                                            playerCharacter = 2;
                                            for (int i = 0; i < playerNames.length; i++)
                                                GameClientFrame.playerNames.add(playerNames[i]);
                                        } else { // 대기하고 있던 플레이어인 경우
                                            GameClientFrame.playerNames.add(playerNames[1]);
                                        }
                                        GameClientFrame.waitingPlayerNum = 2;
                                        break;
                                }

                                System.out.println(userName + " : " + playerCharacter + "번 캐릭터");
                                GameClientFrame.userNum = playerCharacter;

                                GameClientFrame.isChanged = true; // 화면 변화가 필요함
                                GameClientFrame.isWaitScreen = true; // 게임 대기화면으로 변화
                            } else if (loginResult.equals(DENY_LOGIN_MSG)) {
                                GameClientFrame.isWaitScreen = false;
                                JOptionPane.showMessageDialog(null, "해당 서버는 가득 찼습니다. 다른 서버를 선택해주세요.");
                                return;
                            }
                            break;
                    }
                }
            } catch (IOException e) {
                try {
                    System.out.println("e1");
                    e.printStackTrace();
                    ois.close();
                    oos.close();
                    socket.close();
                    GameClientFrame.net = null;
                    break;
                } catch (Exception ee) {
                    System.out.println("e2");
                    break;
                } // catch문 끝
            } // 바깥 catch문끝
        }
    }

    public void exitRoom() {
        ChatMsg obcm = new ChatMsg(this.userName, "999");
        System.out.println(obcm.getUserName() + ", " + obcm.getCode() + ", " + obcm.getData());
        SendObject(obcm);
        try {
            ois.close();
            oos.close();
            socket.close();
            GameClientFrame.net = null;
        } catch (Exception ee) {
            System.out.println("exitRoom ee");
        } // catch문 끝
        this.interrupt();
    }

    public static void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos.writeObject(ob);
        } catch (IOException e) {
            // textArea.append("메세지 송신 에러!!\n");
        }
    }
}