package com.kainat.app.android.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.kainat.app.android.bean.MediaPost;
import com.kainat.app.android.bean.SharingContent;
import com.kainat.app.android.bean.UserStatus;
import com.kainat.app.android.core.BusinessProxy;

public class MediaSharing {

	public static final String extensionDefault = "jpg" ;
	public static Map<String, SoftReference<Bitmap>> imageCache;
	private static BitmapDownloaderTask task;
	private static final String POST_DATA = Utilities.getPhoneIMEINumber()
			+ ";" + BusinessProxy.sSelf.getUserId();
	
	SharingContent sharingContent ;
	public static  Hashtable<Integer, UserStatus>  buddyInfo = new Hashtable<Integer, UserStatus>() ;//= new SoftReference<Bitmap>(bitmap);
	private int type =0 ;
	Vector<String> contentUrl = new Vector<String>();
	int index = 0;
	public MediaSharing(MediaPost mediaPost) {
		if (imageCache == null)
			imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	
	public void download(String url,
			ThumbnailReponseHandler thumbnailReponseHandler,int type) {
		this.type=type;
		download( url,  
				 thumbnailReponseHandler);
	}
	public void download(String url, 
			ThumbnailReponseHandler thumbnailReponseHandler) {
		if(sharingContent.content.size()>0){
			Enumeration<String> fileUrl = sharingContent.content.keys();
			while(fileUrl.hasMoreElements()){
				contentUrl.add(fileUrl.nextElement()) ;
			}
			if ((task == null || task.getStatus() == Status.FINISHED)) {
				task = new BitmapDownloaderTask();
				task.execute(contentUrl.get(index++));
			}
		}
	}

	private static File cacheDir;

	private static File getCacheDirectory(Context context) {

		if (cacheDir != null)
			return cacheDir;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, Constants.imagePathPost);
		} 
		//else
			//cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}

	private static void writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;
		if(f!=null&&!f.exists())
		try {
			f.createNewFile();
			out = new FileOutputStream(f);
			bmp.compress(Constants.COMPRESS_TYPE, Constants.COMPRESS, out);
		} catch (Exception e) {
			System.out.println("------image download : "+e.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}
	private static void writeFile(byte[] data, File f) {
		FileOutputStream out = null;
		if(f!=null&&!f.exists())
		try {
			f.createNewFile();
			out = new FileOutputStream(f);
			out.write(data) ;
		} catch (Exception e) {
			System.out.println("------image download : "+e.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}

	public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		@Override
		protected Bitmap doInBackground(String... params) {
			url = (String) params[0];//url.mName.indexOf("http:") == -1
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			if (url != null /* && task.getStatus()==Status.FINISHED */) {
				try{
					task = new BitmapDownloaderTask();
					task.execute(contentUrl.get(index++));
				}catch (Exception e) {
					
				}
			}
		}
	}


	private static final int CHUNK_LENGTH = 1024;
	// the actual download code
	static Bitmap downloadBitmap(String url) {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(params);
//		params.setParameter("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
//		params.setParameter("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		
		final HttpGet getRequest = new HttpGet(url);
		try {
			// getRequest.setPostData(POST_DATA.getBytes());
			getRequest.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
			getRequest.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			
			System.out.println("------------statusCode image download ----------------- : "+statusCode);
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					byte[] data = Utilities.readBytes(inputStream);// new


					String filename = String.valueOf(url.hashCode());
					
					String extension = MimeTypeMap.getFileExtensionFromUrl(url);
					if(url.indexOf(".")==-1)
						extension =  extensionDefault;//"jpg" ;
					if(extension==null||extension.trim().length()<=0){
						try{
							extension = url.substring(url.lastIndexOf(".")+1, url.length());
						}catch (Exception e) {
							extension =  extensionDefault;//"jpg" ;
						}
					}
					filename = filename+"."+extension ;
					
					File f = new File(cacheDir, filename);
					writeFile(data, f) ;
				} catch (Exception e) {
					e.printStackTrace();

				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		
			getRequest.abort();

		} finally {
			if (client != null) {
			}
		}
		return null;
	}
	
	
}
