package ivan.game.bounce;

import android.os.SystemClock;
import android.util.Log;

/**
 * Physics engine that detects and responds to collisions between ball to screen,
 * ball to ball, and ball to line. This Runnable class is used with queueEvent().
 */
public class Physics implements Runnable {
	// Reference of SurfaceView
	public GLSurfaceViewInput mSV;
	
	// Our sprites to render in an array
	public GLSpriteBall[] mRenderables;
	public short mRendLength;
	
	// The blocking line which bounces balls
	public GLRectangle mBlock;
	
	// Store the last time and time steps
	public long mLastTime;
	public float mLastTimeDeltaSec;
	
	// Temp vectors for verlet and ball/line detection
	public Vec2 mTmp, LineVec, VecToLine;
	
	public Physics(GLSurfaceViewInput surfaceView) {
		mSV = surfaceView;
		
		mTmp = new Vec2();
		LineVec = new Vec2();
		VecToLine = new Vec2();
	}
	
	public void run() {
		if(mRenderables != null) {
			final long time = SystemClock.uptimeMillis();
            final long timeDelta = time - mLastTime;

			if(mLastTime != 0) {
			    final float timeDeltaSeconds = timeDelta / 1000.0f;	
				
				if(mLastTimeDeltaSec != 0) {					
					for(short i = 0; i < mRendLength; i++) {
						GLSpriteBall b1 = mRenderables[i];

						// Acceleration is gauged by screen's tilt angle
						final float gravityX = -mSV.mSensorX * b1.MASS;
						final float gravityY = -mSV.mSensorY * b1.MASS;

						computeVerletMethod(b1, gravityX, gravityY, timeDeltaSeconds, mLastTimeDeltaSec);
					}
				}
				
				mLastTimeDeltaSec = timeDeltaSeconds;
			}
			
			mLastTime = time;
		}
	}

	/*
	 * Time-Corrected Verlet Integration
	 * xi+1 = xi + (xi - xi-1) * (dti / dti-1) + a * dti * dti
	 */	
	public void computeVerletMethod(Renderable obj, float gravityX, float gravityY, float dt, float lDT) {
		mTmp.x = obj.pos.x;
		mTmp.y = obj.pos.y;
		
		obj.vel.x = obj.pos.x - obj.oldPos.x;
		obj.vel.y = obj.pos.y - obj.oldPos.y;
		Log.d("1.", "vel.y: " + obj.vel.y); 
		
		resolveScreenCollision(obj);
		
		obj.pos.x += obj.FRICTION * (dt / lDT) * obj.vel.x + gravityX * (dt * dt);
		obj.pos.y += obj.FRICTION * (dt / lDT) * obj.vel.y + gravityY * (dt * dt);
		
		obj.oldPos.x = mTmp.x;
		obj.oldPos.y = mTmp.y;
		
		Log.d("2.", "p.y: " + obj.pos.y + " /op.y: " + obj.oldPos.y + " /dt: " + dt + " /ldt: " + lDT);
	}	
	
    public void resolveScreenCollision(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);
        final float x = obj.pos.x;
        final float y = obj.pos.y;

        if (x > xmax) {

		} else if (x < 0.0f) {

		}
      
        if (y > ymax) {
        	// ...
		} else if (y < 0.5f) {
			if(Math.abs(obj.vel.y) > 2.5f) {
				float imp = (obj.MASS * (obj.vel.y * obj.vel.y) / 2) * obj.RESTITUTION / obj.MASS;
				obj.vel.y += imp;
				Log.d("bounce", "imp: " + imp);
			} else {
				obj.vel.y = obj.pos.y = obj.oldPos.y = mTmp.y = 0.0f;
			}
		}
    }
    
    /*
     * Detects collision between two balls.
     * Responds by keeping them separated at the minimum distance.
     */
	public void resolveBallToBallCollision(GLSpriteBall b1, GLSpriteBall b2) {
		// Distance between balls in X
		final float dx = b2.pos.x - b1.pos.x;
		
		// Distance between balls in Y
		final float dy = b2.pos.y - b1.pos.y;

	    float sumRadius = b1.mRadius + b2.mRadius;
	    float sqrRadius = sumRadius * sumRadius;

	    float distSqr = dx * dx + dy * dy;
	    
	    if (distSqr <= sqrRadius) {
	        /* Balls collided */
	    	
	    	// Distance between objects
	        float d = (float)Math.sqrt(distSqr);
	        
	        // Minimum distance needed to keep objects separated
	        float c = ((b1.mRadius + b2.mRadius) - d) / d;
	        
	        // Separate objects
	        b1.pos.x -= dx * c;
	        b1.pos.y -= dy * c;
	        b2.pos.x += dx * c;
	        b2.pos.y += dy * c;
	    }
	}	
    
	/*
	 * Detects collision between a ball and line.
	 * Responds by velerating the ball in the opposite direction.
	 */
	public void resolveBallToLineCollision(GLSpriteBall obj) {
		LineVec.x = mBlock.line.p2.x - mBlock.line.p1.x;
		LineVec.y = mBlock.line.p2.y - mBlock.line.p1.y;
		
		VecToLine.x = mBlock.line.p1.x - obj.mCenter.x;
		VecToLine.y = mBlock.line.p1.y - obj.mCenter.y;

    	// create a quadratic formula of the form ax^2 + bx + c = 0
    	float a, b, c;
    	float sqrtterm, res1;

    	a = LineVec.x*LineVec.x + LineVec.y*LineVec.y;
    	b = 2 * ( VecToLine.x*LineVec.x + VecToLine.y*LineVec.y );
    	c = ( VecToLine.x*VecToLine.x + VecToLine.y*VecToLine.y) - obj.mRadius*obj.mRadius;

    	sqrtterm = b*b - 4*a*c;

    	if(sqrtterm < 0) {
    		// No collision
    	} else {
	    	sqrtterm = (float) Math.sqrt(sqrtterm);
	    	
	    	// Time until collision
	    	res1 = ( -b - sqrtterm ) / (2 * a);
	
	    	if(res1 >= 0 && res1 <= 1) {
	    		// A collision happened
	    		
	    		// Reverse Y veleration and slow it down
	    		obj.vel.y = -obj.vel.y * obj.RESTITUTION;
	    	} else {
	    		// "Out of range" of ray
	    	}
    	}
    }
}