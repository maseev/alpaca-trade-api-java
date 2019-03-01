package io.github.maseev.alpaca.v1.streaming.exception;

import static java.util.Arrays.asList;

import io.github.maseev.alpaca.v1.streaming.Stream;
import java.util.Set;

public class SubscriptionException extends Exception {

  public SubscriptionException(Set<Stream> streams) {
    super(String.format("unable to subscribe to %s streams; subscribed streams: %s",
      asList(Stream.TRADE_UPDATES, Stream.ACCOUNT_UPDATES), streams));
  }
}
