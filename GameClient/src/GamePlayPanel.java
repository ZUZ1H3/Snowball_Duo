import javax.swing.*;

import Map.Block;

import java.awt.*;
import java.util.ArrayList;

public class GamePlayPanel extends JPanel {
    private final int WIDTH = 780;
    private final int HEIGHT = 580;

    private Map map;
    private ArrayList<Block> blocks = null;

    int myWidth, myHeight;
    int opponenetWidth, opponentHeight;

    Toolkit imageTool = Toolkit.getDefaultToolkit();

    Image mapImg = imageTool.getImage("image/background/background_ingame.png");

    // 이미지 버퍼
    Image buffImg;
    Graphics buffG;

    public void settingMap() {
        //맵을 여러개 만들거면 switch문이나 if문을 써서 주소를 바꿔주면 됨
        String mapPath = "src/resouce/map.txt";

        map = new Map(mapPath);
        blocks = map.getBlocks();
    }

    public void systeminit() {//프로그램 초기화
        //맵 설정
        settingMap();
    }

    public GamePlayPanel() {
        setSize(WIDTH, HEIGHT);

        systeminit();
    }

    public void paint(Graphics g) {
        buffImg = createImage(getWidth(),getHeight()); // 버퍼링용 이미지 ( 도화지 )
        buffG = buffImg.getGraphics(); // 버퍼링용 이미지에 그래픽 객체를 얻어야 그릴 수 있다고 한다. ( 붓? )

        update(g);
    }

    public void update(Graphics g) {
        buffG.clearRect(0, 0, WIDTH, HEIGHT); // 백지화
        buffG.drawImage(mapImg,0,0, this);

        for (Block block : blocks)
            buffG.drawImage(block.getImg(),block.getX(),block.getY(),this);

        g.drawImage(buffImg,0,0,this); // 화면g애 버퍼(buffG)에 그려진 이미지(buffImg)옮김. (도화지에 이미지를 출력)
        repaint();
    }

}
