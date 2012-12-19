package it.patamau.ld25;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class EntityController implements KeyListener, MouseListener, MouseMotionListener {
	
	public final boolean[] keys = new boolean[65535]; //typed keys
	
	public boolean up, down, left, right;
	public boolean run;
	public int mousex, mousey;
	public boolean mousepressed;
	public boolean exit;
	
	private void toggleKey(int code, boolean pressed){
		keys[code]=pressed;
		switch(code){
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				up = pressed;
			break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				down = pressed;
			break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				left = pressed;
			break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				right = pressed;
			break;
			case KeyEvent.VK_SHIFT:
				run = pressed;
			break;
			case KeyEvent.VK_ESCAPE:
				exit = pressed;
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}
	
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		mousepressed=true;
	}

	public void mouseReleased(MouseEvent e) {
		mousepressed=false;
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseDragged(MouseEvent e) {
		mousex=e.getX();
		mousey=e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		mousex=e.getX();
		mousey=e.getY();
	}

}
