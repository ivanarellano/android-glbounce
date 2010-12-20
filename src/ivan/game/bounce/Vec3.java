package ivan.game.bounce;

import android.util.Log;

public final class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3() {
    	x = 0;
    	y = 0;
    	z = 0;
    }

    public Vec3(float x, float y, float z) {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }

    public boolean equals(Vec3 v) {
    	if(this.x == v.x && this.y == v.y && this.z == v.z) return true;
    	else return false;
    }
    
    public final float length() {
    	return (float) Math.sqrt(length2());
    }

    public final float length2() {
    	return (x * x) + (y * y);
    }

    public final void normalize() {
    	float len = length();
    	x /= len;
    	y /= len;
    	z /= len;
    }

    public void set(float x, float y, float z) {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }
    
    public void set(Vec3 v) {
    	x = v.x;
    	y = v.y;
    	z = v.z;
    }   

    public void print(String tag, String msg) {
            Log.i(tag, msg + ": " + x + ", " + y + ", " + z);
    }
}