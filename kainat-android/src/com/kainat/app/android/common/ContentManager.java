package com.kainat.app.android.common;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;

public class ContentManager
{
  private static String[] autdiocols;
  private static ContentManager instance = new ContentManager();
  private static final String[] sAcceptableImageTypes;
  private static Uri sMediaStorageURI;
  private static String sMediaWhereClause;
  private static Uri sStorageURI;
  private static final String sWhereClause = "(mime_type=? or mime_type=?)";

  static
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = "image/jpeg";
    arrayOfString[1] = "image/png";
    sAcceptableImageTypes = arrayOfString;
    sStorageURI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    sMediaStorageURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    arrayOfString = new String[2];
    arrayOfString[0] = "_id";
    arrayOfString[1] = "_data";
    autdiocols = arrayOfString;
    sMediaWhereClause = "(mime_type=? or mime_type=?)";
  }

  private Uri addImageContent(ContentResolver paramContentResolver, String paramString1, String paramString2, long paramLong, Location paramLocation, int paramInt)
  {
    Object localObject = getImageCursor(paramContentResolver, paramString1);
    if ((localObject != null) && (((Cursor)localObject).getCount() > 0))
    {
      ((Cursor)localObject).close();
      localObject = null;
//      return localObject;
    }
    localObject = PathUtils.getFileName(paramString1);
    ContentValues localContentValues = new ContentValues(9);
    localContentValues.put("title", (String)localObject);
    localContentValues.put("_display_name", (String)localObject);
    localContentValues.put("description", paramString2);
    localContentValues.put("datetaken", Long.valueOf(paramLong));
    localContentValues.put("mime_type", "image/jpeg");
    if (paramString1.endsWith(".png"))
    {
      localContentValues.put("mime_type", "image/png");
      localContentValues.put("orientation", Integer.valueOf(paramInt));
      localContentValues.put("bucket_id", Integer.valueOf(getBucketId(paramString1)));
      localContentValues.put("bucket_display_name", PathUtils.getFilePath(paramString1).toLowerCase());
      if (paramLocation != null)
      {
        localContentValues.put("latitude", Double.valueOf(paramLocation.getLatitude()));
        localContentValues.put("longitude", Double.valueOf(paramLocation.getLongitude()));
      }
      localContentValues.put("_data", paramString1);
    }
    try
    {
      while (true)
      {
        localObject = paramContentResolver.insert(sStorageURI, localContentValues);
        localObject = localObject;
      }
//      localContentValues.put("mime_type", "image/jpeg");
    }
    catch (Exception localException)
    {
      while (true)
        localObject = null;
    }
  }

  private int getBucketId(String paramString)
  {
    int i;
    if (paramString != null)
      i = PathUtils.getFilePath(paramString).toLowerCase().hashCode();
    else
      i = 0;
    return i;
  }

  private Cursor getImageCursor(ContentResolver paramContentResolver, String paramString)
  {
	  Cursor localObject2 = null;
    try
    {
      Uri localUri = sStorageURI;
      String localObject1[] = new String[2];
      localObject1[0] = "_id";
      localObject1[1] = "bucket_id";
      localObject2 = MediaStore.Images.Media.query(paramContentResolver, localUri, localObject1, "(mime_type=? or mime_type=?) and _data='" + paramString + "'", sAcceptableImageTypes, "_id ASC");
//      localObject2 = localObject1;
      return localObject2;
    }
    catch (SQLiteException localSQLiteException)
    {
    	return localObject2;
    }
  }

  public static ContentManager instance()
  {
    return instance;
  }

  private Cursor queryAudio(Context paramContext, String paramString)
  {
    Object localObject;
    try
    {
      localObject = paramContext.getContentResolver();
      if (localObject == null)
      {
        localObject = null;
        return null;
      }
      localObject = ((ContentResolver)localObject).query(sMediaStorageURI, autdiocols, sMediaWhereClause + " and " + "_data" + "='" + paramString + "'", null, null);
      localObject = localObject;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException)
    {
      localObject = null;
    }
     return ((Cursor)localObject);
  }

  public void addImageContent(ContentResolver paramContentResolver, String paramString)
  {
    if ((paramString != null) && (!(paramString.contains("/."))))
      addImageContent(paramContentResolver, paramString, "", System.currentTimeMillis(), null, 0);
  }

  public Uri addImageListContent(ContentResolver paramContentResolver, String[] paramArrayOfString, String paramString)
  {
    Uri localUri;
    if ((paramString != null) && (paramArrayOfString != null) && (paramArrayOfString.length != 0) && (!(paramString.contains("/."))))
    {
      localUri = null;
      for (int i = 0; i < paramArrayOfString.length; ++i)
      {
        String str = paramArrayOfString[i];
        if ((!(str.contains("/."))) && (((str.endsWith(".png")) || (str.endsWith(".jpg")) || (str.endsWith(".jpeg")))))
        {
          Cursor localCursor = getImageCursor(paramContentResolver, str);
          if ((localCursor == null) || (localCursor.getCount() <= 0))
            if (!(paramString.equals(str)))
            {
              addImageContent(paramContentResolver, str, "", System.currentTimeMillis(), null, 0);
            }
            else
            {
              localUri = addImageContent(paramContentResolver, str, "", System.currentTimeMillis(), null, 0);
              if (localUri != null)
                localUri = localUri.buildUpon().appendQueryParameter("bucketId", String.valueOf(getBucketId(str))).build();
            }
          else
            localCursor.close();
        }
      }
      localUri = localUri;
    }
    else
    {
      localUri = null;
    }
    return localUri;
  }

  public void deleteImageContent(ContentResolver paramContentResolver, String paramString)
  {
    Cursor localCursor = getImageCursor(paramContentResolver, paramString);
    if ((localCursor != null) && (localCursor.moveToFirst()))
    {
      paramContentResolver.delete(ContentUris.withAppendedId(sStorageURI, localCursor.getLong(0)), null, null);
      localCursor.close();
    }
  }

  public Uri getAudioUri(Context paramContext, String paramString)
  {
    Uri localUri = null;
    Cursor localCursor = queryAudio(paramContext, paramString);
    if ((localCursor != null) && (localCursor.moveToFirst()))
    {
      localUri = ContentUris.withAppendedId(sMediaStorageURI, localCursor.getLong(0));
      localCursor.close();
    }
    return localUri;
  }

  public Uri getImageURI(ContentResolver paramContentResolver, String paramString)
  {
    Uri localUri = null;
    Cursor localCursor = getImageCursor(paramContentResolver, paramString);
    if ((localCursor != null) && (localCursor.moveToFirst()))
    {
      localUri = ContentUris.withAppendedId(sStorageURI, localCursor.getLong(0)).buildUpon().appendQueryParameter("bucketId", ""+localCursor.getInt(1)).build();
      localCursor.close();
    }
    return localUri;
  }
}