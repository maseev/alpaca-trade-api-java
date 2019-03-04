package io.github.maseev.alpaca.v1.streaming;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static io.github.maseev.alpaca.v1.streaming.Stream.ACCOUNT_UPDATES;
import static io.github.maseev.alpaca.v1.streaming.Stream.TRADE_UPDATES;
import static io.github.maseev.alpaca.v1.streaming.message.AuthorizationDetails.Status.AUTHORIZED;
import static io.github.maseev.alpaca.v1.streaming.message.AuthorizationDetails.Status.UNAUTHORIZED;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.maseev.alpaca.v1.account.entity.Account;
import io.github.maseev.alpaca.v1.order.entity.ImmutableOrder;
import io.github.maseev.alpaca.v1.order.entity.Order;
import io.github.maseev.alpaca.v1.streaming.entity.AccountUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.ConnectionClose;
import io.github.maseev.alpaca.v1.streaming.entity.ConnectionCrash;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableAccountUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableConnectionClose;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableConnectionCrash;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableTradeUpdate;
import io.github.maseev.alpaca.v1.streaming.entity.TradeUpdate;
import io.github.maseev.alpaca.v1.streaming.exception.AuthorizationException;
import io.github.maseev.alpaca.v1.streaming.exception.SubscriptionException;
import io.github.maseev.alpaca.v1.streaming.listener.AccountUpdateListener;
import io.github.maseev.alpaca.v1.streaming.listener.ConnectionCloseListener;
import io.github.maseev.alpaca.v1.streaming.listener.ConnectionCrashListener;
import io.github.maseev.alpaca.v1.streaming.listener.TradeUpdateListener;
import io.github.maseev.alpaca.v1.streaming.message.AuthenticationMessage;
import io.github.maseev.alpaca.v1.streaming.message.AuthorizationResponse;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableAuthenticationMessage;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableAuthorizationDetails;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableAuthorizationResponse;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableCredentials;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableStreamUpdate;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableSubscriptionMessage;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableSubscriptionResponse;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableSubscription;
import io.github.maseev.alpaca.v1.streaming.message.StreamUpdate;
import io.github.maseev.alpaca.v1.streaming.message.SubscriptionMessage;
import io.github.maseev.alpaca.v1.streaming.message.SubscriptionResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.asynchttpclient.ws.WebSocket;
import org.junit.jupiter.api.Test;

public class StreamUpdateListenerTest {

  @Test
  public void failedAuthorizationMustThrowException() throws JsonProcessingException {
    String keyId = "keyId";
    String secretKey = "secretKey";
    SubscriptionManager subscriptionManager = new SubscriptionManager();
    StreamUpdateListener streamUpdateListener =
      new StreamUpdateListener(keyId, secretKey, subscriptionManager);
    WebSocket websocket = mock(WebSocket.class);

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

    subscriptionManager.subscribe(new ConnectionCrashListener() {
      @Override
      public void onEvent(ConnectionCrash event) {
        exception.set(event.exception());
      }
    }, ImmutableConnectionCrash.class);

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
    String keyId = "keyId";
    String secretKey = "secretKey";
    SubscriptionManager subscriptionManager = new SubscriptionManager();
    StreamUpdateListener streamUpdateListener =
      new StreamUpdateListener(keyId, secretKey, subscriptionManager);
    WebSocket websocket = mock(WebSocket.class);

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

    subscriptionManager.subscribe(new ConnectionCrashListener() {
      @Override
      public void onEvent(ConnectionCrash event) {
        exception.set(event.exception());
      }
    }, ImmutableConnectionCrash.class);

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
    String keyId = "keyId";
    String secretKey = "secretKey";
    SubscriptionManager subscriptionManager = new SubscriptionManager();
    StreamUpdateListener streamUpdateListener =
      new StreamUpdateListener(keyId, secretKey, subscriptionManager);
    WebSocket websocket = mock(WebSocket.class);

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

    subscriptionManager.subscribe(new AccountUpdateListener() {
      @Override
      public void onEvent(AccountUpdate accountUpdate) {
        accountUpdateEvent.set(accountUpdate);
      }
    }, ImmutableAccountUpdate.class);

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

    subscriptionManager.subscribe(new TradeUpdateListener() {
      @Override
      public void onEvent(TradeUpdate tradeUpdate) {
        tradeUpdateEvent.set(tradeUpdate);
      }
    }, ImmutableTradeUpdate.class);

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
      .assetClass("asset")
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
        .qty(100)
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
    String keyId = "keyId";
    String secretKey = "secretKey";
    SubscriptionManager subscriptionManager = new SubscriptionManager();
    StreamUpdateListener streamUpdateListener =
      new StreamUpdateListener(keyId, secretKey, subscriptionManager);
    WebSocket websocket = mock(WebSocket.class);

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
    String keyId = "keyId";
    String secretKey = "secretKey";
    SubscriptionManager subscriptionManager = new SubscriptionManager();
    StreamUpdateListener streamUpdateListener =
      new StreamUpdateListener(keyId, secretKey, subscriptionManager);
    WebSocket websocket = mock(WebSocket.class);

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

    subscriptionManager.subscribe(new ConnectionCloseListener() {
      @Override
      public void onEvent(ConnectionClose event) {
        connectionClose.set(event);
      }
    }, ImmutableConnectionClose.class);

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