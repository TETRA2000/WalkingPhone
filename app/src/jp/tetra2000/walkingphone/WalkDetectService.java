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
	
	private SensorManager mSensorManager;
	private Sensor mGyro;

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
	        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	        if(mGyro == null) {
	        	stopSelf();
	        }
		}
		
		enableSensor();
		
		return START_STICKY;
	}
	
	private void enableSensor() {
		 mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_UI);
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
		float pitch = event.values[1];
		
		if(Math.abs(pitch) > 0.5) {
			Log.d(TAG, "moving!! pitch=" + pitch);
		}
	}
}
