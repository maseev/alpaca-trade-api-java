package io.github.maseev.alpaca.v1.calendar;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.util.ContentType;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.calendar.entity.Calendar;
import io.github.maseev.alpaca.v1.calendar.entity.ImmutableCalendar;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class CalendarAPITest extends APITest {

  @Test(expected = IllegalArgumentException.class)
  public void specifyingEndDateBeforeStartDateMustThrowException() throws APIException {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    LocalDate start = LocalDate.now();
    LocalDate end = start.minusDays(1);

    api.calendar().get(start, end).await();
  }

  @Test
  public void gettingListOfCalendarsWithinCorrectTimelineMustReturnExpectedCalendars() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), getBaseURL(), validKeyId, validSecretKey);

    LocalDate start = LocalDate.now();
    LocalDate end = start.plusDays(10);

    Calendar expectedCalendar =
      ImmutableCalendar.builder()
        .date(start.plusDays(5))
        .open(LocalTime.now())
        .close(LocalTime.now().plusHours(5))
        .build();

    List<Calendar> expectedCalendars = Collections.singletonList(expectedCalendar);

    mockServer()
      .when(
        request(CalendarAPI.ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, validKeyId)
          .withHeader(APCA_API_SECRET_KEY, validSecretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON)
          .withQueryStringParameter("start", start.toString())
          .withQueryStringParameter("end", end.toString())
      )
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedCalendars), MediaType.JSON_UTF_8)
      );

    List<Calendar> calendars = api.calendar().get(start, end).await();

    assertThat(calendars, is(equalTo(expectedCalendars)));
  }
}
