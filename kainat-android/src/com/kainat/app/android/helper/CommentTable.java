package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommentTable {

	public static final String TABLE = "MEDIA_COMMNET";
	public static final String COLUMN_ID = "_id";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String STORY_ID = "STORY_ID";
	public static final String TRACKING = "TRACKING";
	public static final String SUE = "SUE";
	public static final String PUBLISHED = "PUBLISHED";	
	public static final String USERNAME = "USERNAME";
	public static final String FIRSTNAME = "FIRSTNAME";
	public static final String LASTNAME = "LASTNAME";
	public static final String LINK_MEDIA = "MEDIA_LINK";
	public static final String LINK_OTHER = "OTHER_LINK";
	public static final String TEXT = "TEXT";
	public static final String VID_THUMB_URL = "VID_THUMB_URL";
	public static final String VID_URL = "VID_URL";
	public static final String LIKES = "LIKES";
	public static final String DISLIKES = "DISLIKES";
	public static final String LIKES_VALUE = "LIKES_VALUE";
	public static final String PREV_URL = "PREV_URL";
	public static final String NEXT_URL = "NEXT_URL";
	public static final String TOTAL_COMMENT = "TOTAL_COMMENT";
	public static final String INSERT_TIME  ="INSERT_TIME";
	
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text,"// 1
			+ STORY_ID + " text,"// 2
			+ TRACKING + " text,"// 3
			+ SUE + " text,"// 3
			+ PUBLISHED + " text,"// 4
			+ USERNAME + " text,"// 5
			+ FIRSTNAME + " text,"// 6
			+ LASTNAME + " text,"// 7
			+ TEXT + " text,"// 8
			+ VID_THUMB_URL + " text,"// 9
			+ VID_URL + " text,"// 10
			+ LIKES + " text,"// 11
			+ DISLIKES + " text,"// 12
			+ LIKES_VALUE + " text,"// 12
			+ PREV_URL + " text,"// 13
			+ NEXT_URL + " text,"// 14
			+ LINK_MEDIA + " text,"// 15
			+ LINK_OTHER + " text,"// 16
			+ TOTAL_COMMENT + " text,"// 16
			+ INSERT_TIME + " integer"// 1
			
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(CommentTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE);
		onCreate(database);
	}
}