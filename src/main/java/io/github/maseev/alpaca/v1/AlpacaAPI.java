package io.github.maseev.alpaca.v1;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.v1.account.AccountAPI;
import io.github.maseev.alpaca.v1.asset.AssetAPI;
import io.github.maseev.alpaca.v1.bar.BarAPI;
import io.github.maseev.alpaca.v1.calendar.CalendarAPI;
import io.github.maseev.alpaca.v1.clock.ClockAPI;
import io.github.maseev.alpaca.v1.order.OrderAPI;
import io.github.maseev.alpaca.v1.position.PositionAPI;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;

public class AlpacaAPI {

  public enum Type {
    TEST,
    LIVE
  }

  private static final String APCA_API_BASE_URL_PAPER_TRADING="https://paper-api.alpaca.markets/v1";
  private static final String APCA_API_BASE_URL_LIVE="https://api.alpaca.markets/v1";
  private static final String APCA_API_DATA_URL = "https://data.alpaca.markets/v1";

  private final AsyncHttpClient client;

  private final AccountAPI accountAPI;
  private final OrderAPI orderAPI;
  private final PositionAPI positionAPI;
  private final AssetAPI assetAPI;
  private final CalendarAPI calendarAPI;
  private final ClockAPI clockAPI;
  private final BarAPI barAPI;

  public AlpacaAPI(String baseTradingUrl, String baseDataUrl,
                   String keyId, String secretKey,
                   AsyncHttpClientConfig config) {
    client = config == null ? asyncHttpClient() : asyncHttpClient(config);

    HttpClient httpClient = new HttpClient(baseTradingUrl, keyId, secretKey, client);

    accountAPI = new AccountAPI(httpClient);
    orderAPI = new OrderAPI(httpClient);
    positionAPI = new PositionAPI(httpClient);
    assetAPI = new AssetAPI(httpClient);
    calendarAPI = new CalendarAPI(httpClient);
    clockAPI = new ClockAPI(httpClient);
    barAPI = new BarAPI(new HttpClient(baseDataUrl, keyId, secretKey, client));
  }

  public AlpacaAPI(String baseTradingUrl, String baseDataUrl, String keyId, String secretKey) {
    this(baseTradingUrl, baseDataUrl, keyId, secretKey, null);
  }

  public AlpacaAPI(Type type, String keyId, String secretKey) {
    this(getBaseUrl(type), APCA_API_DATA_URL, keyId, secretKey);
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

  public BarAPI bars() {
    return barAPI;
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
