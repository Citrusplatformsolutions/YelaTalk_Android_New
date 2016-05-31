/**
 * 
 */
package com.kainat.app.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.kainat.app.android.VerificationActivity;
import com.kainat.app.android.bean.MediaPost;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;

/**
 * @author Mahesh Sonker
 *
 */
public class AdConnectionManager {
	
	private String appKey;
	private String rtParams;
	private String screenName;
	private String userParams;
	private String ADVTKey;
	private String stats;
	private static final String TAG = VerificationActivity.class.getSimpleName();
	public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public static AdConnectionManager adConnectionManager;
	
	public static AdConnectionManager getInstance(){
		if(adConnectionManager==null)
			adConnectionManager =new AdConnectionManager();
		return adConnectionManager ;
	}
	
	/**
	 * @param aUrls
	 * @param iD
	 * @param aUserParams
	 */
//	public AdConnectionManager(String[] aUrls, String iD, String aUserParams) {
//		// TODO Auto-generated constructor stub
//	}

	public void setAppKey(String key){
		appKey = key;
	}
	public void setRTParams(String params){
		rtParams = params;
	}
	public void setScreen(String screen){
		screenName = screen;
	}
	public void setUserParams(String uParams){
		userParams = uParams;
	}
	public void setADVTKey(String advtKey){
		ADVTKey = advtKey;
	}
	
	/**
	 * @param urlLocal
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	/*public HttpResponse getResponse(final String url) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("RT-APP-KEY", appKey);
		httpget.setHeader("AD_PARAM", ClientProperty.RT_PARAMS);
		//httpget.setHeader("CURR-SRCN", getCurrentScreen(sScreenNumber));
		httpget.setHeader("CURR-SRCN", screenName);
		if(userParams != null && ADVTKey != null){
			httpget.setHeader("userParam", userParams);
			httpget.setHeader("advtKey", ADVTKey);
		}
		String advtSessionKey = BusinessProxy.sSelf.advertisementData.get("advtSessionKey");
		if(advtSessionKey!=null)
		httpget.setHeader("advtSessionKey", advtSessionKey);
		
		String advtKey = BusinessProxy.sSelf.advertisementData.get("advtKey");
		if(advtKey!=null)
			httpget.setHeader("advtkey", advtKey);
			
		return client.execute(httpget);
	}*/
	public static int tryCtr = 0 ;
	public HttpResponse getResponse(final String url) throws ClientProtocolException, IOException {
		try{
			
//		if(1==1)return null;
		  DefaultHttpClient client = new DefaultHttpClient();
//		  if(SettingData.sSelf.isIm())
//		  System.out.println("------------url------------- : "+url);
//		  System.out.println("------------SEO------------- : "+BusinessProxy.sSelf.SEO);
		  HttpGet httpget = new HttpGet(url);
		  if(BusinessProxy.sSelf.getUserId()>0)
		  httpget.setHeader("RT-APP-KEY", ""+BusinessProxy.sSelf.getUserId());
		  httpget.setHeader("AD_PARAM", ClientProperty.RT_PARAMS);
		  if(screenName!=null)
		  httpget.setHeader("CURR-SRCN", screenName);
		  if(BusinessProxy.sSelf.SEO!=null)
		  httpget.setHeader("CURR-SEO", BusinessProxy.sSelf.SEO);
		  if(getStats()!=null&&getStats().trim().length()>0){
			  httpget.setHeader("STATS", getStats());
//			  if(SettingData.sSelf.isIm())
//			  System.out.println("------------add STATS : "+getStats());
		  }
		  BusinessProxy.sSelf.SEO = null ;
//		  System.out.println("------------getResponse ScreenNaame------------- : "+screenName);
		  httpget.setHeader("CURR-SRCN", screenName);
		  if(!BusinessProxy.sSelf.advertisementData.isEmpty()){
		   if(BusinessProxy.sSelf.advertisementData.get("advtSessionKey") != null)
		    httpget.setHeader("advtSessionKey", BusinessProxy.sSelf.advertisementData.get("advtSessionKey"));
		   if(BusinessProxy.sSelf.advertisementData.get("userParam") != null){
		    httpget.setHeader("userParam", BusinessProxy.sSelf.advertisementData.get("userParam"));
//		    System.out.println("------------adconnetion userParam------------- : "+BusinessProxy.sSelf.advertisementData.get("userParam"));
		   }
		   httpget.setHeader("advtKey", BusinessProxy.sSelf.advertisementData.get("advtKey"));
		  }
		  return client.execute(httpget);
		}catch (Exception e) {
			tryCtr++;
			if(tryCtr<3)
				return getResponse(url);
		}
			return null ;
		 }

/*	
	 � �private HttpResponse sendStats(final String url,String Stats) throws ClientProtocolException, IOException {
	 � � � �DefaultHttpClient client = new DefaultHttpClient();
	 � � � �HttpGet httpget = new HttpGet(url);
	 � � � �httpget.setHeader("RT-APP-KEY", ""+BusinessProxy.sSelf.getUserId());
	 � � � �httpget.setHeader("RT-Params", ClientProperty.RT_PARAMS);
	 � � � �httpget.setHeader("STATS", stats.toString());
	 � � � �return client.execute(httpget);
	 � �}
	 */
/*	public String sendStats(String urlStr,String stats ){
		try
		{
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.addRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
		conn.setRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
		conn.addRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		conn.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		conn.setRequestProperty("STATS", stats.toString());

//		writeToFile(new String(messageByteArray));
//		System.out.println("----------messageByteArray lenght---------------"+messageByteArray.length);
		// Send the body
//		OutputStream dataOS = conn.getOutputStream();
//		dataOS.write(messageByteArray);
//		dataOS.flush();
//		dataOS.close();

		// Ensure we got the HTTP 200 response code
		int responseCode = conn.getResponseCode();
		if (responseCode != 200) {
			throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
		}

		// Read the response
		InputStream is = conn.getInputStream();
		return IOUtils.read(is);
		
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}*/

