package blockdude.model.gamepieces;

/**
 * Represents a game piece which cannot be picked up by default, but subclasses may override that.
 */
abstract class AGamePiece implements GamePiece {
  @Override
  public boolean canPickUp() {
    return false;
  }
}
