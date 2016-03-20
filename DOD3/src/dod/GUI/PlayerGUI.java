package dod.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dod.Communicator.GameCommunicator;
import dod.game.Location;
/**
 * A GUI for a generic player is has functionality that provides a message feed and a game board
 * @author Benjamin
 */
public abstract class PlayerGUI extends MessageFeedGUI{
	private static final long serialVersionUID = 4902461275568435021L;
	
	private JTextField chatField; //The Text field for talking
	protected JPanel gameBoard;
	protected GameCommunicator gameCommunicator; //The communicator for communication to the game
	private String[] lookReply; //Stores the look reply
	private int currentGold;
	private String name; //The players name
	private boolean hasArmour;
	private boolean isFinalWindow; //Used to determine how object should die
	
	//stats labels
	private JLabel currentGoldLabel;
	private JLabel goalLabel;
	//Label to tell the user if they have the sword
	private JLabel swordLabel;
	
	/**
	 * The constructor that sets up the communication and the GUI components
	 * @param gameCommunicator The communicator to the game
	 * @param name The player name
	 * @param isFinalWindow indicates if this is the last window or not
	 */
	public PlayerGUI(GameCommunicator gameCommunicator, String name, boolean isFinalWindow) {
		super();
		//Sets default values
		this.chatField = new JTextField(29); //set length
		this.gameCommunicator = gameCommunicator;
		//adds itself to gameCommunicator for feedback ability
		//Infinitely deep recursion is solved in two different ways
		//Using threads in the Bot GUI
		//Using the nature of event driven commands in swing in the human GUI
		this.gameCommunicator.addListener(this); 
		this.setTitle("Dungeon of Dooom");
		this.gameBoard = new JPanel();
		this.currentGold = 0;
		this.name = name;
		this.hasArmour = false;
		this.isFinalWindow = isFinalWindow;
		
		//Stats labels are set up
		this.currentGoldLabel = new JLabel("GOLD " + currentGold);
		this.goalLabel = new JLabel();
		//Uses sword indicator image but starts out invisible
		this.swordLabel = getImageLabel("SwordIndicator.png");
		this.swordLabel.setVisible(false);
		
		//Gridbag layout is used for gameboard
		gameBoard.setLayout(new GridBagLayout());
	}
	
	/**
	 * Says hello to the server.
	 */
	protected void sayHello()
	{
		gameCommunicator.sendMessageToGame("LOOK");
		gameCommunicator.sendMessageToGame("HELLO " + name);	
	}
	
	/**
	 * Updates the game board to reflect the stored look reply
	 */
	private void updateGameBoard()
	{
		//Removes all components from the game Board
		this.gameBoard.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		//Gets the player location
		Location playerLocation = getPlayerLocation();
		
		//Loop through every character in the look reply
		for(int y = 1; y < lookReply.length - 1; y++)
		{
			gbc.gridy = y;
			char[] row = lookReply[y].toCharArray(); //String is taken to a char array
			for(int x = 0; x < row.length; x++)
			{
				gbc.gridx = x;
				JLabel tile;
				if ((x == playerLocation.getCol()) && (y == playerLocation.getRow()))
				{
					//Puts a player in the centre
					tile = getMatchingLabel(getPlayerCharacter(row[x]));
				}
				else
				{
					//If there is no player than get the picture matching the character
					tile = getMatchingLabel(row[x]);
				}
				//tile is added
				gameBoard.add(tile, gbc);
			}
		}
		//Force an update to visibility
		gameBoard.setVisible(false);
		gameBoard.setVisible(true);
	}
	
	/**
	 * Gets the player location assuming it's in the middle of the look reply
	 * @return Location The location of the player
	 */
	private Location getPlayerLocation()
	{
		if (lookReply.length <= 0)
		{
			return null;
		}
		else
		{
			int row = (int) Math.ceil((lookReply.length - 2) / 2.0);
			int col = (int) (Math.ceil(lookReply[1].length() / 2.0)) - 1;
			return new Location(col, row);
		}
	}
	
