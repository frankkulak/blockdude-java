package blockdude.model;

/**
 * Represents game pieces in the Block Dude game.
 */
public enum GamePiece {
  BLOCK, DOOR, EMPTY, PLAYER_LEFT, PLAYER_RIGHT, PLAYER_DOOR, WALL;

  /**
   * Determines and returns whether the given GamePiece is solid. Solid GamePieces cannot be entered
   * by the player, and they can also
   *
   * @param gp GamePiece that is being tested
   * @return true of given GamePiece is solid, false if it is not
   */
  public static boolean isSolid(GamePiece gp) {
    GamePiece[] solidPieces = {BLOCK, PLAYER_LEFT, PLAYER_RIGHT, WALL};
    return contains(solidPieces, gp);
  }

  /**
   * Determines and returns whether the given GamePiece can be entered by another GamePiece.
   *
   * @param gp GamePiece that is being tested
   * @return true if given GamePiece can be entered, false if it cannot be
   */
  public static boolean canEnter(GamePiece gp) {
    GamePiece[] solidPieces = {DOOR, EMPTY};
    return contains(solidPieces, gp);
  }

  /**
   * Determines and returns whether the given GamePiece can be picked up by the player.
   *
   * @param gp GamePiece that is being tested
   * @return true if given GamePiece can be picked up, false if it cannot be
   */
  public static boolean canPickUp(GamePiece gp) {
    GamePiece[] solidPieces = {BLOCK};
    return contains(solidPieces, gp);
  }

  /**
   * Determines and returns whether the given array of GamePieces contains the given GamePiece.
   *
   * @param arr    array of GamePieces to check
   * @param target GamePiece for which to check contents of array
   * @return true if given array contains given GamePiece, false otherwise
   */
  private static boolean contains(GamePiece[] arr, GamePiece target) {
    for (GamePiece gp : arr) if (gp == target) return true;
    return false;
  }
}
