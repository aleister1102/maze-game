package org.example;

import javax.swing.*;

public class MazeGame extends JFrame {

  public MazeGame() {

    super.add(new GamePanel());
  }

  public static void main(String[] args) {

    MazeGame mazeGame = new MazeGame();
    mazeGame.setVisible(true);
    mazeGame.setTitle("Maze MazeGame");
    mazeGame.setSize(500, 550);
    mazeGame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    mazeGame.setLocationRelativeTo(null);
  }

}
