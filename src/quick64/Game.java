package quick64;
import java.awt.*;
import javax.swing.*;

public class Game extends JFrame {

    public int x = 100, 
            y = 100,
            width = 400, 
            height = 400; // Frame size and location

    public String FRAME_TITLE = "Quick64 \u00a9 2015 Pheng Taing";
    public static int WINNING_TILE = 64;
    public static int GRID = 3; 
    public static int GRID_WIDTH;
    public static int SINGLE = 1, MULTI = 2;

    public Game(int gameMode) {
        setTitle(FRAME_TITLE);
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel frameContent;

        if (gameMode == SINGLE) {
            width = 400;
            GRID_WIDTH = GRID;
            frameContent = new GameSingle(this);
        } else {
            width = 600;
            GRID_WIDTH = (GRID - 1) * 2 + 1; 
            frameContent = new GameMulti(this);
        }

        Container visibleArea = getContentPane();
        visibleArea.add(frameContent);
        frameContent.setPreferredSize(new Dimension(width, height));
        pack();
        frameContent.requestFocusInWindow();
        setVisible(true);
        setResizable(false);
    }
    public static void main(String[] args) {
        Game gui = new Game(SINGLE);
    }
}