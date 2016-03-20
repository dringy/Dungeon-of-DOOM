package dod;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

import dod.game.GameLogic;

/**
 * The class that represents the server it sets up the game and creates threads for each client.
 * @author Benjamin Dring
 */
public class Server extends Thread{
	private ServerSocket server; //The server socket
	private int port; //The chosen port
	private final GameLogic game;
	
	/**
	 * Sets up a server on the current machine using a given game and port number
	 * @param game GameLogic the GameLogic to be run
	 * @param port int the port number to run the server on
	 */
	public Server(GameLogic game, int port) {
		this.game = game;
		this.port = port;
	}
	
	/**
	 * Listens for new clients and sets up a game for them
	 * Can run in a new thread by using Server.start()
	 */
	@Override
	public void run()
	{	
		
		try
		{
			//Server is created
			server = new ServerSocket(port);
			//Loops indefinitely
			while (true)
			{
				try
				{
					//Accepts a new client and creates a new user
					final Socket client = server.accept();
					NetworkUser user = new NetworkUser(game, client);
					
					//User is run in a new thread allowing this one to get the next client
					Thread clientThread = new Thread(user);
					clientThread.start();

				}
				catch (IOException e)
				{
					//If there is a problem with a client just ignore and look for the next one 
					continue;
				}
			}
		}
		catch (IOException e)
		{
			//This should only occur when the server socket throws an exception
			//Best action is just to quit and send an error message
			JOptionPane.showMessageDialog(null, "Server cannot be started - exiting program");
			System.exit(0);
		}

		
	}
}
