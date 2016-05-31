package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LandingPageTable {

	public static final String TABLE_LANDING_PAGE = "TABLE_LANDING_PAGE";
	public static final String COLUMN_ID = "_id";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String STORY_ID = "STORY_ID";
	public static final String TRACKING = "TRACKING";
	public static final String SUE = "SUE";
	public static final String TITLE = "TITLE";
	public static final String TYPE = "TYPE";
	public static final String IMAGE_CONTENT_THUMB_URLS = "IMAGE_CONTENT_THUMB_URLS";
	public static final String IMAGE_CONTENT_URLS = "IMAGE_CONTENT_URLS";
	public static final String DESC = "DESC";
	public static final String DESC_ORI = "DESC_ORI";
	public static final String PUBLISHED = "PUBLISHED";
	public static final String UPDATED = "UPDATED";
	public static final String DATE = "DATE";
	public static final String LIKE = "LIKE";
	public static final String DISLIKE = "DISLIKE";
	public static final String USER_THUMB_URL = "USER_THUMB_URL";
	public static final String COMMENT_URL = "COMMENT_URL";
	public static final String SHARE_URL = "SHARE_URL";
	public static final String PREV_URL = "PREV_URL";
	public static final String NEXT_URL = "NEXT_URL";
	public static final String FIRSTNAME = "FIRSTNAME";
	public static final String LASTNAME = "LASTNAME";
	public static final String USERNAME = "USERNAME";
	public static final String MESSAGE_STATE = "MESSAGE_STATE";
	public static final String DRM = "DRM";
	public static final String ACTION = "ACTION";
	public static final String LOGIN_USER = "LOGIN_USER";
	public static final String TAB = "TAB";
	public static final String DESC2 = "DESC2";
	public static final String VIDEO_CONTENT_THUMB_URLS = "VIDEO_CONTENT_THUMB_URLS";
	public static final String VIDEO_CONTENT_URLS = "VIDEO_CONTENT_URLS";
	public static final String VOICE_CONTENT_THUMB_URLS = "VOICE_CONTENT_THUMB_URLS";
	public static final String VOICE_CONTENT_URLS = "VOICE_CONTENT_URLS";
	public static final String OTHER_USER_THUMB_URL = "OTHER_USER_THUMB_URL";
	public static final String OTHER_USER_FIRSTNAME = "OTHER_USER_FIRSTNAME";
	public static final String OTHER_USER_LASTNAME = "OTHER_USER_LASTNAME";
	public static final String OTHER_USER_USERNAME = "OTHER_USER_USERNAME";
	public static final String OTHER_USER_PROFILE_URL = "OTHER_USER_PROFILE_URL";
	public static final String USERTHUMB_PROFILE_URL = "USERTHUMB_PROFILE_URL";
	
	public static final String IMAGE_CONTENT_CLICK_URL = "IMAGE_CONTENT_CLICK_URL";
	public static final String VIDEO_CONTENT_CLICK_URL = "VIDEO_CONTENT_CLICK_URL";
	public static final String VOICE_CONTENT_CLICK_URL = "VOICE_CONTENT_CLICK_URL";
	
	public static final String IMAGE_CONTENT_ID = "IMAGE_CONTENT_ID";
	public static final String VIDEO_CONTENT_ID = "VIDEO_CONTENT_ID";
	public static final String VOICE_CONTENT_ID = "VOICE_CONTENT_ID";
	public static final String MEDIA_TEXT = "MEDIA_TEXT";
	public static final String DIRECTION = "DIRECTION";
	public static final String INSERT_TIME = "INSERT_TIME";
	public static final String MORE = "MORE";
	public static final String OP_TYPE = "OP_TYPE";
	
	private static final String CREATE_TABLE_LANDING_PAGE = "create table "
			+ TABLE_LANDING_PAGE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text,"// 1
			+ STORY_ID + " text,"// 2
			+ TRACKING + " text,"// 3
			+ SUE + " text,"// 4
			+ TITLE + " text," // 5
			+ TYPE + " text,"// 6		
			+ IMAGE_CONTENT_THUMB_URLS + " text,"// 7
			+ IMAGE_CONTENT_URLS + " text,"// 8		
			+ DESC + " text,"// 9
			+ PUBLISHED + " text,"// 10
			+ UPDATED + " text,"// 11
			+ DISLIKE + " text,"// 12
			+ USER_THUMB_URL + " text,"// 13
			+ COMMENT_URL + " text,"// 14
			+ SHARE_URL + " text,"// 15
			+ PREV_URL + " text,"// 16
			+ NEXT_URL + " text,"// 17
			+ FIRSTNAME + " text,"// 18
			+ LASTNAME + " text,"// 19
			+ USERNAME + " text,"// 20
			+ MESSAGE_STATE + " text,"// 21
			+ DRM + " text,"// 22
			+ ACTION + " text,"// 22
			+ LOGIN_USER + " text,"// 22
			+ TAB + " text,"// 22
			+ DATE + " integer,"// 23
			+ DESC2 + " text,"// 24
			+ VIDEO_CONTENT_THUMB_URLS + " text,"// 25
			+ VIDEO_CONTENT_URLS + " text,"// 26
			+ VOICE_CONTENT_THUMB_URLS + " text,"// 27
			+ VOICE_CONTENT_URLS + " text,"// 28
			+ OTHER_USER_THUMB_URL + " text,"// 29	
			+ OTHER_USER_FIRSTNAME + " text,"// 27
			+ OTHER_USER_LASTNAME + " text,"// 28
			+ OTHER_USER_USERNAME + " text,"// 29
			+ OTHER_USER_PROFILE_URL + " text,"// 30
			+ LIKE + " text,"// 12
			+ USERTHUMB_PROFILE_URL + " text,"// 12
			+ IMAGE_CONTENT_CLICK_URL  + " text,"// 12
			+ VIDEO_CONTENT_CLICK_URL  + " text,"// 12
			+ VOICE_CONTENT_CLICK_URL  + " text,"// 12
			+ IMAGE_CONTENT_ID  + " text,"// 12
			+ VIDEO_CONTENT_ID  + " text,"// 12
			+ VOICE_CONTENT_ID  + " text,"// 12
			+ MORE + " integer,"// 25
			+ DESC_ORI + " text,"// 25
			+ MEDIA_TEXT + " text,"// 25
			+ INSERT_TIME + " integer,"// 25
			+ DIRECTION + " integer,"// 25
			+ OP_TYPE + " integer"// 25
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_LANDING_PAGE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(LandingPageTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LANDING_PAGE);
		onCreate(database);
	}
}