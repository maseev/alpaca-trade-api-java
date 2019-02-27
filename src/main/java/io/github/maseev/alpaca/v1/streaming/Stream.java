package io.github.maseev.alpaca.v1.streaming;

import com.fasterxml.jackson.annotation.JsonValue;
import io.github.maseev.alpaca.v1.streaming.entity.AccountUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.Event;
import io.github.maseev.alpaca.v1.streaming.entity.TradeUpdate;

public enum Stream {
  ACCOUNT_UPDATES(AccountUpdate.class),
  TRADE_UPDATES(TradeUpdate.class);

  private final Class<? extends Event> targetClass;

  Stream(Class<? extends Event> targetClass) {
    this.targetClass = targetClass;
  }

  public Class<? extends Event> getTargetClass() {
    return targetClass;
  }

  @Override
  @JsonValue
  public String toString() {
    return name().toLowerCase();
  }
}
