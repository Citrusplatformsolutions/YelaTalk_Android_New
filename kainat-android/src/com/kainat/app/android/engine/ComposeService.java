package com.kainat.app.android.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.kainat.app.android.ConversationsActivity;
import com.kainat.app.android.bean.MediaPost;
import com.kainat.app.android.bean.MediaPost.MediaContent;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.MediaPostTable;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.util.CompressImage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;

public class ComposeService extends Service {

	String TEJAS_HOST = "api.rocketalk-production.com";
	public static Stack<MediaPost> media = new Stack<MediaPost>();
	String color1 = "#ff0000";
	String color2 = "#ff0000";
	private static boolean log = false;
	private static final String TAG = ComposeService.class.getSimpleName();
	
	
	HandlerThread uiThread;
	UIHandler uiHandler;
	private static HttpSynchInf httpSynchInfRefreshCureentActivity;
	public static void setHttpSynchRefreshCurrentView(HttpSynchInf httpSynchInf) {
		httpSynchInfRefreshCureentActivity = httpSynchInf;
	}

	@Override
	public void onCreate() {
		// uiThread = new HandlerThread("UIHandler");
		// uiThread.start();
		// uiHandler = new UIHandler(uiThread.getLooper());
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	String serverError = null;

	public static MediaPost mediaPostObject(Cursor cursor) {
		try{
		cursor.moveToFirst();
		MediaPost mediaPost = new MediaPost();
		mediaPost.DB_ID = cursor.getInt(cursor.getColumnIndex(MediaPostTable.COLUMN_ID));
		if (log)
			Log.i(TAG, "mediaPostObject :: mediaPost.DB_ID : " + mediaPost.DB_ID);
		mediaPost.TRY = cursor
				.getInt(cursor.getColumnIndex(MediaPostTable.TRY));
		mediaPost.attachmentSize = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.ATTACHMENT_SIZE)) ;
		mediaPost.attachment = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.ATTACHMENT));
		mediaPost.conversationId = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.CONVERSATION_ID));
		mediaPost.mediaText = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.ABOUT));
		mediaPost.cat = 49;// cursor.getInt(cursor.getColumnIndex(MediaPostTable.CATEGORY));
		mediaPost.mediaTag = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.TAG));
		
		mediaPost.comments = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.COMMENT));
		
		int mode = cursor.getInt(cursor.getColumnIndex(MediaPostTable.MODE));

		mediaPost.cltTxnId = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.CLTTXNID));

		mediaPost.userId = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.USER_ID));
		mediaPost.date = cursor.getLong(cursor
				.getColumnIndex(MediaPostTable.DATE));
		mediaPost.userPass = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.USER_PASS));
		mediaPost.clientId = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.CLIENT_ID));
		mediaPost.dist = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.DIST));

		mediaPost.msg_type = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.MSG_TYPE));
		mediaPost.msg_op = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.MSG_OP));
		mediaPost.reference_messageid = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.REFERENCE_MESSAGEID));
		mediaPost.req_id = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.REQ_ID));
		mediaPost.req_type = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.REQ_TYPE));
		
		mediaPost.command = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.COMMAND));
		mediaPost.commandType = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.COMMAND_TYPE));

		// mediaPost.u = cursor.getString(cursor
		// .getColumnIndex(MediaPostTable.TAG));

		mediaPost.mImagesPath = new ArrayList<MediaContent>();
		MediaContent content = mediaPost.new MediaContent();
		for (int i = 1; i < 21; i++) {
			int col_index = cursor.getColumnIndex("IMAGE_" + i);
			if (col_index != -1) {
				content = mediaPost.new MediaContent();
				content.contentPath = cursor.getString(col_index);
				if (content.contentPath != null)
					mediaPost.mImagesPath.add(content);
			}
		}
		mediaPost.mVideoPath = new ArrayList<MediaContent>();
		content = mediaPost.new MediaContent();
		// for (int i = 1; i < 21; i++)
		{
			int col_index = cursor.getColumnIndex(MediaPostTable.VIDEO);
			if (col_index != -1) {
				content = mediaPost.new MediaContent();
				content.contentPath = cursor.getString(col_index);
				if (content.contentPath != null)
					mediaPost.mVideoPath.add(content);
			}
		}

		int col_index = cursor.getColumnIndex(MediaPostTable.AUDIO);
		if (col_index != -1) {
			mediaPost.mVoicePath = mediaPost.new MediaContent();
			mediaPost.mVoicePath.contentPath = cursor.getString(col_index);

		}
		if (mode == 0)
			mediaPost.privacy = "Public";
		else if (mode == 1)
			mediaPost.privacy = "Private";
		else if (mode == 2)
			mediaPost.privacy = "Friend";

		// System.out.println("--------------featuredUserBean :  "+featuredUserBean.toString());
		return mediaPost;
		}catch (Exception e) {
			return null;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (log)
			Log.i(TAG, "onStartCommand ::  Compose service start, isSendingText : "+isSendingText + ", isSendingMultiple : "+isSendingMultiple);
		new Thread(new Runnable() {

			@Override
			public void run() {
				checkAndSendText();
				checkAndSendAttachment();
			}
		}).start();
		return START_STICKY;
	}

	public static boolean isSendingText = false;
	MediaPost m;

	private void checkAndSendText() 
	{
		if (log)
		{
			Log.i(TAG, "checkAndSendText ::  isSendingText : "+ isSendingText);
			Log.i(TAG, "checkAndSendText ::  isSendingMultiple : "+ isSendingMultiple);
		}
		if (isSendingText)
			return;
		Cursor cursor = getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
				null,
				MediaPostTable.MEDIS_STAUTS + " != ?  and "
						+ MediaPostTable.TRY + " < ? and "
						+ MediaPostTable.USER_ID + " = ? and "
						+ MediaPostTable.ATTACHMENT + " = ?",
				new String[] { MediaPostTable.STATUS_SENT, "4",
						"" + BusinessProxy.sSelf.getUserId(), "0" }, null);
		if (log){
			Log.i(TAG, "checkAndSendText ::  2isSendingText cursor count---"+ cursor.getCount());
		}
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			m = mediaPostObject(cursor);
			if(m!=null)
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						isSendingText = true;
						boolean res = false;
						do {
							try {
								if (log)
									Log.i(TAG, "checkAndSendText ::  sending text message  for----"+ m.conversationId);
								res = process(m);
								if (log)
									Log.i(TAG, "checkAndSendText ::  sending done text message for----"+ m.conversationId);
							} catch (Exception e) {
								if (log)
									Log.e(TAG, "checkAndSendText ::  error checkAndSend----"+ e.getLocalizedMessage());
//								e.printStackTrace();
							}
						} while (!res && setRetry(m));
						isSendingText = false;
						checkAndSendText();
						
					} catch (Exception e) {
						isSendingText = false;
						stopServiceL();
						if (log)
							Log.e(TAG, "checkAndSendText ::  error checkAndSend----"+ e.getLocalizedMessage());
//						e.printStackTrace();
					}
				}
			}).start();
			cursor.close();
		} else {
			cursor.close();
			stopService(new Intent(ComposeService.this, ComposeService.class));
			if (log)
				Log.i(TAG, "checkAndSendText ::  nothing to send, so stopping post service");
		}
	}

	public static boolean isSendingMultiple = false;

	private void checkAndSendAttachment() 
	{
		if (log){
			Log.i(TAG, "checkAndSendAttachment ::  3checkAndSendAttachment---"+ isSendingText);
			Log.i(TAG, "checkAndSendAttachment ::  3checkAndSendAttachment---"+ isSendingMultiple);
		}
		
		if (isSendingMultiple)
			return;

		Cursor cursor = getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
				null,
				MediaPostTable.MEDIS_STAUTS + " != ?  and "
						+ MediaPostTable.TRY + " < ? and "
						+ MediaPostTable.USER_ID + " = ? and "
						+ MediaPostTable.ATTACHMENT + " != ?",
				new String[] { MediaPostTable.STATUS_SENT, "4",
						"" + BusinessProxy.sSelf.getUserId(), "0" }, null);
		if (log){
			Log.i(TAG, "checkAndSendAttachment ::  3checkAndSendAttachment cursor count---"+ cursor.getCount());
		}
		if (cursor.getCount() > 0) {
			m = mediaPostObject(cursor);
			m = mediaPostObject(cursor);
			if(m!=null)
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						isSendingMultiple = true;
						//process(m);
						boolean res = false;
						do {
							try {
								if (log)
									Log.i(TAG, "checkAndSendAttachment ::  sending attachment for----"+ m.conversationId);
								res = process(m);
								if (log)
									Log.i(TAG, "checkAndSendAttachment ::  sending done attachment for----"+ m.conversationId);
							} catch (Exception e) {
								if (log)
									Log.e(TAG, "checkAndSendAttachment ::  error checkAndSend----"+e.getLocalizedMessage());
							}
						} while (!res && setRetry(m));
						isSendingMultiple = false;
						checkAndSendAttachment();
					} catch (Exception e) {
						isSendingMultiple = false;
						stopServiceL();
						if (log)
							Log.i(TAG, "checkAndSendAttachment ::  error checkAndSend----"+ e.getLocalizedMessage());
//						e.printStackTrace();
					}
				}
			}).start();
			cursor.close();
		} else {
			cursor.close();
			stopService(new Intent(ComposeService.this, ComposeService.class));
			if (log)
				Log.i(TAG, "checkAndSendAttachment ::  nothing to send, so attachment stopping post service");
		}
	}

	private void completed(MediaPost mediaPost, String s, String s2) {
		try {
			Uri notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
					notification);
			r.play();
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(500);
		} catch (Exception e) {
		}

	}

	private boolean process(MediaPost mediaPost) throws Exception {
		if (log) 
		{
			Log.i(TAG, "process ::  TRY---------"+ mediaPost.TRY);
			Log.i(TAG, "process ::  attachmentSize---------"+ mediaPost.attachmentSize);
			Log.i(TAG, "process ::  attachment---------"+ mediaPost.attachment);
		}
		String urlStr = "http://" + Urls.TEJAS_HOST+ "/tejas/rest/rtmessaging/postMessage";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlStr);
		String textMessage = "compose service";
		MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.STRICT);
		mpEntity.addPart("usrId", new StringBody("" + BusinessProxy.sSelf.getUserId()));
		if (log)
			Log.i(TAG, "process ::  devId -"+BusinessProxy.sSelf.getClientId());
		mpEntity.addPart("devId", new StringBody(""+BusinessProxy.sSelf.getClientId()));
		mpEntity.addPart("ltMFUId", new StringBody("28486"));
		mpEntity.addPart("msgType", new StringBody(mediaPost.msg_type));
		mpEntity.addPart("msgOp", new StringBody(mediaPost.msg_op));

		String dist = mediaPost.dist;
		if (log) {
			Log.i(TAG, "process ::  dist2 before : " + dist);
		}
