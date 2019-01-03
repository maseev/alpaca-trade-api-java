package io.github.maseev.alpaca.v1.order;

import static io.github.maseev.alpaca.util.JsonUtil.toJson;
import static java.math.BigInteger.valueOf;
import static java.time.LocalDateTime.of;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.order.entity.ImmutableOrder;
import io.github.maseev.alpaca.v1.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

public class OrderAPITest extends APITest {

  @Test
  public void gettingFilteredListOfOrdersMustReturnExpectedList() throws Exception {
    String nonValidKeyId = "valid key";
    String nonValidSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), nonValidKeyId, nonValidSecretKey);

    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = 10;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = of(2009, Month.DECEMBER, 1, 10, 00, 10);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    LocalDateTime orderDate = of(2008, Month.JULY, 9, 12, 30, 00);

    ImmutableOrder expectedOrder = ImmutableOrder.builder()
      .id(UUID.randomUUID().toString())
      .clientOrderId(UUID.randomUUID().toString())
      .createdAt(orderDate)
      .updatedAt(orderDate)
      .submittedAt(orderDate)
      .filledAt(orderDate)
      .expiredAt(orderDate)
      .canceledAt(orderDate)
      .failedAt(orderDate)
      .assetId(UUID.randomUUID().toString())
      .symbol("AAPL")
      .assetClass("asset")
      .qty(valueOf(1))
      .filledQty(valueOf(2))
      .type(Order.Type.MARKET)
      .side(Order.Side.BUY)
      .timeInForce(Order.TimeInForce.DAY)
      .limitPrice(BigDecimal.valueOf(3))
      .stopPrice(BigDecimal.valueOf(4))
      .filledAvgPrice(BigDecimal.valueOf(5))
      .status(Order.Status.FILLED)
      .build();

    List<ImmutableOrder> expectedOrders = singletonList(expectedOrder);

    mockServer()
      .when(
        request("/orders")
          .withHeader(APCA_API_KEY_ID, nonValidKeyId)
          .withHeader(APCA_API_SECRET_KEY, nonValidSecretKey)
          .withQueryStringParameter("status", status.toString())
          .withQueryStringParameter("limit", Integer.toString(limit))
          .withQueryStringParameter("after", after.toString())
          .withQueryStringParameter("until", until.toString())
          .withQueryStringParameter("direction", direction.toString()))
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedOrders), MediaType.JSON_UTF_8));

    List<Order> orders =
      api.orders()
        .get(status, limit, after, until, direction)
        .await();

    assertThat(orders, is(equalTo(expectedOrders)));
  }
}
