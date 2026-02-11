package com.autoflex.domain.model.rawmaterial;

/** MeasurementUnit - Enumeration of supported measurement units for raw materials. */
public enum MeasurementUnit {
  KILOGRAM("kg", "Kilogram"),
  GRAM("g", "Gram"),
  LITER("L", "Liter"),
  MILLILITER("mL", "Milliliter"),
  METER("m", "Meter"),
  CENTIMETER("cm", "Centimeter"),
  UNIT("un", "Unit"),
  PIECE("pc", "Piece"),
  PAIR("pr", "Pair"),
  BOX("box", "Box"),
  ROLL("roll", "Roll"),
  SHEET("sheet", "Sheet");

  private final String abbreviation;
  private final String displayName;

  MeasurementUnit(String abbreviation, String displayName) {
    this.abbreviation = abbreviation;
    this.displayName = displayName;
  }

  public String getAbbreviation() {
    return abbreviation;
  }

  public String getDisplayName() {
    return displayName;
  }

  /**
   * Finds a MeasurementUnit by its abbreviation (case-insensitive).
   *
   * @param abbreviation the abbreviation to search for
   * @return the matching MeasurementUnit
   * @throws IllegalArgumentException if no match is found
   */
  public static MeasurementUnit fromAbbreviation(String abbreviation) {
    if (abbreviation == null || abbreviation.isBlank()) {
      throw new IllegalArgumentException("Measurement unit abbreviation cannot be null or empty");
    }
    for (MeasurementUnit unit : values()) {
      if (unit.abbreviation.equalsIgnoreCase(abbreviation.trim())) {
        return unit;
      }
    }
    throw new IllegalArgumentException("Unknown measurement unit: " + abbreviation);
  }
}
