package io.github.maseev.alpaca.v1.streaming.entity;

import org.immutables.value.Value;

@Value.Immutable
public interface ConnectionClose extends Event {

  int statusCode();

  String reasonMessage();
}
