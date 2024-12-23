import java.io.*;
import java.net.Socket;

import javax.swing.JOptionPane;

import Map.Item;

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
                MovingInfo mi = null;
                try {
                    obcm = ois.readObject();
                    System.out.println("obcm read success");
                } catch (EOFException e) {
                    System.out.println("스트림 끝에 도달했습니다.");
                    break; // EOFException 발생 시 루프 종료
                }catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
                if (obcm == null) {
                    System.out.println("obcm is null!");
                    break;
                }
                if (obcm instanceof ChatMsg) {
                    cm = (ChatMsg) obcm;
                    msg = String.format("[%s] %s", cm.getUserName(), cm.getData());
                    System.out.println(msg);
                } else if (obcm instanceof MovingInfo) {
//					System.out.println("obcm을 제대로 받음");
                    mi = (MovingInfo) obcm;
                } else
                    continue;
                if (cm != null) {
                    switch (cm.getCode()) {
                        case "100": // 서버 접속 결과 - allow,deny
                            System.out.println(cm.getData() + " 데이터입니다");
                            String loginResult = cm.getData().split(" ")[0];
                            System.out.println("loginResult = " + loginResult + "로그인 성공 여부");
                            if (loginResult.equals(ALLOW_LOGIN_MSG)) {
                                ClientFrame.isGameScreen = true;
                                //isLogin = true;
                                System.out.println(cm.getData());

                                switch (cm.getData().split(" ")[1]) {
                                    case "1":
                                        if (playerCharacter == 0) // 처음 입장한 플레이어인 경우
                                            playerCharacter = 1;
                                        ClientFrame.waitingPlayerNum = 1;
                                        ClientFrame.playerNames.add(userName);
                                        break;
                                    case "2":
                                        String[] playerNames = cm.getData().split(" ")[2].split("//");
                                        if (playerCharacter == 0) {// 2번째로 입장한 플레이어인 경우
                                            playerCharacter = 2;
                                            for (int i = 0; i < playerNames.length; i++)
                                                ClientFrame.playerNames.add(playerNames[i]);
                                        } else { // 대기하고 있던 플레이어인 경우
                                            ClientFrame.playerNames.add(playerNames[1]);
                                        }
                                        ClientFrame.waitingPlayerNum = 2;
                                        break;
                                }

                                System.out.println(userName + " : " + playerCharacter + "번 캐릭터");
                                ClientFrame.userNum = playerCharacter;

                                ClientFrame.isChanged = true; // 화면 변화가 필요함
                                ClientFrame.isGameScreen = true; // 게임 대기화면으로 변화

                            } else if (loginResult.equals(DENY_LOGIN_MSG)) {
                                ClientFrame.isGameScreen = false;
                                JOptionPane.showMessageDialog(null, "서버 인원 초과");
                                return;
                            }
                            break;

                        case "200":
                            ChatPanel.appendText(cm.getUserName(), cm.getData());
                            break;

                        case "300": //게임 스타트
                            System.out.println("----------게임 스타트---------");
                            ClientFrame.isWaitScreen = false;
                            ClientFrame.isChanged = true;
                            ClientFrame.isPlayingScreen = true;
                            isPlayingGame = true;
                            break;
                        case "301": //다음 스테이지 진행
                            System.out.println("----------다음 스테이지 진행---------");
                            ClientFrame.isChanged = true;
                            ClientFrame.isNextStage = true;
                            break;
                        case "302": // 게임 오버 처리
                            System.out.println("----------게임 Retry---------");
                            ClientFrame.isChanged = true;
                            ClientFrame.isRetry = true;
                            break;
                        case "550":
                            if (cm.getObjType().equals("ITEM")) {
                                int itemIdx = cm.getObjIdx();  // 아이템의 인덱스를 받아오기
                                Item item = GamePlayPanel.getItemByIndex(itemIdx);  // 아이템 객체 찾기

                                if (item != null) {
                                    // mapNumber로 물고기인지 조개인지 구분
                                    if (item.getMapNumber() % 2 == 0) {
                                        GamePlayPanel.removeItem(itemIdx);
                                        ClientFrame.harpSealItemCount++;
                                    } else {  // 펭귄 아이템(조개)
                                        GamePlayPanel.removeItem(itemIdx);
                                        ClientFrame.penguinItemCount++;
                                    }
                                } else {
                                    System.out.println("아이템을 찾을 수 없습니다.");
                                }
                            } else if (cm.getObjType().equals("SWITCH_ON")) {
                                GamePlayPanel.switchOn(cm.getObjIdx());
                                GamePlayPanel.moveButtonBlocksDown(); // ButtonBlock 내리기
                            } else if (cm.getObjType().equals("SWITCH_OFF")) {
                                GamePlayPanel.switchOff(cm.getObjIdx());
                                GamePlayPanel.moveButtonBlocksUp(); // ButtonBlock 올리기
                            }
                            break;

                        case "600":
                            if (cm.getData().equals("GameOver")) {
                                ClientFrame.gameScreenPanel.setDieImage();
                                isPlayingGame = false;
                            }
                            break;
                    }
                } else if (mi != null) {
//					System.out.println(mi);
//					System.out.println("받은 데이터: "+mi.getPosX()+ mi.getPosY()+ mi.getType());
                    if (ClientFrame.gameScreenPanel != null)
                        ClientFrame.gameScreenPanel.setMovingInfo(mi.getPosX(), mi.getPosY(), mi.getType());
                    //break;
                }

            } catch (IOException e) {
                try {
                    System.out.println("e1");
                    e.printStackTrace();
                    ois.close();
                    oos.close();
                    socket.close();
                    ClientFrame.net = null;
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
            ClientFrame.net = null;
        } catch (Exception ee) {
            System.out.println("exitRoom ee");
        } // catch문 끝
        this.interrupt();
    }

    public static void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
        try {
            oos.writeObject(ob);
        } catch (IOException e) {
        }
    }
}