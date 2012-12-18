package it.patamau.ld25;

/**
 * alpha, red, green, blue 0-255
 * @author Rogue
 *
 */
public class Color4i {

	public int argb; //0xAARRGGBB
	private int alpha, red, green, blue;
	
	/**
	 * Adds the blend color to the target using alpha channel to sum the values
	 * @param blend
	 * @param target
	 */
	public static void blend(final Color4i blend, final Color4i target){
		final float a = (float)blend.alpha/255f;
		target.red+=(int)((float)blend.red*a)-target.red*a;
		target.green+=(int)((float)blend.green*a)-target.green*a;
		target.blue+=(int)((float)blend.blue*a)-target.blue*a;
		target.computeARGB();
	}
	
	public Color4i(){
		//mh black transparent? whatever
	}
	
	public Color4i(int argb){
		setARGB(argb);
	}
	
	public Color4i(int red, int green, int blue){
		this(red, green, blue, 255);
	}
	
	public Color4i(int red, int green, int blue, int alpha){
		this.alpha=alpha;
		this.red=red;
		this.green=green;
		this.blue=blue;
		computeARGB();
	}
	
	public void setARGB(int argb){
		this.alpha = ((argb & 0xFF000000) >> 24) & 0x000000FF;
		this.red = (argb & 0x00FF0000) >> 16;
		this.green = (argb & 0x0000FF00) >> 8;
		this.blue = (argb & 0x000000FF);
		this.argb = argb;
	}
	
	private void computeARGB(){
		argb = (alpha & 0x000000FF) << 24;
		argb |= (red & 0x000000FF) << 16;
		argb |= (green & 0x000000FF) << 8;
		argb |= (blue & 0x000000FF);
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
		computeARGB();
	}

	public float getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
		computeARGB();
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
		computeARGB();
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
		computeARGB();
	}
	
	public String toString(){
		return  alpha+","+red+","+green+","+blue;
	}
	
}
