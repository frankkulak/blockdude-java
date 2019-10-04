package blockdude.model;

/**
 * Represents a listener of a BlockDudeModel.
 */
public interface BlockDudeModelListener {
  /**
   * Notifies listener that the game has been won (the player has reached the door).
   */
  void gameWon();
}
