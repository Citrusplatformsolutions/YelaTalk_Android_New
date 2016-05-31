package com.kainat.app.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Stack;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.text.Html;
import android.widget.RemoteViews;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.CompressImage;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.MediaPost;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;

public class PostService extends IntentService {
	public static Stack<MediaPost> media = new Stack<MediaPost>();
	String color1 = "#ff0000" ;
	String color2 = "#ff0000" ;
	public PostService() {
		super("nna");		
	}
@Override
public boolean stopService(Intent name) {
	
//	System.out.println("----------post-------stoping post service-----------------");
	return super.stopService(name);	
	
}

public static void cancelallnotification(){
	if(mNotificationManager!=null)
		mNotificationManager.cancel(NOTIFICATION_ID);
		if(mNotificationManagerDone!=null)
		mNotificationManagerDone.cancel(NOTIFICATION_ID_DONE);
}
	public PostService(String name) {

		super(name);
		
	}

	int i = 0;
	int queueSize = 0 ;
	@Override
	protected void onHandleIntent(Intent intent) {
		try{
		
		String s[] = Utilities.initPostDraftList();
		if(s!=null)
		writeToFile("PostService----total draft len :"+s.length);
		if (s == null || s.length <= 0){
			runtime();
			writeToFile("PostService----run time-----");
		}
		else {
			fromFile();
			writeToFile("PostService----from file-----");
		}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void fromFile() {
		try{
		String s[] = Utilities.initPostDraftList();
		queueSize = s.length ;
		startCountdonn();
//		int notYour = 0 ;
		while (s!=null && s.length > 0) {
//			System.out
//			.println("----------post-----------s[0]--------"
//					+ s[0]);
			MediaPost mediaPost = Utilities.getDraftPostData(s[0], getApplicationContext());
			if(mediaPost.userId != BusinessProxy.sSelf.getUserId())
			{
//				notYour++;
				continue;
			}
			if(mediaPost==null){
				try{
					new File(Environment.getExternalStorageDirectory()
							.getAbsolutePath() + Utilities.draftPostPath+mediaPost.id+".pst").delete() ;
				}catch (Exception e) {
					e.printStackTrace() ;
				}
				s = Utilities.initPostDraftList();
				queueSize = s.length ;
				continue ;
			}
			boolean res =process(mediaPost);
//			if(!res)
//				Utilities.draftPostData(mediaPost, getApplicationContext()) ;
			//if(res)
			{
				s = Utilities.initPostDraftList();
				queueSize = s.length ;
			}
		}
		// http://124.153.95.168:28080/tejas/feeds/api/ugcmedia/create?tag=three+images&desc=my+game+world+of+warcraft&cat=49&privtype=friend&fileids=15174,15175,15176
		// }
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void runtime() {
		try{
		startCountdonn();
		while (media.size() > 0) {
			MediaPost mediaPost = media.pop();
			queueSize = media.size() ;
			writeToFile("----queueSize-----"+queueSize);
			process(mediaPost);
		}
		// http://124.153.95.168:28080/tejas/feeds/api/ugcmedia/create?tag=three+images&desc=my+game+world+of+warcraft&cat=49&privtype=friend&fileids=15174,15175,15176
		// }
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	String serverError = null;
	public boolean process(MediaPost mediaPost){
		if(mNotificationManagerDone !=null)
		mNotificationManagerDone.cancel(NOTIFICATION_ID_DONE);
		String fberror = null ;
		boolean isPublic = false ;
		String allIds = "";
		int totalItem = 0;
		int uploadedItem = 0;
		if (mediaPost.mImagesPath != null
				&& mediaPost.mImagesPath.size() > 0)
			totalItem = mediaPost.mImagesPath.size();
		if (mediaPost.mVideoPath != null && mediaPost.mVideoPath.size() > 0)
			totalItem = mediaPost.mVideoPath.size();
		if (mediaPost.mVoicePath != null)
//				&& mediaPost.mImagesPath.size() > 0)
			totalItem = totalItem + 1;
//		String s = "<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font><font color = '"+color2+"'>"
//		+ "<br>" + mediaPost.mediaText +"</html>";
		
//		String s = "<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font><font color = '"+color2+"'>"
//		+ "<br>" + mediaPost.mediaText +"</html>";
		
		String s = "<html><font color = '"+color1+"'>'"+mediaPost.mediaTag+"' Media Posting</font></html>";
		if(mNotificationManagerDone !=null)
		mNotificationManagerDone.cancel(NOTIFICATION_ID_DONE);
		showNotification("Upload started!", mediaPost.mediaTag, totalItem);
		
		writeToFile("---totalItem---:"+totalItem);
		
//		Utilities.showNotification("Upload started!",  mediaPost.mediaTag,this,false) ;
		int uploadedItemCount = 0  ;
		serverError=null;//"jan booj ker error dala gaya hai :)";//please  set it null 
		if (mediaPost.mImagesPath != null
				&& mediaPost.mImagesPath.size() > 0) {
			try{
			for (int i = 0; i < mediaPost.mImagesPath.size()   && serverError==null && serverError==null; i++) {
				MediaPost.MediaContent mc = mediaPost.mImagesPath.get(i);
				byte[] imageBinaryData = null ;
				try {
					File f = new File(mc.contentPath) ;
					if(!f.exists())
						continue ;
					writeToFile("---size before compress---:"+(f.length()/1024) +" KB");
					writeToFile("---path before compress---:"+mc.contentPath);
					
					CompressImage compressImage = new CompressImage(this) ;
					
					String newPath = compressImage.compressImage(mc.contentPath) ;
					
					
//					imageBinaryData = Utilities.readBytes(Utilities
//							.getFileInputStream(newPath));
					writeToFile("---path after compress---:"+newPath);
					f = new File(newPath) ;
					writeToFile("---size after compress---:"+(f.length()/1024) +" KB");
					
					writeToFile(mc.contentPath);
					String res = createMediaID(imageBinaryData,newPath);//"image/jpeg");
					writeToFile("res:"+res);
					imageBinaryData = null;					
					if (res != null) {
						uploadedItemCount++;
						mc.contentServerID = res;
						allIds += mc.contentServerID + ",";
					}
					progressUpdate(totalItem, ++uploadedItem);
				} catch (Exception e) {
					writeToFile(e.getMessage());
					e.printStackTrace();
				}
			}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (mediaPost.mVideoPath != null && mediaPost.mVideoPath.size() > 0  && serverError==null){
			try{
			for (int i = 0; i < mediaPost.mVideoPath.size()  && serverError==null && serverError==null; i++) {
				MediaPost.MediaContent mc = mediaPost.mVideoPath.get(i);
				byte[] imageBinaryData = null ;
				try {
					File f = new File(mc.contentPath) ;
					if(!f.exists())
						continue ;
//					imageBinaryData = Utilities.readBytes(Utilities
//							.getFileInputStream(mc.contentPath));// /mnt/emmc/DCIM/100MEDIA/VIDEO0006.3gp
					// imageBinaryData = null ;
					// System.out.println("-----------mc.contentPath----------"+mc.contentPath);
					// System.out.println("-----------imageBinaryData------"+imageBinaryData.length);
					writeToFile(mc.contentPath);
					String res = createMediaID(imageBinaryData,mc.contentPath);///*"video/mp4"*/);//audio/mp3
					imageBinaryData = null;
					
					if (res != null) {
						uploadedItemCount++;
						mc.contentServerID = res;
						allIds += mc.contentServerID + ",";
					}
					progressUpdate(totalItem, ++uploadedItem);
				} catch (Exception e) {
					writeToFile(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		if (mediaPost.mVoicePath != null  && serverError==null) {
			try{
			byte[] imageBinaryData=null;
			try {
//				imageBinaryData = Utilities
//						.readBytes(Utilities
//								.getFileInputStream(mediaPost.mVoicePath.contentPath));
//				
				
				File f = new File(mediaPost.mVoicePath.contentPath) ;
				
//				System.out.println("-----------imageBinaryData------"+imageBinaryData.length);
				if(f.exists()){
				String res = createMediaID(imageBinaryData,mediaPost.mVoicePath.contentPath);
				writeToFile(mediaPost.mVoicePath.contentPath);
				imageBinaryData = null;
//				System.out.println("-----------imageBinaryData------"+res);
				if (res != null) {
					uploadedItemCount++;
					mediaPost.mVoicePath.contentServerID = res;
					allIds += mediaPost.mVoicePath.contentServerID + ",";
				}
				}
				progressUpdate(totalItem, ++uploadedItem);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}catch (Exception e) {
				// TODO: handle exception
			}
		}
		writeToFile("serverError : "+serverError);
		if(serverError!=null){;
		try{
			completed(mediaPost.mediaTag,serverError);
			}catch (Exception e) {
				completed(mediaPost.mediaTag,"Some error occured while uploading media!");						
		}
			return false; 
		}
		// http://124.153.95.168:28080/tejas/feeds/api/ugcmedia/create
		// ?tagHjfh&desc=Jtuu&cat=55&privtype=friend&fileids=15286,
		if (allIds.endsWith(",")) {
			allIds = allIds.substring(0, allIds.length() - 1);
		}
		writeToFile("---uploadedItemCount---:"+uploadedItemCount);
		if(uploadedItemCount!=totalItem)
		{
			completed(mediaPost.mediaTag,"Some error occured while uploading media!"); 
			return false;
		}
//		System.out.println("----------post----------allIds" + allIds);
		String makeFburl ="http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/ugcmedia/create?tag="
		+ URLEncoder.encode(mediaPost.mediaTag);
		if (mediaPost.sendOnFaceBook)
		makeFburl = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/ugcmedia/create?postto=facebook&tag="
				+ URLEncoder.encode(mediaPost.mediaTag);
		if(mediaPost.mediaText!=null&&mediaPost.mediaText.trim().length()>0)
		makeFburl += "&desc=" + URLEncoder.encode("" + mediaPost.mediaText);
		makeFburl += "&cat=" + URLEncoder.encode("" + mediaPost.cat);
		if (mediaPost.privacy.indexOf("Public") != -1) {
			makeFburl += "&privtype=public";
			isPublic = true ;
		} else if (mediaPost.privacy.indexOf("Private") != -1) {
			makeFburl += "&privtype=private";	
			isPublic = false ;
		} else if (mediaPost.privacy.indexOf("Friend") != -1) {
			makeFburl += "&privtype=friend";
			isPublic = false ;
		}
//		writeToFile("makeFburl : "+makeFburl);
		 if (mediaPost.locAddress != null
					&& mediaPost.locAddress.length() > 0
					&& mediaPost.lat != 0.0
					&& mediaPost.lon != 0.0) {
			 makeFburl += "&loc="+URLEncoder.encode(mediaPost.lat + ","
				+ mediaPost.lon);
			 makeFburl += "&loctag="+URLEncoder.encode(mediaPost.locAddress) ;			
			}
		
		makeFburl += "&fileids=" + allIds;
		writeToFile("makeFburl : "+makeFburl);
//		System.out.println("----------post------fburl---" + makeFburl);
		String fburl = makeMfurl(makeFburl);
//		for (int i = 0; i < 10; i++) {
//			fburl = makeMfurl(makeFburl);
//			System.out.println("----------post------ori fburl---" + fburl);
//		}
//		System.out.println("----------post------ori fburl---" + fburl);

		try {
			final JSONObject jsonObject = new JSONObject(fburl);
			final String status = jsonObject.getString("status");
			if (status.equals("error")) {
//				try {
//					System.out
//							.println("----------post------------message---"
//									+ jsonObject.getString("message"));
//
//				} catch (JSONException e) {
//				}
				try{
					completed(mediaPost.mediaTag,jsonObject.getString("message"));//"<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font></html>",
					}catch (Exception e) {
						completed(mediaPost.mediaTag,"Some error occured while uploading media!");//"<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font></html>",						
				}
				return false;
			}
			mediaPost.status = "done";
			if ((status.equals("success"))) {
				try {
					// media.remove(i);
					mediaPost.fburl = jsonObject.getString("FbURL");
					String thumbUrl = jsonObject.getString("ThumbURL");
					if( BusinessProxy.sSelf.mFacebook !=null 
					&& BusinessProxy.sSelf.mFacebook.isSessionValid())
					{
//						System.out
//							.println("----------post------------message---"
//									+ jsonObject.getString("message"));
//					System.out.println("----------post------------FbURL---"
//							+ jsonObject.getString("FbURL"));

					Bundle params = new Bundle();
					// params.putString(
					// "message",
					// "When you join RockeTalk, you gain access to more than a hundred thousand interest groups, formed by people like you, with an urge to seek, speak, perform and share � with others locally and globally. ");
					params.putString("link", mediaPost.fburl);
					if ((SettingData.sSelf.getFirstName() != null && SettingData.sSelf.getFirstName().length() > 0) 
							|| (SettingData.sSelf.getLastName() != null && SettingData.sSelf.getLastName().trim().length() > 0)) {
						params.putString("caption", SettingData.sSelf.getFirstName()+" "+SettingData.sSelf.getLastName());
					} else 
						params.putString("caption", SettingData.sSelf.getUserName());
					if(mediaPost.mediaText!=null && mediaPost.mediaText.length()>0)
						
					if(mediaPost.mediaText!=null && mediaPost.mediaText.trim().length() >0)
						params.putString("description",mediaPost.mediaText);
					else
						params.putString("description",getResources().getString(R.string.facebook_desc));
					
					params.putString("picture", thumbUrl);// "http://www.rocketalk.com/images/rocketalk_logo_beta.gif");
					params.putString("name", mediaPost.mediaTag);
//					Toast.makeText(getApplicationContext(),
//							"RocakeTalk Media Post sucessfully!",
//							Toast.LENGTH_SHORT).show();
					if (mediaPost.sendOnFaceBook) {
						String fRes = BusinessProxy.sSelf.mFacebook
								.request("me/feed", params, "POST");
						if(fRes.indexOf("OAuthException") != -1){
						fberror = getResources().getString(R.string.facebook_permission_error);//"Your media could not be posted to Facebook since you picked 'Allow None' in your options." ;
						mediaPost.status += "~fb~"+fRes;
						BusinessProxy.sSelf.mFacebook = null ;
						
					}else 
						mediaPost.status += "~fb";					
					}
					}else
						mediaPost.status += "~fbsessiontimeout";
				} catch (JSONException e) {
				}
			}
		} catch (Exception e) {
			completed(mediaPost.mediaTag,"Some error occured while uploading media!");//"<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font></html>",
			
			return false;
		}
		Utilities.donePosting(mediaPost,
				getApplicationContext());
		String html = "<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font></html>";
		if (mediaPost.sendOnFaceBook){
			if(fberror!=null)
			{
				String ss = "<html>"+"Media Sucessfully posted on Rocketalk.<font color = '"+color2+"'><br>"+fberror+"</html>" ;
				if(isPublic)
					ss = "<html>"+"Media will be posted on RockeTalk after moderation.<font color = '"+color2+"'><br>"+fberror+"</html>" ;
				completed(mediaPost.mediaTag,ss);//
			}
			else{
				if(isPublic)
					completed(mediaPost.mediaTag,"Media successfully posted on Facebook and will be posted on RockeTalk after moderation!");//
					else
				completed(mediaPost.mediaTag,"Media Sucessfully posted on RockeTalk and Facebook!");//
			}
		}
		else{
			if(isPublic)
				completed(mediaPost.mediaTag,"Media will be posted on RockeTalk after moderation!");//
				else 
			completed(mediaPost.mediaTag,"Media Sucessfully posted on RockeTalk!");//
		}
		return true;
	}

	private String createMediaID(byte[] imageData,String contentType) {
		try {
			
			String url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/media/create";
					String response = null;
			boolean done = false;

			int retry = 0;
			while (!done && retry < 3) {
				try {
					Thread.sleep(2000);
					response = uploadPhoto(url, imageData,contentType);
					
					done = true;
				} catch (Exception e) {
					retry++;
					e.printStackTrace();
				}
			}
//			System.out.println("--------------------response------ : "+response);
			imageData = null;
			writeToFile("response:"+response);
			final JSONObject jsonObject = new JSONObject(response);
			final String status = jsonObject.getString("status");
			if (status.equals("error")) {
				try {
//					=null;
					try{
						serverError = jsonObject.getString("message");//completed(mediaPost.mediaTag,);//"<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font></html>",
						}catch (Exception e) {
							//completed(mediaPost.mediaTag,"Some error occurred during uploading!");//"<html><font color = '"+color1+"'>"+mediaPost.mediaTag+"</font></html>",						
					}
//					System.out.println("----------post------------message---"
//							+ jsonObject.getString("message"));
					return null;
				} catch (Exception e) {
				}
			} else if (status.equals("success")) {
				try {
					return jsonObject.getString("fileId");
				} catch (JSONException e) {
					writeToFile(e.getMessage());
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();

		}
		return null;
	}

	private class PhotoUploadResponseHandler implements ResponseHandler {

        @Override
        public Object handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
//            System.out.println("-------------responseString-------------- : "+responseString);
//            Log.d("UPLOAD", responseString);

            return null;
        }

    }
	
	public String uploadPhoto(String urlStr, byte[] imageData,String path) {
//	    Log.d(TAG, "UPLOAD: SendMultipartFile");
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(urlStr);
//	    path = "/mnt/sdcard/Download/483405_133475993475604_356624831_n.jpg" ;

//	    reduceImageAndShift
//	    Utilities.reduceImageAndShift(Uri.parse(path)) ;
	    File file = new File(path);
//	    System.out.println("--------------------file path-----------"+path);
//	    System.out.println("--------------------file length-----------"+file.length());
//	    System.out.println("--------------------file exist-----------"+file.exists());
//	    Log.d(TAG, "UPLOAD: setting up multipart entity");

	    MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.STRICT);
//	    Log.d(TAG, "UPLOAD: file length = " + file.length());
//	    Log.d(TAG, "UPLOAD: file exist = " + file.exists());

	    try {
	        mpEntity.addPart("data", new FileBody(file, "application/octet-stream"));
//	        mpEntity.addPart("id", new StringBody("1"));
	    } catch (Exception e1) {
//	        Log.d(TAG, "UPLOAD: UnsupportedEncodingException");
	        e1.printStackTrace();
	    }
	    writeToFile("BusinessProxy.sSelf.getUserId() : "+BusinessProxy.sSelf.getUserId());
	    httppost.setHeader("RT-DEV-KEY",
				"" + BusinessProxy.sSelf.getClientId());

        httppost.setHeader(
				"RT-APP-KEY",
				HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(),
						SettingData.sSelf.getPassword()));
	    httppost.setEntity(mpEntity);
//	    System.out.println("----------------httppost.getRequestLine()--------"+httppost.getRequestLine());
//	    System.out.println("----------------httppost.getEntity().getContentType().toString()--------"+httppost.getEntity().getContentType().toString());
//	    Log.d(TAG, "UPLOAD: executing request: " + httppost.getRequestLine());
//	    Log.d(TAG, "UPLOAD: request: " + httppost.getEntity().getContentType().toString());


	    HttpResponse response;
	    try {
//	        Log.d(TAG, "UPLOAD: about to execute");
	        response = httpclient.execute(httppost);
//	        Log.d(TAG, "UPLOAD: executed");
	        HttpEntity resEntity = response.getEntity();
//	        System.out.println("-------------------------RES-------- : "+response.getStatusLine().toString());
//	        Log.d(TAG, "UPLOAD: respose code: " + response.getStatusLine().toString());
	        
	        HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            writeToFile("responseString : "+responseString);
//            System.out.println("-------------responseString-------------- : "+responseString);
//            return responseString ;
            
	        if (resEntity != null) {
//	            Log.d(TAG, "UPLOAD: " + EntityUtils.toString(resEntity));
	        }
	        if (resEntity != null) {
	            resEntity.consumeContent();
	        }
	        
	       
	        return responseString;// "sucess" ;
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return "fail" ;
	}
	public String  uploadPhotoPP(String urlStr, byte[] imageData,String path)//(File image)
	{

        try {
        	 DefaultHttpClient client = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlStr);

            
            FileBody data = new FileBody(new File(path));
            StringBody comment = new StringBody("Filename: " + "ddd");

//            MultipartEntity reqEntity = new MultipartEntity();
//            FormBodyPart dataBodyPart = new FormBodyPart("data", data);
//            FormBodyPart commentBodyPart = new FormBodyPart("comment", comment);
//            reqEntity.addPart(dataBodyPart);
//            reqEntity.addPart(commentBodyPart);
//            httppost.setEntity(reqEntity);
            
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
//            multipartEntity.addPart("Title", new StringBody("Title"));
//            multipartEntity.addPart("Nick", new StringBody("Nick"));
//            multipartEntity.addPart("Email", new StringBody("Email"));
//            multipartEntity.addPart("Description", new StringBody(Settings.SHARE.TEXT));
            multipartEntity.addPart("data", new FileBody(new File(path)));
            httppost.setEntity(multipartEntity);

            httppost.setHeader("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());

            httppost.setHeader(
					"RT-APP-KEY",
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));

			
            client.execute(httppost, new PhotoUploadResponseHandler());
            return "ok" ;
        } catch (Exception e) {
//            Log.e(ServerCommunication.class.getName(), e.getLocalizedMessage(), e);
        }
        return "fail" ;
    }
	private String uploadPhotoXX(String urlStr, byte[] imageData,String path)
			throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;

		try {
			
			// This is the standard format for a multipart request
			byte messageByteArray[] = new byte[0];
			byte messageByteArray2[] = new byte[0];
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					"\r\n--".getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					BOUNDRY.getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray,
					"\r\n".getBytes("UTF-8"));
			
			
			if (imageData != null) {
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Disposition: form-data; charset=utf-8; name=\"data\""
								.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"Content-Type: application/octet-stream"
								.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray,
						"\r\n\r\n".getBytes("UTF-8"));
				
				
//				messageByteArray = ArrayUtils.addAll(messageByteArray,
//						imageData);
				
				
				messageByteArray2 = ArrayUtils.addAll(messageByteArray2,
						"\r\n--".getBytes("UTF-8"));
				messageByteArray2 = ArrayUtils.addAll(messageByteArray2,
						BOUNDRY.getBytes("UTF-8"));
				messageByteArray2 = ArrayUtils.addAll(messageByteArray2,
						"--\r\n".getBytes("UTF-8"));
			}
//			imageBinaryData = Utilities.readBytes(Utilities
//					.getFileInputStream(mc.contentPath));
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(1000 * 60);
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; charset=utf-8; boundary=" + BOUNDRY);
			// conn.addRequestProperty("RT-APP-KEY",
			// "" + BusinessProxy.sSelf.getUserId());
			// conn.setRequestProperty("RT-APP-KEY",
			// "" + BusinessProxy.sSelf.getUserId());
			//
			// conn.addRequestProperty("RT-DEV-KEY",
			// "" + BusinessProxy.sSelf.getClientId());
			conn.setRequestProperty("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());

			conn.setRequestProperty(
					"RT-APP-KEY",
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));
//			writeToFile(new String(messageByteArray));

			// Send the body
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(messageByteArray);
			
			try {
				int bufferSize = 1024*50;
				byte[] buffer = new byte[bufferSize];
//				System.out.println("---------------------attachment path : "+path);
				InputStream finput = new FileInputStream(path);
				int len = 0;
				while ((len = finput.read(buffer)) != -1) {
					dataOS.write(buffer, 0, len);
				}
//				dataOS.flush();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				return null;
			}
			
			dataOS.write(messageByteArray2);
			dataOS.flush();
			dataOS.close();
			messageByteArray = null;
			Thread.sleep(500);
			int responseCode = conn.getResponseCode();
			if (responseCode != 200) {
				throw new Exception(String.format(
						"Received the response code %d from the URL %s",
						responseCode, url));
			}
			// Read the response
//			System.out.println("--------------------sucess-");
			is = conn.getInputStream();
			return IOUtils.read(is);
		}

		finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					// do nothing
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public String makeMfurl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			System.out
					.println("----------post------makeMfurl-------------------"
							+ urlStr);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("RT-DEV-KEY",
					"" + BusinessProxy.sSelf.getClientId());

			conn.setRequestProperty(
					"RT-APP-KEY",
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));

			int responseCode = conn.getResponseCode();
//			System.out
//					.println("----------post------fburl---------------post responce code-------"
//							+ responseCode);
			if (responseCode != 200) {
				throw new Exception(String.format(
						"Received the response code %d from the URL %s",
						responseCode, url));
			}
//			System.out
//					.println("----------post---------------------post response code----"
//							+ responseCode);
			// Read the response
			InputStream is = conn.getInputStream();
			return IOUtils.read(is);

		} catch (Exception e) {
			System.out.println("----------post-------------------error--"
					+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private static final boolean log = false ;
	private static void writeToFile(String stacktrace) {
		
		if(log)
			System.out.println("PostService-------"+stacktrace);
		if(!log){
			return ;
		}
		File file = new File("/sdcard/mediapost.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					true));
			writer.write(stacktrace);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Notification mNotification;
	public static NotificationManager mNotificationManager;
	boolean toggle;
	public static int NOTIFICATION_ID = 101;
	RemoteViews contentView;

	public void showNotification(String aTickerText, String aContentText,
			int totalitem) {

		
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
			return;
		}
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		try {
			CharSequence contentTitle = "RockeTalk"; // expanded message title
			int icon = R.drawable.pushicon; // icon from resources
			long when = System.currentTimeMillis(); // notification time - When
													// to notify
			Context context = getApplicationContext(); // application Context
			if (mNotificationManager != null)
				mNotificationManager.cancel(NOTIFICATION_ID);
			if (toggle) {
				NOTIFICATION_ID = 2;
				toggle = false;
			} else {
				NOTIFICATION_ID = 1;
				toggle = true;
			}
			String myVersion = android.os.Build.VERSION.RELEASE;
			
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
//			     // only for android older than gingerbread
//			}
			
			if(Build.VERSION.SDK_INT>10){
			if (mNotification == null) {
				mNotification = new Notification(icon, aTickerText, when);
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotification.defaults |= Notification.DEFAULT_LIGHTS;
			}
			mNotification.number = BusinessProxy.sSelf.newMessageCount;
			mNotification.tickerText = aTickerText;
			Intent notificationIntent = new Intent(this, KainatHomeActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			mNotification.contentIntent = contentIntent;
//			if(Utilities.getScreenType(context)>Constants.SCREENLAYOUT_SIZE_LARGE)
			contentView = new RemoteViews(getPackageName(),
					R.layout.post_notification);
			if(context.getResources().getDisplayMetrics().density<=2.0f)
				contentView = new RemoteViews(getPackageName(),
						R.layout.post_notification_small_screen);
			contentView.setProgressBar(R.id.media_upload, totalitem+1, 1, false);
			contentView.setImageViewResource(R.id.image, R.drawable.pushicon);
			contentView.setTextViewText(R.id.text, Html.fromHtml(aContentText));// "Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
			contentView.setTextViewText(R.id.text2, "Upload started!");// "Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
			mNotification.contentView = contentView;
			
			}else
			{
//				 Intent notificationIntent = new Intent(this, KainatHomeActivity.class);
//				 PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//				 mNotification = new Notification(icon, aTickerText, when);
//				 mNotification.setLatestEventInfo(context, aTickerText, aContentText, contentIntent);
				 
				  mNotification = new Notification.Builder(this)
		         .setContentTitle(aTickerText)
		         .setContentText(aContentText)
		         .setSmallIcon(icon)
		         .build(); // available from API level 11 and onwards
			}
			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void progressUpdate(int totlaItem, int uploadedItem) {
		if(Build.VERSION.SDK_INT>10){
		contentView.setProgressBar(R.id.media_upload, totlaItem+1, uploadedItem,
				false);
//		contentView.setTextViewText(R.id.post_count, ""+queueSize);
		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		}
	}

	public void completed(String s1, String s2) {
		writeToFile("s1 : "+s1);
		writeToFile("s2 : "+s2);
		mNotificationManager.cancel(NOTIFICATION_ID);
//		showNotificationDone(s1, s2);
		Utilities.showNotification(s1,  s2,this,false,-1) ;
	}

	CountDownTimer countDownTimer;
	int totTimeToRec = 1000 * 60 * 5;

	private void startCountdonn() {
//		countDownTimer = new CountDownTimer(totTimeToRec, 1000) {
//			public void onTick(long millisUntilFinished) {
//				System.out.println("----------post----CountDownTimer ");
//			}
//
//			public void onFinish() {
//				// stopService(new Intent(PostService.this,PostService.class));
//			}
//		}.start();
	}

	public static Notification mNotificationDone;
	public static NotificationManager mNotificationManagerDone;
	// boolean toggle;
	public static int NOTIFICATION_ID_DONE = 101;
	RemoteViews contentViewDone;

	public void showNotificationDone(String aContentText, String aTickerText) {
		NOTIFICATION_ID_DONE++;
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
			return;
		}
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		try {
			CharSequence contentTitle = "RockeTalk"; // expanded message title
			int icon = R.drawable.pushicon; // icon from resources
			long when = System.currentTimeMillis(); // notification time - When
													// to notify
			Context context = getApplicationContext(); // application Context
			if (mNotificationManagerDone != null)
				mNotificationManagerDone.cancel(NOTIFICATION_ID_DONE);
			if (toggle) {
				NOTIFICATION_ID_DONE = 2;
				toggle = false;
			} else {
				NOTIFICATION_ID_DONE = 1;
				toggle = true;
			}
			if(Build.VERSION.SDK_INT>10){
			if (mNotificationDone == null) {
				mNotificationDone = new Notification(icon, Html.fromHtml(aTickerText), when);
				mNotificationDone.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotificationDone.defaults |= Notification.DEFAULT_LIGHTS;
//				mNotificationDone.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotificationDone.defaults |= Notification.DEFAULT_SOUND;
				mNotificationDone.defaults |= Notification.DEFAULT_VIBRATE;
				// mNotificationDone.flags |= Notification.FLAG_NO_CLEAR;
			}
			// mNotificationDone.number = BusinessProxy.sSelf.newMessageCount;
			mNotificationDone.tickerText = Html.fromHtml(aTickerText);
			 Intent notificationIntent = new Intent(this, KainatHomeActivity.class);
//			Intent notificationIntent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			mNotificationDone.contentIntent = contentIntent;
			contentViewDone = new RemoteViews(getPackageName(),
					R.layout.post_notification_done);
			// contentView
			// contentView.setProgressBar(R.id.media_upload, totalitem, 0,
			// false);
			contentViewDone.setImageViewResource(R.id.image, R.drawable.pushicon);
			contentViewDone.setTextViewText(R.id.text, aContentText);
			contentViewDone.setTextViewText(R.id.text2, Html.fromHtml(aTickerText));
			mNotificationDone.contentView = contentViewDone;			
		}else
		{
//			 Intent notificationIntent = new Intent(this, KainatHomeActivity.class);
//			 PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//			 mNotificationDone = new Notification(icon, aTickerText, when);
//			 mNotificationDone.setLatestEventInfo(this, aTickerText, aContentText, contentIntent);
			
			 mNotification = new Notification.Builder(this)
	         .setContentTitle(aTickerText)
	         .setContentText(aContentText)
	         .setSmallIcon(icon)
	         .build(); // available from API level 11 and onwards
		}
			mNotificationManagerDone = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManagerDone.notify(NOTIFICATION_ID_DONE,
					mNotificationDone);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
}