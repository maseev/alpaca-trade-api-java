package io.github.maseev.alpaca.api.clock;

import static io.github.maseev.alpaca.util.FutureTransformerUtil.transform;

import io.github.maseev.alpaca.api.clock.entity.Clock;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import java.util.concurrent.CompletableFuture;
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
  public CompletableFuture<Clock> get() {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT).execute();

    return transform(future, new ValueTransformer<>(Clock.class));
  }
}
