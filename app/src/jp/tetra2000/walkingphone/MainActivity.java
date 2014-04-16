package jp.tetra2000.walkingphone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

import java.util.Calendar;
import java.util.Locale;



public class MainActivity extends Activity
{
	private TextView mTimeView;
	private Calendar mCal;
	
	private DB mDb;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mTimeView = (TextView) findViewById(R.id.time);
        
    	mCal = Calendar.getInstance(Locale.getDefault());
    	
    	mDb = new DB(this);
        
		startService(new Intent(this, WalkDetectService.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this); // Add this method.
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	
		
		mCal.setTimeInMillis(System.currentTimeMillis());
		int year = mCal.get(Calendar.YEAR);
		int month = mCal.get(Calendar.MONTH);
		int day = mCal.get(Calendar.DAY_OF_MONTH);
		
		int len = mDb.get(year, month, day);
		
		int hour = len / (60 * 60);
		int min = (len % (60 * 60)) / 60 ;
		int sec = len % 60;
		
		String text = getString(R.string.time_format, hour, min, sec);
		mTimeView.setText(text);
    }

    @Override
    public void onStop() {
        super.onStop();

        EasyTracker.getInstance().activityStop(this); // Add this method.
    }
}
