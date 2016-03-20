package dod.game;

/**
 * An exception to handle invalid commands
 */
public class CommandException extends Exception {
    private static final long serialVersionUID = -1965743877993357846L;

    public CommandException(String message) {
	super(message);
    }
}
