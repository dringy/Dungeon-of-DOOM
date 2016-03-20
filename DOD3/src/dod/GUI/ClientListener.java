package dod.GUI;
/**
 * An interface that ensure Communicator Classes can correctly interface with the Listening object
 * @author Benjamin Dring
 *
 */
public interface ClientListener {
	/**
	 * Send a message to the client
	 * @param message the message to be sent
	 */
	public void pushMessage(String message);
	
	/**
	 * Prompts the user to restart the game
	 */
	public void restartGame();
	
}
