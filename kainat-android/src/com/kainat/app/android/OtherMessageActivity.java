package com.kainat.app.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.ConversiotionAdaptorInf;
import com.kainat.app.android.adaptor.InviteAdapter;
import com.kainat.app.android.adaptor.MessageViewHolder;
import com.kainat.app.android.adaptor.SystemMessageAdapter;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.MediaPost;
import com.kainat.app.android.bean.MediaPost.MediaContent;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.bean.ParticipantInfo;
import com.kainat.app.android.bean.VideoUrl;
import com.kainat.app.android.constanst.PictureTypes;
import com.kainat.app.android.constanst.VideoTypes;
import com.kainat.app.android.constanst.VoiceTypes;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.ComposeService;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.MediaPostTable;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.model.InviteModel;
import com.kainat.app.android.rtcamera.DgCamActivity;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.uicontrol.StreemMultiPhotoViewer;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.InboxTblObject;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VDownloader;
import com.kainat.app.android.util.VoiceMedia;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;

public class OtherMessageActivity extends UIActivityManager implements
HttpSynchInf, ThumbnailReponseHandler, OnClickListener,
OnScrollListener, ConversiotionAdaptorInf {
	private static final String TAG = OtherMessageActivity.class.getSimpleName();
	public static final int ADDCONVERSATIONS = 1000;
	private static final int CONTENT_ID_VOICE = 7000;
	private static final int CONTENT_ID_PICTURE = 8000;
	private static final int CONTENT_ID_VIDEO = 9000;
	private static final byte POSITION_PICTURE = 0;
	private static final byte POSITION_CAMERA_PICTURE = 1;
	private static final byte POSITION_MULTI_SELECT_PICTURE = 4;
	private static final byte POSITION_VOICE = 2;
	private static final byte POSITION_VIDEO = 3;
	private static final byte POSITION_PICTURE_RT_CANVAS = 4;
	private String cameraImagePath = null;
	private String mVoicePath, mVideoPath;
	private Vector<String> mPicturePath = new Vector<String>();
	private Vector<Integer> mPicturePathId = new Vector<Integer>();
//	MyHorizontalScrollView scrollView;
	View menuLeft, menuRight;
	boolean frontCam, rearCam;
	private VoiceMedia mVoiceMedia;
	int idsReport[] = new int[] { 1 };
	String idsNameReport[] = new String[] { "You can attach video of up to 10MB, about 20-25 seconds. For longer videos, please use a lower resolution setting." };

	// View app;
	ImageView btnSlideLeft, btnSlideRight;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 200;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	boolean menuOut = false;
	public static ScrollView leftMenu;
	ListView listViewActivity,invite_activity_list;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	SystemMessageAdapter activityAdapter;
	int idsOptions[] = new int[] { 1, 2, 3, 4 };
	String idsNameOptions[] = new String[] { "Users", "Community", "Media",
	"Cancle" };

	private static final byte ID_ADD_TO_CONVERSATION = 101;
	private static final byte ID_LEAVE_CONVERSATION = 102;
	private static final byte ID_UPDATE_SUBJECT = 103;
	private static final byte DELETE_CONVERSATION = 104;
	private static final int ID_CANCEL = 1004;
	// ,"Delete All"

	int idsMenuOptionsIcon[] = new int[] { R.drawable.add, R.drawable.add,
			R.drawable.add, R.drawable.deletemessage, R.drawable.close };
	int idsMenuOptions[] = new int[] { ID_ADD_TO_CONVERSATION,
			ID_LEAVE_CONVERSATION, ID_UPDATE_SUBJECT, DELETE_CONVERSATION,
			ID_CANCEL };
	String idsMenuNameOptions[] = new String[] { "Add to Conversation",
			"Leave Conversation", "Update Subject", "Delete Conversation",
	"Cancle" };

	private static final int ID_DELETE_MESSAGE = 201;
	private static final int ID_SAVE_MESSAGE = 202;
	// private static final byte ID_UPDATE_SUBJECT = 103;

	int idsRowMenuOptionsIcon[] = new int[] { R.drawable.deletemessage,
			R.drawable.savemessage, R.drawable.close };
	int idsRowMenuOptions[] = new int[] { ID_DELETE_MESSAGE, ID_SAVE_MESSAGE,
			ID_CANCEL };
	String idsRowMenuNameOptions[] = new String[] { "Delete Message",
			"Save Message", "Cancle" };

	DisplayMetrics metrics;
	FeedTask feedTaskMore = null;
	private static long totalSize = 0;
	boolean startRecoridng = false;
	String conversationUser = "";
	//	LinearLayout media_play_layout;
	Dialog dialog;

	SeekBar baradd;
	TextView total_autio_time, played_autio_time;
	public boolean isAudio = false;
	TextView load_prevoius_message;
	private static Handler handler;
	LinearLayout layout_notification_btn,layout_invite_btn,invite_bar,notification_bar;
	ProgressDialog rTDialog;
	private final int PARENT_ACTIVITY_CALLBACK = 21;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		idsMenuOptions = new int[] { ID_ADD_TO_CONVERSATION,
				ID_LEAVE_CONVERSATION, ID_UPDATE_SUBJECT, DELETE_CONVERSATION,
				ID_CANCEL };
		idsMenuNameOptions = new String[] {getString(R.string.add_conversation_subject),
				getString(R.string.leave_community), getString(R.string.update_subject), getString(R.string.delete_conversation),
				getString(R.string.cancel) };
		idsRowMenuOptions = new int[] { ID_DELETE_MESSAGE, ID_SAVE_MESSAGE,
				ID_CANCEL };
		idsRowMenuNameOptions = new String[] {getString(R.string.delete_message),
				getString(R.string.save_message), getString(R.string.cancel)};

//		if (BusinessProxy.sSelf == null || !BusinessProxy.sSelf.isRegistered()) {
//			// Logger.conversationLog("AnimFeed : ", "====Oncreate Call=====");
//			finish();
//			startActivity(new Intent(OtherMessageActivity.this, SplashActivity.class));
//			return;
//		}
		handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				setUnreadCount();
			}
		};

		screenSlide(this, R.layout.other_message_screen);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		((EditText) findViewById(R.id.search_text)).setVisibility(View.GONE);
		
		//Commented as its of no use
		//		media_play_layout 		= (LinearLayout) findViewById(R.id.media_play_layout);
		listViewActivity 		= (ListView) findViewById(R.conversations.landing_discovery_activity_list);
		invite_activity_list	= (ListView)findViewById(R.id.invite_activity_list);
		invite_activity_list.setVisibility(View.GONE);
		layout_notification_btn = (LinearLayout)findViewById(R.id.layout_notification_btn);
		layout_invite_btn		= (LinearLayout)findViewById(R.id.layout_invite_btn);
		invite_bar				= (LinearLayout)findViewById(R.id.invite_bar);
		notification_bar		= (LinearLayout)findViewById(R.id.notification_bar);
		invite_bar.setVisibility(View.INVISIBLE);
		notification_bar.setVisibility(View.VISIBLE);
		ArrayList<InviteModel> imageList =  new ArrayList<InviteModel>();
		InviteAdapter IA =  new InviteAdapter(this, imageList);
		invite_activity_list.setAdapter(IA);
		layout_invite_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				invite_activity_list.setVisibility(View.VISIBLE);
				listViewActivity.setVisibility(View.GONE);
				invite_bar.setVisibility(View.VISIBLE);
				notification_bar.setVisibility(View.INVISIBLE);
			}
		});

		layout_notification_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				invite_activity_list.setVisibility(View.GONE);
				listViewActivity.setVisibility(View.VISIBLE);
				invite_bar.setVisibility(View.INVISIBLE);
				notification_bar.setVisibility(View.VISIBLE);

			}
		});
		//
		// activityAdapter = new SystemMessageAdapter(this, getCursor(), true,
		// this);
		//
		// listViewActivity.setAdapter(activityAdapter);
		// listViewActivity.setOnItemClickListener(onitemClickList);
		// listViewActivity.setOnScrollListener(this);

		//		TextView imgStop = (TextView) findViewById(R.id.media_stop_play);
		//		imgStop.setOnClickListener(playerClickEvent);
		mVoiceMedia = new VoiceMedia(this, this);

		load_prevoius_message = ((TextView) findViewById(R.conversation.load_prevoius_message));

		if (FeedRequester.feedTaskConversationMessages != null
				&& FeedRequester.feedTaskConversationMessages.getStatus() != Status.FINISHED) {
			load_prevoius_message.setText(getResources().getString(
					R.string.refreshing_list));
		}

		//		findViewById(R.chat_panel.more_option).setVisibility(View.GONE);
		findViewById(R.conversation.user_info).setVisibility(View.GONE);
		findViewById(R.community_chat.msgBox).setVisibility(View.GONE);
		findViewById(R.conversation.option).setVisibility(View.GONE);
		load_prevoius_message.setVisibility(View.VISIBLE);

		FeedTask.setHttpSynchRefreshCurrentView(this);
		ComposeService.setHttpSynchRefreshCurrentView(this);

		listViewActivity.setOnScrollListener(this);
		listViewActivity.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				//-------------------------
				// MANOJ SINGH
				// 22-12-2015
				//d
				//END
				//--------------------------
				return false;
			}
		});
		ImageView backImageView = (ImageView) findViewById(R.id.back_iv);
		backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();	
			}
		});
		menuNew = (ImageButton)findViewById(R.id.menu) ;		
		menuNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList) ;	
			}
		});
		initLeftMenu();
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Other Message Activity");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onResume() {
		stopChannelRefresh();
		if (FeedRequester.feedTaskFeatureUserFeed != null)
			FeedRequester.feedTaskFeatureUserFeed.setHttpSynch(this);
