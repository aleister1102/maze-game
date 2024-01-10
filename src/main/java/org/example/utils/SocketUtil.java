package org.example.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.example.constants.Configuration;

public class SocketUtil {

  public static ServerSocket createServerSocket(int port) {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      LogUtil.log("[DEBUG-createServerSocket]: server socket is created on port %s", serverSocket.getLocalPort());
      return serverSocket;
    } catch (IOException e) {
      LogUtil.log("[ERROR-createServerSocket]: port %s is already in use", port);
      return null;
    }
  }

  public static Socket createClientSocket(int port) {
    try {
      return new Socket(Configuration.LOCALHOST, port);
    } catch (IOException e) {
      LogUtil.log("[ERROR-createClientSocket]: error(s) occurred when connecting to port %s: %s", port, e.getMessage());
      return null;
    }
  }

  public static void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
    try {
      if (bufferedReader != null) {
        bufferedReader.close(); // also close the inside stream
      }
      if (bufferedWriter != null) {
        bufferedWriter.close();
      }
      if (socket != null) {
        socket.close(); // also close the input and output stream
      }
    } catch (IOException exception) {
      LogUtil.log("[ERROR-closeEverything]: error(s) occurred while closing the socket: %s", exception.getMessage());
    }
  }

}
