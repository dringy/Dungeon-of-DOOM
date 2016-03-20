package dod.Communicator;

import dod.LocalUser;
import dod.game.GameLogic;

/**
 * A GameCommunicator object that communicates to the game locally through localUser
 * @author Benjamin Dring
 */
public class LocalGameCommunicator extends GameCommunicator {
	private LocalUser user; //The localuser object
	
	/**
	 * Constructor that sets up the communication to a given GameLogic reference
	 * @param game GameLogic The game to be communicated with
	 */
	public LocalGameCommunicator(GameLogic game) {
		super();
		//Localuser is created using the given game
		//A reference of this object is given to local user to allow communication from user to this object
		user = new LocalUser(game, this);
	}

	@Override
	public void sendMessageToGame(String message) {
		//Calls function directly in user
		user.sendCommand(message);
	}

	/**
	 * Message function that sends a message to the client listener object
	 * @param reply String the reply message
	 */
	public void sendMessageFromGame(String reply)
	{
		//sends a message only if the client exists
		if (client != null)
		{
			client.pushMessage(reply);
		}
	}

}
