package blockdude.model.gamepieces;

/**
 * Represents an empty space in the BlockDude game. Players can move through empty spaces.
 */
public class Empty extends OpenGamePiece {
  @Override
  public GamePiece copy() {
    return new Empty();
  }

  @Override
  public GamePiece enteredBy(GamePiece gp) throws UnsupportedOperationException {
    return gp;
  }
}
