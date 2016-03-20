package dod.game;

/**
 * Class to refer to locations on the map to avoid having to store the row and
 * column separately. There are also some "helper" methods to assist code
 * readability
 */
public class Location {
    private final int row;
    private final int col;

    /**
     * Default constructor
     * 
     * @param row
     * @param col
     */
    public Location(int col, int row) {
	this.row = row;
	this.col = col;
    }

    /**
     * 
     * @return the row of the location
     */
    public int getRow() {
	return this.row;
    }

    /**
     * 
     * @return the column of the location
     */
    public int getCol() {
	return this.col;
    }

    /**
     * Returns a location offset to the current Location
     * 
     * @param colOffset
     *            the number of columns offset to the current location
     * @param rowOffset
     *            the number of rows offset to the current location
     * @return the new location
     */
    public Location atOffset(int colOffset, int rowOffset) {
	final Location offsetLocation = new Location(this.col + colOffset,
		this.row + rowOffset);

	return offsetLocation;
    }

    /**
     * Used to obtain the location immediately to the north, east, south or west
     * of the current location
     * 
     * @param direction
     *            the compass direction to look up.
     * @return the location immediately at the compass direction to the current
     *         location
     */
    public Location atCompassDirection(CompassDirection direction) {
	switch (direction) {
	case NORTH:
	    return atOffset(0, -1);

	case EAST:
	    return atOffset(1, 0);

	case SOUTH:
	    return atOffset(0, 1);

	case WEST:
	    return atOffset(-1, 0);

	default:
	    throw new RuntimeException("invalid compass direction");
	}
    }
}
