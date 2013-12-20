package jp.tetra2000.walkingphone;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;



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
    protected void onResume() {
    	super.onResume();
    	
    	
		
		mCal.setTimeInMillis(System.currentTimeMillis());
		int year = mCal.get(Calendar.YEAR);
		int month = mCal.get(Calendar.MONTH);
		int day = mCal.get(Calendar.DAY_OF_MONTH);
		
		int len = mDb.get(year, month, day);
		
		int hour = len / (60 * 60);
		int min = (len % (60 * 60)) / 60 ;
		
		String text = getString(R.string.time_format, hour, min);
		mTimeView.setText(text);
    }
}
