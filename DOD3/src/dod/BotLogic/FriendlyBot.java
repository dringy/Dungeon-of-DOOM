package dod.BotLogic;

import java.util.ArrayList;
import java.util.Random;

import dod.Communicator.GameCommunicator;
import dod.game.CompassDirection;
import dod.game.Location;

public class FriendlyBot extends PlayerFindingBot {
	private boolean hasArmour;
	
	/**
	 * The constructor for the friendly bot it sets up it's decision making processes and 
	 * Prepares communication with the game
	 * @param comm GameComunicator The communicator to the Game Logic Class
	 */
	public FriendlyBot(GameCommunicator comm) {
		super(comm);
		this.hasArmour = false;
	}

	@Override
	protected String getAction() {
		//Gets the player location and tile
		Location playerLocation = getPlayerLocation();
		char tile = getTile(playerLocation);
		
		//If it's standing on armour pick it up
		if ((tile == 'A') && (!hasArmour))
		{
			this.hasArmour= true;
			return "PICKUP";
		}
		
		//It then tries to give gold away
		//Only give if there is gold to give
		if (this.currentGold > 0)
		{
			//Gets surrounding players
			ArrayList<CompassDirection> surroundingPlayerDirections = getSurroundingPlayerDirections(playerLocation);
			short numberOfNearbyPlayers = (short) surroundingPlayerDirections.size();
			
			if (numberOfNearbyPlayers > 0)
			{
				//If there is a nearby player then give gold to a player at random
				short randomNumber = (short) (new Random()).nextInt(numberOfNearbyPlayers);
				this.currentGold -= 1;
				return "GIFT " + getDirectionCharacter(surroundingPlayerDirections.get(randomNumber));
			}
			
			//Otherwise it finds the nearest player
			ArrayList<CompassDirection> playerPath = getShortestPathToPlayer();
			
			if (playerPath != null)
			{
				//If there is a visible player then the bot moves towards them
				return "MOVE " + getDirectionCharacter(playerPath.get(0));
			}
		}
		
		//Otherwise it picks up the lantern to maximise its player finding ability
		if ((tile == 'L') && (!hasLantern))
		{
			this.hasLantern = true;
			return "PICKUP";
		}
		//Otherwise Pickup gold even if we have the right amount so it can give it away
		if ((tile == 'G'))
		{
			this.pickupGold();
			return "PICKUP";
		}
		
		//If there is no one to give gold to or we do not have enough gold act objectively by using a target tile
		char targetTile;
		if (hasRequiredGold())
		{
			targetTile = 'E';
		}
		else
		{
			targetTile = 'G';
		}
		
		//Gets the shortest path to the target tile
		ArrayList<CompassDirection> goldPath = getShortestPathToTile(targetTile);
		if (goldPath != null)
		{
			//If we find our path follow it
			return "MOVE " + getDirectionCharacter(goldPath.get(0));
		}
		//Otherwise go for the lantern if we do not already have it
		else if (!hasLantern)
		{
			ArrayList<CompassDirection> lanternPath = getShortestPathToTile('L');
			if (lanternPath != null)
			{
				//If a lantern path is found follow it
				return "MOVE " + getDirectionCharacter(lanternPath.get(0));
			}
		}
		//If all else fails move randomly
		return "MOVE " + getDirectionCharacter(getRandomNonBlockDirection(getPlayerLocation()));
	}

}
