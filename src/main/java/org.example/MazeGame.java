package org.example;

import lombok.Builder;
import lombok.Data;

import javax.swing.*;

public class MazeGame extends JFrame {

  public MazeGame() {
    add(new Model());
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
