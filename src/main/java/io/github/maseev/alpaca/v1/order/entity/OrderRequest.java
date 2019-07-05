package io.github.maseev.alpaca.v1.order.entity;

import static io.github.maseev.alpaca.v1.util.Available.Version.V2;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.v1.order.entity.Order.TimeInForce;
import io.github.maseev.alpaca.v1.order.entity.Order.Type;
import io.github.maseev.alpaca.v1.util.Available;
import java.math.BigDecimal;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableOrderRequest.class)
@JsonDeserialize(as = ImmutableOrderRequest.class)
public interface OrderRequest {

  /**
   * @return Symbol or asset ID to identify the asset to trade
   */
  String symbol();

  /**
   * @return Number of shares to trade
   */
  long qty();

  Order.Side side();

  Type type();

  /**
   * @return Allowed values: {@link TimeInForce#DAY DAY}, {@link TimeInForce#GTC GTC}, {@link
   * TimeInForce#OPG OPG}
   */
  @JsonProperty("time_in_force")
  TimeInForce timeInForce();

  /**
   * @return Required if {@link OrderRequest#type() type} is {@link Type#LIMIT limit} or {@link
   * Type#STOP_LIMIT stop-limit}
   */
  @Nullable
  @JsonProperty("limit_price")
  BigDecimal limitPrice();

  /**
   * @return Required if type is {@link Type#STOP stop} or {@link Type#STOP_LIMIT stop-limit}
   */
  @Nullable
  @JsonProperty("stop_price")
  BigDecimal stopPrice();

  /**
   * @return A unique identifier for the order. Automatically generated if not sent
   */
  @Nullable
  @JsonProperty("client_order_id")
  String clientOrderId();

  /**
   * @return (default) false. If true, order will be eligible to execute in premarket/afterhours.
   * Only works with type limit and time_in_force day.
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("extended_hours")
  Boolean extendedHours();

  @Value.Check
  default void check() {
    if (qty() <= 0) {
      throw new IllegalStateException(format("'qty' must be positive; qty: %s", qty()));
    }

    if (timeInForce() == TimeInForce.IOC || timeInForce() == TimeInForce.FOK) {
      throw new IllegalStateException(
        format("'timeInForce' can only be %s; timeInForce: %s",
          asList(TimeInForce.DAY, TimeInForce.GTC, TimeInForce.OPG), timeInForce()));
    }

    if (type() == Type.MARKET && (nonNull(limitPrice()) || nonNull(stopPrice()))) {
      throw new IllegalStateException(
        format("market orders require no stop or limit price; limitPrice: %s, stopPrice: %s",
          limitPrice(), stopPrice()));
    }

    if ((type() == Type.LIMIT || type() == Type.STOP_LIMIT)
      && limitPrice() == null) {
      throw new IllegalStateException(
        format("'limitPrice' can't be null when 'type' is %s or %s; limitPrice: %s",
          Type.LIMIT, Type.STOP, limitPrice()));
    }

    if ((type() == Type.STOP || type() == Type.STOP_LIMIT)
      && stopPrice() == null) {
      throw new IllegalStateException(
        format("'stopPrice' can't be null when 'type' is %s or %s; stopPrice: %s",
          Type.STOP, Type.STOP_LIMIT, stopPrice()));
    }

    if (limitPrice() != null && limitPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException(
        format("'limitPrice' can't be negative; limitPrice: %s", limitPrice()));
    }

    if (stopPrice() != null && stopPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException(
        format("'stopPrice' can't be negative; stopPrice: %s", stopPrice()));
    }

    final int MAX_CLIENT_ORDER_ID_LENGTH = 48;

    if (clientOrderId().length() > MAX_CLIENT_ORDER_ID_LENGTH) {
      throw new IllegalStateException(
        format("'clientOrderId' must be less than or equal to %s; clientOrderId: %s",
          MAX_CLIENT_ORDER_ID_LENGTH, clientOrderId()));
    }

    if (nonNull(extendedHours()) && extendedHours()
      && (type() != Type.LIMIT || timeInForce() != TimeInForce.DAY)) {
      throw new IllegalStateException(
        format("true 'extendedHours' parameter is only available when type is '%s' and timeInForce " +
            "is '%s'; type: %s, timeInForce: %s",
          Type.LIMIT, TimeInForce.DAY, type(), timeInForce()));
    }
  }
}
