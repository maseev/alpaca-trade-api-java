package io.github.maseev.alpaca.api.streaming.listener;

import io.github.maseev.alpaca.api.streaming.entity.TradeUpdate;

@FunctionalInterface
public interface TradeUpdateListener extends EventListener<TradeUpdate> {
}
