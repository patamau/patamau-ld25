package it.patamau.ld25;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Grid {
	
	/**
	 * Colored map for tile types, not the background tip
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Grid loadObstacleMap(final String resource) throws IOException{
		final URL url = Grid.class.getResource(resource);
		System.err.println("Loading obstacle map from image "+resource +"...");
		final BufferedImage img = ImageIO.read(url);
		//get image size
		final int width = img.getWidth();
		final int height = img.getHeight();
		System.err.println("Preparing grid "+width+"x"+height);
		final Grid grid = new Grid(width, height);
		//precompute white color
		final int white = Color.WHITE.getRGB();
		final int background = new Color4i(0,0,0,0).argb;
		//allocate pixels buffer
		final int[] rgb = new int[width*height];
		//load pixles
		img.getRGB(0, 0, width, height, rgb, 0, width);
		//for all the pixels set the obstacle map to either true or false depending on the color
		int i;
		System.err.print("Filling the obstacle map >");
		long last = System.currentTimeMillis();
		for(int x=0; x<width; ++x){
			for(int y=0; y<height; ++y){
				i = x+y*width;
				if((System.currentTimeMillis()-last)>=500){
					last = System.currentTimeMillis();
					System.err.print('.');
				}
				grid.map[x][y]=rgb[i]==white?0:1;
				grid.rgbMap[i]=rgb[i]==white?background:rgb[i];
			}
		}
		System.err.println('<');
		
		return grid;
	}
	
	public static int MAX_FREEAREA_ITERATIONS = 100;
	
	public final BufferedImage img;
	private final int[] rgbMap;
	public final int[][] map;
	public final int width, height;
	
	private final Vector2f support = new Vector2f();
	
	public Grid(int width, int height){
		this.width = width;
		this.height = height;
		map = new int[width][height];
		img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		rgbMap = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();		
	}
	
	/**
	 * Force a vector to stay in bounds
	 * @param v
	 */
	public void checkInBounds(final Vector2f v){
		v.x=getInBoundsX(v.x);
		v.y=getInBoundsY(v.y);
	}
	
	public float getInBoundsX(float x){
		if(x<0) x = width+x%width;
		if(x>=width) x%=width;
		return x;
	}
	
	public float getInBoundsY(float y){
		if(y<0) y = height+y%height;
		if(y>=height) y%=height;
		return y;
	}
	
	public int getInBoundsX(int x){
		if(x<0) x = width+x%width;
		if(x>=width) x%=width;
		return x;
	}
	
	public int getInBoundsY(int y){
		if(y<0) y = height+y%height;
		if(y>=height) y%=height;
		return y;
	}
	
	/**
	 * Fix vector in bound and return tile
	 * @param v
	 * @return
	 */
	public Tile getTile(final Vector2f v){
		checkInBounds(v);
		return Tile.getTile(map[(int)v.x][(int)v.y]);
	}
	
	public Tile getTile(int x, int y){
		return Tile.getTile(map[getInBoundsX(x)][getInBoundsY(y)]);
	}
	
	public double getDistance(final Vector2f pos, float x, float y){
		final float dx = pos.x-x;
		final float dy = pos.y-y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public void destroy(final Vector2f pos, final float range){
		if(range==1f){
			final int x = (int) getInBoundsX(pos.x);
			final int y = (int) getInBoundsY(pos.y);
			map[x][y]=0;
			rgbMap[x+y*width]=Tile.getTile(0).color.argb;
		}else{
			final double hrange = range*0.5d; //half range
			final int xrange = (int)(pos.x+range);
			final int yrange = (int)(pos.y+range);
			for(int x=(int)(pos.x-hrange); x<=xrange; ++x){
				for(int y=(int)(pos.y-hrange); y<=yrange; ++y){
					if(getDistance(pos, x, y)>hrange) continue;
					//verify we are in range
					final int _x = getInBoundsX(x);
					final int _y = getInBoundsY(y);
					//FIXME: destroy
					map[_x][_y]=0;
					rgbMap[_x+_y*width]=Tile.getTile(0).color.argb;
				}
			}
		}
	}	
	
	public boolean isColliding(final Vector2f pos, float size) {
		final int hsiz = (int)(size/2);
		final int x0 = getInBoundsX((int)(pos.x-hsiz)),
				y0 = getInBoundsY((int)(pos.y-hsiz)),
				x1 = getInBoundsX((int)(pos.x+hsiz)),
				y1 = getInBoundsY((int)(pos.y+hsiz));
		for(int x=x0; x<=x1; ++x){
			for(int y=y0; y<=y1; ++y){
				if(Tile.getTile(map[x][y]).obstacle) return true;
			}
		}
		return false;
	}
	
	/**
	 * Cast a ray from src to dest
	 * Store final cell coordinates into hit
	 * @param src
	 * @param dest
	 * @param hit
	 * @return true if hits
	 */
	public boolean checkCollision(final Entity src, final Vector2f dest, final Vector2f hit){
		//discrete handling (that's a bad way to round floating points pal!)
		support.set(src.vel);
		support.normalize();
		support.mul(src.size/2f);
		hit.x = (int) (src.pos.x+support.x);
		hit.y = (int) (src.pos.y+support.y);
		final int destX = (int) dest.x;
		final int destY = (int) dest.y;
		//compute diff vector and squared constants to compute delta constants
		final float vx = destX-hit.x;
		final float vy = destY-hit.y;
		final float pvx = vx*vx;
		final float pvy = vy*vy;
		//length of ray from one x or y-side to next x or y-side
		final double deltaDistX = pvx!=0f?Math.sqrt(1 + pvy / pvx):0d;
		final double deltaDistY = pvy!=0f?Math.sqrt(1 + pvx / pvy):0d;
		//allocate step variables
		final int sx, sy;
		double sideDistX, sideDistY;

		//calculate step and initial sideDist
		sx = vx<0f?-1:1;
		sideDistX = deltaDistX;
		sy = vy<0f?-1:1;
		sideDistY = deltaDistY;
		
		int limit = (int)(destX>hit.x?destX-hit.x:hit.x-destX)+(int)(destY>hit.y?destY-hit.y:hit.y-destY);
		
		//perform DDA
		for(;limit>=0;--limit) {			
			if(getTile(hit).obstacle){
				return true;
			}
			//jump to next map square
			if (sideDistX < sideDistY) { //X step
				sideDistX += deltaDistX;
				hit.x += sx;
			} else {
				sideDistY += deltaDistY; // Y step
				hit.y += sy;
			}			
		}
		return false;
	}	
	
	/**
	 * Set a random non colliding position in the given range to the store vector.
	 * A random position is generated for a MAX_FREEAREA_ITERATIONS maximum number of times
	 * afterwards it returns false giving up. 
	 * Be aware this method is not checking against other entities on the scene 
	 * @param range the range around the vector which should be checked non colliding
	 * @param offsetx horizontal offset for the random position generator
	 * @param offsety vertical offset for the random position generator
	 * @param sizx the horizontal size to generate positions into
	 * @param sizy the vertical size to generate positions into
	 * @param store where the coordinates will be stored
	 * @return true if a valid position has been found, false if not
	 */
	public boolean findFreeArea(float range, long offsetx, long offsety, long sizx, long sizy, final Vector2f store){
		//try with a random position
		for(int i=0; i<MAX_FREEAREA_ITERATIONS; ++i){
			store.set(offsetx+(float)(Math.random())*sizx, offsety+(float)(Math.random())*sizy);
			if(!isColliding(store, range)) return true;
		}
		return false;
	}
}
