package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MediaPostTable {

	

	public static final String STATUS_QUEUE = "Queue";
	public static final String STATUS_SENT = "Sent";
	public static final String STATUS_SENDING = "Sending...";
	public static final String STATUS_ERROR = "Error";
	
	
	public static final String TABLE = "COMPOSE";
	public static final String COLUMN_ID = "_id";
	public static final String USER_ID = "USER_ID";
	public static final String USER_PASS = "USER_PASS";
	public static final String USER_NAME = "USER_NAME";
	public static final String DIST = "DIST";
	public static final String CLTTXNID="CLTTXNID" ;
	
	
	public static final String MSG_TYPE = "MSG_TYPE";
	public static final String MSG_OP = "MSG_OP";
	public static final String REQ_ID = "REQ_ID";
	public static final String REQ_TYPE = "REQ_TYPE";
	public static final String REFERENCE_MESSAGEID = "REFERENCE_MESSAGEID";
	
	
	public static final String CLIENT_ID = "CLIENT_ID";
	public static final String DATE = "DATE";
	public static final String SENT_DATE = "SENT_DATE";
	public static final String TRY = "TRY";
	public static final String ATTACHMENT = "ATTACHMENT";
	public static final String ATTACHMENT_DONE = "ATTACHMENT_DONE";
	public static final String ATTACHMENT_SIZE = "ATTACHMENT_SIZE";
	public static final String TAG = "TAG";
	public static final String ABOUT = "ABOUT";
	public static final String MODE = "MODE";
	public static final String CATEGORY = "CATEGORY";
	public static final String LAT = "LAT";
	public static final String LON = "LON";
	public static final String FACEBOOK = "FACEBOOK";
	public static final String ERROR_MSG = "ERROR_MSG";
	public static final String AUDIO = "AUDIO";
	public static final String AUDIO_STAUTS = "AUDIO_STAUTS";
	public static final String MEDIS_STAUTS = "MEDIS_STAUTS";
	public static String CONVERSATION_ID = "CONVERSATION_ID";
	
	
	public static final String COMMAND = "COMMAND";
	public static final String COMMAND_TYPE = "COMMAND_TYPE";
	
	public static final String VIDEO = "VIDEO";
	public static final String VIDEO_STATUS = "VIDEO_STATUS";
	public static final String IMAGE_1 = "IMAGE_1";
	public static final String IMAGE_1_STATUS = "IMAGE_1_STATUS";
	public static final String IMAGE_2 = "IMAGE_2";
	public static final String IMAGE_2_STATUS = "IMAGE_2_STATUS";
	public static final String IMAGE_3 = "IMAGE_3";
	public static final String IMAGE_3_STATUS = "IMAGE_3_STATUS";
	public static final String IMAGE_4 = "IMAGE_4";
	public static final String IMAGE_4_STATUS = "IMAGE_4_STATUS";
	public static final String IMAGE_5 = "IMAGE_5";
	public static final String IMAGE_5_STATUS = "IMAGE_5_STATUS";
	public static final String IMAGE_6 = "IMAGE_6";
	public static final String IMAGE_6_STATUS = "IMAGE_6_STATUS";
	public static final String IMAGE_7 = "IMAGE_7";
	public static final String IMAGE_7_STATUS = "IMAGE_7_STATUS";
	public static final String IMAGE_8 = "IMAGE_8";
	public static final String IMAGE_8_STATUS = "IMAGE_8_STATUS";
	public static final String IMAGE_9 = "IMAGE_9";
	public static final String IMAGE_9_STATUS = "IMAGE_9_STATUS";
	public static final String IMAGE_10 = "IMAGE_10";
	public static final String IMAGE_10_STATUS = "IMAGE_10_STATUS";
	public static final String IMAGE_11 = "IMAGE_11";
	public static final String IMAGE_11_STATUS = "IMAGE_11_STATUS";
	public static final String IMAGE_12 = "IMAGE_12";
	public static final String IMAGE_12_STATUS = "IMAGE_12_STATUS";
	public static final String IMAGE_13 = "IMAGE_13";
	public static final String IMAGE_13_STATUS = "IMAGE_13_STATUS";
	public static final String IMAGE_14 = "IMAGE_14";
	public static final String IMAGE_14_STATUS = "IMAGE_14_STATUS";
	public static final String IMAGE_15 = "IMAGE_15";
	public static final String IMAGE_15_STATUS = "IMAGE_15_STATUS";
	public static final String IMAGE_16 = "IMAGE_16";
	public static final String IMAGE_16_STATUS = "IMAGE_16_STATUS";
	public static final String IMAGE_17 = "IMAGE_17";
	public static final String IMAGE_17_STATUS = "IMAGE_17_STATUS";
	public static final String IMAGE_18 = "IMAGE_18";
	public static final String IMAGE_18_STATUS = "IMAGE_18_STATUS";
	public static final String IMAGE_19 = "IMAGE_19";
	public static final String IMAGE_19_STATUS = "IMAGE_19_STATUS";
	public static final String IMAGE_20 = "IMAGE_20";
	public static final String IMAGE_20_STATUS = "IMAGE_20_STATUS";
	public static final String COMMENT="COMMENT" ;
	private static final String CREATE_TABLE = "create table "
			+ TABLE + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " // 0
			+ USER_ID + " integer,"// 1
			+ USER_NAME + " text,"// 1		
			+ DIST + " text,"// 1
			+ CLIENT_ID + " integer,"// 1
			+ DATE + " integer,"// 1
			+ TAG + " text,"// 1
			+ COMMENT + " text,"// 1
			+ ABOUT + " text,"// 1
			+ MODE + " text,"// 1
			+ CATEGORY + " text,"// 1
			+ LAT + " text,"// 1
			+ LON + " text,"// 1
			+ FACEBOOK + " text,"// 1
			+ AUDIO + " BLOB ,"// 1
			+ AUDIO_STAUTS + " text,"// 1
			+ VIDEO + " text,"// 1
			+ VIDEO_STATUS + " text,"// 1
			+ IMAGE_1 + " text,"// 1
			+ IMAGE_1_STATUS + " text,"// 1
			+ IMAGE_2 + " text,"// 1
			+ IMAGE_2_STATUS + " text,"// 1
			+ IMAGE_3 + " text,"// 1
			+ IMAGE_3_STATUS + " text,"// 1
			+ IMAGE_4 + " text,"// 1
			+ IMAGE_4_STATUS + " text,"// 1
			+ IMAGE_5 + " text,"// 1
			+ IMAGE_5_STATUS + " text,"// 1
			+ IMAGE_6 + " text,"// 1
			+ IMAGE_6_STATUS + " text,"// 1
			+ IMAGE_7 + " text,"// 1
			+ IMAGE_7_STATUS + " text,"// 1
			+ IMAGE_8 + " text,"// 1
			+ IMAGE_8_STATUS + " text,"// 1
			+ IMAGE_9 + " text,"// 1
			+ IMAGE_9_STATUS + " text,"// 1
			+ IMAGE_10 + " text,"// 1
			+ IMAGE_10_STATUS + " text,"// 1
			+ IMAGE_11 + " text,"// 1
			+ IMAGE_11_STATUS + " text,"// 1
			+ IMAGE_12 + " text,"// 1
			+ IMAGE_12_STATUS + " text,"// 1
			+ IMAGE_13 + " text,"// 1
			+ IMAGE_13_STATUS + " text,"// 1
			+ IMAGE_14 + " text,"// 1
			+ IMAGE_14_STATUS + " text,"// 1
			+ IMAGE_15 + " text,"// 1
			+ IMAGE_15_STATUS + " text,"// 1
			+ IMAGE_16 + " text,"// 1
			+ IMAGE_16_STATUS + " text,"// 1
			+ IMAGE_17 + " text,"// 1
			+ IMAGE_17_STATUS + " text,"// 1
			+ IMAGE_18 + " text,"// 1
			+ IMAGE_18_STATUS + " text,"// 1
			+ IMAGE_19 + " text,"// 1
			+ IMAGE_19_STATUS + " text,"// 1
			+ IMAGE_20 + " text,"// 1
			+ IMAGE_20_STATUS + " text,"// 1
			+ MEDIS_STAUTS + " text,"// 1
			+ CONVERSATION_ID + " text,"// 1
			+ SENT_DATE + " integer,"// 1
			+ TRY + " integer,"// 1
			+ ATTACHMENT + " integer,"// 1
			+ ATTACHMENT_SIZE + " integer,"// 1
			+ ERROR_MSG + " text,"// 1
			+ ATTACHMENT_DONE + " integer,"// 1
			+ CLTTXNID + " integer,"// 1
			
			+ MSG_TYPE + " text,"// 1
			+ MSG_OP + " text,"// 1
			+ REQ_ID + " text,"// 1
			+ REQ_TYPE + " text,"// 1
			+ COMMAND + " text,"// 1
			+ COMMAND_TYPE + " text,"// 1
			+ REFERENCE_MESSAGEID + " text,"// 1
			
			+ USER_PASS + " text"// 1
			
			
	+ ");";
	

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(MediaPostTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE);
		onCreate(database);
	}
}