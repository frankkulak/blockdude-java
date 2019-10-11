package blockdude.controller;

import blockdude.model.BlockDudeModel;
import blockdude.model.BlockDudeModelListener;
import blockdude.model.Level;
import blockdude.model.LevelSet;
import blockdude.view.BlockDudeView;

/**
 * Represents a generic controller for the classic BlockDude game.
 */
public class ClassicBlockDudeController implements BlockDudeController, BlockDudeModelListener {
  private final BlockDudeModel model;
  private final LevelSet levels;
  private BlockDudeControllerListener listener;

  /**
   * Constructs a new ClassicBlockDudeController using given model, view, and set of levels.
   *
   * @param model  model to control
   * @param view   view for output / listener for this controller
   * @param levels levels to load into model
   * @throws IllegalArgumentException if given model or level set are null
   */
  public ClassicBlockDudeController(BlockDudeModel model, BlockDudeControllerListener view,
                                    LevelSet levels) throws IllegalArgumentException {
    if (model == null || levels == null) {
      throw new IllegalArgumentException("Model and levels must be non-null.");
    }

    model.loadLevel(levels.currentLevel());
    model.setListener(this);
    this.model = model;
    this.levels = levels;
    setListener(view);
    ((BlockDudeView) view).setHelper(this);
  }

  /**
   * A private listener to use when no real listener has been assigned. Created to reduce number of
   * NullPointerExceptions if no listener is assigned to this controller.
   */
  private class EmptyListener implements BlockDudeControllerListener {
    @Override
    public void modelUpdated(BlockDudeModel model) {
      // intentionally empty
    }
  }

  @Override
  public boolean handleCommand(String command) throws IllegalArgumentException, IllegalStateException {
    if (this.handleCommandHelper(command)) {
      this.notifyListenerThatModelUpdated();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void start() throws RuntimeException {
    // todo impl
  }

  /**
   * Handles user command and manipulates model accordingly.
   *
   * @param command string to direct how to change model
   * @return whether or not command was successful
   * @throws IllegalArgumentException if command not valid
   * @throws IllegalStateException    if something goes wrong and program needs to terminate
   */
  private boolean handleCommandHelper(String command) throws IllegalArgumentException, IllegalStateException {
    // fixme I know this is ugly, but commands will be used eventually
    if (command.equalsIgnoreCase("A")) {
      // move left
      return this.model.moveLeft();
    } else if (command.equalsIgnoreCase("D")) {
      // move right
      return this.model.moveRight();
    } else if (command.equalsIgnoreCase("W")) {
      // move up
      return this.model.moveUp();
    } else if (command.equalsIgnoreCase("S")) {
      // either put down or pick up
      if (this.model.putDown()) {
        return true;
      } else {
        return this.model.pickUp();
      }
    } else if (command.equalsIgnoreCase("/reg")) {
      // restart game
      this.restartGame();
      return true;
    } else if (command.equalsIgnoreCase("/rel")) {
      // restart level
      this.restartLevel();
      return true;
    } else if (command.equalsIgnoreCase("/quit")) {
      throw new IllegalStateException("Game ended by player.");
    } else {
      // try password (maybe?)
      String[] passArr = command.split(":");
      if (passArr.length == 2 && passArr[0].equalsIgnoreCase("/pass")) {
        return this.tryLevelPassword(passArr[1]);
      }
    }

    // if nothing returned and threw by now, there was no valid command
    throw new IllegalArgumentException("Command \"" + command + "\" not recognized.");
  }

  @Override
  public void setListener(BlockDudeControllerListener listener) {
    if (listener == null) {
      this.listener = new EmptyListener();
    } else {
      this.listener = listener;
    }
  }

  @Override
  public void gameWon() {
    // fixme what else? notify view?
    this.nextLevel();
  }

  @Override
  public int currentLevelIndex() {
    return levels.currentLevelIndex();
  }

  @Override
  public String currentLevelPassword() {
    return levels.currentLevel().password();
  }

  /**
   * Restarts the game from the first level.
   */
  private void restartGame() {
    this.levels.restart();
    this.model.loadLevel(this.levels.currentLevel());
  }

  /**
   * Restarts the current level.
   */
  private void restartLevel() {
    this.model.restartLevel();
  }

  /**
   * Tries given password to see if it unlocks some level, if so, skips to that level.
   *
   * @param password password to try
   * @return boolean for whether level was successfully unlocked with password
   */
  private boolean tryLevelPassword(String password) {
    try {
      Level level = this.levels.tryPassword(password);
      this.model.loadLevel(level);
      return true;
    } catch (IllegalArgumentException e) {
      // no level has the given password, return false
      return false;
    }
  }

  /**
   * Goes to next level, if there is a next level, and returns boolean for whether or not it could.
   *
   * @return whether or not level could be advanced
   */
  private boolean nextLevel() {
    try {
      Level nextLevel = this.levels.nextLevel();
      this.model.loadLevel(nextLevel);
      return true;
    } catch (IllegalStateException e) {
      // there is no next level, return false
      return false;
    }
  }

  /**
   * Notifies listener that model has been updated.
   */
  private void notifyListenerThatModelUpdated() {
    this.listener.modelUpdated(this.model);
  }
}
