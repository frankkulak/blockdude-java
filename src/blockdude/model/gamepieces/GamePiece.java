package blockdude.model.gamepieces;

/**
 * Represents a game piece in the BlockDude game.
 */
public interface GamePiece {
  /**
   * Determines and returns whether this game piece is solid (defined as: cannot be entered by the
   * player, and can be stepped on by the player).
   *
   * @return whether or not game piece is solid
   */
  boolean isSolid();

  /**
   * Determines and returns whether this game piece can be picked up by the player.
   *
   * @return whether or not game piece can be picked up
   */
  boolean canPickUp();

  /**
   * Returns a copy of this GamePiece object.
   *
   * @return copy of self
   */
  GamePiece copy();

  /**
   * Determines and returns what GamePiece should be used when the given GamePiece enters this
   * GamePiece. This is only supported by certain open game pieces.
   *
   * @param gp GamePiece that is entering this GamePiece
   * @return GamePiece that represents given GamePiece entering this one
   * @throws UnsupportedOperationException if called on GamePiece which cannot be entered
   */
  GamePiece enteredBy(GamePiece gp) throws UnsupportedOperationException;
}
