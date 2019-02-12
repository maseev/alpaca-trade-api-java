package io.github.maseev.alpaca.v1.order;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.v1.order.entity.Order;
import io.github.maseev.alpaca.v1.order.entity.OrderRequest;
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

  static final String ORDERS_ENDPOINT = "/orders";
  static final String GET_ORDERS_BY_CLIENT_ORDER_ID_ENDPOINT =
    ORDERS_ENDPOINT + ":by_client_order_id";

  private final HttpClient httpClient;

  public OrderAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<List<Order>> get(Status status, int limit, LocalDateTime after,
                                     LocalDateTime until,
                                     Direction direction) {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ORDERS_ENDPOINT)
        .addQueryParam("status", status.toString())
        .addQueryParam("limit", Integer.toString(limit))
        .addQueryParam("after", after.toString())
        .addQueryParam("until", until.toString())
        .addQueryParam("direction", direction.toString())
        .execute();

    return new Listenable<>(new GenericTransformer<>(new TypeReference<List<Order>>() {
    }), future);
  }

  public Listenable<Order> place(OrderRequest request) throws JsonProcessingException {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.POST, ORDERS_ENDPOINT)
        .setBody(toJson(request))
        .execute();

    return new Listenable<>(new ValueTransformer<>(Order.class), future);
  }

  public Listenable<Order> get(String orderId) {
    ListenableFuture<Response> future = httpClient.prepare(HttpClient.HttpMethod.GET, ORDERS_ENDPOINT, orderId).execute();

    return new Listenable<>(new ValueTransformer<>(Order.class), future);
  }

  public Listenable<Order> getByClientOrderId(String clientOrderId) {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, GET_ORDERS_BY_CLIENT_ORDER_ID_ENDPOINT)
        .addQueryParam("client_order_id", clientOrderId)
        .execute();

    return new Listenable<>(new ValueTransformer<>(Order.class), future);
  }

  public Listenable<Void> cancel(String orderId) {
    ListenableFuture<Response> future = httpClient.prepare(HttpClient.HttpMethod.DELETE, ORDERS_ENDPOINT, orderId).execute();

    return new Listenable<>(new ValueTransformer<>(Void.class), future);
  }
}
