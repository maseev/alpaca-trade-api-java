package io.github.maseev.alpaca.api.bar;

import static java.time.OffsetDateTime.of;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.maseev.alpaca.api.AlpacaAPI;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BarAPIValidatationTest {

  private AlpacaAPI api;

  @BeforeEach
  public void before() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";

    api = new AlpacaAPI(baseUrl, baseUrl, baseUrl, validKeyId, validSecretKey);
  }

  @Test
  public void nullSymbolsMustThrowExceptions() {
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(1);
    String[] symbols = null;

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get(symbols, BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void emptySymbolsMustThrowException() {
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(1);
    String[] symbols = {};

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get(symbols, BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void symbolsWithTooManyTickersMustThrowException() {
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(1);
    String[] symbols = new String[350];

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get(symbols, BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void specifyingStartAfterEndDateMustThrowException() {
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.minusDays(1);

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get("AAPL", BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void specifyingTooLowLimitMustThrowException() {
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusDays(1);

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get("AAPL", BarAPI.Timeframe.DAY, start, end, true, 0));
  }

  @Test
  public void specifyingTooHighLimitMustThrowException() {
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusDays(1);

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get("AAPL", BarAPI.Timeframe.DAY, start, end, true, 9000));
  }
}
