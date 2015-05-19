package quick64;
import java.awt.*;
import java.util.HashMap;


public class Tile {
    private static final int MAX_TILE_VALUE = 512;
    int value, x, y;
    public static int WINNING_TILE = Game.WINNING_TILE;
    public static int tileSize = 80, tileRound = 12, tileMargin = 10;
    public static HashMap<Integer, Color> tileColours;
    public static HashMap<Integer, Integer> tileFontSizes;
    public static HashMap<Integer, Color> tileFontColours;
    public Font myFont;
    
    public Tile(int x, int y) {
        this.value = 0;
        this.x = x;
        this.y = y;
    }
    
    public void setValue(int value){
        this.value = value;
        if (value > 0) updateFont();
    }
    
    public boolean doubleUp(){
        value *= 2;
        if (value > 0) updateFont();
        
        return value == WINNING_TILE;
        
    }
    public void updateFont(){
        myFont = new Font(GameSingle.FONT_NAME, Font.BOLD, tileFontSizes.get(this.value));
    }
    public String toString() {
        return "[" + value + ']';
    }
    public Tile duplicate() {
        Tile t = new Tile(x,y);
        t.value = this.value;
        return t;
    }
    public boolean equals(Tile t){
        return t.value == this.value;
    }
    public void draw(Graphics g){
        
        g.setColor(tileColours.get(value));
        g.fillRoundRect(x, y, tileSize, tileSize, tileRound, tileRound);
        
        if (value == 0) return;
        int width = tileSize, height = tileSize;
        String text = String.valueOf(value);
        //text = "512";
        g.setFont(myFont);
        FontMetrics fm = g.getFontMetrics();
        int totalWidth = fm.stringWidth(text);
        
     // absolutely centred
        int x = (width / 2) - (totalWidth / 2);
        int y = (height - fm.getHeight()) / 2 + fm.getAscent(); 
        //x -= fm.stringWidth(text) / 2;
        
        g.setColor(tileFontColours.get(value));
        g.drawString(text, this.x + x, this.y + y);
        
    }
    
    public static void initialiseValues() {
        tileColours = new HashMap<Integer, Color>();
        tileColours.put(0, new Color(148, 142, 131));
        tileColours.put(2, new Color(239, 227, 222));
        tileColours.put(4, new Color(238, 223, 197));
        tileColours.put(8, new Color(247, 178, 123));
        
        Color tile16 = new Color(247, 150, 99);
        for (int i = 16; i < 128; i *= 2) {
            tileColours.put(i, tile16);
        }
        
        Color tile128 = new Color(238, 206, 115);
        for (int i = 128; i < 512; i *= 2) {
            tileColours.put(i, tile128);
        }
        tileColours.put(512, new Color(239, 198, 82));
        
        tileFontSizes = new HashMap<Integer, Integer>();
        int fontSize = 40;
        for (int i = 2; i <= MAX_TILE_VALUE; i *= 2) {
            tileFontSizes.put(i, fontSize);
            
            if (i == 128) fontSize -= 5;
        }
            
        
        tileFontColours = new HashMap<Integer, Color>();
        tileFontColours.put(2, new Color(123, 109, 99));
        tileFontColours.put(4, new Color(123, 109, 99));
        Color whiteFont = new Color(247,247,247);
        for (int i = 8; i <= MAX_TILE_VALUE; i *= 2 ) {
            tileFontColours.put(i, whiteFont);
        }
    }
}
