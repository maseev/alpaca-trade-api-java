package io.github.maseev.alpaca.http.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.maseev.alpaca.v1.streaming.Stream;
import io.github.maseev.alpaca.v1.streaming.entity.Event;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableStreamUpdate;
import io.github.maseev.alpaca.v1.streaming.message.StreamUpdate;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class StreamUpdateDeserializer extends JsonDeserializer<StreamUpdate> {

  @Override
  public StreamUpdate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    ObjectNode obj = mapper.readTree(p);
    Iterator<Map.Entry<String, JsonNode>> fields = obj.fields();

    Map.Entry<String, JsonNode>  streamField = fields.next();
    String streamFieldValue = streamField.getValue().asText();
    Stream stream = Stream.valueOf(streamFieldValue.toUpperCase());

    Map.Entry<String, JsonNode> dataField = fields.next();
    Event event = mapper.readValue(dataField.getValue().traverse(), stream.getTargetClass());

    return ImmutableStreamUpdate.builder()
      .stream(stream)
      .data(event)
      .build();
  }
}
