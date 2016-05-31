package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupEventTable {

	public static final String TABLE = "GroupEvent";
	public static final String COLUMN_ID = "_id";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String ID ="ID";
	public static final String TITLE  ="TITLE";
	public static final String TOTAL_COUNT ="TOTAL_COUNT" ;
	public static final String GROUP_NAME  ="GROUP_NAME";
	public static final String COM_PROFILE_URL  ="COM_PROFILE_URL";
	public static final String HOSTER_USER_NAME  ="HOSTER_USER_NAME";
	public static final String HOSTER_THUMBUR ="HOSTER_THUMBUR" ;
	public static final String HOSTER_PROFILE_URL  ="HOSTER_PROFILE_URL";
	public static final String HOSTER_DISPLAY_NAME ="HOSTER_DISPLAY_NAME" ;
	public static final String DESCRIPTION  ="DESCRIPTION";
	public static final String START_DATE  ="START_DATE";
	public static final String END_DATE  ="END_DATE";
	public static final String PICTURE_URL  ="PICTURE_URL";
	public static final String THUMB_PICTURE_URL  ="THUMB_PICTURE_URL";
	public static final String AUDIO_URL ="AUDIO_URL" ;
	public static final String NEXT_URL  ="NEXT_URL";
	public static final String PREV_URL  ="PREV_URL";
	public static final String TRACKING ="TRACKING";	
	public static final String SEO  ="SEO";
	public static final String INSERT_TIME  ="INSERT_TIME";
	public static final String MORE  ="MORE";
	
	public static final String MESSAGE_INFO1 ="MESSAGE_INFO1";	
	public static final String MESSAGE_INFO2 ="MESSAGE_INFO2";	
	public static final String BUTTON_NAME ="BUTTON_NAME";
	public static final String BUTTON_ACTION ="BUTTON_ACTION";
	public static final String MESSAGE_ALERT1 ="MESSAGE_ALERT1";
	public static final String MESSAGE_ALERT2 ="MESSAGE_ALERT2";
	public static final String START_TIME ="START_TIME";
	public static final String END_TIME ="END_TIME";
	
	public static final String OWNER_USER_NAME ="OWNER_USER_NAME";
	public static final String OWNER_THUMB_URL ="OWNER_THUMB_URL";
	public static final String OWNERP_ROFILE_URL ="OWNERP_ROFILE_URL";
	public static final String OWNER_DISPLAY_NAME ="OWNER_DISPLAY_NAME";
	
	
	
	
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text,"// 1
			+ ID + " text,"// 1
			+ TITLE + " text,"// 1
			+ TOTAL_COUNT + " text,"// 1
			+ GROUP_NAME + " text,"// 1
			+ COM_PROFILE_URL + " text,"// 1
			+ HOSTER_USER_NAME + " text,"// 1
			+ HOSTER_THUMBUR + " text,"// 1
			+ HOSTER_PROFILE_URL + " text,"// 1
			+ HOSTER_DISPLAY_NAME + " text,"// 1
			+ DESCRIPTION + " text,"// 1
			+ START_DATE + " text,"// 1
			+ END_DATE + " text,"// 1
			+ PICTURE_URL + " text,"// 1
			+ THUMB_PICTURE_URL + " text,"// 1
			+ AUDIO_URL + " text,"// 1
			+ NEXT_URL + " text,"// 1
			+ PREV_URL + " text,"// 1
			+ TRACKING + " text,"// 1
			+ SEO + " text,"// 1
			+ INSERT_TIME + " integer,"// 1
			+ MORE + " integer,"// 1			

			+ MESSAGE_INFO1 + " text,"// 1
			+ MESSAGE_INFO2 + " text,"// 1
			+ BUTTON_NAME + " text,"// 1
			+ BUTTON_ACTION + " text,"// 1
			+ MESSAGE_ALERT1 + " text,"// 1
			+ MESSAGE_ALERT2 + " text,"// 1
			+ START_TIME + " text,"// 1
			+ END_TIME + " text,"// 1
			
			+ OWNER_DISPLAY_NAME + " text,"// 1
			+ OWNER_THUMB_URL + " text,"// 1
			+ OWNER_USER_NAME + " text,"// 1
			+ OWNERP_ROFILE_URL + " text"// 1
					
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(GroupEventTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE);
		onCreate(database);
	}
}