package io.github.maseev.alpaca.v1.order;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
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
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.EntityNotFoundException;
import io.github.maseev.alpaca.http.exception.UnprocessableException;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.order.entity.ImmutableOrder;
import io.github.maseev.alpaca.v1.order.entity.ImmutableOrderRequest;
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
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

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
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
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

  @Test(expected = UnprocessableException.class)
  public void cancellingNoLongerCancelableOrderMustThrowException() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String orderId = UUID.randomUUID().toString();

    setUpMockServer(orderId, validKeyId, validSecretKey, HttpCode.UNPROCESSABLE,
      "The order status is not cancelable");

    api.orders().cancel(orderId).await();
  }

  @Test(expected = EntityNotFoundException.class)
  public void cancellingNonexistentOrderMustThrowException() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String orderId = UUID.randomUUID().toString();

    setUpMockServer(orderId, validKeyId, validSecretKey, HttpCode.NOT_FOUND,
      "The order doesn't exist");

    api.orders().cancel(orderId).await();
  }

  @Test
  public void cancellingValidOrderMustCancelIt() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String orderId = UUID.randomUUID().toString();

    setUpMockServer(orderId, validKeyId, validSecretKey, HttpCode.OK,
      "The order has been cancelled");

    api.orders().cancel(orderId).await();
  }

  @Test(expected = EntityNotFoundException.class)
  public void gettingNonExistentOrderMustThrowException() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String orderId = UUID.randomUUID().toString();

    mockServer().when(
      request("/orders/" + orderId)
        .withMethod(HttpClient.HttpMethod.GET.toString())
        .withHeader(APCA_API_KEY_ID, validKeyId)
        .withHeader(APCA_API_SECRET_KEY, validSecretKey)
    ).respond(
      response()
        .withStatusCode(HttpCode.NOT_FOUND.getCode())
        .withReasonPhrase("Order not found")
    );

    api.orders().get(orderId).await();
  }

  @Test
  public void gettingExistentOrderMustReturnExpectedOrder() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String orderId = UUID.randomUUID().toString();

    LocalDateTime orderDate = of(2008, Month.JULY, 9, 12, 30, 00);

    ImmutableOrder expectedOrder = ImmutableOrder.builder()
      .id(orderId)
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

    mockServer().when(
      request("/orders/" + orderId)
        .withMethod(HttpClient.HttpMethod.GET.toString())
        .withHeader(APCA_API_KEY_ID, validKeyId)
        .withHeader(APCA_API_SECRET_KEY, validSecretKey)
    ).respond(
      response()
        .withStatusCode(HttpCode.OK.getCode())
        .withBody(toJson(expectedOrder), MediaType.JSON_UTF_8)
    );

    Order order = api.orders().get(orderId).await();

    assertThat(order, is(equalTo(expectedOrder)));
  }

  @Test(expected = EntityNotFoundException.class)
  public void gettingNonExistentOrderByClientIdMustThrowException() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String clientOrderId = UUID.randomUUID().toString();

    mockServer().when(
      request("/orders:by_client_order_id")
        .withMethod(HttpClient.HttpMethod.GET.toString())
        .withHeader(APCA_API_KEY_ID, validKeyId)
        .withHeader(APCA_API_SECRET_KEY, validSecretKey)
        .withQueryStringParameter("client_order_id", clientOrderId)
    ).respond(
      response()
        .withStatusCode(HttpCode.NOT_FOUND.getCode())
        .withReasonPhrase("Order not found")
    );

    api.orders().getByClientOrderId(clientOrderId).await();
  }

  @Test
  public void gettingExistentOrderByClientIdMustReturnExpectedOrder() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    String clientOrderId = UUID.randomUUID().toString();

    LocalDateTime orderDate = of(2008, Month.JULY, 9, 12, 30, 00);

    ImmutableOrder expectedOrder = ImmutableOrder.builder()
      .id(UUID.randomUUID().toString())
      .clientOrderId(clientOrderId)
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

    mockServer().when(
      request("/orders:by_client_order_id")
        .withMethod(HttpClient.HttpMethod.GET.toString())
        .withHeader(APCA_API_KEY_ID, validKeyId)
        .withHeader(APCA_API_SECRET_KEY, validSecretKey)
        .withQueryStringParameter("client_order_id", clientOrderId)
    ).respond(
      response()
        .withStatusCode(HttpCode.OK.getCode())
        .withBody(toJson(expectedOrder), MediaType.JSON_UTF_8)
    );

    Order order = api.orders().getByClientOrderId(clientOrderId).await();

    assertThat(order, is(equalTo(expectedOrder)));
  }

  @Test
  public void placingNewOrderRequestMustReturnOrderWithExpectedParameters() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    ImmutableOrderRequest orderRequest = ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.STOP_LIMIT)
      .timeInForce(Order.TimeInForce.DAY)
      .limitPrice(BigDecimal.valueOf(10))
      .stopPrice(BigDecimal.valueOf(5))
      .clientOrderId(UUID.randomUUID().toString())
      .build();

    LocalDateTime orderDate = of(2008, Month.JULY, 9, 12, 30, 00);

    ImmutableOrder expectedOrder = ImmutableOrder.builder()
      .id(UUID.randomUUID().toString())
      .clientOrderId(orderRequest.clientOrderId())
      .createdAt(orderDate)
      .updatedAt(orderDate)
      .submittedAt(orderDate)
      .filledAt(orderDate)
      .expiredAt(orderDate)
      .canceledAt(orderDate)
      .failedAt(orderDate)
      .assetId(UUID.randomUUID().toString())
      .symbol(orderRequest.symbol())
      .assetClass("asset")
      .qty(valueOf(orderRequest.qty()))
      .filledQty(valueOf(orderRequest.qty()))
      .type(orderRequest.type())
      .side(orderRequest.side())
      .timeInForce(orderRequest.timeInForce())
      .limitPrice(orderRequest.limitPrice())
      .stopPrice(orderRequest.stopPrice())
      .filledAvgPrice(BigDecimal.valueOf(5))
      .status(Order.Status.FILLED)
      .build();

    mockServer().when(
      request("/orders")
        .withMethod(HttpClient.HttpMethod.POST.toString())
        .withHeader(APCA_API_KEY_ID, validKeyId)
        .withHeader(APCA_API_SECRET_KEY, validSecretKey)
        .withBody(toJson(orderRequest))
    ).respond(
      response()
        .withStatusCode(HttpCode.OK.getCode())
        .withBody(toJson(expectedOrder), MediaType.JSON_UTF_8)
    );

    Order order = api.orders().place(orderRequest).await();

    assertThat(order, is(equalTo(expectedOrder)));
  }

  private void setUpMockServer(String expectedOrderId,
                               String expectedKey,
                               String expectedSecretKey,
                               HttpCode expectedStatusCode,
                               String expectedMessage) {
    mockServer().when(
      request("/orders/" + expectedOrderId)
        .withMethod(HttpClient.HttpMethod.DELETE.toString())
        .withHeader(APCA_API_KEY_ID, expectedKey)
        .withHeader(APCA_API_SECRET_KEY, expectedSecretKey)
    ).respond(
      response()
        .withStatusCode(expectedStatusCode.getCode())
        .withReasonPhrase(expectedMessage)
    );
  }
}
