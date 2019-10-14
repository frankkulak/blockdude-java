package blockdude.model;

import java.util.ArrayList;
import java.util.List;

import blockdude.util.GamePiece;
import blockdude.util.Level;
import blockdude.util.Position;

/**
 * Represents the classic Block Dude game model.
 */
public class ClassicBlockDudeModel implements BlockDudeModel {
  // INVARIANT: below chunk of fields will never be null once set
  private Level level;
  private GamePiece player; // INVARIANT: either PLAYER_LEFT or PLAYER_RIGHT
  private Position playerPosition; // INVARIANT: never has coors beyond limits of board
  private List<List<GamePiece>> layout; // INVARIANT: correct for current level

  private GamePiece heldPiece; // will be null if nothing is held
  private boolean doorReached;

  private enum Direction { LEFT, RIGHT }

  /* Interface methods -------------------------------------------------------------------------- */

  @Override
  public void restartLevel() throws RuntimeException {
    requireLevel();
    playerPosition = level.playerPosition();
    player = level.player();
    heldPiece = null;
    layout = level.layout();
    doorReached = false;
  }

  @Override
  public void loadLevel(Level level) throws IllegalArgumentException {
    if (level == null) throw new IllegalArgumentException("Cannot load null level into model.");
    this.level = level;
    restartLevel();
  }

  @Override
  public boolean moveLeft() throws RuntimeException {
    return movePlayerHorizontally(Direction.LEFT);
  }

  @Override
  public boolean moveRight() throws RuntimeException {
    return movePlayerHorizontally(Direction.RIGHT);
  }

  @Override
  public boolean moveUp() throws RuntimeException {
    requireLevel();

    // making sure piece above player is not solid
    GamePiece pieceAbovePlayer = getGamePiece(shiftPosition(playerPosition, 0, -1));
    if (GamePiece.isSolid(pieceAbovePlayer)) return false;

    // making sure piece to side is solid and does not have a solid piece above it
    int colDif = (player == GamePiece.PLAYER_LEFT ? -1 : 1);
    Position positionToSide = shiftPosition(playerPosition, colDif, 0);
    GamePiece pieceToSide = getGamePiece(positionToSide);
    Position targetPosition = shiftPosition(playerPosition, colDif, -1);
    GamePiece targetPiece = getGamePiece(targetPosition);
    if (!GamePiece.isSolid(pieceToSide) || GamePiece.isSolid(targetPiece)) return false;

    // move player to target position
    setGamePiece(targetPosition, player);
    setGamePiece(playerPosition, GamePiece.EMPTY);
    playerPosition = targetPosition.copy();

    // check if door reached
    if (targetPiece == GamePiece.DOOR) doorReached = true;

    return true;
  }

  @Override
  public boolean pickUpOrPutDown() throws RuntimeException {
    return (heldPiece == null) ? pickUp() : putDown();
  }

  @Override
  public List<List<GamePiece>> layoutToRender() throws RuntimeException {
    requireLevel();

    List<List<GamePiece>> layoutToRender = new ArrayList<>();
    for (List<GamePiece> row : layout) layoutToRender.add(new ArrayList<>(row));

    if (heldPiece != null) {
      int row = playerPosition.row - 1;
      int col = playerPosition.col;
      layoutToRender.get(row).set(col, heldPiece);
    }

    return layoutToRender;
  }

  @Override
  public boolean isLevelCompleted() throws RuntimeException {
    requireLevel();
    return doorReached;
  }

  /* Private methods ---------------------------------------------------------------------------- */

  /**
   * Moves player in specified direction. Will notify listener if game won.
   *
   * @param direction horizontal direction to move in
   * @return whether or not anything changed after making this move
   * @throws RuntimeException if no level has been loaded into model yet or if piece reaches board
   *                          edge (index off board is accessed)
   */
  private boolean movePlayerHorizontally(Direction direction) throws RuntimeException {
    requireLevel();

    boolean playerOrientationChanged = changePlayerDirection(direction);

    int colDif = (direction == Direction.LEFT ? -1 : 1);
    Position positionToSide = shiftPosition(playerPosition, colDif, 0);
    GamePiece pieceToSide = getGamePiece(positionToSide);

    // making sure piece at player's side is not solid
    if (GamePiece.isSolid(pieceToSide)) return playerOrientationChanged;

    // moving player in direction and applying gravity
    setGamePiece(positionToSide, player);
    setGamePiece(playerPosition, GamePiece.EMPTY);
    playerPosition = positionToSide.copy();
    applyGravity(playerPosition);

    // checking if piece to player's side is a door
    if (pieceToSide == GamePiece.DOOR) doorReached = true;

    return true;
  }

  /**
   * Picks up the game piece in front of player, if able to do so. If there is a block above the
   * player or target game piece, the player will not be able to pick it up.
   *
   * @return true if picking up was successful and changed the state of the board, false otherwise
   * @throws RuntimeException if no level has been loaded into model yet or if piece reaches board
   *                          edge (index off board is accessed)
   */
  private boolean pickUp() throws RuntimeException {
    requireLevel();

    // finding piece at side of player
    int colDif = (player == GamePiece.PLAYER_LEFT ? -1 : 1);
    Position targetPosition = shiftPosition(playerPosition, colDif, 0);
    GamePiece pieceToSide = getGamePiece(targetPosition);

    // make sure the piece is able to be picked up
    if (!GamePiece.canPickUp(pieceToSide)) return false;

    // make sure there is nothing on top of piece or above the player
    Position posAboveTarget = shiftPosition(targetPosition, 0, -1);
    GamePiece pieceAboveTarget = getGamePiece(posAboveTarget);
    if (GamePiece.isSolid(pieceAboveTarget)) return false;
    Position posAbovePlayer = shiftPosition(playerPosition, 0, -1);
    GamePiece pieceAbovePlayer = getGamePiece(posAbovePlayer);
    if (GamePiece.isSolid(pieceAbovePlayer)) return false;

    // pick up the piece and return true
    heldPiece = pieceToSide;
    setGamePiece(targetPosition, GamePiece.EMPTY);
    return true;
  }

