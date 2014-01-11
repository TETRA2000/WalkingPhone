package jp.tetra2000.walkingphone;
import java.util.*;
import java.nio.*;

public class FCounter
{
	private long mDelta = 1000000000;
	
	private ArrayList<Long> mTimes = new ArrayList<Long>();
	
	private float mLastValue;
	
	public FCounter() {
		
	}
	
	public FCounter(long nanodelta) {
		mDelta = nanodelta;
	}
	
	public void addData(float value, long nanosec) {
		if(mLastValue>0 != value>0) {
			// sign changed
			mTimes.add(nanosec);
		}
		
		mLastValue = value;
		
		updateList(nanosec);
	}
	
	public void reset() {
		mTimes.clear();
		mLastValue = 0;
	}
	
	public float getFrequency() {
		return (float)mTimes.size() / mDelta;
	}
	
	private void updateList(long nowtime) {
		while(nowtime - mTimes.get(0) < mDelta)
			mTimes.remove(0);
	}
}
