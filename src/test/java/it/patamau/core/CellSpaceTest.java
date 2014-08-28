package it.patamau.core;

import org.junit.Test;

import it.patamau.util.Vector2f;

public class CellSpaceTest {

	@Test
	public void offsetTest(){
		
		CellSpacePartition csp = new CellSpacePartition(10);
		for(int y=-5; y<16; y+=10){
			for(int x=-5; x<16; x+=10){
				Entity e = new Entity(x+"x"+y);
				e.pos.set(x,y);
				csp.addEntity(e);
			}
		}
		
		Vector2f pos = new Vector2f(5,5);
		System.err.println(pos);
		csp.setCenter(pos);
		csp.dump(System.err);
		
		pos.set(11,5); //move right 1 cell
		System.err.println(pos);
		csp.setCenter(pos); //3 cell faults
		csp.dump(System.err);
		
		pos.set(15,12); //move up 1 cell
		System.err.println(pos);
		csp.setCenter(pos); //3 cell faults
		csp.dump(System.err);
		
		pos.set(5,11); //move left 1 cell
		System.err.println(pos);
		csp.setCenter(pos); //3 cell faults
		csp.dump(System.err);
		
		pos.set(-5,5); //move down-left 1+1 cells
		System.err.println(pos);
		csp.setCenter(pos); //5 cell faults
		csp.dump(System.err);
		
		pos.set(-500,-500);
		System.err.println(pos);
		csp.setCenter(pos); //all cell faults
		csp.dump(System.err);
	}
}
