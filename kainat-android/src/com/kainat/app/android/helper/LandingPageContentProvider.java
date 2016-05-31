package com.kainat.app.android.helper;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.kainat.app.android.util.Logger;

public class LandingPageContentProvider extends ContentProvider {
  private LandingPageDatabaseHelper database;

  private static final int LANDAING_PAGE = 10;
  
  private static final int LANDAING_PAGE_ID = 20;

  private static final String AUTHORITY = "com.kainat.app.android.helper";

  private static final String BASE_PATH = "landingpage";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
      + "/" + BASE_PATH);

  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
      + "/landingpage";
  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
      + "/landingpage";

  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  static {
    sURIMatcher.addURI(AUTHORITY, BASE_PATH, LANDAING_PAGE);
    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", LANDAING_PAGE_ID);
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
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    queryBuilder.setTables(LandingPageTable.TABLE_LANDING_PAGE);

    int uriType = sURIMatcher.match(uri);
//    System.out.println("-------------------downloadContentProvider -uriType-------------------"+uriType);
    switch (uriType) {
    case LANDAING_PAGE:
      break;
    case LANDAING_PAGE_ID:
      // Adding the ID to the original query
      queryBuilder.appendWhere(LandingPageTable.COLUMN_ID + "="
          + uri.getLastPathSegment());
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    SQLiteDatabase db=null ;

     db = database.getWritableDatabase();

    Cursor cursor = queryBuilder.query(db, projection, selection,
        selectionArgs, null, null, sortOrder);
    // Make sure that potential listeners are getting notified
    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    return cursor;
	  }catch (Exception e) {
		  Logger.conversationLog("LandingPageContentProvider query error : ", e.toString());
		  Logger.conversationLog("LandingPageContentProvider query error : ", uri.toString());
		  query( uri,  projection,  selection,
			       selectionArgs,  sortOrder);
		}
	  return null;
  }

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
    case LANDAING_PAGE:
      id = sqlDB.insert(LandingPageTable.TABLE_LANDING_PAGE, null, values);
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return Uri.parse(BASE_PATH + "/" + id);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB=null;
    try{
    	sqlDB = database.getWritableDatabase();
    }catch (Exception e) {
		// TODO: handle exception
	}
    int rowsDeleted = 0;
    switch (uriType) {
    case LANDAING_PAGE:
    	try{
      rowsDeleted = sqlDB.delete(LandingPageTable.TABLE_LANDING_PAGE, selection,
          selectionArgs);
    	}catch (Exception e) {
			// TODO: handle exception
		}
      break;
    case LANDAING_PAGE_ID:
      String id = uri.getLastPathSegment();
      if (TextUtils.isEmpty(selection)) {
        rowsDeleted = sqlDB.delete(LandingPageTable.TABLE_LANDING_PAGE,
        		LandingPageTable.COLUMN_ID + "=" + id, 
            null);
      } else {
        rowsDeleted = sqlDB.delete(LandingPageTable.TABLE_LANDING_PAGE,
        		LandingPageTable.COLUMN_ID + "=" + id 
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

    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsUpdated = 0;
    switch (uriType) {
    case LANDAING_PAGE:
      rowsUpdated = sqlDB.update(LandingPageTable.TABLE_LANDING_PAGE, 
          values, 
          selection,
          selectionArgs);
      break;
    case LANDAING_PAGE_ID:
      String id = uri.getLastPathSegment();
      if (TextUtils.isEmpty(selection)) {
        rowsUpdated = sqlDB.update(LandingPageTable.TABLE_LANDING_PAGE, 
            values,
            LandingPageTable.COLUMN_ID + "=" + id, 
            null);
      } else {
        rowsUpdated = sqlDB.update(LandingPageTable.TABLE_LANDING_PAGE, 
            values,
            LandingPageTable.COLUMN_ID + "=" + id 
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