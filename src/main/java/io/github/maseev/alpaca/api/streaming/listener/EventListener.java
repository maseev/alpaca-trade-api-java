package io.github.maseev.alpaca.api.streaming.listener;

import io.github.maseev.alpaca.api.streaming.entity.Event;

@FunctionalInterface
public interface EventListener<T extends Event> {

  void onEvent(T event);
}
