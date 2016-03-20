package dod.Communicator;

import dod.GUI.ClientListener;

/**
 * A Generic game communicator class that gives functionality to communicate to a game.
 * If given a client listener object it will also relay messages back to the client from the game.
 * @author Benjamin Dring
 */
public abstract class GameCommunicator{
	protected ClientListener client; //The client listener for feed back

	/**
	 * Constructor that initiates the GameCommunicator
	 */
	public GameCommunicator() {
		this.client = null; //defaults to none
	}
	 /**
	  * Adds a client listener to the game communicator to allow feedback
	  * @param client ClientListener the client listener object to be feedback messages from the game
	  */
	public void addListener(ClientListener client){
		this.client = client;
	}
	
	/**
	 * Sends a give message as a command to the game
	 * @param message String the message to be sent
	 */
	abstract public void sendMessageToGame(String message);
	
}