//		myScreenName(Constants.SCRTEEN_NAME_SYSTEM);
		if (getIntent().getIntExtra("What", 0) == 1) {
			((TextView) findViewById(R.conversation.title)).setText(getString(R.string.notification));
		}
		if (getIntent().getIntExtra("What", 0) == 2) {
			((TextView) findViewById(R.conversation.title)).setText("Friend Request");
		}
		super.onResume();
		CursorLoad task = new CursorLoad();
		task.execute(new String[] { "load" });
	}
	
	private void stopChannelRefresh(){
		//Stop Channel Refresh
		//Set refresh time to 10 secs.
		UIActivityManager.startChannelRefresh = false;
		RefreshService.setRefreshTime(5000);
	}
	private void startChannelRefresh(){
		if(!UIActivityManager.startChannelRefresh){
			UIActivityManager.startChannelRefresh = true;
			RefreshService.setRefreshTime(5000);
		}
	}

	boolean dontCloseCursol;
	//	OnItemClickListener onitemClickList = new OnItemClickListener() {
	//		public void onItemClick(android.widget.AdapterView<?> arg0, View view,
	//				int pos, long arg3) {
	//			 System.out.println("---------on item clicked");
	//
	//			MessageViewHolder messageViewHolder = (MessageViewHolder) view
	//					.getTag();
	//			Utilities.setInt(OtherMessageActivity.this, "listpos", pos);
	//			int top = (view == null) ? 0 : view.getTop();
	//			Utilities.setInt(OtherMessageActivity.this, "top", top);
	//
	//			finish();
	//
	//			message = messageViewHolder.message;
	//			if (message.contentBitMap != null
	//					&& message.contentBitMap.indexOf("RTML") != -1)// txt.indexOf("<?xml version=")
	//																	// != -1)
	//			{
	////				showToast("RTML Message clicked 2");
	//				InboxMessage mMessage = new InboxMessage();
	//				mMessage.mPayload = new Payload();
	//				mMessage.mPayload.mRTML = new byte[1][1];
	//				message.msgTxt = getRtmlText(message.messageId);
	//				mMessage.mPayload.mRTML[0] = message.msgTxt.getBytes();
	//
	//				// Get All Thumb URL's
	//				String thumbURLs[] = null;
	//
	//				if (message.image_content_thumb_urls != null) {
	//					thumbURLs = Utilities.split(new StringBuffer(
	//							message.image_content_thumb_urls),
	//							Constants.COL_SEPRETOR);
	//				}
	//				// Get All Images URL's
	//				String imageURLs[] = null;
	//				if (message.image_content_urls != null)
	//					imageURLs = Utilities
	//							.split(new StringBuffer(message.image_content_urls),
	//									Constants.COL_SEPRETOR);
	//
	//				mMessage.mSenderName = message.source;// "Qts" ;
	//				mMessage.mPayload.mPicture = new byte[thumbURLs.length][];
	//				if (imageURLs != null)
	//					for (int i = 0; i < imageURLs.length; i++)
	//						mMessage.mPayload.mPicture[i] = thumbURLs[i].getBytes();
	//				if (imageURLs != null && imageURLs.length > 0)
	//					mMessage.mPayload.mSlideShowURLs = new String[imageURLs.length];
	//				if (imageURLs != null)
	//					for (int i = 0; i < imageURLs.length; i++)
	//						mMessage.mPayload.mSlideShowURLs[i] = imageURLs[i];
	//				DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
	//						(InboxMessage) mMessage);
	//				Intent intent = new Intent(OtherMessageActivity.this,
	//						CustomViewDemo.class);
	//				intent.putExtra("PAGE", (byte) 1);
	//				// intent.putExtra("force_message", forceMessage);
	//				startActivityForResult(intent, 0);
	//			} else if (0 < (message.notificationFlags & InboxTblObject.NOTI_FRIEND_INVITATION_MSG)) {
	//				String pendingUrl = "http://" + Urls.WAP_HOST
	//						+ "/mypage/pendinginvite/"
	//						+ BusinessProxy.sSelf.getUserId();
	//				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
	//						"Pending Invite");
	//				DataModel.sSelf.storeObject(DMKeys.XHTML_URL, pendingUrl);
	//				Intent intentPending = new Intent(OtherMessageActivity.this,
	//						WebviewActivity.class);
	//				intentPending.putExtra("PAGE", (byte) 1);
	//				startActivity(intentPending);
	//			}
	//
	//			// startActivity(intent1);//, Constants.ACTIVITY_FOR_RESULT_INT);
	//		};
	//	};

	@Override
	public void onBackPressed() {
		if(deleteMode){
			deleteMode = false;
			listViewActivity.invalidateViews() ;
			((Button)app.findViewById(R.conversation.delete)).setText(getString(R.string.edit));
			return;
		}
		setUnreadCount();
		if (getIntent().getBooleanExtra(Constants.ACTIVITY_FOR_RESULT, false))
			finish();
		else {
			finish();
			Intent intent1 = new Intent(OtherMessageActivity.this, KainatHomeActivity.class);
			startActivity(intent1);
		}
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		startChannelRefresh();
	}

	private class CursorLoad extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try{
				activityAdapter = new SystemMessageAdapter(
						OtherMessageActivity.this, getCursor(), true,
						OtherMessageActivity.this);
				//
				listViewActivity.setAdapter(activityAdapter);
				handler.sendEmptyMessage(0);

				if (activityAdapter != null && activityAdapter.getCursor() != null
						&& activityAdapter.getCursor().getCount() > 0) {
					load_prevoius_message.setVisibility(View.GONE);
				} else {
					load_prevoius_message.setVisibility(View.VISIBLE);
					load_prevoius_message.setText(getString(R.string.your_notification_box_is_empty));
				}
			}catch (Exception e) {
				e.printStackTrace() ;
			}
		}
	}

	@Override
	protected void onPause() {
		FeedTask.setHttpSynchRefreshCurrentView(this);
		ComposeService.setHttpSynchRefreshCurrentView(this);
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == ADDCONVERSATIONS) {
			String destinations = DataModel.sSelf
					.removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS_IDS);
			String destinationsUserName = DataModel.sSelf
					.removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS);
			if (destinations != null && destinations.trim().length() > 0) {

				String dist[] = Utilities.split(new StringBuffer(destinations),";");
				String actorUserid = "";
				for (int i = 0; i < dist.length; i++) {
					actorUserid = actorUserid + dist[i] + ";";
				}
				actorUserid = actorUserid
						.substring(0, actorUserid.length() - 1);

				String destinationsUserNameArr[] = Utilities.split(
						new StringBuffer(destinationsUserName), ";");
				if (destinationsUserNameArr.length > 1) {
					showToast("You can not add more than one friends at a time. Slect only one friends !");
					return;
				}
				if (getParticipantInfo()
						.indexOf(
								BusinessProxy.sSelf
								.parseUsernameInformation(destinationsUserNameArr[0])) != -1) {
					showToast("This friend already in group chat!");
					return;
				}
				request = new Request(ID_ADD_TO_CONVERSATION, "url");
				request.actorUserid = actorUserid;
				if (!OtherMessageActivity.this.checkInternetConnection()) {
					OtherMessageActivity.this.networkLossAlert();
					return;
				}
				request.execute("LikeDislike");

			}
		}
		else if(requestCode == PARENT_ACTIVITY_CALLBACK){
			if (resultCode == Activity.RESULT_OK) {
	            deleteMessage(intent.getStringExtra("DELETE_MESSAGE_ID"), true);
	        }
		}
	}

	// private LandingPageDatabaseHelper database;
	// private SQLiteDatabase sqlDB ;
	public Cursor getCursor() {
		try{
			int type = 0;
			if (getIntent()!=null && getIntent().getIntExtra("What", 0) == 1) {// system
				type = Message.MESSAGE_TYPE_SYSTEM_MESSAGE;
			}
			if (getIntent()!=null && getIntent().getIntExtra("What", 0) == 2) {// friend req
				type = Message.MESSAGE_TYPE_FRIEND_REQUEST;
			}
			// Cursor cursor = getContentResolver().query(
			// MediaContentProvider.CONTENT_URI_INBOX, null,
			// MessageTable.MESSAGE_TYPE + " = ? and " +MessageTable.USER_ID+" =?",
			// new String[] { ""+type,""+BusinessProxy.sSelf.getUserId() },
			// MessageTable.INSERT_TIME+" DESC");

			// if(database==null)
			// database = new LandingPageDatabaseHelper(this);
			//
			// if(sqlDB==null)
			// sqlDB = database.getWritableDatabase();





			String[] columns = new String[] { MessageTable.AUDIO_DOWNLOAD_STATUE,MessageTable.PARTICIPANT_INFO,MessageTable.MORE,
					MessageTable.SORT_TIME,MessageTable.PARTICIPANT_INFO,MessageTable.PARTICIPANT_INFO,
					MessageTable.DIRECTION,MessageTable.USER_URI,MessageTable.USER_NAME,
					MessageTable.USER_LASTNAME,MessageTable.USER_FIRSTNAME,MessageTable.IMAGE_CONTENT_URLS,
					MessageTable.IMAGE_CONTENT_THUMB_URLS,MessageTable.VOICE_CONTENT_URLS,MessageTable.VOICE_CONTENT_THUMB_URLS,
					MessageTable.VIDEO_CONTENT_URLS,MessageTable.VIDEO_CONTENT_THUMB_URLS,MessageTable.TARGETUSER_ID,
					MessageTable.SOURCE,MessageTable.SUBJECT,MessageTable.GROUP_PIC,MessageTable.SENDERUSER_ID,
					MessageTable.REFMESSAGE_ID,MessageTable.NOTIFICATION_FLAGS,MessageTable.MSG_OP,
					MessageTable.TAG,MessageTable.SUBJECT,MessageTable.MFU_ID,
					MessageTable.MESSAGE_ID,MessageTable.HAS_BEEN_VIEWED_BY_SIP,MessageTable.HAS_BEEN_VIEWED,
					MessageTable.DRM_FLAGS,MessageTable.DATE_TIME,MessageTable.CONVERSATION_ID,
					MessageTable.AUDIO_DOWNLOAD_STATUE,MessageTable.CONTENT_BITMAP,MessageTable.CONTENT_BITMAP,
					MessageTable.CLIENTTRANSACTION_ID,MessageTable.PARENT_ID,MessageTable.USER_ID,
					MessageTable.COLUMN_ID,MessageTable.CONTENT_BITMAP,MessageTable.CONTENT_BITMAP,
					MessageTable.ITEM_SELECTED,MessageTable.CONTENT_BITMAP,MessageTable.CONTENT_BITMAP,
					MessageTable.INSERT_TIME,MessageTable.IS_LEFT,MessageTable.VIDEO_SIZE,
					MessageTable.IMAGE_SIZE,MessageTable.AUDIO_SIZE,MessageTable.MESSAGE_TYPE,
					MessageTable.ANI_TYPE,MessageTable.ANI_VALUE,MessageTable.DIRECTION,
					MessageTable.SENDING_STATE,MessageTable.IS_NEXT,MessageTable.MSG_TXT,

			};
			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(MessageTable.TABLE,
					columns, MessageTable.USER_ID + " = ? and "
							+ MessageTable.MESSAGE_TYPE + " =?", new String[] {
					BusinessProxy.sSelf.getUserId() + "", "" + type },
					null, null, MessageTable.INSERT_TIME + " DESC");

			return cursor;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public int getCursorCount() {
		try{
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_BOOKMARK, null,
					MessageTable.USER_ID + " =?",
					new String[] { "" + BusinessProxy.sSelf.getUserId() }, null);
			int count = cursor.getCount();
			cursor.close();
			return count;
		}catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

	public String getParticipantInfo() {
		try{
			String usersForthisConversition = "";
			Message message = new Message();
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_BOOKMARK, null, null, null,
					null);
			cursor.moveToLast();

			String s = cursor.getString(cursor
					.getColumnIndex(MessageTable.PARTICIPANT_INFO));
			if (s != null) {
				String pi[] = Utilities.split(new StringBuffer(s),
						Constants.ROW_SEPRETOR);
				ParticipantInfo participantInfo = new ParticipantInfo();
				message.participantInfo = new Vector<ParticipantInfo>();
				try {
					for (int i = 0; i < pi.length; i++) {
						participantInfo = new ParticipantInfo();
						String info[] = Utilities.split(new StringBuffer(pi[i]),
								Constants.COL_SEPRETOR);
						// participantInfo.isSender=Boolean.parseBoolean(info[0]);
						// participantInfo.userName=info[1];
						// message.participantInfo.add(participantInfo);
						usersForthisConversition = usersForthisConversition
								+ info[1] + ";";
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			cursor.close();
			return usersForthisConversition;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	private class RtLiveListAdaptor extends BaseAdapter {

		private Context context;
		private final LinearLayout.LayoutParams LAYOUT_PARAM = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		String menuItems[];

		public RtLiveListAdaptor(Context context, String[] menuItems) {
			this.context = context;
			this.menuItems = menuItems;
		}

		public String[] getItem() {
			return menuItems;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		public int getCount() {
			return menuItems.length;
		}

		public String getItem(int position) {
			if (position > -1 && position < menuItems.length)
				return menuItems[position];
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(parent.getContext());

			View retView = inflater.inflate(R.layout.left_menu_grid, parent, false);

			return retView;
		}
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		// refreshList();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				{

					// if(activityAdapter!=null)
					// stopManagingCursor(activityAdapter.getCursor());
					// if(activityAdapter!=null){
					// activityAdapter.getCursor().close() ;
					// }
					if (activityAdapter != null) {
						activityAdapter.getCursor().requery();
						activityAdapter.notifyDataSetChanged();
					}
					listViewActivity.invalidate();

					// activityAdapter = new
					// ConversationsAdapter(BookmarkActivity.this, getCursor(),
					// true,
					// BookmarkActivity.this);
					// // if(count>0)
					// listViewActivity.setAdapter(activityAdapter);
				}
			}
		});
	}

	public void refreshList() {
		if (activityAdapter != null)
			activityAdapter.notifyDataSetChanged();
	}

	Message message;
	boolean deleteMode = false;

	@Override
	public void onClick(final View v) {
		// if(listViewActivity!=null)
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
		try{


			Object obj = v.getTag();
			if (obj instanceof Message)
				message = (Message) obj;
			if (obj instanceof MessageViewHolder)
				message = ((MessageViewHolder) obj).message;

			// messageViewHolder

			super.onClick(v);
			switch (v.getId()) {
			case R.message_row.datacontainer:
			case R.message_row.message:
			case R.message_row.conversation_id:
				if(message != null)
					openChatFromPush(message.msgTxt);
				break;
			case R.message_row.activity_layout:
				// System.out.println("--------onlcick-----");
				// Utilities.setInt(OtherMessageActivity.this, "listpos", pos) ;
				// int top = (view == null) ? 0 : view.getTop();
				// Utilities.setInt(OtherMessageActivity.this, "top", top) ;

				// Intent intent1 = new Intent(OtherMessageActivity.this,
				// ConversationsActivity.class);
				// intent1.putExtra(Constants.CONVERSATION_ID,conversationsViewHolder.conversationList.conversationId);
				// if(conversationsViewHolder.conversationList.subject==null ||
				// conversationsViewHolder.conversationList.subject.trim().length()<=0)
				// intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.source);
				// else
				// intent1.putExtra(Constants.TITLE,conversationsViewHolder.conversationList.subject);

				// finish();

				// message = messageViewHolder.message ;
				//			if (message.contentBitMap != null
				//					&& message.contentBitMap.indexOf("RTML") != -1)// txt.indexOf("<?xml version=")
				//																	// != -1)
				//			{
				////				showToast("RTML Message clicked 3");
				//				// iPayloadData = aInboxdata.mPayload;
				//				InboxMessage mMessage = new InboxMessage();
				//				mMessage.mPayload = new Payload();
				//				mMessage.mPayload.mRTML = new byte[1][1];
				//				message.msgTxt = getRtmlText(message.messageId);
				//				mMessage.mPayload.mRTML[0] = message.msgTxt.getBytes();
				//
				//				// Get All Thumb URL's
				//				String thumbURLs[] = null;
				//
				//				if (message.image_content_thumb_urls != null) {
				//					thumbURLs = Utilities.split(new StringBuffer(
				//							message.image_content_thumb_urls),
				//							Constants.COL_SEPRETOR);
				//				}
				//				// Get All Images URL's
				//				String imageURLs[] = null;
				//				if (message.image_content_urls != null)
				//					imageURLs = Utilities
				//							.split(new StringBuffer(message.image_content_urls),
				//									Constants.COL_SEPRETOR);
				//
				//				mMessage.mSenderName = message.source;// "Qts" ;
				//				if (imageURLs != null)
				//					mMessage.mPayload.mPicture = new byte[imageURLs.length][];
				//				if (imageURLs != null)
				//					for (int i = 0; i < imageURLs.length; i++)
				//						mMessage.mPayload.mPicture[i] = imageURLs[i].getBytes();
				//				if (imageURLs != null && imageURLs.length > 0)
				//					mMessage.mPayload.mSlideShowURLs = new String[imageURLs.length];
				//				if (imageURLs != null)
				//					for (int i = 0; i < imageURLs.length; i++)
				//						mMessage.mPayload.mSlideShowURLs[i] = imageURLs[i];
				//
				//				// Add Voice url
				//				if (message.voice_content_urls != null) {
				//					String voiceURLs1[] = Utilities
				//							.split(new StringBuffer(message.voice_content_urls),
				//									Constants.COL_SEPRETOR);
				//					mMessage.mPayload.mVoice = new byte[voiceURLs1.length][];
				//					for (int i = 0; i < voiceURLs1.length; i++)
				//						mMessage.mPayload.mVoice[i] = voiceURLs1[i].getBytes();
				//				}
				//
				//				if(DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
				//					DataModel.sSelf.storeObject(DMKeys.FROM_REGISTRATION,false);
				//				DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
				//						(InboxMessage) mMessage);
				//				Intent intent = new Intent(OtherMessageActivity.this,
				//						CustomViewDemo.class);
				//				intent.putExtra("PAGE", (byte) 1);
				//				// intent.putExtra("force_message", forceMessage);
				//				startActivityForResult(intent, 0);
				//			} else if (0 < (message.notificationFlags & InboxTblObject.NOTI_FRIEND_INVITATION_MSG)) {
				//				String pendingUrl = "http://" + Urls.WAP_HOST
				//						+ "/mypage/pendinginvite/"
				//						+ BusinessProxy.sSelf.getUserId();
				//				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
				//						"Pending Invite");
				//				DataModel.sSelf.storeObject(DMKeys.XHTML_URL, pendingUrl);
				//				Intent intentPending = new Intent(OtherMessageActivity.this,
				//						WebviewActivity.class);
				//				intentPending.putExtra("PAGE", (byte) 1);
				//				startActivityForResult(intentPending, 0);
				//			}

				break;
			case R.conversation.delete:
				if (!deleteMode) {
					deleteMode = true;
					listViewActivity.invalidateViews();
					// listViewActivity.invalidate();
					// ((Button)v).setText(" Done ");
					//System.out.println("deleteMode===================done");
					//				((Button) app.findViewById(R.conversation.delete))
					//						.setBackgroundResource(R.drawable.doneinconversation);
					((Button)app.findViewById(R.conversation.delete)).setText(R.string.done);
				} else {
					deleteMode = false;
					//System.out.println("deleteMode===================edit");
					listViewActivity.invalidateViews();
					// listViewActivity.invalidate();
					// message_row/menu
					// ((TextView)findViewById(R.message_row.menu)).setVisibility(View.GONE);
					//				((Button) app.findViewById(R.conversation.delete))
					//						.setBackgroundResource(R.drawable.edit_chat);
					((Button)app.findViewById(R.conversation.delete)).setText(R.string.edit);
				}
				break;
			case R.message_row.me_image:
				//			showToast("other profiel");
				break;
			case R.message_row_right.other_image:
				//			showToast("my profile");
				break;
				// case R.message_row.video:
				// case R.message_row_right.video:
				// playFromURLMain(message.video_content_urls);
				// break;
			case 1000:
				ArrayList<String> imagesPathl = new ArrayList<String>();
				Feed.Entry entry = new Feed().new Entry();// feed.entry.get(Integer.parseInt(index));
				entry.media = new Vector<String>();

				String s[] = Utilities.split(new StringBuffer(
						message.image_content_thumb_urls), Constants.COL_SEPRETOR);
				for (int i = 0; i < s.length; i++) {
					imagesPathl.add(s[i]);
				}

				for (int j = 0; j < imagesPathl.size(); j++) {

					entry.media.add(imagesPathl.get(j));
					entry.media.add("image");
					entry.media.add(imagesPathl.get(j));
					entry.media.add("local");
					entry.media
					.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
					entry.media.add("image");
					entry.media.add(imagesPathl.get(j));
					entry.media.add("thumb");
				}
				DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
				// System.out.println("----CONTENT_ID_PICTU-: " + url);

				// System.out.println("---------index----" + index);
				Intent intentpics = new Intent(OtherMessageActivity.this,
						StreemMultiPhotoViewer.class);
				startActivityForResult(intentpics, Constants.ResultCode);

				break;
				//		case R.message_row_right.voice:
				//			media_play_layout.setVisibility(View.VISIBLE);
				//			baradd = (SeekBar) media_play_layout
				//					.findViewById(R.id.mediavoicePlayingDialog_progressbar);
				//			if (((ImageView) media_play_layout.findViewById(R.id.media_play)) != null)
				//				((ImageView) media_play_layout.findViewById(R.id.media_play))
				//						.setOnClickListener(playerClickEvent);
				//
				//			total_autio_time = ((TextView) media_play_layout
				//					.findViewById(R.id.audio_counter_max));
				//			played_autio_time = ((TextView) media_play_layout
				//					.findViewById(R.id.audio_counter_time));
				//			total_autio_time.setText("00:00)");
				//			played_autio_time.setText("(00:00 of ");
				//			openPlayScreen(
				//					Downloader.getInstance().getPlayUrl(
				//							message.voice_content_urls), false,
				//					media_play_layout);
				//			break;
			case R.message_row_right.receving_voice:

				Downloader downloader = Downloader.getInstance();
				downloader.download(message.voice_content_urls, 2, this);
				// activityAdapter.notifyDataSetChanged();
				// activityAdapter.notifyDataSetInvalidated() ;

				listViewActivity.invalidateViews();
				break;
				//		case R.message_row.view_rtml:
				//		case R.message_row_right.view_rtml:
				////			showToast("RTML Message clicked 1");
				//			// iPayloadData = aInboxdata.mPayload;
				//			InboxMessage mMessage = new InboxMessage();
				//			mMessage.mPayload = new Payload();
				//			mMessage.mPayload.mRTML = new byte[1][1];
				//			message.msgTxt = getRtmlText(message.messageId);
				//			mMessage.mPayload.mRTML[0] = message.msgTxt.getBytes();
				//
				//			// Get All Thumb URL's
				//			String thumbURLs[] = null;
				//
				//			if (message.image_content_thumb_urls != null) {
				//				thumbURLs = Utilities.split(new StringBuffer(
				//						message.image_content_thumb_urls),
				//						Constants.COL_SEPRETOR);
				//			}
				//			// Get All Images URL's
				//			String imageURLs[] = null;
				//			if (message.image_content_urls != null)
				//				imageURLs = Utilities.split(new StringBuffer(
				//						message.image_content_urls), Constants.COL_SEPRETOR);
				//			mMessage.mSenderName = message.source;// "Qts" ;
				//			if (imageURLs != null)
				//				mMessage.mPayload.mPicture = new byte[thumbURLs.length][];
				//			if (imageURLs != null)
				//				for (int i = 0; i < imageURLs.length; i++)
				//					mMessage.mPayload.mPicture[i] = thumbURLs[i].getBytes();
				//			if (imageURLs != null && imageURLs.length > 0)
				//				mMessage.mPayload.mSlideShowURLs = new String[imageURLs.length];
				//			if (imageURLs != null)
				//				for (int i = 0; i < imageURLs.length; i++)
				//					mMessage.mPayload.mSlideShowURLs[i] = imageURLs[i];
				//
				//			// Add Voice url
				//			if (message.voice_content_urls != null) {
				//				String voiceURLs1[] = Utilities.split(new StringBuffer(
				//						message.voice_content_urls), Constants.COL_SEPRETOR);
				//				mMessage.mPayload.mVoice = new byte[voiceURLs1.length][];
				//				for (int i = 0; i < voiceURLs1.length; i++)
				//					mMessage.mPayload.mVoice[i] = voiceURLs1[i].getBytes();
				//			}
				//			DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
				//					(InboxMessage) mMessage);
				//			Intent intent = new Intent(OtherMessageActivity.this,
				//					CustomViewDemo.class);
				//			intent.putExtra("PAGE", (byte) 1);
				//			// intent.putExtra("force_message", forceMessage);
				//			startActivityForResult(intent, 0);
				//			break;
				// case R.message_row.view_rtml:
				// showToast("System clicked");
				// //iPayloadData = aInboxdata.mPayload;
				// mMessage = new InboxMessage() ;
				// mMessage.mPayload = new Payload();
				// mMessage.mPayload.mRTML = new byte[1][1];
				// mMessage.mPayload.mRTML[0] = message.msgTxt.getBytes();
				//
				// //Get All Thumb URL's
				// String thumbURLs1[] = Utilities.split(new StringBuffer(
				// message.image_content_thumb_urls), Constants.COL_SEPRETOR);
				//
				// //Get All Images URL's
				// String imageURLs1[] = Utilities.split(new StringBuffer(
				// message.image_content_urls), Constants.COL_SEPRETOR);
				//
				// mMessage.mPayload.mPicture = new byte[thumbURLs1.length][];
				// for(int i = 0; i < imageURLs1.length; i++)
				// mMessage.mPayload.mPicture[i] = thumbURLs1[i].getBytes();
				// mMessage.mPayload.mSlideShowURLs = new String[imageURLs1.length];
				// for(int i = 0; i < imageURLs1.length; i++)
				// mMessage.mPayload.mSlideShowURLs[i] = imageURLs1[i];
				//
				// //Add Voice url
				// //Get All Images URL's
				// String voiceURLs1[] = Utilities.split(new StringBuffer(
				// message.voice_content_urls), Constants.COL_SEPRETOR);
				// mMessage.mPayload.mVoice = new byte[voiceURLs1.length][];
				// for(int i = 0; i < voiceURLs1.length; i++)
				// mMessage.mPayload.mVoice[i] = voiceURLs1[i].getBytes();
				//
				// mMessage.mSenderName = message.source;//"Qts" ;
				// DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
				// (InboxMessage) mMessage);
				// intent = new Intent(OtherMessageActivity.this, CustomViewDemo.class);
				// intent.putExtra("PAGE", (byte) 1);
				// // intent.putExtra("force_message", forceMessage);
				// startActivityForResult(intent, 0);
				// break;
			case R.message_row.view_Friend_Request:
				// if(BusinessProxy.sSelf.getUserId()!=-1 )
				//			String pendingUrl = "http://" + Urls.WAP_HOST
				//					+ "/mypage/pendinginvite/"
				//					+ BusinessProxy.sSelf.getUserId();
				//			DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, "Pending Invite");
				//			DataModel.sSelf.storeObject(DMKeys.XHTML_URL, pendingUrl);
				//			Intent intentPending = new Intent(OtherMessageActivity.this,
				//					WebviewActivity.class);
				//			intentPending.putExtra("PAGE", (byte) 1);
				//			startActivity(intentPending);

				// BusinessProxy.sSelf.addPush.closeAdd();
				// DataModel.sSelf.storeObject(DMKeys.PENDING_INVITE,
				// DMKeys.PENDING_TEXT);
				// pushNewScreen(WebviewActivity.class, false);
				break;
//			case R.message_row.inbox_accept:
//				// showToast("accept clicked");
//				Utilities.jsonParserEngine(message.msgTxt);
//				String acceptsource = Utilities.accept();
//				String acceptdestination = "";
//				if (acceptsource.indexOf("?") != -1) {
//					acceptdestination = acceptsource.substring(0,
//							acceptsource.indexOf("?"));
//					acceptsource = acceptsource.substring(
//							acceptsource.indexOf("=") + 1, acceptsource.length());
//				}
//
//				if (BusinessProxy.sSelf.sendNewTextMessage(acceptdestination,
//						acceptsource, true)) {
//					String alert = (String) getString(R.string.acceptAlert);
//					// showSimpleAlert(
//					// "Info",
//					// String.format(alert,
//					// (inboxElement.mSenderName.indexOf(';') != -1) ?
//					// inboxElement.mSenderName.subSequence(0,
//					// inboxElement.mSenderName.indexOf(';')) :
//					// inboxElement.mSenderName));
//
//					DialogInterface.OnClickListener blockHandler = null;
//
//					blockHandler = new DialogInterface.OnClickListener() {
//
//						public void onClick(DialogInterface dialog, int which) {
//							final Object deleteElement;
//
//							deleteElement = message.messageId;
//
//							List<Object> list = new ArrayList<Object>();
//							list.add(deleteElement);
//
//							BusinessProxy.sSelf.deleteForIDs(list,
//									DBEngine.INBOX_TABLE);
//							// sendDeleteMessageRequest(deleteElement);
//
//							// finish();
//						}
//					};
//					showAlertMessage("Confirm", alert,
//							new int[] { DialogInterface.BUTTON_POSITIVE },
//							new String[] { "OK" },
//							new DialogInterface.OnClickListener[] { blockHandler,
//							null });
//				}
//				break;
//			case R.message_row.inbox_decline:
//				//			showToast("decline clicked");
//				break;
//			case R.message_row.inbox_ignore:
//				//			showToast("ignore clicked");
//				break;
			case R.conversation.option:
				action(idsMenuOptions, idsMenuNameOptions, (byte) 1,
						idsMenuOptionsIcon);
				quickAction.show(v);
				break;
			case R.conversation.load_prevoius_message:
				break;
			case R.message_row_right.menu:
				// action(idsRowMenuOptions, idsRowMenuNameOptions, (byte)
				// 1,idsRowMenuOptionsIcon);
				// quickAction.show(v);
				openRowOption(2);
				break;
			case R.message_row.menu:
				// action(idsRowMenuOptions, idsRowMenuNameOptions, (byte)
				// 1,idsRowMenuOptionsIcon);
				// quickAction.show(v);

				DialogInterface.OnClickListener deleteHandler = new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (!OtherMessageActivity.this.checkInternetConnection()) {
							OtherMessageActivity.this.networkLossAlert();
							return;
						}
						request = new Request(ID_DELETE_MESSAGE, "url");
						request.execute("LikeDislike");
					}
				};

//				showAlertMessage("Confirm", getString(R.string.delete_confirmation),
//						new int[] { DialogInterface.BUTTON_POSITIVE,
//								DialogInterface.BUTTON_NEGATIVE },
//								new String[] { "Yes", "No" },
//								new DialogInterface.OnClickListener[] { deleteHandler, null });
				
				new AlertDialog.Builder(this).setMessage(getString(R.string.delete_confirmation))
				.setPositiveButton(R.string.yes, deleteHandler)
				.setNegativeButton(R.string.no, null).show();
				
				// openRowOption(2);
				break;
			case R.id.discovery_messageDisplay_FeatureUserName:
				//			FeaturedUserBean bean = (FeaturedUserBean) v.getTag();
				//
				//			DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, bean.displayName);
				//			DataModel.sSelf.storeObject(DMKeys.XHTML_URL, bean.profileUrl);
				//			intent = new Intent(this, WebviewActivity.class);
				//			intent.putExtra("PAGE", (byte) 1);
				//			startActivity(intent);
				break;

			case R.community_chat.sendButton:
				Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
						this);
				String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
						.getText().toString();
				if (null == mVoicePath && null == mPicturePath
						&& 0 == textMessage.trim().length()) {
					showSimpleAlert("Error", "Please add content to send message");
					return;
				}

				request = new Request(1000, "url");
				if (!OtherMessageActivity.this.checkInternetConnection()) {
					OtherMessageActivity.this.networkLossAlert();
					return;
				}
				request.execute("LikeDislike");
				// sendMessage();

				break;
			case R.id.discoveryRow_FeatureUserIcon:
				//			bean = (FeaturedUserBean) v.getTag();
				//
				//			if (bean.displayName != null)
				//				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
				//						bean.displayName);
				//			if (bean.profileUrl != null)
				//				DataModel.sSelf.storeObject(DMKeys.XHTML_URL, bean.profileUrl);
				//			intent = new Intent(this, WebviewActivity.class);
				//			intent.putExtra("PAGE", (byte) 1);
				//			startActivity(intent);
				break;

			case R.community_chat.capturePhoto:
				Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
						this);
				Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
						this);
				/*
				 * builder.setItems(new String[] { "Attach from file system",
				 * "Click using camera" }, new DialogInterface.OnClickListener() {
				 * public void onClick(DialogInterface dialog, int which) { switch
				 * (which) { case 0: openThumbnailsToAttach(POSITION_PICTURE);
				 * break; case 1: dialog.dismiss(); openCamera(); break; }
				 * dialog.dismiss(); } }); builder.setNegativeButton("Cancel",
				 * null); builder.create(); builder.show();
				 */

				final Dialog dialogVideo = new Dialog(OtherMessageActivity.this,
						android.R.style.Theme_Translucent_NoTitleBar);
				dialogVideo.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialogVideo.setContentView(R.layout.open_choice_dialog);
				dialogVideo.setCancelable(true);

				TextView text1 = (TextView) dialogVideo
						.findViewById(R.open_choice.message);
				text1.setText("");// String.format("Max count: 20 within max size: 4 MB. Current attached size: %s MB",
				// (totalSize / 1024.0 / 1024.0)));

				// TextView text1 = (TextView)
				// dialogVideo.findViewById(R.open_choice.message);
				// text1.setText(msg);

				Button button1 = (Button) dialogVideo
						.findViewById(R.open_choice.button1);
				button1.setText("Attach from file system");
				button1.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						dialogVideo.dismiss();
						openThumbnailsToAttach(POSITION_PICTURE);
					}
				});

				Button button2 = (Button) dialogVideo
						.findViewById(R.open_choice.button2);
				button2.setText("Click using camera");
				button2.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						dialogVideo.dismiss();
						openCamera();
					}
				});
				Button button3 = (Button) dialogVideo
						.findViewById(R.open_choice.rt_canvas);
				button3.setText("RT Canvas");
				if (Constants.DISPLAY_RT_CANVAS)
					button3.setVisibility(View.VISIBLE);
				button3.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						dialogVideo.dismiss();
						openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS);
					}
				});
				Button cancelButton = (Button) dialogVideo
						.findViewById(R.open_choice.cancelButton);
				cancelButton.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						dialogVideo.dismiss();
						// openCameraForVedioRecording();
					}
				});
				dialogVideo.show();
				break;
			case R.community_chat.recordAudio:
				// RTMediaPlayer.reset();
				closePlayScreen();
				mVoiceMedia.startRecording(getString(R.string.done), getString(R.string.cancel), null,
						Constants.MAX_AUDIO_RECORD_TIME_REST);
				break;
			case R.community_chat.recordVideo:

				String msg = getMaxSizeTextVideo();
				final Dialog dialog = new Dialog(OtherMessageActivity.this,
						android.R.style.Theme_Translucent_NoTitleBar);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.open_choice_dialog);
				dialog.setCancelable(true);

				TextView text2 = (TextView) dialog
						.findViewById(R.open_choice.message);
				text2.setText(msg);

				button3 = (Button) dialog.findViewById(R.open_choice.button1);
				button3.setText("Attach from file system");
				button3.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						saveMessageContents();
						dialog.dismiss();
						openThumbnailsToAttach(POSITION_VIDEO);
					}
				});

				Button button4 = (Button) dialog
						.findViewById(R.open_choice.button2);
				button4.setText("Record using camera");
				button4.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						saveMessageContents();
						dialog.dismiss();
						// action(idsReport, idsNameReport, (byte) 1);
						// quickAction.show(v);
						if (BusinessProxy.sSelf.rToolTipsFlag) {
							Timer timer = new Timer();

							timer.schedule(new TimerTask() {
								public void run() {
									openCameraForVedioRecording();
									BusinessProxy.sSelf.rToolTipsFlag = false;
								}
							}, 5000);
						} else {
							openCameraForVedioRecording();
						}

					}
				});
				Button cancelButton1 = (Button) dialog
						.findViewById(R.open_choice.cancelButton);
				cancelButton1.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}

	protected void sendDeleteMessageRequest(Object aMessageID) {
		try {

			OutboxTblObject newRequest = new OutboxTblObject(1,
					Constants.FRAME_TYPE_INBOX_DEL,
					Constants.MSG_OP_MULTIPLE_DELETE);
			if (Logger.ENABLE) {
				Logger.info(TAG,
						"sendDeleteMessageRequest-- DELETE MESSAGE ID = "
								+ aMessageID);
			}
			newRequest.mMessageIDString = aMessageID.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void voiceRecordingCompleted(String recordedVoicePath) {
		mVoicePath = recordedVoicePath;
		String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
				.getText().toString();
		if (null == mVoicePath && null == mPicturePath
				&& 0 == textMessage.trim().length()) {
			showSimpleAlert("Error", "Please add content to send message");
			return;
		}
		if (textMessage == null)
			textMessage = "";
		quickSend();
	}

	// public void sendMessage(){
	//
	// }

	private void sendMessage() {
		String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
				.getText().toString();
		OutboxTblObject newRequest = new OutboxTblObject(1,
				Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_VOICE_NEW);
		if (null != mVoicePath) {
			newRequest.mPayloadData.mVoiceType[0] = VoiceTypes.PCM_FORMAT;
			newRequest.mPayloadData.mVoice[0] = mVoicePath.getBytes();
			newRequest.mPayloadData.mPayloadTypeBitmap = Payload.PAYLOAD_BITMAP_VOICE;
		}
		if (textMessage != null && textMessage.length() > 0) {
			newRequest.mPayloadData.mText[0] = textMessage.getBytes();
			newRequest.mPayloadData.mTextType[0] = 1;
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
		}
		if (!mPicturePath.isEmpty()) {
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
			newRequest.mPayloadData.mPicture = new byte[mPicturePath.size()][];
			newRequest.mPayloadData.mPictureType = new byte[mPicturePath.size()];
			int counter = 0;
			for (String path : mPicturePath) {
				ImageUtils.rotateAndSaveImage(path);
				newRequest.mPayloadData.mPicture[counter] = path.getBytes();
				newRequest.mPayloadData.mPictureType[counter] = PictureTypes.PICS_JPEG;
				counter++;
			}
		}

		if (null != mVideoPath && mVideoPath.length() > 0) {
			newRequest.mPayloadData.mVideoType[0] = VideoTypes.VIDEO_3GP_FORMAT;
			newRequest.mPayloadData.mVideo[0] = mVideoPath.getBytes();
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
		}

		Message message = new Message();
		message.msgTxt = textMessage;
		message.messageId = "" + (System.currentTimeMillis() - 10000);
		message.mfuId = "-1";
		message.datetime = "" + (new Date()).toString();
		message.clientTransactionId = "" + (new Date()).toString();
		message.conversationId = getIntent().getStringExtra(
				Constants.CONVERSATION_ID);
		saveMessage(message);

		newRequest.mDestContacts = new String[] { "qts" };
		int ret = BusinessProxy.sSelf.sendToTransport(newRequest);
		switch (ret) {
		case Constants.ERROR_NONE:
			// MediaEngine.getMediaEngineInstance().playResource(R.raw.rocketalert);
			((EditText) findViewById(R.community_chat.msgBox)).setText("");
			break;
		case Constants.ERROR_OUTQUEUE_FULL:
			showSimpleAlert("Error", "Outbox full. Please try after some time!");
			return;
		default:
			if (Logger.ENABLE) {
				Logger.error(TAG,
						"voiceRecordingCompleted-sendMessage -- Error " + ret,
						null);
			}
			showSimpleAlert("Error", getString(R.string.network_busy));
			return;
		}
	}

	public void saveMessage(Message message) {

		long st = System.currentTimeMillis();
		ContentValues values = new ContentValues();
		values.put(MessageTable.USER_ID, System.currentTimeMillis());// int
		values.put(MessageTable.PARENT_ID, message.parent_id);
		values.put(MessageTable.CLIENTTRANSACTION_ID,
				message.clientTransactionId);
		values.put(MessageTable.CONTENT_BITMAP, message.contentBitMap);
		values.put(MessageTable.CONVERSATION_ID, message.conversationId);
		values.put(MessageTable.DATE_TIME, message.datetime);
		values.put(MessageTable.DRM_FLAGS, message.drmFlags);// int
		values.put(MessageTable.HAS_BEEN_VIEWED, message.hasBeenViewed);
		values.put(MessageTable.HAS_BEEN_VIEWED_BY_SIP,
				message.hasBeenViewedBySIP);
		values.put(MessageTable.MESSAGE_ID, message.messageId);
		values.put(MessageTable.MFU_ID, message.mfuId);
		values.put(MessageTable.MSG_OP, message.msgOp);// int
		values.put(MessageTable.MSG_TXT, message.msgTxt);
		values.put(MessageTable.NOTIFICATION_FLAGS, message.notificationFlags);
		values.put(MessageTable.REFMESSAGE_ID, message.refMessageId);
		values.put(MessageTable.SENDERUSER_ID, message.senderUserId);
		values.put(MessageTable.SOURCE, message.source);
		values.put(MessageTable.TARGETUSER_ID, message.targetUserId);
		values.put(MessageTable.USER_FIRSTNAME, message.user_firstname);
		values.put(MessageTable.USER_LASTNAME, message.user_lastname);
		values.put(MessageTable.USER_NAME, message.user_name);
		values.put(MessageTable.USER_URI, message.user_uri);
		// values.put(MessageTable.DIRECTION, message.);
		values.put(MessageTable.INSERT_TIME, System.currentTimeMillis());

		if (message.voice_content_urls != null) {

			// message.video_content_thumb_urls = mVoicePath ;
			values.put(MessageTable.VOICE_CONTENT_URLS,
					message.voice_content_urls);
			values.put(MessageTable.AUDIO_ID,
					message.voice_content_urls.hashCode());
		}

		// values.put(MessageTable.MORE, System.currentTimeMillis());
		Uri u = getContentResolver().insert(
				MediaContentProvider.CONTENT_URI_INBOX, values);

	}

	private void openCamera() {

		if ((frontCam && rearCam) || (!frontCam && rearCam)) {
			cameraImagePath = null;
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			cameraImagePath = file.getPath();
			Uri outputFileUri = Uri.fromFile(file);
			// Intent i = new Intent("android.media.action.IMAGE_CAPTURE");

			// System.out.println("cameraImagePath===on"+cameraImagePath);
			Intent i = new Intent(OtherMessageActivity.this,
					DgCamActivity.class);
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			i.putExtra("urlpath", cameraImagePath);

			this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		}
		if (frontCam && !rearCam) {
			cameraImagePath = null;
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			cameraImagePath = file.getPath();
			Uri outputFileUri = Uri.fromFile(file);
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		}
		/*
		 * cameraImagePath = null; // File file = new File(Utilities.
		 * getPicPath(), getRandomNumber() + // ".jpg"); File file = new
		 * File(Environment.getExternalStorageDirectory(), getRandomNumber() +
		 * ".jpg"); cameraImagePath = file.getPath(); Uri outputFileUri =
		 * Uri.fromFile(file); Intent i = new
		 * Intent("android.media.action.IMAGE_CAPTURE");
		 * i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		 * this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		 */
	}

	private void openThumbnailsToAttach(byte which) {
		Intent intent = new Intent();
		switch (which) {
		case POSITION_PICTURE_RT_CANVAS:
			intent = new Intent(this, RTCanvas.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
			// | Intent.FLAG_ACTIVITY_CLEAR_TOP
			// | Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.setData(Uri.parse("ddd")) ;
			startActivityForResult(intent, POSITION_PICTURE_RT_CANVAS);
			break;
		case POSITION_PICTURE:

			if (mPicturePath != null && mPicturePath.size() > 0) {
				String[] data = new String[mPicturePath.size()];
				for (int i = 0; i < mPicturePath.size(); i++) {
					data[i] = mPicturePath.get(i);
				}
				DataModel.sSelf.storeObject("3333", data);
			}
			if (mPicturePathId != null && mPicturePathId.size() > 0) {
				Integer[] dataId = new Integer[mPicturePathId.size()];
				for (int i = 0; i < mPicturePathId.size(); i++) {
					dataId[i] = mPicturePathId.get(i);
				}
				DataModel.sSelf.storeObject("3333" + "ID", dataId);
			}

			intent = new Intent(this, RocketalkMultipleSelectImage.class);

			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);
			// intent.setType("image/*");
			// intent.setAction(Intent.ACTION_GET_CONTENT);
			// startActivityForResult(Intent.createChooser(intent,
			// "Select Picture"), POSITION_PICTURE);
			break;

		case POSITION_VIDEO:
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Video"),
					POSITION_VIDEO);
			break;
		}
	}

	private void saveMessageContents() {
		String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
				.getText().toString();
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_TEXT_MESSAGE,
				textMessage.toString());
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_VOICE_MESSAGE, mVoicePath);
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_PICTURE_MESSAGE,
				mPicturePath);
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_VIDEO_MESSAGE, mVideoPath);
	}

	private String getMaxSizeTextVideo() {
		double sizeInMB = totalSize / 1024.0 / 1024.0;
		String text = Double.toString(sizeInMB);
		int index = text.indexOf('.');
		if (-1 != index && index + 5 < text.length()) {
			text = text.substring(0, index + 5);
		}
		return String.format("Max size: " + BusinessProxy.sSelf.MaxSizeData
				+ " MB. Current attached size: %s MB", text);
	}

	public void openCameraForVedioRecording() {
		File videofile = null;
		try {
			videofile = File.createTempFile("temp", ".mp4", getCacheDir());
			// String videofileName = videofile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File file = new File(Environment.getExternalStorageDirectory(), "rt"
				+ getRandomNumber() + ".mp4");
		cameraImagePath = file.getPath();
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
		// intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,outputFileUri);//
		// Uri.fromFile(videofile));
		if (BusinessProxy.sSelf.buildInfo.MODEL
				.equalsIgnoreCase(Constants.VIDEO_REC_PROB_ON_DEVICE))
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					outputFileUri);// Uri.fromFile(videofile));
		else
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(videofile));
		// recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// intent.setType("image/*");
		intent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY,
				Constants.EXTRA_VIDEO_QUALITY);
		intent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, 0);
		intent.putExtra("android.intent.extra.durationLimit",
				Constants.VIDEO_RECORDING_DUERATION);
		intent.putExtra("android.intent.extra.sizeLimit",
				Constants.VIDEO_RECORDING_SIZE_LIMITE);
		startActivityForResult(intent, POSITION_VIDEO);
		startRecoridng = true;
	}

	String dist = "";

	@Override
	public void viewMessage(View convertView, Context context, Message message) {
		try {

			//			load_prevoius_message.setVisibility(View.GONE);
			MessageViewHolder messageViewHolder = (MessageViewHolder) convertView.getTag();
			checkIsView(message);
			if(message != null && message.messageId != null)
				sendReadReceipt("",message.messageId);
			// if(message.isLeft==1){
			// FeedTask.updateParticipantInfoUI=false ;
			// initParticipantInfo();
			// updateParticepentLabel() ;
			// //findViewById(R.chat_panel.chat_panel).setVisibility(View.GONE)
			// ;
			// }
			// checkIsView(message);
			dist = "";
			//			if (load_prevoius_message != null && message.isNext == 1) {
			//				load_prevoius_message.setVisibility(View.VISIBLE);
			//			} else {
			//				if (load_prevoius_message != null)
			//					load_prevoius_message.setVisibility(View.GONE);
			//			}

			// ((TextView) findViewById(R.conversation.allParticipent))
			// .setText(getParticipantInfo());

			// if (message.subject != null && message.subject.trim().length() >
			// 0) {
			// title.setText(message.subject);
			// title2.setText(message.subject);
			// }

			messageViewHolder.message = message;
			convertView.setTag(messageViewHolder);
			messageViewHolder.menuLeft.setOnClickListener(this);
			messageViewHolder.menuLeft.setTag(message);
			// akm messageViewHolder.menuRight.setOnClickListener(this);
			// akm messageViewHolder.menuRight.setTag(message);
			// view_Friend_Request
			// messageViewHolder.friend_invite.setTag(message);
			// messageViewHolder.friend_invite.setOnClickListener(this);
			// akmmessageViewHolder.inbox_accept.setTag(message);
			// akmmessageViewHolder.inbox_accept.setOnClickListener(this);
			// akmmessageViewHolder.inbox_decline.setTag(message);
			// akmmessageViewHolder.inbox_decline.setOnClickListener(this);
			// akmmessageViewHolder.inbox_ignore.setTag(message);
			// akmmessageViewHolder.inbox_ignore.setOnClickListener(this);

			messageViewHolder.view_rtml.setVisibility(View.GONE);

			// convertView.findViewById(R.message_row_right.friend_invite)
			// .setVisibility(View.GONE);

			ParticipantInfo senderParticipantInfo = null;
			boolean isSender = false;
			if (messageViewHolder.message.participantInfo != null
					&& messageViewHolder.message.participantInfo.size() > 0) {
				// System.out.println("---------participantInfoStr view: "+messageViewHolder.message.participantInfo);
				// System.out.println("-------------user name : "+SettingData.sSelf
				// .getUserName());
				for (int i = 0; i < messageViewHolder.message.participantInfo
						.size(); i++) {
					ParticipantInfo participantInfo = messageViewHolder.message.participantInfo
							.get(i);
					if (participantInfo.isSender
							&& participantInfo.userName
							.equalsIgnoreCase(SettingData.sSelf
									.getUserName()))
						isSender = true;
					if (participantInfo.isSender)
						senderParticipantInfo = participantInfo;
					dist = dist + participantInfo.userName + ";";
				}
			}
			// System.out.println("------------mfuId 2 :" + message.mfuId);
			messageViewHolder.left.setVisibility(View.VISIBLE);
			// akmmessageViewHolder.right.setVisibility(View.VISIBLE);
			messageViewHolder.left.setTag(messageViewHolder.message);
			// akmmessageViewHolder.right.setTag(messageViewHolder.message);
			// messageViewHolder.left
			// .setOnLongClickListener(new OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// // showCopy(v);
			// return false;
			// }
			// });
			// messageViewHolder.right
			// .setOnLongClickListener(new OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// // showCopy(v);
			// return false;
			// }
			// });
			messageViewHolder.menuLeft.setBackgroundResource(R.drawable.retake);
			//messageViewHolder.menuLeft.setText("Delete");
			// messageViewHolder.menuLeft.setLayoutParams(params)

//			messageViewHolder.menuLeft.setVisibility(View.GONE);
			// if (conversationList != null && conversationList.isGroup == 0) {
			// messageViewHolder.other_image_layout.setVisibility(View.GONE);
			// messageViewHolder.me_image_layout.setVisibility(View.GONE);
			// }

			/*
			 * if (isSender) { renderRight(convertView, context, message,
			 * messageViewHolder, senderParticipantInfo);
			 * messageViewHolder.left.setVisibility(View.GONE); } else
			 */{
				 renderLeft(convertView, context, message, messageViewHolder,
						 senderParticipantInfo);
				 // akm messageViewHolder.right.setVisibility(View.GONE);
			 }

			 // akm messageViewHolder.menuRight.setVisibility(View.VISIBLE);
			 // if (messageViewHolder.message.mfuId == null
			 // || messageViewHolder.message.mfuId.equalsIgnoreCase("-1")
			 // || messageViewHolder.message.mfuId.trim().length() <= 0)
			 // if (messageViewHolder.message.mfuId.equalsIgnoreCase("-1")
			 // )
			 // {
			 // messageViewHolder.menuLeft.setVisibility(View.GONE);
			 // messageViewHolder.menuRight.setVisibility(View.GONE);
			 // }
			 messageViewHolder.right.setVisibility(View.GONE);
			 // convertView.findViewById(R.message_row.friend_invite)
			 // .setVisibility(View.GONE);

			 // @drawable/landingpagebackground

			 if (message.hasBeenViewed == null
					 || message.hasBeenViewed.length() <= 0
					 || message.hasBeenViewed.equalsIgnoreCase("true")) {
				 // messageViewHolder.datacontainer.setBackgroundResource(R.drawable.list_selector_ontap)
				 // ;
				 convertView
				 .setBackgroundResource(R.drawable.list_selector_ontap);
			 } else {
				 // messageViewHolder.datacontainer.setBackgroundResource(R.drawable.list_selector_ontop_read)
				 // ;
				 convertView
				 .setBackgroundResource(R.drawable.list_selector_ontop_read);
			 }

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	String s[] = {
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg",
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/572860_100000665125941_1173252510_q.jpg",
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/572658_100000431658511_1470386684_q.jpg",
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash4/211623_1250359870_2139397439_q.jpg",
			"http://fbcdn-sphotos-g-a.akamaihd.net/hphotos-ak-ash4/381821_469242199814603_1765457441_n.jpg",
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash4/373297_58762883453_691832530_q.jpg",
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg",
			"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/276675_240755355970062_1295775176_q.jpg",
	"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/274160_100001570279923_1957553652_q.jpg" };

	public void renderLeft(View convertView, Context context, Message message,
			MessageViewHolder messageViewHolder, ParticipantInfo participantInfo) {

		message.participantInfoClazz = participantInfo;
		messageViewHolder.viewAll.setVisibility(View.VISIBLE);
		messageViewHolder.notification.setVisibility(View.GONE);
		messageViewHolder.arrowViewForSystem.setVisibility(View.GONE);
		// if (message.datetime != null) {
		messageViewHolder.datetime.setVisibility(View.VISIBLE);
		// messageViewHolder.datetime.setText(message.datetime);
		messageViewHolder.datetime.setText(Utilities
				.compareDate(Utilities.gettimemillis(message.datetime),OtherMessageActivity.this));
		// } else {
		// messageViewHolder.datetime.setVisibility(View.GONE);
		// }

		if (message.messageType == message.MESSAGE_TYPE_UPDATE_SUBJECT
				|| message.messageType == message.MESSAGE_TYPE_LEAVE_CONVERSATION
				|| message.messageType == message.MESSAGE_TYPE_ADD_TO_CONVERSATION) {
			messageViewHolder.viewAll.setVisibility(View.GONE);
			messageViewHolder.notification.setVisibility(View.VISIBLE);
			messageViewHolder.notification.setText(message.msgTxt);
			messageViewHolder.left.setOnLongClickListener(null);
			return;
		}
		//System.out.println("deleteMode===================deletmod=="
		//	+ deleteMode);
		if (deleteMode) {
			messageViewHolder.menuLeft.setVisibility(View.VISIBLE);

		}

		String txt = message.msgTxt;
		if (message.datetime != null) {
			messageViewHolder.messageView.setVisibility(View.VISIBLE);
			if (message.contentBitMap != null
					&& message.contentBitMap.indexOf("RTML") != -1)// txt.indexOf("<?xml version=")
				// != -1)
			{
				txt = "You have received a system notification";
				messageViewHolder.view_rtml.setVisibility(View.VISIBLE);
				messageViewHolder.arrowViewForSystem
				.setVisibility(View.VISIBLE);
				messageViewHolder.view_rtml.setTag(message);
				messageViewHolder.view_rtml.setOnClickListener(this);
				messageViewHolder.view_rtml.setVisibility(View.GONE);
			}

			// will remove this login this temp because m not geting
			// notification flag in feed
			convertView.findViewById(R.message_row.view_Friend_Request).setVisibility(View.GONE);
			// if (txt.startsWith("{")) {
			if (0 < (message.notificationFlags & InboxTblObject.NOTI_FRIEND_INVITATION_MSG)) {
				Utilities.jsonParserEngine(txt);
				if (Utilities.accept() != null) {
					if (participantInfo.firstName != null
							&& participantInfo.lastName != null)
						txt = participantInfo.firstName + " "
								+ participantInfo.lastName
								+ " has sent you a friend request";
					else
						txt = participantInfo.userName
						+ " has sent you a friend request";
					// convertView.findViewById(R.message_row_right.friend_invite)
					// .setVisibility(View.VISIBLE);
					// messageViewHolder.friend_invite
					convertView.findViewById(R.message_row.view_Friend_Request).setVisibility(View.GONE);
					convertView.findViewById(R.message_row.view_Friend_Request).setOnClickListener(this);
				}
				messageViewHolder.arrowViewForSystem.setVisibility(View.VISIBLE);
			}
			if (participantInfo != null) {
				if(txt.startsWith("{\"")){
					messageViewHolder.messageView.setText(getString(R.string.reported_message));
					messageViewHolder.messageView.setTag(message);
					messageViewHolder.messageView.setOnClickListener(OtherMessageActivity.this);
					messageViewHolder.conversation_id.setTag(message);
					messageViewHolder.conversation_id.setOnClickListener(OtherMessageActivity.this);
					messageViewHolder.datacontainer.setTag(message);
					messageViewHolder.datacontainer.setOnClickListener(OtherMessageActivity.this);
				}
				else{
					messageViewHolder.messageView.setText(txt);
				}
			} else {
				if(txt.startsWith("{\"")){
					messageViewHolder.messageView.setText(getString(R.string.reported_message));
					messageViewHolder.messageView.setTag(message);
					messageViewHolder.messageView.setOnClickListener(OtherMessageActivity.this);
					messageViewHolder.conversation_id.setTag(message);
					messageViewHolder.conversation_id.setOnClickListener(OtherMessageActivity.this);
					messageViewHolder.datacontainer.setTag(message);
					messageViewHolder.datacontainer.setOnClickListener(OtherMessageActivity.this);
				}
				else
					messageViewHolder.messageView.setText(txt);
				participantInfo = new ParticipantInfo();
				participantInfo.userName = SettingData.sSelf.getUserName();
				participantInfo.profileUrl = SettingData.sSelf.getUserName();
				// participantInfo.profileUrl=SettingData.sSelf.getUserName() ;
			}
//			messageViewHolder.messageView.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			messageViewHolder.messageView.setVisibility(View.GONE);
		}

		if (message.conversationId != null) {
			messageViewHolder.conversation_id.setVisibility(View.VISIBLE);

			// // if cheate
			if (Logger.CHEAT)
				messageViewHolder.conversation_id.setText(message.drmFlags
						+ ":" + message.notificationFlags + ":" + message.mfuId
						+ " : " + message.messageId + " view :"
						+ message.hasBeenViewed + ":"
						+ message.messageAttribute);
			else {

				if ((participantInfo.firstName != null && participantInfo.firstName
						.length() > 0)
						&& (participantInfo.lastName != null && participantInfo.lastName
						.length() > 0))
					messageViewHolder.conversation_id
					.setText(participantInfo.firstName + " "
							+ participantInfo.lastName);
				else {
					if (participantInfo.userName.indexOf("<") != -1
							&& participantInfo.userName.indexOf(">") != -1)
						messageViewHolder.conversation_id
						.setText(BusinessProxy.sSelf
								.parseNameInformation(participantInfo.userName));
					else
						messageViewHolder.conversation_id
						.setText(participantInfo.userName);
				}
			}

			messageViewHolder.conversation_id
			.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			messageViewHolder.conversation_id.setVisibility(View.GONE);
		}
		// added akm
		messageViewHolder.me_image
		.setBackgroundResource(R.drawable.defaultrtsystem);
		messageViewHolder.me_image.setVisibility(View.VISIBLE);
		// /////////////
		// messageViewHolder.voice.setVisibility(View.GONE);
		// messageViewHolder.receving_voiceLeft.setVisibility(View.GONE);
		// messageViewHolder.receving_voiceLeft.setText(getResources().getString(
		// R.string.reciving_voice));
		// messageViewHolder.receving_voiceLeft.setOnClickListener(null);
		// messageViewHolder.voice.setOnClickListener(this);
		// messageViewHolder.voice.setTag(message);
		// if (message.voice_content_urls != null) {
		// if (message.audio_download_statue != 1) {
		// if ((System.currentTimeMillis() - message.inserTime) < 1000 * 30) {
		// Downloader downloader = Downloader.getInstance();
		// downloader.download(message.voice_content_urls, 2, this);
		// messageViewHolder.receving_voiceRight
		// .setText(getResources().getString(
		// R.string.reciving_voice));
		// messageViewHolder.receving_voiceLeft
		// .setVisibility(View.VISIBLE);
		// } else if (Downloader.links
		// .contains(message.voice_content_urls)) {
		// messageViewHolder.receving_voiceLeft.setText(getResources()
		// .getString(R.string.reciving_voice));
		// messageViewHolder.receving_voiceLeft
		// .setVisibility(View.VISIBLE);
		//
		// } else {
		//
		// {
		// if (Downloader.getInstance().getUrl() != null
		// && Downloader
		// .getInstance()
		// .getUrl()
		// .equalsIgnoreCase(
		// message.voice_content_urls))
		// messageViewHolder.receving_voiceRight
		// .setText(getResources().getString(
		// R.string.reciving_voice));
		// else
		// messageViewHolder.receving_voiceLeft
		// .setText(getResources().getString(
		// R.string.get_voice));
		//
		// messageViewHolder.receving_voiceLeft
		// .setOnClickListener(this);
		// messageViewHolder.receving_voiceLeft.setTag(message);
		// messageViewHolder.receving_voiceLeft
		// .setVisibility(View.VISIBLE);
		// }
		// }
		// } else {
		// messageViewHolder.voice
		// .setBackgroundResource(R.drawable.audio_activity);
		// messageViewHolder.voice.setVisibility(View.VISIBLE);
		// }
		// }
		//
		// ImageDownloader imageManager = new ImageDownloader();
		// imageManager.download(/*participantInfo.userName*/participantInfo.userName,
		// messageViewHolder.me_image, OtherMessageActivity.this);
		// messageViewHolder.me_image.setVisibility(View.VISIBLE);
		// messageViewHolder.me_image.setOnClickListener(this);
		// messageViewHolder.me_image.setTag(participantInfo);
		//
		// messageViewHolder.images.removeAllViews();
		//
		// float sizeInDipHeight = 150f;
		// float sizeInDipWeidth = 150f;
		// if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_LARGE)
		// {
		// sizeInDipHeight = 150f;
		// sizeInDipWeidth = 150f;
		// }else
		// if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_NORMAL)
		// {
		// sizeInDipHeight = 150f/2;
		// sizeInDipWeidth = 150f/2;
		// }
		// else
		// if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_SMALL)
		// {
		// sizeInDipHeight = 150f/2;
		// sizeInDipWeidth = 150f/2;
		// }
		// int heightPX = (int)
		// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		// sizeInDipHeight, getResources().getDisplayMetrics());
		// int weigthPX = (int)
		// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		// sizeInDipWeidth, getResources().getDisplayMetrics());
		//
		// LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
		// weigthPX, heightPX);
		// parms.setMargins(5, 0, 5, 0);
		//
		// if (message.image_content_thumb_urls != null
		// && message.image_content_thumb_urls.trim().length() > 0) {
		// messageViewHolder.images.setVisibility(View.VISIBLE);
		// s = Utilities.split(new StringBuffer(
		// message.image_content_thumb_urls), Constants.COL_SEPRETOR);
		// for (int j = 0; j < s.length; j++) {
		// CImageView b = new CImageView(OtherMessageActivity.this);
		// parms.setMargins(5, 0, 5, 0);
		//
		// b.setBackgroundResource(R.drawable.border);
		//
		// b.setLayoutParams(parms);
		//
		// imageManager = new ImageDownloader();
		// imageManager.download(s[j], b, OtherMessageActivity.this);
		// b.setTag(message);
		// b.setOnClickListener(this);
		// messageViewHolder.images.addView(b);
		// b.setId(1000);
		// }
		// }
		//
		// messageViewHolder.video.setVisibility(View.GONE);
		// messageViewHolder.video.removeAllViews();
		// if (message.video_content_thumb_urls != null
		// && message.video_content_thumb_urls.trim().length() > 0) {
		// messageViewHolder.video.setVisibility(View.VISIBLE);
		// s = Utilities.split(new StringBuffer(
		// message.video_content_thumb_urls), Constants.COL_SEPRETOR);
		// String []ss = Utilities.split(new StringBuffer(
		// message.video_content_urls), Constants.COL_SEPRETOR);
		//
		// if(message.video_size==null)
		// message.video_size="" ;
		//
		// String []size = Utilities.split(new StringBuffer(
		// message.video_size), Constants.COL_SEPRETOR);
		// for (int j = 0; j < s.length; j++) {
		// RelativeLayout l1 = new RelativeLayout(this);
		// l1.setLayoutParams(parms);//new
		// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)) ;
		//
		// CImageView b = new CImageView(OtherMessageActivity.this);
		//
		// parms.setMargins(5, 0, 5, 0);
		// l1.setLayoutParams(parms);
		//
		// b.setBackgroundResource(R.drawable.border);
		//
		// imageManager = new ImageDownloader();
		// imageManager.download(s[j], b, OtherMessageActivity.this);
		// b.setTag(message);
		// b.setScaleType(ScaleType.FIT_CENTER) ;
		// b.setOnClickListener(null);
		// l1.addView(b) ;
		// Button btn=new Button(this);
		// VideoUrl videoUrl = new VideoUrl() ;
		// videoUrl.url = ss[j] ;
		// videoUrl.index = (j+1) ;
		// if (s[j].trim().startsWith("http"))
		// btn.setText(getResources().getString(R.string.get_video)+"["+Utilities.humanReadableByteCount(Long.parseLong(size[j]),
		// true)+"]") ;
		// videoUrl.acaton=0 ;
		// if (VDownloader.getInstance().getUrl() != null
		// && VDownloader
		// .getInstance()
		// .getUrl()
		// .equalsIgnoreCase(
		// ss[j])){
		// btn.setText(getResources().getString(R.string.reciving_video));
		// videoUrl.acaton=1 ;
		// }
		// else if(VDownloader.getInstance().check(ss[j]))
		// {
		// b.setOnClickListener(this);
		// b.setId(100012);
		// b.setTag(ss[j]) ;
		// btn.setVisibility(View.GONE) ;
		//
		// parms.setMargins(5, 0, 5, 0);
		// l1.setLayoutParams(parms);
		// }
		// btn.setBackgroundResource(R.drawable.unfollow) ;
		// btn.setTextColor(Color.WHITE) ;
		// l1.addView(btn) ;
		// btn.setId(100011);
		// btn.setOnClickListener(this);
		//
		// btn.setTag(videoUrl ) ;
		//
		// b.setImageResource(R.drawable.video_overlay);
		//
		// messageViewHolder.video.addView(l1);
		//
		// }
		// }
		// //System.out.println("message.video_content_thumb_urls=="+message.video_content_thumb_urls);
		// if (message.video_content_thumb_urls != null
		// && message.video_content_thumb_urls.trim().length() > 0) {
		// messageViewHolder.images.setVisibility(View.VISIBLE);
		// imageManager = new ImageDownloader();
		// imageManager.download(message.video_content_thumb_urls,
		// messageViewHolder.video, OtherMessageActivity.this);
		// messageViewHolder.video.setLayoutParams(parms);
		// messageViewHolder.video.setVisibility(View.VISIBLE);
		// messageViewHolder.video.setTag(message);
		// messageViewHolder.video.setOnClickListener(this);
		// }
	}

	public int getThumbwidht(int param) {
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_LARGE)
			param = 500;
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_MEDIUM)
			param = 300;
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_SMALL)
			param = 100;

		return (int) Utilities.convertPixelsToDp(param, this);
	}

	public int getThumbHeight(int param) {
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_LARGE)
			param = 500;
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_MEDIUM)
			param = 300;
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_SMALL)
			param = 100;
		return (int) Utilities.convertPixelsToDp(param, this);
	}

	public void renderRight(View convertView, Context context, Message message,
			MessageViewHolder messageViewHolder, ParticipantInfo participantInfo) {

		// if (message.datetime != null) {
		// messageViewHolder.datetimeRight.setVisibility(View.VISIBLE);
		// // messageViewHolder.datetime.setText(message.datetime);
		// messageViewHolder.datetimeRight.setText(Utilities.compareDate(message.inserTime));
		// } else {
		// messageViewHolder.datetimeRight.setVisibility(View.GONE);
		// }

		messageViewHolder.viewAllRight.setVisibility(View.VISIBLE);
		messageViewHolder.notificationRight.setVisibility(View.GONE);

		if (message.messageType == message.MESSAGE_TYPE_UPDATE_SUBJECT
				|| message.messageType == message.MESSAGE_TYPE_LEAVE_CONVERSATION
				|| message.messageType == message.MESSAGE_TYPE_ADD_TO_CONVERSATION) {
			messageViewHolder.viewAllRight.setVisibility(View.GONE);
			messageViewHolder.notificationRight.setVisibility(View.VISIBLE);
			messageViewHolder.notificationRight.setText(message.msgTxt);
			messageViewHolder.left.setOnLongClickListener(null);
			return;
		}

		message.participantInfoClazz = participantInfo;
		messageViewHolder.sending_status_right.setVisibility(View.GONE);
		// if (message.inserTime != null)
		// {
		messageViewHolder.datetimeRight.setVisibility(View.VISIBLE);
		messageViewHolder.datetimeRight.setText(Utilities
				.compareDate(Utilities.gettimemillis(message.datetime),OtherMessageActivity.this));
		// } else {
		// messageViewHolder.datetimeRight.setVisibility(View.GONE);
		// }

		if (message.sending_state == Constants.MESSAGE_STATUS_SENDING) {
			messageViewHolder.sending_status_right.setVisibility(View.VISIBLE);
			messageViewHolder.sending_status_right.setText(getResources()
					.getString(R.string.message_sending));
			Drawable search_unsel = getResources().getDrawable(
					R.drawable.message_waiting);
			Rect r = null;
			Drawable[] d = messageViewHolder.sending_status_right
					.getCompoundDrawables();
			r = null;
			if (d[0] != null)
				r = d[0].getBounds();
			if (r != null)
				search_unsel.setBounds(r);
			messageViewHolder.sending_status_right.setBackgroundDrawable(null);
			messageViewHolder.sending_status_right.setCompoundDrawables(
					search_unsel, null, null, null);

		} else if (message.sending_state == Constants.MESSAGE_STATUS_UNABLE_TO_SEND) {
			messageViewHolder.sending_status_right.setVisibility(View.VISIBLE);
			messageViewHolder.sending_status_right.setText(getResources()
					.getString(R.string.message_retry));

			Drawable search_unsel = getResources().getDrawable(
					R.drawable.message_retry);
			Rect r = null;
			Drawable[] d = messageViewHolder.sending_status_right
					.getCompoundDrawables();
			r = null;
			if (d[0] != null)
				r = d[0].getBounds();
			if (r != null)
				search_unsel.setBounds(r);
			messageViewHolder.sending_status_right.setBackgroundDrawable(null);
			messageViewHolder.sending_status_right.setCompoundDrawables(
					search_unsel, null, null, null);

			messageViewHolder.sending_status_right.setTag(message.messageId);
		} else if (message.sending_state == Constants.MESSAGE_STATUS_SENT) {
			messageViewHolder.sending_status_right.setVisibility(View.VISIBLE);
			messageViewHolder.sending_status_right.setText(getResources()
					.getString(R.string.message_sent));
			Drawable search_unsel = getResources().getDrawable(
					R.drawable.message_sent);
			Rect r = null;
			Drawable[] d = messageViewHolder.sending_status_right
					.getCompoundDrawables();
			r = null;
			if (d[0] != null)
				r = d[0].getBounds();
			if (r != null)
				search_unsel.setBounds(r);
			messageViewHolder.sending_status_right.setBackgroundDrawable(null);
			messageViewHolder.sending_status_right.setCompoundDrawables(
					search_unsel, null, null, null);
		}
		messageViewHolder.sending_status_right.setOnClickListener(this);

		String txt = message.msgTxt;
		if (message.datetime != null) {
			messageViewHolder.messageViewRight.setVisibility(View.VISIBLE);
			if (txt.indexOf("<?xml version=") != -1) {
				txt = "This is System Message";
				messageViewHolder.view_rtml.setVisibility(View.VISIBLE);
				messageViewHolder.view_rtml.setTag(message);
				messageViewHolder.view_rtml.setOnClickListener(this);
			}

			// will remove this login this temp because m not geting
			// notification flag in feed
//			if (txt.startsWith("{")) {
//				Utilities.jsonParserEngine(txt);
//				if (Utilities.accept() != null) {
//					txt = "This is Friend Request";
//					convertView.findViewById(R.message_row_right.friend_invite)
//					.setVisibility(View.VISIBLE);
//				}
//			}
			Spanned spannedContent = getSmiledText(this, txt);
			messageViewHolder.messageViewRight.setText(spannedContent,
					BufferType.SPANNABLE);
			// messageViewHolder.messageViewRight.setText(txt);//+" : "+message.voice_content_urls);
			// messageViewHolder.messageViewRight.setText(txt+" : "+message.sending_state);//
			// +":"+participantInfo.userName+":"+":"+participantInfo.firstName+":"+participantInfo.lastName+":"+participantInfo.profileImageUrl+":"+participantInfo.profileUrl);
			messageViewHolder.messageViewRight
			.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			messageViewHolder.messageViewRight.setVisibility(View.GONE);
		}

		if (message.conversationId != null) {
			messageViewHolder.conversation_idRight.setVisibility(View.VISIBLE);
			// messageViewHolder.conversation_idRight.setText(message.drmFlags+":"+message.notificationFlags+":"+message.mfuId
			// + " : " + message.messageId);

			if ((participantInfo.firstName != null && participantInfo.firstName
					.length() > 0)
					&& (participantInfo.lastName != null && participantInfo.lastName
					.length() > 0))
				messageViewHolder.conversation_idRight
				.setText(participantInfo.firstName + " "
						+ participantInfo.lastName);
			else
				messageViewHolder.conversation_idRight
				.setText(participantInfo.userName);
			messageViewHolder.conversation_idRight
			.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			messageViewHolder.conversation_idRight.setVisibility(View.GONE);
		}
		messageViewHolder.voiceRight.setVisibility(View.GONE);
		messageViewHolder.receving_voiceRight.setVisibility(View.GONE);
		messageViewHolder.receving_voiceRight.setText(getResources().getString(
				R.string.reciving_voice));
		messageViewHolder.receving_voiceRight.setOnClickListener(null);
		messageViewHolder.voiceRight.setOnClickListener(this);
		messageViewHolder.voiceRight.setTag(message);
		if (message.voice_content_urls != null) {
			if (message.audio_download_statue != 1) {
				// System.out.println("-----------time diff-- :"+(System.currentTimeMillis()
				// - message.inserTime));
				if ((System.currentTimeMillis() - message.inserTime) < 1000 * 30) {
					Downloader downloader = Downloader.getInstance();
					downloader.download(message.voice_content_urls, 2, this);
					messageViewHolder.receving_voiceRight
					.setText(getResources().getString(
							R.string.reciving_voice));
					messageViewHolder.receving_voiceRight
					.setVisibility(View.VISIBLE);
					// messageViewHolder.voiceRight.setBackgroundResource(R.drawable.image_for_rotation_green_small);
				} else if (Downloader.links
						.contains(message.voice_content_urls))// else
					// if(Downloader.getInstance().getUrl()!=null
					// &&
					// Downloader.getInstance().getUrl().equalsIgnoreCase(message.voice_content_urls))
				{
					// messageViewHolder.voiceRight.setBackgroundResource(R.drawable.image_for_rotation_green_small);
					messageViewHolder.receving_voiceRight
					.setText(getResources().getString(
							R.string.reciving_voice));
					messageViewHolder.receving_voiceRight
					.setVisibility(View.VISIBLE);
				} else {
					// if (Downloader.getInstance().checkAndUpdate(
					// message.voice_content_urls)) {
					// messageViewHolder.voiceRight
					// .setBackgroundResource(R.drawable.audio_activity);
					// messageViewHolder.voiceRight
					// .setVisibility(View.VISIBLE);
					// } else
					{
						if (Downloader.getInstance().getUrl() != null
								&& Downloader
								.getInstance()
								.getUrl()
								.equalsIgnoreCase(
										message.voice_content_urls))
							messageViewHolder.receving_voiceRight
							.setText(getResources().getString(
									R.string.reciving_voice));
						else
							messageViewHolder.receving_voiceRight
							.setText(getResources().getString(
									R.string.get_voice));
						messageViewHolder.receving_voiceRight
						.setOnClickListener(this);
						messageViewHolder.receving_voiceRight.setTag(message);
						messageViewHolder.receving_voiceRight
						.setVisibility(View.VISIBLE);
					}
				}
				// messageViewHolder.voiceRight.setBackgroundResource(R.drawable.watch);
			}
			// else if(message.audio_download_statue!=1 ){
			// messageViewHolder.receving_voiceRight.setText("  Get Voice 2 ") ;
			// messageViewHolder.receving_voiceRight.setOnClickListener(this) ;
			// messageViewHolder.receving_voiceRight.setTag(message) ;
			// messageViewHolder.receving_voiceRight.setVisibility(View.VISIBLE);
			// }
			// else
			// if(Downloader.links.contains(message.voice_content_urls))//else
			// if(Downloader.getInstance().getUrl()!=null &&
			// Downloader.getInstance().getUrl().equalsIgnoreCase(message.voice_content_urls))
			// {
			// //
			// messageViewHolder.voiceRight.setBackgroundResource(R.drawable.image_for_rotation_green_small);
			// messageViewHolder.receving_voiceRight.setText("Reciving voice...")
			// ;
			// }
			else {
				messageViewHolder.voiceRight
				.setBackgroundResource(R.drawable.audio_activity);
				messageViewHolder.voiceRight.setVisibility(View.VISIBLE);
			}
		}
		ImageDownloader imageManager = new ImageDownloader();
		imageManager.download(participantInfo.userName,
				messageViewHolder.otherImageRight, OtherMessageActivity.this);
		messageViewHolder.otherImageRight.setVisibility(View.VISIBLE);
		messageViewHolder.otherImageRight.setOnClickListener(this);
		messageViewHolder.otherImageRight.setTag(participantInfo);
		// messageViewHolder.otherImageRight.setVisi/bility(View.GONE);
		//
		// int width = getWindowManager().getDefaultDisplay().getWidth() /
		// 2-((16*getWindowManager().getDefaultDisplay().getWidth())/100);
		// int height = width * 3 / 4;
		// int heightPX = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_DIP, height, getResources()
		// .getDisplayMetrics());
		// int weigthPX = (int) TypedValue.applyDimension(
		// TypedValue.COMPLEX_UNIT_DIP, width, getResources()
		// .getDisplayMetrics());

		float sizeInDipHeight = 150f;
		float sizeInDipWeidth = 150f;
		if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_LARGE) {
			sizeInDipHeight = 150f;
			sizeInDipWeidth = 150f;
		} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_MEDIUM) {
			sizeInDipHeight = 150f / 2;
			sizeInDipWeidth = 150f / 2;
		} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_SMALL) {
			sizeInDipHeight = 150f / 2;
			sizeInDipWeidth = 150f / 2;
		}
		int heightPX = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDipHeight, getResources()
				.getDisplayMetrics());
		int weigthPX = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDipWeidth, getResources()
				.getDisplayMetrics());

		LinearLayout.LayoutParams parms = null;// new LinearLayout.LayoutParams(
		// width, heightPX);
		parms = new LinearLayout.LayoutParams(weigthPX, heightPX);
		parms.setMargins(5, 0, 5, 0);

		messageViewHolder.imagesRight.removeAllViews();
		messageViewHolder.imagesRight.setVisibility(View.GONE);
		if (message.image_content_thumb_urls != null
				&& message.image_content_thumb_urls.trim().length() > 0) {
			messageViewHolder.imagesRight.setVisibility(View.VISIBLE);

			// messageViewHolder.imagesRight.removeAllViews();

			// parms.setMargins(5, 5, 5, 5);
			// messageViewHolder.videoRight.setLayoutParams(parms);
			s = Utilities.split(new StringBuffer(
					message.image_content_thumb_urls), Constants.COL_SEPRETOR);
			String ss[] = Utilities.split(new StringBuffer(
					message.image_content_urls), Constants.COL_SEPRETOR);
			for (int j = 0; j < s.length; j++) {
				CImageView b = new CImageView(OtherMessageActivity.this);
				parms.setMargins(5, 0, 5, 0);
				b.setLayoutParams(parms);// new
				// LinearLayout.LayoutParams(getThumbwidht(400),
				// getThumbHeight(450)));

				// b.setBackgroundResource(R.drawable.border);
				if (s[j].trim().startsWith("http")) {// local
					b.setBackgroundResource(R.drawable.border);
					imageManager = new ImageDownloader();
					imageManager.download(s[j], b, OtherMessageActivity.this);
				} else {
					if (s[j].indexOf("/") == -1 && !s[j].startsWith("/")
							&& Integer.parseInt(s[j]) != -1)
						b.setImageBitmap(ImageUtils.getImageFileThumb(
								OtherMessageActivity.this,
								Integer.parseInt(s[j])));
					else
						// b.setImageBitmap(ImageUtils.getImageFileThumb(
						// OtherMessageActivity.this, Integer.parseInt(s[j])));
					{
						Bitmap bm = ImageUtils.getImageFileThumb(
								OtherMessageActivity.this, ImageUtils
								.getImagesFileId(
										OtherMessageActivity.this,
										ss[j]));
						if (bm != null) {
							BitmapDrawable bmd = new BitmapDrawable(bm);
							b.setLayoutParams(parms);

							// b.setImageResource()
							// LinearLayout.LayoutParams parmsnew = new
							// LinearLayout.LayoutParams(
							// LayoutParams.WRAP_CONTENT, heightPX);

							// parms.setMargins(5, 5, 5, 5);
							// b.setBackgroundResource(R.drawable.border);

							b.setImageDrawable(bmd);
							// b.setLayoutParams(parmsnew);
							// System.out.println("create thumb nail===1");
							// b.setBackgroundDrawable(new BitmapDrawable(
							// getResources(), bm));

						} else {
							if (bm == null) {
								BitmapFactory.Options bfo = new BitmapFactory.Options();
								bfo.inSampleSize = 4;
								bm = BitmapFactory.decodeFile(ss[j], bfo);
								bm = ImageUtils.rotateImage(ss[j], bm);
							}
							// System.out.println("create thumb nail===2");
							if (bm != null) {
								bm = Bitmap
										.createScaledBitmap(bm, 60, 60, true);
								// LinearLayout.LayoutParams newparm = new
								// LinearLayout.LayoutParams(
								// ayoutParams.WRAP_CONTENT, heightPX);
								// b.setLayoutParams(newparm);
								b.setLayoutParams(parms);
								b.setImageBitmap(bm);
							}
							// LinearLayout.LayoutParams newparm = new
							// LinearLayout.LayoutParams(
							// LayoutParams.WRAP_CONTENT, heightPX);
							// parms.setMargins(5, 5, 5, 5);
							// b.setLayoutParams(parms);
							// b.setBackgroundDrawable(new BitmapDrawable(
							// getResources(), bm));
						}
					}
				}
				messageViewHolder.imagesRight.addView(b);
				b.setTag(message);
				b.setOnClickListener(this);
				b.setId(1000);
			}
		}
		// LinearLayout.LayoutParams parmsvid = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, heightPX);
		// parmsvid.setMargins(5, 0, 5, 0);
		messageViewHolder.videoRight.setVisibility(View.GONE);
		messageViewHolder.videoRight.removeAllViews();
		// if (message.video_content_thumb_urls != null
		// && message.video_content_thumb_urls.trim().length() > 0) {
		// messageViewHolder.imagesRight.setVisibility(View.VISIBLE);
		// //System.out.println("---------------message.video_content_thumb_urls : "+message.video_content_thumb_urls);
		// if (message.video_content_thumb_urls.trim().startsWith("http")) {//
		// local
		// imageManager = new ImageDownloader();
		// imageManager.download(message.video_content_thumb_urls,
		// messageViewHolder.videoRight,
		// OtherMessageActivity.this);
		// } else {
		// Bitmap bm = ImageUtils.getVideoFileThumb(
		// OtherMessageActivity.this, ImageUtils.getVideoFileId(
		// OtherMessageActivity.this,
		// message.video_content_thumb_urls));
		// if (bm != null)
		// messageViewHolder.videoRight
		// .setBackgroundDrawable(new BitmapDrawable(
		// getResources(), bm));
		// // else
		// messageViewHolder.videoRight
		// .setImageResource(R.drawable.video_overlay);
		// }
		// messageViewHolder.videoRight.setLayoutParams(parmsvid);
		// messageViewHolder.videoRight.setVisibility(View.VISIBLE);
		// messageViewHolder.videoRight.setTag(message);
		// messageViewHolder.videoRight.setOnClickListener(this);
		// }

		// parms = new LinearLayout.LayoutParams(getThumbwidht(500),
		// getThumbHeight(450));
		// parms.setMargins(5, 0, 5, 0);
		messageViewHolder.videoRight.setVisibility(View.GONE);
		if (message.video_content_thumb_urls != null
				&& message.video_content_thumb_urls.trim().length() > 0) {
			messageViewHolder.videoRight.setVisibility(View.VISIBLE);
			// System.out.println("----------- ideo thumb : "+message.video_content_thumb_urls);
			s = Utilities.split(new StringBuffer(
					message.video_content_thumb_urls), Constants.COL_SEPRETOR);
			String[] ss = Utilities.split(new StringBuffer(
					message.video_content_urls), Constants.COL_SEPRETOR);
			if (message.video_size == null)
				message.video_size = "";
			String[] size = Utilities.split(
					new StringBuffer(message.video_size),
					Constants.COL_SEPRETOR);
			for (int j = 0; j < ss.length; j++) {
				RelativeLayout l1 = new RelativeLayout(this);
				parms.setMargins(5, 0, 5, 0);
				l1.setLayoutParams(parms);// new
				// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT))
				// ;
				// l1.setOrientation(LinearLayout.VERTICAL) ;
				CImageView b = new CImageView(OtherMessageActivity.this);
				// parms.setMargins(5, 5, 5, 5);
				// b.setLayoutParams(new
				// LinearLayout.LayoutParams(getThumbwidht(500),
				// getThumbHeight(320)));

				b.setBackgroundResource(R.drawable.border);
				// LinearLayout frame = new
				// LinearLayout(OtherMessageActivity.this);
				// parms.setMargins(5, 5, 5, 5);
				// frame.setLayoutParams(parms);
				// b.setLayoutParams(parms);

				// b.setBackgroundResource(R.drawable.photo_frame);
				// frame.setBackgroundResource(R.drawable.photo_frame);//nage
				// tl.addView(frame);
				if (s[j].trim().startsWith("http")) {
					imageManager = new ImageDownloader();
					imageManager.download(s[j], b, OtherMessageActivity.this);
				} else {
					Bitmap bm = ImageUtils.getVideoFileThumb(
							OtherMessageActivity.this, ImageUtils
							.getVideoFileId(OtherMessageActivity.this,
									s[j]));
					if (bm != null)
						b.setBackgroundDrawable(new BitmapDrawable(
								getResources(), bm));
					// else
					// b
					// .setImageResource(R.drawable.video_overlay);
					// parms =new LinearLayout.LayoutParams(getThumbwidht(500),
					// getThumbHeight(320));
					// parms.setMargins(5, 0, 5, 0);
					l1.setLayoutParams(parms);
				}
				b.setTag(message);
				b.setScaleType(ScaleType.FIT_CENTER);
				b.setOnClickListener(null);
				l1.addView(b);
				Button btn = new Button(this);
				VideoUrl videoUrl = new VideoUrl();
				videoUrl.url = ss[j];
				videoUrl.index = (j + 1);
				if (s[j].trim().startsWith("http"))
					btn.setText(getResources().getString(R.string.get_video)
							+ "["
							+ Utilities.humanReadableByteCount(
									Long.parseLong(size[j]), true) + "]");
				videoUrl.acaton = 0;
				if (s[j].trim().startsWith("http")) {
					if (VDownloader.getInstance().getUrl() != null
							&& VDownloader.getInstance().getUrl()
							.equalsIgnoreCase(ss[j])) {
						btn.setText(getResources().getString(
								R.string.reciving_video));
						videoUrl.acaton = 1;
					} else if (VDownloader.getInstance().check(ss[j])) {
						b.setOnClickListener(this);
						b.setId(100012);
						b.setTag(ss[j]);
						btn.setVisibility(View.GONE);
						// parms =new
						// LinearLayout.LayoutParams(getThumbwidht(500),
						// getThumbHeight(320));
						// parms.setMargins(5, 0, 5, 0);
						l1.setLayoutParams(parms);

					}
				} else {
					b.setOnClickListener(this);
					b.setId(100012);
					b.setTag(ss[j]);
					btn.setVisibility(View.GONE);
					// parms =new LinearLayout.LayoutParams(getThumbwidht(500),
					// getThumbHeight(320));
					// parms.setMargins(5, 0, 5, 0);
					l1.setLayoutParams(parms);
				}
				btn.setBackgroundResource(R.drawable.unfollow);
				btn.setTextColor(Color.WHITE);
				l1.addView(btn);
				btn.setId(100011);
				btn.setOnClickListener(this);

				btn.setTag(videoUrl);

				b.setImageResource(R.drawable.video_overlay);

				messageViewHolder.videoRight.addView(l1);
				// b.setId(1000*3049);
			}
		}

	}

	@Override
	public void onResponseSucess(final String responseStr,
			final int requestForID) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// if (Constants.FEED_GET_BOOKMARK_MESSAGES == requestForID)
				try {

					if ((responseStr != null && responseStr.indexOf("REFRESH") != -1)
							|| FeedTask.UPDATE_ME || ComposeService.refreshList)
						// if(FeedTask.UPDATE_ME || ComposeService.refreshList)
					{
						// System.out.println("------onResponseSucess--"+responseStr+"   :f "+FeedTask.refreshList);
						// System.out.println("------onResponseSucess--"+responseStr+"   :c "+ComposeService.refreshList);
						activityAdapter.getCursor().requery();
						// activityAdapter.getCursor().moveToLast();
						activityAdapter.notifyDataSetInvalidated();
						// if(FeedTask.UPDATE_ME && !FeedTask.KEEP_POSITION)
						// listViewActivity
						// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
						if (!FeedTask.KEEP_POSITION)
							listViewActivity
							.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
						FeedTask.KEEP_POSITION = false;
						if (activityAdapter != null) {
							activityAdapter.getCursor().requery();
							activityAdapter.notifyDataSetChanged();
						}
						listViewActivity.invalidate();

						if (activityAdapter.getCursor().getCount() > 0) {
							load_prevoius_message.setVisibility(View.GONE);
						} else {
							load_prevoius_message.setVisibility(View.VISIBLE);
							load_prevoius_message.setText("Your notification box is empty");
						}

					}
					// if(load_prevoius_message!=null)
					// load_prevoius_message.setText(getResources().getString(R.string.load_prevoius_message));
					// else
					// {
					// load_prevoius_message= ((TextView)
					// findViewById(R.conversation.load_prevoius_message));
					// load_prevoius_message.setText(getResources().getString(R.string.load_prevoius_message));
					// }
					// if(activityAdapter!=null){
					// activityAdapter.getCursor().close() ;
					// }
					// activityAdapter = new
					// SystemMessageAdapter(OtherMessageActivity.this,
					// getCursor(), true,
					// OtherMessageActivity.this);
					// // if(count>0)
					// listViewActivity.setAdapter(activityAdapter);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	@Override
	public void onResponseSucess(Object responseStr, final int requestForID) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if ("user".hashCode() == requestForID) {
					// listViewActivity.onRefreshComplete();
					final int oldPos = listViewActivity
							.getFirstVisiblePosition();
					if (activityAdapter != null) {
						activityAdapter.getCursor().close();
					}
					activityAdapter = new SystemMessageAdapter(
							OtherMessageActivity.this, getCursor(), true,
							OtherMessageActivity.this);
					listViewActivity.setAdapter(activityAdapter);

					if (oldPos > 1)
						listViewActivity.setSelection(oldPos);
				}
			}
		});
	}

	@Override
	public void viewConversations(View convertView, Context context,
			ConversationList conversationList) {
	}

	protected void sendDeleteMessageRequest(String aMessageID) {
		try {
			OutboxTblObject newRequest = new OutboxTblObject(1,
					Constants.FRAME_TYPE_INBOX_DEL,
					Constants.MSG_OP_MULTIPLE_DELETE);
			if (Logger.ENABLE) {
				Logger.info(TAG,
						"sendDeleteMessageRequest-- DELETE MESSAGE ID = "
								+ aMessageID);
			}
			newRequest.mMessageIDString = aMessageID;
			int ret = BusinessProxy.sSelf.sendToTransport(newRequest);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void action(int ids[], String[] name, final byte type,
			int[] idsMenuOptionsIcon) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, true,name.length);

		for (int i = 0; i < name.length; i++) {
			ActionItem nextItem = new ActionItem(ids[i], name[i],
					getResources().getDrawable(idsMenuOptionsIcon[i]));
			quickAction.addActionItem(nextItem);
		}

		quickAction
		.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {
				switch (actionId) {
				case ID_ADD_TO_CONVERSATION:
//					Intent intent = new Intent(
//							OtherMessageActivity.this,
//							BuddyActivity.class);
//					intent.putExtra(BuddyActivity.SCREEN_MODE,
//							BuddyActivity.MODE_ONLY_FRIENDS);
//					intent.putExtra("MULTISELECTED", true);
//					intent.putExtra("ADDCONVERSATIONS", true);
//					startActivityForResult(intent, 1);
					break;
				case ID_LEAVE_CONVERSATION:
					request = new Request(ID_LEAVE_CONVERSATION, "url");
					if (!OtherMessageActivity.this
							.checkInternetConnection()) {
						OtherMessageActivity.this.networkLossAlert();
						return;
					}
					request.execute("LikeDislike");
					break;
				case ID_UPDATE_SUBJECT:
					// final InboxMessage element = inboxElement;
					dialog = new Dialog(
							OtherMessageActivity.this,
							android.R.style.Theme_Translucent_NoTitleBar);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.save_msg_dialog);
					dialog.setCancelable(true);

					((TextView) dialog
							.findViewById(R.password_dialog.message)).setText(OtherMessageActivity.this
									.getString(R.string.add_conversation_subject));
					Button okButton = (Button) dialog
							.findViewById(R.id.save);
					okButton.setText(OtherMessageActivity.this
							.getString(R.string.update));
					okButton.setOnClickListener(new OnClickListener() {
						public void onClick(View view) {
							Utilities.closeSoftKeyBoard(view,
									OtherMessageActivity.this);
							dialog.dismiss();
							String value = ((EditText) dialog
									.findViewById(R.id.tag)).getText()
									.toString();
							request = new Request(ID_UPDATE_SUBJECT,
									"url");

							request.subject = value;
							if (!OtherMessageActivity.this
									.checkInternetConnection()) {
								OtherMessageActivity.this
								.networkLossAlert();
								return;
							}
							request.execute("LikeDislike");
						}
					});
					okButton = (Button) dialog
							.findViewById(R.id.cancel);
					okButton.setOnClickListener(new OnClickListener() {
						public void onClick(View view) {
							Utilities.closeSoftKeyBoard(view,
									OtherMessageActivity.this);
							dialog.dismiss();

						}
					});
					dialog.show();
					break;
				case DELETE_CONVERSATION:
					request = new Request(DELETE_CONVERSATION, "url");
					if (!OtherMessageActivity.this
							.checkInternetConnection()) {
						OtherMessageActivity.this.networkLossAlert();
						return;
					}
					request.execute("LikeDislike");

					break;
				case ID_DELETE_MESSAGE:
					if (!OtherMessageActivity.this
							.checkInternetConnection()) {
						OtherMessageActivity.this.networkLossAlert();
						return;
					}
					request = new Request(ID_DELETE_MESSAGE, "url");
					request.execute("LikeDislike");

					break;
				case ID_SAVE_MESSAGE:
					dialog = new Dialog(
							OtherMessageActivity.this,
							android.R.style.Theme_Translucent_NoTitleBar);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.save_msg_dialog);
					dialog.setCancelable(true);

					// ((TextView)
					// dialog.findViewById(R.password_dialog.message)).setText(OtherMessageActivity.this.getString(R.string.add_conversation_subject))
					// ;
					okButton = (Button) dialog.findViewById(R.id.save);
					// okButton.setText(OtherMessageActivity.this.getString(R.string.update));
					okButton.setOnClickListener(new OnClickListener() {
						public void onClick(View view) {
							Utilities.closeSoftKeyBoard(view,
									OtherMessageActivity.this);
							dialog.dismiss();
							String value = ((EditText) dialog
									.findViewById(R.id.tag)).getText()
									.toString();

							if (value != null && value.length() > 0) {
								// value =
								// String.format("SaveMessage::Messageid##%s::Tag##%s",
								// message.messageId, value);
								// if
								// (BusinessProxy.sSelf.sendNewTextMessage(Constants.AGENT_MESSAGE_MANAGER,
								// value, true)) {
								// showSimpleAlert("RockeTalk",
								// "Your message has been saved");
								// }

								if (!OtherMessageActivity.this
										.checkInternetConnection()) {
									OtherMessageActivity.this
									.networkLossAlert();
									return;
								}

								request = new Request(ID_SAVE_MESSAGE,
										"url");
								request.subject = value;
								request.execute("LikeDislike");
							} else
								showSimpleAlert("RockeTalk",
										"Please enter tag to save message!");
						}
					});
					okButton = (Button) dialog
							.findViewById(R.id.cancel);
					okButton.setOnClickListener(new OnClickListener() {
						public void onClick(View view) {
							Utilities.closeSoftKeyBoard(view,
									OtherMessageActivity.this);
							dialog.dismiss();

						}
					});
					dialog.show();
					break;
				}
			}
		});

		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				// Toast.makeText(getApplicationContext(), "Dismissed",
				// Toast.LENGTH_SHORT).show();
			}
		});
	}

	// http://http://124.153.95.174:28080/tejas/rest/rtmessaging/postMessage
	public String quickSend() {
		String d = ((EditText) findViewById(R.community_chat.msgBox)).getText()
				.toString();

		// ///////////

		int attachemtnt = 0;
		long attachemtntsize = d.length();
		if (mPicturePath != null && mPicturePath.size() > 0) {

			attachemtnt = attachemtnt + mPicturePath.size();
		}

		if (mVideoPath != null && mVideoPath.length() > 0) {

			attachemtnt = attachemtnt + 1;
		}

		if (mVoicePath != null) {

			attachemtnt = attachemtnt + 1;
		}

		ContentValues values = new ContentValues();
		values.put(MediaPostTable.DATE, System.currentTimeMillis());
		values.put(MediaPostTable.ATTACHMENT, attachemtnt);
		// values.put(MediaPostTable.TAG, tag);
		values.put(MediaPostTable.MEDIS_STAUTS, MediaPostTable.STATUS_QUEUE);
		values.put(MediaPostTable.ABOUT, d.trim());
		// values.put(MediaPostTable.MODE, mode);
		values.put(MediaPostTable.TRY, 0);

		// values.put(MediaPostTable.CATEGORY, catId);

		if (mPicturePath != null && mPicturePath.size() > 0) {
			if (mPicturePath != null && mPicturePath.size() > 0) {
				for (int i = 0; i < mPicturePath.size(); i++) {
					attachemtntsize = attachemtntsize
							+ ImageUtils.getImageSize(mPicturePath.get(i));
					values.put("IMAGE_" + (i + 1), mPicturePath.get(i));
				}
			}
		}

		if (mVideoPath != null && mVideoPath.length() > 0) {
			if (mVideoPath != null && mVideoPath.length() > 0) {
				// for (int i = 0; i < mVideoPath.length(); i++)
				{
					attachemtntsize = attachemtntsize
							+ ImageUtils.getImageSize(mVideoPath);
					values.put(MediaPostTable.VIDEO, mVideoPath);
				}
			}
		}

		if (mVoicePath != null) {
			values.put(MediaPostTable.AUDIO, mVoicePath);
		}
		values.put(MediaPostTable.ATTACHMENT_SIZE, attachemtntsize);

		values.put(MediaPostTable.USER_ID, BusinessProxy.sSelf.getUserId());
		values.put(MediaPostTable.USER_PASS, SettingData.sSelf.getPassword());
		values.put(MediaPostTable.CLIENT_ID, BusinessProxy.sSelf.getClientId());
		values.put(MediaPostTable.USER_NAME, SettingData.sSelf.getUserName());
		// public static final String IMAGE_1 = "IMAGE_1";
		// public static final String IMAGE_1_STATUS = "IMAGE_1_STATUS";

		getContentResolver().insert(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, values);

		((EditText) findViewById(R.community_chat.msgBox)).post(new Runnable() {

			@Override
			public void run() {
				((EditText) findViewById(R.community_chat.msgBox)).setText("");
			}
		});
		String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
				.getText().toString();
		Message message = new Message();
		message.msgTxt = d;
		message.messageId = "" + (System.currentTimeMillis() - 10000);
		message.mfuId = "-1";
		message.datetime = "" + (new Date()).toString();
		message.clientTransactionId = "" + (new Date()).toString();
		message.conversationId = getIntent().getStringExtra(
				Constants.CONVERSATION_ID);
		if (mVoicePath != null) {

			message.voice_content_urls = mVoicePath;

		}
		saveMessage(message);

		checkAndSend();
		return "fail";
	}

	public String send(MediaPost mediaPost) {

		// http://124.153.95.174:28080/tejas/rest/rtmessaging/postMessage
		String urlStr = "http://" + Urls.TEJAS_HOST
				+ "/tejas/rest/rtmessaging/postMessage";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(urlStr);

		String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
				.getText().toString();
		Message message = new Message();
		message.msgTxt = mediaPost.mediaText;
		message.messageId = "" + (System.currentTimeMillis() - 10000);
		message.mfuId = "-1";
		message.datetime = "" + (new Date()).toString();
		message.clientTransactionId = "" + (new Date()).toString();
		message.conversationId = getIntent().getStringExtra(
				Constants.CONVERSATION_ID);

		MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.STRICT);

		// <input type="text" name="usrId" value="10253730" />
		// <input type="text" name="devId" value="1116" />
		// <input type="text" name="ltMFUId" value="28486" />
		// <input type="text" name="msgType" value="1" />
		// <input type="text" name="msgTxt" value="suresh" />
		// <input type="text" name="vndr" value="TOMO" />
		// <input type="text" name="dest" value="Qts" />
		// <input type="text" name="msgOp" value="7" />
		// <input type="text" name="reqId" value="8950819552855773454" />
		// <input type="text" name="reqType" value="2_7" />

		try {
			// mpEntity.addPart("data", new FileBody(file,
			// "application/octet-stream"));
			mpEntity.addPart("usrId",
					new StringBody("" + BusinessProxy.sSelf.getUserId()));
			// mpEntity.addPart("usrNm", new StringBody("1"));
			mpEntity.addPart("devId", new StringBody("1116"));
			mpEntity.addPart("ltMFUId", new StringBody("28486"));
			mpEntity.addPart("msgType", new StringBody("1"));
			mpEntity.addPart("msgOp", new StringBody("7"));
			if (dist == null || dist.length() <= 0)
				mpEntity.addPart("dest", new StringBody("qts"));

			else
				mpEntity.addPart("dest", new StringBody(getParticipantInfo()));

			mpEntity.addPart("msgTxt", new StringBody("1"));
			// mpEntity.addPart("cltTxnId", new
			// StringBody(message.clientTransactionId));
			mpEntity.addPart("msgTxt", new StringBody(mediaPost.mediaText));
			mpEntity.addPart("Vndr", new StringBody("TOMO"));
			mpEntity.addPart("reqId", new StringBody("8950819552855773454"));
			mpEntity.addPart("reqType", new StringBody("2_7"));
			// mpEntity.addPart("fwdMsgId", new StringBody("1"));
			mpEntity.addPart("convId", new StringBody(getIntent()
					.getStringExtra(Constants.CONVERSATION_ID)));

			if (mediaPost.mVoicePath != null
					&& mediaPost.mVoicePath.contentPath != null) {
				File file = new File(mediaPost.mVoicePath.contentPath);
				mpEntity.addPart("file", new FileBody(file,
						"application/octet-stream"));
			}
			if (mediaPost.mImagesPath != null
					&& mediaPost.mImagesPath.size() > 0) {
				for (int i = 0; i < mediaPost.mImagesPath.size(); i++) {
					MediaPost.MediaContent mc = mediaPost.mImagesPath.get(i);
					File file = new File(mc.contentPath);
					mpEntity.addPart("file", new FileBody(file,
							"application/octet-stream"));
				}
			}
			File file = new File("/mnt/sdcard/1362745550363.jpg");
			// mpEntity.addPart("file", new FileBody(file,
			// "application/octet-stream"));
			// file = new File("/mnt/sdcard/hello2.amr");
			// mpEntity.addPart("file", new FileBody(file,
			// "application/octet-stream"));
			// /mnt/sdcard/1362745550363.jpg
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		httppost.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());

		httppost.setHeader(
				"RT-APP-KEY",
				HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(),
						SettingData.sSelf.getPassword()));
		httppost.setHeader("password", HttpHeaderUtils.encriptPass(

				SettingData.sSelf.getPassword()));
		httppost.setHeader("authkey", "RTAPP@#$%!@");
		httppost.setEntity(mpEntity);

		HttpResponse response;
		try {

			response = httpclient.execute(httppost);

			HttpEntity resEntity = response.getEntity();

			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);

			if (resEntity != null) {

			}
			if (resEntity != null) {
				resEntity.consumeContent();
			}
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
					// columns
					// to
					// update
					MediaPostTable.COLUMN_ID + "=" + mediaPost.DB_ID, // the
					// column
					// to
					// select
					// on
					null // the value to compare to
					);
			checkAndSend();
			return responseString;// "sucess" ;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "fail";
	}

	final protected DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER_SUB = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			// System.out.println("-----------PROGRESS_CANCEL_HANDLER_SUB-------------");
			if (request != null && !request.isCancelled()) {
				request.cancel(true);
			}
		}
	};

	boolean cancelOperation;

	private HttpResponse getResponse(final String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader(
				"RT-APP-KEY",
				HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(),
						SettingData.sSelf.getPassword()));
		HttpResponse response = null;
		try {
			// System.out.println("----------httpget------"+httpget);
			response = client.execute(httpget);
			if (cancelOperation)
				return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private HttpResponse getPostResponse(final String url) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpget = new HttpPost(url);
		httpget.setHeader(
				"RT-APP-KEY",
				HttpHeaderUtils.createRTAppKeyHeader(
						BusinessProxy.sSelf.getUserId(),
						SettingData.sSelf.getPassword()));
		httpget.setHeader("Content-Type", "application/xml");
		HttpResponse response = null;
		try {
			// System.out.println("----------httpget------"+httpget);
			response = client.execute(httpget);
			if (cancelOperation)
				return null;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	Request request;

	public class Request extends AsyncTask<String, Integer, String> {

		int type = 0;
		String url;
		String actorUserid = "";
		String subject = "";

		public Request(int type, String url) {
			this.type = type;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			if (type != 1000)
				rTDialog = ProgressDialog.show(OtherMessageActivity.this, "", getString(R.string.updating), true);
//				showAnimationDialogNonUIThread("",
//						"Sending request please wait", true,
//						PROGRESS_CANCEL_HANDLER_SUB);
		}

		protected void onPostExecute(String result) {
			if (result != null && result.trim().equalsIgnoreCase("true")
					&& this.type == DELETE_CONVERSATION) {
				rTDialog.dismiss() ;
				finish();
				return;
			}
			if (type != 1000) {
				rTDialog.dismiss() ;
				DialogInterface.OnClickListener handler = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						lDialog.dismiss();
					}
				};

				if (result != null) {
					if (result != null && result.trim().equalsIgnoreCase("1")) {
						switch (this.type) {
						case ID_DELETE_MESSAGE:
							result = getString(R.string.you_have_successfully_deleted_this_message);
							break;
						}
					} else {
						Hashtable<String, String> err = Utilities.parseError(result);
						if (err.containsKey("message"))
							result = err.get("message");
					}
				}
				if (result != null) {
//					showAlertMessage("RockeTalk", result,
//							new int[] { DialogInterface.BUTTON_POSITIVE },
//							new String[] { "Ok" },
//							new DialogInterface.OnClickListener[] { handler });
					String desc = getString(R.string.you_have_successfully_deleted_this_message);
				try {
					JSONObject jsonObj = new JSONObject(result);
					 desc  =  jsonObj.getString("desc");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
					new AlertDialog.Builder(OtherMessageActivity.this).setMessage(desc)
					.setNegativeButton(R.string.ok, null).show();
				} else{
//						showSimpleAlert("Error", "Please try after sometime!!!");
					new AlertDialog.Builder(OtherMessageActivity.this).setMessage(getString(R.string.please_try_after_some_times))
					.setNegativeButton(R.string.ok, null).show();
				}
			}

		}

		@Override
		protected String doInBackground(String... params) {
			try {
				switch (type) {
				case 1000:
					break;
				case ID_ADD_TO_CONVERSATION:
					URLConnection connection = new URL("http://" + Urls.TEJAS_HOST + "/tejas/rest/rtmessaging/addToConversation").openConnection();
					connection.setDoOutput(true);
					String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationParticipationRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId><actorUserId>#ACTORUSERID#</actorUserId></conversationParticipationRequest>";
					content = content.replace("#USERID#", ""+ BusinessProxy.sSelf.getUserId());
					content = content.replace("#ACTORUSERID#", actorUserid);
					content = content.replace("#CONVERSATIONID#", getIntent().getStringExtra(Constants.CONVERSATION_ID));
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("password", HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					OutputStream output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					return Utilities.readStream(connection.getInputStream());
				case ID_LEAVE_CONVERSATION:
					connection = new URL("http://" +Urls. TEJAS_HOST + "/tejas/rest/rtmessaging/leaveConversation").openConnection();
					connection.setDoOutput(true);
					content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationParticipationRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId></conversationParticipationRequest>";
					content = content.replace("#USERID#", ""+ BusinessProxy.sSelf.getUserId());
					content = content.replace("#CONVERSATIONID#", getIntent().getStringExtra(Constants.CONVERSATION_ID));
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("password", HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					return Utilities.readStream(connection.getInputStream());
				case ID_UPDATE_SUBJECT:
					connection = new URL("http://" + Urls.TEJAS_HOST + "/tejas/rest/rtmessaging/updateSubject").openConnection();
					connection.setDoOutput(true);
					content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationSubjectRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId><subject>#SUBJECT#</subject></conversationSubjectRequest>";
					content = content.replace("#USERID#", ""+ BusinessProxy.sSelf.getUserId());
					content = content.replace("#SUBJECT#", subject);
					content = content.replace("#CONVERSATIONID#", getIntent().getStringExtra(Constants.CONVERSATION_ID));
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("password", HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					return Utilities.readStream(connection.getInputStream());
				case DELETE_CONVERSATION:
					content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<deleteConversationsRequest>"
							+ " �<isDeleteAll>true</isDeleteAll>"
							+ " �<userId>"
							+ BusinessProxy.sSelf.getUserId()
							+ "</userId>"
							+ " <conversationIds>"
							+ " �<conversationIds>"
							+ getIntent().getStringExtra(
									Constants.CONVERSATION_ID)
									+ "</conversationIds>" + " </conversationIds>"
									+ "</deleteConversationsRequest>";
					connection = new URL("http://" +Urls. TEJAS_HOST + "/tejas/rest/rtmessaging/deleteConversations").openConnection();
					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("password", HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					String s = Utilities.readStream(connection.getInputStream());
					if (s != null && s.trim().equalsIgnoreCase("1")) {
						// MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST
						deleteConversation(getIntent().getStringExtra(Constants.CONVERSATION_ID), false);
					}
					return s;
				case ID_SAVE_MESSAGE:
					// http://124.153.95.174:28080/tejas/rest/rtmessaging/bookmarkMessage
					// BookmarkMessagesRequest
					// <?xml version="1.0" encoding="utf-8"?>
					// <bookmarkMessagesRequest>
					// <messageId>1</messageId>
					// <userId>104</userId>
					// <tag>string</tag>
					// </bookmarkMessagesRequest>
					content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<bookmarkMessagesRequest>"

							+ "<userId>" + BusinessProxy.sSelf.getUserId()
							+ "</userId>" + "<tag>" + subject + "</tag>"
							+ " �<messageId>" + message.messageId
							+ "</messageId>"

							+ "</bookmarkMessagesRequest>";

					connection = new URL("http://" + Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/bookmarkMessage")
					.openConnection();
					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Type",
							"application/xml");
					connection.setRequestProperty("password",
							HttpHeaderUtils.encriptPass(

									SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					output = connection.getOutputStream();

					output.write(content.getBytes());
					output.close();
					s = Utilities.readStream(connection.getInputStream());
					if (s != null && s.trim().equalsIgnoreCase("1")) {
						showToast("Message saved !");
					}
					return s;

				case ID_DELETE_MESSAGE:
				/*	content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<deleteMessageRequest>"
							+ " �<isDeleteAll>false</isDeleteAll>"
							+ " �<userId>" + BusinessProxy.sSelf.getUserId()
							+ "</userId>" + " <messageIds>" + " �<messageIds>"
							+ message.messageId + "</messageIds>"
							+ " </messageIds>" + "</deleteMessageRequest>";
					// http://124.153.95.165:28080/tejas/rest/rtmessaging/deleteMessages
					connection = new URL("http://" + Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/deleteMessages")
					.openConnection();
					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Type",
							"application/xml");
					connection.setRequestProperty("password",
							HttpHeaderUtils.encriptPass(

									SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					output = connection.getOutputStream();

					output.write(content.getBytes());
					output.close();*/
					url = "http://" + Urls.TEJAS_HOST
					+ "/tejas/feeds/message/deletemfubyid?id="
					+ message.mfuId;
					
					URL connect = new URL(url);
					byte[] data = new byte[2048];
					int len = 0;
					Log.i(TAG, "Final Delete URL, with messages - " + url);
					HttpURLConnection conn = (HttpURLConnection) connect
							.openConnection();
					conn.setRequestProperty("RT-APP-KEY", HttpHeaderUtils
							.createRTAppKeyHeader(
									BusinessProxy.sSelf.getUserId(),
									SettingData.sSelf.getPassword()));
					conn.setReadTimeout(10000);
					conn.setConnectTimeout(15000);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					s = readResponse(is);
					
				//	s = Utilities.readStream(connection.getInputStream());
					// System.out.println("-------response content------"+content);
					// System.out.println("-------response delete------"+s);
					//if (s != null && s.trim().equalsIgnoreCase("1")) {
					if (s != null && s.contains("Message deleted")) {
						getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = ? ",
								new String[] { message.messageId });
						// activityAdapter.getCursor().requery();
						// activityAdapter.notifyDataSetChanged() ;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								activityAdapter.getCursor().requery();
								activityAdapter.notifyDataSetChanged();

								if (activityAdapter.getCursor().getCount() > 0) {
									load_prevoius_message
									.setVisibility(View.GONE);
								} else {
									load_prevoius_message
									.setVisibility(View.VISIBLE);
									load_prevoius_message
									.setText("Your notification box is empty");
								}
							}
						});
					}
					return s;

				}

				// if (url == null)
				// return null;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Thanks for using this option";
		}

	}
	private static String readResponse(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = new byte[2048];
		int len = 0;
		while ((len = is.read(data, 0, data.length)) >= 0) {
			bos.write(data, 0, len);
		}
		is.close();
		return new String(bos.toByteArray(), "UTF-8");
	}
	private void openPlayScreen(final String url, boolean autoPlay,
			final LinearLayout ln) {
		TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
		tv.setText("Please wait while getting audio...");
		RTMediaPlayer.setUrl(null);
		tv.setTextColor(0xff6AABB4);
		if (!autoPlay) {
			ImageView imageView1 = (ImageView) ln.findViewById(R.id.media_play);
			// imageView1.setTag(clickId+"^"+url);
			imageView1.setTag("PLAY");
			imageView1.setVisibility(View.INVISIBLE);
			imageView1.setOnClickListener(playerClickEvent);
		}
		RTMediaPlayer.setProgressBar(baradd);
		RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

			@Override
			public void voiceRecordingStarted(String recordingPath) {
			}

			@Override
			public void voiceRecordingCompleted(String recordedVoicePath) {
				// TODO Auto-generated method stub

			}

			public void voicePlayStarted() {

				try {
					ln.findViewById(R.id.streemStatus).setVisibility(View.GONE);
					LinearLayout d = ((LinearLayout) ln
							.findViewById(R.id.media_play_layout));
					d.setVisibility(View.VISIBLE);
					ImageView imageView1 = (ImageView) ln
							.findViewById(R.id.media_close);// media_play
					// imageView1.setImageResource(R.drawable.pause);
					if (imageView1 != null) {
						imageView1.setVisibility(View.INVISIBLE);
						imageView1.setOnClickListener(playerClickEvent);
						imageView1.setTag("PAUSE");
					}
					imageView1 = (ImageView) ln.findViewById(R.id.media_play);

					// imageView1.setTag(clickId+"^"+url);
					if (imageView1 != null) {
						imageView1.setOnClickListener(playerClickEvent);
						imageView1.setTag("PLAY");
						imageView1.setBackgroundResource(R.drawable.addpause);
						imageView1.setTag("PAUSE");
						imageView1.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			public void voicePlayCompleted() {
				//				OtherMessageActivity.this.runOnUiThread(new Runnable() {
				//
				//					@Override
				//					public void run() {
				//						// closePlayScreen();
				//						try {
				//							ImageView imageView1 = (ImageView) ln
				//									.findViewById(R.id.media_play);
				//							imageView1
				//									.setBackgroundResource(R.drawable.addplay);
				//							imageView1.setTag("PLAY");
				//							RTMediaPlayer.reset();
				//							media_play_layout.setVisibility(View.GONE);
				//							RTMediaPlayer.clear();
				//							resetProgress();
				//						} catch (Exception e) {
				//							// TODO: handle exception
				//						}
				//					}
				//				});
			}

			public void onError(int i) {
				try {
					TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
					tv.setTextColor(0xffff0000);
					tv.setText("Unable to play please try later!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onDureationchanged(final long total, final long current) {
				OtherMessageActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {

							total_autio_time.setText(""
									+ Utilities.converMiliSecond(total) + ")");
							total_autio_time.setTypeface(Typeface.DEFAULT_BOLD);
							played_autio_time.setText("("
									+ Utilities.converMiliSecond(current)
									+ " of ");
							played_autio_time
							.setTypeface(Typeface.DEFAULT_BOLD);
						} catch (Exception e) {
							// if(isAudio)
							RTMediaPlayer.reset();
						}
					}
				});
			}
		});
		ln.findViewById(R.id.streemStatus).setVisibility(View.VISIBLE);
		// if(autoPlay)
		RTMediaPlayer._startPlay(url);
		ln.findViewById(R.id.media_play_layout).setVisibility(View.VISIBLE);
	}

	private View.OnClickListener playerClickEvent = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.media_play:
				ImageView imageView1 = null;
				try {

					imageView1 = (ImageView) media_play_layout
							.findViewById(R.id.media_play);
					if (((String) v.getTag()).equals("PLAY")) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {

									// baradd.setTag(new Stats(object)) ;
									TextView tv = (TextView) media_play_layout
											.findViewById(R.id.streemStatus);
									tv.setTextColor(0xff6AABB4);
									if (RTMediaPlayer.getUrl() != null)
										RTMediaPlayer.start();
									else {
										try {
											//
											RTMediaPlayer._startPlay(v.getTag()
													.toString());

										} catch (Exception e) {
											// e.print
										}
									}
								} catch (Exception e) {
								}
							}
						}).start();
						imageView1.setBackgroundResource(R.drawable.addpause);
						imageView1.setTag("PAUSE");

					} else if (((String) v.getTag()).equals("PAUSE")) {
						imageView1.setBackgroundResource(R.drawable.addplay);
						imageView1.setTag("PLAY");
						RTMediaPlayer.pause();
						imageView1 = (ImageView) media_play_layout
								.findViewById(R.id.media_pause_play);
						imageView1.setVisibility(View.INVISIBLE);
					}
				} catch (Exception e) {
				}
				break;
			case R.id.media_stop_play:
				//				media_play_layout.setVisibility(View.GONE);
				//				RTMediaPlayer.reset();
				//				RTMediaPlayer.clear();
				break;
			}
		}
	};

	public void resetProgress() {
		// ProgressBar progressBar = (ProgressBar)
		// findViewById(R.id.progressbar);
		baradd.setVisibility(View.VISIBLE);
		baradd.setProgress(0);
		baradd.invalidate();
	}

	MediaPost m;

	public void checkAndSend() {

		Cursor cursor = getContentResolver().query(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
				null,
				MediaPostTable.MEDIS_STAUTS + " != ?  and "
						+ MediaPostTable.TRY + " < ? and "
						+ MediaPostTable.USER_ID + " = ?",
						new String[] { MediaPostTable.STATUS_SENT, "4",
						"" + BusinessProxy.sSelf.getUserId() }, null);

		if (cursor.getCount() > 0) {
			m = mediaPostObject(cursor);
			// cursor.moveToFirst();
			new Thread(new Runnable() {

				@Override
				public void run() {
					send(m);
				}
			}).start();

		} else {

		}
	}

	public static MediaPost mediaPostObject(Cursor cursor) {

		// ContentValues values = new ContentValues();
		// values.put(MediaPostTable.DATE, System.currentTimeMillis());
		// values.put(MediaPostTable.TAG, tag);
		// values.put(MediaPostTable.ABOUT,
		// composeScreen_msgBox.getText().toString());
		// values.put(MediaPostTable.MODE, mode);
		// values.put(MediaPostTable.CATEGORY, catId);
		// 03-02 15:45:40.839: I/System.out(17944): ----------post------ori
		// fburl---{"message":"One or more input parameters are incorrect. Only values allowed for cat range from 49-57 both inclusive","status":"error"}

		cursor.moveToFirst();
		MediaPost mediaPost = new MediaPost();
		mediaPost.DB_ID = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.COLUMN_ID));

		mediaPost.TRY = cursor
				.getInt(cursor.getColumnIndex(MediaPostTable.TRY));
		mediaPost.mediaText = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.ABOUT));
		mediaPost.cat = 49;// cursor.getInt(cursor.getColumnIndex(MediaPostTable.CATEGORY));
		mediaPost.mediaTag = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.TAG));
		int mode = cursor.getInt(cursor.getColumnIndex(MediaPostTable.MODE));

		mediaPost.userId = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.USER_ID));
		mediaPost.userPass = cursor.getString(cursor
				.getColumnIndex(MediaPostTable.USER_PASS));
		mediaPost.clientId = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.CLIENT_ID));
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
	}

	public void openRowOption(int type) {// 1 right 2 left
		HashMap<Integer, String> menuItems = new HashMap<Integer, String>();

		menuItems.put(1, "Delete Message");

		menuItems.put(2, "Cancel");

		// if(message.participantInfoClazz!=null &&
		// BusinessProxy.sSelf.isInBuddyList(message.participantInfoClazz.userName)){
		// System.out.println("---------removing friend list option");
		// menuItems.remove(4) ;
		// }
		// if(message.mfuId.equalsIgnoreCase("-1")){
		// System.out.println("---------removing friend list option");
		// menuItems.remove(0) ;
		// }
		openActionSheet(menuItems, new OnMenuItemSelectedListener() {
			@Override
			public void MenuItemSelectedEvent(Integer selection) {
				switch (selection) {

				case 1:
					// request = new Request(ID_DELETE_MESSAGE, "url");
					// request.execute("LikeDislike");

					DialogInterface.OnClickListener deleteHandler = new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							if (!OtherMessageActivity.this
									.checkInternetConnection()) {
								OtherMessageActivity.this.networkLossAlert();
								return;
							}
							request = new Request(ID_DELETE_MESSAGE, "url");
							request.execute("LikeDislike");
						}
					};

					showAlertMessage("Confirm",
							"Do you really want to delete this message?",
							new int[] { DialogInterface.BUTTON_POSITIVE,
							DialogInterface.BUTTON_NEGATIVE },
							new String[] { "Yes", "No" },
							new DialogInterface.OnClickListener[] {
							deleteHandler, null });

					break;

				}
			}
		},null);
	}

	@Override
	protected void onDestroy() {
		// System.out.println("-------------on stop bookmark activity--------");
		// stopManagingCursor(activityAdapter.getCursor());
		try{
			if (activityAdapter != null) {
				if(!activityAdapter.getCursor().isClosed())
					activityAdapter.getCursor().close();
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		// sqlDB.close(); database.close();
		super.onDestroy();
		if(listViewActivity!=null){
			listViewActivity.destroyDrawingCache();
			listViewActivity=null;
		}
		if(activityAdapter!=null){
			activityAdapter=null;
		}
		//System.gc();
	}

	Vector<String> readIds = new Vector<String>();
	int readCount = 0;
	Thread checkThreah;

	public void checkIsView(Message message) {
		if (message.hasBeenViewed != null
				&& message.hasBeenViewed.equalsIgnoreCase("false")) {

			if (!readIds.contains((message.messageId))) {
				readCount++;
				readIds.add(message.messageId);
				setValuesForReadMessage(message.mfuId);
			}
			// setUnreadCount();
		}

	}

	private void setValuesForReadMessage(String messageId) {

		// Send Request For read message.
		if (null == BusinessProxy.messageIDString) {
			BusinessProxy.messageIDString = new StringBuffer();
			BusinessProxy.messageIDString.append(messageId);
		} else {
			StringBuffer currentMessageId = new StringBuffer();
			currentMessageId.append(messageId);

			if (Constants.ERROR_NOT_FOUND == BusinessProxy.messageIDString
					.toString().indexOf(currentMessageId.toString())) {
				BusinessProxy.messageIDString.append(';');
				BusinessProxy.messageIDString.append(currentMessageId
						.toString());
			}
			currentMessageId = null;
		}
		// System.out.println("--------------BusinessProxy.messageIDString. :"+BusinessProxy.messageIDString.toString());
	}

	private void setUnreadCount() {
		// if(1==1)return;
		if (readIds.size() > 0) {

			String id = "";
			for (int i = 0; i < readIds.size(); i++) {
				id = id + "'" + readIds.get(i) + "',";
			}
			// if(id.lastIndexOf(",")!= -1)
			if (id.length() > 0)
				id = id.substring(0, id.length() - 1);
			String sql = "UPDATE  " + MessageTable.TABLE + " set  "
					+ MessageTable.HAS_BEEN_VIEWED + "= 'true'  WHERE "
					+ MessageTable.MESSAGE_ID + " IN (" + id + ")";
			// System.out.println("---------read count updated- sqlDB---- "+sql);
			BusinessProxy.sSelf.sqlDB.execSQL(sql);
			onNotification(Constants.FEED_NOTIFICATION_COUNT, null, null);
			readIds.clear();
			if (activityAdapter != null) {
				activityAdapter.getCursor().requery();
				activityAdapter.notifyDataSetChanged();
			}
			// listViewActivity.invalidate();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			setUnreadCount();
			// onNotification(requestCode)
		}

	}

	public String getRtmlText(String id) {
		Cursor cursor = BusinessProxy.sSelf.sqlDB.query(MessageTable.TABLE,
				null, MessageTable.MESSAGE_ID + " = ? and "
						+ MessageTable.USER_ID + " = ?", new String[] { id,
				BusinessProxy.sSelf.getUserId() + "" }, null, null,
				null);
		cursor.moveToFirst();
		String s = cursor
				.getString(cursor.getColumnIndex(MessageTable.VIDEO_ID_8));
		cursor.close();

		return s;

	}
    //
	//
    //	Manoj Singh
	// Date 31-07-2015
	//[--Start
	private String postDataOnServer(String url, String msgId){
		//		url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings";
		//http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings?groupName=pkrpriv&op={TURNOFF or TURNON}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		msgId = msgId.replaceAll(";",",");
		nameValuePair.add(new BasicNameValuePair("messageIds", msgId));
		//nameValuePair.add(new BasicNameValuePair("op", op));//{TURNOFF or TURNON}
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);
			int responceCode = response.getStatusLine().getStatusCode();
			if(responceCode == 200){
				//InputStream ins = response.getEntity().getContent();
				String strResponse = EntityUtils.toString(response.getEntity());
				//	//System.out.printlnprintln("DATA = "+IOUtils.read(ins));
				UIActivityManager.refreshChannelList = true;
				return strResponse;
			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// writing exception to log
			e.printStackTrace();
			return null;
		}
		return null;
	}
	public void sendReadReceipt(final String url, final String msgID)
	{
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String responce = postDataOnServer("http://"+Urls.TEJAS_HOST+"/tejas/feeds/message/markread", msgID );
				OtherMessageActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(responce != null && responce.contains("success")){
							try{
								JSONObject  jobj = new JSONObject(responce);
								if(responce.contains("desc")){
									String desc = jobj.optString("desc").toString();
//									showSimpleAlert(getString(R.string.app_name), desc);
//									if(desc != null && desc.length() > 0)
//									Toast.makeText(ConversationsActivity.this, desc, Toast.LENGTH_SHORT).show();
								}	
							}catch(Exception e){}
						}

					}
				});
			}
		}).start();
	}
	//--END]
	//
	//
	
