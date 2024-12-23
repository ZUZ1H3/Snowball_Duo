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

public class PlayPanel extends JPanel implements Runnable {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int CHARACTER_WIDTH = 34;
    private final int CHARACTER_HEIGHT = 34;

    int myWidth, myHeight; // 내 캐릭터의 크기
    int opponentWidth, opponentHeight; // 상대 캐릭터의 크기

    private int stageNum = 1; // 현재 스테이지 번호

    private Map map;
    private ArrayList<Block> blocks = null;
    private static ArrayList<Barrier> barriers = null;
    private static ArrayList<Item> items = null;
    private static ArrayList<Door> doors = null;
    private static ArrayList<Button> buttons = null;
    private static ArrayList<ButtonBlock> buttonBlocks = null;

    //버튼 관련 변수
    public static boolean isOpponentButtonOn = false;
    public static int opponentlastButtonIndex = -1;  // 상대방이 마지막으로 조작한 버튼 인덱스

    public static boolean isMyButtonOn = false;
    public static int mylastOnButtonIndex = -1; // 내가 마지막으로 조작한 버튼 인덱스

    //키 어댑터
    public KeyAdapter testKey;

    //게임 제어
    int status; //게임 상태
    int cnt; //컨트롤 변수
    int delay; //루프 딜레이
    long pretime; //시간 체크값
    int keybuff; //키 버퍼
    Thread mainwork;
    MoveThread moveThread = new MoveThread();  // 이동 관련 스레드

    //현재 상태 변수
    boolean isMovingRight = false;
    boolean isMovingLeft = false;
    boolean isJumping = false;
    boolean isFalling = false;
    boolean isDie = false;
    boolean isOpponentDie = false;
    boolean isArrive = false;
    boolean isOpponentArrive = false;
    boolean isGameClear = false;

    int resetTotalDistance = 150;  // 리셋 거리
    int jumpingTotalDistance = resetTotalDistance; // 점프 거리
    int jumpingDist = 9; // 점프 이동 거리
    int fallingDist = 6; // 떨어지는 거리
    int xmovingDist = 6; // x축 이동 거리

    // 이미지 툴킷
    Toolkit imageTool = Toolkit.getDefaultToolkit();
    Image mapImg = imageTool.getImage("GameClient/image/background/background_ingame.png");
    PlayerInfo myInfo = new PlayerInfo(); // 내 캐릭터 정보
    PlayerInfo opponentInfo = new PlayerInfo(); // 상대 캐릭터 정보

    //캐릭터와 상대를 그리는 이미지
    Image character;
    Image opponent;
    Rectangle characterRec;

    //열린 문 이미지
    Image openPengDoorImg = imageTool.getImage("GameClient/image/map/open_peng_door.png");
    Image openHarpDoorImg = imageTool.getImage("GameClient/image/map/open_harp_door.png");

    // 이미지 버퍼
    Image buffImg;
    Graphics buffG;

    // 플레이어 위치값.
    int myXpos = 50;
    int myYpos = 50;

    int opponentXpos = 100;
    int opponentYpos = 150;

    boolean roof = true;//스레드 루프 정보

    //게임 컨트롤 함수
    public void gameControll() {
        playerItemGetCheck(); // 아이템 획득 여부 체크
        playerObstacleCheck(); // 장애물 충돌 여부 체크
        playerArriveCheck(); // 목표 도달 여부 체크
    }

    // 상대방이 먹은 아이템을 리스트에서 제거
    public static void removeItem(int i) { // 상대방이 먹은 item 없애기
        items.remove(i);
    }

    // 버튼 ON 메소드
    public static void buttonOn(int i) {
        if (buttons != null) {
            buttons.get(i).setSwitchState(true);
            isOpponentButtonOn = true; // 상대방 스위치 상태 설정
            opponentlastButtonIndex = i; // 마지막으로 조작한 스위치 저장
        }
    }

    // 버튼 OFF 메소드
    public static void buttonOff(int i) {
        if (buttons != null) {
            buttons.get(i).setSwitchState(false);
            isOpponentButtonOn = false;
            opponentlastButtonIndex = -1;
        }
    }

    //발판 아래로 이동
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

    // 아이템을 획득했는지 체크하는 메소드
    public void playerItemGetCheck() {
        for (int i = 0; i < items.size(); i++) {//Item m : items
            Item m = items.get(i);
            if (!(m.getMapNumber() % 2 == ClientFrame.userNum % 2)) continue;

            if (((m.getX() <= myXpos && myXpos <= m.getX() + m.getWidth()) || (m.getX() <= myXpos + myWidth && myXpos + myWidth <= m.getX() + m.getWidth()))
                    && ((myYpos <= m.getY() && m.getY() <= myYpos + myHeight) || (myYpos <= m.getY() + m.getHeight() && m.getY() + m.getHeight() <= myYpos + myHeight))) {
                items.remove(m);
                if (m.getMapNumber() % 2 == 0) { //짝수 : 하프물범
                    ClientFrame.harpSealItemCount++;  // 하프물범 아이템 카운트 증가
                } else {
                    ClientFrame.penguinItemCount++;  // 펭귄 아이템 카운트 증가
                }
                //사라진 아이템 인덱스 보내주기
                ListenNetwork.SendObject(new ChatMsg("550", i, "ITEM"));
                break;
            }
        }
    }

