package org.example;

import javax.swing.*;

public class MazeGame extends JFrame {

  public MazeGame() {
    super.add(new Model());
  }

  public static void main(String[] args) {
    MazeGame mazeGame = new MazeGame();
    mazeGame.setVisible(true);
    mazeGame.setTitle("Maze Game");
    mazeGame.setSize(380, 420);
    mazeGame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    mazeGame.setLocationRelativeTo(null);
  }
}
