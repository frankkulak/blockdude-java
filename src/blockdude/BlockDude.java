package blockdude;

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
 * A class for running the BlockDude puzzle game.
 */
public class BlockDude {
  public static void main(String[] args) {
    // generating levels from txt file (could throw IAE if filename wrong)
    String filename = "levels.txt";
    LevelSet levels = LevelSetFileReader.parseLevelSetFile(filename);

    // creating model, view, and controller
    BlockDudeModel model = new ClassicBlockDudeModel();
    BlockDudeView view = new TextBasedBlockDudeView(System.out);
    BlockDudeController controller = new ClassicBlockDudeController(model, view, levels);

    // pre-game instructions
    System.out.print("Welcome to BlockDude!\n\nControls:\n- A = move left\n- D = move right\n" +
            "- W = move up\n- S = put block down, pick block up\n- /pass: = try password (after " +
            ":)\n- /rel = restart level\n- /reg = restart game\n- /quit = end game\n\n");

    // show game for first time
    view.render(model);
    System.out.print("\n\n");

    // playing the game
    String exitMessage;
    Scanner scan = new Scanner(System.in);
    while(true) {
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
}
