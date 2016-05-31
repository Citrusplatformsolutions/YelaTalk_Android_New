package com.kainat.app.android.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.kainat.app.android.R;
import com.kainat.app.android.core.BusinessProxy;

/**
 * @author maheshsonker
 *
 */
public class FileDownloader extends AsyncTask<String, Integer, String[]> {
	private static final String TAG = FileDownloader.class.getSimpleName();
	boolean log = false;
	private FileDownloadResponseHandler iResponseHandler;
	Context context;
	String fileName;
	String[] fullFilePath;
	String extension;
	int[] type;
	View view;
	boolean downloading;
	private static final int MEDIA_TYPE_VIDEO = 1;
	private static final int MEDIA_TYPE_IMAGE = 2;
	private static final int MEDIA_TYPE_AUDIO = 3;
	
	public FileDownloader() {

	}
//	public static FileDownloader getInstance(){
//		if(iFileDownLoader == null){
//			iFileDownLoader = new FileDownloader();
//		}
//		return iFileDownLoader;
//	}
	
	public void downloadFile(View view, String file_url, int type, FileDownloadResponseHandler responseHandler) 
	{
		this.iResponseHandler = responseHandler;
		this.view = view;
		this.type[0] = type;
		fullFilePath = new String[1];
		if(log)
			Log.i(TAG, "downloadFile :: file_url : "+file_url);
		extension = MimeTypeMap.getFileExtensionFromUrl(file_url);
		if(extension == null || extension.trim().length() <= 0)
		{
			try{
				extension = file_url.substring(file_url.lastIndexOf(".") + 1);
			}catch (Exception e) {
				extension = "mp3";
			}
		}
		//Extract File name from the URL
		if(file_url.lastIndexOf('/') != -1 && file_url.lastIndexOf('.') != -1)
		{
			fileName = file_url.substring(file_url.lastIndexOf("/") + 1, file_url.lastIndexOf("."));
			File folder = new File(Environment.getExternalStorageDirectory() + "/YelaTalk");
			if (!folder.exists()) {
			     folder.mkdir();
			}
			fullFilePath[0] = new StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append("YelaTalk").append('/').
								append(fileName).append('.').append(extension).toString();
		}
		if(log)
		{
			Log.i(TAG, "downloadFile :: extension : "+extension);
			Log.i(TAG, "downloadFile :: fileName : "+fileName);
			Log.i(TAG, "downloadFile :: fullFilePath : "+fullFilePath);
		}
		execute(file_url);
	}
	public void downloadFile(View view, String[] file_url, FileDownloadResponseHandler responseHandler) 
	{
		this.iResponseHandler = responseHandler;
		this.view = view;
		type = new int[file_url.length];
		fullFilePath = new String[file_url.length];
		String[] tempPath = new String[file_url.length];
		for(int i = 0; i < file_url.length; i++)
		{
			if(log)
				Log.i(TAG, "downloadFile :: file_url at "+ (i+1)+" : "+file_url[i]);
			extension = MimeTypeMap.getFileExtensionFromUrl(file_url[i]);
			if(extension == null || extension.trim().length() <= 0)
			{
				try{
					if(file_url[i].lastIndexOf('.') > file_url[i].lastIndexOf('/'))
						extension = file_url[i].substring(file_url[i].lastIndexOf(".") + 1);
					else
						extension = "jpg";
						
				}catch (Exception e) {
					extension = "mp3";
				}
			}
			if(extension.equalsIgnoreCase("mp3"))
				type[i] = MEDIA_TYPE_AUDIO;
			else if(extension.equalsIgnoreCase("jpg"))
				type[i] = MEDIA_TYPE_IMAGE;
			else
				type[i] = MEDIA_TYPE_IMAGE;
			//Extract File name from the URL
			if(file_url[i].lastIndexOf('/') != -1 && file_url[i].lastIndexOf('.') != -1)
			{
				if(file_url[i].lastIndexOf('.') > file_url[i].lastIndexOf('/'))
					fileName = file_url[i].substring(file_url[i].lastIndexOf("/") + 1, file_url[i].lastIndexOf("."));
				else
					fileName = file_url[i].substring(file_url[i].lastIndexOf("=") + 1);
				File folder = new File(Environment.getExternalStorageDirectory() + "/YelaTalk");
				if (!folder.exists()) {
				     folder.mkdir();
				}
				fullFilePath[i] = new StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append("YelaTalk").append('/').
									append(fileName).append('.').append(extension).toString();
			}
			if(log)
			{
				Log.i(TAG, "downloadFile :: extension at " + i + " : "+extension);
				Log.i(TAG, "downloadFile :: fileName at " + i + " : "+fileName);
				Log.i(TAG, "downloadFile :: fullFilePath at " + i + " : "+fullFilePath[i]);
			}
			//Check here in file System if that file exists, give the path directly from here and return
//			if(isFileExists(fullFilePath[i]))
		}
		
		if(log)
		{
			Log.i(TAG, "downloadFile :: extension : "+extension);
			Log.i(TAG, "downloadFile :: fileName : "+fileName);
			Log.i(TAG, "downloadFile :: fullFilePath : "+fullFilePath);
		}
		execute(file_url);
	}
	private boolean isFileExists(String file_name)
	{
		return new File(file_name).isFile();
	}
	public boolean isExecuting()
	{
		return downloading;
	}
	public void setExecuting(boolean bool)
	{
		downloading = bool;
	}


