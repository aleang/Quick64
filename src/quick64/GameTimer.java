package quick64;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

public class GameTimer extends JLabel {
    
    public long startTime, currentTime, timeTaken;
    Timer gameTimer;
    
    
    public GameTimer() {
        super();
        super.setPreferredSize(new Dimension(100, 25));
        setText("Timer will start at first move.");
        setFont(new Font(GameSingle.FONT_NAME, Font.BOLD, 20));
        setForeground(Color.WHITE);
        
        gameTimer = new Timer(10, new ActionListener() {
            public void actionPerformed(ActionEvent e){
                tick();
            }
         });
    }
    public void reset() {
        setText("Timer will start at first move.");
    }
    public void startTimer() {
        gameTimer.start();
        startTime = System.nanoTime();
    }
    public void stopTimer() {
        gameTimer.stop();
    }
    public double getTime() {
        return Double.parseDouble(timeToString());
    }
    public void tick() {
        currentTime = System.nanoTime();
        timeTaken = currentTime - startTime;
        setText("Time: " + timeToString() + " s");
    }
    public String timeToString() {
        return String.format("%.3f", timeTaken / 1e9);
    }

    public boolean isTimerRunning() {
        return gameTimer.isRunning();
    }
}