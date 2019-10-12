package blockdude.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A class for reading text files with level data.
 */
public class LevelSetFileReader {
  /**
   * Parses file with given name to generate level set for use in game.
   *
   * @param filename name of file to parse
   * @return level set created from input file
   * @throws IllegalArgumentException if file could not be found
   * @throws IllegalStateException    if file could not be parsed as level set
   */
  public static LevelSet parseLevelSetFile(String filename) throws IllegalArgumentException {
    try {
      return LevelSetFileReader.parseLevelSetFile(new FileReader(filename));
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("No file named " + filename + " found.");
    }
  }

  /**
   * Parses file to generate level set for use in game.
   *
   * @param readable txt file to read from
   * @return level set generated from file
   * @throws IllegalStateException if file could not be parsed as level set
   */
  private static LevelSet parseLevelSetFile(Readable readable) throws IllegalStateException {
    Objects.requireNonNull(readable, "Must have non-null readable source.");
    Scanner scan = new Scanner(readable);
    scan.useDelimiter(Pattern.compile("(\\p{Space}+|#.*)+"));

    LevelSet.Builder levelSetBuilder = new LevelSet.Builder();

    while (scan.hasNext()) {
      String next = scan.next();

      switch (next) {
        case "-level":
          levelSetBuilder.addLevel(parseLevel(scan));
          break;
        default:
          throw new IllegalStateException("Unexpected token ('" + next + "') found in file.");
      }
    }

    // building might throw ISE - don't catch it
    return levelSetBuilder.build();
  }

  /**
   * Parses level from given scanner. Might throw errors if input is invalid.
   *
   * @param scan scanner from which to parse level
   * @return level parsed from scanner
   * @throws IllegalStateException if could not parse a level
   */
  private static Level parseLevel(Scanner scan) throws IllegalStateException {
    Level.Builder levelBuilder = new Level.Builder();

    // might throw ISE - don't catch it
    requireHasNext(scan);
    levelBuilder.setPassword(scan.next());

    // creating layout of level
    while (scan.hasNext()) {
      String next = scan.next();
      if (next.equals("-/level")) break;
      levelBuilder.nextRow();
      char[] chars = next.toCharArray();
      for (char c : chars) levelBuilder.addGamePieceToRow(parseGamePiece(c));
    }

    // building might throw ISE - don't catch it
    return levelBuilder.build();
  }

  /**
   * Throws IAE if given scanner does not have a next token. Created for brevity in above code.
   *
   * @param scan scanner to check for next token
   * @throws IllegalStateException if no next token found
   */
  private static void requireHasNext(Scanner scan) throws IllegalStateException {
    if (!scan.hasNext()) throw new IllegalStateException("Expected token, but did not find one.");
  }

  /**
   * Parses given char as a GamePiece, returns that GamePiece as an object.
   *
   * @param c char of game piece to return
   * @return game piece corresponding to given char
   * @throws IllegalStateException if given char is not a valid game piece
   */
  private static GamePiece parseGamePiece(char c) throws IllegalStateException {
    switch (c) {
      case 'X':
        return GamePiece.WALL;
      case '_':
        return GamePiece.EMPTY;
      case 'B':
        return GamePiece.BLOCK;
      case 'D':
        return GamePiece.DOOR;
      case 'L':
        return GamePiece.PLAYER_LEFT;
      case 'R':
        return GamePiece.PLAYER_RIGHT;
      default:
        throw new IllegalStateException("Char '" + c + "' cannot be parsed as GamePiece. " +
                "The only valid chars are: {'X', '_', 'B', 'D', 'L', 'R'}.");
    }
  }
}
