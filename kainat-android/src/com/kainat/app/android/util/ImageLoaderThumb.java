package com.kainat.app.android.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.HttpConnectionHelper;

public final class ImageLoaderThumb implements Runnable {
	public static final int LAOD_IMAGES_FORM_RT = 1;
	public static final int LAOD_IMAGES_FORM_XML = 2;

	private static final String TAG = ImageLoaderThumb.class.getSimpleName();
	private static final String BASE_URL = "http://" + Urls.WAP_HOST + "/mypage/user/%s";
	private static final String POST_DATA = Utilities.getPhoneIMEINumber() + ";" + BusinessProxy.sSelf.getUserId();
	private static final int CHUNK_LENGTH = 1024;
	private static final Object lock = new Object();
	private int mRequestType;

	private ThumbnailReponseHandler mParent;
	private boolean mRunning = true;
	private boolean mIsReset = false;
	private List<ThumbnailImage> mQueue = new ArrayList<ThumbnailImage>();
	private Thread mThread;
//	private Map<String, ThumbnailImage> ThumbnailImage.cache = new HashMap<String, ThumbnailImage>();
	private static ImageLoaderThumb sSelf = new ImageLoaderThumb();
	private ThumbnailImage request = null;
	private boolean buddyThumb = false ;
	private static long longRemoveTime = 0 ;//System.currentTimeMillis();
	private long buddyThumbRefreshTime = 1000*10;//1000*60*5 ; //five minute
	public boolean isBuddyThumb() {
		return buddyThumb;
	}

	public void setBuddyThumb(boolean buddyThumb) {
		this.buddyThumb = buddyThumb;
	}
	private ImageLoaderThumb() {
		mThread = new Thread(this);
		mThread.start();
	}

	public static ImageLoaderThumb getInstance(ThumbnailReponseHandler parent) {
		if (null == sSelf) {
			sSelf = new ImageLoaderThumb();
		}
		sSelf.setParent(parent);
		sSelf.setBuddyThumb(false);
		return sSelf;
	}

	public void setParent(ThumbnailReponseHandler parent) {
		this.mParent = parent;
	}

	public void setRequestType(int aRequestType) {
		mRequestType = aRequestType;
	}

	public int getRequestType() {
		return mRequestType;
	}

	public int cacheSize() {
		if (ThumbnailImage.cache != null)
			return ThumbnailImage.cache.size();
		return 0;
	}

	public ThumbnailImage getThumb(String name) {
		ThumbnailImage image = (ThumbnailImage) ThumbnailImage.cache.get(name);
		return image;
	}

	public void cleanAndPutRequest(String[] nameList, int[] index) {
		if (null == nameList || null == index || nameList.length != index.length) {
			return;
		}
		if (Logger.ENABLE) {
			Logger.verbose(TAG, "Image loader RESET");
		}
		synchronized (lock) {
			mQueue.clear();
			for (int i = 0; i < nameList.length; i++) {
				if (null != nameList[i])
					mQueue.add(new ThumbnailImage(nameList[i], index[i]));
			}
			mThread.interrupt();
			mIsReset = true;
		}
		notifyThread();
	}

	public void cleanRequestList() {
		synchronized (lock) {
			mQueue.clear();
			mThread.interrupt();
		}
		notifyThread();
	}

	private void notifyThread() {
		synchronized (this) {
			notify();
		}
	}