	/**
	 * Gets the player character
	 * @param floorTile The tile character the player is standing on
	 * @return char the player character
	 */
	private char getPlayerCharacter(char floorTile)
	{
		//A character depending on the state of the player
		if (floorTile == 'E')
		{
			if (hasArmour)
			{
				return 'K';
			}
			else
			{
				return 'Q';
			}
		}
		else
		{
			if (hasArmour)
			{
				return 'R';
			}
			else
			{
				return 'P';
			}
		}
	}
	
	/**
	 * Gets the messenger JPanel
	 * @return JPanel the messanger JPanel
	 */
	protected JPanel getMessenger()
	{
		//JPanel using the grid bag layout
		JPanel messenger = new JPanel();
		messenger.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		//Adds the message feed from the superclass
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		messenger.add(getMessageFeed(), gbc);
		
		//Adds the text field chat field
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		messenger.add(chatField, gbc);
		
		//Creation of a send button
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Sends the message and wipes the text field
				sendChatMessage(chatField.getText());
				chatField.setText("");
			}
		});
		gbc.gridx = 1;
		messenger.add(sendButton, gbc);
		
		return messenger;
	}
	
	/**
	 * Gets a quit button with built in functionality
	 * @return JButton The Quit Button
	 */
	protected JButton getQuitButton()
	{
		//uses a set Image
		JButton quitButton = getImageButton("Quit.png");
		quitButton.setToolTipText("Quit Game");
		quitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// Sends a die message and exits
				die("DIE You Quit");
			}
		});
		return quitButton;
	}
	
	/**
	 * Gets the JLabel containing the current gold
	 * @return JLabel
	 */
	protected JLabel getCurrentGoldLabel()
	{
		return this.currentGoldLabel;
	}
	
	/**
	 * Gets the JLabel containg the goal gold to win
	 * @return JLabel
	 */
	protected JLabel getGoalLabel()
	{
		return this.goalLabel;
	}
	
	protected JLabel getSwordIndicator()
	{
		return this.swordLabel;
	}
	
	/**
	 * Gets a button with an image using a given file name
	 * @param imageFileName String the Image File Name
	 * @return JButton a button with the associated file name
	 */
	protected JButton getImageButton(String imageFileName)
	{
		JButton button = new JButton();
		setImageButton(imageFileName, button);
		//Standardised format to make it visually appealing
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		return button;
	}
	
	/**
	 * Given a button this function will modify it by changing it's image to that of a given file name
	 * @param imageFileName String The file name of the new image
	 * @param button JButton The button to receive the new image
	 */
	protected void setImageButton(String imageFileName, JButton button)
	{
		//Image is taken from file
		ImageIcon imageIcon = new ImageIcon(getClass().getResource(imageFileName), "");
		button.setIcon(imageIcon);
	}
	
	/**
	 * Gets a Label with an image using a given file name
	 * @param imageFileName String the Image File Name
	 * @return JLabel a label with the associated file name
	 */
	protected JLabel getImageLabel(String imageFileName)
	{
		JLabel label = new JLabel();
		//Image is taken from file
		ImageIcon imageIcon = new ImageIcon(getClass().getResource(imageFileName), "");
		label.setIcon(imageIcon);
		return label;
	}
	
	//Interprets the message from the game
	@Override
	public void pushMessage(String message)
	{
		//Die messages are treated differently
		if (message.startsWith(("DIE")))
		{
			die(message);
		}
		//Look replies are stored in the lookReply attribute
		else if (message.startsWith(("LOOKREPLY")))
		{
			lookReply = message.split(System.getProperty("line.separator"));
			//The game board is updated
			updateGameBoard();
			//Class allows subclasses to handle the look reply also
			handelLookReply(this.lookReply);
		}
		//Other messages are displayed on message feed
		else
		{
			//Checks for armour
			if(message.equals("You equip Armour"))
			{
				this.hasArmour = true;
			}
			else if(message.equals("You equip Sword"))
			{
				this.swordLabel.setVisible(true);
			}
			//Updates the goal message if need be
			else if(message.startsWith("GOAL"))
			{
				goalLabel.setText(message);
			}
			//Updates the current gold message if need be
			else if(message.startsWith("TREASUREMOD "))
			{
				this.currentGold += getGoldChange(message.substring(12).replace(" ", ""));
				this.currentGoldLabel.setText("GOLD " + currentGold);
			}
			
			//Allows subclassses to read the message if it's needed
			handelMessage(message);
			//Checks to see if the message is a server of player message and formats it correctly
			if (!message.startsWith("["))
			{
				addMessageToFeed("Server: " + message);
			}
			else
			{
				addMessageToFeed(message);
			}
			
		}
	}
	
	/**
	 * Called whenever a message is received it is designed to be optionally overridden
	 * By default is does nothing
	 * @param message String The message that was received
	 */
	protected void handelMessage(String message)
	{
		return;
	}
	
	/**
	 * Called whenever a look reply is received it is designed to be optionally overridden
	 * By default is does nothing
	 * @param lookReply String[] The look reply that was received
	 */
	protected void handelLookReply(String[] lookReply)
	{
		return;
	}
	
	/**
	 * Reads of the gold change as a string and returns it as an integer
	 * @param treasureMod The string for which contains the gold change
	 * @return int The parsed string, if the string was not a number 0 is returned
	 */
	private int getGoldChange(String treasureMod)
	{
		try{
			int goldChange = Integer.parseInt(treasureMod);
			return goldChange;
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}
	
	/**
	 * Displays a message and kills the window. If it is the final window it also exits the program.
	 * @param message String The message to be displayed
	 */
	protected void die(String message)
	{
		JOptionPane.showMessageDialog(null, message.substring(3));
		//Window is removed
		this.dispose();
		if (this.isFinalWindow)
		{
			//Exits the program
			System.exit(0);
		}
	}
	
	/**
	 * Given a character a JLabel with the matching image is returned
	 * @param Tile char The Tile Character
	 * @return JLabel with an image of the matching character, if the character is not recognised a black space is seen instead
	 */
	private JLabel getMatchingLabel(char Tile)
	{
		String fileName;
		//Case statement for all file names
		switch(Tile)
		{
		case '.':
		{
			fileName = "Floor";
			break;
		}
		case 'G':
		{
			fileName = "Gold";
			break;
		}
		case 'E':
		{
			fileName = "Exit";
			break;
		}
		case '#':
		{
			fileName = "Wall";
			break;
		}
		case 'A':
		{
			fileName = "Armour";
			break;
		}
		case 'S':
		{
			fileName = "Sword";
			break;
		}
		case 'H':
		{
			fileName = "Health";
			break;
		}
		case 'P':
		{
			fileName = "Player";
			break;
		}
		case 'Q':
		{
			fileName = "ExitPlayer";
			break;
		}
		case 'R':
		{
			fileName = "aPlayer";
			break;
		}
		case 'K':
		{
			fileName = "aExitPlayer";
			break;
		}
		case 'L':
		{
			fileName = "Lantern";
			break;
		}
		//Has a default one for X and any extra tiles that does not exist yet
		default: fileName = "BlackSpace";
		}
		//File Extension is added and the JLabel is formed
		return getImageLabel(fileName + ".png");
	}
	
	/**
	 * Sends a chat message to the game
	 * @param message String the message to be sent
	 */
	private void sendChatMessage(String message)
	{
		//It will not send an empty message - only contains spaces
		if (!message.replace(" ", "").equals(""))
		{
			gameCommunicator.sendMessageToGame("SHOUT " + message);
		}
	}

}
