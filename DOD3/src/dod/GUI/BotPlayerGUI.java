package dod.GUI;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import dod.BotLogic.Bot;
import dod.Communicator.GameCommunicator;

/**
 * The GUI for the non-human bot.
 * displayGUI() will completely display the GUI with all its functionality.
 * @author Benjamin Dring
 */
public class BotPlayerGUI extends PlayerGUI {
	private static final long serialVersionUID = 9001606473323883119L;
	
	private Bot bot; //Logical bot used to decide on next actions

	/**
	 * The constructor for setting up the Bot Player GUI
	 * @param gameCommunicator GameCommunicator the communicator object used to communicate to the game
	 * @param name String the name of the user
	 * @param isFinalWindow Boolean indicates if this is the final window
	 * @param bot Bot The Bot Logic to decide on the next action
	 */
	public BotPlayerGUI(GameCommunicator gameCommunicator, String name, boolean isFinalWindow, Bot bot) {
		//Gives parameters to super class
		super(gameCommunicator, name, isFinalWindow);
		this.bot = bot;
		sayHello();
		//Bot is started in its own thread
		bot.start();
	}
	
	/**
	 * Displays the Bot GUI
	 */
	@Override
	public void displayGUI() {
		canvas.setLayout(new FlowLayout());
		
		//JPanel for gold is made
		JPanel goldPanel = new JPanel();
		goldPanel.setLayout(new GridLayout(2, 1));
		goldPanel.add(getCurrentGoldLabel());
		goldPanel.add(getGoalLabel());
		
		//JPanel for quit button and sword indicator
		JPanel quitPanel = new JPanel();
		quitPanel.setLayout(new GridLayout(2, 1));
		quitPanel.add(getQuitButton());
		quitPanel.add(getSwordIndicator());
		
		canvas.add(quitPanel);
		
		canvas.add(gameBoard);
	
		canvas.add(goldPanel);
	
		canvas.add(getMessenger());
		
		this.setSize(1500, 750);
		this.show();
	}
	
	/**
	 * Passes messages from the server to the bot for the bot to interpret
	 */
	@Override
	protected void handelMessage(String message)
	{
		if (bot != null)
		{
			bot.handelMessage(message);
		}
	}
	
	/**
	 * Passes look replies from the server to the bot for the bot to interpret
	 */
	@Override
	protected void handelLookReply(String[] lookReply)
	{
		if (bot != null)
		{
			bot.giveLookReply(lookReply);
		}
	}
	
	/**
	 * Causes the bot thread to eventually terminate.
	 * This is useful as we general want the bot to stop when the GUI is closed.
	 */
	@Override
	protected void die(String message)
	{
		if (bot != null)
		{
			bot.die();
		}
		super.die(message);
		
	}

}
