package io.github.maseev.alpaca.v1.streaming.listener;

import io.github.maseev.alpaca.v1.streaming.entity.Event;

@FunctionalInterface
public interface EventListener<T extends Event> {

  void onEvent(T event);
}
