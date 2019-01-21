package io.github.maseev.alpaca.http.transformer;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.maseev.alpaca.http.json.JsonMapper;
import io.github.maseev.alpaca.http.exception.APIException;
import java.io.IOException;

public class ListTransformer<T> extends Transformer<T> {

  private final TypeReference<T> typeReference;

  public ListTransformer(TypeReference<T> typeReference) {
    this.typeReference = typeReference;
  }

  @Override
  public T transform(String responseBody) throws APIException, IOException {
    return JsonMapper.getMapper().readValue(responseBody, typeReference);
  }
}
