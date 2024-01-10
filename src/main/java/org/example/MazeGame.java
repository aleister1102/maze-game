package org.example;

import javax.swing.JFrame;

public class MazeGame extends JFrame {

  public static final int screenWidth = 340;
  public static final int screenHeight = 710;

  private static Process process;
  private static int port;

  public MazeGame() {
    // Network
    process = new Process(port);
    if (process.getServerSocket() != null) {
      Receiver receiver = new Receiver(process.getServerSocket());

      // open for connections in a separate thread
      ThreadUtil.start(receiver::open);

      // create senders for the process
      ThreadUtil.start(() -> Sender.createSendersWithRetrying(process));

      // ping to all clients
      for (Sender sender : process.getSenders().values()) {
        // send notify messages in a separate thread
        ThreadUtil.start(() -> sender.sendNotifyMessage("Hello"));
      }
    }

    // UI
    super.add(new GamePanel(process.getLogFile()));
  }

  public static void main(String[] args) {
    if (args.length > 0)
      port = Integer.parseInt(args[0]);

    MazeGame mazeGame = new MazeGame();
    mazeGame.setVisible(true);
    mazeGame.setTitle("MazeGame on port " + process.getPort());
    mazeGame.setSize(screenWidth, screenHeight);
    mazeGame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    mazeGame.setLocationRelativeTo(null);
  }

}
