package io.github.maseev.alpaca.http.transformer;

import io.github.maseev.alpaca.http.HttpCode;
import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.exception.ParsingException;
import java.io.IOException;
import org.asynchttpclient.Response;

public abstract class Transformer<T> {

  public abstract T transform(String responseBody) throws APIException, IOException;

  public final T transform(Response response) throws APIException {
    validate(response);

    try {
      return transform(response.getResponseBody());
    } catch (IOException e) {
      throw new ParsingException(e);
    }
  }

  private static void validate(Response response) throws APIException {
    HttpCode httpCode = HttpCode.valueOf(response.getStatusCode());

    httpCode.doThrow(response);
  }
}
