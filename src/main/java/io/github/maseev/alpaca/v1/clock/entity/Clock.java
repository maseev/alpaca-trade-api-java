package io.github.maseev.alpaca.v1.clock.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDateTime;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableClock.class)
@JsonDeserialize(as = ImmutableClock.class)
public interface Clock {

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  LocalDateTime timestamp();

  @JsonProperty("is_open")
  boolean isOpen();

  @JsonProperty("next_open")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  LocalDateTime nextOpen();

  @JsonProperty("next_close")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  LocalDateTime nextClose();
}
