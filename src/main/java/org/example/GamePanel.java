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
  private Pacman pacman;
  private Timer timer;

  public GamePanel() {

    initialize();

    super.addKeyListener(new TAdapter());
    super.setFocusable(true);
  }

  private void initialize() {

    dimension = new Dimension(MazeGame.screenWidth, MazeGame.screenHeight);
    textFont = new Font("Arial", Font.BOLD, 14);
    maze = new Maze();
    pacman = new Pacman();
    timer = new Timer(40, this);
    timer.start();
  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    graphics2D = (Graphics2D) g;
    maze.draw(graphics2D, dimension);

    if (isGameRunning) {
      playGame(graphics2D);
    } else {
      showIntroScreen(graphics2D);
    }

    Toolkit.getDefaultToolkit().sync();
    graphics2D.dispose();
  }

  private void playGame(Graphics2D graphics2D) {
    pacman.move(graphics2D, this, maze);
  }

  private void showIntroScreen(Graphics2D graphics2D) {

    String start = "Press ENTER to start";
    graphics2D.setColor(Color.yellow);
    graphics2D.setFont(textFont);
    graphics2D.drawString(start, Maze.SCREEN_WIDTH / 4 + 10, Maze.SCREEN_HEIGHT / 2);
  }

  class TAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {

      int key = e.getKeyCode();

      if (isGameRunning) {

        if (key == KeyEvent.VK_A) {
          pacman.requestToMoveLeft();
        } else if (key == KeyEvent.VK_D) {
          pacman.requestToMoveRight();
        } else if (key == KeyEvent.VK_W) {
          pacman.requestToMoveUp();
        } else if (key == KeyEvent.VK_S) {
          pacman.requestToMoveDown();
        } else if (key == KeyEvent.VK_SPACE) {
          pacman.fire();
        } else if (key == KeyEvent.VK_Q && timer.isRunning()) {
          isGameRunning = false;
        }

        pacman.resetCumulativeDelta();
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

