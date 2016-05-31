package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MediaLikeUserTable {

	public static final String TABLE = "USER_LIKE_DISLIKE";
	public static final String COLUMN_ID = "_id";
	public static final String USERID = "USERID";
	public static final String USERNAME = "USERNAME";
	public static final String OPDATETIME = "OPDATETIME";
	public static final String PROFILEURL = "PROFILEURL";
	public static final String USERTHUMBURL = "USERTHUMBURL";
	public static final String USERDISPLAYNAME = "USERDISPLAYNAME";
	public static final String NEXT_URL = "NEXT_URL";
	public static final String PREV_URL = "PREV_URL";
	public static final String TIMESTAMP = "TIMESTAMP";
	
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ USERID + " text,"// 2
			+ USERNAME + " text,"// 3
			+ OPDATETIME + " text,"// 4
			+ PROFILEURL + " text,"// 5
			+ USERTHUMBURL + " text,"// 6
			+ USERDISPLAYNAME + " text,"// 7
			+ NEXT_URL + " text,"// 7
			+ PREV_URL + " text,"// 7
			+ TIMESTAMP + " integer"// 7
			
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MediaLikeUserTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE);
		onCreate(database);
	}
}