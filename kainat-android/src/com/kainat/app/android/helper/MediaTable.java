package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MediaTable {

	public static final String TABLE_MEDIA = "TABLE_MEDIA";
	public static final String COLUMN_ID = "_id";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String STORY_ID = "STORY_ID";
	public static final String TRACKING = "TRACKING";
	public static final String PUBLISHED = "PUBLISHED";
	public static final String SUE = "SUE";
	public static final String TITLE = "TITLE";
	public static final String TYPE = "TYPE";
	public static final String CATEGORY = "CATEGORY";
	public static final String LINK_MEDIA = "LINK_MEDIA";
	public static final String LINK_OTHER = "LINK_OTHER";
	public static final String AUTHOR_FIRSTNAME = "AUTHOR_FIRSTNAME";
	public static final String AUTHOR_LASTNAME = "AUTHOR_LASTNAME";
	public static final String AUTHOR_NAME = "AUTHOR_NAME";
	public static final String AUTHOR_URI = "AUTHOR_URI";
	public static final String COMMENT_COUNT = "COMMENT_COUNT";
	public static final String FBURL = "FBURL";
	public static final String MEDIASTATSURL = "MEDIASTATSURL";
	public static final String MEDAICOUNT = "MEDAICOUNT";
	public static final String VOTE_UP_COUNT = "VOTE_UP_COUNT";
	public static final String VOTE_DOWN_COUNT = "VOTE_DOWN_COUNT";
	public static final String CONTENT_PICTURE = "CONTENT_PICTURE";
	public static final String CONTENT_VIEW = "CONTENT_VIEW";
	public static final String SUMMARY = "SUMMARY";
	public static final String INSERT_DATE = "INSERT_DATE";
	public static final String NEXT_URL = "NEXT_URL";
	public static final String PREV_URL = "PREV_URL";
	public static final String COMMENT_URL = "COMMENT_URL";
	public static final String LOCATION = "LOCATION";
	public static final String INSERT_TIME  ="INSERT_TIME";
	public static final String MORE  ="MORE";

	private static final String CREATE_TABLE_MEDIA = "create table "
			+ TABLE_MEDIA + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text,"// 1
			+ STORY_ID + " text,"// 2
			+ TRACKING + " text,"// 3
			+ SUE + " text,"// 4
			+ TITLE + " text," // 5
			+ TYPE + " text,"// 6				
			+ CATEGORY + " text,"// 7	
			+ LINK_MEDIA + " text,"// 8	
			+ LINK_OTHER + " text,"// 9	
			+ AUTHOR_FIRSTNAME + " text,"// 10	
			+ AUTHOR_LASTNAME + " text,"// 11
			+ AUTHOR_NAME + " text,"// 12
			+ AUTHOR_URI + " text,"// 13
			+ COMMENT_COUNT + " text,"// 14
			+ FBURL + " text,"// 15
			+ MEDIASTATSURL + " text,"// 16
			+ MEDAICOUNT + " text,"// 17
			+ VOTE_UP_COUNT + " text,"// 18
			+ VOTE_DOWN_COUNT + " text,"// 18
			+ CONTENT_PICTURE + " text,"// 19
			+ SUMMARY + " text,"// 20
			+ INSERT_DATE + " text,"// 21
			+ NEXT_URL + " text,"// 22	
			+ PREV_URL + " text,"// 23	
			+ PUBLISHED + " text,"// 24
			+ COMMENT_URL + " text,"// 25
			+ LOCATION + " text,"// 25
			+ INSERT_TIME + " integer,"// 1
			+ MORE + " integer,"// 1
			+ CONTENT_VIEW + " text"// 25
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_MEDIA);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MediaTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_MEDIA);
		onCreate(database);
	}
}