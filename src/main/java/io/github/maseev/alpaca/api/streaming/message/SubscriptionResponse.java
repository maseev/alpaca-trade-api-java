package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSubscriptionResponse.class)
@JsonDeserialize(as = ImmutableSubscriptionResponse.class)
public interface SubscriptionResponse {

  @Value.Default
  default String stream() {
    return "listening";
  }

  @JsonProperty("data")
  Subscription subscription();
}
