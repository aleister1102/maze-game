package org.example;

import java.util.Arrays;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class GamePanel extends JPanel implements ActionListener {

  private Dimension dimension;
  private Font textFont;
  private Image heart;

  private final int SCREEN_SIZE = Maze.N_BLOCKS * Maze.BLOCK_SIZE;

  private Maze maze;

  private final int PACMAN_LIVES = 3;
  private final int INITIAL_SPEED = Actor.VALID_SPEEDS[3];
  private final int MAX_SPEED = Actor.getMaxSpeed();
  private Pacman pacman;

  private final int MAX_GHOSTS = 12;
  private int numberOfGhosts = 6;
  private Ghost[] ghosts;
  private int[] dx, dy;

  private boolean isGameRunning = false;
  private int scores;
  private Timer timer;

  public GamePanel() {

    loadImages();
    initialize();
    initializeGame();

    super.addKeyListener(new TAdapter());
    super.setFocusable(true);
  }

  private void loadImages() {

    heart = new ImageIcon("src/main/resources/images/heart.png").getImage();
  }

  private void initialize() {

    dimension = new Dimension(500, 550);
    textFont = new Font("Arial", Font.BOLD, 14);

    maze = new Maze();
    ghosts = new Ghost[numberOfGhosts];
    ghosts = Arrays.stream(ghosts).map(ghost -> new Ghost()).toArray(Ghost[]::new);
    dx = new int[4];
    dy = new int[4];
    pacman = new Pacman();

    timer = new Timer(40, this);
    timer.start();
  }

  private void initializeGame() {

    pacman.setSpeed(INITIAL_SPEED);
    pacman.setLives(PACMAN_LIVES);
    initializeLevel();
    scores = 0;
  }

  private void initializeLevel() {

    maze.copyMapDataToScreenData();
    setupLevel();
  }

  private void setupLevel() {

    pacman.initialize();

    for (Ghost ghost : ghosts) {
      int maxGhostSpeed = pacman.getSpeed();
      ghost.initialize(maxGhostSpeed);
    }
  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D graphics2D = (Graphics2D) g;
    drawBackground(graphics2D);
    drawMaze(graphics2D);
    drawLives(graphics2D);
    drawScores(graphics2D);

    if (isGameRunning) {
      playGame(graphics2D);
    } else {
      showIntroScreen(graphics2D);
    }

    Toolkit.getDefaultToolkit().sync();
    graphics2D.dispose();
  }


  private void drawBackground(Graphics2D graphics2D) {

    graphics2D.setColor(Color.black);
    graphics2D.fillRect(0, 0, dimension.width, dimension.height);
  }

  private void drawMaze(Graphics2D graphics2D) {

    graphics2D.setStroke(new BasicStroke(5));

    short blockIndex = 0;
    for (int y = 0; y < SCREEN_SIZE; y += Maze.BLOCK_SIZE) {
      for (int x = 0; x < SCREEN_SIZE; x += Maze.BLOCK_SIZE) {
        short block = maze.getScreenDataAtIndex(blockIndex);

        maze.drawObstacle(graphics2D, x, y, blockIndex);
        maze.drawObstacleBorders(graphics2D, x, y, block);
        maze.drawWhiteDot(graphics2D, x, y, block);

        blockIndex++;
      }
    }
  }

  public void drawLives(Graphics2D graphics2D) {

    for (int i = 0; i < pacman.getLives(); i++) {
      graphics2D.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
    }
  }

  public void drawScores(Graphics2D graphics2D) {

    graphics2D.setFont(textFont);
    graphics2D.setColor(new Color(5, 181, 79));
    String scoreString = String.format("Score: %s", scores);
    graphics2D.drawString(scoreString, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
  }

  private void playGame(Graphics2D graphics2D) {

    if (!pacman.isDying()) {
      movePacman(graphics2D);
      moveGhosts(graphics2D);
      checkMaze();
    } else {
      death();
    }
  }

  private void movePacman(Graphics2D graphics2D) {

    if (pacman.getX() % Maze.BLOCK_SIZE == 0 && pacman.getY() % Maze.BLOCK_SIZE == 0) {
      // get current position of pacman
      int blockIndex = pacman.computeBlockIndexFromCurrentPosition();
      short block = maze.getScreenDataAtIndex(blockIndex);

      // pacman is at white dot
      if (maze.isDot(block)) {
        maze.setScreenDataAtIndex(blockIndex, (short) (block & 15));
        scores++;
      }

      // check for valid move request
      if (pacman.getRequestDeltaX() != 0 || pacman.getRequestDeltaY() != 0) {
        if (pacman.isValidMoveRequest(block)) {
          pacman.setDeltaX(pacman.getRequestDeltaX());
          pacman.setDeltaY(pacman.getRequestDeltaY());
        }
      }

      // check for standstill
      if (pacman.isInvalidMoveRequest(block)) {
        // if can't move anymore, place pacman at the origin
        pacman.setDeltaX(0);
        pacman.setDeltaY(0);
      }
    }

    pacman.move(graphics2D, this);
  }

  private void moveGhosts(Graphics2D graphics2D) {

    int pos;
    int count;

    for (int i = 0; i < numberOfGhosts; i++) {
      Ghost ghost = ghosts[i];
      if (ghost.getX() % Maze.BLOCK_SIZE == 0 && ghost.getY() % Maze.BLOCK_SIZE == 0) {
        pos = ghost.computeBlockIndexFromCurrentPosition();

        count = 0;

        if ((maze.getScreenData()[pos] & 1) == 0 && ghost.getDeltaX() != 1) {
          dx[count] = -1;
          dy[count] = 0;
          count++;
        }

        if ((maze.getScreenData()[pos] & 2) == 0 && ghost.getDeltaY() != 1) {
          dx[count] = 0;
          dy[count] = -1;
          count++;
        }

        if ((maze.getScreenData()[pos] & 4) == 0 && ghost.getDeltaX() != -1) {
          dx[count] = 1;
          dy[count] = 0;
          count++;
        }

        if ((maze.getScreenData()[pos] & 8) == 0 && ghost.getDeltaY() != -1) {
          dx[count] = 0;
          dy[count] = 1;
          count++;
        }

        if (count == 0) {
          if ((maze.getScreenData()[pos] & 15) == 15) {
            ghost.setDeltaX(0);
            ghost.setDeltaY(0);
          }
        } else {
          count = (int) (Math.random() * count);

          if (count > 3) {
            count = 3;
          }

          ghost.setDeltaX(dx[count]);
          ghost.setDeltaY(dy[count]);
        }

      }

      ghost.move(graphics2D, this);

      if (isPacmanEatenByAGhost(ghost)) {
        pacman.setDying(true);
      }
    }
  }

  private boolean isPacmanEatenByAGhost(Ghost ghost) {

    return (pacman.getX() > (ghost.getX() - 12) && pacman.getX() < (ghost.getX() + 12)
      && pacman.getY() > (ghost.getY() - 12) && pacman.getY() < (ghost.getY() + 12)
      && isGameRunning);
  }

  private void checkMaze() {

    int i = 0;
    boolean finished = true;

    while (i < Maze.N_BLOCKS * Maze.N_BLOCKS && finished) {

      if ((maze.getScreenData()[i]) != 0) {
        finished = false;
      }

      i++;
    }

    if (finished) {

      scores += 50;

      increaseGhostNumber();
      increasePacmanSpeed();
      initializeLevel();
    }
  }

  private void increaseGhostNumber() {

    if (numberOfGhosts < MAX_GHOSTS) {
      numberOfGhosts++;
    }
  }

  private void increasePacmanSpeed() {

    if (pacman.getSpeed() < MAX_SPEED) {
      pacman.setSpeed(pacman.getSpeed() + 1);
    }
  }

  private void death() {

    pacman.setLives(pacman.getLives() - 1);

    if (pacman.getLives() == 0) {
      isGameRunning = false;
    }

    setupLevel();
  }

  private void showIntroScreen(Graphics2D graphics2D) {

    String start = "Press SPACE to start";
    graphics2D.setColor(Color.yellow);
    graphics2D.drawString(start, (SCREEN_SIZE) / 4, 150);
  }

  // TODO: learn about key adapter
  class TAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {

      int key = e.getKeyCode();

      if (isGameRunning) {
        // TODO: learn about key events
        if (key == KeyEvent.VK_LEFT) {
          pacman.setRequestDeltaX(-1);
          pacman.setRequestDeltaY(0);
        } else if (key == KeyEvent.VK_RIGHT) {
          pacman.setRequestDeltaX(1);
          pacman.setRequestDeltaY(0);
        } else if (key == KeyEvent.VK_UP) {
          pacman.setRequestDeltaX(0);
          pacman.setRequestDeltaY(-1);
        } else if (key == KeyEvent.VK_DOWN) {
          pacman.setRequestDeltaX(0);
          pacman.setRequestDeltaY(1);
        } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
          isGameRunning = false;
        }
      } else {
        if (key == KeyEvent.VK_SPACE) {
          isGameRunning = true;
          initializeGame();
        }
      }
    }

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    super.repaint();
  }

}
