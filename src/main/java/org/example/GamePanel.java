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
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import javax.swing.Timer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import static org.example.Maze.SCREEN_HEIGHT;
import static org.example.Maze.SCREEN_WIDTH;

@Data
@EqualsAndHashCode(callSuper = true)
public class GamePanel extends JPanel implements ActionListener {

  private final int NUMBER_OF_PLAYERS = 4;
  public static List<Player> players;
  private Dimension dimension;
  private Font textFont;
  private Graphics2D graphics2D;
  private Maze maze;
  private Timer timer;
  private File logFile;

  private int previousKey;
  private int repeatedKeyCount;
  private boolean isGameRunning = false;

  public GamePanel() {

    initialize();

    super.addKeyListener(new TAdapter());
    super.setFocusable(true);
  }

  private void initialize() {

    dimension = new Dimension(MazeGame.screenWidth, MazeGame.screenHeight);
    textFont = new Font("Arial", Font.BOLD, 14);
    maze = new Maze();
    players = Player.randomPlayers(NUMBER_OF_PLAYERS);
    LogUtil.log("[DEBUG]: players:\n" + players.stream().map(Player::toString).collect(Collectors.joining("\n")));
    timer = new Timer(40, this);
    timer.start();
    logFile = FileUtil.createFile(LogUtil.LOG_FILE);
    previousKey = 0;
    repeatedKeyCount = 0;
  }

  private void reset() {
    maze = new Maze();
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
    Player.movePlayersAndTheirBullets(graphics2D, this, maze, players);
  }

  private void showIntroScreen(Graphics2D graphics2D) {

    String start = "Press ENTER to start";
    graphics2D.setColor(Color.yellow);
    graphics2D.setFont(textFont);
    graphics2D.drawString(start, SCREEN_WIDTH / 4 + 10, SCREEN_HEIGHT + 20);
  }

  private void showKeyPressed(Graphics2D graphics2D) {
    if (previousKey == 0) return;

    String displayKey = repeatedKeyCount == 0
      ? KeyEvent.getKeyText(previousKey)
      : KeyEvent.getKeyText(previousKey) + " (x" + repeatedKeyCount + ")";

    graphics2D.setColor(Color.white);
    graphics2D.setFont(textFont);
    graphics2D.drawString(displayKey, 10, SCREEN_HEIGHT + 20);
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

      Player firstPlayer = players.get(0);
      if (firstPlayer == null) return;
      if (isGameRunning) {
        if (key == KeyEvent.VK_A) {
          //LogUtil.log("[EVENT]: request player to move left");
          firstPlayer.requestToMoveLeft();
        } else if (key == KeyEvent.VK_D) {
          //LogUtil.log("[EVENT]: request player to move right");
          firstPlayer.requestToMoveRight();
        } else if (key == KeyEvent.VK_W) {
          //LogUtil.log("[EVENT]: request player to move up");
          firstPlayer.requestToMoveUp();
        } else if (key == KeyEvent.VK_S) {
          //LogUtil.log("[EVENT]: request player to move down");
          firstPlayer.requestToMoveDown();
        } else if (key == KeyEvent.VK_SPACE) {
          LogUtil.log("[EVENT]: fire bullet");
          firstPlayer.fire();
        } else if (key == KeyEvent.VK_P && timer.isRunning()) {
          isGameRunning = false;
          LogUtil.log("[EVENT]: pause game");
          FileUtil.clearFile(logFile);
          LogUtil.writeAndClearLog(logFile);
        } else if (key == KeyEvent.VK_Q && timer.isRunning()) {
          LogUtil.log("[EVENT]: quit game");
          FileUtil.clearFile(logFile);
          LogUtil.writeAndClearLog(logFile);
          System.exit(0);
        }
      } else {
        if (key == KeyEvent.VK_ENTER) {
          isGameRunning = true;
          reset();
        }
      }
    }

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    super.repaint();
  }

}

