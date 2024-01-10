package org.example;


import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;

@Data
@EqualsAndHashCode(callSuper = true)
public class Pacman extends Actor {

  private final int INITIAL_SPEED = 5;
  private List<Bullet> bullets;

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
    this.direction = Direction.UP;
    this.speed = INITIAL_SPEED;
    this.bullets = new LinkedList<>();
    bullets.add(new Bullet(this));
  }

  @Override
  public void move(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {
    boolean isMoving = this.isMoving();
    boolean hasValidMoveRequest = maze.isHavingValidMoveRequest(this);
    boolean canMove = this.canMove();
    //LogUtil.log("[DEBUG-pacman.move]: hasValidMoveRequest = " + hasValidMoveRequest);
    //LogUtil.log("[DEBUG-pacman.move]: canMove = " + canMove);

    if (!isMoving || !hasValidMoveRequest) {
      // do not draw at new position if is not moving or has invalid move request
      draw(graphics2D, imageObserver);
      this.resetCumulativeDelta();
    } else if (canMove) {
      // can not move if has not moved at least 1 block
      updateDeltaBasedOnMoveRequest(maze);
      updatePosition();
      draw(graphics2D, imageObserver);
      //LogUtil.log("[DEBUG-pacman.move]: cumulativeDeltaX = " + this.cumulativeDeltaX);
      //LogUtil.log("[DEBUG-pacman.move]: cumulativeDeltaY = " + this.cumulativeDeltaY);
    } else {
      // stop moving if has moved 1 block
      this.stopMoving();
    }
  }

  private boolean canMove() {

    return this.cumulativeDeltaX < Maze.BLOCK_SIZE && this.cumulativeDeltaY < Maze.BLOCK_SIZE;
  }

  public void moveBullets(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {
    for (Bullet bullet : bullets) {
      bullet.move(graphics2D, imageObserver, maze);
    }
  }

  public void fire() {
    LogUtil.log("[DEBUG-fire]: current list of bullets:\n%s", bullets.stream().map(Bullet::toString).collect(Collectors.joining("\n")));

    //* FEAT3: prevent firing when bullet has not moved at least 4 block
    boolean canFire = canFire();
    LogUtil.log("[DEBUG-fire]: canFire = " + canFire);
    if (!canFire) return;

    LogUtil.log("[DEBUG-fire]: reset bullet position");
    Bullet bullet = getBulletToFire();
    bullet.setX(this.x);
    bullet.setY(this.y);
    bullet.resetCumulativeDelta();
    bullet.setHasCollisionWithWall(false);
    //bullet.setSpeed(1); //? used when demo FEAT3

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

  private Bullet getBulletToFire() {
    // find inactive bullet
    Bullet bullet = findInactiveBullet();
    if (bullet != null) return bullet;

    // if not found, create new bullet
    bullet = new Bullet(this);
    bullets.add(bullet);
    return bullet;
  }

  private Bullet findInactiveBullet() {
    return bullets.stream().filter(b -> !b.isMoving()).findFirst().orElse(null);
  }

  private boolean canFire() {
    for (Bullet bullet : bullets) {
      boolean isMoving = bullet.isMoving();
      Direction direction = bullet.getDirection();
      int cumulativeDeltaX = bullet.cumulativeDeltaX;
      int cumulativeDeltaY = bullet.cumulativeDeltaY;

      if (!isMoving) continue;
      if ((direction.equals(Direction.LEFT) || direction.equals(Direction.RIGHT)) && (cumulativeDeltaX < Maze.BLOCK_SIZE * 4))
        return false;
      else if ((direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) && (cumulativeDeltaY < Maze.BLOCK_SIZE * 4))
        return false;

    }
    return true;
  }

}
