package com.kainat.app.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

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
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.kainat.app.android.bean.VideoUrl;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.inf.HttpSynchInf;

public class VDownloader {

	private static BitmapDownloaderTask task;
	static ThumbnailReponseHandler thumbnailReponseHandler;
	private static final String POST_DATA = Utilities.getPhoneIMEINumber()
			+ ";" + BusinessProxy.sSelf.getUserId();
	private int type = 0;
	Context context ;
	private static VDownloader downloader ;
	private  String url;
	private static HttpSynchInf httpSynchInfRefreshCureentActivity;
	public static void setHttpSynchRefreshCurrentView(HttpSynchInf httpSynchInf) {
		httpSynchInfRefreshCureentActivity = httpSynchInf;
	}
	private VDownloader() {

	}
	private int state=0;
	public static VDownloader getInstance(){
		if(downloader==null){
			downloader = new VDownloader();
		}
		return downloader ;
	}
	public static LinkedList<String> links = new LinkedList<String>();

	public int getState(){
		return state ;
	}
	public String getUrl(){
//		System.out.println("--------------url"+url);
		return url ;
	}
	VideoUrl videoUrl;
	public void download(VideoUrl videoUrl, int type,Context context) {
		this.context =context ;
		this.type = type;
		this.videoUrl = videoUrl ;
//		this.url=url ;
		if(videoUrl.url.endsWith(Constants.COL_SEPRETOR))
			videoUrl.url = videoUrl.url.substring(0,videoUrl.url.length()-1) ;
		downloadContent(videoUrl.url);
	}

	public void downloadContent(String url
			) {
			getCacheDirectory();

		this.thumbnailReponseHandler = thumbnailReponseHandler;

		{
//			System.out.println("VDOWNLODER---------URL-----: "+url);
			// Caching code right here
			String filename = String.valueOf(url.hashCode());
			
			String extension = MimeTypeMap.getFileExtensionFromUrl(url);
			if(extension==null||extension.trim().length()<=0){
				try{
					extension = url.substring(url.lastIndexOf(".")+1, url.length());
				}catch (Exception e) {
					extension = "3gp" ;
				}
			}
			filename = filename+"."+extension ;
			File f = new File(cacheDir, filename);

			if (f.exists()) {
//				System.out.println("VDOWNLODER---------updating tabe after download conent-file found :)-----");
				updateDownload( url.hashCode(),1);
			} else //if( state==0) 
			{
				updateDownload(  url.hashCode(),0);
				if (links.contains(url))
					links.remove(url);
				links.addFirst(url);
				if ((task == null || task.getStatus() == Status.FINISHED)) {
					updateDownload(videoUrl.url.hashCode(), 0);
					task = new BitmapDownloaderTask();
					task.execute(links.poll());
				}
				if (task != null )
						{
//							System.out.println("VDOWNLODER---------TASK STATUS-----: "+task.getStatus());
						}
			}
		}
	}
	public  boolean cancel(){
		if ((task != null && task.getStatus() == Status.RUNNING)){
			task.cancel(true) ;
			url=null;
			updateDownload(videoUrl.url.hashCode(), 0);
			return true ;
		}
		else
			return false ;
	}
	public static boolean isRunning(){
		if ((task != null && task.getStatus() == Status.RUNNING))
			return true ;
		else
			return false ;
	}
	public String getPlayUrl(String url){
//		System.out.println("VDOWNLODER---------getPlayUrl-----"+url);
		if(url!=null && url.startsWith("/"))
			return url ;
		String filename = String.valueOf(url.hashCode());
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if(extension==null||extension.trim().length()<=0){
			try{
				extension = url.substring(url.lastIndexOf(".")+1, url.length());
			}catch (Exception e) {
				extension = "3gp" ;
			}
		}
		filename = filename+"."+extension ;
		File f = new File(cacheDir, filename);
//		System.out.println("VDOWNLODER---------getPlayUrl-f.getAbsolutePath()----"+f.getAbsolutePath());
		return f.getAbsolutePath() ;
	}
//	public boolean checkAndUpdate(String url){
//		String filename = String.valueOf(url.hashCode());
//		File f = new File(cacheDir, filename);
//		if (f.exists()) {
//			System.out.println("VDOWNLODER---------checkAndUpdate conent-file found :)-----");
//			updateDownload( url.hashCode(),1);
//			links.remove(url);
//			return true;
//		}
//		return false;
//	}
	public boolean check(String url){
		getCacheDirectory();
		String filename = String.valueOf(url.hashCode());
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if(extension==null||extension.trim().length()<=0){
			try{
				extension = url.substring(url.lastIndexOf(".")+1, url.length());
			}catch (Exception e) {
				extension = "3gp" ;
			}
		}
		filename = filename+"."+extension ;
		
		File f = new File(cacheDir, filename);
		if (f.exists()) {
//			System.out.println("---check url exist : "+url);
			return true;
		}
//		else
//			System.out.println("---check url not exist : "+url);
		return false;
	}
	private static File cacheDir;

