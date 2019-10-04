package blockdude.model.gamepieces;

/**
 * Represents the player of the BlockDude game. Players can move either left or right into open game
 * pieces, or they may move up in the direction they are facing if there is a solid piece in front
 * of them and no solid piece directly above them or the piece they are facing. Players may also
 * pick up and put down pieces that are able to be picked up by players.
 */
public final class Player extends SolidGamePiece {
  private boolean facingLeft;
  private GamePiece pieceBeingHeld;

  /**
   * Constructs a new Player object using given boolean to determine facing orientation.
   *
   * @param facingLeft whether or not player should face left
   */
  public Player(boolean facingLeft) {
    this.facingLeft = facingLeft;
    this.pieceBeingHeld = null;
  }

  @Override
  public GamePiece copy() {
    // copying facing direction
    Player copy = new Player(this.facingLeft);

    // copying piece that is being held
    if (this.pieceBeingHeld != null) {
      copy.pieceBeingHeld = this.pieceBeingHeld.copy();
    }

    return copy;
  }

  /**
   * Determines and returns whether this payer is facing left.
   *
   * @return whether or not this player is facing left
   */
  public boolean isFacingLeft() {
    return this.facingLeft;
  }

  /**
   * Changes the facing direction of this player to the left.
   *
   * @return whether or not facing direction has changed
   */
  public boolean faceLeft() {
    return this.changeFacingDirectionToLeft(true);
  }

  /**
   * Changes the facing direction of this player to the right.
   *
   * @return whether or not facing direction has changed
   */
  public boolean faceRight() {
    return this.changeFacingDirectionToLeft(false);
  }

  /**
   * Changes facing direction of this player.
   *
   * @param faceLeft whether or not to face left
   * @return whether or not facing direction has changed
   */
  private boolean changeFacingDirectionToLeft(boolean faceLeft) {
    if (this.facingLeft == faceLeft) {
      return false;
    } else {
      this.facingLeft = faceLeft;
      return true;
    }
  }

  /**
   * Determines and returns whether this player is holding a game piece.
   *
   * @return whether or not player is holding a game piece
   */
  public boolean isHoldingSomething() {
    return this.pieceBeingHeld != null;
  }

  /**
   * Picks up given game piece, is able to do so.
   *
   * @param gp block for player to pick up
   * @throws IllegalArgumentException if given block is null or unable to be picked up
   * @throws IllegalStateException is player is already holding a piece
   */
  public void pickUpPiece(GamePiece gp) throws IllegalArgumentException, IllegalStateException {
    if (gp == null || !gp.canPickUp()) {
      throw new IllegalArgumentException("Given GamePiece is not able to be picked up.");
    } else if (this.pieceBeingHeld != null) {
      throw new IllegalStateException("Player is already holding a GamePiece.");
    } else {
      this.pieceBeingHeld = gp;
    }
  }

  /**
   * Puts down current piece that player is holding.
   *
   * @return piece that player put down
   * @throws IllegalStateException if player does not have block to put down
   */
  public GamePiece putDownPiece() throws IllegalStateException {
    if (this.pieceBeingHeld == null) {
      throw new IllegalStateException("Player does not have GamePiece to put down.");
    } else {
      GamePiece gp = this.pieceBeingHeld;
      this.pieceBeingHeld = null;
      return gp;
    }
  }
}
