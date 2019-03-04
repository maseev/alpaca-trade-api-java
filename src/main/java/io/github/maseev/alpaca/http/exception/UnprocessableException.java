package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class UnprocessableException extends ResponseException {

  public UnprocessableException(Response response) {
    super("Unable to process the request", response);
  }
}
