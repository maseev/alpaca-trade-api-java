package io.github.maseev.alpaca.api.calendar;

import static io.github.maseev.alpaca.util.FutureTransformerUtil.transform;
import static java.lang.String.format;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.api.calendar.entity.Calendar;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * The calendar API serves the full list of market days from 1970 to 2029. It can also be queried by
 * specifying a start and/or end time to narrow down the results. In addition to the dates, the
 * response also contains the specific open and close times for the market days, taking into account
 * early closures.
 */
public class CalendarAPI {

  static final String ENDPOINT = "/calendar";

  private final HttpClient httpClient;

  public CalendarAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Returns the market calendar.
   *
   * @param start The first date to retrieve data for (inclusive)
   * @param end The last date to retrieve data for (inclusive)
   * @return the market {@link Calendar}
   */
  public CompletableFuture<List<Calendar>> get(LocalDate start, LocalDate end) {
    if (start.isAfter(end)) {
      throw new IllegalArgumentException(
        format("'start' can't be after 'end'; start: %s, end: %s", start, end));
    }

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT)
        .addQueryParam("start", start.toString())
        .addQueryParam("end", end.toString())
        .execute();

    return transform(future,
      new GenericTransformer<>(new TypeReference<List<Calendar>>() {}));
  }
}
