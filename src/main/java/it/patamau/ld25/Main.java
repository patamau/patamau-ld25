package it.patamau.ld25;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * This is the main for the desktop version
 * @author Rogue
 *
 */
public class Main extends JFrame {
	
	final Game game;
	
	public Main(){
		game = new Game();
	}

	public void createAndShowGUI(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600); //FIXME: use statics
		this.setLocationRelativeTo(null); //center
		this.getContentPane().add(game.renderer);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				setVisible(true);
				game.start();
			}
		});
	}
	
	public static void main(final String args[]){
		Main m = new Main();
		m.createAndShowGUI();
	}
}
