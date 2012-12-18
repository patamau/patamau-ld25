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

	public boolean createAndShowGUI(){
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600, 600); //FIXME: use statics
		this.setLocationRelativeTo(null); //center
		this.getContentPane().add(game.renderer);
		
		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					setVisible(true);
				}
			});
	    } catch (Exception e) { 
	        System.err.println("createGUI didn't successfully complete");
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
	
	public void play(){
		game.start();
		this.dispose();
	}
	
	public static void main(final String args[]){
		Main m = new Main();
		if(m.createAndShowGUI()){
			m.play();
		}
	}
}
