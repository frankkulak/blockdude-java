package blockdude.model.gamepieces;

/**
 * Represents a game piece that is not solid (can be entered, cannot stand on).
 */
abstract class OpenGamePiece extends AGamePiece {
  @Override
  public boolean isSolid() {
    return false;
  }
}
