package org.example;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import lombok.Data;
import static org.example.Maze.BLOCK_SIZE;
import static org.example.Maze.COLUMNS;
import static org.example.Maze.SCREEN_HEIGHT;
import static org.example.Maze.SCREEN_WIDTH;

@Data
public abstract class Actor {

  protected Image image, down, up, left, right;
  protected int id;

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

  protected boolean isNotMoving() {
    return !isMoving();
  }

  protected boolean isMoving() {
    return this.requestDeltaX != 0 || this.requestDeltaY != 0;
  }

  public abstract void move(Graphics2D graphics, ImageObserver observer, Maze maze);

  public boolean isCurrentPositionDivisibleByBlockSize() {
    return this.x % BLOCK_SIZE == 0 && this.y % BLOCK_SIZE == 0;
  }

  public int computeBlockIndexFromCurrentPosition() {
    return this.x / BLOCK_SIZE + COLUMNS * (this.y / BLOCK_SIZE);
  }

  protected void updateDeltaBasedOnMoveRequest() {
    boolean currentPositionIsDivisibleByBlockSize = isCurrentPositionDivisibleByBlockSize();
    if (currentPositionIsDivisibleByBlockSize) {
      deltaX = requestDeltaX;
      deltaY = requestDeltaY;
    }
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

  protected void updatePosition() {
    // compute travel distance and update actor state
    Pair<Integer, Integer> travelDistance = computeTravelDistance();
    setNewPosition(travelDistance);
    addToCumulativeDelta(travelDistance);
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

  protected void resetCumulativeDelta() {
    this.cumulativeDeltaX = 0;
    this.cumulativeDeltaY = 0;
  }

  protected boolean isOutsideTheWall() {
    return this.x < 0 || this.y < 0 || this.x > SCREEN_WIDTH || this.y > SCREEN_HEIGHT;
  }

}
