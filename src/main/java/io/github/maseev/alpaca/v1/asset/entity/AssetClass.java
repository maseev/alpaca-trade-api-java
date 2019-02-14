package io.github.maseev.alpaca.v1.asset.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AssetClass {
  US_EQUITY;

  @Override
  @JsonValue
  public String toString() {
    return name().toLowerCase();
  }
}
