package org.example;

import java.awt.Color;
import java.awt.Graphics2D;
import lombok.Data;

@Data
public class Maze {

  public static final int BLOCK_SIZE = 20;
  public static final int ROWS = 32;
  public static final int COLUMNS = 16;

  // 0: blue obstacles, 16: white dots
  // 1: left border, 2: top border, 4: right border, 8: bottom border
  private final short[] mapData = {
    19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
    17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 16, 24, 24, 24, 24, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
    17, 16, 16, 16, 24, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 18, 18, 18, 18, 20,
    17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 16, 20,
    21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 20,
    17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 18, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
    25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
  };

  private short[] screenData;

  public Maze() {

    screenData = new short[ROWS * COLUMNS];
  }

  public short getScreenDataAtIndex(int index) {

    if (index > screenData.length - 1) {
      return 0;
    }

    return screenData[index];
  }

  public void setScreenDataAtIndex(int index, short value) {

    if (index > screenData.length - 1) {
      return;
    }

    screenData[index] = value;
  }

  public void copyMapDataToScreenData() {

    System.arraycopy(mapData, 0, screenData, 0, ROWS * COLUMNS);
  }

  public void drawObstacle(Graphics2D graphics2D, int x, int y, int blockIndex) {

    graphics2D.setColor(new Color(0, 72, 251));
    if (isObstacle(blockIndex)) {
      graphics2D.fillRect(x, y, Maze.BLOCK_SIZE, Maze.BLOCK_SIZE);
    }
  }

  private boolean isObstacle(int blockIndex) {

    return mapData[blockIndex] == 0; // only check mapData, not screenData
  }

  public void drawObstacleBorders(Graphics2D graphics2D, int x, int y, short block) {

    graphics2D.setColor(new Color(112, 32, 224));
    if (hasLeftBorder(block)) {
      graphics2D.drawLine(x, y, x, y + Maze.BLOCK_SIZE - 1);
    }

    if (hasTopBorder(block)) {
      graphics2D.drawLine(x, y, x + Maze.BLOCK_SIZE - 1, y);
    }

    if (hasRightBorder(block)) {
      graphics2D.drawLine(x + Maze.BLOCK_SIZE - 1, y, x + Maze.BLOCK_SIZE - 1,
        y + Maze.BLOCK_SIZE - 1);
    }

    if (hasBottomBorder(block)) {
      graphics2D.drawLine(x, y + Maze.BLOCK_SIZE - 1, x + Maze.BLOCK_SIZE - 1,
        y + Maze.BLOCK_SIZE - 1);
    }
  }

  private boolean hasLeftBorder(short block) {

    return (block & 1) != 0;
  }

  private boolean hasTopBorder(short block) {

    return (block & 2) != 0;
  }

  private boolean hasRightBorder(short block) {

    return (block & 4) != 0;
  }

  private boolean hasBottomBorder(short block) {

    return (block & 8) != 0;
  }

  public void drawWhiteDot(Graphics2D graphics2D, int x, int y, short block) {

    if (isDot(block)) {
      graphics2D.setColor(new Color(255, 255, 255));
      graphics2D.fillOval(x + 10, y + 10, 6, 6);
    }
  }

  public boolean isDot(short block) {

    return (block & 16) != 0;
  }

}
