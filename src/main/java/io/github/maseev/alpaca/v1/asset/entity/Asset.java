package io.github.maseev.alpaca.v1.asset.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.v1.entity.Exchange;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAsset.class)
@JsonDeserialize(as = ImmutableAsset.class)
public interface Asset {

  enum Status {
    ACTIVE,
    INACTIVE;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  String id();

  @JsonProperty("asset_class")
  AssetClass assetClass();

  Exchange exchange();

  String symbol();

  Status status();

  boolean tradable();
}
