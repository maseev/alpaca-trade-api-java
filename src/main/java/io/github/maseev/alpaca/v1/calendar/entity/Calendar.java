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

  LocalTime open();

  LocalTime close();
}
