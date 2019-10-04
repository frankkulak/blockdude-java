package blockdude.view;

import java.io.PrintStream;
import java.util.List;

import blockdude.model.BlockDudeModel;
import blockdude.model.gamepieces.Block;
import blockdude.model.gamepieces.Door;
import blockdude.model.gamepieces.Empty;
import blockdude.model.gamepieces.GamePiece;
import blockdude.model.gamepieces.Player;
import blockdude.model.gamepieces.PlayerInDoor;
import blockdude.model.gamepieces.Wall;

/**
 * Represents a simple text-based view for the BlockDude game. Intended for use with the console.
 */
public class TextBasedBlockDudeView implements BlockDudeView {
  private PrintStream out;

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
    // fixme the way player holding blocks are rendered physically pains me
    StringBuilder outputString = new StringBuilder();

    outputString.append("Level ").append(model.curLevelIndex());
    outputString.append(" (password: ").append(model.curLevelPassword()).append(")\n\n");

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
          if (pieceBelow instanceof Player && ((Player) pieceBelow).isHoldingSomething()) {
            outputString.append(charFor(new Block()));
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
    // fixme I know this code is extremely ugly and brittle but it works for now
    if (gp instanceof Block) {
      return '\u25A2'; // hallow box
    } else if (gp instanceof Door) {
      return 'Π';
    } else if (gp instanceof Empty) {
      return ' ';
    } else if (gp instanceof Player) {
      Player p = (Player) gp;
      return (p.isFacingLeft() ? '<' : '>'); // for direction that player is facing
    } else if (gp instanceof Wall) {
      return '\u2588'; // solid box // fixme use u25A4?
    } else if (gp instanceof PlayerInDoor) {
      return '!'; // ! for winner
    } else {
      throw new IllegalArgumentException("Given GamePiece cannot be rendered.");
    }
  }
}
