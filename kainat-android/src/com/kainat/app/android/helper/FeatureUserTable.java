package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FeatureUserTable {

	public static final String TABLE = "FeatureUserTable";
	public static final String COLUMN_ID = "_id";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String STORY_ID = "STORY_ID";
	public static final String TRACKING = "TRACKING";
	public static final String SUE = "SUE";
	public static final String TITLE = "TITLE";
	public static final String TYPE = "TYPE";
	
	public static final String DISPLAY_NAME = "DISPLAY_NAME";
	public static final String MEDIA_POSTS = "MEDIA_POSTS";
	public static final String MEDIA_FOLLOWERS = "MEDIA_FOLLOWERS";
	public static final String COMMUNITIES_MEMBER = "COMMUNITIES_MEMBER";
	public static final String THUMB_URL = "THUMB_URL";
	public static final String PROFIL_EURL = "PROFIL_EURL";
	public static final String NEXT_URL  ="NEXT_URL";
	public static final String PREV_URL  ="PREV_URL";
	public static final String TOTAL_COUNT  ="totalCount";
	public static final String MORE  ="MORE";
	public static final String INSERT_TIME  ="INSERT_TIME";
	
	
	
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text,"// 1
			+ STORY_ID + " text,"// 2
			+ TRACKING + " text,"// 3
			+ SUE + " text,"// 4
			+ TITLE + " text," // 5
			+ TYPE + " text,"// 6				
			+ DISPLAY_NAME + " text,"// 7	
			+ MEDIA_POSTS + " text,"// 7
			+ MEDIA_FOLLOWERS + " text,"// 7
			+ COMMUNITIES_MEMBER + " text,"// 7
			+ THUMB_URL + " text,"// 7
			+ NEXT_URL + " text,"// 1
			+ PREV_URL + " text,"// 1
			+ PROFIL_EURL + " text,"// 7
			+ TOTAL_COUNT + " text,"// 7
			+ INSERT_TIME + " integer,"// 1
			+ MORE + " integer"// 1								
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(FeatureUserTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE);
		onCreate(database);
	}
}