package io.github.maseev.alpaca.api.asset;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.api.asset.entity.Asset;
import io.github.maseev.alpaca.api.asset.entity.AssetClass;
import io.github.maseev.alpaca.api.asset.entity.ImmutableAsset;
import io.github.maseev.alpaca.api.entity.Exchange;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.EntityNotFoundException;
import io.github.maseev.alpaca.http.util.ContentType;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class AssetAPITest extends APITest {

  @Test
  public void gettingListOfAssetsMustReturnExpectedAssets() throws Exception {
    AssetClass assetClass = AssetClass.US_EQUITY;
    Asset.Status status = Asset.Status.ACTIVE;
    Asset expectedAsset =
      ImmutableAsset.builder()
        .id(UUID.randomUUID().toString())
        .assetClass(assetClass)
        .exchange(Exchange.NYSE)
        .symbol("AAPL")
        .status(status)
        .tradable(true)
        .marginable(true)
        .shortable(true)
        .easyToBorrow(true)
        .build();

    List<Asset> expectedAssets = Collections.singletonList(expectedAsset);

    mockServer()
      .when(
        request(AssetAPI.ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
          .withQueryStringParameter("status", status.toString())
          .withQueryStringParameter("asset_class", assetClass.toString())
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedAssets), MediaType.JSON_UTF_8)
      );

    expectEntity(api.assets().get(status, assetClass), expectedAssets);
  }

  @Test
  public void gettingNonExistentAssetMustThrowException() throws Exception {
    String symbol = "AAPL";

    mockServer()
      .when(
        request(AssetAPI.ENDPOINT + '/' + symbol)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
      )
      .respond(
        response()
          .withStatusCode(HttpCode.NOT_FOUND.getCode())
          .withReasonPhrase("Asset not found")
      );

    expectException(api.assets().get(symbol), EntityNotFoundException.class);
  }

  @Test
  public void gettingExistentAssetMustReturnExpectedAsset() throws Exception {
    Asset expectedAsset =
      ImmutableAsset.builder()
        .id(UUID.randomUUID().toString())
        .assetClass(AssetClass.US_EQUITY)
        .exchange(Exchange.NYSE)
        .symbol("AAPL")
        .status(Asset.Status.ACTIVE)
        .tradable(true)
        .build();

    mockServer()
      .when(
        request(AssetAPI.ENDPOINT + '/' + expectedAsset.symbol())
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedAsset), MediaType.JSON_UTF_8)
      );

    expectEntity(api.assets().get(expectedAsset.symbol()), expectedAsset);
  }
}
