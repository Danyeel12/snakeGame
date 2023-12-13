package Snakes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final int CELL_SIZE = 10;
    private static final int DELAY = 100;

    private ArrayList<Point> snake;
    private Point food;
    private int direction;
    private boolean inGame;
    private int score = 0;
    private int highScore = 0;
    private Timer timer;

    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem restartItem;
    private JMenuItem quitItem;

    public SnakeGame() {
        snake = new ArrayList<>();
        direction = KeyEvent.VK_RIGHT;
        inGame = true;
        timer = new Timer(DELAY, this);

        snake.add(new Point(50, 50));
        snake.add(new Point(40, 50));
        snake.add(new Point(30, 50));

        generateFood();

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                changeDirection(e.getKeyCode());
            }
        });

        timer.start();
    }

    private void changeDirection(int key) {
        if ((key == KeyEvent.VK_LEFT) && (direction != KeyEvent.VK_RIGHT)) {
            direction = KeyEvent.VK_LEFT;
        } else if ((key == KeyEvent.VK_RIGHT) && (direction != KeyEvent.VK_LEFT)) {
            direction = KeyEvent.VK_RIGHT;
        } else if ((key == KeyEvent.VK_UP) && (direction != KeyEvent.VK_DOWN)) {
            direction = KeyEvent.VK_UP;
        } else if ((key == KeyEvent.VK_DOWN) && (direction != KeyEvent.VK_UP)) {
            direction = KeyEvent.VK_DOWN;
        }
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = (Point) head.clone();

        switch (direction) {
            case KeyEvent.VK_LEFT:
                newHead.x -= CELL_SIZE;
                break;
            case KeyEvent.VK_RIGHT:
                newHead.x += CELL_SIZE;
                break;
            case KeyEvent.VK_UP:
                newHead.y -= CELL_SIZE;
                break;
            case KeyEvent.VK_DOWN:
                newHead.y += CELL_SIZE;
                break;
        }

        if (newHead.x < 0) {
            newHead.x = WIDTH - CELL_SIZE;
        } else if (newHead.x >= WIDTH) {
            newHead.x = 0;
        } else if (newHead.y < 0) {
            newHead.y = HEIGHT - CELL_SIZE;
        } else if (newHead.y >= HEIGHT) {
            newHead.y = 0;
        }

        if (newHead.equals(food)) {
            snake.add(0, newHead);
            generateFood();
            score += 10;
            if (score > highScore) {
                highScore = score;
            }
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }

        if (checkSelfCollision()) {
            inGame = false;
            timer.stop();
        }
    }

    private boolean checkSelfCollision() {
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).equals(head)) {
                return true;
            }
        }
        return false;
    }

    private void generateFood() {
        Random rand = new Random();
        int x, y;

        do {
            x = rand.nextInt(WIDTH / CELL_SIZE) * CELL_SIZE;
            y = rand.nextInt(HEIGHT / CELL_SIZE) * CELL_SIZE;
        } while (snake.contains(new Point(x, y)));

        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            g.setColor(Color.RED);
            g.fillRect(food.x, food.y, CELL_SIZE, CELL_SIZE);

            g.setColor(Color.GREEN);
            for (Point segment : snake) {
                g.fillRect(segment.x, segment.y, CELL_SIZE, CELL_SIZE);
            }

            // Display the score and high score
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("High Score: " + highScore, 10, 40);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Game Over - Press 'R' to Restart", WIDTH / 2 - 100, HEIGHT / 2);
        }
    }

    private void restartGame() {
        snake.clear();
        snake.add(new Point(50, 50));
        snake.add(new Point(40, 50));
        snake.add(new Point(30, 50));
        direction = KeyEvent.VK_RIGHT;
        inGame = true;
        score = 0;
        generateFood();
        timer.restart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            move();
            repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            SnakeGame game = new SnakeGame();
            frame.add(game);

            JMenuBar menuBar = new JMenuBar();
            JMenu gameMenu = new JMenu("Game");
            JMenuItem restartItem = new JMenuItem("Restart");
            JMenuItem quitItem = new JMenuItem("Quit");

            restartItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.restartGame();
                }
            });

            quitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            gameMenu.add(restartItem);
            gameMenu.add(quitItem);
            menuBar.add(gameMenu);
            frame.setJMenuBar(menuBar);

            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
