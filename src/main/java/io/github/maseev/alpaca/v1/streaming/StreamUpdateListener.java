package io.github.maseev.alpaca.v1.streaming;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.fromJson;
import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static io.github.maseev.alpaca.v1.streaming.Stream.ACCOUNT_UPDATES;
import static io.github.maseev.alpaca.v1.streaming.Stream.TRADE_UPDATES;
import static io.github.maseev.alpaca.v1.streaming.message.AuthorizationDetails.Status.UNAUTHORIZED;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.maseev.alpaca.v1.streaming.entity.ConnectionClose;
import io.github.maseev.alpaca.v1.streaming.entity.ConnectionCrash;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableConnectionClose;
import io.github.maseev.alpaca.v1.streaming.entity.ImmutableConnectionCrash;
import io.github.maseev.alpaca.v1.streaming.exception.AuthorizationException;
import io.github.maseev.alpaca.v1.streaming.exception.SubscriptionException;
import io.github.maseev.alpaca.v1.streaming.message.AuthenticationMessage;
import io.github.maseev.alpaca.v1.streaming.message.AuthorizationResponse;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableAuthenticationMessage;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableCredentials;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableSubscribtionMessage;
import io.github.maseev.alpaca.v1.streaming.message.ImmutableSubscription;
import io.github.maseev.alpaca.v1.streaming.message.StreamUpdate;
import io.github.maseev.alpaca.v1.streaming.message.SubscribtionMessage;
import io.github.maseev.alpaca.v1.streaming.message.SubscribtionResponse;
import java.io.IOException;
import java.util.Set;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketListener;

class StreamUpdateListener implements WebSocketListener {

  private enum MessagePipeline {
    SUBSCRIBED,
    SUBSCRIBTION_SENT(SUBSCRIBED),
    AUTHENTICATION_SENT(SUBSCRIBTION_SENT),
    CONNECTED(AUTHENTICATION_SENT);

    private final MessagePipeline next;

    MessagePipeline() {
      this(null);
    }

    MessagePipeline(MessagePipeline next) {
      this.next = next;
    }

    public MessagePipeline next() {
      return next;
    }

    public boolean hasNext() {
      return next != null;
    }
  }

  private final String keyId;
  private final String secretKey;
  private final SubscriptionManager subscriptionManager;
  private WebSocket websocket;
  private MessagePipeline pipeline = MessagePipeline.CONNECTED;

  StreamUpdateListener(String keyId, String secretKey,
                       SubscriptionManager subscriptionManager) {
    this.keyId = keyId;
    this.secretKey = secretKey;
    this.subscriptionManager = subscriptionManager;
  }

  @Override
  public void onOpen(WebSocket websocket) {
    this.websocket = websocket;

    AuthenticationMessage authenticationMessage =
      ImmutableAuthenticationMessage.builder()
        .credentials(ImmutableCredentials.builder()
          .keyId(keyId)
          .secretKey(secretKey)
          .build())
        .build();

    sendMessage(authenticationMessage);
  }

  @Override
  public void onTextFrame(String payload, boolean finalFragment, int rsv) {
    updatePipeline();

    try {
      switch (pipeline) {
        case AUTHENTICATION_SENT:
          AuthorizationResponse authorizationResponse =
            fromJson(payload, AuthorizationResponse.class);

          if (authorizationResponse.details().status() == UNAUTHORIZED) {
            throw new AuthorizationException();
          }

          SubscribtionMessage subscribtionMessage =
            ImmutableSubscribtionMessage.builder()
              .subscription(ImmutableSubscription.builder()
                .addStreams(ACCOUNT_UPDATES, TRADE_UPDATES)
                .build())
              .build();

          sendMessage(subscribtionMessage);
          break;
        case SUBSCRIBTION_SENT:
          SubscribtionResponse subscribtionResponse =
            fromJson(payload, SubscribtionResponse.class);
          Set<Stream> streams = subscribtionResponse.subscription().streams();

          if (!streams.containsAll(asList(TRADE_UPDATES, ACCOUNT_UPDATES))) {
            throw new SubscriptionException();
          }
          break;
        case SUBSCRIBED:
          StreamUpdate streamUpdate = fromJson(payload, StreamUpdate.class);

          subscriptionManager.invoke(streamUpdate.data());
          break;
        default:
          throw new IllegalArgumentException(
            format("unrecognized pipeline element; pipeline: %s", pipeline));
      }
    } catch (IOException | AuthorizationException | SubscriptionException ex) {
      onError(ex);
    }
  }

  @Override
  public void onPingFrame(byte[] payload) {
    websocket.sendPongFrame(payload);
  }

  @Override
  public void onPongFrame(byte[] payload) {
    websocket.sendPingFrame(payload);
  }

  @Override
  public void onClose(WebSocket websocket, int code, String reason) {
    ConnectionClose connectionClose =
      ImmutableConnectionClose.builder()
        .statusCode(code)
        .reasonMessage(reason)
        .build();

    subscriptionManager.invoke(connectionClose);
  }

  @Override
  public void onError(Throwable t) {
    ConnectionCrash connectionCrash =
      ImmutableConnectionCrash.builder()
        .exception(t)
        .build();

    subscriptionManager.invoke(connectionCrash);
  }

  private void sendMessage(Object message) {
    try {
      websocket.sendTextFrame(toJson(message));
    } catch (JsonProcessingException e) {
      close();
      onError(e);
    }
  }

  private void updatePipeline() {
    if (pipeline.hasNext()) {
      pipeline = pipeline.next();
    }
  }

  private void close() {
    websocket.sendCloseFrame().awaitUninterruptibly();
  }
}
