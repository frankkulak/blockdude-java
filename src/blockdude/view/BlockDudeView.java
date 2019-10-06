package blockdude.view;

import blockdude.controller.BlockDudeControllerListener;
import blockdude.model.BlockDudeModel;

/**
 * Represents a view for the BlockDude game.
 */
public interface BlockDudeView extends BlockDudeControllerListener {
  /**
   * Renders given BlockDudeModel using this view.
   *
   * @param model model to render
   * @throws IllegalStateException if model cannot be rendered
   */
  void render(BlockDudeModel model) throws IllegalStateException;

  /**
   * Sets the helper for this view.
   *
   * @param helper helper for this view
   */
  void setHelper(BlockDudeViewHelper helper);
}
