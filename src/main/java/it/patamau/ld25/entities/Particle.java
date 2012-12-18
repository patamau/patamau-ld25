package it.patamau.ld25.entities;

import it.patamau.ld25.Entity;
import it.patamau.ld25.Sprite;

/**
 * A particle effect is meant to change quickly over time
 * Use a buffer to cache particles moron
 * @author Rogue
 *
 */
public class Particle extends Entity {
	
	//FIXME: do you think this is smart? I doubt it
	public static final Particle SMOKE = new Particle("smoke", "s");

	public float ttl; //time to leave or time to live, does it matter?
	
	public Particle(String id, String spritename) {
		super(id);
		sprite = Sprite.getSprite(spritename); //FIXME: use a generic particle sprite
	}

	public boolean checkAlive(float dt){
		return (ttl-=dt)>0f;
	}
	
}
