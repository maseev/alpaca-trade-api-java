package io.github.maseev.alpaca.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public final class Expecter<T> {

  private final CountDownLatch latch = new CountDownLatch(1);
  private final AtomicReference<Throwable> exception = new AtomicReference<>();
  private final AtomicReference<T> result = new AtomicReference<>();

  public Expecter(CompletableFuture<T> completableFuture) {
    completableFuture.whenComplete(new BiConsumer<T, Throwable>() {
      @Override
      public void accept(T entity, Throwable ex) {
        result.set(entity);
        exception.set(ex);
        latch.countDown();
      }
    });
  }

  public void await() throws InterruptedException {
    latch.await(5, TimeUnit.SECONDS);
  }

  public T getResult() {
    return result.get();
  }

  public Throwable getRootException() {
    return exception.get().getCause();
  }
}
