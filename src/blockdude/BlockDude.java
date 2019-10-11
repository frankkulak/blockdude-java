package blockdude;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import blockdude.controller.BlockDudeController;
import blockdude.controller.ClassicBlockDudeController;
import blockdude.model.BlockDudeModel;
import blockdude.model.ClassicBlockDudeModel;
import blockdude.model.LevelSet;
import blockdude.util.LevelSetFileReader;
import blockdude.view.BlockDudeView;
import blockdude.view.TextBasedBlockDudeView;

/**
 * A class for running the Block Dude puzzle game.
 */
public class BlockDude {
  private static Map<String, BlockDudeView> views = new HashMap<>();

  static {
    // map of view keys
    views.put("text", new TextBasedBlockDudeView(System.out));
  }

  /**
   * Main method for running the Block Dude game. Args should be of the structure: {"-source",
   * FILE_NAME, "-view", VIEW_TYPE} where FILE_NAME is the name of the file from which to read level
   * data (including the extension) and VIEW_TYPE is the view to use for the game (currently, only
   * "text" is supported).
   *
   * @param args list of game configuration arguments
   */
  public static void main(String[] args) {
    BlockDudeConfigurations gameConfigurations = parseArgs(args);
    runGame(gameConfigurations);
  }

  /**
   * Runs the Block Dude game using the given configurations.
   *
   * @param config configurations to use for the game
   */
  private static void runGame(BlockDudeConfigurations config) {
    BlockDudeModel model = config.model;
    BlockDudeView view = config.view;
    BlockDudeController controller = config.controller;

    // fixme - clean up below code, right now this only works for text view, and this code should not be here

    // show game for first time
    view.refresh(model);
    System.out.print("\n\n");

    // playing the game
    String exitMessage;
    Scanner scan = new Scanner(System.in);
    while (true) {
      try {
        if (controller.handleCommand(scan.next())) { // fixme change output to know why true/false so there is msg to disp
          System.out.print("\n\n");
        } else {
          System.out.print("Invalid move.\n\n");
        }
      } catch (IllegalArgumentException iae) {
        System.out.print(iae.getMessage() + "\n\n");
      } catch (IllegalStateException ise) {
        // ISE is a sign from the controller that the game must end
        exitMessage = ise.getMessage();
        break;
      }
    }

    // ending game message
    System.out.print("\nGame ended with message: " + exitMessage);
  }

  /* Parsing game configurations ---------------------------------------------------------------- */

  /**
   * A class to represent the configurations for the Block Dude game.
   */
  private static class BlockDudeConfigurations {
    LevelSet levels;
    BlockDudeModel model;
    BlockDudeView view;
    BlockDudeController controller;
  }

  /**
   * Parses the array of string arguments as BlockDudeConfigurations.
   *
   * @param args arguments to parse BlockDudeConfigurations from
   * @return BlockDudeConfigurations parsed from given string arguments
   * @throws IllegalArgumentException if given args cannot be used to generate configurations
   */
  private static BlockDudeConfigurations parseArgs(String[] args) throws IllegalArgumentException {
    BlockDudeConfigurations config = new BlockDudeConfigurations();

    for (int argIndex = 0; argIndex < args.length; argIndex++) {
      String arg = args[argIndex];

      switch (arg) {
        case "-source":
          if (argIndex == args.length - 1)
            throw new IllegalArgumentException("Expected token to follow \"" + arg + "\".");
          argIndex++;
          config.levels = parseLevelSet(args[argIndex]);
          break;
        case "-view":
          if (argIndex == args.length - 1)
            throw new IllegalArgumentException("Expected token to follow \"" + arg + "\".");
          argIndex++;
          config.view = parseView(args[argIndex]);
          break;
        default:
          throw new IllegalArgumentException("Unexpected token found: \"" + arg + "\".");
      }
    }

    config.model = new ClassicBlockDudeModel();
    // below line will throw IAE if view or levels is null, do not catch it
    config.controller = new ClassicBlockDudeController(config.model, config.view, config.levels);

    return config;
  }

  /**
   * Parses a view from the specified view name.
   *
   * @param viewName name of view to return
   * @return view generated from given string
   * @throws IllegalArgumentException if no view can be parsed from the given string
   */
  private static BlockDudeView parseView(String viewName) throws IllegalArgumentException {
    BlockDudeView view = views.get(viewName);
    if (view == null)
      throw new IllegalArgumentException("View named \"" + viewName + "\" could not be parsed.");
    return view;
  }

  /**
   * Returns a level set parsed from the given file name.
   *
   * @param filename name of file to generate level set from
   * @return level set parsed from file with given name
   * @throws IllegalArgumentException if a file with the given name does not exist, or if it does
   *                                  exist but cannot be parsed as a level set fixme catch more exceptions
   */
  private static LevelSet parseLevelSet(String filename) throws IllegalArgumentException {
    // will throw IAE if no level with given file name exists
    return LevelSetFileReader.parseLevelSetFile(filename);
  }
}
