package io.github.maseev.alpaca.api.account;

import static io.github.maseev.alpaca.api.account.AccountAPI.ENDPOINT;
import static io.github.maseev.alpaca.http.json.util.JsonUtil.toJson;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.of;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.api.account.entity.Account;
import io.github.maseev.alpaca.api.account.entity.ImmutableAccount;
import io.github.maseev.alpaca.http.HttpClient;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.AuthenticationException;
import io.github.maseev.alpaca.http.exception.ParsingException;
import io.github.maseev.alpaca.http.exception.RateLimitException;
import io.github.maseev.alpaca.http.exception.UnrecognizedServerErrorException;
import io.github.maseev.alpaca.http.util.ContentType;
import java.time.Month;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class AccountAPITest extends APITest {

  @Test
  public void recevingUnknownJSONResponseMustThrowException() throws InterruptedException {
    mockServer()
      .when(
        request(ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON))
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody("}{}{}{}", MediaType.JSON_UTF_8)
      );

    expectException(api.account().get(), ParsingException.class);
  }

  @Test
  public void unknownHttpStatusCodeMustBeWrappedIntoKnownExceptionType() throws InterruptedException {
    mockServer()
      .when(
        request(ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON))
      .respond(
        response()
          .withStatusCode(418)
          .withReasonPhrase("I'm a teapot!")
      );

    expectException(api.account().get(), UnrecognizedServerErrorException.class);
  }

  @Test
  public void sendingToManyRequestsMustThrowException() throws InterruptedException {
    mockServer()
      .when(
        request(ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON))
      .respond(
        response()
          .withStatusCode(HttpCode.TOO_MANY_REQUESTS.getCode())
          .withReasonPhrase("Rate limit exceeded")
      );

    expectException(api.account().get(), RateLimitException.class);
  }

  @Test
  public void gettingAccountAsyncWithIncorrectCredentialsMustThrowException() throws Exception {
    mockServer()
      .when(
        request(ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON))
      .respond(
        response()
          .withStatusCode(HttpCode.UNAUTHENTICATED.getCode())
          .withReasonPhrase("Authentication has failed")
      );

    expectException(api.account().get(), AuthenticationException.class);
  }

  @Test
  public void gettingAccountDetailsAsyncMustReturnCorrectAccountObject() throws Exception {
    Account expectedAccount =
      ImmutableAccount.builder()
        .id(UUID.randomUUID().toString())
        .status(Account.Status.ACTIVE)
        .currency("USD")
        .buyingPower(valueOf(1))
        .cash(valueOf(2))
        .cashWithdrawable(valueOf(3))
        .portfolioValue(valueOf(4))
        .patternDayTrader(true)
        .tradingBlocked(false)
        .tradingBlocked(false)
        .transfersBlocked(false)
        .accountBlocked(false)
        .tradeSuspendedByUser(false)
        .createdAt(of(2007, Month.DECEMBER, 1, 10, 00, 10))
        .build();

    mockServer()
      .when(
        request(ENDPOINT)
          .withMethod(HttpClient.HttpMethod.GET.toString())
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey)
          .withHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON))
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedAccount), MediaType.JSON_UTF_8)
      );

    expectEntity(api.account().get(), expectedAccount);
  }
}
