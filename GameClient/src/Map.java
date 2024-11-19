import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;

import Map.Block;
import Map.Barrier;
import Map.Item;
import Map.Door;
import Map.Button;
import Map.ButtonBlock;

public class Map {
    final static public int BLOCK_WIDTH_LENGTH = 23;
    public String path;

    public ArrayList<Block> blocks = new ArrayList<>();
    public ArrayList<Barrier> barriers = new ArrayList<>();
    public ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Door> doors = new ArrayList<>();
    public ArrayList<Button> buttons = new ArrayList<>();
    public ArrayList<ButtonBlock> buttonBlocks = new ArrayList<>();

    ArrayList<String> packet = new ArrayList<>();

    public Map(String path) {
        String[] mapArr = setMapArr(path);
        setBlockObject(mapArr);
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public void setBarriers(ArrayList<Barrier> barriers) {
        this.barriers = barriers;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

    public void setButtons(ArrayList<Button> buttons) {
        this.buttons = buttons;
    }

    public void setButtonBlocks(ArrayList<ButtonBlock> buttonBlocks) {
        this.buttonBlocks = buttonBlocks;
    }

    public ArrayList getBlocks() {
        return blocks;
    }

    public ArrayList getBarriers() {
        return barriers;
    }

    public ArrayList getItems() {
        return items;
    }

    public ArrayList getDoors() {
        return doors;
    }

    public ArrayList getButtons() {
        return buttons;
    }

    public ArrayList getButtonBlocks() {
        return buttonBlocks;
    }

    public String[] setMapArr (String path) {
        String[] mapArr = new String[packet.size()];
        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new FileReader(new File(path)));
            String string;
            while ((string = buffer.readLine()) != null) {
                String stringArr[] = string.split(" ");
                for (int i = 0; i < stringArr.length; i++)
                    packet.add(stringArr[i]);
            }
            mapArr = packet.toArray(mapArr);
            buffer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return mapArr;
    }

    public void setBlockObject(String [] mapArr) {
        for(int i=0; i<mapArr.length; i++) {
            int mapNumber = Integer.parseInt(mapArr[i]);
            if(mapNumber == 1) {
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                Block block = new Block(x, y);
                blocks.add(block);
            }
            if(mapNumber == 2 || mapNumber == 3 || mapNumber < 0) {
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                Barrier barrier = new Barrier(x, y, mapNumber);
                barriers.add(barrier);
            }
            if(mapNumber == 4 || mapNumber == 5) { //아이템
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                Item item = new Item(x, y, mapNumber);
                items.add(item);
            }
            if(mapNumber == 6 || mapNumber == 7) { //문
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                Door door = new Door(x, y, mapNumber);
                doors.add(door);
            }
            if(mapNumber == 8) { //버튼(밟는버튼)
                int heightDifference = Block.BLOCK_HEIGHT - Button.BUTTON_HEIGHT; //땅과의 높이 차이
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                Button button = new Button(x, y + heightDifference);
                buttons.add(button);
            }
            if(mapNumber == 9) { //움직이는 발판 = 버튼블록
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                ButtonBlock buttonBlock = new ButtonBlock(x, y);
                buttonBlocks.add(buttonBlock);
            }
        }
    }
}
