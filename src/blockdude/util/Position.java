package blockdude.util;

/**
 * Represents a position on the Block Dude game board. Column indices are equivalent to coordinates
 * on the x axis, while row indices are equivalent to those on the y axis.
 */
public final class Position {
  public int col, row;

  /**
   * Constructs a new Position object using given col and row values.
   *
   * @param col column index of position
   * @param row row index of position
   */
  public Position(int col, int row) {
    this.col = col;
    this.row = row;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Position)) return false;
    Position that = (Position) other;
    return col == that.col && row == that.row;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public String toString() {
    return "(" + col + "," + row + ")";
  }

  /**
   * Returns a new position with the same col and row coordinates as this one.
   *
   * @return new position with same col and row as this one
   */
  public Position copy() {
    return new Position(col, row);
  }
}
