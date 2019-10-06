package blockdude.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a level of the Block Dude puzzle game.
 */
public class Level {
  private final String password;
  private final List<List<GamePiece>> layout;
  private final boolean playerFacingLeft;
  private final Position playerPosition;

  /**
   * Constructs a new Level.
   *
   * @param password         password of this level
   * @param layout           layout of this level
   * @param playerFacingLeft whether player is initially facing left or not
   * @param playerPosition   where player is initially located
   * @throws IllegalArgumentException if password is null or empty, if layout is empty or
   *                                  non-square, or if there is no player at the specified
   *                                  position
   */
  private Level(String password,
                List<List<GamePiece>> layout,
                boolean playerFacingLeft,
                Position playerPosition)
          throws IllegalArgumentException {
    // validating password
    if (password == null || password.isEmpty())
      throw new IllegalArgumentException("Password must be >= 1 character.");

    // validating layout
    if (layout == null || layout.isEmpty())
      throw new IllegalArgumentException("Layout must have at least one row.");
    for (List<GamePiece> row : layout)
      if (row.size() != layout.size()) throw new IllegalArgumentException("Layout must be square.");

    // validating player position
    if (playerPosition == null)
      throw new IllegalArgumentException("Player position cannot be null.");
    int row = playerPosition.y;
    int col = playerPosition.x;
    if (row < 0 || col < 0)
      throw new IllegalArgumentException("Player position cannot be negative.");
    if (row >= layout.size() || col >= layout.get(row).size())
      throw new IllegalArgumentException("Player position out of board bounds.");
    GamePiece player = layout.get(row).get(col);
    if (player != GamePiece.PLAYER_RIGHT && player != GamePiece.PLAYER_LEFT)
      throw new IllegalArgumentException("No player found at player position.");

    // level is valid
    this.password = password;
    this.layout = layout;
    this.playerFacingLeft = playerFacingLeft;
    this.playerPosition = playerPosition;
  }

  /**
   * Builder for creating a Level.
   */
  public static class Builder {
    private String password;
    private List<List<GamePiece>> layout;
    private boolean playerFacingLeft;
    private Position playerPosition;
    private boolean invalidLevelConfiguration;

    public Builder() {
      password = "";
      layout = new ArrayList<>();
      playerFacingLeft = false;
      playerPosition = null;
      invalidLevelConfiguration = false;
    }

    public Builder setPassword(String password) {
      this.password = password;
      return this;
    }

    public Builder nextRow() {
      layout.add(new ArrayList<>());
      return this;
    }

    public Builder addGamePieceToRow(GamePiece gp) { // fixme make return void
      List<GamePiece> finalRow = layout.get(layout.size() - 1);
      finalRow.add(gp);

      switch (gp) {
        case PLAYER_LEFT:
        case PLAYER_RIGHT:
          if (playerPosition != null) invalidLevelConfiguration = true;
          playerFacingLeft = gp == GamePiece.PLAYER_LEFT;
          int x = finalRow.size() - 1;
          int y = layout.size() - 1;
          playerPosition = new Position(x, y);
          break;
        case PLAYER_DOOR:
          invalidLevelConfiguration = true;
          break;
      }

      return this;
    }

    public Level build() throws IllegalStateException {
      try {
        if (invalidLevelConfiguration) throw new IllegalStateException();
        return new Level(password, layout, playerFacingLeft, playerPosition);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("Could not build Level as specified.");
      }
    }
  }

  /**
   * Returns password of this level.
   *
   * @return password of level
   */
  String password() {
    return this.password;
  }

  /**
   * Checks if given password is correct for this level, returns result.
   *
   * @param password password to try
   * @return true is password is correct, false otherwise
   */
  public boolean tryPassword(String password) {
    if (password == null) return false;
    return this.password.equals(password);
  }

  /**
   * Returns layout of this level.
   *
   * @return layout of this level
   */
  public List<List<GamePiece>> layout() {
    // copying so layout cannot be manipulated externally
    List<List<GamePiece>> newLayout = new ArrayList<>();
    for (List<GamePiece> row : layout) newLayout.add(new ArrayList<>(row));
    return newLayout;
  }

  /**
   * Generates and returns new Player object based off of player needed for this level.
   *
   * @return new Player object
   */
  public GamePiece generatePlayer() {
    return (playerFacingLeft ? GamePiece.PLAYER_LEFT : GamePiece.PLAYER_RIGHT);
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
