package org.example;

import java.io.File;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import lombok.Data;
import static org.example.LogUtil.LOG_DIRECTORY;

@Data
public class Process {

  private int port;
  private ServerSocket serverSocket;
  private Map<Integer, Sender> senders = new HashMap<>();

  private File logFile;
  private final Scanner scanner = new Scanner(System.in);

  public Process(int port) {
    Optional.ofNullable(SocketUtil.createServerSocket(port)).ifPresent(createdServerSocket -> {
        this.serverSocket = createdServerSocket;
        this.port = createdServerSocket.getLocalPort();
        this.logFile = FileUtil.createFile(String.format("%sprocess-%d.txt", LOG_DIRECTORY, port));
        LogUtil.log("[DEBUG-Process]: listener is running on port %d", this.port);
      }
    );
  }

}
