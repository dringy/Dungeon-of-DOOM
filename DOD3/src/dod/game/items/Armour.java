package dod.game.items;

/**
 * A class to represent armour. So far this does nothing, but if attacking is
 * implemented, it could defend the player in the case of an attack.
 */
public class Armour extends GameItem {
    @Override
    public boolean isRetainable() {
	// A sword is retained
	return true;
    }

    @Override
    public String toString() {
	return "armour";
    }

    @Override
    public char toChar() {
	return 'A';
    }
}
