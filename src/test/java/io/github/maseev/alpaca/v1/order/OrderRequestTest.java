package io.github.maseev.alpaca.v1.order;

import io.github.maseev.alpaca.v1.order.entity.ImmutableOrderRequest;
import io.github.maseev.alpaca.v1.order.entity.Order;
import java.math.BigDecimal;
import org.junit.Test;

public class OrderRequestTest {

  @Test(expected = IllegalStateException.class)
  public void usingIncorrectTimeInForceParameterMustThrowException() {
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.MARKET)
      .timeInForce(Order.TimeInForce.IOC)
      .build();
  }

  @Test(expected = IllegalStateException.class)
  public void usingLimitOrderAndNotSpecifyingLimitPriceMustThrowException() {
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.LIMIT)
      .timeInForce(Order.TimeInForce.DAY)
      .build();
  }

  @Test(expected = IllegalStateException.class)
  public void usingStopOrderAndNotSpecifyingStopPriceMustThrowException() {
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.STOP)
      .timeInForce(Order.TimeInForce.DAY)
      .build();
  }

  @Test(expected = IllegalStateException.class)
  public void specifyingNegativeLimitPriceMustThrowException() {
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.LIMIT)
      .timeInForce(Order.TimeInForce.DAY)
      .limitPrice(BigDecimal.valueOf(-1))
      .build();
  }

  @Test(expected = IllegalStateException.class)
  public void specifyingNegativeStopPriceMustThrowException() {
    ImmutableOrderRequest.builder()
      .symbol("AAPL")
      .qty(1)
      .side(Order.Side.BUY)
      .type(Order.Type.STOP)
      .timeInForce(Order.TimeInForce.DAY)
      .stopPrice(BigDecimal.valueOf(-1))
      .build();
  }
}
