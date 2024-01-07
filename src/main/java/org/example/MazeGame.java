package org.example;

import javax.swing.*;

public class MazeGame extends JFrame {

  public static final int screenWidth = 340;
  public static final int screenHeight = 700;

  public MazeGame() {

    super.add(new GamePanel());
  }

  public static void main(String[] args) {

    MazeGame mazeGame = new MazeGame();
    mazeGame.setVisible(true);
    mazeGame.setTitle("Maze MazeGame");
    mazeGame.setSize(screenWidth, screenHeight);
    mazeGame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    mazeGame.setLocationRelativeTo(null);
  }

}