//		Logger.conversationLog("Compose  dist-1 : ", dist);
		String distArr[] = Utilities.split(new StringBuffer(dist), ";");
		if(distArr.length>1){
			dist="" ;
		for (int i = 0; i < distArr.length; i++) {
			if(distArr[i].toLowerCase(Locale.getDefault()).toLowerCase(Locale.getDefault()).indexOf("<"+SettingData.sSelf.getUserName().toLowerCase(Locale.getDefault())+">")!= -1){
				
			}else
				dist = dist+distArr[i] +";";
		}
		}
		if(dist.lastIndexOf(";") != -1)
			dist =dist.substring(0,dist.length()-1);//10259076
		mediaPost.dist = dist ;
		if (log)
			Log.i(TAG, "process ::  dist before : "+dist);
//		
//		Logger.conversationLog("Compose  sending dist : ", dist+" : cid : "+mediaPost.conversationId+ ""+mediaPost.mediaText);
		String dd [] = Utilities.split(new StringBuffer(dist), ";");
//		
		if(dd !=null && dd.length==1)
		{
			if(dist!=null && dist.toLowerCase(Locale.getDefault()).indexOf("<"+SettingData.sSelf.getUserName().toLowerCase(Locale.getDefault())+">")!= -1)
				dist="";//SettingData.sSelf.getUserName().toLowerCase() ;
		}
		if (log)
			Log.i(TAG, "process ::  dist after: "+dist);
		dist = Utilities.replace(dist, SettingData.sSelf.getUserName().toLowerCase(Locale.getDefault()) + ";", " ");
		
		if(dist==null || dist.trim().length()<=0){
			dist=SettingData.sSelf.getUserName().toLowerCase(Locale.getDefault()) ;
		}
		mpEntity.addPart("dest", new StringBody(dist));
		// }
		if (log) 
		{
			Log.i(TAG, "process ::  text : " + ""+mediaPost.mediaText);
			Log.i(TAG, "process ::  dist sending : " + dist+" : cid : "+mediaPost.conversationId);
			Log.i(TAG, "process ::  conversationId : " + mediaPost.conversationId);
		}
		// mpEntity.addPart("msgTxt", new StringBody("1"));
		if(mediaPost.mediaText!=null && mediaPost.mediaText.trim().length()>0)
			mpEntity.addPart("msgTxt", new StringBody(mediaPost.mediaText));
		mpEntity.addPart("Vndr", new StringBody("TOMO"));
		mpEntity.addPart("reqId", new StringBody(mediaPost.req_id));
		mpEntity.addPart("reqType", new StringBody(mediaPost.req_type));
		
		if (log)
			Log.i(TAG, "process ::  Send Message Text :"+mediaPost.mediaText);
		
		if(mediaPost.comments!=null && mediaPost.comments.trim().equalsIgnoreCase("chat")){
		mpEntity.addPart("cmt", new StringBody(mediaPost.comments));
//		Logger.conversationLog("Compose  username : ", ""+SettingData.sSelf.getUserName());
			if (log)
			{
				Log.i(TAG, "process ::  conversationList.comments---cmt :"+mediaPost.comments);
			}
		}
		
		if(mediaPost.command!=null && mediaPost.command.trim().length()>0)
			mpEntity.addPart("cmdType", new StringBody(mediaPost.command));
		if(mediaPost.commandType!=null  && mediaPost.commandType.trim().length()>0)
			mpEntity.addPart("cmd", new StringBody(mediaPost.commandType));
		
		if (mediaPost.reference_messageid != null)
			mpEntity.addPart("refMsgId", new StringBody(mediaPost.reference_messageid));// fwdMsgId
		mpEntity.addPart("cltTxnId", new StringBody("" + mediaPost.cltTxnId));
		if (log)
			Log.i(TAG, "process ::  cltTxnId :"+mediaPost.cltTxnId);
		
		if (mediaPost.conversationId != null && mediaPost.conversationId.indexOf("NP") == -1){
			mpEntity.addPart("convId", new StringBody(mediaPost.conversationId));
			if (log)
				Log.i(TAG, "process ::  conversationId sending :"+mediaPost.conversationId);
		}
		else {
			if (log)
				Log.i(TAG, "process ::  not sending conversationId :");
		}
		if (mediaPost.mVoicePath != null&& mediaPost.mVoicePath.contentPath != null) 
		{
			File file = new File(mediaPost.mVoicePath.contentPath);
			String extension = MimeTypeMap.getFileExtensionFromUrl(mediaPost.mVoicePath.contentPath);
			if (log)
			{
				Log.i(TAG, "process ::  Audio File Path----------" + file.toString());
				Log.i(TAG, "process ::  extension audio----------" + extension);
			}
			mpEntity.addPart("file", new FileBody(file, "audio/" + extension));
		}
		if (mediaPost.mVideoPath != null && mediaPost.mVideoPath.size() > 0) {
			for (int i = 0; i < mediaPost.mVideoPath.size(); i++) {
				MediaPost.MediaContent mc = mediaPost.mVideoPath.get(i);
				File file = new File(mc.contentPath);
				String extension = MimeTypeMap.getFileExtensionFromUrl(mc.contentPath);
				if (log)
				{
					Log.i(TAG, "process ::  Video File Path----------" + file.toString());
					Log.i(TAG, "process ::  extension video----------" + extension);
				}
				if(extension==null || extension.trim().length()<=0)
					extension = "3gp" ;
				mpEntity.addPart("file", new FileBody(file, "video/"+ extension));
			}
		}
		if (mediaPost.mImagesPath != null && mediaPost.mImagesPath.size() > 0) {
			for (int i = 0; i < mediaPost.mImagesPath.size(); i++) {
				MediaPost.MediaContent mc = mediaPost.mImagesPath.get(i);
				CompressImage compressImage = new CompressImage(this) ;
				mc.contentPath = compressImage.compressImage(mc.contentPath) ;
				File file = new File(mc.contentPath);
				String extension = MimeTypeMap.getFileExtensionFromUrl(mc.contentPath);
				if (log)
				{
					Log.i(TAG, "process ::  Image File Path----------" + file.toString());
					Log.i(TAG, "process ::  extension image----------"+ extension);
				}
				if(extension==null || extension.trim().length()<=0)
					extension = "jpg" ;
				mpEntity.addPart("file", new FileBody(file, "image/"+ extension));
			}
		}
		// } catch (Exception e1) {
		// if(!setRetry(mediaPost)){
		// stopServiceL();
		// }
		// if(log)
		// System.out.println("Rocketalk ComposeService------------error process-2---"+e1.getLocalizedMessage());
		// e1.printStackTrace();
		// }
		if (log)
			Log.i(TAG, "process ::  mediaPost.toString---"+ mediaPost.toString());

		httppost.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		httppost.setHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
		httppost.setHeader("password", HttpHeaderUtils.encriptPass(
		SettingData.sSelf.getPassword()));
		httppost.setHeader("authkey", "RTAPP@#$%!@");
		httppost.setEntity(mpEntity);
		HttpResponse response;
		// try {
		response = httpclient.execute(httppost);
		if (log)
			Log.i(TAG, "process ::  " + response.getStatusLine().getStatusCode());
		HttpEntity resEntity = response.getEntity();
		HttpEntity r_entity = response.getEntity();
		String responseString = EntityUtils.toString(r_entity);
		if (resEntity != null) {
			if (log)
				Log.i(TAG, "process ::  Post Response String : " + responseString);
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		} 
//		long cid = 0;
		boolean isSucess = false;
		try {
//			cid = Long.parseLong(responseString);
			if(responseString.indexOf("<")== -1)
				isSucess = true;
			else
				isSucess = false;
		} catch (Exception e) {
			isSucess = false;
			if (log)
			FeedTask.ERROR = responseString ;
		}
		if (isSucess)//cid > 0) 
		{
			showAlert("cidd: " + responseString + " dist: " + mediaPost.dist);
			// saveMessage(message);
			ContentValues mUpdateValues = new ContentValues();

			mUpdateValues.put(MediaPostTable.MEDIS_STAUTS,
					MediaPostTable.STATUS_SENT);
			mUpdateValues.put(MediaPostTable.SENT_DATE,
					System.currentTimeMillis());
			mUpdateValues.put(MediaPostTable.ERROR_MSG, "None");
			int mRowsUpdated = getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
					mUpdateValues, // the
					MediaPostTable.COLUMN_ID + "=" + mediaPost.DB_ID, // the
																		// column
																		// to
																		// select
																		// on
					null // the value to compare to
					);
			if (log)
				Log.i(TAG, "process ::  CONTENT_URI_INBOX_COMPOSE-------mRowsUpdated--"+ mRowsUpdated);
			mUpdateValues = new ContentValues();
			mUpdateValues.put(MessageTable.SENDING_STATE, Constants.MESSAGE_STATUS_SENT);
			mUpdateValues.put(MessageTable.CONVERSATION_ID, responseString);
			mUpdateValues.put(MessageTable.HAS_BEEN_VIEWED, "true");
			// mUpdateValues.put(MessageTable.MSG_TXT,
			// mediaPost.mediaText);
			mUpdateValues.put(MessageTable.HAS_BEEN_VIEWED_BY_SIP, "true");
			mRowsUpdated = getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX, mUpdateValues, // the
					MessageTable.MESSAGE_ID + "=" + mediaPost.date, // the
																	// column
																	// to
																	// select
																	// on
					null // the value to compare to
					);
			
			if(log) 
			{
				Log.i(TAG, "process ::  mediaPost.date---------"+ mediaPost.date);
				Log.i(TAG, "process ::  mediaPost.conversationId---------"+ mediaPost.conversationId);
				Log.i(TAG, "process ::  CONTENT_URI_INBOX-------mRowsUpdated--"+ mRowsUpdated);
			}
			
			mUpdateValues.put(MessageConversationTable.INSERT_TIME, System.currentTimeMillis());
			if(mediaPost.dist.indexOf("a:") == -1 || mediaPost.dist.trim().equalsIgnoreCase("a:help")){
				
				// new added in nonfriend message type
			if(ConversationsActivity.CONVERSATIONID!=null && ConversationsActivity.CONVERSATIONID.startsWith("NP"))
			{
				Cursor cursor2 = getContentResolver().query(
						MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
						null,
						MessageConversationTable.CONVERSATION_ID + " = ?",
						new String[] {responseString}, null);
				if(cursor2.getCount()>0){
					 int i = getContentResolver().delete(
					 MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					 MessageConversationTable.CONVERSATION_ID+ " = ? ",
					 new String[] { ""+mediaPost.conversationId });
					 if (log) {
						 Log.i(TAG, "process ::  deleting info form conversation list because is already in lisy : "+ responseString);
					 }
				}
				cursor2.close();
			}
			mRowsUpdated = getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					mUpdateValues, // the
					MessageTable.CONVERSATION_ID + "='"
							+ mediaPost.conversationId + "'", // the
					// column
					// to
					// select
					// on
					null // the value to compare to
					);
			}
			if(ConversationsActivity.CONVERSATIONID!=null && ConversationsActivity.CONVERSATIONID.startsWith("NP"))
			ConversationsActivity.CONVERSATIONID = responseString;
			
			if(mediaPost.conversationId!=null && ConversationsActivity.CONVERSATIONID!=null && ConversationsActivity.CONVERSATIONID.equals(mediaPost.conversationId))
				refreshList = true ;
			if(httpSynchInfRefreshCureentActivity!=null)
				httpSynchInfRefreshCureentActivity.onResponseSucess("REFRESH VIEW FEOM COMPOSE", 2);
			
			if (log)
				Log.i(TAG, "process ::  CONTENT_URI_INBOX_CONVERSATION_LIST-------mRowsUpdated--"+ mRowsUpdated);

			//Commented by mahesh, as there will not be any play after sent message.