	/**
	 * @param urlStr
	 * @param postParams
	 * @param imageData
	 * @return
	 * @throws Exception
	 */
	public String uploadByteData(String urlStr, Hashtable<String, String> postParams,Hashtable<String, String> header, Payload payload) throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			// This is the standard format for a multipart request
			byte messageByteArray[] = new byte[0];
			messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
			if(header != null)
				Log.v(TAG, "uploadByteData :: header : "+header.toString());
			if(postParams!=null){
				Log.i(TAG, "uploadByteData :: postParams : "+postParams.toString());
				for (Iterator<String> iterator = postParams.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = postParams.get(key);
	//				System.out.println("-------------sendding response----key--"+key);
	//				System.out.println("-------------sendding response----value--"+value);
					messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"".getBytes("UTF-8"));
					messageByteArray = ArrayUtils.addAll(messageByteArray, key.getBytes("UTF-8"));
					messageByteArray = ArrayUtils.addAll(messageByteArray, "\"\r\n\r\n".getBytes("UTF-8"));
					messageByteArray = ArrayUtils.addAll(messageByteArray, value.getBytes("UTF-8"));
					messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
					messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
					messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				}
			}
			int offset = 0;
			while (payload!=null&&payload.mPayloadTypeBitmap > 0) {
			switch (payload.mPayloadTypeBitmap & (1 << offset)) {
			case Payload.PAYLOAD_BITMAP_VOICE:
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"voiceData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mVoice[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mVoice[0] = null ;
				break;
			case Payload.PAYLOAD_BITMAP_PICTURE:
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"picData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mPicture[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mPicture[0] = null ;
				break;
			case Payload.PAYLOAD_BITMAP_VIDEO:
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"videoData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mVideo[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mVideo[0] = null ;
				break;			
			}
			payload.mPayloadTypeBitmap = payload.mPayloadTypeBitmap & ~(1 << offset);
			offset++;
			}
			
			//			if (imageData != null) {
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"picData\"".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, imageData);
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "--\r\n".getBytes("UTF-8"));
//			}

			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + BOUNDRY);

			conn.addRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
			conn.setRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());

			conn.addRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
			conn.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());

			
			if(header!=null){
//				System.out.println("------header--------"+header.toString());
				for (Iterator<String> iterator = header.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = header.get(key);
//					System.out.println("-------------sendding response----key--"+key);
//					System.out.println("-------------sendding response----value--"+value);
					conn.addRequestProperty(key, "" + value);
				}
			}
			
//			writeToFile(new String(messageByteArray));
//			System.out.println("----------messageByteArray lenght---------------"+messageByteArray.length);
			// Send the body
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(messageByteArray);
			dataOS.flush();
			dataOS.close();

			// Ensure we got the HTTP 200 response code
			tryCtr = 0 ;
			int responseCode = conn.getResponseCode();
			
