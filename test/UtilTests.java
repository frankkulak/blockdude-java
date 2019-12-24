import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import blockdude.util.GamePiece;
import blockdude.util.Level;
import blockdude.util.LevelSet;
import blockdude.util.LevelSetReader;
import blockdude.util.Position;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * A class for testing members of the util package.
 */
public class UtilTests {
  private static LevelSet levels;

  @BeforeClass
  public static void onlyOnce() {
    try {
      String filename = "levelSources/levels.txt";
      levels = LevelSetReader.parseLevelSet(new FileReader(filename));
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File name is incorrect.");
    }
  }

  @Before
  public void setUp() {
    levels.restart();
  }

  /* GamePiece Tests ---------------------------------------------------------------------------- */

  // Examples for use in tests
  private final GamePiece block = GamePiece.BLOCK;
  private final GamePiece door = GamePiece.DOOR;
  private final GamePiece empty = GamePiece.EMPTY;
  private final GamePiece wall = GamePiece.WALL;
  private final GamePiece leftPlayer = GamePiece.PLAYER_LEFT;
  private final GamePiece rightPlayer = GamePiece.PLAYER_RIGHT;

  @Test
  public void gamePieceCanPickUpWorks() {
    GamePiece[] piecesThatCanBePickedUp = {block};
    for (GamePiece piece : piecesThatCanBePickedUp) assertTrue(GamePiece.canPickUp(piece));

    GamePiece[] piecesThatCannotBePickedUp = {door, empty, wall, leftPlayer, rightPlayer};
    for (GamePiece piece : piecesThatCannotBePickedUp) assertFalse(GamePiece.canPickUp(piece));

    assertFalse(GamePiece.canPickUp(null));
  }

  @Test
  public void gamePieceIsPlayerWorks() {
    GamePiece[] piecesThatArePlayers = {leftPlayer, rightPlayer};
    for (GamePiece piece : piecesThatArePlayers) assertTrue(GamePiece.isPlayer(piece));

    GamePiece[] piecesThatAreNotPlayers = {door, empty, wall, block};
    for (GamePiece piece : piecesThatAreNotPlayers) assertFalse(GamePiece.isPlayer(piece));

    assertFalse(GamePiece.isPlayer(null));
  }

  @Test
  public void gamePieceIsSolidWorks() {
    GamePiece[] piecesThatAreSolid = {leftPlayer, rightPlayer, wall, block};
    for (GamePiece piece : piecesThatAreSolid) assertTrue(GamePiece.isSolid(piece));

    GamePiece[] piecesThatAreNotSolid = {door, empty};
    for (GamePiece piece : piecesThatAreNotSolid) assertFalse(GamePiece.isSolid(piece));

    assertFalse(GamePiece.isSolid(null));
  }

  /* Level Tests -------------------------------------------------------------------------------- */

  // There are no tests for Level.Builder in this section since it is not public; it is tested as
  // part of the tests for LevelSetReader, since LevelSetReader depends on Level.Builder

  @Test
  public void levelPasswordReturnsCorrectValue() {
    Level firstLevel = levels.currentLevel();
    assertEquals("tcP", firstLevel.password());
    Level secondLevel = levels.nextLevel();
    assertEquals("ARo", secondLevel.password());
    Level lastLevel = levels.tryPassword("wTF");
    assertEquals("wTF", lastLevel.password());
  }

