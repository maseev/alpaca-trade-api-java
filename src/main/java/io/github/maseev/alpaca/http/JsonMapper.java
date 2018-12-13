package io.github.maseev.alpaca.http;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonMapper {

  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();
    MAPPER.findAndRegisterModules();
  }

  private JsonMapper() {
  }

  public static ObjectMapper getMapper() {
    return MAPPER;
  }
}
