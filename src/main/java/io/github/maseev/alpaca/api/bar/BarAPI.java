package io.github.maseev.alpaca.api.bar;

import static io.github.maseev.alpaca.http.json.util.DateFormatUtil.format;
import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.exception.UnprocessableException;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.api.bar.entity.Bar;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * The bars API provides time-aggregated price and volume data.
 */
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

  /**
   * @see BarAPI#get(String[], Timeframe, OffsetDateTime, OffsetDateTime, boolean, int)
   */
  public Listenable<Map<String, List<Bar>>> get(String symbol, Timeframe timeframe,
                                                OffsetDateTime start, OffsetDateTime end,
                                                boolean timeInclusive, int limit) {
    return get(new String[] {symbol}, timeframe, start, end, timeInclusive, limit);
  }

  /**
   * Retrieves a list of bars for each requested symbol. It is guaranteed all bars are in
   * ascending order by time. Currently, no “incomplete” bars are returned. For example, a 1
   * minute bar for 09:30 will not be returned until 09:31.

   * @param symbols One or more (max 200) symbol names
   * @param timeframe A specific timeframe for {@link Bar} instances for every symbol
   * @param start Filter bars equal to or after this time (depending on the {@code timeInclusive})
   * @param end Filter bars equal to or before this time (depending on the {@code timeInclusive})
   * @param timeInclusive Whether or not to include the {@code start} and {@code end} parameters
   *                      into a date range
   * @param limit The maximum number of bars to be returned for each symbol. It can be between 1 and 1000
   * @return A hash-map with a key for each symbol and the list of {@link Bar} as the values.
   * @throws UnprocessableException in case the parameters are not well formed.
   */
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
}