  @Test
  public void levelLayoutReturnsCorrectValue() {
    Level level = levels.currentLevel();
    List<List<GamePiece>> layout = level.layout();

    for (int rowIndex = 0; rowIndex < layout.size(); rowIndex++) {
      List<GamePiece> row = layout.get(rowIndex);
      for (int colIndex = 0; colIndex < row.size(); colIndex++) {
        GamePiece gp = layout.get(rowIndex).get(colIndex);
        if (colIndex == 0 || colIndex == row.size() - 1) {
          assertEquals(GamePiece.WALL, gp);
        } else if (rowIndex < 3) {
          assertEquals(GamePiece.EMPTY, gp);
        } else if (rowIndex == 3) {
          if (colIndex == 4 || colIndex == 12) {
            assertEquals(GamePiece.WALL, gp);
          } else {
            assertEquals(GamePiece.EMPTY, gp);
          }
        } else if (rowIndex == 4) {
          if (colIndex == 1) {
            assertEquals(GamePiece.DOOR, gp);
          } else if (colIndex == 4 || colIndex == 8 || colIndex == 12) {
            assertEquals(GamePiece.WALL, gp);
          } else if (colIndex == 10 || colIndex == 14) {
            assertEquals(GamePiece.BLOCK, gp);
          } else if (colIndex == 16) {
            assertEquals(GamePiece.PLAYER_RIGHT, gp);
          } else {
            assertEquals(GamePiece.EMPTY, gp);
          }
        } else {
          assertEquals(GamePiece.WALL, gp);
        }
      }
    }
  }

  @Test
  public void levelLayoutCannotBeMutated() {
    Level level = levels.currentLevel();
    List<List<GamePiece>> layout = level.layout();
    assertEquals(layout.get(0).get(0), level.layout().get(0).get(0));
    layout.get(0).set(0, GamePiece.BLOCK);
    assertNotEquals(layout.get(0).get(0), level.layout().get(0).get(0));
  }

  @Test
  public void levelPlayerReturnsCorrectValue() {
    Level level = levels.currentLevel();
    assertEquals(GamePiece.PLAYER_RIGHT, level.player());
    // no levels start with PLAYER_LEFT
  }

  @Test
  public void levelPlayerPositionReturnsCorrectValue() {
    Level level = levels.currentLevel();
    assertEquals(new Position(16, 4), level.playerPosition());
    Level nextLevel = levels.nextLevel();
    assertEquals(new Position(18, 6), nextLevel.playerPosition());
  }

  @Test
  public void levelPlayerPositionCannotBeMutated() {
    Level level = levels.currentLevel();
    Position playerPosition = level.playerPosition();
    assertEquals(new Position(16, 4), playerPosition);
    playerPosition.row = 5;
    assertEquals(new Position(16, 5), playerPosition);
    assertEquals(new Position(16, 4), level.playerPosition());
    playerPosition.col = 5;
    assertEquals(new Position(5, 5), playerPosition);
    assertEquals(new Position(16, 4), level.playerPosition());
  }

  /* LevelSet Tests ----------------------------------------------------------------------------- */

  // There are no tests for LevelSet.Builder in this section since it is not public; it is tested as
  // part of the tests for LevelSetReader, since LevelSetReader depends on LevelSet.Builder
  
  @Test
  public void levelSetCurrentLevelWorks() {
    // todo
  }

  @Test
  public void levelSetCurrentLevelIndexWorks() {
    // todo
  }

  @Test
  public void levelSetNextLevelReturnsCorrectLevel() {
    // todo
  }

  @Test
  public void levelSetNextLevelChangesCurrentLevelCorrectly() {
    // todo
  }

  @Test
  public void levelSetRestartSetsLevelIndexToZero() {
    // todo
  }

  @Test
  public void levelSetRestartSetsCurrentLevelToFirstLevel() {
    // todo
  }

  @Test
  public void levelSetTryPasswordReturnsCorrectLevel() {
    // todo
  }

  @Test
  public void levelSetTryPasswordSetsLevelIndexCorrectly() {
    // todo
  }

  @Test
  public void levelSetTryPasswordSetsCurrentLevelCorrectly() {
    // todo
  }

  @Test
  public void levelSetTryPasswordThatDoesNotExistReturnsNull() {
    // todo
  }

  /* LevelSetReader Tests ------------------------------------------------------------------- */

  @Test
  public void levelSetFileReaderParsesFileWithOneLevelCorrectly() {
    // todo
  }

  @Test
  public void levelSetFileReaderParsesFileWithMoreThanOneLevelCorrectly() {
    // todo
  }

  @Test(expected = IllegalArgumentException.class)
  public void levelSetFileReaderThrowsIAEForNullArgument() {
    // todo
  }

