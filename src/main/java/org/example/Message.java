package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {

  private int senderPort;
  private int listenerPort;
  private String content;

  public String toString() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      LogUtil.log("Error(s) occurred while converting message to string: ", e.getMessage());
      return "{}";
    }
  }

}
