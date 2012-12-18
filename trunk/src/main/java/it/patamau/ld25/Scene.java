package it.patamau.ld25;

import it.patamau.ld25.entities.Bullet;
import it.patamau.ld25.entities.Character;
import it.patamau.ld25.entities.Particle;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
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
	private int[] backgroundPixels;
	
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
			BufferedImage img = ImageIO.read(Scene.class.getResourceAsStream("/"+scenename+".jpg"));
			background = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			backgroundPixels = ((DataBufferInt)background.getRaster().getDataBuffer()).getData();
			img.getRGB(0, 0, img.getWidth(), img.getHeight(), backgroundPixels, 0, img.getWidth());
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
	 * Splat a sprite on the rgbMap (eyes-only)
	 * @param sprite
	 */
	public void splat(final Vector2f pos, final Sprite sprite){
		final Color4i target = new Color4i(), patch = new Color4i();
		final int hsw = sprite.getWidth()/2;
		final int hsh = sprite.getHeight()/2;
		for(int x=(int)(pos.x-hsw), sx=0; sx<sprite.getWidth(); ++x, ++sx){
			for(int y=(int)(pos.y-hsh), sy=0; sy<sprite.getHeight(); ++y, ++sy){
				//verify we are in range
				final int i = x+y*background.getWidth();
				if(i<0) continue;
				if(i>=backgroundPixels.length) break;
				target.setARGB(backgroundPixels[i]);
				patch.setARGB(sprite.pixels[sx+sy*sprite.getWidth()]);
				Color4i.blend(patch, target);
				backgroundPixels[i]=target.argb;
			}
		}
	}
}
