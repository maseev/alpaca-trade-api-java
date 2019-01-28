package io.github.maseev.alpaca.v1.asset;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.asset.entity.Asset;
import io.github.maseev.alpaca.v1.asset.entity.AssetClass;
import io.github.maseev.alpaca.v1.asset.entity.ImmutableAsset;
import io.github.maseev.alpaca.v1.entity.Exchange;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

public class AssetAPITest extends APITest {

  @Test
  public void gettingListOfAssetsMustReturnExpectedAssets() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), validKeyId, validSecretKey);

    AssetClass assetClass = AssetClass.US_EQUITY;
    Asset.Status status = Asset.Status.ACTIVE;
    ImmutableAsset expectedAsset =
      ImmutableAsset.builder()
        .id(UUID.randomUUID().toString())
        .assetClass(assetClass)
        .exchange(Exchange.NYSE)
        .symbol("AAPL")
        .status(status)
        .tradable(true)
        .build();

    List<ImmutableAsset> expectedAssets = Collections.singletonList(expectedAsset);

    mockServer()
      .when(
        request("/assets")
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
          .withQueryStringParameter("status", status.toString())
          .withQueryStringParameter("asset_class", assetClass.toString())
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedAssets), MediaType.JSON_UTF_8));

    List<Asset> assets = api.assets().get(status, assetClass).await();

    assertThat(assets, is(equalTo(expectedAssets)));
  }
}
