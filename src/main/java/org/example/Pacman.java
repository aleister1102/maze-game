package org.example;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

@Data
@EqualsAndHashCode(callSuper = true)
public class Pacman extends Actor {

  private Image heart;
  private Image down, up, left, right;

  private int requestDeltaX;
  private int requestDeltaY;

  private int lives;
  private boolean isDying = false;

  public Pacman() {

    loadImages();
  }

  private void loadImages() {

    this.setDown(new ImageIcon("src/main/resources/images/down.gif").getImage());
    this.setUp(new ImageIcon("src/main/resources/images/up.gif").getImage());
    this.setLeft(new ImageIcon("src/main/resources/images/left.gif").getImage());
    this.setRight(new ImageIcon("src/main/resources/images/right.gif").getImage());
  }

  public void initialize() {

    this.setX(15 * Maze.BLOCK_SIZE);
    this.setY(31 * Maze.BLOCK_SIZE);
    this.setDeltaX(0);
    this.setDeltaY(0);
    this.setRequestDeltaX(0);
    this.setRequestDeltaY(0);
    this.setDying(false);
  }

  @Override
  protected void draw(Graphics2D graphics2D, ImageObserver observer) {

    // change image based on pacman direction
    if (requestDeltaX == -1) {
      graphics2D.drawImage(left, x + 1, y + 1, observer);
    } else if (requestDeltaX == 1) {
      graphics2D.drawImage(right, x + 1, y + 1, observer);
    } else if (requestDeltaY == -1) {
      graphics2D.drawImage(up, x + 1, y + 1, observer);
    } else {
      graphics2D.drawImage(down, x + 1, y + 1, observer);
    }
  }

}
