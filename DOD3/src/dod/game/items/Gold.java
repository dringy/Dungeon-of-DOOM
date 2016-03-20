package dod.game.items;

/**
 * An item to represent gold
 * 
 */
public final class Gold extends GameItem {
    @Override
    public void processPickUp(GameItemConsumer player) {
	// Give the player gold
	player.addGold(1);
    }

    @Override
    public boolean isRetainable() {
	// Gold increments the player gold count instantly, so isn't
	// "retainable" like
	// other objects
	return false;
    }

    @Override
    public String toString() {
	return "gold";
    }

    @Override
    public char toChar() {
	return 'G';
    }
}
