package blockdude.model.gamepieces;

/**
 * Represents a door in the BlockDude game. Doors mark the end of the level, AKA where the player is
 * trying to reach in order to win the game.
 */
public class Door extends OpenGamePiece {
  @Override
  public GamePiece copy() {
    return new Door();
  }

  @Override
  public GamePiece enteredBy(GamePiece gp) throws UnsupportedOperationException {
    if (gp instanceof Player) {
      Player player = (Player) gp;
      return new PlayerInDoor(player, this);
    } else {
      throw new UnsupportedOperationException("Door can only be entered by Player.");
    }
  }
}
