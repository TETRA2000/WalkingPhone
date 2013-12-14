package jp.tetra2000.walkingphone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	public static final String NAME = "walk_db";
	
	private SQLiteDatabase mDb;
	
	public DB(Context context) {
		mDb = new DBHelper(context).getWritableDatabase();
	}
	
	public void add(int year, int month, int day, int length) {
		int oldLen = get(year, month, day);
		
		String selection = "year="+year+" and month="+month+" and day="+day;		
		
		ContentValues cv = new ContentValues();
		cv.put("length", oldLen + length);
		
		if(oldLen == 0) {
			cv.put("year", year);
			cv.put("month", month);
			cv.put("day", day);
			mDb.insert("walk", null, cv);
		} else {
			mDb.update("walk", cv, selection, null);
		}
	}
	
	public int get(int year, int month, int day) {
		
		String columns[] = {"length"};
		String selection = "year="+year+" and month="+month+" and day="+day;
		Cursor cursor =
				mDb.query("walk", columns, selection, null, null, null, null);
		
		int len = 0;
		if(cursor.moveToFirst()) {
			len = cursor.getInt(cursor.getColumnIndex("length"));
		}
		
		return len;
	}
}
