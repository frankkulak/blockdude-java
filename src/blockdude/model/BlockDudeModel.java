package blockdude.model;

import java.util.List;

/**
 * Represents a model for the Block Dude game.
 */
public interface BlockDudeModel {
  /**
   * Restarts the current level in the model.
   *
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  void restartLevel() throws IllegalStateException;

  /**
   * Sets given level to be current level of this model.
   *
   * @param level new level to load
   * @throws IllegalArgumentException if given level is null
   */
  void loadNewLevel(Level level) throws IllegalArgumentException;

  /**
   * Moves player to the left, if possible.
   *
   * @return whether or not move could be made
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean moveLeft() throws IllegalStateException;

  /**
   * Moves player to the right, if possible.
   *
   * @return whether or not move could be made
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean moveRight() throws IllegalStateException;

  /**
   * Moves player up, if possible.
   *
   * @return whether or not move could be made
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean moveUp() throws IllegalStateException;

  /**
   * Picks up game piece in front of player, if able to do so.
   *
   * @return whether or not a piece was picked up
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean pickUp() throws IllegalStateException;

  /**
   * Puts game piece that player is holding down in front of player.
   *
   * @return whether or not piece could be put down
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean putDown() throws IllegalStateException;

  /**
   * Returns whether the player is holding something or not.
   *
   * @return true if player holding something, false otherwise
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean playerIsHoldingSomething() throws IllegalStateException;

  /**
   * Finds and returns password for current level.
   *
   * @return password for current level
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  String curLevelPassword() throws IllegalStateException;

  /**
   * Returns current layout of this model.
   *
   * @return layout of model
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  List<List<GamePiece>> layout() throws IllegalStateException;

  /**
   * Sets listener of this model to given listener.
   *
   * @param listener listener to add to this model
   */
  void setListener(BlockDudeModelListener listener);
}
