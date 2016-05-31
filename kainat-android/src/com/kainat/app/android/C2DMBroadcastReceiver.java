package com.kainat.app.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.example.kainat.util.AppUtil;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.Utilities;

public class C2DMBroadcastReceiver extends BroadcastReceiver {
	public static boolean wasScreenOn = true;
	public static int pushScreen = -1;
	Context context;
	public static Vector<String> notification = new Vector<String>() ;
	public void onReceive(Context context, Intent intent) {
		try {
			this.context = context ;
			if (intent != null) 
			{
				String action = intent.getAction();
				if (action != null) {
					if (action.equals(Intent.ACTION_SCREEN_OFF)) {
						wasScreenOn = false;
					} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
						wasScreenOn = true;
					} else if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
						handleRegistration(context, intent);
					} else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
						handleMessage(context, intent);
					}
					else if (action.equals("com.kainat.app.android.engine.ComposeService")) {
						Utilities.startComposeService(context);
					}
					else if (action.equals("com.kainat.app.android.engine.RefreshService")) {
						Utilities.checkAndStartRefresh(context);
					}
				}
			}
		} catch (Exception e) {
			if (e != null) {
				if (BusinessProxy.sSelf.mUIActivityManager != null) {
					BusinessProxy.sSelf.mUIActivityManager.makeToast(e.getMessage());
				}
			}
		}
	}

	private void handleRegistration(Context context, Intent intent) {
		String registration = intent.getStringExtra("registration_id");
		if (intent.getStringExtra("error") != null) {
			if(Logger.ENABLE)Logger.debug("ERROR", "Error received " + registration);
		} else if (intent.getStringExtra("unregistered") != null) {
			if(Logger.ENABLE)Logger.debug("ERROR", "Un-Registered " + registration);
		} else if (registration != null) {
			if(Logger.ENABLE)Logger.debug("registration done", "OK Done");
			int tot = BusinessProxy.sSelf.getRecordCount(DBEngine.PUSH_TABLE);
			ContentValues conValues = new ContentValues();
			conValues.put("regid", registration);
			List<ContentValues> values = new ArrayList<ContentValues>();
			values.add(conValues);
			if (tot > 0) {
				BusinessProxy.sSelf.updateTableValues(DBEngine.PUSH_TABLE, "0", conValues);
			} else {
				BusinessProxy.sSelf.insertValuesInTable(DBEngine.PUSH_TABLE, values);
			}
			Utilities.sRegId = registration;
//			if(Logger.ENABLE)
			System.out.println("registration ::::::::::::::::::::::::::: "+registration);
		}
	}

	private void handleMessage(Context context, Intent intent) {
		if(Logger.ENABLE)Logger.debug("Handling Messageeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "Yes");
		String message = intent.getExtras().getString("C2D_MESSAGE");
		if(Logger.ENABLE)Logger.debug("Message = ", "" + message);

		String message4 = intent.getExtras().getString("message");
		if(!paserJson(message4)){
		if(Logger.ENABLE)Logger.debug("message4 = ", "" + message4);
		String s = message4;
		if (s != null && s.indexOf("<") != -1 && s.indexOf(">") != -1) {
			s = s.substring(0, s.indexOf("<"));
		}
		if (s == null) {
			s = "";
		}
		if (BusinessProxy.sSelf.mUIActivityManager != null) {
//			BusinessProxy.sSelf.mUIActivityManager.addNotification(s, s);
		}
		notification.add(s);
		Utilities.showNotification("RockeTalk",  s,context,true,-1) ;
		}
//		showNotification(s, s);

		String registration = intent.getStringExtra("registration_id");
		
		if (intent.getStringExtra("error") != null) {
		} else if (intent.getStringExtra("unregistered") != null) {
		} else if (registration != null) {
		}
	}
	
	public boolean paserJson(String response){
		try{
			String alert = null ;
			String screen = null ;
			System.out.println(" push message: "+response);
			JSONObject	responseObj = new JSONObject(response);
			JSONArray stationListObj = null;
			if (responseObj.has("aps")){
				JSONObject responseObj2 = (responseObj.getJSONObject("aps")); 
				if (responseObj2.has("alert")){
					System.out.println(responseObj2.getString("alert"));
					alert = responseObj2.getString("alert") ;
				}
				if (responseObj2.has("sound")){
					System.out.println(responseObj2.getString("sound"));
//					screen = responseObj2.getString("alert") ;
				}
			}
			if (responseObj.has("screen"));{
				System.out.println(responseObj.getString("screen"));
				screen = responseObj.getString("screen") ;
			}
			if(screen!=null){
				if(screen.equalsIgnoreCase("GROUPLIST")){
					pushScreen = UIActivityManager.SCREEN_COMMUNITY_CHAT ;
					Utilities.showNotification("RockeTalk",  alert,context,true,UIActivityManager.SCREEN_COMMUNITY_CHAT) ;
				}
				else if(screen.equalsIgnoreCase("GROUPLIST")){
					pushScreen = UIActivityManager.SCREEN_COMMUNITY_CHAT ;
					Utilities.showNotification("RockeTalk",  alert,context,true,UIActivityManager.SCREEN_COMMUNITY_CHAT) ;
				}
			}
			return true ;
			}catch (Exception e) {
				e.printStackTrace() ;
				return false ;
			}
	}
	Notification mNotification;
	public static NotificationManager mNotificationManager;
	boolean toggle;
	int HELLO_ID = 1;
	public void showNotification(String aTickerText, String aContentText) {
		if (BusinessProxy.sSelf.mUIActivityManager != null && !BusinessProxy.sSelf.mUIActivityManager.isForeOrBack() )
		{
			return;
		}
		notification.add(aContentText);
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
			return;
		}
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		try {
			CharSequence contentTitle = "RockeTalk"; // expanded message title
			//        	    CharSequence tickerText = "You have new message in RockeTalk Inbox! Please click on the message to view!!";              // ticker-text
			//        	    CharSequence contentText = "You have new message in RockeTalk Inbox! Please click on the message to view!!";      // expanded message text
			int icon = R.drawable.pushicon; // icon from resources
			long when = System.currentTimeMillis(); // notification time - When to notify 
//			Context context = getApplicationContext(); // application Context
			if (mNotificationManager != null)
				mNotificationManager.cancel(HELLO_ID);
			if (toggle) {
				HELLO_ID = 2;
				toggle = false;
			} else {
				HELLO_ID = 1;
				toggle = true;
			}
			if (mNotification == null) {
				mNotification = new Notification(icon, aTickerText, when);
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotification.defaults |= Notification.DEFAULT_LIGHTS;
			}
			mNotification.number = BusinessProxy.sSelf.newMessageCount;
			mNotification.tickerText = aTickerText;
			//Default
			Intent notificationIntent = new Intent(context, KainatHomeActivity.class);//(UIActivityManager.this, ComposeActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			/*mNotification.setLatestEventInfo(context, contentTitle, aContentText, contentIntent);			
			mNotification.defaults |= Notification.DEFAULT_SOUND;
			mNotification.defaults |= Notification.DEFAULT_VIBRATE;
			mNotification.ledARGB = 0xff00ff00;
			mNotification.ledOnMS = 300;
			mNotification.ledOffMS = 1000;
			mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;			
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(HELLO_ID, mNotification);*/
			//Default
			/*Intent notificationIntent = new Intent(context, InboxActivity.class);//(UIActivityManager.this, ComposeActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			mNotification.setLatestEventInfo(context, contentTitle, aContentText, contentIntent);
			mNotification.defaults |= Notification.DEFAULT_SOUND;
			mNotification.defaults |= Notification.DEFAULT_VIBRATE;
			mNotification.ledARGB = 0xff00ff00;
			mNotification.ledOnMS = 300;
			mNotification.ledOffMS = 1000;
			mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;	
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(HELLO_ID, mNotification);*/
			
			//Custom
	        notificationIntent = new Intent(context, KainatHomeActivity.class);
//	        contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	        contentIntent = PendingIntent.getActivity(
					context, 0, notificationIntent, 0);
	        /*mNotification.contentIntent = contentIntent;
	        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.noti_alert);
	    	contentView.setImageViewResource(R.id.image, R.drawable.pushicon);
	    	contentView.setTextViewText(R.id.text, aContentText);//"Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
	    	mNotification.contentView = contentView;
	    	*/
	        
//	        mNotification = new Notification(icon, aTickerText, when);
//	        mNotification.setLatestEventInfo(context, aTickerText, aContentText, contentIntent);
	        
	        mNotification = new Notification.Builder(context)
	         .setContentTitle(aTickerText)
	         .setContentText(aContentText)
	         .setSmallIcon(icon)
	         .build(); // available from API level 11 and onwards
	        
	        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	        mNotificationManager.notify(HELLO_ID, mNotification);
	        ///
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//System.out.println("Error - " + ex.toString());
		}

	}
}