package it.patamau.ld25;

import java.awt.geom.Line2D;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import it.patamau.ld25.entities.Bullet;
import it.patamau.ld25.entities.Character;
import it.patamau.ld25.entities.Particle;

public class Game implements Runnable {
	
	public static float spd = 100f, accel = 5f, runmul = 3f;

	public final Scene scene;
	public final SceneRenderer renderer;

	private final Vector2f support; //for various math
	private final Line2D.Float ray; //for ray casting
	private final Character player;
	private final Entity target, gridhit;
	private final EntityController playerController;
	
	private final Thread thread;
	private boolean stop;
	
	private long time, lastTime;
	private float dt;
	
	
	public Game(){
		//game environment
		scene = new Scene("map01");
		renderer = new SceneRenderer(scene);
		thread = new Thread(this);
		support = new Vector2f();
		ray = new Line2D.Float();
		
		//player entity
		player = new Character("player");
		player.size = 10f;
		player.weapon = Weapon.ASSAULTRIFLE.clone();
		scene.addCharacter(player);
		
		//target used to aim with the mouse
		target = new Entity("target");
		target.size = 8f;
		target.virtual=true;
		scene.addEntity(target);
		
		gridhit = new Entity("gridhit");
		gridhit.size = 1f;
		gridhit.virtual=true;
		
		//dummy
		for(int i=0; i<100; ++i){
			final Character dummy = new Character("dummy"+i);
			dummy.size = 10f;
			dummy.hitpoints=5;
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
		thread.start();
	}
	
	public void stop(){
		stop = true;
	}
	
	public void playSound(final String resource) throws LineUnavailableException, UnsupportedAudioFileException, IOException{
		Clip clip = AudioSystem.getClip();
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(Main.class.getResourceAsStream("/"+resource));
        clip.open(inputStream);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-20.0f); // Reduce volume by 10 decibels.
        clip.start(); 
	}
	
	/**
	 * 
	 * @param dt delta time in seconds
	 */
	private void update(float dt){
		//process particle effects
		for(Particle p: scene.particles){
			if(!p.hidden && !p.checkAlive(dt)){
				p.hidden=true; //verify ttl expired
			}
		}
		//update characters and handle collisions
		for(Character ch: scene.getCharacters()){
			if((ch.vel.x==0f && ch.vel.y==0f) || ch.hidden) continue; //don't compute if no velocity has to be applied or if hidden
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
				final double _d = ch.pos.getDistanceSquared(gridhit.pos);
				if(_d<d){
					d = _d;
					hit = gridhit;
				}
			}
			if(hit!=null){ //if hit compute final position and block all	
				ch.vel.normalize();
				final float _d = (float)d - ch.size/2f -hit.size/2f;
				ch.vel.mul(_d);
				support.x = (ch.pos.x+ch.vel.x);
				support.y = (ch.pos.y+ch.vel.y);
				ch.vel.set(0,0);
				ch.pos.set(support);
				//block the hit entity
				hit.vel.set(0,0);
			}else{
				ch.pos.set(support);
				ch.vel.x-=ch.vel.x*accel*dt;
				ch.vel.y-=ch.vel.y*accel*dt;
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
			if(scene.grid.checkCollision(bullet, new Vector2f(_x,_y), gridhit.pos)){
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
					bullet.vel.mul(0.25f);
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
					final Sprite sprite = Sprite.getSprite("bloodsplat");
					sprite.setAngle((float)(Math.random()*Math.PI*2.0));
					scene.splat(ch.pos,sprite);
				}
			}
		}else if(directHit!=null){
			directHit.dealDamage(bullet);
			final Sprite sprite = Sprite.getSprite("bloodsplat");
			sprite.setAngle((float)(Math.random()*Math.PI*2.0));
			scene.splat(directHit.pos,sprite);
		}
		scene.grid.destroy(bullet.pos, bullet.damage);
		
	}
	
	private void updatePlayer(){
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
			float playerSpeed = spd;
			if(playerController.run){
				playerSpeed = spd*runmul;
			}
			support.set(xv, yv);
			support.normalize();
			player.vel.x+=(support.x*playerSpeed-player.vel.x)*accel*dt;
			player.vel.y+=(support.y*playerSpeed-player.vel.y)*accel*dt;
		}
		target.pos.set(player.pos.x+playerController.mousex-renderer.getWidth()/2f, 
				player.pos.y+playerController.mousey-renderer.getHeight()/2f);
		player.dir.set(target.pos);
		player.dir.diff(player.pos);
		player.dir.normalize();
		player.sprite.setAngle((float)player.dir.toAngle());
		if(playerController.mousepressed && player.weapon.canShoot(time)){
			//shot the bullet
			final Bullet b = scene.getBullet();
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
			try {
				playSound("assault.wav");
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		lastTime = System.nanoTime();
		while(!stop){
			time = System.nanoTime();
			dt = (time - lastTime)/1000000000f;
			lastTime = time;
			
			updatePlayer();
			update(dt);
			renderer.render(-(int)player.pos.x, -(int)player.pos.y);
		}
	}
}
