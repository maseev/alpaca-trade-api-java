package io.github.maseev.alpaca.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.maseev.alpaca.http.JsonMapper;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static String toJson(Object object) throws JsonProcessingException {
    return JsonMapper.getMapper().writeValueAsString(object);
  }
}
