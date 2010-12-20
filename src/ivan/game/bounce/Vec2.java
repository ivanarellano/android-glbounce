package ivan.game.bounce;

import android.util.Log;

/*
 * 2D Vector with common functions
 */
public class Vec2 {
    public float x;
    public float y;

    public Vec2() {
    	x = 0;
    	y = 0;
    }

    public Vec2(float x, float y) {
    	this.x = x;
    	this.y = y;
    }
    
    public Vec2(Vec2 v) {
    	this.x = v.x;
    	this.y = v.y;
    }    

    public boolean equals(Vec2 v) {
    	if(x == v.x && y == v.y) return true;
    	else return false;
    }
    
    public final float length() {
    	return (float) Math.sqrt(length2());
    }

    public final float length2() {
    	return (x * x) + (y * y);
    }

    public final void normalize() {
        final float magnitude = length();

        if (magnitude != 0.0f) {
            x /= magnitude;
            y /= magnitude;
        }
    }

    public final float distance2(Vec2 v) {
        float dx = x - v.x;
        float dy = y - v.y;
        return (dx * dx) + (dy * dy);
    }

    public final void set(float x, float y) {
    	this.x = x;
    	this.y = y;
    }
    
    public final void set(Vec2 v) {
    	x = v.x;
    	y = v.y;
    }    

    public final void print(String tag, String msg) {
    	Log.i(tag, msg + ": " + x + ", " + y);
    }
    
    public final float dot(Vec2 v)  {
    	return (float) (x * v.x + y * v.y);
    }

    public final void add(Vec2 v) {
    	x += v.x;
    	y += v.y;
    }   

    public final void add(float x, float y) {
    	this.x += x;
    	this.y += y;
    }    
    
    public final void subtract(Vec2 v) {
    	x -= v.x;
    	y -= v.y;
    }
    
    public final void subtract(float x, float y) {
    	this.x -= x;
    	this.y -= y;
    }        
    
    public final void multiply(Vec2 v) {
        x *= v.x;
        y *= v.y;
    }    
    
    public final void multiply(float val) {
    	x *= val;
    	y *= val;
    }
}
