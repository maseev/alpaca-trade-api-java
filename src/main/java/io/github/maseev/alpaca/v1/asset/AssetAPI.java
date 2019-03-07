package io.github.maseev.alpaca.v1.asset;

import static io.github.maseev.alpaca.http.util.StringUtil.requireNonEmpty;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.exception.EntityNotFoundException;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.v1.asset.entity.Asset;
import io.github.maseev.alpaca.v1.asset.entity.AssetClass;
import java.util.List;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * The assets API serves as the master list of assets available for trade and data consumption from
 * Alpaca. Assets are sorted by asset class, exchange and symbol. Some assets are only available for
 * data consumption via Polygon, and are not tradable with Alpaca. These assets will be marked with
 * the flag {@code tradable = false}.
 */
public class AssetAPI {

  static final String ENDPOINT = "/assets";

  private final HttpClient httpClient;

  public AssetAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Returns a list of assets
   *
   * @param status  an asset's status
   * @param assetClass an asset's class
   * @return a list of {@link Asset}
   */
  public Listenable<List<Asset>> get(Asset.Status status, AssetClass assetClass) {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT)
        .addQueryParam("status", status.toString())
        .addQueryParam("asset_class", assetClass.toString())
        .execute();

    return new Listenable<>(new GenericTransformer<>(new TypeReference<List<Asset>>() {}), future);
  }

  /**
   * Returns an asset for the given symbol
   *
   * @param symbol an asset's symbol
   * @return an {@link Asset} instance
   * @throws EntityNotFoundException if an asset is not found
   */
  public Listenable<Asset> get(String symbol) {
    requireNonEmpty(symbol, "symbol");

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT, symbol).execute();

    return new Listenable<>(new ValueTransformer<>(Asset.class), future);
  }
}
