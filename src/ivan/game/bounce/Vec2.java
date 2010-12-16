package ivan.game.bounce;

import android.util.Log;

public class Vec2 {
    public float x;
    public float y;

    public Vec2()
    {
    	this.x = 0;
    	this.y = 0;
    }

    public Vec2(float x, float y)
    {
    	this.x = x;
    	this.y = y;
    }

    /**
     * Calculates if this Vec2 is equal to the one provided
     * @param v is the Vec2 to compare against
     * @return True if it's equal, false if it isn't
     */
    public boolean equals(Vec2 v)
    {
    	if(this.x == v.x && this.y == v.y) return true;
    	else return false;
    }
    
    /**
     * Calculates the vector length
     * @return the length of the vector
     */
    public float length()
    {
    	return (float)Math.sqrt((this.x*this.x + this.y*this.y));
    }
    
    /**
     * Normalizes the vector
     */
    public void normalize()
    {
    	float len = length();
    	this.x /= len;
    	this.y /= len;
    }
    
    /**
     * Returns a vector from this to the point provided
     * @param point to calculate the vector to
     * @return The vector.
     */
    public Vec2 getVectorTo(Vec2 point)
    {
    	Vec2 aux = new Vec2();
            
    	aux.x = point.x - this.x;
    	aux.y = point.y - this.y;
            
    	return aux;
    }
    
    public Vec2 getVectorTo(int x, int y)
    {
    	Vec2 aux = new Vec2();
            
    	aux.x = x - this.x;
    	aux.y = y - this.y;
            
    	return aux;
    }
    
    /**
     * Sets the x,y
     * @param x
     * @param y
     */
    public void set(float x, float y)
    {
    	this.x = x;
    	this.y = y;
    }
    
    public void set(Vec2 v)
    {
    	this.x = v.x;
    	this.y = v.y;
    }    

    /**
     * Adds the offset to the current position
     * @param x to add to the x component
     * @param y to add to the y component
     */
    public void offset(float x, float y)
    {
    	this.x += x;
    	this.y += y;
    }

    /**
     * Prints the vector value to the log
     */
    public void print(String tag, String msg)
    {
    	Log.i(tag, msg + ": " + this.x + ", " + this.y);
    }
    
    /**
     * Calculates the dot product of this Vec2 with another
     * @param vec Vec2 to do the product with
     * @return the dot product
     */
    public float dot(Vec2 vec)
    {
    	return (float) (this.x * vec.x + this.y * vec.y);
    }
    
    /**
     * Adds to this Vec2 the values of another
     * @param vec Vec2 to add
     */
    public void add(Vec2 vec)
    {
    	this.x += vec.x;
    	this.y += vec.y;
    }
    
    /**
     * Multiplies the x and y components by the value
     * @param val Multiplier for the components
     */
    public void scale(float val)
    {
    	this.x *= val;
    	this.y *= val;
    }
    
    /**
     * Gets a Vec2 with the truncated values of the float coordinates
     * @return A Vec2 with no decimals.
     */
    public Vec2 getIntValue()
    {
    	Vec2 intVec = new Vec2();
    	intVec.set((int)this.x, (int)this.y);
    	return intVec;
    }
    
    /**
     * Checks if the rounded coordinates of both vectors are equal
     * @param vec Vec2 to check against
     * @return True if they are equal, false if they are not.
     */
    public boolean roundEqual(Vec2 vec)
    {
    	return ( (Math.round(this.x) == Math.round(vec.x)) && (Math.round(this.y) == Math.round(vec.y)));
    }

}
