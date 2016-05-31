package com.kainat.app.android.engine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask.Status;
import android.os.IBinder;
import android.util.Log;

import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.Utilities;

public class RefreshService extends Service implements HttpSynchInf {

	private static boolean log = false; 
	public static boolean PARSE_REFRESH_CONVERSIOTION_LIST = false; 
	public static boolean PARSE_REFRESH_REFRESH_FIRST = false; 
	public static boolean CONVERSIOTION_LIST_OLD = false; 
	public static int REFRESH_TIME = 2000;
	public static int BACKGROUND_REFRESH_TIME = 4 * REFRESH_TIME;
	private static final String TAG = RefreshService.class.getSimpleName();
	
	@Override
	public void onStart(Intent intent, int startId) 
	{
		try{
				if(log)
				{
					Log.i(TAG, "onStart :: IS app In BG - "+UIActivityManager.sIsRunningInBackGround);
					Log.i(TAG, "onStart :: PARSE_REFRESH_CONVERSIOTION_LIST - "+PARSE_REFRESH_CONVERSIOTION_LIST);
				}
				super.onStart(intent, startId);
				if(Utilities.forgroundApp(this))
				{
					if (!Utilities.getBoolean(this, Constants.REFRESH_CONVERSIOTION_LIST)) 
					{
						if(log)
							Log.i(TAG, "onStart :: IF CASE - start refresh conversition list");
						try{
						Cursor cursor = getContentResolver().query(
								MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								null, MessageConversationTable.USER_ID+" =?", new String[] { BusinessProxy.sSelf.getUserId()+"" }, null);
						Utilities.setBoolean(this, Constants.REFRESH_CONVERSIOTION_LIST, true);
						if(cursor.getCount() > 0)
							CONVERSIOTION_LIST_OLD = true;
						cursor.close();
						}catch (Exception e) {
							// TODO: handle exception
						}
						FeedRequester.requestInboxConversiotionListFeed(this);
						FeedRequester.feedTaskConversationList.setHttpSynchRefresh(this);
					} 
					else 
					{
							if(log)
								Log.i(TAG, "onStart :: ELSE CASE - start refresh conversition list");
							if (PARSE_REFRESH_CONVERSIOTION_LIST)
							{
							 if(FeedRequester.feedTaskConversationList != null 
									 && FeedRequester.feedTaskConversationList.getStatus() != Status.FINISHED)
							 {
								 if(log)
									 Log.i(TAG, "onStart :: returing from refreshservice because getconversiotion is running");
							 }
							 else
							 {
								 boolean notRunnuming = false;
									do{
										if((FeedRequester.feedTaskConversationMessages!=null&&
												FeedRequester.feedTaskConversationMessages.getStatus()==Status.RUNNING)
												|| (FeedRequester.feedTaskConversationMessagesRefresh!=null
												&&FeedRequester.feedTaskConversationMessagesRefresh.getStatus()==Status.RUNNING))
										{
											onError("") ;
											if(log)
												Log.i(TAG, "onStart :: Singlton---1-------------ConversationMessages running");
										}
										else
										{
											notRunnuming=false;
											if(log)
												Log.i(TAG, "onStart :: Singlton---1-------------ConversationMessages not running");
										}
									}while(notRunnuming);
								FeedRequester.requestInboxMessage(this);
								FeedRequester.feedTaskMessageList.setHttpSynchRefresh(this);
								//Request Channel refresh
								if(UIActivityManager.startChannelRefresh){
									FeedRequester.requestChannelRefresh(this);
								}
								else{
									if(log)
										Log.i(TAG, "onStart :: 	CHANNEL REFRESH STOPPED ::");
								}
//								FeedRequester.feedTaskChannelRefresh.setHttpSynchRefreshForChannel(this);
								if(log)
									Log.i(TAG, "onStart :: start refresh message list");
							 }
							}
						}
				}
				else
				{
					onError("") ;
				}
		}catch (Exception e) {
			onError("") ;
		}
		
	}

	Runnable checkRefresh = new Runnable() {
		
		@Override
		public void run() {
			Utilities.checkAndStartRefresh(RefreshService.this);
		}
	};
	
