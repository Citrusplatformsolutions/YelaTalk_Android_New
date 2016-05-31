package com.kainat.app.android.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.kainat.app.android.util.Utilities;

public class ThumbnailFactory
{
  private static final int CACHED_MAX_SIZE = 1000;
  private static final int MINI_THUMB_TARGET_SIZE = 48;
  private static final int THUMBNAIL_TARGET_SIZE = 64;
  private static ThumbnailFactory instance;
  private String appBasePath = null;
  private String basePath = null;
  private Hashtable<String, Drawable> iconTable;
  private Context mContext;
//  private IService service;

  protected ThumbnailFactory(Context paramContextWrapper)
  {
    this.mContext = paramContextWrapper;
//    initThumbBasePath();
//    initAppBasePath();
//    this.iconTable = new Hashtable();
//    this.service = ResourceAccessService.getServiceInstance(paramContextWrapper);
  }

  private static int computeSampleSize(BitmapFactory.Options paramOptions, int paramInt)
  {
    int j = 0 ;
    int k = paramOptions.outWidth;
    int l = paramOptions.outHeight;
    int i = Math.max(k / paramInt, l / paramInt);
    if (i > 1)
    {
      if ((i > 1) && (k > paramInt) && (k / i < paramInt))
        --i;
      if ((i > 1) && (l > paramInt) && (l / i < paramInt))
        --i;
      if (i > 1)
        i = i;
      else
        j = 1;
    }
    else
    {
      j = 1;
    }
    return j;
  }
//  Bitmap localBitmap;
  private Bitmap decodeBitmap(String paramString, BitmapFactory.Options paramOptions)
  {
    InputStream localInputStream = Utilities.getFileInputStream(paramString);
   /* if (localInputStream == null)
    {
      localBitmap = null;
      return localBitmap;
    }*/
    Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream, null, paramOptions);
    if (localInputStream != null)
    try
    {
      localInputStream.close();
     
    }
    catch (IOException localIOException)
    {
//      break label34:
    }
    return localBitmap;
  }

  private void decodeBounds(String paramString, BitmapFactory.Options paramOptions)
  {
    InputStream localInputStream = Utilities.getFileInputStream(paramString);
    if (localInputStream == null)
      return;
    BitmapFactory.decodeStream(localInputStream, null, paramOptions);
    if (localInputStream != null)
		try {
			localInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

  private String getAppIconPath(String paramString)
  {
    return this.appBasePath + "/" + paramString;
  }

  public static ThumbnailFactory getDefault(Context paramContextWrapper)
  {
    if (instance == null)
      instance = new ThumbnailFactory(paramContextWrapper);
    return instance;
  }

//  private InputStream getFileInputStream(String paramString)
//  {
//    return this.service.getFileInputStream(paramString);
//  }

  private String getThumbPath(String paramString)
  {
    return this.basePath + "/" + paramString.hashCode();
  }

  private void initAppBasePath()
  {
    this.appBasePath = this.mContext.getDir(".apps", 0).getAbsolutePath();
  }

  private void initThumbBasePath()
  {
    this.basePath = this.mContext.getDir(".thumbnails", 0).getAbsolutePath();
  }

  private void saveMiniThumbData(Bitmap paramBitmap, OutputStream paramOutputStream)
  {
    if (paramBitmap != null)
    {
      float f;
      if (paramBitmap.getWidth() >= paramBitmap.getHeight())
        f = 48.0F / paramBitmap.getHeight();
      else
        f = 48.0F / paramBitmap.getWidth();
      Matrix localMatrix = new Matrix();
      localMatrix.setScale(f, f);
      Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
      localBitmap.compress(Bitmap.CompressFormat.PNG, 75, paramOutputStream);
      localBitmap.recycle();
    }
  }

  private void setThumbViewIcon(String paramString, Drawable paramDrawable)
  {
    this.iconTable.size();
    this.iconTable.put(paramString, paramDrawable);
  }

  public void checkThumbnail(String paramString)
  {
    File localFile = new File(getThumbPath(paramString));
    if (!(localFile.exists()))
    {
      if (!(localFile.getParentFile().exists()))
        initThumbBasePath();
      generateThumbnail(paramString);
    }
  }

  public void generateThumbnail(String paramString)
  {
    Object localObject = getThumbPath(paramString);
    try
    {
      localObject = new FileOutputStream((String)localObject);
      Bitmap localBitmap = makeBitmap(64, paramString, null);
//      saveMiniThumbData(localBitmap, (OutputStream)localObject);
      ((FileOutputStream)localObject).close();
//      setThumbViewIcon(paramString, new BitmapDrawable(localBitmap));
      return;
    }
    catch (IOException localIOException)
    {
    }
   
  }
  
  Bitmap getThumb(String fileName){
	  byte[] imageData = null;

      try     
      {

          final int THUMBNAIL_SIZE = 64;

          FileInputStream fis = new FileInputStream(fileName);
          Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

          imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

          
          ByteArrayOutputStream baos = new ByteArrayOutputStream();  
          imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
          return imageBitmap;
//          imageData = baos.toByteArray();

      }
      catch(Exception ex) {
    	  return null;
      }
  }
  
  public Bitmap getByteArrayThumb(byte[] imageData){
//	  byte[] imageData = null;
	  ByteArrayOutputStream baos =null;
      try     
      {

          final int THUMBNAIL_SIZE = 64;

//          FileInputStream fis = new FileInputStream(fileName);
          Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);//codeStream(fis);

          imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

          baos = new ByteArrayOutputStream();  
          imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
          return imageBitmap;
//          imageData = baos.toByteArray();

      }
      catch(Exception ex) {
    	  return null;
      }finally{
    	  try {
    		  if(baos!=null)
    			  baos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
      }
  }
  public Bitmap getByteArrayThumb(byte[] imageData,int ratio){
//	  byte[] imageData = null;
	  ByteArrayOutputStream baos=null;
      try     
      {

          final int THUMBNAIL_SIZE = ratio;

//          FileInputStream fis = new FileInputStream(fileName);
          Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);//codeStream(fis);
          
          imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);

          baos = new ByteArrayOutputStream();  
          imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
          return imageBitmap;
//          imageData = baos.toByteArray();

      }
      catch(Exception ex) {
    	  return null;
      }finally{
    	  try {
    		  if(baos!=null)
    			  baos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
      }
  }
  Bitmap getPreview(String uri) {
	  
//	  Uri u = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), Long.parseLong(_imageUri.getLastPathSegment()), type, null);
	  
	    File image = new File(uri);

	    BitmapFactory.Options bounds = new BitmapFactory.Options();
	    bounds.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(image.getPath(), bounds);
	    if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
	        return null;

	    int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
	            : bounds.outWidth;

	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inSampleSize = originalSize / 50;
	    
	    return BitmapFactory.decodeFile(image.getPath(), opts);     
	}
  public Bitmap generateThumbnailRT(String paramString)
  {
	  if(1==1){
		  return getPreview(paramString);
//		  return;
	  }
//    Object localObject = getThumbPath(paramString);
    try
    {
//      localObject = new FileOutputStream((String)localObject);
      Bitmap localBitmap = makeBitmap(64, paramString, null);
//      saveMiniThumbData(localBitmap, (OutputStream)localObject);
//      ((FileOutputStream)localObject).close();
//      setThumbViewIcon(paramString, new BitmapDrawable(localBitmap));
      return localBitmap;
    }
    catch (Exception localIOException)
    {
    }
    return null ;
  }

  public Drawable lookupAppIcon(String paramString)
  {
    Object localObject=null;
    try{
    if (!(this.iconTable.containsKey(paramString)))
    {
      localObject = BitmapFactory.decodeFile(getAppIconPath(paramString));
      if (localObject != null)
      {
        localObject = new BitmapDrawable((Bitmap)localObject);
        setThumbViewIcon(paramString, (Drawable)localObject);
        localObject = localObject;
      }
      else
      {
        localObject = null;
      }
    }
    else
    {
      localObject = (Drawable)this.iconTable.get(paramString);
    }
    }catch (Exception e) {
		// TODO: handle exception
	}
    return ((Drawable)localObject);
  }

  public Drawable lookupThumbnail(String paramString)
  {
	  Object localObject=null;
    try{
    	
    if (!(this.iconTable.containsKey(paramString)))
    {
      localObject = BitmapFactory.decodeFile(getThumbPath(paramString));
      if (localObject != null)
      {
        localObject = new BitmapDrawable((Bitmap)localObject);
        setThumbViewIcon(paramString, (Drawable)localObject);
        localObject = localObject;
      }
      else
      {
        localObject = null;
      }
    }
    else
    {
      localObject = (Drawable)this.iconTable.get(paramString);
    }
    }catch (Exception e) {
		// TODO: handle exception
	}
    return ((Drawable)localObject);
  }

  protected Bitmap makeBitmap(int paramInt, String paramString, BitmapFactory.Options paramOptions)
  {
	  try{
    if (paramOptions == null)
      paramOptions = new BitmapFactory.Options();
    paramOptions.inSampleSize = 1;
    if (paramInt != -1)
    {
      paramOptions.inJustDecodeBounds = true;
//      decodeBounds(paramString, paramOptions);
      paramOptions.inSampleSize = computeSampleSize(paramOptions, paramInt);
      paramOptions.inJustDecodeBounds = false;
    }
	  }catch (Exception e) {
		// TODO: handle exception
	}
    return decodeBitmap(paramString, paramOptions);
  }

  public void saveAppIcon(String paramString, Bitmap paramBitmap)
  {
    /*Object localObject;
    if ((paramString == null) || (paramBitmap == null));
    do
    {
      return;
      localObject = new File(getAppIconPath(paramString));
    }
    while (((File)localObject).exists());
    if (!(((File)localObject).getParentFile().exists()))
      initAppBasePath();
    try
    {
      localObject = new FileOutputStream((File)localObject);
      Bitmap.createBitmap(paramBitmap).compress(Bitmap.CompressFormat.PNG, 75, (OutputStream)localObject);
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
    }*/
  }
}