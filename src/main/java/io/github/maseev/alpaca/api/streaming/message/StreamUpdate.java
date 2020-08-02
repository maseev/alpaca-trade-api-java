package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.http.json.StreamUpdateDeserializer;
import io.github.maseev.alpaca.api.streaming.Stream;
import io.github.maseev.alpaca.api.streaming.entity.Event;
import org.immutables.value.Value;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(as = ImmutableStreamUpdate.class)
@JsonDeserialize(using = StreamUpdateDeserializer.class)
public interface StreamUpdate {

  Stream stream();

  Event data();
}
