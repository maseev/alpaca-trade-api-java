package io.github.maseev.alpaca.api.streaming.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.maseev.alpaca.http.json.util.DateFormatUtil;
import io.github.maseev.alpaca.api.account.entity.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableAccountUpdate.class)
@JsonDeserialize(as = ImmutableAccountUpdate.class)
public interface AccountUpdate extends Event {

  String id();

  @JsonProperty("created_at")
  @JsonFormat(pattern = DateFormatUtil.DATE_TIME_FORMAT)
  LocalDateTime createdAt();

  @JsonProperty("updated_at")
  @JsonFormat(pattern = DateFormatUtil.DATE_TIME_FORMAT)
  LocalDateTime updatedAt();

  @Nullable
  @JsonProperty("deleted_at")
  @JsonFormat(pattern = DateFormatUtil.DATE_TIME_FORMAT)
  LocalDateTime deletedAt();

  Account.Status status();

  String currency();

  BigDecimal cash();

  @JsonProperty("cash_withdrawable")
  BigDecimal cashWithdrawable();
}
