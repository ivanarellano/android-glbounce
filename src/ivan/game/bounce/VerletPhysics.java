package ivan.game.bounce;

import android.os.SystemClock;
import android.util.Log;

/* 
 * Acceleration = velocity / time
 *	 or (1 / mass) * F
 * Force = mass * acceleration
 * Momentum = mass * velocity
*/

public class VerletPhysics implements Runnable{
	public GLSurfaceViewInput mSV;
	public GLSpriteBall[] mRenderables;
	public GLRectangle mBlock;
	
	public long mLastTime;
	public float mLastTimeDeltaSec;
	
	public Vec2 mTmp, LineVec, VecToLine;
	
	public VerletPhysics(GLSurfaceViewInput surfaceView) {
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
					for(short i = 0; i < mRenderables.length; i++) {
						GLSpriteBall obj1 = mRenderables[i];

						// Force of gravity applied to obj
						final float gravityX = -mSV.mSensorX * obj1.MASS;
						final float gravityY = -mSV.mSensorY * obj1.MASS;
						
						//computeVerletMethod(obj1, gravityX, gravityY, timeDeltaSeconds, mLastTimeDeltaSec);
						computeEulerMethod(obj1, gravityX, gravityY, timeDeltaSeconds);
						
						obj1.mCenter.set(obj1.mRadius + obj1.pos.x, obj1.mRadius + obj1.pos.y);
						
						for(short j = (short)(i + 1); j < mRenderables.length; j++) {
							GLSpriteBall obj2 = mRenderables[j];
							
							resolveBallToBallCollision(obj1, obj2);
						}

						resolveBallToLineCollision(obj1);
						
						resolveScreenCollision2(obj1);
					}
				}
				
				mLastTimeDeltaSec = timeDeltaSeconds;
			}
			
			mLastTime = time;
		}
	}
	
	/*
	 * More accurate Euler Integration (2nd order)
	 * x = x + v * dt + 0.5f * a * dt * dt
	 */
	public void computeEulerMethod(Renderable obj, float gravityX, float gravityY, float dt) {
		obj.pos.x += obj.accel.x * dt + 0.5f * gravityX * dt * dt;
		obj.pos.y += obj.accel.y * dt + 0.5f * gravityY * dt * dt;
		
		obj.accel.x += gravityX * dt;
		obj.accel.y += gravityY * dt;
	}
	
	/*
	 * Time-Corrected Verlet Integration
	 * xi+1 = xi + (xi - xi-1) * (dti / dti-1) + a * dti * dti
	 */	
	public void computeVerletMethod(Renderable obj, float gravityX, float gravityY, float dt, float lDT) {
		mTmp.set(obj.pos);
		
		obj.accel.x = obj.pos.x - obj.oldPos.x;
		obj.accel.y = obj.pos.y - obj.oldPos.y;

		obj.pos.x += obj.RESTITUTION * obj.accel.x * (dt / lDT) + gravityX * (dt * dt);
		obj.pos.y += obj.RESTITUTION * obj.accel.y * (dt / lDT) + gravityY * (dt * dt);
		
		obj.oldPos.set(mTmp);
	}	
	
	/*
	 * Checks collision of the obj with the screen size.
	 * Does not bounce when it hits the boundaries.
	 */
    public void resolveScreenCollision(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);
        final float x = obj.pos.x;
        final float y = obj.pos.y;

        if (x > xmax) {
            obj.pos.x = xmax;
            obj.accel.x = 0.0f;
		} else if (x < 0.0f) {
			obj.pos.x = 0.0f;
			obj.accel.x = 0.0f;
		}

        if (y > ymax) {
            obj.pos.y = ymax;
            obj.accel.y = 0.0f;
		} else if (y < 0.0f) {
			obj.pos.y = 0.0f;	
			obj.accel.y = 0.0f;
		}
    }

	/*
	 * Checks collision of the obj with the screen size.
	 * Reverses acceleration to pull obj back into screen.
	 */
    public void resolveScreenCollision2(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);

        // Bounce.
        if ((obj.pos.x < 0.0f && obj.accel.x < 0.0f) || (obj.pos.x > xmax && obj.accel.x > 0.0f)) {
        	
            obj.accel.x = -obj.accel.x * obj.RESTITUTION;
            obj.pos.x = Math.max(0.0f, Math.min(obj.pos.x, xmax));
            
            if (Math.abs(obj.accel.x) < 5.0f) {
                obj.accel.x = 0.0f;
            }
        }
        
        if ((obj.pos.y < 0.0f && obj.accel.y < 0.0f) || (obj.pos.y > ymax && obj.accel.y > 0.0f)) {
            
        	obj.accel.y = -obj.accel.y * obj.RESTITUTION;
            obj.pos.y = Math.max(0.0f, Math.min(obj.pos.y, ymax));
            
            if (Math.abs(obj.accel.y) < 5.0f) {
                obj.accel.y = 0.0f;
            }
        }
    }   
    
	public void resolveBallToBallCollision(GLSpriteBall ball1, GLSpriteBall ball2) {
		final float dx = ball2.pos.x - ball1.pos.x;
		final float dy = ball2.pos.y - ball1.pos.y;

	    float sumRadius = ball1.mRadius + ball2.mRadius;
	    float sqrRadius = sumRadius * sumRadius;

	    float distSqr = dx * dx + dy * dy;
	    
	    if (distSqr <= sqrRadius) {
	        // Balls collided
	    	
	        float d = (float)Math.sqrt(distSqr);
	        float c = ((ball1.mRadius + ball2.mRadius) - d) / d;
	        
	        ball1.pos.x -= dx * c;
	        ball1.pos.y -= dy * c;
	        ball2.pos.x += dx * c;
	        ball2.pos.y += dy * c;
	    }
	}	
    
	public void resolveBallToLineCollision(GLSpriteBall obj) {
		LineVec.set(mBlock.line.p2.x - mBlock.line.p1.x, mBlock.line.p2.y - mBlock.line.p1.y);
		VecToLine.set(mBlock.line.p1.x - obj.mCenter.x, mBlock.line.p1.y - obj.mCenter.y);

    	// create a quadratic formula of the form ax^2 + bx + c = 0
    	float a, b, c;
    	float sqrtterm, res1; // res2

    	a = LineVec.x*LineVec.x + LineVec.y*LineVec.y;
    	b = 2 * ( VecToLine.x*LineVec.x + VecToLine.y*LineVec.y );
    	c = ( VecToLine.x*VecToLine.x + VecToLine.y*VecToLine.y) - obj.mRadius*obj.mRadius;

    	sqrtterm = b*b - 4*a*c;

    	if(sqrtterm < 0) {
    		// No collision
    	} else {
	    	sqrtterm = (float) Math.sqrt(sqrtterm);
	    	res1 = ( -b - sqrtterm ) / (2 * a);
	    	//res2 = ( -b + sqrtterm ) / (2 * a);
	
	    	if(res1 >= 0 && res1 <= 1) {
	    		// A collision happened
	    		obj.accel.y = -obj.accel.y * obj.RESTITUTION;
	    	} else {
	    		// "Out of range" of ray
	    	}
    	}
    }
}