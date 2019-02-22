package io.github.maseev.alpaca.v1.bar;

import static java.time.OffsetDateTime.of;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.maseev.alpaca.v1.AlpacaAPI;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

public class BarAPIValidatationTest {

  @Test
  public void nullSymbolsMustThrowExceptions() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(1);
    String[] symbols = null;

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get(symbols, BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void emptySymbolsMustThrowException() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(1);
    String[] symbols = {};

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get(symbols, BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void symbolsWithTooManyTickersMustThrowException() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(1);
    String[] symbols = new String[350];

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get(symbols, BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void specifyingStartAfterEndDateMustThrowException() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.minusDays(1);

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get("AAPL", BarAPI.Timeframe.DAY, start, end, true, 10));
  }

  @Test
  public void specifyingTooLowLimitMustThrowException() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusDays(1);

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get("AAPL", BarAPI.Timeframe.DAY, start, end, true, 0));
  }

  @Test
  public void specifyingTooHighLimitMustThrowException() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "http://localhost:8080";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusDays(1);

    assertThrows(IllegalArgumentException.class,
      () -> api.bars().get("AAPL", BarAPI.Timeframe.DAY, start, end, true, 9000));
  }
}
