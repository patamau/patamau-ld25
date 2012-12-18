package it.patamau.ld25;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.awt.image.DataBufferInt;

public class Sprite {

	private static final HashMap<String,BufferedImage> imgLib = new HashMap<String,BufferedImage>();

	/**
	 * Lets use this for all the blood splatting
	 */
	public static final Sprite BLOODSPLAT = getSprite("bloodsplat");
	
	/**
	 * Sprite image buffer
	 * @param resource
	 * @return
	 */
	public static BufferedImage getImg(final String resource){
		BufferedImage img = imgLib.get(resource);
		if(null==img){
			try {
				final URL url = Sprite.class.getResource("/sprites/"+resource+".png");
				img = ImageIO.read(url);
				imgLib.put(resource, img);
			} catch (Throwable e) {
				System.err.println("Error loading "+resource);
				e.printStackTrace();
				return null;
			}	
		}
		return img;
	}

	/**
	 * Create a new sprite loading the image called with the given resource name
	 * @param resource
	 * @return
	 */
	public static Sprite getSprite(final String resource){
		final BufferedImage img = getImg(resource);
		
		final int width = img.getWidth();
		final int height = img.getHeight();

		final Sprite sprite = new Sprite(width, height);
		img.getRGB(0, 0, width, height, sprite.src, 0, width);
		sprite.updatePixels();
		return sprite;
	}
	
	public final int[] src;
	private final int width, height;
	
	//internals
	public BufferedImage img; //this is the local image of this sprite, not the original one
	public int[] pixels;
	private int pwidth, pheight;
	private float angle = 0f, scale = 1f;
	
	public Sprite(int width, int height){
		this.width = this.pwidth = width;
		this.height = this.pheight = height;
		src = new int[width*height];
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);		
		pixels = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
		
	}
	
	public int getWidth(){
		return pwidth;
	}
	
	public int getHeight(){
		return pheight;
	}
	
	public float getScale(){
		return scale;
	}
	
	public void clear(){
		for(int i=0; i<pixels.length; ++i){
			pixels[i]=0;
		}
	}
	
	/**
	 * This is an horrible memory sinkhole, dont use this
	 * @param scale
	 */
	public void setScale(float scale){
		if(this.scale==scale) return;
		this.scale=scale;
		//rescale matrix
		this.pwidth=(int)(width*scale);
		this.pheight=(int)(height*scale);		
		//create a new matrix with new dimensions
		pixels = new int[pwidth*pheight];
		//fillin pixels
		updatePixels();
	}
	
	private void updatePixels(){
		clear();
		
		int cx = pwidth/2;
		int cy = pheight/2;
		
		double acos = Math.cos(angle);
		double asin = Math.sin(angle);
		
		//calculate angle of source and destination coordintas
		for(int x=0; x<pwidth; ++x){
			for(int y=0; y<pheight; ++y){
				int a = x - cx;
                int b = y - cy;                
				int x1 = (int)Math.round(a * asin - b * acos)+cx;
				int y1 = (int)Math.round(b * asin + a * acos)+cy;
				if (x1 >= 0 && x1 < pwidth && y1 >= 0 && y1 < pheight) {
                    pixels[x+y*pwidth]=src[(int)(x1/scale)+(int)(y1/scale)*width];
                }
			}
		}
	}
	
	/**
	 * Rotate sprite around the center at the given angle in radians counterclockwise
	 * @param angle
	 */
	public void setAngle(float angle){
		if(this.angle==angle) return;
		this.angle=angle;
		//fill in pixels
		updatePixels();
	}

}
