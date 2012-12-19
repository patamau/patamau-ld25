package it.patamau.ld25;

import it.patamau.ld25.entities.Bullet;
import it.patamau.ld25.entities.Character;

public class AI {
	
	public Character c; //the controlled character
	public Scene scene;
	
	private final Vector2f support = new Vector2f();
	
	//FIXME: bring this one inside the weapon class
	private void fire(final Vector2f target){
		final Bullet b = scene.getBullet(); //use a bullet factory plz
		b.pos.set(c.pos);
		support.set(target);
		support.diff(c.pos);
		support.normalize();
		c.dir.set(support);
		b.pos.x+=c.dir.x*(c.size+b.damage+b.size);
		b.pos.y+=c.dir.y*(c.size+b.damage+b.size);
		b.vel.set(c.dir);
		b.vel.mul(c.weapon.bulletSpeed);
		b.vel.add(c.vel);
		b.size = c.weapon.bulletSize;
		b.damage = c.weapon.bulletDamage;
		b.ttl = c.weapon.ttl;
		b.explode = c.weapon.explosive;
		b.hidden=false; //visible again
		b.sprite.setAngle((float)c.dir.toAngle());
		support.set(c.dir);
		support.invert();
		support.mul(c.weapon.recoil);
		c.vel.add(support);
		c.weapon.sfx.play();
	}

	public void update(float dt, long time){
		final Entity e = scene.getCharacter("player"); //this can be done statically
		if(!scene.grid.checkCollision(c, e.pos, support)){
			if(c.weapon.canShoot(time)){
				fire(e.pos);
			}
		}
	}
}
