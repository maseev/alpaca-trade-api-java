package io.github.maseev.alpaca.v1.account;

import static io.github.maseev.alpaca.http.HttpClient.HttpMethod.GET;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.Listenable;
import io.github.maseev.alpaca.http.Transformer;
import io.github.maseev.alpaca.v1.account.entity.Account;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public class AccountAPI {

  private static final String GET_ACCOUNT_ENDPOINT = "/account";

  private final HttpClient httpClient;

  public AccountAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public Listenable<Account> get() {
    ListenableFuture<Response> future = httpClient.prepare(GET, GET_ACCOUNT_ENDPOINT).execute();

    return new Listenable<>(new Transformer<>(Account.class), future);
  }
}
