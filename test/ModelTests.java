import org.junit.Before;
import org.junit.Test;

import java.util.List;

import blockdude.model.BlockDudeModel;
import blockdude.model.ClassicBlockDudeModel;
import blockdude.util.GamePiece;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * A class for testing members of the model package.
 */
public class ModelTests {
  // Model example for use in tests
  private BlockDudeModel model;

  /**
   * Configures example for use before each test case.
   */
  @Before
  public void setUp() {
    model = new ClassicBlockDudeModel();
  }

  /* restartLevel() Tests ----------------------------------------------------------------------- */

  /**
   * Tests that restartLevel() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void restartLevelThrowsREWhenNoLevelLoaded() {
    model.restartLevel();
  }

  // todo works when no moves have been made

  // todo works when moves have been made

  // todo works after new level is loaded

  /* loadLevel(...) Tests ----------------------------------------------------------------------- */

  /**
   * Tests that loadLevel(...) throws an IllegalArgumentException when the given level is null.
   */
  @Test(expected = IllegalArgumentException.class)
  public void loadLevelThrowsIAEWhenLevelNull() {
    model.loadLevel(null);
  }

  // todo works when level is not null (loaded level is the same as the one passed in)

  /* moveLeft() Tests --------------------------------------------------------------------------- */

  /**
   * Tests that moveLeft() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void moveLeftThrowsREWhenNoLevelLoaded() {
    model.moveLeft();
  }

  // todo throws RE when moving left moves the player off board (horizontally)

  // todo throws RE when moving left moves the player off board (vertically)

  // todo returns false and doesn't change board when there is a block to the left and already facing left

  // todo returns false and doesn't change board when holding a block and there is a block above the target position

  // todo returns true and changes board correctly when cannot move left, but can face left

  // todo returns true and changes board correctly when can move left and already facing left

  // todo returns true and changes board correctly when can move left and facing right

  // todo returns true and changes board correctly when moving left makes the player fall

  // todo returns true and changes board when moving left beats the level

  /* moveRight() Tests -------------------------------------------------------------------------- */

  /**
   * Tests that moveRight() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void moveRightThrowsREWhenNoLevelLoaded() {
    model.moveRight();
  }

  // todo throws RE when moving right moves the player off board (horizontally)

  // todo throws RE when moving right moves the player off board (vertically)

  // todo returns false and doesn't change board when there is a block to the right and already facing right

  // todo returns false and doesn't change board when holding a block and there is a block above the target position

  // todo returns true and changes board correctly when cannot move right, but can face right

  // todo returns true and changes board correctly when can move right and already facing right

  // todo returns true and changes board correctly when can move right and facing left

  // todo returns true and changes board correctly when moving right makes the player fall

  // todo returns true and changes board when moving right beats the level

  /* moveUp() Tests ----------------------------------------------------------------------------- */

  /**
   * Tests that moveUp() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void moveUpThrowsREWhenNoLevelLoaded() {
    model.moveUp();
  }

  // todo throws RE when moving up moves the player off board (horizontally, facing left)

  // todo throws RE when moving up moves the player off board (horizontally, facing right)

  // todo returns false and doesn't change board when blocks on either side are empty, facing left

  // todo returns false and doesn't change board when blocks on either side are empty, facing right

  // todo returns false and doesn't change board when blocks on either side are 2+ tall, facing left

  // todo returns false and doesn't change board when blocks on either side are 2+ tall, facing right

  // todo returns false and doesn't change board when could move up (left), but holding block & there is a block above target position

  // todo returns false and doesn't change board when could move up (right), but holding block & there is a block above target position

  // todo returns false and doesn't change board when can move up in opposite direction, but not facing one (left)

  // todo returns false and doesn't change board when can move up in opposite direction, but not facing one (right)

  // todo returns true and changes board correctly when can move up in facing direction, and opposite direction (left)

  // todo returns true and changes board correctly when can move up in facing direction, and opposite direction (right)

  // todo returns true and changes board correctly when can move up in facing direction, and not opposite direction (left)

  // todo returns true and changes board correctly when can move up in facing direction, and not opposite direction (right)

  // todo returns true and changes board when moving up beats the level (left)

  // todo returns true and changes board when moving up beats the level (right)

  /* pickUpOrPutDown() Tests -------------------------------------------------------------------- */

