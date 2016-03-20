package dod.game.items;

/**
 * A class to represent the items on the map.
 * 
 * For now, items can only act on the player by changing their look distance,
 * but more features could be added in the future, such as increasing attack
 * points or defence against attack.
 */

public abstract class GameItem {
    /**
     * Process the action of picking up an item.
     * 
     * @param player
     *            The player who picks up the object
     */
    public void processPickUp(GameItemConsumer player) {
	// By default, do nothing
    }

    /**
     * Checks if the item can be "retained" by the player, i.e., the player
     * doesn't consume it instantly (like health) but holds on to it, like
     * sword.
     * 
     * @return true if the item is retained
     */
    public abstract boolean isRetainable();

    /**
     * Allows an item to change the distance a player can see.
     * 
     * @returns the increase (or decrease) in the player's look distance.
     */
    public int lookDistanceIncrease() {
	// Only retainable items will be able to affect the distances
	assert isRetainable();

	// Return zero by default
	return 0;
    }

    /**
     * Obtains a character representing the item, used by the textual interface.
     * 
     * @return a single character
     */
    public abstract char toChar();

    public static GameItem fromChar(char ch) {
	switch (ch) {
	case 'A':
	    return new Armour();

	case 'G':
	    return new Gold();

	case 'H':
	    return new Health();

	case 'L':
	    return new Lantern();

	case 'S':
	    return new Sword();

	default:
	    throw new IllegalArgumentException("Invalid tile type" + ch);
	}
    }
}
