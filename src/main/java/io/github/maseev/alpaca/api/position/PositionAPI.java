package io.github.maseev.alpaca.api.position;

import static io.github.maseev.alpaca.http.util.StringUtil.requireNonEmpty;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.exception.EntityNotFoundException;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.api.position.entity.Position;
import java.util.List;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * The positions API provides information about an account’s current open positions. The response
 * will include information such as cost basis, shares traded, and market value, which will be
 * updated live as price information is updated. Once a position is closed, it will no longer be
 * queryable through this API.
 */
public class PositionAPI {

  static final String ENDPOINT = "/positions";

  private final HttpClient httpClient;

  public PositionAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Retrieves a list of the account’s open positions.
   *
   * @return a list of {@link Position}
   */
  public Listenable<List<Position>> get() {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT).execute();

    return new Listenable<>(new GenericTransformer<>(new TypeReference<List<Position>>() {}), future);
  }

  /**
   * Retrieves the account’s open position for the given symbol.
   *
   * @param symbol asset symbol
   * @return a list of {@link Position} for the given symbol
   * @throws EntityNotFoundException if a {@link Position} is not found
   */
  public Listenable<Position> get(String symbol) {
    requireNonEmpty(symbol, "symbol");

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT, symbol).execute();

    return new Listenable<>(new ValueTransformer<>(Position.class), future);
  }
}
