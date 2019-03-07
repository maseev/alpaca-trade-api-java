package io.github.maseev.alpaca.http;

import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.AuthenticationException;
import io.github.maseev.alpaca.http.exception.EntityNotFoundException;
import io.github.maseev.alpaca.http.exception.ForbiddenException;
import io.github.maseev.alpaca.http.exception.UnrecognizedServerErrorException;
import io.github.maseev.alpaca.http.exception.RateLimitException;
import io.github.maseev.alpaca.http.exception.UnprocessableException;
import java.util.function.Function;
import org.asynchttpclient.Response;

public enum HttpCode {

  OK(200),
  NO_CONTENT(204),
  UNAUTHENTICATED(401, AuthenticationException::new),
  TOO_MANY_REQUESTS(429, RateLimitException::new),
  UNPROCESSABLE(422, UnprocessableException::new),
  FORBIDDEN(403, ForbiddenException::new),
  NOT_FOUND(404, EntityNotFoundException::new),
  INTERNAL_SERVER_ERROR(999, UnrecognizedServerErrorException::new);

  private final int code;
  private final Function<Response, APIException> exceptionSupplier;

  HttpCode(int code) {
    this.code = code;
    exceptionSupplier = null;
  }

  HttpCode(int code, Function<Response, APIException> exceptionSupplier) {
    this.code = code;
    this.exceptionSupplier = exceptionSupplier;
  }

  public int getCode() {
    return code;
  }

  public void doThrow(Response response) throws APIException {
    if (exceptionSupplier != null) {
      throw exceptionSupplier.apply(response);
    }
  }

  public static HttpCode valueOf(int statusCode) {
    for (HttpCode httpCode : HttpCode.values()) {
      if (httpCode.code == statusCode) {
        return httpCode;
      }
    }

    return INTERNAL_SERVER_ERROR;
  }
}