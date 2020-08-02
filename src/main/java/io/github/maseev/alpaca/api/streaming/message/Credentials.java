package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCredentials.class)
@JsonDeserialize(as = ImmutableCredentials.class)
public interface Credentials {

  @JsonProperty("key_id")
  String keyId();

  @JsonProperty("secret_key")
  String secretKey();
}
