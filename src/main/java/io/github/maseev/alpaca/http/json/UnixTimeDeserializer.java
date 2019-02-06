package io.github.maseev.alpaca.http.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;

public class UnixTimeDeserializer extends JsonDeserializer<Instant> {

  @Override
  public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String timestamp = p.getText().trim();

    return Instant.ofEpochSecond(Long.valueOf(timestamp));
  }
}
