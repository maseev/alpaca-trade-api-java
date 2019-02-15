package io.github.maseev.alpaca.http.util;

import static java.lang.String.format;

public final class StringUtil {

  private StringUtil() {
  }

  public static void requireNonEmpty(String value, String name) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException(format("'%s' can't be null or empty", name));
    }
  }
}
