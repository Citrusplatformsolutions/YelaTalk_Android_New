package com.kainat.app.android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import com.kainat.app.android.core.BusinessProxy;

public class ImageUtils {
	
	private static final int IMAGE_QUALITY =3 ;
	int width,height;
//	width=getWindowManager().getDefaultDisplay().getWidth();
	//height=getWindowManager().getDefaultDisplay().getHeight();
	private static boolean checkImageNeedCompression(String path) {
		try {
			ExifInterface exifJpeg = new ExifInterface(path);
			int width = exifJpeg.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
			int height = exifJpeg.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
			return (width > 800 || height > 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static Bitmap rotateImage(String path, Bitmap bm) {
	/*	int orientation = 1;
	try {
		ExifInterface exifJpeg = new ExifInterface(path);
		  orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

////			orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
		} catch (IOException e) {
			e.printStackTrace();
	}
	if (orientation != ExifInterface.ORIENTATION_NORMAL)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		Matrix matrix = new Matrix();
		if (orientation == ExifInterface.ORIENTATION_ROTATE_90) 
		{
			matrix.postRotate(90);
		} 
		else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
			matrix.postRotate(180);
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
			matrix.postRotate(270);
		}
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		}*/
			
		return bm;
	}

	private static int[] rotate(double angle,int[] pixels,int width,int height) {
	    final double radians = Math.toRadians(angle),
	    cos = Math.cos(radians), sin = Math.sin(radians);
	     int[] pixels2 = new int[pixels.length];
	    for (int x = 0; x < width; x++)
	      for (int y = 0; y < height; y++) {
	        final int
	          centerx = width / 2, centery =height / 2,
	          m = x - centerx,
	          n = y - centery,
	          j = ((int) (m * cos + n * sin)) + centerx,
	          k = ((int) (n * cos - m * sin)) + centery;
	        if (j >= 0 && j < width && k >= 0 && k < height)
	          pixels2[(y * width + x)] = pixels[(k * width + j)];
	      }
	    System.arraycopy(pixels2, 0, pixels, 0, pixels.length);
	    pixels2 = new int[0];;
	    return pixels;
	  }
	
	public static void fixOrientation(String path, Bitmap  mBitmap) {
		//if(1==1)return;
	    if (mBitmap.getWidth() > mBitmap.getHeight()) {
	        Matrix matrix = new Matrix();
//	        matrix.postRotate(90);
	        int[] pixels;
	        
	        int height = mBitmap.getHeight();
	        int width = mBitmap.getWidth();

	        pixels = new int[height * width];

	        mBitmap.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1); 
	        mBitmap.recycle() ;
	        pixels=  rotate(90,pixels,width,height) ;
	       
//	        BitmapFactory.Options bfo = new BitmapFactory.Options();
//			bfo.inDither=false;                     //Disable Dithering mode
//			bfo.inPurgeable=true;   
	        mBitmap = Bitmap.createBitmap(pixels , mBitmap.getWidth(), mBitmap.getHeight(),Bitmap.Config.RGB_565);
	        
	        FileOutputStream stream = null;
			try {
				stream = new FileOutputStream(new File(path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			mBitmap.compress(Constants.COMPRESS_TYPE, 100, stream);
			try {
				stream.flush();
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			bm.recycle();
	    }
	}
	
	public static void rotateAndSaveImage(String path) {
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inDither=false;                     //Disable Dithering mode
		bfo.inPurgeable=true;        
		if (checkImageNeedCompression(path)) {
			bfo.inSampleSize = IMAGE_QUALITY;
		}
		Bitmap bm = BitmapFactory.decodeFile(path, bfo);
//		bm = rotateImage(path, bm);
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bm.compress(Constants.COMPRESS_TYPE, 100, stream);
		try {
			stream.flush();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bm.recycle();
		Runtime.getRuntime().gc();
	}

	public static long getImageSizeAfterCompression(String path) {
		Bitmap bm = getImageFor(path, IMAGE_QUALITY);
		return getImageSize(bm);
	}

	public static long getImageSize(String path) {
		try {
//			ExifInterface exifJpeg = new ExifInterface(path);
//			int width = exifJpeg.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
//			int height = exifJpeg.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
//			return (width * height * 2);//2 means 2 bytes for 16bit color.
			InputStream finput = Utilities.getFileInputStream(path);
			long fileSize = finput.available() ;
			finput.close() ;
//			long fileSize = new File(path).length();
			//System.out.println("------getImageSize------------"+fileSize);
			return fileSize ;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static int getOrientation(Context context, Uri photoUri) {
		
	    Cursor cursor = context.getContentResolver().query(photoUri,
	            new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
	            null, null, null);

	    try {
	        if (cursor.moveToFirst()) {
	            return cursor.getInt(0);
	        } else {
	            return -1;
	        }
	    } finally {
	        cursor.close();
	    }
	}
	public static long getImageSize(Bitmap bm) {
		return (bm.getRowBytes() * bm.getHeight());
	}
	

	public static Bitmap getImageFor(String path, int size) {
		size = ImageUtils.IMAGE_QUALITY ;
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inDither=false;   //Disable Dithering mode
		bfo.inPreferredConfig = Bitmap.Config.RGB_565;
		bfo.inPurgeable=false;   
			if (checkImageNeedCompression(path)) {
			bfo.inSampleSize = Constants.IN_SAMPLE_SIZE;//size
		}
		Bitmap bm = BitmapFactory.decodeFile(path, bfo);
		return rotateImage(path, bm);
	}
	
	public static Bitmap imageRotateafterClick(Bitmap bm ,String path){
	
		 Matrix matrix = new Matrix();
		    matrix.postRotate(90);
		   if(bm!=null){
		    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		    FileOutputStream fOut;
		    try {
		        fOut = new FileOutputStream(path);
		        if((BusinessProxy.sSelf.displaywidth>240 && BusinessProxy.sSelf.displayheight>320) || BusinessProxy.sSelf.displaywidth>320 && BusinessProxy.sSelf.displayheight>240)
		       bm.compress(Constants.COMPRESS_TYPE,Constants.COMPRESS, fOut);
		        else
		        bm.compress(Constants.COMPRESS_TYPE,Constants.COMPRESS, fOut);
		        fOut.flush();
		        fOut.close();

		    } catch (FileNotFoundException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
	}
		    return bm;
	}
	public static Bitmap imageRotateafterClickforprofile(Bitmap bm ,String path){
		
		 Matrix matrix = new Matrix();
		  //  matrix.postRotate(90);
		   if(bm!=null){ 
		    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		    FileOutputStream fOut;
		    try {
		        fOut = new FileOutputStream(path);
		        if((BusinessProxy.sSelf.displaywidth>240 && BusinessProxy.sSelf.displayheight>320) || BusinessProxy.sSelf.displaywidth>320 && BusinessProxy.sSelf.displayheight>240)
		       bm.compress(Constants.COMPRESS_TYPE,Constants.COMPRESS, fOut);
		        else
		        bm.compress(Constants.COMPRESS_TYPE,Constants.COMPRESS, fOut);
		        fOut.flush();
		        fOut.close();

		    } catch (FileNotFoundException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
	}
		    return bm;
	}
	public static Bitmap imageRotateafterClickforprofilePics(Bitmap bm ,String path){
		
		// Matrix matrix = new Matrix();
		  //  matrix.postRotate(90);
		   //if(bm!=null){ 
		   // bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		    FileOutputStream fOut;
		    try {
		        fOut = new FileOutputStream(path);
		        //if((BusinessProxy.sSelf.displaywidth>240 && BusinessProxy.sSelf.displayheight>320) || BusinessProxy.sSelf.displaywidth>320 && BusinessProxy.sSelf.displayheight>240)
		       bm.compress(Constants.COMPRESS_TYPE,Constants.COMPRESS, fOut);
		      
		        fOut.flush();
		        fOut.close();

		    } catch (FileNotFoundException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
	
		    return bm;
	}
	public static int getImagesFileId(Activity ctx, String path) {
//		System.out.println("-------------media count getting file id for------------"+path);
        String[] projection = { MediaStore.Images.Media._ID };
        Cursor cursor = ctx.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.DATA+" = ? ", new String[] { path}, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        if(!cursor.moveToFirst())
        	return -1 ;
        return cursor.getInt(column_index);
    }
	public static int getVideoFileId(Activity ctx, String path) {
//		System.out.println("-------------media count getting file id for------------"+path);
        String[] projection = { MediaStore.Video.Media._ID };
        Cursor cursor = ctx.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Media.DATA+" = ? ", new String[] { path}, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        if(!cursor.moveToFirst())
        	return -1 ;
        return cursor.getInt(column_index);
    }
	public static Bitmap getImageFileThumb(Activity ctx, int id) {
		if(id==-1)
			return null ;
//		System.out.println("-------------media getImageFileThumb------------ : "+id);
		return MediaStore.Images.Thumbnails.getThumbnail(
				ctx.getApplicationContext().getContentResolver(), id,
				MediaStore.Images.Thumbnails.MINI_KIND, null);
		
    }
	public static Bitmap getVideoFileThumb(Activity ctx, int id) {
//		System.out.println("-------------media getVideoFileThumb------------"+id);
		try{
		if(id< 0)return null;
		return MediaStore.Video.Thumbnails.getThumbnail(
				ctx.getApplicationContext().getContentResolver(), id,
				MediaStore.Video.Thumbnails.MICRO_KIND, null);
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
    }
	
	public static Bitmap decodeBitmap(Uri bitmapUri, ContentResolver resolver, int width, int height) throws IOException{
		
	    InputStream is = resolver.openInputStream(bitmapUri);
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;      
	    BitmapFactory.decodeStream(is,null,options);
	    is.close();

	    int ratio = Math.min(options.outWidth/width, options.outHeight/height);
	    int sampleSize = Integer.highestOneBit((int)Math.floor(ratio));
	    if(sampleSize == 0){
	        sampleSize = 1;
	    }       
	   // Log.d(RSBLBitmapFactory.class, "Sample Size: " + sampleSize);

	    options = new BitmapFactory.Options();
	    options.inSampleSize = sampleSize;

	    is = resolver.openInputStream(bitmapUri);
	    Bitmap b = BitmapFactory.decodeStream(is,null,options);
	    is.close();
	    return b;
	}
	/*public static Bitmap rotate(Bitmap b, int degrees,String url) 
	{
	    if (degrees != 0 && b != null) 
	    {
	        Matrix m = new Matrix();
	        m.postRotate(90);
	       // m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
	        try {
	            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
	            if (b != b2) 
	            {
	                b.recycle();
	                b = b2;
	            }
	            
	            int bytes = b.getWidth()*b.getHeight()*4; //calculate how many bytes our image consists of. Use a different value than 4 if you don't use 32bit images.

	            ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
	            b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

	            byte[] array = buffer.array(); 
	            
	            
	            FileOutputStream purge = new FileOutputStream(url);
				purge.write(array);
				purge.close();
	        } catch (OutOfMemoryError ex) 
	        {
	           throw ex;
	        } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return b;
	}*/
}