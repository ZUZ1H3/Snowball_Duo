import javax.swing.*;
import Map.Block;
import java.awt.*;
import java.util.ArrayList;

public class GamePlayPanel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private Map map;
    private ArrayList<Block> blocks = null;

    Toolkit imageTool = Toolkit.getDefaultToolkit();
    Image mapImg = imageTool.getImage("GameClient/image/background/background_ingame.png");

    public GamePlayPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        systeminit();
    }

    public void settingMap() {
        String mapPath = "GameClient/src/resource/map.txt";
        map = new Map(mapPath);
        blocks = map.getBlocks();

        if (blocks != null) {
            System.out.println("Blocks initialized. Count: " + blocks.size());
            for (Block block : blocks) {
                System.out.println("Block X: " + block.getX() + ", Y: " + block.getY());
                System.out.println("Block Image: " + block.getBlockImage());
            }
        } else {
            System.out.println("Blocks are null!");
        }
    }

    public void systeminit() {
        settingMap();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int xOffset = 10; // 오른쪽으로 10 픽셀 이동
        int yOffset = 10;  // 아래로 10 픽셀 이동

        // 배경 이미지 그리기
        if (mapImg != null) {
            g.drawImage(mapImg, 0, 0, WIDTH, HEIGHT, this);
        }

        // 블록 그리기
        if (blocks != null) {
            for (Block block : blocks) {
                if (block.getBlockImage() != null) {
                    g.drawImage(block.getBlockImage(), block.getX() + xOffset, block.getY() + yOffset, this);
                } else {
                    System.out.println("Block image is null at X: " + block.getX() + ", Y: " + block.getY());
                }
            }
        }
    }

}
