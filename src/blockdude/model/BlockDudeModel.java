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
   * @param level level to load
   * @throws IllegalArgumentException if given level is null
   */
  void loadLevel(Level level) throws IllegalArgumentException;

  /**
   * Moves player to the left, if possible.
   *
   * @return true if moving left changed the state of the board, false otherwise
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean moveLeft() throws IllegalStateException;

  /**
   * Moves player to the right, if possible.
   *
   * @return true if moving right changed the state of the board, false otherwise
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean moveRight() throws IllegalStateException;

  /**
   * Moves player up, if possible.
   *
   * @return true if moving up changed the state of the board, false otherwise
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean moveUp() throws IllegalStateException;

  /**
   * Picks up game piece in front of player, if able to do so.
   *
   * @return true if picking up changed the state of the board, false otherwise
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean pickUp() throws IllegalStateException;

  /**
   * Puts the game piece that player is holding down in front of player.
   *
   * @return true if putting down changed the state of the board, false otherwise
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  boolean putDown() throws IllegalStateException;

  /**
   * Returns the piece being held by the player, which may be null.
   *
   * @return piece that the player is holding
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  GamePiece pieceHeldByPlayer() throws IllegalStateException;

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
