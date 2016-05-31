package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SettingTable {

	public static final String TABLE = "SETTING_TABLE";
	public static final String COLUMN_ID = "_id";
	public static final String INSMORE_TIME = "INSMORE_TIME";
	
	
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ INSMORE_TIME + " integer"// 2
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(SettingTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE);
		onCreate(database);
	}
}