package blockdude.model;

import java.util.List;

import blockdude.model.gamepieces.Empty;
import blockdude.model.gamepieces.GamePiece;
import blockdude.model.gamepieces.Player;
import blockdude.model.gamepieces.PlayerInDoor;
import blockdude.util.Position;

// FIXME code can be significantly improved if you abstract the methods for moving, have one common
// fixme - method for replacing pieces at certain index, and also have a method for moving them
// fixme - down until they hit a solid piece

/**
 * Represents the class BlockDude game model.
 */
public class ClassicBlockDudeModel implements BlockDudeModel {
  private Level level; // INVARIANT: never null
  private Player player; // INVARIANT: never null
  private Position playerPosn; // INVARIANT: never has coors beyond limits of board
  private List<List<GamePiece>> layout; // INVARIANT: never null, correct for stored level
  private BlockDudeModelListener listener; // INVARIANT: never null

  /**
   * Constructs a new ClassicBlockDudeModel with an empty listener.
   */
  public ClassicBlockDudeModel() {
    this.listener = new EmptyListener();
  }

  /**
   * A private listener to use when no real listener has been assigned. Created to reduce number of
   * NullPointerExceptions if no listener is assigned to this model.
   */
  private class EmptyListener implements BlockDudeModelListener {
    @Override
    public void gameWon() {
      // intentionally left blank
    }
  }

  @Override
  public void restartLevel() throws IllegalStateException {
    this.requireLevel();
    this.player = this.level.generatePlayer();
    this.playerPosn = this.level.getPlayerPosn();
    this.layout = this.level.getLayout();
  }

  @Override
  public void loadNewLevel(Level level) throws IllegalArgumentException {
    if (level == null) {
      throw new IllegalArgumentException("Cannot load null level into model.");
    }

    this.level = level;
    this.restartLevel();
  }

  @Override
  public boolean moveLeft() throws IllegalStateException {
    return this.movePlayer(true);
  }

  @Override
  public boolean moveRight() throws IllegalStateException {
    return this.movePlayer(false);
  }

  // fixme add something to detect if player has reached door, if so, create a PlayerInDoor and
  // fixme create a listener that you can notify that the game was won
  /**
   * Moves player in specified direction. Will notify listener if game won.
   *
   * @param moveToLeft whether or not player should move left
   * @return whether or not anything changed after making this move
   * @throws IllegalStateException if no level has been loaded into model yet
   */
  private boolean movePlayer(boolean moveToLeft) throws IllegalStateException {
    this.requireLevel();
    int changeInCol = (moveToLeft ? -1 : 1);
    try {
      // just storing these in variables to reduce confusion about x/y vs. row/col
      int startPlayerRow = this.playerPosn.y;
      int startPlayerCol = this.playerPosn.x;

      // this might throw AIOBE
      GamePiece pieceToSide = this.getGamePieceAt(startPlayerCol + changeInCol, startPlayerRow);

      // check if piece to side is solid
      if (pieceToSide.isSolid()) {
        // player can't move to this side, at least try to face this way
        return this.makePlayerFaceLeft(moveToLeft);
      } else {
        // player can move, so do so

        // first, face correct way
        this.makePlayerFaceLeft(moveToLeft);

        // move one block to the correct side
        int newPlayerRow = startPlayerRow;
        int newPlayerCol = startPlayerCol + changeInCol;

        // move down until hit solid block
        GamePiece pieceBelow = this.getGamePieceAt(newPlayerCol, newPlayerRow + 1);
        while (!pieceBelow.isSolid()) {
          newPlayerRow++;
          pieceBelow = this.getGamePieceAt(newPlayerCol, newPlayerRow + 1);
        }

        // find piece to insert where player should be (this might throw, but shouldn't)
        GamePiece toInsert = this.getGamePieceAt(newPlayerCol, newPlayerRow).enteredBy(this.player);

        // set player to new position, remove player from old position
        this.playerPosn.x = newPlayerCol;
        this.playerPosn.y = newPlayerRow;
        this.setGamePieceAt(toInsert, newPlayerCol, newPlayerRow);
        this.setGamePieceAt(new Empty(), startPlayerCol, startPlayerRow);

        // check if door reached, notify listener
        if (toInsert instanceof PlayerInDoor) {
          this.listener.gameWon();
        }

        return true;
      }
    } catch (ArrayIndexOutOfBoundsException e) { // fixme add UOE?
      // if player changes direction, then something changed, else just return false
      return this.makePlayerFaceLeft(moveToLeft);
    }
  }

