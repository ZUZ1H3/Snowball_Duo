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
    private final int CHARACTER_HEIGHT = 34;

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

    boolean clear_continue_yes = false; //클리어 하고 continue yes

    int resetTotalDistance = 150;
    int jumpingTotalDistance = resetTotalDistance;
    int jumpingDist = 9;
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

    Image openPengDoorImg = imageTool.getImage("GameClient/image/map/open_door.png");
    Image openHarpDoorImg = imageTool.getImage("GameClient/image/map/open_door.png");

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
        //if (items.size() == 0) {
            playerArriveCheck();
        //}
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

    public static void moveButtonBlocksDown() {
        for (ButtonBlock block : buttonBlocks) {
            if (block.getY() == block.getOriginalY()) { // 원래 위치인지 확인
                block.setY(block.getY() + 40); // 블록을 40만큼 내림
            }
        }
    }

    public static void moveButtonBlocksUp() {
        for (ButtonBlock block : buttonBlocks) {
            if (block.getY() != block.getOriginalY()) { // 원래 위치가 아니면
                block.setY(block.getOriginalY()); // 원래 위치로 복귀
            }
        }
    }


    public void playerItemGetCheck() {
        for (int i = 0; i < items.size(); i++) {//Item m : items

            Item m = items.get(i);
            if (!(m.getMapNumber() % 2 == ClientFrame.userNum % 2)) continue;

            if (((m.getX() <= myXpos && myXpos <= m.getX() + m.getWidth()) || (m.getX() <= myXpos + myWidth && myXpos + myWidth <= m.getX() + m.getWidth()))
                    && ((myYpos <= m.getY() && m.getY() <= myYpos + myHeight) || (myYpos <= m.getY() + m.getHeight() && m.getY() + m.getHeight() <= myYpos + myHeight))) {
                items.remove(m);
                if (m.getMapNumber() % 2 == 0) {
                    ClientFrame.harpSealItemCount++;
                } else {
                    ClientFrame.penguinItemCount++;
                }

                //TODO:네트워크로 사라진 아이템 인덱스 보내주기
                ListenNetwork.SendObject(new ChatMsg("550", i, "ITEM"));

                break;
            } else
                continue;

        }
    }

    public void playerOnSwitchCheck() {
        for (int i = 0; i < buttons.size(); i++) {
            Button s = buttons.get(i);

            if (isOpponentSwitchOn && opponentlastSwitchIdx == i) continue;

            if (characterRec.intersects(s.getRectButton())) {
                if (!s.isSwitchOn()) { // 스위치가 꺼져 있으면
                    moveButtonBlocksDown(); // 로컬에서 블록 내리기
                    s.setSwitchOn(true); // 스위치 상태 변경
                    isMySwitchOn = true;
                    mylastOnSwitchIdx = i;

                    // 네트워크로 "SWITCH_ON" 이벤트 전송
                    ListenNetwork.SendObject(new ChatMsg("550", i, "SWITCH_ON"));
                    //break;
                }
            } else {
                if (isMySwitchOn && mylastOnSwitchIdx == i) { // 스위치를 벗어난 경우
                    moveButtonBlocksUp(); // 로컬에서 블록 올리기
                    s.setSwitchOn(false); // 스위치 상태 변경
                    isMySwitchOn = false;
                    mylastOnSwitchIdx = -1;

                    // 네트워크로 "SWITCH_OFF" 이벤트 전송
                    ListenNetwork.SendObject(new ChatMsg("550", i, "SWITCH_OFF"));
                }
            }
        }
    }

    public void playerObstacleCheck() {
        for (int i = 0; i < barriers.size(); i++) {
            Barrier o = barriers.get(i);

            // -1인 장애물이거나 유저 번호가 맞는 장애물일 때만 체크
            if (o.getMapNumber() == -1 || o.getMapNumber() % 2 == ClientFrame.userNum % 2) {
                // 캐릭터가 장애물 바로 위에 있는지 확인
                boolean isDirectlyAbove = Math.abs((myYpos + myHeight) - o.getY()) <= 5;

                // 캐릭터의 x좌표가 장애물의 x범위 안에 실제로 들어가있는지 확인
                boolean xOverlap = (myXpos + (myWidth/2) >= o.getX()) &&
                        (myXpos + (myWidth/2) <= o.getX() + o.getWidth());

                if (isDirectlyAbove && xOverlap) {
                    System.out.println("----------Game Over----------");
                    isDie = true;
                    ClientFrame.net.isPlayingGame = false;
                    character = imageTool.getImage(myInfo.getDieImgPath());
                    repaint();
                    ListenNetwork.SendObject(new ChatMsg(ClientFrame.userName, "600", "GameOver"));
                    break;
                }
            }
        }
    }

    public static Item getItemByIndex(int idx) {
        // 아이템 리스트에서 인덱스에 해당하는 아이템을 찾음
        if (idx >= 0 && idx < items.size()) {
            return items.get(idx);  // 아이템 반환
        }
        return null;  // 아이템이 없으면 null 반환
    }


    public void playerArriveCheck() {
        for (int i = 0; i < doors.size(); i++) {
            Door door = doors.get(i);

            if (door.getMapNumber() % 2 == ClientFrame.userNum % 2) { // user가 도착했는지 판단
                if (myInfo.getState() == State.FRONT && (door.getX() <= myXpos && myXpos <= door.getX() + door.getWidth())
                        && (door.getX() <= myXpos + myWidth && myXpos + myWidth <= door.getX() + door.getWidth())
                        && (door.getY() <= myYpos && myYpos <= door.getY() + door.getHeight())) {
                    System.out.println("----------------문에 도착--------------");
                    isArrive = true;
                } else {
                    isArrive = false;
                }
            } else { // opponent가 도착했는지 판단

                if (opponentInfo.getState() == State.FRONT && (door.getX() <= opponentXpos && opponentXpos <= door.getX() + door.getWidth())
                        && (door.getX() <= opponentXpos + opponentWidth && opponentXpos + opponentWidth <= door.getX() + door.getWidth())
                        && (door.getY() <= opponentYpos && opponentYpos <= door.getY() + door.getHeight())) {
                    System.out.println("-----------상대-----문에 도착--------------");
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
                gameControll();
                if (isDie || isOpponentDie) {
                    //죽은 경우 -> 스레드 종료
                    Thread.sleep(1000);
                    break;
                } else if (isGameClear) {
                    if (stageNum == 1) {
                        Thread.sleep(1100);
                        ClientFrame.isChanged = true;
                        ClientFrame.isGameClearPanel = true;  // 클리어 화면 표시

                        while (ClientFrame.isGameClearPanel) {
                            // 대기 상태로 유지
                            Thread.sleep(100);
                        }

                        // 클리어 버튼을 누르면 두 번째 스테이지로 넘어간다
                        if (ClientFrame.isGameClearPanel == false) {
                            stageNum = 2;  // 두 번째 스테이지로 변경
                            setMap();  // 두 번째 스테이지 맵 설정
                            initState();  // 두 번째 스테이지 초기화
                        }
                        //stageNum = 2;
                        //setMap();
                        //initState();
//                        switch (ClientFrame.userNum) {
//                            case 1:
//                                myXpos = 35;
//                                myYpos = 40;
//
//                                opponentXpos = 35;
//                                opponentYpos = 130;
//                                break;
//                            case 2:
//                                myXpos = 35;
//                                myYpos = 130;
//
//                                opponentXpos = 35;
//                                opponentYpos = 40;
//                                break;
//                        }
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

            //gameoverpanel로 이동
            if( isDie || isOpponentDie ) {
                ClientFrame.isChanged = true;
                ClientFrame.isGameOverPanel = true;
            }

//            else if (isGameClear) {
//                moveThread.interrupt();
//                ClientFrame.net.isPlayingGame = false;
//                ClientFrame.isChanged = true;
//                ClientFrame.isGameClearPanel = true;
//            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMap() {
        String mapPath = "";
        switch(stageNum) {
            case 1:
                mapPath = "GameClient/src/resource/map.txt";
                break;
            case 2:
                mapPath = "GameClient/src/resource/map2.txt";
                break;
        }


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
                myInfo.setRunRightImgPath("GameClient/image/character/penguin_right.png");
                myInfo.setRunLeftImgPath("GameClient/image/character/penguin_left.png");
                myInfo.setJumpImgPath("GameClient/image/character/penguin_jump.png");
                myXpos = 650;
                myYpos = 507;

                opponentInfo.setUserNum(2);
                opponentInfo.setCharacterImgPath("GameClient/image/character/harp_ingame.png");
                opponentInfo.setRunRightImgPath("GameClient/image/character/harp_right.png");
                opponentInfo.setRunLeftImgPath("GameClient/image/character/harp_left.png");
                opponentInfo.setJumpImgPath("GameClient/image/character/harp_jump.png");

                opponentXpos = 700;
                opponentYpos = 507;
                break;
            case 2:
                myInfo.setUserNum(2);
                myInfo.setCharacterImgPath("GameClient/image/character/harp_ingame.png");
                myInfo.setRunRightImgPath("GameClient/image/character/harp_right.png");
                myInfo.setRunLeftImgPath("GameClient/image/character/harp_left.png");
                myInfo.setJumpImgPath("GameClient/image/character/harp_jump.png");

                myXpos = 700;
                myYpos = 507;

                opponentInfo.setUserNum(1);
                opponentInfo.setCharacterImgPath("GameClient/image/character/penguin_ingame.png");
                opponentInfo.setRunRightImgPath("GameClient/image/character/penguin_right.png");
                opponentInfo.setRunLeftImgPath("GameClient/image/character/penguin_left.png");
                opponentInfo.setJumpImgPath("GameClient/image/character/penguin_jump.png");

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
                //repaint(); // 화면 갱신
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
                //repaint(); // 화면 갱신
            }
        };
    }

    public void paint(Graphics g) {
        buffImg = createImage(getWidth(), getHeight());
        buffG = buffImg.getGraphics();

        update(g);
    }

    public void update(Graphics g) {
        buffG.clearRect(0, 0, WIDTH, HEIGHT);
        buffG.drawImage(mapImg, 0, 0, this);

        for (Block block : blocks)
            buffG.drawImage(block.getBlockImage(),block.getX(),block.getY(),this);

        for (Item item : items)
            buffG.drawImage(item.getItemImage(),item.getX(),item.getY(),this);

        for (Barrier obstacle : barriers)
            buffG.drawImage(obstacle.getBarrierImage(),obstacle.getX(),obstacle.getY(),this);


        for (Door door : doors)
            buffG.drawImage(door.getDoorImage(),door.getX(),door.getY(),this);

        for (ButtonBlock switchBlock:buttonBlocks) {
            //if (switchBlock.getIsVisible())
                buffG.drawImage(switchBlock.getButtonBlockImage(),switchBlock.getX(),switchBlock.getY(),this);
        }

        for (Button switchBtn: buttons) {
            if (switchBtn.isVisible()) {  // 버튼이 보일 때만 그리기
                buffG.drawImage(switchBtn.getButtonImage(), switchBtn.getX(), switchBtn.getY(), this);
            }
        }


        if(!(isArrive&&isOpponentArrive)) { // 모두 도착 X
            buffG.drawImage(character, myXpos, myYpos, this);
//          System.out.println("draw character ==> "+character.toString());

            if(!isOpponentDie) {
                switch(opponentInfo.getState()) {
                    case LEFT:
                        opponent = imageTool.getImage(opponentInfo.getRunLeftImgPath());
                        break;
                    case RIGHT:
                        opponent = imageTool.getImage(opponentInfo.getRunRightImgPath());
                        break;
                    case FRONT:
                        opponent = imageTool.getImage(opponentInfo.getCharacterImgPath());
                        break;
                }
            }

            buffG.drawImage(opponent, opponentXpos, opponentYpos, this);
        }
        else { // 모두 도착한 경우
            for(Door door:doors) {
                if(door.getMapNumber()%2==0) {
                    buffG.drawImage(openHarpDoorImg,door.getX(),door.getY(),this);
                }
                else {
                    buffG.drawImage(openPengDoorImg,door.getX(),door.getY(),this);
                }
            }
            isGameClear = true;

        }

        g.drawImage(buffImg,0,0,this); // 화면g애 버퍼(buffG)에 그려진 이미지(buffImg)옮김. (도화지에 이미지를 출력)
        repaint();
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
                playerOnSwitchCheck(); //여기부분이 스위치랑 닿았을때 문제!!
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
        } else if(isJumping){
            character = imageTool.getImage(myInfo.getJumpImgPath());
        }else {
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

        for (int i = 0; i<blocks.size(); i++) {
            if (characterRec.intersects(blocks.get(i).getRectBlock())) {
                return false;
            }
        }

        for (int i=0;i<barriers.size();i++) {
            if (characterRec.intersects(barriers.get(i).getBarrierRect())) {
                return false;
            }
        }

        for (int i=0;i<buttonBlocks.size();i++) {
            if (characterRec.intersects(buttonBlocks.get(i).getRectButtonBlock())) {
                return false;
            }
        }

        return true;
    }
}
