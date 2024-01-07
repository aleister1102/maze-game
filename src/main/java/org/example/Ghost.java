package org.example;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;

@Data
@EqualsAndHashCode(callSuper = true)
public class Ghost extends Actor {

  Image image;

  public Ghost() {

    this.setImage(new ImageIcon("src/main/resources/images/ghost.gif").getImage());
  }

  public void draw(Graphics2D graphics2D, ImageObserver observer) {

    graphics2D.drawImage(image, x, y, observer);
  }

  public void initialize(int maxSpeed) {
    // set ghost position
    this.setX(9 * Maze.BLOCK_SIZE);
    this.setY(9 * Maze.BLOCK_SIZE);

    // random ghost speed
    int randomGhostSpeed = Actor.getRandomSpeed();
    this.setSpeed(randomGhostSpeed);

    // make sure that ghost speed is not greater than pacman speed
    if (this.getSpeed() > maxSpeed) {
      this.setSpeed(maxSpeed);
    }
  }

}
