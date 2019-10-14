package blockdude;

import java.util.HashMap;
import java.util.Map;

import blockdude.controller.BlockDudeController;
import blockdude.controller.ClassicBlockDudeController;
import blockdude.model.BlockDudeModel;
import blockdude.model.ClassicBlockDudeModel;
import blockdude.util.LevelSet;
import blockdude.util.LevelSetFileReader;
import blockdude.view.BlockDudeView;
import blockdude.view.TextBasedBlockDudeView;

/**
 * A class for running the Block Dude puzzle game.
 */
public class BlockDude {
  // the reason I'm using a map instead of a switch statement in the parseView() method is because
  // it's easier to read and update - I think it's cleaner to have all the recognized keywords in
  // one place at the top of the class, and better to simply add to a map than to update the parsing
  // method every time I create a new view
  private static Map<String, BlockDudeView> views = new HashMap<>();

  static {
    // map of view keys
    views.put("text", new TextBasedBlockDudeView(System.in, System.out));
  }

  /**
   * Main method for running the Block Dude game. Args should be of the structure: {"-source",
   * FILE_NAME, "-view", VIEW_TYPE} where FILE_NAME is the name of the file (including its .txt
   * extension) from which to read level data (which must be placed in the levelSources folder) and
   * VIEW_TYPE is the view to use for the game (currently, the only supported view is "text"). An
   * example of valid args is: {"-source", "levels.txt", "-view", "text"}.
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
    BlockDudeController controller = config.controller;
    controller.start();
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

    int argIndex = 0;
    while (argIndex < args.length) {
      String arg = args[argIndex];

      switch (arg) {
        case "-source":
          argIndex = parseSource(args, argIndex, config);
          break;
        case "-view":
          argIndex = parseView(args, argIndex, config);
          break;
        default:
          throw new IllegalArgumentException("Unexpected token ('" + arg + "') found.");
      }
    }

    config.model = new ClassicBlockDudeModel();
    // below line will throw IAE if view or levels is null, do not catch it
    config.controller = new ClassicBlockDudeController(config.model, config.view, config.levels);

    return config;
  }

  /**
   * Parses a view from the current index in the given list of arguments.
   *
   * @param args   array of arguments / tokens
   * @param index  index of '-source' token
   * @param config game configurations to modify
   * @return index immediately after all '-source' arguments
   * @throws IllegalArgumentException if the specified source could not be found
   * @throws IllegalStateException    if the specified source could not be parsed
   */
  private static int parseSource(String[] args, int index, BlockDudeConfigurations config)
          throws IllegalArgumentException, IllegalStateException {
    requireHasMoreTokens(args, index, 1);
    index++;
    String filename = "levelSources/" + args[index];
    // may throw either IAE or ISE - do not catch
    config.levels = LevelSetFileReader.parseLevelSetFile(filename);
    return index + 1;
  }

  /**
   * Parses a view from the current index in the given list of arguments.
   *
   * @param args   array of arguments / tokens
   * @param index  index of '-view' token
   * @param config game configurations to modify
   * @return index immediately after all '-view' arguments
   * @throws IllegalArgumentException if a view could not be parsed from the given arguments
   */
  private static int parseView(String[] args, int index, BlockDudeConfigurations config)
          throws IllegalArgumentException {
    requireHasMoreTokens(args, index, 1);
    index++;
    String viewName = args[index];
    BlockDudeView view = views.get(viewName);
    if (view == null)
      throw new IllegalArgumentException("Token '" + viewName + "' could not be parsed as a view.");
    config.view = view;
    index++;
    return index;
  }

  /**
   * Throws a detailed IllegalArgumentException if there are not at least the specified number of
   * tokens required following the current token.
   *
   * @param args           array of arguments / tokens
   * @param index          index of current argument / token
   * @param tokensRequired tokens required after the current one
   * @throws IllegalArgumentException if there are not enough tokens in the given array
   */
  private static void requireHasMoreTokens(String[] args, int index, int tokensRequired)
          throws IllegalArgumentException {
    int argsLeft = args.length - index - 1;
    String lastToken = args[index];
    if (argsLeft < tokensRequired) throw new IllegalArgumentException("Expected '" + lastToken +
            "' to be followed by " + tokensRequired + " token(s), but found " + argsLeft + ".");
  }
}
