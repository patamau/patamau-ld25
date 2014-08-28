package it.patamau.ld25.entities;

public class Bullet extends Particle {
	
	public float damage, range; //damange vars
	public boolean explode;

	public Bullet(final String id) {
		//FIXME: use a custom sprite depending on bullet type (flames, fist, rocket, projectile)
		super(id, "b"); //b is the b.png file for the sprite
	}
	
}
