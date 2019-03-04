package io.github.maseev.alpaca.v1.order;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static io.github.maseev.alpaca.http.util.StringUtil.requireNonEmpty;
import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.json.util.DateFormat;
import io.github.maseev.alpaca.http.transformer.GenericTransformer;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import io.github.maseev.alpaca.v1.order.entity.Order;
import io.github.maseev.alpaca.v1.order.entity.OrderRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class OrderAPI {

  public enum Status {
    OPEN,
    CLOSED,
    ALL;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  public enum Direction {
    ASC,
    DESC;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  static final String ENDPOINT = "/orders";
  static final String GET_BY_CLIENT_ORDER_ID_ENDPOINT =
    ENDPOINT + ":by_client_order_id";

  static final DateTimeFormatter PATTERN =
    DateTimeFormatter.ofPattern(DateFormat.DATE_TIME_NO_NANOSECONDS_FORMAT);

  private final HttpClient httpClient;

  public OrderAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<List<Order>> get(Status status, int limit, LocalDateTime after,
                                     LocalDateTime until,
                                     Direction direction) {
    validate(limit, after, until);

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT)
        .addQueryParam("status", status.toString())
        .addQueryParam("limit", Integer.toString(limit))
        .addQueryParam("after", PATTERN.format(after))
        .addQueryParam("until", PATTERN.format(until))
        .addQueryParam("direction", direction.toString())
        .execute();

    return new Listenable<>(new GenericTransformer<>(new TypeReference<List<Order>>() {}), future);
  }

  public Listenable<Order> place(OrderRequest request) throws JsonProcessingException {
    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.POST, ENDPOINT)
        .setBody(toJson(request))
        .execute();

    return new Listenable<>(new ValueTransformer<>(Order.class), future);
  }

  public Listenable<Order> get(String orderId) {
    requireNonEmpty(orderId, "orderId");

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, ENDPOINT, orderId).execute();

    return new Listenable<>(new ValueTransformer<>(Order.class), future);
  }

  public Listenable<Order> getByClientOrderId(String clientOrderId) {
    requireNonEmpty(clientOrderId, "clientOrderId");

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.GET, GET_BY_CLIENT_ORDER_ID_ENDPOINT)
        .addQueryParam("client_order_id", clientOrderId)
        .execute();

    return new Listenable<>(new ValueTransformer<>(Order.class), future);
  }

  public Listenable<Void> cancel(String orderId) {
    requireNonEmpty(orderId,"orderId");

    ListenableFuture<Response> future =
      httpClient.prepare(HttpClient.HttpMethod.DELETE, ENDPOINT, orderId).execute();

    return new Listenable<>(new ValueTransformer<>(Void.class), future);
  }

  private static void validate(int limit, LocalDateTime after, LocalDateTime until) {
    final int MAX_NUMBER_OF_ORDERS = 500;

    if (limit <= 0 || limit > MAX_NUMBER_OF_ORDERS) {
      throw new IllegalArgumentException(
        format("'limit' parameter must be greater than 0 and less than %s; limit: %s",
          MAX_NUMBER_OF_ORDERS, limit));
    }

    if (after.isAfter(until)) {
      throw new IllegalArgumentException(
        format("'after' parameter must be before 'until'; after: %s, until: %s", after, until));
    }
  }
}
