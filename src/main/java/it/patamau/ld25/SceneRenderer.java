package it.patamau.ld25;

import it.patamau.ld25.entities.Bullet;
import it.patamau.ld25.entities.Character;
import it.patamau.ld25.entities.Particle;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class SceneRenderer extends Canvas {

	private final Scene scene;
	private BufferStrategy strategy;
	
	private int offsetx, offsety;
	
	public SceneRenderer(final Scene scene){
		this.scene = scene;
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		this.setCursor(blankCursor);
	}
	
	public void init(){
		this.createBufferStrategy(2);
		this.strategy = this.getBufferStrategy();
	}

	private void drawEntity(final Entity e, final Graphics g){
		final int hsiz = (int)e.size/2;
		final int siz = (int)e.size;
		final int x = offsetx+(int)e.pos.x;
		final int y = offsety+(int)e.pos.y;
		if(e.sprite==null){
			g.drawOval(x-hsiz, y-hsiz, siz, siz);
			g.drawLine(x,y, (int)(x+e.dir.x*siz), (int)(y+e.dir.y*siz));
		}else{
			g.drawImage(e.sprite.img, x-e.sprite.getWidth()/2, y-e.sprite.getHeight()/2, null);
		}
	}
	
	public void render(int ox, int oy){
		this.offsetx=ox+this.getWidth()/2;
		this.offsety=oy+this.getHeight()/2;
		Graphics g = strategy.getDrawGraphics();
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		//map layer
		g.drawImage(scene.background, this.offsetx, this.offsety, null);
		g.drawImage(scene.grid.img, this.offsetx, this.offsety, null);
		
		//characters
		for(Character e: scene.getCharacters()){
			if(e.hidden) continue;
			drawEntity(e, g);
		}
		
		//bullets
		for(Bullet b: scene.bullets){
			if(b.hidden) continue;
			drawEntity(b, g);
		}
		
		//entities
		for(Entity e: scene.getEntities()){
			if(e.hidden) continue;
			drawEntity(e, g);
		}
		
		//effects
		for(Particle p: scene.particles){
			if(p.hidden) continue;
			drawEntity(p, g);
		}
		
		strategy.show();
		g.dispose();
	}
}
