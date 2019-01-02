package io.github.maseev.alpaca.v1.order;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.transformer.ListTransformer;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.v1.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class OrderAPI {

  public enum Status {
    OPEN,
    CLOSED,
    ALL
  }

  public enum Direction {
    ASC,
    DESC
  }

  private static final String ORDERS_ENDPOINT = "/orders";
  private static final String GET_ORDERS_ENDPOINT = "/orders/{order_id}";
  private static final String GET_ORDERS_BY_CLIENT_ORDER_ID_ENDPOINT =
    "/orders:by_client_order_id";

  private final HttpClient httpClient;

  public OrderAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<List<Order>> get(Status status, int limit, LocalDateTime after,
                                    LocalDateTime until,
                         Direction direction) {
    final ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ORDERS_ENDPOINT)
        .addQueryParam("status", status.toString())
        .addQueryParam("limit", Integer.toString(limit))
        .addQueryParam("after", after.toString())
        .addQueryParam("until", until.toString())
        .addQueryParam("direction", direction.toString())
        .execute();

    return new Listenable<>(new ListTransformer<>(new TypeReference<List<Order>>() {}), future);
  }

  public Order place(String symbol, long qty, Order.Side side, Order.Type type,
                     Order.TimeInForce timeInForce, BigDecimal limitPrice, BigDecimal stopPrice,
                     String clientOrderId) {
    throw new UnsupportedOperationException();
  }

  public Order get(String orderId) {
    throw new UnsupportedOperationException();
  }

  public Order getByClientOrderId(String clientOrderId) {
    throw new UnsupportedOperationException();
  }

  public void cancel(String orderId) {
    throw new UnsupportedOperationException();
  }
}