  /**
   * Makes player face specified direction.
   *
   * @param faceLeft whether player should face left or not
   * @return whether player direction has actually changed
   */
  private boolean makePlayerFaceLeft(boolean faceLeft) {
    if (faceLeft) {
      return this.player.faceLeft();
    } else {
      return this.player.faceRight();
    }
  }

  @Override
  public boolean moveUp() throws IllegalStateException {
    // fixme add something to control for when player is holding block
    this.requireLevel();
    try {
      // finding change in column (-1 if moving left, +1 if moving right)
      int changeInCol = (this.player.isFacingLeft() ? -1 : 1);

      // finding all pieces needed to check if move is valid
      int newCol = this.playerPosn.x + changeInCol;
      int newRow = this.playerPosn.y - 1;
      GamePiece pieceToSide = this.getGamePieceAt(newCol, this.playerPosn.y);
      GamePiece pieceAbove = this.getGamePieceAt(this.playerPosn.x, newRow);
      GamePiece pieceToMoveTo = this.getGamePieceAt(newCol, newRow);

      // checking if up move is valid, if so make move, if not return false
      if (pieceToSide.isSolid() && (!pieceAbove.isSolid() && !pieceToMoveTo.isSolid())) {
        // find piece to insert where player should be (this might throw, but shouldn't)
        GamePiece toInsert = this.getGamePieceAt(newCol, newRow).enteredBy(this.player);

        // replacing target with new player piece
        this.setGamePieceAt(toInsert, newCol, newRow);

        // replacing player in layout with nothing
        this.setGamePieceAt(new Empty(), this.playerPosn.x, this.playerPosn.y);

        // setting new player position
        this.playerPosn.x = newCol;
        this.playerPosn.y = newRow;

        // check if door reached, notify listener
        if (toInsert instanceof PlayerInDoor) {
          this.listener.gameWon();
        }

        // returning true because move was successfully made
        return true;
      } else {
        // returning false because no move could be made
        return false;
      }
    } catch (ArrayIndexOutOfBoundsException e) { // fixme add UOE?
      // no move up is possible since exception was thrown, so return false
      return false;
    }
  }

  // todo moveFromTo(int fromCol, int fromRow, int toCol, int toRow)

  @Override
  public boolean pickUp() throws IllegalStateException {
    // fixme what if there are pieces above the player?
    this.requireLevel();
    try {
      // finding piece to pick up
      int pieceCol = this.playerPosn.x + (this.player.isFacingLeft() ? -1 : 1);
      int pieceRow = this.playerPosn.y;
      GamePiece pieceToSide = this.getGamePieceAt(pieceCol, pieceRow);

      // make sure there is nothing on top of piece you're trying to pick up
      GamePiece aboveTarget = this.getGamePieceAt(pieceCol, pieceRow - 1);
      if (aboveTarget.isSolid()) {
        throw new IllegalStateException("Cannot pick up piece because it is under another piece.");
      }

      // picking up piece, if possible (adds block to player directly)
      this.player.pickUpPiece(pieceToSide);

      // picking up piece was successful, so remove from where it was & return true
      this.setGamePieceAt(new Empty(), pieceCol, pieceRow);
      return true;
    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | IllegalStateException e) {
      // something prevented block from being picked up
      // - AIOBE = tried to pick something up when player is at board edge
      // - IAE = tried to pick up null piece or piece that cannot be picked up
      // - ISE = player is already holding a block, or there is block on top of one trying to lift
      return false;
    }
  }

