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

  public Pacman() {

    loadImages();
    initialize();
  }

  public Pacman(int initialSpeed) {

    this();
    this.setSpeed(initialSpeed);
  }

  private void loadImages() {

    this.setDown(new ImageIcon("src/main/resources/images/down.gif").getImage());
    this.setUp(new ImageIcon("src/main/resources/images/up.gif").getImage());
    this.setLeft(new ImageIcon("src/main/resources/images/left.gif").getImage());
    this.setRight(new ImageIcon("src/main/resources/images/right.gif").getImage());
  }

  public void initialize() {

    this.x = 15 * Maze.BLOCK_SIZE;
    this.y = 31 * Maze.BLOCK_SIZE;
    this.deltaX = 0;
    this.deltaY = 0;
    this.requestDeltaX = 0;
    this.requestDeltaY = 0;
  }

  public void requestToMoveLeft() {

    this.requestDeltaX = -1;
    this.requestDeltaY = 0;
  }

  public void requestToMoveRight() {

    this.requestDeltaX = 1;
    this.requestDeltaY = 0;
  }

  public void requestToMoveUp() {

    this.requestDeltaX = 0;
    this.requestDeltaY = -1;
  }

  public void requestToMoveDown() {

    this.requestDeltaX = 0;
    this.requestDeltaY = 1;
  }

  public void move(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {

    if (x % Maze.BLOCK_SIZE == 0 && y % Maze.BLOCK_SIZE == 0) {
      // get current pair of pacman
      int blockIndex = computeBlockIndexFromCurrentPosition();

      // check for valid move request
      if (requestDeltaX != 0 || requestDeltaY != 0) {
        if (maze.isHavingValidMoveRequest(this, blockIndex)) {
          deltaX = requestDeltaX;
          deltaY = requestDeltaY;
        }
      }

      // check for standstill
      if (maze.isHavingInvalidMoveRequest(this, blockIndex)) {
        // if can't move anymore, place pacman at the origin
        deltaX = 0;
        deltaY = 0;
      }
    }

    if (canMoveMore()) {
      redrawAtNewPosition(graphics2D, imageObserver);
    } else {
      draw(graphics2D, imageObserver);
    }
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
