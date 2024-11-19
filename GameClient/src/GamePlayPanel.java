import javax.swing.*;
import Map.Block;
import Map.Barrier;
import Map.Item;
import Map.Door;
import Map.Button;
import Map.ButtonBlock;
import java.awt.*;
import java.util.ArrayList;

public class GamePlayPanel extends JPanel {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private Map map;
    private ArrayList<Block> blocks = null;
    private ArrayList<Barrier> barriers = null;
    private ArrayList<Item> items = null;
    private ArrayList<Door> doors = null;
    private ArrayList<Button> buttons = null;
    private ArrayList<ButtonBlock> buttonBlocks = null;

    Toolkit imageTool = Toolkit.getDefaultToolkit();
    Image mapImg = imageTool.getImage("GameClient/image/background/background_ingame.png");

    public GamePlayPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        systeminit();
    }

    public void systeminit() {
        setMap();
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
        //장애물 그리기
        if (barriers != null) {
            for (Barrier barrier : barriers) {
                if (barrier.getBarrierImage() != null) {
                    g.drawImage(barrier.getBarrierImage(), barrier.getX() + xOffset, barrier.getY() + yOffset, this);
                } else {
                    System.out.println("Barrier image is null at X: " + barrier.getX() + ", Y: " + barrier.getY());
                }
            }
        }
        if (items != null) {
            for (Item item : items) {
                if (item.getItemImage() != null) {
                    g.drawImage(item.getItemImage(), item.getX() + xOffset, item.getY() + yOffset, this);
                } else {
                    System.out.println("Item image is null at X: " + item.getX() + ", Y: " + item.getY());
                }
            }
        }
        //문 그리기
        if (doors != null) {
            for (Door door : doors) {
                if (door.getDoorImage() != null) {
                    g.drawImage(door.getDoorImage(), door.getX() + xOffset, door.getY() + yOffset, this);
                } else {
                    System.out.println("Door image is null at X: " + door.getX() + ", Y: " + door.getY());
                }
            }
        }
        if (buttons != null) {
            for (Button button : buttons) {
                if (button.getButtonImage() != null) {
                    g.drawImage(button.getButtonImage(), button.getX() + 5 + xOffset, button.getY() + yOffset, this);
                } else {
                    System.out.println("Button image is null at X: " + button.getX() + ", Y: " + button.getY());
                }
            }
        }
        if (buttonBlocks != null) {
            for (ButtonBlock buttonBlock : buttonBlocks) {
                if (buttonBlock.getButtonBlockImage() != null) {
                    g.drawImage(buttonBlock.getButtonBlockImage(), buttonBlock.getX() + xOffset, buttonBlock.getY() + yOffset, this);
                } else {
                    System.out.println("ButtonBlock image is null at X: " + buttonBlock.getX() + ", Y: " + buttonBlock.getY());
                }
            }
        }
    }
}
