package io.github.maseev.alpaca.v1.account;

import static io.github.maseev.alpaca.util.JsonUtil.toJson;
import static java.math.BigDecimal.valueOf;
import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.net.MediaType;
import io.github.maseev.alpaca.APITest;
import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.AuthenticationException;
import io.github.maseev.alpaca.v1.AlpacaAPI;
import io.github.maseev.alpaca.v1.account.entity.Account;
import io.github.maseev.alpaca.v1.account.entity.ImmutableAccount;
import java.time.Month;
import java.util.UUID;
import org.junit.Test;

public class AccountAPITest extends APITest {

  @Test(expected = AuthenticationException.class)
  public void accessingEndpointWithIncorrectCredentialsMustThrowException() throws APIException {
    String nonValidKeyId = "non-valid";
    String nonValidSecretKey = "non-valid";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), nonValidKeyId, nonValidSecretKey);

    mockServer()
      .when(
        request()
          .withPath("/account")
          .withHeader(APCA_API_KEY_ID, nonValidKeyId)
          .withHeader(APCA_API_SECRET_KEY, nonValidSecretKey))
      .respond(
        response()
          .withStatusCode(HttpCode.UNAUTHENTICATED.getCode())
          .withReasonPhrase("Authentication has failed")
      );

    api.account().get().await();
  }

  @Test
  public void gettingAccountDetailsMustReturnCorrectAccountObject() throws Exception {
    String keyId = "valid key";
    String secretKey = "valid key";
    AlpacaAPI api = new AlpacaAPI(getBaseURL(), keyId, secretKey);

    ImmutableAccount expectedAccount =
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
        .createdAt(of(2007, Month.DECEMBER, 1, 10, 00, 10))
        .build();

    mockServer()
      .when(
        request()
          .withPath("/account")
          .withHeader(APCA_API_KEY_ID, keyId)
          .withHeader(APCA_API_SECRET_KEY, secretKey))
      .respond(
        response()
          .withStatusCode(HttpCode.OK.getCode())
          .withBody(toJson(expectedAccount), MediaType.JSON_UTF_8)
      );

    Account account = api.account().get().await();

    assertThat(account, is(equalTo(expectedAccount)));
  }
}
