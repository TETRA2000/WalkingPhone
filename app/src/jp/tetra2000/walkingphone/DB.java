package jp.tetra2000.walkingphone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
	public static final String NAME = "walk_db";
	
	private DBHelper mHelper;
	
	public DB(Context context) {
		mHelper = new DBHelper(context);
	}
	
	public void add(int year, int month, int day, int length) {
		int oldLen = get(year, month, day);
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		
		String selection = "year="+year+" and month="+month+" and day="+day;		
		
		ContentValues cv = new ContentValues();
		cv.put("length", oldLen + length);
		
		db.update("walk", cv, selection, null);
				
		db.close();
	}
	
	public int get(int year, int month, int day) {
		SQLiteDatabase db = mHelper.getReadableDatabase();
		
		String columns[] = {"length"};
		String selection = "year="+year+" and month="+month+" and day="+day;
		Cursor cursor =
				db.query("walk", columns, selection, null, null, null, null);
		
		if(cursor.moveToFirst()) {
			return cursor.getInt(cursor.getColumnIndex("length"));
		}
		
		return 0;
	}
}
