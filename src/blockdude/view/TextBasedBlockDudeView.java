package blockdude.view;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import blockdude.controller.BlockDudeController;
import blockdude.util.Command;
import blockdude.util.CommandArguments;
import blockdude.util.GamePiece;

/**
 * A text-based view for the Block Dude game (intended for use with the console).
 */
public class TextBasedBlockDudeView implements BlockDudeView {
  private InputStream in;
  private PrintStream out;

  /**
   * Constructs new TextBasedBlockDudeView using given InputStream and PrintStream for I/O.
   *
   * @param in  InputStream to read from
   * @param out PrintStream to write to
   * @throws IllegalArgumentException if either argument is null
   */
  public TextBasedBlockDudeView(InputStream in, PrintStream out) throws IllegalArgumentException {
    if (in == null || out == null) throw new IllegalArgumentException("I/O must be non-null.");

    this.in = in;
    this.out = out;
  }

  /* Interface methods -------------------------------------------------------------------------- */

  @Override
  public void start(BlockDudeController controller) {
    out.print("Welcome to Block Dude!\n\nCommands:\n- a = move left\n- d = move right\n- w = mov" +
            "e up\n- s = put block down / pick block up\n- /pass: = try password (after :)\n- /r" +
            "el = restart level\n- /reg = restart game\n- /quit = end game\n\nPress 'enter' / 'r" +
            "eturn' to use a command.\nCommands are case-insensitive.");

    nextLine();
    controller.refreshView();

    Scanner scan = new Scanner(in);
    while (true) {
      try {
        parseCommand(controller, scan.next());
      } catch (IllegalArgumentException e) {
        // the command was either not valid or not recognized
        displayMessage(e.getMessage());
      } catch (RuntimeException e) {
        // something went wrong and the game needs to terminate
        displayMessage(e.getMessage());
        break;
      }
    }
  }

  @Override
  public void refresh(List<List<GamePiece>> layout, int levelIndex, String levelPassword) {
    StringBuilder outputString = new StringBuilder();

    outputString.append("Level ").append(levelIndexString(levelIndex));
    outputString.append(" (password: ").append(levelPassword).append(")\n\n");

    int rowIndex = 0;
    for (List<GamePiece> row : layout) {
      for (GamePiece piece : row) outputString.append(charFor(piece));
      rowIndex++;
      if (rowIndex < layout.size()) outputString.append('\n');
    }

    out.print(outputString.toString());
    nextLine();
  }

  @Override
  public void displayMessage(String message) {
    out.print(message);
    nextLine();
  }

  /* Private methods ---------------------------------------------------------------------------- */

  /**
   * Prints two newline characters.
   */
  private void nextLine() {
    out.print("\n\n");
  }

  /* Static methods ----------------------------------------------------------------------------- */

  /**
   * Determines and returns character to use to represent given GamePiece.
   *
   * @param gp GamePiece to find char for
   * @return char representing given GamePiece
   * @throws RuntimeException if given GamePiece is null / cannot be rendered
   */
  private static char charFor(GamePiece gp) throws RuntimeException {
    switch (gp) {
      case EMPTY:
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
        throw new RuntimeException("Given GamePiece cannot be rendered.");
    }
  }

  /**
   * Parses the given string as a command and executes it on the given controller.
   *
   * @param controller    controller to execute command on
   * @param commandString string to parse as command
   * @throws IllegalArgumentException if command could not be parsed
   */
  private void parseCommand(BlockDudeController controller, String commandString)
          throws IllegalArgumentException {
    String commandStringUC = commandString.toUpperCase();
    Command command;

    switch (commandStringUC) {
      case "W":
        command = Command.MOVE_UP;
        break;
      case "A":
        command = Command.MOVE_LEFT;
        break;
      case "S":
        command = Command.PICK_UP_PUT_DOWN;
        break;
      case "D":
        command = Command.MOVE_RIGHT;
        break;
      case "/REL":
        command = Command.RESTART_LEVEL;
        break;
      case "/REG":
        command = Command.RESTART_GAME;
        break;
      case "/QUIT":
        command = Command.QUIT;
        break;
      default:
        if (!commandStringUC.startsWith("/PASS:") || commandString.length() < 7)
          throw new IllegalArgumentException("Command '" + commandString + "' not recognized.");
        String password = commandString.split(":")[1];
        CommandArguments args = new CommandArguments();
        args.passwordToTry = password;
        controller.setCommandArguments(args);
        command = Command.TRY_PASSWORD;
        break;
    }

    controller.handleCommand(command);
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
