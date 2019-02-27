package io.github.maseev.alpaca.v1.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSubscribtionResponse.class)
@JsonDeserialize(as = ImmutableSubscribtionResponse.class)
public interface SubscribtionResponse {

  @Value.Default
  default String stream() {
    return "listening";
  }

  @JsonProperty("data")
  Subscription subscription();
}
