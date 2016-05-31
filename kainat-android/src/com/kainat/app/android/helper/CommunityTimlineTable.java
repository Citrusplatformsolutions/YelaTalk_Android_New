package com.kainat.app.android.helper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommunityTimlineTable {
	public static final String TABLE = "TABLE_COMMUNITY_TIMELINE";
	public static final String CHANNEL_POST_ID = "channelPostId";
	public static final String GROUP_NAME = "groupName";
	public static final String POSTED_BY_USER = "postedByUser"; //userId +#$#$#+ userName +#$#$#+ name +#$#$#+ thumbUrl +#$#$#+ profileUrl
	public static final String TEXT = "text"; 
	public static final String HASHTAG = "hashtags";
	public static final String AUDIO_CONTENT = "audioMediaContentUrlList";  // contentType + ##I$I## + contentUrl + ##I$I##  +thumbUrl + #N$N#
	public static final String IMAGE_CONTENT = "imageMediaContentUrlList";
	public static final String VIDEO_CONTENT = "videoMediaContentUrlList";
	public static final String AUDIO_PLAY_COUNT = "audioPlayCount";
	public static final String VIDEO_PLAY_COUNT = "videoPlayCount";
	public static final String LIKES_COUNT = "likesCount";
	public static final String COMMENT_COUNT = "commentsCount";
	public static final String SHARE_COUNT = "shareCount";
	public static final String VIEW_COUNT = "viewCount";
	public static final String CREATED_DATE = "createdDate";
	public static final String NEXTURL = "nextUrl";
	
	private static final String CREATE_COMMUNITY_TIMELINE= "create table "
			+ TABLE + "(" + CHANNEL_POST_ID
			+ " integer primary key , " // 0
			+ GROUP_NAME + " text ,"// 1
			+ POSTED_BY_USER + " text,"// 3
			+ TEXT + " text,"// 4
			+ HASHTAG + " text,"// 5
			+ AUDIO_CONTENT + " text,"// 6				
			+ IMAGE_CONTENT + " text,"// 7	
			+ VIDEO_CONTENT + " text,"// 8	
			+ AUDIO_PLAY_COUNT + " text,"// 9	
			+ VIDEO_PLAY_COUNT + " text,"// 10	
			+ LIKES_COUNT + " text,"// 11
			+ COMMENT_COUNT + " text,"// 12
			+ SHARE_COUNT + " text,"// 13
			+ VIEW_COUNT + " integer,"// 14
			+ CREATED_DATE + " text,"
			+NEXTURL + " text"// 23	
	+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_COMMUNITY_TIMELINE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(CommunityTimlineTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + CREATE_COMMUNITY_TIMELINE);
		onCreate(database);
	}
}