  /**
   * Tests that pickUpOrPutDown() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void pickUpOrPutDownThrowsREWhenNoLevelLoaded() {
    model.pickUpOrPutDown();
  }

  // putting block down

  // todo throws RE when putting block down makes block fall off board (horizontally, left)

  // todo throws RE when putting block down makes block fall off board (horizontally, right)

  // todo throws RE when putting block down makes block fall off board (vertically, left)

  // todo throws RE when putting block down makes block fall off board (vertically, right)

  // todo returns false and doesn't change board when player could put block down to side (left), but isn't holding one

  // todo returns false and doesn't change board when player could put block down to side (right), but isn't holding one

  // todo returns false and doesn't change board when player could put block down to above side (left) but isn't holding one

  // todo returns false and doesn't change board when player could put block down to above side (right), but isn't holding one

  // todo returns false and doesn't change board when player can't put block down (2+ blocks to left, facing left) and isn't holding one

  // todo returns false and doesn't change board when player can't put block down (2+ blocks to right, facing right) and isn't holding one

  // todo returns false and doesn't change board when player can't put block down (2+ blocks to left, facing left) while holding one

  // todo returns false and doesn't change board when player can't put block down (2+ blocks to right, facing right) while holding one

  // todo returns true and changes board correctly when player can put block down to left

  // todo returns true and changes board correctly when player can put block down to right

  // todo returns true and changes board correctly when player can put block down to left even if there is a block above the target position

  // todo returns true and changes board correctly when player can put block down to right even if there is a block above the target position

  // todo returns true and changes board correctly when player can put block down to up-left

  // todo returns true and changes board correctly when player can put block down to up-right

  // picking block up

  // todo returns false and doesn't change board when there is no block to pick up, not holding a block, facing left

  // todo returns false and doesn't change board when there is no block to pick up, not holding a block, facing right

  // todo returns false and doesn't change board when there is no block to pick up, holding a block, facing left

  // todo returns false and doesn't change board when there is no block to pick up, holding a block, facing right

  // todo returns false and doesn't change board when there is a block to pick up, but already holding one, facing left

  // todo returns false and doesn't change board when there is a block to pick up, but already holding one, facing right

  // todo returns false and doesn't change board when there is a block to pick up, not holding one, but there is block above player, facing left

  // todo returns false and doesn't change board when there is a block to pick up, not holding one, but there is block above player, facing right

  // todo returns false and doesn't change board when there is a block to pick up, not holding one, but facing wrong way (left)

  // todo returns false and doesn't change board when there is a block to pick up, not holding one, but facing wrong way (right)

  // todo returns false and doesn't change board when there is a block to pick up, not holding one, but there is one on top of it, facing left

  // todo returns false and doesn't change board when there is a block to pick up, not holding one, but there is one on top of it, facing right

  // todo returns true and changes board correctly when there is a block to pick up in facing direction (left)

  // todo returns true and changes board correctly when there is a block to pick up in facing direction (right)

  /* layoutToRender() Tests --------------------------------------------------------------------- */

  /**
   * Tests that layoutToRender() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void layoutToRenderThrowsREWhenNoLevelLoaded() {
    model.layoutToRender();
  }

  // todo returns correct layout at start of level

  // todo returns correct layout after restarting level that hasn't yet been played

  // todo returns correct layout after restarting level that has been played

  // todo returns correct layout after moving left

  // todo returns correct layout after moving right

  // todo returns correct layout after failing to move left

  // todo returns correct layout after failing to move right

  // todo returns correct layout after facing left

  // todo returns correct layout after facing right

  // todo returns correct layout after moving up-left

  // todo returns correct layout after moving up-right

  // todo returns correct layout after failing to move up-left

  // todo returns correct layout after failing to move up-right

  // todo returns correct layout after moving to left and falling

  // todo returns correct layout after moving to right and falling

  // todo returns correct layout after picking block up from left

  // todo returns correct layout after picking block up from right

  // todo returns correct layout after failing to pick block up from left

  // todo returns correct layout after failing to pick block up from right

  // todo returns correct layout after putting block down to left

  // todo returns correct layout after putting block down to right

  // todo returns correct layout after failing to put block down to left

  // todo returns correct layout after failing to put block down to right

  // todo returns correct layout after failing to put block down to up-left

  // todo returns correct layout after failing to put block down to up-right

  // todo returns correct layout after reaching exit by moving left

  // todo returns correct layout after reaching exit by moving right

  // todo returns correct layout after reaching exit by falling left

  // todo returns correct layout after reaching exit by falling right

  // todo returns correct layout after reaching exit by moving up-left

  // todo returns correct layout after reaching exit by moving up-right

  /* isLevelCompleted() Tests ------------------------------------------------------------------- */

  /**
   * Tests that isLevelCompleted() throws a RuntimeException when no level has been loaded.
   */
  @Test(expected = RuntimeException.class)
  public void isLevelCompletedThrowsREWhenNoLevelLoaded() {
    model.isLevelCompleted();
  }

  // todo returns false at start of level

  // todo returns false after restarting level

  // todo returns false after making moves that do not beat game

  // todo returns false when standing adjacent (left) to a door

  // todo returns false when standing adjacent (right) to a door

  // todo returns true when reaches door from left

  // todo returns true when reaches door from right

  // todo returns true when reaches door from up-left

  // todo returns true when reaches door from up-right

  // todo returns true when reaches door from falling left

  // todo returns true when reaches door from falling right

  /* Private helper methods --------------------------------------------------------------------- */

  /**
   * Determines whether two given layouts are identical.
   *
   * @param layout1 first layout to compare
   * @param layout2 second layout to compare
   * @return true if layouts are exactly the same, false otherwise
   */
  private boolean layoutsAreSame(List<List<GamePiece>> layout1, List<List<GamePiece>> layout2) {
    if (layout1.size() != layout2.size()) return false;

    for (int row = 0; row < layout1.size(); row++) {
      List<GamePiece> row1 = layout1.get(row);
      List<GamePiece> row2 = layout2.get(row);
      if (row1.size() != row2.size()) return false;

      for (int col = 0; col < row1.size(); col++) {
        GamePiece piece1 = row1.get(col);
        GamePiece piece2 = row2.get(col);
        if (piece1 != piece2) return false;
      }
    }

    return true;
  }

}
