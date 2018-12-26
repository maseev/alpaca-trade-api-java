package io.github.maseev.alpaca;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mockserver.integration.ClientAndServer;

public abstract class APITest {

  protected static final String APCA_API_KEY_ID = "APCA-API-KEY-ID";
  protected static final String APCA_API_SECRET_KEY = "APCA-API-SECRET-KEY";

  private static final String host = "http://localhost";
  private static final int port = 8081;

  private static ClientAndServer mockServer;

  @BeforeClass
  public static void setUp() throws Exception {
    mockServer = startClientAndServer(port);
  }

  @AfterClass
  public static void tearDown() {
    mockServer.stop();
  }

  @After
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
