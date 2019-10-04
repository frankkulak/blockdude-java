package blockdude.model.gamepieces;

/**
 * Represents a wall piece in the BlockDude game. Walls are used as barriers which the player cannot
 * pick up or move through. The entire game should be surrounded in walls.
 */
public class Wall extends SolidGamePiece {
  @Override
  public GamePiece copy() {
    return new Wall();
  }
}
