package jp.tetra2000.walkingphone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public DBHelper(Context context) {
		super(context, DB.NAME, null, 1 );
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table walk(year integer, month integer, day integer, length integer);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
