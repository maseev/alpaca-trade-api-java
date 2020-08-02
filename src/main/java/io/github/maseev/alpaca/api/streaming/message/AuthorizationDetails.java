package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAuthorizationDetails.class)
@JsonDeserialize(as = ImmutableAuthorizationDetails.class)
public interface AuthorizationDetails {

  enum Status {
    AUTHORIZED,
    UNAUTHORIZED;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  Status status();

  @Value.Default
  default String action() {
    return "authenticate";
  }
}
