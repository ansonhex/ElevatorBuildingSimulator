package model.building.enums;

/**
 * The direction of the model.elevator.
 */
public enum Direction {
  UP("^"),
  DOWN("v"),
  STOPPED("-");

  private final String display;

  Direction(String symbol) {
    this.display = symbol;
  }

  @Override
  public String toString() {
    return this.display;
  }
}
