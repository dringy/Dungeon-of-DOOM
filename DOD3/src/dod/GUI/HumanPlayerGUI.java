package dod.GUI;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

import dod.Communicator.GameCommunicator;

/**
 * Represents the Dungeon of Dooom 3 GUI for the Human Player.
 * The displayGUI() method will completely display the GUI with all of its functionality
 * @author Benjamin Dring
 */
public class HumanPlayerGUI extends PlayerGUI{
	private static final long serialVersionUID = -9146521400683926566L;
	
	private Action selectedAction; //Enum for use with controller

	/**
	 * The constructor of the class that set up the Human GUI ready for display
	 * @param gameCommunicator GameCommunicator The communicator object for the GUI to communicate to the Game
	 * @param name String The user's name to be used in game
	 * @param isFinalWindow Boolean indicating whether or not this is the last window or not
	 */
	public HumanPlayerGUI(GameCommunicator gameCommunicator, String name, boolean isFinalWindow) {
		super(gameCommunicator, name, isFinalWindow);
		this.selectedAction = Action.Move; //defaults to move
		sayHello();
	}
	
	/**
	 * Displays the Human Player GUI
	 */
	@Override
	public void displayGUI() {
		canvas.setLayout(new FlowLayout()); //Uses flow layout
		//Adds individual components
		
		canvas.add(gameBoard);
		
		canvas.add(getController());
		
		canvas.add(getMessenger());
		
		//Size is set and components are shown
		this.setSize(1800, 750);
		this.show();
	}
	
	/**
	 * Gets the controller JPanel including all of the buttons functionality
	 * @return JPanel The controller ready to be inserted into a container
	 */
	private JPanel getController()
	{
		JPanel controller = new JPanel();
		controller.setLayout(new GridBagLayout()); //Uses grid bag layout
		GridBagConstraints gbc = new GridBagConstraints();
		//Padding is added
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5,5,5,5);
		
		//Controller components are then added
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		controller.add(getDPad(), gbc);
		
		gbc.gridx = 1;
		controller.add(getActionKeys(), gbc);
		
		//Controller is returned
		return controller;
	}
	
	/**
	 * Gets the D Pad used in movement and control it also contains the statistic labels
	 * @return JPanel the D Pad ready to be inserted into a container
	 */
	private JPanel getDPad()
	{
		JPanel dpad = new JPanel();
		dpad.setLayout(new GridBagLayout()); //Uses grid bag layout
		GridBagConstraints gbc = new GridBagConstraints();
		
		//Buttons are created using Images
		//Buttons are then added to JPanel
		gbc.gridx = 1;
		gbc.gridy = 0;
		JButton north = getImageButton("North.png");
		dpad.add(north, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		JButton west = getImageButton("West.png");
		dpad.add(west, gbc);
		
		gbc.gridx = 1;
		JButton pickup = getImageButton("PickUp.png");
		dpad.add(pickup, gbc);
		
		gbc.gridx = 2;
		JButton east = getImageButton("East.png");
		dpad.add(east, gbc);
		
		//Goal Label is added
		gbc.gridy = 2;
		dpad.add(getGoalLabel(), gbc);
		
		gbc.gridx = 1;
		JButton south = getImageButton("South.png");
		dpad.add(south, gbc);
		
		//Current gold label is added
		gbc.gridx = 0;
		dpad.add(getCurrentGoldLabel(), gbc);
		
		gbc.gridy = 3;
		dpad.add(getSwordIndicator(), gbc);
		
		gbc.gridx = 1;
		dpad.add(getQuitButton(), gbc);
		
		//Buttons functions are made using different directional characters
		//getActionString() is used to determine the currently selected action
		//String is then sent as a command to the game
		north.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameCommunicator.sendMessageToGame(getActionString() + " N");
			}
		});
		
		south.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameCommunicator.sendMessageToGame(getActionString() + " S");
			}
		});
		
		east.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameCommunicator.sendMessageToGame(getActionString() + " E");
			}
		});
		
		west.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameCommunicator.sendMessageToGame(getActionString() + " W");
			}
		});
		
		//Simple pickup command
		pickup.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gameCommunicator.sendMessageToGame("PICKUP");
			}
		});
		
		//DPad is returned
		return dpad;
	}
	
	/**
	 * Gets the action keys which allows the user to specify their desired action
	 * @return JPanel The action keys JPanel ready to be inserted into the Container
	 */
	private JPanel getActionKeys()
	{
		JPanel actionKeys = new JPanel();
		actionKeys.setLayout(new GridLayout(4, 0)); //Uses grid layout of a single column with 4 rows
		
		//Buttons are made using images
		//sFilename indicates selected version
		final JButton move = getImageButton("sMove.png"); //Move is defaultly selected
		final JButton attack = getImageButton("Attack.png");
		final JButton gift = getImageButton("Gift.png");
		
		//Button functions are added
		move.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Selected action is changed
				selectedAction = Action.Move;
				setImageButton("sMove.png", move); //Move is selected
				setImageButton("Attack.png", attack);
				setImageButton("Gift.png", gift);
			}
		});
		
		attack.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Selected action is changed
				selectedAction = Action.Attack;
				setImageButton("Move.png", move);
				setImageButton("sAttack.png", attack); //Attack is selected
				setImageButton("Gift.png", gift);
			}
		});
		
		gift.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Selected action is changed
				selectedAction = Action.Gift;
				setImageButton("Move.png", move);
				setImageButton("Attack.png", attack);
				setImageButton("sGift.png", gift); //Gift is selected
			}
		});
		
		//Keys are added
		
		actionKeys.add(move);
		actionKeys.add(attack);
		actionKeys.add(gift);
		actionKeys.add(getEndTurnButton());
		
		//JPanel is returned
		return actionKeys;
	}
	
	/**
	 * Gets the end turn button for ending a players turn
	 * @return JButton The end turn button
	 */
	private JButton getEndTurnButton()
	{
		//Creates a button using the image
		JButton endTurnButton = getImageButton("EndTurn.png");
		endTurnButton.setToolTipText("End Turn");
		//Adds the function
		endTurnButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//Sends an ENDTURN Command
				gameCommunicator.sendMessageToGame("ENDTURN");
			}
		});
		//Button is returned
		return endTurnButton;
	}
	
	/**
	 * An Enum which represents the three types of actions
	 * @author Benjamin Dring
	 */
	private enum Action
	{
		Move, Attack, Gift
	}
	
	/**
	 * Translates the selectedAction attribute into a String that can be sent as a command to the Game
	 * @return String the string format for selectedAction to be sent to the game
	 */
	private String getActionString()
	{
		//Uses simple switch statement
		//returns make breaks unneeded
		switch(selectedAction)
		{
		case Move:
			return "MOVE";
		case Attack:
			return "ATTACK";
		default: //Defaults to gift
			return "GIFT";
		}
	}
	

}
