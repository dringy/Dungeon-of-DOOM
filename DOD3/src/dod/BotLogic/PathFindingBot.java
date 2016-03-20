package dod.BotLogic;

import java.util.ArrayList;

import dod.Communicator.GameCommunicator;
import dod.game.CompassDirection;
import dod.game.Location;

/**
 * Represents a bot that can path find to things, it introduces a complex path finding algorithm and issues commands to the Game
 * @author Benjamin Dring
 */
public abstract class PathFindingBot extends Bot {
	
	/**
	 * The constructor for a path finding bot it sets up it's decision making processes and 
	 * Prepares communication with the game
	 * @param comm GameComunicator The communicator to the Game Logic Class
	 */
	public PathFindingBot(GameCommunicator comm) {
		super(comm);
	}
	
	protected ArrayList<CompassDirection> getShortestPathToTile(char tile)
	{
		ArrayList<CompassDirection> shortestPath = null;
		for (int row = 0; row < this.lookReply.length; row++)
		{
			for (int col = 0; col < this.lookReply[0].length; col++)
			{
				if (tile == lookReply[row][col])
				{
					ArrayList<CompassDirection> nextPath = pathFindTo(new Location(col, row));
					if (shortestPath == null)
					{
						shortestPath = nextPath;
					}
					else if ((shortestPath.size() > nextPath.size()) && (nextPath.size() > 0))
					{
						shortestPath = nextPath;
					}
				}
			}
		}
		return shortestPath;
	}
	
