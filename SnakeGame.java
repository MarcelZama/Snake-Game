import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

//Date : 23/05/2024

public class SnakeGame extends JFrame implements ActionListener {
    private JPanel menuPanel;
    private GamePanel gamePanel;

    public SnakeGame() {
        setTitle("Snake Game          By: Marcel Zama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 1));

        JButton btn10x10 = new JButton("10x10");
        btn10x10.addActionListener(e -> startGame(10));
        JButton btn25x25 = new JButton("25x25");
        btn25x25.addActionListener(e -> startGame(25));
        JButton btn50x50 = new JButton("50x50");
        btn50x50.addActionListener(e -> startGame(50));
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));

        menuPanel.add(btn10x10);
        menuPanel.add(btn25x25);
        menuPanel.add(btn50x50);
        menuPanel.add(btnExit);

        add(menuPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startGame(int gridSize) {
        if (gamePanel != null) {
            remove(gamePanel);
        }
        gamePanel = new GamePanel(gridSize);
        add(gamePanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        gamePanel.requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void showMenu() {
        if (gamePanel != null) {
            remove(gamePanel);
        }
        add(menuPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Unused
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SnakeGame::new);
    }

    private class GamePanel extends JPanel implements ActionListener {
        private final int TILE_SIZE = 25;
        private final int SCREEN_WIDTH;
        private final int SCREEN_HEIGHT;
        private final int ALL_TILES;
        private final int DELAY = 140;

        private final int x[];
        private final int y[];

        private int bodyParts = 3;
        private int appleX;
        private int appleY;

        private char direction = 'R';
        private boolean running = true;
        private Timer timer;
        private Random random;
        private JButton tryAgainButton;
        private JButton menuButton;

        public GamePanel(int gridSize) {
            SCREEN_WIDTH = gridSize * TILE_SIZE;
            SCREEN_HEIGHT = gridSize * TILE_SIZE;
            ALL_TILES = (SCREEN_WIDTH * SCREEN_HEIGHT) / (TILE_SIZE * TILE_SIZE);

            x = new int[ALL_TILES];
            y = new int[ALL_TILES];

            random = new Random();
            setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
            setBackground(Color.white);
            setFocusable(true);
            addKeyListener(new MyKeyAdapter());
            setLayout(null);
            startGame();
        }

        public void startGame() {
            bodyParts = 13;
            for (int i = 0; i < bodyParts; i++) {
                x[i] = 0;
                y[i] = 0;
            }
            direction = 'R';
            running = true;
            spawnApple();
            timer = new Timer(DELAY, this);
            timer.start();
            if (tryAgainButton != null) {
                remove(tryAgainButton);
            }
            if (menuButton != null) {
                remove(menuButton);
            }
            requestFocusInWindow();
        }

        public void spawnApple() {
            appleX = random.nextInt((int) (SCREEN_WIDTH / TILE_SIZE)) * TILE_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / TILE_SIZE)) * TILE_SIZE;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g) {
            if (running) {
                g.setColor(Color.blue);
                g.fillRect(appleX, appleY, TILE_SIZE, TILE_SIZE);

                for (int i = 0; i < bodyParts; i++) {
                    if (i == 0) {
                        g.setColor(Color.darkGray);
                        g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
                    } else {
                        g.setColor(Color.gray);
                        g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
                    }
                }
            } else {
                gameOver(g);
            }
        }

        public void move() {
            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            switch (direction) {
                case 'U':
                    y[0] = y[0] - TILE_SIZE;
                    break;
                case 'D':
                    y[0] = y[0] + TILE_SIZE;
                    break;
                case 'L':
                    x[0] = x[0] - TILE_SIZE;
                    break;
                case 'R':
                    x[0] = x[0] + TILE_SIZE;
                    break;
            }
        }

        public void checkApple() {
            if ((x[0] == appleX) && (y[0] == appleY)) {
                bodyParts++;
                spawnApple();
            }
        }

        public void checkCollisions() {
            for (int i = bodyParts; i > 0; i--) {
                if ((x[0] == x[i]) && (y[0] == y[i])) {
                    running = false;
                }
            }

            if (x[0] < 0) {
                running = false;
            }

            if (x[0] >= SCREEN_WIDTH) {
                running = false;
            }

            if (y[0] < 0) {
                running = false;
            }

            if (y[0] >= SCREEN_HEIGHT) {
                running = false;
            }

            if (!running) {
                timer.stop();
            }
        }

        public void gameOver(Graphics g) {
            g.setColor(Color.red);
            g.setFont(new Font("Helvetica", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

            tryAgainButton = new JButton("Try Again");
            tryAgainButton.setBounds((SCREEN_WIDTH - 100) / 2, (SCREEN_HEIGHT / 2) + 50, 100, 30);
            tryAgainButton.addActionListener(e -> startGame());
            add(tryAgainButton);

            menuButton = new JButton("Menu");
            menuButton.setBounds((SCREEN_WIDTH - 100) / 2, (SCREEN_HEIGHT / 2) + 90, 100, 30);
            menuButton.addActionListener(e -> showMenu());
            add(menuButton);

            tryAgainButton.setVisible(true);
            menuButton.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (running) {
                move();
                checkApple();
                checkCollisions();
            }
            repaint();
        }

        private class MyKeyAdapter extends KeyAdapter {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        if (direction != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        if (direction != 'D') {
                            direction = 'U';
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        if (direction != 'U') {
                            direction = 'D';
                        }
                        break;
                }
            }
        }
    }
}
