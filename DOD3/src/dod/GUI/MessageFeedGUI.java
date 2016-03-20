package dod.GUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Represents a generic abstract GUI which supplies some common GUI functions including a message feed
 * @author Benjamin Dring
 *
 */
public abstract class MessageFeedGUI extends JFrame implements ClientListener{
	private static final long serialVersionUID = 663205082653528881L;
	
	protected Container canvas;
	private JPanel messageFeed; //The message feed JPanel
	private JTextArea messageFeedText; //The TextArea contained in the JPanel
	private GridBagConstraints gbc; //Attributed Constraints
	
	/**
	 * The constructor of the class that sets up the Container and the messageFeed
	 */
	public MessageFeedGUI()
	{
		messageFeed = new JPanel();
		
		//Making the Text Area Scrollable by using a JPanel containg a scrollpane around a JTextArea
		messageFeedText = new JTextArea("");
		messageFeedText.setEditable(false);
		messageFeedText.setLineWrap(true);
		messageFeedText.setWrapStyleWord(true);
		
		JScrollPane scrollFeed = new JScrollPane(messageFeedText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//prefered size is set
		scrollFeed.setPreferredSize(new Dimension(400, 500));
		
		//Feed is added
		messageFeed.add(scrollFeed);
		messageFeed.setOpaque(false);
		
		canvas = getContentPane();
	}
	
	/**
	 * Adds a given message to the message feed
	 * @param message the message to be displayed
	 */
	public void addMessageToFeed(String message)
	{
		if (!messageFeedText.getText().equals(""))
		{
			//Adds two new lines and then the message
			messageFeedText.append("\n\n" + message);
		}
		else
		{
			messageFeedText.append(message);
		}
		//Set scroll to the bottom so new messages can be read
		messageFeedText.setCaretPosition(messageFeedText.getDocument().getLength());
	}
	
	/**
	 * Accessor for the MessageFeed
	 * @return JPanel the MessageFeed
	 */
	protected JPanel getMessageFeed()
	{
		return messageFeed;
	}
	
	/**
	 * The class that will fully display the GUI
	 */
	abstract public void displayGUI();
	
	/**
	 * Message Feed Text is wiped and restart message is posted
	 */
	@Override
	public void restartGame() {
		messageFeedText.setText("");
		addMessageToFeed("Game Reset");
	}
	

}
