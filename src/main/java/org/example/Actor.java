package org.example;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.Arrays;
import lombok.Data;

@Data
public abstract class Actor {

  public static final int[] VALID_SPEEDS = {1, 2, 4, 5, 10};

  // coordinates
  protected int x;
  protected int y;

  // movement
  protected int deltaX;
  protected int deltaY;

  // statistics
  protected int speed;
  protected int cumulativeDeltaX;
  protected int cumulativeDeltaY;

  public static int getMaxSpeed() {

    Arrays.sort(VALID_SPEEDS);
    return VALID_SPEEDS[VALID_SPEEDS.length - 1];
  }

  public static int getRandomSpeed() {

    int randomSpeedIndex = (int) (Math.random() * VALID_SPEEDS.length);
    return VALID_SPEEDS[randomSpeedIndex];
  }

  public void move(Graphics2D graphics2D, ImageObserver observer) {

    computeCurrentPosition();
    draw(graphics2D, observer);
  }

  private void computeCurrentPosition() {

    this.setX(this.getX() + this.getSpeed() * this.getDeltaX());
    this.setY(this.getY() + this.getSpeed() * this.getDeltaY());
    this.cumulativeDeltaX += Math.abs(this.getSpeed() * this.getDeltaX());
    this.cumulativeDeltaY += Math.abs(this.getSpeed() * this.getDeltaY());
    System.out.println("cumulativeDeltaX: " + this.cumulativeDeltaX);
    System.out.println("cumulativeDeltaY: " + this.cumulativeDeltaY);
  }

  protected abstract void draw(Graphics2D graphics2D, ImageObserver imageObserver);

  public int computeBlockIndexFromCurrentPosition() {
    return getX() / Maze.BLOCK_SIZE + Maze.COLUMNS * (getY() / Maze.BLOCK_SIZE);
  }

  public boolean canMoveMore() {
    return this.cumulativeDeltaX < Maze.BLOCK_SIZE && this.cumulativeDeltaY < Maze.BLOCK_SIZE;
  }

  public void resetCumulativeDelta() {
    this.cumulativeDeltaX = 0;
    this.cumulativeDeltaY = 0;
  }

}
