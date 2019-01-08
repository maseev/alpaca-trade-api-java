package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class EntityNotFoundException extends ResponseException {

  public EntityNotFoundException(Response response) {
    super("The entity doesn't exist", response);
  }
}
