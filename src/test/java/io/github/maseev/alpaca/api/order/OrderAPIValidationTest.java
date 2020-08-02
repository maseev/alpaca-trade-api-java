package io.github.maseev.alpaca.api.order;

import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.maseev.alpaca.api.AlpacaAPI;
import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderAPIValidationTest {

  private AlpacaAPI api;

  @BeforeEach
  public void before() {
    String validKeyId = "valid key";
    String validSecretKey = "valid secret";
    String baseUrl = "localhost";

    api = new AlpacaAPI(baseUrl, baseUrl, baseUrl, validKeyId, validSecretKey);
  }

  @Test
  public void passingEmptyOrderIdMustThrowException() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> api.orders().get("").await());
  }

  @Test
  public void gettingListOfOrdersWithIncorrectDatesMustThrowException() throws Exception {
    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = 10;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = after.minusDays(1);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    assertThrows(IllegalArgumentException.class,
      () -> api.orders().get(status, limit, after, until, direction).await());
  }

  @Test
  public void gettingListOfOrdersWithNegativeLimitMustThrowException() throws Exception {
    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = -1;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = after.plusDays(1);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    assertThrows(IllegalArgumentException.class,
      () -> api.orders().get(status, limit, after, until, direction).await());
  }

  @Test
  public void gettingListOfOrdersWithTooBigLimitMustThrowException() throws Exception {
    OrderAPI.Status status = OrderAPI.Status.OPEN;
    int limit = 501;
    LocalDateTime after = of(2007, Month.DECEMBER, 1, 10, 00, 10);
    LocalDateTime until = after.minusDays(1);
    OrderAPI.Direction direction = OrderAPI.Direction.ASC;

    assertThrows(IllegalArgumentException.class,
      () -> api.orders().get(status, limit, after, until, direction).await());
  }
}
