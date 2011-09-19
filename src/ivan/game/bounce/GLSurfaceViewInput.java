package ivan.game.bounce;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;

/*
 * Sets up a dedicated surface for displaying OpenGL rendering.
 * Also implements 
 */
public class GLSurfaceViewInput extends GLSurfaceView implements SensorEventListener {
	private final short BALL_COUNT = 1;
	
	// OpenGL renderer with rendering loop
	public GLRenderer mGLRenderer;
	
	// Physics simulator which detects and responds to collisions
	public Physics mPhysics;
	
	// A line which is used to deflect balls
	public GLRectangle mBlockingRect;	
	
	// Screen information helper
	public Display mDisplay;
	
	// Device screen dimensions
	public short mViewWidth;
	public short mViewHeight;	
	
	// Sensor manager used to control the accelerometer sensor.
    public SensorManager mViewSensorManager;
    public Sensor mAccelerometer;

    // Accelerometer sensor values.
    public float mSensorX = 0.0f;
    public float mSensorY = 0.0f;
	
    public GLSurfaceViewInput(Context context, Display display) {
        super(context);
		
        // Store display screen width and height
		mDisplay = display;
		mViewWidth = (short)display.getWidth();
		mViewHeight = (short)display.getHeight();

		// Create OpenGL renderer
		mGLRenderer = new GLRenderer(context, this);

		// Create storage for balls
		GLSpriteBall[] ballSpriteArray = new GLSpriteBall[BALL_COUNT];
		
		// We have three different ball images.
		// Use different ones if there are enough balls.
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
			
			// Randomizes the bounciness of the balls, 0.6f - 0.8f
			final float r = ((float)Math.random() - 0.5f) * 0.2f;
            ball.RESTITUTION = 1.0f - 0.3f + r;

            // Store newly created ball
			ballSpriteArray[i] = ball;
		}
		
		// Create a line for blocking: (w x h)
		mBlockingRect = new GLRectangle(128.0f, 4.0f);
		
		// Set the renderer's ball array and blocking line
		mGLRenderer.mBallSprites = ballSpriteArray;
		mGLRenderer.mBlock = mBlockingRect;
		
		// Set this view to our renderer
		setRenderer(mGLRenderer);
		
		// Instantiate the physics simulator
		mPhysics = new Physics(this);
		
		// Set the physics' blocking line and ball array
		mPhysics.mRendLength = (short) ballSpriteArray.length;
		mPhysics.mRenderables = ballSpriteArray;
		mPhysics.mBlock = mBlockingRect;
		
		// Run the garbage collector
		Runtime r = Runtime.getRuntime();
		r.gc();
    }

    // Starts tilt sensor when app is in view
	@Override
	public void onResume() {
		mViewSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}

	// Stops tilt sensor when app is out of view
	@Override
	public void onPause() {
		mViewSensorManager.unregisterListener(this);
	}

	// Called when the accuracy of a sensor has changed
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

	// Called when sensor values have changed (the screen is tilted)
	public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        
        // Sets X and Y values from tilt values
        // We inverse the value according to the screen's orientation (CCW)
        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0: // No rotation
                mSensorX = event.values[0];
                mSensorY = event.values[1];
                break;
            case Surface.ROTATION_90: // Landscape
                mSensorX = -event.values[1];
                mSensorY = event.values[0];
                break;
            case Surface.ROTATION_180: // Upside down
                mSensorX = -event.values[0];
                mSensorY = -event.values[1];
                break;
            case Surface.ROTATION_270: // Landscape
                mSensorX = event.values[1];
                mSensorY = -event.values[0];
                break;
        }		
    }

	// Handles touch screen motion events
    public boolean onTouchEvent(final MotionEvent event) {
		float sX = event.getX();
        float sY = event.getY();
        float oglY = mViewHeight - sY;
        
        // A motion gesture. Between Up and Down states.
        // Moves blocking line in X Y coordinates of motion gesture.
		if(event.getAction() == MotionEvent.ACTION_MOVE) {	
            mBlockingRect.pos.x = sX;
            mBlockingRect.pos.y = oglY;
            
            mBlockingRect.line.p1.x = sX;
            mBlockingRect.line.p1.y = oglY;
            mBlockingRect.line.p2.x = sX + mBlockingRect.width;
            mBlockingRect.line.p2.y = oglY;
		}
    	
		// A tap gesture. The initial starting location.
		// Starts physics simulator in OpenGL renderer when tapped.
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
