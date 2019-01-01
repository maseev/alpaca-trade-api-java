package io.github.maseev.alpaca.v1.order.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableOrder.class)
@JsonDeserialize(as = ImmutableOrder.class)
public interface Order {

  enum Type {
    MARKET, LIMIT, STOP, STOP_LIMIT
  }

  enum Side {
    BUY, SELL
  }

  enum TimeInForce {
    DAY, GTC, OPG, IOC, FOK
  }

  enum Status {
    NEW,
    PARTIALLY_FILLED,
    FILLED,
    DONE_FOR_DAY,
    CANCELED,
    EXPIRED,
    ACCEPTED,
    PENDING_NEW,
    ACCEPTED_FOR_BIDDING,
    PENDING_CANCEL,
    STOPPED,
    REJECTED,
    SUSPENDED,
    CALCULATED,
  }

  String id();

  @JsonProperty("client_order_id")
  String clientOrderId();

  @JsonProperty("created_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime createdAt();

  @JsonProperty("updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime updatedAt();

  @JsonProperty("submitted_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime submittedAt();

  @JsonProperty("filled_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime filledAt();

  @JsonProperty("expired_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime expiredAt();

  @JsonProperty("canceled_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime canceledAt();

  @JsonProperty("failed_at")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  LocalDateTime failedAt();

  @JsonProperty("asset_id")
  String assetId();

  String symbol();

  @JsonProperty("asset_class")
  String assetClass();

  BigInteger qty();

  @JsonProperty("filled_qty")
  BigInteger filledQty();

  Type type();

  Side side();

  @JsonProperty("time_in_force")
  TimeInForce timeInForce();

  @JsonProperty("limit_price")
  BigDecimal limitPrice();

  @JsonProperty("stop_price")
  BigDecimal stopPrice();

  @JsonProperty("filled_avg_price")
  BigDecimal filledAvgPrice();

  Status status();
}
