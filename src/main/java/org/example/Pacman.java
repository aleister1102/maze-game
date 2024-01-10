package org.example;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

@Data
@EqualsAndHashCode(callSuper = true)
public class Pacman extends Actor {

  private final int INITIAL_SPEED = 5;
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

  @Override
  public void move(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {
    boolean isMoving = this.isMoving();
    boolean hasValidMoveRequest = maze.isHavingValidMoveRequest(this);
    LogUtil.log("[DEBUG-move]: hasValidMoveRequest = " + hasValidMoveRequest);
    boolean canMoveMore = this.canMoveMore();
    LogUtil.log("[DEBUG-move]: canMoveMore = " + canMoveMore);

    if (!isMoving || !hasValidMoveRequest) {
      // do not draw at new position if is not moving or has invalid move request
      draw(graphics2D, imageObserver);
      this.resetCumulativeDelta();
    } else if (canMoveMore) {
      // can not move if has not moved at least 1 block
      updateDeltaBasedOnMoveRequest(maze);
      updatePosition();
      draw(graphics2D, imageObserver);
      LogUtil.log("[DEBUG-move]: cumulativeDeltaX = " + this.cumulativeDeltaX);
      LogUtil.log("[DEBUG-move]: cumulativeDeltaY = " + this.cumulativeDeltaY);
    } else {
      // stop moving if has moved 1 block
      this.stopMoving();
    }
  }

  private boolean canMoveMore() {

    return this.cumulativeDeltaX < Maze.BLOCK_SIZE && this.cumulativeDeltaY < Maze.BLOCK_SIZE;
  }

  public void fire() {
    // TODO(FEAT): prevent firing when bullet has not moved at least 4 block
    bullet.setX(this.x);
    bullet.setY(this.y);
    bullet.setHasCollisionWithWall(false);

   LogUtil.log("[EVENT]: firing bullet");
    if (isMovingLeft()) {
      bullet.requestToMoveLeft();
    } else if (isMovingRight()) {
      bullet.setX(this.x);
      bullet.setY(this.y);
      bullet.requestToMoveRight();
    } else if (isMovingUp()) {
      bullet.setX(this.x);
      bullet.setY(this.y);
      bullet.requestToMoveUp();
    } else if (isMovingDown()) {
      bullet.setX(this.x);
      bullet.setY(this.y);
      bullet.requestToMoveDown();
    }
  }

}
