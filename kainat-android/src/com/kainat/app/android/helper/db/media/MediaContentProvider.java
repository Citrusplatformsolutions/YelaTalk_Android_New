package com.kainat.app.android.helper.db.media;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.kainat.app.android.helper.CommentTable;
import com.kainat.app.android.helper.CommunityCounterTable;
import com.kainat.app.android.helper.CommunityMessageTable;
import com.kainat.app.android.helper.CommunityTable;
import com.kainat.app.android.helper.CommunityTimlineTable;
import com.kainat.app.android.helper.FeatureUserTable;
import com.kainat.app.android.helper.GroupEventTable;
import com.kainat.app.android.helper.LandingPageDatabaseHelper;
import com.kainat.app.android.helper.MediaLikeUserTable;
import com.kainat.app.android.helper.MediaPostTable;
import com.kainat.app.android.helper.MediaTable;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.SettingTable;
import com.kainat.app.android.util.Logger;

public class MediaContentProvider extends ContentProvider {
	private LandingPageDatabaseHelper database;

	private static final int MEDIA = 10;  
	private static final int MEDIA_ID = 11;
	private static final int FEATUREDUSER = 12;  
	private static final int FEATUREDUSER_ID = 13;
	private static final int GROUPEVENT = 14;  
	private static final int GROUPEVENT_ID = 15;
	private static final int COMMUNITY = 16;  
	private static final int COMMUNITY_ID = 17;
	private static final int MEDIA_COMMENT = 18;  
	private static final int MEDIA_COMMENT_ID = 19;
	private static final int INBOX_MESSAGE = 20;  
	private static final int INBOX_MESSAGE_ID = 21;
	private static final int INBOX_CONVERSATION_LIST = 22;  
	private static final int INBOX_CONVERSATION_LIST_ID = 23;
	private static final int INBOX_COMPOSE = 24;  
	private static final int INBOX_COMPOSE_ID = 25;
	private static final int BOOKMARK = 26;  
	private static final int BOOKMARK_ID = 27;
	private static final int SETTING = 28;  
	private static final int SETTING_ID = 29;

	private static final int LIKES = 30;  
	private static final int LIKES_ID = 31;

	private static final int CHANNEL_CONVERSATION_LIST = 32;  
	private static final int CHANNEL_CONVERSATION_LIST_ID = 33;

	private static final int CHANNEL_COUNTER_LIST = 34;  
	private static final int CHANNEL_COUNTER_LIST_ID = 35;
	
	private static final int CHANNEL_TIMELINE_LIST = 36;  
	private static final int CHANNEL_TIMELINE_LIST_ID = 37;
	
	private static final String PROVIDER_NAME = "com.kainat.app.android.helper.db.media";
	private static final String BASE_PATH_MEDIA = "media";
	private static final String BASE_PATH_FEATUREDUSER = "featureduser";
	private static final String BASE_PATH_GROUPEVENT = "groupevent";
	private static final String BASE_PATH_COMMUNITY = "community";
	private static final String BASE_PATH_MEDIA_COMMENT = "mediacomment";
	private static final String BASE_PATH_INBOX_MESSAGE = "inboxmessage";
	private static final String BASE_PATH_INBOX_CONVERSATION_LIST = "inbox_conversation_list";
	private static final String BASE_PATH_CHANNEL_CONVERSATION_LIST ="MANOJ";
	private static final String BASE_PATH_CHANNEL_TIMELINE_LIST ="channel_timeline_list";
	private static final String BASE_PATH_CHANNEL_COUNTER_LIST ="counter_conversation_list";
	
	private static final String BASE_PATH_INBOX_COMPOSE = "inbox_compose";
	private static final String BASE_PATH_BOOKMARK = "bookmark";
	private static final String BASE_PATH_SETTING = "setting";
	private static final String BASE_PATH_LIKES = "likes";

