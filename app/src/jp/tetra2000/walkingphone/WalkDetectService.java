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
import android.widget.Toast;

public class WalkDetectService extends Service implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mAccel;
	
	private long t0=Long.MIN_VALUE, t1=Long.MIN_VALUE;
	private float z0 = Float.MIN_VALUE, z1 = Float.MIN_VALUE;
	
	private long lastPeek = Long.MIN_VALUE;

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
	        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
		long t2 = event.timestamp;
		float z2 = event.values[2];
		
		// NOT first or second time
		if(t0!=Long.MIN_VALUE && t1!=Long.MIN_VALUE) {
			
			// Not peek
			if((z0-z1) * (z1-z2) > 0)
				return;
			
			if(lastPeek != Long.MIN_VALUE) {
				
				
			} else {
				// first peek
				
				// only update peek
				lastPeek = t1;
			}
			
			
		}
		
		// update t0, t1, z0, z1
		t0 = t1;
		t1 = t2;
		z0 = z1;
		z1 = z2;
	}
}
