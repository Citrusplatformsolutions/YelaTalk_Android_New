package com.kainat.app.android.util;

import java.io.File;
import java.io.FileOutputStream;
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;

public class Downloader {

	private static BitmapDownloaderTask task;
	static ThumbnailReponseHandler thumbnailReponseHandler;
	private static final String POST_DATA = Utilities.getPhoneIMEINumber()
			+ ";" + BusinessProxy.sSelf.getUserId();
	private int type = 0;
	Context context ;
	private static Downloader downloader ;
	private  String url;
	private static HttpSynchInf httpSynchInfRefreshCureentActivity;
	public static void setHttpSynchRefreshCurrentView(HttpSynchInf httpSynchInf) {
		httpSynchInfRefreshCureentActivity = httpSynchInf;
	}
	private Downloader() {

	}
	private int state=0;
	public static Downloader getInstance(){
		if(downloader==null){
			downloader = new Downloader();
		}
		return downloader ;
	}
	public static LinkedList<String> links = new LinkedList<String>();

	public int getState(){
		return state ;
	}
	public String getUrl(){
		return url ;
	}
	public void download(String url, int type,Context context) {
		
//		System.out.println(""+url);
		this.context =context ;
		this.type = type;
//		this.url=url ;
		if(url.endsWith(Constants.COL_SEPRETOR))
			url = url.substring(0,url.length()-1) ;
		downloadContent(url);
	}

	public void downloadContent(String url
			) {
			getCacheDirectory();

		this.thumbnailReponseHandler = thumbnailReponseHandler;

		{
//			System.out.println("DOWNLODER---------URL-----: "+url);
			// Caching code right here
			String filename = String.valueOf(url.hashCode());
			
			String extension = MimeTypeMap.getFileExtensionFromUrl(url);
			if(extension==null||extension.trim().length()<=0){
				try{
					extension = url.substring(url.lastIndexOf(".")+1, url.length());
				}catch (Exception e) {
					extension = "amr" ;
				}
			}
			filename = filename+"."+extension ;
			
			File f = new File(cacheDir, filename);

			if (f.exists()) {
//				System.out.println("DOWNLODER---------updating tabe after download conent-file found :)-----");
				updateDownload( url.hashCode(),1);
				
			} else //if( state==0) 
			{
				updateDownload(  url.hashCode(),0);
				if (links.contains(url))
					links.remove(url);
				links.addFirst(url);
				if ((task == null || task.getStatus() == Status.FINISHED)) {
					task = new BitmapDownloaderTask();
					task.execute(links.poll());
				}
				if (task != null )
						{
//							System.out.println("DOWNLODER---------TASK STATUS-----: "+task.getStatus());
						}
			}
		}
	}

	public String getPlayUrl(String url){
//		System.out.println("DOWNLODER---------getPlayUrl-----"+url);
		if(url!=null && url.startsWith("/"))
			return url ;
		String filename = String.valueOf(url.hashCode());
		
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
//		if(extension==null||extension.trim().length()<=0){
//			try{
//				extension = url.substring(url.lastIndexOf(".")+1, url.length());
//			}catch (Exception e) {
//				extension = "3gp" ;
//			}
//		}
		filename = filename+"."+extension ;
//		System.out.println("DOWNLODER---------getPlayUrl---filename--"+filename);
		File f = new File(cacheDir, filename);
		if(!f.exists())
			return null ;
//		System.out.println("DOWNLODER---------getPlayUrl-f.getAbsolutePath()----"+f.getAbsolutePath());
		return f.getAbsolutePath() ;
	}
	public boolean checkAndUpdate(String url){
		String filename = String.valueOf(url.hashCode());
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if(extension==null||extension.trim().length()<=0){
			try{
				extension = url.substring(url.lastIndexOf(".")+1, url.length());
			}catch (Exception e) {
				extension = "amr" ;
			}
		}
		filename = filename+"."+extension ;
		
		File f = new File(cacheDir, filename);
		if (f.exists()) {
//			System.out.println("DOWNLODER---------checkAndUpdate conent-file found :)-----");
			updateDownload( url.hashCode(),1);
			links.remove(url);
			return true;
		}
		return false;
	}
	public boolean check(String url){
		try{
		getCacheDirectory();
		String filename = String.valueOf(url.hashCode());
		
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if(extension==null||extension.trim().length()<=0){
			try{
				extension = url.substring(url.lastIndexOf(".")+1, url.length());
			}catch (Exception e) {
				extension = "amr" ;
			}
		}
		filename = filename+"."+extension ;
		
		File f = new File(cacheDir, filename);
		if (f.exists()) {
			
			return true;
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	private static File cacheDir;

	private static File getCacheDirectory() {

		if (cacheDir != null)
			return cacheDir;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, Constants.contentVoice);
		}

		if (!cacheDir.exists())
			cacheDir.mkdirs();
		return cacheDir;
	}

	private  void writeFile(byte[] data, File f,int id) {
//		System.out.println("DOWNLODER---------writefile-----: "+url);
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
//				System.out.println("DOWNLODER------image download : " + e.getMessage());
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

	private void updateDownload(int id, int with){
		
//		System.out.println("DOWNLODER---------updating tabe after download conent------ :"+id);
		
		Cursor cursor =context.  getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX,
				null,
				MessageTable.AUDIO_ID + " = ?",
				new String[] { ""+id },
				MessageTable.INSERT_TIME + " ASC");
		cursor.moveToFirst();
		int i=-1;
		if(cursor.getCount()>0)
		i =cursor.getInt(cursor.getColumnIndex(MessageTable.AUDIO_DOWNLOAD_STATUE));
		cursor.close();
//		System.out.println("DOWNLODER---------updating tabe after download conent------ with:"+with+" i : "+ i);
		if(i!=with)
		{
			ContentValues values = new ContentValues();
			values.put(MessageTable.AUDIO_DOWNLOAD_STATUE, with);// int
			int updatedRow =context. getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX,
					values,
					MessageTable.AUDIO_ID + "=?",
					new String[] { ""+id});
			
//			System.out.println("DOWNLODER---------updating tabe after download conent------ updatedRow:"+updatedRow);
		}
		updateDownloadBookmark(id,with);
	}
	
