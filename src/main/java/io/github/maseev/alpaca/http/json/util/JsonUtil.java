package io.github.maseev.alpaca.http.json.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;

public final class JsonUtil {

  private JsonUtil() {
  }

  public static String toJson(Object object) throws JsonProcessingException {
    return JsonMapper.getMapper().writeValueAsString(object);
  }

  public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
    return JsonMapper.getMapper().readValue(json, clazz);
  }

  public static <T> T fromJson(byte[] json, Class<T> clazz) throws IOException {
    return JsonMapper.getMapper().readValue(json, clazz);
  }

  public static <T> T fromJson(String json, TypeReference<T> typeReference) throws IOException {
    return JsonMapper.getMapper().readValue(json, typeReference);
  }
}
