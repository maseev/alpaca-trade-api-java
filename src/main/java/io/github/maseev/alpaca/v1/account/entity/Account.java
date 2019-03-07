package io.github.maseev.alpaca.v1.account.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.http.json.util.DateFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;

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
   * @return Tradable buying power
   */
  @JsonProperty("buying_power")
  BigDecimal buyingPower();

  /**
   * @return Cash balance
   */
  BigDecimal cash();

  /**
   * @return Withdrawable cash amount
   */
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
  @JsonFormat(pattern = DateFormat.DATE_TIME_FORMAT)
  LocalDateTime createdAt();
}
