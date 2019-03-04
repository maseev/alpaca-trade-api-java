package io.github.maseev.alpaca.http.exception;

import org.asynchttpclient.Response;

public abstract class ResponseException extends APIException {

  private final Response response;

  protected ResponseException(String message, Response response) {
    super(message + "; code: " + response.getStatusCode() +
      ", message: " + response.getResponseBody());
    this.response = response;
  }

  public Response getResponse() {
    return response;
  }
}
