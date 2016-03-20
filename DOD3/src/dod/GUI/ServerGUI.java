package dod.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dod.game.GameLogic;

/**
 * Represents the server GUI it can display buttons which can control the server and a message feed for displaying events in game.
 * displayGUI() should be called to display the gui
 * @author Benjamin Dring
 *
 */
public class ServerGUI extends MessageFeedGUI implements ActionListener{
	private static final long serialVersionUID = 4457158873596592971L;
	
	private JButton serverButton; //The server button
	private boolean gameStarted; 
	private GameLogic game; //The given GameLogic object
	
	/**
	 * The constructor for the class it sets up the GUI ready for display
	 */
	public ServerGUI()
	{
		super();
		serverButton = new JButton("");
		//Action Listener is done by the class this time
		serverButton.addActionListener(this);
		//Make it a start button
		setStartButtonAttributes();
	}
	
	/**
	 * Adds a game logic to the class so the server can start the game
	 * @param game GameLogic the GameLogic object to be started and stopped with the server buttons
	 */
	public void addGame(GameLogic game)
	{
		this.game = game;
	}
	
	/**
	 * Displays the Server GUI
	 */
	@Override
	public void displayGUI() {
		//Uses a grid bag layout
		canvas.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//Message feed is added from superclass
		gbc.gridx = 0;
		gbc.gridy = 0;
		canvas.add(getMessageFeed(), gbc);
		
		//Server buttons ias added
		gbc.gridy = 1;
		canvas.add(serverButton, gbc);
		
		//Container settings are set
		this.setSize(525, 600);
		this.setTitle("Server Control");
		//Display components
		this.show();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameStarted)
		{
			//If the game has started then stopping it will exit the program
			System.exit(0);
		}
		else
		{
			//If the game has not started then start the game
			//If there is no game yet then nothing happens
			if (game != null)
			{
				game.startGame();
				//Button is updated
				setStopButtonAttributes();
				this.gameStarted = true;
				//Message is displayed
				addMessageToFeed("Game Start");
			}
		}
	}
	
	/**
	 * Sets the button up as a start button
	 */
	private void setStartButtonAttributes()
	{
		serverButton.setText("Start Server");
		serverButton.setToolTipText("Start The Server");
	}
	
	/**
	 * Sets the button up as a stop button
	 */
	private void setStopButtonAttributes()
	{
		serverButton.setText("Stop Server");
		serverButton.setToolTipText("Stop The Server");
	}
	
	@Override
	/**
	 * Message is added to the feed
	 */
	public void pushMessage(String message) {
		addMessageToFeed(message);
	}

	@Override
	/**
	 * Restarts the game
	 */
	public void restartGame() {
		//Message feed functionality is handled by the super class version of this function
		super.restartGame();
		//ServerGUI state is corrected
		setStartButtonAttributes();
		this.gameStarted = false;
	}

}
