package org.example;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import java.util.List;
import javax.swing.ImageIcon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import static org.example.Maze.BLOCK_SIZE;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bullet extends Actor {

  public static final int SPEED_MULTIPLIER = 4;
  private Player player;
  private boolean hasCollision = false;

  public Bullet() {
    loadImages();
  }

  public Bullet(Player player, int id) {
    this();
    this.id = id;
    this.x = player.getX();
    this.y = player.getY();
    this.direction = Direction.STAY;
    //* FEAT2: speed of bullet is 4 times faster than player
    this.speed = player.getSpeed() * SPEED_MULTIPLIER;
    this.player = player;
  }

  private void loadImages() {
    this.down = new ImageIcon("src/main/resources/images/bullet-down.png").getImage();
    this.up = new ImageIcon("src/main/resources/images/bullet-up.png").getImage();
    this.left = new ImageIcon("src/main/resources/images/bullet-left.png").getImage();
    this.right = new ImageIcon("src/main/resources/images/bullet-right.png").getImage();
  }

  @Override
  public void move(Graphics2D graphics, ImageObserver imageObserver, Maze maze) {
    boolean notMoving = this.isNotMoving();
    boolean isOutsideTheWall = this.isOutsideTheWall();
    boolean isOppositeToWall = this.isOppositeToWall();
    boolean thereIsAPlayerAhead = this.isThereAPlayerAhead();
    boolean hasInvalidMoveRequest = this.hasInvalidMoveRequest();

    if (notMoving) return;
    if (!isOppositeToWall && thereIsAPlayerAhead) collideWithPlayer();
    if (hasInvalidMoveRequest || isOutsideTheWall) hasCollision = true;
    else {
      updateDeltaBasedOnMoveRequest();
      updatePosition();
      draw(graphics, imageObserver);
    }
    if (hasCollision) this.stopMoving();
  }

  private void collideWithPlayer() {
    // find current block index
    int currentBlockIndex = this.computeBlockIndexFromCurrentPosition();
    LogUtil.log("[EVENT]: bullet %s has collision with a player adjacent to block index %s", id, currentBlockIndex);

    // find player that is in an adjacent block
    List<Player> players = GamePanel.players;
    for (Player player : players) {
      int playerBlockIndex = player.computeBlockIndexFromCurrentPosition();
      Player bulletOwner = this.getPlayer();
      if (Maze.isAdjacentBlockIndexes(currentBlockIndex, playerBlockIndex) && player != bulletOwner) {
        LogUtil.log("[DEBUG]: found player %s at adjacent block index %s", player.getId(), playerBlockIndex);
        player.randomPlayerState(players);
        break;
      }
    }
  }

  @Override
  protected void updateDeltaBasedOnMoveRequest() {
    //? allow bullet to move out of the wall
    deltaX = requestDeltaX;
    deltaY = requestDeltaY;
  }

  @Override
  public void draw(Graphics2D graphics2D, ImageObserver imageObserver) {
    if (hasCollision) return;
    graphics2D.setColor(Color.magenta);

    if (isMovingLeft()) {
      graphics2D.drawImage(left, x + 1, y + 5, imageObserver);
      graphics2D.drawString(String.valueOf(id), x + BLOCK_SIZE + 3, y + 15);
    } else if (isMovingRight()) {
      graphics2D.drawImage(right, x + 1, y + 5, imageObserver);
      graphics2D.drawString(String.valueOf(id), x - 5, y + 15);
    } else if (isMovingUp()) {
      graphics2D.drawImage(up, x + 6, y + 1, imageObserver);
      graphics2D.drawString(String.valueOf(id), x + 8, y + 5);
    } else if (isMovingDown()) {
      graphics2D.drawImage(down, x + 6, y + 1, imageObserver);
      graphics2D.drawString(String.valueOf(id), x + 8, y + 5);
    }
  }

  public String toString() {
    return String.format(
      "[Bullet(id=%s, hasCollisionWithWall=%s, isMoving=%s, requestDeltaX=%d, requestDeltaY=%d, cumulativeDeltaX=%d, cumulativeDeltaY=%d)]",
      this.id,
      this.hasCollision,
      this.isMoving(),
      this.requestDeltaX,
      this.requestDeltaY,
      this.cumulativeDeltaX,
      this.cumulativeDeltaY);
  }

}
