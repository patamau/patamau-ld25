package it.patamau.ld25;

public class Weapon {
	
	public static final Weapon
		FIST = new Weapon("Fist", 10f, 1f, 5f, 5f, 2, 0.01f, true, "explode"),
		FLAMETHROWER = new Weapon("FlameThrower", 10f, 120f, 5f, 5f, 20, 1f, true, "explode"),
		REVOLVER = new Weapon("Revolver", 100f, 600f, 1f, 2f, 3, 1f, false, "pistol"),
		PISTOL = new Weapon("Pistol", 75f, 600f, 1f, 2f, 4, 1f, false, "pistol"),
		SMG = new Weapon("SubMachineGun",50f, 600f, 1f, 2f, 10, 1f, false, "smg"),
		BOLTRIFLE = new Weapon("BoltActionRifle",100f, 1000f, 1f, 4f, 1, 1f, false, "boltrifle"),
		RIFLE = new Weapon("SemiAutomaticRifle",100f, 1000f, 1f, 4f, 3, 1f, false, "rifle"),
		ASSAULTRIFLE = new Weapon("AssaultRifle",75f, 1000f, 1f, 4f, 6, 1f, false,"assault"),
		BAZOOKA = new Weapon("Bazooka",500f, 1000f, 2f, 50f, 1, 1f, true,"explode");

	public final String name;
	private long lastShotTime, shotInterval;
	public final float recoil, bulletSpeed, bulletSize, bulletDamage, ttl;
	public final float rof;
	public final boolean explosive;
	public final Sfx sfx;
	
	private Weapon(final String name, float recoil, float bulletSpeed, float bulletSize, float bulletDamage, float rof, float ttl, boolean explosive, String sfx){
		this.name = name;
		this.recoil = recoil;
		this.bulletSpeed = bulletSpeed;
		this.bulletSize = bulletSize;
		this.bulletDamage = bulletDamage;
		this.rof = rof;		
		this.ttl = ttl;
		this.explosive = explosive;
		this.sfx = new Sfx(sfx);
		
		//internals
		shotInterval = (long)(1000000000f/rof);
		lastShotTime = 0l;
	}
	
	public Weapon clone(){
		return new Weapon(name, recoil, bulletSpeed, bulletSize, bulletDamage, rof, ttl, explosive, sfx.name);
	}
	
	public boolean equals(Object o){
		if(o instanceof Weapon){
			final Weapon w = (Weapon)o;
			return w.recoil==recoil &&
					w.bulletDamage==bulletDamage &&
					w.bulletSize==bulletSize &&
					w.bulletSpeed==bulletSpeed &&
					w.rof==rof &&
					w.ttl==ttl &&
					w.explosive==explosive;
		}
		return false;
	}
	
	/**
	 * 
	 * @param time current time in nanoseconds
	 * @return
	 */
	public final boolean canShoot(final long time){
		if(time-lastShotTime>shotInterval){
			lastShotTime = time;
			return true;
		}else{
			return false;
		}
	}
}
