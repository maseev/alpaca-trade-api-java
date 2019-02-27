package io.github.maseev.alpaca.v1.streaming.listener;

import io.github.maseev.alpaca.v1.streaming.entity.TradeUpdate;

@FunctionalInterface
public interface TradeUpdateListener extends EventListener<TradeUpdate> {
}
