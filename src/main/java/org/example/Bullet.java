package org.example;

import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bullet extends Actor {

  private final int SPEED_MULTIPLIER = 4;
  boolean hasCollisionWithWall = false;

  public Bullet() {
    loadImages();
  }

  public Bullet(Pacman pacman) {
    this();
    this.x = pacman.getX();
    this.y = pacman.getY();
    this.speed = pacman.getSpeed() * SPEED_MULTIPLIER;
    this.direction = Direction.STAY;
  }

  private void loadImages() {
    // TODO: adjust image to fit with a block
    this.down = new ImageIcon("src/main/resources/images/bullet-down.png").getImage();
    this.up = new ImageIcon("src/main/resources/images/bullet-up.png").getImage();
    this.left = new ImageIcon("src/main/resources/images/bullet-left.png").getImage();
    this.right = new ImageIcon("src/main/resources/images/bullet-right.png").getImage();
  }

  @Override
  public void move(Graphics2D graphics, ImageObserver observer, Maze maze) {

    if (this.x % Maze.BLOCK_SIZE == 0 && this.y % Maze.BLOCK_SIZE == 0) {
      int blockIndex = computeBlockIndexFromCurrentPosition();

      // check for valid move request
      if (requestDeltaX != 0 || requestDeltaY != 0) {
        if (maze.isHavingValidMoveRequest(this, blockIndex)) {
          deltaX = requestDeltaX;
          deltaY = requestDeltaY;
        } else {
          deltaX = 0;
          deltaY = 0;
          hasCollisionWithWall = true;
          stopMoving();
          return;
        }
      }
    }

    if (isMoving()) {
      redrawAtNewPosition(graphics, observer);
    }
  }

  @Override
  public boolean canMoveMore() {
    return this.cumulativeDeltaX < Maze.BLOCK_SIZE * 3 && this.cumulativeDeltaY < Maze.BLOCK_SIZE * 3;
  }

  @Override
  public void draw(Graphics2D graphics2D, ImageObserver observer) {
    if (hasCollisionWithWall) return;

    if (isMovingLeft()) {
      graphics2D.drawImage(left, x + 1, y + 1, observer);
    } else if (isMovingRight()) {
      graphics2D.drawImage(right, x + 1, y + 1, observer);
    } else if (isMovingUp()) {
      graphics2D.drawImage(up, x + 1, y + 1, observer);
    } else if (isMovingDown()) {
      graphics2D.drawImage(down, x + 1, y + 1, observer);
    }
  }


}
