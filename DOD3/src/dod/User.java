package dod;

import dod.game.CommandException;
import dod.game.CompassDirection;
import dod.game.GameLogic;
import dod.game.PlayerListener;

/**
 * Represents a generic user and has methods that is common for both local and network users.
 * Some methods are common but implemented in different ways and so are abstract.
 * Much of this code has been adjusted to be compatible with client and servers.
 * @author Benjamin Dring
 */
public abstract class User implements PlayerListener{
	// The game which the command line user will operate on.
    // This is protected to enforce the use of "processCommand".
    protected final GameLogic game;
    
    //Used to identify users from each other
    protected int userID;
    
    private boolean didUserWin; //indicates if a user has won
    
    private boolean goalSent; //indicates if a goal message has been sent
    
    private static int autoAsignPlayerNumber = 0; //for when a name is not supplied
    
    /**
     * Sets up the user and adds their character to the game
     * @param game GameLogic the game for the user to interact with
     */
    public User(GameLogic game){
    	this.game = game;

    	// Ensures that the instance will listen to the player in the
    	// game for messages from the game
    	userID = game.addPlayer(this);
    	this.didUserWin = false;
    	this.goalSent = false;
    }
    
    /**
     * Sends a message to the player from the game.
     * 
     * @ param message The message to be sent
     */
    @Override
    public void sendMessage(String message) {
	outputMessage(message);
    }
    
    /**
     * Informs the user of the beginning of a player's turn
     */
    @Override
    public void startTurn() {
	outputMessage("STARTTURN");
    }

    /**
     * Informs the user of the end of a player's turn
     */
    @Override
    public void endTurn() {
	outputMessage("ENDTURN");
    }

    /**
     * Informs the user that the player has won
     */
    @Override
    public void win() {
	outputMessage("DIE You Won!");
	this.didUserWin=true;
    }

    /**
     * Informs the user that the player's hit points have changed
     */
    @Override
    public void hpChange(int value) {
	outputMessage("HITMOD " + value);
    }
    
    /**
     * A new method that is called when the user is hit by a player.
     */
    @Override
    public void damage(int value)
    {
    	outputMessage("You were hit and lost " + value + " hp.");
    }

    /**
     * Informs the user that the player's gold count has changed
     * @author Benjamin Dring
     */
    @Override
    public void treasureChange(int value) {
	outputMessage("TREASUREMOD " + value);
    }
    
    /**
     * Sends a look reply
     */
    @Override
    public void look() {
    	outputMessage("LOOKREPLY" + System.getProperty("line.separator")
    		    + this.game.clientLook(this.userID) + "ENDLOOKREPLY");
    }
    
    /**
     * Processes a text command from the user.
     * 
     * @param commandString
     *            the string containing the command and any argument
     */
    protected final void processCommand(String commandString) {
    	
    	
	// converts to uppercase
	commandString = commandString.toUpperCase();

	// Process the command string e.g. MOVE N
	final String commandStringSplit[] = commandString.split(" ", 2);
	final String command = commandStringSplit[0];
	final String arg = ((commandStringSplit.length == 2) ? commandStringSplit[1]
		: null);

	try {
	    processCommandAndArgument(command, arg);
	} catch (final CommandException e) {
	    outputMessage("FAIL " + e.getMessage());
	}
    }
    
