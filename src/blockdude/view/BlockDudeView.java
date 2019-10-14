package blockdude.view;

import blockdude.model.BlockDudeModel;

// FIXME: view will be updated after controller.. some code is hacky just to make controller function as i work on it

/**
 * Represents a view for the BlockDude game.
 */
public interface BlockDudeView {
  /**
   * Refreshes this view to display the current state of the given model.
   *
   * @param model model to refresh
   * @throws IllegalStateException if model cannot be rendered
   */
  void refresh(BlockDudeModel model, int levelIndex, String levelPassword) throws IllegalStateException; // fixme change to take in layout & level info
}
