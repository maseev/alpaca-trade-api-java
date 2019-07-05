package io.github.maseev.alpaca.v1.account.entity;

import static io.github.maseev.alpaca.v1.util.Available.Version.V1;
import static io.github.maseev.alpaca.v1.util.Available.Version.V2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.http.json.util.DateFormatUtil;
import io.github.maseev.alpaca.v1.AlpacaAPI.Version;
import io.github.maseev.alpaca.v1.util.Available;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableAccount.class)
@JsonDeserialize(as = ImmutableAccount.class)
public interface Account {

  enum Status {
    /**
     * The account is onboarding
     */
    ONBOARDING,

    /**
     * The account application submission failed for some reason
     */
    SUBMISSION_FAILED,

    /**
     * The account application has been submitted for review
     */
    SUBMITTED,

    /**
     * The account information is being updated
     */
    ACCOUNT_UPDATED,

    /**
     * The final account approval is pending
     */
    APPROVAL_PENDING,

    /**
     * The account is active for trading
     */
    ACTIVE,

    /**
     * The account application has been rejected
     */
    REJECTED
  }

  /**
   * @return Account ID
   */
  String id();

  Status status();

  String currency();

  /**
   * @return current available $ buying power; If multiplier = 4, this is your daytrade buying power
   * which is calculated as (last_equity - (last) maintenance_margin) * 4; If multiplier = 2,
   * buying_power = max(equity – initial_margin,0) * 2; If multiplier = 1, buying_power = cash
   */
  @JsonProperty("buying_power")
  BigDecimal buyingPower();

  /**
   * @return Cash balance
   */
  BigDecimal cash();

  /**
   * @return Withdrawable cash amount (this parameter might be empty in {@link Version#V2 V2})
   */
  @Nullable
  @Available(in = V1)
  @JsonProperty("cash_withdrawable")
  BigDecimal cashWithdrawable();

  /**
   * @return Total value of cash + holding positions
   */
  @JsonProperty("portfolio_value")
  BigDecimal portfolioValue();

  /**
   * @return Whether or not the account has been flagged as a pattern day trader
   */
  @JsonProperty("pattern_day_trader")
  boolean patternDayTrader();

  /**
   * @return Whether or not the account is allowed to place orders
   */
  @JsonProperty("trading_blocked")
  boolean tradingBlocked();

  /**
   * @return Whether or not the account is allowed to request money transfers
   */
  @JsonProperty("transfers_blocked")
  boolean transfersBlocked();

  /**
   * @return If true, the account activity by user is prohibited
   */
  @JsonProperty("account_blocked")
  boolean accountBlocked();

  @JsonProperty("trade_suspended_by_user")
  boolean tradeSuspendedByUser();

  /**
   * @return Timestamap this account was created at
   */
  @JsonProperty("created_at")
  @JsonFormat(pattern = DateFormatUtil.DATE_TIME_FORMAT)
  LocalDateTime createdAt();

  /**
   * @return Flag to denote whether or not the account is permitted to short
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("shorting_enabled")
  Boolean shortingEnabled();

  /**
   * @return Buying power multiplier that represents account margin classification; valid values 1
   * (standard limited margin account with 1x buying power), 2 (reg T margin account with 2x
   * intraday and overnight buying power; this is the default for all non-PDT accounts with $2,000
   * or more equity), 4 (PDT account with 4x intraday buying power and 2x reg T overnight buying
   * power)
   */
  @Nullable
  @Available(in = V2)
  Integer multiplier();

  /**
   * @return Real-time MtM value of all long positions held in the account
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("long_market_value")
  BigDecimal longMarketValue();

  /**
   * @return Real-time MtM value of all short positions held in the account
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("short_market_value")
  BigDecimal shortMarketValue();

  /**
   * @return Cash + long_market_value + short_market_value
   */
  @Nullable
  @Available(in = V2)
  BigDecimal equity();

  /**
   * @return Equity as of previous trading day at 16:00:00 ET
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("last_equity")
  BigDecimal lastEquity();

  /**
   * @return Reg T initial margin requirement (continuously updated value)
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("initial_margin")
  BigDecimal initialMargin();

  /**
   * @return Maintenance margin requirement (continuously updated value)
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("maintenance_margin")
  BigDecimal maintenanceMargin();

  /**
   * @return the current number of daytrades that have been made in the last 5 trading days
   * (inclusive of today)
   */
  @Nullable
  @Available(in = V2)
  @JsonProperty("daytrade_count")
  BigDecimal daytradeCount();

  /**
   * @return value of special memorandum account (will be used at a later date to provide additional
   * buying_power)
   */
  @Nullable
  @Available(in = V2)
  BigDecimal sma();
}
