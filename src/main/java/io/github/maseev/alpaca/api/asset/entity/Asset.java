package io.github.maseev.alpaca.api.asset.entity;

import static io.github.maseev.alpaca.api.util.Available.Version.V1;
import static io.github.maseev.alpaca.api.util.Available.Version.V2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.api.entity.Exchange;
import io.github.maseev.alpaca.api.util.Available;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

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

  @Nullable
  @Available(in = V1)
  @JsonProperty("asset_class")
  AssetClass assetClass();

  @Nullable
  @Available(in = V2)
  @JsonProperty("class")
  AssetClass clazz();

  Exchange exchange();

  String symbol();

  Status status();

  /**
   * @return Asset is tradable on Alpaca or not.
   */
  boolean tradable();

  /**
   * @return Asset is marginable or not.
   */
  @Nullable
  @Available(in = V2)
  Boolean marginable();

  /**
   * @return Asset is shortable or not.
   */
  @Nullable
  @Available(in = V2)
  Boolean shortable();

  /**
   * @return Asset is easy-to-borrow or not (filtering for easy_to_borrow = True is the best way to
   * check whether the name is currently available to short at Alpaca).
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("easy_to_borrow")
  Boolean easyToBorrow();
}
