package io.github.maseev.alpaca.http.transformer;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.fromJson;

import io.github.maseev.alpaca.http.exception.APIException;
import java.io.IOException;

public class ValueTransformer<T> extends Transformer<T> {

  private final Class<T> clazz;

  public ValueTransformer(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public T transform(String responseBody) throws APIException, IOException {
    if (clazz == void.class || clazz == Void.class) {
      return null;
    }

    return fromJson(responseBody, clazz);
  }
}