  @Override
  public boolean putDown() throws IllegalStateException {
    this.requireLevel();
    try {
      // finding piece at side of player
      int targetCol = this.playerPosn.x + (this.player.isFacingLeft() ? -1 : 1);
      int targetRow = this.playerPosn.y;
      GamePiece pieceToSide = this.getGamePieceAt(targetCol, targetRow);

      // check if target place is solid or not
      if (pieceToSide.isSolid()) {
        // piece to side is solid, check if can place block above it
        targetRow--;
        GamePiece pieceAbovePieceToSide = this.getGamePieceAt(targetCol, targetRow);
        if (pieceAbovePieceToSide.isSolid()) {
          // nowhere to place block, return false
          return false;
        }
      } else {
        // piece to side is not solid fixme need to check if one above is also not solid?

        // move down until piece hits a solid block
        GamePiece pieceBelow = this.getGamePieceAt(targetCol, targetRow + 1);
        while (!pieceBelow.isSolid()) {
          targetRow++;
          pieceBelow = this.getGamePieceAt(targetCol, targetRow + 1);
        }
      }

      // place block in correct position
      GamePiece pieceToPutDown = this.player.putDownPiece();
      try {
        GamePiece toInsert = this.getGamePieceAt(targetCol, targetRow).enteredBy(pieceToPutDown);
        this.setGamePieceAt(toInsert, targetCol, targetRow);
        return true;
      } catch (UnsupportedOperationException uoe) {
        // tried to place block on door, indo putdown and return false
        this.player.pickUpPiece(pieceToPutDown);
        return false;
      }

    } catch (ArrayIndexOutOfBoundsException | IllegalStateException e) {
      // something prevented block from being put down
      // - AIOBE = tried to put block down out of bounds
      // - ISE = player is not holding a block, or nowhere to put block
      return false;
    }
  }

  @Override
  public int curLevelIndex() throws IllegalStateException {
    this.requireLevel();
    return this.level.index();
  }

  @Override
  public String curLevelPassword() throws IllegalStateException {
    this.requireLevel();
    return this.level.password();
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

  /**
   * Finds and returns GamePiece at given row and column. This is made to reduce confusion with
   * x/y and row/col line up and to therefore (hopefully) reduce bugs.
   *
   * @param col column of GP to retrieve
   * @param row row of GP to retrieve
   * @return GamePiece at given location
   * @throws ArrayIndexOutOfBoundsException if row or col exceeds bounds of layout
   */
  private GamePiece getGamePieceAt(int col, int row) throws ArrayIndexOutOfBoundsException {
    // will throw if out of bounds
    return this.layout.get(row).get(col);
  }

  /**
   * Sets value at given row and col to given GamePiece, if not null.
   *
   * @param gp GamePiece to set value at indices to
   * @param col column of posn for new GamePiece
   * @param row row of posn for new GamePiece
   * @throws IllegalArgumentException if given GamePiece is null
   * @throws ArrayIndexOutOfBoundsException if row or col exceeds bounds of layout
   */
  private void setGamePieceAt(GamePiece gp, int col, int row) throws IllegalArgumentException, ArrayIndexOutOfBoundsException {
    if (gp == null) {
      throw new IllegalArgumentException("Cannot set value to null GamePiece.");
    }

    // will throw if out of bounds
    this.layout.get(row).set(col, gp);
  }

  /**
   * Throws ISE if this model does not have a level to perform operations on.
   *
   * @throws IllegalStateException if model does not have level
   */
  private void requireLevel() throws IllegalStateException {
    if (this.level == null) {
      throw new IllegalStateException("Tried to manipulate model before loading level.");
    }
  }
}
