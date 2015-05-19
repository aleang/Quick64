package quick64;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;


public class GameSingle extends JPanel implements KeyListener, MouseListener {

    public static String FONT_NAME = "Clear Sans";
    public final int WINNING_TILE = Game.WINNING_TILE;
    
    int LEFT = 0, UP = 1, RIGHT = 2, DOWN = 3, 
            FORWARD = 0, BACKWARD = 1, 
            HORIZONTAL = 0, VERTICAL = 1,
            NEW_GAME = 0, WIN = 1, LOSE = 2,
            ANIMATION_DUR = 100;
    
    int grid, gridWidth, gameStatus;     // 0 new game, 1 finished Win, 2 finished Lose
    Game frame;
    JMenuBar menuBar;
    Tile[][] board, oldBoard;
    Point startLocationBoard;
    Timer newTileAppearTimer;
    GameTimer timerBar;
    Point mouseStart, mouseEnd;
    
    public GameSingle(Game frame) {
	this.frame = frame;
        this.grid = Game.GRID;
        this.gridWidth = Game.GRID_WIDTH;
        menuBar = addMenuButtons(new JMenuBar());
        setLayout(new BorderLayout());
        add(menuBar, BorderLayout.NORTH);

        Tile.initialiseValues();
        HighScoreManager.initialise();

        timerBar = new GameTimer();
        add(timerBar, BorderLayout.SOUTH);

        startLocationBoard = new Point(80,80);
        setUpNewGame();

    }

