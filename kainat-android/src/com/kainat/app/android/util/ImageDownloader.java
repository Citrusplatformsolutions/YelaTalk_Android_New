package com.kainat.app.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.kainat.app.android.KainatCreateProfileActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.bean.UserStatus;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.uicontrol.CImageView;

public class ImageDownloader {

	
	public static final int TYPE_IMAGE = 0;
	public static final int TYPE_VIDEO = 2;
	public static final int TYPE_THUMB_BUDDY = 3;
	public static final int TYPE_THUMB_COMMUNITY = 4;
	public static final String extensionDefault = "jpg";
	
//	http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/pic?format={format}&user={username of user}
		 
//
//	private static final String BASE_URL = "http://" + Urls.WAP_HOST
//			+ "/mypage/user/%s";
	
	private static final String BASE_URL = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/pic?format=150x150&user=%s";
	
	public static Map<String, BitmapCache> bigImageCache;
	public static Map<String, BitmapCache> imageCache;
	public static Map<String, BitmapCache> imageCacheThumb;
	public static Map<String, BitmapCache> imageCacheToBeDeleted;
	public static BitmapFactory.Options opt;// = new BitmapFactory.Options();
	private static BitmapDownloaderTaskServer task;
	public static ThumbnailReponseHandler thumbnailReponseHandler;
	public static int  bigImageCacheInt = 50;
	public static ArrayList<ImageToBeLoad> imagesView = new ArrayList<ImageToBeLoad>();
	public static Hashtable<Integer, UserStatus> buddyInfo = new Hashtable<Integer, UserStatus>();// =
																									// new
																									// SoftReference<Bitmap>(bitmap);
	private int type = 0;
	private int downloadFor = 0;// 1 for cache delete/3 for add dimension
//	ExecutorService executorService;
	public Handler handler;

	public static Drawable border_gray = null;
	public static Drawable def2 = null;
	public static Drawable male = null;
	public static Drawable female = null;
	public static Drawable videothumbnew = null;
	public static Drawable placeholder = null;
	public static Drawable community_place_holder = null;
	public static Drawable profile = null;

	public static Drawable video_overlay = null;
	public static Drawable online_icon_chat = null;

	public static Drawable community_slide = null;
	public static Drawable inbox_slide = null;
	public static Drawable media = null;
	public static Drawable left_rtlive = null;	
	public static Drawable leftmenu_feedback = null;
	public static boolean idel = true;
	public static boolean animition = true;
	public static boolean thumbCircile =false;
	public static Drawable chat_slide = null;
	public static Drawable setting_slide = null;
	public static Drawable left_itinvite = null;
	public static Drawable leftmenu_compose = null;
	public static Drawable left_menu_post = null;
	public static Drawable friend_slide = null;
	public static Drawable leftmenugridbackground = null;
	public static int cacheSize = 100 ;
	public static Drawable rtevent_slide = null;
	public static Drawable search_slide = null;
	public static Drawable profile_left_new = null;
	
	boolean loadforCommunity;

	class ImageToBeLoad
	{
		int type;
		boolean bigImage;
		ImageView imageView;
		String url ;
		boolean isBuddyUrl = false;
		long time ;
		
		@Override
		public int hashCode() {
			
			return url.hashCode()+imageView.hashCode();
		}
		@Override
		public boolean equals(Object o) {
			ImageToBeLoad beLoad = (ImageToBeLoad)o;
			if(this.imageView.hashCode() == beLoad.imageView.hashCode() && beLoad.url.hashCode()==url.hashCode())
				return true ;
			
			return super.equals(o);
		}
	}
	static class BitmapCache
	{
		int seq;
		Bitmap bitmap;
		int size ;
	}
	
	public ImageDownloader() {
		
		if (imageCache == null)
			imageCache = new HashMap<String, BitmapCache>();
		if (bigImageCache == null)
			bigImageCache = new HashMap<String, BitmapCache>();
		if (imageCacheThumb == null)
			imageCacheThumb = new HashMap<String, BitmapCache>();
		
//		executorService = Executors.newFixedThreadPool(5);
//		if(executorServiceDownloder == null)
//			executorServiceDownloder = Executors.newFixedThreadPool(5);
		
		if (opt == null) {
			opt = new BitmapFactory.Options();
			opt.inDither = true;
			opt.inPurgeable = true;
			opt.inInputShareable = false;
			// opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// opt. inSampleSize = 4;
		}
	}

	public ImageDownloader(int downloadFor) {

//		executorService = Executors.newFixedThreadPool(5);

		this.downloadFor = downloadFor;
		if (imageCache == null)
			imageCache = new HashMap<String, BitmapCache>();
		if (bigImageCache == null)
			bigImageCache = new HashMap<String, BitmapCache>();
		
		if (opt == null) {
			opt = new BitmapFactory.Options();
			opt.inDither = true;
			opt.inPurgeable = true;
			opt.inInputShareable = false;
			// opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// opt. inSampleSize = 4;
		}
	}

	static LinkedList<ImageToBeLoad> links = new LinkedList<ImageToBeLoad>();
	static LinkedList<ImageToBeLoad> linksForFile = new LinkedList<ImageToBeLoad>();

	// public static Hashtable<Integer, Boolean> requested = new
	// Hashtable<Integer, Boolean>() ;
	// download function
	private static  LoadImageFromFile loadImageRequest;
	static LinkedList<Animation> animitionImageWave = new LinkedList<Animation>();
	static LinkedList<Animation> animitionImage = new LinkedList<Animation>();
	static LinkedList<Animation> buddy = new LinkedList<Animation>();
	
	public static void clearLinks() {
		links.clear();
		links = new LinkedList<ImageToBeLoad>();
	}

//	public class ProcessTask extends AsyncTask<String, Void, Bitmap> {
//
//		String url;
//		ImageView imageView;
//		ThumbnailReponseHandler thumbnailReponseHandler;
//
//		// private String url;
//		@Override
//		protected Bitmap doInBackground(String... params) {
//
//			download2(url, imageView, thumbnailReponseHandler);
//			return null;
//		}
//
//		@Override
//		// Once the image is downloaded, associates it to the imageView
//		protected void onPostExecute(Bitmap bitmap) {
////			System.out.println("--------loading done");
//
//		}
//	}

//	Runnable runnable = new Runnable() {
//
//		@Override
//		public void run() {
//			if (handler != null) {
//
//				/*
//				 * handler.post(new Runnable() {
//				 * 
//				 * @Override public void run() { //
//				 * System.out.println("--------inside handler"); // TODO
//				 * Auto-generated method stub download2( url, imageView,
//				 * thumbnailReponseHandler) ; executorService.shutdown(); } });
//				 */
//
//				download2(url, imageView, thumbnailReponseHandler);
//				executorService.shutdown();
//
//			} else {
//				Activity a = (Activity) imageView.getContext();
//				a.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						download2(url, imageView, thumbnailReponseHandler);
//						executorService.shutdown();
//					}
//				});
//			}
//
//		}
//	};
	String url;
	ImageView imageView;
	boolean bigimage;

	// ThumbnailReponseHandler thumbnailReponseHandler;
	public void download(String url, ImageView imageView,
			ThumbnailReponseHandler thumbnailReponseHandler, int type,boolean bigimage) {
		// if(isFromCache())
		// return;
		this.type = type;
		this.imageView = imageView;
		this.thumbnailReponseHandler = thumbnailReponseHandler;
		this.bigimage=bigimage ;
		this.url = url;
		download2(url, imageView, thumbnailReponseHandler);
	}
	// ThumbnailReponseHandler thumbnailReponseHandler;
		public void download(String url, ImageView imageView,
				ThumbnailReponseHandler thumbnailReponseHandler, int type) {
			// if(isFromCache())
			// return;
			this.type = type;
			this.imageView = imageView;
			this.thumbnailReponseHandler = thumbnailReponseHandler;
			this.url = url;
//			if (downloadFor == 1) {
//				executorService.submit(runnable);
//			} 
//			else 
			{
				download(url, imageView, thumbnailReponseHandler);
			}
		}
		public void loadForCommunity(boolean bool)
		{
			loadforCommunity = bool;
		}

	public void download(String url, ImageView imageView,
			ThumbnailReponseHandler thumbnailReponseHandler) {//
	// ProcessTask processTask = new ProcessTask() ;
	// processTask.url=url;
	// processTask.imageView=imageView;
	// processTask.thumbnailReponseHandler=thumbnailReponseHandler;
	// processTask.execute("");

		// this.type=type;

		// if(isFromCache())
		// return;
		this.url = url;
		this.imageView = imageView;
		// this.thumbnailReponseHandler=thumbnailReponseHandler ;
//		if (downloadFor == 1) {
//			executorService.submit(runnable);
//		} 
//		else
		{
			download2(url, imageView, thumbnailReponseHandler);
		}

	}
	
