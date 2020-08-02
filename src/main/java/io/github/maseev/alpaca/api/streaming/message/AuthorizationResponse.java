package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAuthorizationResponse.class)
@JsonDeserialize(as = ImmutableAuthorizationResponse.class)
public interface AuthorizationResponse {

  @Value.Default
  default String stream() {
    return "authorization";
  }

  @JsonProperty("data")
  AuthorizationDetails details();
}
