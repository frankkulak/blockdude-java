import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;

import blockdude.util.Level;
import blockdude.util.LevelSet;
import blockdude.util.LevelSetReader;
import util.TestUtil;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A class for testing the TestUtil class.
 */
public class TestUtilTests {
  private static LevelSet levels;

  /* JUnit Setup -------------------------------------------------------------------------------- */

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

  /* Tests -------------------------------------------------------------------------------------- */

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
    Level firstLevelFromString = TestUtil.levelFromString(firstLevelString);
    Level firstLevel = levels.currentLevel();
    assertTrue(TestUtil.layoutsAreSame(firstLevel.layout(), firstLevel.layout()));
    assertTrue(TestUtil.layoutsAreSame(firstLevelFromString.layout(), firstLevel.layout()));

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
    Level secondLevelFromString = TestUtil.levelFromString(secondLevelString);
    Level secondLevel = levels.nextLevel();
    assertTrue(TestUtil.layoutsAreSame(secondLevel.layout(), secondLevel.layout()));
    assertTrue(TestUtil.layoutsAreSame(secondLevelFromString.layout(), secondLevel.layout()));
  }

  @Test
  public void layoutsAreSameReturnsFalseWhenCompletelyDifferent() {
    Level firstLevel = levels.currentLevel();
    Level secondLevel = levels.nextLevel();
    assertFalse(TestUtil.layoutsAreSame(firstLevel.layout(), secondLevel.layout()));
  }

  @Test
  public void layoutsAreSameReturnsFalseWhenDifferentSize() {
    String firstLevelMissingRowsString = "-level tcP\n" +
            "X__________________X\n" +
            "X___X_______X______X\n" +
            "XD__X___X_B_X_B_R__X\n" +
            "XXXXXXXXXXXXXXXXXXXX\n" +
            "-/level";
    Level firstLevelMissingRows = TestUtil.levelFromString(firstLevelMissingRowsString);
    Level firstLevel = levels.currentLevel();
    assertFalse(TestUtil.layoutsAreSame(firstLevel.layout(), firstLevelMissingRows.layout()));
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
    Level firstLevelDifContents = TestUtil.levelFromString(firstLevelDifContentsString);
    Level firstLevel = levels.currentLevel();
    assertFalse(TestUtil.layoutsAreSame(firstLevel.layout(), firstLevelDifContents.layout()));
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
    TestUtil.levelFromString(firstLevelWithSecondPlayer);
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
    TestUtil.levelFromString(firstLevelWithoutPlayer);
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
    Level firstLevelFromString = TestUtil.levelFromString(firstLevelString);
    Level firstLevel = levels.currentLevel();
    TestUtil.layoutsAreSame(firstLevel.layout(), firstLevelFromString.layout());

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
    Level secondLevelFromString = TestUtil.levelFromString(secondLevelString);
    Level secondLevel = levels.nextLevel();
    TestUtil.layoutsAreSame(secondLevel.layout(), secondLevelFromString.layout());
  }
}
