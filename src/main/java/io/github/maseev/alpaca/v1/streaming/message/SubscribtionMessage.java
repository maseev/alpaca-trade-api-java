package io.github.maseev.alpaca.v1.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSubscribtionMessage.class)
@JsonDeserialize(as = ImmutableSubscribtionMessage.class)
public interface SubscribtionMessage {

  @Value.Default
  default String action() {
    return "listen";
  }

  @JsonProperty("data")
  Subscription subscription();
}
