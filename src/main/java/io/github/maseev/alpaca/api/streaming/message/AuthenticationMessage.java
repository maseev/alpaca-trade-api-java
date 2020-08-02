package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAuthenticationMessage.class)
@JsonDeserialize(as = ImmutableAuthenticationMessage.class)
public interface AuthenticationMessage {

  @Value.Default
  default String action() {
    return "authenticate";
  }

  @JsonProperty("data")
  Credentials credentials();
}
