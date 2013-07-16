package com.moka.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "Wireless.db";
    private static final int DATABASE_VERSION = 1;
    
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.TABLE_NAME + " ("
                + Tables._ID + " INTEGER PRIMARY KEY,"
                + Tables.NUM + " INTEGER(11),"
                + Tables.DESCRIPTION + " TEXT"
                + ");");
		
		db.execSQL("CREATE TABLE " + Menus.TABLE_NAME + " ("
				+ Menus._ID + " INTEGER PRIMARY KEY,"
				+ Menus.PRICE + " INTEGER,"
				+ Menus.TYPE_ID + " INTEGER,"
				+ Menus.NAME + " TEXT,"
				+ Menus.PIC + " TEXT,"
				+ Menus.REMARK + " TEXT"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Menus.TABLE_NAME);
		onCreate(db);
	}

}
