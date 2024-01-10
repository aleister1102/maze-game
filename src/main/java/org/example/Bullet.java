package org.example;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bullet extends Actor {

  public static final int SPEED_MULTIPLIER = 4;

  private boolean hasCollisionWithWall = false;

  public Bullet() {
    loadImages();
  }

  public Bullet(Pacman pacman, int id) {
    this();
    this.id = id;
    this.x = pacman.getX();
    this.y = pacman.getY();
    this.direction = Direction.STAY;
    //* FEAT2: speed of bullet is 4 times faster than pacman
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
    boolean notMoving = this.isNotMoving();
    boolean isOutsideTheWall = this.isOutsideTheWall();
    boolean hasInvalidMoveRequest = maze.hasInvalidMoveRequest(this);
    LogUtil.log("[DEBUG-bullet.move]: bullet %s hasInvalidMoveRequest=%s", this.id, hasInvalidMoveRequest);

    if (notMoving) return;
    if (hasInvalidMoveRequest || isOutsideTheWall) hasCollisionWithWall = true;
    else {
      updateDeltaBasedOnMoveRequest(maze);
      updatePosition();
      draw(graphics, observer);
    }
    if (hasCollisionWithWall) this.stopMoving();
  }

  @Override
  protected void updateDeltaBasedOnMoveRequest(Maze maze) {
    //? allow bullet to move out of the wall
    deltaX = requestDeltaX;
    deltaY = requestDeltaY;
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
    graphics2D.setColor(Color.magenta);
    graphics2D.drawString(String.valueOf(id), x + 3, y + 1);
  }

  public String toString() {
    return String.format(
      "[Bullet(id=%s, hasCollisionWithWall=%s, isMoving=%s, requestDeltaX=%d, requestDeltaY=%d, cumulativeDeltaX=%d, cumulativeDeltaY=%d)]",
      this.id,
      this.hasCollisionWithWall,
      this.isMoving(),
      this.requestDeltaX,
      this.requestDeltaY,
      this.cumulativeDeltaX,
      this.cumulativeDeltaY);
  }

}