    // 버튼이 켜졌는지 체크
    public void playerOnButtonCheck() {
        for (int i = 0; i < buttons.size(); i++) {
            Button s = buttons.get(i);

            // 상대방이 이미 버튼을 눌렀다면 건너뛰기
            if (isOpponentButtonOn && opponentlastButtonIndex == i) continue;

            // 버튼 영역과 캐릭터 영역이 겹치는지 확인
            if (characterRec.intersects(s.getRectButton())) {
                if (!s.isSwitchOn()) { // 버튼이 꺼져 있으면
                    moveButtonBlocksDown(); // 로컬에서 블록 내리기
                    s.setSwitchOn(true); // 버튼 상태 변경
                    isMyButtonOn = true;
                    mylastOnButtonIndex = i;

                    // 네트워크로 "SWITCH_ON" 이벤트 전송
                    ListenNetwork.SendObject(new ChatMsg("550", i, "SWITCH_ON"));
                    //break;
                }
            } else {
                if (isMyButtonOn && mylastOnButtonIndex == i) { // 버튼에서 벗어난 경우
                    moveButtonBlocksUp(); // 로컬에서 블록 올리기
                    s.setSwitchOn(false); // 버튼 상태 변경
                    isMyButtonOn = false;
                    mylastOnButtonIndex = -1;

                    // 네트워크로 "SWITCH_OFF" 이벤트 전송
                    ListenNetwork.SendObject(new ChatMsg("550", i, "SWITCH_OFF"));
                }
            }
        }
    }

