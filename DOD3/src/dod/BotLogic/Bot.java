package dod.BotLogic;

import java.util.ArrayList;
import java.util.Random;

import dod.Communicator.GameCommunicator;
import dod.game.CompassDirection;
import dod.game.Location;

/**
 * Represents a bot that will play the game.
 * It extends thread and so can be run in a new thread by calling .start()
 * @author Benjamin Dring
 */
public abstract class Bot extends Thread{
	private GameCommunicator comm; //The game communicator
	protected char[][] lookReply; //Stores the look reply in a two dimensional char array
	
	//Game information
	protected int goal;
	protected int currentGold;
	protected boolean hasLantern;
	private boolean myTurn;
	private boolean gameOver;
	
	private boolean updatedLook; //Indicates if the look has been updated
	private Location playerLocation; //Player location in the dungeon
	
	/**
	 * Sets up the bot for decision making
	 * @param comm GameCommunicator The GameCommunicator so it can send commands
	 */
	public Bot(GameCommunicator comm){
		//Sets the default values
		this.comm = comm;
		this.lookReply = null;
		this.goal = -1;
		this.currentGold = 0;
		this.myTurn = false;
		this.gameOver = false;
		this.updatedLook = false;
		this.playerLocation = null;
	}
	
	/**
	 * Interprets a message from the server
	 * @param message String the message from the server
	 */
	public void handelMessage(String message)
	{
		message = message.toUpperCase();
		//If the message is about the GOAL then we need to read off the Goal
		if (message.startsWith("GOAL"))
		{
			convertGoal(message);
		}
		//Sets the turn based on the turn messages
		else if (message.startsWith("STARTTURN"))
		{
			this.myTurn = true;
		}
		else if (message.startsWith("ENDTURN"))
		{
			this.myTurn = false;
		}
		//Other messages are just ignored by the bot
	}
	
	/**
	 * Stores the look reply and reacts to it
	 * @param lookReply String[] the look reply
	 */
	public void giveLookReply(String[] lookReply)
	{
		//Look reply is formated
		this.lookReply = formatLookReply(lookReply);
		//Players location may change if the lantern is picked up
		updatePlayerLocation();
		//This trigger will cause the thread to react, 
		// this is done using threading to avoid recursive memory depth
		this.updatedLook = true;
	}
	
	/**
	 * Decides how to react to the game and sends commands
	 */
	@Override
	public void run()
	{
		try
		{
			//Loops until the game ends
			while (!gameOver)
			{
				//Sleeps allow moves to happen in a human time scale
				//It also means that the polling of the right conditions
				//are not done constantly only every second
				Thread.sleep(1000);
				//Only act if the look has been updates, it is the players turn and 
				//the goal has been set
				if ((this.myTurn) && (this.goal >= 0) && (this.updatedLook))
				{
					this.updatedLook = false; //Look is set to be unupdated
					//Performs an action
					performAction();
				}
			}
		}
		catch (InterruptedException e)
		{
			//If interrupted just stop
			return;
		}
	}
	
	/**
	 * Kills the bot so the bot stops
	 */
	public void die()
	{
		//This effectively allows the run() function to terminate so the thread ends
		this.gameOver = true;
	}
	
	/**
	 * Checks if the user has the required gold to win
	 * @return boolean indicates if the user has the required gold
	 */
	protected boolean hasRequiredGold()
	{
		return (this.currentGold >= this.goal);
	}
	
	/**
	 * Gets a tile character form a given location in the look reply
	 * @param location Location the tile location
	 * @return char The tile character at the given location
	 */
	protected char getTile(Location location)
	{
		return lookReply[location.getRow()][location.getCol()];
	}
	
