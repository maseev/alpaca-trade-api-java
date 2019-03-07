package io.github.maseev.alpaca.v1.calendar.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.time.LocalTime;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableCalendar.class)
@JsonDeserialize(as = ImmutableCalendar.class)
public interface Calendar {

  LocalDate date();

  /**
   * @return The time the market opens at on this {@link Calendar#date() date}
   */
  LocalTime open();

  /**
   * @return The time the market closes at on this {@link Calendar#date() date}
   */
  LocalTime close();
}
