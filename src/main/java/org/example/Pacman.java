package org.example;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

@Data
@EqualsAndHashCode(callSuper = true)
public class Pacman extends Actor {

  private final int INITIAL_SPEED = Actor.VALID_SPEEDS[3];

  private Bullet bullet;

  public Pacman() {

    loadImages();
    initialize();
  }

  private void loadImages() {

    this.image = new ImageIcon("src/main/resources/images/pacman.png").getImage();
    this.down = new ImageIcon("src/main/resources/images/down.gif").getImage();
    this.up = new ImageIcon("src/main/resources/images/up.gif").getImage();
    this.left = new ImageIcon("src/main/resources/images/left.gif").getImage();
    this.right = new ImageIcon("src/main/resources/images/right.gif").getImage();
  }

  private void initialize() {

    this.x = 15 * Maze.BLOCK_SIZE;
    this.y = 31 * Maze.BLOCK_SIZE;
    this.deltaX = 0;
    this.deltaY = 0;
    this.requestDeltaX = 0;
    this.requestDeltaY = 0;
    this.direction = Direction.STAY;
    this.speed = INITIAL_SPEED;
    this.bullet = new Bullet(this);
  }

  // TODO(BUG): sometimes can not move
  @Override
  public void move(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {
    boolean isMoving = this.isMoving();
    boolean hasValidMoveRequest = updateDeltaBasedOnMoveRequest(maze);
    boolean canMoveMore = this.canMoveMore();

    try {
      Thread.sleep(100);
      System.out.println("x: " + this.x + ", y: " + this.y);
      System.out.println("move direction: " + this.direction);
      System.out.println("Pacman::isMoving: " + isMoving);
      System.out.println("Pacman::hasValidMoveRequest: " + hasValidMoveRequest);
      System.out.println("Pacman::canMoveMore: " + canMoveMore);
      System.out.println("-----------------------------------");
    } catch (InterruptedException ignored) {
    }

    if (!isMoving || !hasValidMoveRequest) {
      draw(graphics2D, imageObserver); // do not draw at new position if is not moving or has invalid move request
    } else if (canMoveMore) {
      drawAtNewPosition(graphics2D, imageObserver); // can not move if has not moved at least 1 block
    } else {
      this.stopMoving(); // stop moving if has moved 1 block
    }
  }

  @Override
  public boolean canMoveMore() {

    return this.cumulativeDeltaX < Maze.BLOCK_SIZE && this.cumulativeDeltaY < Maze.BLOCK_SIZE;
  }

  public void fire() {
    // TODO(FEAT): prevent firing when bullet has not moved at least 4 block
    //if(!bullet.canMoveMore()) return;

    bullet.setHasCollisionWithWall(false);

    if (isMovingLeft()) {
      bullet.setX(this.x - Maze.BLOCK_SIZE);
      bullet.setY(this.y);
      bullet.requestToMoveLeft();
    } else if (isMovingRight()) {
      bullet.setX(this.x + Maze.BLOCK_SIZE);
      bullet.setY(this.y);
      bullet.requestToMoveRight();
    } else if (isMovingUp()) {
      bullet.setX(this.x);
      bullet.setY(this.y - Maze.BLOCK_SIZE);
      bullet.requestToMoveUp();
    } else if (isMovingDown()) {
      bullet.setX(this.x);
      bullet.setY(this.y + Maze.BLOCK_SIZE);
      bullet.requestToMoveDown();
    }
  }

}