	private void updateDownloadBookmark(int id, int with){
//		System.out.println("DOWNLODER---------updating tabe after download conent------ :"+id);
		
		Cursor cursor =context.  getContentResolver().query(
				MediaContentProvider.CONTENT_URI_BOOKMARK,
				null,
				MessageTable.AUDIO_ID + " = ?",
				new String[] { ""+id },
				MessageTable.INSERT_TIME + " ASC");
		cursor.moveToFirst();
		int i=-1;
		if(cursor.getCount()>0)
		i =cursor.getInt(cursor.getColumnIndex(MessageTable.AUDIO_DOWNLOAD_STATUE));
		cursor.close();
//		System.out.println("DOWNLODER---------updating tabe after download conent------ with:"+with+" i : "+ i);
		if(i!=with)
		{
			ContentValues values = new ContentValues();
			values.put(MessageTable.AUDIO_DOWNLOAD_STATUE, with);// int
			int updatedRow =context. getContentResolver().update(
					MediaContentProvider.CONTENT_URI_BOOKMARK,
					values,
					MessageTable.AUDIO_ID + "=?",
					new String[] { ""+id});
			
//			System.out.println("DOWNLODER---------updating tabe after download conent------ updatedRow:"+updatedRow);
		}
	}
	
	public class BitmapDownloaderTask extends AsyncTask<String, Void, byte[]> {
		private String url;

		@Override
		protected byte[] doInBackground(String... params) {
			
			state=1 ;
			url = (String) params[0];
//			System.out.println("DOWNLODER---------doin background-----: "+url);
			Downloader.this.url=url ;
			return download(params[0]);
		}

		@Override
		protected void onPostExecute(byte[] data) {
			if (isCancelled()) {
				data = null;
			}

			String filename = String.valueOf(url.hashCode());
			
			String extension = MimeTypeMap.getFileExtensionFromUrl(url);
			if(extension==null||extension.trim().length()<=0){
				try{
					extension = url.substring(url.lastIndexOf(".")+1, url.length());
				}catch (Exception e) {
					extension = "amr" ;
				}
			}
			filename = filename+"."+extension ;
			
			File f = new File(cacheDir, filename);

			
			writeFile(data, f,url.hashCode());
			String url = links.poll();
			if (url != null) {
				task = new BitmapDownloaderTask();
				task.execute(url);
			}
			state=0 ;
//			System.out.println("DOWNLODER---------doPostExecute-----: "+url);
		}
	}

	static byte[] download(String url) {
		
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		HttpClient client = new DefaultHttpClient(params);
//		System.out.println("DOWNLODER------------downloader url : "+url);
		final HttpGet getRequest = new HttpGet(url);
		try {
			getRequest.setHeader("RT-APP-KEY",
					"" + BusinessProxy.sSelf.getUserId());
			getRequest.setHeader("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
//			System.out.println("DOWNLODER------------downloader statusCode : "+statusCode);
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

					return Utilities.readBytes(inputStream);

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
