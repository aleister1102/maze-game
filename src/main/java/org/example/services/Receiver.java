package org.example.services;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.Data;
import org.example.utils.LogUtil;

@Data
public class Receiver {

  private ServerSocket serverSocket;

  public Receiver(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public void open() {
    try {
      while (!serverSocket.isClosed()) {
        Socket senderSocket = serverSocket.accept();
        LogUtil.log("[DEBUG-open]: a sender is connected to the receiver! Client port: %s", senderSocket.getPort());
      }
    } catch (IOException ioException) {
      LogUtil.log("[ERROR-open]: error(s) occurred while accepting the sender socket: %s", ioException.getMessage());
      close(serverSocket);
    }
  }

  private void close(Closeable socket) {
    try {
      if (socket != null) socket.close();
    } catch (IOException ioException) {
      LogUtil.log("[ERROR-close]: error(s) occurred while closing the socket: %s", ioException.getMessage());
    }
  }

}
