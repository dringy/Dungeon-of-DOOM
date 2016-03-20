package dod.game.items;

/**
 * A class to represent sword. So far this does nothing, but if attacking is
 * implemented, it could increase the attack potential of a player.
 */
public class Sword extends GameItem {
    @Override
    public boolean isRetainable() {
	// A sword is retained
	return true;
    }

    @Override
    public String toString() {
	return "sword";
    }

    @Override
    public char toChar() {
	return 'S';
    }
}
