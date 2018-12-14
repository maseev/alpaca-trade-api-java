package io.github.maseev.alpaca.http;

public interface ResponseHandler<T> {

  void onSuccess(T result);

  void onError(Exception ex);
}
