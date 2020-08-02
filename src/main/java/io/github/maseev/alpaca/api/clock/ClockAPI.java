package io.github.maseev.alpaca.api.clock;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.api.clock.entity.Clock;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * The clock API serves the current market timestamp, whether or not the market is currently open,
 * as well as the times of the next market open and close.
 */
public class ClockAPI {

  static final String ENDPOINT = "/clock";

  private final HttpClient httpClient;

  public ClockAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Returns the market clock.
   *
   * @return the market {@link Clock}
   */
  public Listenable<Clock> get() {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT).execute();

    return new Listenable<>(new ValueTransformer<>(Clock.class), future);
  }
}
