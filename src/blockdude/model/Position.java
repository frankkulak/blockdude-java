package blockdude.model;

/**
 * Represents a 2D position in a plane with X and Y coordinates.
 */
final class Position {
  int x, y;

  /**
   * Constructs a new Position object using given x and y values.
   *
   * @param x x coordinate of position
   * @param y y coordinate of position
   */
  Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Returns new Position object with same x and y coordinates as this one.
   *
   * @return new Position object with current x and y values
   */
  Position copy() {
    return new Position(this.x, this.y);
  }
}
