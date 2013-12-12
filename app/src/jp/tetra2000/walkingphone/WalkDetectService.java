package jp.tetra2000.walkingphone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

public class WalkDetectService extends Service {
	private SensorManager mSensorManager;
	private Sensor mAccel;

	@Override
	public void onCreate() {
		
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
		
		
		
		return START_STICKY;
	}
}
