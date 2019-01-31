package io.github.maseev.alpaca.v1.calendar;

import static java.lang.String.format;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.transformer.ListTransformer;
import io.github.maseev.alpaca.v1.calendar.entity.Calendar;
import java.time.LocalDate;
import java.util.List;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class CalendarAPI {

  private static final String CALENDAR_ENDPOINT = "/calendar";

  private final HttpClient httpClient;

  public CalendarAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<List<Calendar>> get(LocalDate start, LocalDate end) {
    if (end.isBefore(start)) {
      throw new IllegalArgumentException(
        format("end date can't be before start date; start date: %s, end date: %s", start, end));
    }

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, CALENDAR_ENDPOINT)
        .addQueryParam("start", start.toString())
        .addQueryParam("end", end.toString())
        .execute();

    return new Listenable<>(new ListTransformer<>(new TypeReference<List<Calendar>>() {}), future);
  }
}
