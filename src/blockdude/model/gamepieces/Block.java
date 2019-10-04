package blockdude.model.gamepieces;

/**
 * Represents a block in the BlockDude game. A block is a piece that the player can pick up, put
 * down, and step on.
 */
public class Block extends SolidGamePiece {
  @Override
  public boolean canPickUp() {
    // overrides method from AGamePiece
    return true;
  }

  @Override
  public GamePiece copy() {
    return new Block();
  }
}
