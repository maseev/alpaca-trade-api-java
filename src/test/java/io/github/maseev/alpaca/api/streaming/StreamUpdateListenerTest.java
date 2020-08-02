package io.github.maseev.alpaca.api.streaming;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static io.github.maseev.alpaca.api.asset.entity.AssetClass.US_EQUITY;
import static io.github.maseev.alpaca.api.streaming.Stream.ACCOUNT_UPDATES;
import static io.github.maseev.alpaca.api.streaming.Stream.TRADE_UPDATES;
import static io.github.maseev.alpaca.api.streaming.message.AuthorizationDetails.Status.AUTHORIZED;
import static io.github.maseev.alpaca.api.streaming.message.AuthorizationDetails.Status.UNAUTHORIZED;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.api.account.entity.Account;
import io.github.maseev.alpaca.api.order.entity.ImmutableOrder;
import io.github.maseev.alpaca.api.order.entity.Order;
import io.github.maseev.alpaca.api.streaming.entity.AccountUpdate;
import io.github.maseev.alpaca.api.streaming.entity.ConnectionClose;
import io.github.maseev.alpaca.api.streaming.entity.ConnectionCrash;
import io.github.maseev.alpaca.api.streaming.entity.Event;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableAccountUpdate;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableConnectionClose;
import io.github.maseev.alpaca.api.streaming.entity.ImmutableTradeUpdate;
import io.github.maseev.alpaca.api.streaming.entity.TradeUpdate;
import io.github.maseev.alpaca.api.streaming.exception.AuthorizationException;
import io.github.maseev.alpaca.api.streaming.exception.SubscriptionException;
import io.github.maseev.alpaca.api.streaming.listener.EventListener;
import io.github.maseev.alpaca.api.streaming.message.AuthenticationMessage;
import io.github.maseev.alpaca.api.streaming.message.AuthorizationResponse;
import io.github.maseev.alpaca.api.streaming.message.ImmutableAuthenticationMessage;
import io.github.maseev.alpaca.api.streaming.message.ImmutableAuthorizationDetails;
import io.github.maseev.alpaca.api.streaming.message.ImmutableAuthorizationResponse;
import io.github.maseev.alpaca.api.streaming.message.ImmutableCredentials;
import io.github.maseev.alpaca.api.streaming.message.ImmutableStreamUpdate;
import io.github.maseev.alpaca.api.streaming.message.ImmutableSubscription;
import io.github.maseev.alpaca.api.streaming.message.ImmutableSubscriptionMessage;
import io.github.maseev.alpaca.api.streaming.message.ImmutableSubscriptionResponse;
import io.github.maseev.alpaca.api.streaming.message.StreamUpdate;
import io.github.maseev.alpaca.api.streaming.message.SubscriptionMessage;
import io.github.maseev.alpaca.api.streaming.message.SubscriptionResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.asynchttpclient.ws.WebSocket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StreamUpdateListenerTest {

  private static final String keyId = "keyId";
  private static final String secretKey = "secretKey";

  private StreamingAPI streamingAPI;
  private SubscriptionManager subscriptionManager;
  private StreamUpdateListener streamUpdateListener;
  private WebSocket websocket;

  @BeforeEach
  public void before() {
    subscriptionManager = new SubscriptionManager();
    streamUpdateListener = new StreamUpdateListener(keyId, secretKey, subscriptionManager);
    websocket = mock(WebSocket.class);
    HttpClient httpClient = mock(HttpClient.class);

    streamingAPI = new StreamingAPI(httpClient, keyId, secretKey, subscriptionManager);
  }

  @Test
  public void subscriptionManagerMustCallAllEventSubscribers() throws InterruptedException {
    class MyEvent implements Event {
    }

    abstract class MyEventListener implements EventListener<MyEvent> {
    }

    CountDownLatch latch = new CountDownLatch(2);

    subscriptionManager.subscribe(new MyEventListener() {
      @Override
      public void onEvent(MyEvent event) {
        latch.countDown();
      }
    }, MyEvent.class);

    subscriptionManager.subscribe(new MyEventListener() {
      @Override
      public void onEvent(MyEvent event) {
        latch.countDown();
      }
    }, MyEvent.class);

    subscriptionManager.invoke(new MyEvent());

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  public void failedAuthorizationMustThrowException() throws JsonProcessingException {
    streamUpdateListener.onOpen(websocket);

    AuthenticationMessage authenticationMessage =
      ImmutableAuthenticationMessage.builder()
        .credentials(ImmutableCredentials.builder()
          .keyId(keyId)
          .secretKey(secretKey)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(authenticationMessage));

    AtomicReference<Throwable> exception = new AtomicReference<>();

    streamingAPI.subscribe((ConnectionCrash event) -> {
      exception.set(event.exception());
    });

    AuthorizationResponse authorizationResponse =
      ImmutableAuthorizationResponse.builder()
        .details(ImmutableAuthorizationDetails.builder()
          .status(UNAUTHORIZED)
          .build())
        .build();

    streamUpdateListener.onBinaryFrame(toJson(authorizationResponse).getBytes(), false, 0);

    assertThat(exception.get().getClass(), is(equalTo(AuthorizationException.class)));
  }

  @Test
  public void unableToSubscribeToAllStreamsMustThrowException() throws JsonProcessingException {
    streamUpdateListener.onOpen(websocket);

    AuthenticationMessage authenticationMessage =
      ImmutableAuthenticationMessage.builder()
        .credentials(ImmutableCredentials.builder()
          .keyId(keyId)
          .secretKey(secretKey)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(authenticationMessage));

    AuthorizationResponse authorizationResponse =
      ImmutableAuthorizationResponse.builder()
        .details(ImmutableAuthorizationDetails.builder()
          .status(AUTHORIZED)
          .build())
        .build();

    streamUpdateListener.onBinaryFrame(toJson(authorizationResponse).getBytes(), false, 0);

    SubscriptionMessage subscribtionMessage =
      ImmutableSubscriptionMessage.builder()
        .subscription(ImmutableSubscription.builder()
          .addStreams(ACCOUNT_UPDATES, TRADE_UPDATES)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(subscribtionMessage));


    AtomicReference<Throwable> exception = new AtomicReference<>();

    streamingAPI.subscribe((ConnectionCrash event) -> {
      exception.set(event.exception());
    });

    SubscriptionResponse subscribtionResponse =
      ImmutableSubscriptionResponse.builder()
        .subscription(ImmutableSubscription.builder()
          .addStreams(ACCOUNT_UPDATES)
          .build())
        .build();

    streamUpdateListener.onBinaryFrame(toJson(subscribtionResponse).getBytes(), false, 0);

    assertThat(exception.get().getClass(), is(equalTo(SubscriptionException.class)));
  }

  @Test
  public void passingMessagePipelineMustSuccessfullyProcessStreamUpdates() throws JsonProcessingException {
    streamUpdateListener.onOpen(websocket);

    AuthenticationMessage authenticationMessage =
      ImmutableAuthenticationMessage.builder()
        .credentials(ImmutableCredentials.builder()
          .keyId(keyId)
          .secretKey(secretKey)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(authenticationMessage));

    AuthorizationResponse authorizationResponse =
      ImmutableAuthorizationResponse.builder()
        .details(ImmutableAuthorizationDetails.builder()
          .status(AUTHORIZED)
          .build())
        .build();

    streamUpdateListener.onBinaryFrame(toJson(authorizationResponse).getBytes(), false, 0);

    SubscriptionMessage subscribtionMessage =
      ImmutableSubscriptionMessage.builder()
        .subscription(ImmutableSubscription.builder()
          .addStreams(ACCOUNT_UPDATES, TRADE_UPDATES)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(subscribtionMessage));

    SubscriptionResponse subscribtionResponse =
      ImmutableSubscriptionResponse.builder()
        .subscription(ImmutableSubscription.builder()
          .addStreams(ACCOUNT_UPDATES, TRADE_UPDATES)
          .build())
        .build();

    streamUpdateListener.onBinaryFrame(toJson(subscribtionResponse).getBytes(), false, 0);

    AtomicReference<AccountUpdate> accountUpdateEvent = new AtomicReference<>();

    streamingAPI.subscribe((AccountUpdate event) -> {
      accountUpdateEvent.set(event);
    });

    LocalDateTime date = of(2008, Month.JULY, 9, 12, 30, 00);
    AccountUpdate accountUpdate =
      ImmutableAccountUpdate.builder()
        .id(UUID.randomUUID().toString())
        .createdAt(date)
        .updatedAt(date)
        .deletedAt(date)
        .status(Account.Status.ACTIVE)
        .currency("USD")
        .cash(valueOf(15.23))
        .cashWithdrawable(valueOf(45.21))
        .build();

    StreamUpdate streamUpdate =
      ImmutableStreamUpdate.builder()
        .stream(ACCOUNT_UPDATES)
        .data(accountUpdate)
        .build();

    streamUpdateListener.onBinaryFrame(toJson(streamUpdate).getBytes(), false, 0);

    AtomicReference<TradeUpdate> tradeUpdateEvent = new AtomicReference<>();

    streamingAPI.subscribe((TradeUpdate event) -> {
      tradeUpdateEvent.set(event);
    });

    Order order = ImmutableOrder.builder()
      .id(UUID.randomUUID().toString())
      .clientOrderId(UUID.randomUUID().toString())
      .createdAt(date)
      .updatedAt(date)
      .submittedAt(date)
      .filledAt(date)
      .expiredAt(date)
      .canceledAt(date)
      .failedAt(date)
      .assetId(UUID.randomUUID().toString())
      .symbol("AAPL")
      .assetClass(US_EQUITY)
      .qty(1)
      .filledQty(2)
      .type(Order.Type.MARKET)
      .orderType(Order.Type.MARKET)
      .side(Order.Side.BUY)
      .timeInForce(Order.TimeInForce.DAY)
      .limitPrice(valueOf(3))
      .stopPrice(valueOf(4))
      .filledAvgPrice(valueOf(5))
      .status(Order.Status.FILLED)
      .build();

    TradeUpdate tradeUpdate =
      ImmutableTradeUpdate.builder()
        .event(TradeUpdate.EventType.NEW)
        .qty(100L)
        .price(valueOf(11.23))
        .timestamp(date)
        .order(order)
        .build();

    StreamUpdate tradeStreamUpdate =
      ImmutableStreamUpdate.builder()
        .stream(TRADE_UPDATES)
        .data(tradeUpdate)
        .build();

    streamUpdateListener.onBinaryFrame(toJson(tradeStreamUpdate).getBytes(), false, 0);

    assertThat(accountUpdateEvent.get(), is(equalTo(accountUpdate)));
    assertThat(tradeUpdateEvent.get(), is(equalTo(tradeUpdate)));
  }

  @Test
  public void receivingPingPongFramesMustRespondRespectively() throws JsonProcessingException {
    streamUpdateListener.onOpen(websocket);

    AuthenticationMessage authenticationMessage =
      ImmutableAuthenticationMessage.builder()
        .credentials(ImmutableCredentials.builder()
          .keyId(keyId)
          .secretKey(secretKey)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(authenticationMessage));

    byte[] payload = {1, 2, 3};

    streamUpdateListener.onPingFrame(payload);

    verify(websocket).sendPongFrame(payload);

    streamUpdateListener.onPongFrame(payload);

    verify(websocket).sendPingFrame(payload);
  }

  @Test
  public void closingWebSocketConnectionMustEmitConnectionCloseEvent() throws JsonProcessingException {
    streamUpdateListener.onOpen(websocket);

    AuthenticationMessage authenticationMessage =
      ImmutableAuthenticationMessage.builder()
        .credentials(ImmutableCredentials.builder()
          .keyId(keyId)
          .secretKey(secretKey)
          .build())
        .build();

    verify(websocket).sendTextFrame(toJson(authenticationMessage));

    AtomicReference<ConnectionClose> connectionClose = new AtomicReference<>();

    streamingAPI.subscribe((ConnectionClose event) -> {
      connectionClose.set(event);
    });

    int code = 999;
    String reason = "999";

    streamUpdateListener.onClose(websocket, code, reason);

    ConnectionClose expectedConnectionClose =
      ImmutableConnectionClose.builder()
        .statusCode(code)
        .reasonMessage(reason)
        .build();

    assertThat(connectionClose.get(), is(equalTo(expectedConnectionClose)));
  }
}