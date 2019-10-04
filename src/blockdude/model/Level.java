package blockdude.model;

import java.util.ArrayList;
import java.util.List;

import blockdude.model.gamepieces.GamePiece;
import blockdude.model.gamepieces.Player;
import blockdude.util.Position;

/**
 * Represents a level of the BlockDude puzzle game.
 */
public class Level {
  // info regarding level itself
  private final int id;
  private final String password;
  private final List<List<GamePiece>> initLayout;
  // info regarding initial conditions of player
  private final boolean playerInitFacingLeft;
  private final Position playerInitPosn;

  // fixme make const ensure legitimacy of level
  /**
   * Constructs a new Level object. IMPORTANT: Just because a Level is successfully constructed by
   * this constructor does not mean it is a valid and playable level, it is possible that a result
   * of this constructor may cause issues when played, such as a level that is created where the
   * player is allowed to wander off screen.
   *
   * @param id id of this level
   * @param p password for this level
   * @param il initial arrangement of all game piece in this level
   * @throws IllegalArgumentException if there is no player included in the level layout, or if any
   *                                  given arguments are invalid (id is negative, pass is empty or
   *                                  null, list is null)
   */
  private Level(int id, String p, List<List<GamePiece>> il) throws IllegalArgumentException {
    if (id < 0) {
      throw new IllegalArgumentException("Level id must be non-negative.");
    } else if (p == null || p.isEmpty()) {
      throw new IllegalArgumentException("Level password must have at least one char.");
    } else if (il == null) {
      throw new IllegalArgumentException("Level layout must be non-null.");
    }

    this.id = id;
    this.password = p;
    this.initLayout = il;

    // finding player fixme O(m•n)... can do better? sorry I know this code is really ugly lol
    Player player = null;
    Position posn = new Position(0, 0);
    ROWS : for (int y = 0; y < this.initLayout.size(); y++) {
      List<GamePiece> row = this.initLayout.get(y);
      for (int x = 0; x < row.size(); x++) {
        GamePiece gp = row.get(x);
        if (gp instanceof Player) { // fixme yikes
          player = (Player) gp; // fixme yikes pt 2
          posn.x = x;
          posn.y = y;
          break ROWS; // fixme yikes 3: the return of yike
        }
      }
    }

    if (player == null) {
      throw new IllegalArgumentException("There must be a player specified in the level.");
    } else {
      this.playerInitFacingLeft = player.isFacingLeft();
      this.playerInitPosn = posn;
    }
  }

  /**
   * Builder for creating a Level object.
   */
  public static class Builder {
    private int id;
    private String password;
    private final List<List<GamePiece>> initLayout;

    public Builder() {
      // all of these will cause const to throw IAE if not changed to something valid
      this.id = -1;
      this.password = "";
      this.initLayout = new ArrayList<>();
    }

    public Builder setID(int id) {
      this.id = id;
      return this;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder createNewRow() {
      this.initLayout.add(new ArrayList<>());
      return this;
    }

    public Builder addToCurrentRow(GamePiece gp) {
      int finalIndex = this.initLayout.size() - 1;
      this.initLayout.get(finalIndex).add(gp);
      return this;
    }

    public Level build() throws IllegalStateException {
      try {
        return new Level(this.id, this.password, this.initLayout);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("Could not build Level as specified.");
      }
    }
  }

  /**
   * Checks if given password is correct for this level, returns result.
   *
   * @param password password to try
   * @return whether or not password was correct
   */
  public boolean tryPassword(String password) {
    if (password == null) {
      return false;
    } else {
      return this.password.equals(password);
    }
  }

  /**
   * Generates and returns new Player object based off of player needed for this level.
   *
   * @return new Player object
   */
  public Player generatePlayer() {
    return new Player(this.playerInitFacingLeft);
  }

  /**
   * Copies and returns initial position of player in this level.
   *
   * @return copy of initial position of player in this level
   */
  public Position getPlayerPosn() {
    // copying values so that internal posn cannot be manipulated when returned
    return this.playerInitPosn.copy();
  }

  /**
   * Finds and returns index of this level.
   *
   * @return index of level.
   */
  public int index() {
    return this.id;
  }

  /**
   * Finds and returns password of this level.
   *
   * @return password of level
   */
  String password() {
    return this.password;
  }

  /**
   * Returns copy of initial layout of this level.
   *
   * @return copy of initial layout of this level
   */
  public List<List<GamePiece>> getLayout() {
    List<List<GamePiece>> layout = new ArrayList<>();

    // copying all rows to layout
    for (List<GamePiece> row : this.initLayout) {
      // creating new row to place in layout
      List<GamePiece> newRow = new ArrayList<>();
      // adding all game pieces in row to new row
      for (GamePiece gp : row) {
        newRow.add(gp.copy());
      }
      // adding new row to layout
      layout.add(newRow);
    }

    return layout;
  }
}
