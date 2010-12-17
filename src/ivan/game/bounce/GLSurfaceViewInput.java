package ivan.game.bounce;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;

public class GLSurfaceViewInput extends GLSurfaceView implements SensorEventListener {
	private final short BALL_COUNT = 2;
	
	public GLRenderer mGLRenderer;
	public VerletPhysics mPhysics;
	public Display mDisplay;
	
	public GLRectangle mBlockingRect;
	
	// Sensor manager used to control the accelerometer sensor.
    public SensorManager mViewSensorManager;
    public Sensor mAccelerometer;

    // Accelerometer sensor values.
    public float mSensorX = 0;
    public float mSensorY = 0;

	// Device screen dimensions
	public short mViewWidth;
	public short mViewHeight;
	
    public GLSurfaceViewInput(Context context, Display display) {
        super(context);
		
		mDisplay = display;
		mViewWidth = (short)display.getWidth();
		mViewHeight = (short)display.getHeight();

		mGLRenderer = new GLRenderer(context, this);

		GLSpriteBall[] ballSpriteArray = new GLSpriteBall[BALL_COUNT];
		
		final short ballBucketSize = BALL_COUNT / 4;
		for(short i = 0; i < BALL_COUNT; i++) {
			GLSpriteBall ball;
			
			if(i < ballBucketSize) {
				ball = new GLSpriteBall(R.drawable.ball_blue_32);
				ball.MASS = 400;
			} else if(i < ballBucketSize * 2) {
				ball = new GLSpriteBall(R.drawable.ball_green_32);
			} else {
				ball = new GLSpriteBall(R.drawable.ball_dragon_32);
			}
			
			final float r = ((float)Math.random() - 0.5f) * 0.2f;
            ball.RESTITUTION = 1.0f - 0.3f + r;

			ballSpriteArray[i] = ball;
		}
		
		mBlockingRect = new GLRectangle(64.0f, 8.0f);
		
		mGLRenderer.mBallSprites = ballSpriteArray;
		mGLRenderer.mBlock = mBlockingRect;
		setRenderer(mGLRenderer);
		
		mPhysics = new VerletPhysics(this);
		mPhysics.mRenderables = ballSpriteArray;
		mPhysics.mBlock = mBlockingRect;
		
		Runtime r = Runtime.getRuntime();
		r.gc();
    }

	@Override
	public void onResume() {
		mViewSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	public void onPause() {
		mViewSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

	public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                break;
            case Surface.ROTATION_90:
                mSensorX = -event.values[1];
                mSensorY = event.values[0];
                break;
            case Surface.ROTATION_180:
                mSensorX = -event.values[0];
                mSensorY = -event.values[1];
                break;
            case Surface.ROTATION_270:
                mSensorX = event.values[1];
                mSensorY = -event.values[0];
                break;
        }		
    }

    public boolean onTouchEvent(final MotionEvent event) {
		float sX = event.getX();
        float sY = event.getY();
        float oglY = mViewHeight - sY;
        
		if(event.getAction() == MotionEvent.ACTION_MOVE) {	
            mBlockingRect.pos.x = sX;
            mBlockingRect.pos.y = oglY;
            mBlockingRect.line.p1.set(sX, oglY);
            mBlockingRect.line.p2.set(sX + mBlockingRect.width, oglY);
		}
    	
		if(event.getAction() == MotionEvent.ACTION_DOWN) {	
        	queueEvent(new Runnable() {
            	public void run() {
					if(!mGLRenderer.mAnimate)
						mGLRenderer.mAnimate = true;
            	}});
		}
		
		return true;
	}
}
