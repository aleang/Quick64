package quick64;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class HighScoreManager {
    static HashMap<String, Double> map;

    static File hsFile;

    public static void initialise() {
        map = new HashMap<String, Double>();

        try {
            hsFile = new File("highscore.txt");
            Scanner sc = new Scanner(hsFile);

            while (sc.hasNext()) {
                map.put(sc.next(), sc.nextDouble());
            }
            sc.close();
           
        } catch (FileNotFoundException e) {
            System.err.println("Missing highscore.txt file");
        }
    }

    public static void addPlayer(double time){
        String name = " ";
        while (name.contains(" ") || name.length() == 0){
            name = (String)JOptionPane.showInputDialog(null,
                "Enter your name (no space) and press OK. Otherwise press Cancel",
                "High Score",
                JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (name == null) break;
            name = name.toLowerCase();
        }
        if (name != null && name.length() > 0) {
            if (map.containsKey(name) && time > map.get(name)){
                return;
            }
            
            map.put(name, time);
            
            System.err.println("high score board updated");
            saveToFile();
        }
        
    }
    
    private static void saveToFile() {
        PrintWriter pf;
        try {
            pf = new PrintWriter(hsFile);
            for (String player : map.keySet()) {
                pf.printf("%s %f%n", player, map.get(player));
            }
            pf.close();
        } catch (FileNotFoundException e) {
            System.err.println("Can't write to highscore.txt file");
        }
        
        
    }

    public static void showHighScore() {
        String[] columnNames = {
                "Player ", "Time"
        };

        Object[][] data = new Object[map.size()][2];
        int i = 0;
        for (String player : map.keySet()) {
            data[i][0] = player;
            data[i][1] = map.get(player);
            i++;
        }

        JTable table = new JTable(data, columnNames);
        
        table.setFont(new Font(GameSingle.FONT_NAME, Font.PLAIN, 15));
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        //table.setAutoCreateRowSorter(true);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);
        
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys); 
        
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(table.getTableHeader(), BorderLayout.PAGE_START);
        container.add(table, BorderLayout.CENTER);
        container.setPreferredSize(new Dimension(200, 300));
        container.requestFocusInWindow();
        
        JFrame frame = new JFrame();
        frame.getContentPane().add(container);
        frame.setLocation(150, 150);
        frame.pack();
        frame.setTitle("High Score");
        frame.setVisible(true);
    }
}
