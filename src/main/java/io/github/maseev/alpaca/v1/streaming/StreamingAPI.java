package io.github.maseev.alpaca.v1.streaming;

import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableAccountUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableConnectionClose;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableConnectionCrash;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableTradeUpdate;
import io.github.maseev.alpaca.v1.streaming.listener.AccountUpdateListener;
import io.github.maseev.alpaca.v1.streaming.listener.ConnectionCloseListener;
import io.github.maseev.alpaca.v1.streaming.listener.ConnectionCrashListener;
import io.github.maseev.alpaca.v1.streaming.listener.TradeUpdateListener;
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

  public StreamingAPI(HttpClient httpClient, String keyId, String secretKey) {
    this.httpClient = httpClient;
    this.keyId = keyId;
    this.secretKey = secretKey;

    subscriptionManager = new SubscriptionManager();
  }

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

  public void subscribe(AccountUpdateListener listener) {
    subscriptionManager.subscribe(listener, ImmutableAccountUpdate.class);
  }

  public void subscribe(TradeUpdateListener listener) {
    subscriptionManager.subscribe(listener, ImmutableTradeUpdate.class);
  }

  public void subscribe(ConnectionCloseListener listener) {
    subscriptionManager.subscribe(listener, ImmutableConnectionClose.class);
  }

  public void subscribe(ConnectionCrashListener listener) {
    subscriptionManager.subscribe(listener, ImmutableConnectionCrash.class);
  }
}
