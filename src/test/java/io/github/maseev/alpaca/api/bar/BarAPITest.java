package io.github.maseev.alpaca.api.bar;

import static io.github.maseev.alpaca.http.json.util.DateFormatUtil.format;
import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static java.math.BigDecimal.valueOf;
import static java.time.OffsetDateTime.of;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.api.bar.entity.Bar;
import io.github.maseev.alpaca.api.bar.entity.BarMimic;
import io.github.maseev.alpaca.api.bar.entity.ImmutableBar;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.UnprocessableException;
import io.github.maseev.alpaca.http.util.ContentType;
import java.time.Instant;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class BarAPITest extends APITest {

  @Test
  public void gettingBarsWithinTimeInclusiveTimeframeMustReturnExpectedBars() throws Exception {
    Bar expectedBar =
      ImmutableBar.builder()
        .time(Instant.ofEpochSecond(1544129220))
        .openPrice(valueOf(172.26))
        .highPrice(valueOf(172.3))
        .lowPrice(valueOf(172.16))
        .closePrice(valueOf(172.18))
        .volume(3892)
        .build();

    String symbol = "AAPL";
    BarAPI.Timeframe timeframe = BarAPI.Timeframe.DAY;
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(3);
    boolean timeInclusive = true;
    int limit = 10;

    Map<String, List<Bar>> expectedBars =
      singletonMap(symbol, singletonList(expectedBar));

    mockServer()
      .when(
        request(BarAPI.ENDPOINT + '/' + timeframe)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
          .withQueryStringParameter("symbols", symbol)
          .withQueryStringParameter("limit", Integer.toString(limit))
          .withQueryStringParameter("start", format(start).toString())
          .withQueryStringParameter("end", format(end).toString())
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(singletonMap(symbol, singletonList(new BarMimic(expectedBar)))),
            MediaType.JSON_UTF_8)
      );

    expectEntity(api.bars()
      .get(symbol, timeframe, start, end, timeInclusive, 10), expectedBars);
  }

  @Test
  public void gettingBarsWithinTimeExclusiveTimeframeMustReturnExpectedBars() throws Exception {
    Bar expectedBar =
      ImmutableBar.builder()
        .time(Instant.ofEpochSecond(1544129220))
        .openPrice(valueOf(172.26))
        .highPrice(valueOf(172.3))
        .lowPrice(valueOf(172.16))
        .closePrice(valueOf(172.18))
        .volume(3892)
        .build();

    String symbol = "AAPL";
    BarAPI.Timeframe timeframe = BarAPI.Timeframe.DAY;
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 10, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(3);
    boolean timeInclusive = false;
    int limit = 10;

    Map<String, List<Bar>> expectedBars =
      singletonMap(symbol, singletonList(expectedBar));

    mockServer()
      .when(
        request(BarAPI.ENDPOINT + '/' + timeframe)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
          .withQueryStringParameter("symbols", symbol)
          .withQueryStringParameter("limit", Integer.toString(limit))
          .withQueryStringParameter("after", format(start).toString())
          .withQueryStringParameter("until", format(end).toString())
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(singletonMap(symbol, singletonList(new BarMimic(expectedBar)))),
            MediaType.JSON_UTF_8)
      );

    expectEntity(api.bars()
      .get(symbol, timeframe, start, end, timeInclusive, 10), expectedBars);
  }

  @Test
  public void gettingNonExistentSymbolBarsMustThrowException() throws Exception {
    String symbol = "ZZZZ";
    BarAPI.Timeframe timeframe = BarAPI.Timeframe.DAY;
    OffsetDateTime start =
      of(2019, Month.FEBRUARY.getValue(), 10, 12, 30, 00, 0, ZoneOffset.UTC);
    OffsetDateTime end = start.plusWeeks(3);
    boolean timeInclusive = false;
    int limit = 10;

    mockServer()
      .when(
        request(BarAPI.ENDPOINT + '/' + timeframe)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
          .withQueryStringParameter("symbols", symbol)
          .withQueryStringParameter("limit", Integer.toString(limit))
          .withQueryStringParameter("after", format(start).toString())
          .withQueryStringParameter("until", format(end).toString())
      )
      .respond(
        response()
          .withStatusCode(HttpCode.UNPROCESSABLE.getCode())
          .withReasonPhrase("The parameters are not well formed")
      );

    expectException(api.bars()
      .get(symbol, timeframe, start, end, timeInclusive, 10), UnprocessableException.class);
  }
}
