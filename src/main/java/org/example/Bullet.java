package org.example;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.ImageIcon;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bullet extends Actor {

  private Image down, up, left, right;


  public Bullet() {

    this.setDown(new ImageIcon("src/main/resources/images/bullet-down.png").getImage());
    this.setUp(new ImageIcon("src/main/resources/images/bullet-up.png").getImage());
    this.setLeft(new ImageIcon("src/main/resources/images/bullet-left.png").getImage());
    this.setRight(new ImageIcon("src/main/resources/images/bullet-right.png").getImage());
  }


  @Override
  protected void draw(Graphics2D graphics2D, ImageObserver imageObserver) {

  }

}
