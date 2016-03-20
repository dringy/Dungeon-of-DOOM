import javax.swing.JFrame;
import javax.swing.JOptionPane;

import dod.GUI.MainMenu;
/**
 * The Main Class to be run to play Dungeon Of Dooom 3
 * @author Benjamin Dring
 */
public class DungeonOfDooom3 {
	
	/**
	 * The main class that is run when the program is run
	 * @param args String[] These are ignored
	 */
	public static void main(String[] args) {
		try
		{
			//Set up selections menus
			MainMenu mainMenu = new MainMenu();
			mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Closing the main menu exits the program
			mainMenu.displayTitleMenu();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Unknown Error");
			//e.printStackTrace();
		}
	}
}