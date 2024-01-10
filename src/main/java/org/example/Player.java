package org.example;


import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import static org.example.Maze.BLOCK_SIZE;
import static org.example.Maze.COLUMNS;
import static org.example.Maze.ROWS;

@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Actor {

  private final int INITIAL_SPEED = 5;
  private final int MINIMUM_BULLET_DISTANCE = 4;
  private final int EXPECTED_MAX_BULLET = ROWS / MINIMUM_BULLET_DISTANCE;
  private List<Bullet> bullets;

  public Player() {

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

    this.id = 0;
    this.x = 15 * BLOCK_SIZE;
    this.y = 31 * BLOCK_SIZE;
    this.deltaX = 0;
    this.deltaY = 0;
    this.requestDeltaX = 0;
    this.requestDeltaY = 0;
    this.direction = Direction.UP;
    this.speed = INITIAL_SPEED;
    this.bullets = new LinkedList<>();
  }

  @Override
  public void move(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {
    boolean isMoving = this.isMoving();
    boolean hasValidMoveRequest = maze.hasValidMoveRequest(this);
    boolean canMove = this.canMove();
    //LogUtil.log("[DEBUG-pacman.move]: hasValidMoveRequest = " + hasValidMoveRequest);
    //LogUtil.log("[DEBUG-pacman.move]: canMove = " + canMove);

    if (!isMoving || !hasValidMoveRequest) {
      // do not draw at new position if is not moving or has invalid move request
      draw(graphics2D, imageObserver);
      this.resetCumulativeDelta();
    } else if (canMove) {
      // can not move if has not moved at least 1 block
      updateDeltaBasedOnMoveRequest();
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

    return this.cumulativeDeltaX < BLOCK_SIZE && this.cumulativeDeltaY < BLOCK_SIZE;
  }

  @Override
  protected void draw(Graphics2D graphics2D, ImageObserver imageObserver) {
    super.draw(graphics2D, imageObserver);
    graphics2D.setColor(Color.GREEN);
    graphics2D.setFont(new Font("Arial", Font.BOLD, 14));
    graphics2D.drawString(String.valueOf(id), x + 9, y + 16);
  }

  public void moveBullets(Graphics2D graphics2D, ImageObserver imageObserver, Maze maze) {
    for (Bullet bullet : bullets) {
      if (!bullet.isMoving()) continue;
      bullet.move(graphics2D, imageObserver, maze);
    }
  }

  public void fire() {
    LogUtil.log("[DEBUG-fire]: current list of bullets:\n%s", bullets.stream().map(Bullet::toString).collect(Collectors.joining("\n")));

    //* FEAT3: prevent firing when bullet has not moved at least 4 block
    boolean canFire = canFire();
    LogUtil.log("[DEBUG-fire]: canFire = " + canFire);
    if (!canFire) return;

    Bullet bullet = getBulletToFire();
    LogUtil.log("[DEBUG-fire]: bullet to fire: %s", bullet.getId());
    bullet.setX(this.x);
    bullet.setY(this.y);
    bullet.resetCumulativeDelta();
    bullet.setHasCollisionWithWall(false);

    //? used when demo FEAT3
    // bullet.setSpeed(1);

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
    int bulletId = bullets.size() + 1;
    bullet = new Bullet(this, bulletId);
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
      if ((direction.equals(Direction.LEFT) || direction.equals(Direction.RIGHT)) && (cumulativeDeltaX < BLOCK_SIZE * MINIMUM_BULLET_DISTANCE))
        return false;
      else if ((direction.equals(Direction.UP) || direction.equals(Direction.DOWN)) && (cumulativeDeltaY < BLOCK_SIZE * MINIMUM_BULLET_DISTANCE))
        return false;

    }
    return true;
  }

  public static List<Player> randomPlayers(int numberOfPlayers) {
    List<Player> randomPlayers = new LinkedList<>();
    for (int i = 0; i < numberOfPlayers; i++) {
      Player randomPlayer = new Player();
      randomPlayer.setId(i);
      randomPlayer.randomPlayerState(randomPlayer, randomPlayers);
      randomPlayers.add(randomPlayer);
    }
    return randomPlayers;
  }

  private void randomPlayerState(Player player, List<Player> players) {
    boolean canNotRandomDirectionFromCurrentPosition = true;
    while (canNotRandomDirectionFromCurrentPosition) {
      player.randomPosition(players);
      canNotRandomDirectionFromCurrentPosition = !player.randomDirection();
    }
  }

  //* FEAT5: random player at random position with random direction. If that position is opposite to wall, random again
  private void randomPosition(List<Player> players) {
    Random random = new Random();
    boolean isAtSamePlaceWithOtherPlayers = true;
    while (isAtSamePlaceWithOtherPlayers) {
      int randomBlockIndex = random.nextInt(ROWS * COLUMNS);
      Pair<Integer, Integer> position = Maze.computePositionFromBlockIndex(randomBlockIndex);
      this.x = position.getFirst();
      this.y = position.getSecond();
      isAtSamePlaceWithOtherPlayers = isAtSamePlaceWithOtherPlayers(players);
    }
  }

  private boolean isAtSamePlaceWithOtherPlayers(List<Player> players) {
    for (Player player : players) {
      if (player.equals(this)) continue;
      if (player.getX() == this.x && player.getY() == this.y) return true;
    }
    return false;
  }

  private boolean randomDirection() {
    Random random = new Random();
    Set<Direction> randomDirections = new HashSet<>();
    boolean isOppositeToWall = true;

    while (isOppositeToWall) {
      int randomDirection = random.nextInt(4);
      if (randomDirection == 0) {
        this.direction = Direction.LEFT;
      } else if (randomDirection == 1) {
        this.direction = Direction.RIGHT;
      } else if (randomDirection == 2) {
        this.direction = Direction.UP;
      } else {
        this.direction = Direction.DOWN;
      }

      isOppositeToWall = Maze.isOppositeToWall(this);
      LogUtil.log("[DEBUG-randomDirection]: current position (%s, %s) of player %s is opposite to wall? %s",
        this.computeBlockIndexFromCurrentPosition(), this.direction, this.id, isOppositeToWall);

      LogUtil.log("[DEBUG-randomDirection]: random direction = " + this.direction);
      randomDirections.add(this.direction);
      if (randomDirections.size() == Direction.values().length) {
        LogUtil.log("[DEBUG-randomDirection]: all of directions are invalid");
        return false; // all of directions are invalid
      }
    }

    return true;
  }

  public static void movePlayersAndTheirBullets(
    Graphics2D graphics2D, ImageObserver imageObserver, Maze maze,
    List<Player> players) {
    for (Player player : players) {
      player.move(graphics2D, imageObserver, maze);
      player.moveBullets(graphics2D, imageObserver, maze);
    }
  }

  public String toString() {
    return String.format(
      "[Player(id=%s, x=%s, y=%s, blockIndex=%s, direction=%s, isMoving=%s, requestDeltaX=%d, requestDeltaY=%d, cumulativeDeltaX=%d, cumulativeDeltaY=%d)]",
      this.id,
      this.x,
      this.y,
      computeBlockIndexFromCurrentPosition(),
      this.direction,
      this.isMoving(),
      this.requestDeltaX,
      this.requestDeltaY,
      this.cumulativeDeltaX,
      this.cumulativeDeltaY);
  }

}
