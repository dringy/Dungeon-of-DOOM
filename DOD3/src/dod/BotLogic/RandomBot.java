package dod.BotLogic;

import dod.Communicator.GameCommunicator;
import dod.game.Location;

/**
 * One of the Bot Logic Classes, this bot moves randomly and picks up items if it needs it.
 * @author Benjamin Dring
 */
public class RandomBot extends Bot {
	
	/**
	 * The constructor for the random bot it sets up it's decision making processes and 
	 * Prepares communication with the game
	 * @param comm GameComunicator The communicator to the Game Logic Class
	 */
	public RandomBot(GameCommunicator comm) {
		super(comm);
	}
	
	@Override
	protected String getAction()
	{
		//Gets the player location and current tile
		Location playerLocation = getPlayerLocation();
		char tile = getTile(playerLocation);
		
		//If the user is standing on gold and it needs it then it wants to pick it up
		if ((tile == 'G') && (!hasRequiredGold()))
		{
			this.pickupGold();
			return "PICKUP";
		}
		//I removed this part as it does not need the lantern if it doesn't look beyond the tile it's on
//		else if ((tile == 'L') && (!hasLantern))
//		{
//			this.hasLantern = true;
//			return "PICKUP";
//		}
		
		//Otherwise we move randomly
		else
		{
			try
			{
				//Forms a move command
				return "MOVE " + getDirectionCharacter(getRandomNonBlockDirection(playerLocation));
			}
			catch (NullPointerException e)
			{
				//If there are no directions just end the turn
				return "ENDTURN";
			}
		}
	}

}
