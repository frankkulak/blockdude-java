package blockdude.controller;

import blockdude.model.BlockDudeModel;

/**
 * Represents a listener for a BlockDudeController.
 */
public interface BlockDudeControllerListener {
  /**
   * Notifies listener that model has been updated.
   *
   * @param model model that has been updated
   */
  void modelUpdated(BlockDudeModel model);
}
