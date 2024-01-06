package org.example;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

@Data
@EqualsAndHashCode(callSuper = true)
public class Model extends JPanel implements ActionListener {
  private Dimension dimension;
  private final Font smallFont = new Font("Arial", Font.BOLD, 14);
  private boolean isRunning = false;
  private boolean hasDied = false;

  private final int BLOCK_SIZE = 24;
  private final int N_BLOCKS = 15;
  private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
  private final int MAX_GHOSTS = 12;
  private final int PACMAN_SPEED = 6;

  private int N_GHOSTS = 6;
  private int lives, score;
  private int[] dx, dy;
  private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

  private Image heart, ghost;
  private Image up, down, left, right;

  private int pacman_x, pacman_y, pacman_dx, pacman_dy;
  private int req_dx, req_dy;

  private final int[] validSpeeds = {1, 2, 3, 4, 6, 8};
  private final int maxSpeed = 6;
  private int currentSpeed = 3;
  private short[] screenData;
  private Timer timer;

  // 0: blue obstacles, 16: white dots
  // 1: left border, 2: top border, 4: right border, 8: bottom border
  private final short[] mapData = {
    19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
    17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
    0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
    19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
    17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
    17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
    21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 20,
    17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
  };

  public Model() {
    loadImages();
    initVariables();
    addKeyListener(new TAdapter());
    setFocusable(true);
    initGame();
  }

  private void loadImages() {
    URL heartImageUrl = this.getClass().getResource("src/main/resources/images/heart.png");
    URL ghostImageUrl = this.getClass().getResource("src/main/resources/images/ghost.gif");
    URL upImageUrl = this.getClass().getResource("src/main/resources/images/up.gif");
    URL downImageUrl = this.getClass().getResource("src/main/resources/images/down.gif");
    URL leftImageUrl = this.getClass().getResource("src/main/resources/images/left.gif");
    URL rightImageUrl = this.getClass().getResource("src/main/resources/images/right.gif");

    if (heartImageUrl == null ||
      ghostImageUrl == null ||
      upImageUrl == null ||
      downImageUrl == null ||
      leftImageUrl == null ||
      rightImageUrl == null
    ) return;

    heart = new ImageIcon(heartImageUrl).getImage();
    ghost = new ImageIcon(ghostImageUrl).getImage();
    up = new ImageIcon(upImageUrl).getImage();
    down = new ImageIcon(downImageUrl).getImage();
    left = new ImageIcon(leftImageUrl).getImage();
    right = new ImageIcon(rightImageUrl).getImage();
  }

  // TODO: explain those variables
  private void initVariables() {
    screenData = new short[N_BLOCKS * N_BLOCKS];
    dimension = new Dimension(400, 600);
    dx = new int[4];
    dy = new int[4];
    ghost_x = new int[MAX_GHOSTS];
    ghost_dx = new int[MAX_GHOSTS];
    ghost_y = new int[MAX_GHOSTS];
    ghost_dy = new int[MAX_GHOSTS];
    ghostSpeed = new int[MAX_GHOSTS];

    timer = new Timer(40, this);
    timer.start();
  }

  private void initGame() {
    lives = 3;
    score = 0;
//    initLevel();
    N_GHOSTS = 6;
    currentSpeed = 3;
  }

  private void initLevel() {
    System.arraycopy(mapData, 0, screenData, 0, N_BLOCKS * N_BLOCKS - 1);
  }

  private void playGame(Graphics2D graphics2D) {

    if (!hasDied) {
      movePacman();
      drawPacman(graphics2D);
      moveGhosts(graphics2D);
      checkMaze();
    } else {
      death();
    }
  }

  private void movePacman() {

    int pos;
    short ch;

    // get current position of pacman
    if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
      pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE);
      ch = screenData[pos];

      // pacman is not at blue obstacles
      if (isNotAtBlueObstacle(ch)) {
        screenData[pos] = (short) (ch & 15);
        score++;
      }

