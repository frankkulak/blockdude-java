package blockdude.model.gamepieces;

/**
 * Represents a game piece that is solid (cannot be entered, can stand on).
 */
abstract class SolidGamePiece extends AGamePiece {
  @Override
  public boolean isSolid() {
    return true;
  }

  @Override
  public GamePiece enteredBy(GamePiece gp) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Given GamePiece cannot enter this GamePiece.");
  }
}
