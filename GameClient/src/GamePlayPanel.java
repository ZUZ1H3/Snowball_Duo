import javax.swing.*;

import Map.Block;
import Map.Barrier;
import Map.Item;
import Map.Door;
import Map.Button;
import Map.ButtonBlock;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GamePlayPanel extends JPanel implements Runnable {
    //화면 크기
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    //캐릭터 크기
    private final int CHARACTER_WIDTH = 34;
    private final int CHARACTER_HEIGHT = 35;

    int myWidth, myHeight;
    int opponentWidth, opponentHeight;

    private int stageNum = 1;

    private Map map;
    private ArrayList<Block> blocks = null;
    private static ArrayList<Barrier> barriers = null;
    private static ArrayList<Item> items = null;
    private static ArrayList<Door> doors = null;
    private static ArrayList<Button> buttons = null;
    private static ArrayList<ButtonBlock> buttonBlocks = null;

    //스위치 관련 변수
    public static boolean isOpponentSwitchOn = false;
    public static int opponentlastSwitchIdx = -1;

    public static boolean isMySwitchOn = false;
    public static int mylastOnSwitchIdx = -1;

    //키 어댑터
    public KeyAdapter testKey;

    //게임 제어
    int status;//게임의 상태
    int cnt;//루프 제어용 컨트롤 변수
    int delay;//루프 딜레이. 1/1000초 단위.
    long pretime;//루프 간격을 조절하기 위한 시간 체크값
    int keybuff;//키 버퍼값
    Thread mainwork;
    MoveThread moveThread = new MoveThread();
    boolean isMovingRight = false;
    boolean isMovingLeft = false;
    boolean isJumping = false;
    boolean isFalling = false;
    boolean isDie = false;
    boolean isOpponentDie = false;
    boolean isArrive = false;
    boolean isOpponentArrive = false;
    boolean isGameClear = false;

    int resetTotalDistance = 120;
    int jumpingTotalDistance = resetTotalDistance;
    int jumpingDist = 8;
    int fallingDist = 6;
    int xmovingDist = 6;

    // 이미지 툴킷
    Toolkit imageTool = Toolkit.getDefaultToolkit();
    Image mapImg = imageTool.getImage("GameClient/image/background/background_ingame.png");
    PlayerInfo myInfo = new PlayerInfo();
    PlayerInfo opponentInfo = new PlayerInfo();

    Image character;
    Image opponent;
    Rectangle characterRec;

    //Image openPengDoorImg = imageTool.getImage("GameClient/image/map/peng_open_door.png");
    //Image openHarpDoorImg = imageTool.getImage("GameClient/image/map/harp_open_door.png");

    // 이미지 버퍼
    Image buffImg;
    Graphics buffG;

    // 플레이어 위치값.
    int myXpos = 50;
    int myYpos = 50;

    int opponentXpos = 100;
    int opponentYpos = 150;

    boolean roof = true;//스레드 루프 정보

    public void gameControll() {
        playerItemGetCheck();
        playerObstacleCheck();
        if (items.size() == 0) {
            playerArriveCheck();
        }
    }

    // 상대방이 먹은 item 없애기
    public static void removeItem(int i) { // 상대방이 먹은 item 없애기
        items.remove(i);
    }

    public static void switchOn(int i) {
        if (buttons != null) {
            buttons.get(i).setSwitchState(true);
            isOpponentSwitchOn = true;
            opponentlastSwitchIdx = i;
        }
    }

    public static void switchOff(int i) {
        if (buttons != null) {
            buttons.get(i).setSwitchState(false);
            isOpponentSwitchOn = false;
            opponentlastSwitchIdx = -1;
        }
    }

    public void playerItemGetCheck() {
        for (int i = 0; i < items.size(); i++) {//Item m : items

            Item m = items.get(i);
            if (!(m.getMapNumber() % 2 == ClientFrame.userNum % 2)) continue;

            if (((m.getX() <= myXpos && myXpos <= m.getX() + m.getWidth()) || (m.getX() <= myXpos + myWidth && myXpos + myWidth <= m.getX() + m.getWidth()))
                    && ((myYpos <= m.getY() && m.getY() <= myYpos + myHeight) || (myYpos <= m.getY() + m.getHeight() && m.getY() + m.getHeight() <= myYpos + myHeight))) {
                items.remove(m);
                //TODO:네트워크로 사라진 아이템 인덱스 보내주기
                ListenNetwork.SendObject(new ChatMsg("550", i, "ITEM"));
                break;
            } else
                continue;

        }
    }

    public void playerOnSwitchCheck() {

        for (int i = 0; i < buttons.size(); i++) {//Item m : items
            Button s = buttons.get(i);

            if (isOpponentSwitchOn && opponentlastSwitchIdx == i) continue;

            if (characterRec.intersects(s.getRectButton())) {
                //TODO:네트워크로 스위치 눌렸다고 보내주기
                //ListenNetwork.SendObject(new ChatMsg(GameClientFrame.roomId,"550",i));
                s.setSwitchState(true);
                System.out.println("스위치가 눌렸음");
                isMySwitchOn = true;
                mylastOnSwitchIdx = i;
                ListenNetwork.SendObject(new ChatMsg( "550", i, "SWITCH_ON"));
                break;
            } else if (isMySwitchOn && mylastOnSwitchIdx == i) {
                System.out.println("스위치가 안눌림");
                isMySwitchOn = false;
                mylastOnSwitchIdx = -1;
                ListenNetwork.SendObject(new ChatMsg( "550", i, "SWITCH_OFF"));
                s.setSwitchState(false);
            }

        }
    }

    public void playerObstacleCheck() {
        for (int i = 0; i < barriers.size(); i++) {//Item m : items

            Barrier o = barriers.get(i);

            if (o.getMapNumber() % 2 == ClientFrame.userNum % 2) {
                continue;
            }

            if (((o.getX() + 20 <= myXpos + myWidth && myXpos + myWidth <= o.getX() + o.getWidth() - 20) || (o.getX() + 20 <= myXpos && myXpos <= o.getX() + o.getWidth() - 20)) &&
                    (o.getY() <= myYpos + myHeight + 15 && myYpos + myHeight + 10 <= o.getY() + o.getHeight())) {
                System.out.println("1  Game Over");
                isDie = true;
                ClientFrame.net.isPlayingGame = false;
                //character = imageTool.getImage(myInfo.getDieImgPath());
                ListenNetwork.SendObject(new ChatMsg(ClientFrame.userName, "600", "GameOver")); // GameOver 전송
                break;
            } else {
                continue;
            }
        }
    }

    public void playerArriveCheck() {
        for (int i = 0; i < doors.size(); i++) {
            Door door = doors.get(i);

            if (door.getMapNumber() % 2 == ClientFrame.userNum % 2) { // user가 도착했는지 판단
                if (myInfo.getState() == State.FRONT && (door.getX() <= myXpos && myXpos <= door.getX() + door.getWidth())
                        && (door.getX() <= myXpos + myWidth && myXpos + myWidth <= door.getX() + door.getWidth())
                        && (door.getY() <= myYpos && myYpos <= door.getY() + door.getHeight())) {
                    isArrive = true;
                } else {
                    isArrive = false;
                }
            } else { // opponent가 도착했는지 판단

                if (opponentInfo.getState() == State.FRONT && (door.getX() <= opponentXpos && opponentXpos <= door.getX() + door.getWidth())
                        && (door.getX() <= opponentXpos + opponentWidth && opponentXpos + opponentWidth <= door.getX() + door.getWidth())
                        && (door.getY() <= opponentYpos && opponentYpos <= door.getY() + door.getHeight())) {
                    isOpponentArrive = true;
                } else {
                    isOpponentArrive = false;
                }
            }


        }
    }

    //---------------------------------------------------
    //스레드 파트
    public void run() {
        try {
            while (roof) {
                pretime = System.currentTimeMillis();
                //게임 컨트롤
                if (isDie || isOpponentDie) {
                    //죽은 경우 -> 스레드 종료
                    Thread.sleep(1000);
                    break;
                } else if (isGameClear) {
                    if (stageNum == 1) {
                        Thread.sleep(1100);
                        stageNum = 2;
                        setMap();
                        initState();
                        switch (ClientFrame.userNum) {
                            case 1:
                                myXpos = 35;
                                myYpos = 40;

                                opponentXpos = 35;
                                opponentYpos = 130;
                                break;
                            case 2:
                                myXpos = 35;
                                myYpos = 130;

                                opponentXpos = 35;
                                opponentYpos = 40;
                                break;
                        }
                    } else {
                        Thread.sleep(700);
                        break;
                    }
                }

                repaint();

                if (System.currentTimeMillis() - pretime < delay)
                    Thread.sleep(delay - System.currentTimeMillis() + pretime);
                if (status != 4) cnt++;
            }


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMap() {
        String mapPath = "GameClient/src/resource/map.txt";
        map = new Map(mapPath);
        blocks = map.getBlocks();
        barriers = map.getBarriers();
        items = map.getItems();
        doors = map.getDoors();
        buttons = map.getButtons();
        buttonBlocks = map.getButtonBlocks();

//        if (blocks != null) {
//            System.out.println("Blocks initialized. Count: " + blocks.size());
//            for (Block block : blocks) {
//                System.out.println("Block X: " + block.getX() + ", Y: " + block.getY());
//                System.out.println("Block Image: " + block.getBlockImage());
//            }
//        } else {
//            System.out.println("Blocks are null!");
//        }
    }

    public void initState() {
        isMovingRight = false;
        isMovingLeft = false;
        isJumping = false;
        isFalling = false;
        isDie = false;
        isOpponentDie = false;
        isArrive = false;
        isOpponentArrive = false;
        isGameClear = false;
    }

    public void systeminit() {
        status = 0;
        cnt = 0;
        delay = 17;// 17/1000초 = 58 (프레임/초)
        keybuff = 0;

        initState();

        setMap();

        // 캐릭터 설정
        switch (ClientFrame.userNum) {
            case 1:
                myInfo.setUserNum(1);
                myInfo.setCharacterImgPath("GameClient/image/character/penguin_ingame.png");
                myInfo.setRunRightImgPath("GameClient/image/character/penguin_ingame.png");
                myInfo.setRunLeftImgPath("GameClient/image/character/penguin_ingame.png");
                myXpos = 650;
                myYpos = 507;

                opponentInfo.setUserNum(2);
                opponentInfo.setCharacterImgPath("GameClient/image/character/harp_ingame.png");
                opponentInfo.setRunRightImgPath("GameClient/image/character/harp_ingame.png");
                opponentInfo.setRunLeftImgPath("GameClient/image/character/harp_ingame.png");
                opponentXpos = 700;
                opponentYpos = 507;
                break;
            case 2:
                myInfo.setUserNum(2);
                myInfo.setCharacterImgPath("GameClient/image/character/harp_ingame.png");
                myInfo.setRunRightImgPath("GameClient/image/character/harp_ingame.png");
                myInfo.setRunLeftImgPath("GameClient/image/character/harp_ingame.png");
                myXpos = 700;
                myYpos = 507;

                opponentInfo.setUserNum(1);
                opponentInfo.setCharacterImgPath("GameClient/image/character/penguin_ingame.png");
                opponentInfo.setRunRightImgPath("GameClient/image/character/penguin_ingame.png");
                opponentInfo.setRunLeftImgPath("GameClient/image/character/penguin_ingame.png");
                opponentXpos = 650;
                opponentYpos = 507;
                break;
        }
        character = imageTool.getImage(myInfo.getCharacterImgPath());
//	  System.out.println(new ImageIcon(character).getIconWidth()+","+new ImageIcon(character).getIconHeight());
        opponent = imageTool.getImage(opponentInfo.getCharacterImgPath());
        mainwork = new Thread(this);
        mainwork.start();

    }

    public GamePlayPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        systeminit();

        // 키 리스너 등록
        setFocusable(true);
        requestFocusInWindow(); // 키 입력을 받기 위한 포커스 설정

        MovingInfo obcm = new MovingInfo("400", myXpos, myYpos, ClientFrame.userNum, State.FRONT); // gameRoom 입장 시도
        ListenNetwork.SendObject(obcm);

        moveThread.start();

        testKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                System.out.println("키가 눌림");
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (!isJumping && !isFalling)
                            isJumping = true;
                        break;
                    case KeyEvent.VK_LEFT:
                        System.out.println("left 키 눌림 ");
                        if (isMovingRight)
                            isMovingRight = false;
                        isMovingLeft = true;
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (!isMovingRight)
                            myXpos -= 10;
                        System.out.println("right 키 눌림");
                        if (isMovingLeft)
                            isMovingLeft = false;
                        isMovingRight = true;
                        break;
                }
                repaint(); // 화면 갱신
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        myXpos += 10;
                        isMovingRight = false;
                        break;
                    case KeyEvent.VK_LEFT:
                        isMovingLeft = false;
                        break;
                }
                repaint(); // 화면 갱신
            }
        };
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //int xOffset = 10; // 오른쪽으로 10 픽셀 이동
        //int yOffset = 10;  // 아래로 10 픽셀 이동

        // 배경 이미지 그리기
        if (mapImg != null) {
            g.drawImage(mapImg, 0, 0, WIDTH, HEIGHT, this);
        }

        // 캐릭터 이미지 그리기
        g.drawImage(character, myXpos, myYpos, this);

        // 상대 캐릭터 이미지 그리기
        g.drawImage(opponent, opponentXpos, opponentYpos, this);


        // 블록 그리기
        if (blocks != null) {
            for (Block block : blocks) {
                if (block.getBlockImage() != null) {
                    g.drawImage(block.getBlockImage(), block.getX(), block.getY(), this);
                } else {
                    System.out.println("Block image is null at X: " + block.getX() + ", Y: " + block.getY());
                }
            }
        }
        //장애물 그리기
        if (barriers != null) {
            for (Barrier barrier : barriers) {
                if (barrier.getBarrierImage() != null) {
                    g.drawImage(barrier.getBarrierImage(), barrier.getX(), barrier.getY(), this);
                } else {
                    System.out.println("Barrier image is null at X: " + barrier.getX() + ", Y: " + barrier.getY());
                }
            }
        }
        if (items != null) {
            for (Item item : items) {
                if (item.getItemImage() != null) {
                    g.drawImage(item.getItemImage(), item.getX(), item.getY(), this);
                } else {
                    System.out.println("Item image is null at X: " + item.getX() + ", Y: " + item.getY());
                }
            }
        }
        //문 그리기
        if (doors != null) {
            for (Door door : doors) {
                if (door.getDoorImage() != null) {
                    g.drawImage(door.getDoorImage(), door.getX(), door.getY(), this);
                } else {
                    System.out.println("Door image is null at X: " + door.getX() + ", Y: " + door.getY());
                }
            }
        }
        if (buttons != null) {
            for (Button button : buttons) {
                if (button.getButtonImage() != null) {
                    g.drawImage(button.getButtonImage(), button.getX() + 5, button.getY(), this);
                } else {
                    System.out.println("Button image is null at X: " + button.getX() + ", Y: " + button.getY());
                }
            }
        }
        if (buttonBlocks != null) {
            for (ButtonBlock buttonBlock : buttonBlocks) {
                if (buttonBlock.getButtonBlockImage() != null) {
                    g.drawImage(buttonBlock.getButtonBlockImage(), buttonBlock.getX(), buttonBlock.getY(), this);
                } else {
                    System.out.println("ButtonBlock image is null at X: " + buttonBlock.getX() + ", Y: " + buttonBlock.getY());
                }
            }
        }
    }

    private class MoveThread extends Thread {
        public void run() {
            while (true) {
                if (isDie || isOpponentDie) // 죽은 경우, 게임 클리어한 경우 스레드 종료
                    break;
                setCharacterImg();
                if (isJumping)
                    jumping();
                else {
                    falling();
                }
                if (isMovingLeft || isMovingRight)
                    xMoving();
                playerOnSwitchCheck();
                MovingInfo obcm = new MovingInfo("400", myXpos, myYpos, ClientFrame.userNum, myInfo.getState());
                ListenNetwork.SendObject(obcm);
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void setCharacterImg() {
        if (isMovingLeft) {
            character = imageTool.getImage(myInfo.getRunLeftImgPath());
            myInfo.setState(State.LEFT);
        } else if (isMovingRight) {
            character = imageTool.getImage(myInfo.getRunRightImgPath());
            myInfo.setState(State.RIGHT);
        } else {
            character = imageTool.getImage(myInfo.getCharacterImgPath());
            myInfo.setState(State.FRONT);
        }
    }

    public void resetTotalJumpDist() {
        jumpingTotalDistance = resetTotalDistance;
    }

    public void jumping() {
        if (jumpingTotalDistance <= 0) {
            isJumping = false;
            isFalling = true;
            resetTotalJumpDist();
        } else {
            if (canMove(myXpos, myYpos - jumpingDist / 2)) {
                myYpos -= jumpingDist;
                jumpingTotalDistance -= jumpingDist;
            } else {
                resetTotalJumpDist();
                isJumping = false;
                isFalling = true;
            }
        }
    }

    public void falling() {
        if (canMove(myXpos, myYpos + fallingDist)) {
            myYpos += fallingDist;
        } else {
            isFalling = false;
        }
    }

    public void xMoving() {
        if (isMovingRight) {
            myXpos += xmovingDist;
            if (!canMove(myXpos, myYpos)) {
                myXpos -= xmovingDist;
            }
        } else if (isMovingLeft) {
            myXpos -= xmovingDist;
            if (!canMove(myXpos, myYpos)) {
                myXpos += xmovingDist;
            }
        }
    }

    public void setMoving(int x, int y, State type) {
//    	System.out.println("setMoving이 불림");
        opponentXpos = x;
        opponentYpos = y;
        opponentInfo.setState(type);
    }

    public void setDieImage() {
//    	System.out.println("opponent die Image");
        opponent = imageTool.getImage(opponentInfo.getDieImgPath());
        isOpponentDie = true;
    }

    public boolean canMove(int x, int y) { // 블럭, 장애물의 위=0,아래=1,좌=2,우=3, 어딘가=4
        myWidth = CHARACTER_WIDTH;
        myHeight = CHARACTER_HEIGHT;
        opponentWidth = CHARACTER_WIDTH;
        opponentHeight = CHARACTER_HEIGHT;

        characterRec = new Rectangle(x, y, myWidth, myHeight);

        for (Block block : blocks) {
            if (characterRec.intersects(block.getRectBlock())) {
                return false;
            }
        }

        for (Barrier barrier : barriers) {
            if (characterRec.intersects(barrier.getBarrierRect())) {
                return false;
            }
        }

        for (ButtonBlock buttonBlock : buttonBlocks) {
            if (characterRec.intersects(buttonBlock.getRectButtonBlock()) && buttonBlock.getIsVisible()) {
                return false;
            }
        }

        return true;
    }
}
