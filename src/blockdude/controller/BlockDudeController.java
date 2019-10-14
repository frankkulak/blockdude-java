package blockdude.controller;

import blockdude.util.Command;
import blockdude.util.CommandArguments;

/**
 * Represents a controller for the Block Dude game.
 */
public interface BlockDudeController {
  /**
   * Starts the game using this controller.
   */
  void start();

  /**
   * Sets this controller's command arguments to the given command arguments.
   *
   * @param args command arguments
   */
  void setCommandArguments(CommandArguments args);

  /**
   * Handles given command and updates the view if anything changed in the model.
   *
   * @param command command to execute
   * @throws RuntimeException if something goes wrong and the program needs to terminate
   */
  void handleCommand(Command command) throws RuntimeException;

  /**
   * Refreshes the view.
   */
  void refreshView();
}
