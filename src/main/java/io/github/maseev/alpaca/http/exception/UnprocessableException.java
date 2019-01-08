package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class UnprocessableException extends ResponseException {

  public UnprocessableException(Response response) {
    super("The order status is not cancelable", response);
  }
}
