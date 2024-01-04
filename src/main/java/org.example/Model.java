package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

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
  private final short[] mapData = {};

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

//  private void paintComponent(Graphics g) {
//    super.paintComponent(g);
//
//    Graphics2D graphics2D = (Graphics2D) g;
//    graphics2D.setColor(Color.black);
//    graphics2D.fillRect(0, 0, dimension.width, dimension.height);
//
//    drawMaze(graphics2D);
//    drawScore(graphics2D);
//
//    if (isRunning) {
//      playGame(graphics2D);
//    } else {
//      showIntroScreen(graphics2D);
//    }
//    Toolkit.getDefaultToolkit().sync();
//  }

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
