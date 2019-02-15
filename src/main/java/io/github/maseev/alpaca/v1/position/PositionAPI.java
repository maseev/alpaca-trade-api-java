package io.github.maseev.alpaca.v1.position;

import static io.github.maseev.alpaca.http.util.StringUtil.requireNonEmpty;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.v1.position.entity.Position;
import java.util.List;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class PositionAPI {

  static final String ENDPOINT = "/positions";

  private final HttpClient httpClient;

  public PositionAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<List<Position>> get() {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT).execute();

    return new Listenable<>(new GenericTransformer<>(new TypeReference<List<Position>>() {}), future);
  }

  public Listenable<Position> get(String symbol) {
    requireNonEmpty(symbol, "symbol");

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT, symbol).execute();

    return new Listenable<>(new ValueTransformer<>(Position.class), future);
  }
}
