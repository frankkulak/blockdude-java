package blockdude.view;

import blockdude.controller.BlockDudeControllerListener;
import blockdude.model.BlockDudeModel;

/**
 * Represents a view for the BlockDude game.
 */
public interface BlockDudeView extends BlockDudeControllerListener {
  /**
   * Refreshes this view to display the current state of the given model.
   *
   * @param model model to refresh
   * @throws IllegalStateException if model cannot be rendered
   */
  void refresh(BlockDudeModel model) throws IllegalStateException; // fixme change to take in layout & level info

  /**
   * Sets the helper for this view.
   *
   * @param helper helper for this view
   */
  void setHelper(BlockDudeViewHelper helper);
}
