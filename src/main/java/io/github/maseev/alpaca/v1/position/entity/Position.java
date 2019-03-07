package io.github.maseev.alpaca.v1.position.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
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
    LONG;

    @Override
    @JsonValue
    public String toString() {
      return name().toLowerCase();
    }
  }

  /**
   * @return Asset ID
   */
  @JsonProperty("asset_id")
  String assetId();

  /**
   * @return Symbol name of the asset
   */
  String symbol();

  /**
   * @return Exchange name of the asset
   */
  Exchange exchange();

  /**
   * @return Asset class name
   */
  @JsonProperty("asset_class")
  AssetClass assetClass();

  /**
   * @return Average entry price of the position
   */
  @JsonProperty("avg_entry_price")
  BigDecimal avgEntryPrice();

  /**
   * @return The number of shares
   */
  long qty();

  Side side();

  /**
   * @return Total dollar amount of the position
   */
  @JsonProperty("market_value")
  BigDecimal marketValue();

  /**
   * @return Total cost basis in dollar
   */
  @JsonProperty("cost_basis")
  BigDecimal costBasis();

  /**
   * @return Unrealized profit/loss in dollar
   */
  @JsonProperty("unrealized_pl")
  BigDecimal unrealizedPl();

  /**
   * @return Unrealized profit/loss percent (by a factor of 1)
   */
  @JsonProperty("unrealized_plpc")
  BigDecimal unrealizedPlpc();

  /**
   * @return Unrealized profit/loss in dollar for the day
   */
  @JsonProperty("unrealized_intraday_pl")
  BigDecimal unrealizedIntradayPl();

  /**
   * @return Unrealized profit/loss percent (by a factor of 1)
   */
  @JsonProperty("unrealized_intraday_plpc")
  BigDecimal unrealizedIntradayPlpc();

  /**
   * @return Current asset price per share
   */
  @JsonProperty("current_price")
  BigDecimal currentPrice();

  /**
   * @return Last day’s asset price per share
   */
  @JsonProperty("lastday_price")
  BigDecimal lastdayPrice();

  /**
   * @return Percent change from last day price (by a factor of 1)
   */
  @JsonProperty("change_today")
  BigDecimal changeToday();
}
