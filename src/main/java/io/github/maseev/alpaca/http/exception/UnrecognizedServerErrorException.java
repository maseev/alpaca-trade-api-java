package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class UnrecognizedServerErrorException extends ResponseException {

  public UnrecognizedServerErrorException(Response response) {
    super("Internal server error", response);
  }
}
