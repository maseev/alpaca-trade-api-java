package io.github.maseev.alpaca.http;

import io.github.maseev.alpaca.http.util.ContentType;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;

public class HttpClient {

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

  public HttpClient(String baseUrl, String keyId, String secretKey, AsyncHttpClient client) {
    this.baseUrl = baseUrl;
    this.keyId = keyId;
    this.secretKey = secretKey;
    this.client = client;
  }

  public BoundRequestBuilder prepare(HttpMethod method, String endpoint) {
    BoundRequestBuilder requestBuilder =
      client.prepare(method.toString(), baseUrl + endpoint)
        .addHeader(APCA_API_KEY_ID, keyId)
        .addHeader(APCA_API_SECRET_KEY, secretKey);

    if (method == HttpMethod.DELETE) {
      return requestBuilder;
    }

    return requestBuilder.addHeader(ContentType.CONTENT_TYPE_HEADER, ContentType.APPLICATION_JSON);
  }

  public BoundRequestBuilder prepare(HttpMethod method, String endpoint, String pathParameter) {
    return prepare(method, endpoint + '/' + pathParameter);
  }
}
