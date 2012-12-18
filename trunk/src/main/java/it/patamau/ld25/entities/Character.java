package it.patamau.ld25.entities;

import it.patamau.ld25.Entity;
import it.patamau.ld25.Sprite;
import it.patamau.ld25.Weapon;

public class Character extends Entity {

	public Weapon weapon;
	public int hitpoints;

	public Character(final String id) {
		super(id);
		size = 24f;
		sprite = Sprite.getSprite("c");
		hitpoints = 100;
		weapon = Weapon.FIST.clone();
	}

	public void dealDamage(Bullet b) {
		hitpoints-=b.damage;
		if(hitpoints<=0){
			hidden=true;
			System.err.println(id+" killed");
		}else{
			System.err.println(id+" hp "+hitpoints);
		}
	}
}
