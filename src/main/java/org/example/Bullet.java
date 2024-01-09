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
    this.direction = Direction.STAY;
    this.speed = pacman.getSpeed() * SPEED_MULTIPLIER;
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
    boolean isMoving = this.isMoving();
    boolean deltaChanged = updateDeltaBasedOnMoveRequest(maze);
    boolean canMoveMore = this.canMoveMore();

    if (!isMoving) draw(graphics, observer);
    else if (!deltaChanged) draw(graphics, observer);
    else if (canMoveMore) drawAtNewPosition(graphics, observer);

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
