package blockdude.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

import blockdude.model.GamePiece;
import blockdude.model.Level;
import blockdude.model.LevelSet;

/**
 * A class for reading txt files with level data.
 */
public class LevelSetFileReader {
  /**
   * Parses file with given name to generate level set for use in game.
   *
   * @param filename name of file to parse
   * @return level set created from input file
   * @throws IllegalArgumentException if file could not be found
   */
  public static LevelSet parseLevelSetFile(String filename) throws IllegalArgumentException {
    FileReader file;
    try {
      file = new FileReader(filename);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("No file named " + filename + " found.");
    }
    return LevelSetFileReader.parseLevelSetFile(file);
  }

  /**
   * Parses file to generate level set for use in game.
   *
   * @param readable txt file to read from
   * @return level set generated from file
   */
  private static LevelSet parseLevelSetFile(Readable readable) {
    Objects.requireNonNull(readable, "Must have non-null readable source.");
    Scanner scan = new Scanner(readable);
    scan.useDelimiter(Pattern.compile("(\\p{Space}+|#.*)+"));

    LevelSet.Builder levelSetBuilder = new LevelSet.Builder();

    while (scan.hasNext()) {
      String next = scan.next();
      if (next.equals("-level")) {
        levelSetBuilder.addLevel(parseLevel(scan));
      } else {
        throw new IllegalArgumentException("Unexpected token \"" + next + "\" found in file.");
      }
    }

    return levelSetBuilder.build();
  }

  /**
   * Parses level from given scanner. Might throw errors if input is invalid.
   *
   * @param scan scanner from which to parse level
   * @return level parsed from scanner
   */
  private static Level parseLevel(Scanner scan) {
    Level.Builder levelBuilder = new Level.Builder();

    // setting password
    requireHasNext(scan);
    levelBuilder.setPassword(scan.next());

    // creating layout of level
    while(scan.hasNext()) {
      String next = scan.next();
      if (next.equals("-/level")) {
        break;
      } else {
        levelBuilder.nextRow();
        char[] chars = next.toCharArray();
        for (char c : chars) {
          levelBuilder.addGamePieceToRow(parseGamePiece(c));
        }
      }
    }

    return levelBuilder.build();
  }

  /**
   * Throws IAE if given scanner does not have a next token. Created for brevity in above code.
   *
   * @param scan scanner to check for next token
   * @throws IllegalStateException if no next token found
   */
  private static void requireHasNext(Scanner scan) throws IllegalArgumentException {
    if (!scan.hasNext()) {
      throw new IllegalArgumentException("Expected token, but did not find one.");
    }
  }

  /**
   * Parses given char as a GamePiece, returns that GamePiece as an object.
   *
   * @param c char of game piece to return
   * @return game piece corresponding to given char
   * @throws IllegalArgumentException if given char is not a valid game piece
   */
  private static GamePiece parseGamePiece(char c) throws IllegalArgumentException {
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
        throw new IllegalArgumentException("Char '" + c + "' cannot be parsed. " +
                "The only valid chars are: {'X', '_', 'B', 'D', 'L', 'R'}.");
    }
  }
}
