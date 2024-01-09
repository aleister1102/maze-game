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

  public void redrawAtNewPosition(Graphics2D graphics2D, ImageObserver observer) {

    // compute travel distance and update actor state
    Pair<Integer, Integer> travelDistance = computeTravelDistance();
    setNewPosition(travelDistance);
    addToCumulativeDelta(travelDistance);

    draw(graphics2D, observer);
  }

  private Pair<Integer, Integer> computeTravelDistance() {
    Integer travelX = getSpeed() * getDeltaX();
    Integer travelY = getSpeed() * getDeltaY();
    return new Pair<>(travelX, travelY);
  }

  private void setNewPosition(Pair<Integer, Integer> travelDistance) {
    this.x += travelDistance.getFirst();
    this.y += travelDistance.getSecond();
  }

  private void addToCumulativeDelta(Pair<Integer, Integer> travelDistance) {
    this.cumulativeDeltaX += Math.abs(travelDistance.getFirst());
    this.cumulativeDeltaY += Math.abs(travelDistance.getSecond());
  }

  public void resetCumulativeDelta() {
    this.cumulativeDeltaX = 0;
    this.cumulativeDeltaY = 0;
  }

  public int computeBlockIndexFromCurrentPosition() {
    return this.x / Maze.BLOCK_SIZE + Maze.COLUMNS * (this.y / Maze.BLOCK_SIZE);
  }

  public boolean canMoveMore() {
    return this.cumulativeDeltaX < Maze.BLOCK_SIZE && this.cumulativeDeltaY < Maze.BLOCK_SIZE;
  }

  protected abstract void draw(Graphics2D graphics2D, ImageObserver imageObserver);

}