	public void run() {

		while (mRunning) {
			try {
				if (mQueue.isEmpty()) {
					synchronized (this) {
						wait();
					}
				}
				synchronized (lock) {
					if (mQueue.isEmpty()) {
						continue;
					}
					request = mQueue.get(0);
				}
				if (Logger.ENABLE) {
					Logger.verbose(TAG, "Load   for - " + request.mName);
				}
				if (null != ThumbnailImage.cache.get(request.mName)) {
					if (Logger.ENABLE) {
						Logger.verbose(TAG, "Loaded for - " + request.mName);
					}
					boolean old = false ;
					if (null != mParent) {
						ThumbnailImage image = (ThumbnailImage) ThumbnailImage.cache.get(request.mName);
						
						if((System.currentTimeMillis() - image.time) > buddyThumbRefreshTime && buddyThumb)//image.mTime!=null && image.mTime.length()>0 ){
						{
//							image = ThumbnailImage.cache.remove(request.mName);
//							if(image != null)
//								System.out.println("removing old thumb--------------------"+image.mName);
							old = true;
						}
						//if(!old)
						{
						request.mBitmap = image.mBitmap;//ThumbnailImage.cache.get(request.mName);
						request.mStatus = image.mStatus;//ThumbnailImage.cache.get(request.mName);
//						if(!old)
							mParent.onThumbnailResponse(request, null);
						}
					}
					if(!old)
					continue;
				}
//				System.out.println("--------------mRequestType----"+mRequestType);
				switch (mRequestType) {
				
				default: {
//					System.out.println("------thumb---LAOD_IMAGES_FORM_XML---------------"+BASE_URL);
					HttpConnectionHelper helper = null;
					if(request.mName.indexOf("http:") == -1)
						 helper = new HttpConnectionHelper(String.format(BASE_URL, request.mName));					
					else						
						 helper = new HttpConnectionHelper(String.format(request.mName));
					helper.setRequestMethod(HttpPost.METHOD_NAME);
					helper.setPostData(POST_DATA.getBytes());
					int responseCode = helper.getResponseCode();
//					System.out.println("------thumb--LAOD_IMAGES_FORM_XML-----responseCode----------"+responseCode);
					
					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
					case HttpURLConnection.HTTP_ACCEPTED:
						String contentEncoding = helper.getHttpHeader("Content-Encoding");
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
//						System.out.println("---------------------------------2"+(buffer.length()));
						parseThumbnail(buffer.buffer(), request);
						if (null != mParent) {
							if (Logger.ENABLE) {
								Logger.verbose(TAG, "Loaded for - " + request.mName);
							}
							storeBitmap(request.mName, request);
							mParent.onThumbnailResponse(request, null);
//							System.out.println("---------------------------------3");
						}
						break;
					}
				}
					break;
				}
			} catch (InterruptedException io) {
			} catch (Exception ex) {
				if (Logger.ENABLE) {
					Logger.error(TAG, "run -- " + ex.getMessage(), ex);
				}
			} catch (OutOfMemoryError th) {
				System.gc();
				if (Logger.ENABLE) {
					Logger.error(TAG, "run -- Memory error ", th);
				}
			} finally {
				synchronized (lock) {
					if (!mIsReset && !mQueue.isEmpty()) {
						mQueue.remove(0);
					}
					mIsReset = false;
				}
			}
		}
	}

	private void storeBitmap(String name, ThumbnailImage bitmap) {
		if (ThumbnailImage.cache.size() >= Constants.MAX_PICTURE_CACHE_SIZE) {
//			String rem = ThumbnailImage.cache.keySet().iterator().next();
			//ThumbnailImage.cache.remove(rem);
			removefirst();
		}
		bitmap.time = System.currentTimeMillis() ;
		ThumbnailImage.cache.put(name, bitmap);
		
	}

	public void removefirst(){
//		if(Logger.ENABLE)Logger.debug(TAG, "-------removefirst-------");
		Iterator<String> set= ThumbnailImage.cache.keySet().iterator();
		String key;
		String smallKey = null;
		long l = System.currentTimeMillis() ;
		while(set.hasNext()){
			key = set.next();
			ThumbnailImage bitmap = ThumbnailImage.cache.get(key);
			if(Logger.ENABLE)Logger.debug(TAG, "----l ---time-------"+l );
			if(Logger.ENABLE)Logger.debug(TAG, "---bitmap---time-------"+bitmap.time);
			
			if(bitmap.time < l){
				l = bitmap.time ;
				smallKey = key;
			}			
		}
		if(smallKey !=null){
			ThumbnailImage.cache.remove(smallKey);
			if(Logger.ENABLE)Logger.debug(TAG, "removing.--"+smallKey);
			if(Logger.ENABLE)Logger.debug(TAG, smallKey);
		}
	}
	
	public Bitmap getCacheBitmap(String name) {
		ThumbnailImage request = (ThumbnailImage) ThumbnailImage.cache.get(name);
		if (request != null){
//			System.out.println("--is not null---getThumbnailImage.cacheBitmap--------"+name);
			return request.mBitmap;
		}
//		System.out.println("--is null---getThumbnailImage.cacheBitmap--------"+name);
		return null;
	}

	/*private void parseThumbnail(byte[] response, ThumbnailImage request) {
		// 1 byte - status online/offline
		// 4 - Length + data
		int offset = 0;
		// Read online/Offline Status in 1 byte
		request.mStatus = response[offset++];
		// Read length of thumb nail in 4 bytes
		int len = Utilities.bytesToInt(response, 1, 4);
		offset += 4;
		request.mBitmap = BitmapFactory.decodeByteArray(response, offset, len);
		response = null;
	}

	public static final class ThumbnailImage {
		public String mName;
		public int mIndex;
		public byte mStatus;
		public Bitmap mBitmap;
		public long time;
		public ThumbnailImage(String name, int index) {
			this.mName = name;
			this.mIndex = index;
		}
	}*/
	public ThumbnailImage getCacheData(String name) {
		ThumbnailImage request = (ThumbnailImage) ThumbnailImage.cache.get(name);
		if (request != null)
			return request;
		return null;
	}
	private void parseThumbnail(byte[] response, ThumbnailImage request) {
		// 1 byte - status online/offline
		// 4 - Length + data
		int offset = 0;
//		System.out.println("-----mName---------->"+request.mName);
		// Read online/Offline Status in 1 byte
		request.mOnOffLine = response[offset++];
		// Read length of thumb nail in 4 bytes
		int len = Utilities.bytesToInt(response, 1, 4);
		offset += 4;
		request.mBitmap = BitmapFactory.decodeByteArray(response, offset, len);
//		if(request.mBitmap == null)
//			System.out.println("------------request.mBitmap is null------------------");
		if(buddyThumb){
			try{
//				System.out.println("-----response-len---------->"+response.length);
//				System.out.println("------len1---------->"+len);
				offset += len;
				
				len = Utilities.bytesToInt(response, offset, 4);
				offset += 4;
//				System.out.println("------len2---------->"+len);
//				System.out.println("------offset---------->"+offset);
				request.mStatus = new String(response, offset, len, Constants.ENC) ;
//				System.out.println("------request.mStatus---------->"+request.mStatus);
				offset += len;
				
				len = Utilities.bytesToInt(response, offset, 4);
				offset += 4;
//				System.out.println("------len3---------->"+len);
//				System.out.println("------offset---------->"+offset);
				request.mContent = new String(response, offset, len, Constants.ENC) ;
//				System.out.println("------mContent---------->"+request.mContent);
				offset += len;
				
				len = Utilities.bytesToInt(response, offset, 4);
				offset += 4;
//				System.out.println("------len4---------->"+len);
//				System.out.println("------offset---------->"+offset);
				request.mTime = new String(response, offset, len, Constants.ENC) ;
//				System.out.println("------mTime---------->"+request.mTime);
			}catch (Exception e) {
				System.out.println("########################EXCEPTION HANDLED#############################");
				e.printStackTrace() ;
			}
		}
		response = null;
	}

}