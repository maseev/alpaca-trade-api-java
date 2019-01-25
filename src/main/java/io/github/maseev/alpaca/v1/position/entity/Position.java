package io.github.maseev.alpaca.v1.position.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.v1.asset.entity.AssetClass;
import io.github.maseev.alpaca.v1.entity.Exchange;
import java.math.BigDecimal;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePosition.class)
@JsonDeserialize(as = ImmutablePosition.class)
public interface Position {

  enum Side {
    SHORT,
    LONG
  }

  @JsonProperty("asset_id")
  String assetId();

  String symbol();

  Exchange exchange();

  @JsonProperty("asset_class")
  AssetClass assetClass();

  @JsonProperty("avg_entry_price")
  BigDecimal avgEntryPrice();

  long qty();

  Side side();

  @JsonProperty("market_value")
  BigDecimal marketValue();

  @JsonProperty("cost_basis")
  BigDecimal costBasis();

  @JsonProperty("unrealized_pl")
  BigDecimal unrealizedPl();

  @JsonProperty("unrealized_plpc")
  BigDecimal unrealizedPlpc();

  @JsonProperty("unrealized_intraday_pl")
  BigDecimal unrealizedIntradayPl();

  @JsonProperty("unrealized_intraday_plpc")
  BigDecimal unrealizedIntradayPlpc();

  @JsonProperty("current_price")
  BigDecimal currentPrice();

  @JsonProperty("lastday_price")
  BigDecimal lastdayPrice();

  @JsonProperty("change_today")
  BigDecimal changeToday();
}