    private void setUpNewGame() {
        addKeyListener(this);
        addMouseListener(this);
        gameStatus = NEW_GAME;
        timerBar.reset();

        // create the board
        int margin = Tile.tileMargin, size = Tile.tileSize;
        int x = startLocationBoard.x, y = startLocationBoard.y;
        board = new Tile[grid][gridWidth];
        for (int i = 0 ; i < grid; i++){
            for (int j = 0; j < board[i].length; j++){
                board[i][j] = new Tile(x, y);
                x += margin + size;
            }
            y += margin + size;
            x = startLocationBoard.x;
        }

        // add two random tile with values 2 or 4
        addNewTile(board);
        addNewTile(board);
        
        newTileAppearTimer = new Timer(ANIMATION_DUR, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNewTile(board);
                newTileAppearTimer.stop();
                repaint();
            }
        });


    }
    private void packDownGame() {
        timerBar.stopTimer();

        if (gameStatus == WIN) {
            HighScoreManager.addPlayer(timerBar.getTime());
        } else {
            paintGameOver();
        }
        removeKeyListener(this);
    }
    private void addNewTile(Tile[][] board) {
	    Point p = getFreeSpot(board);
        board[p.x][p.y].setValue(random(1,3) * 2);
    }

    private Point getFreeSpot(Tile[][] board) {
	    int x = random(0, board.length), y = random(0, board[0].length);
	    while (board[x][y].value != 0) {
	        x = random(0, board.length);
	        y = random(0, board[0].length);
	    }
	    return new Point(x,y);
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
	    
	    JMenuItem newOtherGame = new JMenuItem("2-Player");
            
        newOtherGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                frame.setVisible(false);
                Game gg = new Game(Game.MULTI);
            }
        });
        
        newOtherGame.setFont(font);
        menuBar.add(newOtherGame);
        
        JMenuItem highScore = new JMenuItem("Highscore");
        highScore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                HighScoreManager.showHighScore();
                repaint();
            }
        });
        highScore.setFont(font);
        menuBar.add(highScore);
        
        return menuBar;
    }

	
	
    public void paintComponent(Graphics g){
		g.setColor(new Color(123, 109, 99));
		g.fillRect(0, 0, 10000, 10000);
	    for (Tile[] row: board) {
	        for (Tile t: row) {
	            t.draw(g);
	        }
	    }
	    
	    
	}
	
    public void paintGameOver() {
        Graphics g = this.getGraphics();
        
        Color c = new Color(123, 109, 99, 150);
        g.setColor(c);
        g.fillRect(0, 0, 10000, 10000);
        g.setFont(new Font(FONT_NAME, Font.BOLD, 60));
        
        
        int width = 400, height = 400;
        String text = "Game Over";
        FontMetrics fm = g.getFontMetrics();
        int totalWidth = fm.stringWidth(text);
        
     // absolutely centred
        int x = (width / 2) - (totalWidth / 2);
        int y = (height - fm.getHeight()) / 2 + fm.getAscent(); 
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }
    
    
    
    
	void println(Object o){System.out.println(o.toString());}
	void println(){System.out.println();}
	void print(Object o){System.out.print(o.toString());}
	int random(int x,int y){return (int)(Math.random()*(y-x)+x);}

    public void keyPressed(KeyEvent e) {
        if (! timerBar.isTimerRunning()) timerBar.startTimer();
        int pressed = e.getKeyCode();
        switch (pressed) {
            case KeyEvent.VK_LEFT : pressed = LEFT; break;
            case KeyEvent.VK_UP : pressed = UP; break;
            case KeyEvent.VK_RIGHT : pressed = RIGHT; break;
            default: pressed = DOWN;
        }
        
        boolean vertical =  pressed == UP || pressed == DOWN;
        
        oldBoard = makeCopy(board);
        
        Tile[][] board = this.board;
        if (vertical) board = transpose2DArray(board);
        
        for (Tile[] row : board) {
            ArrayList<Tile> list = extractTileArrayToList(row, pressed);
            list = addUpPairedTiles(list);
            setNewTileValue(list, row, pressed == LEFT || pressed == UP ? FORWARD : BACKWARD);
        }
        if (gameStatus == WIN) {
            // Player has reached winning tile
            repaint();
            gameStatus = WIN;
            packDownGame(); // win
            return;
        }
    
        if (vertical) board = transpose2DArray(board);
        this.board = board;
        
        if (moveWasMade(oldBoard, board)) {
            newTileAppearTimer.start(); // starting the timer will cause a pause, then a new tile is added
            
        } else if (isBoardFull(board) && ! hasPossibleMove(board)) {
            packDownGame(); // lose
            gameStatus = LOSE;
            return;
        }
        
        repaint();
    }
    private boolean hasPossibleMove(Tile[][] board) {
        // checks horizontally first, then transpose and check vertically
        
        for (int i : new int[2]){
            
            for (int row = 0; row < board.length; row++){
                for (int col = 0; col < board[row].length - 1; col++){
                    if (board[row][col].value == board[row][col + 1].value){
                        return true;
                    }
                }
            }
            board = transpose2DArray(board);
            
        }
     
        
        return false;
    }

    private boolean isBoardFull(Tile[][] board) {
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++){
                if (board[row][col].value == 0){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean moveWasMade(Tile[][] oldBoard, Tile[][] board) {
        // returns true for any difference
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++){
                if (! oldBoard[row][col].equals(board[row][col])){
                    return true;
                }
            }
        }
        return false;
    }

    private Tile[][] makeCopy(Tile[][] board) {
        Tile[][] duplicate = new Tile[board.length][board[0].length];
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++){
                duplicate[row][col] = board[row][col].duplicate();
            }
        }
        return duplicate;
    }

    private Tile[][] transpose2DArray(Tile[][] board) {
        Tile[][] newBoard = new Tile[board[0].length][board.length];
        for (int row = 0; row < board.length; row++){
            for (int col = 0; col < board[row].length; col++){
                newBoard[col][row] = board[row][col];
            }
        }
        return newBoard;
    }

    private ArrayList<Tile> extractTileArrayToList(Tile[] row, int pressed){
        ArrayList<Tile> list = new ArrayList<Tile>();
        for (Tile t: row) {
            if (t.value > 0) list.add(t);
        }
        
        if (pressed == RIGHT || pressed == DOWN) {
            list = flipArray(list);
        }
        
        return list;
    }
    private void setNewTileValue(ArrayList<Tile> list, Tile[] row, int direction) {
        if ( direction == FORWARD ){
            for (int i = 0; i < row.length; i++){
                row[i].setValue( list.size() > 0 ? list.remove(0).value : 0);
            }  
        } else {
            for (int i = row.length - 1; i >= 0; i--){
                row[i].setValue( list.size() > 0 ? list.remove(0).value : 0);
            }
        }
    }

    private ArrayList<Tile> addUpPairedTiles(ArrayList<Tile> list) {
        if (list.size() > 0 ){
            // if the row has more than 1 tile, add things up, otherwise no point
            
            for (int i = 1; i < list.size(); i++) {
                if (list.get(i).value == list.get(i-1).value) {
                    gameStatus = list.get(i-1).doubleUp() ? WIN : NEW_GAME;
                    list.remove(i);
                    i--;
                }
            }
        }
        return list;
    }

    private ArrayList<Tile> flipArray(ArrayList<Tile> row) {
        ArrayList<Tile> ret = new ArrayList<Tile>();
        for (Tile t: row) {
            ret.add(0, t);
        }
        return ret;
    }
    public void keyReleased(KeyEvent arg0) {}
    public void keyTyped(KeyEvent arg0) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseStart = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseEnd = e.getPoint();
        
        int diffX = mouseEnd.x - mouseStart.x;
        int diffY = mouseEnd.y - mouseStart.y;
        
        if (Math.abs(diffX) + Math.abs(diffY) > 50) {
            try {
                Robot robo = new Robot();
                
                if (Math.abs(diffX) > Math.abs(diffY)){
                    // horizontal
                    if (diffX > 0) {
                        robo.keyPress(KeyEvent.VK_RIGHT);
                    } else {
                        robo.keyPress(KeyEvent.VK_LEFT);
                    }
                } else {
                    // vertical
                    if (diffY > 0) {
                        robo.keyPress(KeyEvent.VK_DOWN);
                    } else {
                        robo.keyPress(KeyEvent.VK_UP);
                    }
                }
            } catch (Exception e1){
                System.err.println("Errors with robot");
            }
        }
        
        
        
    }

}