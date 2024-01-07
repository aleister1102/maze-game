package org.example;

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
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GamePanel extends JPanel implements ActionListener {

  private Dimension dimension;
  private Font textFont;
  private Image heart;
  private Graphics2D graphics2D;

  private final int SCREEN_HEIGHT = Maze.ROWS * Maze.BLOCK_SIZE;
  private final int SCREEN_WIDTH = Maze.COLUMNS * Maze.BLOCK_SIZE;

  private Maze maze;

  private final int PACMAN_LIVES = 3;
  private final int INITIAL_SPEED = Actor.VALID_SPEEDS[3];
  private final int MAX_SPEED = Actor.getMaxSpeed();
  private Pacman pacman;

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

    dimension = new Dimension(MazeGame.screenWidth, MazeGame.screenHeight);
    textFont = new Font("Arial", Font.BOLD, 14);

    maze = new Maze();
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
  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    graphics2D = (Graphics2D) g;
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
    for (int y = 0; y < SCREEN_HEIGHT; y += Maze.BLOCK_SIZE) {
      for (int x = 0; x < SCREEN_WIDTH; x += Maze.BLOCK_SIZE) {
        short block = maze.getScreenDataAtIndex(blockIndex);

        //maze.drawObstacle(graphics2D, x, y, blockIndex);
        maze.drawObstacleBorders(graphics2D, x, y, block);
        //maze.drawWhiteDot(graphics2D, x, y, block);

        blockIndex++;
      }
    }
  }

  public void drawLives(Graphics2D graphics2D) {

    for (int i = 0; i < pacman.getLives(); i++) {
      graphics2D.drawImage(heart, i * 28 + 8, SCREEN_HEIGHT + 1, this);
    }
  }

  public void drawScores(Graphics2D graphics2D) {

    graphics2D.setFont(textFont);
    graphics2D.setColor(new Color(47, 193, 206));
    String scoreString = String.format("Score: %s", scores);
    graphics2D.drawString(scoreString, SCREEN_WIDTH / 2 + 96, SCREEN_HEIGHT + 16);
  }

  private void playGame(Graphics2D graphics2D) {

    if (!pacman.isDying()) {
      movePacman(graphics2D);
      checkMaze();
    } else {
      death();
    }
  }

  private void movePacman(Graphics2D graphics2D) {

    if (pacman.getX() % Maze.BLOCK_SIZE == 0 && pacman.getY() % Maze.BLOCK_SIZE == 0) {
      // get current position of pacman
      int blockIndex = pacman.computeBlockIndexFromCurrentPosition();

      // check for valid move request
      if (pacman.getRequestDeltaX() != 0 || pacman.getRequestDeltaY() != 0) {
        if (maze.isHavingValidMoveRequest(pacman, blockIndex)) {
          pacman.setDeltaX(pacman.getRequestDeltaX());
          pacman.setDeltaY(pacman.getRequestDeltaY());
        }
      }

      // check for standstill
      if (maze.isHavingInvalidMoveRequest(pacman, blockIndex)) {
        // if can't move anymore, place pacman at the origin
        pacman.setDeltaX(0);
        pacman.setDeltaY(0);
      }
    }

    if (pacman.canMoveMore()) {
      pacman.move(graphics2D, this);
    } else {
      pacman.draw(graphics2D, this);
    }
  }

  private void checkMaze() {

    int i = 0;
    boolean finished = true;

    while (i < Maze.ROWS * Maze.COLUMNS && finished) {

      if (maze.getScreenData()[i] != 0) {
        finished = false;
      }

      i++;
    }

    if (finished) {

      scores += 50;

      increasePacmanSpeed();
      initializeLevel();
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
    graphics2D.setFont(textFont);
    graphics2D.drawString(start, SCREEN_WIDTH / 4 + 10, SCREEN_HEIGHT / 2);
  }

  class TAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {

      int key = e.getKeyCode();

      if (isGameRunning) {
        pacman.resetCumulativeDelta();

        if (key == KeyEvent.VK_A) {
          pacman.setRequestDeltaX(-1);
          pacman.setRequestDeltaY(0);
        } else if (key == KeyEvent.VK_D) {
          pacman.setRequestDeltaX(1);
          pacman.setRequestDeltaY(0);
        } else if (key == KeyEvent.VK_W) {
          pacman.setRequestDeltaX(0);
          pacman.setRequestDeltaY(-1);
        } else if (key == KeyEvent.VK_S) {
          pacman.setRequestDeltaX(0);
          pacman.setRequestDeltaY(1);
        } else if (key == KeyEvent.VK_Q && timer.isRunning()) {
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

