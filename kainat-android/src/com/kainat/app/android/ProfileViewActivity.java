package com.kainat.app.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.Content;
import com.kainat.app.android.bean.Interest;
import com.kainat.app.android.bean.ProfileBean;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.util.AdConnectionManager;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VoiceMedia;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileViewActivity extends UICommunityManager implements
OnClickListener,ThumbnailReponseHandler {
	LinearLayout linearLayoutUserChannels,layout_interests;
	ArrayList<String> GRID_DATA =  new ArrayList<String>();
	ArrayList<String> GRID_DATA_ID =  new ArrayList<String>();
	ArrayList<String> GRID_DATA_IMG =  new ArrayList<String>();
	ArrayList<Integer> GRID_DATA_IMG_INT =  new ArrayList<Integer>();
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisc(true).
			resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.imagedefault)
			.showImageOnFail(R.drawable.imagedefault)
			.showImageOnLoading(R.drawable.imagedefault).build();
	/*static String[] GRID_DATA= new String[] {
		"Sports" ,
		"News", 
		"Entertainment" ,
		"Lifestyle",
	"Spirituality" };
	static String[] GRID_DATA_IMG = new String[] { 
		"Sports",
		"News",
		"Entertainment",
		"Lifestyle",
	"Spirituality" };
	public static final int[] GRID_DATA_IMG_INT = new int[] {
		R.drawable.sports,
		R.drawable.news,
		R.drawable.entertainment,
		R.drawable.lifestyle,
		R.drawable.spirituality};*/
	HorizontalListView listViewIntrestes;
	HorizontalListView listViewUserChannels;
	Button add_friend ;
	ProgressBar loading_bar = null;
	ProgressBar loading_barr = null;
	
	DisplayImageOptions optionsss = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisk(true)
			.resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.male_icon)
			.showImageOnFail(R.drawable.male_icon)
			.showImageOnLoading(R.drawable.male_icon)
			.build();

	public static String TEJAS_HOST = Urls.TEJAS_HOST;//"124.153.95.168:28080";
	public static String TAG = ProfileViewActivity.class.getSimpleName();
	Button btn_status;
	private static byte SCREEN_PROFILE_MAIN = 1;
	private static byte SCREEN_SELECT_INTEREST = 2;
	private static byte SCREEN_PROFILE_VIEW = 3;
	private static byte CURRENT_LOGIN = SCREEN_PROFILE_MAIN;

	private final String URL_SAVE_INTEREST = "http://" + TEJAS_HOST
			+ "/tejas/feeds/user/interest/add?cat_id=102&interes";
	private final String URL_REMOVE_INTEREST = "http://" + TEJAS_HOST
			+ "/tejas/feeds/user/interest/delete?interest_id=6";
	private final String URL_GET_INTEREST = "http://" + TEJAS_HOST
			+ "/tejas/feeds/user/interest/get?user_id=1574403";
	private final String URL_SEARCH_INTEREST = "http://" + TEJAS_HOST
			+ "/tejas/feeds/user/interest/add?cat_id=102&interes";
	private final String URL_VIEW_PROFILE = "http://" + TEJAS_HOST
			+ "/tejas/feeds/user/profile?user=";
	private final String URL_UPDATE_PROFILE = "http://" + TEJAS_HOST
			+ "/tejas/feeds/user/profile?user=";
	// private final String URL_GET_INTEREST = "" ;

	Handler handler = new Handler();

	Context context;

	ListView listview;
	ArrayList<String> list;
	ProfileTask profileTask;
	LinearLayout layout;
	TextView status_profile,status_time;
	boolean log = true;
	private ImageView audioRecordImageView;
	private String mVoicePath;
	SeekBar baradd;
	public boolean availableVoice = false;
	LinearLayout media_play_layout,status_fields;
	TextView total_autio_time, played_autio_time,user_count;
	boolean voiceIsPlaying;
	TextView user_channels;
	String currentUser;
	Button chat_profile_iv;
	//ImageView blockUserButton;
	Button blockUserButton;

	PowerManager powerManager;
	WakeLock wakeLock;
	AlertDialog alertDialogRespond;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		init() ;
		//		BusinessProxy.sSelf.recordScreenStatistics(Constants.SCRTEEN_NAME_VIEW_PROFILE, true, true);// Add
		mVoiceMedia = new VoiceMedia(this, this);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("User Profile Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(ProfileViewActivity.this).reportActivityStart(this);
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(ProfileViewActivity.this).reportActivityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		if (DataModel.sSelf.getRemoveBoolean(DMKeys.NEW_INTENT) == true){
			Utilities.setBoolean(ProfileViewActivity.this, Constants.PROFILE_UPDATED, false) ;
			//			init() ;
		}
		BusinessProxy.sSelf.leftMenuAdFlag =false ;

		if(Utilities.getBoolean(ProfileViewActivity.this, Constants.PROFILE_UPDATED)){
			Utilities.setBoolean(ProfileViewActivity.this, Constants.PROFILE_UPDATED, false) ;
			profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
			if (getIntent().getStringExtra("USERID") == null)
				getIntent().putExtra("USERID", SettingData.sSelf.getUserName());
			profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
		}
		reloadeViewAfterChangeLanguag();

		/*try{
			Get_InterestTask blockUser = new Get_InterestTask();
			blockUser.execute(profileBean.getUserId());
		}catch(Exception e){
			Log.v("Get_InterestTask Exception", ""+e);
		}*/
		try{
			if(!profileBean.getUserId().isEmpty()){
				Get_InterestTask blockUser = new Get_InterestTask();
				blockUser.execute(profileBean.getUserId());
			}
			else if(profileBean.userId!=null && !profileBean.userId.isEmpty()){
				Get_InterestTask intrestuser = new Get_InterestTask();
				intrestuser.execute(profileBean.userId);
			}else if(getIntent().getStringExtra("USERID") != null){
				Get_InterestTask intrestuser = new Get_InterestTask();
				intrestuser.execute(getIntent().getStringExtra("USERID"));
			}else{/*
				profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
				if (getIntent().getStringExtra("USERID") == null)
					getIntent().putExtra("USERID", SettingData.sSelf.getUserName());
				profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
			 */}

		}catch(Exception e){
			Log.v("Get_InterestTask Exception", ""+e);
		}
	}
	public void init(){

				//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		setContentView(R.layout.profile_view);
		add_friend = (Button)findViewById(R.id.add_friend);
		user_count = (TextView)findViewById(R.id.user_count);
		add_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//
				//
				//
				if(profileBean != null  && profileBean.isFriend() != null)
					if(profileBean.isFriend().equals("true")  || profileBean.isFriend().equals("false")){
						AddFriendTask addFriendTask = new AddFriendTask() ;
						addFriendTask.execute(profileBean.userId);
					}
					else if(profileBean.isFriend().equals("pending")){
						Toast.makeText(ProfileViewActivity.this, "Reqeust pending", Toast.LENGTH_LONG).show();
					}
					else if(profileBean.isFriend().equals("respond")){
						repspondFriendDialog();
						//delete-->addfreind
						//accept-->delete friend
					}


				//
				//
			}
		});
		powerManager = (PowerManager)ProfileViewActivity.this.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

		user_channels = (TextView)findViewById(R.id.user_channels);
		chat_profile_iv = (Button)findViewById(R.id.chat_profile_iv);
		blockUserButton = (Button)findViewById(R.id.block);
		blockUserButton.setVisibility(View.GONE);
		blockUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//button
				if(!profileBean.isBlocked){
					blockedServerMessage();	
				}else
				{
					try{
						BlockUserRequest blockUser = new BlockUserRequest();
						blockUser.execute(profileBean.getUserId());
					}catch(Exception e){
						Log.v("blockUserButton Exception", ""+e);
					}	
				}

			}
		});


		chat_profile_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {/*
				String CONVERSATIONID = "0";
				Cursor cursor;
				String where = "LOWER("+MessageConversationTable.PARTICIPANT_INFO+")"+ " like '"+search.toLowerCase()+"' or LOWER("+MessageConversationTable.SUBJECT+") like '"+search.toLowerCase()+"' and "
						+MessageConversationTable.USER_ID+" = '"+BusinessProxy.sSelf.getUserId()+"'" ;

				cursor=  BusinessProxy.sSelf.sqlDB.query(MessageConversationTable.TABLE, null, 
						null, null
						, null, null,MessageConversationTable.INSERT_TIME + " DESC");
				int rows = cursor.getCount();
				cursor.moveToFirst();	
				for (int i = 0; i < rows; i++) {

					Log.v("MANOJ",""+cursor.getString(cursor.getColumnIndex(MessageConversationTable.USER_ID)));
					Log.v("MANOJ",""+cursor.getString(cursor.getColumnIndex(MessageConversationTable.USER_NAME)));
					Log.v("dumpCursorToString", ""+DatabaseUtils.dumpCursorToString(cursor));

					if(cursor.getString(cursor.getColumnIndex(MessageConversationTable.SOURCE)).equals(""+getIntent().getStringExtra("USERID"))){
						//ConversationId  = CursorToObject.conversationListObject(cursor,ProfileViewActivity.this) ;
						CONVERSATIONID = cursor.getString(cursor.getColumnIndex(MessageConversationTable.CONVERSATION_ID));
						break;
					}
					// arrConversationList.add(cu.getString(cu.getColumnIndex(MessageConversationTable.CONVERSATION_ID)));
					cursor.moveToNext();
				}

				UIActivityManager.TabValue = 1;
				Intent callIntent ;
				callIntent= new Intent(ProfileViewActivity.this, KainatHomeActivity.class);
				callIntent.putExtra("TAB_TYPE", 1);
				callIntent.putExtra("SENDER_USERNAME",SettingData.sSelf.getUserName());
				String name ="";
				if(SettingData.sSelf.getFirstName()!=null){
					 name = SettingData.sSelf.getFirstName();
				}
				if(SettingData.sSelf.getLastName()!=null){
					name = name+ " " + SettingData.sSelf.getLastName();
				}
				callIntent.putExtra("SENDER_NAME",name);
				callIntent.putExtra("CONVERSATION_ID",CONVERSATIONID);
				callIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(callIntent);
			 */
				//String destinations = profileBean.username + "<"+getIntent().getStringExtra("USERID")+">";
				String destinations = profileBean.firstName + "<"+ profileBean.username+">";
				DataModel.sSelf.storeObject(DMKeys.COMPOSE_DESTINATION_CONTACTS, destinations);
				Intent intent = new Intent(ProfileViewActivity.this, ConversationsActivity.class);
				intent.putExtra(Constants.CONVERSATION_ID, "-1");
				intent.putExtra("START_CHAT_FROM_EXTERNAL",(byte) 1);
				intent.putExtra("MessageText","");
				startActivity(intent);
				//finish();
			}
		});


		user_channels.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((TextView)findViewById(R.id.profile_link)).performClick();
			}
		});
		((TextView)findViewById(R.id.profile_link)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((TextView)findViewById(R.id.profile_link))!=null  && (((TextView)findViewById(R.id.profile_link)).getText().toString().equals("0")))
				{

				}else{
					Intent intent = new Intent(ProfileViewActivity.this,
							SearchCommunityActivity.class);
					intent.putExtra("TYPE", 1);
					intent.putExtra("CommunityType", "Channel List");
					String url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/user/publicchannel?user="+profileBean.username;
					intent.putExtra("UrlIs",url);
					startActivity(intent);
				}
			}
		});
		btn_status = (Button)findViewById(R.id.status);
		btn_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(profileBean != null && profileBean.currentStatusText != null && profileBean.currentStatusText.length() > 0)
					DataModel.sSelf.storeObject(DMKeys.USER_STATUS + "STATUS", profileBean.currentStatusText);
				Utilities.setBoolean(ProfileViewActivity.this, Constants.PROFILE_UPDATED, true) ;
				Intent intent = new Intent(ProfileViewActivity.this, KainatStatusActivity.class);
				startActivity(intent);
			}
		});
		status_profile = (TextView)findViewById(R.id.status_profile);
		status_profile.setMovementMethod(new ScrollingMovementMethod());
		status_time= (TextView)findViewById(R.id.status_time);

		findViewById(R.id.back_iv).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		status_fields = (LinearLayout) findViewById(R.id.status_fieldss);
		status_fields.setVisibility(View.GONE);
		/*	status_time.setText(profileBean.currentStatusText);
		status_profile.setText(profileBean.currentStatusText);*/
		profileView();
		if(getIntent().getIntExtra("CallFrom", 0) != 1 && !getIntent().getStringExtra("USERID").equalsIgnoreCase(SettingData.sSelf.getUserName())){
			findViewById(R.id.Edit).setVisibility(View.GONE);
			btn_status.setVisibility(View.VISIBLE);
			chat_profile_iv.setVisibility(View.GONE);
			blockUserButton.setVisibility(View.GONE);
		}else if(getIntent().getIntExtra("CallFrom", 0) == 1 && getIntent().getStringExtra("USERID").equalsIgnoreCase(SettingData.sSelf.getUserName())){
			btn_status.setVisibility(View.VISIBLE);
			chat_profile_iv.setVisibility(View.GONE);
			blockUserButton.setVisibility(View.GONE);
		}else if(getIntent().getIntExtra("CallFrom", 0) == 1){
			btn_status.setVisibility(View.GONE);
			chat_profile_iv.setVisibility(View.VISIBLE);
			//blockUserButton.setVisibility(View.VISIBLE);
		}

		//Show Intrests and Channels
		listViewIntrestes = (HorizontalListView) findViewById(R.id.listview_interests);
		listViewIntrestes.setAdapter(mAllCategoryAdapter);
		linearLayoutUserChannels = (LinearLayout) findViewById(R.id.mainlayout_outer_sugested);
		layout_interests  = (LinearLayout) findViewById(R.id.layout_interests);
		listViewUserChannels = (HorizontalListView) findViewById(R.id.listview_two);
		loading_bar = (ProgressBar) findViewById(R.id.loading_bar);
		loading_barr = (ProgressBar) findViewById(R.id.loading_barr);
		checkFriending();

	}
	public void repspondFriendDialog(){
		alertDialogRespond = new AlertDialog.Builder(this).create();

		alertDialogRespond.setTitle("");

		alertDialogRespond.setMessage("Respond Request:");

		alertDialogRespond.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				alertDialogRespond.dismiss();
				//...

			} }); 

		alertDialogRespond.setButton(AlertDialog.BUTTON_NEGATIVE,getString(R.string.Accept), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				//...
				AcceptFriendTask acceptFriendTask = new AcceptFriendTask() ;
				acceptFriendTask.execute(profileBean.userId);
			}}); 

		alertDialogRespond.setButton(AlertDialog.BUTTON_NEUTRAL,getString(R.string.delete_account), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				profileBean.isFriend = "true";
				AddFriendTask addFriendTask = new AddFriendTask() ;
				addFriendTask.execute(profileBean.userId);
				//...

			}});
		alertDialogRespond.show();
	}
	private void checkFriending() {

		/*if(findViewById(R.id.Edit).getVisibility()== 8){
			add_friend.setVisibility(View.GONE);
		}else{
			add_friend.setVisibility(View.VISIBLE);
		}
		 */
		if(profileBean != null  && profileBean.isFriend() != null)
			if(profileBean.isFriend().equals("true")){
				add_friend.setVisibility(View.VISIBLE);
				add_friend.setBackgroundResource(R.drawable.delete_friend);
				//	add_friend.setText(R.string.add_friend);
			}else if(profileBean.isFriend().equals("false")){
				add_friend.setVisibility(View.VISIBLE);

				add_friend.setBackgroundResource(R.drawable.icon_add_friend);
				//add_friend.setText(R.string.delete_friend);
			}
			else if(profileBean.isFriend().equals("pending")){
				add_friend.setVisibility(View.VISIBLE);
				add_friend.setBackgroundResource(R.drawable.request_pending);
				//add_friend.setText(R.string.delete_friend);
			}
			else if(profileBean.isFriend().equals("respond")){
				add_friend.setVisibility(View.VISIBLE);
				add_friend.setBackgroundResource(R.drawable.btn_respond);
				//add_friend.setText(R.string.delete_friend);
			}
		try{
			if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.username.trim())){
				add_friend.setVisibility(View.GONE);
			}
		}catch(Exception e){


		}

	}
	private void parseUserChannelXML(final String aTitle) {
		parseXMLData_suggested();

		if(feed_suggested !=null && feed_suggested.entry !=null){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					user_count.setText(""+feed_suggested.totalGroupCount);
					listViewUserChannels.setAdapter(userChannelAdapter);
				}
			});


		}
	}

	private BaseAdapter mAllCategoryAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			@Override
			public void onClick(View v) {
								Intent intent = new Intent(ProfileViewActivity.this, SearchCommunityActivity.class);
								intent.putExtra("TYPE", 1);
								intent.putExtra("CommunityType", v.getTag().toString());
								intent.putExtra("UrlIs","");
								startActivity(intent);

			}
		};

		@Override
		public int getCount() {
			return GRID_DATA.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_interest_list_row, null);
			TextView title = (TextView) retval.findViewById(R.id.txt_community_name);

			retval.setOnClickListener(mOnButtonClicked);
			title.setText(GRID_DATA.get(position));
			retval.setTag(GRID_DATA.get(position));
			ImageView img_community_background = (ImageView)retval.findViewById(R.id.img_community_background);
			img_community_background.setId(6000+position);
			imageLoader.displayImage("drawable://"+GRID_DATA_IMG_INT.get(position), img_community_background, options);
			return retval;
		}

	};

	private BaseAdapter userChannelAdapter = new BaseAdapter() {

		private OnClickListener mOnButtonClicked = new OnClickListener() {

			@Override
			public void onClick(View v) {
								String int_val = (String)v.getTag();
								int pos = Integer.parseInt(int_val);
							CommunityFeed.Entry entry = feed_suggested.entry.get(pos);
							DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",entry);
							Intent intent = new Intent(ProfileViewActivity.this, CommunityChatActivity.class);
							intent.putExtra("message_url", feed_suggested.entry.get(pos).messageUrl);
							intent.putExtra("tags_name", feed_suggested.entry.get(pos).tags);
							intent.putExtra("group_name", feed_suggested.entry.get(pos).groupName);
							intent.putExtra("group_desc", feed_suggested.entry.get(pos).description);
							intent.putExtra("group_pic", feed_suggested.entry.get(pos).thumbUrl);
								startActivity(intent);


			}
		};

		@Override
		public int getCount() {
			return feed_suggested.entry.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View retval 			 = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_user_channel_list_row, null);
			TextView title 			 = (TextView) retval.findViewById(R.id.txt_community_name);
			TextView txt_membercount = (TextView) retval.findViewById(R.id.txt_membercount);
			TextView txt_postcount   = (TextView) retval.findViewById(R.id.txt_messagecount);
			LinearLayout ll_iconfollow = (LinearLayout)retval.findViewById(R.id.ll_iconfollow);
			ImageView img_follow = (ImageView)retval.findViewById(R.id.img_follow);
			ll_iconfollow.setVisibility(View.VISIBLE);
			if(feed_suggested.entry.get(position).joinOrLeave.equalsIgnoreCase("join")){
				img_follow.setBackgroundResource(R.drawable.iconfollow);
			}else{
				img_follow.setBackgroundResource(R.drawable.iconfollowing);
			}
			img_follow.setTag(position);
			img_follow.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//					joinLeaveCommunity(2,(Integer)v.getTag(),feed_suggested);
				}
			});
			Button btn_feature = (Button)retval.findViewById(R.id.btn_feature);
			btn_feature.setVisibility(View.GONE);

			/*  Button button = (Button) retval.findViewById(R.id.clickbutton);
	            button.setOnClickListener(mOnButtonClicked);*/
			retval.setTag(""+position);
			retval.setOnClickListener(mOnButtonClicked);
			title.setText(feed_suggested.entry.get(position).groupName);
			txt_membercount.setText(""+feed_suggested.entry.get(position).noOfMember);
			txt_postcount.setText(""+feed_suggested.entry.get(position).noOfMessage);
			ImageView img_community_background = (ImageView)retval.findViewById(R.id.img_community_background);
			img_community_background.setId(5000+position);
			imageLoader.displayImage(feed_suggested.entry.get(position).thumbUrl,img_community_background,options);
			return retval;
		}

	};

	private void blockedServerMessage(){
		Spanned sp = Html.fromHtml( getString(R.string.block_confirmation));
		new AlertDialog.Builder(this).setMessage(sp.toString())
		.setPositiveButton(R.string.block,
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				if (!ProfileViewActivity.this
						.checkInternetConnection()) {
					ProfileViewActivity.this.networkLossAlert();
					return;
				}
				BlockUserRequest blockUser = new BlockUserRequest();
				blockUser.execute(profileBean.getUserId());
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

			}
		}).show();

	}


	private void disableViews(){
		audioRecordImageView.setClickable(false);
		findViewById(R.id.user_image).setClickable(false);
		findViewById(R.id.Edit).setClickable(false);
		findViewById(R.id.status).setClickable(false);
		chat_profile_iv.setClickable(false);
		blockUserButton.setClickable(false);
	}

	private void enableViews(){
		audioRecordImageView.setClickable(true);
		findViewById(R.id.user_image).setClickable(true);
		findViewById(R.id.Edit).setClickable(true);
		findViewById(R.id.status).setClickable(true);
		chat_profile_iv.setClickable(true);
		blockUserButton.setClickable(true);
	}
	public void profileView() {
		media_play_layout = (LinearLayout) findViewById(R.id.media_play_layout);

		if (getIntent().getStringExtra("USERID") == null)
			getIntent().putExtra("USERID", SettingData.sSelf.getUserName());
		profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
		profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);


		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.female);


		findViewById(R.id.stats_media_layout_friend).setVisibility(View.GONE);
		findViewById(R.id.stats_post_layout).setVisibility(View.GONE);
		findViewById(R.id.stats_followers_layout).setVisibility(View.GONE);
		findViewById(R.id.stats_ilike_layout).setVisibility(View.GONE);
		blockUserButton.setVisibility(View.GONE);
		audioRecordImageView = (ImageView) findViewById(R.id.audio_record_iv);
		audioRecordImageView.setOnClickListener(this);
		TextView imgStop = (TextView) findViewById(R.id.media_stop_play);
		imgStop.setOnClickListener(playerClickEvent);
	}

	ImageView user_image;
	@Override
	public boolean onBackPressedCheck() {
		return super.onBackPressedCheck();
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	Dialog confirmationDialog;

	public void onClick(View v) {
		//		BusinessProxy.sSelf.addPush.mContext = ProfileViewActivity.this ;
		if(CustomMenu.isShowing()){
			return; 
		}
		switch (v.getId()) {
		case R.id.Edit:

			Intent intent = new Intent(ProfileViewActivity.this, KainatCreateProfileActivity.class);
			intent.putExtra("CallFrom",1);
			String old_intrest  = "";
			String old_intrest_id = "";
			if(GRID_DATA!= null){
				for(int vol = 0;vol < GRID_DATA.size();vol++){
					old_intrest  = old_intrest  + GRID_DATA.get(vol)+",";
					old_intrest_id = old_intrest_id +GRID_DATA_ID.get(vol)+",";
				}
			}
			intent.putExtra("IntrestAlready",old_intrest);
			intent.putExtra("IntrestAlreadyID",old_intrest_id);
			startActivity(intent);
			break;

		case R.id.user_image:
			if(profileBean.pictureMediaList!=null && profileBean.pictureMediaList.size() > 0){
				//				Feed.Entry entry = new Feed().new Entry();
				//				entry.media = new Vector<String>();
				//				for (int j = 0; j < profileBean.pictureMediaList.size(); j++) {
				//					Content content = profileBean.pictureMediaList.get(j);
				//					entry.media.add(content.contentUrl);
				//					entry.media.add("image");
				//					entry.media.add(content.contentUrl);
				//					entry.media.add("local");
				//					entry.media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
				//					entry.media.add("image");
				//					entry.media.add(content.contentUrl);
				//					entry.media.add("thumb");
				//				}
				//				DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", entry);
				//				Intent intentpics = new Intent(ProfileViewActivity.this, StreemMultiPhotoViewer.class);
				//				startActivityForResult(intentpics, Constants.ResultCode);

				Content content = profileBean.pictureMediaList.get(0);
				String pic_url = content.contentUrl;
				if(pic_url != null && !pic_url.trim().equals(""))
				{
					intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					if(pic_url.startsWith("http://"))
						intent.setDataAndType(Uri.parse(pic_url), "image/*");
					else
						intent.setDataAndType(Uri.parse("file://" + pic_url), "image/*");
					startActivity(intent);
				}
			}
			break;
		case R.id.audio_record_iv:
			wakeLock.acquire();
			if (null == mVoicePath) {
				//				mVoiceMedia.startRecording(getString(R.string.done), getString(R.string.cancel), null, 120);
				showAudioDialog(RECORDING_MODE);
			} else {
				if(voiceIsPlaying){
					RTMediaPlayer.reset();
					wakeLock.release();
					media_play_layout.setVisibility(View.GONE);
					RTMediaPlayer.clear();
				}else{
					if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim())){
						HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
						menuItems.put(0, getString(R.string.play));
						menuItems.put(1, getString(R.string.clear));
						menuItems.put(2, getString(R.string.re_record));
						menuItems.put(3, getString(R.string.cancel));
						openActionSheet(menuItems, new OnMenuItemSelectedListener() {
							@Override
							public void MenuItemSelectedEvent(Integer selection) {
								switch (selection) {
								case 0:
									playAvailableVoice();
									disableViews();
									break;
								case 1:
									voiceIsPlaying = false;
									RTMediaPlayer.reset();
									RTMediaPlayer.clear();
									File file = new File(mVoicePath);
									if (file.exists()) {
										file.delete();
									}
									AudioUpdateTask audioTask = new AudioUpdateTask();
									audioTask.execute("Delete");
									audioRecordImageView.setImageResource(R.drawable.profile_audio);
									media_play_layout.setVisibility(View.GONE);
									wakeLock.release();
									mVoicePath = null;
									break;
								case 2:
									voiceIsPlaying = false;
									file = new File(mVoicePath);
									if (file.exists()) {
										file.delete();
									}
									mVoicePath = null;
									//									audioRecordImageView.setImageResource(R.drawable.profile_audio);
									//									media_play_layout.setVisibility(View.GONE);
									//									mVoiceMedia.startRecording("Done", "Cancel", null, 120);

									showAudioDialog(RECORDING_MODE);
									break;
								}

							}
						}, null);
					}else{
						playAvailableVoice();
						disableViews();
					}
				}
			}
			break;
		}
	}
	private class AudioUpdateTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			//			rTDialog = new RTDialog(ProfileViewActivity.this, null,
			//					getString(R.string.updating));
			//
			//			rTDialog.show();
			rTDialog = ProgressDialog.show(ProfileViewActivity.this, "", getString(R.string.updating), true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);

				Hashtable<String, String> postParam = new Hashtable<String, String>();
				String temp = null;
				String audio_file_id = null;
				postParam.put("mode", "update");
				String reqType = urls[0];
				if(mVoicePath != null && reqType.equalsIgnoreCase("Insert")){
					String fileId = Utilities.createMediaID(mVoicePath, Constants.ID_FOR_UPDATE_PROFILE);
					if(fileId.indexOf("}")!=-1)
					{
						return null;
					}
					if (fileId != null) {
						if (audio_file_id == null)
							audio_file_id =  fileId + "";
						else
							audio_file_id = audio_file_id + fileId+ "";
					}
					postParam.put("audio_file_id", audio_file_id);
				}else if(reqType.equalsIgnoreCase("Delete")){
					postParam.put("audio_file_id", "del");
				}


				String responseString = "" ;
				HttpClient httpclientE = new DefaultHttpClient();
				HttpPost httppostw = new HttpPost( "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/completereg?");
				try {
					// Add your data
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					for (Iterator<String> iterator = postParam.keySet().iterator(); iterator.hasNext();) {
						String key = iterator.next();
						String value = postParam.get(key);
						nameValuePairs.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"))));
					}
					httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));

					httppostw.setHeader("RT-Params", ClientProperty.RT_PARAMS);				
					httppostw.setHeader("client_param",
							ClientProperty.CLIENT_PARAMS + "::requesttype##"
									+ "1004");
					httppostw.setHeader("RT-APP-KEY", HttpHeaderUtils
							.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),
									SettingData.sSelf.getPassword()));
					HttpResponse response = httpclientE.execute(httppostw);
					HttpEntity r_entity = response.getEntity();
					responseString = EntityUtils.toString(r_entity);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				ImageDownloader.clearCache();

				Gson gson = new Gson();
				final com.kainat.app.android.bean.Error error = gson.fromJson(responseString,
						com.kainat.app.android.bean.Error.class);
				if(error!=null){
					if(error.getStatus()==2){
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								AppUtil.showTost(ProfileViewActivity.this, error.getDesc());		
							}
						});
						return null;
					}
				}
				return responseString;

			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(ProfileViewActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			closeDialog();
			if(response != null && response.contains("\"status\":\"1\"")){	
				//				Log.d("++++++++++++++++++++++", "On post excute responce = "+response);
				Toast.makeText(ProfileViewActivity.this, getString(R.string.profile_voice_alert), Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(response);
		}
	}
	private void closeDialog(){
		if(rTDialog!=null && rTDialog.isShowing())
			rTDialog.dismiss() ;
	}
	private void openPlayScreen(final String url, boolean autoPlay,
			final LinearLayout ln) {
		availableVoice = false;
		//		TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
		//		tv.setText("Please wait while getting audio...");
		voiceIsPlaying = true;
		RTMediaPlayer.setUrl(null);
		//tv.setTextColor(getResources().getColor(R.color.sub_heading));
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
				if (availableVoice)
					return;
				try {
					ln.findViewById(R.id.streemStatus).setVisibility(View.GONE);
					LinearLayout d = ((LinearLayout) ln
							.findViewById(R.id.media_play_layout));
					d.setVisibility(View.VISIBLE);
					wakeLock.acquire();
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
				availableVoice = true;
				voiceIsPlaying = false;
				ProfileViewActivity.this.handler.post(new Runnable() {

					@Override
					public void run() {
						// closePlayScreen();
						// System.out.println("-----voicePlayCompleted-----");
						try {
							enableViews();
							ImageView imageView1 = (ImageView) ln
									.findViewById(R.id.media_play);
							imageView1
							.setBackgroundResource(R.drawable.addplay);
							imageView1.setTag("PLAY");
							RTMediaPlayer.reset();
							media_play_layout.setVisibility(View.GONE);
							wakeLock.release();
							RTMediaPlayer.clear();
							resetProgress();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				});
				// isFormDisplay = false ;
				// shouldStartDisplayTimer();
			}

			public void onError(int i) {
				try {
					BusinessProxy.sSelf.animPlay = false;
					//					if (fullDialog != null && fullDialog.isShowing()) {
					//						fullDialog.dismiss();
					//					}
					// System.out.println("onerroe=======i");
					TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
					//tv.setTextColor(getResources().getColor(R.color.red));
					voiceIsPlaying = false;
					//tv.setText("Unable to play please try later!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onDureationchanged(final long total, final long current) {
				ProfileViewActivity.this.handler.post(new Runnable() {

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
		wakeLock.acquire();
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
									tv.setTextColor(getResources().getColor(R.color.sub_heading));
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
				media_play_layout.setVisibility(View.GONE);
				wakeLock.release();
				voiceIsPlaying = false;
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
				enableViews();
				break;
			}
		}
	};
	private void playAvailableVoice() {
		wakeLock.acquire();
		availableVoice = true;
		// mVoiceMedia.startPlaying(mVoicePath, null);
		media_play_layout.setVisibility(View.VISIBLE);
		baradd = (SeekBar) media_play_layout
				.findViewById(R.id.mediavoicePlayingDialog_progressbar);
		if (((ImageView) media_play_layout.findViewById(R.id.media_play)) != null)
			((ImageView) media_play_layout.findViewById(R.id.media_play))
			.setOnClickListener(playerClickEvent);

		total_autio_time = ((TextView) media_play_layout
				.findViewById(R.id.audio_counter_max));
		played_autio_time = ((TextView) media_play_layout
				.findViewById(R.id.audio_counter_time));
		total_autio_time.setText("00:00)");
		played_autio_time.setText("(00:00 of ");


		if(mVoicePath.startsWith("http:")){
			openPlayScreen(mVoicePath, false, media_play_layout);
		}else{
			openPlayScreen(Downloader.getInstance().getPlayUrl(mVoicePath), false,
					media_play_layout);
		}
		wakeLock.release();
	}
	public void onBackPressed() {

		if(CustomMenu.isShowing()){
			CustomMenu.hide() ;
			return; 
		}
		setResult(RESULT_OK) ;
		finish();
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	Display display;
	LinearLayout llay;// = new LinearLayout(this);


	public void addSelectedItem() {
		try {
			display = getWindowManager().getDefaultDisplay();
			LinearLayout add_selected_items = (LinearLayout) findViewById(R.id.add_selected_items);
			add_selected_items.removeAllViews();
			add_selected_items.measure(display.getWidth(), display.getHeight());

			llay = new LinearLayout(this);
			llay.setOrientation(LinearLayout.HORIZONTAL);

			LinearLayout.LayoutParams llp = new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			boolean interestAdded = false; 
			for (int i = 0; i < profileBean.interest.size(); i++) {
				interestAdded = true; 
				Interest interest = (Interest) profileBean.interest.get(i);
				//				TextView v = new TextView(this);

				//////
				LayoutInflater inflater = LayoutInflater
						.from(ProfileViewActivity.this);
				inflater = LayoutInflater.from(ProfileViewActivity.this);
				TextView	v = (TextView) inflater.inflate(
						R.layout.interest_testview_screen, null, false);

				//				v.setText("    " + Constants.values[position]);
				Drawable search_unsel = getResources().getDrawable(
						interest.getIcon());
				Rect r = null;
				Drawable[] d = v.getCompoundDrawables();
				r = null;
				if (d[0] != null)
					r = d[0].getBounds();
				if (r != null)
					search_unsel.setBounds(r);
				v.setCompoundDrawables(search_unsel, null, null, null);
				//////
				//				v.setTextColor(getResources().getColor(R.color.sub_heading));
				v.setText(" " + interest.getName() + "  ");
				View view  = new View(ProfileViewActivity.this);
				LinearLayout.LayoutParams layoutParams =
						new LayoutParams(5, 5);
				view.setLayoutParams(layoutParams) ;
				//				view.setBackgroundColor(Color.RED) ;
				if (canAdd(llay, v)) {
					if(llay.getChildCount()>0){
						llay.addView(view);
						view  = new View(ProfileViewActivity.this);
						layoutParams =
								new LayoutParams(5, 5);
						view.setLayoutParams(layoutParams) ;
					}
					llay.addView(v);
					llay.addView(view);
					//					System.out.println("----if-s[i]:" + interest.getName());
				} else {

					add_selected_items.addView(llay);
					llay = new LinearLayout(this);
					llay.setOrientation(LinearLayout.HORIZONTAL);
					//					System.out.println("----else-s[i]:" + interest.getName());

					//					layoutParams =
					//							new LayoutParams(5, 5);
					//					view.setLayoutParams(layoutParams) ;
					llay.addView(view);

					add_selected_items.addView(llay);
					llay = new LinearLayout(this);
					llay.setOrientation(LinearLayout.HORIZONTAL);
					//					System.out.println("----else-s[i]:" + interest.getName());
					llay.addView(v);
				}
			}
			if(interestAdded)
			{
				findViewById(R.id.default_interest).setVisibility(View.GONE);
				findViewById(R.id.add_selected_items).setVisibility(View.VISIBLE);
			}else
			{
				findViewById(R.id.add_selected_items).setVisibility(View.GONE);
				findViewById(R.id.default_interest).setOnClickListener(ProfileViewActivity.this) ;

				if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim()))
					findViewById(R.id.interest_layout).setVisibility(View.VISIBLE);
				else
					findViewById(R.id.interest_layout).setVisibility(View.GONE);
				//				((TextView)findViewById(R.id.default_interest)).setText();
			}
			add_selected_items.addView(llay);

			add_selected_items.setVisibility(View.GONE) ;
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public boolean canAdd(LinearLayout linearLayout, TextView textView) {

		linearLayout.measure(0, 0);
		// linearLayout.requestLayout();
		textView.measure(0, 0);
		// textView.requestLayout();
		TextView vs = new TextView(this);
		vs.setText("Interest");
		vs.measure(0, 0);
		int maxWidth = display.getWidth() - (40+textView.getMeasuredWidth());

		int totWid = 0;
		for (int i = 0; i < linearLayout.getChildCount(); i++) {
			View v = linearLayout.getChildAt(i);
			v.measure(0, 0);
			// v.requestLayout();
			totWid = totWid + v.getMeasuredWidth() + 15;
		}


		if (linearLayout.getChildCount() == 0)
			return true;
		if (maxWidth < (totWid + textView.getMeasuredWidth()))
			return false;
		return true;
	}

	ProfileBean profileBean;

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

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
	public enum RequestType {
		ADD_INTEREST, REMOVE_INTEREST, GET_INTEREST, SEARCH_INTEREST, VIEW_PROFILE,UPDATE_PROFILE,FOLLWO_UNFOLLOW,
	}

	ProgressDialog rTDialog,rTDialogs;

	private class ProfileTask extends AsyncTask<String, Void, String> {
		RequestType requestType;
		Drawable drawable;

		public ProfileTask(RequestType requestType) {
			this.requestType = requestType;
		}

		@Override
		protected void onPreExecute() {
			chat_profile_iv.setVisibility(View.GONE);
			blockUserButton.setVisibility(View.GONE);
			add_friend.setVisibility(View.GONE);
			if(requestType==RequestType.VIEW_PROFILE){
				//				rTDialog = new RTDialog(ProfileViewActivity.this, null,
				//						getString(R.string.feateching_profile));//feateching_profile
				rTDialog = ProgressDialog.show(ProfileViewActivity.this, "", getString(R.string.feateching_profile), true);
			}
			else{
				//				rTDialog = new RTDialog(ProfileViewActivity.this, null,
				//						getString(R.string.updating));
				rTDialog = ProgressDialog.show(ProfileViewActivity.this, "", getString(R.string.updating), true);
			}
			//			rTDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			String url = urls[0];
			if(url.contains("?user="))
				currentUser = url.substring(url.indexOf("?user=") + 6, url.indexOf('&'));
			try {
				if(log)
					Log.i(TAG, "ProfileTask : doInBackground :: url : "+url);
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
				if (requestType == RequestType.FOLLWO_UNFOLLOW) {

					HttpResponse response = getResponse(url);
					if (response == null) {
						dismissAnimationDialog();
						showSimpleAlert("Error", "Please try after sometime!!!");
						return null;
					}
					String responseStr = null;
					try {
						responseStr = EntityUtils
								.toString(response.getEntity());
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if(log)
						Log.i(TAG, "ProfileTask : doInBackground :: response : "+responseStr);
					return responseStr;
				} else if (RequestType.VIEW_PROFILE == requestType) {
					profileBean = new ProfileBean();
					headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
					String response = AdConnectionManager.getInstance().uploadByteData2(url, null, headerParam, null);
					if(log)
						Log.i(TAG, "ProfileTask : doInBackground :: response : "+response);
					JSONObject jsonObjectT = new JSONObject(response);
					if (jsonObjectT.has("status")
							&& jsonObjectT.getString("status")
							.equalsIgnoreCase("1")) {

						JSONObject jsonObject = new JSONObject(
								jsonObjectT.getString("UserProfileFeed"));

						if (jsonObject.has("userId"))
							profileBean.userId = jsonObject.getString("userId");
						if (jsonObject.has("username"))
							profileBean.username = jsonObject
							.getString("username");
						if (jsonObject.has("firstName"))
							profileBean.firstName = jsonObject
							.getString("firstName");
						if (jsonObject.has("lastName"))
							profileBean.lastName = jsonObject
							.getString("lastName");
						if (jsonObject.has("publicChannelCount"))
							profileBean.channelCount = jsonObject
							.getString("publicChannelCount");
						if (jsonObject.has("address"))
							profileBean.address = jsonObject
							.getString("address");
						if (jsonObject.has("city"))
							profileBean.city = jsonObject.getString("city");
						if (jsonObject.has("state"))
							profileBean.state = jsonObject.getString("state");
						if (jsonObject.has("zip"))
							profileBean.zip = jsonObject.getString("zip");
						if (jsonObject.has("country"))
							profileBean.country = jsonObject
							.getString("country");
						if (jsonObject.has("gender"))
							profileBean.gender = jsonObject.getString("gender");

						/*if(profileBean.gender!=null){
							if(profileBean.gender.equalsIgnoreCase("m"))
								profileBean.gender = getString(R.string.male);
						if(profileBean.gender.equalsIgnoreCase("f"))
							profileBean.gender = getString(R.string.female);
						}else
						{*/
						profileBean.gender = getString(R.string.female);
						//}
						if (jsonObject.has("timeLastUpdate"))
							profileBean.timeLastUpdate = jsonObject
							.getString("timeLastUpdate");
						if (jsonObject.has("birthday"))
							profileBean.birthday = jsonObject
							.getString("birthday");
						if (jsonObject.has("numberOfBuddies"))
							profileBean.numberOfBuddies = jsonObject
							.getString("numberOfBuddies");
						if (jsonObject.has("numberOfPosts"))
							profileBean.numberOfPosts = jsonObject
							.getString("numberOfPosts");
						if (jsonObject.has("numberOfMediaFollowers"))
							profileBean.numberOfMediaFollowers = jsonObject
							.getString("numberOfMediaFollowers");
						if (jsonObject.has("numberOfMediaFollowing"))
							profileBean.numberOfMediaFollowing = jsonObject
							.getString("numberOfMediaFollowing");
						if (jsonObject.has("mobileNumber"))
							profileBean.mobileNumber = jsonObject
							.getString("mobileNumber");
						if (jsonObject.has("isMobileNumberVerified"))
							profileBean.isMobileNumberVerified = Boolean
							.parseBoolean(jsonObject
									.getString("isMobileNumberVerified"));
						if (jsonObject.has("email"))
							profileBean.email = jsonObject.getString("email");
						if (jsonObject.has("isEmailVerified"))
							profileBean.isEmailVerified = Boolean
							.parseBoolean(jsonObject.getString("isEmailVerified"));
						if (jsonObject.has("language"))
							profileBean.language = jsonObject
							.getString("language");
						if (jsonObject.has("isSecurityQuestionSet"))
							profileBean.isSecurityQuestionSet = Boolean
							.parseBoolean(jsonObject
									.getString("isSecurityQuestionSet"));
						// Author Manoj Singh
						// Date 21-04-2015
						if (jsonObject.has("currentStatusText"))
							profileBean.currentStatusText = jsonObject
							.getString("currentStatusText");
						if (jsonObject.has("currentStatusDate"))
							profileBean.currentStatusDate = jsonObject
							.getString("currentStatusDate");
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if(profileBean.currentStatusText !=null && !profileBean.currentStatusText.equals("")){
									status_fields.setVisibility(View.VISIBLE);
									status_profile.setText(profileBean.currentStatusText);
									status_time.setText(profileBean.currentStatusDate);
									status_time.setVisibility(View.GONE);
									status_profile.setVisibility(View.VISIBLE);
								}
								else
								{
									status_fields.setVisibility(View.GONE);
									status_fields.setVisibility(View.GONE);
								}

							}
						});

						//			
						if (jsonObject.has("pictureMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("pictureMediaList");
							parseContent(jSONArray, 0);
						}
						if (jsonObject.has("audioMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("audioMediaList");
							parseContent(jSONArray, 1);
						}
						if (jsonObject.has("videoMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("videoMediaList");
							parseContent(jSONArray, 2);
						}

						if (jsonObject.has("currentStatusPictureMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("currentStatusPictureMediaList");
							parseContent(jSONArray, 3);
						}
						if (jsonObject.has("currentStatusAudioMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("currentStatusAudioMediaList");
							parseContent(jSONArray, 4);
						}
						if (jsonObject.has("currentStatusVideoMediaList")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("currentStatusVideoMediaList");
							parseContent(jSONArray, 5);
						}

						if (jsonObject.has("isFriend")) {
							profileBean.setFriend(jsonObject
									.getString("isFriend"));
						}

						if (jsonObject.has("isBlocked")) {
							profileBean.setBlocked(jsonObject
									.getBoolean("isBlocked"));
						}
						if (jsonObject.has("isMediaFollower")) {

							profileBean.setMediaFollower(jsonObject
									.getBoolean("isMediaFollower"));

						}
						if (jsonObject.has("followURL")) {

							profileBean.setFollowURL(jsonObject
									.getString("followURL"));
						}
						if (jsonObject.has("currentStatusText")) {

							profileBean.setStatus_text(jsonObject
									.getString("currentStatusText"));
						}
						if (jsonObject.has("userInterest")) {
							JSONArray jSONArray = jsonObject
									.getJSONArray("userInterest");
							profileBean.interest = new Vector<Interest>();

							for (int i = 0; i < jSONArray.length(); i++) {
								JSONObject nameObjectw = jSONArray
										.getJSONObject(i);
								Interest interet = new Interest();
								if (nameObjectw.has("interestId")) {
									interet.setId(Long.parseLong(nameObjectw
											.getString("interestId")));
								}
								if (nameObjectw.has("interestName")) {
									interet.setName(nameObjectw
											.getString("interestName"));
								}
								if (nameObjectw.has("catId")) {
									interet.setCatId(nameObjectw
											.getString("catId"));
								}
								for (int j = 0; j < Constants.valuesId.length; j++) {
									if (interet.getCatId().equalsIgnoreCase(Constants.valuesId[j])) {
										interet.setIcon(/*Constants.icon[j]*/Constants.iconHash.get(Constants.valuesId[j]));
										break;
									}
								}
								profileBean.interest.add(interet);
							}
						}
						return response;
					}else
						return response;
				} else if (RequestType.ADD_INTEREST == requestType) {

				} else if (RequestType.REMOVE_INTEREST == requestType) {

				}

			} catch (Exception e) {

				e.printStackTrace();
				someThingWentWrong(getResources().getString(R.string.something_went_wrong));

				return null;
			} catch (OutOfMemoryError e) {
				someThingWentWrong(getResources().getString(R.string.something_went_wrong));
				return null;
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (RequestType.FOLLWO_UNFOLLOW == requestType) {
					try {
						JSONObject jsonObjectT = new JSONObject(result);
						if (jsonObjectT.getString("status").equalsIgnoreCase(
								"1")) {

							if (jsonObjectT.has("unfollow")) {
								profileBean.followURL = jsonObjectT
										.getString("unfollow");
								findViewById(R.id.follow)
								.setBackgroundResource(
										R.drawable.profile_upfollow);
								((Button)findViewById(R.id.follow)).setText("         Unfollow");
								findViewById(R.id.follow).setTag(
										profileBean.getFollowURL());

							}
							if (jsonObjectT.has("follow")) {
								profileBean.followURL = jsonObjectT
										.getString("follow");
								findViewById(R.id.follow)
								.setBackgroundResource(
										R.drawable.profile_follow);
								((Button)findViewById(R.id.follow)).setText("         Follow");
								findViewById(R.id.follow).setTag(
										profileBean.getFollowURL());

							}

							// if(profileBean.isMediaFollower){
							// findViewById(R.id.follow).setBackgroundResource(R.drawable.profile_upfollow)
							// ;
							// findViewById(R.id.follow).setTag(profileBean.getFollowURL());
							// }else{
							// findViewById(R.id.follow).setBackgroundResource(R.drawable.profile_follow)
							// ;
							// findViewById(R.id.follow).setTag(profileBean.getFollowURL());
							// }
							//							BusinessProxy.sSelf.addPush.mContext = ProfileViewActivity.this ;
							showCustomMessage("Alert", jsonObjectT.getString("desc"),
									null,null,
									null) ;

							//							showSimpleAlert("Alert",
							//									jsonObjectT.getString("desc"));
						} else if (jsonObjectT.getString("status")
								.equalsIgnoreCase("2")) {
							showSimpleAlert("Alert",
									jsonObjectT.getString("desc"));
						}
					} catch (Exception e) {

					}

				} else if (RequestType.VIEW_PROFILE == requestType) {
					try{
						JSONObject jsonObjectT = new JSONObject(result);
						if (jsonObjectT.getString("status").equalsIgnoreCase("1")) {
							((TextView)findViewById(R.id.profile_link)).setText(profileBean.channelCount);
							user_channels.setVisibility(View.VISIBLE);
							((TextView)findViewById(R.id.profile_link)).setVisibility(View.VISIBLE);
							((TextView) findViewById(R.id.display_name)).setText(profileBean.getDisplayName());
							if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim())){
								((TextView) findViewById(R.id.display_number)).setVisibility(View.VISIBLE);
								((TextView) findViewById(R.id.display_number)).setText("+"+profileBean.getMobileNumber());
							}
							((TextView) findViewById(R.id.gender)).setText(profileBean.getGender() + " ");
							((TextView) findViewById(R.id.country)).setText(profileBean.getCountyCity());
							try{
								Spanned spannedContent = getSmiledText(ProfileViewActivity.this, profileBean.getStatustext());
								((TextView) findViewById(R.id.status_text)).setText(spannedContent);
							}catch (Exception e) {
								// TODO: handle exception
							}
							if(profileBean.getStatustext()!=null && profileBean.getStatustext().length()>0){
								findViewById(R.id.status_layout).setVisibility(View.VISIBLE);
								findViewById(R.id.status_text_layout).setVisibility(View.VISIBLE);
							}

							((TextView) findViewById(R.id.language))
							.setText(profileBean.getLanguage());

							if(profileBean.getLanguage()==null || profileBean.getLanguage().trim().length()<=0)
								findViewById(R.id.language_layout).setVisibility(View.GONE) ;

							((Button) findViewById(R.id.friends)).setText(profileBean
									.getNumberOfBuddies());
							((Button) findViewById(R.id.post)).setText(profileBean
									.getNumberOfPosts());
							((Button) findViewById(R.id.followers)).setText(profileBean
									.getNumberOfMediaFollowers());
							((Button) findViewById(R.id.i_like)).setText(profileBean
									.getNumberOfMediaFollowing());

							try{
								if(Integer.parseInt(profileBean
										.getNumberOfBuddies())<=0){
									findViewById(R.id.stats_media_layout_friend).setOnClickListener(null);
								}
							}catch (Exception e) {
								findViewById(R.id.stats_media_layout_friend).setOnClickListener(null);
							}
							try{
								if(Integer.parseInt(profileBean
										.getNumberOfPosts())<=0){
									findViewById(R.id.stats_post_layout).setOnClickListener(null);
								}
							}catch (Exception e) {
								findViewById(R.id.stats_post_layout).setOnClickListener(null);
							}try{
								if(Integer.parseInt(profileBean
										.getNumberOfMediaFollowers())<=0){
									findViewById(R.id.stats_followers_layout).setOnClickListener(null);
								}
							}catch (Exception e) {
								findViewById(R.id.stats_followers_layout).setOnClickListener(null);
							}
							try{
								if(Integer.parseInt(profileBean
										.getNumberOfMediaFollowing())<=0){
									findViewById(R.id.stats_ilike_layout).setOnClickListener(null);
								}
							}catch (Exception e) {
								findViewById(R.id.stats_ilike_layout).setOnClickListener(null);
							}
							//					findViewById(R.id.stats_media_layout_friend).setOnClickListener(this);
							//					findViewById(R.id.stats_post_layout).setOnClickListener(this);
							//					findViewById(R.id.stats_followers_layout).setOnClickListener(this);
							//					findViewById(R.id.stats_ilike_layout).setOnClickListener(this);

							if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim()) || profileBean.isFriend.equals("true")){
								findViewById(R.id.invite).setVisibility(View.GONE) ;
							}else{
								//						findViewById(R.id.invite).setVisibility(View.VISIBLE) ;
							}


							findViewById(R.id.invite).setOnClickListener(ProfileViewActivity.this) ;

							if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim()) || profileBean.isFriend.equals("false")){
								findViewById(R.id.unfriend).setVisibility(View.GONE) ;
							}else{
								//						findViewById(R.id.unfriend).setVisibility(View.VISIBLE) ;
							}

							if(profileBean.isMediaFollower){
								findViewById(R.id.follow).setBackgroundResource(R.drawable.profile_upfollow) ;
								findViewById(R.id.follow).setTag(profileBean.getFollowURL());
								((Button)findViewById(R.id.follow)).setText("         Un Follow");
							}else{
								findViewById(R.id.follow).setBackgroundResource(R.drawable.profile_follow) ;
								findViewById(R.id.follow).setTag(profileBean.getFollowURL());
								((Button)findViewById(R.id.follow)).setText("         Follow");
							}

							if(!profileBean.isBlocked){
								//btn_unblock
								blockUserButton.setBackgroundResource(R.drawable.block) ;
								//blockUserButton.setText(getString(R.string.block));
							}else{
								blockUserButton.setBackgroundResource(R.drawable.btn_unblock) ;
								//blockUserButton.setText(getString(R.string.unblock));
							}
							if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim())){
								blockUserButton.setVisibility(View.GONE);
							}

							findViewById(R.id.follow).setOnClickListener(ProfileViewActivity.this) ;

							if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim())){
								findViewById(R.id.Edit).setVisibility(View.VISIBLE) ;
								btn_status.setVisibility(View.VISIBLE) ;
								findViewById(R.id.follow).setVisibility(View.GONE) ;
								chat_profile_iv.setVisibility(View.GONE);
								blockUserButton.setVisibility(View.GONE);
								findViewById(R.id.share_profile).setVisibility(View.VISIBLE) ;
								findViewById(R.id.update_profile).setVisibility(View.VISIBLE) ;
								findViewById(R.id.send_message).setVisibility(View.GONE) ;
								findViewById(R.id.more).setVisibility(View.GONE) ;
							}else{
								findViewById(R.id.Edit).setVisibility(View.GONE) ;
								btn_status.setVisibility(View.GONE) ;
								blockUserButton.setVisibility(View.VISIBLE);
								chat_profile_iv.setVisibility(View.VISIBLE);
							}

							addSelectedItem();


							if (profileBean.pictureMediaList != null
									&& profileBean.pictureMediaList.size() > 0) {
								Content content = profileBean.pictureMediaList.get(0);//profileBean.pictureMediaList.size()-1
								loadProfilePic = new LoadProfilePic();
								loadProfilePic.execute(content.thumbUrl);
								if(profileBean.pictureMediaList.size() > 1){
									((TextView) findViewById(R.id.photo_count)).setText(""
											+ profileBean.pictureMediaList.size());
									((TextView) findViewById(R.id.photo_count))
									.setVisibility(View.VISIBLE);
								}
							}
							if (profileBean.audioMediaList != null
									&& profileBean.audioMediaList.size() > 0) {
								Content content = profileBean.audioMediaList.get(0);
								mVoicePath = content.contentUrl;
								audioRecordImageView.setImageResource(R.drawable.profile_listen);
							}else{
								if(SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim())){
									audioRecordImageView.setImageResource(R.drawable.profile_audio);
								}else{
									audioRecordImageView.setVisibility(View.GONE);
								}	
							}

							if (profileBean.audioMediaListStatus != null
									&& profileBean.audioMediaListStatus.size() > 0) {
								Content content = profileBean.audioMediaListStatus
										.get(0);
								findViewById(R.id.status_voice).setVisibility(
										View.VISIBLE);
								findViewById(R.id.status_voice).setTag(
										content.contentUrl);
								findViewById(R.id.status_voice).setOnClickListener(
										ProfileViewActivity.this);
								findViewById(R.id.status_layout).setVisibility(
										View.VISIBLE);
							} else
								findViewById(R.id.status_voice)
								.setVisibility(View.GONE);
							if (profileBean.pictureMediaListStatus != null
									&& profileBean.pictureMediaListStatus.size() > 0) {
								Content content = profileBean.pictureMediaListStatus.get(0);
								findViewById(R.id.status_image).setVisibility(
										View.VISIBLE);
								findViewById(R.id.status_layout).setVisibility(
										View.VISIBLE);

							} else
								findViewById(R.id.status_image)
								.setVisibility(View.GONE);

							if (profileBean.videoMediaListStatus != null
									&& profileBean.videoMediaListStatus.size() > 0) {
								Content content = profileBean.videoMediaListStatus.get(0);
								findViewById(R.id.status_video).setVisibility(
										View.VISIBLE);
								findViewById(R.id.status_layout).setVisibility(
										View.VISIBLE);
							} else
								findViewById(R.id.status_video)
								.setVisibility(View.GONE);



							/*Bitmap icon = null ;
							if(profileBean.getGender().equalsIgnoreCase(getString(R.string.male)))
								icon = BitmapFactory.decodeResource(context.getResources(),
										R.drawable.male_icon);
							else if(profileBean.getGender().equalsIgnoreCase(getString(R.string.female)))
								icon = BitmapFactory.decodeResource(context.getResources(),
										R.drawable.male_icon);
							else
								icon = BitmapFactory.decodeResource(context.getResources(),
										R.drawable.male_icon);*/

							//((ImageView)findViewById(R.id.user_image)).setImageBitmap(icon) ;
							((ImageView)findViewById(R.id.user_image)).setBackground(getResources().getDrawable(R.drawable.male_icon)) ;

							if(findViewById(R.id.status_layout).getVisibility()!=View.VISIBLE && SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.getUsername().trim())){
								findViewById(R.id.update_profile).setVisibility(View.VISIBLE) ;
							}
							else
								findViewById(R.id.update_profile).setVisibility(View.GONE) ;

							findViewById(R.id.container).setVisibility(View.VISIBLE);
							findViewById(R.id.container1).setVisibility(View.VISIBLE);


							if(mVoicePath != null && !mVoicePath.equals("")){
								playAvailableVoice();
								disableViews();
							}
							//Try here Loading the user channels.
							GetUserChannelTask userChannel = new GetUserChannelTask() ;
							userChannel.execute("http://www.yelatalkprod.com/tejas/feeds/api/group/user/publicchannel?user="+profileBean.getUsername());
						}
						else if (jsonObjectT.getString("status").equalsIgnoreCase("2")) {
							Toast.makeText(ProfileViewActivity.this, jsonObjectT.getString("desc"), Toast.LENGTH_LONG).show();
							onBackPressed();
						}
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				} else if (RequestType.REMOVE_INTEREST == requestType) {
				}
			}else
				onBackPressed() ;
			if (rTDialog != null && rTDialog.isShowing()) {
				rTDialog.dismiss();
				//				if (! ((Activity) BusinessProxy.sSelf.addPush.mContext).isFinishing()) 
				//					rTDialog.dismiss();

				rTDialog = null;
			}

			if(  SettingData.sSelf.getUserName()!=null && profileBean.username!=null &&( SettingData.sSelf.getUserName().trim().equalsIgnoreCase(profileBean.username.trim()))){
				add_friend.setVisibility(View.GONE);
			}
			checkFriending();
			try{
				if(!profileBean.getUserId().isEmpty()){
					Get_InterestTask blockUser = new Get_InterestTask();
					blockUser.execute(profileBean.getUserId());
				}
				else if(profileBean.userId!=null && !profileBean.userId.isEmpty()){
					Get_InterestTask blockUser = new Get_InterestTask();
					blockUser.execute(profileBean.userId);
				}
			}catch(Exception e){
				Log.v("Get_InterestTask Exception", ""+e);
			}
		}
	}

	public void parseContent(JSONArray jSONArray, int forContent) {
		try {
			if (forContent == 0)
				profileBean.pictureMediaList = new Vector<Content>();// 0
			if (forContent == 1)
				profileBean.audioMediaList = new Vector<Content>();// 1
			if (forContent == 2)
				profileBean.videoMediaList = new Vector<Content>();// 2
			if (forContent == 3)
				profileBean.pictureMediaListStatus = new Vector<Content>();// 0
			if (forContent == 4)
				profileBean.audioMediaListStatus = new Vector<Content>();// 1
			if (forContent == 5)
				profileBean.videoMediaListStatus = new Vector<Content>();// 2

			for (int i = 0; i < jSONArray.length(); i++) {
				JSONObject nameObjectw = jSONArray.getJSONObject(i);
				Content content = new Content();
				if (nameObjectw.has("contentId")) {
					content.contentId = nameObjectw.getString("contentId");
				}
				if (nameObjectw.has("contentType")) {
					content.contentId = nameObjectw.getString("contentType");
				}
				if (nameObjectw.has("contentUrl")) {
					content.contentUrl = nameObjectw.getString("contentUrl");
				}
				if (nameObjectw.has("thumbUrl")) {
					content.thumbUrl = nameObjectw.getString("thumbUrl");
				}
				if (content.contentId != null
						&& content.contentId.trim().equalsIgnoreCase("image")) {
					if (forContent == 0)
						profileBean.pictureMediaList.add(content);
					if (forContent == 3)
						profileBean.pictureMediaListStatus.add(content);
				}
				if (content.contentId != null
						&& content.contentId.trim().equalsIgnoreCase("audio")) {
					if (forContent == 1)
						profileBean.audioMediaList.add(content);
					if (forContent == 4)
						profileBean.audioMediaListStatus.add(content);
				}
				if (content.contentId != null
						&& content.contentId.trim().equalsIgnoreCase("video")) {
					if (forContent == 2)
						profileBean.videoMediaList.add(content);
					if (forContent == 5)
						profileBean.videoMediaListStatus.add(content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	LoadProfilePic loadProfilePic;


	@Override
	public void voiceRecordingStarted(String recordingPath) {
	}
	@Override
	public void voiceRecordingCompleted(final String recordedVoicePath) {
		if(!isCancelClick){
			if(dialog.isShowing())
				dialog.dismiss();
			mVoicePath = recordedVoicePath;
			ProfileViewActivity.this.handler.post(new Runnable() {

				@Override
				public void run() {
					audioRecordImageView.setImageResource(R.drawable.profile_listen);
					AudioUpdateTask audioTask = new AudioUpdateTask();
					audioTask.execute("Insert");
				}
			});
		}
		else
			mVoicePath = null;

	}



	private VoiceMedia  mVoiceMedia;

	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = 0;
		byte[] buffer = new byte[1024];
		while (read != -1) {
			read = in.read(buffer);
			if (read != -1)
				out.write(buffer, 0, read);
		}
		out.close();
		return out.toByteArray();
	}
	private class LoadProfilePic extends AsyncTask<String, Void, String> {

		Drawable drawable;

		@Override
		protected void onPreExecute() {
			Bitmap icon = null ;
			if(profileBean.getGender().equalsIgnoreCase("male"))
				icon = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.male);
			if(profileBean.getGender().equalsIgnoreCase("Female"))
				icon = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.female);
			((ImageView)findViewById(R.id.user_image)).setImageBitmap(icon) ;
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
				if(log)
					Log.i(TAG, "LoadProfilePic : doInBackground :: url : "+urls[0]);
				if(SettingData.sSelf.getUserName().equalsIgnoreCase(currentUser))
					myProfilePicURL = urls[0];
				getCacheDirectoryImage();
				String filename = String.valueOf(urls[0].hashCode());
				String extension = "jpg";
				filename = filename + "." + extension;
				File f = new File(cacheDirImage, filename);
				drawable = Drawable.createFromPath(f.getAbsolutePath());
				if(drawable==null){
					InputStream is = (InputStream) new URL(urls[0]).getContent();
					byte data[] = Utilities.inputStreamToByteArray(is);
					if(data.length>0)
					{
						filename = String.valueOf(urls[0].hashCode());
						extension = "jpg";
						filename = filename + "." + extension;
						f = new File(cacheDirImage, filename);
						writeFile(data, f, urls[0].hashCode());
						drawable = Drawable.createFromPath(f.getAbsolutePath());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				makeToast(e.toString());
				return null;
			} catch (OutOfMemoryError e) {
				makeToast(e.toString());
				return null;
			}
			return "";


		}

		@Override
		protected void onPostExecute(String result) {
			//

			ImageView i = ((ImageView) findViewById(R.id.user_image));
			if(drawable!=null){
				Bitmap bitmap=drawableToBitmap(drawable);
				i.setImageBitmap(bitmap);
			}
			//i.setImageDrawable(drawable);

			if(drawable==null){
				Bitmap icon = null ;
				if(profileBean.getGender().equalsIgnoreCase("male"))
					icon = BitmapFactory.decodeResource(context.getResources(),
							R.drawable.male);
				if(profileBean.getGender().equalsIgnoreCase("Female"))
					icon = BitmapFactory.decodeResource(context.getResources(),
							R.drawable.female);

				((ImageView)findViewById(R.id.user_image)).setImageBitmap(icon) ;
			}
		}
	}


	Bitmap bitmap;


	public static Bitmap drawableToBitmap (Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); 
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}


	private void writeFile(byte[] data, File f, int id) {

		FileOutputStream out = null;
		if (f != null && !f.exists() && data != null) {
			try {

				f.createNewFile();
				out = new FileOutputStream(f);
				out.write(data);

			} catch (Exception e) {
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}

	}


	public void onError(int i) {
		TextView tv = (TextView) findViewById(R.id.streemStatus);
		tv.setTextColor(0xffff0000);
		tv.setText("Unable to play please try later!");
	}


	@Override
	public void voicePlayStarted() {
		// TODO Auto-generated method stub'runon
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView tv = (TextView) findViewById(R.id.streemStatus);
				tv.setText("");
			}
		});

		super.voicePlayStarted();
	}


	@Override
	public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
		// TODO Auto-generated method stub

	}

	//------------------------------------------------------------------
	Dialog dialog;
	private boolean isCancelClick = false;
	AudioProgressWheel wheelProgress;
	private String mRecordedVoicePath;
	static boolean wheelRunning;
	private int progress = 0;
	//	private ImageView voiceBt;
	private Button cancelBt;
	final public static byte UI_STATE_IDLE = 2;
	private int iCurrentState = UI_STATE_IDLE;
	final public static byte UI_STATE_RECORDING = 5;
	final public static byte UI_STATE_PLAYING = 12;
	private boolean isTimeOver = false;
	private boolean isAudioVisible = false;
	private final byte RECORDING_MODE = 1;
	private final byte PLAY_MODE = 2;
	private TextView leftTimeView;
	private final int RECORDING_TIME = 2 * 60;
	public void showAudioDialog(final byte TYPE){
		progress = 0;
		isCancelClick = false;
		dialog = new Dialog(ProfileViewActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.player_dialog);
		//dialog.setCancelable(true);
		final Runnable wheelThread = new Runnable() 
		{
			public void run() {
				wheelRunning = false;
				while(!wheelRunning)
				{
					if(progress < RECORDING_TIME)
					{
						isTimeOver = false;
						wheelProgress.incrementProgress(RECORDING_TIME);
						progress++;
						if(TYPE == PLAY_MODE)
							setPlayLeftTime(mVoiceMedia.getMediaDuration(), mVoiceMedia.getCurrentMediaTime());
						else if(TYPE == RECORDING_MODE){
							recordLeftTime(RECORDING_TIME - progress);
						}
					}
					else
					{
						isTimeOver = true;
						wheelRunning = true;
						iCurrentState = UI_STATE_IDLE;
						ProfileViewActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								stopRecording();
							}
						});
					}
					try 
					{
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				wheelRunning = true;
			}
		};

		wheelProgress  = (AudioProgressWheel) dialog.findViewById(R.id.player_wheel);
		wheelProgress.setWheelType(AudioProgressWheel.RECORD_WHEEL);

		if(mRecordedVoicePath != null && TYPE == PLAY_MODE){
			wheelProgress.resetCount();
			startPlaying(null);
			Thread s = new Thread(wheelThread);
			s.start();
			if(wheelProgress != null)
				wheelProgress.setBackgroundResource(R.drawable.profile_listen);
		}else{
			iCurrentState = UI_STATE_IDLE;
			wheelProgress.resetCount();
			startRecording(null);
			Thread s = new Thread(wheelThread);
			s.start();
			if(wheelProgress != null)
				wheelProgress.setBackgroundResource(R.drawable.profile_audio);
		}
		leftTimeView = (TextView)dialog.findViewById(R.id.left_time);

		Button cancelBt = (Button)dialog.findViewById(R.id.button_01);
		cancelBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isCancelClick = true;
				dialog.dismiss();
				wakeLock.release();
			}
		});

		final Button doneReset = (Button)dialog.findViewById(R.id.button_02);
		if(TYPE == RECORDING_MODE){
			doneReset.setText(getString(R.string.done));
		}else if(TYPE == PLAY_MODE){
			doneReset.setText(getString(R.string.reset));
		}
		doneReset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isCancelClick = false;
				progress = 0;
				// TODO Auto-generated method stub
				if(doneReset.getText().toString().equalsIgnoreCase(getString(R.string.done))){
					wheelRunning = true;
					stopRecording();
					if(wheelProgress != null){
						wheelProgress.setBackgroundResource(R.drawable.profile_audio);
						wheelProgress.resetCount();
					}
					//					voiceBt.setImageResource(R.drawable.profile_listen);

					dialog.dismiss();
				}else if(doneReset.getText().toString().equalsIgnoreCase(getString(R.string.reset))){// if(TYPE == PLAY_MODE){
					dialog.dismiss();
					wheelRunning = true;
					stopPlaying(v);
					if(wheelProgress != null)
						wheelProgress.setBackgroundResource(R.drawable.profile_listen);
					//					voiceBt.setImageResource(R.drawable.profile_listen);
					File file = new File(mRecordedVoicePath);
					if (file.exists()) {
						file.delete();
					}
					//					voiceBt.setImageResource(R.drawable.profile_audio);
					mRecordedVoicePath = null;
				}
				wakeLock.release();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if(isCancelClick){
					progress = 0;
					if(TYPE == RECORDING_MODE){
						wheelRunning = true;
						stopRecording();
						if(wheelProgress != null)
							wheelProgress.setBackgroundResource(R.drawable.profile_audio);
						mRecordedVoicePath = null;
						//						voiceBt.setImageResource(R.drawable.profile_audio);
						dialog.dismiss();
					}else if(TYPE == PLAY_MODE){
						wheelRunning = true;
						stopPlaying(null);
						if(wheelProgress != null)
							wheelProgress.setBackgroundResource(R.drawable.profile_listen);
						dialog.dismiss();
					}
				}
			}
		});
		dialog.show();
	}
	public void setPlayLeftTime(final int total, final int time){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				leftTimeView.setText(Utilities.converMiliSecond(time)+" secs");
			}
		});
	}
	public void recordLeftTime(final int time){
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				leftTimeView.setText(String.format("%02d:%02d", (time / 60), (time % 60))+" secs");
				//				leftTimeView.setText(time+" secs");
			}
		});
	}
	private void startRecording(View v)
	{
		if (mVoiceMedia == null)
			mVoiceMedia = new VoiceMedia(ProfileViewActivity.this, this);
		mVoiceMedia.startRecording(120);
		iCurrentState = Constants.UI_STATE_RECORDING;
		Log.d(TAG, "startRecording()");
	}
	private void stopRecording()
	{
		if (mVoiceMedia != null)
			mVoiceMedia.stopRec();
		iCurrentState = Constants.UI_STATE_IDLE;
		Log.i(TAG, "stopRecording()");
	}
	private void startPlaying(View v) {
		if(mRecordedVoicePath != null)
		{
			mVoiceMedia.startNewPlaying(mRecordedVoicePath, null, false);
			iCurrentState = Constants.UI_STATE_PLAYING;
		}
		Log.i(TAG, "Playing started..");
	}
	private void stopPlaying(View v) {
		if (mVoiceMedia != null)
			mVoiceMedia.stopVoicePlaying();
		iCurrentState = Constants.UI_STATE_IDLE;
		Log.i(TAG, "stopPlaying()");
	}
	//MaNOJ
	//Date 03-12-2015
	public class BlockUserRequest extends AsyncTask<String, Integer, String> {
		ProgressDialog rTDialog;
		@Override
		protected void onPreExecute() {
			rTDialog = ProgressDialog.show(ProfileViewActivity.this, "", getString(R.string.loading_dot), true);
			rTDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServer(params[0]);
			/*if (strData != null  && (strData.trim().indexOf("1") != -1)) {
				return strData;
			}*/
			if(strData != null)
			{
				if(strData.contains("desc") ){
					return strData;
				}
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!= null)
			{
				try{
					JSONObject  jobj = new JSONObject(result);
					if(result.contains("desc")){
						if(result.contains("status")){
							String stst = jobj.optString("status").toString();
							if(stst.equals("1")){
								if(profileBean.isBlocked){
									profileBean.isBlocked = false;
									//blockUserButton.setText(getString(R.string.block));
									blockUserButton.setBackgroundResource(R.drawable.block) ;
								}else
								{
									profileBean.isBlocked = true;
									blockUserButton.setBackgroundResource(R.drawable.btn_unblock) ;
									//blockUserButton.setText(getString(R.string.unblock));
								}
							}
						}
						String desc = jobj.optString("desc").toString();
						Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
					}
				}catch(Exception e){

				}
			}
			rTDialog.dismiss();
		}
	}

	private String postDataOnServer(String userId){
		String strData = null;
		String url_a = null;
		//if(systemMessageAction){
		//url = msgId;
		//}
		//else{
		/*if(type.equals(DELETE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/delete";
			}else if(type.equals(SHARE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/post/share";
			}else if(type.equals(REPORT_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/report";
			}else if(type.equals(SPAM_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/notspam";
			}*/
		//}
		//http://www.yelatalkprod.com/tejas/feeds/user/block?blockeeUserId=15769184
		if(!profileBean.isBlocked){
			url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/block";
		}else
		{
			url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/unblock";
		}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("blockeeUserId", new String(userId.trim().getBytes("UTF-8"))));
			/*	if(systemMessageAction){
			}
			else{
				if(type.equals(DELETE_REQUEST) || type.equals(REPORT_REQUEST) || type.equals(SPAM_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("groupName", new String(param.getBytes("UTF-8"))));
				}else if(type.equals(SHARE_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("sharedTo", new String(param.getBytes("UTF-8"))));
				}
			}*/
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
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
	// End block user 
	@Override
	public void onTaskCallback(Object parameter, byte mRequestObjNo) {
		// TODO Auto-generated method stub
		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case HTTP_TIMEOUT:
			break;
		case DATA_CALLBACK:
			parseUserChannelXML(null);
			break;
		case ERROR_CALLBACK:
			break;
		}
	}

	private class GetUserChannelTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			//			try{
			//				showLoadingDialog();
			//
			//			}catch(Exception e){
			//
			//			}
			if(loading_bar != null)
				loading_bar.setVisibility(View.VISIBLE);
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String url = urls[0];
				Hashtable<String, String> headerParam = new Hashtable<String, String>();
				headerParam.put("RT-Params", ClientProperty.RT_PARAMS);

				String responseString = "" ;
				HttpClient httpclientE = new DefaultHttpClient();

				if(urls[0].indexOf("?")!=-1)
					urls[0] = urls[0] + "&"+BusinessProxy.thumbInfo;
				else
					urls[0] = urls[0] + "?"+BusinessProxy.thumbInfo;

				urls[0] = urls[0] + "&textMode=base64&limit=10";

				HttpPost httppostw = new HttpPost( urls[0]);
				try {
					/*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					String key = "category";
					String value ="News";
					nameValuePairs.add(new BasicNameValuePair(key, new String(value.getBytes("UTF-8"))));*/
					//	httppostw.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
					httppostw.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
					HttpResponse response = httpclientE.execute(httppostw);
					HttpEntity r_entity = response.getEntity();


					responseString = EntityUtils.toString(r_entity);
					//					System.out.println(responseString);

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mResponseDataSuggested = responseString.getBytes();
				return responseString;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(ProfileViewActivity.this, getString(R.string.something_went_wrong));
					}
				});
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			if(mCallBackTimer!=null)
				mCallBackTimer.cancel();
			mCallBackTimer = new Timer();
			if(loading_bar != null)
				loading_bar.setVisibility(View.GONE);
			if(response!=null){
				mCallBackTimer.schedule(new OtsSchedularTask(ProfileViewActivity.this, DATA_CALLBACK, (byte)0), 0);
			}
			else 
			{
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(ProfileViewActivity.this, ERROR_CALLBACK, (byte)0), 0);
			}
			super.onPostExecute(response);
		}
	}


	//  Add Friend
	//
	//
	private class AddFriendTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = postDataFriendsOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(ProfileViewActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			if(response!= null)
			{String stst="";
			try{
				JSONObject  jobj = new JSONObject(response);
				if(response.contains("message")){
					if(response.contains("status")){
						stst = jobj.optString("status").toString();
					}
					if(stst.equals("success")){
						String desc = jobj.optString("message").toString();
						Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
						if(profileBean.isFriend().equals("false")){
							profileBean.isFriend = "pending";
							add_friend.setBackgroundResource(R.drawable.request_pending);

						}else if(profileBean.isFriend().equals("true")){
							profileBean.isFriend = "false";
							add_friend.setBackgroundResource(R.drawable.icon_add_friend);
						}

					}else if(stst.equals("error"))
					{
						String ytError_msg = jobj.optString("ytErrors").toString();
						JSONArray jSONArray =  new JSONArray(ytError_msg);
						for (int i = 0; i < jSONArray.length(); i++) {
							JSONObject nameObjectw = jSONArray.getJSONObject(0);
							String desc = nameObjectw.optString("message").toString();
							Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
						}
					}

					//profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
					//profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
				}
			}catch(Exception e){

			}
			}

		}
	}
	//
	//Date:01-03-2016
	//Name: MAnoj Singh
	//Accept Friend request
	//
	//
	private class AcceptFriendTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				showLoadingDialog();

			}catch(Exception e){

			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = AcceptFriendsOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("message") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(ProfileViewActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialog!=null)
				rTDialog.dismiss();
			if(response!= null)
			{String stst="";
			try{
				JSONObject  jobj = new JSONObject(response);
				if(response.contains("message")){
					if(response.contains("status")){
						stst = jobj.optString("status").toString();
					}
					if(stst.equals("success")){
						String desc = jobj.optString("message").toString();
						Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
						if(profileBean.isFriend().equals("respond")){
							profileBean.isFriend = "true";
							add_friend.setBackgroundResource(R.drawable.delete_friend);
						}

					}else if(stst.equals("error"))
					{
						String ytError_msg = jobj.optString("ytErrors").toString();
						JSONArray jSONArray =  new JSONArray(ytError_msg);
						for (int i = 0; i < jSONArray.length(); i++) {
							JSONObject nameObjectw = jSONArray.getJSONObject(0);
							String desc = nameObjectw.optString("message").toString();
							Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
						}
					}

					//profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
					//profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
				}
			}catch(Exception e){

			}
			}

		}
	}

	private String AcceptFriendsOnServer(String userId){
		String strData = null;
		String url_a = null;
		url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/acceptrequest";
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("buddyUserId", new String(userId.trim().getBytes("UTF-8"))));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
	}

	//END ACCEPT FRIEND

	private String postDataFriendsOnServer(String userId){
		String strData = null;
		String url_a = null;
		//if(systemMessageAction){
		//url = msgId;
		//}
		//else{
		/*if(type.equals(DELETE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/delete";
			}else if(type.equals(SHARE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/post/share";
			}else if(type.equals(REPORT_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/report";
			}else if(type.equals(SPAM_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/notspam";
			}*/
		//}
		//http://www.yelatalkprod.com/tejas/feeds/user/block?blockeeUserId=15769184
		if(profileBean.isFriend().equals("false")){
			url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/add";

		}else if(profileBean.isFriend().equals("true")){
			url_a= "http://" +Urls.TEJAS_HOST + "/tejas/feeds/user/friend/delete";
		}
		//	profileBean.setFriend(!profileBean.isFriend());
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url_a);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			nameValuePair.add(new BasicNameValuePair("buddyUserId", new String(userId.trim().getBytes("UTF-8"))));
			/*	if(systemMessageAction){
			}
			else{
				if(type.equals(DELETE_REQUEST) || type.equals(REPORT_REQUEST) || type.equals(SPAM_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("groupName", new String(param.getBytes("UTF-8"))));
				}else if(type.equals(SHARE_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("sharedTo", new String(param.getBytes("UTF-8"))));
				}
			}*/
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Url Encoding the POST parameters
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// writing error to Log
			e.printStackTrace();
		}

		// Making HTTP Request
		try {
			HttpResponse response = httpClient.execute(httpPost);	
			int responceCode = response.getStatusLine().getStatusCode();

			if(responceCode == 200){
				InputStream ins = response.getEntity().getContent();
				strData = IOUtils.read(ins);
				ins.close();

			}
			// writing response to log
		} catch (ClientProtocolException e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		} catch (Exception e) {
			// writing exception to log
			e.printStackTrace();
			return strData;
		}

		return strData;
	}

	protected void showLoadingDialog() {
		//		showAnimationDialog("", getString(R.string.please_wait_while_loadin), true,
		//				PROGRESS_CANCEL_HANDLER);
		if(rTDialog==null && !rTDialog.isShowing())
			rTDialog = ProgressDialog.show(ProfileViewActivity.this, "", getString(R.string.loading_dot), true);
	}
	protected void showLoadingDialogs() {
		//		showAnimationDialog("", getString(R.string.please_wait_while_loadin), true,
		//				PROGRESS_CANCEL_HANDLER);
		if(rTDialogs==null && !rTDialogs.isShowing())
			rTDialogs = ProgressDialog.show(ProfileViewActivity.this, "", getString(R.string.loading_dot), true);
	}

	//
	//

	//Date : 25-02-2016
	//Name : Manoj Singh

	private class Get_InterestTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {			
			try{
				if(loading_barr != null)
					loading_barr.setVisibility(View.VISIBLE);

			}catch(Exception e){
				Log.e("EXCEPTION", ""+e);
			}
		}
		@Override
		protected String doInBackground(String... urls) {
			try{
				String strData = GetInterestOnServer(urls[0]);
				if(strData != null)
				{
					if(strData.contains("userInterests") ){
						return strData;
					}
				}
				return null;
			}catch (final Exception e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						AppUtil.showTost(ProfileViewActivity.this, getString(R.string.something_went_wrong));//+"\n"+e.getLocalizedMessage()) ;
					}
				});
				return null;
			}

		}

		@Override
		protected void onPostExecute(String response) {
			if(rTDialogs!=null)
				rTDialogs.dismiss();
			if(loading_barr != null)
				loading_barr.setVisibility(View.GONE);
			if(response!= null)
			{
				String stst="";
				try{
					JSONObject  jobj = new JSONObject(response);

					if(jobj.has("userInterests"))
					{  GRID_DATA =  new ArrayList<String>();
					GRID_DATA_ID =  new ArrayList<String>();
					GRID_DATA_IMG =  new ArrayList<String>();
					GRID_DATA_IMG_INT =  new ArrayList<Integer>(); 
					String ytError_msg = jobj.optString("userInterests").toString();
					JSONArray jSONArray =  new JSONArray(ytError_msg);
					for (int i = 0; i < jSONArray.length(); i++) {
						JSONObject nameObjectw = jSONArray.getJSONObject(i);
						String interestName = nameObjectw.optString("interestName").toString();
						stst = stst +interestName+",";
						GRID_DATA.add(interestName);
						GRID_DATA_ID.add(nameObjectw.optString("interestId").toString());
						//GRID_DATA_IMG.add(interestName);
						GRID_DATA_IMG_INT.add(getResources().getIdentifier(interestName.toLowerCase(), "drawable", getPackageName()));
						//Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
					}
					//
					if(findViewById(R.id.Edit).isShown()){
					KeyValue.setString(ProfileViewActivity.this, KeyValue.INTEREST, stst);
					}
					listViewIntrestes.setAdapter(mAllCategoryAdapter);
					mAllCategoryAdapter.notifyDataSetChanged();
					}

					/*
					JSONObject  jobj = new JSONObject(response);
					if(response.contains("message")){
						if(response.contains("status")){
							 stst = jobj.optString("status").toString();
						}
						if(stst.equals("success")){
							String desc = jobj.optString("message").toString();
							Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
						}else if(stst.equals("error"))
						{
							String ytError_msg = jobj.optString("ytErrors").toString();
							JSONArray jSONArray =  new JSONArray(ytError_msg);
							for (int i = 0; i < jSONArray.length(); i++) {
								JSONObject nameObjectw = jSONArray.getJSONObject(0);
								String desc = nameObjectw.optString("message").toString();
								Toast.makeText(ProfileViewActivity.this, desc,  Toast.LENGTH_LONG).show();
							}
						}
						//profileTask = new ProfileTask(RequestType.VIEW_PROFILE);
						//profileTask.execute(URL_VIEW_PROFILE + getIntent().getStringExtra("USERID") + "&mode=view&" + BusinessProxy.sSelf.thumbInfo);
					}
					 */}catch(Exception e){
						 Log.e("",""+e);
					 }
			}else
			{
				layout_interests.setVisibility(View.GONE);
			}
			if(GRID_DATA.size()<=0){
				layout_interests.setVisibility(View.GONE);
			}else
			{
				layout_interests.setVisibility(View.VISIBLE);
			}
		}
	}

	private String  GetInterestOnServer(String userId){
		String responseStr = null;
		try {				
			String url = "http://www.yelatalkprod.com/tejas/feeds/user/interest/get?user_id="+userId;

			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
			try {
				HttpResponse responseHttp = client.execute(httpget);
				if (responseHttp != null) {
					responseStr = EntityUtils.toString(responseHttp.getEntity());
				}
			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}
		} catch (Exception e) {
			Log.e("GetInterestOnServer ==>",""+e);
		}
		return responseStr;
	}



}
