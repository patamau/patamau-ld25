package it.patamau.core;

import it.patamau.util.Vector2f;

public class Entity {

	public final String id;
	public final Vector2f pos;
	public final Vector2f dir;
	public float size;
	
	public Entity(final String id){
		this.id = id;
		pos = new Vector2f();
		dir = new Vector2f();
	}
}
