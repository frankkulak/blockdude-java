package blockdude.model;

import java.util.List;

// FIXME: POTENTIAL ISSUES WITH THIS MODEL
//        1) It is possible to move left, right, and up while holding a block, even if there is a
//           solid game piece directly above the player. This means that the block the player is
//           holding would overlap with the block above the player. I do not know the expected
//           behavior for this case, but it will never become an issue with the current levels.
//        2) It is possible to pick up a block while there is a block above the player. See above
//           for why this is an issue.

/**
 * Represents the classic Block Dude game model.
 */
public class ClassicBlockDudeModel implements BlockDudeModel {
  private Level level; // INVARIANT: never null once set
  private GamePiece player; // INVARIANT: will be one of PLAYER_LEFT, PLAYER_RIGHT, PLAYER_DOOR
  private GamePiece heldPiece; // might be null if player is not holding a piece
  private Position playerPosition; // INVARIANT: never has coors beyond limits of board
  private List<List<GamePiece>> layout; // INVARIANT: never null, correct for stored level
  private BlockDudeModelListener listener; // INVARIANT: never null

  /* Initialization ----------------------------------------------------------------------------- */

  /**
   * Constructs a new ClassicBlockDudeModel with an empty listener.
   */
  public ClassicBlockDudeModel() {
    listener = new EmptyListener();
  }

  /* Private classes/enums ---------------------------------------------------------------------- */

  /**
   * A private listener to use when no real listener has been assigned. Created to reduce number of
   * NullPointerExceptions if no listener is assigned to this model, making debugging easier.
   */
  private class EmptyListener implements BlockDudeModelListener {
    @Override
    public void gameWon() {
      throw new RuntimeException("No listener has been set for the model.");
    }
  }

  /**
   * Represents a direction the player can be facing and move in, either left or right.
   */
  private enum Direction {
    LEFT, RIGHT
  }

  /* Interface methods -------------------------------------------------------------------------- */

  @Override
  public void restartLevel() throws IllegalStateException {
    requireLevel();
    playerPosition = level.playerPosition();
    player = level.generatePlayer();
    heldPiece = null;
    layout = level.layout();
  }

  @Override
  public void loadLevel(Level level) throws IllegalArgumentException {
    if (level == null) throw new IllegalArgumentException("Cannot load null level into model.");
    this.level = level;
    restartLevel();
  }

  @Override
  public boolean moveLeft() throws IllegalStateException {
    return movePlayerHorizontally(Direction.LEFT);
  }

  @Override
  public boolean moveRight() throws IllegalStateException {
    return movePlayerHorizontally(Direction.RIGHT);
  }

  @Override
  public boolean moveUp() throws IllegalStateException {
    requireLevel();

    try {
      int playerRow = playerPosition.y;
      int playerCol = playerPosition.x;
      int newPlayerRow = playerRow - 1;
      GamePiece pieceAbove = getGamePieceAt(playerCol, newPlayerRow);

      // if the piece above the player is not solid, move the player up one block
      if (GamePiece.isSolid(pieceAbove)) throw new IllegalStateException("Unable to move up.");
      playerPosition.y = newPlayerRow;
      setGamePieceAt(GamePiece.EMPTY, playerCol, playerRow);
      setGamePieceAt(player, playerCol, newPlayerRow);

      // move the player horizontally the way it is facing
      Direction direction = getDirectionFromPlayer(player);
      boolean movedHorizontally = movePlayerHorizontally(direction);

      // if player couldn't move horizontally, place it back to where it was
      if (!movedHorizontally) {
        playerPosition.y++;
        setGamePieceAt(GamePiece.EMPTY, playerCol, newPlayerRow);
        setGamePieceAt(player, playerCol, playerRow);
        return false;
      }

      return true;
    } catch (ArrayIndexOutOfBoundsException | IllegalStateException e) {
      // no move up is possible since exception was thrown, so return false
      return false;
    }
  }

