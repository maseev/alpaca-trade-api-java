package io.github.maseev.alpaca.v1.clock;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static java.time.OffsetDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.util.ContentType;
import io.github.maseev.alpaca.v1.clock.entity.Clock;
import io.github.maseev.alpaca.v1.clock.entity.ImmutableClock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

public class ClockAPITest extends APITest {

  @Test
  public void gettingClockMustReturnTheExpectedClock() throws Exception {
    OffsetDateTime timestamp = of(LocalDateTime.now(), ZoneOffset.UTC);
    Clock expectedClock =
      ImmutableClock.builder()
        .timestamp(timestamp)
        .isOpen(true)
        .nextOpen(timestamp.plusDays(1))
        .nextClose(timestamp.plusDays(2))
        .build();

    mockServer()
      .when(
        request(ClockAPI.ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedClock), MediaType.JSON_UTF_8)
      );

    Clock clock = api.clock().get().await();

    assertThat(clock, is(equalTo(expectedClock)));
  }
}
