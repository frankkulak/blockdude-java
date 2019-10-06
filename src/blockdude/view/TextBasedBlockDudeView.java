package blockdude.view;

import java.io.PrintStream;
import java.util.List;

import blockdude.model.BlockDudeModel;
import blockdude.model.GamePiece;

/**
 * Represents a simple text-based view for the BlockDude game. Intended for use with the console.
 */
public class TextBasedBlockDudeView implements BlockDudeView {
  private PrintStream out;
  private BlockDudeViewHelper helper;

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
  }

  @Override
  public void render(BlockDudeModel model) throws IllegalStateException {
    // fixme the way a player holding a block is rendered physically pains me
    // fixme this method is so ugly i am so sorry to whoever is reading this
    StringBuilder outputString = new StringBuilder();

    outputString.append("Level ").append(currentLevelIndexString());
    outputString.append(" (password: ").append(helper.currentLevelPassword()).append(")\n\n");

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
          if (GamePiece.isPlayer(pieceBelow) && model.playerIsHoldingSomething()) {
            outputString.append(charFor(GamePiece.BLOCK));
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

  @Override
  public void setHelper(BlockDudeViewHelper helper) {
    this.helper = helper;
  }

  @Override
  public void modelUpdated(BlockDudeModel model) {
    this.render(model);
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
        return ' ';
      case PLAYER_LEFT:
        return '<';
      case PLAYER_RIGHT:
        return '>';
      case PLAYER_DOOR:
        return '!';
      case BLOCK:
        return '\u25A2'; // hollow box
      case WALL:
        return 'X';//'\u2588'; // solid box
      case DOOR:
        return 'Π';
      default:
        throw new IllegalArgumentException("Given GamePiece cannot be rendered.");
    }
  }

  /**
   * Returns the current level index as a string.
   *
   * @return current level index as a string
   */
  private String currentLevelIndexString() {
    if (helper == null) return "[UNKNOWN]";
    return Integer.toString(helper.currentLevelIndex() + 1);
  }
}
