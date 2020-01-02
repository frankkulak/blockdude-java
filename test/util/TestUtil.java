package util;

import java.io.StringReader;
import java.util.List;

import blockdude.util.GamePiece;
import blockdude.util.Level;
import blockdude.util.LevelSet;
import blockdude.util.LevelSetReader;

/**
 * A class of static methods to aid in testing of src directory.
 */
public class TestUtil {
  /**
   * Determines whether two given layouts are identical.
   *
   * @param lo1 first layout to compare
   * @param lo2 second layout to compare
   * @return true if layouts are exactly the same, false otherwise
   */
  public static boolean layoutsAreSame(List<List<GamePiece>> lo1, List<List<GamePiece>> lo2) {
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
  public static Level levelFromString(String levelString) throws IllegalStateException {
    StringReader stringReader = new StringReader(levelString);
    LevelSet levels = LevelSetReader.parseLevelSet(stringReader);
    return levels.currentLevel();
  }

}
