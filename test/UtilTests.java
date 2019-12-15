import org.junit.Test;

import blockdude.util.GamePiece;
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
  /* GamePiece Tests ---------------------------------------------------------------------------- */

  // Examples for use in tests
  private final GamePiece block = GamePiece.BLOCK;
  private final GamePiece door = GamePiece.DOOR;
  private final GamePiece empty = GamePiece.EMPTY;
  private final GamePiece wall = GamePiece.WALL;
  private final GamePiece leftPlayer = GamePiece.PLAYER_LEFT;
  private final GamePiece rightPlayer = GamePiece.PLAYER_RIGHT;

  /**
   * Tests that canPickUp(...) returns true when given a piece that can be picked up, and false when
   * given a piece that cannot be picked up or when given null.
   */
  @Test
  public void gamePieceCanPickUpWorks() {
    GamePiece[] piecesThatCanBePickedUp = {block};
    for (GamePiece piece : piecesThatCanBePickedUp) assertTrue(GamePiece.canPickUp(piece));

    GamePiece[] piecesThatCannotBePickedUp = {door, empty, wall, leftPlayer, rightPlayer};
    for (GamePiece piece : piecesThatCannotBePickedUp) assertFalse(GamePiece.canPickUp(piece));

    assertFalse(GamePiece.canPickUp(null));
  }

  /**
   * Tests that isPlayer(...) returns true when given a piece that is a player, and false when given
   * a piece that is not a player or when given null.
   */
  @Test
  public void gamePieceIsPlayerWorks() {
    GamePiece[] piecesThatArePlayers = {leftPlayer, rightPlayer};
    for (GamePiece piece : piecesThatArePlayers) assertTrue(GamePiece.isPlayer(piece));

    GamePiece[] piecesThatAreNotPlayers = {door, empty, wall, block};
    for (GamePiece piece : piecesThatAreNotPlayers) assertFalse(GamePiece.isPlayer(piece));

    assertFalse(GamePiece.isPlayer(null));
  }

  /**
   * Tests that isSolid(...) returns true when given a piece that is solid, and false when given a
   * piece that is not solid or when given null.
   */
  @Test
  public void gamePieceIsSolidWorks() {
    GamePiece[] piecesThatAreSolid = {leftPlayer, rightPlayer, wall, block};
    for (GamePiece piece : piecesThatAreSolid) assertTrue(GamePiece.isSolid(piece));

    GamePiece[] piecesThatAreNotSolid = {door, empty};
    for (GamePiece piece : piecesThatAreNotSolid) assertFalse(GamePiece.isSolid(piece));

    assertFalse(GamePiece.isSolid(null));
  }

  /* Level Tests -------------------------------------------------------------------------------- */

  /**
   * Tests that a Level can be built correctly.
   */
  @Test
  public void buildingLevelWorks() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level with a password that is
   * either null or empty.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelThrowsWhenPasswordNullOrEmpty() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level that did not have any rows
   * or game pieces added to it.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelThrowsWhenLayoutEmpty() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level that does not have a
   * rectangular layout (not all the rows are the same length).
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelThrowsWhenLayoutIsNotRectangle() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level with no player.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelThrowsWhenNoPlayerDefined() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level with more than one player.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelThrowsWhenMoreThanOnePlayerDefined() {
    // todo impl
  }

  /**
   * Tests that password() returns the correct password for the level.
   */
  @Test
  public void levelPasswordWorks() {
    // todo impl
  }

  /**
   * Tests that layout() returns the correct layout for the initial state of the level.
   */
  @Test
  public void levelLayoutReturnsCorrectValue() {
    // todo impl
  }

  /**
   * Tests that the level layout cannot be mutated via the object output by layout().
   */
  @Test
  public void levelLayoutCannotBeMutated() {
    // todo impl
  }

  /**
   * Tests that player() returns the correct player for the initial state of the level.
   */
  @Test
  public void levelPlayerWorks() {
    // todo impl
  }

  /**
   * Tests that playerPosition() returns the correct position for the initial state of the level.
   */
  @Test
  public void levelPlayerPositionReturnsCorrectValue() {
    // todo impl
  }

  /**
   * Tests that the player position cannot be mutated via the object output by playerPosition().
   */
  @Test
  public void levelPlayerPositionCannotBeMutated() {
    // todo impl
  }

  /* LevelSet Tests ----------------------------------------------------------------------------- */

  /**
   * Tests that a LevelSet can be built correctly.
   */
  @Test
  public void buildingLevelSetWorks() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level set without any levels.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelSetThrowsWhenNoLevelsAdded() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level set with a null level.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelSetThrowsWhenNullLevelAdded() {
    // todo impl
  }

  /**
   * Tests that an IllegalStateException is thrown when building a level set that has multiple
   * levels with the same password.
   */
  @Test (expected = IllegalStateException.class)
  public void buildingLevelSetThrowsWhenLevelsWithSamePasswordAreAdded() {
    // todo impl
  }

  /**
   * Tests that currentLevel() returns the correct level for the state of the level set.
   */
  @Test
  public void levelSetCurrentLevelWorks() {
    // todo
  }

  /**
   * Tests that currentLevelIndex() returns the correct index for the state of the level set.
   */
  @Test
  public void levelSetCurrentLevelIndexWorks() {
    // todo
  }

  /**
   * Tests that nextLevel() returns the correct next level from the level set.
   */
  @Test
  public void levelSetNextLevelReturnsCorrectLevel() {
    // todo
  }

  /**
   * Tests that nextLevel() changes the current level of the level set.
   */
  @Test
  public void levelSetNextLevelChangesCurrentLevelCorrectly() {
    // todo
  }

  /**
   * Tests that restart() sets the current level index to 0.
   */
  @Test
  public void levelSetRestartSetsLevelIndexToZero() {
    // todo
  }

  /**
   * Tests that restart() sets the current level to be the first level of this level set.
   */
  @Test
  public void levelSetRestartSetsCurrentLevelToFirstLevel() {
    // todo
  }

  /**
   * Tests that tryPassword(...) returns the correct level for the given password.
   */
  @Test
  public void levelSetTryPasswordReturnsCorrectLevel() {
    // todo
  }

  /**
   * Tests that tryPassword(...) sets the level index correctly for the given password.
   */
  @Test
  public void levelSetTryPasswordSetsLevelIndexCorrectly() {
    // todo
  }

  /**
   * Tests that tryPassword(...) sets the current level correctly for the given password.
   */
  @Test
  public void levelSetTryPasswordSetsCurrentLevelCorrectly() {
    // todo
  }

  /**
   * Tests that tryPassword(...) returns null when given an incorrect password.
   */
  @Test
  public void levelSetTryPasswordThatDoesNotExistReturnsNull() {
    // todo
  }

  /* LevelSetFileReader Tests ------------------------------------------------------------------- */

  /**
   * TODO
   */
  @Test
  public void levelSetFileReaderParsesFileWithOneLevelCorrectly() {
    // todo
  }

  /**
   * TODO
   */
  @Test
  public void levelSetFileReaderParsesFileWithMoreThanOneLevelCorrectly() {
    // todo
  }

  /**
   * TODO
   */
  @Test (expected = IllegalArgumentException.class)
  public void levelSetFileReaderThrowsIAEForNullArgument() {
    // todo
  }

  /**
   * TODO
   */
  @Test (expected = IllegalStateException.class)
  public void levelSetFileReaderThrowsISEForEmptyFile() {
    // todo
  }

  /**
   * TODO
   */
  @Test (expected = IllegalStateException.class)
  public void levelSetFileReaderThrowsISEForFileWithIncorrectSyntax() {
    // todo
  }

  /**
   * TODO
   */
  @Test (expected = IllegalStateException.class)
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

  /**
   * Tests that a Position can be correctly constructed.
   */
  @Test
  public void makePositionWorks() {
    Position pos = new Position(1, 2);
    assertNotNull(pos);
    assertEquals(1, pos.col);
    assertEquals(2, pos.row);
  }

  /**
   * Tests that equals(...) returns true only when two positions have the same row and col values.
   */
  @Test
  public void positionEqualsWorks() {
    // equal when same objects
    assertEquals(pos0x0, pos0x0); // zero
    assertEquals(pos5x5, pos5x5); // row == col
    assertEquals(pos5x7, pos5x7); // row != col
    assertEquals(posMAXxMAX, posMAXxMAX); // large

    // equal when col same, row same
    assertEquals(pos5x3_1, pos5x3_2);

    // not equal when col dif, row same
    assertNotEquals(posPOSxNEG, posNEGxNEG);

    // not equal when col same, row dif
    assertNotEquals(pos5x5, pos5x7);

    // not equal when col and row dif
    assertNotEquals(pos0x0, pos5x7);

    // not equal when col and row flipped
    assertNotEquals(pos5x7, pos7x5);

    // not equal to null
    assertNotEquals(null, pos0x0);
    assertNotEquals(null, pos5x5);
    assertNotEquals(null, pos5x7);
  }

  /**
   * Tests that hashCode() returns the same value when called on equal objects.
   */
  @Test
  public void positionHashCodeWorks() {
    // same objects
    assertEquals(pos0x0.hashCode(), pos0x0.hashCode());
    assertEquals(pos5x5.hashCode(), pos5x5.hashCode());
    assertEquals(posMAXxMAX.hashCode(), posMAXxMAX.hashCode());

    // different objects, but equal
    assertNotSame(pos5x3_1, pos5x3_2);
    assertEquals(pos5x3_1, pos5x3_2);
    assertEquals(pos5x3_1.hashCode(), pos5x3_2.hashCode());
  }

  /**
   * Tests that toString() returns a String in the format "(col,row)".
   */
  @Test
  public void positionToStringWorks() {
    // zero
    assertEquals("(0,0)", pos0x0.toString());

    // positive
    assertEquals("(5,7)", pos5x7.toString());

    // negative
    assertEquals("(-4,4)", posNEGxPOS.toString());

    // max
    String maxPosExpected = "(" + Integer.MAX_VALUE + "," + Integer.MAX_VALUE + ")";
    assertEquals(maxPosExpected, posMAXxMAX.toString());
  }

  /**
   * Tests that copy() returns a different position object that has the same row and col.
   */
  @Test
  public void positionCopyWorks() {
    // zero
    Position pos0x0Copy = pos0x0.copy();
    assertNotSame(pos0x0, pos0x0Copy);
    assertEquals(pos0x0, pos0x0Copy);

    // positive
    Position pos5x7Copy = pos5x7.copy();
    assertNotSame(pos5x7, pos5x7Copy);
    assertEquals(pos5x7, pos5x7Copy);

    // negative
    Position posNEGxNEGCopy = posNEGxNEG.copy();
    assertNotSame(posNEGxNEG, posNEGxNEGCopy);
    assertEquals(posNEGxNEG, posNEGxNEGCopy);

    // max
    Position posMAXxMAXCopy = posMAXxMAX.copy();
    assertNotSame(posMAXxMAX, posMAXxMAXCopy);
    assertEquals(posMAXxMAX, posMAXxMAXCopy);
  }
}
