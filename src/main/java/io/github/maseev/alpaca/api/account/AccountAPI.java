package io.github.maseev.alpaca.api.account;

import static io.github.maseev.alpaca.http.HttpClient.HttpMethod.GET;
import static io.github.maseev.alpaca.util.FutureTransformerUtil.transform;

import io.github.maseev.alpaca.api.account.entity.Account;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.transformer.ValueTransformer;
import java.util.concurrent.CompletableFuture;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

/**
 * The accounts API serves important information related to an account, including account status,
 * funds available for trade, funds available for withdrawal, and various flags relevant to an
 * account’s ability to trade. An account may be blocked for just for trades ({@link
 * Account#tradingBlocked()}) or for both trades and transfers ({@link Account#accountBlocked()}) if
 * Alpaca identifies the account to be engaged in any suspicious activity. Also, in accordance with
 * FINRA’s pattern day trading rule, an account may be flagged for pattern day trading ({@link
 * Account#patternDayTrader()}), which would inhibit an account from placing any further
 * day-trades.
 */
public class AccountAPI {

  static final String ENDPOINT = "/account";

  private final HttpClient httpClient;

  public AccountAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * Returns the account associated with the API key.
   *
   * @return the {@link Account} associated with the API key
   */
  public CompletableFuture<Account> get() {
    ListenableFuture<Response> future = httpClient.prepare(GET, ENDPOINT).execute();

    return transform(future, new ValueTransformer<>(Account.class));
  }
}