	/**
	 * Gets a random direction at a given location that is not blocking a player
	 * @param location Location the location to check
	 * @return CompassDirection the direction to move in, null is returned if none is available
	 */
	protected CompassDirection getRandomNonBlockDirection(Location location)
	{
		//All directions are addes to an array list
		ArrayList<CompassDirection> possibleDirections = new ArrayList<CompassDirection>();
		possibleDirections.add(CompassDirection.NORTH);
		possibleDirections.add(CompassDirection.SOUTH);
		possibleDirections.add(CompassDirection.EAST);
		possibleDirections.add(CompassDirection.WEST);
		
		//for each direction check if the location in that direction will block a player
		for (int index = 0; index < possibleDirections.size();)
		{
			CompassDirection direction = possibleDirections.get(index);
			if (doesBlock(location.atCompassDirection(direction)))
			{
				//If it does block then remove it	
				possibleDirections.remove(index);
			}
			else
			{
				//Only increment the index if nothing was removed
				index++;
			}
		}
		
		
		int listSize = possibleDirections.size();
		if (listSize < 1)
		{
			//If there was no directions return null
			return null;
		}
		else
		{
			//Random Index is made and direction is returned
			int RandomNumber = (new Random()).nextInt(listSize);
			return possibleDirections.get(RandomNumber);
		}
	}
	
	/**
	 * Gets the player's location
	 * @return Location the player's location on the look reply
	 */
	protected Location getPlayerLocation()
	{
		return this.playerLocation;
	}
	
	/**
	 * Converts a compass direction to a string that can be sent to the game
	 * @param direction CompassDirection the CompassDirection to be converted
	 * @return String The direction character
	 */
	protected String getDirectionCharacter(CompassDirection direction)
	{
		return direction.toString();
	}
	
	/**
	 * Checks if a character at a given location blocks a player
	 * @param location Location the location to check
	 * @return boolean that indicates if it blocks a player
	 */
	protected boolean doesBlock(Location location)
	{
		char tile = getTile(location);
		return ((tile == 'X')||(tile == '#')||(tile == 'P')||(tile == 'Q')||(tile == 'R')||(tile == 'K'));
	}
	
	/**
	 * Increments the gold counter
	 */
	protected void pickupGold()
	{
		currentGold++;
	}
	
	/**
	 * Updates the player location on the look reply
	 */
	private void updatePlayerLocation()
	{
		if (lookReply.length <= 0)
		{
			//Can only be done if there is a look reply
			this.playerLocation = null;
		}
		else
		{
			//Works out the half way point
			//Works a little differently for even sized maps but
			//there is no clear indication of what to do when that happens
			int row = (int) (Math.ceil(lookReply.length / 2.0) - 1);
			int col = (int) (Math.ceil(lookReply[0].length / 2.0) - 1);
			this.playerLocation = new Location(col, row);
		}
	}
	
	/**
	 * Converts the goal to an integer and stores it in its attribute
	 * @param string String the Goal String to be converted
	 */
	private void convertGoal(String string)
	{
		//GOAL part is removes and spaces are srtipped to leave just the number
		string = string.substring(4).replace(" ", "");
		try
		{
			//String is parsed
			this.goal = Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			//If there is an error set it to -1 which is recognised as an invalid value
			this.goal = -1;
		}
	}
	
	/**
	 * Formats the look reply into a form nicer for the bots to use, it also filters out the header and trailer
	 * @param lookReply String[] the given format of the string
	 * @return char[][] the new format for the look reply
	 */
	private char[][] formatLookReply(String[] lookReply)
	{
		//This function helped me reuse my code from CW1 as that is how the bot handled it
		if (lookReply.length < 2)
		{
			// look reply needs a body
			return null;
		}
		else
		{
			char[][] lookMap = new char[lookReply.length - 2][lookReply[1].length()];
			//header and trailer are not included in this
			for (int i = 0; i < lookReply.length - 2; i++)
			{
				lookMap[i] = lookReply[i+1].toCharArray();
			}
			return lookMap;
		}
	}
	
	/**
	 * Sends a command to the game
	 */
	private void performAction()
	{
		this.comm.sendMessageToGame(getAction());
	}
	
	/**
	 * Gets the command decided by the bot
	 * @return String the command
	 */
	abstract protected String getAction();
}
