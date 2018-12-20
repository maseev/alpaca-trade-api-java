package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class RateLimitException extends ResponseException {

  public RateLimitException(Response response) {
    super("Rate limit is exceeded", response);
  }
}
