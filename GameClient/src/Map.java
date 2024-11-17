import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;

import Map.Block;

public class Map {
    final static public int BLOCK_WIDTH_LENGTH = 23;
    public String path;

    public ArrayList<Block> blocks = new ArrayList<>();
    ArrayList<String> packet = new ArrayList<>();

    public Map(String path) {
        String[] mapArr = setMapArr(path);
        setBlockObject(mapArr);
    }

    public void setBlocks(ArrayList<Block> blocks) {
        this.blocks = blocks;
    }

    public ArrayList getBlocks() {
        return blocks;
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
            int mapBlock = Integer.parseInt(mapArr[i]);
            if(mapBlock == 1) {
                int x = (i%BLOCK_WIDTH_LENGTH) * Block.BLOCK_WIDTH;
                int y = (i/BLOCK_WIDTH_LENGTH) * Block.BLOCK_HEIGHT;
                Block block = new Block(x, y);
                blocks.add(block);
            }
        }
    }
}
