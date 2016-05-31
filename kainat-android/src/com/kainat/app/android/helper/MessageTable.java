package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTable {
	
	public static final String BOOKMARK_TABLE = "BOOKMARK_TABLE";
	public static final String TABLE = "INBOXMESSAGE";
	public static final String TABLE_CURRENT_CHAT = "TABLE_CURRENT_CHAT";
	public static final String COLUMN_ID = "_id";
	public static final String USER_ID = "USER_ID";
	public static final String PARENT_ID = "PARENTID";
	public static String CLIENTTRANSACTION_ID = "CLIENTTRANSACTION_ID";
	public static String CONTENT_BITMAP = "CONTENT_BITMAP";
	public static final String CONVERSATION_ID = "CONVERSATION_ID";
	public static String DATE_TIME = "DATE_TIME";
	public static String DRM_FLAGS = "DRM_FLAGS";
	public static String HAS_BEEN_VIEWED = "HAS_BEEN_VIEWED";
	public static String HAS_BEEN_VIEWED_BY_SIP = "HAS_BEEN_VIEWED_BY_SIP";
	public static String MESSAGE_ID = "MESSAGE_ID";
	public static String MFU_ID = "MFU_ID";
	public static String USMId = "USMId";
	public static String SUBJECT = "SUBJECT";
	public static String GROUP_PIC = "GROUP_PIC";
	public static String TAG = "TAG";
	public static String MSG_OP = "MSG_OP";
	public static String MSG_TXT = "MSG_TXT";
	public static String NOTIFICATION_FLAGS= "NOTIFICATION_FLAGS";
	public static String REFMESSAGE_ID= "REFMESSAGE_ID";
	public static String SENDERUSER_ID= "SENDERUSER_ID";
	public static String SOURCE= "SOURCE";
	public static String TARGETUSER_ID= "TARGETUSER_ID";
	public static final String VIDEO_CONTENT_THUMB_URLS = "VIDEO_CONTENT_THUMB_URLS";
	public static final String VIDEO_CONTENT_URLS = "VIDEO_CONTENT_URLS";
	public static final String VOICE_CONTENT_THUMB_URLS = "VOICE_CONTENT_THUMB_URLS";
	public static final String VOICE_CONTENT_URLS = "VOICE_CONTENT_URLS";
	public static final String IMAGE_CONTENT_THUMB_URLS = "IMAGE_CONTENT_THUMB_URLS";
	public static final String IMAGE_CONTENT_URLS = "IMAGE_CONTENT_URLS";
	public static final String USER_FIRSTNAME = "USER_FIRSTNAME";
	public static final String USER_LASTNAME = "USER_LASTNAME";
	public static final String USER_NAME = "USER_NAME";
	public static final String USER_URI = "USER_URI";
	public static final String DIRECTION = "DIRECTION";
	public static final String INSERT_TIME = "INSERT_TIME";
	public static final String SORT_TIME = "SORT_TIME";
	public static final String MORE = "MORE";
	public static final String PARTICIPANT_INFO = "PARTICIPANT_INFO";
	public static final String AUDIO_DOWNLOAD_STATUE = "AUDIO_DOWNLOAD_STATUE";
	
	public static final String VIDEO_DOWNLOAD_STATUE_1 = "VIDEO_DOWNLOAD_STATUE_1";
	public static final String VIDEO_DOWNLOAD_STATUE_2 = "VIDEO_DOWNLOAD_STATUE_2";
	public static final String VIDEO_DOWNLOAD_STATUE_3 = "VIDEO_DOWNLOAD_STATUE_3";
	public static final String VIDEO_DOWNLOAD_STATUE_4 = "VIDEO_DOWNLOAD_STATUE_4";
	public static final String VIDEO_DOWNLOAD_STATUE_5 = "VIDEO_DOWNLOAD_STATUE_5";
	public static final String VIDEO_DOWNLOAD_STATUE_6 = "VIDEO_DOWNLOAD_STATUE_6";
	public static final String VIDEO_DOWNLOAD_STATUE_7 = "VIDEO_DOWNLOAD_STATUE_7";
	public static final String VIDEO_DOWNLOAD_STATUE_8 = "VIDEO_DOWNLOAD_STATUE_8";
	
	public static final String AUDIO_ID = "AUDIO_ID";
	
	public static final String VIDEO_ID_1 = "VIDEO_ID_1";
	public static final String VIDEO_ID_2 = "VIDEO_ID_2";
	public static final String VIDEO_ID_3 = "VIDEO_ID_3";
	public static final String VIDEO_ID_4 = "VIDEO_ID_4";
	public static final String VIDEO_ID_5 = "VIDEO_ID_5";
	public static final String VIDEO_ID_6 = "VIDEO_ID_6";
	public static final String VIDEO_ID_7 = "VIDEO_ID_7";
	public static final String VIDEO_ID_8 = "VIDEO_ID_8";//i am going to store RTML
	
	public static final String SENDING_STATE = "SENDING_STATE";
	public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
	public static final String ITEM_SELECTED = "ITEM_SELECTED";
	public static final String IS_BOOKMARK = "IS_BOOKMARK";
	public static final String IS_NEXT = "IS_NEXT";
	public static final String CLTTXNID="CLTTXNID" ;
	public static final String IS_LAST_MESSAGE="IS_LAST_MESSAGE" ;
	public static final String IS_LEFT="IS_LEAVED" ;
	public static final String VIDEO_SIZE="VIDEO_SIZE" ;
	public static final String AUDIO_SIZE="AUDIO_SIZE" ;
	public static final String IMAGE_SIZE="IMAGE_SIZE" ;
	
	public static final String MESSAGE_ATTRIBUTE="MESSAGE_ATTRIBUTE" ;
	public static final String ANI_TYPE="ANI_TYPE" ;
	public static final String ANI_VALUE="ANI_VALUE" ;
	public static final String COMMENT="COMMENT" ;
	
//	public static final String PARTICIPANT_INFO_SENDER = "PARTICIPANT_INFO_SENDER";
	
	
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ PARENT_ID + " text,"// 25
			+ USER_ID + " integer,"// 25
			+ VIDEO_CONTENT_THUMB_URLS + " text,"// 25
			+ VIDEO_CONTENT_URLS + " text,"// 26
			+ VOICE_CONTENT_THUMB_URLS + " text,"// 27
			+ VOICE_CONTENT_URLS + " text,"// 28
			+ IMAGE_CONTENT_THUMB_URLS + " text,"// 7
			+ IMAGE_CONTENT_URLS + " text,"// 8	
			+ USER_FIRSTNAME + " text,"// 1
			+ USER_LASTNAME + " text,"// 1
			+ USER_NAME + " text,"// 1
			+ USER_URI + " text,"// 1
			+ TARGETUSER_ID + " text,"// 1
			+ SOURCE + " text,"// 1
			+ SENDERUSER_ID + " text,"// 1
			+ REFMESSAGE_ID + " text,"// 1
			+ NOTIFICATION_FLAGS + " text,"// 1
			+ MSG_TXT + " text,"// 1
			+ MSG_OP + " integer,"// 1
			+ MFU_ID + " text,"// 1
			+ MESSAGE_ID + " text,"// 1
			+ USMId + " text,"// 1
			+ HAS_BEEN_VIEWED_BY_SIP + " text,"// 1
			+ HAS_BEEN_VIEWED + " text,"// 1
			+ DRM_FLAGS + " integer,"// 1
			+ DATE_TIME + " text,"// 1
			+ ANI_TYPE + " text,"// 1
			+ ANI_VALUE + " text,"// 1
			+ CONVERSATION_ID + " text,"// 1
			+ CONTENT_BITMAP + " text,"// 1
			+ DIRECTION + " text,"// 1
			+ INSERT_TIME + " text,"// 1
			+ SORT_TIME + " integer,"// 1
			+ MORE + " text,"// 1
			+ CLIENTTRANSACTION_ID + " text,"// 1
			+ PARTICIPANT_INFO + " text,"// 1
			+ AUDIO_DOWNLOAD_STATUE + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_1 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_2 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_3 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_4 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_5 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_6 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_7 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_8 + " integer,"// 1
			+ SENDING_STATE + " integer,"// 1
			+ AUDIO_ID + " integer,"// 1
			+ VIDEO_ID_1 + " integer,"// 1
			+ VIDEO_ID_2 + " integer,"// 1
			+ VIDEO_ID_3 + " integer,"// 1
			+ VIDEO_ID_4 + " integer,"// 1
			+ VIDEO_ID_5 + " integer,"// 1
			+ VIDEO_ID_6 + " integer,"// 1
			+ VIDEO_ID_7 + " integer,"// 1
			+ VIDEO_ID_8 + " integer,"// 1
			+ SUBJECT + " text,"// 1
			+ GROUP_PIC + " text,"// 1
			+ MESSAGE_ATTRIBUTE + " text,"// 1
			+ TAG + " text,"// 1
			+ MESSAGE_TYPE + " integer,"// 1
			+ ITEM_SELECTED + " integer,"// 1
			+ IS_BOOKMARK + " integer,"// 1
			+ CLTTXNID + " integer,"// 1
			+ IS_LAST_MESSAGE + " text,"// 1
			+ VIDEO_SIZE + " text,"// 1
			+ AUDIO_SIZE + " text,"// 1
			+ IMAGE_SIZE + " text,"// 1
			+ COMMENT + " text,"// 1
			+ IS_LEFT + " integer,"// 1
			+ IS_NEXT + " integer"// 1
			+ ");";
	
	private static final String CREATE_TABLE_CURRENT_CHAT = "create table "
			+ TABLE_CURRENT_CHAT + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ PARENT_ID + " text,"// 25
			+ USER_ID + " integer,"// 25
			+ VIDEO_CONTENT_THUMB_URLS + " text,"// 25
			+ VIDEO_CONTENT_URLS + " text,"// 26
			+ VOICE_CONTENT_THUMB_URLS + " text,"// 27
			+ VOICE_CONTENT_URLS + " text,"// 28
			+ IMAGE_CONTENT_THUMB_URLS + " text,"// 7
			+ IMAGE_CONTENT_URLS + " text,"// 8	
			+ USER_FIRSTNAME + " text,"// 1
			+ USER_LASTNAME + " text,"// 1
			+ USER_NAME + " text,"// 1
			+ USER_URI + " text,"// 1
			+ TARGETUSER_ID + " text,"// 1
			+ SOURCE + " text,"// 1
			+ SENDERUSER_ID + " text,"// 1
			+ REFMESSAGE_ID + " text,"// 1
			+ NOTIFICATION_FLAGS + " text,"// 1
			+ MSG_TXT + " text,"// 1
			+ MSG_OP + " integer,"// 1
			+ MFU_ID + " text,"// 1
			+ MESSAGE_ID + " text,"// 1
			+ USMId + " text,"// 1
			+ HAS_BEEN_VIEWED_BY_SIP + " text,"// 1
			+ HAS_BEEN_VIEWED + " text,"// 1
			+ DRM_FLAGS + " integer,"// 1
			+ ANI_TYPE + " text,"// 1
			+ ANI_VALUE + " text,"// 1
			+ DATE_TIME + " text,"// 1
			+ CONVERSATION_ID + " text,"// 1
			+ CONTENT_BITMAP + " text,"// 1
			+ DIRECTION + " text,"// 1
			+ MESSAGE_ATTRIBUTE + " text,"// 1
			+ INSERT_TIME + " text,"// 1
			+ SORT_TIME + " integer,"// 1
			+ MORE + " text,"// 1
			+ CLIENTTRANSACTION_ID + " text,"// 1
			+ PARTICIPANT_INFO + " text,"// 1
			+ AUDIO_DOWNLOAD_STATUE + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_1 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_2 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_3 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_4 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_5 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_6 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_7 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_8 + " integer,"// 1
			+ SENDING_STATE + " integer,"// 1
			+ AUDIO_ID + " integer,"// 1
			+ VIDEO_ID_1 + " integer,"// 1
			+ VIDEO_ID_2 + " integer,"// 1
			+ VIDEO_ID_3 + " integer,"// 1
			+ VIDEO_ID_4 + " integer,"// 1
			+ VIDEO_ID_5 + " integer,"// 1
			+ VIDEO_ID_6 + " integer,"// 1
			+ VIDEO_ID_7 + " integer,"// 1
			+ VIDEO_ID_8 + " integer,"// 1
			+ SUBJECT + " text,"// 1
			+ GROUP_PIC + " text,"// 1
			+ TAG + " text,"// 1
			+ MESSAGE_TYPE + " integer,"// 1
			+ ITEM_SELECTED + " integer,"// 1
			+ IS_BOOKMARK + " integer,"// 1
			+ CLTTXNID + " integer,"// 1
			+ IS_LAST_MESSAGE + " text,"// 1
			+ VIDEO_SIZE + " text,"// 1
			+ AUDIO_SIZE + " text,"// 1
			+ IMAGE_SIZE + " text,"// 1
			+ COMMENT + " text,"// 1
			+ IS_LEFT + " integer,"// 1
			+ IS_NEXT + " integer"// 1
			+ ");";
	
	private static final String CREATE_TABLE_BOOKMARK = "create table "
			+ BOOKMARK_TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, "
			+ PARENT_ID + " text,"// 25
			+ USER_ID + " integer,"// 25
			+ VIDEO_CONTENT_THUMB_URLS + " text,"// 25
			+ VIDEO_CONTENT_URLS + " text,"// 26
			+ VOICE_CONTENT_THUMB_URLS + " text,"// 27
			+ VOICE_CONTENT_URLS + " text,"// 28
			+ IMAGE_CONTENT_THUMB_URLS + " text,"// 7
			+ IMAGE_CONTENT_URLS + " text,"// 8	
			+ USER_FIRSTNAME + " text,"// 1
			+ USER_LASTNAME + " text,"// 1
			+ USER_NAME + " text,"// 1
			+ USER_URI + " text,"// 1
			+ TARGETUSER_ID + " text,"// 1
			+ SOURCE + " text,"// 1
			+ SENDERUSER_ID + " text,"// 1
			+ REFMESSAGE_ID + " text,"// 1
			+ NOTIFICATION_FLAGS + " text,"// 1
			+ MSG_TXT + " text,"// 1
			+ MSG_OP + " integer,"// 1
			+ MFU_ID + " text,"// 1
			+ MESSAGE_ID + " text,"// 1
			+ USMId + " text,"// 1
			+ HAS_BEEN_VIEWED_BY_SIP + " text,"// 1
			+ HAS_BEEN_VIEWED + " text,"// 1
			+ DRM_FLAGS + " integer,"// 1
			+ DATE_TIME + " text,"// 1
			+ CONVERSATION_ID + " text,"// 1
			+ CONTENT_BITMAP + " text,"// 1
			+ DIRECTION + " text,"// 1
			+ INSERT_TIME + " text,"// 1
			+ SORT_TIME + " integer,"// 1
			+ MORE + " text,"// 1
			+ ANI_TYPE + " text,"// 1
			+ ANI_VALUE + " text,"// 1
			+ CLIENTTRANSACTION_ID + " text,"// 1
			+ PARTICIPANT_INFO + " text,"// 1
			+ AUDIO_DOWNLOAD_STATUE + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_1 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_2 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_3 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_4 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_5 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_6 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_7 + " integer,"// 1
			+ VIDEO_DOWNLOAD_STATUE_8 + " integer,"// 1
			+ SENDING_STATE + " integer,"// 1
			+ AUDIO_ID + " integer,"// 1
			+ VIDEO_ID_1 + " integer,"// 1
			+ VIDEO_ID_2 + " integer,"// 1
			+ VIDEO_ID_3 + " integer,"// 1
			+ VIDEO_ID_4 + " integer,"// 1
			+ VIDEO_ID_5 + " integer,"// 1
			+ VIDEO_ID_6 + " integer,"// 1
			+ VIDEO_ID_7 + " integer,"// 1
			+ VIDEO_ID_8 + " integer,"// 1
			+ SUBJECT + " text,"// 1
			+ GROUP_PIC + " text,"// 1
			+ TAG + " text,"// 1
			+ MESSAGE_TYPE + " integer,"// 1
			+ VIDEO_SIZE + " text,"// 1
			+ AUDIO_SIZE + " text,"// 1
			+ IMAGE_SIZE + " text,"// 1
			+ COMMENT + " text,"// 1
			+ ITEM_SELECTED + " integer,"// 1
			+ IS_BOOKMARK + " integer,"// 1
			+ IS_LEFT + " integer,"// 1
			+ IS_NEXT + " integer"// 1
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
//		System.out.println(CREATE_TABLE);
		database.execSQL(CREATE_TABLE);
		database.execSQL(CREATE_TABLE_BOOKMARK);
		database.execSQL(CREATE_TABLE_CURRENT_CHAT);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(CommentTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(database);
	}
}
