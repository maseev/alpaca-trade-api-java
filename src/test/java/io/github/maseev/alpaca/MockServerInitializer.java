package io.github.maseev.alpaca;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MockServerInitializer implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

  private static boolean started;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    if (!started) {
      started = true;
      context.getRoot().getStore(GLOBAL).put("any unique name", this);
      APITest.setUp();
    }
  }

  @Override
  public void close() throws Throwable {
    APITest.tearDown();
  }
}
