package jp.tetra2000.walkingphone;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WalkDetectService extends Service implements SensorEventListener {
	private static final String TAG = "WalkDetectService";
	private static final int NANO_INVERSE = 10^9;
	
	private SensorManager mSensorManager;
	private Sensor mAccel;
	
	private float lastPeek=0, peek=0;
	// last peek time
	private long lastPeekTime, peekTime ;
	// true=PLUS, false=MINUS
	private boolean lastSign = false;
	

	@Override
	public void onCreate() {
		// add screen receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		this.registerReceiver(mScreenReceiver, filter);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId) {
		// setup sensor
		if(mSensorManager == null) {
	        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
	        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
	        if(mAccel == null) {
	        	stopSelf();
	        }
		}
		
		enableSensor();
		
		return START_STICKY;
	}
	
	private void enableSensor() {
		 mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	private void disableSensor() {
		mSensorManager.unregisterListener(this);
	}
	
	private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(Intent.ACTION_SCREEN_ON.equals(action)) {
				enableSensor();
			} else if(Intent.ACTION_SCREEN_OFF.equals(action)) {
				disableSensor();
			}
		}
		
	};

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float z = event.values[2];
		long t = event.timestamp;
		boolean sign = z>0;
		
		// first sensor value
		if(lastPeekTime == Long.MIN_VALUE) {
			lastPeekTime = t;
			lastSign = sign;
			return;
		}
		
		// sign of z has changed
		if(sign != lastSign) {
			
			// NOT first time
			if(lastPeek != 0) {
				double T = 4 * (t - lastPeekTime) / (double)NANO_INVERSE;
				double f = 1/T;
				
				onNewFrequency(f);
			}
			
			// update lastPeek, lastPeekTime and lastSign
			lastPeek = peek;
			lastPeekTime = peekTime;
			lastSign = sign;
		}
		
		// update peek
		if(sign ? z > peek : z < peek ) {
			peek = z;
			peekTime = t;
		}
	}
	
	private void onNewFrequency(double f) {
		Log.d(TAG, f+"hz");
	}
}
