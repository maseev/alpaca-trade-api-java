package io.github.maseev.alpaca.util;

import io.github.maseev.alpaca.http.exception.APIException;
import io.github.maseev.alpaca.http.transformer.Transformer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public final class FutureTransformerUtil {

  private FutureTransformerUtil() {
  }

  public static <T> CompletableFuture<T> transform(ListenableFuture<Response> future,
                                                   Transformer<T> transformer) {
    return future.toCompletableFuture().thenApply(new Function<Response, T>() {
      @Override
      public T apply(Response response) {
        try {
          return transformer.transform(response);
        } catch (APIException e) {
          throw new CompletionException(e);
        }
      }
    });
  }
}
