package blockdude.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a level of the Block Dude puzzle game.
 */
public class Level {
  private final String password;
  private final List<List<GamePiece>> layout;
  private final GamePiece player;
  private final Position playerPosition;

  /**
   * Constructs a new Level.
   *
   * @param password       password of this level
   * @param layout         layout of this level
   * @param player         player to use at start of level
   * @param playerPosition where player is initially located
   * @throws IllegalArgumentException if password is null or empty, if layout is null or empty, if
   *                                  layout is not rectangular, if player is null or if
   *                                  playerPosition is null
   */
  private Level(String password, List<List<GamePiece>> layout,
                GamePiece player, Position playerPosition) throws IllegalArgumentException {
    // validating password exists and is not empty
    if (password == null || password.isEmpty())
      throw new IllegalArgumentException("Password must be >= 1 character.");

    // validating layout exists and is not empty
    if (layout == null || layout.isEmpty())
      throw new IllegalArgumentException("Layout must have at least one row.");

    // validating that layout is a rectangle
    int prevRowSize = layout.get(0).size();
    for (List<GamePiece> row : layout) {
      if (row.size() != prevRowSize)
        throw new IllegalArgumentException("Layout must be rectangular.");
      prevRowSize = row.size();
    }

    // validating player and player position are not null
    if (player == null || playerPosition == null)
      throw new IllegalArgumentException("Player and its position cannot be null.");

    // the invalid game states of the player position not being correct, having multiple players,
    // and having players in doors at the start of a level, are all protected by the builder

    this.password = password;
    this.layout = layout;
    this.player = player;
    this.playerPosition = playerPosition;
  }

  /**
   * Builder for creating a Level. Access is package private since Levels should only ever be
   * created by a LevelSetFileReader.
   */
  static class Builder {
    private String password;
    private List<List<GamePiece>> layout;
    private GamePiece player;
    private Position playerPosition;
    private boolean invalidLevelConfiguration;
    private String errorMessage;

    /**
     * Creates a new Level.Builder object.
     */
    Builder() {
      password = "";
      layout = new ArrayList<>();
      player = null;
      playerPosition = null;
      invalidLevelConfiguration = false;
      errorMessage = "";
    }

    /**
     * Sets the password of this level.
     *
     * @param password password of level
     */
    void setPassword(String password) {
      this.password = password;
    }

    /**
     * Creates a new row in the game board.
     */
    void nextRow() {
      layout.add(new ArrayList<>());
    }

    /**
     * Adds the given game piece to the row that is currently being built.
     *
     * @param gp game piece to add to row
     */
    void addGamePieceToRow(GamePiece gp) {
      List<GamePiece> finalRow = layout.get(layout.size() - 1);
      finalRow.add(gp);

      if (GamePiece.isPlayer(gp)) {
        if (player != null) {
          invalidLevelConfiguration = true;
          errorMessage += "More than one player specified;";
        }

        if (gp == GamePiece.PLAYER_DOOR) {
          invalidLevelConfiguration = true;
          errorMessage += "Player initially placed in door;";
        } else {
          player = gp;
          int x = finalRow.size() - 1;
          int y = layout.size() - 1;
          playerPosition = new Position(x, y);
        }
      }
    }

    /**
     * Builds a Level using the specified properties.
     *
     * @return a new Level with the specified properties.
     * @throws IllegalStateException if cannot build level as specified
     */
    Level build() throws IllegalStateException {
      try {
        if (invalidLevelConfiguration) throw new IllegalStateException();
        return new Level(password, layout, player, playerPosition);
      } catch (IllegalArgumentException | IllegalStateException e) {
        if (e.getMessage() != null) errorMessage += e.getMessage();
        throw new IllegalStateException("Could not build Level as specified: " + errorMessage);
      }
    }
  }

  /**
   * Returns password of this level.
   *
   * @return password of level
   */
  public String password() {
    return password;
  }

  /**
   * Returns layout of this level.
   *
   * @return layout of this level
   */
  public List<List<GamePiece>> layout() {
    // copying layout so that it cannot be manipulated externally
    List<List<GamePiece>> layoutCopy = new ArrayList<>();
    for (List<GamePiece> row : layout) layoutCopy.add(new ArrayList<>(row));
    return layoutCopy;
  }

  /**
   * Returns the player to use for this level.
   *
   * @return player to use for this level
   */
  public GamePiece player() {
    return player;
  }

  /**
   * Returns position of player in this level.
   *
   * @return position of player in this level
   */
  public Position playerPosition() {
    // copying so player position cannot be manipulated externally
    return playerPosition.copy();
  }
}
