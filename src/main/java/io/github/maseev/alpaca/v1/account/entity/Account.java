package io.github.maseev.alpaca.v1.account.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAccount.class)
@JsonDeserialize(as = ImmutableAccount.class)
public interface Account {

  enum Status {
    ONBOARDING,
    SUBMISSION_FAILED,
    SUBMITTED,
    ACCOUNT_UPDATED,
    APPROVAL_PENDING,
    ACTIVE,
    REJECTED
  }

  String id();

  Status status();

  String currency();

  @JsonProperty("buying_power")
  BigDecimal buyingPower();

  BigDecimal cash();

  @JsonProperty("cash_withdrawable")
  BigDecimal cashWithdrawable();

  @JsonProperty("portfolio_value")
  BigDecimal portfolioValue();

  @JsonProperty("pattern_day_trader")
  boolean patternDayTrader();

  @JsonProperty("trading_blocked")
  boolean tradingBlocked();

  @JsonProperty("transfers_blocked")
  boolean transfersBlocked();

  @JsonProperty("account_blocked")
  boolean accountBlocked();

  @JsonProperty("created_at")
  LocalDateTime createdAt();
}
