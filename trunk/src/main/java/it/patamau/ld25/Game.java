package it.patamau.ld25;

import it.patamau.ld25.entities.Bullet;
import it.patamau.ld25.entities.Character;
import it.patamau.ld25.entities.Particle;

import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;

public class Game implements Runnable {
	
	public static float 
			SPEED_DEF = 100f, 
			ACCEL_DEF = 5f, 
			RUN_MUL = 2f, 
			PUSH_MUL = 0.1f;

	public final Scene scene;
	public final SceneRenderer renderer;

	private final Vector2f support; //for various math
	private final Line2D.Float ray; //for ray casting
	private final Character player;
	private final Entity target, gridhit;
	private final EntityController playerController;
	
	private boolean stop;
	
	private long time, lastTime;
	private float dt;
	
	public Game(){
		System.err.println("Loading environment...");
		//game environment
		scene = new Scene("map01");
		renderer = new SceneRenderer(scene);
		support = new Vector2f();
		ray = new Line2D.Float();
		
		System.err.println("Loading entities...");
		//player entity
		player = new Character("player");
		player.weapon = Weapon.ASSAULTRIFLE.clone();
		scene.addCharacter(player);
		
		//target used to aim with the mouse
		target = new Entity("target");
		target.size = 10f;
		target.virtual=true;
		scene.addEntity(target);
		
		gridhit = new Entity("gridhit");
		gridhit.size = 1f;
		gridhit.virtual=true;
		
		//dummy
		for(int i=0; i<15; ++i){
			final Character dummy = new Character("dummy"+i);
			dummy.hitpoints=5;
			dummy.ai = new AI(); //FIXME
			dummy.ai.c = dummy;
			dummy.ai.scene = scene;
			dummy.pos.set((float)(Math.random()*scene.grid.width), (float)(Math.random()*scene.grid.height));
			scene.addCharacter(dummy);
		}
		//controller
		playerController = new EntityController();
		renderer.addKeyListener(playerController);
		renderer.addMouseListener(playerController);
		renderer.addMouseMotionListener(playerController);
	}
	
	public void start(){
		renderer.addKeyListener(playerController);
		renderer.init();
		//init player pos to center
		player.pos.set(renderer.getWidth(),renderer.getHeight());
		
		stop = false;
		run();
	}
	
	public void stop(){
		stop = true;
	}
	
	/**
	 * 
	 * @param dt delta time in seconds
	 */
	private void update(){
		//process particle effects
		for(Particle p: scene.particles){
			if(!p.hidden && !p.checkAlive(dt)){
				p.hidden=true; //verify ttl expired
			}
		}
		//update characters and handle collisions
		for(Character ch: scene.getCharacters()){
			if(ch.hidden) continue; 
			if(ch.ai!=null){
				ch.ai.update(dt, time);
			}
			ch.sprite.setAngle((float)ch.dir.toAngle());
			if(ch.vel.x==0f && ch.vel.y==0f) continue; //don't compute if no velocity has to be applied or if hidden
			//compute next position
			support.x = ch.pos.x+ch.vel.x*dt;
			support.y = ch.pos.y+ch.vel.y*dt;
			//verify collision
			ray.setLine(ch.pos.x, ch.pos.y, support.x, support.y); //FIXME: use shared object to increase performances
			double d = Double.MAX_VALUE; //collision distance
			Entity hit = null;
			//check collision vs entities
			for(Character _e: scene.getCharacters()){
				if(_e == ch || _e.virtual || _e.hidden) continue;
				if(ray.ptSegDist(_e.pos.x, _e.pos.y)<(ch.size/2f+_e.size/2f)){
					final double _d = Math.sqrt(_e.pos.getDistanceSquared(ch.pos));
					if(_d<d){
						d = _d;
						hit=_e;
					}
				}
			}
			//check collision vs obstacle map
			if(scene.grid.checkCollision(ch, support, gridhit.pos)){
				final double _d = Math.sqrt(ch.pos.getDistanceSquared(gridhit.pos));
				if(_d<d){
					d = _d;
					hit = gridhit;
				}
			}
			if(hit!=null){ //if hit compute final position and block all	
				support.set(ch.vel);
				ch.vel.normalize();
				d = d - ch.size/2f - hit.size/2f -1f;
				ch.vel.mul((float)d);
				ch.pos.set(ch.pos.x+ch.vel.x, ch.pos.y+ch.vel.y);
				//block the hit entity
				//FIXME: push it!
				support.mul(PUSH_MUL);
				hit.vel.add(support);
				ch.vel.set(0,0);
			}else{
				ch.pos.set(support);
				ch.vel.x-=ch.vel.x*ACCEL_DEF*dt;
				ch.vel.y-=ch.vel.y*ACCEL_DEF*dt;
			}
			//verify out of bounds and keep it in
			scene.grid.checkInBounds(ch.pos);
		}
		//deal with bullets
		for(Bullet bullet: scene.bullets){
			if((bullet.vel.x==0f && bullet.vel.y==0f) || bullet.hidden) continue; //don't compute if no velocity has to be applied or if hidden
			//compute next position
			float _x = bullet.pos.x+bullet.vel.x*dt;
			float _y = bullet.pos.y+bullet.vel.y*dt;
			//verify collision
			ray.setLine(bullet.pos.x, bullet.pos.y, _x, _y);
			double d = Double.MAX_VALUE; //collision distance
			Entity hit = null;
			//check collision vs entities
			for(Entity ch: scene.getCharacters()){
				if(ch == bullet || ch.virtual || ch.hidden) continue;
				if(ray.ptSegDist(ch.pos.x, ch.pos.y)<(bullet.size/2f+ch.size/2f)){
					final double _d = Math.sqrt(ch.pos.getDistanceSquared(bullet.pos));
					if(_d<d){
						d = _d;
						hit=ch;
					}
				}
			}
			//check collision vs obstacle map
			support.set(_x, _y);
			if(scene.grid.checkCollision(bullet, support, gridhit.pos)){
				final double _d = bullet.pos.getDistanceSquared(gridhit.pos);
				if(_d<d){
					d = _d;
					hit = gridhit;
				}
			}
			if(hit!=null){	//something hit!
				bullet.hidden=true;
				bullet.pos.set(hit.pos);
				if(hit instanceof Character){
					damage(bullet, (Character)hit);
					//push character back a bit
					bullet.vel.mul(PUSH_MUL);
					hit.vel.add(bullet.vel);
				}else{
					damage(bullet, null);
				}
				bullet.vel.set(0,0); //this is optional...				
			}else{
				if(!bullet.checkAlive(dt)){
					bullet.hidden=true; //verify range expired
				}else{
					bullet.pos.set(_x, _y);
				}
			}
			//keep bullet in bounds
			scene.grid.checkInBounds(bullet.pos);
		}
	}
	