  /**
   * Puts down the game piece that player is holding. If the block immediately to the facing
   * direction of the player is solid, then the piece will be placed on top of it (as long as the
   * piece above is not also solid), otherwise, it will be placed immediately in front of the player
   * and will fall until it hits a solid block.
   *
   * @return true if putting down was successful and changed the state of the board, false otherwise
   * @throws RuntimeException if no level has been loaded into model yet or if piece reaches board
   *                          edge (index off board is accessed)
   */
  private boolean putDown() throws RuntimeException {
    requireLevel();

    // finding piece at side of player
    int colDif = (player == GamePiece.PLAYER_LEFT ? -1 : 1);
    Position targetPosition = shiftPosition(playerPosition, colDif, 0);
    GamePiece pieceToSide = getGamePiece(targetPosition);

    // checking if piece at target position is solid or not
    if (GamePiece.isSolid(pieceToSide)) {
      // piece to side is solid, check if can place block above it
      Position positionAbove = shiftPosition(targetPosition, 0, -1);
      GamePiece pieceAbove = getGamePiece(positionAbove);
      if (GamePiece.isSolid(pieceAbove)) return false;
      targetPosition = positionAbove;
    }

    // put piece down, apply gravity, and return true
    setGamePiece(targetPosition, heldPiece);
    heldPiece = null;
    applyGravity(targetPosition);
    return true;
  }

  /**
   * Moves the game piece at the given position down until it hits a solid game piece.
   *
   * @param pos position of piece to apply gravity to
   * @throws RuntimeException if piece reaches board edge (index off board is accessed)
   */
  private void applyGravity(Position pos) throws RuntimeException {
    // finding new position of piece
    Position newPos = pos.copy();
    GamePiece pieceBelow = getGamePiece(shiftPosition(pos, 0, 1));
    while (!GamePiece.isSolid(pieceBelow)) {
      newPos.row++;
      pieceBelow = getGamePiece(shiftPosition(newPos, 0, 1));
    }

    // move piece to new position
    if (pos.equals(newPos)) return;
    GamePiece piece = getGamePiece(pos);
    GamePiece reachedPiece = getGamePiece(newPos);
    setGamePiece(newPos, piece);
    setGamePiece(pos, GamePiece.EMPTY);

    // if piece is the player, update player position & check if door reached
    if (GamePiece.isPlayer(piece)) {
      playerPosition = newPos.copy();
      if (reachedPiece == GamePiece.DOOR) doorReached = true;
    }
  }

  /**
   * Finds and returns the game piece at the given position.
   *
   * @param pos position at which to get game piece
   * @return game piece at the given position
   * @throws RuntimeException if position is not on game board
   */
  private GamePiece getGamePiece(Position pos) throws RuntimeException {
    try {
      return layout.get(pos.row).get(pos.col);
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeException("Tried to access index that is not on the board.");
    }
  }

  /**
   * Sets the value at the given position to the given game piece.
   *
   * @param pos position at which to set game piece
   * @param gp  game piece to set at position
   * @throws RuntimeException if position is not on game board
   */
  private void setGamePiece(Position pos, GamePiece gp) throws RuntimeException {
    try {
      layout.get(pos.row).set(pos.col, gp);
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeException("Tried to access index that is not on the board.");
    }
  }

  /**
   * Makes player face specified direction.
   *
   * @param direction direction for player to face
   * @return true if player changed directions, false otherwise
   */
  private boolean changePlayerDirection(Direction direction) {
    GamePiece playerBefore = player;
    player = getPlayerFromDirection(direction);
    setGamePiece(playerPosition, player);
    return playerBefore != player;
  }

  /**
   * Throws RuntimeException if this model does not have a level to perform operations on.
   *
   * @throws RuntimeException if model does not have a level set
   */
  private void requireLevel() throws RuntimeException {
    if (level == null) throw new RuntimeException("Tried to use model before loading level.");
  }

  /* Static methods ----------------------------------------------------------------------------- */

  /**
   * Returns a player facing the given direction.
   *
   * @param direction direction that player should be facing
   * @return player facing given direction
   */
  private static GamePiece getPlayerFromDirection(Direction direction) {
    return direction == Direction.LEFT ? GamePiece.PLAYER_LEFT : GamePiece.PLAYER_RIGHT;
  }

  /**
   * Returns a new position in which the column and row indices are shifted from the given position
   * by the given row and column offsets.
   *
   * @param pos       original position
   * @param colOffset difference in column index
   * @param rowOffset difference in row index
   * @return new position with shifted row and column indices
   */
  private static Position shiftPosition(Position pos, int colOffset, int rowOffset) {
    return new Position(pos.col + colOffset, pos.row + rowOffset);
  }
}
