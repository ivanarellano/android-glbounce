package ivan.game.bounce;

import android.util.Log;

public final class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3()
    {
            this.x = 0;
            this.y = 0;
            this.z = 0;
    }

    public Vec3(float x, float y, float z)
    {
            this.x = x;
            this.y = y;
            this.z = z;
    }

    /**
     * Calculates if this Vec2 is equal to the one provided
     * @param v is the Vec2 to compare against
     * @return True if it's equal, false if it isn't
     */
    public boolean equals(Vec3 v)
    {
            if(this.x == v.x && this.y == v.y && this.z == v.z) return true;
            else return false;
    }
    
    /**
     * Calculates the vector length
     * @return the length of the vector
     */
    public float length()
    {
            return (float)Math.sqrt((this.x*this.x + this.y*this.y + this.z*this.z));
    }
    
    /**
     * Normalizes the vector
     */
    public void normalize()
    {
            float len = length();
            this.x /= len;
            this.y /= len;
            this.z /= len;
    }
    
    /**
     * Returns a vector from this to the point provided
     * @param point to calculate the vector to
     * @return The vector.
     */
    public Vec3 getVectorTo(Vec3 point)
    {
            Vec3 aux = new Vec3();
            
            aux.x = point.x - this.x;
            aux.y = point.y - this.y;
            aux.z = point.z - this.z;
            
            return aux;
    }
    
    /**
     * Sets the x,y
     * @param x
     * @param y
     */
    public void set(float x, float y, float z)
    {
            this.x = x;
            this.y = y;
            this.z = z;
    }
    
    public void set(Vec3 v)
    {
            this.x = v.x;
            this.y = v.y;
            this.z = v.z;
    }   

    /**
     * Adds the offset to the current position
     * @param x to add to the x component
     * @param y to add to the y component
     */
    public void offset(float x, float y, float z)
    {
            this.x += x;
            this.y += y;
            this.z += z;
    }

    /**
     * Prints the vector value to the log
     */
    public void print(String tag, String msg)
    {
            Log.i(tag, msg + ": " + this.x + ", " + this.y + ", " + this.z);
    }
}