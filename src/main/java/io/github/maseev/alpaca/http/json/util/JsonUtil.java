package io.github.maseev.alpaca.http.json.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.maseev.alpaca.http.json.JsonMapper;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static String toJson(Object object) throws JsonProcessingException {
    return JsonMapper.getMapper().writeValueAsString(object);
  }
}
