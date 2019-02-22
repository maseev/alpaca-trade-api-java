package io.github.maseev.alpaca.v1.order;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.maseev.alpaca.v1.order.entity.ImmutableOrderRequest;
import io.github.maseev.alpaca.v1.order.entity.Order;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class OrderRequestTest {

  @Test
  public void usingNegativeQtyParameterMustThrowException() {
    assertThrows(IllegalStateException.class, () ->
      ImmutableOrderRequest.builder()
        .symbol("AAPL")
        .qty(-1)
        .side(Order.Side.BUY)
        .type(Order.Type.MARKET)
        .timeInForce(Order.TimeInForce.IOC)
        .build());
  }

  @Test
  public void usingIncorrectTimeInForceParameterMustThrowException() {
    assertThrows(IllegalStateException.class, () ->
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.MARKET)
      .timeInForce(Order.TimeInForce.IOC)
      .build());
  }

  @Test
  public void usingLimitOrderAndNotSpecifyingLimitPriceMustThrowException() {
    assertThrows(IllegalStateException.class, () ->
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.LIMIT)
      .timeInForce(Order.TimeInForce.DAY)
      .build());
  }

  @Test
  public void usingStopOrderAndNotSpecifyingStopPriceMustThrowException() {
    assertThrows(IllegalStateException.class, () ->
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.STOP)
      .timeInForce(Order.TimeInForce.DAY)
      .build());
  }

  @Test
  public void specifyingNegativeLimitPriceMustThrowException() {
    assertThrows(IllegalStateException.class, () ->
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.LIMIT)
      .timeInForce(Order.TimeInForce.DAY)
      .limitPrice(BigDecimal.valueOf(-1))
      .build());
  }

  @Test
  public void specifyingNegativeStopPriceMustThrowException() {
    assertThrows(IllegalStateException.class, () ->
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.STOP)
      .timeInForce(Order.TimeInForce.DAY)
      .stopPrice(BigDecimal.valueOf(-1))
      .build());
  }
}
