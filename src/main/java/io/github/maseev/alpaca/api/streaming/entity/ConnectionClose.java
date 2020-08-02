package io.github.maseev.alpaca.api.streaming.entity;

import org.immutables.value.Value;

@Value.Immutable
public interface ConnectionClose extends Event {

  int statusCode();

  String reasonMessage();
}
