package io.github.maseev.alpaca.http;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;

public final class Listenable<T> {

  private final Transformer<T> transformer;
  private final ListenableFuture<Response> future;

  public Listenable(Transformer<T> transformer, ListenableFuture<Response> future) {
    this.transformer = transformer;
    this.future = future;
  }

  public void onComplete(ResponseHandler<T> responseHandler) {
    future.addListener(() -> {
      try {
        Response response = future.get();
        T entity = transformer.transform(response);

        responseHandler.onSuccess(entity);
      } catch (Exception ex) {
        responseHandler.onError(ex);
      }
    }, null);
  }
}
