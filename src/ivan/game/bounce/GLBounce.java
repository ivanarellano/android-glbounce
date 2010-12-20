package ivan.game.bounce;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Display;
import android.view.WindowManager;

/**
 * Initial entry point that creates an application life cycle
 */
public class GLBounce extends Activity {
	public GLSurfaceViewInput mGLSurfaceView;
	public SensorManager mSensorManager;
    public PowerManager mPowerManager;	
	public WindowManager mWindowManager;
	public Display mDisplay;
	public WakeLock mWakeLock;

	// Called when this application is starting
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Get an instance of SensorManager
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Get an instance of PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright WakeLock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                .getName());

		// Instantiate a SurfaceView and set it as this application's content
		mGLSurfaceView = new GLSurfaceViewInput(this, mDisplay);
		setContentView(mGLSurfaceView);

		// This view is touchable
		mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
		
        // Set sensor as accelerometer
		mGLSurfaceView.mViewSensorManager = mSensorManager;
		mGLSurfaceView.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);		
	}

	// Called when application is brought to background
    @Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
		mWakeLock.release();
	}
	
    // Called when application is visible to user
	@Override
	protected void onResume() {
		super.onResume();
		mGLSurfaceView.onResume();
		mWakeLock.acquire();
	}
}