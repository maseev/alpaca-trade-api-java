package io.github.maseev.alpaca.v1.streaming.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.http.json.util.DateFormatUtil;
import io.github.maseev.alpaca.v1.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableTradeUpdate.class)
@JsonDeserialize(as = ImmutableTradeUpdate.class)
public interface TradeUpdate extends Event {

  enum EventType {
    NEW,
    PARTIAL_FILL,
    FILL,
    DONE_FOR_DAY,
    CANCELED,
    EXPIRED,
    PENDING_CANCEL,
    STOPPED,
    REJECTED,
    SUSPENDED,
    PENDING_NEW,
    CALCULATED;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  EventType event();

  @Nullable
  Long qty();

  @Nullable
  BigDecimal price();

  @Nullable
  @JsonFormat(pattern = DateFormatUtil.DATE_TIME_FORMAT)
  LocalDateTime timestamp();

  Order order();
}
