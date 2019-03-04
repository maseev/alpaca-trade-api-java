package io.github.maseev.alpaca.v1.order.entity;

import static io.github.maseev.alpaca.http.json.util.DateFormat.DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableOrder.class)
@JsonDeserialize(as = ImmutableOrder.class)
public interface Order {

  enum Type {
    MARKET, LIMIT, STOP, STOP_LIMIT;

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
    DAY, GTC, OPG, IOC, FOK;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
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
  String assetClass();

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
}
