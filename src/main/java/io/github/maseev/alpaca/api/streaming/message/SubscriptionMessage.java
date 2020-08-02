package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSubscriptionMessage.class)
@JsonDeserialize(as = ImmutableSubscriptionMessage.class)
public interface SubscriptionMessage {

  @Value.Default
  default String action() {
    return "listen";
  }

  @JsonProperty("data")
  Subscription subscription();
}
