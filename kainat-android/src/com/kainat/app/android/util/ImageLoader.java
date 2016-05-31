package com.kainat.app.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.HttpConnectionHelper;

public final class ImageLoader implements Runnable {
	
//	public static final boolean NO_LIMIT = false;//Commented by mahesh on 24th Feb, as it was not used. May be we use this in future
	
	public static final int LAOD_IMAGES_FORM_RT = 1;
	public static final int LAOD_IMAGES_FORM_XML = 2;

	private static final String TAG = ImageLoader.class.getSimpleName();
//	private static final String BASE_URL = "http://" + Urls.WAP_HOST + "/mypage/user/%s";
	
	private static final String BASE_URL = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/pic?format=150x150&user=%s";
	
	private static final String POST_DATA = Utilities.getPhoneIMEINumber() + ";" + BusinessProxy.sSelf.getUserId();
	private static final int CHUNK_LENGTH = 1024;
	private static final Object lock = new Object();
	private int mRequestType;
	public static List<ThumbnailImage> mQueue = new ArrayList<ThumbnailImage>();
	
	private ThumbnailReponseHandler mParent;
	private boolean mRunning = true;
	private boolean mIsReset = false;
	private Thread mThread;
//	public static  Map<String, ThumbnailImage> ThumbnailImage.cache = new HashMap<String, ThumbnailImage>();
	private static ImageLoader sSelf = new ImageLoader();
	private ThumbnailImage request = null;
	private boolean buddyThumb = false ;
	private static long longRemoveTime = 0 ;//System.currentTimeMillis();
	private long buddyThumbRefreshTime = 1000*10;//1000*60*5 ; //five minute
	private int refreshCtr = 0 ;
	private boolean log = false;
	public boolean isBuddyThumb() {
		return buddyThumb;
	}

	public void setBuddyThumb(boolean buddyThumb) {
		this.buddyThumb = buddyThumb;
	}
	private ImageLoader() {
		mThread = new Thread(this);
		mThread.start();
	}

