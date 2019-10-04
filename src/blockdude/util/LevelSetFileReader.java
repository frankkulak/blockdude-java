package blockdude.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

import blockdude.model.Level;
import blockdude.model.LevelSet;
import blockdude.model.gamepieces.Block;
import blockdude.model.gamepieces.Door;
import blockdude.model.gamepieces.Empty;
import blockdude.model.gamepieces.GamePiece;
import blockdude.model.gamepieces.Player;
import blockdude.model.gamepieces.Wall;

/**
 * A class for reading txt files with level data.
 */
public class LevelSetFileReader {
  /**
   * Parses file with given name to generate level set for use in game.
   *
   * @param fileName name of file to parse
   * @return level set created from input file
   * @throws IllegalArgumentException if file could not be found
   */
  public static LevelSet parseLevelSetFile(String fileName) throws IllegalArgumentException {
    FileReader file;
    try {
      file = new FileReader(fileName);
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("No file named " + fileName + " found.");
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
      } else if (next.equals("-start")) {
        requireHasNext(scan);
        levelSetBuilder.setStartingIndex(Integer.parseInt(scan.next())); // might throw, das ok
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

    // setting ID and password
    requireHasNext(scan);
    levelBuilder.setID(Integer.parseInt(scan.next())); // might throw, das ok
    requireHasNext(scan);
    levelBuilder.setPassword(scan.next());

    // creating layout of level
    while(scan.hasNext()) {
      String next = scan.next();
      if (next.equals("-/level")) {
        break;
      } else {
        levelBuilder.createNewRow();
        char[] chars = next.toCharArray();
        for (char c : chars) {
          levelBuilder.addToCurrentRow(parseGamePiece(c));
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
        return new Wall();
      case '_':
        return new Empty();
      case 'B':
        return new Block();
      case 'D':
        return new Door();
      case 'L':
        return new Player(true);
      case 'R':
        return new Player(false);
      default:
        throw new IllegalArgumentException("Char '" + c + "' cannot be parsed. " +
                "The only valid chars are: {'X', '_', 'B', 'D', 'L', 'R'}.");
    }
  }
}
