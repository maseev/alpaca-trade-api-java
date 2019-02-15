package io.github.maseev.alpaca.v1.order;

import static java.time.LocalDateTime.of;

import io.github.maseev.alpaca.v1.AlpacaAPI;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.Test;

public class OrderAPIValidationTest {

  @Test(expected = IllegalArgumentException.class)
  public void passingEmptyOrderIdMustThrowException() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "localhost";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    api.orders().get("").await();
  }

  @Test(expected = IllegalArgumentException.class)
  public void gettingListOfOrdersWithIncorrectDatesMustThrowException() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "localhost";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = 10;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = after.minusDays(1);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    api.orders()
      .get(status, limit, after, until, direction)
      .await();
  }

  @Test(expected = IllegalArgumentException.class)
  public void gettingListOfOrdersWithNegativeLimitMustThrowException() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "localhost";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = -1;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = after.plusDays(1);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    api.orders()
      .get(status, limit, after, until, direction)
      .await();
  }

  @Test(expected = IllegalArgumentException.class)
  public void gettingListOfOrdersWithTooBigLimitMustThrowException() throws Exception {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "localhost";
    AlpacaAPI api = new AlpacaAPI(baseUrl, baseUrl, validKeyId, validSecretKey);

    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = 501;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = after.minusDays(1);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    api.orders()
      .get(status, limit, after, until, direction)
      .await();
  }
}
