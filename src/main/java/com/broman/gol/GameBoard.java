package com.broman.gol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

public class GameBoard extends JPanel implements ActionListener {

    private int[] board;
    private Timer timer;
    private boolean paused;
    private int generation;
    private int population;

    private final static int CELL_SIZE = 10;
    private final static int SPEED_FACTOR = 10;
    private final static Color FONT_COLOR = Color.RED;
    private final static Font FONT = new Font("Arial", Font.BOLD, 15);

    public GameBoard(Dimension dimension) {
        board = new int[((int) dimension.getWidth() / CELL_SIZE) * ((int) dimension.getHeight() / CELL_SIZE)];
        timer = new Timer(100, this);
        timer.setDelay(100);
        timer.start();
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(dimension);
        setMaximumSize(dimension);

        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                // Fill the board with random cells
                if (event.getButton() == MouseEvent.BUTTON2) {
                    for (int i = 0; i < board.length; i++) {
                        board[i] = Math.random() < 0.5 ? 0 : 1;
                    }
                }

                // Clear the board
                if (event.getButton() == MouseEvent.BUTTON3) {
                    for (int i = 0; i < board.length; i++) {
                        board[i] = 0;
                    }
                }

                // Toggle a cell
                if (event.getButton() == MouseEvent.BUTTON1) {
                    int x = event.getX() / CELL_SIZE;
                    int y = event.getY() / CELL_SIZE;
                    int index = x + y * (getWidth() / CELL_SIZE);
                    board[index] = board[index] == 1 ? 0 : 1;
                }

                repaint();
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "PAUSE");
        getActionMap().put("PAUSE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;

                if (paused) {
                    timer.stop();
                } else {
                    timer.start();
                }
                repaint();
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("UP"), "UP");
        getActionMap().put("UP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.getDelay() > SPEED_FACTOR) {
                    timer.setDelay(timer.getDelay() - SPEED_FACTOR);
                    repaint();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
        getActionMap().put("DOWN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.setDelay(timer.getDelay() + SPEED_FACTOR);
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        render((Graphics2D) graphics);
    }

    private void render(Graphics2D graphics) {
        for (int index = 0; index < board.length; index++) {
            if (board[index] == 1) {
                graphics.setColor(Color.BLACK);
            } else {
                graphics.setColor(Color.WHITE);
            }

            int x = (index % (getWidth() / CELL_SIZE)) * CELL_SIZE;
            int y = (index / (getWidth() / CELL_SIZE)) * CELL_SIZE;

            graphics.fillRect(x, y, CELL_SIZE, CELL_SIZE);

            graphics.setColor(Color.GRAY);
            graphics.drawRect(x, y, CELL_SIZE, CELL_SIZE);
        }

        graphics.setColor(FONT_COLOR);
        graphics.setFont(FONT);
        graphics.drawString("Delay: " + timer.getDelay() + "ms, Generation: " + generation + ", Population: " + population + (paused ? " - PAUSED" : ""), 10, 15);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        int size = getWidth() / CELL_SIZE;
        int[] nextGeneration = new int[board.length];

        population = 0;
        generation++;

        for (int index = 0; index < board.length; index++) {
            int[] neighbours = new int[] {
                index - size - 1, index - size, index - size + 1,
                index - 1       , /** cell **/  index + 1       ,
                index + size - 1, index + size, index + size + 1
            };

            int alive = 0;

            for (int n : neighbours) {
                n = n % board.length;
                if (n < 0) {
                    n += board.length;
                }
                if (board[n] == 1) {
                    alive++;
                }
            }

            if (alive < 2) {
                // Any live cell with fewer than two live neighbors dies, as if by underpopulation.
                nextGeneration[index] = 0;
            } else if (alive == 2 || alive == 3) {
                if (board[index] == 0) {
                    if (alive == 3) {
                        // Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
                        nextGeneration[index] = 1;
                    }
                } else {
                    // Any live cell with two or three live neighbors lives on to the next generation.
                    nextGeneration[index] = 1;
                }
            } else if (alive > 3) {
                // Any live cell with more than three live neighbors dies, as if by overpopulation.
                nextGeneration[index] = 0;
            }

            if (nextGeneration[index] == 1) {
                population++;
            }
        }

        board = nextGeneration;
        repaint();
    }

}
