package io.github.maseev.alpaca.http;

import io.github.maseev.alpaca.http.exception.APIException;
import org.asynchttpclient.Response;

public class Transformer<T> {

  private enum HttpCode {

    TOO_MANY_REQUESTS(429),
    UNAUTHORIZED(401);

    private final int code;

    HttpCode(int code) {
      this.code = code;
    }

    public int getCode() {
      return code;
    }
  }

  private final Class<T> clazz;

  public Transformer(Class<T> clazz) {
    this.clazz = clazz;
  }

  public T transform(Response response) throws APIException {
    int statusCode = response.getStatusCode();
    // TODO handle status code
    if (clazz == void.class) {
      return null;
    }

    // TODO convert response to entity and return it
    return null;
  }
}
