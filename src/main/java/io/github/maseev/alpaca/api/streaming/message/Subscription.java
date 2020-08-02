package io.github.maseev.alpaca.api.streaming.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.api.streaming.Stream;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSubscription.class)
@JsonDeserialize(as = ImmutableSubscription.class)
public interface Subscription {

  Set<Stream> streams();
}
