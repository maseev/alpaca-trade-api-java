package io.github.maseev.alpaca.v1;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.v1.account.AccountAPI;
import io.github.maseev.alpaca.v1.order.OrderAPI;
import org.asynchttpclient.AsyncHttpClientConfig;

public class AlpacaAPI {

  public enum Type {
    TEST,
    LIVE
  }

  private static final String APCA_API_BASE_URL_PAPER_TRADING="https://paper-api.alpaca.markets/v1";
  private static final String APCA_API_BASE_URL_LIVE="https://api.alpaca.markets/v1";

  private final AccountAPI accountAPI;
  private final OrderAPI orderAPI;

  public AlpacaAPI(String baseUrl, String keyId, String secretKey, AsyncHttpClientConfig config) {
    HttpClient httpClient = new HttpClient(baseUrl, keyId, secretKey, config);

    accountAPI = new AccountAPI(httpClient);
    orderAPI = new OrderAPI(httpClient);
  }

  public AlpacaAPI(String baseUrl, String keyId, String secretKey) {
    this(baseUrl, keyId, secretKey, null);
  }

  public AlpacaAPI(Type type, String keyId, String secretKey) {
    this(getBaseUrl(type), keyId, secretKey);
  }

  public AccountAPI account() {
    return accountAPI;
  }

  public OrderAPI orders() {
    return orderAPI;
  }

  private static String getBaseUrl(Type type) {
    switch (type) {
      case TEST:
        return APCA_API_BASE_URL_PAPER_TRADING;
      case LIVE:
        return APCA_API_BASE_URL_LIVE;
      default:
        throw new IllegalArgumentException(String.format("Unknown type; type: %s", type));
    }
  }
}
