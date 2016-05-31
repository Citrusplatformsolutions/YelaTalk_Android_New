package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommunityMessageTable {
	
	public static final String TABLE = "TABLE_MASSAGE_COMMUNITY";
	public static final String GROUPID = "GROUPID";
	public static final String GROUP_NAME = "TITLE";
	public static final String IS_OWNER = "ISOWNER";
	public static final String IS_ADMIN = "ISADMIN";
	public static final String MSG_ID = "MSGID";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String SENDER_ID = "SENDER_ID";
	public static final String SENDER_NAME = "SENDER_NAME";
	public static final String FIRST_NAME = "FIRST_NAME";
	public static final String LAST_NAME = "LAST_NAME";
	public static final String MSG_STATE = "MSG_STATE";
	public static final String DRM_FLAGS = "DRM_FLAGS";
	public static final String CREATED_DATE = "CREATED_DATE";
	public static final String MSG_TEXT = "MSG_TEXT";
	public static final String MEDIA = "MEDIA";
	public static final String NEXTURL = "NEXTURL";
	
	
	
	
	private static final String CREATE_COMMUNITY_MESSAGE = "create table "
			+ TABLE + "(" + MSG_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text ,"// 1
			+ GROUPID + " integer ,"// 2
			+ GROUP_NAME + " text ," // 5
			+ IS_ADMIN + " integer,"
			+ IS_OWNER + " text,"// 23	
			+ SENDER_ID + " text ,"
			+ SENDER_NAME + " text ,"
			+ FIRST_NAME + " text ,"
			+ LAST_NAME + " text ,"
			+ MSG_STATE + " text ,"
			+ DRM_FLAGS + " text ,"
			+ CREATED_DATE + " text ,"
			+ MSG_TEXT + " text ,"
			+ NEXTURL + " text ,"
			+ MEDIA + " text "
	+ ");";

	
	
	
	
	/*public static final String TABLE = "TABLE_COMMUNITY";
	public static final String COLUMN_ID = "_id";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String GROUPID = "GROUPID";
	public static final String TRACKING = "TRACKING";
	public static final String SUE = "SUE";
	public static final String GROUP_NAME = "TITLE";
	public static final String TYPE = "TYPE";
	public static final String CATEGORY = "CATEGORY";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String IS_MODERATED = "ISMODERATED";
	public static final String AUTOA_CCEPTUSER = "AUTOACCEPTUSER";
	public static final String IS_PUBLIC = "ISPUBLIC";
	public static final String LAST_UPDATED = "LASTUPDATED";
	public static final String TIME_OF_CREATION = "TIMEOFCREATION";
	public static final String NUMBER_OF_MEMBERS = "NUMBEROFMEMBERS";
	public static final String NUMBER_OF_MESSAGES = "NUMBEROFMESSAGES";
	public static final String OWNERUSER_ID = "OWNERUSERID";
	public static final String WELCOME_MSG_ID = "WELCOMEMSGID";
	public static final String MSG_ID = "MSGID";
	public static final String VENDOR_NAME = "VENDORNAME";
	public static final String NUMBEROF_ONLINE_MEMBERS = "NUMBEROFONLINEMEMBERS";
	public static final String COMM_ON_FRIENDS_COUNT = "COMMONFRIENDSCOUNT";
	public static final String ACTIVE_MEMBERS = "ACTIVEMEMBERS";
	public static final String MEMBER_PERMISSION = "MEMBERPERMISSION";
	public static final String IS_ADMIN = "ISADMIN";
	public static final String THUMB_URL = "THUMBURL";
	public static final String MESSAGE_URL = "MESSAGEURL";	
	public static final String PROFILE_URL = "PROFILEURL";
	public static final String SEARCH_MEMBER_URL = "SEARCHMEMBERURL";
	public static final String NEXT_URL = "NEXT_URL";
	public static final String PREV_URL = "PREV_URL";
	public static final String INSERT_TIME  ="INSERT_TIME";
	public static final String ADMIN_STATE  ="ADMIN_STATE";
	public static final String MORE  ="MORE";
	
	public static final String OWNER_USERNAME  ="OWNER_USERNAME";
	public static final String GROUP_NEW_MSG_COUNT = "NEWMSGCOUNT";
	public static final String OWNER_PROFILE_URL  ="OWNER_PROFILE_URL";
	public static final String OWNER_DISPLAY_NAME  ="OWNER_DISPLAY_NAME";
	public static final String OWNER_THUMB_URL  ="OWNER_THUMB_URL";*/

	/*private static final String CREATE_COMMUNITY_MESSAGE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ PARENT_ID + " text ,"// 1
			+ GROUPID + " integer ,"// 2
			+ TRACKING + " text,"// 3
			+ SUE + " text,"// 4
			+ GROUP_NAME + " text unique," // 5
			+ TYPE + " text,"// 6				
			+ CATEGORY + " text,"// 7	
			+ DESCRIPTION + " text,"// 8	
			+ IS_MODERATED + " text,"// 9	
			+ AUTOA_CCEPTUSER + " text,"// 10	
			+ IS_PUBLIC + " text,"// 11
			+ LAST_UPDATED + " text,"// 12
			+ TIME_OF_CREATION + " text,"// 13
			+ NUMBER_OF_MEMBERS + " integer,"// 14
			+ NUMBER_OF_MESSAGES + " integer,"// 15
			+ OWNERUSER_ID + " integer,"// 16
			+ WELCOME_MSG_ID + " text,"// 17
			+ MSG_ID + " text,"// 18
			+ VENDOR_NAME + " text,"// 18
			+ NUMBEROF_ONLINE_MEMBERS + " integer,"// 19
			+ COMM_ON_FRIENDS_COUNT + " integer,"// 20
			+ ACTIVE_MEMBERS + " integer,"// 21
			+ MEMBER_PERMISSION + " text,"// 22	
			+ IS_ADMIN + " integer,"// 23	
			+ THUMB_URL + " text,"// 23	
			+ MESSAGE_URL + " text,"// 23
			+ PROFILE_URL + " text,"// 23
			+ SEARCH_MEMBER_URL + " text,"// 23
			+ NEXT_URL + " text,"// 23
			+ PREV_URL + " text,"// 23
			+ INSERT_TIME + " integer,"// 1
			+ MORE + " integer,"// 1
			+ OWNER_DISPLAY_NAME + " text,"// 23
			+ OWNER_PROFILE_URL + " text,"// 23
			+ OWNER_THUMB_URL + " text,"// 23
			+ OWNER_USERNAME + " text,"// 23
			+ GROUP_NEW_MSG_COUNT + " integer,"// 24
			+ ADMIN_STATE + " text"// 25
	+ ");";
*/
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_COMMUNITY_MESSAGE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(CommunityMessageTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_COMMUNITY_MESSAGE);
		onCreate(database);
	}
}
