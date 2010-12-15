package ivan.game.bounce;

import android.os.SystemClock;
//import android.util.Log;

/* 
 * Acceleration = velocity / time
 *	 or (1 / mass) * F
 * Force = mass * acceleration
 * Momentum = mass * velocity
*/

public class VerletPhysics implements Runnable{
	public GLSurfaceViewInput mSV;
	public Renderable[] mRenderables, mBlocking;
	public long mLastTime;
	public float mLastTimeDeltaSec;
	public Vector3 mTmp;
	
	public VerletPhysics(GLSurfaceViewInput surfaceView) {
		mSV = surfaceView;
		mTmp = new Vector3();
	}
	
	public void run() {
		if(mRenderables != null) {
			final long time = SystemClock.uptimeMillis();
            final long timeDelta = time - mLastTime;

			if(mLastTime != 0) {
			    final float timeDeltaSeconds = timeDelta / 1000.0f;	
				
				if(mLastTimeDeltaSec != 0) {					
					for(short i = 0; i < mRenderables.length; i++) {
						Renderable obj1 = mRenderables[i];

						// Force of gravity applied to obj
						final float gravityX = -mSV.mSensorX * obj1.MASS;
						final float gravityY = -mSV.mSensorY * obj1.MASS;
						
						computeVerletMethod(obj1, gravityX, gravityY, timeDeltaSeconds, mLastTimeDeltaSec);

						for(short j = (short)(i + 1); j < mRenderables.length; j++) {
							Renderable obj2 = mRenderables[j];
							
							final float dx = obj2.pos.x - obj1.pos.x;
							final float dy = obj2.pos.y - obj1.pos.y;
							final float dd = dx * dx + dy * dy;

							if (dd <= (obj1.width * obj2.width)) {
								final float d = (float)Math.sqrt(dd);
						        final float c = (0.5f * (obj1.width - d)) / d;
								obj1.pos.x -= dx * c;	
								obj1.pos.y -= dy * c;
						        obj2.pos.x += dx * c;
						        obj2.pos.y += dy * c;
							}
							
						}

						resolveScreenCollision2(obj1);
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
	public void computeVerletMethod(Renderable obj, float gravityX, float gravityY, float tDS, float tLDS) {
		mTmp.set(obj.pos);
		
		obj.accel.x = obj.pos.x - obj.oldPos.x;
		obj.accel.y = obj.pos.y - obj.oldPos.y;
		
		obj.pos.x += obj.COEFFICIENT_OF_RESTITUTION * obj.accel.x * (tDS / tLDS) + gravityX * (tDS * tDS);
		obj.pos.y += obj.COEFFICIENT_OF_RESTITUTION * obj.accel.y * (tDS / tLDS) + gravityY * (tDS * tDS);
		
		obj.oldPos.set(mTmp);
	}	
	
	/*
	 * Checks collision of the obj with the screen size.
	 */
    public void resolveScreenCollision(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);
        final float x = obj.pos.x;
        final float y = obj.pos.y;

        if (x > xmax) {
            obj.pos.x = xmax;
		} else if (x < 0.0f) {
			obj.pos.x = 0.0f;
		}

        if (y > ymax) {
            obj.pos.y = ymax;
		} else if (y < 0.0f) {
			obj.pos.y = 0.0f;		
		}
    }

	/*
	 * Checks collision of the obj with the screen size.
	 * Creates spring energy to push it back in the screen.
	 */
    public void resolveScreenCollision2(Renderable obj) {
        final short xmax = (short) (mSV.mViewWidth - obj.width);
        final short ymax = (short) (mSV.mViewHeight - obj.height);
        final float x = obj.pos.x;
        final float y = obj.pos.y;

		// Kinetic energy: 1/2(m)(v2)
		final float collision = 0.5f * obj.width;
		
        if (x > xmax) {
            obj.pos.x = xmax;
			
			if(obj.accel.x >= 0.1f)
				obj.pos.x += collision * (obj.accel.x / 25.0f);
		} else if (x < 0.0f) {
			obj.pos.x = 0.0f;
			
			if(obj.accel.x < 0.0f)
				obj.pos.x += collision * (obj.accel.x / 25.0f);
		}

        if (y > ymax) {
            obj.pos.y = ymax;

			if(obj.accel.y >= 0.1f)
				obj.pos.y += collision * (obj.accel.y / 25.0f);
		} else if (y < 0.0f) {
			obj.pos.y = 0.0f;
			
			if(obj.accel.y < 0.0f)
				obj.pos.y += collision * (obj.accel.y / 25.0f);			
		}
    }	
}