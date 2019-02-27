package io.github.maseev.alpaca.http.json.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

final class JsonMapper {

  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();

    MAPPER.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
    MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    MAPPER.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

    MAPPER.registerModule(new JavaTimeModule());
    MAPPER.findAndRegisterModules();
  }

  private JsonMapper() {
  }

  static ObjectMapper getMapper() {
    return MAPPER;
  }
}
