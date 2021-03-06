package it.patamau.ld25;

import it.patamau.ld25.entities.Bullet;
import it.patamau.ld25.entities.Character;
import it.patamau.ld25.entities.Particle;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Scene {

	public static int nbullets = 512, nparticles = 512;
	
	private final Map<String, Entity> entities;
	private final Map<String, Character> characters;
	public final Bullet[] bullets;
	public final Particle[] particles;
	private int nextBullet = 0, nextParticle = 0;
	public Grid grid;
	
	public BufferedImage background;
	
	//support objects for splatting
	private final Color4i 
		splatTarget = new Color4i(), 
		splatPatch = new Color4i();
	
	public Scene(final String scenename){
		characters = new HashMap<String, Character>();
		entities = new HashMap<String, Entity>();
		bullets = new Bullet[nbullets];
		for(int i=0; i<nbullets; ++i){
			bullets[i] = new Bullet("bullet"+i);
			bullets[i].hidden=true;
		}

		particles = new Particle[nparticles];
		for(int i=0; i<nparticles; ++i){
			particles[i] = new Particle("smoke"+i, "s");
			particles[i].hidden=true;
		}
		try {
			grid = Grid.loadObstacleMap("/"+scenename+".png");
		} catch (IOException e) {
			e.printStackTrace();
			grid = new Grid(200,200);
		}
		try{
			System.err.println("Loading background image "+scenename+".jpg");
			background = ImageIO.read(Scene.class.getResourceAsStream("/"+scenename+".jpg"));
		} catch (IOException e){
			background = null;
			e.printStackTrace();
		}
	}
	
	public Character getCharacter(final String id){
		return characters.get(id);
	}
	
	public void addCharacter(final Character e){
		characters.put(e.id, e);
	}
	
	public void addEntity(final Entity e){
		entities.put(e.id, e);
	}
	
	public Entity getEntity(final String id){
		return entities.get(id);
	}
	
	public void clear(){
		characters.clear();
		entities.clear();
	}
	
	public Collection<Character> getCharacters(){
		return characters.values();
	}
	
	public Collection<Entity> getEntities(){
		return entities.values();
	}
	
	public Bullet getBullet(){
		final Bullet b = bullets[nextBullet];
		++nextBullet;
		nextBullet%=nbullets;
		return b;
	}
	
	public Particle getParticle(){
		final Particle b = particles[nextParticle];
		++nextParticle;
		nextParticle%=nparticles;
		return b;
	}
	
	/**
	 * Splat a sprite on the background (eyes-only)
	 * @param sprite
	 */
	public void splat(final Vector2f pos, final Sprite sprite){
		final int hsw = sprite.getWidth()/2;
		final int hsh = sprite.getHeight()/2;
		for(int x=(int)(pos.x-hsw), sx=0; sx<sprite.getWidth(); ++x, ++sx){
			if(x<0) continue;
			if(x>=background.getWidth()) break;
			for(int y=(int)(pos.y-hsh), sy=0; sy<sprite.getHeight(); ++y, ++sy){
				//verify we are in range
				if(y<0) continue;
				if(y>=background.getHeight()) break;
				splatTarget.setARGB(background.getRGB(x, y));
				splatPatch.setARGB(sprite.pixels[sx+sy*sprite.getWidth()]);
				Color4i.blend(splatPatch, splatTarget);
				background.setRGB(x, y, splatTarget.argb);
			}
		}
	}
}
