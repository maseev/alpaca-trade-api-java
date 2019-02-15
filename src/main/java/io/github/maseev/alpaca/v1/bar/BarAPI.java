package io.github.maseev.alpaca.v1.bar;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.v1.bar.entity.Bar;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class BarAPI {

  public enum Timeframe {
    MINUTE("1Min"),
    FIVE_MINUTES("5Min"),
    FIVETEEN_MINUTES("15Min"),
    DAY("1D");

    private final String alias;

    Timeframe(String alias) {
      this.alias = alias;
    }

    @Override
    @JsonValue
    public String toString() {
      return alias;
    }
  }

  static final String ENDPOINT = "/bars";

  private final HttpClient httpClient;

  public BarAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<Map<String, List<Bar>>> get(String symbol, Timeframe timeframe,
                                                OffsetDateTime start, OffsetDateTime end,
                                                boolean timeInclusive, int limit) {
    return get(new String[] {symbol}, timeframe, start, end, timeInclusive, limit);
  }

  public Listenable<Map<String, List<Bar>>> get(String[] symbols, Timeframe timeframe,
                                                OffsetDateTime start, OffsetDateTime end,
                                                boolean timeInclusive, int limit) {
    validate(symbols, start, end, limit);

    BoundRequestBuilder requestBuilder =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT, timeframe.toString())
      .addQueryParam("symbols", String.join(",", symbols))
      .addQueryParam("limit", Integer.toString(limit));

    if (timeInclusive) {
      requestBuilder.addQueryParam("start", format(start).toString());
      requestBuilder.addQueryParam("end", format(end).toString());
    } else {
      requestBuilder.addQueryParam("after", format(start).toString());
      requestBuilder.addQueryParam("until", format(end).toString());
    }

    ListenableFuture<Response> future = requestBuilder.execute();

    return new Listenable<>(
      new GenericTransformer<>(new TypeReference<Map<String, List<Bar>>>() {}), future);
  }

  private static void validate(String[] symbols, OffsetDateTime start, OffsetDateTime end,
                               int limit) {
    if (symbols == null) {
      throw new IllegalArgumentException("'symbols' parameter can't be null; symbols: null");
    }

    if (symbols.length == 0) {
      throw new IllegalArgumentException(
        String.format("'symbols' parameter can't be empty; symbols: %s", asList(symbols)));
    }

    final int MAX_SYMBOLS_NAMES = 200;

    if (symbols.length > MAX_SYMBOLS_NAMES) {
      throw new IllegalArgumentException(
        String.format("'symbols' parameter can't have more than %s symbol names; symbol: %s",
          MAX_SYMBOLS_NAMES, asList(symbols))
      );
    }

    if (start.isAfter(end)) {
      throw new IllegalArgumentException(
        String.format("'start' date can't be after 'end' date; start: %s, end: %s", start, end));
    }

    if (limit < 1 || limit > 1000) {
      throw new IllegalArgumentException(
        String.format("'limit' value must be between 1 and 1000; limit: %s", limit));
    }
  }

  private static OffsetDateTime format(OffsetDateTime time) {
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
