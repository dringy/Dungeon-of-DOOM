package dod.game;

import dod.game.items.GameItem;

/**
 * An class to represent the tiles on the Map.
 */
public class Tile {

    // An enum to handle the different tile types
    public enum TileType {
	FLOOR('.', true), WALL('#', false), EXIT('E', true);

	// The textual representation of the character
	private final char character;

	// Whether or not the character can be walked on
	private final boolean walkable;

	private TileType(char character, boolean walkable) {
	    this.character = character;
	    this.walkable = walkable;
	}

	public boolean walkable() {
	    return this.walkable;
	}

	public char toChar() {
	    return this.character;
	}
    }

    // The type of the tile
    private final TileType type;

    // A tile may contain an item
    private GameItem item = null;

    /**
     * Creates a tile without an item
     * 
     * @param type
     *            the type of tile to create
     */
    public Tile(TileType type) {
	this.type = type;
    }

    /**
     * Creates a tile with an item
     * 
     * @param item
     *            the item to add to the tile
     */
    public Tile(GameItem item) {
	// Only a floor tile can have an item
	this(TileType.FLOOR);

	this.item = item;
    }

    /**
     * Turn the tile into a char
     * 
     * @return the char corresponding to the tile
     */
    public char toChar() {
	if (!hasItem()) {
	    return this.type.toChar();
	} else {
	    return this.item.toChar();
	}
    }

    /**
     * Check if the tile can be walked on.
     * 
     * @return true if the tile can be walked on
     */
    public boolean isWalkable() {
	return this.type.walkable();
    }

    /**
     * @return true if the square is an exit square
     */
    public boolean isExit() {
	return (this.type == TileType.EXIT);
    }

    /**
     * Check if the tile has the item
     * 
     * @return true if there is an item on the tile
     */
    public boolean hasItem() {
	return (this.item != null);
    }

    /**
     * Gets the tile on the item, but *does not remove it*. This allows us to
     * see if the player already has the item, without removing it.
     * 
     * @return the item on the tile
     */
    public GameItem getItem() {
	return this.item;
    }

    /**
     * Removes the item from the tile, e.g. after a successful pickup.
     */
    public void removeItem() {
	if (this.item == null) {
	    // There is no item to pick up
	    throw new IllegalStateException("there is no item to pick up");
	} else {
	    this.item = null;
	}
    }

    /**
     * Turn a character into a map tile
     * 
     * @param ch
     *            the character representing the map tile
     * @return the Tile object corresponding to the character
     */
    public static Tile fromChar(char character) {
	for (final TileType type : TileType.values()) {
	    if (character == type.toChar()) {
		return new Tile(type);
	    }
	}

	// If we get here, it must be an tile with an item
	return new Tile(GameItem.fromChar(character));
    }
}