	public static final Uri CONTENT_URI_MEDIA = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_MEDIA);

	public static final Uri CONTENT_URI_FEATUREDUSER = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_FEATUREDUSER);

	public static final Uri CONTENT_URI_GROUPEVENT = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_GROUPEVENT);
	public static final Uri CONTENT_URI_COMMUNITY = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_COMMUNITY);
	public static final Uri CONTENT_URI_MEDIA_COMMENT = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_MEDIA_COMMENT);
	public static final Uri CONTENT_URI_INBOX = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_INBOX_MESSAGE);
	public static final Uri CONTENT_URI_INBOX_CONVERSATION_LIST = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_INBOX_CONVERSATION_LIST);
	public static final Uri CONTENT_URI_INBOX_COMPOSE = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_INBOX_COMPOSE);
	public static final Uri CONTENT_URI_BOOKMARK = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_BOOKMARK);
	public static final Uri CONTENT_URI_SETTING = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_SETTING);
	public static final Uri CONTENT_URI_LIKES = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_LIKES);
	//DATE - 22/6/2015
	public static final Uri CONTENT_URI_CHANNEL_CONVERSATION_LIST = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_CHANNEL_CONVERSATION_LIST);
	//DATE 05-05-2016
	public static final Uri CONTENT_URI_CHANNEL_TIMELINE_LIST = Uri.parse("content://" + PROVIDER_NAME
			+ "/" + BASE_PATH_CHANNEL_TIMELINE_LIST);
	
	//DATE - 24/08/2015
		public static final Uri CONTENT_URI_CHANNEL_COUNTER_LIST = Uri.parse("content://" + PROVIDER_NAME
				+ "/" + BASE_PATH_CHANNEL_COUNTER_LIST);

	public static final String MEDIA_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/media";
	public static final String MEDIA_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/media";
	public static final String EVENTUSER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/groupevent";
	public static final String EVENTUSER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/groupevent";

	public static final String FEATUREDUSER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/featureduser";
	public static final String FEATUREDUSER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/featureduser";

	public static final String COMMUNITY_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/community";
	public static final String COMMUNITY_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/community";

	public static final String MEDIA_COMMENT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/mediacomment";
	public static final String MEDIA_COMMENT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/mediacomment";

	public static final String INBOX_COMMENT_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/inboxmessage";
	public static final String INBOX_COMMENT_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/inboxmessage";

	public static final String INBOX_CONVERSATION_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/inbox_conversation_list";
	public static final String INBOX_CONVERSATION_LIST_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/inbox_conversation_list";

	public static final String INBOX_COMPOSE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/inbox_conversation_list";
	public static final String INBOX_COMPOSE_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/inbox_conversation_list";

	public static final String BOOKMARK_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/bookamrk";
	public static final String BOOKMARK_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/bookamrk";

	public static final String SETTING_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/currentchat";
	public static final String SETTING_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/currentchat";

	public static final String LIKES_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/medialikes";
	public static final String LIKES_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/medialikes";


	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_MEDIA, MEDIA);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_MEDIA+ "/#", MEDIA_ID);


		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_GROUPEVENT, GROUPEVENT);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_GROUPEVENT+ "/#", GROUPEVENT_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_FEATUREDUSER, FEATUREDUSER);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_FEATUREDUSER + "/#", FEATUREDUSER_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_COMMUNITY, COMMUNITY);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_COMMUNITY + "/#", COMMUNITY_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_MEDIA_COMMENT, MEDIA_COMMENT);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_MEDIA_COMMENT + "/#", MEDIA_COMMENT_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_INBOX_MESSAGE, INBOX_MESSAGE);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_INBOX_MESSAGE + "/#", INBOX_MESSAGE_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_INBOX_CONVERSATION_LIST, INBOX_CONVERSATION_LIST);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_INBOX_CONVERSATION_LIST + "/#", INBOX_CONVERSATION_LIST_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_INBOX_COMPOSE, INBOX_COMPOSE);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_INBOX_COMPOSE + "/#", INBOX_COMPOSE_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_BOOKMARK, BOOKMARK);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_BOOKMARK + "/#", BOOKMARK_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_SETTING, SETTING);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_SETTING + "/#", SETTING_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_LIKES, LIKES);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_LIKES + "/#", LIKES_ID);

		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_CHANNEL_CONVERSATION_LIST, CHANNEL_CONVERSATION_LIST);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_CHANNEL_CONVERSATION_LIST + "/#", CHANNEL_CONVERSATION_LIST_ID);
		
		//DATE :24-08-2015
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_CHANNEL_COUNTER_LIST, CHANNEL_COUNTER_LIST);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_CHANNEL_COUNTER_LIST + "/#", CHANNEL_COUNTER_LIST_ID);
		
		//Date -05-05-2016
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_CHANNEL_TIMELINE_LIST, CHANNEL_TIMELINE_LIST);
		sURIMatcher.addURI(PROVIDER_NAME, BASE_PATH_CHANNEL_TIMELINE_LIST + "/#", CHANNEL_TIMELINE_LIST_ID);
	}

	@Override
	public boolean onCreate() {
		database = new LandingPageDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		try{
			SQLiteQueryBuilder queryBuilder=null;
			//    = new SQLiteQueryBuilder();
			//    queryBuilder.setTables(MediaTable.TABLE_MEDIA);

			int uriType = sURIMatcher.match(uri);
			//    System.out.println("-------------------downloadContentProvider -uriType-------------------"+uriType);
			//    System.out.println("-------------------downloadContentProvider -uri-------------------"+uri.toString());
			switch (uriType) {
			case MEDIA:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MediaTable.TABLE_MEDIA);
				break;
			case MEDIA_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MediaTable.TABLE_MEDIA);
				queryBuilder.appendWhere(MediaTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case FEATUREDUSER:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(FeatureUserTable.TABLE);
				break;
			case FEATUREDUSER_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(FeatureUserTable.TABLE);
				queryBuilder.appendWhere(FeatureUserTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case GROUPEVENT:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(GroupEventTable.TABLE);
				break;
			case GROUPEVENT_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(GroupEventTable.TABLE);
				queryBuilder.appendWhere(GroupEventTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case COMMUNITY:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityTable.TABLE);
				break;
			case COMMUNITY_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityTable.TABLE);
				queryBuilder.appendWhere(CommunityTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case MEDIA_COMMENT:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommentTable.TABLE);
				break;
			case MEDIA_COMMENT_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommentTable.TABLE);
				queryBuilder.appendWhere(CommentTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;

			case INBOX_MESSAGE:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MessageTable.TABLE);
				break;
			case INBOX_MESSAGE_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MessageTable.TABLE);
				queryBuilder.appendWhere(MessageTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case INBOX_CONVERSATION_LIST:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MessageConversationTable.TABLE);
				break;
			case INBOX_CONVERSATION_LIST_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MessageConversationTable.TABLE);
				queryBuilder.appendWhere(MessageConversationTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;

				//DATE 22-06-2015
				//START
			case CHANNEL_CONVERSATION_LIST:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityMessageTable.TABLE);
				break;
			case CHANNEL_CONVERSATION_LIST_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityMessageTable.TABLE);
				queryBuilder.appendWhere(CommunityMessageTable.MSG_ID + "="
						+ uri.getLastPathSegment());
				break;
			case CHANNEL_TIMELINE_LIST:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityTimlineTable.TABLE);
				break;
			case CHANNEL_TIMELINE_LIST_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityTimlineTable.TABLE);
				queryBuilder.appendWhere(CommunityTimlineTable.CHANNEL_POST_ID + "="
						+ uri.getLastPathSegment());
				break;
				//END 22-06-2015
				
				//DATE 24-08-2015
				//START
			case CHANNEL_COUNTER_LIST:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityCounterTable.TABLE);
				break;
			case CHANNEL_COUNTER_LIST_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(CommunityCounterTable.TABLE);
				queryBuilder.appendWhere(CommunityCounterTable.GROUPID + "="
						+ uri.getLastPathSegment());
				break;

				//END 24-08-2015

			case INBOX_COMPOSE:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MediaPostTable.TABLE);
				break;
			case INBOX_COMPOSE_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MediaPostTable.TABLE);
				queryBuilder.appendWhere(MediaPostTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			case BOOKMARK:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MessageTable.BOOKMARK_TABLE);
				break;
			case BOOKMARK_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MessageTable.BOOKMARK_TABLE);
				queryBuilder.appendWhere(MessageTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;

			case SETTING:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(SettingTable.TABLE);
				break;
			case SETTING_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(SettingTable.TABLE);
				queryBuilder.appendWhere(MessageTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;

			case LIKES:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MediaLikeUserTable.TABLE);
				break;
			case LIKES_ID:
				queryBuilder = new SQLiteQueryBuilder();
				queryBuilder.setTables(MediaLikeUserTable.TABLE);
				queryBuilder.appendWhere(MediaLikeUserTable.COLUMN_ID + "="
						+ uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
			}
			//    System.out.println("----------queryBuilder----- : "+queryBuilder.getTables());
			//    if(cursor!=null){
			//    	System.out.println("---------------closing cursor : ");
			//    	cursor.close() ;
			//    }

			SQLiteDatabase db = database.getWritableDatabase();
			cursor = queryBuilder.query(db, projection, selection,
					selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			//    db.close();
			return cursor;
		}catch (Exception e) {
			Logger.conversationLog("MediaContentProvider query error : ", e.toString());
			Logger.conversationLog("MediaContentProvider query error : ", uri.toString());
			//query( uri,  projection,  selection,selectionArgs,  sortOrder);
		}
		return null;
	}
	Cursor cursor ;
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		long id = 0;

		switch (uriType) {
		case MEDIA:
			id = sqlDB.insert(MediaTable.TABLE_MEDIA, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_MEDIA + "/" + id);     
		case MEDIA_ID:
			id = sqlDB.insert(MediaTable.TABLE_MEDIA, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_MEDIA + "/" + id);
		case FEATUREDUSER:
			id = sqlDB.insert(FeatureUserTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_FEATUREDUSER + "/" + id);
		case FEATUREDUSER_ID:
			id = sqlDB.insert(FeatureUserTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_FEATUREDUSER + "/" + id);
		case GROUPEVENT:
			id = sqlDB.insert(GroupEventTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_FEATUREDUSER + "/" + id);
		case GROUPEVENT_ID:
			id = sqlDB.insert(GroupEventTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_FEATUREDUSER + "/" + id);
		case COMMUNITY:
			id = sqlDB.insert(CommunityTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
//			Log.e("Community Table ", "insertion  ==> Groupname : "+values.get(CommunityTable.GROUP_NAME)+", groupID : "+values.get(CommunityTable.GROUPID));
			return Uri.parse(BASE_PATH_COMMUNITY + "/" + id);
		case COMMUNITY_ID:
			id = sqlDB.insert(CommunityTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_COMMUNITY + "/" + id);
		case MEDIA_COMMENT:
			id = sqlDB.insert(CommentTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_MEDIA_COMMENT + "/" + id);
		case MEDIA_COMMENT_ID:
			id = sqlDB.insert(CommentTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_MEDIA_COMMENT + "/" + id);

		case INBOX_MESSAGE:
			id = sqlDB.insert(MessageTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_INBOX_MESSAGE + "/" + id);
		case INBOX_MESSAGE_ID:
			id = sqlDB.insert(MessageTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_INBOX_MESSAGE + "/" + id);

		case INBOX_CONVERSATION_LIST:
			id = sqlDB.insert(MessageConversationTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_INBOX_MESSAGE + "/" + id);
		case INBOX_CONVERSATION_LIST_ID:
			id = sqlDB.insert(MessageConversationTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_INBOX_MESSAGE + "/" + id);

		case INBOX_COMPOSE:
			id = sqlDB.insert(MediaPostTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_INBOX_MESSAGE + "/" + id);
		case INBOX_COMPOSE_ID:
			id = sqlDB.insert(MediaPostTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_INBOX_MESSAGE + "/" + id);
		case BOOKMARK:
			id = sqlDB.insert(MessageTable.BOOKMARK_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_BOOKMARK+ "/" + id);
		case BOOKMARK_ID:
			id = sqlDB.insert(MessageTable.BOOKMARK_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_BOOKMARK + "/" + id);

		case SETTING:
			id = sqlDB.insert(SettingTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_SETTING+ "/" + id);
		case SETTING_ID:
			id = sqlDB.insert(SettingTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_SETTING + "/" + id);

		case LIKES:
			id = sqlDB.insert(MediaLikeUserTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_LIKES+ "/" + id);
		case LIKES_ID:
			id = sqlDB.insert(MediaLikeUserTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_LIKES + "/" + id);

		case CHANNEL_CONVERSATION_LIST:
			id = sqlDB.insert(CommunityMessageTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_CHANNEL_CONVERSATION_LIST+ "/" + id);
		case CHANNEL_CONVERSATION_LIST_ID:
			id = sqlDB.insert(CommunityMessageTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_CHANNEL_CONVERSATION_LIST + "/" + id);
		case CHANNEL_TIMELINE_LIST:
			id = sqlDB.insert(CommunityTimlineTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_CHANNEL_TIMELINE_LIST + "/" + id);
		case CHANNEL_TIMELINE_LIST_ID:
			id = sqlDB.insert(CommunityTimlineTable.TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(BASE_PATH_CHANNEL_TIMELINE_LIST + "/" + id);
			

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		//    System.out.println("-----------uriType---"+uriType);
		switch (uriType) {
		case MEDIA:
			rowsDeleted = sqlDB.delete(MediaTable.TABLE_MEDIA, selection,
					selectionArgs);
			break;
		case MEDIA_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MediaTable.TABLE_MEDIA,
						MediaTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MediaTable.TABLE_MEDIA,
						MediaTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
		case FEATUREDUSER:
			rowsDeleted = sqlDB.delete(FeatureUserTable.TABLE, selection,
					selectionArgs);
			break;
		case FEATUREDUSER_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(FeatureUserTable.TABLE,
						FeatureUserTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(FeatureUserTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case GROUPEVENT:
			rowsDeleted = sqlDB.delete(GroupEventTable.TABLE, selection,
					selectionArgs);
			break;
		case GROUPEVENT_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(GroupEventTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(GroupEventTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case COMMUNITY:
			/*rowsDeleted = sqlDB.delete(CommunityTable.TABLE, selection,
                selectionArgs);*/
			rowsDeleted = sqlDB.delete(CommunityTable.TABLE, null,
					null);
			Log.e("Community Table ", "deleted number "+rowsDeleted);
			break;
		case COMMUNITY_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(CommunityTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(CommunityTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case MEDIA_COMMENT:
			rowsDeleted = sqlDB.delete(CommentTable.TABLE, selection,
					selectionArgs);
			break;
		case MEDIA_COMMENT_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(CommentTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(CommentTable.TABLE,
						MediaTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case INBOX_MESSAGE:
			rowsDeleted = sqlDB.delete(MessageTable.TABLE, selection,
					selectionArgs);
			break;
		case INBOX_MESSAGE_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MessageTable.TABLE,
						MessageTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MessageTable.TABLE,
						MessageTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;

		case INBOX_CONVERSATION_LIST:
			rowsDeleted = sqlDB.delete(MessageConversationTable.TABLE, selection,
					selectionArgs);
			break;
		case INBOX_CONVERSATION_LIST_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MessageConversationTable.TABLE,
						MessageTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MessageConversationTable.TABLE,
						MessageTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;

		case INBOX_COMPOSE:
			rowsDeleted = sqlDB.delete(MediaPostTable.TABLE, selection,
					selectionArgs);
			break;
		case INBOX_COMPOSE_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MediaPostTable.TABLE,
						MessageTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MediaPostTable.TABLE,
						MessageTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;

		case BOOKMARK:
			rowsDeleted = sqlDB.delete(MessageTable.BOOKMARK_TABLE, selection,
					selectionArgs);
			break;
		case BOOKMARK_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MessageTable.BOOKMARK_TABLE,
						MessageTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MessageTable.BOOKMARK_TABLE,
						MessageTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case SETTING:
			rowsDeleted = sqlDB.delete(SettingTable.TABLE, selection,
					selectionArgs);
			break;
		case SETTING_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(SettingTable.TABLE,
						SettingTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(SettingTable.TABLE,
						SettingTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case LIKES:
			rowsDeleted = sqlDB.delete(MediaLikeUserTable.TABLE, selection,
					selectionArgs);
			break;
		case LIKES_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(MediaLikeUserTable.TABLE,
						MediaLikeUserTable.COLUMN_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(MediaLikeUserTable.TABLE,
						MediaLikeUserTable.COLUMN_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
			
		case CHANNEL_CONVERSATION_LIST:
			rowsDeleted = sqlDB.delete(CommunityMessageTable.TABLE, selection,
					selectionArgs);
			break;
		case CHANNEL_CONVERSATION_LIST_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(CommunityMessageTable.TABLE,
						CommunityMessageTable.MSG_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(CommunityMessageTable.TABLE,
						CommunityMessageTable.MSG_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case CHANNEL_TIMELINE_LIST:
			rowsDeleted = sqlDB.delete(CommunityTimlineTable.TABLE, selection,
					selectionArgs);
			break;
		case CHANNEL_TIMELINE_LIST_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(CommunityTimlineTable.TABLE,
						CommunityTimlineTable.CHANNEL_POST_ID + "=" + id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(CommunityTimlineTable.TABLE,
						CommunityTimlineTable.CHANNEL_POST_ID + "=" + id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
			

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		try{


			int uriType = sURIMatcher.match(uri);
			SQLiteDatabase sqlDB = database.getWritableDatabase();
			int rowsUpdated = 0;
			switch (uriType) {
			case MEDIA:
				rowsUpdated = sqlDB.update(MediaTable.TABLE_MEDIA, 
						values, 
						selection,
						selectionArgs);
				break;
			case MEDIA_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(MediaTable.TABLE_MEDIA, 
							values,
							MediaTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(MediaTable.TABLE_MEDIA, 
							values,
							MediaTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			case FEATUREDUSER:
				rowsUpdated = sqlDB.update(FeatureUserTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case FEATUREDUSER_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(FeatureUserTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(FeatureUserTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			case GROUPEVENT:
				rowsUpdated = sqlDB.update(FeatureUserTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case GROUPEVENT_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(GroupEventTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(GroupEventTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			case COMMUNITY:
				rowsUpdated = sqlDB.update(CommunityTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				Log.e("Community Table ", "Update  ==> Groupname : "+values.get(CommunityTable.GROUP_NAME)+", groupID : "+values.get(CommunityTable.GROUPID)
						+", Members : "+values.get(CommunityTable.NUMBER_OF_MEMBERS));
				break;
			case COMMUNITY_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(CommunityTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(CommunityTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			case MEDIA_COMMENT:
				rowsUpdated = sqlDB.update(CommentTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case MEDIA_COMMENT_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(CommentTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(CommentTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;

			case INBOX_MESSAGE:
				rowsUpdated = sqlDB.update(MessageTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case INBOX_MESSAGE_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(MessageTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(MessageTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;

			case INBOX_CONVERSATION_LIST:
				rowsUpdated = sqlDB.update(MessageConversationTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case INBOX_CONVERSATION_LIST_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(MessageConversationTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(MessageConversationTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;

			case INBOX_COMPOSE:
				rowsUpdated = sqlDB.update(MediaPostTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case INBOX_COMPOSE_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(MediaPostTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(MediaPostTable.TABLE, 
							values,
							FeatureUserTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			case BOOKMARK:
				rowsUpdated = sqlDB.update(MessageTable.BOOKMARK_TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case BOOKMARK_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(MessageTable.BOOKMARK_TABLE, 
							values,
							MessageTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(MessageTable.BOOKMARK_TABLE, 
							values,
							MessageTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
			case SETTING:
				rowsUpdated = sqlDB.update(SettingTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case SETTING_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(SettingTable.TABLE, 
							values,
							SettingTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(SettingTable.TABLE, 
							values,
							SettingTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;

			case LIKES:
				rowsUpdated = sqlDB.update(MediaLikeUserTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case LIKES_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(MediaLikeUserTable.TABLE, 
							values,
							SettingTable.COLUMN_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(MediaLikeUserTable.TABLE, 
							values,
							SettingTable.COLUMN_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
				
			case CHANNEL_CONVERSATION_LIST:
				rowsUpdated = sqlDB.update(CommunityMessageTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case CHANNEL_CONVERSATION_LIST_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(CommunityMessageTable.TABLE, 
							values,
							CommunityMessageTable.MSG_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(CommunityMessageTable.TABLE, 
							values,
							CommunityMessageTable.MSG_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
				
			case CHANNEL_TIMELINE_LIST:
				rowsUpdated = sqlDB.update(CommunityTimlineTable.TABLE, 
						values, 
						selection,
						selectionArgs);
				break;
			case CHANNEL_TIMELINE_LIST_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = sqlDB.update(CommunityTimlineTable.TABLE, 
							values,
							CommunityTimlineTable.CHANNEL_POST_ID + "=" + id, 
							null);
				} else {
					rowsUpdated = sqlDB.update(CommunityMessageTable.TABLE, 
							values,
							CommunityTimlineTable.CHANNEL_POST_ID + "=" + id 
							+ " and " 
							+ selection,
							selectionArgs);
				}
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
			}
			getContext().getContentResolver().notifyChange(uri, null);
			return rowsUpdated;
		}catch (Exception e) {
			//		  Logger.conversationLog("MediaContentProvider update error : ", e.toString());
			//		  Logger.conversationLog("MediaContentProvider update error : ", uri.toString());
			//		  update(uri, values, selection, selectionArgs);
			e.printStackTrace();
		}
		return -1;
	}

	//  private void checkColumns(String[] projection) {
	//    String[] available = { PlayListTable.COLUMN_CATEGORY,
	//        TodoTable.COLUMN_SUMMARY, TodoTable.COLUMN_DESCRIPTION,
	//        TodoTable.COLUMN_ID };
	//    if (projection != null) {
	//      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
	//      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
	//      // Check if all columns which are requested are available
	//      if (!availableColumns.containsAll(requestedColumns)) {
	//        throw new IllegalArgumentException("Unknown columns in projection");
	//      }
	//    }
	//  }

} 