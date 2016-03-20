package dod.game;

/**
 * An enum to handle the different compass Directions
 * 
 */
public enum CompassDirection {
    NORTH('N'), EAST('E'), SOUTH('S'), WEST('W');

    // This and the constructor handle text to enum conversion
    private char text;

    CompassDirection(char text) {
	this.text = text;
    }

    /**
     * @return a string representation of the compass direction
     */
    @Override
    public String toString() {
	// Convert to string
	return String.valueOf(this.text);
    }

    /**
     * @return the compass direction corresponding to the string
     * @throws NullPointerException
     *             , IllegalArgumentException
     */
    public static CompassDirection fromString(String string) {
	if (string == null) {
	    throw new NullPointerException();
	}

	if (string.length() != 1) {
	    throw new IllegalArgumentException("invalid compass direction");
	}

	return fromChar(string.charAt(0));
    }

    /**
     * Converts a char into the compass direction
     * 
     * @return the compass direction corresponding to the character
     * 
     * @throws NullPointerException
     *             , IllegalArgumentException
     */
    public static CompassDirection fromChar(char ch) {
	for (final CompassDirection direction : CompassDirection.values()) {
	    if (ch == direction.text) {
		return direction;
	    }
	}

	throw new IllegalArgumentException("invalid compass direction");
    }
}