	boolean buddyRefresh = false;
	boolean isBuddyUrl = false;
//	private static ExecutorService executorServiceDownloder; 
	static boolean log = false;
	public void download2(String url, ImageView imageView,
			ThumbnailReponseHandler thumbnailReponseHandler) {		
		if(log)
		System.out.println("---black list url ---:"+blackListUrl.toString());
		if(url==null)return;

		if(imageView instanceof CImageView)
			((CImageView)imageView).setImageUrl(url) ;
		Bitmap bitmap = getFromCache(url,type);
//		bitmap = null;
		if(bitmap!=null){
			if(type==TYPE_THUMB_BUDDY){
				setImage(imageView, bitmap, null,type,false,url);
				return ;
			}
			else{
				setImage(imageView, bitmap, null,type,false,url);
				return ;
			}
		}

		if (url.indexOf("mypage/user") != -1 || url.indexOf("http:") == -1)
			isBuddyUrl = true;
		if (linksForFile.contains(url)){
			linksForFile.remove(url);
			if(log)
			System.out.println("--------IMAGEDOWNLODER-remove from  linksForFile: "
					+ url);
		}
		if (links.contains(url)){
			links.remove(url);
			if(log)
			System.out.println("--------IMAGEDOWNLODER-remove from  links: "
					+ url);
		}
		
		
		if(log)
		System.out.println("-----IMAGEDOWNLODER buddy id-info idel--------->"+idel);
		
		
		if(isBuddyUrl && buddyInfo!=null){
			
			UserStatus status =  buddyInfo.get(url.hashCode());
//			if(status!=null)
//			System.out.println("-----IMAGEDOWNLODER buddy refresh time--------->"+((System.currentTimeMillis()-status.cacheTime)/1000));
			
			if(status==null || ((System.currentTimeMillis()-status.cacheTime)/1000)>300){
				buddyRefresh=true;
			}
		}else
			buddyRefresh=true;
		
		if(log)
		 System.out.println("----IMAGEDOWNLODER image cache size : "+imageCache.size());
		if (imageView instanceof CImageView){
			if(log)
			{
			System.out.println("----IMAGEDOWNLODER saving image imageView----"+imageView.hashCode());
			System.out.println("----IMAGEDOWNLODER saving image url----"+url);
			}
//			images.put(url.hashCode(), (CImageView) imageView);
			ImageToBeLoad beLoad = new ImageToBeLoad() ;
			beLoad.imageView = imageView ;
			beLoad.url = url ;
			if(!imagesView.contains(beLoad))
			imagesView.add(beLoad);
		}
		if((!isBuddyUrl  || !buddyRefresh) && bitmap!=null)return ;
			
		initImage();
		if(!recycleRunning){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					recycleBitmap();
					recycleBigBitmap() ;
				}
			}).start();
			
		}
		
		getCacheDirectory(imageView.getContext());
		
		if (bitmap == null){
			setDefaultThumb(imageView);
			if(idel)
			{
			if (isFileCache(url)) {
				downloadFromFile(url);
			} else {
				buddyRefresh=false;
				downloadFromServer(url);
			}
			}
			
		}
		if(isBuddyUrl && buddyRefresh){
			downloadFromServer(url);
		}
	}
	public boolean isImageViewSaved(CImageView imageView,String url){
		if(imageView.getImageUrl().equalsIgnoreCase(url))
			return true;
		else
			return false ;
	}
	class RetriveImage implements Runnable {
		ImageToBeLoad imageToBeLoad;
		RetriveImage(ImageToBeLoad imageToBeLoad){
            this.imageToBeLoad=imageToBeLoad;
        }
  
        @Override
        public void run() {
        	
        	Bitmap bitmap=null;
//        	if ((loadImageRequest == null || loadImageRequest.getStatus() == Status.FINISHED)) {
//				loadImageRequest = new LoadImageRequest();
//				loadImageRequest.execute(url);
//			}
        	boolean file=true ;
        	LoadImageFromFile imageRequest = new LoadImageFromFile();
        	bitmap=imageRequest.doInBackground(imageToBeLoad);//getBitmap(retriveImage.url);
        	if(bitmap==null){
	        	BitmapDownloaderTaskServer photoToLoad = new BitmapDownloaderTaskServer();
	            bitmap=photoToLoad.doInBackground(imageToBeLoad);//getBitmap(retriveImage.url);
	            file=false;
        	}
//            saveCache(bitmap,url);
        	/*if (imageToBeLoad.type == TYPE_THUMB_BUDDY)
				saveCacheUserThumb(bitmap,imageToBeLoad.url);
			else*/
				saveCache(bitmap,imageToBeLoad.url);
            setImage(imageToBeLoad.imageView, bitmap, null,imageToBeLoad.type,false,imageToBeLoad.url);
//            startImageAnimition(imageToBeLoad.imageView, imageToBeLoad.type) ;
           
        }
    }
	public void setDefaultThumb(ImageView imageView){
//		System.out.println("----IMAGEDOWNLODER setDefaultThumb : "+url);
		if(type==TYPE_THUMB_BUDDY){
		setImage(imageView,
				((BitmapDrawable) def2)
						.getBitmap(),
				(Drawable) border_gray,type,false,url);
		
		if(imageView instanceof CImageView){
			if(((CImageView)imageView).getGender()==2)
			setImage(imageView,
					((BitmapDrawable) imageView.getContext().getResources()
							.getDrawable(R.drawable.male))
							.getBitmap(),
					(Drawable) border_gray,type,false,url);
			if(((CImageView)imageView).getGender()==3)
			setImage(imageView,
					((BitmapDrawable) imageView.getContext().getResources()
							.getDrawable(R.drawable.female))
							.getBitmap(),
					(Drawable) border_gray,type,false,url);
		}
		if(type==TYPE_THUMB_COMMUNITY){
			setImage(imageView,
					((BitmapDrawable) community_place_holder)
							.getBitmap(),
					(Drawable) border_gray,type,false,url);
		}
		}else if(type==TYPE_IMAGE){
			setImage(imageView,
					((BitmapDrawable) placeholder)
							.getBitmap(),
					(Drawable) border_gray, type,false,url);
			imageView.setBackgroundResource(R.drawable.border_gray);//BackgroundDrawable(bitmapDrawable);
		}
		else if(type==TYPE_VIDEO){
			setImage(imageView,
					((BitmapDrawable) video_overlay)
							.getBitmap(),null
					/*(Drawable) border_gray*/,type,false,url);
			imageView.setBackgroundResource(R.drawable.border_gray);//BackgroundDrawable(bitmapDrawable);
		}
	}
	public void setDefaultThumb(ImageView imageView, int tpye){
		this.type=type;
		if(type==TYPE_THUMB_BUDDY){
		setImage(imageView,
				((BitmapDrawable) def2)
						.getBitmap(),
				(Drawable) border_gray,type,false,url);
		}else if(type==TYPE_IMAGE){
			setImage(imageView,
					((BitmapDrawable) placeholder)
							.getBitmap(),
					(Drawable) border_gray,type,false,url);
			imageView.setBackgroundResource(R.drawable.border_gray);//BackgroundDrawable(bitmapDrawable);
		}
		else if(type==TYPE_VIDEO){
			setImage(imageView,
					((BitmapDrawable) video_overlay)
							.getBitmap(),
					(Drawable) border_gray,type,false,url);
			imageView.setBackgroundResource(R.drawable.border_gray);//BackgroundDrawable(bitmapDrawable);
		}
	}
	public void downloadFromFile(String url) {
		try {
			
			ImageToBeLoad imageToBeLoad=new ImageToBeLoad();//url, imageView);
			imageToBeLoad.type=type ;
			imageToBeLoad.url = url ;
			imageToBeLoad.bigImage = bigimage ;
			imageToBeLoad.imageView=imageView ;
			
			if(log)
			System.out.println("--------IMAGEDOWNLODER-downloadFromFile : "
					+ url.hashCode());
			
			if(linksForFile.contains(imageToBeLoad))
				linksForFile.remove(imageToBeLoad) ;
			
			/*if (linksForFile.size() > 40) {
				for (int i = 0; i < linksForFile.size()-40; i++) {
					linksForFile.poll();
				}

			}*/
			
			linksForFile.add(imageToBeLoad);
			if ((loadImageRequest == null || loadImageRequest.getStatus() == Status.FINISHED)) {
				loadImageRequest = new LoadImageFromFile();
				linksForFile.poll();
				loadImageRequest.execute(imageToBeLoad);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveBigIamgeCache(Bitmap bitmap, String url) {
		try {
			if(log)
			{
			System.out.println("--------IMAGEDOWNLODER-bigImageCache : "
					+ url.hashCode());
			System.out.println("--------IMAGEDOWNLODER-bigImageCache : "
					+ url);
			}
//			bitmap = BitmapFactory.decodeFile(f.getPath());
			BitmapCache bitmapCache = new BitmapCache();
			bitmapCache.bitmap = bitmap ;
			bitmapCache.seq = seq++ ;
			bitmapCache.size = bitmap.getRowBytes() ;
//			SoftReference bitmapRef = new BitmapCache(bitmapCache);
			if (bitmap != null) {
				bigImageCache.put(url.hashCode() + "", bitmapCache);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static int seq = 1;
	public static void saveCache(Bitmap bitmap, String url) {
		try {
//			if(url.equalsIgnoreCase("http://media.rocketalk.com/get/18_1_7_G_I_I3_dmnhkkp8zt.jpg?height=150&width=200"))
//			{
//				System.out.println("444");
//			}
			if(log){
			System.out.println("--------IMAGEDOWNLODER-saveCache : "
					+ url.hashCode());
			System.out.println("--------IMAGEDOWNLODER-saveCache : "
					+ url);
			}
//			bitmap = BitmapFactory.decodeFile(f.getPath());
			BitmapCache bitmapCache = new BitmapCache();
			bitmapCache.bitmap = bitmap ;
			bitmapCache.seq = seq++ ;
			bitmapCache.size = bitmap.getRowBytes() ;
//			SoftReference bitmapRef = new SoftReference<BitmapCache>(bitmapCache);
			if (bitmap != null) {
				imageCache.put(url.hashCode() + "", bitmapCache);
				if(cacheKey.contains(url.hashCode()+""))
					cacheKey.remove(url.hashCode()+"") ;
				
				cacheKey.add(url.hashCode() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<String> cacheKey = new ArrayList<String>() ;
	/*public void saveCacheUserThumb(Bitmap bitmap, String url) {
		try {
			if(log)
			{
			System.out.println("--------IMAGEDOWNLODER-saveCache : "
					+ url.hashCode());
			System.out.println("--------IMAGEDOWNLODER-saveCache : "
					+ url);
			}
//			bitmap = BitmapFactory.decodeFile(f.getPath());
			BitmapCache bitmapCache = new BitmapCache();
			bitmapCache.bitmap = bitmap ;
			SoftReference bitmapRef = new SoftReference<BitmapCache>(bitmapCache);
			if (bitmap != null) {
				imageCache.put(url.hashCode() + "", bitmapRef);
				
				if(cacheKey.contains(url.hashCode()+""))
					cacheKey.remove(url.hashCode()+"") ;
				
				cacheKey.add(url.hashCode() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public static void recycleBigBitmap() {
		try {
//			System.out.println("----IMAGEDOWNLODER------recycleBigBitmap---bigImageCache-"+bigImageCache.size());
			if(bigImageCache.size()<bigImageCacheInt)
				return ;
//			if (imageCache.size() > 50) 
			{
//				if (imageCacheToBeDeleted != null) 
				{
					int seq = Integer.MAX_VALUE ;
					Set<Map.Entry<String, BitmapCache>> entries = bigImageCache
							.entrySet();
					String keyT = null ;
					for (Map.Entry<String, BitmapCache> entry : entries) {
						String key = entry.getKey();
						BitmapCache temp = entry.getValue();
						if(temp.seq<=seq)
						{
							seq = temp.seq ;
							keyT = key ;
						}
//						if (temp != null && temp != null
//								&& !temp.bitmap.isRecycled()) {
//							temp.bitmap.recycle();
//							System.gc();
//							System.out.println("----IMAGEDOWNLODER------recycleBigBitmap-");
//						}

					}
					BitmapCache temp = bigImageCache.remove(keyT) ;
					if (temp != null && temp != null
					&& !temp.bitmap.isRecycled()) {
						temp.bitmap.recycle();
						System.gc();
//						System.out.println("----IMAGEDOWNLODER------recycleBigBitmap-"+keyT + " seq :"+seq);
					}
				}
//				bigImageCache = new HashMap<String, BitmapCache>();
			}
		} catch (Exception e) {

		}
	}
	public static Bitmap getFromCache(String url,int type) {
		if(log){
		System.out.println("--------IMAGEDOWNLODER-getFromCache : "
				+ url.hashCode());
		System.out.println("--------IMAGEDOWNLODER-getFromCache : "
				+ url);
		}
		
//		System.out.println("--------IMAGEDOWNLODER#################################");
		Bitmap bitmap = null;
		try {

			/*Set set = imageCache.keySet();
			// check key set values
			System.out.println("--------IMAGEDOWNLODER-getFromCache:"+url.hashCode()+":");
		      System.out.println("Key set values are: " + set);
			Iterator iterator = set.iterator() ;
			 while(iterator.hasNext())
			 {
		           String item = (String)iterator.next();
		           System.out.println("--------IMAGEDOWNLODER-getFromCache: item :"+item+":");
		           if(item.equalsIgnoreCase(url.hashCode()+"")){
		        	   System.out.println("--------IMAGEDOWNLODER-getFromCache :item available");
		           }
		           else
		        	   System.out.println("--------IMAGEDOWNLODER-getFromCache :  item not available");
		        }
		        */
			BitmapCache bitmapRef = null ;
//			if(type==TYPE_THUMB_BUDDY){
//				bitmapRef = (SoftReference<BitmapCache>) imageCacheThumb
//						.get(url.hashCode()+"");
//			}
			if(bitmapRef==null)
			bitmapRef = (BitmapCache) imageCache
					.get(url.hashCode()+"");
			if (bitmapRef == null)
				bitmapRef = (BitmapCache) bigImageCache
				.get(url.hashCode()+"");
			
			if (bitmapRef != null  && !bitmapRef.bitmap.isRecycled())
				return bitmapRef.bitmap;
			else
			{
//				imageCacheThumb.remove(url.hashCode()+"") ;
				imageCache.remove(url.hashCode()+"") ;
			}
			/*if(bitmap!=null)
				System.out.println("--------IMAGEDOWNLODER-getFromCache : available");
				else
					System.out.println("--------IMAGEDOWNLODER-getFromCache : not available");
			System.out.println("--------IMAGEDOWNLODER#################################\n\n");
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void downloadFromServer(String url) {
		try {
			
			ImageToBeLoad imageToBeLoad=new ImageToBeLoad();//url, imageView);
			imageToBeLoad.type=type ;
			imageToBeLoad.url = url ;
			imageToBeLoad.bigImage = bigimage ;
			imageToBeLoad.imageView=imageView ;
			imageToBeLoad.isBuddyUrl=isBuddyUrl;
			
			if(log){
				
				
				
			System.out.println("--------IMAGEDOWNLODER-downloadFromServer : "
					+ url.hashCode());
			System.out.println("--------IMAGEDOWNLODER-downloadFromServer : "
					+ url);
			}
			if (links.contains(imageToBeLoad))
				links.remove(imageToBeLoad);
//			if (downloadFor == 1 || downloadFor == 2 || downloadFor == 3) 
			/*{
				if (links.size() > 25) {
					for (int i = 0; i < links.size() - 25; i++) {
						links.poll();
					}

				}
//				links.addLast(url);
			} */
//			else
//				links.addFirst(url);
			
			
			
			links.add(imageToBeLoad);
			if ((task == null || task.getStatus() == Status.FINISHED)) {
				task = new BitmapDownloaderTaskServer();
				links.poll();
				task.execute(imageToBeLoad);
			}
		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}

	public boolean isFileCache(String url) {
		try {
			String filename = String.valueOf(url.hashCode());

			/*String extension = MimeTypeMap.getFileExtensionFromUrl(url);

			if (url.indexOf(".") == -1)
				extension = extensionDefault;
			if (extension == null || extension.trim().length() <= 0) {
				try {
					extension = url.substring(url.lastIndexOf(".") + 1,
							url.length());
				} catch (Exception e) {
					extension = extensionDefault;
				}
			}*/
			filename = filename + "." + extensionDefault;
			File f = new File(cacheDir, filename);
			return f.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean recycleRunning = false ;
	public void recycleBitmap() {
		try {
//			System.out.println("----IMAGEDOWNLODER recycleRunning: 1 "+recycleRunning);
			if(recycleRunning)return;
//			System.out.println("----IMAGEDOWNLODER recycleRunning: 2 "+recycleRunning);
			recycleRunning = true;
			if(!idel){
				for (int i = 0; i < links.size(); i++) {
					links.poll();
				}
				for (int i = 0; i < linksForFile.size() ; i++) {
					linksForFile.poll();
				}
				if(log)
					System.out.println("--------IMAGEDOWNLODER-removeving links and linksForFile"+linksForFile.size()+":"+links.size() );
				task=null;
				loadImageRequest=null;
			}
			if(imageCacheToBeDeleted==null)
				imageCacheToBeDeleted = new Hashtable<String, BitmapCache>();
//			 System.out.println("----IMAGEDOWNLODER imageCacheToBeDeleted.size : "+imageCacheToBeDeleted.size());
//			 System.out.println("----IMAGEDOWNLODER cacheSize.size : "+imageCache.size());
//			 System.out.println("----IMAGEDOWNLODER cacheKey size:"+cacheKey.size());
			
			if (imageCache.size() >= cacheSize || imageCacheToBeDeleted.size()>cacheSize/2) {
				
				if (imageCacheToBeDeleted != null && imageCacheToBeDeleted.size()>=cacheSize/2) 
				{
					
					 Map<String, BitmapCache> imageCacheToBeDeleted=this.imageCacheToBeDeleted;
					 this.imageCacheToBeDeleted = new Hashtable<String, BitmapCache>();
					 
					Set<Map.Entry<String, BitmapCache>> entries = imageCacheToBeDeleted
							.entrySet();
//					synchronized (entries) 
//					{
						
					for (Map.Entry<String, BitmapCache> entry : entries) {
						String key = entry.getKey();
						BitmapCache temp = entry.getValue();
						if(temp!=null && temp!=null){
//						System.out.println("----IMAGEDOWNLODER-seq--0-"+temp.seq);
						if (temp != null && temp.bitmap != null && temp.size >400
								&& !temp.bitmap.isRecycled()) {
							temp.bitmap.recycle();
//							System.out.println("----IMAGEDOWNLODER-seq---"+temp.seq);
							System.gc();
//							System.out.println("----IMAGEDOWNLODER image recycle : "+key);
						}
						}
//						imageCacheToBeDeleted.remove(key) ;
						// imageCache.remove(key) ;
						// System.out.println("---IMAGE DOWNLOADER--bytes c : "+bytess
						// + " : "+imageCache.size()+" rec ");
					}
//					}
				}
//				System.out.println("----IMAGEDOWNLODER image addind for recycle recycle key  cacheKey size:"+cacheKey.size());
//				System.out.println("----IMAGEDOWNLODER image addind for recycle recycle key  imageCacheToBeDeleted 2size:"+imageCacheToBeDeleted.size());
				
				if(cacheKey.size()>0)
				{
					if(cacheKey.get(0)!=null && imageCache.get(cacheKey.get(0))!=null){
					this.imageCacheToBeDeleted.put(cacheKey.get(0), imageCache.get(cacheKey.get(0)));
					imageCache.remove(cacheKey.get(0)) ;
					cacheKey.remove(0) ;
					}else
						cacheKey.remove(0) ;
				}
				
				/*Set<Map.Entry<String, SoftReference<Bitmap>>> entries = imageCache
						.entrySet();
				for (Map.Entry<String, SoftReference<Bitmap>> entry : entries) {
					String key = entry.getKey();
					SoftReference<Bitmap> temp = entry.getValue();
					imageCacheToBeDeleted.put(key, temp);
					imageCache.remove(key) ;
					
					System.gc();
					 System.out.println("----IMAGEDOWNLODER image addind for recycle recycle"+key);
					 
					break;
				}*/
				//imageCacheToBeDeleted = imageCache;
				//imageCache = new HashMap<String, SoftReference<Bitmap>>();
			}
		} catch (Exception e) {
e.printStackTrace();
		}
		recycleRunning = false ;
	}
/*
	public void download2XX(String url, ImageView imageView,
			ThumbnailReponseHandler thumbnailReponseHandler) {

		initImage();

		long inTime = System.currentTimeMillis();
		try {

			if (imageCache.size() > 50) {
				if (imageCacheToBeDeleted != null) {
					Set<Map.Entry<String, SoftReference<Bitmap>>> entries = imageCacheToBeDeleted
							.entrySet();
					for (Map.Entry<String, SoftReference<Bitmap>> entry : entries) {
						String key = entry.getKey();
						SoftReference<Bitmap> temp = entry.getValue();
						if (temp != null && temp.get() != null
								&& !temp.get().isRecycled()) {
							temp.get().recycle();
							System.gc();
							
						}
						
					}
				}
				imageCacheToBeDeleted = imageCache;
				imageCache = new HashMap<String, SoftReference<Bitmap>>();
			}

			
			if (imageView instanceof CImageView)
				images.put(url.hashCode(), (CImageView) imageView);
			if (url == null || url.indexOf("escapeXML(${mediaurl.url})") != -1) {
				// System.out.println("--------------INVALID IMAGE URL------ ;"+url);
				return;
			}
			boolean forceDownload = false;
			// int s = ;
			getCacheDirectory(imageView.getContext());
			if (url.indexOf("mypage/user") != -1 || url.indexOf("http:") == -1) {
				type = TYPE_THUMB_BUDDY;
				
				if (!buddyInfo.containsKey((String.format(BASE_URL, url))
						.hashCode())
						|| (buddyInfo
								.containsKey((String.format(BASE_URL, url))
										.hashCode()) && ((System
								.currentTimeMillis() - buddyInfo.get((String
								.format(BASE_URL, url)).hashCode()).cacheTime) / 1000) > 15)) {

					forceDownload = true;

				}
			}
			
			this.thumbnailReponseHandler = thumbnailReponseHandler;
			{

				// Caching code right here
				String filename = String.valueOf(url.hashCode());

				filename = filename + "." + extensionDefault;
				// if(downloadFor==1)
				// filename = filename ;
				CompressImage compressImage = null;
				File f = new File(cacheDir, filename);
				;// new File(cacheDir, "filename");
				// if(type==TYPE_THUMB_BUDDY)
				// f = new File(cacheDir, filename);
				// try{
				// compressImage = new CompressImage(imageView.getContext()) ;
				// f = new File(compressImage.compressImage(f.getPath())) ;
				// }catch (Exception e) {
				// // TODO: handle exception
				// }
				// System.out.println("-----IMAGE DOWNLOADER-TIME TAKEN 2--"+((System.currentTimeMillis()-inTime)));

				// Is the bitmap in our memory cache?
				Bitmap bitmap = null;
				// System.out.println("-----------default type...."+type);

				
				// System.out.println("-----IMAGE DOWNLOADER-TIME TAKEN 3--"+((System.currentTimeMillis()-inTime)));
				SoftReference<Bitmap> bitmapRef = null;// (SoftReference<Bitmap>)
														// imageCache
				if (type != TYPE_THUMB_BUDDY && bitmapRef == null && f.exists()) {

					if (linksForFile.size() > 20) {
						for (int i = 0; i < linksForFile.size(); i++) {
							linksForFile.poll();
						}

					}

					linksForFile.addFirst(url);
					if ((loadImageRequest == null || loadImageRequest
							.getStatus() == Status.FINISHED)) {
						loadImageRequest = new LoadImageRequest();
						loadImageRequest.execute(url);
					}
					if (1 == 1)
						return;//

					try {
						bitmap = BitmapFactory.decodeFile(f.getPath());
						bitmapRef = new SoftReference<Bitmap>(bitmap);
						if (bitmap != null) {
							imageCache.puturl.hashCode()
									+ "", bitmapRef);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						compressImage = new CompressImage(
								imageView.getContext());
						f = new File(compressImage.compressImage(f.getPath()));
						bitmap = BitmapFactory.decodeFile(f.getPath());
						bitmapRef = new SoftReference<Bitmap>(bitmap);
						if (bitmap != null) {
							imageCache.put(url.hashCode()
									+ "", bitmapRef);
						}
					}

				} else {
					if (bitmapRef != null && bitmapRef.get() != null)
						bitmap = bitmapRef.get();
				}
				if (bitmap == null && f.exists()) {
					try {
						bitmap = null;// BitmapFactory.decodeFile(f.getPath());
						bitmapRef = new SoftReference<Bitmap>(bitmap);
						if (bitmap != null) {
							imageCache.put(url.hashCode()
									+ "", bitmapRef);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
				}
				// System.out.println("-----IMAGE DOWNLOADER-TIME TAKEN 5--"+((System.currentTimeMillis()-inTime)));
				// bitmap = null ;
				if (forceDownload) {
					Drawable d = null;
					if (bitmap == null) {
						if (type == TYPE_THUMB_BUDDY) {
							d = def2;
							// imageView.getContext()
							// .getResources().getDrawable(R.drawable.def2) ;

						}

						bitmap = ((BitmapDrawable) d).getBitmap();
						if (type == TYPE_THUMB_BUDDY
								&& (bitmapRefDefaultThumb == null || bitmapRefDefaultThumb
										.get() == null))
							bitmapRefDefaultThumb = new SoftReference<Bitmap>(
									bitmap);
					}
					if (links.contains(url))
						links.remove(url);
					// links.addFirst(url);
					if (downloadFor == 1 || downloadFor == 2
							|| downloadFor == 3) {
						if (links.size() > 15) {
							for (int i = 0; i < links.size() - 25; i++) {
								links.poll();
							}

						}
						links.addLast(url);
					} else
						links.addFirst(url);
					if ((task == null || task.getStatus() == Status.FINISHED)) {
						task = new BitmapDownloaderTask();
						task.execute(links.poll());
					}
				}
				if (bitmap == null) {
					if (links.contains(url))
						links.remove(url);
					if (downloadFor == 1 || downloadFor == 2
							|| downloadFor == 3) {
						if (links.size() > 50) {
							for (int i = 0; i < links.size() - 50; i++) {
								links.poll();
							}

						}
						System.out.println("--------------inserting at first"
								+ url);
						links.addFirst(url);

					} else
						links.addFirst(url);

					if ((task == null || task.getStatus() == Status.FINISHED)) {
						try {
							task = new BitmapDownloaderTask();
							task.execute(links.poll());
						} catch (Exception e) {
							// TODO: handle exception
						}
					}

					if ((bitmapRefDefault == null || bitmapRefDefault.get() == null)
							|| (bitmapRefDefaultVideo == null || bitmapRefDefaultVideo
									.get() == null)
							|| (bitmapRefDefaultThumb == null || bitmapRefDefault
									.get() == null) && !forceDownload) {
					
						Drawable d = null;
						if (type == TYPE_IMAGE) {

							d = placeholder;
						} else if (type == TYPE_VIDEO) {
							d = videothumbnew;
						} else if (type == TYPE_THUMB_BUDDY) {
							d = def2;
						}

						bitmap = ((BitmapDrawable) d).getBitmap();
						if (type == TYPE_IMAGE) {
							bitmapRefDefault = new SoftReference<Bitmap>(bitmap);
						} else if (type == TYPE_VIDEO)
							bitmapRefDefaultVideo = new SoftReference<Bitmap>(
									bitmap);
						else if (type == TYPE_THUMB_BUDDY)
							bitmapRefDefaultThumb = new SoftReference<Bitmap>(
									bitmap);

					} else {
					}
					if (forceDownload && bitmap != null) {
						// BitmapDrawable d = new
						// BitmapDrawable(imageView.getContext()
						// .getResources(), bitmap);
						// imageView.setBackgroundDrawable(d);
						setImageBG(imageView, (BitmapDrawable) def2);
						if (this.type == 1) {
						}
					}
					if (!forceDownload)
						if (this.type == 1) {
							if (type == TYPE_IMAGE && bitmapRefDefault != null) {
								if (downloadFor == 1) {

									imageView.setImageBitmap(bitmapRefDefault
											.get());// ImageBitmap(
													// bitmapRefDefault.get());
									imageView
											.setBackgroundDrawable(border_gray);// Resource(R.drawable.border_gray);
								}
								setImageBG(imageView,
										(BitmapDrawable) placeholder);// ImageBitmap(
																		// bitmapRefDefault.get());
							} else if (type == TYPE_VIDEO
									&& bitmapRefDefaultVideo != null) {
							
								setImage(imageView,
										((BitmapDrawable) video_overlay)
												.getBitmap(),
										(Drawable) border_gray);
							} else if (type == TYPE_THUMB_BUDDY
									&& bitmapRefDefaultThumb != null) {
								setImageBG(imageView, (BitmapDrawable) def2);// ImageBitmap(
																				// bitmapRefDefault.get());
							}
						} else {

							if (type == TYPE_IMAGE && bitmapRefDefault != null) {
								if (downloadFor == 1) {
									
									setImage(imageView, bitmapRefDefault.get(),
											border_gray);
								} else
									// imageView.setBackgroundDrawable(new
									// BitmapDrawable(imageView.getResources(),bitmapRefDefault.get()));//ImageBitmap(
									// bitmapRefDefault.get());
									setImageBG(imageView,
											(BitmapDrawable) placeholder);// ImageBitmap(
																			// bitmapRefDefault.get());
							} else if (type == TYPE_VIDEO
									&& bitmapRefDefaultVideo != null) {
								
								setImage(imageView,
										((BitmapDrawable) video_overlay)
												.getBitmap(),
										(Drawable) border_gray);
							} else if (type == TYPE_THUMB_BUDDY
									&& bitmapRefDefaultThumb != null) {
								setImageBG(imageView, (BitmapDrawable) def2);// ImageBitmap(
																				// bitmapRefDefault.get());
							}
						}
				} else {
					if (type == TYPE_THUMB_BUDDY) {
						setImage(imageView, bitmap, null);
					} else if (this.type == TYPE_IMAGE) {
						{
						setImage(imageView, bitmap, null);
						}
					} else {
						BitmapDrawable d = new BitmapDrawable(imageView
								.getContext().getResources(), bitmap);
						// imageView.setBackgroundDrawable(d);
						setImageBG(imageView, d);
					}
				}
				bitmap = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}
*/
	
	private void initImage() {
		if (border_gray == null) {
//			border_gray = imageView.getContext().getResources()
//					.getDrawable(R.drawable.border_gray);
		}
		if (def2 == null) 
		{
			if(loadforCommunity)
				def2 = imageView.getContext().getResources().getDrawable(R.drawable.attachicon);//def2
			else
				def2 = imageView.getContext().getResources().getDrawable(R.drawable.male);//def2
			
			if(ImageDownloader.thumbCircile){
				Bitmap bitmap = ((BitmapDrawable)def2).getBitmap();
				bitmap= getRoundedBitmap(bitmap,50,Color.WHITE);
			
			def2 = new BitmapDrawable(imageView
					.getContext().getResources(), bitmap);
			}
		}
		else 
		{
			if(loadforCommunity)
				def2 = imageView.getContext().getResources().getDrawable(R.drawable.attachicon);//def2
			else
				def2 = imageView.getContext().getResources().getDrawable(R.drawable.male);//def2
		}
		if (male == null) {
			male = imageView.getContext().getResources()
					.getDrawable(R.drawable.male);//def2
			
			if(ImageDownloader.thumbCircile){
				Bitmap bitmap = ((BitmapDrawable)male).getBitmap();
				bitmap= getRoundedBitmap(bitmap,50,Color.WHITE);
				
				male = new BitmapDrawable(imageView
					.getContext().getResources(), bitmap);
			}
		}
		if (female == null) {
			female = imageView.getContext().getResources()
					.getDrawable(R.drawable.female);//def2
			
			if(ImageDownloader.thumbCircile){
				Bitmap bitmap = ((BitmapDrawable)female).getBitmap();
				bitmap= getRoundedBitmap(bitmap,50,Color.WHITE);
			
				female = new BitmapDrawable(imageView
					.getContext().getResources(), bitmap);
			}
		}
		if (videothumbnew == null) {
			videothumbnew = imageView.getContext().getResources()
					.getDrawable(R.drawable.videothumbnew);
		}
		if (placeholder == null) {
			placeholder = imageView.getContext().getResources()
					.getDrawable(R.drawable.placeholder);
		}
		if (community_place_holder == null) {
			community_place_holder = imageView.getContext().getResources()
					.getDrawable(R.drawable.groupicon);
		}

		if (video_overlay == null) {
			video_overlay = imageView.getContext().getResources()
					.getDrawable(R.drawable.video_overlay);
		}
		if (profile == null) {
			profile = imageView.getContext().getResources()
					.getDrawable(R.drawable.profile);
		}
		if (community_slide == null) {
			community_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.community_slide);
		}
		if (inbox_slide == null) {
			inbox_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.inbox_slide);
		}
		if (media == null) {
			media = imageView.getContext().getResources()
					.getDrawable(R.drawable.media);
		}
		if (left_rtlive == null) {
			left_rtlive = imageView.getContext().getResources()
					.getDrawable(R.drawable.left_rtlive);
		}
		if (leftmenu_feedback == null) {
			leftmenu_feedback = imageView.getContext().getResources()
					.getDrawable(R.drawable.feedback);
		}
		if (chat_slide == null) {
			chat_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.chat_slide);
		}
		if (setting_slide == null) {
			setting_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.setting_slide);
		}
		if (left_itinvite == null) {
			left_itinvite = imageView.getContext().getResources()
					.getDrawable(R.drawable.left_itinvite);
		}
		if (leftmenu_compose == null) {
			leftmenu_compose = imageView.getContext().getResources()
					.getDrawable(R.drawable.leftmenu_compose);
		}
		if (left_menu_post == null) {
			left_menu_post = imageView.getContext().getResources()
					.getDrawable(R.drawable.left_menu_post);
		}
		if (friend_slide == null) {
			friend_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.friend_slide);
		}
		if (leftmenugridbackground == null) {
			leftmenugridbackground = imageView.getContext().getResources()
					.getDrawable(R.drawable.leftmenugridbackground);
		}
		if (rtevent_slide == null) {
			rtevent_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.rtevent_slide);
		}
		if (search_slide == null) {
			search_slide = imageView.getContext().getResources()
					.getDrawable(R.drawable.search_slide);
		}
		if (profile_left_new == null) {
			profile_left_new = imageView.getContext().getResources()
					.getDrawable(R.drawable.profile_left_new);
		}
	}

	public void setImageBG(final ImageView imageView,
			final BitmapDrawable bitmapDrawable) {

		Activity a = (Activity) imageView.getContext();
		a.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				imageView.setBackgroundDrawable(bitmapDrawable);
			}
		});

	}

	// public void setImage(final ImageView imageView,final Bitmap bitmap,final
	// int res){
	//
	// Activity a=(Activity)imageView.getContext();
	// a.runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// imageView.setImageBitmap(bitmap);//ImageBitmap( bitmapRefDefault.get());
	// imageView.setBackgroundResource(res);
	// }
	// });
	//
	// }
	public void setImage(final ImageView imageView, final Bitmap bitmap,
			final Drawable bitmapDrawable,final int type,final boolean ani,String url) {

		if(imageView instanceof CImageView)
			if(!(((CImageView)imageView).getImageUrl()).equalsIgnoreCase(url)){
				return ;
			}
		try{
			imageView.setImageBitmap(bitmap);
			imageView.setBackgroundDrawable(bitmapDrawable);
			if(ani)
			startImageAnimition(imageView, type) ;
		}catch (Exception e) {
			
		try{
		Activity a = (Activity) imageView.getContext();
		a.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				imageView.setImageBitmap(bitmap);
				imageView.setBackgroundDrawable(bitmapDrawable);
				if(ani)
				startImageAnimition(imageView, type) ;
			}
		});
		}catch (ClassCastException e8) {
			imageView.setImageBitmap(bitmap);
			imageView.setBackgroundDrawable(bitmapDrawable);
			if(ani)
			startImageAnimition(imageView, type) ;
		}
		}
	}

	public void setImageNullBg() {

		Activity a = (Activity) imageView.getContext();
		a.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// imageView.setImageBitmap(bitmap);//ImageBitmap(
				// bitmapRefDefault.get());
				// imageView.setBackgroundResource(res);
			}
		});

	}
	public static Map<String, BitmapCache> imageCacheToBeDeleted2;
	public  void removeAll() {
//		imageCacheToBeDeleted=imageCache;
		imageCacheToBeDeleted2 = bigImageCache ;
		cacheKey = new ArrayList<String>() ;
		imageCache = new Hashtable<String, BitmapCache>();
//		RemoveCache cache = new RemoveCache() ;
//		cache.execute(null) ;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				removeBitmoaCache()  ;
			}
		}).start();
	}
	public static void removeBitmoaCache() {
		if (imageCache == null)
			return;
		try {
			
			
			Set<Map.Entry<String, BitmapCache>> entries = imageCacheToBeDeleted
					.entrySet();
//			System.out.println("----IMAGEDOWNLODER-cacheSize--"+cacheSize);
			for (Map.Entry<String, BitmapCache> entry : entries) {
				String key = entry.getKey();
				BitmapCache temp = entry.getValue();
				if(temp!=null && temp!=null){
//				System.out.println("----IMAGEDOWNLODER-seq--0-"+temp.seq);
//				System.out.println("----IMAGEDOWNLODER-size--0-"+temp.size);
				if (temp != null && temp.size>0 && temp.bitmap != null
						&& !temp.bitmap.isRecycled()) {
					temp.bitmap.recycle();
//					System.out.println("----IMAGEDOWNLODER-seq---"+temp.seq);
//					System.gc();
				}
				}
			}
			
			
		} catch (Exception e) {
			
		}
	}

	public static void clearCache() {
		
//		 System.out.println("----IMAGEDOWNLODER clearCache method");
		 
		if (imageCache != null)
			imageCache.clear();
	}
	public static void clearImageview() {
//		if (images != null)
//			images.clear();
	}

	private static Bitmap parseThumbnail(byte[] response, ThumbnailImage request) {
		// 1 byte - status online/offline
		// 4 - Length + data
		request = new ThumbnailImage("user", 0);
		int offset = 0;
		// System.out.println("-----mName---------->"+request.mName);
		// Read online/Offline Status in 1 byte
		request.mOnOffLine = response[offset++];
		// Read length of thumb nail in 4 bytes
		int len = Utilities.bytesToInt(response, 1, 4);
		offset += 4;
		return BitmapFactory.decodeByteArray(response, offset, len);
	}

	public boolean buddyThumb;

	private Bitmap parseThumbnailWithInfo(byte[] response,
			ThumbnailImage request) {
		try {
			request.mBitmap = BitmapFactory.decodeByteArray(response, 0,
					response.length);
			return request.mBitmap ;
		}catch (Exception e) {
			e.printStackTrace();
			return null ;
		}
	}
	private Bitmap parseThumbnailWithInfoXXX(byte[] response,
			ThumbnailImage request) {
		// 1 byte - status online/offline
		// 4 - Length + data
		try {

			int offset = 0;
			UserStatus userStatus = new UserStatus();
			userStatus.userId = request.mName.hashCode();

//			 System.out.println("-----IMAGEDOWNLODER buddy id-info store--------->"+request.mName);
			// Read online/Offline Status in 1 byte
			request.mOnOffLine = response[offset++];
			userStatus.onLineOffline = request.mOnOffLine;
			// Read length of thumb nail in 4 bytes
			int len = Utilities.bytesToInt(response, 1, 4);
			offset += 4;
			request.mBitmap = BitmapFactory.decodeByteArray(response, offset,
					len);
			
			byte data[] = new byte[len] ;
			System.arraycopy(response, offset, data, 0, len) ;
			
			String filename = String.valueOf(request.mName.hashCode());
			filename = filename + "." + extensionDefault;
			File f = new File(cacheDir, filename);
			
				if(f!=null && f.exists())
				f.delete() ;
			
			writeFile(data, f, request.mName.hashCode());
			
			
			
			// if(request.mBitmap == null)
			// System.out.println("------------request.mBitmap is null------------------");
			// if(buddyThumb)
			{
				try {
					// System.out.println("-----response-len---------->"+response.length);
					// System.out.println("------len1---------->"+len);
					offset += len;

					len = Utilities.bytesToInt(response, offset, 4);
					offset += 4;
					// System.out.println("------len2---------->"+len);
					// System.out.println("------offset---------->"+offset);
					request.mStatus = new String(response, offset, len,
							Constants.ENC);
					userStatus.statusText = request.mStatus;
					// System.out.println("------request.mStatus---------->"+request.mStatus);
					offset += len;

					len = Utilities.bytesToInt(response, offset, 4);
					offset += 4;
					// System.out.println("------len3---------->"+len);
					// System.out.println("------offset---------->"+offset);
					request.mContent = new String(response, offset, len,
							Constants.ENC);
					userStatus.mContent = request.mContent;
					// System.out.println("------mContent---------->"+request.mContent);
					offset += len;

					len = Utilities.bytesToInt(response, offset, 4);
					offset += 4;
					// System.out.println("------len4---------->"+len);
					// System.out.println("------offset---------->"+offset);
					request.mTime = new String(response, offset, len,
							Constants.ENC);
					userStatus.mTime = request.mTime;
					// System.out.println("------mTime---------->"+request.mTime);
				} catch (Exception e) {
					System.out
							.println("########################EXCEPTION HANDLED#############################");
					e.printStackTrace();
				}
			}
			userStatus.cacheTime = System.currentTimeMillis();
			// System.out.println("-------------saving user details : "+userStatus.userId);
			buddyInfo.put(userStatus.userId, userStatus);

			return request.mBitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	// cancel a download (internal only)
	// private static boolean cancelPotentialDownload(String url, ImageView
	// imageView) {
	// BitmapDownloaderTask bitmapDownloaderTask =
	// getBitmapDownloaderTask(imageView);
	//
	// if (bitmapDownloaderTask != null) {
	// String bitmapUrl = bitmapDownloaderTask.url;
	// if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
	// bitmapDownloaderTask.cancel(true);
	// } else {
	// // The same URL is already being downloaded.
	// return false;
	// }
	// }
	// return true;
	// }

	// gets an existing download if one exists for the imageview
	// private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView
	// imageView) {
	// if (imageView != null) {
	// Drawable drawable = imageView.getDrawable();
	// if (drawable instanceof DownloadedDrawable) {
	// DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
	// return downloadedDrawable.getBitmapDownloaderTask();
	// }
	// }
	// return null;
	// }

	// our caching functions
	// Find the dir to save cached images
	private static File cacheDir;

	private File getCacheDirectory(Context context) {

		/*
		 * if(downloadFor==1){ String sdState =
		 * android.os.Environment.getExternalStorageState(); if
		 * (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) { File sdDir =
		 * android.os.Environment.getExternalStorageDirectory(); cacheDir = new
		 * File(sdDir, Constants.imageTempPath); }
		 * 
		 * if (!cacheDir.exists()) cacheDir.mkdirs(); return cacheDir; }
		 */
		 if (cacheDir != null)
		 return cacheDir;
		 String sdState = android.os.Environment.getExternalStorageState();
		 if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
		 File sdDir = android.os.Environment.getExternalStorageDirectory();
		 cacheDir = new File(sdDir, Constants.imagePath);
		 }
		// else
		if(cacheDir==null)
		cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;

		// if (cacheDir != null)
		// return cacheDir;

	}

	private void writeFile(Bitmap bmp, File f) {
		// System.out.println("------------------image url write filename: "+f.getPath())
		// ;
		FileOutputStream out = null;
		if (f != null && !f.exists())
			try {
				// if(f.exists())

				f.createNewFile();
				out = new FileOutputStream(f);
				if (type == TYPE_THUMB_BUDDY)
					bmp.compress(Bitmap.CompressFormat.PNG, 100, out);//100
				else
					bmp.compress(Bitmap.CompressFormat.PNG, 100, out);//70
				bmp.recycle();
			} catch (Exception e) {
				System.out.println("------image download : " + e.getMessage());
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
	}

	// /////////////////////

	// download asynctask
	public class BitmapDownloaderTaskServer extends AsyncTask<ImageToBeLoad, Void, Bitmap> {
		private ImageToBeLoad lmageToBeLoad;

		@Override
		protected Bitmap doInBackground(ImageToBeLoad... params) {
			lmageToBeLoad = (ImageToBeLoad) params[0];
//			lmageToBeLoad.url"http://media.rocketalk.com/get/18_1_7_G_I_I1_dmnhkl2p1k.jpg?height=1280&amp;width=768" ;
			Bitmap bitmap=null;
			if (lmageToBeLoad.url.indexOf("mypage/user") != -1 || lmageToBeLoad.url.indexOf("http:") == -1) {
//				System.out
//						.println("------------image download downloadBitmapBuddyStyle--- : "
//								+ lmageToBeLoad.url);
				bitmap= downloadBitmapBuddyStyle(lmageToBeLoad.url);
				
				String filename = String.valueOf(lmageToBeLoad.url.hashCode());
				filename = filename + "." + extensionDefault;
				
				
//				final File f = new File(cacheDir, filename);
//				
//				if(lmageToBeLoad.isBuddyUrl)
//					if(f!=null && f.exists())
//					f.delete() ;
//				
//				writeFile(bitmap, f);
//				if(!bitmap.isRecycled())
//				bitmap.recycle();
//				bitmap = BitmapFactory.decodeFile(f.getPath());
				
//				if(ImageDownloader.thumbCircile)
//				bitmap= getRoundedBitmap(bitmap,50,Color.WHITE);
			} else {
//				System.out
//						.println("------------image download downloadBitmap--- : "
//								+ lmageToBeLoad.url);
				bitmap= downloadBitmap(lmageToBeLoad.url);
			}
			if(bitmap!=null){
			String filename = String.valueOf(lmageToBeLoad.url.hashCode());
			filename = filename + "." + extensionDefault;
//			final File f = new File(cacheDir, filename);

//			if(lmageToBeLoad.isBuddyUrl)
//				if(f!=null && f.exists())
//				f.delete() ;
			
//			if(ImageDownloader.thumbCircile && lmageToBeLoad.type == TYPE_THUMB_BUDDY)
//				bitmap= getRoundedBitmap(bitmap,50,Color.WHITE);
			/*writeFile(bitmap, f);
			if(!bitmap.isRecycled())
			bitmap.recycle();
			bitmap = BitmapFactory.decodeFile(f.getPath());*/
//			System.out.println("---------#SERVER####----");
//			System.out.println("-------------size file url"+lmageToBeLoad.url);
//			System.out.println("-------------size file"+(f.length()));
//			System.out.println("---------######----");
			}
			return bitmap;
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			try {
				if (isCancelled()) {
					bitmap = null;
				}
				if (bitmap == null) {
					ImageToBeLoad ImageToBeLoadL = links.poll();
					if (ImageToBeLoadL != null /* && task.getStatus()==Status.FINISHED */) {
						task = new BitmapDownloaderTaskServer();
						task.execute(ImageToBeLoadL);
					}
					return;
				}
				/*if (lmageToBeLoad.type == TYPE_THUMB_BUDDY)
					saveCacheUserThumb(bitmap,lmageToBeLoad.url);
				else*/
					saveCache(bitmap,lmageToBeLoad.url);
				
				if(lmageToBeLoad.bigImage)
				saveBigIamgeCache(bitmap, lmageToBeLoad.url);
				
				ThumbnailImage value = new ThumbnailImage("", 0);
				value.mBitmap = bitmap;
//				if (downloadFor == 1 || downloadFor == 2) 
				{
					final Bitmap tbitmap = bitmap;
					Activity activity = (Activity) imageView.getContext();
					activity.runOnUiThread(new Runnable() 
					{

						@Override
						public void run() 
						{
							try {
								Vector<ImageToBeLoad> t = new Vector<ImageDownloader.ImageToBeLoad>() ;
								for (int i = 0; i < imagesView.size(); i++) {
									ImageToBeLoad beLoad = imagesView.get(i) ;
									if(beLoad.url.hashCode()==lmageToBeLoad.url
										.hashCode()){
										if (beLoad.imageView != null) {
											setImage(beLoad.imageView, tbitmap, null,lmageToBeLoad.type,true,lmageToBeLoad.url);
										}
										t.add(beLoad) ;
									}
								}
								for (int i = 0; i < t.size(); i++) {
									imagesView.remove(t.get(i)) ;
								}
								t.clear();
								/*ImageView cImageView = images.remove(lmageToBeLoad.url
										.hashCode());
								if (cImageView != null) {
									setImage(cImageView, tbitmap, null,lmageToBeLoad.type,true,lmageToBeLoad.url);
								}*/
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						
					}
					);
				} 
//				else
//					thumbnailReponseHandler.onThumbnailResponse(value,
//							url.getBytes());// np

//				if(thumbnailReponseHandler instanceof KainatCreateProfileActivity)
					thumbnailReponseHandler.onThumbnailResponse(value, url.getBytes());

				ImageToBeLoad ImageToBeLoadL = links.poll();
				if (ImageToBeLoadL != null /* && task.getStatus()==Status.FINISHED */) {
					task = new BitmapDownloaderTaskServer();
					task.execute(ImageToBeLoadL);
				}

				// System.out.println("------IMAGE DOWNLOADE--totalMemory------------"
				// + ""
				// + Runtime.getRuntime().totalMemory() / 1024 + " KB");
				// System.out.println("----IMAGE DOWNLOADE----freeMemory------------"
				// + ""
				// + Runtime.getRuntime().freeMemory() / 1024 + " KB");

			} catch (Exception e) {
				// TODO: handle exception
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
			}
		}
	}

	// static class DownloadedDrawable extends ColorDrawable {
	// private final WeakReference<BitmapDownloaderTask>
	// bitmapDownloaderTaskReference;
	//
	// public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
	// super(Color.BLACK);
	// bitmapDownloaderTaskReference =
	// new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
	// }
	//
	// public BitmapDownloaderTask getBitmapDownloaderTask() {
	// return bitmapDownloaderTaskReference.get();
	// }
	// }

	private static final int CHUNK_LENGTH = 1024;

	private Bitmap downloadBitmapBuddyStyle(String url) {

		try {
			String id = url;
			HttpConnectionHelper helper = null;
//			System.out.println("--------IMAGEDOWNLODER-downloadBitmapBuddyStyle : "
//					+ url);
//			System.out.println("--------IMAGEDOWNLODER-downloadBitmapBuddyStyle : "
//					+ url.hashCode());
			// System.out.println("-------downloadBitmapBuddyStyle : " +
			// String.format(BASE_URL, url));
			if (url.indexOf("http:") == -1)
				helper = new HttpConnectionHelper(String.format(BASE_URL, url));
			else {
				// url =
				// "http://android-rtd4.onetouchstar.com/mypage/user/uk1";//url.trim();
				helper = new HttpConnectionHelper(String.format(url));
			}
			// helper = new HttpConnectionHelper(url);
//			helper.setRequestMethod(HttpPost.METHOD_NAME);
			helper.setRequestMethod(HttpGet.METHOD_NAME);
//			helper.setPostData(POST_DATA.getBytes());
			int responseCode = helper.getResponseCode();
			switch (responseCode) {
			case HttpURLConnection.HTTP_OK:
			case HttpURLConnection.HTTP_ACCEPTED:
				String contentEncoding = helper
						.getHttpHeader("Content-Encoding");
				InputStream inputStream = null;
				if (null == contentEncoding) {
					inputStream = helper.getInputStream();
				} else if (contentEncoding.equals("gzip")) {
					inputStream = new GZIPInputStream(helper.getInputStream());
				}
				ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
				byte[] chunk = new byte[CHUNK_LENGTH];
				int len;
				while (-1 != (len = inputStream.read(chunk))) {
					buffer.append(chunk, 0, len);
				}
				// System.out.println("---------------------------------buddyThumb : "+buddyThumb);

				// if(buddyThumb)
				// {
				ThumbnailImage request = new ThumbnailImage(id, 0);

				parseThumbnailWithInfo(buffer.buffer(), request);
				buffer.clear();
//				thumbnailReponseHandler.onThumbnailResponse(request, null);// np
				return request.mBitmap;
				// }
				// else
				// return parseThumbnail(buffer.buffer(), null);
			}
		} catch (Exception e) {
//			System.out.println("------------image download buddy style--- : "+ e.getLocalizedMessage());
		}

		return null;
	}

	static Vector<String> blackListUrl = new Vector<String>() ;
	static Bitmap downloadBitmap(String url) {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(params);
		final HttpGet getRequest = new HttpGet(url);
		try {
			getRequest.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
			getRequest.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode+ " while retrieving bitmap from " + url);
				blackListUrl.add(url);
				return null;
			}
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					if(!idel)
						return null;
					inputStream = entity.getContent();
					byte[] data = Utilities.readBytes(inputStream);// new
					Bitmap bitmap = null;
					String filename = String.valueOf(url.hashCode());
//					String filename = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('?'));
					filename = filename + "." + extensionDefault;
					final File f = new File(cacheDir, filename);
					writeFile(data, f, url.hashCode());
					bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
					data = null;
					return bitmap;
				} catch (Exception e) {
					blackListUrl.add(url) ;
					e.printStackTrace();
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			blackListUrl.add(url) ;
			e.printStackTrace();
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			// Log.w("ImageDownloader", "Error while retrieving bitmap from "
			// + url + e.toString());
			// int w = 50, h = 50;
			// Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf
			// // types
			// Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a
			// // MUTABLE bitmap
			// Canvas canvas = new Canvas(bmp);
			// return bmp;

		} finally {
			if (client != null) {
				// client.close();
			}
		}
		return null;
	}
	private static void writeFile(byte[] data, File f, int id) {

		FileOutputStream out = null;
		if (f != null && !f.exists() && data != null) {
			try {

				f.createNewFile();
				out = new FileOutputStream(f);
				out.write(data);

			} catch (Exception e) {
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}

	}

	private static Bitmap scaleImage(int newWidth, Bitmap scaled) {
		try {
			int width = scaled.getWidth();
			int height = scaled.getHeight();

			float scaleWidth = ((float) newWidth) / width;
			float ratio = ((float) scaled.getWidth()) / newWidth;
			int newHeight = (int) (height / ratio);
			float scaleHeight = ((float) newHeight) / height;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);

			scaled = Bitmap.createBitmap(scaled, 0, 0, width, height, matrix,
					true);
			return scaled;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static UserStatus getUserInfo(String userName) {
		/*if (ImageDownloader.buddyInfo.containsKey((String.format(BASE_URL,
				userName)).hashCode())) {
			UserStatus userStatus = ImageDownloader.buddyInfo.get((String
					.format(BASE_URL, userName)).hashCode());
			return userStatus;
		}*/
		if (ImageDownloader.buddyInfo.containsKey(userName.hashCode())) {
			UserStatus userStatus = ImageDownloader.buddyInfo.get(userName.hashCode());
			return userStatus;
		}
		return null;
	}

	// String.format(BASE_URL, url)
	// public static void refreshMe(int id,Bitmap mBitmap){
	// try{
	// CImageView cImageView = (CImageView)images.get(id) ;
	// Drawable d = new BitmapDrawable(cImageView.getContext()
	// .getResources(), mBitmap);
	// cImageView.setBackgroundDrawable(d);
	// Utilities.startAnimition(cImageView.getContext(), cImageView,
	// android.R.anim.fade_in) ;
	// }catch (Exception e) {
	// }
	// }
	// public static void removedMe(CImageView me){
	// Enumeration<Integer> enm = ImageDownloader.images.keys();
	// while(enm.hasMoreElements()){
	// Integer i = enm.nextElement() ;
	// CImageView c = ImageDownloader.images.get(i) ;
	// if(c==me){
	// ImageDownloader.images.remove(i);
	// break;
	// }
	// }
	//
	private class RemoveCache extends AsyncTask<ImageToBeLoad, Void, Bitmap> {
		
		ImageToBeLoad imageToBeLoad;
		@Override
		protected Bitmap doInBackground(ImageToBeLoad... params) {
			removeBitmoaCache();
			return null ;
		}
	}
	private class LoadImageFromFile extends AsyncTask<ImageToBeLoad, Void, Bitmap> {
		// ImageView imageView ;
//		String urlLocal; 

		// public LoadImageRequest(ImageView imageView, String url){
		// this.imageView = imageView ;
		// this.url = url ;
		// }
		ImageToBeLoad imageToBeLoad;
		@Override
		protected Bitmap doInBackground(ImageToBeLoad... params) {

			try {
				imageToBeLoad	 = (ImageToBeLoad)params[0];
//				imageToBeLoad.url ;
				String filename = String.valueOf(imageToBeLoad.url.hashCode());

				/*String extension = MimeTypeMap.getFileExtensionFromUrl(url);

				if (url.indexOf(".") == -1)
					extension = extensionDefault;// "jpg" ;
				if (extension == null || extension.trim().length() <= 0) {
					try {
						extension = url.substring(url.lastIndexOf(".") + 1,
								url.length());
					} catch (Exception e) {
						extension = extensionDefault;// "jpg" ;
					}
				}*/
				filename = filename + "." + extensionDefault;
				// if(downloadFor==1)
				// filename = filename ;
				CompressImage compressImage = null;
				File f = new File(cacheDir, filename);
//				System.out.println("---------#FILE####----");
//				System.out.println("-------------url"+imageToBeLoad.url);
//				System.out.println("-------------size"+(f.length())/1024);
//				System.out.println("---------######----");
				Drawable drawable = Drawable
						.createFromPath(f.getAbsolutePath());
				// drawable=null;
				// drawable=null;
				if (drawable != null) {
					
					Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
					
					
					
//					SoftReference<Bitmap> bitmapRef = new SoftReference<Bitmap>(
//							bitmap);
//					saveCache(bitmap,url);
					/*if (imageToBeLoad.type == TYPE_THUMB_BUDDY)
						saveCacheUserThumb(bitmap,imageToBeLoad.url);
					else*/
						saveCache(bitmap,imageToBeLoad.url);
					
					if(imageToBeLoad.bigImage)
						saveBigIamgeCache(bitmap, imageToBeLoad.url);
					
//					imageCache.put(/* f.getPath() */url.hashCode() + "",
//							bitmapRef);
					return bitmap;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		// @Override
		// protected void onPostExecute(Bitmap result) {
		// //// if(imageView instanceof ImageView)
		// //
		// // ImageDownloader.this.type=type;
		// // if(imageView instanceof CImageView)
		// //// ((CImageView)imageView).code = url.hashCode() ;
		// // download( url, imageView,
		// // thumbnailReponseHandler);
		// //
		//
		// }

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			try {
				if (isCancelled()) {
					bitmap = null;
				}
				if (bitmap == null) {

					return;
				}

//				if (downloadFor == 1 || downloadFor == 2) 
				{
					
					{

						// @Override
						// public void run()
						{
							try {
								
								Vector<ImageToBeLoad> t = new Vector<ImageDownloader.ImageToBeLoad>() ;
								for (int i = 0; i < imagesView.size(); i++) {
									ImageToBeLoad beLoad = imagesView.get(i) ;
									if(beLoad.url.hashCode()==imageToBeLoad.url
										.hashCode()){
										if (beLoad.imageView != null) {
											setImage(beLoad.imageView, bitmap, null,imageToBeLoad.type,true,imageToBeLoad.url);
										}
										t.add(beLoad) ;
									}
								}
								for (int i = 0; i < t.size(); i++) {
									imagesView.remove(t.get(i)) ;
								}
								t.clear();
								
								/*ImageView cImageView = images.remove(imageToBeLoad.url
										.hashCode());
								if (cImageView != null) {
									setImage(cImageView, bitmap, null,imageToBeLoad.type,true,imageToBeLoad.url);
								}*/
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}
				ImageToBeLoad imageToBeLoad = linksForFile.poll();
				if (imageToBeLoad != null /* && task.getStatus()==Status.FINISHED */) {
					loadImageRequest = new LoadImageFromFile();
					loadImageRequest.execute(imageToBeLoad);
				}

			} catch (Exception e) {
				// TODO: handle exception
			} catch (OutOfMemoryError e) {
				// TODO: handle exception
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	public boolean isFromCache() {
		try {
			// System.out.println("----image cache size : "+imageCache.size());
			String filename = String.valueOf(url.hashCode());

			/*String extension = MimeTypeMap.getFileExtensionFromUrl(url);

			if (url.indexOf(".") == -1)
				extension = extensionDefault;// "jpg" ;
			if (extension == null || extension.trim().length() <= 0) {
				try {
					extension = url.substring(url.lastIndexOf(".") + 1,
							url.length());
				} catch (Exception e) {
					extension = extensionDefault;// "jpg" ;
				}
			}*/
			filename = filename + "." + extensionDefault;
			CompressImage compressImage = null;
			File f = new File(cacheDir, filename);
			Bitmap bitmap = null;
			BitmapCache bitmapRef = (BitmapCache) imageCache
					.get(f.getPath());

			if (bitmapRef != null && bitmapRef != null)
				bitmap = bitmapRef.bitmap;
			if (bitmap != null && !bitmap.isRecycled()) {

				if (type != TYPE_VIDEO) {
					imageView.setBackgroundDrawable(null);
					imageView.setImageBitmap(bitmap);
				} else if (type == TYPE_IMAGE) {
					{
						imageView.setBackgroundDrawable(null);
						// if(downloadFor==3){
						// if(bitmap.getHeight()>BusinessProxy.sSelf.displayheight-600)
						// bitmap = Bitmap.createScaledBitmap(bitmap,
						// bitmap.getWidth()/2, bitmap.getHeight()/2, false) ;
						// }
						imageView.setImageBitmap(bitmap);
					}
				} else {
					Drawable d = new BitmapDrawable(imageView.getContext()
							.getResources(), bitmap);

					imageView.setBackgroundDrawable(d);
				}
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	public Bitmap getCacheBitmap() {
		try {
			String filename = String.valueOf(url.hashCode());

			/*String extension = MimeTypeMap.getFileExtensionFromUrl(url);

			if (url.indexOf(".") == -1)
				extension = extensionDefault;// "jpg" ;
			if (extension == null || extension.trim().length() <= 0) {
				try {
					extension = url.substring(url.lastIndexOf(".") + 1,
							url.length());
				} catch (Exception e) {
					extension = extensionDefault;// "jpg" ;
				}
			}*/
			filename = filename + "." + extensionDefault;
			CompressImage compressImage = null;
			File f = new File(cacheDir, filename);
			Bitmap bitmap = null;
			BitmapCache bitmapRef = (BitmapCache) imageCache
					.get(f.getPath());

			if (bitmapRef != null && bitmapRef != null)
				bitmap = bitmapRef.bitmap;
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	Animation userThumb,image ;
	private void startImageAnimition(ImageView imageView, int type) {
		if (animition) {
			
			if(log)
			System.out.println("---animition type-"+type);
			switch (type) {
			case TYPE_THUMB_BUDDY:
//				Utilities.startAnimition(imageView.stagetContext(), imageView,
//						userThumb);
				userThumb = buddy.poll() ;
				
				if(userThumb==null){
					userThumb = AnimationUtils.loadAnimation(imageView.getContext(), R.anim.fade);
					RTAnimationListener animationListener = new RTAnimationListener() ;
					animationListener.imageView = imageView ;
					animationListener.animationList = buddy;
					userThumb.setAnimationListener(animationListener) ;
				}
				
				
				imageView.startAnimation(userThumb) ;
				break;
			case TYPE_VIDEO:
//				Utilities.startAnimition(imageView.getContext(), imageView,
//						 android.R.anim.slide_in_left);
				image = animitionImage.poll() ;
				if(image==null){
					image = AnimationUtils.loadAnimation(imageView.getContext(), android.R.anim.fade_in);
					RTAnimationListener animationListener = new RTAnimationListener() ;
					animationListener.imageView = imageView ;
					animationListener.animationList = animitionImage;
					image.setAnimationListener(animationListener) ;
				}
				imageView.startAnimation(image) ;
				break;
			case TYPE_IMAGE:
//				Utilities.startAnimition(imageView.getContext(), imageView,
//						 android.R.anim.slide_in_left);
				if(imageView instanceof CImageView && ((CImageView)imageView).aniType!=-1)
					image = animitionImageWave .poll() ;
				else
				image = animitionImage.poll() ;
				
				if(image==null){
					if(imageView instanceof CImageView && ((CImageView)imageView).aniType!=-1)
						image = AnimationUtils.loadAnimation(imageView.getContext(),  android.R.anim.slide_in_left);
					else
					image = AnimationUtils.loadAnimation(imageView.getContext(), android.R.anim.fade_in);
					RTAnimationListener animationListener = new RTAnimationListener() ;
					animationListener.imageView = imageView ;
					if(imageView instanceof CImageView && ((CImageView)imageView).aniType!=-1)
						animationListener.animationList = animitionImageWave ;//.poll() ;
					else
					 animationListener.animationList = animitionImage;
					image.setAnimationListener(animationListener) ;
				}
				imageView.startAnimation(image) ;
				break;
				default:
//					Utilities.startAnimition(imageView.getContext(), imageView,
//							 android.R.anim.slide_in_left);
					image = animitionImage.poll() ;
					if(image==null){
						image = AnimationUtils.loadAnimation(imageView.getContext(), android.R.anim.fade_in);
						RTAnimationListener animationListener = new RTAnimationListener() ;
						animationListener.imageView = imageView ;
						animationListener.animationList = animitionImage;
						image.setAnimationListener(animationListener) ;
					}
					imageView.startAnimation(image) ;
					break;
			}

		}
	}
	
	class RTAnimationListener implements AnimationListener
	{

		public ImageView imageView ;
		public LinkedList<Animation> animationList ;
		@Override
		public void onAnimationEnd(Animation animation) {
			animationList.add(animation);
			imageView.clearAnimation() ;
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
	}
public  Bitmap getRoundedBitmap(Bitmap inpBitmap, int pixels, int color) {
		
		pixels=8;//;
//		Matrix matrix = new Matrix();
//		matrix.postScale(0.5f, 0.5f);
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0,50, 50, matrix, true);

//		int w = inpBitmap.getWidth();int h = inpBitmap.getHeight();
//	    Bitmap inpBitmap = bitmap;
	    int width = 0;
	    int height = 0;
	    width = inpBitmap.getWidth();
	    height = inpBitmap.getHeight();

	    if (width <= height) {
	        height = width;
	    } else {
	        width = height;
	    }

	    Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, width, height);
	    final RectF rectF = new RectF(0, 0, width, height);
	    final RectF rectF2 = new RectF(2, 2, width-2, height-2);
	    final float roundPx = pixels;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
//	    paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f));
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//	    paint.setMaskFilter(null);
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	    int  centreX = (inpBitmap.getWidth()  - inpBitmap.getWidth()) /2 ;
		int  centreY = (inpBitmap.getHeight() - inpBitmap.getHeight()) /2 ;
	    canvas.drawBitmap(inpBitmap, centreX, centreY, paint);
	    paint.setColor(Color.WHITE);
	    paint.setStyle(Paint.Style.STROKE);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawRoundRect(rectF2, roundPx, roundPx, paint);
	    return output;
	}

}
