package org.example.constants;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Configuration {

  public static final String LOCALHOST = "localhost";
  public static final List<Integer> PORTS = new LinkedList<>();

  static {
    PORTS.add(8080);
    PORTS.add(8081);
    PORTS.add(8082);
  }

}
