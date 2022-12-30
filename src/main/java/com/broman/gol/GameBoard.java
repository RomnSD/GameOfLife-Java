package com.broman.gol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
        
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(dimension);
        setMaximumSize(dimension);

        // Mouse listener for adding cells and clearing the board
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

        // Key listeners for pausing, increasing and decreasing the speed
        getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "PAUSE");
        getActionMap().put("PAUSE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPaused(timer.isRunning());
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("UP"), "INCREASE SPEED");
        getActionMap().put("INCREASE SPEED", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer.getDelay() > SPEED_FACTOR) {
                    timer.setDelay(timer.getDelay() - SPEED_FACTOR);
                    repaint();
                }
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "DECREASE SPEED");
        getActionMap().put("DECREASE SPEED", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.setDelay(timer.getDelay() + SPEED_FACTOR);
                repaint();
            }
        });
    }

    public void setPaused(boolean paused) {
        if (paused && timer.isRunning()) {
            timer.stop();
        } else if (!paused && !timer.isRunning()) {
            timer.start();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        render((Graphics2D) graphics);
    }

    /**
     * Render the game board.
     */
    private void render(Graphics2D graphics) {
        // Make the drawing look nicer
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHints(hints);

        for (int index = 0; index < board.length; index++) {
            int x = (index % (getWidth() / CELL_SIZE)) * CELL_SIZE;
            int y = (index / (getWidth() / CELL_SIZE)) * CELL_SIZE;

            graphics.setColor(getCellAgeColor(board[index]));
            graphics.fillRect(x, y, CELL_SIZE, CELL_SIZE);

            graphics.setColor(Color.GRAY);
            graphics.drawRect(x, y, CELL_SIZE, CELL_SIZE);
        }

        graphics.setColor(FONT_COLOR);
        graphics.setFont(FONT);
        graphics.drawString("Delay: " + timer.getDelay() + "ms, Generation: " + generation + ", Population: " + population + (timer.isRunning() ? "" : " - PAUSED"), 10, 15);
    }

    private Color getCellAgeColor(int age) {
        if (age == 0) {
            return Color.WHITE;
        } else if (age < 10) {
            return new Color(20, 51, 14);
        } else if (age < 20) {
            return new Color(25, 63, 17);
        } else if (age < 30) {
            return new Color(34, 88, 21);
        } else if (age < 40) {
            return new Color(46, 118, 26);
        } else if (age < 50) {
            return new Color(54, 140, 29);
        } else if (age < 60) {
            return new Color(58, 152, 31);
        } else if (age < 70) {
            return new Color(62, 164, 32);
        } else if (age < 80) {
            return new Color(67, 178, 34);
        } else if (age < 90) {
            return new Color(72, 193, 35);
        } else if (age < 100) {
            return new Color(78, 210, 37);
        } else if (age < 110) {
            return new Color(84, 227, 38);
        } else if (age < 120) {
            return new Color(91, 246, 39);
        } else {
            return Color.PINK;
        }
    }

    /**
     * Calculate the next generation.
     * @param event the action event
     */
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
                // If n is outside the board, wrap it around
                // Example: if n is -1, add the board length to it to get the last index
                if (n < 0) {
                    n += board.length;
                }
                if (board[n % board.length] > 0) {
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
                    nextGeneration[index] = board[index] + 1;
                    //System.out.println(index + " has " + alive + " neighbours and is " + board[index] + " old");
                }
            } else if (alive > 3) {
                // Any live cell with more than three live neighbors dies, as if by overpopulation.
                nextGeneration[index] = 0;
            }

            if (nextGeneration[index] > 0) {
                population++;
            }
        }

        board = nextGeneration;
        repaint();
    }

}
