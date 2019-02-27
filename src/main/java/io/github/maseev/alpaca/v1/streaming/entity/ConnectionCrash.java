package io.github.maseev.alpaca.v1.streaming.entity;

import org.immutables.value.Value;

@Value.Immutable
public interface ConnectionCrash extends Event {

  Throwable exception();
}
