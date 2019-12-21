package blockdude.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of levels of the Block Dude game.
 */
public class LevelSet {
  private int currentLevelIndex;
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
    if (levels == null || levels.size() == 0)
      throw new IllegalArgumentException("Level list must be non-null and have >= 1 level.");
    currentLevelIndex = 0;
    this.levels = levels;
    this.passwords = passwords;
  }

  /**
   * Class for building LevelSets. Access is package private since LevelSets should only ever be
   * created by a LevelSetReader.
   */
  static class Builder {
    private final List<Level> levels;
    private final Map<String, Integer> passwords;
    private boolean invalidConfiguration;
    private String errorMessage;

    /**
     * Constructs a new LevelSet.Builder.
     */
    Builder() {
      levels = new ArrayList<>();
      passwords = new HashMap<>();
      invalidConfiguration = false;
      errorMessage = "";
    }

    /**
     * Adds a level to this builder and registers its password.
     *
     * @param level level to add to this builder
     */
    void addLevel(Level level) {
      String password = level.password();
      if (passwords.get(password) != null) {
        invalidConfiguration = true;
        errorMessage += "More than one level has the password '" + password + "';";
      }

      // make sure levels.size() is called before levels.add(level), or else off by 1 error
      passwords.put(level.password(), levels.size());
      levels.add(level);
    }

    /**
     * Builds a LevelSet using the levels added to this builder.
     *
     * @return a new LevelSet with the levels added to this builder
     * @throws IllegalStateException if cannot build level set as specified
     */
    LevelSet build() throws IllegalStateException {
      try {
        if (invalidConfiguration) throw new IllegalStateException();
        return new LevelSet(levels, passwords);
      } catch (IllegalArgumentException | IllegalStateException e) {
        if (e.getMessage() != null) errorMessage += e.getMessage();
        throw new IllegalStateException("Could not build LevelSet as specified: " + errorMessage);
      }
    }
  }

  /**
   * Returns current level of this level set.
   *
   * @return current level
   */
  public Level currentLevel() {
    return levels.get(currentLevelIndex);
  }

  /**
   * Returns index of current level.
   *
   * @return index of current level
   */
  public int currentLevelIndex() {
    return currentLevelIndex;
  }

  /**
   * Advances to and returns next level in this set, if possible. Throws ISE if on last level.
   *
   * @return next level if available
   * @throws IllegalStateException if on that last level / no next level
   */
  public Level nextLevel() throws IllegalStateException {
    if (currentLevelIndex == levels.size() - 1)
      throw new IllegalStateException("There is no next level.");
    currentLevelIndex++;
    return currentLevel();
  }

  /**
   * Restarts this level set to be at the first level.
   */
  public void restart() {
    currentLevelIndex = 0;
  }

  /**
   * Returns the level in this level set with the given password, if it exists.
   *
   * @param password password to guess
   * @return level with given password, if there is one, null otherwise
   */
  public Level tryPassword(String password) {
    if (!passwords.containsKey(password)) return null;
    currentLevelIndex = passwords.get(password);
    return currentLevel();
  }
}
