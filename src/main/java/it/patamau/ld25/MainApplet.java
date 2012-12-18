package it.patamau.ld25;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * This is the main for the applet version.
 * @author Rogue
 *
 */
public class MainApplet extends JApplet {

	public void init(){
		try {
			final Game game = new Game();
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                	getContentPane().add(game.renderer);
                }
            });
    		game.start();
        } catch (Exception e) { 
            System.err.println("createGUI didn't successfully complete");
            e.printStackTrace();
        }
	}
	
}
