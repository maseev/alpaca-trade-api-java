package io.github.maseev.alpaca.v1.order.entity;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.v1.order.entity.Order.TimeInForce;
import io.github.maseev.alpaca.v1.order.entity.Order.Type;
import java.math.BigDecimal;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableOrderRequest.class)
@JsonDeserialize(as = ImmutableOrderRequest.class)
public interface OrderRequest {

  String symbol();

  long qty();

  Order.Side side();

  Type type();

  @JsonProperty("time_in_force")
  TimeInForce timeInForce();

  @Nullable
  @JsonProperty("limit_price")
  BigDecimal limitPrice();

  @Nullable
  @JsonProperty("stop_price")
  BigDecimal stopPrice();

  @Nullable
  @JsonProperty("client_order_id")
  String clientOrderId();

  @Value.Check
  default void check() {
    if (timeInForce() == TimeInForce.IOC || timeInForce() == TimeInForce.FOK) {
      throw new IllegalStateException(
        format("'timeInForce' can only be %s",
          asList(TimeInForce.DAY, TimeInForce.GTC, TimeInForce.OPG)));
    }

    if ((type() == Type.LIMIT || type() == Type.STOP_LIMIT)
      && limitPrice() == null) {
      throw new IllegalStateException(
        format("'limitPrice' can't be null when 'type' is %s or %s", Type.LIMIT, Type.STOP));
    }

    if ((type() == Type.STOP || type() == Type.STOP_LIMIT)
      && stopPrice() == null) {
      throw new IllegalStateException(
        format("'stopPrice' can't be null when 'type' is %s or %s",
          Type.STOP, Type.STOP_LIMIT));
    }

    if (limitPrice() != null && limitPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("'limitPrice' can't be negative");
    }

    if (stopPrice() != null && stopPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("'stopPrice' can't be negative");
    }
  }
}
