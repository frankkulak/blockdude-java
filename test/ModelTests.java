import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import blockdude.model.BlockDudeModel;
import blockdude.model.ClassicBlockDudeModel;
import blockdude.util.GamePiece;
import blockdude.util.Level;
import blockdude.util.LevelSet;
import blockdude.util.LevelSetFileReader;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * A class for testing members of the model package.
 */
public class ModelTests {
  // Model example for use in tests
  private BlockDudeModel model;
  private static LevelSet levels;

  @BeforeClass
  public static void onlyOnce() {
    try {
      String filename = "levelSources/levels.txt";
      levels = LevelSetFileReader.parseLevelSetFile(new FileReader(filename));
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File name is incorrect.");
    }
  }

  @Before
  public void setUp() {
    model = new ClassicBlockDudeModel();
    levels.restart();
  }

  /* restartLevel() Tests ----------------------------------------------------------------------- */

  @Test(expected = RuntimeException.class)
  public void restartLevelThrowsREWhenNoLevelLoaded() {
    model.restartLevel();
  }

  @Test
  public void restartLevelWorksWhenNoMovesMade() {
    Level level = levels.currentLevel();
    model.loadLevel(level);
    List<List<GamePiece>> originalModelLayout = model.layoutToRender();
    model.restartLevel();
    List<List<GamePiece>> newModelLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalModelLayout, newModelLayout));
  }

  @Test
  public void restartLevelWorksAfterMovesMade() {
    Level level = levels.currentLevel();
    model.loadLevel(level);
    List<List<GamePiece>> originalModelLayout = model.layoutToRender();

    // testing just one move
    model.moveLeft();
    List<List<GamePiece>> modelLayoutAfterMovingLeft = model.layoutToRender();
    assertFalse(layoutsAreSame(originalModelLayout, modelLayoutAfterMovingLeft));
    model.restartLevel();
    List<List<GamePiece>> modelLayoutAfterFirstRestart = model.layoutToRender();
    assertFalse(layoutsAreSame(modelLayoutAfterMovingLeft, modelLayoutAfterFirstRestart));
    assertTrue(layoutsAreSame(originalModelLayout, modelLayoutAfterFirstRestart));

    // testing more than one move
    model.moveLeft();
    model.moveUp();
    List<List<GamePiece>> modelLayoutAfterMovingLeftAndUp = model.layoutToRender();
    assertFalse(layoutsAreSame(originalModelLayout, modelLayoutAfterMovingLeftAndUp));
    model.restartLevel();
    List<List<GamePiece>> modelLayoutAfterSecondRestart = model.layoutToRender();
    assertFalse(layoutsAreSame(modelLayoutAfterMovingLeftAndUp, modelLayoutAfterSecondRestart));
    assertTrue(layoutsAreSame(originalModelLayout, modelLayoutAfterSecondRestart));
  }

  @Test
  public void restartLevelWorksAfterOtherLevelLoaded() {
    Level firstLevel = levels.currentLevel();
    model.loadLevel(firstLevel);
    List<List<GamePiece>> firstModelLayout = model.layoutToRender();
    Level secondLevel = levels.nextLevel();
    model.loadLevel(secondLevel);
    List<List<GamePiece>> secondModelLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(firstModelLayout, secondModelLayout));
    model.restartLevel();
    List<List<GamePiece>> restartedModelLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(firstModelLayout, restartedModelLayout));
    assertTrue(layoutsAreSame(secondModelLayout, restartedModelLayout));
  }

  /* loadLevel(...) Tests ----------------------------------------------------------------------- */

  @Test(expected = IllegalArgumentException.class)
  public void loadLevelThrowsIAEWhenLevelNull() {
    model.loadLevel(null);
  }

  @Test
  public void loadLevelLoadsGivenLevelCorrectly() {
    // testing loading one level
    Level firstLevel = levels.currentLevel();
    List<List<GamePiece>> firstLevelLayout = firstLevel.layout();
    model.loadLevel(firstLevel);
    List<List<GamePiece>> firstModelLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(firstLevelLayout, firstModelLayout));

    // testing loading second level
    Level secondLevel = levels.nextLevel();
    List<List<GamePiece>> secondLevelLayout = secondLevel.layout();
    model.loadLevel(secondLevel);
    List<List<GamePiece>> secondModelLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(secondLevelLayout, secondModelLayout));

    // testing interactivity of loaded levels
    assertFalse(layoutsAreSame(firstLevelLayout, secondModelLayout));
    assertFalse(layoutsAreSame(secondLevelLayout, firstModelLayout));
    assertFalse(layoutsAreSame(firstModelLayout, secondModelLayout));
  }

  /* moveLeft() Tests --------------------------------------------------------------------------- */

  @Test(expected = RuntimeException.class)
  public void moveLeftThrowsREWhenNoLevelLoaded() {
    model.moveLeft();
  }

  @Test(expected = RuntimeException.class)
  public void moveLeftThrowsREWhenMovingPlayerOffBoardHorizontally() {
    try {
      String levelString = "-level test\n" +
              "L_X\n" +
              "XXX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.moveLeft();
    }
  }

  @Test(expected = RuntimeException.class)
  public void moveLeftThrowsREWhenMovingPlayerOffBoardVertically() {
    try {
      String levelString = "-level test\n" +
              "_LX\n" +
              "_XX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.moveLeft();
    }
  }

  @Test
  public void moveLeftReturnsFalseWhenBlockInWayAndCannotTurn() {
    String levelString = "-level test\n" +
            "X_BL_X\n" +
            "XXXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveLeft());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, resultLayout));
  }

  @Test
  public void moveLeftReturnsFalseWhenHoldingBlockAndThereIsBlockAboveTargetPosition() {
    String levelString = "-level test\n" +
            "XX__X\n" +
            "X_BLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> layoutAfterPickUp = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterPickUp));
    assertTrue(model.moveLeft());
    List<List<GamePiece>> layoutAfterFirstMove = model.layoutToRender();
    assertFalse(layoutsAreSame(layoutAfterPickUp, layoutAfterFirstMove));
    assertFalse(model.moveLeft());
    List<List<GamePiece>> layoutAfterSecondMove = model.layoutToRender();
    assertTrue(layoutsAreSame(layoutAfterFirstMove, layoutAfterSecondMove));

    String expectedResultString = "-level test\n" +
            "XX__X\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterSecondMove, expectedLayout));
  }

  @Test
  public void moveLeftReturnsTrueWhenBlockInWayButCanTurnLeft() {
    String levelString = "-level test\n" +
            "X_BR_X\n" +
            "XXXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveLeft());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X_BL_X\n" +
            "XXXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveLeftReturnsTrueWhenFacingLeftAndCanMoveLeft() {
    String levelString = "-level test\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveLeft());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "XL__X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveLeftReturnsTrueWhenFacingRightAndCanMoveLeft() {
    String levelString = "-level test\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveLeft());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "XL__X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveLeftReturnsTrueWhenMovingLeftMakesPlayerFall() {
    String levelString = "-level test\n" +
            "X_LX\n" +
            "X_XX\n" +
            "X_XX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveLeft());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X__X\n" +
            "X_XX\n" +
            "XLXX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveLeftReturnsTrueWhenMovingLeftReachesDoor() {
    String levelString = "-level test\n" +
            "XDLX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveLeft());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertTrue(model.isLevelCompleted());
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "XL_X\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  /* moveRight() Tests -------------------------------------------------------------------------- */

  @Test(expected = RuntimeException.class)
  public void moveRightThrowsREWhenNoLevelLoaded() {
    model.moveRight();
  }

  @Test(expected = RuntimeException.class)
  public void moveRightThrowsREWhenMovingPlayerOffBoardHorizontally() {
    try {
      String levelString = "-level test\n" +
              "X_R\n" +
              "XXX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.moveRight();
    }
  }

  @Test(expected = RuntimeException.class)
  public void moveRightThrowsREWhenMovingPlayerOffBoardVertically() {
    try {
      String levelString = "-level test\n" +
              "XR_\n" +
              "XX_\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.moveRight();
    }
  }

  @Test
  public void moveRightReturnsFalseWhenBlockInWayAndCannotTurn() {
    String levelString = "-level test\n" +
            "X_RB_X\n" +
            "XXXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveRight());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, resultLayout));
  }

  @Test
  public void moveRightReturnsFalseWhenHoldingBlockAndThereIsBlockAboveTargetPosition() {
    String levelString = "-level test\n" +
            "X__XX\n" +
            "XRB_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> layoutAfterPickUp = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterPickUp));
    assertTrue(model.moveRight());
    List<List<GamePiece>> layoutAfterFirstMove = model.layoutToRender();
    assertFalse(layoutsAreSame(layoutAfterPickUp, layoutAfterFirstMove));
    assertFalse(model.moveRight());
    List<List<GamePiece>> layoutAfterSecondMove = model.layoutToRender();
    assertTrue(layoutsAreSame(layoutAfterFirstMove, layoutAfterSecondMove));

    String expectedResultString = "-level test\n" +
            "X__XX\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterSecondMove, expectedLayout));
  }

  @Test
  public void moveRightReturnsTrueWhenBlockInWayButCanTurnRight() {
    String levelString = "-level test\n" +
            "X_LB_X\n" +
            "XXXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveRight());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X_RB_X\n" +
            "XXXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveRightReturnsTrueWhenFacingRightAndCanMoveRight() {
    String levelString = "-level test\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveRight());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X__RX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveRightReturnsTrueWhenFacingLeftAndCanMoveRight() {
    String levelString = "-level test\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveRight());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X__RX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveRightReturnsTrueWhenMovingRightMakesPlayerFall() {
    String levelString = "-level test\n" +
            "XR_X\n" +
            "XX_X\n" +
            "XX_X\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveRight());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X__X\n" +
            "XX_X\n" +
            "XXRX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  @Test
  public void moveRightReturnsTrueWhenMovingRightReachesDoor() {
    String levelString = "-level test\n" +
            "XRDX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveRight());
    List<List<GamePiece>> resultLayout = model.layoutToRender();
    assertTrue(model.isLevelCompleted());
    assertFalse(layoutsAreSame(originalLayout, resultLayout));

    String expectedResultString = "-level test\n" +
            "X_RX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(resultLayout, expectedResultLayout));
  }

  /* moveUp() Tests ----------------------------------------------------------------------------- */

  @Test(expected = RuntimeException.class)
  public void moveUpThrowsREWhenNoLevelLoaded() {
    model.moveUp();
  }

  @Test(expected = RuntimeException.class)
  public void moveUpThrowsREWhenMovingPlayerOffBoardHorizontallyToLeft() {
    try {
      String levelString = "-level test\n" +
              "XLX\n" +
              "XXX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.moveUp();
    }
  }

  @Test(expected = RuntimeException.class)
  public void moveUpThrowsREWhenMovingPlayerOffBoardHorizontallyToRight() {
    try {
      String levelString = "-level test\n" +
              "XRX\n" +
              "XXX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.moveUp();
    }
  }

  @Test
  public void moveUpReturnsFalseWhenSurroundedByEmptyGamePiecesFacingLeft() {
    String levelString = "-level test\n" +
            "___\n" +
            "_L_\n" +
            "XXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, layoutAfterMove));
  }

  @Test
  public void moveUpReturnsFalseWhenSurroundedByEmptyGamePiecesFacingRight() {
    String levelString = "-level test\n" +
            "___\n" +
            "_R_\n" +
            "XXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, layoutAfterMove));
  }

  @Test
  public void moveUpReturnsFalseWhenSurroundedByTallGamePiecesFacingLeft() {
    String levelString = "-level test\n" +
            "X_X\n" +
            "XLX\n" +
            "XXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, layoutAfterMove));
  }

  @Test
  public void moveUpReturnsFalseWhenSurroundedByTallGamePiecesFacingRight() {
    String levelString = "-level test\n" +
            "X_X\n" +
            "XRX\n" +
            "XXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, layoutAfterMove));
  }

  @Test
  public void moveUpReturnsFalseWhenCantMoveUpBecauseHoldingBlockFacingLeft() {
    String levelString = "-level test\n" +
            "XX__X\n" +
            "X___X\n" +
            "XXBLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    List<List<GamePiece>> layoutAfterMovingLeft = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMovingLeft));
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMovingUp = model.layoutToRender();
    assertTrue(layoutsAreSame(layoutAfterMovingLeft, layoutAfterMovingUp));

    String expectedResultString = "-level test\n" +
            "XX__X\n" +
            "X___X\n" +
            "XXL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMovingUp, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsFalseWhenCantMoveUpBecauseHoldingBlockFacingRight() {
    String levelString = "-level test\n" +
            "X__XX\n" +
            "X___X\n" +
            "XRBXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    List<List<GamePiece>> layoutAfterMovingRight = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMovingRight));
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMovingUp = model.layoutToRender();
    assertTrue(layoutsAreSame(layoutAfterMovingRight, layoutAfterMovingUp));

    String expectedResultString = "-level test\n" +
            "X__XX\n" +
            "X___X\n" +
            "X_RXX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMovingUp, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsFalseWhenCanOnlyMoveUpRightAndFacingLeft() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_LXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, layoutAfterMove));
  }

  @Test
  public void moveUpReturnsFalseWhenCanOnlyMoveUpLeftAndFacingRight() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XXR_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, layoutAfterMove));
  }

  @Test
  public void moveUpReturnsFalseWhenHoldingBlockAndMovingUpLeftToTopRow() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XXBLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> layoutAfterPickUp = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterPickUp));
    assertTrue(model.moveLeft());
    List<List<GamePiece>> layoutAfterMoveLeft = model.layoutToRender();
    assertFalse(layoutsAreSame(layoutAfterPickUp, layoutAfterMoveLeft));
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMoveUp = model.layoutToRender();
    assertTrue(layoutsAreSame(layoutAfterMoveLeft, layoutAfterMoveUp));
  }

  @Test
  public void moveUpReturnsFalseWhenHoldingBlockAndMovingUpRightToTopRow() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XRBXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> layoutAfterPickUp = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterPickUp));
    assertTrue(model.moveRight());
    List<List<GamePiece>> layoutAfterMoveRight = model.layoutToRender();
    assertFalse(layoutsAreSame(layoutAfterPickUp, layoutAfterMoveRight));
    assertFalse(model.moveUp());
    List<List<GamePiece>> layoutAfterMoveUp = model.layoutToRender();
    assertTrue(layoutsAreSame(layoutAfterMoveRight, layoutAfterMoveUp));
  }

  @Test
  public void moveUpReturnsTrueWhenCanMoveUpInEitherDirectionAndFacingLeft() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XXLXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));

    String expectedResultString = "-level test\n" +
            "XL__X\n" +
            "XX_XX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsTrueWhenCanMoveUpInEitherDirectionAndFacingRight() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XXRXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));

    String expectedResultString = "-level test\n" +
            "X__RX\n" +
            "XX_XX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsTrueWhenCanOnlyMoveUpLeftAndFacingLeft() {
    String levelString = "-level test\n" +
            "X__X\n" +
            "XXLX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));

    String expectedResultString = "-level test\n" +
            "XL_X\n" +
            "XX_X\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsTrueWhenCanOnlyMoveUpRightAndFacingRight() {
    String levelString = "-level test\n" +
            "X__X\n" +
            "XRXX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));

    String expectedResultString = "-level test\n" +
            "X_RX\n" +
            "X_XX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsTrueWhenMovingUpLeftBeatsLevel() {
    String levelString = "-level test\n" +
            "XD_X\n" +
            "XXLX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));
    assertTrue(model.isLevelCompleted());

    String expectedResultString = "-level test\n" +
            "XL_X\n" +
            "XX_X\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  @Test
  public void moveUpReturnsTrueWhenMovingUpRightBeatsLevel() {
    String levelString = "-level test\n" +
            "X_DX\n" +
            "XRXX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.moveUp());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));
    assertTrue(model.isLevelCompleted());

    String expectedResultString = "-level test\n" +
            "X_RX\n" +
            "X_XX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  /* pickUpOrPutDown() Tests -------------------------------------------------------------------- */

  @Test(expected = RuntimeException.class)
  public void pickUpOrPutDownThrowsREWhenNoLevelLoaded() {
    model.pickUpOrPutDown();
  }

  // putting block down

  @Test(expected = RuntimeException.class)
  public void putDownThrowsREWhenBlockFallsOffBoardHorizontallyToLeft() {
    try {
      String levelString = "-level test\n" +
              "__\n" +
              "BL\n" +
              "XX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);

      assertTrue(model.pickUpOrPutDown());
      assertTrue(model.moveLeft());
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.pickUpOrPutDown();
    }
  }

  @Test(expected = RuntimeException.class)
  public void putDownThrowsREWhenBlockFallsOffBoardHorizontallyToRight() {
    try {
      String levelString = "-level test\n" +
              "__\n" +
              "RB\n" +
              "XX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);

      assertTrue(model.pickUpOrPutDown());
      assertTrue(model.moveRight());
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.pickUpOrPutDown();
    }
  }

  @Test(expected = RuntimeException.class)
  public void putDownThrowsREWhenBlockFallsOffBoardVerticallyToLeft() {
    try {
      String levelString = "-level test\n" +
              "___\n" +
              "_BL\n" +
              "_XX\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);

      assertTrue(model.pickUpOrPutDown());
      assertTrue(model.moveLeft());
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.pickUpOrPutDown();
    }
  }

  @Test(expected = RuntimeException.class)
  public void putDownThrowsREWhenBlockFallsOffBoardVerticallyToRight() {
    try {
      String levelString = "-level test\n" +
              "___\n" +
              "RB_\n" +
              "XX_\n" +
              "-/level";
      Level level = levelFromString(levelString);
      model.loadLevel(level);

      assertTrue(model.pickUpOrPutDown());
      assertTrue(model.moveRight());
    } catch (RuntimeException e) {
      fail("RuntimeException thrown too soon: " + e.getMessage());
    } finally {
      model.pickUpOrPutDown();
    }
  }

  @Test
  public void putDownReturnsFalseWhenWouldBeAbleToPlaceBlockLeftButNotHoldingOne() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void putDownReturnsFalseWhenWouldBeAbleToPlaceBlockRightButNotHoldingOne() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void putDownReturnsFalseWhenWouldBeAbleToPlaceBlockUpLeftButNotHoldingOne() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XXL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void putDownReturnsFalseWhenWouldBeAbleToPlaceBlockUpRightButNotHoldingOne() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_RXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void putDownReturnsFalseWhenCantPlaceBlockLeftAndNotHoldingOne() {
    String levelString = "-level test\n" +
            "X__X\n" +
            "XL_X\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void putDownReturnsFalseWhenCantPlaceBlockRightAndNotHoldingOne() {
    String levelString = "-level test\n" +
            "X__X\n" +
            "X_RX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void putDownReturnsFalseWhenHoldingBlockButCantPlaceBlockLeft() {
    String levelString = "-level test\n" +
            "X__X\n" +
            "XBLX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "XB_X\n" +
            "XL_X\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsFalseWhenHoldingBlockButCantPlaceBlockRight() {
    String levelString = "-level test\n" +
            "X__X\n" +
            "XRBX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X_BX\n" +
            "X_RX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToLeft() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_BLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X___X\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToRight() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XRB_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X___X\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToLeftAndThereIsBlockAboveTarget() {
    String levelString = "-level test\n" +
            "XX__X\n" +
            "X_BLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "XX__X\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToRightAndThereIsBlockAboveTarget() {
    String levelString = "-level test\n" +
            "X__XX\n" +
            "XRB_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X__XX\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToUpLeftWall() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XXBLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "XB__X\n" +
            "XXL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToUpRightWall() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XRBXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X__BX\n" +
            "X_RXX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToUpLeftBlock() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XBBLX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "XB__X\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToUpRightBlock() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XRBBX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X__BX\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToLeftAndFall() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_BLX\n" +
            "X_XXX\n" +
            "X_XXX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveLeft());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X___X\n" +
            "X_L_X\n" +
            "X_XXX\n" +
            "XBXXX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  @Test
  public void putDownReturnsTrueWhenCanPutDownToRightAndFall() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XRB_X\n" +
            "XXX_X\n" +
            "XXX_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertTrue(model.pickUpOrPutDown());
    assertTrue(model.moveRight());
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedResultString = "-level test\n" +
            "X___X\n" +
            "X_R_X\n" +
            "XXX_X\n" +
            "XXXBX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(expectedResultLayout, finalLayout));
  }

  // picking block up

  @Test
  public void pickUpReturnsFalseWhenNotHoldingBlockAndNoBlockToPickUpFacingLeft() {
    String levelString = "-level test\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenNotHoldingBlockAndNoBlockToPickUpFacingRight() {
    String levelString = "-level test\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenCouldPickUpBlockButPieceAbovePlayerFacingLeft() {
    String levelString = "-level test\n" +
            "X_X_X\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenCouldPickUpBlockButPieceAbovePlayerFacingRight() {
    String levelString = "-level test\n" +
            "X_X_X\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenCouldPickUpBlockButInTopRowFacingLeft() {
    String levelString = "-level test\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenCouldPickUpBlockButInTopRowFacingRight() {
    String levelString = "-level test\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenCouldPickUpBlockOnRightButFacingLeft() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_LBX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenCouldPickUpBlockOnLeftButFacingRight() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XBR_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenFacingStackOfBlocksToLeft() {
    String levelString = "-level test\n" +
            "XB__X\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsFalseWhenFacingStackOfBlocksToRight() {
    String levelString = "-level test\n" +
            "X__BX\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.pickUpOrPutDown());
    List<List<GamePiece>> newLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, newLayout));
  }

  @Test
  public void pickUpReturnsTrueWhenCanPickUpFromLeft() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "XBL_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));

    String expectedResultString = "-level test\n" +
            "X_B_X\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  @Test
  public void pickUpReturnsTrueWhenCanPickUpFromRight() {
    String levelString = "-level test\n" +
            "X___X\n" +
            "X_RBX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertTrue(model.pickUpOrPutDown());
    List<List<GamePiece>> layoutAfterMove = model.layoutToRender();
    assertFalse(layoutsAreSame(originalLayout, layoutAfterMove));

    String expectedResultString = "-level test\n" +
            "X_B_X\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedResult = levelFromString(expectedResultString);
    List<List<GamePiece>> expectedResultLayout = expectedResult.layout();
    assertTrue(layoutsAreSame(layoutAfterMove, expectedResultLayout));
  }

  /* layoutToRender() Tests --------------------------------------------------------------------- */

  // Tests for layoutToRender() are not as intensive as those for other methods as it is effectively
  // being tested in every test that requires checking the board layout, which is many of them.
  // Writing extensive tests for layoutToRender() would be just re-writing tests for other methods.

  @Test(expected = RuntimeException.class)
  public void layoutToRenderThrowsREWhenNoLevelLoaded() {
    model.layoutToRender();
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAtLevelStartAndRestart() {
    String firstLevelString = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevel = levelFromString(firstLevelString);
    List<List<GamePiece>> firstLevelLayout = firstLevel.layout();
    model.loadLevel(firstLevel);
    assertTrue(layoutsAreSame(firstLevelLayout, model.layoutToRender()));
    model.restartLevel();
    assertTrue(layoutsAreSame(firstLevelLayout, model.layoutToRender()));

    String secondLevelString = "-level ARo\n" +
            "_X____XX________XX____\n" +
            "_X________________X___\n" +
            "XX_________________X__\n" +
            "XD__________________X_\n" +
            "XX___________________X\n" +
            "_X___________X__B____X\n" +
            "_X___________XB_BBR__X\n" +
            "_XXXXX___XXXXXXXXXXXXX\n" +
            "_____X__BX____________\n" +
            "_____XXXXX____________\n" +
            "-/level";
    Level secondLevel = levelFromString(secondLevelString);
    List<List<GamePiece>> secondLevelLayout = secondLevel.layout();
    model.loadLevel(secondLevel);
    assertTrue(layoutsAreSame(secondLevelLayout, model.layoutToRender()));
    model.restartLevel();
    assertTrue(layoutsAreSame(secondLevelLayout, model.layoutToRender()));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterMovesMadeAndRestarted() {
    String firstLevelString = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevel = levelFromString(firstLevelString);
    List<List<GamePiece>> firstLevelLayout = firstLevel.layout();
    model.loadLevel(firstLevel);

    assertTrue(layoutsAreSame(firstLevelLayout, model.layoutToRender()));
    model.moveLeft();
    model.pickUpOrPutDown();
    model.moveLeft();
    model.pickUpOrPutDown();
    model.moveUp();
    model.moveUp();
    List<List<GamePiece>> layoutAfterMoving = model.layoutToRender();

    String expectedLayoutString = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___________L______X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_XB_____X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(layoutAfterMoving, expectedLayout));
    model.restartLevel();
    assertTrue(layoutsAreSame(firstLevelLayout, model.layoutToRender()));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterMovingLeftFacingLeft() {
    String levelString = "-level test\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    model.moveLeft();
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedLayoutString = "-level test\n" +
            "XL__X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(expectedLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterMovingLeftFacingRight() {
    String levelString = "-level test\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    model.moveLeft();
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedLayoutString = "-level test\n" +
            "XL__X\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(expectedLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterMovingRightFacingLeft() {
    String levelString = "-level test\n" +
            "X_L_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    model.moveRight();
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedLayoutString = "-level test\n" +
            "X__RX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(expectedLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterMovingRightFacingRight() {
    String levelString = "-level test\n" +
            "X_R_X\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    model.moveRight();
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedLayoutString = "-level test\n" +
            "X__RX\n" +
            "XXXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(expectedLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterFailingToMoveLeft() {
    String levelString = "-level test\n" +
            "XL_X\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveLeft());
    List<List<GamePiece>> finalLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterFailingToMoveRight() {
    String levelString = "-level test\n" +
            "X_RX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    List<List<GamePiece>> originalLayout = model.layoutToRender();
    assertFalse(model.moveRight());
    List<List<GamePiece>> finalLayout = model.layoutToRender();
    assertTrue(layoutsAreSame(originalLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterFacingLeft() {
    String levelString = "-level test\n" +
            "X_RX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    model.moveLeft();
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedLayoutString = "-level test\n" +
            "XL_X\n" +
            "XXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(expectedLayout, finalLayout));
  }

  @Test
  public void layoutToRenderReturnsCorrectLayoutAfterFacingRight() {
    String levelString = "-level test\n" +
            "XL_X\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    model.moveRight();
    List<List<GamePiece>> finalLayout = model.layoutToRender();

    String expectedLayoutString = "-level test\n" +
            "X_RX\n" +
            "XXXX\n" +
            "-/level";
    Level expectedLayoutLevel = levelFromString(expectedLayoutString);
    List<List<GamePiece>> expectedLayout = expectedLayoutLevel.layout();
    assertTrue(layoutsAreSame(expectedLayout, finalLayout));
  }

  /* isLevelCompleted() Tests ------------------------------------------------------------------- */

  @Test(expected = RuntimeException.class)
  public void isLevelCompletedThrowsREWhenNoLevelLoaded() {
    model.isLevelCompleted();
  }

  @Test
  public void isLevelCompleteReturnsFalseAtStartAndAfterRestarting() {
    model.loadLevel(levels.currentLevel());
    assertFalse(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsFalseAfterMakingNonWinningMoves() {
    String levelString = "-level test\n" +
            "X_____X\n" +
            "XD_B_LX\n" +
            "XXXXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveUp());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveRight());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.pickUpOrPutDown());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveRight());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.pickUpOrPutDown());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertFalse(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsTrueAfterReachingDoorFromLeftAndFalseAfterRestarting() {
    String levelString = "-level test\n" +
            "XD_LX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertTrue(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsTrueAfterReachingDoorFromRightAndFalseAfterRestarting() {
    String levelString = "-level test\n" +
            "XR_DX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveRight());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveRight());
    assertTrue(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsTrueAfterReachingDoorFromUpLeftAndFalseAfterRestarting() {
    String levelString = "-level test\n" +
            "XD__X\n" +
            "XX_LX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveUp());
    assertTrue(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsTrueAfterReachingDoorFromUpRightAndFalseAfterRestarting() {
    String levelString = "-level test\n" +
            "X__DX\n" +
            "XR_XX\n" +
            "XXXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveRight());
    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveUp());
    assertTrue(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsTrueAfterReachingDoorFromFallingLeftAndFalseAfterRestarting() {
    String levelString = "-level test\n" +
            "X_LX\n" +
            "X_XX\n" +
            "XDXX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveLeft());
    assertTrue(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  @Test
  public void isLevelCompleteReturnsTrueAfterReachingDoorFromFallingRightAndFalseAfterRestarting() {
    String levelString = "-level test\n" +
            "XR_X\n" +
            "XX_X\n" +
            "XXDX\n" +
            "XXXX\n" +
            "-/level";
    Level level = levelFromString(levelString);
    model.loadLevel(level);

    assertFalse(model.isLevelCompleted());
    assertTrue(model.moveRight());
    assertTrue(model.isLevelCompleted());
    model.restartLevel();
    assertFalse(model.isLevelCompleted());
  }

  /* Private helper methods --------------------------------------------------------------------- */

  /**
   * Determines whether two given layouts are identical.
   *
   * @param lo1 first layout to compare
   * @param lo2 second layout to compare
   * @return true if layouts are exactly the same, false otherwise
   */
  private static boolean layoutsAreSame(List<List<GamePiece>> lo1, List<List<GamePiece>> lo2) {
    if (lo1.size() != lo2.size()) return false;

    for (int row = 0; row < lo1.size(); row++) {
      List<GamePiece> row1 = lo1.get(row);
      List<GamePiece> row2 = lo2.get(row);
      if (row1.size() != row2.size()) return false;

      for (int col = 0; col < row1.size(); col++) {
        GamePiece piece1 = row1.get(col);
        GamePiece piece2 = row2.get(col);
        if (piece1 != piece2) return false;
      }
    }

    return true;
  }

  /**
   * Parses and returns a Level from the given String.
   *
   * @param levelString String describing Level
   * @return Level parsed from given String
   * @throws IllegalStateException if given String could not be parsed as Level
   */
  private static Level levelFromString(String levelString) throws IllegalStateException {
    StringReader stringReader = new StringReader(levelString);
    LevelSet levels = LevelSetFileReader.parseLevelSetFile(stringReader);
    return levels.currentLevel();
  }

  /* Helper method tests ------------------------------------------------------------------------ */

  // These tests are intentionally not as intensive as those for the actual Block Dude program,
  // since I am the only one who will be using these methods; therefore, it is pointless to test
  // what would happen if, say, levelFromString(...) is passed null or "", as I will not do that.
  // The purpose of these tests is just for my sanity to make sure that they work as intended.

  // layoutsAreSame(...) tests

  @Test
  public void layoutsAreSameReturnsTrueWhenSame() {
    String firstLevelString = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevelFromString = levelFromString(firstLevelString);
    Level firstLevel = levels.currentLevel();
    assertTrue(layoutsAreSame(firstLevel.layout(), firstLevel.layout()));
    assertTrue(layoutsAreSame(firstLevelFromString.layout(), firstLevel.layout()));

    String secondLevelString = "-level ARo\n" +
            "_X____XX________XX____\n" +
            "_X________________X___\n" +
            "XX_________________X__\n" +
            "XD__________________X_\n" +
            "XX___________________X\n" +
            "_X___________X__B____X\n" +
            "_X___________XB_BBR__X\n" +
            "_XXXXX___XXXXXXXXXXXXX\n" +
            "_____X__BX____________\n" +
            "_____XXXXX____________\n" +
            "-/level";
    Level secondLevelFromString = levelFromString(secondLevelString);
    Level secondLevel = levels.nextLevel();
    assertTrue(layoutsAreSame(secondLevel.layout(), secondLevel.layout()));
    assertTrue(layoutsAreSame(secondLevelFromString.layout(), secondLevel.layout()));
  }

  @Test
  public void layoutsAreSameReturnsFalseWhenCompletelyDifferent() {
    Level firstLevel = levels.currentLevel();
    Level secondLevel = levels.nextLevel();
    assertFalse(layoutsAreSame(firstLevel.layout(), secondLevel.layout()));
  }

  @Test
  public void layoutsAreSameReturnsFalseWhenDifferentSize() {
    String firstLevelMissingRowsString = "-level tcP\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevelMissingRows = levelFromString(firstLevelMissingRowsString);
    Level firstLevel = levels.currentLevel();
    assertFalse(layoutsAreSame(firstLevel.layout(), firstLevelMissingRows.layout()));
  }

  @Test
  public void layoutsAreSameReturnsFalseWhenSameSizeButDifferentContents() {
    String firstLevelDifContentsString = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD_XX___X_B_X_B_L__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevelDifContents = levelFromString(firstLevelDifContentsString);
    Level firstLevel = levels.currentLevel();
    assertFalse(layoutsAreSame(firstLevel.layout(), firstLevelDifContents.layout()));
  }

  // levelFromString(...) tests

  @Test(expected = IllegalStateException.class)
  public void levelFromStringThrowsISEWhenStringContainsMoreThanOnePlayer() {
    String firstLevelWithSecondPlayer = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___________L______X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    levelFromString(firstLevelWithSecondPlayer);
  }

  @Test(expected = IllegalStateException.class)
  public void levelFromStringThrowsISEWhenStringContainsNoPlayer() {
    String firstLevelWithoutPlayer = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B____X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    levelFromString(firstLevelWithoutPlayer);
  }

  @Test
  public void levelFromStringReturnsCorrectLevel() {
    String firstLevelString = "-level tcP\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevelFromString = levelFromString(firstLevelString);
    Level firstLevel = levels.currentLevel();
    layoutsAreSame(firstLevel.layout(), firstLevelFromString.layout());

    String secondLevelString = "-level ARo\n" +
            "_X____XX________XX____\n" +
            "_X________________X___\n" +
            "XX_________________X__\n" +
            "XD__________________X_\n" +
            "XX___________________X\n" +
            "_X___________X__B____X\n" +
            "_X___________XB_BBR__X\n" +
            "_XXXXX___XXXXXXXXXXXXX\n" +
            "_____X__BX____________\n" +
            "_____XXXXX____________\n" +
            "-/level";
    Level secondLevelFromString = levelFromString(secondLevelString);
    Level secondLevel = levels.nextLevel();
    layoutsAreSame(secondLevel.layout(), secondLevelFromString.layout());
  }

}
