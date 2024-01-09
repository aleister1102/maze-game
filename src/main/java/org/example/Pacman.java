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

  @Override
  public void move(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {

    // TODO: sometimes can not move
    if (x % Maze.BLOCK_SIZE == 0 && y % Maze.BLOCK_SIZE == 0) {
      // get current position of pacman
      int blockIndex = computeBlockIndexFromCurrentPosition();

      // check for valid move request
      if (requestDeltaX != 0 || requestDeltaY != 0) {
        if (maze.isHavingValidMoveRequest(this, blockIndex)) {
          deltaX = requestDeltaX;
          deltaY = requestDeltaY;
        } else {
          deltaX = 0;
          deltaY = 0;
        }
      }
    }

    // can not move if bullet is moving
    if (isMoving() && canMoveMore() && !bullet.isMoving()) {
      redrawAtNewPosition(graphics2D, imageObserver);
    } else {
      draw(graphics2D, imageObserver);
      stopMoving();

      bullet.move(graphics2D, imageObserver, maze);
    }
  }

  @Override
  public boolean canMoveMore() {

    return this.cumulativeDeltaX < Maze.BLOCK_SIZE && this.cumulativeDeltaY < Maze.BLOCK_SIZE;
  }

  @Override
  public void draw(Graphics2D graphics2D, ImageObserver observer) {

    super.draw(graphics2D, observer);
    bullet.draw(graphics2D, observer);
  }

  public void fire() {
    // TODO: prevent firing bullet when opposite with a wall
    // TODO: prevent firing when bullet has not moved at least 4 block
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
