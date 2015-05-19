package quick64;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

public class GameMulti extends JPanel implements KeyListener {
    public static String FONT_NAME = "Clear Sans";
    public final int WINNING_TILE = Game.WINNING_TILE;
    
    int LEFT = 0, UP = 1, RIGHT = 2, DOWN = 3, 
            FORWARD = 0, BACKWARD = 1, 
            HORIZONTAL = 0, VERTICAL = 1,
            NEW_GAME = 0, WIN = 1, LOSE = 2,
            ANIMATION_DUR = 100;
    
    int grid, gridWidth, currPlayer, gameStatus;

    JMenuBar menuBar;
    Tile[][] board, oldBoard;
    Point startLocationBoard;
    Game frame;
    Timer newTileAppearTimer;
    Point mouseStart, mouseEnd;
    boolean currentWin;

    public GameMulti(Game frame) {
        this.frame = frame;
        this.grid = Game.GRID;
        this.gridWidth = Game.GRID_WIDTH;
        this.currPlayer = 1;
        menuBar = addMenuButtons(new JMenuBar());
        setLayout(new BorderLayout());
        add(menuBar, BorderLayout.NORTH);

        Tile.initialiseValues();
        HighScoreManager.initialise();

        startLocationBoard = new Point(80, 80);
        setUpNewGame();
    }

    private void setUpNewGame() {
        addKeyListener(this);
        gameStatus = NEW_GAME;
        currPlayer = 1;

        // create the board
        int margin = Tile.tileMargin, size = Tile.tileSize;
        int x = startLocationBoard.x, y = startLocationBoard.y;
        board = new Tile[grid][gridWidth];
        for (int i = 0; i < grid; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = new Tile(x, y);
                x += margin + size;
            }
            y += margin + size;
            x = startLocationBoard.x;
        }
        Tile[][] board1 = carbonCopyBoard(1, board);
        addNewTile(board1, NEW_GAME, 1);
        addNewTile(board1, NEW_GAME, 1);
        carbonPasteBoard(1, board1);

        Tile[][] board2 = carbonCopyBoard(2, board);
        addNewTile(board2, NEW_GAME, 2);
        addNewTile(board2, NEW_GAME, 2);
        carbonPasteBoard(2, board2);

