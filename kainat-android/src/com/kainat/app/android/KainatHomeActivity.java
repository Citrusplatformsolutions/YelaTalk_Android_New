package com.kainat.app.android;

/*
 public class MediaActivity {

 }
 */

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.NavDrawerListAdapter;
import com.kainat.app.android.bean.NavDrawerItem;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.HttpSynch;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.helper.LandingPageDatabaseHelper;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.FeedManager;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.format.SettingData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class KainatHomeActivity extends TabActivity implements OnClickListener {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	// sur04sep : password
	private ActionBarDrawerToggle mDrawerToggle;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private TabHost tabHost;
	private static ProgressBar channelRefreshLoader;
	public static KainatHomeActivity mediaActivity;
	ImageView img_search;
	int counter_hitoutsidedata = 0;
	ImageButton menu, group_chat, notification, chat, create_community,
			menu_three;
	public int sharingFromOutside = 0;
	public static TextView counter;
	ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisc(true).
			resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.male_icon)
			.showImageOnFail(R.drawable.male_icon)
			.showImageOnLoading(R.drawable.male_icon).build();

	@SuppressWarnings("deprecation")	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab);
		
		counter = (TextView) findViewById(R.id.counter);
		counter.setVisibility(View.GONE);
		SaveRemember();
		if (BusinessProxy.sSelf.sqlDB == null) {
			LandingPageDatabaseHelper database = null;
			if (database == null) {
				database = new LandingPageDatabaseHelper(
						KainatHomeActivity.this);
			}
			if (BusinessProxy.sSelf.sqlDB == null) {
				BusinessProxy.sSelf.sqlDB = database.getWritableDatabase();
			}
			// BusinessProxy.sSelf.initializeDatabase();
		}
		SettingData.sSelf.setRemember(true);
		group_chat = (ImageButton) findViewById(R.id.group_chat);
		notification = (ImageButton) findViewById(R.id.notification);
		chat = (ImageButton) findViewById(R.id.chat);
		img_search = (ImageView) findViewById(R.id.img_search);
		create_community = (ImageButton) findViewById(R.id.create_community);
		menu_three = (ImageButton) findViewById(R.id.menu_three);
		Button btn_create_channel = (Button)findViewById(R.id.btn_create_channel);
		btn_create_channel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(KainatHomeActivity.this,
						CreateCommunityActivity.class);
				startActivity(intent);
				
			}
		});
		group_chat.setOnClickListener(this);
		img_search.setOnClickListener(this);

		notification.setOnClickListener(this);
		chat.setOnClickListener(this);
		create_community.setOnClickListener(this);
		menu_three.setOnClickListener(this);

		menu = (ImageButton) findViewById(R.id.menu);
		menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.openDrawer(mDrawerList);
				if (UIActivityManager.myProfilePicURL != null) {
					imageLoader.displayImage(UIActivityManager.myProfilePicURL, imgAvatar, options);
				/*	imageManager.download(UIActivityManager.myProfilePicURL,
							imgAvatar, null, ImageDownloader.TYPE_THUMB_BUDDY);*/
				}else{
					imageLoader.displayImage(SettingData.sSelf.getUserName(), imgAvatar, options);
					//imageManager.download(SettingData.sSelf.getUserName(), imgAvatar, null,ImageDownloader.TYPE_THUMB_BUDDY);
				}
			}
		});
		initLeftMenu();
		mediaActivity = this;
		tabHost = getTabHost();
		addTab("inbox", getString(R.string.discover).toUpperCase(),
				R.drawable.tab_caht,
				new Intent(this, KainatDiscoverNewActivity.class));// 0
		addTab("community", getString(R.string.my_communities).toUpperCase(),
				R.drawable.tab_com, new Intent(this,
						KainatCommunityActivity.class));// 2
		KeyValue.setBoolean(this, KeyValue.VERIFIED, true);
		if (/* UIActivityManager.PushNotification == 2 && */UIActivityManager.TabValue == 2) {
			tabHost.setCurrentTab(UIActivityManager.TAB_COMMUNITY);
			UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
		} else {
			tabHost.setCurrentTab(UIActivityManager.TAB_CHAT);
			UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_CHAT;
		}
		group_chat.setVisibility(View.GONE);
		notification.setVisibility(View.GONE);
		chat.setVisibility(View.GONE);

		create_community.setVisibility(View.GONE);
		menu_three.setVisibility(View.GONE);
		img_search.setVisibility(View.VISIBLE);
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				tabChangeEvent(tabId);

				/*
				 * if(tabId.equalsIgnoreCase("inbox")){
				 * group_chat.setVisibility(View.VISIBLE);
				 * notification.setVisibility(View.VISIBLE);
				 * chat.setVisibility(View.VISIBLE);
				 * 
				 * create_community.setVisibility(View.GONE);
				 * menu_three.setVisibility(View.GONE);
				 * img_search.setVisibility(View.GONE);
				 * channelRefreshLoader.setVisibility(View.GONE);
				 * UIActivityManager.mCurrentSelectedTab =
				 * UIActivityManager.TAB_CHAT;
				 * 
				 * }else if(tabId.equalsIgnoreCase("community")){
				 * group_chat.setVisibility(View.GONE); //
				 * notification.setVisibility(View.GONE);
				 * chat.setVisibility(View.GONE);
				 * 
				 * create_community.setVisibility(View.VISIBLE);
				 * //menu_three.setVisibility(View.VISIBLE);
				 * img_search.setVisibility(View.VISIBLE);
				 * UIActivityManager.mCurrentSelectedTab =
				 * UIActivityManager.TAB_COMMUNITY; }
				 */
			}
		});
		tabHost.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// System.out.println("-----------onFocusChange> "+v.getId());
			}
		});
		findViewById(R.id.contact).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(KainatHomeActivity.this,
						KainatContactActivity.class);
				intent.putExtra("HEADER_TITLE", getString(R.string.friend));
				startActivity(intent);
			}
		});

		tabHost.setCurrentTab(UIActivityManager.mCurrentSelectedTab);
		if (UIActivityManager.mCurrentSelectedTab == UIActivityManager.TAB_CHAT) {
			tabChangeEvent("inbox");
		} else {
			tabChangeEvent("community");
		}
		// Set crashlytics additional parameters for logged in user here -
		try {
			new ClientProperty(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * //UserID, UserName, Display Name
		 * Crashlytics.setUserIdentifier(""+BusinessProxy.sSelf.getUserId());
		 * Crashlytics.setUserName(SettingData.sSelf.getDisplayName());
		 * if(SettingData.sSelf.getEmail() != null)
		 * Crashlytics.setUserEmail(SettingData.sSelf.getEmail()); //Set Mobile
		 * Model if(ClientProperty.CLIENT_PARAMS != null)
		 * Crashlytics.setString("mob_prop",
		 * "UN##"+SettingData.sSelf.getUserName
		 * ()+"::"+ClientProperty.CLIENT_PARAMS);
		 */

		// Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Home Screen");
		// t.set(Field.customDimension(1), ""+BusinessProxy.sSelf.getUserId());
		t.set("&uid", "" + BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	
	//ManojSIngh
	// Date 30-09-2015
	void handleSendMultipleImages(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// Update UI to reflect multiple images being shared
			String con_str = "";
			for(int i = 0;i < imageUris.size() ;i++){
				con_str = con_str + getRealPathFromURI(this,imageUris.get(i)) + ",";
			}
			UIActivityManager.sharedImage = con_str;
		}
	}
	
	void handleSendMultipleVideo(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// Update UI to reflect multiple images being shared
			String con_str = "";
			for(int i = 0;i < imageUris.size() ;i++){
				if(imageUris.get(i).toString().startsWith("content://")){
					//	UIActivityManager.sharedVideo = getRealPathFromURI(this,""+imageUris[i]);
					con_str = con_str +getRealPathFromURI(this,imageUris.get(i))  + ",";
				}else
					con_str = con_str + imageUris.get(i) + ",";
			}
			UIActivityManager.sharedVideo = con_str;
			if(UIActivityManager.sharedVideo != null && UIActivityManager.sharedVideo.startsWith("file")){
				UIActivityManager.sharedVideo = UIActivityManager.sharedVideo.replaceFirst("file://", "");
			}
			
		}
	}
	void handleSendMultipleAudio(Intent intent) {
		ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
		if (imageUris != null) {
			// Update UI to reflect multiple images being shared
			String con_str = "";
			for(int i = 0;i < imageUris.size() ;i++){
				con_str = con_str + imageUris.get(i) + ",";
			}
			UIActivityManager.sharedAudio = con_str;
		}
	}
	
	
	
	void handleSendText(Intent intent) {
		UIActivityManager.sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	}
	
	void handleSendImage(Intent intent) {
		Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (imageUri != null) {
			if(imageUri.toString().startsWith("content://")){
			UIActivityManager.sharedImage = getRealPathFromURI(this,imageUri);
			}else
				UIActivityManager.sharedImage = imageUri.toString();
			// Update UI to reflect image being shared
		}
		
		/*if(UIActivityManager.sharedImage != null && UIActivityManager.sharedImage.startsWith("file")){
			UIActivityManager.sharedImage = UIActivityManager.sharedImage.replaceFirst("file://", "");
		}*/
	}
	void handleSendVideo(Intent intent) {
		Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (videoUri != null) {
			if(videoUri.toString().startsWith("content://")){
			UIActivityManager.sharedVideo = getRealPathFromURI(this,videoUri);
			}else
			UIActivityManager.sharedVideo = videoUri.toString();
			//UIActivityManager.sharedVideo = UIActivityManager.sharedVideo.replaceAll("%20", " ");
			// Update UI to reflect image being shared
		}
		if(UIActivityManager.sharedVideo != null && UIActivityManager.sharedVideo.startsWith("file")){
			UIActivityManager.sharedVideo = UIActivityManager.sharedVideo.replaceFirst("file://", "");
		}
	}
	
	void handleSendAudio(Intent intent) {
		Uri audioUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if (audioUri != null) {
		//	UIActivityManager.sharedAudio = getRealPathFromURI(this,audioUri);
			// Update UI to reflect image being shared
			
			UIActivityManager.sharedAudio = audioUri.toString();
			UIActivityManager.sharedAudio = UIActivityManager.sharedAudio.replaceAll("%20", " ");
		}
		if(UIActivityManager.sharedAudio != null && UIActivityManager.sharedAudio.startsWith("file")){
			UIActivityManager.sharedAudio = UIActivityManager.sharedAudio.replaceFirst("file://", "");
		}
	}
	
	
	public String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try { 
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
	//END

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(KainatHomeActivity.this)
				.reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		GoogleAnalytics.getInstance(KainatHomeActivity.this)
				.reportActivityStop(this);
		//UIActivityManager.counter_hitoutsidedata = 0;
	}

	public static void refreshNotificationCounter() {
		// Set counter for system messages
		if (FeedRequester.systemMessage > 0) {
			try {
				counter.setVisibility(View.GONE);
				counter.setText("" + FeedRequester.systemMessage);
			} catch (NullPointerException nex) {

			}
		}
	}

	public static void showChannelRefreshLoader() {
		// Commented for now, may be used later, when data is optimized
		// if(channelRefreshLoader != null)
		// channelRefreshLoader.setVisibility(View.VISIBLE);
	}

	public static void hideChannelRefreshLoader() {
		if (channelRefreshLoader != null)
			channelRefreshLoader.setVisibility(View.GONE);
	}

	//ImageDownloader imageManager = new ImageDownloader();
	CImageView imgAvatar;
	LayoutInflater mInflater;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// now getIntent() should always return the last received intent
	}

	@Override
	public void onBackPressed() {
	/*	if(UIActivityManager.sharingFromOutside == 1)
		{
			UIActivityManager.sharingFromOutside = 0;
			finish();
		}*/
		HttpSynch.getInstance().cancel();
		moveTaskToBack(true);
		
		// super.onBackPressed();
	}

	public void tabChangeEvent(String tabId) {
		if (tabId.equalsIgnoreCase("inbox")) {
			group_chat.setVisibility(View.GONE);
			notification.setVisibility(View.GONE);
			chat.setVisibility(View.GONE);

			create_community.setVisibility(View.GONE);
			menu_three.setVisibility(View.GONE);
			img_search.setVisibility(View.VISIBLE);
			channelRefreshLoader.setVisibility(View.GONE);
		//	 KainatDiscoverNewActivity.self.theMethodYouWantToCall();
			UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_CHAT;

		} else if (tabId.equalsIgnoreCase("community")) {
			group_chat.setVisibility(View.GONE);
			// notification.setVisibility(View.GONE);
			chat.setVisibility(View.GONE);

			create_community.setVisibility(View.GONE);
			// menu_three.setVisibility(View.VISIBLE);
			img_search.setVisibility(View.VISIBLE);
			UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
		}
	}

	public void removeNotification() {
		try {
			NotificationManager mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			for (Integer in : GcmIntentService.notificationID) {
				mNotificationManager.cancel(in);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	
	@Override
	protected void onResume() {
		super.onResume();
		//if(UIActivityManager.counter_hitoutsidedata == 0 ){
		getDatafromOutside();
		//}
		
		Bundle extras = getIntent().getExtras();
		int cid = 0;
		boolean fromPush = false;
		String groupURL = null;
		String groupName = null;
		String groupID = null;
		if (extras != null || UIActivityManager.pushData != null) {
			if (UIActivityManager.pushData != null) {
				extras = UIActivityManager.pushData;
				UIActivityManager.pushData = null;
			}
			UIActivityManager.PushNotification = 2;
			if (extras.containsKey("CONVERSATION_ID")) {
				cid = Integer.parseInt(extras.getString("CONVERSATION_ID"));
				UIActivityManager.TabValue = 1;
			}
			if (extras.containsKey("GROUP_ID")) {
				groupURL = "http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/api/group/message/"
						+ extras.getString("GROUP_ID");
				groupName = extras.getString("GROUP_NAME");
				groupID = extras.getString("GROUP_ID");
				cid = 0;
				UIActivityManager.TabValue = 2;
				Utilities.setString(this, Constants.LAST_VISITED_GROUPID,
						groupID);
			} else {
				// cid = 0;
			}
			fromPush = true;
			removeNotification();
		}

		// Set counter for system messages
		if (FeedRequester.systemMessage > 0) {
			counter.setVisibility(View.GONE);
			counter.setText("" + FeedRequester.systemMessage);
			navMenuIcons = getResources().obtainTypedArray(
					R.array.nav_drawer_icons);

			if(navDrawerItems != null && navDrawerItems.size()>3){
				if( FeedRequester.systemMessage > 0 ) 
				{ 
					navDrawerItems.remove(1);
					navDrawerItems.add(1,new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,""+FeedRequester.systemMessage));
				}else
				{
					navDrawerItems.remove(1);
					navDrawerItems.add(1,new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
				}
				adapter.notifyDataSetChanged();
			}
		}
		// communityAdapter in able to join after registration
		BusinessProxy.sSelf.setNetActive(true);
		try {
			if (KeyValue.getBoolean(this, KeyValue.LANGUAGE_CHANGE) == true) {
				KeyValue.setBoolean(this, KeyValue.LANGUAGE_CHANGE, false);
				finish();
				Intent intenta = new Intent(this, KainatHomeActivity.class);
				startActivity(intenta);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (BusinessProxy.sSelf.getUserId() == 0) {
			if (fromPush) {
				UIActivityManager.pushData = extras;
				fromPush = false;
			}
			removeNotification();
			sessionExpired();
		}
		// Open Conversation
		if (fromPush && BusinessProxy.sSelf.isRegistered()
				&& BusinessProxy.sSelf.getUserId() != 0) {
			if (getCurrentActivity() instanceof KainatDiscoverNewActivity) {
				if (cid > 0) {
					Intent intenta = new Intent(KainatHomeActivity.this,KainatInboxAvtivity.class);
					intenta.putExtra("TYPE", 2);
					intenta.putExtra("CID", cid);
					intenta.putExtra("IsP2P", 1);
					startActivity(intenta);
					
					
				/*	tabHost.setCurrentTab(UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_CHAT);
					KainatInboxAvtivity activity = (KainatInboxAvtivity) getLocalActivityManager()
							.getActivity("inbox");
					activity.openConversation(cid);
					setSelectionForCurrentTab(UIActivityManager.TAB_CHAT);*/
				} else if (groupURL != null) {
					
				/*	Intent intenta = new Intent(KainatHomeActivity.this,KainatInboxAvtivity.class);
					intenta.putExtra("TYPE", 2);
					intenta.putExtra("CID", cid);
					intenta.putExtra("IsP2P", 2);
					intenta.putExtra("GROUP_URL_PUSH", groupURL);
					intenta.putExtra("GROUP_ID_PUSH", groupID);
					intenta.putExtra("GROUP_NAME_PUSH", groupName);
					startActivity(intenta);*/
					tabHost.setCurrentTab(UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY);
					KainatCommunityActivity activity = (KainatCommunityActivity) getLocalActivityManager()
							.getActivity("community");
					activity.openChatFromPush(groupURL, groupID, groupName, "");
					extras.clear();
					groupURL = null;
					setSelectionForCurrentTab(UIActivityManager.TAB_COMMUNITY);
					
				}
			} else if (getCurrentActivity() instanceof KainatCommunityActivity) {
				if (cid > 0) {
					group_chat.setVisibility(View.GONE);
					notification.setVisibility(View.GONE);
					chat.setVisibility(View.GONE);
					create_community.setVisibility(View.GONE);
					menu_three.setVisibility(View.GONE);
					img_search.setVisibility(View.VISIBLE);
					Intent intenta = new Intent(KainatHomeActivity.this,KainatInboxAvtivity.class);
					intenta.putExtra("TYPE", 2);
					intenta.putExtra("CID", cid);
					intenta.putExtra("IsP2P", 1);
					startActivity(intenta);
				/*	tabHost.setCurrentTab(UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_CHAT);
					KainatInboxAvtivity activity = (KainatInboxAvtivity) getLocalActivityManager()
							.getActivity("inbox");
					activity.openConversation(cid);
					setSelectionForCurrentTab(UIActivityManager.TAB_CHAT);*/
				} else if (groupURL != null) {
					UIActivityManager.mCurrentSelectedTab = UIActivityManager.TAB_COMMUNITY;
					KainatCommunityActivity activity = (KainatCommunityActivity) getLocalActivityManager()
							.getActivity("community");
					activity.openChatFromPush(groupURL, groupID, groupName, "");
					extras.clear();
					groupURL = null;
					setSelectionForCurrentTab(UIActivityManager.TAB_COMMUNITY);

				}
			}
			fromPush = false;
		}
		//mDrawerList.invalidateViews();
		if (!UIActivityManager.startChannelRefresh) {
			UIActivityManager.startChannelRefresh = true;
			RefreshService.setRefreshTime(5000);
		}
		

	}

	private void getDatafromOutside() {
		// TODO Auto-generated method stub

		   Intent intent = getIntent();
			String action = intent.getAction();
			String type = intent.getType();
			//intent.removeExtra(android.content.Intent.ACTION_SEND);
			if (type != null && Intent.ACTION_SEND.equals(action) ) {
				UIActivityManager.sharingFromOutside = 1;
				UIActivityManager.counter_hitoutsidedata = 1;
				if ("text/plain".equals(type)) {
					
					handleSendText(intent); // Handle text being sent
				} else if (type.startsWith("image/")) {
					if(type.startsWith("image/jpg") || type.startsWith("image/jpeg") || type.startsWith("image/png")  || type.startsWith("image/*")){
					handleSendImage(intent); // Handle single image being sent
					}
					else
					{
						Toast.makeText(this, "This Image format not supported", Toast.LENGTH_LONG).show();	
					}
				}else if(type.startsWith("video/")){
					if(type.startsWith("video/3gpp")|| type.startsWith("video/mp4") || type.startsWith("video/*")){
					handleSendVideo(intent); 
					}else
					{
						Toast.makeText(this, "This Video format not supported", Toast.LENGTH_LONG).show();	
					}
				}else if(type.startsWith("audio/"))  {
					if(type.startsWith("audio/aac-adts") || type.startsWith("audio/mpeg")|| type.startsWith("audio/amr")  ||type.startsWith("audio/aac")){
					handleSendAudio(intent); 
					}else{
						Toast.makeText(this, "This audio format not supported", Toast.LENGTH_LONG).show();
					}
				}
			} else if (type != null && Intent.ACTION_SEND_MULTIPLE.equals(action) ) {
				UIActivityManager.sharingFromOutside = 1;
				UIActivityManager.counter_hitoutsidedata = 1;
				if (type.startsWith("image/")) {
					handleSendMultipleImages(intent); // Handle multiple images being sent
				}
				if(type.startsWith("video/")){
						if(type.startsWith("video/3gpp")|| type.startsWith("video/mp4")|| type.startsWith("video/*"))
					{
						handleSendMultipleVideo(intent); 
						}else
						{
							Toast.makeText(this, "This Video format not supported", Toast.LENGTH_LONG).show();	
						}
				}
				
				if(type.startsWith("audio/")){
					//if(type.startsWith("video/mp4") || type.startsWith("video/3gp")){
					if(true){
						handleSendMultipleAudio(intent); 
						}else
						{
							Toast.makeText(this, "This Video format not supported", Toast.LENGTH_LONG).show();	
						}
				}
			} else {
				// Handle other intents, such as being started from the home screen
			}
			intent.setAction(null);
			intent.setType(null);
	}

	private void setSelectionForCurrentTab(int tab) {
		if (tab == UIActivityManager.TAB_COMMUNITY) {
			group_chat.setVisibility(View.GONE);
			notification.setVisibility(View.GONE);
			chat.setVisibility(View.GONE);
			create_community.setVisibility(View.GONE);
			menu_three.setVisibility(View.GONE);
			img_search.setVisibility(View.VISIBLE);
		} else if (tab == UIActivityManager.TAB_CHAT) {
			group_chat.setVisibility(View.GONE);
			notification.setVisibility(View.GONE);
			chat.setVisibility(View.GONE);
			create_community.setVisibility(View.GONE);
			menu_three.setVisibility(View.GONE);
			img_search.setVisibility(View.VISIBLE);

		}
	}

	private void sessionExpired() {
		finish();
		FeedManager.clearAll();
		Intent intent = new Intent(this, SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void setTab(int tabId) {
		tabHost.setCurrentTab(tabId);
	}

	public int getTab() {
		return tabHost.getCurrentTab();
	}

	@SuppressWarnings("deprecation")
	private void addTab(String tabID, String labelId, int drawableId,
			Intent intent) {
		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.customtabs, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setVisibility(View.GONE);
		icon.setImageResource(drawableId);
		channelRefreshLoader = (ProgressBar) tabIndicator
				.findViewById(R.id.refresh_loader);
		// icon.setBackgroundResource(R.drawable.tab_indicator_gen);
		tabHost.addTab(tabHost.newTabSpec(tabID).setIndicator(tabIndicator)
				.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

	}

	public void updateTitle(String a) {
		final TextView label = (TextView) tabHost.getTabWidget().getChildAt(1)
				.findViewById(R.id.title);
		label.setText(a);
	}

	@SuppressLint("NewApi")
	private void initLeftMenu() {
		// /

		try {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
			// load slide menu items
			navMenuTitles = getResources().getStringArray(
					R.array.nav_drawer_items);

			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
					GravityCompat.START);
			// SlidingDrawer drawer = getActionBar().getna
			// sm = getSlidingMenu();
			// sm.setShadowDrawable(R.drawable.shadowbar);

			mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			final View convertView = mInflater.inflate(
					R.layout.left_menu_profile, null);

			((TextView) convertView.findViewById(R.id.name))
					.setText(SettingData.sSelf.getDisplayName() + "");

			((TextView) convertView.findViewById(R.id.location))
					.setText(SettingData.sSelf.getUserName() + ":"
							+ SettingData.sSelf.getPassword());// SettingData.sSelf.getCountry()
																// + "");
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(KainatHomeActivity.this,
							ProfileViewActivity.class);
					if (SettingData.sSelf.getUserName() != null)
						intent.putExtra("USERID", SettingData.sSelf
								.getUserName().toLowerCase());
					startActivity(intent);
					mDrawerLayout.closeDrawers();
				}
			});
			mDrawerLayout.setOnDragListener(new OnDragListener() {

				@Override
				public boolean onDrag(View v, DragEvent event) {
					imgAvatar = (CImageView) convertView
							.findViewById(R.id.thumb);

			/*		ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

						@Override
						public void onThumbnailResponse(ThumbnailImage value,
								byte[] data) {
							try {
								BitmapDrawable background = new BitmapDrawable(
										value.mBitmap);
								imgAvatar.setImageDrawable(background);
								imgAvatar.invalidate();
								mDrawerList.invalidateViews();
							} catch (Exception e) {
							}
						}
					};*/
					if (UIActivityManager.myProfilePicURL != null){
						/*imageManager.download(
								UIActivityManager.myProfilePicURL, imgAvatar,
								handler, ImageDownloader.TYPE_THUMB_BUDDY);*/
						imageLoader.displayImage(UIActivityManager.myProfilePicURL, imgAvatar, options);
					}
					else{
						/*imageManager.download(SettingData.sSelf.getUserName(),
								imgAvatar, handler,
								ImageDownloader.TYPE_THUMB_BUDDY);*/
						imageLoader.displayImage(SettingData.sSelf.getUserName(), imgAvatar, options);
					}
					return false;
				}
			});
			imgAvatar = (CImageView) convertView.findViewById(R.id.thumb);
		/*	imgAvatar.setGender(SettingData.sSelf.getGender() + 1);
			ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

				@Override
				public void onThumbnailResponse(ThumbnailImage value,
						byte[] data) {
					try {
						BitmapDrawable background = new BitmapDrawable(
								value.mBitmap);
						imgAvatar.setImageDrawable(background);
						imgAvatar.invalidate();
						mDrawerList.invalidateViews();
					} catch (Exception e) {
					}
				}
			};*/
			if (UIActivityManager.myProfilePicURL != null){
	/*	a		imageManager.download(UIActivityManager.myProfilePicURL,
						imgAvatar, handler, ImageDownloader.TYPE_THUMB_BUDDY);
		*/
		imageLoader.displayImage(UIActivityManager.myProfilePicURL, imgAvatar, options);
			}
			else{
				imageLoader.displayImage(SettingData.sSelf.getUserName(), imgAvatar, options);
			}
			/*	imageManager.download(SettingData.sSelf.getUserName(),
						imgAvatar, handler, ImageDownloader.TYPE_THUMB_BUDDY);
			}*/
			mDrawerList.addHeaderView(convertView);
			// nav drawer icons from resources
			navMenuIcons = getResources().obtainTypedArray(
					R.array.nav_drawer_icons);

			navDrawerItems = new ArrayList<NavDrawerItem>();

			//Counter 
			//
			/*int count_notification = FeedRequester.systemMessage;
			if (FeedRequester.systemMessage > 0) {
				try {
					counter.setVisibility(View.VISIBLE);
					counter.setText("" + FeedRequester.systemMessage);
				} catch (NullPointerException nex) {

				}
			}*/
			//
			//
			// adding nav drawer items to array
			// Home
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
					.getResourceId(0, -1)));
			// Notification
			if( FeedRequester.systemMessage > 0 ) 
			{
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,""+FeedRequester.systemMessage));
			}else
			{
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
			}
			// Chat
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
					.getResourceId(2, -1)));
			// Discover
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
					.getResourceId(3, -1)));
			// Find People
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
					.getResourceId(4, -1)));
			// Photos
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
					.getResourceId(5, -1)));
			// Communities, Will add a counter here
			/*navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons
					.getResourceId(6, -1)));*/
			// Invite
			navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons
					.getResourceId(7, -1)));
			// Pages
			// navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
			// navMenuIcons
			// .getResourceId(4, -1)));
			// // What's hot, We will add a counter here
			// navDrawerItems.add(new NavDrawerItem(navMenuTitles[5],
			// navMenuIcons
			// .getResourceId(5, -1), true, "50+"));

			// Recycle the typed array
			navMenuIcons.recycle();

			mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

			// setting the nav drawer list adapter
			adapter = new NavDrawerListAdapter(getApplicationContext(),
					navDrawerItems);
			mDrawerList.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// enabling action bar app icon and behaving it as toggle button
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);
		// getActionBar().setIcon(R.drawable.action_menu) ;

		// @drawable/
		// navDrawerItems = new ArrayList<NavDrawerItem>();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mDrawerLayout.openDrawer(mDrawerList);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 1:// home
				tabHost.setCurrentTab(1);
				mDrawerLayout.closeDrawers();

				break;
			case 2://notification
				Intent intent = new Intent(KainatHomeActivity.this,
						OtherMessageActivity.class);
				intent.putExtra("What", 1);
				intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
				startActivityForResult(intent, "OtherMessageActivity".hashCode());
				mDrawerLayout.closeDrawers();

				break;
			case 3://chat
				Intent intenta = new Intent(KainatHomeActivity.this,KainatInboxAvtivity.class);
				intenta.putExtra("TYPE", 2);
				startActivity(intenta);
				mDrawerLayout.closeDrawers();

				break;
			case 4:// contact

				Intent intentc = new Intent(KainatHomeActivity.this,
						KainatContactActivity.class);
				intentc.putExtra("HEADER_TITLE", getString(R.string.friend));
				startActivity(intentc);
				mDrawerLayout.closeDrawers();

				break;
			case 5:// search

				Intent intents = new Intent(KainatHomeActivity.this,
						SearchCommunityActivity.class);
				intents.putExtra("TYPE", 2);
				startActivity(intents);
				mDrawerLayout.closeDrawers();

				break;
			case 6:// Invite
				intent = new Intent(KainatHomeActivity.this,
						KainatInviteSelection.class);
				startActivity(intent);
				mDrawerLayout.closeDrawers();
				break;

			case 7:// Discover

				intent = new Intent(KainatHomeActivity.this,
						KainatSettings.class);
				/*intent = new Intent(KainatHomeActivity.this,
						KainatDiscoverActivity.class);*/
				startActivity(intent);
				mDrawerLayout.closeDrawers();
				break;
			case 8: // Setting

				// User.getInstance().clean();
				// SettingData.sSelf.deleteSettings();
				// intent = new
				// Intent(KainatHomeActivity.this,ContactActivity.class);
				intent = new Intent(KainatHomeActivity.this,
						KainatSettings.class);
				startActivity(intent);
				// Toast.makeText(KainatHomeActivity.this,
				// getString(R.string.comeing_soon), 3000).show();
				mDrawerLayout.closeDrawers();
				break;
			}
		}

	}

	Button notifCount;

	// public boolean onCreateOptionsMenu(Menu menu) {
	// // super.onCreateOptionsMenu(menu, inflater);
	// menu.clear();
	// mInflater.inflate(R.menu.action_bar_menu, null);
	//
	// // MenuItem item = menu.findItem(R.id.saved_badge);
	// // MenuItemCompat.setActionView(item, R.layout.feed_update_count);
	// // View view = MenuItemCompat.getActionView(item);
	// // notifCount = (Button)view.findViewById(R.id.notif_count);
	// // notifCount.setText(String.valueOf(mNotifCount));
	//
	// return super.onCreateOptionsMenu(menu);
	// }

	private void setNotifCount(int count) {
		// mNotifCount = count;
		// supportInvalidateOptionsMenu();
	}

	QuickAction quickAction;
	int idsBuzz[] = new int[] { 1, 2, 3 };
	String idsNameBuzz[] = new String[] { // "Pending request",
	"Recommended", "Search" };

	@Override
	protected void onPause() {
		((ImageView) findViewById(R.id.chat))
				.setImageResource(R.drawable.x_chat_creation);
		super.onPause();
	}

	@Override
	public void onClick(View v) {

		idsNameBuzz = new String[] {// getString(R.string.pending_request),
		getString(R.string.recommended), getString(R.string.search) };

		switch (v.getId()) {
		case R.id.chat:
			Intent intent = new Intent(KainatHomeActivity.this,
					KainatContactActivity.class);
			intent.putExtra("HEADER_TITLE", getString(R.string.start_chat_lbl));
			startActivity(intent);
			break;
		case R.id.notification:
			// intent = new Intent(KainatHomeActivity.this,
			// OtherMessageActivity.class);
			// startActivity(intent);
			intent = new Intent(KainatHomeActivity.this,
					OtherMessageActivity.class);
			intent.putExtra("What", 1);
			intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
			startActivityForResult(intent, "OtherMessageActivity".hashCode());
			FeedRequester.systemMessage = 0;
			counter.setVisibility(View.GONE);
			break;

		case R.id.group_chat:
			/*
			 * intent = new Intent(KainatHomeActivity.this,
			 * KainatContactActivity.class); intent.putExtra("HEADER_TITLE",
			 * getString(R.string.friend)); startActivity(intent);
			 */
			intent = new Intent(KainatHomeActivity.this,
					KainatContactActivity.class);
			intent.putExtra("HEADER_TITLE",
					getString(R.string.create_group_lbl));
			intent.putExtra("group", true);
			startActivity(intent);
			break;
		case R.id.img_search:
			/*
			 * intent = new Intent(KainatHomeActivity.this,
			 * KainatContactActivity.class); startActivity(intent);
			 */
			
			//MANOJ REMOVE COMMENT
			DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
			intent = new Intent(KainatHomeActivity.this,
					SearchCommunityActivity.class);
			intent.putExtra("TYPE", 3);
			startActivity(intent);
		/*	Intent intents = new Intent(KainatHomeActivity.this,InterestActivity.class);
			intents.putExtra("REG_FLOW", true);
			startActivity(intents);*/
			break;

		case R.id.create_community:
			intent = new Intent(KainatHomeActivity.this,
					CreateCommunityActivity.class);
			startActivity(intent);
			mDrawerLayout.closeDrawers();
			;
			break;

		case R.id.menu_three:
			action(idsBuzz, idsNameBuzz, (byte) 4);
			quickAction.show(v);
			break;

		}
	}

	public void action(int ids[], String[] name, final byte type) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, true,
				name.length);

		for (int i = 0; i < name.length; i++) {
			ActionItem nextItem = new ActionItem(ids[i], name[i], null);
			quickAction.addActionItem(nextItem);
		}

		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);
						// if(actionId==1){
						// Intent intent = new Intent(KainatHomeActivity.this,
						// CommunityActivity.class);
						// intent.putExtra("TYPE", 1);
						// startActivity(intent);
						// }
						// else
						if (actionId == 1) {
//							Intent intent = new Intent(KainatHomeActivity.this, CommunityActivity.class);
//							DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
//							intent.putExtra("TYPE", 2);
//							startActivity(intent);
//							overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
						} else if (actionId == 2) {

							// DMKeys.NEW_INTENT
							DataModel.sSelf
									.storeObject(DMKeys.NEW_INTENT, true);
							Intent intent = new Intent(KainatHomeActivity.this,
									SearchCommunityActivity.class);
							intent.putExtra("TYPE", 2);
							startActivity(intent);
						}
					}
				});

		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		finish();
		Intent intent = new Intent(KainatHomeActivity.this,
				KainatHomeActivity.class);
		intent.putExtra("TYPE", 2);
		super.onConfigurationChanged(newConfig);
	}

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, KainatHomeActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.defaultrtsystem)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	public void bindInvite() {

	}
	public void SaveRemember(){
		SharedPreferences.Editor editor = this.getSharedPreferences("SettingData", Context.MODE_PRIVATE).edit();
		editor.putBoolean("isremember",true);
		editor.commit();
	}
}
