package io.github.maseev.alpaca.http.transformer;

import static io.github.maseev.alpaca.http.json.util.JsonUtil.fromJson;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.exception.APIException;
import java.io.IOException;

public class GenericTransformer<T> extends Transformer<T> {

  private final TypeReference<T> typeReference;

  public GenericTransformer(TypeReference<T> typeReference) {
    this.typeReference = typeReference;
  }

  @Override
  public T transform(String responseBody) throws APIException, IOException {
    return fromJson(responseBody, typeReference);
  }
}
