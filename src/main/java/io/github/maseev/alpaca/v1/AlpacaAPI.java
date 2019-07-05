package io.github.maseev.alpaca.v1;

import static io.github.maseev.alpaca.v1.AlpacaAPI.Version.V1;
import static org.asynchttpclient.Dsl.asyncHttpClient;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.v1.account.AccountAPI;
import io.github.maseev.alpaca.v1.asset.AssetAPI;
import io.github.maseev.alpaca.v1.bar.BarAPI;
import io.github.maseev.alpaca.v1.calendar.CalendarAPI;
import io.github.maseev.alpaca.v1.clock.ClockAPI;
import io.github.maseev.alpaca.v1.order.OrderAPI;
import io.github.maseev.alpaca.v1.position.PositionAPI;
import io.github.maseev.alpaca.v1.streaming.StreamingAPI;
import io.github.maseev.alpaca.v1.streaming.SubscriptionManager;
import java.io.Closeable;
import java.io.IOException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;

public class AlpacaAPI implements Closeable {

  public enum Version {
    V1("/v1"),
    V2("/v2");

    private final String alias;

    Version(String alias) {
      this.alias = alias;
    }

    @Override
    public String toString() {
      return alias;
    }
  }

  public enum Type {
    TEST,
    LIVE
  }

  private static final String APCA_API_BASE_URL_PAPER_TRADING = "https://paper-api.alpaca.markets";
  private static final String APCA_API_BASE_URL_LIVE = "https://api.alpaca.markets";
  private static final String APCA_API_DATA_URL = "https://data.alpaca.markets";

  private final AsyncHttpClient client;

  private final AccountAPI accountAPI;
  private final OrderAPI orderAPI;
  private final PositionAPI positionAPI;
  private final AssetAPI assetAPI;
  private final CalendarAPI calendarAPI;
  private final ClockAPI clockAPI;
  private final BarAPI barAPI;
  private final StreamingAPI streamingAPI;

  public AlpacaAPI(String baseTradingUrl, String baseDataUrl, String baseStreamingUrl,
                   String keyId, String secretKey, AsyncHttpClientConfig config) {
    client = config == null ? asyncHttpClient() : asyncHttpClient(config);

    HttpClient httpClient = new HttpClient(baseTradingUrl, keyId, secretKey, client);

    accountAPI = new AccountAPI(httpClient);
    orderAPI = new OrderAPI(httpClient);
    positionAPI = new PositionAPI(httpClient);
    assetAPI = new AssetAPI(httpClient);
    calendarAPI = new CalendarAPI(httpClient);
    clockAPI = new ClockAPI(httpClient);
    barAPI = new BarAPI(new HttpClient(baseDataUrl, keyId, secretKey, client));

    streamingAPI =
      new StreamingAPI(
        new HttpClient(getStreamingUrl(baseStreamingUrl), keyId, secretKey, client),
        keyId, secretKey, new SubscriptionManager());
  }

  public AlpacaAPI(String baseTradingUrl, String baseDataUrl, String baseStreamingUrl,
                   String keyId, String secretKey) {
    this(baseTradingUrl, baseDataUrl, baseStreamingUrl, keyId, secretKey, null);
  }

  /**
   * Constructs a main API class which provides access to Alpaca's services
   *
   * @param type      {@link Type#TEST TEST} is for paper trading (a real-time simulation
   *                  environment), {@link Type#LIVE LIVE} is for trading with real money
   * @param version   Alpaca API version.
   * @param keyId     API key ID
   * @param secretKey Secret key. Both {@code secretKey} and {@code keyId} can be obtained via
   *                  Alpaca's website
   */
  public AlpacaAPI(Type type, Version version, String keyId, String secretKey) {
    this(getBaseUrl(type) + version,
      APCA_API_DATA_URL + V1,
      getBaseUrl(type),
      keyId, secretKey);
  }

  @Override
  public void close() throws IOException {
    client.close();
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

  public StreamingAPI streaming() {
    return streamingAPI;
  }

  private static String getBaseUrl(Type type) {
    switch (type) {
      case TEST:
        return APCA_API_BASE_URL_PAPER_TRADING;
      case LIVE:
        return APCA_API_BASE_URL_LIVE;
      default:
        throw new IllegalArgumentException(String.format("unknown type; type: %s", type));
    }
  }

  private static String getStreamingUrl(String baseTradingUrl) {
    boolean isSecureConnection = baseTradingUrl.startsWith("https");

    if (isSecureConnection) {
      return baseTradingUrl.replace("https", "wss");
    } else {
      return baseTradingUrl.replace("http", "ws");
    }
  }
}
