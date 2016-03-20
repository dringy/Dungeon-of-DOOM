package dod.Communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import dod.GUI.ClientListener;

/**
 * A GameCommunicator that provides functionality for communicating to a server game over a network
 * @author Benjamin Dring
 */
public class NetworkGameCommunicator extends GameCommunicator {
	private Socket server; //Server Socket
	private PrintWriter socketPrinter;
	//These two objects are used in constructing the look reply
	private String lookReply;
	private boolean readingLookReply;
	
	/**
	 * The constructor for the class, it sets up the connection to the Network User over the network
	 * @param ipAddress String the IP address of the machine that server is running on
	 * @param portNumber int the Port Number the server is running on
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public NetworkGameCommunicator(String ipAddress, int portNumber) throws UnknownHostException, IOException {
		super();
		//Socket is created, exceptions are thrown out of this function
		server = new Socket(ipAddress, portNumber);
		//Set defaults
		this.lookReply = "";
		this.readingLookReply = false;
	}
	
	@Override
	public void addListener(ClientListener client){
		//First it adds the listener using the superclass version of this function
		super.addListener(client);
		//Then is creates starts a new thread that listens for input through the socket
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run()
			{
				//Thread code is a single function
				getMessageFromGame();
			}
		});
		thread.start();
	}

	@Override
	public void sendMessageToGame(String message) {
		//Messages are sent over the socket in contrast to LocalGameCommunciator which simply passed in values
		try
		{
			//Creates a new socketPrinter if needed
			if (socketPrinter == null)
			{
				socketPrinter = new PrintWriter(server.getOutputStream(), true);
			}
			socketPrinter.println(message);
		}
		catch (IOException e)
		{
			//If there's a problem attempt to close
			if (client != null)
			{
				client.pushMessage("DIE Connection Error");
			}
			else
			{
				//Not much can be done from this point
				System.out.println("Can't connect to server");
				System.exit(0);
			}
		}
	}
	
	/**
	 * Listens to the game and relays any messages to the client listener
	 */
	private void getMessageFromGame()
	{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream())); //uses buffered reader
			while(true)
			{
				//Look replies through a network occur line by line
				//Look replies occur in a single string so the decision was
				//made to format the network replies to that of the local replies
				//So look replies are formed into a single string until ENDLOOKREPLY is read
				//In which case the reply is then sent through to the client
				String message = br.readLine();
				if (message.startsWith("LOOKREPLY"))
				{
					//We then start the look reply
					readingLookReply = true;
					lookReply = message;
				}
				else if (message.startsWith("ENDLOOKREPLY"))
				{
					//end the look reply and push the look reply to the client listener
					readingLookReply = false;
					addToLookReply(message);
					client.pushMessage(lookReply);
				}
				else if(readingLookReply)
				{
					//If we are still reading a look reply add it to the string
					addToLookReply(message);
				}
				else
				{
					//Otherwise jsut send the message to the client
					client.pushMessage(message);
				}
			}
		} catch (IOException e) {
			//This causes the program to exit
			client.pushMessage("DIE Server Connection Error");
		}
		
	}
	
	/**
	 * Adds a message as a new line to the look reply
	 * @param message String the message to be added
	 */
	private void addToLookReply(String message)
	{
		//We use line separators in the string to split the reply up
		lookReply += System.getProperty("line.separator") + message;
	}

}
