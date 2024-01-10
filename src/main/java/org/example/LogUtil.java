package org.example;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class LogUtil {

  public static final String LOG_FILE = "src/main/resources/logs/logs.txt";
  private static final String DATE_FORMAT = "yyyy/MM/dd:HH:mm:ss.SSS";
  private static final Set<String> logTrace = new LinkedHashSet<>();

  public static void log(String format, Object... args) {
    String message = String.format(format, args);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    String formattedDate = simpleDateFormat.format(new Date());
    String trimmedDate = formattedDate.substring(0, formattedDate.length() - 2);
    String messageWithTrimmedTimestamp = String.format("[%s] - %s", trimmedDate, message);
    logTrace.add(messageWithTrimmedTimestamp);
    System.out.println(messageWithTrimmedTimestamp);
  }

  public static void dumpLog(File logFile) {
    if (logFile == null) return;
    logTrace.forEach(message -> FileUtil.write(message, logFile));
  }

  public static void clearLogTrace() {
    logTrace.clear();
  }

}

