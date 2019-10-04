package blockdude.model.gamepieces;

/**
 * Represents a player standing in a door in the BlockDude game. This block is only used when a
 * level has been won, once the player has reached the door.
 */
public class PlayerInDoor extends SolidGamePiece {
  private final Player player;
  private final Door door;

  /**
   * Constructs a new PlayerInDoor object using given Player and Door.
   *
   * @param player player in the door
   * @param door door that player is standing in
   * @throws IllegalArgumentException if either argument is null
   */
  public PlayerInDoor(Player player, Door door) throws IllegalArgumentException {
    if (player == null || door == null) {
      throw new IllegalArgumentException("Neither player nor door may be null.");
    }

    this.player = player;
    this.door = door;
  }

  @Override
  public GamePiece copy() {
    return new PlayerInDoor((Player) this.player.copy(), (Door) this.door.copy()); // fixme yikes
  }

  /**
   * Determines and returns whether the player in this piece is facing left.
   *
   * @return whether or not player is facing left
   */
  public boolean isPlayerFacingLeft() {
    return this.player.isFacingLeft();
  }
}