        newTileAppearTimer = new Timer(ANIMATION_DUR, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Tile[][] board2 = carbonCopyBoard(currPlayer, board);
                addNewTile(board2);
                carbonPasteBoard(currPlayer, board2);
                newTileAppearTimer.stop();
                repaint();
            }

        });

    }

    private void carbonPasteBoard(int player, Tile[][] newBoard) {
        for (int row = 0; row < newBoard.length; row++) {
            int fromLocation = player == 1 ? 0 : (newBoard[row].length / 2) + 1;
            for (int col = 0; col < newBoard[row].length; col++, fromLocation++) {
                this.board[row][fromLocation].value = newBoard[row][col].value;
            }
        }

    }

    private Tile[][] carbonCopyBoard(int player, Tile[][] board) {
        Tile[][] copy = new Tile[grid][grid];

        for (int row = 0; row < copy.length; row++) {
            int fromLocation = player == 1 ? 0 : (copy[row].length / 2) + 1;
            for (int col = 0; col < copy[row].length; col++, fromLocation++) {
                copy[row][col] = board[row][fromLocation];
            }
        }

        return copy;
    }

    private void packDownGame() {

        removeKeyListener(this);
        repaint();
    }

    private void addNewTile(Tile[][] board) {
        Point p = getFreeSpot(board);
        board[p.x][p.y].setValue(random(1, 3) * 2);
    }

    private void addNewTile(Tile[][] board, int initialTiles, int player) {
        int x = random(0, board.length);
        int y = random(player == 1 ? 0 : 1, board[0].length - player == 1 ? 1 : 0);

        while (board[x][y].value != 0) {
            x = random(0, board.length);
            y = random(player == 1 ? 0 : 1, board[0].length - player == 1 ? 1 : 0);
        }
        board[x][y].setValue(random(1, 3) * 2);
    }

    private Point getFreeSpot(Tile[][] board) {
        int x = random(0, board.length), y = random(0, board[0].length);
        while (board[x][y].value != 0) {
            x = random(0, board.length);
            y = random(0, board[0].length);
        }
        return new Point(x, y);
    }

    private JMenuBar addMenuButtons(JMenuBar menuBar) {

        Font font = new Font(FONT_NAME, Font.BOLD, 20);
        menuBar.setFont(font);

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setUpNewGame();
                repaint();
            }
        });
        newGame.setFont(font);
        menuBar.add(newGame);

        JMenuItem newOtherGame = new JMenuItem("Single Player");
        newOtherGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
                Game gg = new Game(Game.SINGLE);
            }
        });
        newOtherGame.setFont(font);
        menuBar.add(newOtherGame);
        
        JMenuItem help = new JMenuItem("About");
        help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ImageIcon icon = new ImageIcon("pixport.png");
                JOptionPane
                        .showMessageDialog(
                                null,
                                "Aim of the game: Get tile 64 before the other player.\n(c) Pheng Taing, major credit to original creator Gabriele Circulli.",
                                "About Quick64", 1, icon);
                repaint();
            }
        });
        help.setFont(font);
        menuBar.add(help);

        return menuBar;
    }

    public void paintComponent(Graphics g) {
        g.setColor(new Color(123, 109, 99));
        g.fillRect(0, 0, 10000, 10000);
        for (Tile[] row : board) {
            for (Tile t : row) {
                t.draw(g);
            }
        }

        paintPlayerWindow(g);
        if (gameStatus > NEW_GAME) {
            paintGameOver(g);
        }

        g.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString("Player 1: ASWD         " + currPlayer + "            Player 2: Arrow Keys",
                75, 380);

    }

    private void paintPlayerWindow(Graphics g) {

        Color shade = new Color(123, 109, 99, 200);
        Color border = new Color(239, 227, 214);
        int board2Size = (Tile.tileSize + Tile.tileMargin) * 2, board3Size = (Tile.tileSize + Tile.tileMargin) * 3;
        int player1X = startLocationBoard.x - Tile.tileMargin / 2, player2X = startLocationBoard.x
                + board2Size - Tile.tileMargin / 2, player2Shade = startLocationBoard.x
                + board3Size - Tile.tileMargin / 2;
        int Y = startLocationBoard.y - Tile.tileMargin / 2;

        g.setColor(shade);
        g.fillRect(currPlayer == 1 ? player2Shade : player1X, Y, board2Size, board3Size);

        g.setColor(border);
        g.drawRoundRect(currPlayer == 1 ? player1X : player2X, Y, board3Size, board3Size,
                Tile.tileRound * 2, Tile.tileRound * 2);
    }

    private void paintGameOver(Graphics g) {
        Color c = new Color(123, 109, 99, 150);
        g.setColor(c);
        g.fillRect(0, 0, 10000, 10000);
        g.setFont(new Font(FONT_NAME, Font.BOLD, 60));

        int width = 600, height = 400;
        String text = "Game Over";
        FontMetrics fm = g.getFontMetrics();
        int totalWidth = fm.stringWidth(text);

        // absolutely centred
        int x = (width / 2) - (totalWidth / 2);
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);

        text = "Player " + (gameStatus == WIN ? currPlayer : currPlayer == 1 ? 2 : 1) + " wins.";
        g.setFont(new Font(FONT_NAME, Font.BOLD, 30));
        fm = g.getFontMetrics();
        totalWidth = fm.stringWidth(text);

        x = (width / 2) - (totalWidth / 2);
        y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g.setColor(Color.YELLOW);
        g.drawString(text, x, y + 60);
    }

    void println(Object o) {
        System.out.println(o.toString());
    }

    void println() {
        System.out.println();
    }

    void print(Object o) {
        System.out.print(o.toString());
    }

    void printf(String string, Object... o) {
        System.out.printf(string + "%n", o);
    }

    int random(int x, int y) {
        return (int)(Math.random() * (y - x) + x);
    }

    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();
        if (currPlayer == 1) {
            switch (key) {
                case KeyEvent.VK_A: key = LEFT; break;
                case KeyEvent.VK_W: key = UP; break;
                case KeyEvent.VK_D: key = RIGHT; break;
                case KeyEvent.VK_S: key = DOWN; break;
                default: return;
            }
        } else if (currPlayer == 2) {
            switch (key) {
                case KeyEvent.VK_LEFT:  key = LEFT; break;
                case KeyEvent.VK_UP:    key = UP; break;
                case KeyEvent.VK_RIGHT: key = RIGHT; break;
                case KeyEvent.VK_DOWN:  key = DOWN; break;
                default: return;
            }
        }

        makeAMove(key);
        repaint();
    }

    private void makeAMove(int pressed) {
        boolean vertical = pressed == UP || pressed == DOWN;

        currentWin = false;

        Tile[][] currBoard = carbonCopyBoard(currPlayer, this.board);
        oldBoard = makeCopy(currBoard);

        if (vertical)
            currBoard = transpose2DArray(currBoard);

        for (Tile[] row : currBoard) {
            ArrayList<Tile> list = extractTileArrayToList(row, pressed);
            list = addUpPairedTiles(list);
            setNewTileValue(list, row, pressed == LEFT || pressed == UP ? FORWARD : BACKWARD);
        }

        if (vertical) {
            currBoard = transpose2DArray(currBoard);
        }

        carbonPasteBoard(currPlayer, currBoard);

        if (currentWin) {
            // Player has reached winning tile
            gameStatus = WIN;
            packDownGame(); // win
            return;
        }

        if (moveWasMade(oldBoard, currBoard)) {
            currPlayer = currPlayer == 1 ? 2 : 1;

            newTileAppearTimer.start();
            // starting the timer will cause a pause, then a new tile is added
        }

        checkGameStatus(currBoard);

    }

    private void checkGameStatus(Tile[][] currBoard) {
        if (isBoardFull(currBoard) && !hasPossibleMove(currBoard)) {
            gameStatus = LOSE;
            packDownGame();
        }

    }

    private boolean hasPossibleMove(Tile[][] board) {
        // checks horizontally first, then transpose and check vertically

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length - 1; col++) {
                if (board[row][col].value == board[row][col + 1].value) {

                    return true;
                }
            }
        }
        board = transpose2DArray(board);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length - 1; col++) {
                if (board[row][col].value == board[row][col + 1].value) {

                    return true;
                }
            }
        }

        return false;
    }

    private boolean isBoardFull(Tile[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].value == 0) {

                    return false;
                }
            }
        }
        return true;
    }

    private boolean moveWasMade(Tile[][] oldBoard, Tile[][] board) {

        // returns true for any difference
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (!oldBoard[row][col].equals(board[row][col])) {

                    return true;
                }
            }
        }
        return false;
    }

    private Tile[][] makeCopy(Tile[][] board) {
        Tile[][] duplicate = new Tile[board.length][board[0].length];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                duplicate[row][col] = board[row][col].duplicate();
            }
        }
        return duplicate;
    }

    private Tile[][] transpose2DArray(Tile[][] board) {
        Tile[][] newBoard = new Tile[board[0].length][board.length];
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                newBoard[col][row] = board[row][col];
            }
        }
        return newBoard;
    }

    private ArrayList<Tile> extractTileArrayToList(Tile[] row, int pressed) {
        ArrayList<Tile> list = new ArrayList<Tile>();
        for (Tile t : row) {
            if (t.value > 0)
                list.add(t);
        }

        if (pressed == RIGHT || pressed == DOWN) {
            list = flipArray(list);
        }

        return list;
    }

    private void setNewTileValue(ArrayList<Tile> list, Tile[] row, int direction) {

        if (direction == FORWARD) {
            for (int i = 0; i < row.length; i++) {
                row[i].setValue(list.size() > 0 ? list.remove(0).value : 0);
            }
        } else {
            for (int i = row.length - 1; i >= 0; i--) {
                row[i].setValue(list.size() > 0 ? list.remove(0).value : 0);
            }
        }
    }

    private ArrayList<Tile> addUpPairedTiles(ArrayList<Tile> list) {

        if (list.size() > 1) {
            // if the row has more than 1 tile, add things up, otherwise nothing
            // to do

            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i).value == list.get(i + 1).value) {
                    currentWin = list.get(i).doubleUp() || currentWin;
                    list.remove(i + 1);
                }
            }
        }
        return list;
    }

    private ArrayList<Tile> flipArray(ArrayList<Tile> row) {
        Collections.reverse(row);
        return row;
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

}
