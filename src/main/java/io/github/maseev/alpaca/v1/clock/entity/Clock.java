package io.github.maseev.alpaca.v1.clock.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableClock.class)
@JsonDeserialize(as = ImmutableClock.class)
public interface Clock {

  OffsetDateTime timestamp();

  @JsonProperty("is_open")
  boolean isOpen();

  @JsonProperty("next_open")
  OffsetDateTime nextOpen();

  @JsonProperty("next_close")
  OffsetDateTime nextClose();
}