//			System.out.println("----------responseCode---------------"+responseCode);
			if (responseCode != 200) {
				throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
			}

			// Read the response
			is = conn.getInputStream();
			return IOUtils.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	public String uploadByteDataAPI(String urlStr, Hashtable<String, String> postParams,Hashtable<String, String> header, MediaPost mediaPost) throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlStr);
			
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.STRICT);
			for (Iterator<String> iterator = postParams.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				String value = postParams.get(key);
				mpEntity.addPart(key,new StringBody("" + value));
			}
			
			
			if(header!=null){
//				System.out.println("------header--------"+header.toString());
				for (Iterator<String> iterator = header.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = header.get(key);
					httppost.setHeader(key, value);
				}
			}
			
			if (mediaPost.mImagesPath != null && mediaPost.mImagesPath.size() > 0) {
				for (int i = 0; i < mediaPost.mImagesPath.size(); i++) {
					MediaPost.MediaContent mc = mediaPost.mImagesPath.get(i);
					File file = new File(mc.contentPath);
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(mc.contentPath);
//					if (log)
//						System.out
//								.println("Rocketalk-----------------extension image----------"
//										+ extension);
					if(extension==null || extension.trim().length()<=0)
						extension = "jpg" ;
					mpEntity.addPart("data", new FileBody(file, "image/"
							+ extension));
				}
			}
			httppost.setEntity(mpEntity);
			
			HttpResponse response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);
//			if(responseString!=null)
				return responseString ;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	public String uploadByteData2(String urlStr, Hashtable<String, String> postParams,Hashtable<String, String> header, Payload payload) throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			// This is the standard format for a multipart request
			byte messageByteArray[] = new byte[0];
			messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
			if(postParams!=null)
			for (Iterator<String> iterator = postParams.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next();
				String value = postParams.get(key);
//				System.out.println("-------------sendding response----key--"+key);
//				System.out.println("-------------sendding response----value--"+value);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, key.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\"\r\n\r\n".getBytes("UTF-8"));
				System.out.println("value------"+value);
				System.out.println("messageByteArray length ------"+messageByteArray.length);
				
				System.out.println("messageByteArray Data ------"+new String(messageByteArray));
				messageByteArray = ArrayUtils.addAll(messageByteArray, value.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				System.out.println("messageByteArray Data ------"+new String(messageByteArray));
			}
			int offset = 0;
			while (payload!=null&&payload.mPayloadTypeBitmap > 0) {
			switch (payload.mPayloadTypeBitmap & (1 << offset)) {
			case Payload.PAYLOAD_BITMAP_VOICE:
//				System.out.println("-------------------------attaching voice data--------------------"+payload.mVoice[0].length);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"voiceData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mVoice[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mVoice[0] = null ;
				break;
			case Payload.PAYLOAD_BITMAP_PICTURE:
//				System.out.println("-------------------------attaching pic data--------------------"+payload.mPicture[0].length);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"picData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mPicture[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mPicture[0] = null ;
				break;
			case Payload.PAYLOAD_BITMAP_VIDEO:
//				System.out.println("-------------------------attaching video data--------------------"+payload.mVideo[0].length);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"videoData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mVideo[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mVideo[0] = null ;
				break;			
			}
			payload.mPayloadTypeBitmap = payload.mPayloadTypeBitmap & ~(1 << offset);
			offset++;
			}
			
			//			if (imageData != null) {
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"picData\"".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, imageData);
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "--\r\n".getBytes("UTF-8"));
//			}

			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "text/html; charset=utf-8; boundary=" + BOUNDRY);

//			conn.addRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
//			conn.setRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
//			conn.setRequestProperty(
//					"RT-APP-KEY",
//					HttpHeaderUtils.createRTAppKeyHeader(10253730,"aaaaaa"));
			
			System.out.println("RT-APP-KEY : "+
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));
			conn.setRequestProperty(
					"RT-APP-KEY",
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));
			
//			conn.addRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
//			conn.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());

			
			if(header!=null){
//				System.out.println("------header--------"+header.toString());
				for (Iterator<String> iterator = header.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = header.get(key);
//					System.out.println("-------------sendding response----key--"+key);
//					System.out.println("-------------sendding response----value--"+value);
					conn.addRequestProperty(key, "" + value);
				}
			}
			
//			writeToFile(new String(messageByteArray));
//			System.out.println("----------messageByteArray lenght---------------"+messageByteArray.length);
			// Send the body
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(messageByteArray);
			dataOS.flush();
			dataOS.close();

			// Ensure we got the HTTP 200 response code
			tryCtr = 0 ;
			int responseCode = conn.getResponseCode();
			
//			System.out.println("----------responseCode---------------"+responseCode);
			if (responseCode != 200) {
				throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
			}

			// Read the response
			is = conn.getInputStream();
			return IOUtils.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
