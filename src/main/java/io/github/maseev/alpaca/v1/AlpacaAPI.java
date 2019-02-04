package io.github.maseev.alpaca.v1;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.v1.account.AccountAPI;
import io.github.maseev.alpaca.v1.asset.AssetAPI;
import io.github.maseev.alpaca.v1.calendar.CalendarAPI;
import io.github.maseev.alpaca.v1.clock.ClockAPI;
import io.github.maseev.alpaca.v1.order.OrderAPI;
import io.github.maseev.alpaca.v1.position.PositionAPI;
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
  private final PositionAPI positionAPI;
  private final AssetAPI assetAPI;
  private final CalendarAPI calendarAPI;
  private final ClockAPI clockAPI;

  public AlpacaAPI(String baseUrl, String keyId, String secretKey, AsyncHttpClientConfig config) {
    HttpClient httpClient = new HttpClient(baseUrl, keyId, secretKey, config);

    accountAPI = new AccountAPI(httpClient);
    orderAPI = new OrderAPI(httpClient);
    positionAPI = new PositionAPI(httpClient);
    assetAPI = new AssetAPI(httpClient);
    calendarAPI = new CalendarAPI(httpClient);
    clockAPI = new ClockAPI(httpClient);
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

  public PositionAPI positions() {
    return positionAPI;
  }

  public AssetAPI assets() {
    return assetAPI;
  }

  public CalendarAPI calendar() {
    return calendarAPI;
  }

  public ClockAPI clock() {
    return clockAPI;
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
