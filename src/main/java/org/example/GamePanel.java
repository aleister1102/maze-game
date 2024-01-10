package org.example;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JPanel;
import javax.swing.Timer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GamePanel extends JPanel implements ActionListener {

  private boolean isGameRunning = false;

  private Dimension dimension;
  private Font textFont;
  private Graphics2D graphics2D;
  private Maze maze;
  private Player player;
  private Timer timer;
  private File logFile;
  private int previousKey;
  private int repeatedKeyCount;

  public GamePanel() {

    initialize();

    super.addKeyListener(new TAdapter());
    super.setFocusable(true);
  }

  private void initialize() {

    dimension = new Dimension(MazeGame.screenWidth, MazeGame.screenHeight);
    textFont = new Font("Arial", Font.BOLD, 14);
    maze = new Maze();
    player = new Player();
    timer = new Timer(40, this);
    timer.start();
    logFile = FileUtil.createFile(LogUtil.LOG_FILE);
    previousKey = 0;
    repeatedKeyCount = 0;
  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);
    graphics2D = (Graphics2D) g;
    maze.draw(graphics2D, dimension);

    if (isGameRunning) {
      playGame(graphics2D);
      showKeyPressed(graphics2D);
    } else {
      showIntroScreen(graphics2D);
    }

    Toolkit.getDefaultToolkit().sync();
    graphics2D.dispose();
  }

  private void playGame(Graphics2D graphics2D) {
    player.move(graphics2D, this, maze);
    player.moveBullets(graphics2D, this, maze);
  }

  private void showIntroScreen(Graphics2D graphics2D) {

    String start = "Press ENTER to start";
    graphics2D.setColor(Color.yellow);
    graphics2D.setFont(textFont);
    graphics2D.drawString(start, Maze.SCREEN_WIDTH / 4 + 10, Maze.SCREEN_HEIGHT + 20);
  }

  private void showKeyPressed(Graphics2D graphics2D) {
    if (previousKey == 0) return;

    String displayKey = repeatedKeyCount == 0
      ? KeyEvent.getKeyText(previousKey)
      : KeyEvent.getKeyText(previousKey) + " (x" + repeatedKeyCount + ")";

    graphics2D.setColor(Color.white);
    graphics2D.setFont(textFont);
    graphics2D.drawString(displayKey, 10, Maze.SCREEN_HEIGHT + 20);
  }

  class TAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();

      if (key == previousKey) {
        repeatedKeyCount += 1;
      } else {
        previousKey = key;
        repeatedKeyCount = 0;
      }

      if (isGameRunning) {
        if (key == KeyEvent.VK_A) {
          //LogUtil.log("[EVENT]: request player to move left");
          player.requestToMoveLeft();
        } else if (key == KeyEvent.VK_D) {
          //LogUtil.log("[EVENT]: request player to move right");
          player.requestToMoveRight();
        } else if (key == KeyEvent.VK_W) {
          //LogUtil.log("[EVENT]: request player to move up");
          player.requestToMoveUp();
        } else if (key == KeyEvent.VK_S) {
          //LogUtil.log("[EVENT]: request player to move down");
          player.requestToMoveDown();
        } else if (key == KeyEvent.VK_SPACE) {
          LogUtil.log("[EVENT]: fire bullet");
          player.fire();
        } else if (key == KeyEvent.VK_Q && timer.isRunning()) {
          isGameRunning = false;

          // process log
          LogUtil.log("[EVENT]: pause game");
          FileUtil.clearFile(logFile);
          LogUtil.dumpLog(logFile);
          LogUtil.clearLogTrace();
        }
      } else {
        if (key == KeyEvent.VK_ENTER) {
          isGameRunning = true;
          initialize();
        }
      }
    }

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    super.repaint();
  }

}

