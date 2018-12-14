package io.github.maseev.alpaca.http;

import static org.asynchttpclient.Dsl.asyncHttpClient;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.BoundRequestBuilder;

public final class HttpClient {

  public enum HttpMethod {
    GET,
    POST,
    DELETE
  }

  private static final String APCA_API_KEY_ID = "APCA-API-KEY-ID";
  private static final String APCA_API_SECRET_KEY = "APCA-API-SECRET-KEY";

  private final String baseUrl;
  private final String keyId;
  private final String secretKey;
  private final AsyncHttpClient client;

  public HttpClient(String baseUrl, String keyId, String secretKey, AsyncHttpClientConfig config) {
    this.baseUrl = baseUrl;
    this.keyId = keyId;
    this.secretKey = secretKey;

    client = config == null ? asyncHttpClient() : asyncHttpClient(config);
  }

  public BoundRequestBuilder prepare(HttpMethod method, String endpoint) {
    return client.prepare(method.toString(), baseUrl + endpoint)
      .addHeader(APCA_API_KEY_ID, keyId)
      .addHeader(APCA_API_SECRET_KEY, secretKey);
  }
}
