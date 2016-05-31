package com.kainat.app.android.uicontrol;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.util.Utilities;

public class ThumbImageView extends ImageView {

	private String mImagePah;
	private String mImageUrl;
	public Bitmap bitmap;
	
	/**
	 * @param context
	 */
	public ThumbImageView(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ThumbImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ThumbImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Gets the value of mImagePah
	 * 
	 * @return The mImagePah
	 */
	public final String getImagePah() {
		return mImagePah;
	}

	/**
	 * Sets the value of imagePah
	 * 
	 * @param imagePah
	 *            The mImagePah to set
	 */
	public final void setImagePah(String imagePah) {
		mImagePah = imagePah;
		setImageURI(Uri.parse(imagePah));
	}
	public final void setImageUrl(String imagePah) {
		mImageUrl = imagePah;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		try{
			
			if(bitmap != null && bitmap.isRecycled()){

						System.out
						 .println("--------------------CIMAGEVIEW---onDraw return---");
						return ;
			}
			
//			Drawable drawable = getDrawable();
//			if (drawable instanceof BitmapDrawable) {
//				BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//				Bitmap bitmap = bitmapDrawable.getBitmap();
//					if(bitmap != null && bitmap.isRecycled()){
////				bitmap.recycle();
//						System.out
//						 .println("--------------------CIMAGEVIEW---onDraw return---");
//						return ;
//					}
//				
//			}
			super.onDraw(canvas);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	@Override
	protected void finalize() throws Throwable {
//		System.out
//		 .println("--------------------CIMAGEVIEW---finalize-xy--"+getLeft()+"x"+getTop());
//		onDetachedFromWindowss();
		super.finalize();
	}
//	@Override
	protected void onDetachedFromWindow() {
		
//		
////		System.out
////			 .println("----------------CIMAGEVIEW----recycle---xy-1--"+getLeft()+"x"+getTop());
//		destroyDrawingCache() ;
//		
//		try{
//		Drawable drawable = getDrawable();
//		if (drawable instanceof BitmapDrawable) {
//			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//			Bitmap bitmap = bitmapDrawable.getBitmap();
////			System.out
////			 .println("-----------------CIMAGEVIEW---recycle----1--"+bitmap.getRowBytes()+" : "+bitmap.getWidth()+"x"+bitmap.getHeight());
////			if(!bitmap.isRecycled())
////			bitmap.recycle();
//			
////			bitmapDrawable = null;
////			 System.out
////			 .println("--------------------recycle------"+bitmap.getRowBytes());
//		}
////		drawable = getBackground();
////		if (drawable instanceof BitmapDrawable) {
////			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
////			Bitmap bitmap = bitmapDrawable.getBitmap();
////			if(!bitmap.isRecycled())
////			bitmap.recycle();
////			bitmapDrawable = null;
////			System.out
////			 .println("----------------CIMAGEVIEW----recycle-----2-"+bitmap.getRowBytes()+" : "+bitmap.getWidth()+"x"+bitmap.getHeight());
////		}
////		Bitmap bitmap = getDrawingCache();
////		if (bitmap !=null) {
////			if(!bitmap.isRecycled())
////			bitmap.recycle();
////			bitmap = null;
////			System.out
////			 .println("--------------------recycle-----2-"+bitmap.getRowBytes()+" : "+bitmap.getWidth()+"x"+bitmap.getHeight());
////		}
//		}catch (Exception e) {
//			// TODO: handle exception
//		}
//		
////		System.out.println("-----onDetachedFromWindow-------"+getTag());
////		String s = (String)getTag();
////		SoftReference b = ImageDownloader.imageCache.remove(s.hashCode()) ;
////		((Bitmap)b.get()).recycle();
//		if(mImageUrl!=null && mImageUrl.trim().length()>0){
////			getDrawable().
//		}
		super.onDetachedFromWindow();
	}
	@Override
	protected void onAttachedToWindow() {
		setDrawingCacheEnabled(false) ;
//		System.out.println("-----onAttachedToWindow-------");
		super.onAttachedToWindow();
		
//		if(mImageUrl!=null && mImageUrl.trim().length()>0){
//		loadProfilePic = new LoadProfilePic();
//		loadProfilePic.execute(mImageUrl);
//		}
	}
	
	
	LoadProfilePic loadProfilePic;

	private class LoadProfilePic extends AsyncTask<String, Void, String> {

		Drawable drawable;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {

			try {
//				System.out.println("----image url ----:"+urls[0]);
//				UIActivityManager.getCacheDirectoryImage();
				
				String filename = String.valueOf(urls[0].hashCode());
				String extension = "jpg";
				filename = filename + "." + extension;
				File f = new File(UIActivityManager.cacheDirImage, filename);
				drawable = Drawable.createFromPath(f.getAbsolutePath());
				if(drawable==null){
				InputStream is = (InputStream) new URL(urls[0]).getContent();
				byte data[] = Utilities.inputStreamToByteArray(is);
				if(data.length>0)
				{
					filename = String.valueOf(urls[0].hashCode());
					extension = "jpg";
					filename = filename + "." + extension;
					f = new File(UIActivityManager.cacheDirImage, filename);
					Utilities.writeFile(data, f, urls[0].hashCode());
					drawable = Drawable.createFromPath(f.getAbsolutePath());
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
//				makeToast(e.toString());
				return null;
			} catch (OutOfMemoryError e) {
//				makeToast(e.toString());
				return null;
			}
			// drawable=loadImage(User.getInstance().profilePic);
			return "";

		}

		@Override
		protected void onPostExecute(String result) {
			//
			try{
//			ImageView i = ((ImageView) findViewById(R.id.signup_thumb));
			if(drawable!=null)
			setImageDrawable(drawable);
			
//			Utilities.startAnimition(ProfileActivity.this, i, R.anim.fade);
			}catch(Exception e){
				
			}
		}
	}
}
