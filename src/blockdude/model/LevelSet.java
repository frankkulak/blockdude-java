package blockdude.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of levels of the BlockDude game which are in a particular order. This set allows
 * for levels to be iterated through in order.
 */
public class LevelSet {
  private final int startingIndex;
  private int curLevelIndex;
  private final List<Level> levels;

  /**
   * Constructs new LevelSet object with given list of levels and starting index.
   *
   * @param levels list of levels to include in this set
   * @param startingIndex index to start from
   * @throws IllegalArgumentException if given set is null or empty or if index isn't in levels
   */
  private LevelSet(List<Level> levels, int startingIndex) throws IllegalArgumentException {
    if (levels == null || levels.size() < 1) {
      throw new IllegalArgumentException("Level list must be non-null and have >= 1 level.");
    } else if (startingIndex >= levels.size() || startingIndex < 0) {
      throw new IllegalArgumentException("Starting index does not exist in level set.");
    }

    this.startingIndex = startingIndex;
    this.curLevelIndex = startingIndex;
    this.levels = levels;
  }

  /**
   * Class for building LevelSet objects.
   */
  public static class Builder {
    private int startingIndex;
    private final List<Level> levels;

    public Builder() {
      // const will throw IAE if not added to
      this.startingIndex = 0;
      this.levels = new ArrayList<>();
    }

    public Builder setStartingIndex(int index) {
      this.startingIndex = index;
      return this;
    }

    public Builder addLevel(Level level) {
      this.levels.add(level);
      return this;
    }

    public LevelSet build() throws IllegalStateException {
      try {
        return new LevelSet(this.levels, startingIndex);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("Could not build LevelSet as specified.");
      }
    }
  }

  /**
   * Returns current level of this level set.
   *
   * @return current level
   */
  public Level curLevel() {
    return this.levels.get(this.curLevelIndex);
  }

  /**
   * Advances to and returns next level in this set, if possible. Throws ISE if on last level.
   *
   * @return next level if available
   * @throws IllegalStateException if on that last level / no next level
   */
  public Level nextLevel() throws IllegalStateException {
    if (this.curLevelIndex == this.levels.size() - 1) {
      throw new IllegalStateException("There is no next level.");
    } else {
      this.curLevelIndex++;
      return this.levels.get(this.curLevelIndex);
    }
  }

  /**
   * Restarts this level set to be at the first level, then returns the first level.
   *
   * @return first level of this level set
   */
  public Level restart() {
    this.curLevelIndex = this.startingIndex;
    return this.curLevel();
  }

  /**
   * Tries password on this level set; if password exists, returns level, if not, throws IAE.
   *
   * @param password password to guess
   * @return level with given password
   * @throws IllegalArgumentException if no level has given password
   */
  public Level tryPassword(String password) throws IllegalArgumentException {
    // fixme this is linear time, maybe organize levels better by password in tree?
    for (int i = 0; i < this.levels.size(); i++) {
      Level level = this.levels.get(i);
      if (level.tryPassword(password)) {
        this.curLevelIndex = i;
        return level;
      }
    }

    throw new IllegalArgumentException("Password \"" + password + "\" is not valid.");
  }
}