    /**
     * Processes the command and an optional argument
     * This has been partially modified by Benjamin Dring
     * 
     * @param command
     *            the text command
     * @param arg
     *            the text argument (null if no argument)
     * @throws CommandException
     */
    private void processCommandAndArgument(String command, String arg)
	    throws CommandException {
	if (command.equals("HELLO")) {
	    if (arg == null) {
		throw new CommandException("HELLO needs an argument");
	    }
	    String name = sanitiseMessage(arg);
	    
	    //Gives a name if the name string is empty
	    if (name.replace(" ", "").equals(""))
	    {
	    	name = "Player " + (++autoAsignPlayerNumber);
	    }
	    
	    this.game.clientHello(name, userID);
	    outputMessage("HELLO " + name);
	    if (!goalSent)
	    {
	    	//Goal is sent to the player
	    	goalSent = true;
	    	outputMessage("GOAL " + this.game.getGoal());
	    }
	    if ((game.isPlayerTurn(userID)) && (game.hasGameStarted()))
	    {
	    	//If it is the players turn then we need to send a message to the user
	    	startTurn();
	    }
	    //Informs everyone the user has joines
	    this.game.sendToAll(name + " has joined the game.");
	} 
	else if (command.equals("LOOK")) {
	    if (arg != null) {
		throw new CommandException("LOOK does not take an argument");
	    }
	    game.lookAll();

	}
	else if (command.equals("DIE"))
	{
		game.die(userID);
		game.lookAll();
	}
	
	else if (command.equals("SHOUT")) {
	    // Ensure they have given us something to shout.
	    if (arg == null) {
		throw new CommandException("need something to shout");
	    }

	    this.game.clientShout(sanitiseMessage(arg), this.userID);

	}
	
	/**
	 * @author Benjamin Dring
	 * Makes sure that it is currently the user's turn if it isn't it throws an exception
	 */
	else if(!game.hasGameStarted())
	{
		throw new CommandException("Game has not started");
	}
	else if (!game.isPlayerTurn(userID))
    {
    	throw new CommandException("It is not your turn");
    }
	/**
	 * End of work by Benjamin Dring
	 */
    	
	 else if (command.equals("PICKUP")) {
	    if (arg != null) {
		throw new CommandException("PICKUP does not take an argument");
	    }
	    this.game.clientPickup();
	    game.lookAll();
	    outputSuccess();

	} else if (command.equals("MOVE")) {
	    // We need to know which direction to move in.
	    if (arg == null) {
		throw new CommandException("MOVE needs a direction");
	    }

	    this.game.clientMove(getDirection(arg));
	    game.lookAll();
	    outputSuccess();

	} else if (command.equals("ATTACK")) {
	    // We need to know which direction to move in.
	    if (arg == null) {
		throw new CommandException("ATTACK needs a direction");
	    }

	    this.game.clientAttack(getDirection(arg));
	    game.lookAll();
	    outputSuccess();
	    
	} else if (command.equals("GIFT")) {
		if (arg == null) {
		throw new CommandException("ATTACK needs a direction");
		}

		this.game.clientGift(getDirection(arg));
		outputSuccess();
	    
	} else if (command.equals("ENDTURN")) {
	    this.game.newTurn();

	}  else if (command.equals("SETPLAYERPOS")) {
	    if (arg == null) {
		throw new CommandException("need a position");
	    }

	    // Obtain two co-ordinates
	    final String coordinates[] = arg.split(" ");

	    if (coordinates.length != 2) {
		throw new CommandException("need two co-ordinates");
	    }

	    try {
		final int col = Integer.parseInt(coordinates[0]);
		final int row = Integer.parseInt(coordinates[1]);

		this.game.setPlayerPosition(col, row);
		game.lookAll();
		outputSuccess();
	    } catch (final NumberFormatException e) {
		throw new CommandException("co-ordinates must be integers");
	    }

	} else {
	    // If it is none of the above then it must be a bad command.
	    throw new CommandException("invalid command");
	}
    }
    


    /**
     * Obtains a compass direction from a string. Used to ensure the correct
     * exception type is thrown, and for consistency between MOVE and ATTACK.
     * 
     * @param string
     *            the direction string
     * 
     * @return the compass direction
     * @throws CommandException
     */
    private CompassDirection getDirection(String string)
	    throws CommandException {
	try {
	    return CompassDirection.fromString(string);
	} catch (final IllegalArgumentException e) {
	    throw new CommandException("invalid direction");
	}
    }
    
    /**
     * Sanitises the given message - there are some characters that we can put
     * in the messages that we don't want in other stuff that we sanitise.
     * 
     * @param s
     *            The message to be sanitised
     * @return The sanitised message
     */
    private static String sanitiseMessage(String s) {
	return sanitise(s, "[a-zA-Z0-9-_ \\.,:!\\(\\)#]");
    }

    /**
     * Strip out anything that isn't in the specified regex.
     * 
     * @param s
     *            The string to be sanitised
     * @param regex
     *            The regex to use for sanitisiation
     * @return The sanitised string
     */
    private static String sanitise(String s, String regex) {
	String rv = "";

	for (int i = 0; i < s.length(); i++) {
	    final String tmp = s.substring(i, i + 1);

	    if (tmp.matches(regex)) {
		rv += tmp;
	    }
	}

	return rv;
    }
    
    /**
     * Sends a success message in the event that a command has succeeded
     */
    private void outputSuccess() {
    	//I chose to remove this
    	//outputMessage("SUCCESS");
    }
    
    /**
     * Outputs a message to the user .
     * Abstract method was made by Benjamin Dring.
     * @param message the message to be out put
     */
    abstract protected void outputMessage(String message);
    
    /**
     * Works out if the game is over if it is then messages are sent informing the user so
     * @return boolean Indicates if the game is over
     */
    protected boolean isGameOver(){
    	if (game.isGameOver())
    	{
    		if(!didUserWin)
    		{
    			outputMessage("DIE You Lost");
    		}
    		outputMessage("DIE You Won!");
    		return true;
    	}
    	return false;
    }
    
}
