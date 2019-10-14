package blockdude.model;

import java.util.List;

import blockdude.util.GamePiece;
import blockdude.util.Level;

/**
 * Represents a model for the Block Dude game.
 */
public interface BlockDudeModel {
  /**
   * Restarts the current level in the model.
   *
   * @throws RuntimeException if no level has been loaded into model yet
   */
  void restartLevel() throws RuntimeException;

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
   * @throws RuntimeException if no level has been loaded into model yet or if an error that
   *                          prevents the game from being playable occurs
   */
  boolean moveLeft() throws RuntimeException;

  /**
   * Moves player to the right, if possible.
   *
   * @return true if moving right changed the state of the board, false otherwise
   * @throws RuntimeException if no level has been loaded into model yet or if an error that
   *                          prevents the game from being playable occurs
   */
  boolean moveRight() throws RuntimeException;

  /**
   * Moves player up, if possible.
   *
   * @return true if moving up changed the state of the board, false otherwise
   * @throws RuntimeException if no level has been loaded into model yet or if an error that
   *                          prevents the game from being playable occurs
   */
  boolean moveUp() throws RuntimeException;

  /**
   * Picks up the block in front of the player if they are not currently holding something,
   * otherwise puts the block they are currently holding down.
   *
   * @return true if player picked something up or put something down
   * @throws RuntimeException if no level has been loaded into model yet or if an error that
   *                          prevents the game from being playable occurs
   */
  boolean pickUpOrPutDown() throws RuntimeException;

  /**
   * Returns the piece being held by the player, which may be null.
   *
   * @return piece that the player is holding
   * @throws RuntimeException if no level has been loaded into model yet
   */
  GamePiece pieceHeldByPlayer() throws RuntimeException;

  /**
   * Returns current layout of this model.
   *
   * @return layout of model
   * @throws RuntimeException if no level has been loaded into model yet
   */
  List<List<GamePiece>> layout() throws RuntimeException;

  /**
   * Sets listener of this model to given listener.
   *
   * @param listener listener to add to this model
   */
  void setListener(BlockDudeModelListener listener);
}
