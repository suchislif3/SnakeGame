import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

  static final int INFO_HEIGHT = 0;
  static final int GAME_HEIGHT = 600;

  static final int SCREEN_HEIGHT = INFO_HEIGHT + GAME_HEIGHT;
  static final int SCREEN_WIDTH = 600;

  static final int UNIT_SIZE = 25;
  static final int GAME_UNITS = (SCREEN_WIDTH * GAME_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
  static final int DELAY = 80;
  static final Color BACKGROUND_COLOR = Color.black;
  static final Color BODY_COLOR = new Color(4, 159, 4);
  static final Color HEAD_COLOR = new Color(22, 208, 22);
  static final Color APPLE_COLOR = new Color(210, 13, 13);
  static final Color GAME_OVER_COLOR = new Color(225, 4, 4);
  static final Color SCORE_COLOR = new Color(255, 255, 255);
  static final Font GAME_OVER_FONT = new Font("Consolas", Font.BOLD, 75);
  static final Font SCORE_FONT = new Font("Consolas", Font.BOLD, 25);
  static int score;
  final int[] x = new int[GAME_UNITS];
  final int[] y = new int[GAME_UNITS];
  int bodyParts = 5; // excl. head
  boolean changeDirectionAllowed = true;

  int appleX;
  int appleY;

  char direction = 'R';
  boolean running = false;
  Timer timer;
  Random random;

  GamePanel() {
    random = new Random();
    this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    this.setBackground(BACKGROUND_COLOR);
    this.setFocusable(true);
    this.addKeyListener(new MyKeyAdapter());
    startGame();
  }

  public void startGame() {
    newApple();
    running = true;
    timer = new Timer(DELAY, this);
    timer.start();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {

    //----- draw grid ------------------------------------------------------

    for (int i = 0; i < GAME_HEIGHT / UNIT_SIZE; i++) {
      g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, GAME_HEIGHT);
    }
    for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
      g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
    }


    //----------------------------------------------------------------------

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

    //-------DRAW SCORE------------------------------
    g.setColor(SCORE_COLOR);
    g.setFont(SCORE_FONT);
    String scoreText = "Score: " + score;

    // lining up text in the center of the screen
    FontMetrics metrics = getFontMetrics(g.getFont());
    g.drawString(scoreText, (SCREEN_WIDTH - metrics.stringWidth(scoreText)) / 2, g.getFont().getSize());
    //------------------------------------------------

    if(!running) {
      gameOver(g);
    }
  }

  public void newApple() {
    // TODO: apples shouldn't be generated in the snake's body
    appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
    appleY = random.nextInt((int)(GAME_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
  }

  public void move() {
    for (int i = bodyParts + 1; i > 0 ; i--) {
      x[i] = x[i - 1];
      y[i] = y[i - 1];
    }

    switch (direction) {
      case 'U':
        y[0] = y[0] - UNIT_SIZE;
        break;
      case 'D':
        y[0] = y[0] + UNIT_SIZE;
        break;
      case 'L':
        x[0] = x[0] - UNIT_SIZE;
        break;
      case 'R':
        x[0] = x[0] + UNIT_SIZE;
        break;
    }

    // checks if head crosses left border
    if(x[0] < 0) {
      x[0] = SCREEN_WIDTH - UNIT_SIZE;
    }
    // checks if head crosses right border
    if(x[0] >= SCREEN_WIDTH) {
      x[0] = 0;
    }
    // checks if head crosses top border
    if(y[0] < 0) {
      y[0] = GAME_HEIGHT - UNIT_SIZE;
    }
    // checks if head crosses bottom border
    if(y[0] >= GAME_HEIGHT) {
      y[0] = 0;
    }
    changeDirectionAllowed = true;
  }

  public void checkApple() {
    if((x[0] == appleX) && (y[0] == appleY)) {
      bodyParts++;
      score++;
      newApple();
    }
  }

  public void checkCollisions() {
    // TODO: collision check should take the new x and y arrays!
    //  make the new x and y as tempX tempY, check collision,
    //  and if OK, assign temX to x and tempY to y

    // checks if head collides with body
      int nextX = x[0];
      int nextY = y[0];

      switch (direction) {
        case 'U':
          nextY = y[0] - UNIT_SIZE;
          break;
        case 'D':
          nextY = y[0] + UNIT_SIZE;
          break;
        case 'L':
          nextX = x[0] - UNIT_SIZE;
          break;
        case 'R':
          nextX = x[0] + UNIT_SIZE;
          break;
      }

    for (int i = bodyParts; i > 0; i--) {
      if((nextX == x[i]) && (nextY == y[i])) {
        System.out.println("nextX: " + nextX + " = " + "x[" + i + "]: " + x[i]);
        System.out.println("nextY: " + nextY + " = " + "y[" + i + "]: " + y[i]);
        running = false;
      }
    }

    //---------BORDER COLLISION--------------
    /*
    // checks if head touches left border
    if(x[0] < 0) {
      running = false;
    }
    // checks if head touches right border
    if(x[0] >= SCREEN_WIDTH) {
      running = false;
    }
    // checks if head touches top border
    if(y[0] < 0) {
      running = false;
    }
    // checks if head touches bottom border
    if(y[0] >= GAME_HEIGHT) {
      running = false;
    }

     */
    //--------------------------------------

    if(!running) {
      timer.stop();
    }
  }

  public void gameOver(Graphics g) {
    // Game Over text
    g.setColor(GAME_OVER_COLOR);
    g.setFont(GAME_OVER_FONT);
    String gameOverText = "Game Over";

    // lining up text in the center of the screen
    FontMetrics metrics = getFontMetrics(g.getFont());
    g.drawString(gameOverText, (SCREEN_WIDTH - metrics.stringWidth(gameOverText)) / 2, GAME_HEIGHT / 2);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    checkCollisions();
    if(running) {
      move();
      checkApple();
    }
    repaint();
  }

  public class MyKeyAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      if (changeDirectionAllowed) {
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
}