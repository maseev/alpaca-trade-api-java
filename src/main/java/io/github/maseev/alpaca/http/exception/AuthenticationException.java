package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public class AuthenticationException extends ResponseException {

  public AuthenticationException(Response response) {
    super("Authentication error has occured", response);
  }
}
