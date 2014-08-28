package it.patamau.util;

public class Vector2f {

	public float x, y;
	
	public Vector2f(){
		x = 0f;
		y = 0f;
	}
	
	public Vector2f(float x, float y){
		this.x=x;
		this.y=y;
	}
	
	public Vector2f(final Vector2f v){
		x = v.x;
		y = v.y;
	}
	
	public void set(final Vector2f v){
		x = v.x;
		y = v.y;
	}
	
	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void setValues(final Vector2f v){
		x=v.x;
		y=v.y;
	}
	
	public double getLength(){
		return Math.sqrt(getLengthSquared());
	}
	
	public float getLengthSquared(){
		 return x * x + y * y;
	}
	
	public float getDistanceSquared(float x, float y){
		final float dx = this.x-x;
		final float dy = this.y-y;
		return dx*dx+dy*dy;
	}
	
	public float getDistanceSquared(final Vector2f v){
		final float dx = x-v.x;
		final float dy = y-v.y;
		return dx*dx+dy*dy;
	}
	
	public void add(final Vector2f v){
		x+=v.x;
		y+=v.y;
	}
	
	public void mul(float scalar){
		x*=scalar;
		y*=scalar;
	}
	
	public void diff(final Vector2f v){
		x-=v.x;
		y-=v.y;
	}
	
	public void normalize(){
		double l = Math.sqrt(getLengthSquared());
		if(l!=0){
			x/=l;
			y/=l;
		}
	}
	
	public void invert(){
		x=-x;
		y=-y;
	}
	
	public double toAngle(){
		return Math.atan2(y, x);
	}
	
	public double getAngleBetween(final Vector2f v){
		return Math.atan2(v.y, v.x) - Math.atan2(y, x);
	}
	
	public void fromAngle(float angle){
		final double _x = Math.cos(angle) * x - Math.sin(angle) * y;
        y = (float)(Math.sin(angle) * x + Math.cos(angle) * y);
        x = (float)_x;
	}
	
	public String toString(){
		return new String(x+","+y);
	}

	public double getDistance(float _x, float _y) {
		return Math.sqrt(getDistanceSquared(_x, _y));
	}

	public double getDistance(Vector2f pos) {
		return Math.sqrt(getDistanceSquared(pos));
	}
}
