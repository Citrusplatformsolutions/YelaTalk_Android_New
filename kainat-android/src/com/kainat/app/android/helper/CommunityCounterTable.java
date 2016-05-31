package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommunityCounterTable {

	public static final String TABLE = "TABLE_COMMUNITY_COUNTER_TABLE";
	public static final String GROUPID = "GROUPID";
	public static final String GROUP_NAME = "GROUPNAME";
	public static final String GROUP_MSG_COUNT = "GROUPMSGCOUNT";
	public static final String GROUP_NEXT_URL = "GROUPNEXTURL";
	public static final String GROUP_PREV_URL = "GROUPPREVURL";
	
	
	
	private static final String CREATE_COMMUNITY_COUNTER_MESSAGE = "create table "
			+ TABLE + "(" + GROUPID
			+ " integer primary key autoincrement, " 
			+ GROUP_NAME + " text ," 
			+ GROUP_MSG_COUNT + " integer,"
			+ GROUP_NEXT_URL + " text,"
			+ GROUP_PREV_URL + " text "
	+ ");";

	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_COMMUNITY_COUNTER_MESSAGE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(CommunityMessageTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_COMMUNITY_COUNTER_MESSAGE);
		onCreate(database);
	}
}
