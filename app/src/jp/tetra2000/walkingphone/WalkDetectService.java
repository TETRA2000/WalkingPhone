package jp.tetra2000.walkingphone;

import java.util.Calendar;
import java.util.Locale;

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
	
	// milli seconds
	private static final int MIN_WALK_TIME = 10 * 1000;
	// milli seconds
	private static final int MAX_MOVE_INTERVAL = 1500;
	
	private SensorManager mSensorManager;
	private Sensor mGyro;
	
	private long walkStartTime;
	private long lastMoveTime;
	
	private DB mDb;
	private Calendar mCal;

	@Override
	public void onCreate() {
		// add screen receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		this.registerReceiver(mScreenReceiver, filter);
		
		// open db
		mDb = new DB(this);
		
		// XXX not support dynamic location change
		// TODO setup BroadcastReceiver
		mCal = Calendar.getInstance(Locale.getDefault());
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
	
	@Override
	public void onDestroy() {
		this.unregisterReceiver(mScreenReceiver);
		mSensorManager = null;
	}
	
	private void enableSensor() {
		// init
		walkStartTime = 0;
		lastMoveTime = 0;
		
		mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
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
		
		if(Math.abs(pitch) > 0.4) {
			long t = event.timestamp / 1000000; // nano to milli
			Log.d(TAG, "moving!! pitch=" + pitch);
			
			if(t - lastMoveTime < MAX_MOVE_INTERVAL) {
				Log.d(TAG, "walking...");
				
				if(t - walkStartTime > MIN_WALK_TIME) {
					
					Log.d(TAG, "You walked 10sec");
					Toast.makeText(this, "walk", Toast.LENGTH_LONG).show();
					
					// update calendar
					mCal.setTimeInMillis(t);
					
					int year = mCal.get(Calendar.YEAR);
					int month = mCal.get(Calendar.MONTH);
					int day = mCal.get(Calendar.DAY_OF_MONTH);
					
					mDb.add(year, month, day, MIN_WALK_TIME/1000);
					
					// reset walkStartTime
					walkStartTime = t;
				} 
				
			} else {
					Log.d(TAG, "resume walking");
					
					// resume walking
					walkStartTime = t;
			}
					
				lastMoveTime = t;
		}
	}
}
