package it.patamau.ld25;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * This is the main for the applet version.
 * @author Rogue
 *
 */
public class MainApplet extends JApplet {
	
	private void createGUI(){
		Game game = new Game();
		this.getContentPane().add(game.renderer);
	}

	public void init(){
		try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) { 
            System.err.println("createGUI didn't successfully complete");
            e.printStackTrace();
        }
	}
	
}
