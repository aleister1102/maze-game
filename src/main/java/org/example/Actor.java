package org.example;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import lombok.Data;

@Data
public abstract class Actor {

  public static final int[] VALID_SPEEDS = {1, 2, 4, 5, 10};
  protected Image image, down, up, left, right;

  // coordinates
  protected int x;
  protected int y;

  // movement
  protected int deltaX;
  protected int deltaY;
  protected int requestDeltaX;
  protected int requestDeltaY;
  protected Direction direction;

  // statistics
  protected int speed;
  protected int cumulativeDeltaX;
  protected int cumulativeDeltaY;

  public void requestToMoveLeft() {

    this.requestDeltaX = -1;
    this.requestDeltaY = 0;
    this.direction = Direction.LEFT;
  }

  public void requestToMoveRight() {

    this.requestDeltaX = 1;
    this.requestDeltaY = 0;
    this.direction = Direction.RIGHT;
  }

  public void requestToMoveUp() {

    this.requestDeltaX = 0;
    this.requestDeltaY = -1;
    this.direction = Direction.UP;
  }

  public void requestToMoveDown() {

    this.requestDeltaX = 0;
    this.requestDeltaY = 1;
    this.direction = Direction.DOWN;
  }

  protected void stopMoving() {

    this.deltaX = 0;
    this.deltaY = 0;
    this.requestDeltaX = 0;
    this.requestDeltaY = 0;
  }

  protected boolean isMovingLeft() {
    return this.direction == Direction.LEFT;
  }

  protected boolean isMovingRight() {
    return this.direction == Direction.RIGHT;
  }

  protected boolean isMovingUp() {
    return this.direction == Direction.UP;
  }

  protected boolean isMovingDown() {
    return this.direction == Direction.DOWN;
  }

  protected boolean isMoving() {
    return this.requestDeltaX != 0 || this.requestDeltaY != 0;
  }

  public abstract void move(Graphics2D graphics, ImageObserver observer, Maze maze);

  protected abstract boolean canMoveMore();

  protected boolean updateDeltaBasedOnMoveRequest(Maze maze) {
    boolean isMoving = this.isMoving();
    boolean currentPositionIsDivisibleByBlockSize = maze.isCurrentPositionDivisibleByBlockSize(this);
    int blockIndex = maze.computeBlockIndexFromCurrentPosition(this);
    boolean hasValidMoveRequest = maze.isHavingValidMoveRequest(this, blockIndex);

    if (!isMoving) return false;
    if (currentPositionIsDivisibleByBlockSize) {
      if (hasValidMoveRequest) {
        deltaX = requestDeltaX;
        deltaY = requestDeltaY;
      } else {
        deltaX = 0;
        deltaY = 0;
      }
    }

    return hasValidMoveRequest;
  }

  protected void draw(Graphics2D graphics2D, ImageObserver observer) {
    // change image based on pacman direction
    if (isMovingLeft()) {
      graphics2D.drawImage(left, x + 1, y + 1, observer);
    } else if (isMovingRight()) {
      graphics2D.drawImage(right, x + 1, y + 1, observer);
    } else if (isMovingUp()) {
      graphics2D.drawImage(up, x + 1, y + 1, observer);
    } else {
      graphics2D.drawImage(down, x + 1, y + 1, observer);
    }
  }

  protected void drawAtNewPosition(Graphics2D graphics2D, ImageObserver observer) {
    // compute travel distance and update actor state
    Pair<Integer, Integer> travelDistance = computeTravelDistance();
    setNewPosition(travelDistance);
    addToCumulativeDelta(travelDistance);

    draw(graphics2D, observer);
  }

  private Pair<Integer, Integer> computeTravelDistance() {
    int travelX = getSpeed() * getDeltaX();
    int travelY = getSpeed() * getDeltaY();
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

  public void printInformation() {
    System.out.println("------------------------------------");
    System.out.println("Actor name: " + getClass().getName());
    System.out.println("x: " + x + ", y: " + y);
    System.out.println("deltaX: " + deltaX + ", deltaY: " + deltaY);
    System.out.println("requestDeltaX: " + requestDeltaX + ", requestDeltaY: " + requestDeltaY);
    System.out.println("speed: " + speed);
    System.out.println("cumulativeDeltaX: " + cumulativeDeltaX + ", cumulativeDeltaY: " + cumulativeDeltaY);
  }

}
