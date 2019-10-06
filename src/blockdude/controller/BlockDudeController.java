package blockdude.controller;

import blockdude.view.BlockDudeViewHelper;

/**
 * Represents a controller for the BlockDude puzzle game.
 */
public interface BlockDudeController extends BlockDudeViewHelper {
  /**
   * Handles user command and manipulates model accordingly.
   *
   * Valid commands (not case sensitive):
   * - "A" to go left
   * - "D" to go right
   * - "W" to go up
   * - "S" to either pick up or put down a block
   * - "/reg" to restart game
   * - "/rel" to restart level
   * - "/pass X" to try a password
   * - "/quit" to quit the game
   *
   * @param command string to direct how to change model
   * @return whether or not command was successful
   * @throws IllegalArgumentException if command not valid
   * @throws IllegalStateException if something goes wrong and program needs to terminate
   */
  boolean handleCommand(String command) throws IllegalArgumentException, IllegalStateException;

  /**
   * Sets listener of this controller to given listener.
   *
   * @param listener listener to add to this controller
   */
  void setListener(BlockDudeControllerListener listener);
}
