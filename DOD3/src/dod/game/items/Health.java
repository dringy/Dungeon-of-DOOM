package dod.game.items;

/**
 * A class to represent health "potion", which is consumed immediately and gives
 * the player an extra HP.
 */
public class Health extends GameItem {

    @Override
    public void processPickUp(GameItemConsumer player) {
	player.incrementHealth(1);
    }

    @Override
    public boolean isRetainable() {
	// Health potion is consumed instantly
	return false;
    }

    @Override
    public char toChar() {
	return 'H';
    }

    @Override
    public String toString() {
	return "health potion";
    }

}
