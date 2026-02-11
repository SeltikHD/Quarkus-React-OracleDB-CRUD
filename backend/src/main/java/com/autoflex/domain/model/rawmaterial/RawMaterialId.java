package com.autoflex.domain.model.rawmaterial;

import java.util.Objects;

/** RawMaterialId - Value Object representing a unique raw material identifier. */
public record RawMaterialId(Long value) {

  public RawMaterialId {
    Objects.requireNonNull(value, "RawMaterialId value cannot be null");
    if (value <= 0) {
      throw new IllegalArgumentException("RawMaterialId must be a positive number");
    }
  }

  public static RawMaterialId of(Long value) {
    return new RawMaterialId(value);
  }

  @Override
  public String toString() {
    return "RawMaterialId(" + value + ")";
  }
}