	private static File getCacheDirectory() {

		if (cacheDir != null)
			return cacheDir;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, Constants.contentVideo);
		}

		if (!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}

	private  void writeFile(byte[] data, File f,int id) {
//		System.out.println("VDOWNLODER---------writefile-----: "+url);
		FileOutputStream out = null;
		if (f != null && !f.exists() && data != null){
			try {
				// if(f.exists())
				f.createNewFile();
				out = new FileOutputStream(f);
				out.write(data);
				updateDownload( id,1);
				links.remove(url);
				
			} catch (Exception e) {
				System.out.println("VDOWNLODER------image download : " + e.getMessage());
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}
		else
		{
			updateDownload( id,0);
			links.remove(url);
		}
		if(httpSynchInfRefreshCureentActivity!=null)
			httpSynchInfRefreshCureentActivity.onResponseSucess("REFRESH VIEW FEOM DOWNLOADER", 2);
	}
	public static byte[] readBytes(InputStream inputStream) throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
//		System.out.println("VDOWNLODER-----byteBuffer is availabel : "+inputStream.available());
		while ((len = inputStream.read(buffer)) != -1 && !task.isCancelled()) {
			byteBuffer.write(buffer, 0, len);
//			System.out.println("VDOWNLODER-----byteBuffer size : "+byteBuffer.size());
		}
		if(task.isCancelled())
			return null;
		// inputStream.close() ;
		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}
	public void updateDownload(int id, int with){
//		System.out.println("VDOWNLODER---------updating tabe after download conent------ :"+id);
		
//		Cursor cursor =context.  getContentResolver().query(
//				MediaContentProvider.CONTENT_URI_INBOX,
//				null,
//				"VIDEO_DOWNLOAD_STATUE_"+videoUrl.index+ " = ?",
//				new String[] { ""+id },
//				MessageTable.INSERT_TIME + " ASC");
//		cursor.moveToFirst();
//		int i=-1;
//		if(cursor.getCount()>0)
//		i =cursor.getInt(cursor.getColumnIndex("VIDEO_DOWNLOAD_STATUE_"+videoUrl.index));
//		cursor.close();
//		System.out.println("VDOWNLODER---------updating tabe after download conent------ with:"+with+" i : "+ i);
//		if(i!=with)
//		{
//			ContentValues values = new ContentValues();
//			values.put("VIDEO_DOWNLOAD_STATUE_"+videoUrl.index, with);// int
//			int updatedRow =context. getContentResolver().update(
//					MediaContentProvider.CONTENT_URI_INBOX,
//					values,
//					"VIDEO_ID_"+videoUrl + "=?",
//					new String[] { ""+id});
//			
//			System.out.println("VDOWNLODER---------updating tabe after download conent------ updatedRow:"+updatedRow);
//		}
		
		/*Cursor cursor =context.  getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX,
				null,
				MessageTable.VIDEO_DOWNLOAD_STATUE_1+ " = ?",
				new String[] { ""+id },
				MessageTable.INSERT_TIME + " ASC");
		cursor.moveToFirst();
		int i=-1;
		if(cursor.getCount()>0)
		i =cursor.getInt(cursor.getColumnIndex(MessageTable.VIDEO_DOWNLOAD_STATUE_1));
		cursor.close();
		System.out.println("VDOWNLODER---------updating tabe after download conent------ with:"+with+" i : "+ i);
		if(i!=with)
		{
			ContentValues values = new ContentValues();
			values.put(MessageTable.VIDEO_DOWNLOAD_STATUE_1, with);// int
			int updatedRow =context. getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX,
					values,
					MessageTable.VIDEO_ID_1 + "=?",
					new String[] { ""+id});
			
			System.out.println("VDOWNLODER---------updating tabe after download conent------ updatedRow:"+updatedRow);
		}
		*/
	}
	public class BitmapDownloaderTask extends AsyncTask<String, Void, byte[]> {
		private String url;

		@Override
		protected byte[] doInBackground(String... params) {
			
			state=1 ;
			url = (String) params[0];
			System.out.println("VDOWNLODER---------doin background-----: "+url);
			VDownloader.this.url=url ;
			return download(params[0]);
		}

		@Override
		protected void onPostExecute(byte[] data) {
			if (isCancelled()) {
				data = null;
			}
//			String url = links.poll();
			links.clear() ;
if(data!=null){
			String filename = String.valueOf(url.hashCode());
			String extension = MimeTypeMap.getFileExtensionFromUrl(url);
			if(extension==null||extension.trim().length()<=0){
				try{
					extension = url.substring(url.lastIndexOf(".")+1, url.length());
				}catch (Exception e) {
					extension = "3gp" ;
				}
			}
			filename = filename+"."+extension ;
			System.out.println("VDOWNLODER---------doPostExecute-filename----: "+filename);
			File f = new File(cacheDir, filename);

			writeFile(data, f,url.hashCode());
			
//			if (url != null) {
//				task = new BitmapDownloaderTask();
//				task.execute(url);
//			}
			
}
VDownloader.this.url=null;
state=0 ;
			System.out.println("VDOWNLODER---------doPostExecute-----: "+url);
		}
	}

	static byte[] download(String url) {
		
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(params);
		System.out.println("VDOWNLODER------------downloader url : "+url);
		final HttpGet getRequest = new HttpGet(url);
		try {
			getRequest.setHeader("RT-APP-KEY",
					"" + BusinessProxy.sSelf.getUserId());
			getRequest.setHeader("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("VDOWNLODER------------downloader statusCode : "+statusCode);
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

					return readBytes(inputStream);

				} catch (Exception e) {
					e.printStackTrace();

					return null;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			return null;
		} finally {
			if (client != null) {

			}
		}
		return null;
	}

}
