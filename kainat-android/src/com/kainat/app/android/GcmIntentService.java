/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kainat.app.android;

import java.util.Vector;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.util.Urls;
import com.rockerhieu.emojicon.EmojiconTextView;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */


public class GcmIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	Activity currentActivity;
    String senderUserName;
    boolean showPushMessage;
	String TabType = "0";
	private static final String TAG = GcmIntentService.class.getSimpleName();

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {


		Bundle extras = intent.getExtras();
		if(extras.containsKey("message"))
			Log.i(TAG, "onHandleIntent ::   ********** INCOMEING PUSH ********** "+extras.getString("message"));
		else{
			//Blank Push Message Receive, So reject that./
			Log.i(TAG, "onHandleIntent ::   << Blank Push Received, So Ignoring!! >>");
			return;
		}
		
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM will be
			 * extended in the future with new message types, just ignore any message types you're
			 * not interested in, or that you don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString(),extras);
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString(),extras);
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
			{
//				// Post notification of received message.
				showPushMessage = true;
            	//Check here for the received notification from the same user and chat is open then do not show notification
				if(BusinessProxy.sSelf.mUIActivityManager != null)
					currentActivity = BusinessProxy.sSelf.mUIActivityManager.getCurrentActivity();
//            	if(senderUserName!=null && senderUserName.indexOf(":") != -1)
//            		senderUserName = senderUserName.substring(0, senderUserName.indexOf(":")).trim();
				if(currentActivity != null)
				{
					//Commented for Push message, if app is in foreground.
//					if(BusinessProxy.sSelf.mUIActivityManager.isRunningInForeground() && !extras.containsKey("gn"))
//					{
//						showPushMessage = false;
//					}
//					else
					{
						if(currentActivity instanceof ConversationsActivity)
		            	{
		            		senderUserName = extras.getString("su");
		            		if(BusinessProxy.sSelf.mUIActivityManager.isRunningInForeground() 
		            				&& ((ConversationsActivity) currentActivity).getTitleName() != null 
		            				&& ((ConversationsActivity) currentActivity).getTitleName().equalsIgnoreCase(senderUserName)
		            				&& extras.getString("gn") == null)
		            			showPushMessage = false;
		            		else
		            			showPushMessage = true;
		            		 Log.i(TAG, "Received: in ConversationsActivity, from : " + senderUserName);
		            	}
		            	else if(currentActivity instanceof CommunityChatActivity)
		            	{
		            		senderUserName = extras.getString("gn");
		            		if(BusinessProxy.sSelf.mUIActivityManager.isRunningInForeground() && ((CommunityChatActivity) currentActivity).getTitleName() != null && ((CommunityChatActivity) currentActivity).getTitleName().equalsIgnoreCase(senderUserName))
		            			showPushMessage = false;
		            		else
		            			showPushMessage = true;
		            		Log.i(TAG, "Received: in ConversationsActivity, from : " + senderUserName);
		            	}
		            	else if(currentActivity instanceof KainatInboxAvtivity || currentActivity instanceof KainatCommunityActivity)
		            	{
		            		senderUserName = extras.getString("su");
		            		if(BusinessProxy.sSelf.mUIActivityManager.isRunningInForeground() 
		            				&& extras.getString("gn") == null)
		            			showPushMessage = false;
		            		else
		            			showPushMessage = true;
		            	}
					}
				}
            	if(showPushMessage)
            		sendNotification(extras.getString("message"),extras);
//            	else
//            		Log.i(TAG, "Received: Same chat window, so NO PUSH!!");
            	
            	senderUserName = null;
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	public static Vector<Integer> notificationID = new Vector<Integer>() ;
	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.

	/* public void createNotification(Context context, String payload) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.ic_launcher,"Message received", System.currentTimeMillis());
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //adding LED lights to notification
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        Intent intent = new Intent("android.intent.action.VIEW", 
         Uri.parse("http://my.example.com/"));
PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);
        notification.setLatestEventInfo(context, "Message",
                "New message received", pendingIntent);
        notificationManager.notify(0, notification);

    }*/


	private void sendNotification(String msg,Bundle extras) {
		//Bundle[{groupMessageUrl=http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/message/126012, from=1001502560648, 
		//title=title, message=mahesh sonker <Crick>: Ghjjj, android.support.content.wakelockid=2, collapse_key=do_not_collapse}]
		//Bundle[{conversationId=632, from=1001502560648, title=title, message=mahesh ios: Ufffc, android.support.content.wakelockid=2, collapse_key=do_not_collapse}]
		try{
			//    	AppUtil.showTost(this, "NEW PUSH");
			String ar []  = new String[2];
			if(msg!=null && msg.indexOf(":") != -1)
			{
				ar[0] = msg.substring(0,msg.indexOf(":")) ;
				ar[1] = msg.substring(msg.indexOf(":")+1,msg.length()) ;
			}
			else
			{
				ar[0] = getString(R.string.app_name) ;
				ar[1] = msg;
			}

			//    	showNotificationForMessage(ar[0], ar[1]);
			mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
			int tab = 1;// Shows landing in Inbox or Community
			Intent callIntent ;
			callIntent= new Intent(this, KainatHomeActivity.class);

			if(extras.containsKey("t"))
			{
				TabType = extras.getString("t");
				callIntent.putExtra("TAB_TYPE", extras.getString("t"));
			}
			if(extras.containsKey("su"))
			{
				callIntent.putExtra("SENDER_USERNAME",extras.getString("su"));
			}
			if(extras.containsKey("sn"))
			{
				callIntent.putExtra("SENDER_NAME",extras.getString("sn"));

			}
			if(TabType.equals("1"))
			{
				if(extras.containsKey("cid"))
				{
					callIntent.putExtra("CONVERSATION_ID",extras.getString("cid"));
				}
			}
			else if(TabType.equals("2"))
			{
				if(extras.containsKey("gid"))
				{
					callIntent.putExtra("GROUP_ID",extras.getString("gid"));
				}
				if(extras.containsKey("gn"))
				{
					callIntent.putExtra("GROUP_NAME",extras.getString("gn"));
				}
			}
			//  callIntent.putExtra("Tab", "1");
			// callIntent.putExtra("USERID", "1");
			// callIntent.putExtra("CONVERSATIONID",extras.getString("conversationId"));
			// callIntent.putExtra("lastMsgId", "1333");
			//  callIntent.putExtra("transactionId", "14444");
			callIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,callIntent,PendingIntent.FLAG_UPDATE_CURRENT);

			// PendingIntent.FLAG_UPDATE_CURRENT
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.noti_ticker_icon)
			.setContentTitle(ar[0])//getString(R.string.app_name))
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(ar[1]))
			.setAutoCancel(true)
			.setTicker(msg)
			.setContentText(ar[1]);

			Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);

			mBuilder.setContentIntent(contentIntent);
			//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			mNotificationManager.notify(ar[0].hashCode(), mBuilder.build());
			notificationID.add(ar[0].hashCode()) ;
		}catch (Exception e) {
			e.printStackTrace();
			AppUtil.showTost(this, ""+msg) ;
		}
		//extras.clear();
	}

	private NotificationManager notificationManager;
	private Builder messageNotification;
	EmojiconTextView messageView;
	public void showNotificationForMessage(String senderName, String msg) {
		CharSequence tickerText = msg;
		if (notificationManager == null)
			notificationManager = (NotificationManager) YelatalkApplication.applicationcContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if (messageNotification == null) {
			messageNotification = new NotificationCompat.Builder(YelatalkApplication.applicationcContext);
			messageNotification.setSmallIcon(R.drawable.noti_ticker_icon);
			messageNotification.setAutoCancel(true);
			messageNotification.setLights(Color.RED, 3000, 3000);
			Notification note = messageNotification.build();
			note.defaults |= Notification.DEFAULT_SOUND;
			note.defaults |= Notification.DEFAULT_LIGHTS;
			Uri alarmSound = RingtoneManager .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			messageNotification.setSound(alarmSound);
		}
		tickerText = "Message from " + senderName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		Intent notificationIntent = new Intent(YelatalkApplication.applicationcContext, KainatHomeActivity.class);
		notificationIntent.putExtra("GCM_DATA", msg);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//		notificationIntent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, grpDisplayName);
		//		notificationIntent.putExtra(DatabaseConstants.USER_NAME_FIELD, user);
		//		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TASK);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);// FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(YelatalkApplication.applicationcContext, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		messageNotification.setContentTitle(senderName);
		messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
		Notification notification = messageNotification.build();
		RemoteViews contentView = new RemoteViews(YelatalkApplication.applicationcContext.getPackageName(), R.layout.notification_layout);
		contentView.setTextViewText(R.id.chat_person_name, senderName);
		contentView.setTextViewText(R.id.chat_message, msg);
		notification.contentView = contentView;
		int id = (senderName).hashCode();
		notificationManager.notify(id, notification);
	}
}
