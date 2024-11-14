import javax.swing.*;
import java.awt.*;

public class GameClientFrame extends JFrame {
    public GameClientFrame(){
        setTitle("Snowball Duo - Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //GameStartPanel mainPanel = new GameStartPanel();
        GameMainPanel mainPanel = new GameMainPanel();
        mainPanel.setPreferredSize(new Dimension(1000, 600));
        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }
}
