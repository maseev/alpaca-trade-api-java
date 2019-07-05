package io.github.maseev.alpaca.v1.order.entity;

import static io.github.maseev.alpaca.http.json.util.DateFormatUtil.DATE_TIME_FORMAT;
import static io.github.maseev.alpaca.v1.util.Available.Version.V2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.v1.asset.entity.AssetClass;
import io.github.maseev.alpaca.v1.util.Available;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableOrder.class)
@JsonDeserialize(as = ImmutableOrder.class)
public interface Order {

  enum Type {
    /**
     * A market order is a request to buy or sell a security at the currently available market
     * price. It provides the most likely method of filling an order. Market orders fill nearly
     * instantaneously.
     */
    MARKET,

    /**
     * A limit order is an order to buy or sell at a specified price or better. A buy limit order (a
     * limit order to buy) is executed at the specified limit price or lower (i.e., better).
     * Conversely, a sell limit order (a limit order to sell) is executed at the specified limit
     * price or higher (better). Unlike a market order, you have to specify the limit price
     * parameter when submitting your order.
     */
    LIMIT,

    /**
     * A stop (market) order is an order to buy or sell a security when its price moves past a
     * particular point, ensuring a higher probability of achieving a predetermined entry or exit
     * price. Once the market price crosses the specified stop price, the stop order becomes a
     * market order. Alpaca converts buy stop orders into stop limit orders with a limit price that
     * is 4% higher than a stop price < $50 (or 2.5% higher than a stop price >= $50). Sell stop
     * orders are not converted into stop limit orders.
     *
     * A stop order does not guarantee the order will be filled at a certain price after it is
     * converted to a market order.
     *
     * In order to submit a stop order, you will need to specify the stop price parameter in the
     * API.
     */
    STOP,

    /**
     * A stop-limit order is a conditional trade over a set time frame that combines the features of
     * a stop order with those of a limit order and is used to mitigate risk. The stop-limit order
     * will be executed at a specified limit price, or better, after a given stop price has been
     * reached. Once the stop price is reached, the stop-limit order becomes a limit order to buy or
     * sell at the limit price or better.
     *
     * In order to submit a stop limit order, you will need to specify both the limit and stop price
     * parameters in the API.
     */
    STOP_LIMIT;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  enum Side {
    BUY, SELL;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  enum TimeInForce {
    /**
     * A day order is eligible for execution only on the day it is live. By default, the order is
     * only valid during Regular Trading Hours (9:30am - 4:00pm ET). If unfilled after the closing
     * auction, it is automatically canceled. If submitted after the close, it is queued and
     * submitted the following trading day. However, if marked as eligible for extended hours, the
     * order can also execute during supported extended hours.
     */
    DAY,

    /**
     * The order is good until canceled. Non-marketable GTC limit orders are subject to price
     * adjustments to offset corporate actions affecting the issue. We do not currently support Do
     * Not Reduce(DNR) orders to opt out of such price adjustments.
     */
    GTC,

    /**
     * The order is eligible to execute only in the market opening auction. The order will be
     * accepted if it is received before 9:15AM ET. The order can be cancelled after 9:15AM, but it
     * cannot be edited. After 9:28AM, OPG orders cannot be edited or cancelled. Any unfilled orders
     * after the open will be cancelled. If you submit an OPG order during market hours, it will
     * appear as “rejected” in your dashboard.
     */
    OPG,

    /**
     * An Immediate Or Cancel (IOC) order requires all or part of the order to be executed
     * immediately. Any unfilled portion of the order is canceled. Only available with API v2.
     */
    @Available(in = V2)
    IOC,

    /**
     * A Fill or Kill (FOK) order is only executed if the entire order quantity can be filled,
     * otherwise the order is canceled. Only available with API v2.
     */
    @Available(in = V2)
    FOK;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  enum Status {
    /**
     * The order has been received by Alpaca, and routed to exchanges for execution. This is the
     * usual initial state of an order.
     */
    NEW,

    /**
     * The order has been partially filled.
     */
    PARTIALLY_FILLED,

    /**
     * The order has been filled, and no further updates will occur for the order.
     */
    FILLED,

    /**
     * The order is done executing for the day, and will not receive further updates until the next
     * trading day.
     */
    DONE_FOR_DAY,

    /**
     * The order has been canceled, and no further updates will occur for the order. This can be
     * either due to a cancel request by the user, or the order has been canceled by the exchanges
     * due to its time-in-force.
     */
    CANCELED,

    /**
     * The order has expired, and no further updates will occur for the order.
     */
    EXPIRED,

    /**
     * The order has been received by Alpaca, but hasn’t yet been routed to the execution venue.
     * This state only occurs on rare occasions.
     */
    ACCEPTED,

    /**
     * The order has been received by Alpaca, and routed to the exchanges, but has not yet been
     * accepted for execution. This state only occurs on rare occasions.
     */
    PENDING_NEW,

    /**
     * The order has been received by exchanges, and is evaluated for pricing. This state only
     * occurs on rare occasions.
     */
    ACCEPTED_FOR_BIDDING,

    /**
     * The order is waiting to be canceled. This state only occurs on rare occasions.
     */
    PENDING_CANCEL,

    /**
     * The order has been stopped, and a trade is guaranteed for the order, usually at a stated
     * price or better, but has not yet occurred. This state only occurs on rare occasions.
     */
    STOPPED,

    /**
     * The order has been rejected, and no further updates will occur for the order. This state
     * occurs on rare occasions and may occur based on various conditions decided by the exchanges.
     */
    REJECTED,

    /**
     * The order has been suspended, and is not eligible for trading. This state only occurs on rare
     * occasions.
     */
    SUSPENDED,

    /**
     * The order has been completed for the day (either filled or done for day), but remaining
     * settlement calculations are still pending. This state only occurs on rare occasions.
     */
    CALCULATED;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  String id();

  @JsonProperty("client_order_id")
  String clientOrderId();

  @JsonProperty("created_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime createdAt();

  @Nullable
  @JsonProperty("updated_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime updatedAt();

  @Nullable
  @JsonProperty("submitted_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime submittedAt();

  @Nullable
  @JsonProperty("filled_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime filledAt();

  @Nullable
  @JsonProperty("expired_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime expiredAt();

  @Nullable
  @JsonProperty("canceled_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime canceledAt();

  @Nullable
  @JsonProperty("failed_at")
  @JsonFormat(pattern = DATE_TIME_FORMAT)
  LocalDateTime failedAt();

  @JsonProperty("asset_id")
  String assetId();

  String symbol();

  @JsonProperty("asset_class")
  AssetClass assetClass();

  long qty();

  @JsonProperty("filled_qty")
  long filledQty();

  Type type();

  @JsonProperty("order_type")
  Type orderType();

  Side side();

  @JsonProperty("time_in_force")
  TimeInForce timeInForce();

  @Nullable
  @JsonProperty("limit_price")
  BigDecimal limitPrice();

  @Nullable
  @JsonProperty("stop_price")
  BigDecimal stopPrice();

  @Nullable
  @JsonProperty("filled_avg_price")
  BigDecimal filledAvgPrice();

  Status status();

  /**
   * @return If true, eligible for execution outside regular trading hours.
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("extended_hours")
  Boolean extendedHours();
}
