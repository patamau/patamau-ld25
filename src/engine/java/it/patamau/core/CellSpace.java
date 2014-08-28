package it.patamau.core;

import java.util.Collection;
import java.util.HashMap;

/**
 * A single cell space containing what a space is supped to contain.
 * It's up to the CellSpacePartition to organize the cellspaces
 * @author Rogue
 *
 */
public class CellSpace {

	/**
	 * Absolute offsets
	 */
	public int offsetx, offsety;
	
	public HashMap<String, Entity> entities;
	
	public CellSpace(){
	}

	public Collection<Entity> getEntities() {
		return entities.values();
	}
	
	public String toString(){
		return CellSpace.class.getSimpleName()+"["+offsetx+"x"+offsety+":"+(entities!=null?entities.size():"<null>")+" entities]";
	}
	
	//TODO: implement persistence
}
