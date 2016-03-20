package dod.game.items;

/**
 * A class to represent a lantern, which the player holds to allow the player to
 * see farther.
 */
public class Lantern extends GameItem {

    @Override
    public boolean isRetainable() {
	// The lantern is retained
	return true;
    }

    @Override
    public int lookDistanceIncrease() {
	// The lantern increases the look distance by one
	return 1;
    }

    @Override
    public String toString() {
	return "lantern";
    }

    @Override
    public char toChar() {
	return 'L';
    }
}