      // check for valid move request
      if (req_dx != 0 || req_dy != 0) {
        if (isValidMoveRequest(req_dx, req_dy, ch)) {
          pacman_dx = req_dx;
          pacman_dy = req_dy;
        }
      }

      // check for standstill
      if (isInvalidMoveRequest(pacman_dx, pacman_dy, ch)) {
        // if can move anymore, place pacman at the origin
        pacman_dx = 0;
        pacman_dy = 0;
      }
    }

    // new x = current x + speed * delta x
    // new y = current y + speed * delta y
    pacman_x = pacman_x + PACMAN_SPEED * pacman_dx;
    pacman_y = pacman_y + PACMAN_SPEED * pacman_dy;
  }

  private boolean isNotAtBlueObstacle(short currentPosition) {
    return (currentPosition & 16) != 0;
  }

  private boolean isValidMoveRequest(int req_dx, int req_dy, short currentPosition) {
    return !((req_dx == -1 && req_dy == 0 && (currentPosition & 1) != 0) // if move to left, current position should not have left border
      || (req_dx == 1 && req_dy == 0 && (currentPosition & 4) != 0) // if move to right, current position should not have right border
      || (req_dx == 0 && req_dy == -1 && (currentPosition & 2) != 0) // if move to top, current position should not have top border
      || (req_dx == 0 && req_dy == 1 && (currentPosition & 8) != 0) // if move to bottom, current position should not have bottom border
    );
  }

  private boolean isInvalidMoveRequest(int req_dx, int req_dy, short currentPosition) {
    return !isValidMoveRequest(req_dx, req_dy, currentPosition);
  }

  private void drawPacman(Graphics2D graphics2D) {

    // change image based on pacman direction
    if (req_dx == -1) {
      graphics2D.drawImage(left, pacman_x + 1, pacman_y + 1, this);
    } else if (req_dx == 1) {
      graphics2D.drawImage(right, pacman_x + 1, pacman_y + 1, this);
    } else if (req_dy == -1) {
      graphics2D.drawImage(up, pacman_x + 1, pacman_y + 1, this);
    } else {
      graphics2D.drawImage(down, pacman_x + 1, pacman_y + 1, this);
    }
  }

  private void moveGhosts(Graphics2D graphics2D) {
    int pos;
    int count;

    for (int i = 0; i < N_GHOSTS; i++) {
      if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
        pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (ghost_y[i] / BLOCK_SIZE);

        count = 0;

        if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
          dx[count] = -1;
          dy[count] = 0;
          count++;
        }

        if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
          dx[count] = 0;
          dy[count] = -1;
          count++;
        }

        if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
          dx[count] = 1;
          dy[count] = 0;
          count++;
        }

        if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
          dx[count] = 0;
          dy[count] = 1;
          count++;
        }

        if (count == 0) {

          if ((screenData[pos] & 15) == 15) {
            ghost_dx[i] = 0;
            ghost_dy[i] = 0;
          } else {
            ghost_dx[i] = -ghost_dx[i];
            ghost_dy[i] = -ghost_dy[i];
          }

        } else {

          count = (int) (Math.random() * count);

          if (count > 3) {
            count = 3;
          }

          ghost_dx[i] = dx[count];
          ghost_dy[i] = dy[count];
        }

      }

      ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
      ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
      drawGhost(graphics2D, ghost_x[i] + 1, ghost_y[i] + 1);

      if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
        && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
        && isRunning) {

        hasDied = true;
      }
    }
  }

  private void drawGhost(Graphics2D graphics2D, int x, int y) {
    graphics2D.drawImage(ghost, x, y, this);
  }

  private void checkMaze() {

    int i = 0;
    boolean finished = true;

    while (i < N_BLOCKS * N_BLOCKS && finished) {

      if ((screenData[i]) != 0) {
        finished = false;
      }

      i++;
    }

    if (finished) {

      score += 50;

      if (N_GHOSTS < MAX_GHOSTS) {
        N_GHOSTS++;
      }

      if (currentSpeed < maxSpeed) {
        currentSpeed++;
      }

      initLevel();
    }
  }

  private void death() {

    lives--;

    if (lives == 0) {
      isRunning = false;
    }

    continueLevel();
  }

  private void continueLevel() {

    int dx = 1;
    int random;

    for (int i = 0; i < N_GHOSTS; i++) {
      ghost_y[i] = 4 * BLOCK_SIZE;
      ghost_x[i] = 4 * BLOCK_SIZE;
      ghost_dy[i] = 0;
      ghost_dx[i] = dx;
      dx = -dx;
      random = (int) (Math.random() * (currentSpeed + 1));

      if (random > currentSpeed) {
        random = currentSpeed;
      }

      ghostSpeed[i] = validSpeeds[random];
    }

    pacman_x = 7 * BLOCK_SIZE;
    pacman_y = 11 * BLOCK_SIZE;
    pacman_dx = 0;
    pacman_dy = 0;
    req_dx = 0;
    req_dy = 0;
    hasDied = false;
  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D graphics2D = (Graphics2D) g;
    graphics2D.setColor(Color.black);
    graphics2D.fillRect(0, 0, dimension.width, dimension.height);

    drawMaze(graphics2D);
    drawScore(graphics2D);

    if (isRunning) {
      playGame(graphics2D);
    } else {
      showIntroScreen(graphics2D);
    }
    Toolkit.getDefaultToolkit().sync();
  }

  private void drawMaze(Graphics2D graphics2D) {
    short i = 0;
    int x, y;

    for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
      for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

        graphics2D.setColor(new Color(0, 72, 251));
        graphics2D.setStroke(new BasicStroke(5));

        if ((mapData[i] == 0)) {
          graphics2D.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        }

        if ((screenData[i] & 1) != 0) {
          graphics2D.drawLine(x, y, x, y + BLOCK_SIZE - 1);
        }

        if ((screenData[i] & 2) != 0) {
          graphics2D.drawLine(x, y, x + BLOCK_SIZE - 1, y);
        }

        if ((screenData[i] & 4) != 0) {
          graphics2D.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
            y + BLOCK_SIZE - 1);
        }

        if ((screenData[i] & 8) != 0) {
          graphics2D.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
            y + BLOCK_SIZE - 1);
        }

        if ((screenData[i] & 16) != 0) {
          graphics2D.setColor(new Color(255, 255, 255));
          graphics2D.fillOval(x + 10, y + 10, 6, 6);
        }

        i++;
      }
    }
  }

  private void drawScore(Graphics2D graphics2D) {

    graphics2D.setFont(smallFont);
    graphics2D.setColor(new Color(5, 181, 79));
    String s = "Score: " + score;
    graphics2D.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

    for (int i = 0; i < lives; i++) {
      graphics2D.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
    }
  }

  private void showIntroScreen(Graphics2D graphics2D) {

    String start = "Press SPACE to start";
    graphics2D.setColor(Color.yellow);
    graphics2D.drawString(start, (SCREEN_SIZE) / 4, 150);
  }

  // TODO: learn about key adapter
  class TAdapter extends KeyAdapter {

    public void keyPressed(KeyEvent e) {

      int key = e.getKeyCode();

      if (isRunning) {
        // TODO: learn about key events
        if (key == KeyEvent.VK_LEFT) {
          req_dx = -1;
          req_dy = 0;
        } else if (key == KeyEvent.VK_RIGHT) {
          req_dx = 1;
          req_dy = 0;
        } else if (key == KeyEvent.VK_UP) {
          req_dx = 0;
          req_dy = -1;
        } else if (key == KeyEvent.VK_DOWN) {
          req_dx = 0;
          req_dy = 1;
        } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
          isRunning = false;
        } else {
          if (key == KeyEvent.VK_SPACE) {
            initGame();
          }
        }
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {

  }

}
