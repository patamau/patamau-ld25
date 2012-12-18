package it.patamau.ld25;

public class Weapon {
	
	public static final Weapon
		FIST = new Weapon("Fist", 10f, 1f, 5f, 5f, 0.5f,0.01f, true),
		FLAMETHROWER = new Weapon("FlameThrower",25f, 80f, 5f, 5f, 0.02f, 1f, true),
		REVOLVER = new Weapon("Revolver", 100f, 400f, 1f, 2f, 0.3f, 1f, false),
		PISTOL = new Weapon("Pistol", 75f, 400f, 1f, 2f, 0.2f, 1f, false),
		SMG = new Weapon("SubMachineGun",50f, 400f, 1f, 2f, 0.1f, 1f, false),
		BOLTRIFLE = new Weapon("BoltActionRifle",100f, 600f, 1f, 4f, 0.4f, 1f, false),
		RIFLE = new Weapon("SemiAutomaticRifle",100f, 600f, 1f, 4f, 0.3f, 1f, false),
		ASSAULTRIFLE = new Weapon("AssaultRifle",75f, 600f, 1f, 4f, 0.15f, 1f, false),
		BAZOOKA = new Weapon("Bazooka",500f, 300f, 2f, 50f, 1f, 1f, true);

	public final String name;
	private long lastShotTime, shotInterval;
	public final float recoil, bulletSpeed, bulletSize, bulletDamage, rof, ttl;
	public final boolean explosive;
	
	private Weapon(final String name, float recoil, float bulletSpeed, float bulletSize, float bulletDamage, float rof, float ttl, boolean explosive){
		this.name = name;
		this.recoil = recoil;
		this.bulletSpeed = bulletSpeed;
		this.bulletSize = bulletSize;
		this.bulletDamage = bulletDamage;
		this.rof = rof;		
		this.ttl = ttl;
		this.explosive = explosive;
		shotInterval = (long)(rof*1000000000f); //get in nanoseconds
		lastShotTime = 0l;
	}
	
	public Weapon clone(){
		return new Weapon(name, recoil, bulletSpeed, bulletSize, bulletDamage, rof, ttl, explosive);
	}
	
	public boolean equals(Object o){
		if(o instanceof Weapon){
			final Weapon w = (Weapon)o;
			return w.recoil==recoil &&
					w.bulletDamage==bulletDamage &&
					w.bulletSize==bulletSize &&
					w.bulletSpeed==bulletSpeed &&
					w.rof==rof;
		}
		return false;
	}
	
	/**
	 * 
	 * @param time current time in nanoseconds
	 * @return
	 */
	public final boolean canShoot(long time){
		if(time-lastShotTime>shotInterval){
			lastShotTime = time;
			return true;
		}else{
			return false;
		}
	}
}
