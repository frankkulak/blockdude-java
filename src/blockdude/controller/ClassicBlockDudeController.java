package blockdude.controller;

import blockdude.model.BlockDudeModel;
import blockdude.util.Command;
import blockdude.util.CommandArguments;
import blockdude.util.Level;
import blockdude.util.LevelSet;
import blockdude.view.BlockDudeView;

/**
 * A classic controller for the Block Dude game.
 */
public class ClassicBlockDudeController implements BlockDudeController {
  private final BlockDudeModel model;
  private final BlockDudeView view;
  private final LevelSet levels;
  private CommandArguments commandArguments;

  /**
   * Constructs a new ClassicBlockDudeController using given model, view, and set of levels.
   *
   * @param model  model to control
   * @param view   view to use for output
   * @param levels levels to load into model
   * @throws IllegalArgumentException if given model, view, or level set are null
   */
  public ClassicBlockDudeController(BlockDudeModel model, BlockDudeView view, LevelSet levels)
          throws IllegalArgumentException {
    if (model == null || levels == null || view == null)
      throw new IllegalArgumentException("Model, view, and level set must be non-null.");

    model.loadLevel(levels.currentLevel());
    this.model = model;
    this.view = view;
    this.levels = levels;
  }

  /* Interface methods -------------------------------------------------------------------------- */

  @Override
  public void start() {
    view.start(this);
  }

  @Override
  public void setCommandArguments(CommandArguments args) {
    commandArguments = args;
  }

  @Override
  public void handleCommand(Command command) throws RuntimeException {
    boolean commandSuccessful;
    String errorMessage = "Unspecified error.";

    switch (command) {
      case MOVE_LEFT:
        commandSuccessful = model.moveLeft();
        errorMessage = "Cannot move left.";
        break;
      case MOVE_RIGHT:
        commandSuccessful = model.moveRight();
        errorMessage = "Cannot move right.";
        break;
      case MOVE_UP:
        commandSuccessful = model.moveUp();
        errorMessage = "Cannot move up.";
        break;
      case PICK_UP_PUT_DOWN:
        commandSuccessful = model.pickUpOrPutDown();
        errorMessage = "Cannot pick up or put down.";
        break;
      case RESTART_LEVEL:
        restartLevel();
        commandSuccessful = true;
        break;
      case RESTART_GAME:
        restartGame();
        commandSuccessful = true;
        break;
      case QUIT:
        throw new RuntimeException("Game ended by player.");
      case TRY_PASSWORD:
        commandSuccessful = tryPassword();
        errorMessage = "Password not recognized.";
        break;
      default:
        // this will never actually be thrown
        throw new RuntimeException("Cannot handle null command.");
    }

    if (commandSuccessful) {
      refreshView();
      if (model.isLevelCompleted() && !nextLevel()) {
        view.displayMessage("Congrats! You beat this level set.");
        handleCommand(Command.RESTART_GAME);
      }
    } else {
      view.displayMessage(errorMessage);
    }

    commandArguments = null;
  }

  @Override
  public void refreshView() {
    int levelIndex = levels.currentLevelIndex();
    String levelPassword = levels.currentLevel().password();
    view.refresh(model.layoutToRender(), levelIndex, levelPassword);
  }

  /* Private methods ---------------------------------------------------------------------------- */

  /**
   * Restarts the current level.
   */
  private void restartLevel() {
    model.restartLevel();
  }

  /**
   * Restarts the game from the first level.
   */
  private void restartGame() {
    levels.restart();
    model.loadLevel(levels.currentLevel());
  }

  /**
   * Goes to next level, if there is one.
   *
   * @return true if could advance to next level, false otherwise.
   */
  private boolean nextLevel() {
    try {
      Level nextLevel = levels.nextLevel();
      model.loadLevel(nextLevel);
      refreshView();
      return true;
    } catch (IllegalStateException e) {
      // there is no next level, return false
      return false;
    }
  }

  /**
   * Tries the password that is currently in the command arguments.
   *
   * @return true if level successfully loaded from password, false otherwise
   * @throws RuntimeException if command arguments are invalid
   */
  private boolean tryPassword() throws RuntimeException {
    try {
      String password = commandArguments.passwordToTry;
      Level levelToLoad = levels.tryPassword(password);
      model.loadLevel(levelToLoad);
      return true;
    } catch (NullPointerException | IndexOutOfBoundsException e) {
      // commandArguments hasn't been updated
      throw new RuntimeException("Tried to guess password without specifying password.");
    } catch (IllegalArgumentException e) {
      // password doesn't match any levels in the level set
      return false;
    }
  }
}
