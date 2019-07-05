package io.github.maseev.alpaca.http.json.util;

import java.time.OffsetDateTime;

public final class DateFormatUtil {

  public static final String DATE_TIME_FORMAT =
    "yyyy-MM-dd'T'HH:mm:ss.[SSSSSSSSS][SSSSSS][SSSSSS][SSSS]'Z'";

  public static final String DATE_TIME_NO_NANOSECONDS_FORMAT =
    "yyyy-MM-dd'T'HH:mm:ss'Z'";

  private DateFormatUtil() {
  }

  public static OffsetDateTime format(OffsetDateTime time) {
    if (time.getNano() == 0) {
      return time;
    }

    return OffsetDateTime.of(
      time.getYear(),
      time.getMonthValue(),
      time.getDayOfMonth(),
      time.getHour(),
      time.getMinute(),
      time.getSecond(),
      0,
      time.getOffset());
  }
}
