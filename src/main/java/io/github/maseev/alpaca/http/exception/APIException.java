package io.github.maseev.alpaca.http.exception;

public abstract class APIException extends Exception {

  protected APIException(String message) {
    super(message);
  }

  protected APIException(Throwable cause) {
    super(cause);
  }
}
