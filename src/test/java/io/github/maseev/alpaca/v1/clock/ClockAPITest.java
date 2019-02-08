package io.github.maseev.alpaca.v1.clock;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.clock.entity.Clock;
import io.github.maseev.alpaca.v1.clock.entity.ImmutableClock;
import java.time.LocalDateTime;
import org.junit.Test;

public class ClockAPITest extends APITest {

  @Test
  public void gettingClockMustReturnTheExpectedClock() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    LocalDateTime timestamp =
      of(2019, 01, 02, 12, 30, 45);
    Clock expectedClock =
      ImmutableClock.builder()
        .timestamp(timestamp)
        .isOpen(true)
        .nextOpen(timestamp.plusDays(1))
        .nextClose(timestamp.plusDays(2))
        .build();

    mockServer()
      .when(
        request("/clock")
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
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