  @Override
  public boolean pickUp() throws IllegalStateException {
    requireLevel();

    try {
      // finding piece to pick up
      int colDif = (getDirectionFromPlayer(player) == Direction.LEFT ? -1 : 1);
      int pieceCol = playerPosition.x + colDif;
      int pieceRow = playerPosition.y;
      GamePiece pieceToSide = getGamePieceAt(pieceCol, pieceRow);

      // make sure the piece is solid
      if (!GamePiece.isSolid(pieceToSide))
        throw new IllegalStateException("Cannot pick up piece because it is not solid.");

      // make sure there is nothing on top of piece you're trying to pick up
      GamePiece aboveTarget = getGamePieceAt(pieceCol, pieceRow - 1);
      if (GamePiece.isSolid(aboveTarget))
        throw new IllegalStateException("Cannot pick up piece because it is under another piece.");

      // picking up piece, if possible (adds block to player directly)
      if (heldPiece != null)
        throw new IllegalStateException("Cannot pick up block because already holding one.");
      heldPiece = pieceToSide;

      // picking up piece was successful, so remove from where it was & return true
      setGamePieceAt(GamePiece.EMPTY, pieceCol, pieceRow);
      return true;
    } catch (ArrayIndexOutOfBoundsException | IllegalStateException e) {
      // something prevented block from being picked up
      // - AIOBE = tried to pick something up when player is at board edge
      // - ISE = player is already holding a block, or there is block on top of one trying to lift
      return false;
    }
  }

  @Override
  public boolean putDown() throws IllegalStateException {
    requireLevel();

    try {
      // check if player is even holding anything
      if (!playerIsHoldingSomething()) throw new IllegalStateException();

      // finding piece at side of player
      int colDif = (getDirectionFromPlayer(player) == Direction.LEFT ? -1 : 1);
      int targetCol = playerPosition.x + colDif;
      int targetRow = playerPosition.y;
      GamePiece pieceToSide = getGamePieceAt(targetCol, targetRow);

      // check if target place is solid or not
      if (GamePiece.isSolid(pieceToSide)) {
        // piece to side is solid, check if can place block above it
        targetRow--;
        GamePiece pieceAbovePieceToSide = getGamePieceAt(targetCol, targetRow);
        if (GamePiece.isSolid(pieceAbovePieceToSide)) return false;
      } else {
        // move down until piece hits a solid block
        GamePiece pieceBelow = getGamePieceAt(targetCol, targetRow + 1);
        while (!GamePiece.isSolid(pieceBelow)) {
          targetRow++;
          pieceBelow = getGamePieceAt(targetCol, targetRow + 1);
        }
      }

      // put piece down
      GamePiece pieceToPutDown = heldPiece;
      heldPiece = null;
      setGamePieceAt(pieceToPutDown, targetCol, targetRow);

      return true;
    } catch (ArrayIndexOutOfBoundsException | IllegalStateException e) {
      // something prevented block from being put down
      // - AIOBE = tried to put block down out of bounds
      // - ISE = player is not holding a block, or nowhere to put block
      return false;
    }
  }

  @Override
  public boolean playerIsHoldingSomething() throws IllegalStateException {
    requireLevel();
    return heldPiece != null;
  }

  @Override
  public List<List<GamePiece>> layout() throws IllegalStateException {
    this.requireLevel();
    return this.layout;
  }

  @Override
  public void setListener(BlockDudeModelListener listener) {
    if (listener == null) {
      this.listener = new EmptyListener();
    } else {
      this.listener = listener;
    }
  }

  /* Private methods ---------------------------------------------------------------------------- */

