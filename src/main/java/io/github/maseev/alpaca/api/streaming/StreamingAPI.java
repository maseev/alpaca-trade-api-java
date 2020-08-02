package io.github.maseev.alpaca.api.streaming;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.api.streaming.entity.AccountUpdate;
import io.github.maseev.alpaca.api.streaming.entity.ConnectionClose;
import io.github.maseev.alpaca.api.streaming.entity.ConnectionCrash;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableAccountUpdate;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableConnectionClose;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableConnectionCrash;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableTradeUpdate;
import io.github.maseev.alpaca.api.streaming.entity.TradeUpdate;
import io.github.maseev.alpaca.api.streaming.listener.AccountUpdateListener;
import io.github.maseev.alpaca.api.streaming.listener.ConnectionCloseListener;
import io.github.maseev.alpaca.api.streaming.listener.ConnectionCrashListener;
import io.github.maseev.alpaca.api.streaming.listener.TradeUpdateListener;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

public class StreamingAPI implements Closeable {

  private static final String STREAMING_ENDPOINT = "/stream";

  private final HttpClient httpClient;
  private final String keyId;
  private final String secretKey;
  private final SubscriptionManager subscriptionManager;
  private WebSocket connection;

  public StreamingAPI(HttpClient httpClient, String keyId, String secretKey,
                      SubscriptionManager subscriptionManager) {
    this.httpClient = httpClient;
    this.keyId = keyId;
    this.secretKey = secretKey;
    this.subscriptionManager = subscriptionManager;
  }

  /**
   * Establishes a connection to the Alpaca's streaming API and subscribes to the {@link
   * Stream#TRADE_UPDATES} and {@link Stream#ACCOUNT_UPDATES} streams
   */
  public synchronized void connect() throws ExecutionException, InterruptedException, IOException {
    close();

    connection = httpClient.prepare(HttpClient.HttpMethod.GET, STREAMING_ENDPOINT)
      .execute(new WebSocketUpgradeHandler.Builder()
        .addWebSocketListener(new StreamUpdateListener(keyId, secretKey, subscriptionManager))
        .build())
      .get();
  }

  @Override
  public synchronized void close() throws IOException {
    if (connection != null) {
      connection.sendCloseFrame().awaitUninterruptibly();
      connection = null;
    }
  }

  /**
   * Subscribes to {@link AccountUpdate} events
   *
   * @param listener an instance of {@link AccountUpdateListener} which listens to
   *                 {@link AccountUpdate} events
   */
  public void subscribe(AccountUpdateListener listener) {
    subscriptionManager.subscribe(listener, ImmutableAccountUpdate.class);
  }

  /**
   * Subscribes to {@link TradeUpdate} events
   *
   * @param listener an instance of {@link TradeUpdateListener} which listens to
   *                 {@link TradeUpdate} events
   */
  public void subscribe(TradeUpdateListener listener) {
    subscriptionManager.subscribe(listener, ImmutableTradeUpdate.class);
  }

  /**
   * Subscribes to {@link ConnectionClose} events
   *
   * @param listener an instance of {@link ConnectionCloseListener} which listens to
   *                 {@link ConnectionClose} events
   */
  public void subscribe(ConnectionCloseListener listener) {
    subscriptionManager.subscribe(listener, ImmutableConnectionClose.class);
  }

  /**
   * Subscribes to {@link ConnectionCrash} events
   *
   * @param listener an instance of {@link ConnectionCrashListener} which listens to
   *                 {@link ConnectionCrash} events
   */
  public void subscribe(ConnectionCrashListener listener) {
    subscriptionManager.subscribe(listener, ImmutableConnectionCrash.class);
  }
}
