package io.github.maseev.alpaca.v1.streaming;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.fromJson;
import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import io.github.maseev.alpaca.v1.account.entity.Account;
import io.github.maseev.alpaca.v1.account.entity.ImmutableAccount;
import io.github.maseev.alpaca.v1.order.entity.ImmutableOrder;
import io.github.maseev.alpaca.v1.order.entity.Order;
import io.github.maseev.alpaca.v1.streaming.entity.AccountUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableAccountUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableTradeUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.TradeUpdate;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableStreamUpdate;
import io.github.maseev.alpaca.v1.streaming.message.StreamUpdate;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class StreamUpdateTest {

  @Test
  public void serializingTradeUpdateMustReturnExpectedStreamUpdate() throws IOException {
    LocalDateTime orderDate = of(2008, Month.JULY, 9, 12, 30, 00);

    Order order = ImmutableOrder.builder()
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
      .qty(1)
      .filledQty(2)
      .type(Order.Type.MARKET)
      .orderType(Order.Type.MARKET)
      .side(Order.Side.BUY)
      .timeInForce(Order.TimeInForce.DAY)
      .limitPrice(valueOf(3))
      .stopPrice(valueOf(4))
      .filledAvgPrice(valueOf(5))
      .status(Order.Status.FILLED)
      .build();

    TradeUpdate tradeUpdate = ImmutableTradeUpdate.builder()
      .event(TradeUpdate.EventType.FILL)
      .qty(1L)
      .timestamp(orderDate)
      .price(valueOf(1))
      .order(order)
      .build();

    StreamUpdate expectedStreamUpdate =
      ImmutableStreamUpdate.builder()
        .stream(Stream.TRADE_UPDATES)
        .data(tradeUpdate)
        .build();

    String json = toJson(expectedStreamUpdate);

    StreamUpdate streamUpdate = fromJson(json, StreamUpdate.class);

    assertThat(streamUpdate, is(equalTo(expectedStreamUpdate)));
  }

  @Test
  public void serializingAccountUpdateMustReturnExpectedStreamUpdate() throws IOException {
    Account account =
      ImmutableAccount.builder()
        .id(UUID.randomUUID().toString())
        .status(Account.Status.ACTIVE)
        .currency("USD")
        .buyingPower(valueOf(1))
        .cash(valueOf(2))
        .cashWithdrawable(valueOf(3))
        .portfolioValue(valueOf(4))
        .patternDayTrader(true)
        .tradingBlocked(false)
        .tradingBlocked(false)
        .transfersBlocked(false)
        .accountBlocked(false)
        .tradeSuspendedByUser(false)
        .createdAt(of(2008, Month.JULY, 9, 12, 30, 00))
        .build();

    AccountUpdate accountUpdate =
      ImmutableAccountUpdate.builder()
        .id(UUID.randomUUID().toString())
        .createdAt(account.createdAt())
        .updatedAt(account.createdAt())
        .deletedAt(account.createdAt())
        .status(Account.Status.ACTIVE)
        .currency("USD")
        .cash(valueOf(1))
        .cashWithdrawable(valueOf(2))
        .build();

    StreamUpdate expectedStreamUpdate =
      ImmutableStreamUpdate.builder()
        .stream(Stream.ACCOUNT_UPDATES)
        .data(accountUpdate)
        .build();

    String json = toJson(expectedStreamUpdate);

    StreamUpdate streamUpdate = fromJson(json, StreamUpdate.class);

    assertThat(streamUpdate, is(equalTo(expectedStreamUpdate)));
  }
}
