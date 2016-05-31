package com.kainat.app.android.helper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kainat.app.android.util.Constants;

public class LandingPageDatabaseHelper extends SQLiteOpenHelper {
//  private static final String DATABASE_NAME = "kainat123";////"NAGEDNRAROCKETALK090";//+System.currentTimeMillis()+".db";//"nagenerss9gffghghj9899898s3.db";
  private static final int DATABASE_VERSION = 2;

  public LandingPageDatabaseHelper(Context context) {
    super(context, Constants.DATABASE_NAME_2, null, DATABASE_VERSION);
  }
  @Override
  public void onCreate(SQLiteDatabase database) {
    LandingPageTable.onCreate(database);
    MediaTable.onCreate(database);
//    FeatureUserTable.onCreate(database);
//    GroupEventTable.onCreate(database) ;
    CommunityTable.onCreate(database) ;
    CommentTable.onCreate(database) ;
    MessageTable.onCreate(database);
    MessageConversationTable.onCreate(database);
    CommunityCounterTable.onCreate(database);
    MediaPostTable.onCreate(database);
    SettingTable.onCreate(database);
//    MediaLikeUserTable.onCreate(database);
    CommunityMessageTable.onCreate(database);
    CommunityTimlineTable.onCreate(database);
  }
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
	  LandingPageTable.onUpgrade(database, oldVersion, newVersion);
	  CommunityTable.onUpgrade(database, oldVersion, newVersion);
  }
}	