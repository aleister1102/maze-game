package org.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;
import lombok.Data;

@Data
public class Maze {

  public static final int BLOCK_SIZE = 20;
  public static final int ROWS = 32;
  public static final int COLUMNS = 16;
  public static final int SCREEN_HEIGHT = Maze.ROWS * Maze.BLOCK_SIZE;
  public static final int SCREEN_WIDTH = Maze.COLUMNS * Maze.BLOCK_SIZE;

  public static final int LEFT_BORDER = 1;
  public static final int TOP_BORDER = 2;
  public static final int RIGHT_BORDER = 4;
  public static final int BOTTOM_BORDER = 8;

  // 0: blue obstacles, 16: white dots
  // 1: left border, 2: top border, 4: right border, 8: bottom border
  //private final short[] mapData = {
  //  19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 0, 0, 0,
  //  17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0,
  //  25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0,
  //  0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0,
  //  19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 16, 24, 24, 24, 24, 20, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21, 0, 0, 0, 0,
  //  17, 16, 16, 16, 24, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 21, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20, 0, 0, 0, 0, 0,
  //  17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 0,
  //  21, 0, 0, 0, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 0,
  //  17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 18, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 17, 20, 0, 0, 0, 0, 0,
  //  25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28, 0, 0, 0, 0, 0
  //};

  private short[] mapData;
  private short[] screenData;

  public Maze() {

    mapData = new short[ROWS * COLUMNS];
    screenData = new short[ROWS * COLUMNS];

    generateRandomMaze(mapData);
    copyMapDataToScreenData();
  }

  public short getScreenDataAtIndex(int index) {

    if (index > screenData.length - 1 || index < 0) {
      return 0;
    }

    return screenData[index];
  }

  public void setScreenDataAtIndex(int index, short value) {

    if (index > screenData.length - 1 || index < 0) {
      return;
    }

    screenData[index] = value;
  }

  private void generateRandomMaze(short[] mapData) {

    Arrays.fill(mapData, (short) 0);
    Random random = new Random();

    // first row has top wall
    for (int i = 0; i < COLUMNS; i++) {
      mapData[i] += TOP_BORDER;
    }

    // last row has bottom wall
    for (int i = (ROWS - 1) * COLUMNS; i < ROWS * COLUMNS; i++) {
      mapData[i] += BOTTOM_BORDER;
    }

    // first column has left wall
    for (int i = 0; i < ROWS * COLUMNS; i += COLUMNS) {
      mapData[i] += LEFT_BORDER;
    }

    // last column has right wall
    for (int i = COLUMNS - 1; i < ROWS * COLUMNS; i += COLUMNS) {
      mapData[i] += RIGHT_BORDER;
    }

    // randomly set left, top, right, or bottom wall of each cell,
    // except for the first and last row and column
    for (int i = COLUMNS; i < (ROWS - 1) * COLUMNS; i++) {
      if (i % COLUMNS != 0 && i % COLUMNS != COLUMNS - 1) {
        int randomInt = random.nextInt(2);
        if (randomInt == 0) {
          mapData[i] += LEFT_BORDER;
        } else {
          mapData[i] += TOP_BORDER;
        }
      }
    }
  }

  private void copyMapDataToScreenData() {

    System.arraycopy(mapData, 0, screenData, 0, ROWS * COLUMNS);
  }

  public void draw(Graphics2D graphics2D, Dimension dimension) {
    drawBackground(graphics2D, dimension);
    drawWalls(graphics2D);
  }

  private void drawBackground(Graphics2D graphics2D, Dimension dimension) {
    graphics2D.setColor(Color.black);
    graphics2D.fillRect(0, 0, dimension.width, dimension.height);
  }

  private void drawWalls(Graphics2D graphics2D) {
    graphics2D.setStroke(new BasicStroke(2));

    short blockIndex = 0;
    for (int y = 0; y < SCREEN_HEIGHT; y += Maze.BLOCK_SIZE) {
      for (int x = 0; x < SCREEN_WIDTH; x += Maze.BLOCK_SIZE) {
        short block = this.getScreenDataAtIndex(blockIndex);

        this.drawWall(graphics2D, x, y, block);

        blockIndex++;
      }
    }
  }

  private void drawWall(Graphics2D graphics2D, int x, int y, short block) {

    graphics2D.setColor(new Color(112, 32, 224));
    if (hasLeftBorder(block)) {
      graphics2D.drawLine(x, y, x, y + Maze.BLOCK_SIZE - 1);
    }

    if (hasRightBorder(block)) {
      graphics2D.drawLine(x + Maze.BLOCK_SIZE - 1, y, x + Maze.BLOCK_SIZE - 1,
        y + Maze.BLOCK_SIZE - 1);
    }

    graphics2D.setColor(new Color(47, 193, 206));
    if (hasTopBorder(block)) {
      graphics2D.drawLine(x, y, x + Maze.BLOCK_SIZE - 1, y);
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

  public boolean isHavingValidMoveRequest(Pacman pacman, int blockIndex) {

    short currentBlock = getScreenDataAtIndex(blockIndex);
    short nextBlock = getScreenDataAtIndex(blockIndex + 1);
    short belowBlock = getScreenDataAtIndex(blockIndex + COLUMNS);

    // move to left
    if (pacman.getRequestDeltaX() == -1 && pacman.getRequestDeltaY() == 0 && hasLeftBorder(currentBlock)) return false;
      // move to top
    else if (pacman.getRequestDeltaX() == 0 && pacman.getRequestDeltaY() == -1 && hasTopBorder(currentBlock))
      return false;
      // move to right
    else if (pacman.getRequestDeltaX() == 1 && pacman.getRequestDeltaY() == 0 && (hasRightBorder(currentBlock) || hasLeftBorder(nextBlock)))
      return false;
      // move to bottom
    else
      return pacman.getRequestDeltaX() != 0 || pacman.getRequestDeltaY() != 1 || (!hasBottomBorder(currentBlock) && !hasTopBorder(belowBlock));
  }

  public boolean isHavingInvalidMoveRequest(Pacman pacman, int blockIndex) {

    return !isHavingValidMoveRequest(pacman, blockIndex);
  }

}