    // 장애물과의 충돌 여부 체크
    public void playerObstacleCheck() {
        for (int i = 0; i < barriers.size(); i++) {
            Barrier o = barriers.get(i);

            // -1인 장애물이거나 유저 번호가 맞는 장애물일 때만 체크
            if (o.getMapNumber() == -1 || o.getMapNumber() % 2 == ClientFrame.userNum % 2) {
                // 캐릭터가 장애물 바로 위에 있는지 확인
                boolean isDirectlyAbove = Math.abs((myYpos + myHeight) - o.getY()) <= 5;

                // 캐릭터의 x좌표가 장애물의 x범위 안에 실제로 들어가있는지 확인
                boolean xOverlap = (myXpos + (myWidth / 2) >= o.getX()) &&
                        (myXpos + (myWidth / 2) <= o.getX() + o.getWidth());

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

    // 목표에 도달했는지 여부를 체크
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

    //스레드 파트
    public void run() {
        try {
            while (roof) {
                pretime = System.currentTimeMillis();
                gameControll();
                if (isDie || isOpponentDie) { //죽었을 경우
                    Thread.sleep(1100);
                    ClientFrame.isChanged = true;
                    ClientFrame.isGameOverPanel = true; //게임 오버 패널로 전환

                    while (ClientFrame.isGameOverPanel) {
                        Thread.sleep(100);
                    }

                    if (ClientFrame.isGameOverPanel == false) {
                        systeminit(); //상태 초기화
                        switch (ClientFrame.userNum) {
                            case 1:
                                myXpos = 650;
                                myYpos = 507;

                                opponentXpos = 700;
                                opponentYpos = 507;
                                break;
                            case 2:
                                myXpos = 700;
                                myYpos = 507;

                                opponentXpos = 650;
                                opponentYpos = 507;
                                break;
                        }
                        // 새로운 스레드 실행
                        mainwork = new MoveThread();
                        mainwork.start(); // 스레드 시작
                    }

                } else if (isGameClear) { //게임 클리어 시
                    if (stageNum == 1) { //첫 번째 스테이지일 경우
                        Thread.sleep(1100);
                        ClientFrame.isChanged = true;
                        ClientFrame.isGameClearPanel = true;  // 클리어 화면 표시

                        while (ClientFrame.isGameClearPanel) {
                            Thread.sleep(100); // 대기 상태로 유지
                        }

                        // 클리어 버튼을 누르면 두 번째 스테이지로 넘어간다
                        if (ClientFrame.isGameClearPanel == false) {
                            stageNum = 2;  // 두 번째 스테이지로 변경
                            setMap();  // 두 번째 스테이지 맵 설정
                            initState();  // 두 번째 스테이지 초기화

                            // 플레이어 위치 설정 (첫 번째 사용자와 두 번째 사용자에 대해 구분)
                            switch (ClientFrame.userNum) {
                                case 1:
                                    myXpos = 70;
                                    myYpos = 507;

                                    opponentXpos = 110;
                                    opponentYpos = 507;
                                    break;
                                case 2:
                                    myXpos = 110;
                                    myYpos = 507;

                                    opponentXpos = 70;
                                    opponentYpos = 507;
                                    break;
                            }
                        }
                    } else {
                        Thread.sleep(700);
                        break;
                    }
                }

                repaint();
                // 시간 차이를 계산하여 프레임 지연을 맞춤
                if (System.currentTimeMillis() - pretime < delay)
                    Thread.sleep(delay - System.currentTimeMillis() + pretime);
                if (status != 4) cnt++;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // 현재 스테이지 맵 설정
    public void setMap() {
        String mapPath = "";
        switch (stageNum) {
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
    }

    // 상태 변수 초기화
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

    // 게임 시스템 초기화
    public void systeminit() {
        status = 0;
        cnt = 0;
        delay = 17;// 17/1000초
        keybuff = 0; // 키 입력 버퍼 초기화

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
        opponent = imageTool.getImage(opponentInfo.getCharacterImgPath());

        mainwork = new Thread(this);
        mainwork.start();
    }

    public PlayPanel() {
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
            }
        };
    }

    public void paint(Graphics g) {
        buffImg = createImage(getWidth(), getHeight());
        buffG = buffImg.getGraphics();

        update(g);
    }

    // 화면을 실제로 업데이트하는 메소드
    public void update(Graphics g) {
        buffG.clearRect(0, 0, WIDTH, HEIGHT);
        buffG.drawImage(mapImg, 0, 0, this);

        for (Block block : blocks)
            buffG.drawImage(block.getBlockImage(), block.getX(), block.getY(), this);

        for (Item item : items)
            buffG.drawImage(item.getItemImage(), item.getX(), item.getY(), this);

        for (Barrier obstacle : barriers)
            buffG.drawImage(obstacle.getBarrierImage(), obstacle.getX(), obstacle.getY(), this);


        for (Door door : doors)
            buffG.drawImage(door.getDoorImage(), door.getX(), door.getY(), this);

        for (ButtonBlock switchBlock : buttonBlocks) {
            buffG.drawImage(switchBlock.getButtonBlockImage(), switchBlock.getX(), switchBlock.getY(), this);
        }

        for (Button switchBtn : buttons) {
            if (switchBtn.isVisible()) {  // 버튼이 보일 때만 그리기
                buffG.drawImage(switchBtn.getButtonImage(), switchBtn.getX(), switchBtn.getY(), this);
            }
        }

        // 도착 여부 확인
        if (!(isArrive && isOpponentArrive)) { // 모두 도착 X
            buffG.drawImage(character, myXpos, myYpos, this);
            if (!isOpponentDie) {
                switch (opponentInfo.getState()) {
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
        } else { // 모두 도착한 경우
            for (Door door : doors) {
                if (door.getMapNumber() % 2 == 0) {
                    buffG.drawImage(openHarpDoorImg, door.getX(), door.getY(), this);
                } else {
                    buffG.drawImage(openPengDoorImg, door.getX(), door.getY(), this);
                }
            }
            isGameClear = true;

        }

        g.drawImage(buffImg, 0, 0, this); // 이미지 출력
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
                playerOnButtonCheck();
                MovingInfo obcm = new MovingInfo("400", myXpos, myYpos, ClientFrame.userNum, myInfo.getState());
                ListenNetwork.SendObject(obcm); // 서버로 현재 위치와 상태 정보를 전송.
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
        } else if (isJumping) {
            character = imageTool.getImage(myInfo.getJumpImgPath());
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
        opponentXpos = x;
        opponentYpos = y;
        opponentInfo.setState(type);
    }

    public void setDieImage() {
        opponent = imageTool.getImage(opponentInfo.getDieImgPath());
        isOpponentDie = true;
    }

    // 움직임 가능 여부 관련 함수 - 블럭/장애물/버튼블록
    public boolean canMove(int x, int y) {
        myWidth = CHARACTER_WIDTH + 10;
        myHeight = CHARACTER_HEIGHT + 3;
        opponentWidth = CHARACTER_WIDTH + 10;
        opponentHeight = CHARACTER_HEIGHT + 3;

        characterRec = new Rectangle(x, y, myWidth, myHeight);

        for (int i = 0; i < blocks.size(); i++) {
            if (characterRec.intersects(blocks.get(i).getRectBlock())) {
                return false;
            }
        }

        for (int i = 0; i < barriers.size(); i++) {
            if (characterRec.intersects(barriers.get(i).getBarrierRect())) {
                return false;
            }
        }

        for (int i = 0; i < buttonBlocks.size(); i++) {
            if (characterRec.intersects(buttonBlocks.get(i).getRectButtonBlock())) {
                return false;
            }
        }
        return true;
    }
}
