package blockdude.view;

import java.io.PrintStream;
import java.util.List;

import blockdude.model.BlockDudeModel;
import blockdude.util.GamePiece;

// FIXME: view will be updated after controller.. some code is hacky just to make controller function as i work on it

/**
 * Represents a simple text-based view for the BlockDude game. Intended for use with the console.
 */
public class TextBasedBlockDudeView implements BlockDudeView {
  private PrintStream out;
  private boolean hasRendered;

  /**
   * Constructs new TextBasedBlockDudeView that outputs to given PrintStream.
   *
   * @param out PrintStream to output to
   * @throws IllegalArgumentException if given PrintStream is null
   */
  public TextBasedBlockDudeView(PrintStream out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("PrintStream must be non-null.");
    }

    this.out = out;
    hasRendered = false;
  }

  @Override
  public void refresh(BlockDudeModel model, int levelIndex, String levelPassword) throws IllegalStateException {
    if (!hasRendered) {
      System.out.print("Welcome to BlockDude!\n\nControls:\n- A = move left\n- D = move right\n- " +
              "W = move up\n- S = put block down, pick block up\n- /pass: = try password (after :" +
              ")\n- /rel = restart level\n- /reg = restart game\n- /quit = end game\n\n");
      hasRendered = true;
    }

    // fixme the way a player holding a block is rendered physically pains me
    // fixme this method is so ugly i am so sorry to whoever is reading this
    StringBuilder outputString = new StringBuilder();

    outputString.append("Level ").append(levelIndexString(levelIndex));
    outputString.append(" (password: ").append(levelPassword).append(")\n\n");

    List<List<GamePiece>> layout = model.layout();

    // iterating through rows of layout
    for (int i = 0; i < layout.size(); i++) {
      // finding current row
      List<GamePiece> row = layout.get(i);

      // iterating through column of row
      for (int j = 0; j < row.size(); j++) {
        // check if on last row, if not, then check if player is below and has block
        if (i != layout.size() - 1) {
          GamePiece pieceBelow = layout.get(i + 1).get(j);
          if (GamePiece.isPlayer(pieceBelow) && (model.pieceHeldByPlayer() != null)) {
            outputString.append(charFor(model.pieceHeldByPlayer()));
          } else {
            outputString.append(charFor(layout.get(i).get(j)));
          }
        } else {
          // just add piece to output
          outputString.append(charFor(layout.get(i).get(j)));
        }
      }

      // add new line to outputString for all but last row
      outputString.append((i != layout.size() - 1 ? "\n" : ""));
    }

    // rendering output string
    this.out.print(outputString.toString());
  }

  /**
   * Determines and returns character to use to represent given GamePiece.
   *
   * @param gp GamePiece to find char for
   * @return char representing given GamePiece
   * @throws IllegalArgumentException if given GamePiece is null / cannot be rendered
   */
  private char charFor(GamePiece gp) throws IllegalArgumentException {
    switch (gp) {
      case EMPTY:
        // return '\u00B7'; // middle dot
        return ' ';
      case PLAYER_LEFT:
        return '<';
      case PLAYER_RIGHT:
        return '>';
      case BLOCK:
        return '\u25A2'; // hollow box
      case WALL:
        return '\u2588'; // solid box
      case DOOR:
        return 'Π';
      default:
        throw new IllegalArgumentException("Given GamePiece cannot be rendered.");
    }
  }

  /**
   * Returns a string to use for the given level index (adds one and converts to string).
   *
   * @param levelIndex index of current level
   * @return string for given level index
   */
  private static String levelIndexString(int levelIndex) {
    return Integer.toString(levelIndex + 1);
  }
}