public String uploadByteDataBody(String urlStr, String postStr,Hashtable<String, String> header) throws Exception {
		
		HttpURLConnection conn = null;
		InputStream is = null;
		try {		
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
						conn.setRequestProperty(
					"RT-APP-KEY",
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));
			
			
			if(header!=null){
				for (Iterator<String> iterator = header.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = header.get(key);
					conn.addRequestProperty(key, "" + value);
				}
			}
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(postStr.getBytes());
			dataOS.flush();
			dataOS.close();

			tryCtr = 0 ;
			int responseCode = conn.getResponseCode();

			if (responseCode != 200) {
				throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
			}

			is = conn.getInputStream();
			return IOUtils.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	public String uploadByteDataBody(String urlStr, String postStr,Hashtable<String, String> header, Payload payload) throws Exception {
		String BOUNDRY = "0xKhTmLbOuNdArY";
		HttpURLConnection conn = null;
		InputStream is = null;
		try {
			// This is the standard format for a multipart request
			byte messageByteArray[] = new byte[0];
			messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
			messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
//			if(postParams!=null)
//			for (Iterator<String> iterator = postParams.keySet().iterator(); iterator.hasNext();) {
//				String key = iterator.next();
//				String value = postParams.get(key);
////				System.out.println("-------------sendding response----key--"+key);
////				System.out.println("-------------sendding response----value--"+value);
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, key.getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\"\r\n\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, value.getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
//			}
			int offset = 0;
			while (payload!=null&&payload.mPayloadTypeBitmap > 0) {
			switch (payload.mPayloadTypeBitmap & (1 << offset)) {
			case Payload.PAYLOAD_BITMAP_VOICE:
//				System.out.println("-------------------------attaching voice data--------------------"+payload.mVoice[0].length);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"voiceData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mVoice[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mVoice[0] = null ;
				break;
			case Payload.PAYLOAD_BITMAP_PICTURE:
//				System.out.println("-------------------------attaching pic data--------------------"+payload.mPicture[0].length);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"picData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mPicture[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mPicture[0] = null ;
				break;
			case Payload.PAYLOAD_BITMAP_VIDEO:
//				System.out.println("-------------------------attaching video data--------------------"+payload.mVideo[0].length);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"videoData\"".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, payload.mVideo[0]);
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
				payload.mVideo[0] = null ;
				break;			
			}
			payload.mPayloadTypeBitmap = payload.mPayloadTypeBitmap & ~(1 << offset);
			offset++;
			}
			
			//			if (imageData != null) {
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Disposition: form-data; charset=utf-8; name=\"picData\"".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "Content-Type: application/octet-stream".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n\r\n".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, imageData);
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "\r\n--".getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, BOUNDRY.getBytes("UTF-8"));
//				messageByteArray = ArrayUtils.addAll(messageByteArray, "--\r\n".getBytes("UTF-8"));
//			}

			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + BOUNDRY);

//			conn.addRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
//			conn.setRequestProperty("RT-APP-KEY", "" + BusicomnessProxy.sSelf.getUserId());
//			conn.setRequestProperty(
//					"RT-APP-KEY",
//					HttpHeaderUtils.createRTAppKeyHeader(10253730,"aaaaaa"));
			
			conn.setRequestProperty(
					"RT-APP-KEY",
					HttpHeaderUtils.createRTAppKeyHeader(
							BusinessProxy.sSelf.getUserId(),
							SettingData.sSelf.getPassword()));
			
//			conn.addRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
//			conn.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());

			
			if(header!=null){
//				System.out.println("------header--------"+header.toString());
				for (Iterator<String> iterator = header.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = header.get(key);
//					System.out.println("-------------sendding response----key--"+key);
//					System.out.println("-------------sendding response----value--"+value);
					conn.addRequestProperty(key, "" + value);
				}
			}
			
//			writeToFile(new String(messageByteArray));
//			System.out.println("----------messageByteArray lenght---------------"+messageByteArray.length);
			// Send the body
			OutputStream dataOS = conn.getOutputStream();
			dataOS.write(postStr.getBytes());
			dataOS.flush();
			dataOS.close();

			// Ensure we got the HTTP 200 response code
			tryCtr = 0 ;
			int responseCode = conn.getResponseCode();
			
//			System.out.println("----------responseCode---------------"+responseCode);
			if (responseCode != 200) {
				throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
			}

			// Read the response
			is = conn.getInputStream();
			return IOUtils.read(is);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	private static void writeToFile(String stacktrace) {
		File file = new File("/sdcard/comm_post.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(stacktrace);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
