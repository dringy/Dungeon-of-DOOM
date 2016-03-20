package dod.game;

import java.util.ArrayList;
import java.util.List;

import dod.game.items.GameItem;
import dod.game.items.GameItemConsumer;

/**
 * Class representing a player
 * 
 * Note: Player variables should be kept as a part of the server functionality
 * in your coursework implementation, they are the game's internal
 * representation of a player, not used by the client side code.
 */
public class Player implements GameItemConsumer {
    private String name;

    // Does the player have a default name
    boolean defaultName = true;

    // The player may be "listened to" to interpret updates
    private final PlayerListener listener;

    // Location on the map
    private Location location;

    // How much gold they have, initially zero
    private int gold = 0;

    // Player attribute value things
    private int hp = 3;
    private int ap = 0;

    // Items the player has
    List<GameItem> items;

    // Constants
    // How many AP does a player have by default
    private static final int defaultAP = 6;

    // How many AP does the player lose per item
    private static final int apPenaltyPerItem = 1;

    // How far can a player see by default and with a lantern
    private static final int defaultLookDistance = 2;

    /**
     * Constructor for players
     * 
     * @param name
     *            the name of the player
     * @param location
     *            the location of the player
     * @param listener
     *            a player may be "listened to" for updates.
     */
    public Player(String name, Location location, PlayerListener listener) {
	this.name = name;
	this.location = location;

	// By default the player starts with nothing
	this.items = new ArrayList<GameItem>();

	this.listener = listener;

	// Reset the player's AP
	resetAP();
    }

    /**
     * Returns the name of the player
     * 
     * @return The name of the player
     */
    public String getName() {
	return this.name;
    }

    /**
     * Sets the name of the player
     * 
     * @param name
     *            The new name of the player
     * @throws CommandException
     */
    public void setName(String name) throws CommandException {
	if (!this.defaultName) {
	    throw new CommandException("player's name already set");
	}

	this.name = name;
	this.defaultName = false;
    }

    /**
     * Returns the current location of the player
     * 
     * @return player's location
     */
    public Location getLocation() {
	return this.location;
    }

    /**
     * Sets the player to a new location
     * 
     * @param location
     *            the player's new location
     */
    public void setLocation(Location location) {
	this.location = location;
    }

    /**
     * @return The amount of gold the player has
     */
    public int getGold() {
	return this.gold;
    }

    /**
     * Adds gold to the player
     * 
     * @param gold
     *            Amount of gold to give to the player
     */
    @Override
    public void addGold(int gold) {
	this.gold += gold;
	this.listener.treasureChange(gold);
    }

    /**
     * A player is dead if they have 0hp or less
     * 
     * @return true if the player is dead, false otherwise
     */
    public boolean isDead() {
	return (this.hp <= 0);
    }
    
    public void kill(){
    	this.hp = 0;
    }

    /**
     * @return The amount of HP the player has
     */
    public int getHp() {
	return this.hp;
    }

    /**
     * Increments the player's health, e.g. for health potion
     * 
     * @param hp
     *            The amount of hp to add ot the player
     */
    @Override
    public void incrementHealth(int hp) {
	this.hp += hp;
	this.listener.hpChange(hp);
    }
    
    /**
     * @author Benjamin Dring
     * removes hp from a player for an attack
     * @param hpLoss the hp that is lost
     */
    public void damage(int hpLoss)
    {
    	this.hp -= hpLoss;
    	this.listener.damage(hpLoss);
    }

    /**
     * Returns the amount of AP the player has
     * 
     * @return The amount of AP the player has
     */
    public int remainingAp() {
	return this.ap;
    }

    /**
     * Decreases the amount of AP the player has by 1
     */
    public void decrementAp() {
	this.ap--;
	assert this.ap >= 0;
    }

    /**
     * Set's the player's AP to zero instantly
     */
    @Override
    public void zeroAP() {
	this.ap = 0;
    }

    /**
     * Calculates the distances the player can see
     * 
     * @return the distance visible to the player
     */
    public int lookDistance() {
	int lookDistance = defaultLookDistance;

	// Some items, e.g. the lantern, may increase the look distance
	for (final GameItem item : this.items) {
	    lookDistance += item.lookDistanceIncrease();
	}

	return lookDistance;
    }

    /**
     * Returns true if a player can see a tile, based on the offset from the
     * player
     * 
     * @param rowOffset
     * @param colOffset
     * @return true if the player can see the tile with the specified offset.
     */
    public boolean canSeeTile(int rowOffset, int colOffset) {
	// This is based on the Manhattan distance

	final boolean canSeeTile = (Math.abs(rowOffset) + Math.abs(colOffset) <= lookDistance() + 1);
	return canSeeTile;
    }

    /**
     * Check if the player already has a given item type (e.g. any sword, not
     * just "that" sword)
     * 
     * @param item
     *            An instance of the item to compare with
     * @return true if the player has the item, false otherwise
     */
    public boolean hasItem(GameItem item) {
	for (final GameItem itemToCompare : this.items) {
	    if (item.getClass() == itemToCompare.getClass()) {
		return true;
	    }
	}

	return false;
    }

    /**
     * Gives the item to the player. The player allows the item to act on the
     * player (through this), and keeps it in the item list if it is retainable.
     * 
     * @param item
     *            the item to pick up
     */
    public void giveItem(GameItem item) {
	if (hasItem(item)) {
	    throw new IllegalStateException("the player already has this item.");
	}

	// The item may do something to the player straight away
	item.processPickUp(this);

	// See if the item is retained by the player
	if (item.isRetainable()) {
	    this.items.add(item);
	}
    }

    /**
     * @param message
     *            message to send to the listener
     */
    public void sendMessage(String message) {
	this.listener.sendMessage(message);
    }

    /**
     * Handle the start of a player's turn
     */
    public void startTurn() {
	resetAP();
	this.listener.startTurn();
    }

    /**
     * Handle the end of a player's turn
     */
    public void endTurn() {
	this.zeroAP();
	this.listener.endTurn();
    }

    /**
     * Handle the player winning
     */
    public void win() {
	this.listener.win();
    }

    /**
     * Reset the player's AP to the initial value.
     */
    private void resetAP() {
	this.ap = initialAP();
    }
    
    /**
     * @author Benjamin Dring
     * Causes the player to issue a look command
     */
    public void look(){
    	this.listener.look();
    }

    /**
     * Calculates the number of AP a player starts his or her turn with
     * 
     * @return The amount of AP at the start of a turn
     */
    private int initialAP() {
	final int initialAP = Player.defaultAP - Player.apPenaltyPerItem
		* this.items.size();

	if (initialAP < 0) {
	    // Better drop some items
	    return 0;
	}

	return initialAP;
    }
}