  /**
   * Moves player in specified direction. Will notify listener if game won.
   *
   * @param direction horizontal direction to move in
   * @return whether or not anything changed after making this move
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  private boolean movePlayerHorizontally(Direction direction) throws IllegalStateException {
    requireLevel();

    try {
      int changeInCol = (direction == Direction.LEFT ? -1 : 1);

      int playerRow = playerPosition.y;
      int playerCol = playerPosition.x;
      int newPlayerRow = playerRow;
      int newPlayerCol = playerCol + changeInCol;

      // this might throw ArrayIndexOutOfBoundsException
      GamePiece pieceToSide = getGamePieceAt(newPlayerCol, playerRow);

      // check if piece to side is solid, if so, player cannot move that way
      if (GamePiece.isSolid(pieceToSide)) throw new IllegalStateException("Cannot move that way.");

      // make the player face the correct direction
      changePlayerDirection(direction);

      // fixme: trying to access newPlayerRow + 1 might throw an ArrayIndexOutOfBoundsException
      // move down until the player hits a solid block
      GamePiece pieceBelow = getGamePieceAt(newPlayerCol, newPlayerRow + 1);
      while (!GamePiece.isSolid(pieceBelow)) {
        newPlayerRow++;
        pieceBelow = getGamePieceAt(newPlayerCol, newPlayerRow + 1);
      }

      // move player to target location
      GamePiece pieceEnteredByPlayer = getGamePieceAt(newPlayerCol, newPlayerRow);
      boolean gameWon = false;
      GamePiece pieceToUseForPlayer = player;
      switch (pieceEnteredByPlayer) {
        case DOOR:
          pieceToUseForPlayer = GamePiece.PLAYER_DOOR;
          gameWon = true;
          break;
        case EMPTY:
          break;
        default:
          // this should never be thrown - don't catch it, because if it is thrown, that's an issue
          throw new RuntimeException("Player tried to enter a block that cannot be entered.");
      }

      // set player to new position, remove player from old position
      playerPosition.x = newPlayerCol;
      playerPosition.y = newPlayerRow;
      this.setGamePieceAt(pieceToUseForPlayer, newPlayerCol, newPlayerRow);
      this.setGamePieceAt(GamePiece.EMPTY, playerCol, playerRow);

      // notify listener that game was won
      if (gameWon) listener.gameWon();

      return true;
    } catch (ArrayIndexOutOfBoundsException | IllegalStateException e) {
      // either the player tried to move off the board, or there is a solid block in the way of the
      // player moving in the specified direction - try to at least make the player face the
      // direction and return whether or not the player's direction has changed
      return changePlayerDirection(direction);
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
    return playerBefore != player;
  }

  /**
   * Returns a player facing the given direction.
   *
   * @param direction direction for player to face
   * @return player facing giving direction
   */
  private static GamePiece getPlayerFromDirection(Direction direction) {
    return direction == Direction.LEFT ? GamePiece.PLAYER_LEFT : GamePiece.PLAYER_RIGHT;
  }

  /**
   * Returns the direction in which the given player is facing.
   *
   * @param player player that is facing some direction
   * @return direction that player is facing
   */
  private static Direction getDirectionFromPlayer(GamePiece player) {
    return player == GamePiece.PLAYER_LEFT ? Direction.LEFT : Direction.RIGHT;
  }

  /**
   * Finds and returns GamePiece at given row and column. This is made to reduce confusion with x/y
   * and row/col line up and to therefore (hopefully) reduce bugs.
   *
   * @param col column of GP to retrieve
   * @param row row of GP to retrieve
   * @return GamePiece at given location
   * @throws ArrayIndexOutOfBoundsException if row or col exceeds bounds of layout
   */
  private GamePiece getGamePieceAt(int col, int row) throws ArrayIndexOutOfBoundsException {
    return this.layout.get(row).get(col);
  }

  /**
   * Sets value at given row and col to given GamePiece, if not null.
   *
   * @param gp  GamePiece to set value at indices to
   * @param col column of posn for new GamePiece
   * @param row row of posn for new GamePiece
   * @throws IllegalArgumentException       if given GamePiece is null
   * @throws ArrayIndexOutOfBoundsException if row or col exceeds bounds of layout
   */
  private void setGamePieceAt(GamePiece gp, int col, int row)
          throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
    if (gp == null) throw new IllegalArgumentException("Cannot set value to null GamePiece.");
    this.layout.get(row).set(col, gp);
  }

  /**
   * Throws ISE if this model does not have a level to perform operations on.
   *
   * @throws IllegalStateException if model does not have a level set
   */
  private void requireLevel() throws IllegalStateException {
    if (level == null) throw new IllegalStateException("Tried to use model before loading level.");
  }
}
