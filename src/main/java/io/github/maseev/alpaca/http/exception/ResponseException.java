package io.github.maseev.alpaca.http.exception;

import static java.lang.String.format;

import org.asynchttpclient.Response;

public abstract class ResponseException extends APIException {

  protected ResponseException(String message, Response response) {
    super(format("%s, status code: %s, response body: %s",
      message, response.getStatusCode(), response.getResponseBody()));
  }
}
