package org.example.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import lombok.Data;
import org.example.constants.Configuration;
import org.example.models.Message;
import org.example.utils.LogUtil;
import org.example.utils.SocketUtil;

@Data
public class Sender {

  private static final int RETRY_DELAY = 10000;

  private int senderPort;
  private int receiverPort;
  private Socket socket;
  private BufferedReader bufferedReader; // for reading message from server
  private BufferedWriter bufferedWriter; // for sending message to server
  private File logFile;

  public Sender(int senderPort, int receiverPort, Socket socket, File logFile) {
    try {
      this.senderPort = senderPort;
      this.receiverPort = receiverPort;
      this.socket = socket;
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.logFile = logFile;
    } catch (IOException exception) {
      SocketUtil.closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  public static void createSendersWithRetrying(Process process) {
    try {
      boolean createdFailed = true;
      while (createdFailed) {
        createSenders(process);
        createdFailed = process.getSenders().size() < Configuration.PORTS.size() - 1;
        if (!createdFailed) continue;

        LogUtil.log("[DEBUG-createSenders]: failed to create all of senders, retrying in %d seconds", RETRY_DELAY / 1000);
        Thread.sleep(RETRY_DELAY);
      }
    } catch (InterruptedException e) {
      LogUtil.log("[ERROR-createSendersWithRetry]: error(s) occurred while trying to create senders: %s", e.getMessage());
    }
  }

  private static void createSenders(Process process) {
    for (int runningPort : Configuration.PORTS) {
      if (runningPort == process.getPort()) continue;
      if (process.getSenders().containsKey(runningPort)) continue;
      LogUtil.log("[DEBUG-createSenders]: creating a socket for port %s", runningPort);
      Socket socket = SocketUtil.createClientSocket(runningPort);

      if (socket == null) continue;
      LogUtil.log("[DEBUG-createSenders]: socket for port %s is created", runningPort);
      Sender sender = new Sender(process.getPort(), runningPort, socket, process.getLogFile());

      LogUtil.log("[DEBUG-createSenders]: sender for port %s is created", runningPort);
      process.getSenders().put(runningPort, sender);
    }
  }

  public void sendNotifyMessage(String content) {
    String currentThreadName = Thread.currentThread().getName();
    LogUtil.log("[DEBUG-sendNotifyMessage]: %s of port %s is sending notify message to port %s",
      currentThreadName, senderPort, receiverPort);

    try {
      if (socket.isConnected()) {
        Message notifyMessage = buildNotifyMessage(content);
        bufferedWriter.write(notifyMessage.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } catch (Exception e) {
      LogUtil.log("[ERROR-sendNotifyMessage]: error(s) occurred while sending notify message: %s", e.getMessage());
      SocketUtil.closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  private Message buildNotifyMessage(String content) {
    return Message.builder()
      .senderPort(senderPort)
      .listenerPort(receiverPort)
      .content(content)
      .build();
  }

}