//----------------- Open view message of Channel to report ----------
	public void openChatFromPush(String response) 
	{
		try{
			//Parse and set JSON Respone
			//Entry Objects
			String deleteMessageAPI = null;
			String groupName = null;
			String messageId = null;
			int senderId = 0;
			String firstName = null;
			String lastName = null;
			String createdDate = null;
			String senderName = null;
			String textMessage = null;
			String groupID = null;
			//Media Objects
			String mVoicePath = null;
			String  mVideoPath = null;;
			String  mVideoThumbPath = null;;
			Vector<String> mPicturePath = new Vector<String>();
			Vector<String> mPictureThumbPath = new Vector<String>();
			String mediaId = null;
			String mediaType = null;
			String url = null;
			String urlType = null;
			//Make Community.Entry type object, so that message can be directly displayed in communitychatactivity
			Entry entrys = new CommunityFeed.Entry();
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has("deleteMessageAPI")){
				deleteMessageAPI = jsonObject.getString("deleteMessageAPI");
				if(deleteMessageAPI.contains("groupName="))
					groupName = deleteMessageAPI.substring(deleteMessageAPI.lastIndexOf("groupName=") + "groupName=".length());
				if(deleteMessageAPI.contains("messageId=")){
					messageId = deleteMessageAPI.substring(deleteMessageAPI.lastIndexOf("messageId=") + "messageId=".length());
					if(messageId.indexOf('&') != -1)
						messageId = messageId.substring(0, messageId.indexOf('&'));
				}
			}
			if (jsonObject.has("text")){
				textMessage = jsonObject.getString("text");
			}
			if (jsonObject.has("groupID")){
				groupID = jsonObject.getString("groupID");
			}
			if (jsonObject.has("firstName")){
				firstName = jsonObject.getString("firstName");
			}
			if (jsonObject.has("lastName")){
				lastName = jsonObject.getString("lastName");
			}
			if (jsonObject.has("createdDate")){
				createdDate = jsonObject.getString("createdDate");
			}
			if (jsonObject.has("senderName")){
				senderName = jsonObject.getString("senderName");
			}
			if (jsonObject.has("mediaUrlList")) {
				JSONArray jSONArray = jsonObject.getJSONArray("mediaUrlList");
				for (int i = 0; i < jSONArray.length(); i++) {
					JSONObject media_data = jSONArray.getJSONObject(i);
					if (media_data.has("mediaType")) {
						mediaType = media_data.getString("mediaType");
					}
					if (media_data.has("mediaId")) {
						mediaId = media_data.getString("mediaId");
					}
					if (media_data.has("url")) {
						url = media_data.getString("url");
					}
					if (media_data.has("urlType")) {
						urlType = media_data.getString("urlType");
					}
					//Now add Media to entry in required format
					if(mediaType.equalsIgnoreCase("audio")){
						mVoicePath = url;
					}
					else if(mediaType.equalsIgnoreCase("video")){
						if(urlType.equalsIgnoreCase("normal"))
							mVideoPath = url;
						else if(urlType.equalsIgnoreCase("thumb"))
							mVideoThumbPath = url;
					}
					else if(mediaType.equalsIgnoreCase("image")){
						if(urlType.equalsIgnoreCase("normal"))
							mPicturePath.add(url);
						else if(urlType.equalsIgnoreCase("thumb"))
							mPictureThumbPath.add(url);
					}
				}
			}
			
			if(groupName != null)
				entrys.groupName = groupName;
			else
				entrys.groupName = getString(R.string.system_message);
			
			if(messageId != null)
				entrys.messageId = messageId;
			else
				entrys.messageId = "" + System.currentTimeMillis();
			
			if(senderId > 0)
				entrys.senderId = senderId;
			else
				entrys.senderId = 0;
			
			if(firstName != null)
				entrys.firstName = firstName;
			else
				entrys.firstName = "";
			
			if(lastName != null)
				entrys.lastName = lastName;
			else
				entrys.lastName = "";

			if(createdDate != null)
				entrys.createdDate = createdDate;
			else
				entrys.createdDate = Utilities.getCurrentdate();
			
			if(senderName != null)
				entrys.senderName = senderName;
			else
				entrys.senderName = "";
			
			//Add media message here
			entrys.media = new Vector<String>();
			//First Add Audio
			if (mVoicePath != null && mVoicePath.length() > 0) {
				entrys.audioUrl = "~n~p" + mVoicePath;
				entrys.media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
				entrys.media.add("audio");
				entrys.media.add(mVoicePath);
				entrys.media.add("normal");
			}
			//Then Add video
			if (mVideoPath != null && mVideoPath.length() > 0) {
				entrys.vidUrl = "~n~p" + mVideoPath;
				entrys.media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
				entrys.media.add("video");
				entrys.media.add(mVideoPath);
				entrys.media.add("normal");
				entrys.media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
				entrys.media.add("video");
				entrys.media.add(mVideoThumbPath);
				entrys.media.add("thumb");
			}
			//Then Add Picture
			if (mPicturePath != null && mPicturePath.size() > 0) {
				entrys.imgUrl = "~n~p";
				for (int i = 0; i < mPicturePath.size(); i++) 
				{
					entrys.imgUrl = entrys.imgUrl + mPicturePath.elementAt(i) + "~";
					entrys.imgThumbUrl = entrys.imgThumbUrl + mPictureThumbPath.elementAt(i) + "~";

					entrys.media.add(RandomUtils.nextInt(9999));
					entrys.media.add("image");
					entrys.media.add((mPicturePath.elementAt(i)));
					entrys.media.add("normal");

					//Duplicate values entered forcefully to re-use the existing protocol
					entrys.media.add(RandomUtils.nextInt(9999));
					entrys.media.add("image");
					entrys.media.add((mPicturePath.elementAt(i)));
					entrys.media.add("normal");
				}
			}

			//Add text message if there
			if(textMessage != null)
				entrys.messageText = Base64.encodeToString(textMessage.getBytes(), Base64.DEFAULT);
			else
				entrys.messageText = null;
			
			if(groupID != null)
				entrys.groupId = Integer.parseInt(groupID);
			else
				entrys.groupId = 0;
					
			//Add Entry to Data Model
			DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entrys);
			Intent intent = new Intent(OtherMessageActivity.this, CommunityChatActivity.class);
			intent.putExtra("SYSTEM_MSG_VIEW", true);
			if(deleteMessageAPI != null && deleteMessageAPI.length() > 0){
				intent.putExtra("DELETE_MESSAGE_URL", deleteMessageAPI);
				intent.putExtra("DELETE_MESSAGE_ID", message.messageId);
				startActivityForResult(intent, PARENT_ACTIVITY_CALLBACK);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
//-----------------------------------------------------
	public void deleteConversation(String cid, boolean callback){
		try{
			if(cid != null){
				getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
						MessageConversationTable.CONVERSATION_ID + " = ? ",
						new String[] { getIntent().getStringExtra(Constants.CONVERSATION_ID) });
				getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX,
						MessageTable.CONVERSATION_ID + " = ? ",
						new String[] { cid });
				activityAdapter.getCursor().requery();
				activityAdapter.notifyDataSetChanged();
				if(callback)
					showToast(getString(R.string.you_have_successfully_deleted_this_message));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void deleteMessage(String message_id, boolean callback){
		try{
			if(message_id != null){
				getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX,
						MessageTable.MESSAGE_ID + " = ? ",
						new String[] { message.messageId});
				activityAdapter.getCursor().requery();
				activityAdapter.notifyDataSetChanged();
//				if(callback)
//					showToast(getString(R.string.you_have_successfully_deleted_this_message));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