	    /**
	     * Before starting background thread
	     * Show Progress Bar Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
//	        showDialog(progress_bar_type);
	        downloading = true;
	        switch (type[0]) {
			case MEDIA_TYPE_IMAGE:
				if(view instanceof ImageView){
					try{
						Activity a = (Activity) view.getContext();
						a.runOnUiThread(new Runnable() {
							public void run() {
								((ImageView) view).setImageResource(R.drawable.def_bt_img);
							}
						});
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
				break;
			case MEDIA_TYPE_AUDIO:
				break;

			default:
				break;
			}
	    }
	 
	    /**
	     * Downloading file in background thread
	     * */
	    @Override
	    protected String[] doInBackground(String... f_url) {
	        int count;
	        try {
	        	
	        	for(int i = 0; i < f_url.length; i++)
	        	{
		            URL url = new URL(f_url[i]);
		            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		            if(f_url[i].lastIndexOf(".jpg") != -1 ||f_url[i].lastIndexOf(".mp3") != -1){
			            connection.setRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
			            connection.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		            }
		            connection.connect();
		            
		            // getting file length
		            int lenghtOfFile = connection.getContentLength();
		 
		            // input stream to read file - with 8k buffer
		            InputStream input = new BufferedInputStream(url.openStream(), 8192);
		 
		            // Output stream to write file
		            OutputStream output = new FileOutputStream(fullFilePath[i]);
		 
		            byte data[] = new byte[4096];
		 
		            long total = 0;
		 
		            while ((count = input.read(data)) != -1) {
		                total += count;
		                // publishing the progress....
		                // After this onProgressUpdate will be called
		                publishProgress((int)((total*100)/lenghtOfFile));
		 
		                // writing data to file
		                output.write(data, 0, count);
		            }
		 
		            // flushing output
		            output.flush();
		 
		            // closing streams
		            output.close();
		            input.close();
	        	}
	 
	        } catch (Exception e) {
	            Log.e("Error: ", e.getMessage());
	            downloading = false;
	        }
	 
	        return f_url;
	    }
	 
	    /**
	     * Updating progress bar
	     * */
	    protected void onProgressUpdate(Integer... progress) {
	        // setting progress percentage
//	        pDialog.setProgress(Integer.parseInt(progress[0]));
	    	if(log)
	    		Log.i(TAG, "onProgressUpdate :: progress : "+progress[0]);
	   }
	 
	    /**
	     * After completing background task
	     * Dismiss the progress dialog
	     * **/
	    @Override
	    protected void onPostExecute(String[] file_urls) {
	    	downloading = false;
	        // dismiss the dialog after the file was downloaded
//	        dismissDialog(progress_bar_type);
	 
	        // Displaying downloaded image into image view
	        // Reading image path from sdcard
//	        String fullPath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
	        // setting downloaded into image view
//	        my_image.setImageDrawable(Drawable.createFromPath(imagePath));
//	    	if(log)
//	    		Log.i(TAG, "onPostExecute :: Downloaded file path : "+fullFilePath);
	        if(iResponseHandler != null){
	        	Log.w(TAG, "onPostExecute :: full path : "+fullFilePath[0]);
	        	switch (type[0]) {
				case MEDIA_TYPE_IMAGE:
					if(view instanceof ImageView){
						try{
							Activity a = (Activity) view.getContext();
							a.runOnUiThread(new Runnable() {
								public void run() {
									((ImageView) view).setImageURI(Uri.parse(fullFilePath[0]));
								}
							});
						}catch(Exception ex){
							ex.printStackTrace();
							iResponseHandler.onFileDownloadResposne(view, type, file_urls, fullFilePath);
						}
					}
					break;
				case MEDIA_TYPE_AUDIO:
					iResponseHandler.onFileDownloadResposne(view, type, file_urls, fullFilePath);
					break;

				default:
					break;
				}
	        }
	    }
	 
}
