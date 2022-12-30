package com.broman.gol;

import java.awt.EventQueue;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game extends JFrame {

    public static final int WINDOW_WIDTH = 950;
    public static final int WINDOW_HEIGHT = 650;

    public static final int BOARD_WIDTH = WINDOW_WIDTH - 50;
    public static final int BOARD_HEIGHT = WINDOW_HEIGHT - 50;

    public static void start() {
        EventQueue.invokeLater(() -> {
            Game game = new Game();
            game.setVisible(true);
        });
    }

    public Game() {
        iniUI();
    }

    private void iniUI() {
        setTitle("Game Of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(new GameBoard(new Dimension(BOARD_WIDTH, BOARD_HEIGHT)));

        JOptionPane.showMessageDialog(this, 
            """
                1. Press SPACE to start/pause the game.                            
                2. Press UP to increase the game speed.                              
                3. Press DOWN to decrease the game speed.                               
                4. Press LEFT MOUSE BUTTON to toggle a cell.                      
                5. Press MIDDLE MOUSE BUTTON to fill the board with random cells. 
                6. Press RIGHT MOUSE BUTTON to clear the board.
            """, "Instructions for Game of Life", JOptionPane.INFORMATION_MESSAGE);
    }
    
}
