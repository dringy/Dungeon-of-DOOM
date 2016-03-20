package dod.BotLogic;

import java.util.ArrayList;

import dod.Communicator.GameCommunicator;
import dod.game.CompassDirection;
import dod.game.Location;

public abstract class PlayerFindingBot extends PathFindingBot {
	
	/**
	 * The constructor for a player finding bot it sets up it's decision making processes and 
	 * Prepares communication with the game
	 * @param comm GameComunicator The communicator to the Game Logic Class
	 */
	public PlayerFindingBot(GameCommunicator comm) {
		super(comm);
	}
	
	/**
	 * Gets the surrounding tiles containing a player
	 * @param location Location the location of the tile this function works round
	 * @return ArrayList<CompassDirection)
	 */
	protected ArrayList<CompassDirection> getSurroundingPlayerDirections(Location location)
	{
		ArrayList<CompassDirection> surroundingPlayerDirections = new ArrayList<CompassDirection>();
		
		//Each direction
		Location northCords = location.atCompassDirection(CompassDirection.NORTH);
		Location southCords = location.atCompassDirection(CompassDirection.SOUTH);
		Location eastCords = location.atCompassDirection(CompassDirection.EAST);
		Location westCords = location.atCompassDirection(CompassDirection.WEST);
		
		//If there's a player there then add it to the list and return it
		if (isPlayerTile(northCords))
		{
			surroundingPlayerDirections.add(CompassDirection.NORTH);
		}
		if (isPlayerTile(southCords))
		{
			surroundingPlayerDirections.add(CompassDirection.SOUTH);
		}
		if (isPlayerTile(eastCords))
		{
			surroundingPlayerDirections.add(CompassDirection.EAST);
		}
		if (isPlayerTile(westCords))
		{
			surroundingPlayerDirections.add(CompassDirection.WEST);
		}
		
		//returns arraylist
		return surroundingPlayerDirections;
	}
	
	/**
	 * Gets the shortest path to a player on the look reply
	 * @return ArrayList<CompassDirection> The path formed by a series of directions to take
	 */
	protected ArrayList<CompassDirection> getShortestPathToPlayer()
	{
		//This can't just use PathTo as we want to get near a player not on the player
		//So we have to consider all next-to tiles
		
		ArrayList<CompassDirection> shortestPath = null;
		//Loops through every tile on the look reply
		for(int row = 0; row < lookReply.length; row++)
		{
			for(int col = 0; col < lookReply[0].length; col++)
			{
				//if the player is a tile
				if (isPlayerTile(lookReply[row][col]))
				{
					Location otherPlayerLocation = new Location(col, row);
					//Function call for each direction
					//Function works out which path is quickest
					//The quickest path is then placed in the variable shortestPath for future comparisons
					shortestPath = pathFindToAndCompare(otherPlayerLocation.atCompassDirection(CompassDirection.NORTH),
							shortestPath);
					shortestPath = pathFindToAndCompare(otherPlayerLocation.atCompassDirection(CompassDirection.SOUTH),
							shortestPath);
					shortestPath = pathFindToAndCompare(otherPlayerLocation.atCompassDirection(CompassDirection.EAST),
							shortestPath);
					shortestPath = pathFindToAndCompare(otherPlayerLocation.atCompassDirection(CompassDirection.WEST),
							shortestPath);
				}
			}
		}
		return shortestPath;
	}
	
	/**
	 * Checks if the tile has a player on it
	 * @param tile char the tile character
	 * @return boolean which indicates if the tile character matches a player
	 */
	protected boolean isPlayerTile(char tile)
	{
		return ((tile == 'P') || (tile == 'R') || (tile == 'K') || (tile == 'Q'));
	}
	
	/**
	 * Checks if the tile has a player on it
	 * @param tile Location the location of the tile
	 * @return boolean which indicates if the tile has a player on it
	 */
	private boolean isPlayerTile(Location location)
	{
		char tile = getTile(location);
		return isPlayerTile(tile);
	}
	
	/**
	 * Calculates a path to a given location and returns the shortest path of the one just worked out and an additional given path given.
	 * @param location The location for a path to be worked out
	 * @param shortestPath The current shortest path to compare with the new one
	 * @return ArrayList<CompassDirection> the shortest of the two paths
	 */
	private ArrayList<CompassDirection> pathFindToAndCompare(Location location, ArrayList<CompassDirection> shortestPath)
	{
		//If the location is out of bounds or is a wall then there is no path to it
		if ((location.getCol() < 0) || (location.getCol() >= lookReply.length) ||
				(location.getRow() < 0) || (location.getRow() >= lookReply[0].length) || (doesBlock(location)))
		{
			return shortestPath;
		}
		else
		{
			//Calculates the path
			ArrayList<CompassDirection> nextPath = pathFindTo(location);
			//Prioritises the non-null path
			if (shortestPath == null)
			{
				return nextPath;
			}
			else if (nextPath == null)
			{
				return shortestPath;
			}
			//Compares the number of movements in each path and decides on a result
			else if ((shortestPath.size() > nextPath.size()) && (nextPath.size() > 0))
			{
				return nextPath;
			}
			else
			{
				return shortestPath;
			}
		}
	}

}
