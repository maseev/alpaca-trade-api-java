package io.github.maseev.alpaca;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import io.github.maseev.alpaca.api.AlpacaAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;

@ExtendWith(MockServerInitializer.class)
public abstract class APITest {

  protected static final String APCA_API_KEY_ID = "APCA-API-KEY-ID";
  protected static final String APCA_API_SECRET_KEY = "APCA-API-SECRET-KEY";
  protected static final String keyId = "key-id";
  protected static final String secretKey = "secret-key";

  private static final String host = "http://localhost";
  private static final int port = 8081;

  private static ClientAndServer mockServer;

  protected AlpacaAPI api;

  public static void setUp() throws Exception {
    mockServer = startClientAndServer(port);
  }

  public static void tearDown() {
    mockServer.stop();
  }

  @BeforeEach
  public void before() {
    api = new AlpacaAPI(getBaseURL(), getBaseURL(), getBaseURL(), keyId, secretKey);
  }

  @AfterEach
  public void after() {
    mockServer.reset();
  }

  public ClientAndServer mockServer() {
    return mockServer;
  }

  public String getBaseURL() {
    return host + ':' + port;
  }
}
