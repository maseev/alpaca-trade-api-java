package io.github.maseev.alpaca.v1.clock;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.v1.clock.entity.Clock;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class ClockAPI {

  static final String CLOCK_ENDPOINT = "/clock";

  private final HttpClient httpClient;

  public ClockAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<Clock> get() {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, CLOCK_ENDPOINT).execute();

    return new Listenable<>(new ValueTransformer<>(Clock.class), future);
  }
}