	public static ImageLoader getInstance(ThumbnailReponseHandler parent) {
		if (null == sSelf) {
			sSelf = new ImageLoader();
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
				if (log) {
					Log.i(TAG, "Load   for - " + request.mName);
				}
				if (null != ThumbnailImage.cache.get(request.mName)) {
					if (log) {
						Log.i(TAG, "Loaded for - " + request.mName);
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
				
				case LAOD_IMAGES_FORM_XML: {
					if(/*NO_LIMIT && */isThumbFromCash(request.mName))
					{
						{
							byte[] data1 =getThumbFromCash(request.mName);
							if(data1 != null)
							{
								request.mBitmap = BitmapFactory.decodeByteArray(data1, 0, data1.length);
								mParent.onThumbnailResponse(request, null);
								data1 = null ;
							}
						}
//						continue;
					}
					else
					{
						try
						{
		//					System.out.println("-------request.mName---" + request.mName);
							HttpConnectionHelper helper = new HttpConnectionHelper(request.mName);
							helper.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
							helper.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
							int responsecode = helper.getResponseCode();
							if(responsecode != 200)
							{
								helper = new HttpConnectionHelper(request.mName);
								helper.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
								helper.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
								responsecode = helper.getResponseCode();
								
							}
							switch (responsecode) {
							case HttpURLConnection.HTTP_OK:
							case HttpURLConnection.HTTP_ACCEPTED:
								InputStream is = helper.getInputStream();
								ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
								byte[]data = new byte[CHUNK_LENGTH];
								int len;
								while (-1 != (len = is.read(data))) {
									buffer.append(data, 0, len);
								}
								data = buffer.toByteArray();
		//						System.out.println("---------------------------------2"+(data.length/1024));
								request.mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		//						System.out.println("---------------------------image wxh------"+request.mBitmap.getWidth()+"x"+request.mBitmap.getHeight());
								if (null != mParent) {
									if (log) {
										Log.i(TAG, "Loaded for - " + request.mName);
									}
									storeBitmap(request.mName, request);
		//							if(NO_LIMIT)
		//								saveThumb(request.mName, data);
									//if(refreshCtr++ > 3)
									{
										refreshCtr = 0 ;
										mParent.onThumbnailResponse(request, data);
									}
									data = null ;
								}
		//						System.out.println("---------------------------------3");
								break;
							}
							}
					catch(Exception ex)
					{
						refreshCtr = 0 ;
						mParent.onThumbnailResponse(request, null);
					}
				}
				}
					break;
				default: {

					HttpConnectionHelper helper = null;
//					System.out.println("-------thumb name : "+request.mName);
					if(request.mName.indexOf("http:") == -1)
						 helper = new HttpConnectionHelper(String.format(BASE_URL, request.mName));					
					else						
						 helper = new HttpConnectionHelper(String.format(request.mName));
					helper.setRequestMethod(HttpPost.METHOD_NAME);
					helper.setPostData(POST_DATA.getBytes());
					int responseCode = helper.getResponseCode();
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
//						System.out.println("---------------------------image wxh------"+request.mBitmap.getWidth()+"x"+request.mBitmap.getHeight());
						if (null != mParent) {
							if (log) {
								Log.i(TAG, "Loaded for - " + request.mName);
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
//				io.printStackTrace() ;
			} catch (Exception ex) {
//				ex.printStackTrace() ;
				if (log) 
				{
					Log.e(TAG, "run -- " + ex.getMessage(), ex);
				}
			} catch (OutOfMemoryError th) {
				System.gc();
				if (log) {
					Log.e(TAG, "run -- Memory error ", th);
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

	Vector<Bitmap> cacheToRecycle ;
	public void removefirst(){
//		if(Logger.ENABLE)Logger.debug(TAG, "-------removefirst-------");
		Iterator<String> set= ThumbnailImage.cache.keySet().iterator();
		String key;
		String smallKey = null;
		long l = System.currentTimeMillis() ;
		while(set.hasNext()){
			key = set.next();
			ThumbnailImage bitmap = ThumbnailImage.cache.get(key);
			if(bitmap.time < l){
				l = bitmap.time ;
				smallKey = key;
			}			
		}
		if(smallKey !=null){
//			ThumbnailImage bitmap = 
				ThumbnailImage.cache.remove(smallKey);
//			ThumbnailImage.cacheToRecycle.add(bitmap.mBitmap);
//			bitmap.mBitmap.recycle() ;
			if(log)Log.i(TAG, "removing.--"+smallKey);
		}
		
//		
//		if(ThumbnailImage.cacheToRecycle.size() > 200){
//			cacheToRecycle = ThumbnailImage.cacheToRecycle;
//			ThumbnailImage.cacheToRecycle.removeAllElements() ;
//			new Thread(new Runnable() {				
//				@Override
//				public void run() {
//					for (int i = 0; i < cacheToRecycle.size(); i++) {
//						cacheToRecycle.elementAt(i).recycle() ;
//						System.out.println("-------recycle image loader----");
//					}					
//				}
//			}).start();
//		}
	}
	
	public Bitmap getCacheBitmap(String name) {
		ThumbnailImage request = (ThumbnailImage) ThumbnailImage.cache.get(name);
		if (request != null){
//			System.out.println("--is not null---getThumbnailImage.cacheBitmap--------"+name);
			return request.mBitmap;
		}
//		else if(NO_LIMIT)
//		{
//			byte[] data1 =getThumbFromCash(name);
//			if(data1 != null)
//			{
//				return BitmapFactory.decodeByteArray(data1, 0, data1.length);			
//			}
//		}
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
	private void  parseThumbnail(byte[] response, ThumbnailImage request) {
		try {
			request.mBitmap = BitmapFactory.decodeByteArray(response, 0,
					response.length);			
		}catch (Exception e) {
//			e.printStackTrace();			
		}
	}

	private void parseThumbnailXXX(byte[] response, ThumbnailImage request) {
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
//		System.out.println("-------thumb size : "+request.mBitmap.getHeight()+"x"+request.mBitmap.getHeight());
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
//				System.out.println("########################EXCEPTION HANDLED#############################");
//				e.printStackTrace() ;
			}
		}
		response = null;
	}
	public static boolean isThumbFromCash(String name)
	{
		if(name.length() > 15)
		name = name.substring(name.length()-15, name.length()) ;
		try {
			name = HexStringConverter.getHexStringConverterInstance().stringToHex(name);
		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
		}
		String pathd = String.format("%s%s%s.th", BusinessProxy.sSelf.cashDir, File.separator,
				name);
		
		try
		{
			return (new File(pathd)).exists();
		}catch (Exception e) 
		{		
//			e.printStackTrace();
		}
		return false ;
	}
	public static byte[] getThumbFromCash(String name)
	{
//		System.out.println("--------getThumbFromCash-name=="+name);
		if(name.length() > 15)
		name = name.substring(name.length()-15, name.length()) ;
//		name = HexStringConverter.getHexStringConverterInstance().hexToString(name);
		try {
			name = HexStringConverter.getHexStringConverterInstance().stringToHex(name);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
//		System.out.println("--------saveThumb-name=="+name);
		String pathd = String.format("%s%s%s.th", BusinessProxy.sSelf.cashDir, File.separator,
				name);
		
		try
		{
//			System.out.println("--------getThumbFromCash-=="+pathd);
			FileInputStream fin = new FileInputStream(pathd);
			byte[] entrys =new byte[fin.available()];
			fin.read(entrys);
			fin.close();
			return entrys ;
		}catch (Exception e) 
		{		
//			e.printStackTrace();
		}
		return null ;
	}
	
	private static File cacheDir;

	private static File getCacheDirectory(Context context) {

		if (cacheDir != null)
			return cacheDir;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, Constants.imagePath);
		} else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}
	public static void saveThumb(String name,byte array[])
	{
//		System.out.println("--------saveThumb-name=="+name);
		if(name.length() > 15)
		name = name.substring(name.length()-15, name.length()) ;
//		System.out.println("--------saveThumb-name=="+name);
		try {
			name = HexStringConverter.getHexStringConverterInstance().stringToHex(name);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
		String pathd = String.format("%s%s%s.th", BusinessProxy.sSelf.cashDir, File.separator,
				name);
		try{
//			System.out.println("--------saveThumb-=="+pathd);
			FileOutputStream fout = new FileOutputStream(pathd);
			fout.write(array);
			fout.flush();
			fout.close();
		}catch (Exception e) {
//			e.printStackTrace();
		}	
	}	
}