	/**
	 * This function finds the shortest path from one destination to another and gives it's output as a series of directions which
	 * if executed in order will take the player from one place to the other.
	 * @param destinationX int The row number of the tile you wish to get to
	 * @param destinationY int The column number of the tile you wish to get to
	 * @param startX int the row number you wish to start from
	 * @param startY the column number you wish to start from
	 * @param map the visible map that the execution will take place
	 * @param visibleMapSize int the size of a length of the visible map
	 * @return ArrayList<CompassDirection> contains all the necessary movement directions to complete the path, it returns null if there is no path.
	 */
	protected ArrayList<CompassDirection> pathFindTo(Location destinationLocation)
	{	
		Location playerLocation = getPlayerLocation();
		
		//This is a map that's the same size of the visible character map but stores integers instead
		//these integers hold the number of steps required to get from the destination to the point on the character map of the same indexe
		//by default it starts of as null (hence why Integer is used over int)
		Integer[][] stepCounterMap = new Integer[lookReply.length][lookReply[0].length];
		
		//The destination is of course 0 steps away so that is added in
		stepCounterMap[destinationLocation.getRow()][destinationLocation.getCol()] = 0;
		
		//This is an arrayList of all coordinates of the stepCounterMap that have been recorded, this list's order is very important for my algorithm
		ArrayList<Location> cordList = new ArrayList<Location>();
		//The destination coordinates are already present so they are put in the list
		cordList.add(destinationLocation);
		
		boolean pathFound = false; //Represents if a path is found yet
		int cordListIndex = 0; //The index that the loop is currently looking at in cordList
		
		//So this loops until either a path is found or we have run out of coordinates in the list
		//If we run out of coordinates it indicates that we found no path
		//This loop starts out with a single item in the list, but the list can grow.
		while ((!pathFound) && (cordListIndex < cordList.size()))
		{	
			//Firstly it gets the coordinates out from the list
			Location location = cordList.get(cordListIndex);
			
			//We get the numberOfSteps to the next position to be the number of steps to get to this position + 1
			int numberOfSteps = stepCounterMap[location.getRow()][location.getCol()] + 1;
			
			Location[] surroundingTiles = getSurroundingTiles(location); //This contains the 4 surrounding tiles north, south, east & west
			
			//It then loops through every tile one movement away
			for (Location nearbyTile : surroundingTiles)
			{
				//If that tile is the starting tile then we have mapped enough for our path so we break from the loop
				if ((nearbyTile.getRow() == playerLocation.getRow()) && (nearbyTile.getCol() == playerLocation.getCol()))
				{
					pathFound = true;
					break;
				}
				
				//Otherwise check to see if that direction is valid
				//Invalid tiles would includes walls, tiles out of bounds and other players
				//Also if a tile like that already exists and has a lower counter that means
				//we've already inspected that tile and got there in less steps so we say it's invalid
				//if we see the same tile with a higher counter then we have found a more efficient route
				//so the tile is valid and the function will remove the longer route from the list
				validPathAnalysis validPathResult = isValidPathFindingStep(lookReply, stepCounterMap, 
						nearbyTile, numberOfSteps, cordList, cordListIndex);
				
				//If the Coordinates are valid then we add it to the list and map the counter on the map
				if ((validPathResult == validPathAnalysis.Valid) || (validPathResult == validPathAnalysis.ValidWithDecrement))
				{
					cordList.add(nearbyTile);
					stepCounterMap[nearbyTile.getRow()][nearbyTile.getCol()] = numberOfSteps;
				}
				//If the function had to remove an element of CordLists that is before where our index is pointing
				//Then it will return ValidWithDecrement which means we have to decrement the cordListIndex
				//In order to make sure we're still looking through the list in order without skipping elements
				if (validPathResult == validPathAnalysis.ValidWithDecrement)
				{
					cordListIndex--;
				}
			}
			//We then move on to the next element (if it exists)
			cordListIndex++;
		}
		
		//If we ran out of coordinates and the list was finished then there is no visible path so we return null
		if(!pathFound)
		{
			return null;
		}
		
		//implied else
		
		//Now that we have mapped these step counters we can construct out path which will be in the form ArrayList<CompassDirection>
		ArrayList<CompassDirection> path = new ArrayList<CompassDirection>(); //The path we need to take
		
		//We will now start from the player's position and follow the smallest numbers we can find to get to our destination
		Location currentPositionInPath = new Location(playerLocation.getRow(), playerLocation.getCol()); //This is a copy not a reference
		
		//Every loop we move one space in the path until we reach the end
		while (true)
		{
			int minimunNearbyCounter = -1; //This represents the smallest counter we've found around us
			boolean nearbyDirectionFound = false; //this represents if we have found a direction to go yet
			CompassDirection nextMovement = null; //This holds the next movement we under take
			
			//We then get the locations for each of the positions around the current position in the path
			Location northCords = currentPositionInPath.atCompassDirection(CompassDirection.NORTH);
			Location southCords = currentPositionInPath.atCompassDirection(CompassDirection.SOUTH);
			Location eastCords = currentPositionInPath.atCompassDirection(CompassDirection.EAST);
			Location westCords = currentPositionInPath.atCompassDirection(CompassDirection.WEST);
			
			//isNextInPath checks if those coordinates are a good idea to follow, it checks if it's counter is mapped
			//and if it has a smaller counter number than we have found so far, multiple directions can pass hence
			//the lack of else-ifs (this occurs when there are multiple directions with different counters)
			
			//In each of the 4 directions if it is a good candidate for the next step in the pass the information is stored
			//in the relevant variables described within the while loop
			if (isNextInPath(northCords, stepCounterMap, lookReply, minimunNearbyCounter, false))
			{
				nearbyDirectionFound = true;
				minimunNearbyCounter = stepCounterMap[northCords.getRow()][northCords.getCol()];
				nextMovement = CompassDirection.NORTH;
				currentPositionInPath = northCords;
			}
			
			if (isNextInPath(southCords, stepCounterMap, lookReply, minimunNearbyCounter, nearbyDirectionFound))
			{
				nearbyDirectionFound = true;
				minimunNearbyCounter = stepCounterMap[southCords.getRow()][southCords.getCol()];
				nextMovement = CompassDirection.SOUTH;
				currentPositionInPath = southCords;
			}
			
			if (isNextInPath(eastCords, stepCounterMap, lookReply, minimunNearbyCounter, nearbyDirectionFound))
			{
				nearbyDirectionFound = true;
				minimunNearbyCounter = stepCounterMap[eastCords.getRow()][eastCords.getCol()];
				nextMovement = CompassDirection.EAST;
				currentPositionInPath = eastCords;
			}
			
			if (isNextInPath(westCords, stepCounterMap, lookReply, minimunNearbyCounter, nearbyDirectionFound))
			{
				nearbyDirectionFound = true;
				minimunNearbyCounter = stepCounterMap[westCords.getRow()][westCords.getCol()];
				nextMovement = CompassDirection.WEST;
				currentPositionInPath = westCords;
			}
			
			//after it checks all the directions it adds the smallest countered direction onto the path
			path.add(nextMovement);
			
			//The first part of the algorithm should prevent this happening but if it does then
			//we don't want to be in an indefinite loop
			if (nextMovement == null)
			{
				return null;
			}
			
			//If we have found a 0 counter that means we have found our destination
			//this means we can stop forming a path so we break from the loop
			if (minimunNearbyCounter == 0)
			{
				break;
			}
		}
		
		//it then returns the completed path
		return path;
		
	}
	
