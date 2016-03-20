package dod.GUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import dod.Server;
import dod.BotLogic.*;
import dod.Communicator.*;
import dod.game.GameLogic;


/**
 * A GUI class that displays the Selection Menus that allows the user to set up their desired session of Dungeon of Dooom 3
 * @author Benjamin Dring
 */
public class MainMenu extends JFrame {
	private static final long serialVersionUID = 2304527111075624043L;
	
	private Container canvas;
	private final short textBoxSize = 15; //the set text box side
	
	/**
	 * Constructor of the class that sets up the JFrame Container
	 */
	public MainMenu()
	{
		canvas = getContentPane();
		canvas.setLayout(new GridBagLayout()); //All menus use the grid bag layout
		canvas.setBackground(Color.BLACK); //Starts off with a black background
	}
	
	/**
	 * Displays the Title Menu and gives all buttons the the required functionality
	 */
	public void displayTitleMenu()
	{
		this.setSize(1000, 300); //first menu is larger than the others
		canvas.removeAll(); //removes any previous components
		this.setTitle("DUNGEON OF DOOOM"); 
		GridBagConstraints gbc= getNewgbc();
		
		//The two buttons are stored in a Grid Layout JPanel
		JPanel startButtons = new JPanel();
		startButtons.setLayout(new GridLayout(1, 2));
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		//Create and Add multiplayer button
		JButton networkButton = new JButton("Multiplayer");
		networkButton.setToolTipText("Play over a network");
		networkButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				displayNetworkGameMenu();
			}
		});
		startButtons.add(networkButton);
		
		//Create and Add Single button
		gbc.gridx = 1;
		JButton localButton = new JButton("Single Player");
		localButton.setToolTipText("Play locally");
		localButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				displayLocalSettingsMenu();
			}
		});
		startButtons.add(localButton);
		
		
		//Add the buttons panel to the container
		gbc.gridx = 0;
		gbc.gridy = 1;
		canvas.add(startButtons, gbc);
		
		//Creates and adds the title picture JLabel to the container
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		JLabel titlePicture = new JLabel();
		ImageIcon imageIcon = new ImageIcon(getClass().getResource("Title.png"), "");
		titlePicture.setIcon(imageIcon);
		canvas.add(titlePicture, gbc);
		
		//Update container
		updateMenu();
	}
	
	/**
	 * Displays the Menu for single player mode
	 */
	private void displayLocalSettingsMenu()
	{
		//Colour is reset, menu is resized and title is changed
		this.setSize(450, 300);
		canvas.setBackground(null);
		this.setTitle("Local Settings");
		//Previous parts are removed
		canvas.removeAll();
		GridBagConstraints gbc= getNewgbc();
	
		//Text "Map Name" is added
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel mapLabel = new JLabel("Map Name");
		canvas.add(mapLabel, gbc);
		
		//Text field for map name is added defaultMap is entered automatically
		gbc.gridx = 1;
		final JTextField mapTextField = getNewTextField("defaultMap");
		canvas.add(mapTextField, gbc);
		
		//Text "Name" is added
		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel nameLabel = new JLabel("Name");
		canvas.add(nameLabel, gbc);
		
		//Text Field for the players name is added it is blank by default
		gbc.gridx = 1;
		final JTextField nameTextField = getNewTextField("");
		canvas.add(nameTextField, gbc);
		
		//Create the Bot Button
		gbc.gridy = 2;
		JButton botButton = new JButton("Play as Bot");
		botButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				try {
					//Game and Communicator is made
					GameLogic game = new GameLogic(mapTextField.getText()); //Map name is taken from the text field
					//Bot Selection Menu is displayed
					displayBotSelectionMenu(new LocalGameCommunicator(game), nameTextField.getText(), false); //Name is taken from the text field
					//Game is started
					game.startGame();
				//Catch statements send error message using JOptionPanes, they are located here so the user may enter in the details again
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Map File not Found");
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, "Map File is corrupted or misformated");
				}
			}
		});
		//Add the Bot Button
		canvas.add(botButton, gbc);
		
		//Create the Human Button
		gbc.gridx = 0;
		JButton humanButton = new JButton("Play as Human");
		humanButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				try {
					//Game and Communicator is made
					GameLogic game = new GameLogic(mapTextField.getText()); //Map name is taken from the text field
					//GUI is displayed
					displayGameGUI(new HumanPlayerGUI(new LocalGameCommunicator(game), nameTextField.getText(), true)); //Name is taken from the text field
					//Game is started
					game.startGame();
				//Catch statements send error message using JOptionPanes, they are located here so the user may enter in the details again
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Map File not Found");
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, "Map File is corrupted or misformated");
				}
				
			}
		});
		//Human Button is added
		canvas.add(humanButton, gbc);
		
		//Update the menu
		updateMenu();
	}
	
	/**
	 * Displays the menu for playing over a network
	 */
	private void displayNetworkGameMenu()
	{
		//Background is reset and screen is resized and title is changed
		canvas.setBackground(null);
		this.setSize(450, 300);
		this.setTitle("Network game");
		//Previous components are removed
		canvas.removeAll();
		GridBagConstraints gbc= getNewgbc();
		
		//Host Button is Created
		gbc.gridx = 0;
		gbc.gridy = 0;
		JButton hostButton = new JButton("Host Game");
		hostButton.setToolTipText("Host the Game over a network");
		hostButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				//New menu is displayed
				displayStartServerMenu();
			}
		});
		//Host Button is Added
		canvas.add(hostButton, gbc);
		//Join Button is Created
		gbc.gridy = 1;
		JButton joinButton = new JButton("Join Game");
		joinButton.setToolTipText("Join a Game over a network");
		joinButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent a) {
				//New menu is displayed
				displayJoinServerMenu();
			}
		});
		//Join Button is Added
		canvas.add(joinButton, gbc);
		
		//Menu is updated
		updateMenu();
	}
	
	/**
	 * Displays the menu to start the server
	 */
	private void displayStartServerMenu()
	{
		//Previous components are removed
		canvas.removeAll();
		//Title is set
		this.setTitle("Start Server");
		GridBagConstraints gbc= getNewgbc();
		
		JLabel portLabel = new JLabel("Port"); //Port Text
		final JTextField portTextField = getNewTextField(""); //Text field for the Port Number
		final JLabel mapLabel = new JLabel("Map Name"); //"Map Name" text
		final JTextField mapTextField = getNewTextField("defaultMap"); //Text field for the Map name
		final JCheckBox localPlayCheckBox = new JCheckBox("Play on Server"); //Check box to indicate if the user wants to play on the server
		final JButton startButton = new JButton("Start Server"); //Button to start the server
		
		//The start function
		startButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				//Port is converted
				int port = convertStringToPortNumber(portTextField.getText());
				
				//If port is less than 0 then we show an error message and nothing more is happened
				if((port < 0) || (port >= 65535))
				{
					JOptionPane.showMessageDialog(null, "Invalid Port"); //error message
					return;
				}
				
				try
				{
					//Server is created using the map name from the field and the converted port number and the created game is returned
					//This also opens the Server GUI
					GameLogic game = startServerGUI(mapTextField.getText(), port);
					//If the user wants to play on the server then a LocalGameCommunicator is made and the user plays on the game locally
					//The next menu is then displayed asking details of the player
					if(localPlayCheckBox.isSelected())
					{
						displayPlayNetworkGameMenu(new LocalGameCommunicator(game), true);
					}
					else
					{
						//Otherwise the menu is simply hidden as it is no longer needed
						hideMenu();
					}
				}
				//Catch statements send error message using JOptionPanes, they are located here so the user may enter in the details again
				catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, "Map File not Found");
				} catch (ParseException e) {
					JOptionPane.showMessageDialog(null, "Map File is corrupted or misformated");
				}
			}
		});
		
		//The components are then added to the container in the designed format
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		canvas.add(portLabel, gbc);
		
		gbc.gridx = 1;
		canvas.add(portTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		canvas.add(mapLabel, gbc);
		
		gbc.gridx = 1;
		canvas.add(mapTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		canvas.add(localPlayCheckBox, gbc);
		
		gbc.gridwidth = 0;
		gbc.gridy = 3;
		canvas.add(startButton, gbc);
		
		//Menu is updated
		updateMenu();
	}
	
	/**
	 * The Join Menu to allow the user to join a Dungeon of Dooom 3 Server
	 */
	private void displayJoinServerMenu()
	{
		//Components are removed
		canvas.removeAll();
		//Title is set
		this.setTitle("Join Server");
		GridBagConstraints gbc= getNewgbc();
		
		JLabel ipAddressLabel = new JLabel("IP Address"); //"IP Address" Text
		final JTextField ipAddressTextField = getNewTextField(""); //Text Field for the user to enter in the IP Address
		JLabel portLabel = new JLabel("Port"); //"Port" Text
		final JTextField portTextField = getNewTextField(""); //Text field for the user to enter in the Port Number
		//Creating the Join Button
		JButton joinButton = new JButton("Join Server");
		joinButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//The port is converted from the text field
				int port = convertStringToPortNumber(portTextField.getText());
				
				//If the port is invalid display error message and do nothing more
				if(port < 0)
				{
					JOptionPane.showMessageDialog(null, "Invalid Port");
					return;
				}
				
				try {
					//New game communicator id made using the converted port and the ip address in the text field
					GameCommunicator comm = new NetworkGameCommunicator(ipAddressTextField.getText(), port);
					//If no exception is thrown the next menu is displayed which allows the player to select how they want to play
					displayPlayNetworkGameMenu(comm, false);
				//Catch statements send error message using JOptionPanes, they are located here so the user may enter in the details again
				} catch (UnknownHostException e) {
					JOptionPane.showMessageDialog(null, "Server not found.");
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Network Error");
				}
			}
		});
		
		//Components are added
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		canvas.add(ipAddressLabel, gbc);
		
		gbc.gridy = 1;
		canvas.add(portLabel, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		canvas.add(ipAddressTextField, gbc);
		
		gbc.gridy = 1;
		canvas.add(portTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		canvas.add(joinButton, gbc);
		
		//Menu is updated
		updateMenu();
	}
	
	/**
	 * Displays the Menu for joining the game
	 * @param comm GameCommunication The communication object for the player
	 * @param isServer Boolean indicates if the player is also running the server
	 */
	private void displayPlayNetworkGameMenu(final GameCommunicator comm, final boolean isServer)
	{
		//Removes all the components from the container
		canvas.removeAll();
		//Set the title
		this.setTitle("Play Game");
		GridBagConstraints gbc= getNewgbc();
		
		JLabel nameLabel = new JLabel("Name"); //"Name" text
		final JTextField nameTextField = getNewTextField(""); //Text Field for the player name
		JButton botButton = new JButton("Play as Bot"); //Button to play as a Bot
		JButton humanButton = new JButton("Play as Human"); //Button to play as a human
		
		//Add action listener
		botButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Bot Selection menu is displayed
				displayBotSelectionMenu(comm, nameTextField.getText(), isServer);
			}
		});
		
		humanButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Human GUI is displayed
				displayGameGUI(new HumanPlayerGUI(comm, nameTextField.getText(), !isServer));
			}
		});
		
		//Components are added
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		canvas.add(nameLabel, gbc);
		
		gbc.gridx = 1;
		canvas.add(nameTextField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		canvas.add(botButton, gbc);
		
		gbc.gridx = 1;
		canvas.add(humanButton, gbc);
		
		//Menu is updated
		updateMenu();
	}
	
	/**
	 * Display the Bot Selection menu where the user can choose the Bot they want to use
	 * @param comm GameCommunicator The Communication object used for the player
	 * @param name String The Name of the player
	 * @param isServer Boolean indicates if the player is also running the server
	 */
	private void displayBotSelectionMenu(final GameCommunicator comm, final String name, final boolean isServer)
	{
		//Components are removed
		canvas.removeAll();
		//Title is set
		this.setTitle("Choose your Bot AI");
		GridBagConstraints gbc= getNewgbc();
		
		//Radio Button for each bot type with a description in the tool tip
		final JRadioButton randomBotButton = new JRadioButton("Baldrick"); 
		randomBotButton.setToolTipText("Likes to run in circles"); 
		final JRadioButton objectiveBotButton = new JRadioButton("Wes");
		objectiveBotButton.setToolTipText("Determined to get out as fast as possible");
		final JRadioButton aggresiveBotButton = new JRadioButton("Ledeon");
		aggresiveBotButton.setToolTipText("Not a force to be recokned with");
		final JRadioButton friendlyBotButton = new JRadioButton("Alison");
		friendlyBotButton.setToolTipText("Strangley helpful, We're not sure she knows how to win");
		//Create play button
		final JButton playButton = new JButton("Play");
		playButton.setToolTipText("Start Game");
		
		//All radio buttons are added to the button group
		ButtonGroup AIBotButtons = new ButtonGroup();
		AIBotButtons.add(randomBotButton);
		AIBotButtons.add(objectiveBotButton);
		AIBotButtons.add(aggresiveBotButton);
		AIBotButtons.add(friendlyBotButton);
		
		
		randomBotButton.setSelected(true); //First radio button is selected
		
		//Add function
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Bot bot;
				//Bot Type Radio buttons determines the dynamic type of bot
				if (randomBotButton.isSelected())
				{
					bot = new RandomBot(comm);
				}
				else if (objectiveBotButton.isSelected())
				{
					bot = new ObjectiveBot(comm);
				}
				else if (aggresiveBotButton.isSelected())
				{
					bot = new AggressiveBot(comm);
				}
				else if (friendlyBotButton.isSelected())
				{
					bot = new FriendlyBot(comm);
				}
				else
				{
					//If none of them are selected display error
					JOptionPane.showMessageDialog(null, "Please select a bot AI");
					return;
				}
				//Bot Player is created using the newly created bot and the other parameters passed into this function
				displayGameGUI(new BotPlayerGUI(comm, name, !isServer, bot));
			}
		});
		
		//Components are added into the container
		gbc.gridx = 0;
		gbc.gridy = 0;
		canvas.add(randomBotButton, gbc);
		
		gbc.gridx = 1;
		canvas.add(objectiveBotButton, gbc);
		
		gbc.gridy = 1;
		canvas.add(friendlyBotButton, gbc);
		
		gbc.gridx = 0;
		canvas.add(aggresiveBotButton, gbc);
		
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		canvas.add(playButton, gbc);
		
		//Menu is updated
		updateMenu();
	}
	
	/**
	 * Displays a given Game GUI
	 * @param gui MessageFeedGUI The Game GUI to be displayed
	 */
	private void displayGameGUI(MessageFeedGUI gui)
	{
		//Menu is hidden
		hideMenu();
		//Menu doesn't exit the program
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Display function is called
		gui.displayGUI();
	}
	
	/**
	 * Hides the menu fro view
	 */
	private void hideMenu()
	{
		//All components are removed
		canvas.removeAll();
		//Container is removed
		this.setVisible(false);
	}
	
	/**
	 * Creates a standardised GridBagConstraints
	 * @return GridBagConstraints standardised format for menu GridBagConstraints
	 */
	private GridBagConstraints getNewgbc()
	{
		GridBagConstraints gbc = new GridBagConstraints();
		//Add padding
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5,5,5,5);
		return gbc;
	}
	
	/**
	 * Updates the menu
	 */
	private void updateMenu(){
		//Update functions are called
		this.show();
		canvas.repaint();
	}
	
	/**
	 * Gets a new standardised Test Field
	 * @param defaultText String the default text to be places in the text field
	 * @return JTextField the standardised text field
	 */
	private JTextField getNewTextField(String defaultText)
	{
		//Uses standardised text box size
		JTextField textField = new JTextField(textBoxSize);
		textField.setText(defaultText);
		return textField;
	}
	
	/**
	 * Starts the server and displays the server GUI
	 * @param mapName String The name of the map file
	 * @param port int The port number
	 * @return GameLogic The game created in the launching of ther server
	 * @throws ParseException
	 * @throws FileNotFoundException
	 */
	private GameLogic startServerGUI(String mapName, int port) throws ParseException, FileNotFoundException
	{
		//GUI class is created
		ServerGUI gui = new ServerGUI();
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Game is created
		GameLogic game = new GameLogic(mapName, gui);
		//Game is added to the GUI
		gui.addGame(game);
		//Server class is created and started
		Server server = new Server(game, port);
		server.start();
		//gui is displayed
		gui.displayGUI();
		return game;
	}
	
	/**
	 * Converts a string to an int
	 * @param portString The String that represents the port number
	 * @return int The converted string as an int, it returns -1 if the string is in a number format
	 */
	private int convertStringToPortNumber(String portString)
	{
		try{
			//String is parsed
			int port = Integer.parseInt(portString);
			return port;
		}
		catch (NumberFormatException e)
		{
			//If parse fails then -1 is returned
			return -1;
		}
		
	}
	
}
