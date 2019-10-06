package blockdude.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of levels of the Block Dude game which are in a particular order.
 */
public class LevelSet {
  private int curLevelIndex;
  private final List<Level> levels;
  private final Map<String, Integer> passwords;

  /**
   * Constructs new LevelSet with given list of levels and map of passwords to indices.
   *
   * @param levels    list of levels to include in this set
   * @param passwords map of passwords to level indices
   * @throws IllegalArgumentException if given set is null or empty
   */
  private LevelSet(List<Level> levels, Map<String, Integer> passwords) throws IllegalArgumentException {
    if (levels == null || levels.size() < 1) {
      throw new IllegalArgumentException("Level list must be non-null and have >= 1 level.");
    }

    curLevelIndex = 0;
    this.levels = levels;
    this.passwords = passwords;
  }

  /**
   * Class for building LevelSets.
   */
  public static class Builder {
    private final List<Level> levels;
    private final Map<String, Integer> passwords;

    public Builder() {
      levels = new ArrayList<>();
      passwords = new HashMap<>();
    }

    public Builder addLevel(Level level) {
      // make sure levels.size() is called before levels.add(level)
      passwords.put(level.password(), levels.size());
      levels.add(level);
      return this;
    }

    public LevelSet build() throws IllegalStateException {
      try {
        return new LevelSet(levels, passwords);
      } catch (IllegalArgumentException e) {
        throw new IllegalStateException("Tried to build LevelSet without adding any levels.");
      }
    }
  }

  /**
   * Returns current level of this level set.
   *
   * @return current level
   */
  public Level curLevel() {
    return levels.get(curLevelIndex);
  }

  /**
   * Returns index of current level.
   *
   * @return index of current level
   */
  public int curLevelIndex() {
    return curLevelIndex;
  }

  /**
   * Advances to and returns next level in this set, if possible. Throws ISE if on last level.
   *
   * @return next level if available
   * @throws IllegalStateException if on that last level / no next level
   */
  public Level nextLevel() throws IllegalStateException {
    if (curLevelIndex == levels.size() - 1) {
      throw new IllegalStateException("There is no next level.");
    } else {
      curLevelIndex++;
      return curLevel();
    }
  }

  /**
   * Restarts this level set to be at the first level.
   */
  public void restart() {
    curLevelIndex = 0;
  }

  /**
   * Tries password on this level set; if password exists, returns level, if not, throws IAE.
   *
   * @param password password to guess
   * @return level with given password
   * @throws IllegalArgumentException if no level has given password
   */
  public Level tryPassword(String password) throws IllegalArgumentException {
    if (!passwords.containsKey(password)) {
      throw new IllegalArgumentException("Level with password '" + password + "' does not exist.");
    }

    curLevelIndex = passwords.get(password);
    return curLevel();
  }
}