	/**
	 * Using information regarding the progress and comparisons in a path it makes an informed decision if the given coordinates would be
	 * a good candidate for the next moment
	 * @param cords Loaction The location you wish to be checked
	 * @param counterMap Integer[][] A map of all of the step counters from the destination to the start
	 * @param minimunCounterSoFar int The number of counters of the smallest countered direction it has looked at
	 * @param minimunCounterFound boolean Whether it has found a valid direction yet
	 * @return boolean true means it is a good candidate when false means it is not
	 */
	private boolean isNextInPath(Location cords, Integer[][] counterMap,char[][] map ,int minimunCounterSoFar, boolean minimunCounterFound)
	{
		//It checks to see the coordinates are in the range of the map
		if ((cords.getRow() >= 0) && (cords.getRow() < map.length) && 
				(cords.getCol() >= 0) && (cords.getCol() < map[0].length))
		{
			//It then gets the counter from the counterMap
			Integer counter = counterMap[cords.getRow()][cords.getCol()];
			//If those coordinates have not been mapped then we can return false as that cannot be the shortest route
			if (counter == null)
			{
				return false;
			}

			//If we have not found a direction yet then this is now a candidate and so can return true
			else if (!minimunCounterFound)
			{
				return true;
			}

			//If we have found a direction but this one has a smaller counter then we can return true
			//If it has a larger counter we return false by default
			else if (minimunCounterSoFar > counter)
			{
				return true;
			}
		}
		//implied else
		return false;
	}
	
	/**
	 * Gets The coordinates of all of the surrounding tiles in the 4 direction around the input coordinates. 
	 * @param cords Location The location you wish to centralise this function around
	 * @return Location[] an array of length 4 containing the locations
	 */
	private Location[] getSurroundingTiles(Location cords)
	{
		Location[] surroundingTiles = new Location[4];
		surroundingTiles[0] = cords.atCompassDirection(CompassDirection.NORTH);
		surroundingTiles[1] = cords.atCompassDirection(CompassDirection.SOUTH);
		surroundingTiles[2] = cords.atCompassDirection(CompassDirection.EAST);
		surroundingTiles[3] = cords.atCompassDirection(CompassDirection.WEST);
		return surroundingTiles;
	}
	
	/**
	 * The Results from validPathAnalysis It either replies with valid, invalid of ValidWithDecrmeents which means it is valid if you decrement
	 * the index of the list.
	 * @author Benjamin Dring
	 */
	private enum validPathAnalysis
	{
		Invalid, Valid, ValidWithDecrement;
	}
	
	/**
	 * Checks to see if a coordinates is set 
	 * @param map char[][] The Map of characters
	 * @param counterMap Integer[][] The map of step counters
	 * @param cords Location The location of the potential move
	 * @param currentCounter int The smallest counter we have seen so far
	 * @param cordList ArrayList<Location> The full path
	 * @param cordListIndex int The index of the cordList being observed
	 * @return validPathAnalysis An Enum with the results meaning different things
	 */
	private validPathAnalysis isValidPathFindingStep (char[][] map, Integer[][] counterMap, Location cords,
			int currentCounter, ArrayList<Location> cordList, int cordListIndex)
	{
		//First it checks to see if the coordinates are in the bounds of the map
		if ((cords.getRow() >= 0) && (cords.getRow() < map.length) && 
				(cords.getCol() >= 0) && (cords.getCol() < map[0].length))
		{
			//It then checks to see if that tile is a wall
			if (!doesBlock(cords))
			{
				//It gets the counter from the counter map
				Integer cordCounter = counterMap[cords.getRow()][cords.getCol()];
				//If the coordinates counter is not yet matched it is valid
				if(cordCounter == null)
				{
					return validPathAnalysis.Valid;
				}
				//implied else
				//if the current counter is smaller than the already mapped one then it is valid
				if (currentCounter < cordCounter)
				{
					//It needs to remove the old Coordinates from the list however
					//it loops through the entire list
					for (int listIndex = 0; listIndex < cordList.size(); listIndex++)
					{
						//It gets the coordinates from the list and checks to see if the coordinates match
						Location listElementCords = cordList.get(listIndex);
						if ((listElementCords.getCol() == cords.getCol()) && (listElementCords.getRow() == cords.getRow()))
						{
							//if they do match the coordinates are removed from the list
							cordList.remove(listIndex);
							//If the index of the removed element is less than the current index then we to decrement the index when we finish
							if (listIndex <= cordListIndex)
							{
								return validPathAnalysis.ValidWithDecrement;
							}
							else
							{
								return validPathAnalysis.Valid;
							}
						}
					}
				}
			}
		}
		//By default it is invalid
		return validPathAnalysis.Invalid;
	}
	
}