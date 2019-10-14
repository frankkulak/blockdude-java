package blockdude.view;

import java.util.List;

import blockdude.controller.BlockDudeController;
import blockdude.util.GamePiece;

/**
 * Represents a view for the Block Dude game.
 */
public interface BlockDudeView {
  /**
   * Starts this view, passing all read input to the given controller.
   *
   * @param controller controller to use for running the game
   */
  void start(BlockDudeController controller);

  /**
   * Refreshes this view to display the given information.
   *
   * @param layout        layout to display
   * @param levelIndex    index of current level
   * @param levelPassword password of current level
   */
  void refresh(List<List<GamePiece>> layout, int levelIndex, String levelPassword);

  /**
   * Displays a message to the user.
   *
   * @param message message to display
   */
  void displayMessage(String message);
}