	/**
	 * This can be used for bombs too
	 * @param bullet
	 */
	private void damage(final Bullet bullet, final Character directHit){
		//FIXME: not a generic particle it is... damn	
		//smoke here
		final Particle p = scene.getParticle();
		p.pos.set(bullet.pos);
		p.ttl=0.5f;
		p.hidden=false;
		if(bullet.explode){
			final float range = bullet.damage/2f+1f;
			for(Character ch: scene.getCharacters()){
				if(ch.hidden||ch.virtual) continue;
				final double d = ch.pos.getDistance(bullet.pos);
				if(d<(range+ch.size/2f)){
					ch.dealDamage(bullet);	
					Sprite.BLOODSPLAT.setAngle((float)(Math.random()*Math.PI*2.0));
					scene.splat(ch.pos,Sprite.BLOODSPLAT);
				}
			}
		}else if(directHit!=null){
			directHit.dealDamage(bullet);
			Sprite.BLOODSPLAT.setAngle((float)(Math.random()*Math.PI*2.0));
			scene.splat(directHit.pos,Sprite.BLOODSPLAT);
		}
		scene.grid.destroy(bullet.pos, bullet.damage);
		
	}
	
	private void updatePlayer(){
		if(playerController.exit){
			stop=true;
			return;
		}
		if(playerController.keys[KeyEvent.VK_0]){
			player.weapon = Weapon.FIST.clone();
		}else if(playerController.keys[KeyEvent.VK_1]){
			player.weapon = Weapon.PISTOL.clone();
		}else if(playerController.keys[KeyEvent.VK_2]){
			player.weapon = Weapon.SMG.clone();
		}else if(playerController.keys[KeyEvent.VK_3]){
			player.weapon = Weapon.ASSAULTRIFLE.clone();
		}else if(playerController.keys[KeyEvent.VK_4]){
			player.weapon = Weapon.BAZOOKA.clone();
		}
		if(playerController.left ||
				playerController.right ||
				playerController.up ||
				playerController.down){
			float xv = 0f, yv = 0f;
			if(playerController.left){
				xv=-1f;
			}
			if(playerController.right){
				xv=1f;
			}
			if(playerController.up){
				yv=-1f;
			}
			if(playerController.down){
				yv=1f;
			}
			float playerSpeed = SPEED_DEF;
			if(playerController.run){
				playerSpeed = SPEED_DEF*RUN_MUL;
			}
			support.set(xv, yv);
			support.normalize();
			player.vel.x+=(support.x*playerSpeed-player.vel.x)*ACCEL_DEF*dt;
			player.vel.y+=(support.y*playerSpeed-player.vel.y)*ACCEL_DEF*dt;
		}
		target.pos.set(player.pos.x+playerController.mousex-renderer.getWidth()/2f, 
				player.pos.y+playerController.mousey-renderer.getHeight()/2f);
		player.dir.set(target.pos);
		player.dir.diff(player.pos);
		player.dir.normalize();
		player.sprite.setAngle((float)player.dir.toAngle());
		if(playerController.mousepressed && player.weapon.canShoot(time)){
			//shot the bullet
			final Bullet b = scene.getBullet(); //use a bullet factory plz
			b.pos.set(player.pos);
			b.pos.x+=player.dir.x*(player.size+b.damage+b.size);
			b.pos.y+=player.dir.y*(player.size+b.damage+b.size);
			b.vel.set(player.dir);
			b.vel.mul(player.weapon.bulletSpeed);
			b.vel.add(player.vel);
			b.size = player.weapon.bulletSize;
			b.damage = player.weapon.bulletDamage;
			b.ttl = player.weapon.ttl;
			b.explode = player.weapon.explosive;
			b.hidden=false; //visible again
			b.sprite.setAngle((float)player.dir.toAngle());
			support.set(player.dir);
			support.invert();
			support.mul(player.weapon.recoil);
			player.vel.add(support);
			player.weapon.sfx.play();
		}
	}
	
	public void run() {
		lastTime = System.nanoTime();
		while(!stop){
			time = System.nanoTime();
			dt = (time - lastTime)/1000000000f;
			lastTime = time;
			
			updatePlayer();
			update();
			renderer.render(-(int)player.pos.x, -(int)player.pos.y);
		}
		System.err.println("Stopped");
	}
}
