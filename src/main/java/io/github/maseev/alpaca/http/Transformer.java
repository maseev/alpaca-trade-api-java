package io.github.maseev.alpaca.http;

import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.ParsingException;
import java.io.IOException;
import org.asynchttpclient.Response;

public class Transformer<T> {

  private final Class<T> clazz;

  public Transformer(Class<T> clazz) {
    this.clazz = clazz;
  }

  public T transform(Response response) throws APIException {
    validate(response);

    if (clazz == void.class) {
      return null;
    }

    try {
      return JsonMapper.getMapper().readValue(response.getResponseBody(), clazz);
    } catch (IOException e) {
      throw new ParsingException(e);
    }
  }

  private static void validate(Response response) throws APIException {
    HttpCode httpCode = HttpCode.valueOf(response.getStatusCode());

    httpCode.doThrow(response);
  }
}