	public static void setRefreshTime(int time){
		REFRESH_TIME = time;
		BACKGROUND_REFRESH_TIME = 4 * REFRESH_TIME;
		if(log)
			Log.i(TAG, "setRefreshTime :: Channel Refresh time set to : "+REFRESH_TIME);
	}

	public static long idealTime = 0;
	public void stopService() {
		if(log)
			Log.i(TAG, "stopService :: Foreground - "+Utilities.forgroundApp(this));
//		if(!BusinessProxy.sSelf.isRegistered() || !Utilities.forgroundApp(this))
//		{
//			Intent service = new Intent(this, RefreshService.class);
//			stopService(service);
//			return;
//		}
		startTimer = false ;
		if(timer!=null && timer.isAlive())
			timer.stop() ;
		
		idealTime = System.currentTimeMillis();
		if(log)
			Log.i(TAG, "stopService ::  Stop refresh service");
		Intent service = new Intent(this, RefreshService.class);
		stopService(service);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(log)
						Log.i(TAG, "stopService :: Waiting for : "+REFRESH_TIME / 1000+" secs..");
					Thread.sleep(REFRESH_TIME) ;
					if(!Utilities.forgroundApp(RefreshService.this))
					{
						if(log)
							Log.i(TAG, "stopService :: extra wating for : "+BACKGROUND_REFRESH_TIME /1000 +" secs.");
						Thread.sleep(BACKGROUND_REFRESH_TIME) ;
					}
					 Intent service = new Intent(RefreshService.this,RefreshService.class);
			    	 if(!isRefreshServiceRunning(RefreshService.this))
			    	 {
			    		 RefreshService.this.startService(service);
			    	 }
			    	 else{
			    		 if(log)
			    			 Log.i(TAG, "stopService :: In alaram revicer service already running");
			    	 }
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		startTimer = true ;
//		timer = new Thread(runnable);
	}
	
	Thread timer ;//new Thread();
	boolean startTimer = false ;
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			while(startTimer){
					try {
						Log.i(TAG, "IN thread :: Entry sleeping for 10 secs with idealTime - "+idealTime);
						Thread.sleep(10000) ;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					idealTime = idealTime + 10000;
					Log.i(TAG, "IN thread :: idealTime - "+idealTime);
					if(idealTime > 60000)
					{
						startTimer = false;
						Log.i(TAG, "IN thread :: idealTime > 60 secs, starttimer = false,  idealTime- "+idealTime);
					}
			}
			if(log)
				Log.i(TAG, "IN thread :: Exit-  stopping refresh timer");
		}
	};
	 public boolean isRefreshServiceRunning(Context context) {
		 return  Utilities.isRefreshServiceRunning(context) ;
	  	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(log)
			Log.i(TAG, "onDestroy :: Destroy refresh service");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(log)
			Log.i(TAG, "onStartCommand :: start refresh service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResponseSucess(Object respons, int requestForID) {
		stopService();
	}

	@Override
	public void onResponseSucess(String response, int requestForID) {
		if(log)
			Log.i(TAG, "onResponseSucess 2 ::, requestForID - "+requestForID+", and response - "+response);
		stopService();
	}

	@Override
	public void onResponseSucess(String response, int requestForID, int subType,
			int totNewFeed) {
		if(log)
			Log.i(TAG, "onResponseSucess 3 :: requestForID - "+requestForID+", and response - "+response);
		stopService();
	}

	@Override
	public void onError(String err) {
		if(log)
			Log.i(TAG, "onError :: err - "+err);
		stopService();
	}

	@Override
	public void onError(String err, int requestCode) {
		if(log)
			Log.i(TAG, "onError ::  with requestCode - "+requestCode+", Error - "+err);
		stopService();
	}

	@Override
	public void onNotification(int requestCode, String sender, String msg) {
		if(log)
			Log.i(TAG, "onNotification :: ");
		stopService();
	}
	@Override
	public void onNotificationThreadInbox(int requestCode, String sender, String msg) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onNotificationThreadInbox :: ");
	}

}