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
    int port_no;
    int roomId;
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

    public ListenNetwork(String userName, int port_no) {
        this.userName = userName;
        this.port_no = port_no;
        System.out.println(ip_addr);
        System.out.println(port_no);
        this.roomId = roomId;
        try {
            socket = new Socket(ip_addr, port_no);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            ChatMsg obcm = new ChatMsg(userName, "100", ""); // gameRoom 입장 시도
            SendObject(obcm);
        } catch (NumberFormatException | IOException error) {
            // TODO Auto-generated catch block
            //error.printStackTrace();
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
//					System.out.println("obcm read success");
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    break;
                }
                if (obcm == null) {
//					System.out.println("obcm is null!");
                    break;
                }
                if (obcm instanceof ChatMsg) {
                    cm = (ChatMsg) obcm;
                    msg = String.format("[%s] %s", cm.getUserName(), cm.getData());
                    System.out.println(msg);
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
        ChatMsg obcm = new ChatMsg(this.userName, roomId, "999");
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