  @Test(expected = IllegalStateException.class)
  public void levelSetFileReaderThrowsISEForEmptyFile() {
    // todo
  }

  @Test(expected = IllegalStateException.class)
  public void levelSetFileReaderThrowsISEForFileWithIncorrectSyntax() {
    // todo
  }

  @Test(expected = IllegalStateException.class)
  public void levelSetFileReaderThrowsISEForFileWithIncorrectLevelFormat() {
    // todo repeat tests for level builder
  }

  /* Position Tests ----------------------------------------------------------------------------- */

  // Examples for use in tests
  private final Position pos0x0 = new Position(0, 0);
  private final Position pos5x5 = new Position(5, 5);
  private final Position pos5x7 = new Position(5, 7);
  private final Position pos7x5 = new Position(7, 5);
  private final Position pos5x3_1 = new Position(5, 3);
  private final Position pos5x3_2 = new Position(5, 3);
  private final Position posMAXxMAX = new Position(Integer.MAX_VALUE, Integer.MAX_VALUE);
  private final Position posNEGxPOS = new Position(-4, 4);
  private final Position posPOSxNEG = new Position(4, -4);
  private final Position posNEGxNEG = new Position(-4, -4);

  @Test
  public void makePositionWorks() {
    Position pos = new Position(1, 2);
    assertNotNull(pos);
    assertEquals(1, pos.col);
    assertEquals(2, pos.row);
  }

  @Test
  public void positionEqualsWorks() {
    assertEquals(pos0x0, pos0x0); // zero
    assertEquals(pos5x5, pos5x5); // row == col
    assertEquals(pos5x7, pos5x7); // row != col
    assertEquals(posMAXxMAX, posMAXxMAX); // large
    assertEquals(pos5x3_1, pos5x3_2);
    assertNotEquals(posPOSxNEG, posNEGxNEG);
    assertNotEquals(pos5x5, pos5x7);
    assertNotEquals(pos0x0, pos5x7);
    assertNotEquals(pos5x7, pos7x5);
    assertNotEquals(null, pos0x0);
    assertNotEquals(null, pos5x5);
    assertNotEquals(null, pos5x7);
  }

  @Test
  public void positionHashCodeWorks() {
    assertEquals(pos0x0.hashCode(), pos0x0.hashCode());
    assertEquals(pos5x5.hashCode(), pos5x5.hashCode());
    assertEquals(posMAXxMAX.hashCode(), posMAXxMAX.hashCode());
    assertNotSame(pos5x3_1, pos5x3_2);
    assertEquals(pos5x3_1, pos5x3_2);
    assertEquals(pos5x3_1.hashCode(), pos5x3_2.hashCode());
  }

  @Test
  public void positionToStringWorks() {
    assertEquals("(0,0)", pos0x0.toString());
    assertEquals("(5,7)", pos5x7.toString());
    assertEquals("(-4,4)", posNEGxPOS.toString());
    String maxPosExpected = "(" + Integer.MAX_VALUE + "," + Integer.MAX_VALUE + ")";
    assertEquals(maxPosExpected, posMAXxMAX.toString());
  }

  @Test
  public void positionCopyWorks() {
    Position pos0x0Copy = pos0x0.copy();
    assertNotSame(pos0x0, pos0x0Copy);
    assertEquals(pos0x0, pos0x0Copy);
    Position pos5x7Copy = pos5x7.copy();
    assertNotSame(pos5x7, pos5x7Copy);
    assertEquals(pos5x7, pos5x7Copy);
    Position posNEGxNEGCopy = posNEGxNEG.copy();
    assertNotSame(posNEGxNEG, posNEGxNEGCopy);
    assertEquals(posNEGxNEG, posNEGxNEGCopy);
    Position posMAXxMAXCopy = posMAXxMAX.copy();
    assertNotSame(posMAXxMAX, posMAXxMAXCopy);
    assertEquals(posMAXxMAX, posMAXxMAXCopy);
  }
}
