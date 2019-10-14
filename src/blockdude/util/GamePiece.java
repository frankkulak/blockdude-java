package blockdude.util;

/**
 * Represents game pieces in the Block Dude game.
 */
public enum GamePiece {
  BLOCK, DOOR, EMPTY, PLAYER_LEFT, PLAYER_RIGHT, WALL;

  /* Public methods ----------------------------------------------------------------------------- */

  /**
   * Determines and returns whether the given GamePiece can be picked up by the player.
   *
   * @param gp GamePiece that is being tested
   * @return true if given GamePiece can be picked up, false if it cannot be
   */
  public static boolean canPickUp(GamePiece gp) {
    GamePiece[] pickUpPieces = {BLOCK};
    return contains(pickUpPieces, gp);
  }

  /**
   * Returns whether the given GamePiece is a player.
   *
   * @param gp GamePiece to check
   * @return true if GamePiece is a player, false otherwise
   */
  public static boolean isPlayer(GamePiece gp) {
    GamePiece[] playerPieces = {PLAYER_LEFT, PLAYER_RIGHT};
    return contains(playerPieces, gp);
  }

  /**
   * Determines and returns whether the given GamePiece is solid. A GamePiece is solid if it cannot
   * be entered and will stop any game piece that falls on it.
   *
   * @param gp GamePiece that is being tested
   * @return true of given GamePiece is solid, false if it is not
   */
  public static boolean isSolid(GamePiece gp) {
    GamePiece[] solidPieces = {BLOCK, WALL};
    return contains(solidPieces, gp) || isPlayer(gp);
  }

  /* Private methods ---------------------------------------------------------------------------- */

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
