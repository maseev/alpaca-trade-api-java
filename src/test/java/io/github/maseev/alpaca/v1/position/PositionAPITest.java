package io.github.maseev.alpaca.v1.position;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.EntityNotFoundException;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.position.entity.ImmutablePosition;
import io.github.maseev.alpaca.v1.position.entity.Position;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

public class PositionAPITest extends APITest {

  @Test
  public void gettingOpenPositionsMustReturnExpectedListOfPositions() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), validKeyId, validSecretKey);

    ImmutablePosition expectedPosition =
      ImmutablePosition.builder()
        .assetId(UUID.randomUUID().toString())
        .symbol("AAPL")
        .exchange(Position.Exchange.NYSE)
        .assetClass(Position.AssetClass.US_EQUITY)
        .avgEntryPrice(valueOf(1.11))
        .qty(1)
        .side(Position.Side.LONG)
        .marketValue(valueOf(2.22))
        .costBasis(valueOf(3.33))
        .unrealizedPl(valueOf(4.44))
        .unrealizedPlpc(valueOf(5.55))
        .unrealizedIntradayPl(valueOf(6.66))
        .unrealizedIntradayPlpc(valueOf(7.77))
        .currentPrice(valueOf(8.88))
        .lastdayPrice(valueOf(9.99))
        .changeToday(valueOf(10.1010))
        .build();

    List<ImmutablePosition> expectedPositions = singletonList(expectedPosition);

    mockServer()
      .when(
        request("/positions")
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedPositions), MediaType.JSON_UTF_8));

    List<Position> positions = api.positions().get().await();

    assertThat(positions, is(equalTo(expectedPositions)));
  }

  @Test(expected = EntityNotFoundException.class)
  public void gettingNonExistentPositionMustThrowException() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), validKeyId, validSecretKey);

    String symbol = UUID.randomUUID().toString();

    mockServer()
      .when(
        request("/positions/" + symbol)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
      )
      .respond(
        response()
          .withStatusCode(HttpCode.NOT_FOUND.getCode())
          .withReasonPhrase("Position not found"));

    api.positions().get(symbol).await();
  }

  @Test
  public void gettingExistentPositionMustReturnExpectedPosition() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), validKeyId, validSecretKey);

    ImmutablePosition expectedPosition =
      ImmutablePosition.builder()
        .assetId(UUID.randomUUID().toString())
        .symbol("AAPL")
        .exchange(Position.Exchange.NYSE)
        .assetClass(Position.AssetClass.US_EQUITY)
        .avgEntryPrice(valueOf(1.11))
        .qty(1)
        .side(Position.Side.LONG)
        .marketValue(valueOf(2.22))
        .costBasis(valueOf(3.33))
        .unrealizedPl(valueOf(4.44))
        .unrealizedPlpc(valueOf(5.55))
        .unrealizedIntradayPl(valueOf(6.66))
        .unrealizedIntradayPlpc(valueOf(7.77))
        .currentPrice(valueOf(8.88))
        .lastdayPrice(valueOf(9.99))
        .changeToday(valueOf(10.1010))
        .build();

    mockServer()
      .when(
        request("/positions/" + expectedPosition.symbol())
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedPosition), MediaType.JSON_UTF_8));

    Position position = api.positions().get(expectedPosition.symbol()).await();

    assertThat(position, is(equalTo(expectedPosition)));
  }
}
