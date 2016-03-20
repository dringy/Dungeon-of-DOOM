package dod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dod.game.GameLogic;

/**
 * Represents a Network User and bridges the communication a network game communicator over a network to the Game logic object.
 * This class implements Runnable so can be used in threading.
 * @author Benjamin Dring
 */
public class NetworkUser extends User implements Runnable{
	Socket client; //Client socket
	PrintWriter socketPrinter;

	public NetworkUser(GameLogic game, Socket sock) {
		super(game);
		this.client = sock;
		//Calls a look command instantly
		game.lookAll();
	}
	
	/**
	 * Gets command strings from the socket and processes them and sends them to the game
	 * Function ends when the game is over
	 */
	@Override
	public void run() {
		//New thread is created
    	Thread clientReader = null;
    	try {
    		//Reads from client socket
    		final BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
    		
    		clientReader = new Thread(){
				public void run()
				{
					try
					{
						while (true)
						{
							// Try to grab a command from the command line this is exactly as it was before except now it takes data from the client
							final String command = br.readLine();
							//Only respond to command if it is the current turn
							// Test for EOF (ctrl-D)
							if (command == null) {
								processCommand("Die");
							}
							processCommand(command);
						}
					}
					catch (IOException e)
					{
						processCommand("Die");
					}
				}
			};
			
			//Start listening to user input
			clientReader.start();
    		
    		// Keep listening forever
    		while (true) {
    			//If it's game over than we need to stop
    			if (isGameOver())
    			{
    				break;
    			}

    			//sleep for a second if it isn't your go
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
    		}
    	} catch (final RuntimeException e) {
    		// Die if something goes wrong.
    		processCommand("Die");
    	} catch (final IOException e) {
    		processCommand("Die");
    	}
    	try {
			client.close();
		} catch (IOException e) {}
    	//Close the listening thread
    	if (clientReader != null)
    	{
    		clientReader.interrupt();
    	}
		
	}
	
	/**
	 * Outputs messages through the socket to the client
	 */
	@Override
	protected void outputMessage(String message) {
		try
		{
			//If a print writer is not present one is created
			if (socketPrinter == null)
			{
				socketPrinter = new PrintWriter(client.getOutputStream(), true);
			}
			socketPrinter.println(message);
		}
		catch (IOException e)
		{
			System.out.println("Can't connect to client");
		}
	}

}