//			MediaEngine.getMediaEngineInstance().playResource(R.raw.off);
		}
		else {
			showAlert("res: " + responseString + " dist: " + mediaPost.dist);
			if (!setRetry(mediaPost)) {
				stopServiceL();
			}
		}
		// checkAndSend();
		return true;// "sucess" ;
	}
	
	public static boolean refreshList = false; ;
	private final class UIHandler extends Handler {
		public static final int DISPLAY_UI_TOAST = 0;
		public static final int DISPLAY_UI_DIALOG = 1;

		public UIHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UIHandler.DISPLAY_UI_TOAST: {
				Context context = getApplicationContext();
				Toast t = Toast.makeText(context, (String) msg.obj,
						Toast.LENGTH_LONG);
				t.show();
			}
			case UIHandler.DISPLAY_UI_DIALOG:
				// TBD
			default:
				break;
			}
		}
	}

	public void showAlert(String s) {

		// Message msg = uiHandler.obtainMessage(UIHandler.DISPLAY_UI_TOAST);
		// msg.obj = message;
		// uiHandler.sendMessage(msg);

		// Toast.makeText(this, s, Toast.LENGTH_LONG).show() ;
	}

	private boolean setRetry(MediaPost mediaPost) 
	{
		Log.i(TAG, "setRetry ::  setRetry------"+ mediaPost.TRY);
		if (mediaPost.TRY < 4) {
			ContentValues mUpdateValues = new ContentValues();
			mUpdateValues.put(MediaPostTable.TRY, ++mediaPost.TRY);
			// mUpdateValues.put(MediaPostTable.SENT_DATE,
			// System.currentTimeMillis());
			mUpdateValues.put(MediaPostTable.ERROR_MSG, "None");
			int mRowsUpdated = getContentResolver().update(
					MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
					mUpdateValues, // the
					MediaPostTable.COLUMN_ID + "=" + mediaPost.DB_ID, null // the
																			// value
																			// to
																			// compare
																			// to
					);
			
			if (log)
				Log.i(TAG, "setRetry ::  CONTENT_URI_INBOX_COMPOSE-------mRowsUpdated--"+ mRowsUpdated);

			if (mediaPost.TRY == 3)
				setUnableToSend(mediaPost);
			return true;
		} else {
			setUnableToSend(mediaPost);
		}
		return false;
	}

	private void setUnableToSend(MediaPost mediaPost) {
		if (log)
			Log.i(TAG, "setUnableToSend ::  setUnableToSend------");
		ContentValues mUpdateValues = new ContentValues();
		mUpdateValues.put(MessageTable.SENDING_STATE,
				Constants.MESSAGE_STATUS_UNABLE_TO_SEND);
		int mRowsUpdated = getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX, mUpdateValues, // the
				MessageTable.MESSAGE_ID + "=" + mediaPost.date, // the
																// column
																// to
																// select
																// on
				null // the value to compare to
				);
		
		if (log)
			Log.i(TAG, "setUnableToSend ::  CONTENT_URI_INBOX--setUnableToSend-----mRowsUpdated--"+ mRowsUpdated);
		if(ConversationsActivity.CONVERSATIONID.equals(mediaPost.conversationId))
			refreshList = true ;
		if(httpSynchInfRefreshCureentActivity!=null)
			httpSynchInfRefreshCureentActivity.onResponseSucess("REFRESH VIEW FEOM COMPOSE", 2);
		
		// getContentResolver().delete(
		// MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
		// MediaPostTable.COLUMN_ID+ " = ? ",
		// new String[] { ""+mediaPost.DB_ID });
	}

	private void stopServiceL() 
	{
		if (log)
			Log.i(TAG, "stopServiceL ::  stopServiceL");
		stopService(new Intent(ComposeService.this, ComposeService.class));
	}

}