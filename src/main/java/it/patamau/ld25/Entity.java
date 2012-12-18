package it.patamau.ld25;

public class Entity {

	public final String id;
	public boolean virtual, hidden;
	public float size;
	public final Vector2f pos, vel, dir;
	public Sprite sprite;
	
	public Entity(final String id){
		this.id = id;
		this.pos = new Vector2f();
		this.vel = new Vector2f();
		this.dir = new Vector2f();
		sprite = null;
	}
	
	public double getDistance(final Entity e){
		return Math.sqrt(pos.getDistanceSquared(e.pos));
	}
	
	public String toString(){
		return new String(id+":"+pos+":"+vel);
	}
}
