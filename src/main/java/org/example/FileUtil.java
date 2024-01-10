package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class FileUtil {

  public static File createFile(String filePath) {
    File file = new File(filePath);
    createFile(file);
    return file;
  }

  private static void createFile(File file) {
    try {
      if (file.createNewFile()) {
        LogUtil.log("[DEBUG-createFile]: file created: %s", file.getName());
      }
    } catch (IOException e) {
      LogUtil.log("[ERROR-createFile]: error(s) occurred while creating file",
        Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
      );
    }
  }

  public static void clearFile(File file) {
    try {
      if (!file.exists()) return;

      FileWriter writer = new FileWriter(file);
      writer.write("");
      writer.close();
    } catch (IOException e) {
      LogUtil.log("[ERROR-createFile]: error(s) have been occurred while clearing file: ",
        Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
      );
    }
  }

  public static void write(String message, File logFile) {
    try (FileWriter writer = new FileWriter(logFile, true)) {
      writer.write(message + "\n");
    } catch (IOException e) {
      LogUtil.log("[ERROR-createFile]: error(s) have been occurred while writing to file: ",
        Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
      );
    }
  }

}
