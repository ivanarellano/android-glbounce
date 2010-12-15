package ivan.game.bounce;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Display;
import android.view.WindowManager;

public class GLBounce extends Activity {
	public GLSurfaceViewInput mGLSurfaceView;
	public SensorManager mSensorManager;
    public PowerManager mPowerManager;	
	public WindowManager mWindowManager;
	public Display mDisplay;
	public WakeLock mWakeLock;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		// Get an instance of the SensorManager
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                .getName());

		// Instantiate the view and set it as the Activity's content
		mGLSurfaceView = new GLSurfaceViewInput(this, mDisplay);
		setContentView(mGLSurfaceView);

		mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
				
		mGLSurfaceView.mViewSensorManager = mSensorManager;
		mGLSurfaceView.mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);		
	}

    @Override
	protected void onPause() {
		super.onPause();
		mGLSurfaceView.onPause();
		mWakeLock.release();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
		mGLSurfaceView.onResume();
	}
}