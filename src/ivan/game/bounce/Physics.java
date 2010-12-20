package ivan.game.bounce;

import android.os.SystemClock;

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

						// Force of gravity applied to object (F = Accel * Mass)
						// Acceleration is gauged by screen's tilt angle
						final float gravityX = -mSV.mSensorX * b1.MASS;
						final float gravityY = -mSV.mSensorY * b1.MASS;
						
						// Euler integrator
						computeEulerMethod(b1, gravityX, gravityY, timeDeltaSeconds);
						
						// Update the location of the center of the ball
						b1.mCenter.x = b1.mRadius + b1.pos.x;
						b1.mCenter.y = b1.mRadius + b1.pos.y;
						
						// Check collision with every other ball to current ball
						for(short j = (short)(i + 1); j < mRendLength; j++) {
							GLSpriteBall b2 = mRenderables[j];
							
							resolveBallToBallCollision(b1, b2);
						}

						// Check collision with current ball to a line
						resolveBallToLineCollision(b1);
						
						// Check collision with current ball and the screen
						resolveScreenCollision2(b1);
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
		
		// Create momentum
		// M = Mass * Velocity
		obj.accel.x += gravityX * dt;
		obj.accel.y += gravityY * dt;
	}
	
	/*
	 * Time-Corrected Verlet Integration
	 * xi+1 = xi + (xi - xi-1) * (dti / dti-1) + a * dti * dti
	 */	
	public void computeVerletMethod(Renderable obj, float gravityX, float gravityY, float dt, float lDT) {
		mTmp.x = obj.pos.x;
		mTmp.y = obj.pos.y;
		
		obj.accel.x = obj.pos.x - obj.oldPos.x;
		obj.accel.y = obj.pos.y - obj.oldPos.y;

		obj.pos.x += obj.RESTITUTION * obj.accel.x * (dt / lDT) + gravityX * (dt * dt);
		obj.pos.y += obj.RESTITUTION * obj.accel.y * (dt / lDT) + gravityY * (dt * dt);
		
		obj.oldPos.x = mTmp.x;
		obj.oldPos.y = mTmp.y;
	}	
	
	/*
	 * Detects collision between the ball and the screen.
	 * Responds by stopping the ball's acceleration.
	 */
    public void resolveScreenCollision(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);
        final float x = obj.pos.x;
        final float y = obj.pos.y;

        // Detect if ball is beyond left or right sides of screen
        // and stop the ball's acceleration
        if (x > xmax) {
            obj.pos.x = xmax;
            obj.accel.x = 0.0f;
		} else if (x < 0.0f) {
			obj.pos.x = 0.0f;
			obj.accel.x = 0.0f;
		}

        // Detect if ball is beyond top or bottom sides of screen
        // and stop the ball's acceleration        
        if (y > ymax) {
            obj.pos.y = ymax;
            obj.accel.y = 0.0f;
		} else if (y < 0.0f) {
			obj.pos.y = 0.0f;	
			obj.accel.y = 0.0f;
		}
    }

	/*
	 * Detects collision between the ball and the screen.
	 * Responds with reversing acceleration of the ball.
	 */
    public void resolveScreenCollision2(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);

        // Bounce horizontally against left or right of screen
        if ((obj.pos.x < 0.0f && obj.accel.x < 0.0f) || (obj.pos.x > xmax && obj.accel.x > 0.0f)) {
        	
        	// Reverse X acceleration and slow it down
            obj.accel.x = -obj.accel.x * obj.RESTITUTION;
            
            // Determines where to stop the ball at, 0 or xmax
            obj.pos.x = Math.max(0.0f, Math.min(obj.pos.x, xmax));
            
            // Stop the ball from bouncing if it's too slow
            if (Math.abs(obj.accel.x) < 5.0f) {
                obj.accel.x = 0.0f;
            }
        }
        
        // Bounce vertically against top or bottom of screen
        if ((obj.pos.y < 0.0f && obj.accel.y < 0.0f) || (obj.pos.y > ymax && obj.accel.y > 0.0f)) {
            
        	// Reverse Y acceleration and slow it down
        	obj.accel.y = -obj.accel.y * obj.RESTITUTION;
        	
        	// Determines where to stop the ball at, 0 or ymax
            obj.pos.y = Math.max(0.0f, Math.min(obj.pos.y, ymax));
            
            // Stop the ball from bouncing if it's too slow
            if (Math.abs(obj.accel.y) < 5.0f) {
                obj.accel.y = 0.0f;
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
	 * Responds by accelerating the ball in the opposite direction.
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
	    		
	    		// Reverse Y acceleration and slow it down
	    		obj.accel.y = -obj.accel.y * obj.RESTITUTION;
	    	} else {
	    		// "Out of range" of ray
	    	}
    	}
    }
}