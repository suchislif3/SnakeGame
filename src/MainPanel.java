import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

public class MainPanel extends JPanel {

  static final int CONTROL_PANEL_HEIGHT = 100;
  static final int GAME_HEIGHT = 600;
  static final int SCREEN_HEIGHT = CONTROL_PANEL_HEIGHT + GAME_HEIGHT;
  static final int SCREEN_WIDTH = 900;

  static int score;
  char direction = 'R';
  boolean changeDirectionAllowed = true;

  boolean isGameStarted = false;
  boolean isGameRunning = false;
  boolean isGameOver = false;

  Random random;
  ControlPanel controlPanel;
  GamePanel gamePanel;

  MainPanel() {
    random = new Random();
    controlPanel = new ControlPanel();
    gamePanel = new GamePanel();
    this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    this.setFocusable(true);
    this.addKeyListener(new MyKeyAdapter());
    this.setLayout(null);
    controlPanel.setBounds(0, 0, SCREEN_WIDTH, CONTROL_PANEL_HEIGHT);
    gamePanel.setBounds(0, CONTROL_PANEL_HEIGHT, SCREEN_WIDTH, GAME_HEIGHT);
    this.add(controlPanel);
    this.add(gamePanel);
  }



  public class MyKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      if(!isGameStarted) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_ENTER:
            if (isGameOver) {
              gamePanel.restartGame();
            } else {
              gamePanel.startGame();
            }
            break;
        }
      }
      if(isGameStarted) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_SPACE:
            isGameRunning = !isGameRunning;
            break;
          case KeyEvent.VK_ESCAPE:
            if(isGameRunning) {
              isGameRunning = false;
            }
        }
      }
      if (changeDirectionAllowed && isGameRunning) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_A:
          case KeyEvent.VK_LEFT:
            if(direction != 'R') {
              direction = 'L';
            }
            break;
          case KeyEvent.VK_D:
          case KeyEvent.VK_RIGHT:
            if(direction != 'L') {
              direction = 'R';
            }
            break;
          case KeyEvent.VK_W:
          case KeyEvent.VK_UP:
            if(direction != 'D') {
              direction = 'U';
            }
            break;
          case KeyEvent.VK_S:
          case KeyEvent.VK_DOWN:
            if(direction != 'U') {
              direction = 'D';
            }
            break;
        }
      changeDirectionAllowed = false;
      }
    }
  }

  public class ControlPanel extends JPanel {
    static final Color CONTROL_PANEL_BG_COLOR = Color.lightGray;
    static final Color SCORE_COLOR = new Color(59, 59, 59);
    static final Font SCORE_FONT = new Font("Consolas", Font.BOLD, 25);
    ControlPanel() {
      this.setPreferredSize(new Dimension(SCREEN_WIDTH, CONTROL_PANEL_HEIGHT));
      this.setBackground(CONTROL_PANEL_BG_COLOR);
      this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawScore(g);
    }
    public void drawScore (Graphics g) {
      //-------DRAW SCORE------------------------------
      g.setColor(SCORE_COLOR);
      g.setFont(SCORE_FONT);
      String scoreText = "Score: " + score;

      // lining up text in the center of the screen
      FontMetrics metrics = getFontMetrics(g.getFont());
      g.drawString(scoreText, (SCREEN_WIDTH - metrics.stringWidth(scoreText)) / 2, CONTROL_PANEL_HEIGHT - 10);
      //------------------------------------------------
    }

  }

  public class GamePanel extends JPanel implements ActionListener {

    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * GAME_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 80;
    static final Color GAME_BG_COLOR = Color.black;
    static final Color GRID_COLOR = new Color(42, 42, 42);
    static final Color BODY_COLOR = new Color(4, 159, 4);
    static final Color HEAD_COLOR = new Color(22, 208, 22);
    static final Color APPLE_COLOR = new Color(210, 13, 13);
    static final Color GAME_OVER_COLOR = new Color(225, 4, 4);
    static final Color PAUSE_COLOR = new Color(225, 214, 4);
    static final Font MESSAGE_FONT = new Font("Consolas", Font.BOLD, 75);
    Timer timer;

    // SNAKE
    int[] x = new int[GAME_UNITS];
    int[] y = new int[GAME_UNITS];
    int[] nextX = new int [x.length];
    int[] nextY = new int [y.length];
    int bodyParts = 5; // excl. head

    // APPLE
    int appleX;
    int appleY;

    GamePanel() {
      this.setPreferredSize(new Dimension(SCREEN_WIDTH, GAME_HEIGHT));
      this.setBackground(GAME_BG_COLOR);
      this.setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      draw(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      calculateNextPosition();
      checkCollisions();
      if(isGameRunning) {
        move();
        checkApple();
      }
      repaint();
      //controlPanel.validate();
      controlPanel.repaint();
    }

    public void initializeGame() {
      x = new int[GAME_UNITS];
      y = new int[GAME_UNITS];
      nextX = new int [x.length];
      nextY = new int [y.length];
      bodyParts = 5; // excl. head
      direction = 'R';
      changeDirectionAllowed = true;
      score = 0;
    }
    public void startGame() {
      newApple();
      isGameStarted = true;
      isGameRunning = true;
      isGameOver = false;
      timer = new Timer(DELAY, gamePanel);
      timer.start();
    }

    public  void restartGame() {
      initializeGame();
      isGameStarted = true;
      isGameRunning = true;
      isGameOver = false;
      timer.start();
    }

    public void gameOver() {
      isGameStarted = false;
      isGameRunning = false;
      isGameOver = true;
      timer.stop();
    }

    public void draw(Graphics g) {
      //----- DRAW GRID -------------------------------
      // vertical lines
      g.setColor(GRID_COLOR);
      for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
        g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, GAME_HEIGHT);
      }
      // horizontal lines
      for (int i = 0; i < GAME_HEIGHT / UNIT_SIZE; i++) {
        g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
      }
      //-----------------------------------------------

      //--------DRAW APPLE-----------------------------
      g.setColor(APPLE_COLOR);
      g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
      //-----------------------------------------------

      //--------DRAW SNAKE-----------------------------
      for(int i = 0; i <= bodyParts; i++) {
        if(i == 0) {
          g.setColor(HEAD_COLOR);
        } else {
          // single color body
          g.setColor(BODY_COLOR);

          // random multicolor body
          //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        }
        g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
      }
      //-----------------------------------------------

      //--------DRAW MESSAGE---------------------------
      if(isGameOver) {
        message(g, GAME_OVER_COLOR, "Game Over");
        return;
      }
      if(!isGameStarted) {
        message(g, PAUSE_COLOR, "Press ENTER to start");
        return;
      }
      if(!isGameRunning) {
        message(g, PAUSE_COLOR, "Pause");
      }
      //-----------------------------------------------
    }

    public void newApple() {
      // checks if apple is under the snake's body
      int newAppleX;
      int newAppleY;
      boolean isAppleUnderTheSnakesBody;
      do {
        isAppleUnderTheSnakesBody = false;
        newAppleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        newAppleY = random.nextInt((int)(GAME_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        for (int i = 0; i < bodyParts + 1; i++) {
          if (newAppleX == x[i] && newAppleY == y[i]){
            isAppleUnderTheSnakesBody = true;
            break;
          }
        }
      } while(isAppleUnderTheSnakesBody);

      appleX = newAppleX;
      appleY = newAppleY;
    }

    public void move() {
      x = Arrays.copyOf(nextX, nextX.length);
      y = Arrays.copyOf(nextY, nextY.length);
      changeDirectionAllowed = true;
    }

    private void calculateNextPosition() {
      for (int i = bodyParts + 1; i > 0 ; i--) {
        nextX[i] = x[i - 1];
        nextY[i] = y[i - 1];
      }

      switch (direction) {
        case 'U':
          nextY[0] = y[0] - UNIT_SIZE;
          break;
        case 'D':
          nextY[0] = y[0] + UNIT_SIZE;
          break;
        case 'L':
          nextX[0] = x[0] - UNIT_SIZE;
          break;
        case 'R':
          nextX[0] = x[0] + UNIT_SIZE;
          break;
      }

      // checks if head crosses left border
      if(nextX[0] < 0) {
        nextX[0] = SCREEN_WIDTH - UNIT_SIZE;
      }
      // checks if head crosses right border
      if(nextX[0] >= SCREEN_WIDTH) {
        nextX[0] = 0;
      }
      // checks if head crosses top border
      if(nextY[0] < 0) {
        nextY[0] = GAME_HEIGHT - UNIT_SIZE;
      }
      // checks if head crosses bottom border
      if(nextY[0] >= GAME_HEIGHT) {
        nextY[0] = 0;
      }
    }

    public void checkApple() {
      if((x[0] == appleX) && (y[0] == appleY)) {
        bodyParts++;
        score++;
        newApple();
      }
    }

    public void checkCollisions() {
      // checks if head collides with body
      for (int i = bodyParts; i > 0; i--) {
        if((nextX[0] == nextX[i]) && (nextY[0] == nextY[i])) {
          gameOver();
          break;
        }
      }
    }

    public void message(Graphics g, Color color, String message) {
      g.setColor(color);
      g.setFont(MESSAGE_FONT);

      // lining up text in the center of the screen
      FontMetrics metrics = getFontMetrics(g.getFont());
      g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, GAME_HEIGHT / 2);
    }
  }
}
