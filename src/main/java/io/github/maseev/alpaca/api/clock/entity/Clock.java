package io.github.maseev.alpaca.api.clock.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableClock.class)
@JsonDeserialize(as = ImmutableClock.class)
public interface Clock {

  /**
   * @return Current timestamp
   */
  OffsetDateTime timestamp();

  /**
   * @return Whether or not the market is open
   */
  @JsonProperty("is_open")
  boolean isOpen();

  /**
   * @return Next market open timestamp
   */
  @JsonProperty("next_open")
  OffsetDateTime nextOpen();

  /**
   * @return Next market close timestamp
   */
  @JsonProperty("next_close")
  OffsetDateTime nextClose();
}
