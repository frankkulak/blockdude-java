package blockdude.model;

/**
 * Represents a listener of a BlockDudeModel.
 */
public interface BlockDudeModelListener {
  /**
   * Notifies listener that the player has reached the door (the level has been beat).
   */
  void doorReached();

  /**
   * Notifies listener that the model has finished updating.
   */
  void finishedUpdating();
}
