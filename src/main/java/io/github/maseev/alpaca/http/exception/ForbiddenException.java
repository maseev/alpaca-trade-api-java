package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class ForbiddenException extends ResponseException {

  public ForbiddenException(Response response) {
    super("Buying power is not sufficient", response);
  }
}
