package blockdude.view;

/**
 * Represents a class that can help get a BlockDudeView the info that it needs.
 */
public interface BlockDudeViewHelper {
  /**
   * Returns the current level index.
   *
   * @return current level index
   */
  int currentLevelIndex();

  /**
   * Returns the current level password.
   *
   * @return current level password
   */
  String currentLevelPassword();
}
