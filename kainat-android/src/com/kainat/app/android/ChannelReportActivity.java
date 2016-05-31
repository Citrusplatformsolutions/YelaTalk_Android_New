package com.kainat.app.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.text.ClipboardManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonObject;
import com.kainat.app.android.CommunityProfileNewActivity.ReportedRequest;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.constanst.PictureTypes;
import com.kainat.app.android.constanst.VideoTypes;
import com.kainat.app.android.constanst.VoiceTypes;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.helper.CommunityChatDB;
import com.kainat.app.android.helper.CommunityMessageTable;
import com.kainat.app.android.helper.CommunityTable;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.uicontrol.MultiPhotoViewer;
import com.kainat.app.android.uicontrol.SlideShowScreen;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeed.Entry;
import com.kainat.app.android.util.CommunityFeed.ReportedByUser;
import com.kainat.app.android.util.CommunityFeedParser;
import com.kainat.app.android.util.CompressImage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.DateFormatUtils;
import com.kainat.app.android.util.EmoticonInf;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.FileDownloadResponseHandler;
import com.kainat.app.android.util.FileDownloader;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ImageLoader;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.RTDialog;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class ChannelReportActivity  extends UIActivityManager implements
Runnable, OnScrollListener, OnSchedularListener, OnClickListener, HttpSynchInf,
EmoticonInf, ThumbnailReponseHandler, FileDownloadResponseHandler, 
EmojiconsFragment.OnEmojiconBackspaceClickedListener,
EmojiconGridFragment.OnEmojiconClickedListener {
	private static final String TAG 						= CommunityChatActivity.class.getSimpleName();
	private static final int CONTENT_ID_VOICE 				= 7000;
	private static final int CONTENT_ID_PICTURE 			= 8000;
	private static final int CONTENT_ID_VIDEO 				= 9000;
	private static final int CONTENT_ID_VIDEO_CANCEL		= 6001;
	private static final int CONTENT_ID_VOICE_UPDATE 		= 7001;
	private static final byte POSITION_PICTURE 				= 0; 
	private static final byte POSITION_CAMERA_PICTURE 		= 1;
	private static final byte POSITION_MULTI_SELECT_PICTURE = 4;
	private static final byte POSITION_VOICE 				= 2;
	private static final byte POSITION_VIDEO 				= 3;
	private static final byte POSITION_PICTURE_RT_CANVAS 	= 5;
	private static final byte POSITION_TALKING_PIC      	= 6;
	private boolean isShowSmiley 							= false;
	Handler handler											= new Handler();
	private static final byte ERROR_CALLBACK 				= 2;
	private static final int CHUNK_LENGTH 					= 4 * 1024;
	private static final byte HTTP_TIMEOUT 					= 3;
	private static final int LOADING_ID 					= -1000;
	private static final byte ACTUAL_AUDIO_DATA_CALLBACK 	= 6;
	private static final byte ACTUAL_VIDEO_DATA_CALLBACK 	= 7;
	private static final byte ACTUAL_IMAGE_DATA_CALLBACK 	= 8;
	private static final byte ACTUAL_REFRESH_DATA_CALLBACK 	= 9;
	private static final byte XML_DATA_CALLBACK 			= 1;
	long MAX_IMAGE_ATTACH_SIZE 								= BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
	private static byte DATA_CALLBACK 						= XML_DATA_CALLBACK;
	private static byte DATA_CALLBACK_TEMP 					= XML_DATA_CALLBACK;
	Hashtable<String, Drawable> cacheImgIcon 				= new Hashtable<String, Drawable>();
	Hashtable<String, byte[]> cache 						= new Hashtable<String, byte[]>();
	private String mVoicePath, mVideoPath;
	private Vector<String> mPicturePath 					= new Vector<String>();
	private Vector<Integer> mPicturePathId 					= new Vector<Integer>();
	private VoiceMedia mVoiceMedia;
	private ImageLoader mImageLoader;
	private String cameraImagePath 							= null;
	private ListView mMainList;
	private CommunityChatAdapter mMediaAdapter;
	private CommunityFeed feed;
	private CommunityFeed oldFeed;
	private int iLoggedUserID;
	private Thread mThread;
	private boolean lodingCanceled 							= false;
	private String mCurrentScreen;
	private String mPostURL;
	private boolean mIsRunning 								= true;
	private byte[] mResponseData;
	private byte[] mResponseDataRefresh;
	private HttpConnectionHelper helper 					= null;
	private int responseCode;
	private Timer mCallBackTimer;
	private Button btn_join_leave_chat;
	private View loading 									= null;
	private boolean requestednextPage 						= false;
	private boolean replyToSender 							= false;
	private String replyMessageId 							= null;
	public String replyTo 									= "";
	public String replyToFirstName 							= "";
	public String replyToLastName 							= "";
	private Payload payloadData 							= new Payload();
	private Timer refreshTimer;
	private int lastListSelectedIndex 						= 0;
	private boolean needToScroll, justin;
	Vector<Entry> vector 									= new Vector<Entry>();
	private int POST_IMAGE_SIZE 							= 220;
	private CommunityFeed.Entry entry 						= null;
	private CommunityFeed.Entry profileEntry 				= null;
	private final byte READY 								= 0;
	private final byte BUSY 								= 1;
	private byte STATE 										= READY;
	boolean cancelOperation 								= true;
	LinearLayout mLoadingBar,linearlyout_write,linearlyout_read,layout_broadcast;
	SeekBar bar;
	int totalFeedOnServer 									= 0;
	boolean activityStop 									= false;
	boolean frontCam, rearCam;
	private LinearLayout.LayoutParams IMAGE_DIMENSION = new LinearLayout.LayoutParams(
			POST_IMAGE_SIZE, POST_IMAGE_SIZE);
	TextView loadingView;
	boolean feedLoadedFirstTime = false;
	boolean switchedToScreenWhileLoading = false;
	boolean systemMessageAction,systemMessageReportAction;
	String deleteMessageAPI;
	String deleteMessageMessageID;
	//	boolean sendingMediaMessage;
	int uploadingPercentage;
	boolean isVideoDownloading;
	boolean videoDownloadCancelled;
	private static final Pattern COM_NAME_PAT = Pattern.compile(".*comname[=](.*)");

	int voCounter = 1;
	int autoPlayIsPlaying = 1;

	int optionIDsTop[] = new int[] { 1,3,2};
	String idsNameOptionsTop[] = null;
	FrameLayout smileyView = null;
	View attachment_panel ;
	private String titleName;
	RTDialog rTDialog;
	boolean log = false;
	private int CHAT_REFRESH_TIME = 5000;
	private int CHAT_MIN_REFRESH_TIME = 500;
	private int CHAT_NORMAL_REFRESH_TIME = 5000;
	private int CHAT_MAX_REFRESH_TIME = 10000;
	//	private CommunityFeed.Entry senderEntry = null;
	private static final int MEDIA_TYPE_VIDEO = 1;
	private static final int MEDIA_TYPE_IMAGE = 2;
	private static final int MEDIA_TYPE_AUDIO = 3;
	LinearLayout media_play_layout;
	Dialog dialog;
	private String groupID = null;
	private boolean voiceAutoPlay = false;
	CommunityChatDB CCB ;
	boolean refreshStarted;
	private final int CHANNEL_MSG_MAX_COUNT = 10;
	private final String BIG_THUMB_INFO = "iThumbFormat=300x300&vThumbFormat=300x300&iFormat=600x600";
	private boolean wasAppInBG;
	ViewHolder viewHolder = null;
	Pattern EMOJI_CHARS = Pattern.compile("[\\uF601-\\uF64F]*+", Pattern.UNICODE_CASE);

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		justin = true;
		idsNameOptionsTop = new String[] {getString(R.id.profile),getString(R.string.followers_u), getString(R.id.cancel)};
		idsNameOptionsTop = new String[] {
				getString(R.string.profile),
				getString(R.string.followers_u),
				getString(R.string.cancel) };
		iLoggedUserID  = BusinessProxy.sSelf.getUserId();
		mThread = new Thread(this);
		mThread.start();
		setContentView(R.layout.community_chat);
		UIActivityManager.counter_hitoutsidedata = 0;
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).
				resetViewBeforeLoading(true)
				.showImageForEmptyUri(null)
				.showImageOnFail(null)
				.showImageOnLoading(null).build();
		CCB = new CommunityChatDB(this);

		//Gallary
		((ImageView) findViewById(R.id.photo_gallary)).setBackgroundResource(R.drawable.icon_gallery);
		//Camera
		((ImageView) findViewById(R.id.camera)).setBackgroundResource(R.drawable.icon_photo);
		//Video
		((ImageView) findViewById(R.id.video_record)).setBackgroundResource(R.drawable.icon_video);
		//Doodle
		((ImageView) findViewById(R.id.chatScreen_doodle)).setBackgroundResource(R.drawable.icon_doodle);


		//Talking Pics
		((ImageView) findViewById(R.id.talking_pic)).setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.name_talking_pic)).setVisibility(View.VISIBLE);
		((ImageView) findViewById(R.id.talking_pic)).setBackgroundResource(R.drawable.icon_talk_pic);
		//Audio
		((ImageView) findViewById(R.id.audio_file)).setBackgroundResource(R.drawable.attach_audio);

		attachment_panel = findViewById(R.id.attachment_panel);
		btn_join_leave_chat = (Button)findViewById(R.id.btn_join_leave_chat);
		attachment_panel.setVisibility(View.INVISIBLE);
		btn_join_leave_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				joinLeaveCommunity();
			}
		});
		findViewById(R.id.attachement).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(RTMediaPlayer.isPlaying()){
					RTMediaPlayer.reset();
					RTMediaPlayer.clear();
				}
				if(attachment_panel.getVisibility()!=View.VISIBLE){
					attachment_panel.setVisibility(View.VISIBLE);
					Utilities.startAnimition(attachment_panel.getContext(), attachment_panel, R.anim.grow_from_top);
					((ImageView)v).setImageResource(R.drawable.ic_action_attach_2) ;
				}
				else{
					attachment_panel.setVisibility(View.INVISIBLE);
					Utilities.startAnimition(attachment_panel.getContext(), attachment_panel, R.anim.shrink_from_bottom);
					((ImageView)v).setImageResource(R.drawable.attachment_unsel) ;
				}
			}
		});

		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		smileyView = (FrameLayout) findViewById(R.id.emojicons);
		setEmojiconFragment(false);
		PackageManager pm = getPackageManager();
		frontCam = pm.hasSystemFeature("android.hardware.camera.front");
		rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		media_play_layout = (LinearLayout) findViewById(R.id.media_play_layout);
		findViewById(R.id.menu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ImageView)findViewById(R.id.menu)).setImageResource(R.drawable.x_options_sel);
				action(optionIDsTop, idsNameOptionsTop, (byte) 3);
				quickAction.show(v);
			}
		});

		bar = (SeekBar) findViewById(R.id.mediavoicePlayingDialog_progressbar);
		loadingView = new TextView(this);
		loadingView.setOnClickListener(this);
		mMainList = (ListView) findViewById(R.community_chat.chatList);
		mMainList.setOnScrollListener(this);

		loadingView.setText(R.string.load_more);
		loadingView.setId(LOADING_ID);
		loadingView.setWidth(250);
		loadingView.setHeight(60);
		loadingView.setTextColor(getResources().getColor(R.color.tag_text_color));
		loadingView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		loadingView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		loadingView.setPadding(20, 20, 20, 20);
		loadingView.setBackgroundResource(R.drawable.loadmorebutton);
		LinearLayout lLayout = new LinearLayout(getBaseContext());
		lLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		lLayout.setPadding(0, 3, 0, 0);
		lLayout.addView(loadingView);

		mImageLoader = ImageLoader.getInstance(this);
		mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_RT);
		mLoadingBar = (LinearLayout) findViewById(R.id.loading_linearlayout);
		linearlyout_write = (LinearLayout) findViewById(R.id.linearlyout_write);
		linearlyout_read  = (LinearLayout) findViewById(R.id.linearlyout_read);
		layout_broadcast  = (LinearLayout) findViewById(R.id.layout_broadcast);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		((TextView) findViewById(R.community_chat.title)).setOnClickListener(profileClick);
		((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isShowSmiley){
					smileyView.setVisibility(View.GONE);
					isShowSmiley = !isShowSmiley;
				}

			}
		});
		Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox), this);
		image_count = ((TextView) findViewById(R.id.image_count));
		int density = getResources().getDisplayMetrics().densityDpi;
		switch (density)
		{
		case DisplayMetrics.DENSITY_LOW:
			POST_IMAGE_SIZE = 80;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(POST_IMAGE_SIZE, POST_IMAGE_SIZE);
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			POST_IMAGE_SIZE = 100;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(POST_IMAGE_SIZE, POST_IMAGE_SIZE);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			POST_IMAGE_SIZE = 120;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(POST_IMAGE_SIZE + 15, POST_IMAGE_SIZE + 15);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			POST_IMAGE_SIZE = 180;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(2 * POST_IMAGE_SIZE, 2 * POST_IMAGE_SIZE);
			break;
		case DisplayMetrics.DENSITY_TV:
			POST_IMAGE_SIZE = 200;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(2 * POST_IMAGE_SIZE, 2 * POST_IMAGE_SIZE);
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			POST_IMAGE_SIZE = 200;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(2 * POST_IMAGE_SIZE, 2 * POST_IMAGE_SIZE);
			break;
		default:
			POST_IMAGE_SIZE = 120;
			IMAGE_DIMENSION = new LinearLayout.LayoutParams(POST_IMAGE_SIZE + 15, POST_IMAGE_SIZE + 15);
			break;
		}

		mMediaAdapter = new CommunityChatAdapter(this);
		mMainList.addHeaderView(lLayout);
		mMainList.setAdapter(mMediaAdapter);

		//Remove Notifications
		removeNotification();
		mVoiceMedia = new VoiceMedia(this, this);
		systemMessageAction = getIntent().getBooleanExtra("SYSTEM_MSG_VIEW", false);
		systemMessageReportAction = getIntent().getBooleanExtra("SYSTEM_MSG_VIEW_REPORT", false);

		if(systemMessageAction){
			getIntent().removeExtra("SYSTEM_MSG_VIEW");
			deleteMessageAPI = getIntent().getStringExtra("DELETE_MESSAGE_URL");
			deleteMessageMessageID = getIntent().getStringExtra("DELETE_MESSAGE_ID");
			getIntent().removeExtra("DELETE_MESSAGE_URL");
			getIntent().removeExtra("DELETE_MESSAGE_ID");
			//Header Options Gone
			((CImageView) findViewById(R.id.comm_profile_image)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.headerpart_3)).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.attachement)).setVisibility(View.GONE);
			//Bottom Bar media and text entry gone
			((LinearLayout)findViewById(R.id.bottom_menu_com)).setVisibility(View.GONE);
			linearlyout_write.setVisibility(View.GONE);

			//Create feed object here.
			CommunityFeed.Entry entry  = (Entry) DataModel.sSelf.removeObject(DMKeys.ENTRY + "COMM");
			//Set Header Title 
			((TextView) findViewById(R.community_chat.title)).setText(entry.groupName);
			setTitleName(entry.groupName);

			feed = new CommunityFeed();
			feed.entry.add(entry);

			//Notify Adaptor to display on screen
			if (mMediaAdapter != null) {
				mMediaAdapter.notifyDataSetChanged();
				mMainList.invalidateViews();
			}
		}/*else if(systemMessageReportAction){
			getIntent().removeExtra("SYSTEM_MSG_VIEW_REPORT");
			deleteMessageAPI = getIntent().getStringExtra("DELETE_MESSAGE_URL");
			deleteMessageMessageID = getIntent().getStringExtra("DELETE_MESSAGE_ID");
			getIntent().removeExtra("DELETE_MESSAGE_URL");
			getIntent().removeExtra("DELETE_MESSAGE_ID");
			//Header Options Gone
			((CImageView) findViewById(R.id.comm_profile_image)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.headerpart_3)).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.attachement)).setVisibility(View.GONE);
			//Bottom Bar media and text entry gone
			((LinearLayout)findViewById(R.id.bottom_menu_com)).setVisibility(View.GONE);
			linearlyout_write.setVisibility(View.GONE);

			//Create feed object here.
			CommunityFeed.Entry entry  = (Entry) DataModel.sSelf.removeObject(DMKeys.ENTRY + "COMM");
			//Set Header Title 
			((TextView) findViewById(R.community_chat.title)).setText(entry.groupName);
			setTitleName(entry.groupName);
			feed = new CommunityFeed();
			feed = (CommunityFeed)DataModel.sSelf.removeObject("FEED_REPORT");
		//	feed.entry.add(entry);

			//Notify Adaptor to display on screen
			if (mMediaAdapter != null) {
				mMediaAdapter.notifyDataSetChanged();
				mMainList.invalidateViews();
			}
		}*/
		else{
			DATA_CALLBACK = XML_DATA_CALLBACK;


			if (entry == null)
			{
				entry = (Entry) DataModel.sSelf.getObject(DMKeys.ENTRY + "COMM");

				if(entry!= null && entry.groupId >0)
				{
					profileEntry = entry;
					groupID = ""+entry.groupId;
					//Added By Manoj for counter
					Utilities.setString(this, Constants.LAST_VISITED_GROUPID,groupID);
					totalFeedOnServer=entry.noOfMessage;
					Log.i(TAG, "OnCreate :: ChannelName : "+entry.groupName+", ChannelID : "+entry.groupId);
				}
				//Set Community profile pic here
				if(entry != null && entry.thumbUrl != null)
				{
					/*if(imageManager == null)
						imageManager = new ImageDownloader();
					imageManager.buddyThumb = true;
					imageManager.loadForCommunity(true);*/
					//imageManager.download(entry.thumbUrl, (CImageView) findViewById(R.id.comm_profile_image), thumbnailHandler, ImageDownloader.TYPE_THUMB_BUDDY);
					if(entry.thumbUrl.trim().startsWith("http://"))
						imageLoader.displayImage(entry.thumbUrl, (CImageView) findViewById(R.id.comm_profile_image), options);
					else
						((CImageView) findViewById(R.id.comm_profile_image)).setImageURI(Uri.parse(entry.thumbUrl));
				}
				else
					((CImageView) findViewById(R.id.comm_profile_image)).setBackgroundResource(R.drawable.groupicon);

				((CImageView) findViewById(R.id.comm_profile_image)).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});


			}
			loadChannelMessages20();
			rTDialog = new RTDialog(ChannelReportActivity.this, null, getString(R.string.updating));
			//rTDialog = ProgressDialog.show(CommunityChatActivity.this, "", getString(R.string.updating), true);
		}

		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Channel Post Screen");
		t.set("&uid",""+BusinessProxy.sSelf.getUserId());
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GoogleAnalytics.getInstance(ChannelReportActivity.this).reportActivityStart(this);
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		activityStop = true;
		GoogleAnalytics.getInstance(ChannelReportActivity.this).reportActivityStop(this);
	}

	private void loadChannelMessages20(){
		//Stop Channel refresh service, channel refresh service will be resumed when goes back.
		stopChannelRefresh();
		if(entry != null){
			if(entry != null && entry.groupName != null)
			{
				((TextView) findViewById(R.community_chat.title)).setText(entry.groupName);
				setTitleName(entry.groupName);
				mCurrentScreen = entry.groupName;
			}
			if(entry != null && entry.newMessageCount > 20){
				if(groupID != null  && !groupID.equals(""))
					deleteAllMsg(""+groupID);
			}
			if(groupID != null  && !groupID.equals(""))
				getCommunityChat(groupID, true);
			if(feed == null   &&  !systemMessageReportAction){
				String messageUrl = entry.messageUrl + "?textMode=base64&" + BIG_THUMB_INFO;
				executeRequestData(messageUrl, entry.groupName);
			}
		}

	}

	ThumbnailReponseHandler thumbnailHandler = new ThumbnailReponseHandler() {

		@Override
		public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
			try {

			} catch (Exception e) {
			}
		}
	};

	// DATE 23-11-2015
	//Delete msg from db
	public void deleteAllMsg(String groupId){
		int mRowsDeleted =getContentResolver().delete(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,CommunityMessageTable.GROUPID+"='"+ groupId+"'", null);
	}
	//End Date 23-11-2015
	public void getCommunityChat(String groupId, boolean updateSelection){
		try{
			Cursor cursor = getContentResolver().query(MediaContentProvider.CONTENT_URI_CHANNEL_CONVERSATION_LIST,
					null,
					CommunityMessageTable.GROUPID + " = ?",
					new String[] {groupId},
					CommunityMessageTable.MSG_ID + " desc");
			cursor.moveToFirst();
			if(cursor != null)
			{
				//feed.entry = null;
				feed = CursorToObject.ChannelCommentEntryObject(cursor);
				if (UIActivityManager.fromPushNot == 2 ){
					UIActivityManager.fromPushNot = 0;
					if(groupId!= null)
						feed.nexturl  = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/message/"+groupId+"?textMode=base64&iThumbFormat=300x300&vThumbFormat=300x300&iFormat=600x600&pg=2";
					totalFeedOnServer = 21;
				}
				if(feed!=null && feed.entry!=null &&  feed.entry.size() > 0){
					//feed_db.socketUrl= feed.socketUrl;
					feed.nexturl  = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/message/"+groupId+"?textMode=base64&iThumbFormat=300x300&vThumbFormat=300x300&iFormat=600x600&pg=2";
					feed.socketedMessageUrl= GenerateLoadMoreURL(""+groupId,Integer.parseInt(getMessageId()));
					feed.groupId=Integer.parseInt(groupId);
					if(updateSelection && !voiceIsPlaying)
						mMainList.post(new Runnable() {
							@Override
							public void run() {
								try{
									mMainList.setSelection(feed.entry.size() - 1);
								}
								catch(Exception e){

								}
							}
						});
					//As this has been loaded from DB, so making this true
					feedLoadedFirstTime = true;
				}else
				{
					feed = null;
				}
				cursor.close();
				//Safe checks, as sometimes feed.socketedMessageUrl was null
				if(feed != null && feed.socketedMessageUrl == null)
					feed.socketedMessageUrl= GenerateLoadMoreURL(""+groupId,Integer.parseInt(getMessageId()));
				if(feedLoadedFirstTime)
					startTimerToRefresh();
			}
		}catch(Exception e){
			//feed=null;
			Log.e(TAG, ""+e);
			//e.printStackTrace();
		}

		//		if(Mbean_Global== null && feed!=null && feed.entry!=null){
		//			Mbean_Global= new ArrayList<MediaBean>();
		//			if(feed!=null && feed.entry!=null)
		//				for(int y=0;y<feed.entry.size();y++)
		//				{
		//					MediaBean mb = new MediaBean();
		//					mb.setEntry(feed.entry.get(y));
		//					mb.setMsgId(feed.entry.get(y).messageId);
		//					mb.setAutoplay("False");
		//					mb.setPlayingPercentage(0);
		//					mb.setmIsPlaying(false);
		//					Mbean_Global.add(mb);
		//				}
		//		}
	}


	private TextView image_count;

	protected void onResume() {
		activityStop = false;
		startRecoridng = false;
		super.onResume();

		// Sharing from outside

		if(UIActivityManager.sharedText != null){
			((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).setText(UIActivityManager.sharedText);
			((ImageView) findViewById(R.community_chat.sendButton))
			.performClick();
			UIActivityManager.sharedText = null;
		}

		// for image sharing
		if (UIActivityManager.sharedImage != null) {
			final String[] s;
			s=UIActivityManager.sharedImage.split(",");
			mPicturePath = new Vector<String>();
			if(s != null && s.length > 5)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.multiimage_selection))
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						for (int i = 0; i < 5; i++) {
							try {
								s[i] = s[i].replaceAll(" ", "%20");
								mPicturePath.add(s[i]);

							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						((ImageView)findViewById(R.community_chat.sendButton)).performClick();
						UIActivityManager.sharedImage  = null;
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						UIActivityManager.sharedImage  = null;
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}else if (s != null && s.length > 0 )
			{
				for (int i = 0; i < s.length; i++) {
					try {
						s[i] = s[i].replaceAll(" ", "%20");
						mPicturePath.add(s[i]);

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				((ImageView)findViewById(R.community_chat.sendButton)).performClick();
				UIActivityManager.sharedImage  = null;

			}
		}
		// for Video Sharing
		if (UIActivityManager.sharedVideo!= null) {
			final String[] s;
			s=UIActivityManager.sharedVideo.split(",");
			if(s.length > 1){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.multivideo_selection))
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						for (int i = 0; i < 1; i++) {
							try {
								mVideoPath = s[i];

							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						mVideoPath = mVideoPath.replaceAll("%20", " ");
						mVideoPath = mVideoPath.replaceFirst("file://", "");
						if (isSizeReachedMaximum(mVideoPath)) {
							Toast.makeText(ChannelReportActivity.this, getString(R.string.max_attachment_reached_update), Toast.LENGTH_LONG).show();
							mVideoPath = null;
						}else
						{
							sendMessage("");
							//											new PostMessageInChannel().execute();
						}
						UIActivityManager.sharedVideo = null;
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						UIActivityManager.sharedVideo = null;
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			else{

				if(UIActivityManager.sharedVideo != null){
					mVideoPath = UIActivityManager.sharedVideo;
					mVideoPath = mVideoPath.replaceAll("%20", " ");
					mVideoPath = mVideoPath.replaceFirst("file://", "");
					UIActivityManager.sharedVideo = null;
					if (isSizeReachedMaximum(mVideoPath)) {
						Toast.makeText(ChannelReportActivity.this, getString(R.string.max_attachment_reached_update), Toast.LENGTH_LONG).show();
						mVideoPath = null;
					}else
					{
						sendMessage("");
						//										new PostMessageInChannel().execute();
					}
				}
			}
			UIActivityManager.sharedVideo = null;

		}
		// for Audio Sharing
		if (UIActivityManager.sharedAudio!= null) {
			if(isInternetOn()){
				final String[] s;
				s = UIActivityManager.sharedAudio.split(",");
				if(s.length > 1){
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getString(R.string.multiaudio_selection))
					.setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							final String[] ss = UIActivityManager.sharedAudio.split(",");
							for (int i = 0; i < 1; i++) {
								try {
									mVoicePath = ss[i];

								} catch (Exception e) {
									// TODO: handle exception
								}
							}
							mVoicePath = mVoicePath.replaceAll("%20", " ");
							if(mVoicePath.startsWith("file")){

								mVoicePath = mVoicePath.replaceFirst("file://", "");
							}
							UIActivityManager.sharedAudio = null;
							//sendMessage("");
							((ImageView)findViewById(R.community_chat.sendButton)).performClick();
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							UIActivityManager.sharedAudio = null;
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}else
				{
					mVoicePath = UIActivityManager.sharedAudio;
					mVoicePath = mVoicePath.replaceAll("%20", " ");
					if(mVoicePath.startsWith("file")){
						mVoicePath = mVoicePath.replaceFirst("file://", "");
					}
					UIActivityManager.sharedAudio = null;
					((ImageView)findViewById(R.community_chat.sendButton)).performClick();
				}




				//=====================================================================

			}
		}




		//mVoicePath = (String) DataModel.sSelf.removeObject(DMKeys.VOICE_PATH);

		if(systemMessageAction){
		} else if(UIActivityManager.systemMessageReportAction){

			//getIntent().removeExtra("SYSTEM_MSG_VIEW_REPORT");
			UIActivityManager.systemMessageReportAction = false;
			systemMessageReportAction = true;
			deleteMessageAPI = getIntent().getStringExtra("DELETE_MESSAGE_URL");
			deleteMessageMessageID = getIntent().getStringExtra("DELETE_MESSAGE_ID");
			getIntent().removeExtra("DELETE_MESSAGE_URL");
			getIntent().removeExtra("DELETE_MESSAGE_ID");
			//Header Options Gone
			((CImageView) findViewById(R.id.comm_profile_image)).setVisibility(View.GONE);
			((LinearLayout)findViewById(R.id.headerpart_3)).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.attachement)).setVisibility(View.GONE);
			//Bottom Bar media and text entry gone
			((LinearLayout)findViewById(R.id.bottom_menu_com)).setVisibility(View.GONE);
			linearlyout_write.setVisibility(View.GONE);

			//Create feed object here.
			//	CommunityFeed.Entry entry  = (Entry) DataModel.sSelf.removeObject(DMKeys.ENTRY + "COMM");
			//DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", feed.entry.get(0));
			//Set Header Title 
			//((TextView) findViewById(R.community_chat.title)).setText(entry.groupName);
			//setTitleName(entry.groupName);

			feed = new CommunityFeed();
			/*if(feed != null)
							{
								feed = null;
								feed.entry = null;
							}*/
			feed = (CommunityFeed)DataModel.sSelf.removeObject("FEED_REPORT");
			DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM", feed.entry.get(0));
			//	feed.entry.add(entry);

			//Notify Adaptor to display on screen
			if (mMediaAdapter != null) {
				mMediaAdapter.notifyDataSetChanged();
				mMainList.invalidateViews();
			}

			linearlyout_write.setVisibility(View.GONE);
		}
		else{
			//Check if app is resumed and no data to display then load the data again.
			if((wasAppInBG || feedLoadedFirstTime || switchedToScreenWhileLoading) && (feed == null || feed.entry == null)){
				loadChannelMessages20();
				wasAppInBG = false;
				if(switchedToScreenWhileLoading)
					switchedToScreenWhileLoading = false;
				Log.w(TAG, "onResume :: wasAppInBG = true or called from other screen, so calling get messsage again.");
			}
			else
				wasAppInBG = false;
			try{/*
				if (lastListSelectedIndex >= 0 && feed != null && feed.entry != null
						&& feed.entry.size() > 0) {
					mMainList.setSelection(lastListSelectedIndex);
				}
				if (mMediaAdapter != null) {
					mMediaAdapter.notifyDataSetChanged();
					mMediaAdapter.notifyDataSetInvalidated();
				}
				addMultiImages();

				if (mPicturePath == null)
					mPicturePath = new Vector<String>();

				image_count.setText("" + mPicturePath.size());
				if (mPicturePath != null && mPicturePath.size() <= 0)
					//									image_count.setVisibility(View.GONE);
					//								else
					//									image_count.setVisibility(View.VISIBLE);
					if (lDialogProgress != null && lDialogProgress.isShowing()
					&& STATE == READY) {
						lDialogProgress.dismiss();
					}
				if(entry!=null)
				{
					refershFollowUnfollow();
					//				if(!entry.joinOrLeave.equalsIgnoreCase("join") && !entry.ownerUsername.equalsIgnoreCase(SettingData.sSelf.getUserName()))
					if(entry.joinOrLeave != null && !entry.joinOrLeave.equalsIgnoreCase("join") 
							&& entry.ownerUsername != null && !entry.ownerUsername.equalsIgnoreCase(SettingData.sSelf.getUserName()) 
							&& feed != null && feed.isOwner == 0 && feed.isAdmin == 0)
						setToBroadcast(entry.adminState);
					if(!startTimerToRefreshBool){
						startTimerToRefresh();
					}
				}
			*/}catch(Exception e){
				e.printStackTrace();
			}
		}


		if(systemMessageReportAction){
			linearlyout_write.setVisibility(View.GONE);
		}


	}

	private boolean isSizeReachedMaximum(long newSize) {
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	/*	
	 *WORKING FINE CODE 
	 * private boolean isSizeReachedMaximum(String newPath) {
		FileInputStream fin = null;

		long newSize = 0;
		if (null != newPath) {
			try {
				fin = new FileInputStream(newPath);
				newSize = fin.available();
			} catch (IOException ex) {
				if (Logger.ENABLE)
					Logger.error(TAG, "Error getting size for path - "
							+ newPath, ex);
			} finally {
				if (fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
					}
				}
			}
		}
		// makeToast("Image Size: " + newSize + ", Total Attachment Size: " +
		// totalSize);
		if ((newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}*/
	////////////////////////////
	private boolean isSizeReachedMaximum(String newPath) {
		long size = 0;
		try{
			File file = new File(newPath);
			size = file.length();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		Log.i(TAG, "isSizeReachedMaximum :: MAX_IMAGE_ATTACH_SIZE = "+MAX_IMAGE_ATTACH_SIZE + ", attached file size : "+size);
		if (size > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		return false;
	}



	////////////////////////////
	public void cancelLoading() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mLoadingBar.isShown()) {
					mLoadingBar.setVisibility(View.GONE);
				}
			}
		});
	}

	Message message;
	PopupWindow mPopupWindow;
	public void closeCopy() {
		if (mPopupWindow != null && mPopupWindow.isShowing())
			mPopupWindow.dismiss();
	}
	public void showCopy(View anchor) {
		try {
			message = (Message) anchor.getTag();
			if (mPopupWindow != null && mPopupWindow.isShowing())
				mPopupWindow.dismiss();

			Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			View mRootView = ((Activity) this).getLayoutInflater().inflate(R.layout.copy_panel, null);
			// System.out.println("message.msgTxt==="+message.msgTxt+"message.mfuId=="+message.mfuId);
			if (message.mfuId.equalsIgnoreCase("-1")) {
				if (message.msgTxt != null && message.msgTxt.length() > 0) {
					mRootView.findViewById(R.bubble_long_press.forward).setVisibility(View.GONE);
					mRootView.findViewById(R.bubble_long_press.copy).setBackgroundResource(R.drawable.dialpad_btn_call_normal);
				}
				if (message.msgTxt == null) {
					mRootView.findViewById(R.bubble_long_press.forward).setVisibility(View.GONE);
					mRootView.findViewById(R.bubble_long_press.copy).setVisibility(View.GONE);
					return;
				} else {
					mRootView.findViewById(R.bubble_long_press.forward).setVisibility(View.GONE);
					mRootView.findViewById(R.bubble_long_press.copy).setVisibility(View.GONE);
					return;
				}
			} else if (message.msgTxt != null && message.msgTxt.length() == 0
					&& !message.mfuId.equalsIgnoreCase("-1")) {
				// System.out.println("message.msgTxt==="+message.msgTxt);
				mRootView.findViewById(R.bubble_long_press.forward).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.bubble_long_press.forward).setBackgroundResource(R.drawable.dialpad_btn_call_normal);
				mRootView.findViewById(R.bubble_long_press.copy).setVisibility(View.GONE);// .setBackgroundResource(R.drawable.dialpad_btn_call_normal);
			}
			if ((message.aniType != null && message.aniType.equalsIgnoreCase("animation") && message.aniValue != null)) {
				mRootView.findViewById(R.bubble_long_press.forward).setVisibility(View.GONE);
				mRootView.findViewById(R.bubble_long_press.copy).setVisibility(View.GONE);
				;// .
			}
			mRootView.findViewById(R.bubble_long_press.copy)
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPopupWindow != null && mPopupWindow.isShowing())
						mPopupWindow.dismiss();
					if (message.msgTxt != null && message.msgTxt.trim().length() > 0) {
						ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(message.msgTxt);
						showToast(getString(R.string.text_copied));
					} else {
						showToast(getString(R.string.nothing_to_copy));
					}
					// ClipData clip =
					// ClipData.newPlainText("label","Your Text");
					// clipboard.setPrimaryClip(clip);
				}
			});
			mPopupWindow = new PopupWindow(mRootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
			mPopupWindow.setAnimationStyle(R.style.Animations_PopUpMenu);
			int rootHeight = 0;
			int[] location = new int[2];
			int xPos;
			anchor.getLocationOnScreen(location);
			mWindowManager = (WindowManager) this .getSystemService(Context.WINDOW_SERVICE);
			int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
			int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
			Rect anchorRect = new Rect(location[0], location[1], location[0]
					+ anchor.getWidth(), location[1] + anchor.getHeight());
			int rootWidth = 0;
			mRootView.measure(display.getWidth(), display.getHeight());
			if (rootWidth == 0) {
				rootWidth = mRootView.getMeasuredWidth();
			}
			if (rootHeight == 0) {
				rootHeight = mRootView.getMeasuredHeight();
			}

			// automatically get X coord of popup (top left)
			if ((anchorRect.left + rootWidth) > screenWidth) {
				xPos = anchorRect.left - (rootWidth - anchor.getWidth());
				xPos = (xPos < 0) ? 0 : xPos;
			} else {
				if (anchor.getWidth() > rootWidth) {
					xPos = anchorRect.centerX() - (rootWidth / 2);
				} else {
					xPos = anchorRect.left;
				}
			}

			int dyTop = anchorRect.top;
			int dyBottom = screenHeight - anchorRect.bottom;

			boolean onTop = (dyTop > dyBottom) ? true : false;

			mPopupWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
			mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, (screenWidth / 2) - (rootWidth / 2), anchorRect.top<=0?150:anchorRect.top);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}	
	// ////////////////////
	private void calculateTotSize() {
		if (mPicturePath != null)
			for (int i = 0; i < mPicturePath.size(); i++) {
				try {
					totalSize = 0;
					long size = ImageUtils.getImageSize(mPicturePath.get(i));
					if (isSizeReachedMaximum(size)) {
					}
				} catch (Exception e) {
				}
			}
	}

	private void calculateTotSizeAfterClear() {
		totalSize = 0;
		if (mPicturePath != null) {

			for (int i = 0; i < mPicturePath.size(); i++) {
				try {
					// totalSize = 0 ;
					long size = ImageUtils.getImageSize(mPicturePath.get(i));
					totalSize += size;
					if (isSizeReachedMaximum(size)) {
					}
				} catch (Exception e) {
				}
			}
		}
		// if (mPicturePathCanvas != null) {
		// for (int i = 0; i < mPicturePathCanvas.size(); i++) {
		// try {
		// // totalSize = 0 ;
		// long size = ImageUtils.getImageSize(mPicturePathCanvas.get(i));
		// totalSize += size;
		// if (isSizeReachedMaximum(size)) {
		// }
		// } catch (Exception e) {
		// }
		// }
		// }
		if (mVideoPath != null) {
			long size = ImageUtils.getImageSize(mVideoPath);
			totalSize += size;
		}
	}

	private void addMultiImages() {
		String s[] = (String[]) DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES);
		if (s != null) {
			mPicturePath = new Vector<String>();
			for (int i = 0; i < s.length; i++) {
				try {
					mPicturePath.add(s[i]);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		Integer sid[] = (Integer[]) DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES + "ID");
		if (sid != null) {
			mPicturePathId = new Vector<Integer>();
			for (int i = 0; i < sid.length; i++) {
				try {
					mPicturePathId.add(sid[i]);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		calculateTotSize();
	}

	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Toast.makeText(ChannelReportActivity.this, "yippiee Video ", Toast.LENGTH_SHORT).show();
			Toast.makeText(ChannelReportActivity.this, "Video SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			ArrayList<String >str = intent.getStringArrayListExtra("list");
			if(str != null)
				mVideoPath = str.get(0);
			sendMessage("");
			//			new PostMessageInChannel().execute("");
			mVideoPath = null;
		}
	};

	BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(ChannelReportActivity.this, "yippiee Image ", Toast.LENGTH_SHORT).show();
			Toast.makeText(ChannelReportActivity.this, "Image SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();

			if(!isInternetOn())
				return;
			//		String[] all_path = data.getStringArrayExtra("all_path");
			//		imagePathPos = (int[]) DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES+"IDINT");
			ArrayList<String> s = intent.getStringArrayListExtra("list");
			if (s != null) {
				mPicturePath = new Vector<String>();
				for (int i = 0; i < s.size(); i++) {
					try {
						mPicturePath.add(s.get(i));

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				//mPicturePath = addAll
				s  = null;
				((ImageView)findViewById(R.community_chat.sendButton)).performClick();	
				mPicturePath = null;
			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		if (refreshTimer != null)
			try {
				refreshTimer.cancel();
			} catch (Exception e) {
				// TODO: handle exception
			}
		startTimerToRefreshBool = false;
	}

	private void stopRefresh() {
		if (refreshTimer != null)
			try {
				refreshTimer.cancel();
			} catch (Exception e) {
				// TODO: handle exception
			}
	}

	//	private void startRefresh() {
	//		if (1 == 1)
	//			return;
	//		// System.out.println("-------------------refresh rimer-------startRefresh");
	//		stopRefresh();
	//		if (activityStop)
	//			return;
	//
	//		refreshTimer = new Timer();
	//		refreshTimer.schedule(new TimerTask() {
	//			public void run() {
	//				while (/* STATE != READY || */requestednextPage) {
	//					try {
	//						Thread.sleep(5000);
	//					} catch (InterruptedException e) {
	//						// TODO Auto-generated catch block
	//						e.printStackTrace();
	//					}
	//				}
	//				// System.out.println("-------------------refresh rimer-------");
	//
	//				if (feed != null && STATE == READY) {
	//					String url = feed.socketedMessageUrl;
	//					url = url.replace("$maxCount$", "3");
	//					url = url.replace("$order$", "ASC");
	//					url = url.replace("$messageId$", getMessageId());
	//					if (!requestednextPage)
	//						DATA_CALLBACK = ACTUAL_REFRESH_DATA_CALLBACK;
	//					//					if (Logger.ENABLE)
	//					//						System.out.println("chating executeRequestData-2");
	//					executeRequestData(url, null);
	//					// System.out.println("----------------------refresh url="+url);
	//					refreshTimer.cancel();
	//				}
	//			}
	//		}, 5000);
	//	}

	Object obj = new Object();
	Timer startTimerToRefresh;
	boolean startTimerToRefreshBool = true;

	public void startTimerToRefresh() {
		try {
			// startTimerToRefresh.schedule(refresh, 5000);
			if(systemMessageAction){
				Log.v(TAG, "startTimerToRefresh : [REFRESH NOT STARTED - SYSTEM MESSAGE VIEW]");
				return;
			}
			if(!refreshStarted){
				refreshStarted = true;
				startTimerToRefreshBool = true;
				new Thread(refresh).start();
			}
			else
				startTimerToRefreshBool = true;
			Log.w(TAG, "startTimerToRefresh : [Refresh Started]");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void stopTimerToRefresh() {
		try {
			if(refreshStarted){
				refreshStarted = false;
				Log.w(TAG, "stopTimerToRefresh : [Refresh Stopped]");
			}
			startTimerToRefreshBool = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String messageId = null;

	private String getMessageId() {
		if (messageId == null) {
			for (int i = 0; i < feed.entry.size(); i++) {
				// old have i 
				// new have zero(0)
				//	messageId = feed.entry.get(i).messageId;
				messageId = feed.entry.get(0).messageId;
				boolean mymesg = false;
				for (int j = 0; j < vector.size(); j++) {
					Entry en = vector.get(j);
					if (en.messageId == messageId)
						mymesg = true;
				}
				if (messageId == null || messageId.equals("") || mymesg) {
					continue;
				}
				return messageId;
			}
		}
		return messageId;// "";
	}


	private String getMessageIdRefresh() {
		if (messageId == null) {
			for (int i = 0; i < feed.entry.size(); i++) {
				messageId = feed.entry.get(feed.entry.size()-1).messageId;
				return messageId;
			}
		}
		if(messageId == null)
			messageId = "1";
		return messageId;// "";
	}
	Thread payloadThread;
	byte requestType;
	DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dismissAnimationDialog();
			cancelOperation = true;
		}
	};

	protected void showLoadingDialog() {
		showAnimationDialog("", getString(R.string.please_wait_while_loadin),
				true, PROGRESS_CANCEL_HANDLER);
	}

	private void executeRequestData(String aURL, String aMedia) {
		lodingCanceled = false;
		(new Timer()).schedule(new TimerTask() {
			public void run() {
				if (DATA_CALLBACK != ACTUAL_REFRESH_DATA_CALLBACK && !requestednextPage) {

					// if(mMainList != null)
					// mMainList.setVisibility(View.GONE);
					runOnUiThread(new Runnable() {
						public void run() {
							//							if (mLoadingBar.isShown()) 
							{

								if(isInternetOn()){
									mLoadingBar.setVisibility(View.VISIBLE);
									(findViewById(R.id.loading_linearlayout))
									.setVisibility(View.VISIBLE);
									((TextView) findViewById(R.id.loading_text))
									.setText(getString(R.string.please_wait_while_loadin));
								}else
								{
									mLoadingBar.setVisibility(View.GONE);
								}
							}
						}
					});
					if (DATA_CALLBACK == ACTUAL_AUDIO_DATA_CALLBACK
							|| DATA_CALLBACK == ACTUAL_IMAGE_DATA_CALLBACK
							|| DATA_CALLBACK == ACTUAL_VIDEO_DATA_CALLBACK) {
						if (lDialogProgress == null
								|| !lDialogProgress.isShowing()) {
							showLoadingDialog();

							// showLoggingDialog("Please wait while loading...");
							// Thread.yield();
							// Thread.sleep(1000);
						}

						if (lDialogProgress != null
								&& lDialogProgress.isShowing()
								&& STATE == READY) {
							// lDialogProgress.dismiss();
						}
					}
				}

			}
		}, 100);

		while (STATE != READY) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//		if (Logger.ENABLE)
		//			System.out.println("chating executeRequestData : " + aURL + " , "
		//					+ aMedia);

		if (aURL == null || aURL.trim().length() <= 0)
			return;
		if (aMedia != null)
			((TextView) findViewById(R.community_chat.title)).setText(aMedia);
		if(entry != null && entry.groupName != null)
		{
			setTitleName(entry.groupName);
			mCurrentScreen = entry.groupName;
		}
		//		setTitleName(aMedia);
		//	mCurrentScreen = aMedia;
		mPostURL = aURL;
		mIsRunning = true;

		// if(DATA_CALLBACK != ACTUAL_REFRESH_DATA_CALLBACK){
		// // mResponseData = null;
		//
		// //else
		// {
		//
		// //synchronized (this) {
		// payloadThread.notify();
		// //}
		// }
		// return ;
		// }
		// mResponseDataRefresh = null ;
		// DATA_CALLBACK = XML_DATA_CALLBACK ;
		requestType = DATA_CALLBACK;
		synchronized (this) {
			notify();
		}
	}

	public void setTitleName(String title)
	{
		if(title!=null)
			this.titleName = title.trim();
		else{
			if(entry!=null && entry.groupName!=null)
				this.titleName = entry.groupName;
		}
	}
	public String getTitleName()
	{
		return this.titleName;
	}

	class ViewHolder implements OnClickListener, OnLongClickListener {

		//Reported Layout
		LinearLayout reported_layout;
		LinearLayout reported_layout_in;
		TextView txtview_reportedby;
		//End reported
		View bubbleLayout;
		LinearLayout otherLayout;
		LinearLayout myLayout;
		ImageView senderImage;
		ImageView myImage;
		ImageView user_menus;
		ImageView my_menus;
		TextView senderName;
		TextView fn_ln;
		EmojiconTextView chatMessage;
		TextView replyto;
		TextView createdDate;
		LinearLayout inboxLayout_imageLayout_video;
		LinearLayout inboxLayout_imageLayout_image;
		LinearLayout inboxLayout_imageLayout_voice;
		LinearLayout main_parent_layout;

		//Static One
		ImageView ll_media_play_static;
		LinearLayout inboxLayout_imageLayout_voice_media_play_layout_static;
		SeekBar ll_mediavoicePlayingDialog_progressbar_static;
		LinearLayout ll_time_panel_static;
		TextView ll_total_autio_time_static;
		TextView ll_played_autio_time_static;
		TextView ll_dummyp_static;
		LinearLayout ll_streemStatus_linear_static;
		TextView ll_streemStatus_static;

		//Playing One
		ImageView ll_media_play;
		LinearLayout inboxLayout_imageLayout_voice_media_play_layout;
		SeekBar ll_mediavoicePlayingDialog_progressbar;
		LinearLayout ll_time_panel;
		TextView ll_total_autio_time;
		TextView ll_played_autio_time;
		TextView ll_dummyp;
		LinearLayout ll_streemStatus_linear;
		TextView ll_streemStatus;

		//For Video and Video download
		View download_view;
		ImageView video_img_view;
		ImageView download_arrow;
		ProgressBar progress_bar;
		TextView download_circular_progress_percentage;

		LinearLayout report_action_linear;
		LoadVideoFromURL videoDownLoaderTask;
		TextView txt_reporter_one;
		TextView txt_reporter_two;
		TextView txt_reporter_three;

		public LoadVideoFromURL getVideoDownLoaderTask(){
			return videoDownLoaderTask;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case CONTENT_ID_VIDEO_CANCEL:
				if(videoDownLoaderTask != null && !videoDownLoaderTask.isCancelled()){
					videoDownLoaderTask.cancel(true);
					Log.i(TAG, "ViewHolder :: onClick : CONTENT_ID_VIDEO_CANCEL - true");
				}
				break;
			case CONTENT_ID_VIDEO:
			case CONTENT_ID_VIDEO + 1:
			case CONTENT_ID_VIDEO + 2:
			case CONTENT_ID_VIDEO + 3:
			case CONTENT_ID_VIDEO + 4:
			case CONTENT_ID_VIDEO + 5:
			case CONTENT_ID_VIDEO + 6:
			case CONTENT_ID_VIDEO + 7:
			case CONTENT_ID_VIDEO + 8:
			case CONTENT_ID_VIDEO + 9:
			case CONTENT_ID_VIDEO + 10:
			case CONTENT_ID_VIDEO + 11:
			case CONTENT_ID_VIDEO + 12:
			case CONTENT_ID_VIDEO + 13:
			case CONTENT_ID_VIDEO + 14:
			case CONTENT_ID_VIDEO + 15:
			case CONTENT_ID_VIDEO + 16:
			case CONTENT_ID_VIDEO + 17:
			case CONTENT_ID_VIDEO + 18:
			case CONTENT_ID_VIDEO + 19:
				if(isVideoDownloadInProgress()){
					//Show a toast that video is already in progress.
					Toast.makeText(getApplicationContext(), getString(R.string.already_download_in_progress), Toast.LENGTH_SHORT).show();
					return;
				}
			String url = null;
			Object[] data = null;
			if(v.getTag() instanceof Object[]){
				data = (Object[]) v.getTag();
				url = (String) data[1];
				//				CommunityFeed.Entry voiceEntry = (CommunityFeed.Entry) data[0];
				//				voiceEntry.isVideoDownloading = true;
			}
			else if(v.getTag() instanceof String)
				url = (String) v.getTag();

			if(url == null)
				return;

			if (url.endsWith(".3gp") || url.endsWith(".mp4"))
				url = url.substring(url.lastIndexOf("~") + 1, url.length());
			else
				url = url.substring(0, url.indexOf("~"));
			if(url != null && url.startsWith("http://")){
				String fileName = null;
				String extension;
				String data_url = url;
				String fullPath = null;

				extension = MimeTypeMap.getFileExtensionFromUrl(data_url);
				if(extension == null || extension.trim().length() <= 0)
				{
					try{
						extension = data_url.substring(data_url.lastIndexOf(".") + 1);
					}catch (Exception e) {
						extension = "mp4";
					}
				}
				//Extract File name from the URL
				if(data_url.lastIndexOf('/') != -1 && data_url.lastIndexOf('.') != -1)
				{
					if(data_url.lastIndexOf('.') > data_url.lastIndexOf('/'))
						fileName = data_url.substring(data_url.lastIndexOf("/") + 1, data_url.lastIndexOf("."));
					else
						fileName = data_url.substring(data_url.lastIndexOf("=") + 1);

					File folder = new File(Environment.getExternalStorageDirectory() + "/YelaTalk");
					if (!folder.exists()) {
						folder.mkdir();
					}
					fullPath = new StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append("YelaTalk").append('/').
							append(fileName).append('.').append(extension).toString();

				}
				//Check if filename exists
				if(isFileExists(data_url.substring(data_url.lastIndexOf("/") + 1))){
					//Directly Play the same path
					if(fullPath != null){
						playFromURLMain(fullPath);
					}
				}else{
					if(data[0] != null){
						CommunityFeed.Entry voiceEntry = (CommunityFeed.Entry) data[0];
						voiceEntry.isVideoDownloading = true;
						progress_bar.setProgress(0);
						progress_bar.setSecondaryProgress(100);
						download_arrow.setVisibility(View.VISIBLE);
						download_arrow.setId(CONTENT_ID_VIDEO);
						viewHolder.download_arrow.setImageResource(R.drawable.cancel_download);
						viewHolder.download_arrow.setOnClickListener(viewHolder);
						if(mMediaAdapter != null)
							mMediaAdapter.notifyDataSetChanged();
					}
					videoDownLoaderTask = new LoadVideoFromURL();
					videoDownLoaderTask.execute(url);
				}
			}
			else{
				try {
					playFromURLMain(VDownloader.getInstance().getPlayUrl(url));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
			case CONTENT_ID_VOICE:
				if(isStreamingAudio){
					Log.w(TAG, "ViewHolder :: onClick : AUDIO STREAMING FOR ONE VOICE, PLEASE WAIT!!");
					return;
				}
				try{
					String local_str = "";
					if(v.getTag() instanceof Object[]){
						Object[] obj = (Object[]) v.getTag();
						local_str = (String) obj[1];
						CommunityFeed.Entry voiceEntry = (CommunityFeed.Entry) obj[0];
						voiceEntry.isPlaying = true;
						//					for(int k = 0; k < feed.entry.size(); k++){
						//						CommunityFeed.Entry modified_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
						//						Log.v(TAG, "ViewHolder :: onClick :: position : "+k+", modified_entry.isPlaying : "+modified_entry.isPlaying);
						//					}
					}
					else if(v.getTag() instanceof String){
						local_str = (String) v.getTag();
					}
					else{
						CommunityFeed.Entry voiceEntry = (CommunityFeed.Entry)v.getTag();
						voiceEntry.isPlaying = true;
						for (int j = 0; j < (voiceEntry.media.size()); j += 8) {
							String s = (String) voiceEntry.media.elementAt(j + 1);
							if (((String) voiceEntry.media.elementAt(j + 1)).equals("image"))
							{
								//continue;
								break;
							}
							if (!s.equalsIgnoreCase("audio"))
								continue;
							local_str = (String) voiceEntry.media.elementAt(j + 2);
						}
					}
					url = local_str;
					//String url = (String) v.getTag();
					if(url != null && url.indexOf('~') != -1){
						String index = url.substring(0, url.indexOf("~"));
						url = url.substring(2, url.lastIndexOf("~"));
						CommunityFeed.Entry imgEntry = feed.entry.get(Integer.parseInt(index));
						ArrayList<String> mImagesPath = new ArrayList<String>();
						for (int j = 0; j < (imgEntry.media.size()); j += 8) {
							if (((String) imgEntry.media.elementAt(j + 3))
									.equalsIgnoreCase("local")) {
								if (!((String) imgEntry.media.elementAt(j + 1))
										.equalsIgnoreCase("image"))
									continue;
								mImagesPath.add((String) imgEntry.media.elementAt(j + 2));
							}
						}
						getSlideData(imgEntry);
					}
					else{
						if(url.endsWith(".amr"))
							url = getCacheDir() + "/" +url;
						else if(url.startsWith("http://") && url.lastIndexOf('/') != -1 
								&& url.lastIndexOf('.') > url.lastIndexOf('/'))
						{
							String fileName = url.substring(url.lastIndexOf('/')+1);
							String a  = getImageLocalPath(fileName);
							if(isFileExists(a)){
								url = a;
							}else
							{
								//@TODO
								// download audio code need here 
							}
						}
						else if(!url.startsWith("http://")){
							if(!url.contains("storage")  && !url.contains("content://media"))
								url = getImageLocalPath(url);
							if(url.contains("content://media")){
								url = getRealPathFromURI(getApplicationContext(),Uri.parse(url));
							}
						}
						if(url!=null){
							if(voiceIsPlaying){
								RTMediaPlayer.reset();
								RTMediaPlayer.clear();
								resetPlayingInFeed();
								resetProgress();
								inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.VISIBLE);
								inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.GONE);
							}
							else{
								inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.GONE);
								inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.VISIBLE);
							}
							if(mMediaAdapter != null)
								mMediaAdapter.notifyDataSetChanged();
							openPlayScreen(url);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			}
		}
		//----------------------------------------------------------------------------------------------------------


		private void openPlayScreen(final String url) {
			availableVoice = false;
			voiceIsPlaying = true;
			if(url.startsWith("http://")){
				//Timer panel
				ll_time_panel.setVisibility(View.GONE);
				ll_total_autio_time.setVisibility(View.GONE);
				ll_played_autio_time.setVisibility(View.GONE);
				ll_dummyp.setVisibility(View.GONE);

				//Streaming - Please wait panel
				isStreamingAudio = true;
				ll_streemStatus_linear.setVisibility(View.VISIBLE);
				ll_streemStatus.setText(getString(R.string.geeting_audio));
				ll_streemStatus.setTextColor(getResources().getColor(R.color.sub_heading));
			}
			RTMediaPlayer.setProgressBar(ll_mediavoicePlayingDialog_progressbar);
			RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

				@Override
				public void voiceRecordingStarted(String recordingPath) {
				}

				@Override
				public void voiceRecordingCompleted(String recordedVoicePath) {
					// TODO Auto-generated method stub

				}

				public void voicePlayStarted() {
					isStreamingAudio = false;
					if (availableVoice)
						return;
					try {
						//Show original Play Layout and Hide Static one
						inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.VISIBLE);
						inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.GONE);

						//Hide Stream status as play started
						ll_streemStatus_linear.setVisibility(View.GONE);

						//Show Timer panel
						ll_time_panel.setVisibility(View.VISIBLE);
						ll_total_autio_time.setVisibility(View.VISIBLE);
						ll_dummyp.setVisibility(View.VISIBLE);
						ll_played_autio_time.setVisibility(View.VISIBLE);

						//Set Media Play Panel Properties
						ll_media_play.setTag("PLAY");
						ll_media_play.setBackgroundResource(R.drawable.addstop);
						ll_media_play.setTag("STOP");
						ll_media_play.setVisibility(View.VISIBLE);
						ll_media_play.setId(R.id.media_play);
						ll_media_play.setOnClickListener(playerClickEvent);
						if(mMediaAdapter != null)
							mMediaAdapter.notifyDataSetChanged();
					} catch (Exception e) {
						// TODO: handle exception
						isStreamingAudio = false;
						e.printStackTrace();
					}
				}
				public void voicePlayCompleted() {
					ChannelReportActivity.this.handler.post(new Runnable() {

						@Override
						public void run() {
							try {
								inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.VISIBLE);
								inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.GONE);
								ll_dummyp.setVisibility(View.GONE);
								ll_time_panel.setVisibility(View.GONE);
								ll_streemStatus_linear.setVisibility(View.GONE);
								if(ll_media_play_static != null){
									ll_media_play_static.setBackgroundResource(R.drawable.addplay);
									ll_media_play.setBackgroundResource(R.drawable.addplay);
									ll_media_play.setTag("PLAY");
									ll_media_play_static.setTag("PLAY");
									ll_media_play_static.setId(CONTENT_ID_VOICE);
									ll_media_play.setId(CONTENT_ID_VOICE);
									ll_media_play.setOnClickListener(ViewHolder.this);
									ll_media_play_static.setOnClickListener(ViewHolder.this);
								}
								if(mMediaAdapter != null)
									mMediaAdapter.notifyDataSetChanged();
								availableVoice = true;
								voiceIsPlaying = false;
								isStreamingAudio = false;
								RTMediaPlayer.reset();
								RTMediaPlayer.clear();
								resetPlayingInFeed();
								resetProgress();
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					});
				}

				public void onError(int i) {
					try {
						BusinessProxy.sSelf.animPlay = false;
						if(ll_streemStatus != null){
							ll_streemStatus_linear.setVisibility(View.VISIBLE);
							ll_streemStatus.setTextColor(getResources().getColor(R.color.red));
							ll_streemStatus.setText("Unable to play please try later!");
						}
						voiceIsPlaying = false;
						isStreamingAudio = false;
						RTMediaPlayer.reset();
						RTMediaPlayer.clear();
						resetPlayingInFeed();
						resetProgress();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

				@Override
				public void onDureationchanged(final long total, final long progress) {
					try {
						runOnUiThread(new Runnable() {
							public void run() {
								//									progress_bar.setProgress(progress);
								if(ll_total_autio_time != null){
									ll_total_autio_time.setText(""+Utilities.converMiliSecond(total));
									//								ll_total_autio_time.setTypeface(Typeface.DEFAULT_BOLD);
								}
								if(ll_played_autio_time != null){
									ll_played_autio_time.setText(Utilities.converMiliSecond(progress));
									//								ll_played_autio_time.setTypeface(Typeface.DEFAULT_BOLD);
								}
								//							if(mMediaAdapter != null)
								//								mMediaAdapter.notifyDataSetChanged();
							}
						});
					} catch (Exception e) {
						RTMediaPlayer.reset();
					}
				}
			});
			RTMediaPlayer._startPlay(url);
		}
		//---------------------------------------------------------------------------------
		View.OnClickListener playerClickEvent = new OnClickListener() {

			@Override
			public void onClick(final View v) {
				switch (v.getId()) {
				case R.id.media_play:
					try {
						if (((String) v.getTag()).equals("PLAY")) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										ll_streemStatus.setTextColor(getResources().getColor(R.color.sub_heading));
										if (RTMediaPlayer.getUrl() != null)
											RTMediaPlayer.start();
										else {
											try {
												RTMediaPlayer._startPlay(v.getTag().toString());
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}).start();
							ll_media_play.setBackgroundResource(R.drawable.addstop);
							ll_media_play.setTag("STOP");
						} else if(((String) v.getTag()).equals("STOP")) {
							inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.VISIBLE);
							inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.GONE);
							ll_time_panel.setVisibility(View.GONE);
							ll_dummyp.setVisibility(View.GONE);
							if(ll_media_play_static != null){
								ll_media_play_static.setBackgroundResource(R.drawable.addplay);
								ll_media_play_static.setTag("PLAY");
							}
							//							RTMediaPlayer.pause();
							voiceIsPlaying = false;
							RTMediaPlayer.reset();
							RTMediaPlayer.clear();
							resetPlayingInFeed();
						}
					} catch (Exception e) {
					}
					break;
				case R.id.media_stop_play:
					media_play_layout.setVisibility(View.GONE);
					voiceIsPlaying = false;
					RTMediaPlayer.reset();
					RTMediaPlayer.clear();
					break;
				}
			}
		};
		//----------------------------------------------------------------------------------------
		public void cancelVideoDownload(){
			if(videoDownLoaderTask != null && !videoDownLoaderTask.isCancelled()){
				videoDownLoaderTask.cancel(true);
				Log.i(TAG, "ViewHolder :: cancelVideoDownload : onBackPressed : CONTENT_ID_VIDEO_CANCEL - true");
			}

		}
		public void resetPlayingInFeed(){
			Log.w(TAG, "---resetPlayingInFeed----");
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				playing_entry.isPlaying = false;
				playing_entry.isAutoPlay = false;
				//				Log.v(TAG, "ViewHolder :: onClick :: position : "+k+", modified_entry.isPlaying : "+playing_entry.isPlaying);
			}
		}
		public void resetVideoDownloadInfeed(){
			Log.w(TAG, "---resetVideoDownloadInfeed----");
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				playing_entry.isVideoDownloading = false;
			}
		}
		public void resetVideoUploaddInfeed(){
			Log.w(TAG, "---resetVideoDownloadInfeed----");
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				playing_entry.isMediaUploading = false;
			}
		}
		public boolean isVideoDownloadInProgress(){
			boolean upload = false;
			Log.w(TAG, "---isVideoDownloadInProgress----");
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				Log.w(TAG, "isVideoDownloadInProgress at : "+k+" : "+playing_entry.isVideoDownloading);
				if(playing_entry.isVideoDownloading)
					return true;
			}
			return upload;
		}
		//--------------------------------------------Video Downloading-----------------------------
		private class LoadVideoFromURL extends AsyncTask<String, Integer, String>{

			@Override
			protected void onCancelled(String result) {
				Log.i(TAG, "LoadVideoFromURL :: onCancelled : "+result);
				if(result != null && isFileExists(result.substring(result.lastIndexOf("/") + 1))){
					File file = new File(result);
					boolean deleted = file.delete();
					Log.i(TAG, "LoadVideoFromURL :: onCancelled : "+result+" deleted : "+deleted);
				}
				resetVideoDownloadInfeed();
				runOnUiThread(new Runnable() {
					public void run() {
						if(mMediaAdapter != null)
							mMediaAdapter.notifyDataSetChanged();
						progress_bar.setProgress(0);
						progress_bar.setVisibility(View.GONE);
						download_circular_progress_percentage.setVisibility(View.GONE);
						viewHolder.download_arrow.setId(CONTENT_ID_VIDEO);
						viewHolder.download_arrow.setImageResource(R.drawable.download_down);
					}
				});
				super.onCancelled(result);
			}
			@Override
			protected void onPostExecute(String file_path) {
				//				CHAT_REFRESH_TIME = CHAT_NORMAL_REFRESH_TIME;
				if(videoDownloadCancelled){
					if(file_path != null && isFileExists(file_path.substring(file_path.lastIndexOf("/") + 1))){
						File file = new File(file_path);
						boolean deleted = file.delete();
						Log.i(TAG, "LoadVideoFromURL :: onPostExecute : videoDownloadCancelled = true - "+file_path+" deleted : "+deleted);
						return;
					}
				}
				startTimerToRefresh();
				if(progress_bar != null)
					progress_bar.setVisibility(View.GONE);
				if(download_circular_progress_percentage != null)
					download_circular_progress_percentage.setVisibility(View.GONE);
				if(file_path != null){
					resetVideoDownloadInfeed();
					playFromURLMain(file_path);
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				//				CHAT_REFRESH_TIME = CHAT_MAX_REFRESH_TIME;
				stopTimerToRefresh();
				progress_bar.setVisibility(View.VISIBLE);
				viewHolder.download_arrow.setImageResource(R.drawable.cancel_download);
				viewHolder.download_arrow.setOnClickListener(viewHolder);
				download_circular_progress_percentage.setVisibility(View.VISIBLE);
				download_circular_progress_percentage.setText("0%");
				isVideoDownloading = true;
			}
			@Override
			protected String doInBackground(String... params) {
				String fullPath = null;
				String fileName = null;
				String data_url = params[0];
				int count;
				String extension;
				// TODO Auto-generated method stub
				try{

					extension = MimeTypeMap.getFileExtensionFromUrl(data_url);
					if(extension == null || extension.trim().length() <= 0)
					{
						try{
							extension = data_url.substring(data_url.lastIndexOf(".") + 1);
						}catch (Exception e) {
							extension = "mp4";
						}
					}
					//Extract File name from the URL
					if(data_url.lastIndexOf('/') != -1 && data_url.lastIndexOf('.') != -1)
					{
						if(data_url.lastIndexOf('.') > data_url.lastIndexOf('/'))
							fileName = data_url.substring(data_url.lastIndexOf("/") + 1, data_url.lastIndexOf("."));
						else
							fileName = data_url.substring(data_url.lastIndexOf("=") + 1);
						File folder = new File(Environment.getExternalStorageDirectory() + "/YelaTalk");
						if (!folder.exists()) {
							folder.mkdir();
						}
						//fullPath = new StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append(fileName).append('.').append(extension).toString();
						fullPath = new StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append("YelaTalk").append('/').append(fileName).append('.').append(extension).toString();
						//Check if filename exists
						if(isFileExists(data_url.substring(data_url.lastIndexOf("/") + 1))){
							//Return the same path
							return fullPath;
						}
					}

					URL url = new URL(data_url);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					//		            if(data_url.lastIndexOf(".mp3") != -1)
					{
						connection.setRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
						connection.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
					}
					connection.connect();
					// getting file length
					int lenghtOfFile = connection.getContentLength();
					Log.d(TAG, "LoadVideoFromURL :: doInBackground : Length of File : "+lenghtOfFile);
					// input stream to read file - with 8k buffer
					InputStream input = new BufferedInputStream(url.openStream(), 8192);
					// Output stream to write file
					OutputStream output = new FileOutputStream(fullPath);
					byte data[] = new byte[4096];
					long total = 0;
					while ((count = input.read(data)) != -1) {
						if(isCancelled() || videoDownloadCancelled){
							output.close();
							break;
						}
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
					try{
						if(extension.equals("mp4")  || extension.equals("3gp")){
							ContentValues values = new ContentValues();
							values.put(Video.Media.TITLE, fileName);
							values.put(Video.Media.DESCRIPTION, "YelaTalk");
							values.put(Video.Media.DATE_TAKEN, System.currentTimeMillis ());
							/* values.put(Video.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
								    values.put(Video.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));*/
							values.put("_data", fullPath);

							android.content.ContentResolver cr = getContentResolver();
							cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
						}
					}
					catch(Exception x){

					}
				}catch(Exception ex){
					resetVideoDownloadInfeed();
				}
				return fullPath;
			}

			/**
			 * Updating progress bar
			 * */
			protected void onProgressUpdate(Integer... progress) {
				//		    	if(log)
				//		    	Log.i(TAG, "onProgressUpdate :: progress : "+progress[0]);
				progress_bar.setVisibility(View.VISIBLE);
				progress_bar.setProgress(progress[0]);
				//		    	progress_bar.setSecondaryProgress(progress[0] + 5);
				download_circular_progress_percentage.setVisibility(View.VISIBLE);
				download_circular_progress_percentage.setText(""+progress[0]+"%");
			}

		}
	}

	private class CommunityChatAdapter extends BaseAdapter {
		private Context context;
		public boolean isScrolling;
		private int currentPlayingPos = -1;

		public CommunityChatAdapter(Context context) {
			this.context = context;
		}
		//
		// public int getCount() {
		// if (feed == null || feed.entry == null || feed.entry.size() == 0)
		// return -1;
		// return feed.entry.size() + 1;
		// }
		//
		// public Entry getItem(int position) {
		// try{
		// if (position > -1 && null != feed && position <= feed.entry.size())
		// return feed.entry.get(feed.entry.size() - position);
		// }catch (Exception e) {
		// return feed.entry.get(feed.entry.size()-1);
		// }
		// return null;
		// }

		public int getCount() {
			try{
				if (feed == null || feed.entry == null || feed.entry.size() == 0)
					return -1;
				return feed.entry.size();
			}catch(Exception e){
				return -1;
			}

		}

		public Entry getItem(int position) {
			try{
				return feed.entry.get((feed.entry.size() - position) - 1);
			}catch(Exception e){
				return null;
			}
		}
		/*public ReportedByUser getItemR(int position) {
			try{
				return feed.ReportedByUser.get((feed.ReportedByUser.size() - position) - 1);
			}catch(Exception e){
				return null;
			}
		}*/
		public int getFeedItemCount() {
			if(feed.entry != null)
				return feed.entry.size();
			else
				return 0;
		}
		public void setAutoPlayAtPos(int position, boolean bool) {
			try{
				CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				entry.isAutoPlay = bool;
			}catch(Exception e){
			}
		}
		public void setPlayAtPos(int position, boolean bool) {
			try{
				CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				entry.isPlaying = bool;
			}catch(Exception e){
			}
		}
		public void resetVideoUploaddInfeed(){
			Log.w(TAG, "---resetVideoDownloadInfeed----");
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				playing_entry.isMediaUploading = false;
			}
		}
		public void resetSelectionInfeed(){
			Log.w(TAG, "---resetVideoDownloadInfeed----");
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				playing_entry.isSelected = false;
			}
		}
		public int getCurrentPlayingPosition() {
			return currentPlayingPos;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		private void setFontSizeForDeviceResolution(TextView text_view)
		{   try{
			int density = getResources().getDisplayMetrics().densityDpi;
			switch (density)
			{
			case DisplayMetrics.DENSITY_MEDIUM:
				text_view.setTextSize(14);
				break;
			case DisplayMetrics.DENSITY_HIGH:
				text_view.setTextSize(14);
				break;
			case DisplayMetrics.DENSITY_LOW:
				text_view.setTextSize(14);
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				text_view.setTextSize(15);
				break;
			case DisplayMetrics.DENSITY_TV:
				break;
			case DisplayMetrics.DENSITY_XXHIGH:
				text_view.setTextSize(15);
				break;
			/*case DisplayMetrics.DENSITY_XXXHIGH:
				text_view.setTextSize(18);
				break;*/
			default:
				text_view.setTextSize(15);
				break;
			}
		}catch(Exception ex){
			text_view.setTextSize(15);
		}
		}
		@SuppressWarnings("deprecation")
		public View getView(final int position, View convertView, ViewGroup arg2) 
		{
			View listItemView = null;
			String gMuc = null;
			try {
				if (feed.nexturl != null && feed.nexturl.indexOf("http") != -1
						&& totalFeedOnServer > 20) {
					loadingView.setVisibility(View.VISIBLE);
				} else
					loadingView.setVisibility(View.GONE);
				final CommunityFeed.Entry entry = (CommunityFeed.Entry) getItem(position);
				//CommunityFeed.ReportedByUser reportedbyUser = (CommunityFeed.ReportedByUser) getItemR(position);
				if (convertView == null) 
				{
					viewHolder = new ViewHolder();
					listItemView = LayoutInflater.from(context).inflate(R.layout.community_chatlist_row, null);

					viewHolder.txtview_reportedby = (TextView) listItemView.findViewById(R.community_chatlist_row.txtview_reportedby);
					viewHolder.reported_layout = (LinearLayout) listItemView.findViewById(R.community_chatlist_row.reported_layout);
					viewHolder.reported_layout_in = (LinearLayout) listItemView.findViewById(R.community_chatlist_row.reported_layout_in);
					viewHolder.txt_reporter_one = (TextView)listItemView.findViewById(R.id.txt_reporter_one);
					viewHolder.txt_reporter_two = (TextView)listItemView.findViewById(R.id.txt_reporter_two);
					viewHolder.txt_reporter_three = (TextView)listItemView.findViewById(R.id.txt_reporter_three);
					viewHolder.main_parent_layout = (LinearLayout) listItemView
							.findViewById(R.community_chatlist_row.main_parent_layout);
					viewHolder.bubbleLayout = (LinearLayout) listItemView
							.findViewById(R.community_chatlist_row.communityInfoLayout);
					viewHolder.otherLayout = (LinearLayout) listItemView
							.findViewById(R.community_chatlist_row.otherSenderLayout);
					viewHolder.myLayout = (LinearLayout) listItemView
							.findViewById(R.community_chatlist_row.myLayout);
					viewHolder.senderImage = ((ImageView) listItemView
							.findViewById(R.community_chatlist_row.senderImage));
					viewHolder.myImage = ((ImageView) listItemView
							.findViewById(R.community_chatlist_row.myImage));
					viewHolder.user_menus = ((ImageView) listItemView
							.findViewById(R.community_chatlist_row.user_menus));
					viewHolder.my_menus = ((ImageView) listItemView
							.findViewById(R.community_chatlist_row.my_menus));
					viewHolder.senderName = ((TextView) listItemView
							.findViewById(R.community_chatlist_row.senderName));
					viewHolder.fn_ln = (TextView) listItemView
							.findViewById(R.community_chatlist_row.fn_ln);
					viewHolder.chatMessage = ((EmojiconTextView) listItemView
							.findViewById(R.community_chatlist_row.chatMessage));
					viewHolder.replyto = ((TextView) listItemView
							.findViewById(R.community_chatlist_row.replyto));
					viewHolder.createdDate = ((TextView) listItemView
							.findViewById(R.community_chatlist_row.createdDate));
					viewHolder.inboxLayout_imageLayout_video = (LinearLayout) listItemView
							.findViewById(R.id.inboxLayout_imageLayout_video);
					viewHolder.inboxLayout_imageLayout_image = (LinearLayout) listItemView
							.findViewById(R.id.inboxLayout_imageLayout_image);
					viewHolder.inboxLayout_imageLayout_voice = (LinearLayout) listItemView
							.findViewById(R.id.inboxLayout_imageLayout_voice);
					if(systemMessageAction){
						//Report message Action Bar
						viewHolder.report_action_linear = (LinearLayout) listItemView.findViewById(R.community_chatlist_row.report_action_linear);
					}

					//Static one
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static = (LinearLayout) listItemView
							.findViewById(R.id.inboxLayout_imageLayout_voice_media_play_layout_static);
					viewHolder.ll_media_play_static = ((ImageView) listItemView.findViewById(R.id.ll_media_play_static));
					viewHolder.ll_mediavoicePlayingDialog_progressbar_static = ((SeekBar) listItemView.findViewById(R.id.ll_mediavoicePlayingDialog_progressbar_static));
					viewHolder.ll_time_panel_static = ((LinearLayout) listItemView.findViewById(R.id.ll_time_panel_static));
					viewHolder.ll_dummyp_static = ((TextView) listItemView.findViewById(R.id.ll_dummyp_static));
					viewHolder.ll_total_autio_time_static = ((TextView) listItemView.findViewById(R.id.ll_total_autio_time_static));
					viewHolder.ll_played_autio_time_static = ((TextView) listItemView.findViewById(R.id.ll_played_autio_time_static));
					viewHolder.ll_streemStatus_linear_static = ((LinearLayout) listItemView.findViewById(R.id.ll_streemStatus_linear_static));
					viewHolder.ll_streemStatus_static = ((TextView) listItemView.findViewById(R.id.ll_streemStatus_static));

					//Playing one
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout = (LinearLayout) listItemView
							.findViewById(R.id.inboxLayout_imageLayout_voice_media_play_layout);
					viewHolder.ll_media_play = ((ImageView) listItemView.findViewById(R.id.ll_media_play));
					viewHolder.ll_mediavoicePlayingDialog_progressbar = ((SeekBar) listItemView.findViewById(R.id.ll_mediavoicePlayingDialog_progressbar));
					viewHolder.ll_time_panel = ((LinearLayout) listItemView.findViewById(R.id.ll_time_panel));
					viewHolder.ll_dummyp = ((TextView) listItemView.findViewById(R.id.ll_dummyp));
					viewHolder.ll_total_autio_time = ((TextView) listItemView.findViewById(R.id.ll_total_autio_time));
					viewHolder.ll_played_autio_time = ((TextView) listItemView.findViewById(R.id.ll_played_autio_time));
					viewHolder.ll_streemStatus_linear = ((LinearLayout) listItemView.findViewById(R.id.ll_streemStatus_linear));
					viewHolder.ll_streemStatus = ((TextView) listItemView.findViewById(R.id.ll_streemStatus));

					//For Video Download
					viewHolder.download_view = ((RelativeLayout) listItemView.findViewById(R.id.video_view));
					viewHolder.video_img_view = ((ImageView) listItemView.findViewById(R.id.video_img));
					viewHolder.download_arrow = ((ImageView) listItemView.findViewById(R.id.video_download_down));
					viewHolder.progress_bar = ((ProgressBar) listItemView.findViewById(R.id.download_circular_progress));
					viewHolder.download_circular_progress_percentage = ((TextView) listItemView.findViewById(R.id.download_circular_progress_percentage));
					listItemView.setTag(viewHolder);
				} else {
					listItemView = (View) convertView;
					viewHolder = (ViewHolder) listItemView.getTag();
				}

				if(entry.isSelected)
					viewHolder.main_parent_layout.setBackgroundResource(R.drawable.channel_profile_patch);
				else{
					//viewHolder.main_parent_layout.setBackground(null);
					viewHolder.main_parent_layout.setBackgroundResource(0);
				}
				if (entry.senderId == BusinessProxy.sSelf.getUserId()) 
				{
					viewHolder.bubbleLayout.setBackgroundResource(R.drawable.green_bubble);
					((LinearLayout.LayoutParams) viewHolder.bubbleLayout.getLayoutParams()).gravity = Gravity.RIGHT;
					viewHolder.myLayout.setVisibility(View.VISIBLE);
					viewHolder.otherLayout.setVisibility(View.GONE);
					viewHolder.chatMessage.setTextColor(getResources().getColor(R.color.sub_heading));
					viewHolder.fn_ln.setTextColor(getResources().getColor(R.color.chat_bubble_name));
					viewHolder.fn_ln.setVisibility(View.GONE);
					viewHolder.createdDate.setTextColor(getResources().getColor(R.color.sub_heading));
					viewHolder.createdDate.setGravity(Gravity.RIGHT);
					viewHolder.reported_layout.setGravity(Gravity.RIGHT);
					viewHolder.reported_layout_in.setGravity(Gravity.RIGHT);

					viewHolder.senderImage.setVisibility(View.GONE);
				} 
				else
				{
					viewHolder.bubbleLayout.setBackgroundResource(R.drawable.gray_bubble);
					((LinearLayout.LayoutParams) viewHolder.bubbleLayout.getLayoutParams()).gravity = Gravity.LEFT;
					viewHolder.myLayout.setVisibility(View.GONE);
					viewHolder.otherLayout.setVisibility(View.VISIBLE);
					viewHolder.senderName.setGravity(Gravity.LEFT);
					viewHolder.chatMessage.setGravity(Gravity.LEFT);
					viewHolder.replyto.setGravity(Gravity.LEFT);
					viewHolder.reported_layout.setGravity(Gravity.LEFT);
					viewHolder.reported_layout_in.setGravity(Gravity.LEFT);
					viewHolder.createdDate.setGravity(Gravity.RIGHT);
					viewHolder.inboxLayout_imageLayout_voice.setGravity(Gravity.LEFT);
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setGravity(Gravity.LEFT);
					viewHolder.chatMessage.setTextColor(getResources().getColor(R.color.sub_heading));

					viewHolder.fn_ln.setGravity(Gravity.LEFT);
					viewHolder.fn_ln.setTextColor(getResources().getColor(R.color.chat_bubble_name));

					viewHolder.createdDate.setTextColor(getResources().getColor(R.color.sub_heading));
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
					viewHolder.inboxLayout_imageLayout_image.setLayoutParams(params);
					params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
					viewHolder.inboxLayout_imageLayout_video.setLayoutParams(params);
					params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
					params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
					viewHolder.inboxLayout_imageLayout_voice.setLayoutParams(params);
					if(!systemMessageAction){
						viewHolder.senderImage.setVisibility(View.VISIBLE);
						viewHolder.senderImage.setTag(position);
						String n = entry.senderName.trim();
						viewHolder.senderImage.setOnClickListener(userProfileClick);
						viewHolder.senderImage.setTag(n);
						if (n.indexOf('<') != -1 && n.indexOf('>') != -1)
							n = n.substring(n.indexOf('<') + 1, n.lastIndexOf('>'));
						// viewHolder.myImage.setTag(position);
						imageManager.download(n, viewHolder.senderImage, ChannelReportActivity.this, ImageDownloader.TYPE_THUMB_BUDDY);
						//	imageLoader.displayImage(n, viewHolder.senderImage, options);
					}
					else
						viewHolder.senderImage.setVisibility(View.GONE);
					viewHolder.user_menus.setTag(position);
					viewHolder.my_menus.setTag(position);
					if ((entry.firstName != null && entry.firstName.trim().length() > 0)
							|| (entry.lastName != null && entry.lastName.trim().length() > 0)) 
					{
						viewHolder.fn_ln.setVisibility(View.VISIBLE);
						viewHolder.fn_ln.setText(entry.firstName + " "+ entry.lastName);
					} else {
						viewHolder.fn_ln.setText("");
					}
				}

				if (entry.senderName != null && entry.senderName.trim().length() > 0) {
					viewHolder.senderName.setText(entry.senderName);
				} else {
					viewHolder.senderName.setText("");
				}
				//==============================================
				/*if(entry.reportedBy != null  && entry.reportedBy.trim().length() > 0)
				{
					//if(entry.reportedBy.endsWith(", ")){
					//	entry.reportedBy  = entry.reportedBy.substring(0, entry.reportedBy.length() - 2);
						int occurance = StringUtils.countMatches(entry.reportedBy, ",");
						if(occurance > 3){
							String[] parts = entry.reportedBy.split(",");
								String local = parts[0] + "," + parts[1]+ "," + parts[2] +" and "+(parts.length-3)+" more";
								viewHolder.txtview_reportedby.setText(""+local);	
						}else
						{
							viewHolder.txtview_reportedby.setText(""+entry.reportedBy);	
						}
					//}
					viewHolder.reported_layout.setVisibility(View.VISIBLE);

				}
				else
				{
					viewHolder.reported_layout.setVisibility(View.GONE);
					viewHolder.txtview_reportedby.setText("");
				}*/
				///// ====NEW 
				if(entry.ReportedByUser != null){
					viewHolder.txtview_reportedby.setText("");
					viewHolder.txt_reporter_three.setText("");
					viewHolder.txt_reporter_two.setText("");
					viewHolder.txt_reporter_one.setText("");
					
					int countRegister = entry.ReportedByUser.size();
					if(countRegister != 0 && (countRegister >= 1 )){
						viewHolder.reported_layout.setVisibility(View.VISIBLE);
						viewHolder.reported_layout_in.setVisibility(View.VISIBLE);
						viewHolder.txt_reporter_one.setText(entry.ReportedByUser.get(0).firstName +" " + entry.ReportedByUser.get(0).lastName);
					}
					if(countRegister != 0 && countRegister >= 2){
						viewHolder.reported_layout.setVisibility(View.VISIBLE);
						viewHolder.reported_layout_in.setVisibility(View.VISIBLE);		
						viewHolder.txt_reporter_two.setText(", " + entry.ReportedByUser.get(1).firstName +" " + entry.ReportedByUser.get(1).lastName);
					}
					if(countRegister != 0 && countRegister >= 3){
						viewHolder.reported_layout.setVisibility(View.VISIBLE);
						viewHolder.reported_layout_in.setVisibility(View.VISIBLE);
						viewHolder.txt_reporter_three.setText(", " + entry.ReportedByUser.get(2).firstName +" " + entry.ReportedByUser.get(2).lastName);
						viewHolder.txtview_reportedby.setVisibility(View.VISIBLE);
						viewHolder.txtview_reportedby.setText(getString(R.string.and) +" "+(entry.ReportedByUser.size()-3)+getString(R.string.more));
					}
					/*if(countRegister > 3){

				}*/
				}else
				{
					viewHolder.reported_layout.setVisibility(View.GONE);
					viewHolder.reported_layout_in.setVisibility(View.GONE);
					viewHolder.txtview_reportedby.setVisibility(View.GONE);
					viewHolder.txtview_reportedby.setText("");
					viewHolder.txt_reporter_three.setText("");
					viewHolder.txt_reporter_two.setText("");
					viewHolder.txt_reporter_one.setText("");
				}


				viewHolder.txtview_reportedby.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Vector abc = new Vector();
						for(int i=0;i<entry.ReportedByUser.size();i++)
						{
							abc.add(entry.ReportedByUser.get(i).firstName);
						}
						showDialogList(abc);
						//
						//
						//
						//
						//
						//

					}
				});
				//int countRegister
				//=================================================
				if (entry.messageText != null && entry.messageText.trim().length() > 0) 
				{
					byte[] messageText = Base64.decode(entry.messageText, Base64.DEFAULT);
					if((new String(messageText, "utf-8")).length() == 1){
						//						viewHolder.chatMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
						viewHolder.chatMessage.setEmojiconSize(50);
					}
					else
						setFontSizeForDeviceResolution(viewHolder.chatMessage);
					viewHolder.chatMessage.setText(new String(messageText, "utf-8"));
					viewHolder.chatMessage.setVisibility(View.VISIBLE);
				} else {
					viewHolder.chatMessage.setText("");
					viewHolder.chatMessage.setVisibility(View.GONE);
				}

				//Added for enabling on text message
				if(!systemMessageAction){
					viewHolder.chatMessage.setClickable(true);
					viewHolder.chatMessage.setOnLongClickListener(onLongClickListenerShowCopy);
					viewHolder.chatMessage.setTag(entry);
				}
				else{
					//Report Action linear layout
					viewHolder.report_action_linear.setVisibility(View.VISIBLE);
				}

				if (entry.replyTo != null && entry.replyTo.length() > 0) {
					if (entry.replyToFirstName.trim().length() > 0
							|| entry.replyToLastName.trim().length() > 0)
						viewHolder.replyto.setText("@(" + entry.replyTo + "<"
								+ entry.replyToFirstName.trim() + " "
								+ entry.replyToLastName.trim() + ">" + ")");
					else
						viewHolder.replyto.setText("@(" + entry.replyTo + ")");
					viewHolder.replyto.setVisibility(View.VISIBLE);
				} else
					viewHolder.replyto.setVisibility(View.GONE);
				if (entry.createdDate != null && entry.createdDate.trim().length() > 0) {
					viewHolder.createdDate.setText(compareDate(
							entry.createdDate, ChannelReportActivity.this));
					/*String[] str = entry.createdDate.split(" ");
					if(str.length>=1)
						viewHolder.createdDate.setText(str[1]);*/
					viewHolder.createdDate.setText(compareDate(
							entry.createdDate, ChannelReportActivity.this));
				} else {
					viewHolder.createdDate.setText("");
				}
				// viewHolder.chatMessage.setText(""+entry.messageId);

				viewHolder.download_view.setVisibility(View.GONE);
				viewHolder.video_img_view.setImageDrawable(null);
				int index = 0;
				boolean audio = false;
				InputStream is = null;
				//				String tag_value = null;
				if(entry.media!=null && entry.media.size()>0)
				{
					for (int j = 0; j < (entry.media.size()); j += 8) 
					{
						String s = (String) entry.media.elementAt(j + 1);
						String local_path = null;
						String file_name = null;
						String video_temp_url = null;
						//						for(int p = 0; p < entry.media.size(); p++)
						//							Log.i(TAG, "entry at "+p+" = "+(String)entry.media.elementAt(p));
						if (audio) {
							j = j - 4;
							audio = false;
						}
						if (s.equalsIgnoreCase("audio"))
							audio = true;
						if (!s.equalsIgnoreCase("video"))
							continue;
						viewHolder.inboxLayout_imageLayout_video.setVisibility(View.VISIBLE);
						try {
							//							ImageView imageViewl = getNewImageView(MEDIA_TYPE_VIDEO);
							//							imageViewl.setTag((String) entry.media.elementAt(j + 2) + "~" + (String) entry.media.elementAt(j + 6));
							//							imageViewl.setId(CONTENT_ID_VIDEO + index++);//
							//							s = (String) imageViewl.getTag();

							if(entry.media.size() > 4){
								s = (String) entry.media.elementAt(j + 2) + "~" + (String) entry.media.elementAt(j + 6);
								file_name = s.substring(0, s.indexOf("~"));
								file_name = file_name.substring(file_name.lastIndexOf("/") + 1);
								//Show Video download view, and add tags
								local_path = (String) entry.media.elementAt(j + 2) + "~" + (String) entry.media.elementAt(j + 6);
							}else{
								video_temp_url = (String) entry.media.elementAt(j + 2);
								if(video_temp_url.indexOf('.') != -1)
									video_temp_url = video_temp_url.substring(0, video_temp_url.lastIndexOf('.')) + ".jpg?height=300&width=300";
								else
									video_temp_url = "no_thumb";
								s = (String) entry.media.elementAt(j + 2) + "~" + video_temp_url;
								file_name = s.substring(0, s.indexOf("~"));
								file_name = file_name.substring(file_name.lastIndexOf("/") + 1);
								//Show Video download view, and add tags
								local_path = (String) entry.media.elementAt(j + 2) + "~" + video_temp_url;
								video_temp_url = null;
							}

							Object[] obj = new Object[2];
							obj[0] = entry;
							obj[1] = local_path;
							viewHolder.download_view.setVisibility(View.VISIBLE);
							viewHolder.download_view.setTag(obj);
							viewHolder.download_view.setId(CONTENT_ID_VIDEO + index++);
							viewHolder.video_img_view.setTag(obj);
							viewHolder.video_img_view.setId(CONTENT_ID_VIDEO + index++);
							//Check if file has been already down-loaded by filename, then do not show arrow icon, instead show play icon.
							viewHolder.video_img_view.setOnClickListener(viewHolder);
							if(!systemMessageAction){
								final CommunityFeed.Entry tempEntry = entry;
								viewHolder.video_img_view.setOnLongClickListener(new OnLongClickListener() {
									@Override
									public boolean onLongClick(View v) {
										// TODO Auto-generated method stub
										viewHolder.bubbleLayout.setTag(tempEntry);
										viewHolder.bubbleLayout.performLongClick();
										return false;
									}
								});
							}
							//							viewHolder.inboxLayout_imageLayout_video.addView(imageViewl);
							viewHolder.video_img_view.setVisibility(View.VISIBLE);
							viewHolder.video_img_view.setScaleType(ScaleType.CENTER_CROP);
							if (((String) entry.media.elementAt(j + 3)).equalsIgnoreCase("local")) 
							{
								//Get Local Thumbnail Image to display here
								Bitmap bm = ImageUtils.getVideoFileThumb(ChannelReportActivity.this, ImageUtils
										.getVideoFileId(ChannelReportActivity.this, s.substring(s.lastIndexOf("~") + 1, s.length())));
								if (bm != null)
									viewHolder.video_img_view.setImageBitmap(bm);
								if(entry.isMediaUploading){
									viewHolder.progress_bar.setVisibility(View.VISIBLE);
									viewHolder.download_arrow.setVisibility(View.GONE);
									if(uploadingPercentage <= 100){
										viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
									}
								}else{
									if(!entry.isVideoDownloading){
										viewHolder.download_circular_progress_percentage.setVisibility(View.GONE);
										viewHolder.progress_bar.setVisibility(View.GONE);
										viewHolder.download_arrow.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setImageResource(R.drawable.play_video);
									}
								}
							}
							else{
								if(s.substring(s.lastIndexOf("~") + 1, s.length()).contains("no_thumb"))
									viewHolder.video_img_view.setBackgroundResource(0);
								else
									imageLoader.displayImage(s.substring(s.lastIndexOf("~") + 1, s.length()), viewHolder.video_img_view, options);
								if(isFileExists(file_name)){
									if(entry.isVideoDownloading){
										viewHolder.download_arrow.setTag(obj);
										viewHolder.download_arrow.setImageResource(R.drawable.cancel_download);
										viewHolder.download_arrow.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setId(CONTENT_ID_VIDEO_CANCEL);
										viewHolder.progress_bar.setVisibility(View.VISIBLE);
										viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setOnClickListener(viewHolder);
									}
									else{
										viewHolder.download_arrow.setTag(obj);
										viewHolder.progress_bar.setVisibility(View.GONE);
										viewHolder.download_circular_progress_percentage.setVisibility(View.GONE);
										viewHolder.download_arrow.setImageResource(R.drawable.play_video);
										viewHolder.download_arrow.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setId(CONTENT_ID_VIDEO + index++);
									}
								}else{
									if(entry.isVideoDownloading){
										viewHolder.download_arrow.setTag(obj);
										viewHolder.download_arrow.setImageResource(R.drawable.cancel_download);
										viewHolder.download_arrow.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setId(CONTENT_ID_VIDEO_CANCEL);
										viewHolder.progress_bar.setVisibility(View.VISIBLE);
										viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setOnClickListener(viewHolder);
									}else{
										viewHolder.download_arrow.setTag(obj);
										viewHolder.download_circular_progress_percentage.setVisibility(View.GONE);
										viewHolder.progress_bar.setVisibility(View.GONE);
										viewHolder.download_arrow.setImageResource(R.drawable.download_down);
										viewHolder.download_arrow.setVisibility(View.VISIBLE);
										viewHolder.download_arrow.setId(CONTENT_ID_VIDEO + index++);
										viewHolder.download_view.setOnClickListener(viewHolder);
									}
								}
							}

						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}else
				{
					viewHolder.download_view.setVisibility(View.GONE);
					//					viewHolder.progress_bar.setVisibility(View.GONE);
					viewHolder.download_arrow.setVisibility(View.GONE);
					viewHolder.video_img_view.setVisibility(View.GONE);

					viewHolder.inboxLayout_imageLayout_voice.setVisibility(View.GONE);
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.GONE);
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.GONE);
					viewHolder.inboxLayout_imageLayout_video.setVisibility(View.GONE);
				}
				recycleImageView(viewHolder.inboxLayout_imageLayout_image);
				viewHolder.inboxLayout_imageLayout_image.removeAllViews();
				viewHolder.inboxLayout_imageLayout_image.setVisibility(View.GONE);
				index = 0;
				if(entry.media != null)
				{
					for (int j = 0; j < (entry.media.size()); j += 8) 
					{
						String s = (String) entry.media.elementAt(j + 1);
						if (audio) {
							j = j - 4;
							audio = false;
						}
						if(j < 0)
							j = 0;
						if (s.equalsIgnoreCase("audio"))
							audio = true;
						if (!s.equalsIgnoreCase("image"))
							continue;
						viewHolder.inboxLayout_imageLayout_image.setVisibility(View.VISIBLE);

						ImageView imageViewl = getNewImageView(MEDIA_TYPE_IMAGE);
						imageViewl.setScaleType(ScaleType.CENTER_CROP);
						String so=(feed.entry.size() - position) - 1 + "~"
								+ (String) entry.media.elementAt(j + 2) + "~"
								+ (String) entry.media.elementAt(j + 6);
						imageViewl.setTag((feed.entry.size() - position) - 1 + "~"
								+ (String) entry.media.elementAt(j + 2) + "~"
								+ (String) entry.media.elementAt(j + 6));
						s = (String) imageViewl.getTag();
						s = so;
						//						tag_value = so;
						gMuc = so;
						imageViewl.setId(CONTENT_ID_PICTURE + index++);
						String path = s;
						if (!((String) entry.media.elementAt(j + 3)).equalsIgnoreCase("local"))
							path = s.substring(s.lastIndexOf("/") + 1, s.length());
						s.substring(s.lastIndexOf("/") + 1, s.length());
						try {
							String img_path = s.substring(s.lastIndexOf("~") + 1);
							if (!((String) entry.media.elementAt(j + 3)).equalsIgnoreCase("local"))
							{
								imageLoader.displayImage(img_path, imageViewl, options);
								/*
	//							Log.i(TAG, "<< DOWNLOAGING IMAGE >> : "+img_path);
	//							ImageDownloader downloader = new ImageDownloader();
	//							downloader.download(img_path, imageViewl, CommunityChatActivity.this, ImageDownloader.TYPE_IMAGE);

								//http://"+Urls.TEJAS_HOST+"/rtMediaServer/get/1_1_1_A_I_I3_e4zmsn2hws.jpg?height=300&width=300
								//Check here is image file path exists
								String local_path = img_path.substring(img_path.lastIndexOf('/') + 1);
								if(local_path.indexOf('?') != -1)
									local_path = local_path.substring(0, local_path.indexOf('?'));
								if(!isFileExists(local_path)){
									if(fileDownloader == null)
										fileDownloader = new FileDownloader();
									if(!fileDownloader.isExecuting())
										fileDownloader.downloadFile(imageViewl, new String[]{img_path}, CommunityChatActivity.this);
								}else{
									//Set Local Image path
									imageViewl.setImageURI(getImageLocalPathUri(local_path));
								}
								 */} 
							else //Local Image received
							{
								//							Log.w(TAG, "getView :: ---entry.media.elementAt(j) -" + (Integer) entry.media.elementAt(j));
								//							if ((Integer) entry.media.elementAt(j) > 0) 
								//							{
								//								Log.w(TAG, "getView :: IF");
								//								imageViewl.setImageBitmap(MediaStore.Images.Thumbnails
								//										.getThumbnail(getApplicationContext().getContentResolver(),
								//												(Integer) entry.media.elementAt(j),
								//												MediaStore.Images.Thumbnails.MICRO_KIND, null));
								//							} 
								//							else 
								{
									//								Log.w(TAG, "getView :: ELSE");
									//								imageViewl.setImageURI(Uri.parse(path.substring(path.lastIndexOf("~") + 1, s.length())));
									/*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
									   params.setMargins(20,20,20,20);
									   imageViewl.setLayoutParams(params);*/
									BitmapFactory.Options bfo = new BitmapFactory.Options();
									bfo.inSampleSize = 2;
									Bitmap bm = BitmapFactory.decodeFile(path.substring(path.lastIndexOf("~") + 1, s.length()), bfo);
									bm = ImageUtils.rotateImage(path.substring(path.lastIndexOf("~") + 1, s.length()), bm);
									bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
									imageViewl.setImageBitmap(bm);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if(!systemMessageAction){
							final CommunityFeed.Entry tempEntry = entry;
							imageViewl.setOnLongClickListener(new OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {
									// TODO Auto-generated method stub
									viewHolder.bubbleLayout.setTag(tempEntry);
									viewHolder.bubbleLayout.performLongClick();
									return false;
								}
							});
						}
						viewHolder.inboxLayout_imageLayout_image.addView(imageViewl);
					}
					viewHolder.inboxLayout_imageLayout_voice.removeAllViews();
					viewHolder.inboxLayout_imageLayout_voice.setVisibility(View.GONE);
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.GONE);
					viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.GONE);
					for (int j = 0; j < (entry.media.size()); j += 8) {
						String s = (String) entry.media.elementAt(j + 1);
						if (!s.equalsIgnoreCase("audio"))
							continue;
						//						viewHolder.inboxLayout_imageLayout_voice.setVisibility(View.GONE);
						//						viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.VISIBLE);

						//Set progress bar Visible gone
						if(entry.isPlaying || entry.isAutoPlay){
							if(entry.isPlaying)
								currentPlayingPos = position;
							//							if(log)
							//								Log.v(TAG, "getView :: item count : " + getFeedItemCount() + ", pos : "+position+", isPlaying : "+entry.isPlaying+", % prog : "+viewHolder.ll_mediavoicePlayingDialog_progressbar.getProgress()+", ID : "+viewHolder.ll_mediavoicePlayingDialog_progressbar.getId());
							viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setVisibility(View.VISIBLE);
							viewHolder.ll_mediavoicePlayingDialog_progressbar.setVisibility(View.VISIBLE);
							viewHolder.ll_mediavoicePlayingDialog_progressbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

								@Override
								public void onStopTrackingTouch(SeekBar seekBar) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onStartTrackingTouch(SeekBar seekBar) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onProgressChanged(SeekBar seekBar, int progress,
										boolean fromUser) {
									// TODO Auto-generated method stub
									if(position == currentPlayingPos){
										//										if(log)
										//											Log.v(TAG, "getView :: onProgressChanged :: progress : "+progress);
										viewHolder.ll_mediavoicePlayingDialog_progressbar.setEnabled(true);
										viewHolder.ll_mediavoicePlayingDialog_progressbar.setProgress(progress);
									}
								}
							});
							viewHolder.ll_time_panel.setVisibility(View.VISIBLE);
							viewHolder.ll_total_autio_time.setVisibility(View.VISIBLE);
							viewHolder.ll_dummyp.setVisibility(View.VISIBLE);
							viewHolder.ll_played_autio_time.setVisibility(View.VISIBLE);
							if(entry.isAutoPlay && !entry.isPlaying)
								viewHolder.ll_streemStatus_linear.setVisibility(View.VISIBLE);
						}
						else{
							viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setVisibility(View.VISIBLE);
							//							viewHolder.ll_time_panel.setVisibility(View.GONE);
							//							viewHolder.ll_total_autio_time.setVisibility(View.GONE);
							//							viewHolder.ll_dummyp.setVisibility(View.GONE);
							//							viewHolder.ll_played_autio_time.setVisibility(View.GONE);
						}

						//						viewHolder.ll_mediavoicePlayingDialog_progressbar.setTag(entry.messageId);
						//						viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setId(CONTENT_ID_VOICE);
						s = (String) entry.media.elementAt(j + 2);
						//						ImageView imageViewl = getNewImageView(MEDIA_TYPE_AUDIO);
						String fileName = s.substring(s.lastIndexOf("/") + 1);

						String local_path = fileName.substring(fileName.lastIndexOf('/') + 1);
						if(local_path.indexOf('?') != -1)
							local_path = local_path.substring(0, local_path.indexOf('?'));
						if(!isFileExists(local_path) && !local_path.endsWith(".amr")){
							//Start Downloading voice here
							/*FileDownloader downloader = new FileDownloader();
							if(!downloader.isExecuting())
								downloader.downloadFile(imageViewl, new String[]{s}, CommunityChatActivity.this);*/
							//							imageViewl.setTag(entry);
							viewHolder.ll_media_play_static.setTag(entry); 
						}else{
							//Set Local Image path
							//							imageViewl.setTag(local_path);
							Object[] obj = new Object[2];
							obj[0] = entry;
							obj[1] = local_path;
							viewHolder.ll_media_play_static.setTag(obj);
							//							viewHolder.ll_media_play.setTag(local_path);
						}
						viewHolder.ll_media_play_static.setId(CONTENT_ID_VOICE);
						String local_str = null;
						for (int jj = 0; jj < (entry.media.size()); jj += 8) {
							if (((String) entry.media.elementAt(jj + 1)).equals("image"))
							{
								local_str = local_str +"image";
							}
							if (((String) entry.media.elementAt(jj + 1)).equals("audio"))
							{
								local_str = local_str +"audio";
							}
						}
						if(local_str.contains("image") && local_str.contains("audio")){
							//This is Talking-pic case
							viewHolder.ll_media_play_static.setTag(gMuc);
							viewHolder.ll_media_play.setOnClickListener(ChannelReportActivity.this);

							viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setTag(gMuc);
							viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setId(CONTENT_ID_PICTURE + index++);
							viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setOnClickListener(ChannelReportActivity.this);


							//					          viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setTag(gMuc);
							//					          viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setId(CONTENT_ID_PICTURE + index++);
							//					          viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setOnClickListener(ChannelReportActivity.this);

						}else if(local_str.contains("image") && !local_str.contains("audio")){
							viewHolder.ll_media_play_static.setTag(entry);
							viewHolder.ll_media_play_static.setOnClickListener(ChannelReportActivity.this);
						}else if(!local_str.contains("image") && local_str.contains("audio"))
						{
							viewHolder.ll_media_play_static.setOnClickListener(viewHolder);
							if(entry.isAutoPlay){
								if(!voiceIsPlaying){
									viewHolder.ll_media_play_static.performClick();
									if(log)
										Log.v(TAG, "getView :: performClick :: position : "+position+", entry.isAutoPlay : "+entry.isAutoPlay);
								}
								entry.isAutoPlay = false;
							}
						}

						if(!systemMessageAction){
							final CommunityFeed.Entry tempEntry = entry;
							viewHolder.inboxLayout_imageLayout_voice_media_play_layout_static.setOnLongClickListener(new OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {
									// TODO Auto-generated method stub
									viewHolder.bubbleLayout.setTag(tempEntry);
									viewHolder.bubbleLayout.performLongClick();
									return false;
								}
							});
						}

						//						viewHolder.ll_media_play.setOnLongClickListener(new OnLongClickListener() {
						//							@Override
						//							public boolean onLongClick(View v) {
						//								// TODO Auto-generated method stub
						//								viewHolder.bubbleLayout.setTag(entry);
						//								viewHolder.bubbleLayout.performLongClick();
						//								return false;
						//							}
						//						});
						//						viewHolder.inboxLayout_imageLayout_voice_media_play_layout.setOnLongClickListener(new OnLongClickListener() {
						//							@Override
						//							public boolean onLongClick(View v) {
						//								// TODO Auto-generated method stub
						//								viewHolder.bubbleLayout.setTag(entry);
						//								viewHolder.bubbleLayout.performLongClick();
						//								return false;
						//							}
						//						});
						//						viewHolder.inboxLayout_imageLayout_voice.addView(viewHolder.inboxLayout_imageLayout_voice_media_play_layout);
					}
				}
				viewHolder.bubbleLayout.setTag(entry);
				if(!systemMessageAction)
					viewHolder.bubbleLayout.setOnLongClickListener(onLongClickListenerShowCopy);
				return listItemView;
			} catch (Exception e) {
				e.printStackTrace();
				return listItemView;
			}
		}
	}
	boolean clickLeftRightCheck = false;
	boolean longPressFlag = false;
	OnLongClickListener onLongClickListenerShowCopy = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			Object obj = v.getTag();
			//			int AUDIO = 1 << 1;
			//			int IMAGE = 1 << 2;
			//			int VIDEO = 1 << 3;
			if(obj instanceof CommunityFeed.Entry)
			{
				if(systemMessageReportAction){
					final CommunityFeed.Entry message = (CommunityFeed.Entry) obj;
					AlertDialog.Builder builder = new AlertDialog.Builder(ChannelReportActivity.this);
					//    builder.setTitle(R.string.option);
					builder.setItems(R.array.report_options, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch(item){
							case 0:
								if (!ChannelReportActivity.this.checkInternetConnection()) {
									ChannelReportActivity.this.networkLossAlert();
									break;
								}
								DeleteRequest request = new DeleteRequest();
								request.execute(message.messageId, message.groupName, SPAM_REQUEST);
								if(mMediaAdapter != null){
									mMediaAdapter.notifyDataSetChanged();
								}
								break;
							case 1:
								if (!ChannelReportActivity.this.checkInternetConnection()) {
									ChannelReportActivity.this.networkLossAlert();
									break;
								}
								DeleteRequest requestdel = new DeleteRequest();
								requestdel.execute(message.messageId, message.groupName, DELETE_REQUEST);
								if(mMediaAdapter != null){
									mMediaAdapter.notifyDataSetChanged();
								}
							}
						}
					});
					AlertDialog alert = builder.create();
					alert.show();

				}else{
					if(mMediaAdapter != null){
						mMediaAdapter.resetSelectionInfeed();
					}
					final CommunityFeed.Entry message = (CommunityFeed.Entry) obj;
					message.isSelected = true;
					if(mMediaAdapter != null){
						mMediaAdapter.notifyDataSetChanged();
					}
					//				int media_Data = 0;
					//				if(message.media != null){
					//					for(int m = 0; m < message.media.size(); m++){
					//						if(((String)message.media.elementAt(m)).contains("audio"))
					//							media_Data |= AUDIO;
					//						if(((String)message.media.elementAt(m)).contains("image"))
					//							media_Data |= IMAGE;
					//						if(((String)message.media.elementAt(m)).contains("video"))
					//							media_Data |= VIDEO;
					//					}
					//				}
					if(!SettingData.sSelf.getUserName().equalsIgnoreCase(entry.ownerUsername) 
							&& !SettingData.sSelf.getUserName().equalsIgnoreCase(message.senderName))
					{	
						((ImageView) findViewById(R.id.option_delete_iv)).setVisibility(View.GONE);
					}
					else
						((ImageView) findViewById(R.id.option_delete_iv)).setVisibility(View.VISIBLE);

					if(SettingData.sSelf.getUserName().equalsIgnoreCase(message.senderName))
						((ImageView) findViewById(R.id.option_report_iv)).setVisibility(View.GONE);
					else
						((ImageView) findViewById(R.id.option_report_iv)).setVisibility(View.VISIBLE);

					((ImageView) findViewById(R.id.option_share_iv)).setVisibility(View.GONE);
					((ImageView) findViewById(R.id.option_copy_iv)).setVisibility(View.GONE);
					((ImageView) findViewById(R.id.option_share_iv)).setVisibility(View.VISIBLE);

					//				if((message.audioUrl != null && message.audioUrl.equals("")) 
					//						&& (message.imgUrl != null && message.imgUrl.equals(""))
					//						&& (message.vidUrl != null && message.vidUrl.equals(""))){
					//					((ImageView) findViewById(R.id.option_share_iv)).setVisibility(View.VISIBLE);
					//				}
					//				if(media_Data == 0){
					//					((ImageView) findViewById(R.id.option_share_iv)).setVisibility(View.VISIBLE);
					//				}
					//				else
					//					((ImageView) findViewById(R.id.option_share_iv)).setVisibility(View.GONE);

					if(message.messageText != null
							&& !message.messageText.equals("") ){
						((ImageView) findViewById(R.id.option_copy_iv)).setVisibility(View.VISIBLE);

					}

					((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.GONE);
					((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.VISIBLE);

					((ImageView) findViewById(R.id.option_close_iv)).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(mMediaAdapter != null)
								mMediaAdapter.resetSelectionInfeed();
							((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.VISIBLE);
							((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.GONE);
						}
					});

					((ImageView) findViewById(R.id.option_delete_iv)).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							deleteServerMessage(message.messageId, entry.groupName);
							CCB.MessageDeletion(message.messageId);
							((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.VISIBLE);
							((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.GONE);
						}
					});

					((ImageView) findViewById(R.id.option_report_iv)).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							reportServerMessage(message.messageId, entry.groupName);
							//	CCB.MessageDeletion(message.messageId);
							((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.VISIBLE);
							((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.GONE);
						}
					});
					((ImageView) findViewById(R.id.option_share_iv)).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.VISIBLE);
							((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.GONE);
							HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
							//						menuItems.put(0, getString(R.string.facebook_lbl));
							//						menuItems.put(1, getString(R.string.twitter_lbl));
							menuItems.put(0, getString(R.string.whatsapp_lbl));
							menuItems.put(1, getString(R.string.email_lbl));
							if(mMediaAdapter != null){
								mMediaAdapter.resetSelectionInfeed();
							}
							openActionSheet(menuItems, new OnMenuItemSelectedListener() {
								@Override
								public void MenuItemSelectedEvent(Integer selection) {
									switch (selection) {
									case 0:
										//new ShareRequest(selection).execute(message.messageId, "whatsapp", SHARE_REQUEST);
										//									new ShareRequest(selection).execute(entry.messageId, "fb", SHARE_REQUEST);
										break;
									case 1:
										//new ShareRequest(selection).execute(message.messageId, "email", SHARE_REQUEST);
										//									new ShareRequest(selection).execute(entry.messageId, "twitter", SHARE_REQUEST);
										break;
									case 2:
										//									new ShareRequest(selection).execute(entry.messageId, "whatsapp", SHARE_REQUEST);
										break;
									case 3:
										//									new ShareRequest(selection).execute(entry.messageId, "email", SHARE_REQUEST);
										break;
									}

								}
							}, null);
						}
					});
					((ImageView) findViewById(R.id.option_copy_iv)).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							final byte[] messageText = Base64.decode(message.messageText, Base64.DEFAULT);
							String copiedText = null;
							try {
								copiedText = new String(Base64.decode(message.messageText, Base64.DEFAULT), "utf-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							if (copiedText != null && copiedText.length() > 0
									&& !copiedText.equals("")) {

								int currentapiVersion = android.os.Build.VERSION.SDK_INT;
								if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
									android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
									ClipData clip = ClipData.newPlainText("label", copiedText);
									clipboard.setPrimaryClip(clip);
								} else {
									android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
									clipboard.setText(copiedText);
								}
								Toast.makeText(getApplicationContext(), getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
								if(mMediaAdapter != null){
									mMediaAdapter.resetSelectionInfeed();
									mMediaAdapter.notifyDataSetChanged();
								}
							}
							((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.VISIBLE);
							((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.GONE);
						}
					});
				}
			}
			return true;
		}
	};
	private String DELETE_REQUEST = "Delete";
	private String REPORT_REQUEST = "Report";
	private String SHARE_REQUEST = "Share";
	private String SPAM_REQUEST = "notspam";
	private void deleteServerMessage(final String msgId, final String groupName){
		new AlertDialog.Builder(this).setMessage(getString(R.string.delete_confirmation))
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				if (!ChannelReportActivity.this.checkInternetConnection()) {
					ChannelReportActivity.this.networkLossAlert();
					return;
				}
				DeleteRequest request = new DeleteRequest();
				request.execute(msgId, groupName, DELETE_REQUEST);
			}
		}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				if(mMediaAdapter != null){
					mMediaAdapter.resetSelectionInfeed();
					mMediaAdapter.notifyDataSetChanged();
				}
			}
		}).show();
	}

	private void reportServerMessage(final String msgId, final String groupName){

		new AlertDialog.Builder(this).setMessage(getString(R.string.report_confirmation))
		.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				if (!ChannelReportActivity.this
						.checkInternetConnection()) {
					ChannelReportActivity.this.networkLossAlert();
					return;
				}
				ReportRequest request = new ReportRequest();
				request.execute(msgId, groupName, REPORT_REQUEST);
			}
		}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				if(mMediaAdapter != null){
					mMediaAdapter.resetSelectionInfeed();
					mMediaAdapter.notifyDataSetChanged();
				}
			}
		}).show();

	}
	ProgressDialog loadingDialog;
	/*public class ShareRequest extends AsyncTask<String, Integer, String> {
		private int shareType = -1;
		@Override
		protected void onPreExecute() {
			//			rTDialog.show();
			loadingDialog = ProgressDialog.show(ChannelReportActivity.this, "", getString(R.string.loading_dot), true);
		}

		public ShareRequest(int type){
			this.shareType = type;	
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String strData = postDataOnServer(params[0], params[1], params[2]);
			return strData;
		}
		@Override
		protected void onPostExecute(String result) {
			//			rTDialog.dismiss();
			loadingDialog.dismiss();
			JSONObject jsonObject;
			try {
				if(result != null && !result.equals("") && result.contains("success")){
					jsonObject = new JSONObject(result);
					String status = jsonObject.getString("status");
					String url = jsonObject.getString("shareUrl");
					switch (shareType) {
					case 2:// Facebook
						break;
					case 3: // Twitter
						break;
					case 0:// Whats App
						PackageManager pm = getPackageManager();
						try {

							Intent waIntent = new Intent(Intent.ACTION_SEND);
							waIntent.setType("text/plain");

							PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
							//Check if package exists or not. If not then code 
							//in catch block will be called
							waIntent.setPackage("com.whatsapp");
							waIntent.putExtra(Intent.EXTRA_TEXT, url);
							startActivity(Intent.createChooser(waIntent, "Share with"));

						} catch (NameNotFoundException e) {
							Toast.makeText(ChannelReportActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
						}  
						break;
					case 1: // Email
						if (url != null && url.length() > 0 && !url.equals("")) {
							Intent i = new Intent(Intent.ACTION_SEND);
							i.setType("message/rfc822");
							i.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
							i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
							i.putExtra(Intent.EXTRA_TEXT, url);
							try {
								startActivity(Intent.createChooser(i, "Send mail..."));
							} catch (android.content.ActivityNotFoundException ex) {
								Toast.makeText(getApplicationContext(),
										getString(R.string.something_went_wrong),
										Toast.LENGTH_SHORT).show();
							}
						}
						break;
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/

	public class ReportRequest extends AsyncTask<String, Integer, String> {
		ProgressDialog rTDialog;
		@Override
		protected void onPreExecute() {
			rTDialog = ProgressDialog.show(ChannelReportActivity.this, "", getString(R.string.loading_dot), true);
			rTDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			//String strData = postDataOnServer(params[0], params[1], params[2]);
			/*if (strData != null  && (strData.trim().indexOf("1") != -1)) {
				return strData;
			}
			if(strData != null)
			{
				if(strData.contains("desc") ){
					return strData;
				}
			}*/
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			if(result!= null)
			{
				try{
					JSONObject  jobj = new JSONObject(result);
					if(result.contains("desc")){
						String desc = jobj.optString("desc").toString();
						Toast.makeText(ChannelReportActivity.this, desc,  Toast.LENGTH_LONG).show();
						if(mMediaAdapter != null){
							mMediaAdapter.resetSelectionInfeed();
							mMediaAdapter.notifyDataSetChanged();
						}
					}
				}catch(Exception e){

				}
			}
			rTDialog.dismiss();
		}
	}

	ProgressDialog deleteDialog;
	public class DeleteRequest extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			deleteDialog = ProgressDialog.show(ChannelReportActivity.this, "", getString(R.string.loading_dot), true);
			deleteDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
				String strData = postDataOnServer(params[0], params[1], params[2]);
				String desc ="Error";
		/*	if (strData != null  && (strData.trim().indexOf("1") != -1)) {
				return params[0];
				}*/
				if(strData.contains("desc")){
					try {
						JSONObject mainStr =  new JSONObject(strData);
						 desc = mainStr.getString("desc"); 
						 if(mainStr.has("status") && params[2].equals(DELETE_REQUEST)){
							 String sts = mainStr.getString("status"); 
							 if(sts.equals("1"))
							 deleteTextLocalMessage(params[0]);
						 }
						return desc;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else
				{
			return null;
				}
				return desc;
		}
		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "DeleteRequest :: onPostExecute : result : "+result);
			if(systemMessageAction){
				//Cancel Dialog and go back to previous Activity
				deleteDialog.dismiss();
				setResult(Activity.RESULT_OK, new Intent().putExtra("DELETE_MESSAGE_ID", deleteMessageMessageID));
				finish();
				overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
				return;
			}
			else{
				if(result != null){
					Toast.makeText(ChannelReportActivity.this, result,  Toast.LENGTH_SHORT).show();
					if (mMediaAdapter != null) {
						mMediaAdapter.resetSelectionInfeed();
						mMediaAdapter.notifyDataSetChanged();
					}
				}
				//	Toast.makeText(ChannelReportActivity.this, getString(R.string.error),  Toast.LENGTH_SHORT).show();
				//	deleteTextLocalMessage(result);
				else
					Toast.makeText(ChannelReportActivity.this, getString(R.string.error),  Toast.LENGTH_SHORT).show();
				deleteDialog.dismiss();
			}
		}
	}
	private String postDataOnServer_res(String url, String gpName, String op,int typeHit){/*
		String strData = null;
		String url = null;
		if(systemMessageAction){
			url = msgId;
		}
		else{
			if(type.equals(DELETE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/delete";
			}else if(type.equals(SHARE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/post/share";
			}else if(type.equals(REPORT_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/report";
			}else if(type.equals(SPAM_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/notspam";
			}
		}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			if(systemMessageAction){
			}
			else{
				if(type.equals(DELETE_REQUEST) || type.equals(REPORT_REQUEST) || type.equals(SPAM_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("groupName", new String(param.getBytes("UTF-8"))));
				}else if(type.equals(SHARE_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("sharedTo", new String(param.getBytes("UTF-8"))));
				}
			}
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
				if(systemMessageAction)
					return "success";
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
	 */

		//		url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings";
		//http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings?groupName=pkrpriv&op={TURNOFF or TURNON}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		if(typeHit == 1){
			nameValuePair.add(new BasicNameValuePair("groupName", gpName));
			nameValuePair.add(new BasicNameValuePair("op", op));//{TURNOFF or TURNON}
		}
		try {
			String appKeyValue = HttpHeaderUtils.createRTAppKeyHeader(
					BusinessProxy.sSelf.getUserId(),
					SettingData.sSelf.getPassword());
			httpPost.addHeader("RT-APP-KEY", appKeyValue);
			//httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));.
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "utf-8"));
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
				//	System.out.println("DATA = "+IOUtils.read(ins));
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
	private String postDataOnServer(String url, String gpName, String op){
		String strData = null;
		String type = op;
		String msgId = url;
		if(systemMessageAction){
			url = msgId;
		}
		else{
			if(type.equals(DELETE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/delete";
			}else if(type.equals(SHARE_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/post/share";
			}else if(type.equals(REPORT_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/report";
			}else if(type.equals(SPAM_REQUEST)){
				url = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/group/message/notspam";
			}
		}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);

		// Building post parameters
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		try {
			if(systemMessageAction){
			}
			else{
				if(type.equals(DELETE_REQUEST) || type.equals(REPORT_REQUEST) || type.equals(SPAM_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("groupName", new String(gpName.getBytes("UTF-8"))));
				}else if(type.equals(SHARE_REQUEST)){
					nameValuePair.add(new BasicNameValuePair("messageId", new String(msgId.trim().getBytes("UTF-8"))));
					nameValuePair.add(new BasicNameValuePair("sharedTo", new String(gpName.getBytes("UTF-8"))));
				}
			}
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
				if(systemMessageAction)
					return "success";
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
	 

		
		//return null;
	}
	public String compareDate(String date, Context context) {
		date = date.trim();
		date = date.replaceAll("\n", " ");
		try {
			StringBuilder time = new StringBuilder();
			SimpleDateFormat sdf = null;
			if (date.indexOf('/') != -1) {
				// "dd/MM/yyyy HH:mm:ss"
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			} else {
				date = DateFormatUtils.convertGMTDateToCurrentGMTDate(date,
						"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			java.util.Date past = sdf.parse(date);
			java.util.Date now = new java.util.Date();
			String s = sdf.format(now);
			now = sdf.parse(s);

			long agosecond = Math.abs(TimeUnit.MILLISECONDS.toSeconds(now
					.getTime() - past.getTime()));

			if (agosecond < 110)
				agosecond = 0;

			// System.out.println("-------compareDate-----now-----"+now
			// .getTime());
			// System.out.println("-------compareDate----server-----"+past
			// .getTime());

			int seconds = (int) (agosecond % 60);
			int minutes = (int) ((agosecond / 60) % 60);
			int hours = (int) ((agosecond / 3600) % 24);

			long agodays = ((agosecond / 86400));
			long month = ((agodays / 30));

			if (month > 0) {
				time.append(month);
				if (month == 1)
					time.append(" " + context.getString(R.string.month_ago));// month
				// ago
				else
					time.append(" " + context.getString(R.string.month_ago));// months
				// ago

				if (month > 12)
					return (new Date()).toLocaleString();// oGMTString();///Sep
				// 14, 2013 12:33:19
				// AM

				return time.toString();
			} else if (agodays > 0) {
				time.append(agodays);
				if (agodays == 1)
					time.append(" " + context.getString(R.string.days_ago));// day
				// ago
				else
					time.append(" " + context.getString(R.string.days_ago));// days
				// ago
				return time.toString();
			} else if (hours > 0) {
				if (hours == 1) {
					time.append(hours);
					time.append("h");
				} else {

					time.append(hours);
					time.append("h");
				}
				if (minutes > 0) {
					// time.append(" and ");
					time.append(" ");
					time.append(minutes);

					if (minutes == 1)
						time.append(context.getString(R.string.m_ago));// m ago
					else
						time.append(context.getString(R.string.m_ago));// m ago
				} else {
					time.append(" " + context.getString(R.string.ago));// ago
				}
				return time.toString();
			} else if (minutes > 0) {

				time.append(minutes);
				if (minutes == 1)
					time.append("m");
				else
					time.append("m");
				if (seconds > 0) {
					// time.append(" and ");
					time.append(" ");
					time.append(seconds);
					if (seconds == 1)
						time.append(context.getString(R.string.s_ago));
					else
						time.append(context.getString(R.string.s_ago));
				} else {
					time.append(" " + context.getString(R.string.ago));
				}
				return time.toString();
			} else {
				if (agosecond < 60) {
					time.append(context.getString(R.string.a_moment_ago));
				} else {
					time.append(agosecond);
					if (agosecond == 1)
						time.append(context.getString(R.string.s_ago));
					else
						time.append(context.getString(R.string.s_ago));
				}
				return time.toString();
			}
		} catch (Exception ex) {
			if (date != null)
				return date;
			ex.printStackTrace();

		}
		return date;
	}

	public void onClick(final View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ChannelReportActivity.this);

		builder.setTitle("Select choice");
		builder.setNegativeButton("Cancel", null);
		switch (v.getId()) {
		case R.community_chatlist_row.report_ignore:
			setResult(Activity.RESULT_OK, new Intent().putExtra("DELETE_MESSAGE_ID", deleteMessageMessageID));
			finish();
			overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
			break;
		case R.community_chatlist_row.report_delete:
			//			new AlertDialog.Builder(this).setMessage(getString(R.string.delete_confirmation))
			//			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			//
			//				public void onClick(DialogInterface dialog, int whichButton) {
			//					if (!ChannelReportActivity.this.checkInternetConnection()) {
			//						CommunityChatActivity.this.networkLossAlert();
			//						return;
			//					}
			//					if(deleteMessageAPI != null && deleteMessageAPI.length() > 0){
			//						DeleteRequest request = new DeleteRequest();
			//						request.execute(URLEncoder.encode(deleteMessageAPI), groupName, DELETE_REQUEST);
			//					}
			//				}
			//			}).setNegativeButton(R.string.no, null).show();
			if(deleteMessageAPI != null){
				if(deleteMessageAPI.indexOf(' ') != -1)
					deleteMessageAPI = deleteMessageAPI.replace(" ", "%20");
				deleteServerMessage(deleteMessageAPI, feed.entry.elementAt(0).groupName);
			}
			break;
			//		case R.id.menu:
			//			DataModel.sSelf.storeObject(DMKeys.ENTRY, entry);
			//			Intent intentC = new Intent(CommunityChatActivity.this, CommunityProfileActivity.class);
			//			intentC.putExtra("group_name",groupName);
			//			intentC.putExtra("group_desc", group_desc);
			//			intentC.putExtra("group_pic", group_pic);
			//			startActivity(intentC);
			//			break;
		case R.id.media_close:
			closePlayScreen();
			// RTMediaPlayer.reset();
			break;
		case R.id.media_play:
			ImageView imageView1 = null;
			try {
				imageView1 = (ImageView) findViewById(R.id.media_play);
				if (v.getTag().equals("PLAY")) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								RTMediaPlayer.start();
							} catch (Exception e) {
							}
						}
					}).start();
					imageView1.setImageResource(R.drawable.pause);
					imageView1.setTag("STOP");
				} else if (v.getTag().equals("STOP")) {
					imageView1.setImageResource(R.drawable.play);
					imageView1.setTag("PLAY");
					RTMediaPlayer.pause();
					imageView1 = (ImageView) findViewById(R.id.media_close);
					imageView1.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
			}
			break;

			//		case CONTENT_ID_VOICE:
			//
			//			// Name   :MANOJ SINGH
			//			// DATE   :06-06-2015
			//			// Reason : TAlking picture and Normal voice change
			//			// Status : Wrong Position is passing currently
			//			// //[[ Start----
			//			String url_audio = null;
			//			if(v.getTag() instanceof String){
			//				url_audio = (String) v.getTag();
			//				if(url_audio.endsWith(".amr"))
			//					url_audio = this.getCacheDir() + "/" +url_audio;
			//				else
			//					url_audio = getImageLocalPath(url_audio);
			//				if(url_audio!=null)
			//					playAvailableVoice(url_audio, false);
			//			}
			//			else
			//			{
			//				int val=0;
			//				CommunityFeed.Entry voiceEntry = (CommunityFeed.Entry)v.getTag();
			//	
			//				for (int j = 0; j < (voiceEntry.media.size()); j += 8) {
			//					String s = (String) voiceEntry.media.elementAt(j + 1);
			//					if (((String) voiceEntry.media.elementAt(j + 1)).equals("image"))
			//					{
			//						//continue;
			//						if(val==0)
			//							val = 1;
			//						break;
			//					}
			//					if (!s.equalsIgnoreCase("audio"))
			//						continue;
			//					url_audio = (String) voiceEntry.media.elementAt(j + 2);
			//				}
			//				if(val == 0){
			//					voicePlayCompleted();
			//					if(url_audio!=null)
			//						openPlayScreen(url_audio);
			//				}
			//				else{
			//					getSlideData(voiceEntry);
			//				}
			//			}
			//			break;
		case CONTENT_ID_PICTURE:
		case CONTENT_ID_PICTURE + 1:
		case CONTENT_ID_PICTURE + 2:
		case CONTENT_ID_PICTURE + 3:
		case CONTENT_ID_PICTURE + 4:
		case CONTENT_ID_PICTURE + 5:
		case CONTENT_ID_PICTURE + 6:
		case CONTENT_ID_PICTURE + 7:
		case CONTENT_ID_PICTURE + 8:
		case CONTENT_ID_PICTURE + 9:
		case CONTENT_ID_PICTURE + 10:
		case CONTENT_ID_PICTURE + 11:
		case CONTENT_ID_PICTURE + 12:
		case CONTENT_ID_PICTURE + 13:
		case CONTENT_ID_PICTURE + 14:
		case CONTENT_ID_PICTURE + 15:
		case CONTENT_ID_PICTURE + 16:
		case CONTENT_ID_PICTURE + 17:
		case CONTENT_ID_PICTURE + 18:
		case CONTENT_ID_PICTURE + 19:
		case CONTENT_ID_VOICE:
			try{
				//if(!isFileExists(local_path) && !local_path.endsWith(".amr")){
				//String url_audio = null;
				String local_str = "";
				if(v.getTag() instanceof String){
					local_str = (String) v.getTag();
				}else
				{
					CommunityFeed.Entry voiceEntry = (CommunityFeed.Entry)v.getTag();
					for (int j = 0; j < (voiceEntry.media.size()); j += 8) {
						String s = (String) voiceEntry.media.elementAt(j + 1);
						if (((String) voiceEntry.media.elementAt(j + 1)).equals("image"))
						{
							//continue;
							break;
						}
						if (!s.equalsIgnoreCase("audio"))
							continue;
						local_str = (String) voiceEntry.media.elementAt(j + 2);
					}
				}
				String url = local_str;
				//String url = (String) v.getTag();
				if(url != null && url.indexOf('~') != -1){
					String index = url.substring(0, url.indexOf("~"));
					url = url.substring(2, url.lastIndexOf("~"));
					CommunityFeed.Entry imgEntry = feed.entry.get(Integer.parseInt(index));
					ArrayList<String> mImagesPath = new ArrayList<String>();
					for (int j = 0; j < (imgEntry.media.size()); j += 8) {
						if (((String) imgEntry.media.elementAt(j + 3))
								.equalsIgnoreCase("local")) {
							if (!((String) imgEntry.media.elementAt(j + 1))
									.equalsIgnoreCase("image"))
								continue;
							mImagesPath.add((String) imgEntry.media.elementAt(j + 2));
						}
					}
					getSlideData(imgEntry);
				}
				else{
					if(url.endsWith(".amr"))
						url = this.getCacheDir() + "/" +url;
					else if(url.startsWith("http://") && url.lastIndexOf('/') != -1 
							&& url.lastIndexOf('.') > url.lastIndexOf('/'))
					{
						String fileName = url.substring(url.lastIndexOf('/')+1);
						String a  = getImageLocalPath(fileName);
						if(isFileExists(a)){
							url = a;
						}else
						{
							//@TODO
							// download audio code need here 
						}
					}
					else if(!url.startsWith("http://"))
						url = getImageLocalPath(url);
					if(url!=null)
						playAvailableVoice(url, false);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			break;
		case CONTENT_ID_VOICE_UPDATE:
			Toast.makeText(ChannelReportActivity.this, "CONTENT_ID_VOICE_UPDATE",  Toast.LENGTH_SHORT).show();
			break;
		case CONTENT_ID_VIDEO:
		case CONTENT_ID_VIDEO + 1:
		case CONTENT_ID_VIDEO + 2:
		case CONTENT_ID_VIDEO + 3:
		case CONTENT_ID_VIDEO + 4:
		case CONTENT_ID_VIDEO + 5:
		case CONTENT_ID_VIDEO + 6:
		case CONTENT_ID_VIDEO + 7:
		case CONTENT_ID_VIDEO + 8:
		case CONTENT_ID_VIDEO + 9:
		case CONTENT_ID_VIDEO + 10:
		case CONTENT_ID_VIDEO + 11:
		case CONTENT_ID_VIDEO + 12:
		case CONTENT_ID_VIDEO + 13:
		case CONTENT_ID_VIDEO + 14:
		case CONTENT_ID_VIDEO + 15:
		case CONTENT_ID_VIDEO + 16:
		case CONTENT_ID_VIDEO + 17:
		case CONTENT_ID_VIDEO + 18:
		case CONTENT_ID_VIDEO + 19:
			String url = (String) v.getTag();
		if (url.endsWith(".3gp") || url.endsWith(".mp4"))
			url = url.substring(url.lastIndexOf("~") + 1, url.length());
		else
			url = url.substring(0, url.indexOf("~"));
		if(url != null && url.startsWith("http://"))
			playFromURL(url);
		else{
			try {
				playFromURLMain(VDownloader.getInstance().getPlayUrl(url));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		break;
		case R.community_chat.sendButton:
			if(!isInternetOn())
				return;
			Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox), this);
			String textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).getText().toString();
			if (null == mVoicePath && null == mPicturePath && 0 == textMessage.trim().length()) 
			{
				showSimpleAlert("Error", getString(R.string.please_add_content_to_send_message));
				return;
			}
			smileyView.setVisibility(View.GONE);
			sendMessage(textMessage);
			//			new PostMessageInChannel().execute(textMessage);
			findViewById(R.community_chat.replyLayout).setVisibility(View.GONE);
			((TextView) findViewById(R.community_chat.replyName)).setText(null);
			replyToSender = false;
			break;
		case R.id.photo_gallary:
			Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox), this);
			openGallery(POSITION_PICTURE);
			Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("Channel Attach Pic Screen");
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.id.camera:
			Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox), this);
			openCamera();
			//			String arr[] = {
			//				     getString(R.string.choose_from_library),
			//				     getString(R.string.take_photo)
			//				   };
			//				   AlertDialog.Builder builder01 = new AlertDialog.Builder(this);
			//				   builder01.setTitle(R.string.select_choice);
			//				   builder01.setItems(arr, new DialogInterface.OnClickListener() {
			//				    @Override
			//				    public void onClick(DialogInterface dialog, int which) {
			//				     switch (which) {
			//				     case 0:
			//				      openGallery(POSITION_PICTURE);
			//				      break;
			//				     case 1:
			//				      openCamera();
			//				      break;
			//				     }
			//				    }   
			//				   });
			//				   builder01.show();
			t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("Channel Click From Camera Screen");
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.id.video_record:
			String arr[] = new String[]{getString(R.string.choose_from_library),getString(R.string.record_with_camera)};
			builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.select_choice);
			builder.setItems(arr, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						saveMessageContents();
						dialog.dismiss();
						openThumbnailsToAttach(POSITION_VIDEO);
						break;
					case 1:
						saveMessageContents();
						dialog.dismiss();						
						openCameraForVedioRecording();
						break;
					}
				}			
			});
			builder.show();
			if(attachment_panel != null)
				attachment_panel.setVisibility(View.INVISIBLE);
			break;
		case R.id.chatScreen_doodle:
			openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS);
			//Add Google Analytics
			t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.setScreenName("Channel Doodle Screen");
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.id.talking_pic:
			openThumbnailsToAttach(POSITION_TALKING_PIC);
			//Add Google Analytics
			t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("Channel Talking Pic Screen");
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.id.audio_file:
			openThumbnailsToAttach(POSITION_VOICE);
			t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("Channel Attach Audio Screen");
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.community_chat.recordVideo:
			String msg = getMaxSizeTextVideo();
			final Dialog dialog = new Dialog(ChannelReportActivity.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.open_choice_dialog);
			dialog.setCancelable(true);

			TextView text2 = (TextView) dialog
					.findViewById(R.open_choice.message);
			text2.setText(msg);
			Button button3 = (Button) dialog
					.findViewById(R.open_choice.button1);
			button3.setText((getString(R.string.choose_from_library)));
			button3.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					saveMessageContents();
					dialog.dismiss();
					openThumbnailsToAttach(POSITION_VIDEO);
					openThumbnailsToAttach(POSITION_VOICE);
					Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
					t.setScreenName("Channel Attach Video Screen");
					t.set("&uid",""+BusinessProxy.sSelf.getUserId());
					t.send(new HitBuilders.AppViewBuilder().build());
				}
			});
			Button button4 = (Button) dialog.findViewById(R.open_choice.button2);
			button4.setText(getString(R.string.record_with_camera));
			button4.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					saveMessageContents();
					dialog.dismiss();
					openCameraForVedioRecording();
					Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
					t.setScreenName("Channel Record Video Screen");
					t.set("&uid",""+BusinessProxy.sSelf.getUserId());
					t.send(new HitBuilders.AppViewBuilder().build());
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
		case R.community_chat.recordAudio:
			// RTMediaPlayer.reset();
			//			closePlayScreen();
			//			mVoiceMedia.startRecording(getString(R.string.done),
			//					getString(R.string.cancel), null,
			//					Constants.MAX_AUDIO_RECORD_TIME_REST);
			if(RTMediaPlayer.isPlaying()){
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
			}
			if(mRecordedVoicePath == null){
				showAudioDialog(RECORDING_MODE);
			}else{
				showAudioDialog(PLAY_MODE);
			}
			t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("Channel Record Audio Screen");
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.community_chat.smiley:
			Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox), this);
			if (isShowSmiley) {
				smileyView.setVisibility(View.GONE);
			} else {
				smileyView.setVisibility(View.VISIBLE);
			}
			isShowSmiley = !isShowSmiley;

			t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("Channel Attch Emoticon Screen");
			t.set("&uid",""+BusinessProxy.sSelf.getUserId());
			t.send(new HitBuilders.AppViewBuilder().build());
			break;
		case R.community_chatlist_row.my_menus:
			Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
					this);

			int position = (Integer) v.getTag();
			position = getActualPosition(position + 1);
			CommunityFeed.Entry entry = feed.entry.elementAt(position);

			HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
			HashMap<Integer, String> menuItemsOne = new HashMap<Integer, String>();
			if (feed != null && feed.isOwner == 1) {
				menuItemsOne.put(0, "Recommend community");
				menuItemsOne.put(1, "Leave community");
				if (feed != null && (feed.isOwner == 1 || feed.isAdmin == 1)) {
					menuItemsOne.put(2, "Delete message");
					menuItemsOne.put(3, "Cancel");
				} else
					menuItemsOne.put(2, "Cancel");
				openRowOptionRight(1, feed, true, menuItemsOne, entry);
			} else {
				menuItems.put(0, "Recommend community");
				menuItems.put(1, "Leave community");
				menuItems.put(2, "Report community");
				if (feed != null && (feed.isOwner == 1 || feed.isAdmin == 1)) {
					menuItems.put(3, "Delete message");
					menuItems.put(4, "Cancel");
				} else
					menuItems.put(3, "Cancel");
				openRowOptionRight(1, feed, false, menuItems, entry);
			}
			break;
		case R.community_chatlist_row.user_menus: {
			Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
					this);
			position = (Integer) v.getTag();
			position = getActualPosition(position + 1);
			entry = feed.entry.elementAt(position);
			StringBuffer buffer = new StringBuffer();
			buffer.append(entry.senderName).append("'s Profile");
			menuItems = new HashMap<Integer, String>();
			menuItemsOne = new HashMap<Integer, String>();
			// <rt:drmFlags>DONOTFORWARD,DONOTSAVECONTENTS</rt:drmFlags>
			Vector vector = new Vector<String>();
			/*
			 * vector.add("Reply"); vector.add("Send Personal Message");
			 */
			// vector.add("Reply");
			vector.add(entry.senderName + " 's Profile");

			String s[] = new String[] { "Forward", "" };// , "Forward",
			// "Report Message",
			// buffer.toString() }
			String drm[] = entry.drmFlags.split(",");
			boolean b = false;
			for (int i = 0; i < drm.length; i++) {
				if (drm[0].equalsIgnoreCase("DONOTFORWARD")) {
					b = true;
				}
			}
			if (!b) {
				vector.add("Forward");
				vector.add("Report message");

				menuItems.put(0, "Reply");
				menuItems.put(1, "Send personal message");
				menuItems.put(2, "Forward");

				menuItems.put(3, "Report message");

			}
			s = new String[vector.size()];
			for (int i = 0; i < vector.size(); i++) {
				s[i] = (String) vector.get(i);
			}
			vector.add(buffer.toString());
			if (b) {

				menuItemsOne.put(0, "Reply");
				menuItemsOne.put(1, "Send personal message");

				menuItemsOne.put(2, buffer.toString());
				if (feed != null && (feed.isOwner == 1 || feed.isAdmin == 1)) {
					menuItemsOne.put(3, getString(R.string.delete_message));
					menuItemsOne.put(4, getString(R.string.load_more));
				} else
					menuItemsOne.put(3, getString(R.string.load_more));
			} else {
				menuItems.put(4, buffer.toString());
				if (feed != null && (feed.isOwner == 1 || feed.isAdmin == 1)) {
					menuItems.put(5, getString(R.string.delete_message));
					menuItems.put(6, getString(R.string.cancel));
				} else
					menuItems.put(5, getString(R.string.cancel));
			}

			if (!b) {
				openRowOption(1, entry, false, menuItems);
			}
			if (b) {
				openRowOption(1, entry, true, menuItemsOne);
			}
		}
		break;
		/*
		 * case R.community_chatlist_row.audioButton: { int position = (Integer)
		 * v.getTag(); position = getActualPosition(position); final
		 * CommunityFeed.Entry entry = feed.entry.elementAt(position); String
		 * path = entry.audioUrl; if (path.startsWith("~n~p")) { path =
		 * path.substring(4, path.length()); mVoiceMedia.startPlaying(path,
		 * null); } else { DATA_CALLBACK = ACTUAL_AUDIO_DATA_CALLBACK; if
		 * (cache.containsKey(path)) { this.mResponseData = cache.get(path);
		 * mCallBackTimer.cancel(); mCallBackTimer = new Timer();
		 * mCallBackTimer.schedule(new OtsSchedularTask(this, DATA_CALLBACK,
		 * (byte) 0), 0);
		 * 
		 * } else { if (Logger.ENABLE)
		 * System.out.println("chating executeRequestData-3"); // lodingCanceled
		 * = true; executeRequestData(path, null); } } } break; case
		 * R.community_chatlist_row.imageButton: { int position = (Integer)
		 * v.getTag(); position = getActualPosition(position); final
		 * CommunityFeed.Entry entry = feed.entry.elementAt(position); String
		 * path = entry.imgUrl; if (path.startsWith("~n~p")) { path =
		 * path.substring(4, path.length()); String allpath[] = path.split("~");
		 * List<String> imagesPath = new ArrayList<String>(); for (int i = 0; i
		 * < allpath.length; i++) { imagesPath.add(allpath[i]); } Intent intent
		 * = new Intent(CommunityChatActivity.this, MultiPhotoViewer.class);
		 * intent.putStringArrayListExtra("DATA", (ArrayList<String>)
		 * imagesPath); startActivity(intent); } else { DATA_CALLBACK =
		 * ACTUAL_IMAGE_DATA_CALLBACK; mPostURL = path; if
		 * (cache.containsKey(path)) { this.mResponseData = cache.get(path);
		 * mCallBackTimer.cancel(); mCallBackTimer = new Timer();
		 * mCallBackTimer.schedule(new OtsSchedularTask(this, DATA_CALLBACK,
		 * (byte) 0), 0);
		 * 
		 * } else { if (Logger.ENABLE)
		 * System.out.println("chating executeRequestData-4"); // lodingCanceled
		 * = true; executeRequestData(path, null); } } } break; case
		 * R.community_chatlist_row.videoButton: {
		 * Utilities.closeSoftKeyBoard(v, this); int position = (Integer)
		 * v.getTag(); position = getActualPosition(position); final
		 * CommunityFeed.Entry entry = feed.entry.elementAt(position); String
		 * path = entry.vidUrl; if (path.startsWith("~n~p")) { path =
		 * path.substring(4, path.length()); playVideo(path); } else { Intent
		 * intent = new Intent(CommunityChatActivity.this, VideoPlayer.class);
		 * intent.putExtra("BACK", "INBOX");
		 * DataModel.sSelf.storeObject(DMKeys.VIDEO_DATA,
		 * getPayloadFrom(entry.vidUrl)); startActivity(intent); } } break;
		 */case R.community_chat.cancelButton:
			 findViewById(R.community_chat.replyLayout).setVisibility(View.GONE);
			 ((TextView) findViewById(R.community_chat.replyName)).setText(null);
			 replyToSender = false;
			 break;
		 case LOADING_ID:
			 loading = v;
			 // ProgressBar progressBar = ((ProgressBar)
			 // v.findViewById(R.id.progressbar));
			 // if (progressBar != null)
			 {
				 // TextView textView = ((TextView)
				 // v.findViewById(R.id.loadingtext));
				 // progressBar.setVisibility(View.VISIBLE);
				 if (loadingView.getText().toString()
						 .equalsIgnoreCase(getString(R.string.load_more))) {
					 requestednextPage = true;
					 loadingView.setText(getString(R.string.loading_dot));
				 }

			 }
			 if (requestednextPage) {
				 String aurl = feed.nexturl;
				 if(feed!=null && feed.entry.size()>0){
					 int len = (int)(feed.entry.size()/20);
					 if(len<1){
						 len=1;
					 }
					 else{
						 len = len+1;
					 }
					 int i = aurl.lastIndexOf("=");
					 aurl  = aurl.substring(0, i);
					 aurl = aurl+"="+len;
				 }

				 //
				 //
				 /* String aurl = feed.socketedMessageUrl;
					aurl = aurl.replace("$maxCount$", "20");
					aurl = aurl.replace("$order$", "ASC");
					aurl = aurl.replace("$messageId$", getMessageId());*/
				 //
				 //
				 if(aurl!=null){
				 ReportedRequest request = new ReportedRequest();
				 request.execute(aurl);
				 }else
				 {
					 if (loadingView.getText().toString()
							 .equalsIgnoreCase(getString(R.string.loading_dot))) {
						 requestednextPage = true;
						 loadingView.setText(getString(R.string.load_more));
					 }
				 }
				/* oldFeed = feed;
				 DATA_CALLBACK = XML_DATA_CALLBACK;

				 //				 if (Logger.ENABLE)
				 //					 System.out.println("chating executeRequestData-5");
				 // lodingCanceled = true;
				 executeRequestData(aurl, mCurrentScreen);*/
			 }
			 break;
		}
	}

	public String GenerateLoadMoreURL(String groupid,int msgId)
	{
		String socketedMessageUrl="http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/message/"+groupid+"?messageId=$messageId$&order=$order$&maxCount=$maxCount$";
		return socketedMessageUrl;

	}

	//By Ravi
	private void openGallery(byte which) {
		Intent intent = new Intent();
		if(attachment_panel != null)
			attachment_panel.setVisibility(View.INVISIBLE);
		switch (which) {
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
			intent = new Intent(this, KainatMultiImageSelection.class);
			intent.putExtra("MAX", 5);
			intent.putExtra("AUDIO_VISIBLITY", false);
			startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);


			//			MediaChooser.setSelectionLimit(5);
			//			MediaChooser.setSelectedMediaCount(0);
			//			Intent intenta = new Intent(CommunityChatActivity.this, BucketHomeFragmentActivity.class);
			//			startActivity(intenta);
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

		quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				onOptionsItemSelected(actionId);
			}
		});

		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				((ImageView)findViewById(R.id.menu)).setImageResource(R.drawable.x_options);
			}
		});
	}

	private void getSlideData(CommunityFeed.Entry  entry){
		String url = null;
		ArrayList<String> imgUrlList = new ArrayList<String>();
		boolean audio = false;
		try{
			if(entry !=null){
				for (int j = 0; j < (entry.media.size()); j += 8) {

					if (audio) {
						j = j - 4;
						audio = false;
					}
					if (((String) entry.media.elementAt(j + 1)).equalsIgnoreCase("audio")){
						audio = true;
						url = ((String) entry.media.elementAt(j + 2));
						continue;
					}
					String s = (String) entry.media.elementAt(j + 1);
					if (!s.equalsIgnoreCase("image"))
						continue;
					imgUrlList.add((String) entry.media.elementAt(j + 2));
				}
				if(imgUrlList != null && imgUrlList.size() > 0){
					stockArr = new String[imgUrlList.size()];
					stockArr = imgUrlList.toArray(stockArr);
					//					Intent intent = new Intent(CommunityChatActivity.this, SlideShowScreen.class);
					//					intent.putExtra("AUDIO_URL", url);
					//					intent.putExtra("IMAGE_ARR", stockArr);
					//					//slideBitmapArray = result;
					//					startActivityForResult(intent, Constants.ResultCode);

					//Load Audio
					if(url != null && url.startsWith("http://"))
						new LoadDataFromURL(url).execute(imgUrlList);
					else{
						Intent intent = new Intent(ChannelReportActivity.this, SlideShowScreen.class);
						if(url != null && !url.startsWith("http://"))//Means local recorded
							intent.putExtra("AUDIO_URL", url);
						intent.putExtra("IMAGE_ARR", stockArr);
						startActivityForResult(intent, Constants.ResultCode);
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	String[] stockArr = null;
	public static Bitmap[] slideBitmapArray = null;
	private class LoadDataFromURL extends AsyncTask<ArrayList<String>, Void, String>{

		private String audioUrl = null;
		public LoadDataFromURL(String aUrl){
			this.audioUrl = aUrl;
		}
		ProgressDialog rTDialog;
		@Override
		protected void onPostExecute(String result) {
			if (rTDialog != null && rTDialog.isShowing()) {
				rTDialog.dismiss();
			}

			Intent intent = new Intent(ChannelReportActivity.this, SlideShowScreen.class);
			intent.putExtra("AUDIO_URL", result);
			intent.putExtra("IMAGE_ARR", stockArr);
			//slideBitmapArray = result;
			startActivityForResult(intent, Constants.ResultCode);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			//			rTDialog = new RTDialog(ChannelReportActivity.this, null, getString(R.string.feateching_profile));
			rTDialog = ProgressDialog.show(ChannelReportActivity.this, "", getString(R.string.loading_dot), true);
			rTDialog.show();
		}
		@Override
		protected String doInBackground(ArrayList<String>... params) {
			Bitmap[] bitmapArray = new Bitmap[params[0].size()];
			String path = null;
			String fullPath = null;
			String fileName = null;
			int count;
			String extension;
			// TODO Auto-generated method stub

			try{
				extension = MimeTypeMap.getFileExtensionFromUrl(audioUrl);
				if(extension == null || extension.trim().length() <= 0)
				{
					try{
						extension = audioUrl.substring(audioUrl.lastIndexOf(".") + 1);
					}catch (Exception e) {
						extension = "mp3";
					}
				}
				//Extract File name from the URL
				if(audioUrl.lastIndexOf('/') != -1 && audioUrl.lastIndexOf('.') != -1)
				{
					if(audioUrl.lastIndexOf('.') > audioUrl.lastIndexOf('/'))
						fileName = audioUrl.substring(audioUrl.lastIndexOf("/") + 1, audioUrl.lastIndexOf("."));
					else
						fileName = audioUrl.substring(audioUrl.lastIndexOf("=") + 1);
					File folder = new File(Environment.getExternalStorageDirectory() + "/YelaTalk");
					if (!folder.exists()) {
						folder.mkdir();
					}
					fullPath = new StringBuffer(Environment.getExternalStorageDirectory().getPath()).append('/').append("YelaTalk").append('/').
							append(fileName).append('.').append(extension).toString();
					//Check if filename exists
					if(isFileExists(audioUrl.substring(audioUrl.lastIndexOf("/") + 1))){
						//Return the same path
						return fullPath;
					}
				}

				URL url = new URL(audioUrl);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				if(audioUrl.lastIndexOf(".mp3") != -1){
					connection.setRequestProperty("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
					connection.setRequestProperty("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
				}
				connection.connect();

				// getting file length
				int lenghtOfFile = connection.getContentLength();

				// input stream to read file - with 8k buffer
				InputStream input = new BufferedInputStream(url.openStream(), 8192);

				// Output stream to write file
				OutputStream output = new FileOutputStream(fullPath);

				byte data[] = new byte[4096];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					//	                publishProgress((int)((total*100)/lenghtOfFile));

					// writing data to file
					output.write(data, 0, count);
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();
			}catch(Exception ex){

			}

			return fullPath;
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

	}
	//	private class LoadHttpImage extends AsyncTask<ArrayList<String>, Void, Bitmap[]>{
	//		
	//		private String audioUrl = null;
	//		public LoadHttpImage(String aUrl){
	//			this.audioUrl = aUrl;
	//		}
	//		ProgressDialog rTDialog;
	//		@Override
	//		protected void onPostExecute(Bitmap[] result) {
	//			if(result != null)
	//				if (rTDialog != null && rTDialog.isShowing()) {
	//					rTDialog.dismiss();
	//					if (!((Activity) BusinessProxy.sSelf.addPush.mContext).isFinishing()) 
	//						rTDialog.dismiss();
	//					rTDialog = null;
	//				}
	//			Intent intent = new Intent(ChannelReportActivity.this, SlideShowScreen.class);
	//			intent.putExtra("AUDIO_URL", audioUrl);
	//			slideBitmapArray = result;
	//			startActivityForResult(intent, Constants.ResultCode);
	//		}
	//		
	//		@Override
	//		protected void onPreExecute() {
	//			// TODO Auto-generated method stub
	////			rTDialog = new RTDialog(CommunityChatActivity.this, null, getString(R.string.feateching_profile));
	//			rTDialog = ProgressDialog.show(CommunityChatActivity.this, "", getString(R.string.loading_dot), true);
	//			rTDialog.show();
	//		}
	//		@Override
	//		protected Bitmap[] doInBackground(ArrayList<String>... params) {
	//			Bitmap[] bitmapArray = new Bitmap[params[0].size()];
	//			String path = null;
	//			// TODO Auto-generated method stub
	//			for(byte i = 0; i < params[0].size(); i++){
	////				bitmapArray[i] = LoadImage(params[0].get(i), 0, 0);
	//				//all the images has been loaded in the local Gallary
	//				path = params[0].get(i);
	//				if(path.startsWith("http://")){
	//					if(path.lastIndexOf('/') != -1 && path.lastIndexOf('?') != -1)
	//						path = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('?'));
	//					else
	//						path = path.substring(path.lastIndexOf('/') + 1);
	//					if(isFileExists(path)){
	//						path = getImageLocalPath(path);
	//						bitmapArray[i] = BitmapFactory.decodeFile(path);
	//					}
	//					else
	//						bitmapArray[i] = LoadImage(params[0].get(i), 0, 0);
	//				}else{
	//					bitmapArray[i] = BitmapFactory.decodeFile(path);
	//				}
	//			}
	//			return bitmapArray;
	//		}
	//		
	//	}
	private Bitmap LoadImage(String strurl, int width, int height)
	{       


		Bitmap bitmap = null;
		BufferedInputStream in = null;       
		try {
			URL url = new URL(strurl);

			if(width != 0 && height != 0){
				bitmap = getImageBitmap(url);//, null, bmOptions);
				bitmap = getResizedBitmap(bitmap, width, height);
			}else{
				bitmap = getImageBitmap(url);
			}
		} catch (IOException e1) {
			Log.d("+++++++++++++++", "Erro in img loading");
		}
		finally{
			if(in != null)
				try { 
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}

		return bitmap;               	
	}
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);
		// RECREATE THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}
	private Bitmap getImageBitmap(URL url) {		
		Bitmap bitmap = null;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
		} catch (IOException e1) {e1.printStackTrace();}
		try {
			connection.connect();
		} catch (IOException e1) {e1.printStackTrace();}
		InputStream is = null;
		try {
			is = connection.getInputStream();
		} catch (IOException e1) {e1.printStackTrace();}
		BufferedInputStream bis = new BufferedInputStream(is);

		try{
			bitmap = BitmapFactory.decodeStream(bis);
		}catch (OutOfMemoryError e) {bitmap = null;}
		bis = null;
		is = null;
		Runtime.getRuntime().gc();
		System.gc();
		return bitmap;
	}
	public boolean onOptionsItemSelected(int item) {
		try {
			switch (item) {
			case 1:
				stopTimerToRefresh();
				if(!feedLoadedFirstTime)
				{
					cancelLoading();
					resetLoading();
					switchedToScreenWhileLoading = true;
					Log.i(TAG, "onOptionsItemSelected :: CommunityProfileNewActivity called.");
				}
				if(entry != null){
					DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
					Intent intentC = new Intent(ChannelReportActivity.this, CommunityProfileNewActivity.class);
					intentC.putExtra("group_name", entry.groupName);
					intentC.putExtra("tags_name", entry.tags);
					intentC.putExtra("group_id", entry.groupId);
					if(entry.description != null)
						intentC.putExtra("group_desc", entry.description);
					else
						intentC.putExtra("group_desc", " ");
					if(entry.thumbUrl != null)
						intentC.putExtra("group_pic", entry.thumbUrl);
					else
						intentC.putExtra("group_pic", " ");
					Log.i(TAG, "onOptionsItemSelected :: ChannelName : "+entry.groupName+", ChannelID : "+entry.groupId);
					startActivity(intentC);
					//this.finish();
				}
				return true;
			case 3:
				stopTimerToRefresh();
				if(!feedLoadedFirstTime)
				{
					cancelLoading();
					resetLoading();
					switchedToScreenWhileLoading = true;
					Log.i(TAG, "onOptionsItemSelected :: CommunityMemberActivity called.");
				}
				Intent intent = new Intent(ChannelReportActivity.this, CommunityMemberActivity.class);
				String val = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/searchmember?groupid="+groupID+"&user=";
				if(feed.searchMemberUrl!=null && !feed.searchMemberUrl.equals("")){
					intent.putExtra("member_url", feed.searchMemberUrl);
				}else
				{
					intent.putExtra("member_url", val);
				}
				intent.putExtra("OwnerUserName", entry.ownerUsername);
				intent.putExtra("title",
						((TextView) findViewById(R.community_chat.title))
						.getText());
				DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
				startActivity(intent);
				return true;
			case 4:
				intent = new Intent(ChannelReportActivity.this, KainatContactActivity.class);// BuddyActivity
				intent.putExtra("group", true);
				intent.putExtra("from_community", true);
				if(entry != null)
					intent.putExtra("community_name", entry.groupName);
				//				intent.putExtra(BuddyActivity.SCREEN_MODE, BuddyActivity.MODE_ONLY_FRIENDS);
				startActivity(intent);
				return true;
			case 5:
				DialogInterface.OnClickListener handler = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(entry != null){
							StringBuilder text = new StringBuilder(
									"DeleteCommunity::Name##");
							text.append(entry.groupName);
							if (BusinessProxy.sSelf.sendNewTextMessage(
									"Community manager<a:communitymgr>",
									text.toString(), true)) {
								ChannelReportActivity.this.finish();
								DataModel.sSelf.storeObject(
										DMKeys.COMMUNITY_DELETED, Boolean.TRUE);
							}
						}
					}
				};
				showAlertMessage(
						getString(R.string.app_name),
						getString(R.string.do_you_really_want_to_delete_this_community),
						new int[] { DialogInterface.BUTTON_POSITIVE,
							DialogInterface.BUTTON_NEUTRAL }, new String[] {
							"Yes", "No" },
							new DialogInterface.OnClickListener[] { handler, null });

				return true;
			case 6:

				handler = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(entry != null){
							StringBuilder text = new StringBuilder("Leave::Name##");
							text.append(entry.groupName);
							if (BusinessProxy.sSelf.sendNewTextMessage(
									"Community manager<a:communitymgr>",
									text.toString(), true)) {
								ChannelReportActivity.this.finish();
								DataModel.sSelf.storeObject(
										DMKeys.COMMUNITY_DELETED, Boolean.TRUE);
							}
						}
					}
				};
				showAlertMessage(
						getString(R.string.app_name),
						getString(R.string.do_you_really_want_to_leave_this_community),
						new int[] { DialogInterface.BUTTON_POSITIVE,
							DialogInterface.BUTTON_NEUTRAL }, new String[] {
							getString(R.string.yes),
							getString(R.string.no), },
							new DialogInterface.OnClickListener[] { handler, null });

				return true;
			case 7: {

				DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(entry != null){
							StringBuilder text = new StringBuilder("Report::grpN##");
							text.append(entry.groupName);
							text.append("::gId##");
							text.append(feed.groupId);
							if (BusinessProxy.sSelf.sendNewTextMessage(
									"Community Manager<a:communitymgr>",
									text.toString(), true)) {

								new Thread(new Runnable() {
									public void run() {
										showSimpleAlert(
												getString(R.string.info),
												getString(R.string.yourrequesthasbeensent));
									}
								}).start();//
							}
						}
					}
				};
				showAlertMessage(
						getString(R.string.confirm),
						getString(R.string.do_you_really_want_to_report_this_community),
						new int[] { DialogInterface.BUTTON_POSITIVE,
							DialogInterface.BUTTON_NEGATIVE },
							new String[] { getString(R.string.yes),
							getString(R.string.no) },
							new DialogInterface.OnClickListener[] { exitHandler,
							null });
			}
			return true;
			default:

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void playVideo(final String path) {
		runOnUiThread(new Runnable() {
			public void run() {
				Intent intent = new Intent(ChannelReportActivity.this,
						VideoPlayer.class);
				intent.putExtra("MODE", "file");
				intent.putExtra("BACK", "INBOX");
				intent.putExtra("VIDEO_PATH", path);
				startActivity(intent);
			}
		});
	}

	// private EditText textbox;
	private static long totalSize = 0;

	private String getMaxSizeText() {
		/*
		 * double sizeInMB = totalSize / 1024.0 / 1024.0; String text =
		 * Double.toString(sizeInMB); int index = text.indexOf('.'); if (-1 !=
		 * index && index + 5 < text.length()) { text = text.substring(0, index
		 * + 5); } return String .format(
		 * "Max count: 20 within max size: 4 MB. Current attached size: %s MB",
		 * text);
		 */

		calculateTotSizeAfterClear();
		double sizeInMB = totalSize / 1024.0 / 1024.0;
		String text = Double.toString(sizeInMB);
		int index = text.indexOf('.');
		if (-1 != index && index + 5 < text.length()) {
			text = text.substring(0, index + 5);
		}
		// return String.format("Max size: " + BusinessProxy.sSelf.MaxSizeData
		// + " MB. Current attached size: %s MB", text);
		return ("Max count: 10.");// Current attached size: " +
		// onMbKbReturnResult(totalSize));

		// return
		// ("Max count: 5 within max size: "+BusinessProxy.sSelf.MaxSizeData+" MB. Current attached size: "+onMbKbReturnResult(totalSize));
	}

	private String getMaxSizeTextVideo() {
		// double sizeInMB = totalSize / 1024.0 / 1024.0;
		// String text = Double.toString(sizeInMB);
		// int index = text.indexOf('.');
		// if (-1 != index && index + 5 < text.length()) {
		// text = text.substring(0, index + 5);
		// }
		// return
		// String.format("Max size: "+BusinessProxy.sSelf.MaxSizeData+" MB. Current attached size: %s MB",
		// text);

		calculateTotSizeAfterClear();
		double sizeInMB = totalSize / 1024.0 / 1024.0;
		String text = Double.toString(sizeInMB);
		int index = text.indexOf('.');
		if (-1 != index && index + 5 < text.length()) {
			text = text.substring(0, index + 5);
		}
		return String.format("Max size: " + BusinessProxy.sSelf.MaxSizeData
				+ " MB. Current attached size: %s MB", text);
	}

	private void saveMessageContents() {
		String textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox))
				.getText().toString();
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_TEXT_MESSAGE,
				textMessage.toString());
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_VOICE_MESSAGE, mVoicePath);
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_PICTURE_MESSAGE,
				mPicturePath);
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_VIDEO_MESSAGE, mVideoPath);
	}

	public void openCameraForVedioRecording() {
		File videofile = null;
		try {
			videofile = File.createTempFile("temp", ".mp4", getCacheDir());
			// String videofileName = videofile.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File file = new File(Environment.getExternalStorageDirectory(), "rt" + getRandomNumber() + ".mp4");

		if (BusinessProxy.sSelf.buildInfo.MODEL.equalsIgnoreCase(Constants.VIDEO_REC_PROB_ON_DEVICE2))
			videofile = file;

		cameraImagePath = file.getPath();
		Uri outputFileUri = Uri.fromFile(file);

		Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
		// intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,outputFileUri);//
		// Uri.fromFile(videofile));
		if (BusinessProxy.sSelf.buildInfo.MODEL.equalsIgnoreCase(Constants.VIDEO_REC_PROB_ON_DEVICE))
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);// Uri.fromFile(videofile));
		else
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(videofile));
		// recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// intent.setType("image/*");
		intent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, Constants.EXTRA_VIDEO_QUALITY);
		intent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, 0);
		//		intent.putExtra("android.intent.extra.durationLimit", Constants.VIDEO_RECORDING_DUERATION);
		//		intent.putExtra("android.intent.extra.sizeLimit", Constants.VIDEO_RECORDING_SIZE_LIMITE);

		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Constants.VIDEO_RECORDING_DUERATION);
		intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, Constants.VIDEO_RECORDING_SIZE_LIMITE);
		startActivityForResult(intent, POSITION_VIDEO);
		startRecoridng = true;

		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// while(startRecoridng){
		// StatFs stat = new
		// StatFs(Environment.getExternalStorageDirectory().getPath());
		// long bytesAvailable = (long)stat.getBlockSize() *
		// (long)stat.getBlockCount();
		// long megAvailable = bytesAvailable / 1048576;
		// System.out.println("--------megAvailable-------"+megAvailable);
		// try{
		// Thread.sleep(1000);
		// }catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		//
		// }
		// }).start();
	}

	boolean startRecoridng = false;

	public void multiView() {
		List<String> imagesPath = new ArrayList<String>();
		String path;
		int i = 0;
		if (payloadData == null || payloadData.mPicture == null) {
			return;
		}
		for (i = 0; i < payloadData.mPicture.length; i++) {
			try {
				FileOutputStream fout = new FileOutputStream(
						path = String.format("%s%sFile%d.jpg", getCacheDir(),
								File.separator, i));
				fout.write(payloadData.mPicture[i]);
				fout.flush();
				fout.close();
				imagesPath.add(path);
			} catch (Exception ex) {
			}
		}
		Drawable d = Drawable.createFromPath(imagesPath.get(0));
		cacheImgIcon.put(mPostURL, d);
		Intent intent = new Intent(ChannelReportActivity.this,
				MultiPhotoViewer.class);
		intent.putStringArrayListExtra("DATA", (ArrayList<String>) imagesPath);
		intent.putExtra("AudioExist", ""+entry.media.elementAt(2));
		startActivity(intent);
	}

	private int parseForAttachment(byte mResponseData[])
			throws OutOfMemoryError, Throwable {
		int aOffset = 0;
		try {
			// GET PAYLOAD COUNT
			int payloadCount = mResponseData[aOffset++];
			for (int i = 0; i < payloadCount; i++) {
				// GET PAYLOAD TYPE
				byte paloadType = mResponseData[aOffset++];
				// GET ATTACHMENT COUNT
				int tempInt = mResponseData[aOffset++];
				switch (paloadType) {
				case Payload.PAYLOAD_TYPE_VOICE:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VOICE;
					payloadData.mVoice = new byte[tempInt][];
					payloadData.mVoiceType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mVoice, payloadData.mVoiceType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_TEXT:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
					payloadData.mText = new byte[tempInt][];
					payloadData.mTextType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mText, payloadData.mTextType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_PICTURE:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
					payloadData.mPicture = new byte[tempInt][];
					payloadData.mPictureType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mPicture, payloadData.mPictureType,
							tempInt);
					break;
				case Payload.PAYLOAD_TYPE_RTML:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_RTML;
					payloadData.mRTML = new byte[tempInt][];
					payloadData.mRTMLType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mRTML, payloadData.mRTMLType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_VIDEO:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
					payloadData.mVideo = new byte[tempInt][];
					payloadData.mVideoType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mVideo, payloadData.mVideoType, tempInt);
					break;
				}
			}
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseForAttachment():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseForAttachment():[ERROR]: " + ex, ex);
			throw ex;
		}
		return aOffset;
	}

	private int parseForAttachment() throws OutOfMemoryError, Throwable {
		int aOffset = 0;
		try {
			// GET PAYLOAD COUNT
			int payloadCount = mResponseData[aOffset++];
			for (int i = 0; i < payloadCount; i++) {
				// GET PAYLOAD TYPE
				byte paloadType = mResponseData[aOffset++];
				// GET ATTACHMENT COUNT
				int tempInt = mResponseData[aOffset++];
				switch (paloadType) {
				case Payload.PAYLOAD_TYPE_VOICE:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VOICE;
					payloadData.mVoice = new byte[tempInt][];
					payloadData.mVoiceType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mVoice, payloadData.mVoiceType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_TEXT:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
					payloadData.mText = new byte[tempInt][];
					payloadData.mTextType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mText, payloadData.mTextType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_PICTURE:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
					payloadData.mPicture = new byte[tempInt][];
					payloadData.mPictureType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mPicture, payloadData.mPictureType,
							tempInt);
					break;
				case Payload.PAYLOAD_TYPE_RTML:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_RTML;
					payloadData.mRTML = new byte[tempInt][];
					payloadData.mRTMLType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mRTML, payloadData.mRTMLType, tempInt);
					break;
				case Payload.PAYLOAD_TYPE_VIDEO:
					payloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
					payloadData.mVideo = new byte[tempInt][];
					payloadData.mVideoType = new byte[tempInt];
					aOffset = parseAttachment(mResponseData, aOffset,
							payloadData.mVideo, payloadData.mVideoType, tempInt);
					break;
				}
			}
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseForAttachment():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseForAttachment():[ERROR]: " + ex, ex);
			throw ex;
		}
		return aOffset;
	}

	private int parseAttachment(byte[] mResponseData, int aOffset,
			byte[][] aAttachment, byte[] aAttachmentType, int aCount)
					throws OutOfMemoryError, Throwable {
		int len = 0;
		try {
			for (int j = 0; j < aCount; j++) {
				aAttachmentType[j] = mResponseData[aOffset++];
				len = Utilities.bytesToInt(mResponseData, aOffset, 4);
				aOffset += 4;
				// Attachment
				aAttachment[j] = new byte[len];
				System.arraycopy(mResponseData, aOffset, aAttachment[j], 0, len);
				aOffset += len;
			}
		} catch (OutOfMemoryError me) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseAttachment():[ERROR]:" + me, me);
			throw me;
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--parseAttachment():[ERROR]:" + ex, ex);
		}
		return aOffset;
	}

	private int getActualPosition(int position) {
		return (feed.entry.size() - position);
	}

	private byte[] getPayloadFrom(final String url) {
		HttpConnectionHelper helper = new HttpConnectionHelper(url);
		helper.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());
		helper.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
		try {
			int responsecode = helper.getResponseCode();
			switch (responsecode) {
			case HttpURLConnection.HTTP_OK:
			case HttpURLConnection.HTTP_ACCEPTED:
				InputStream is = helper.getInputStream();
				ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
				byte[] data = new byte[CHUNK_LENGTH];
				int len;
				while (-1 != (len = is.read(data))) {
					buffer.append(data, 0, len);
				}
				return buffer.toByteArray();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void onBackPressed() {
		if(RTMediaPlayer.isPlaying()){
			RTMediaPlayer.reset();
			RTMediaPlayer.clear();
		}
		if(rTDialog!= null && rTDialog.isShowing()){
			rTDialog.dismiss();
			return;
		}
		if(isVideoDownloading){
			videoDownloadCancelled = true;
			Log.i(TAG, "ViewHolder :: onBackPressed : onBackPressed = true");
		}
		if(systemMessageAction){
			DataModel.sSelf.removeObject(DMKeys.ENTRY);
			finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
		}
		else{
			//Stop the refresh thread
			if(refreshStarted && refresh != null){
				try{
					stopTimerToRefresh();
					refresh.cancel();
					refresh = null;
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			if(((RelativeLayout)findViewById(R.id.chat_option_layout)).isShown()){
				((LinearLayout)findViewById(R.community_chat.community_chat_header)).setVisibility(View.VISIBLE);
				((RelativeLayout)findViewById(R.id.chat_option_layout)).setVisibility(View.GONE);
				if(mMediaAdapter != null){
					mMediaAdapter.resetSelectionInfeed();
					mMediaAdapter.notifyDataSetChanged();
				}
				return;
			}

			DataModel.sSelf.removeObject(DMKeys.ENTRY);
			if(UIActivityManager.PushNotification == 2)
			{
				DataModel.sSelf.removeObject(DMKeys.ENTRY + "COMM");
				Intent intent = new Intent(ChannelReportActivity.this, KainatHomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
			}
			if(attachment_panel.getVisibility()==View.VISIBLE){
				attachment_panel.setVisibility(View.INVISIBLE);
				((ImageView)findViewById(R.id.attachement)).setImageResource(R.drawable.attachment_unsel) ;
				return;
			}

			if (onBackPressedCheck())
				return;
			closePlayScreen();


			if (currentLoadMedia != null) {
				// currentLoadMedia.cancel(true);
				currentLoadMedia.ursl.clear();
			}
			if (CustomMenu.hideRet())
				return;
			UIActivityManager.directFromCreateChannel = false;
			//write here code to reset new message count in DB
			//		if(entry != null && entry.newMessageCount > 0)
			resetNewMessageCount();
			finish();
			overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
			startChannelRefresh();

		}
		if(!systemMessageAction){
			DbDeletionAsyn DDA= new DbDeletionAsyn();
			DDA.execute("") ;
		}

	}
	private class DbDeletionAsyn extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			maintainDbLoad();
			return null ;
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
		}
	}



	private void maintainDbLoad()
	{
		try{
			int Db_count = 0;
			Db_count = CCB.getCommunityChatCount(groupID);
			if(Db_count > (20+Constants.DB_MSG_COUNT)){
				CCB.getCommunityChatMessageId(groupID,Constants.DB_MSG_COUNT);
			}
		}catch(Exception e){

		}

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

	private String getImagesFilePath(byte mPicture[], byte type, int index) {
		try {
			switch (type) {
			case POSITION_PICTURE:
				String path;
				FileOutputStream fout = new FileOutputStream(
						path = String.format("%s%sFile%d.png", getCacheDir(),
								File.separator, index));
				fout.write(mPicture);
				fout.flush();
				fout.close();
				return path;
			}
		} catch (Exception e) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "Error creating temp file for pciture show",
						e);
			}
		}
		return null;
	}

	// public boolean onCreateOptionsMenu(Menu menu) {
	// if (feed == null) {
	// return false;
	// }
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.community_chat_menu, menu);
	// if (feed != null && feed.isOwner == 1) {
	// menu.findItem(R.id.leave_community).setVisible(false);
	// menu.findItem(R.id.report_community).setVisible(false);
	// menu.findItem(R.id.edit_community).setVisible(true);
	// menu.findItem(R.id.delete_community).setVisible(true);
	// } else {
	// menu.findItem(R.id.leave_community).setVisible(true);
	// menu.findItem(R.id.report_community).setVisible(true);
	// menu.findItem(R.id.edit_community).setVisible(false);
	// menu.findItem(R.id.delete_community).setVisible(false);
	// }
	// return true;
	// }

	public boolean onOptionsItemSelected(MenuItem item) {
		// AlertDialog.Builder builder = new
		// AlertDialog.Builder(CommunityChatActivity.this);
		// builder.setTitle("RockeTalk");
		// builder.setMessage("Do you really want to leave this Community?");
		switch (item.getItemId()) {
		case R.id.view_description:
			//			if(entry != null)
			//				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, entry.groupName);
			//			DataModel.sSelf.storeObject(DMKeys.XHTML_URL, feed.profileUrl);
			//
			//			Intent intent = new Intent(CommunityChatActivity.this,
			//					WebviewActivity.class);
			//			intent.putExtra("PAGE", (byte) 1);
			//			startActivity(intent);
			return true;
		case R.id.view_members:
			Intent intent = new Intent(ChannelReportActivity.this,
					CommunityMemberActivity.class);
			intent.putExtra("member_url", feed.searchMemberUrl);
			intent.putExtra("title",
					((TextView) findViewById(R.community_chat.title)).getText());
			startActivity(intent);
			return true;
		case R.id.edit_community:
			DataModel.sSelf.storeObject(DMKeys.ENTRY, entry);
			intent = new Intent(ChannelReportActivity.this,
					CreateCommunityActivity.class);
			startActivity(intent);
			return true;
		case R.id.recommend_community:
			//			intent = new Intent(CommunityChatActivity.this, BuddyActivity.class);
			//			intent.putExtra("from_community", true);
			//			if(entry != null)
			//				intent.putExtra("community_name", entry.groupName);
			//			intent.putExtra(BuddyActivity.SCREEN_MODE, BuddyActivity.MODE_ONLY_FRIENDS);
			//			startActivity(intent);
			return true;
		case R.id.delete_community:
			// builder.setMessage("Do you really want to delete this Community?");
			DialogInterface.OnClickListener handler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(entry != null){
						StringBuilder text = new StringBuilder(
								"DeleteCommunity::Name##");
						text.append(entry.groupName);
						if (BusinessProxy.sSelf.sendNewTextMessage(
								"Community manager<a:communitymgr>",
								text.toString(), true)) {
							ChannelReportActivity.this.finish();
							DataModel.sSelf.storeObject(DMKeys.COMMUNITY_DELETED,
									Boolean.TRUE);
						}
					}
				}
			};
			showAlertMessage(
					getString(R.string.app_name),
					getString(R.string.do_you_really_want_to_delete_this_community),
					new int[] { DialogInterface.BUTTON_POSITIVE,
						DialogInterface.BUTTON_NEUTRAL }, new String[] {
						getString(R.string.yes), getString(R.string.no) },
						new DialogInterface.OnClickListener[] { handler, null });

			return true;
		case R.id.leave_community:
			/*
			 * builder.setPositiveButton("Yes", new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialog, int which) { dialog.dismiss();
			 * StringBuilder text = new StringBuilder("Leave::Name##");
			 * text.append(groupName); if
			 * (BusinessProxy.sSelf.sendNewTextMessage
			 * ("Community manager<a:communitymgr>", text.toString(), true)) {
			 * CommunityChatActivity.this.finish();
			 * DataModel.sSelf.storeObject(DMKeys.COMMUNITY_DELETED,
			 * Boolean.TRUE); } } }); builder.setNegativeButton("Cancel", null);
			 * builder.create(); builder.show();
			 */

			handler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(entry != null){
						StringBuilder text = new StringBuilder("Leave::Name##");
						text.append(entry.groupName);
						if (BusinessProxy.sSelf.sendNewTextMessage(
								"Community manager<a:communitymgr>",
								text.toString(), true)) {
							ChannelReportActivity.this.finish();
							DataModel.sSelf.storeObject(DMKeys.COMMUNITY_DELETED,
									Boolean.TRUE);
						}
					}
				}
			};
			showAlertMessage(
					getString(R.string.app_name),
					getString(R.string.do_you_really_want_to_leave_this_community),
					new int[] { DialogInterface.BUTTON_POSITIVE,
						DialogInterface.BUTTON_NEUTRAL }, new String[] {
						getString(R.string.yes), getString(R.string.no) },
						new DialogInterface.OnClickListener[] { handler, null });

			return true;
		case R.id.report_community: {
			/*
			 * StringBuilder text = new StringBuilder("Report::grpN##");
			 * text.append(groupName); text.append("::gId##");
			 * text.append(feed.groupId); if
			 * (BusinessProxy.sSelf.sendNewTextMessage
			 * ("Community Manager<a:communitymgr>", text.toString(), true)) {
			 * showSimpleAlert("Info",
			 * "Your request has been sent! You will receive a response shortly"
			 * ); }
			 */

			DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(entry != null){
						StringBuilder text = new StringBuilder("Report::grpN##");
						text.append(entry.groupName);
						text.append("::gId##");
						text.append(feed.groupId);
						if (BusinessProxy.sSelf.sendNewTextMessage(
								"Community Manager<a:communitymgr>",
								text.toString(), true)) {

							new Thread(new Runnable() {
								public void run() {
									showSimpleAlert(
											getString(R.string.info),
											getString(R.string.yourrequesthasbeensent));
								}
							}).start();//
						}
					}
				}
			};
			showAlertMessage(
					getString(R.string.confirm),
					getString(R.string.do_you_really_want_to_report_this_community),
					new int[] { DialogInterface.BUTTON_POSITIVE,
						DialogInterface.BUTTON_NEGATIVE }, new String[] {
						getString(R.string.yes), getString(R.string.no) },
						new DialogInterface.OnClickListener[] { exitHandler, null });
		}
		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String getFilePath(Payload payload, byte type) {
		String ret = "";
		FileOutputStream fout = null;
		try {
			StringBuilder path = new StringBuilder(getCacheDir()
					.getAbsolutePath());
			switch (type) {
			case POSITION_PICTURE:
				if (payload.mPicture[0].length < Constants.MAX_PATH_SIZE_IN_SYSTEM) {
					path = null;
					return new String(payload.mPicture[0]);
				}
				path.append("temp.jpg");
				fout = new FileOutputStream(path.toString());
				fout.write(payload.mPicture[0]);
				break;
			case POSITION_VOICE:
				if (payload.mVoice[0].length < Constants.MAX_PATH_SIZE_IN_SYSTEM) {
					path = null;
					return new String(payload.mVoice[0]);
				}
				path.append("temp.amr");
				fout = new FileOutputStream(path.toString());
				fout.write(payload.mVoice[0]);
				break;
			case POSITION_VIDEO:
				if (payload.mVideo[0].length < Constants.MAX_PATH_SIZE_IN_SYSTEM) {
					path = null;
					return new String(payload.mVideo[0]);
				}
				path.append("temp.3gp");
				fout = new FileOutputStream(path.toString());
				fout.write(payload.mVideo[0]);
				break;
			}
			fout.flush();
			fout.close();
			ret = path.toString();
			path = null;
		} catch (Exception e) {
			if (Logger.ENABLE) {
				Logger.error(TAG, "Error creating temp file for pciture show",
						e);
			}
		}
		return ret;
	}

	private void openThumbnailsToAttach(byte which) {
		Intent intent = new Intent();
		if(attachment_panel != null)
			attachment_panel.setVisibility(View.INVISIBLE);
		switch (which) {
		case POSITION_VOICE:
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, POSITION_VOICE);
			break;
		case POSITION_PICTURE_RT_CANVAS:
			intent = new Intent(this, RTCanvas.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
			// | Intent.FLAG_ACTIVITY_CLEAR_TOP
			// | Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.setData(Uri.parse("ddd")) ;
			startActivityForResult(intent, POSITION_PICTURE_RT_CANVAS);
			break;
		case POSITION_PICTURE:
		case POSITION_TALKING_PIC:
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
			intent = new Intent(this, KainatMultiImageSelection.class);
			//	intent = new Intent(this, RocketalkMultipleSelectImage.class);
			intent.putExtra("MAX", 5);
			//intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP	| Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			if(which == POSITION_TALKING_PIC)
				intent.putExtra("AUDIO_VISIBLITY", true);
			else
				intent.putExtra("AUDIO_VISIBLITY", false);
			startActivityForResult(intent, POSITION_MULTI_SELECT_PICTURE);
			// intent.setType("image/*");
			// intent.setAction(Intent.ACTION_GET_CONTENT);
			// startActivityForResult(Intent.createChooser(intent,
			// "Select Picture"), POSITION_PICTURE);
			break;

		case POSITION_VIDEO:
			/*
			 * intent.setType("video/*");
			 * intent.setAction(Intent.ACTION_GET_CONTENT);
			 * startActivityForResult( Intent.createChooser(intent,
			 * "Select Video"), POSITION_VIDEO);
			 */
			intent.setType("video/*");
			intent.setAction(Intent.ACTION_PICK);

			PackageManager pm = getPackageManager();
			List<ResolveInfo> allHandlers = pm.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			ApplicationInfo applicationInfo = null;
			for (ResolveInfo packageInfo : allHandlers) {
				applicationInfo = packageInfo.activityInfo.applicationInfo;

			}

			/*	// open Gallery in Nexus plus All Google based ROMs
			if (doesPackageExist("" + applicationInfo))
				intent.setClassName("" + applicationInfo,
						"com.android.gallery3d.app.Gallery");

			// open Gallery in Sony Xperia android devices
			if (doesPackageExist("" + applicationInfo))
				intent.setClassName("" + applicationInfo,
						"com.android.gallery3d.app.Gallery");

			// open gallery in HTC Sense android phones
			if (doesPackageExist("" + applicationInfo))
				intent.setClassName("" + applicationInfo,
						"com.htc.album.picker.PickerFolderActivity");

			// open gallery in Samsung TouchWiz based ROMs
			if (doesPackageExist("" + applicationInfo))
				intent.setClassName("" + applicationInfo,
						"com.cooliris.media.Gallery");*/

			startActivityForResult(intent, POSITION_VIDEO);

			break;
		}
	}

	public boolean doesPackageExist(String targetPackage) {

		PackageManager pm = getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(targetPackage,
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return false;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == -999) {
			if(profileEntry != null)
				entry = profileEntry;
			return;
		}
		if (resultCode != RESULT_OK) {
			if(requestCode == POSITION_MULTI_SELECT_PICTURE){
				DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES);
				mPicturePath = null;
			}
			return;
		}
		switch (requestCode) {
		case POSITION_VOICE:
			if(isInternetOn()){
				mVoicePath = getPath(data.getData());
				((ImageView)findViewById(R.community_chat.sendButton)).performClick();
			}
			break;
		case POSITION_CAMERA_PICTURE:
			if(!isInternetOn())
				return;
			String path = cameraImagePath;
			if (mPicturePath != null) {
				Bitmap bm = null;
				CompressImage compressImage = new CompressImage(ChannelReportActivity.this);
				cameraImagePath = compressImage.compressImage(cameraImagePath);
				if ((frontCam && rearCam) || (!frontCam && rearCam)) {
					try {
						BitmapFactory.Options bfo = new BitmapFactory.Options();
						bfo.inDither = false; // Disable Dithering mode
						bfo.inPurgeable = true;
						bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
						// bm =ImageUtils.rotate(bm,90,cameraImagePath);
						// bm=ImageUtils.imageRotateafterClick(bm,cameraImagePath);
						mPicturePath.add(path);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				if (frontCam && !rearCam) {
					// bm = ImageUtils.getImageFor(cameraImagePath, 4);
					// ImageUtils.fixOrientation(cameraImagePath,bm);
					// bm.recycle();
					Bitmap bmp = BitmapFactory.decodeFile(cameraImagePath);
					// ImageUtils.fixOrientation(cameraImagePath,bmp);
					// ImageUtils.rotateImage(cameraImagePath,bmp);
					bmp.recycle();
					// ImageUtils.rotateAndSaveImage(cameraImagePath);
					bm = ImageUtils.getImageFor(cameraImagePath, 4);
					mPicturePath.add(path);
				}
				((ImageView)findViewById(R.community_chat.sendButton)).performClick();
			}

			//Commented as Direct send enabled
			//			if (mPicturePathId != null) {
			//				mPicturePathId.add(-1);
			//			}
			//			image_count.setText("" + mPicturePath.size());
			//			if (mPicturePath != null && mPicturePath.size() <= 0)
			//				image_count.setVisibility(View.GONE);
			//			else
			//				image_count.setVisibility(View.VISIBLE);
			//			String textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox))
			//					.getText().toString();
			//			if (null == mPicturePath && textMessage != null
			//					&& 0 == textMessage.trim().length()) {
			//				showSimpleAlert(getString(R.string.error),
			//						getString(R.string.please_add_content_to_send_message));
			//				textMessage = "";
			//			}
			// ImageUtils.rotateAndSaveImage(path);
			// sendMessage(textMessage);
			break;
		case POSITION_PICTURE_RT_CANVAS:
			if(!isInternetOn())
				return;
			path = getPath(data.getData());
			mPicturePath.add(path);
			mPicturePathId.add(path.hashCode());
			image_count.setText("" + mPicturePath.size());
			//			if (mPicturePath != null && mPicturePath.size() <= 0)
			//				image_count.setVisibility(View.GONE);
			//			else
			//				image_count.setVisibility(View.VISIBLE);
			String textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox))
					.getText().toString();
			if (null == mPicturePath && textMessage != null
					&& 0 == textMessage.trim().length()) {
				showSimpleAlert(getString(R.string.error),
						getString(R.string.please_add_content_to_send_message));
				textMessage = "";
			}
			sendMessage(textMessage);
			//			new PostMessageInChannel().execute(textMessage);
			break;
		case POSITION_PICTURE:
			if(!isInternetOn())
				return;
			path = getPath(data.getData());
			mPicturePath.add(path);
			mPicturePathId.add(path.hashCode());
			image_count.setText("" + mPicturePath.size());
			//			if (mPicturePath != null && mPicturePath.size() <= 0)
			//				image_count.setVisibility(View.GONE);
			//			else
			//				image_count.setVisibility(View.VISIBLE);
			textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).getText().toString();
			if (null == mPicturePath && textMessage != null	&& 0 == textMessage.trim().length()) {
				showSimpleAlert(getString(R.string.error),getString(R.string.please_add_content_to_send_message));
				textMessage = "";
			}
			// sendMessage(textMessage);
			break;
		case POSITION_VIDEO:
			if(!isInternetOn()){
				return;
			}
			try {
				mVideoPath = getPath(data.getData());
			} catch (Exception e) {
				mVideoPath = cameraImagePath;
			}
			if (isSizeReachedMaximum(mVideoPath)) {
				if (BusinessProxy.sSelf.MaxSizeData == 2) {
					showSimpleAlert("Error",
							"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
				} else {

					//showSimpleAlert("Error", getString(R.string.max_attachment_reached_update));
					Toast.makeText(ChannelReportActivity.this, getString(R.string.max_attachment_reached_update), Toast.LENGTH_LONG).show();
				}
				mVideoPath = cameraImagePath = null;
				return;
			}
			if (Utilities.getFileInputStream(mVideoPath) == null)
				mVideoPath = Utilities.getVideoLastVideoFile(this);
			textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).getText().toString();
			if (textMessage == null)
				textMessage = "";
			sendMessage(textMessage);
			//			new PostMessageInChannel().execute(textMessage);
			break;


		case POSITION_MULTI_SELECT_PICTURE:
			if(!isInternetOn())
				return;
			//		String[] all_path = data.getStringArrayExtra("all_path");
			//		imagePathPos = (int[]) DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES+"IDINT");
			String s[] = (String[]) DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES);
			if (s != null) {
				mPicturePath = new Vector<String>();
				for (int i = 0; i < s.length; i++) {
					try {
						mPicturePath.add(s[i]);

					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				s  = null;
			}
			mVoicePath = (String) DataModel.sSelf.removeObject(DMKeys.VOICE_PATH);
			((ImageView)findViewById(R.community_chat.sendButton)).performClick();

			//		if (mPicturePathId != null && mPicturePathId.size() > 0) {
			//			Integer[] dataId = new Integer[mPicturePathId.size()];
			//			for (int i = 0; i < mPicturePathId.size(); i++) {
			//				dataId[i] = mPicturePathId.get(i);
			//			}
			//			DataModel.sSelf.storeObject("3333" + "ID", dataId);
			//		}
			//		if (mPicturePathId_int != null && mPicturePathId_int.size() > 0) {
			//			int[] dataIdint = new int[mPicturePathId_int.size()];
			//			for (int i = 0; i < mPicturePathId_int.size(); i++) {
			//				dataIdint[i] = mPicturePathId_int.get(i);
			//			}
			//			DataModel.sSelf.storeObject("3333"+"int", dataIdint);
			//		}
			break;
		}
	}

	// public String getPath(Uri uri) {
	// String[] projection = { MediaStore.Images.Media.DATA };
	// Cursor cursor = managedQuery(uri, projection, null, null, null);
	// int column_index = cursor
	// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	// cursor.moveToFirst();
	// return cursor.getString(column_index);
	// }

	public String getPath(Uri uri) {
		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();
		}
	}

	private void openCamera() {
		if(attachment_panel != null)
			attachment_panel.setVisibility(View.INVISIBLE);

		/*
		 * if((frontCam && rearCam) || (!frontCam && rearCam)){ cameraImagePath
		 * = null; File file = new
		 * File(Environment.getExternalStorageDirectory(), getRandomNumber() +
		 * ".jpg"); cameraImagePath = file.getPath(); Uri outputFileUri =
		 * Uri.fromFile(file); //Intent i = new
		 * Intent("android.media.action.IMAGE_CAPTURE");
		 * 
		 * //System.out.println("cameraImagePath===on"+cameraImagePath); Intent
		 * i =new Intent(CommunityChatActivity.this,DgCamActivity.class);
		 * i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		 * i.putExtra("urlpath", cameraImagePath );
		 * 
		 * this.startActivityForResult(i, POSITION_CAMERA_PICTURE); }if(frontCam
		 * && !rearCam){
		 */
		cameraImagePath = null;
		File file = new File(Environment.getExternalStorageDirectory(),
				getRandomNumber() + ".jpg");
		cameraImagePath = file.getPath();
		Uri outputFileUri = Uri.fromFile(file);
		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		// }
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

	public void onThumbnailResponse(final ThumbnailImage value, byte[] data) {
		handler.postDelayed(new Runnable() {
			public void run() {

				if (mMediaAdapter != null)
					mMediaAdapter.notifyDataSetChanged();

				if (!mMediaAdapter.isScrolling) {
					mMainList.invalidateViews();
				}
				int index = mMainList.getFirstVisiblePosition();
				index = value.mIndex - index;
				View view = mMainList.getChildAt(index);
				if (null == view)
					return;
				//				ImageView img = (ImageView) view.findViewById(R.community_chatlist_row.senderImage);
				//				if (null != img) {
				//					img.setVisibility(View.VISIBLE);
				//					img.setImageBitmap(value.mBitmap);
				//				}
				//				ImageView img = (ImageView) view.findViewById(R.community_chatlist_row.myImage);
				//				if (null != img) {
				//					img.setVisibility(View.VISIBLE);
				//					img.setImageBitmap(value.mBitmap);
				//				}
			}
		}, 500);
	}

	public void voiceRecordingStarted(String recordingPath) {

	}

	public void voiceRecordingCompleted(String recordedVoicePath) {
		if(!isCancelClick){
			if(dialog.isShowing())
				dialog.dismiss();
			mVoicePath = recordedVoicePath;
			String textMessage = ((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox))
					.getText().toString();
			if (null == mVoicePath && null == mPicturePath
					&& 0 == textMessage.trim().length()) {
				showSimpleAlert(getString(R.string.error),
						getString(R.string.please_add_content_to_send_message));
				return;
			}
			if (textMessage == null)
				textMessage = "";
			sendMessage(textMessage);
			//			new PostMessageInChannel().execute(textMessage);
		}
		else
			mVoicePath = null;
	}

	private void deleteTextLocalMessage(String messageId){
		if(mMediaAdapter != null){
			for(int i = 0; i < feed.entry.size(); i++){
				final CommunityFeed.Entry entry  = mMediaAdapter.getItem(i);
				if(messageId != null && messageId.equals(entry.messageId)){
					feed.entry.remove(entry);
					break;
				}
			}
			
		}
	}
	ProgressDialog sending = null;

	class PostMessageInChannel extends AsyncTask<String, Void, Integer>{
		Entry entrys = new CommunityFeed.Entry();
		boolean mediaLoading;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sending = ProgressDialog.show(ChannelReportActivity.this, "", getString(R.string.message_sending), true);
			sending.show();
			image_count.setVisibility(View.GONE);
			attachment_panel.setVisibility(View.INVISIBLE);
			((ImageView)findViewById(R.id.attachement)).setImageResource(R.drawable.attachment_unsel) ;
			if (mMainList != null)
				mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		}
		@Override
		protected Integer doInBackground(String... textMessage) {
			if (textMessage[0] != null && textMessage[0].trim().length() <= 0
					&& (mPicturePath == null || mPicturePath.size() <= 0)
					&& (mVoicePath == null || mVoicePath.length() <= 0)
					&& (mVideoPath == null || mVideoPath.length() <= 0)) {
				return -1;
			}
			entrys.messageId = "" + System.currentTimeMillis();
			entrys.senderId = BusinessProxy.sSelf.getUserId();
			entrys.firstName = SettingData.sSelf.getFirstName();
			entrys.lastName = SettingData.sSelf.getLastName();

			if (replyToSender) {
				entrys.replyTo = replyTo;
				entrys.replyToFirstName = replyToFirstName;
				entrys.replyToLastName = replyToLastName;
			}
			entrys.createdDate = Utilities.getCurrentdate();
			entrys.senderName = SettingData.sSelf.getUserName();
			entrys.media = new Vector<String>();
			if(mVoicePath != null && mVoicePath.length() > 0 && mVoicePath.contains("content://media")){
				mVoicePath = getRealPathFromURI(getApplicationContext(),Uri.parse(mVoicePath));
			}


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
				entrys.media.add("local");
				entrys.media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
				entrys.media.add("video");
				entrys.media.add(mVideoPath);
				entrys.media.add("thumb");
			}
			//Then Add Picture
			if (mPicturePath != null && mPicturePath.size() > 0) {
				entrys.imgUrl = "~n~p";
				for (int i = 0; i < mPicturePath.size(); i++) 
				{
					//Format to send 
					//mediaurl, "image", imageurl, normal/local, mediaurl, "image", imageurl, "thumb"
					entrys.imgUrl = entrys.imgUrl + mPicturePath.elementAt(i) + "~";
					entrys.imgThumbUrl = entrys.imgThumbUrl + mPicturePath.elementAt(i) + "~";
					//				if (mPicturePathId.size() > i)
					//					entrys.media.add(mPicturePathId.get(i));

					entrys.media.add(RandomUtils.nextInt(9999));
					entrys.media.add("image");
					entrys.media.add((mPicturePath.elementAt(i)));
					entrys.media.add("local");

					//				if (mPicturePathId.size() > i)
					//					entrys.media.add(mPicturePathId.get(i));

					entrys.media.add(RandomUtils.nextInt(9999));
					entrys.media.add("image");
					entrys.media.add((mPicturePath.elementAt(i)));
					entrys.media.add("local");
				}
			}
			entrys.messageText = Base64.encodeToString(textMessage[0].getBytes(), Base64.DEFAULT);
			image_count.setVisibility(View.GONE);

			if (feed == null) {
				feed = new CommunityFeed();
			}
			if (feed.entry == null)
				feed.entry = new Vector<CommunityFeed.Entry>();

			final OutboxTblObject newRequest = new OutboxTblObject(1, Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_VOICE_NEW);
			if (null != mVoicePath) {
				newRequest.mPayloadData.mVoiceType[0] = VoiceTypes.PCM_FORMAT;
				newRequest.mPayloadData.mVoice[0] = mVoicePath.getBytes();
				newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VOICE;
			}
			if (null != mVideoPath && mVideoPath.length() > 0) {
				newRequest.mPayloadData.mVideoType[0] = VideoTypes.VIDEO_3GP_FORMAT;
				newRequest.mPayloadData.mVideo[0] = mVideoPath.getBytes();
				newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
				entrys.isMediaUploading = true;
			}
			if (!"".equals(textMessage[0])) {
				newRequest.mPayloadData.mText[0] = textMessage[0].getBytes();
				newRequest.mPayloadData.mTextType[0] = 1;
				newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
			}
			if (null != mPicturePath && mPicturePath.size() > 0) {
				newRequest.mPayloadData.mPicture = new byte[mPicturePath.size()][];
				newRequest.mPayloadData.mPictureType = new byte[mPicturePath.size()];
				newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
				for (int i = 0; i < mPicturePath.size(); i++) {
					CompressImage compressImage = new CompressImage(ChannelReportActivity.this);
					compressImage.checkRotation(false);
					String path = compressImage.compressImage(mPicturePath.get(i));
					newRequest.mPayloadData.mPicture[i] = ((String) path).getBytes();
					newRequest.mPayloadData.mPictureType[i] = PictureTypes.PICS_JPEG;
				}
			}
			if(entry != null)
				newRequest.mDestContacts = new String[] { (entry.groupName + "<g:"+ entry.groupName + ">") };
			if (replyToSender) {
				newRequest.mMessageId = replyMessageId;
				newRequest.mMessageOp = Constants.MSG_OP_REPLY;
			}

			/*int ret = BusinessProxy.sSelf.sendToTransport(ChannelReportActivity.this, newRequest);
			return ret;*/
			return 0;
		}

		@Override
		protected void onPostExecute(Integer ret) {
			vector.add(entrys);
			feed.entry.add(0, entrys);
			//Add this Entry to senderEntry so that, a check can be made at receiver side
			//		senderEntry = entrys;
			if((null != mVideoPath && mVideoPath.length() > 0)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(viewHolder != null && viewHolder.progress_bar !=null ){
							viewHolder.progress_bar.setProgress(0);
							viewHolder.progress_bar.setSecondaryProgress(100);
							viewHolder.progress_bar.setVisibility(View.VISIBLE);
						}
						if(viewHolder != null && viewHolder.download_circular_progress_percentage != null){
							viewHolder.download_circular_progress_percentage.setText("0%");
							viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
						}
					}
				});
			}
			hideShowNoMessageView();
			mMainList.setSelection(feed.entry.size() - 1);
			mMediaAdapter.notifyDataSetChanged();
			mMainList.setStackFromBottom(true);
			mMainList.invalidateViews();

			switch (ret.intValue()) {
			case Constants.ERROR_NONE:
				stopTimerToRefresh();
				((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).setText("");
				mVoicePath = null;
				mVideoPath = null;
				mPicturePath.clear();
				mPicturePathId.clear();
				replyMessageId = null;
				if (mMediaAdapter != null)
					mMediaAdapter.notifyDataSetChanged();
				if (mMainList != null)
					getContent(mMainList.getFirstVisiblePosition());
				break;
			case Constants.ERROR_OUTQUEUE_FULL:
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(mMediaAdapter != null){
							mMediaAdapter.resetVideoUploaddInfeed();
							mMediaAdapter.notifyDataSetChanged();
						}
					}
				});
				showSimpleAlert(getString(R.string.error), "Outbox full. Please try after some time!");
				break;
			default:
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(mMediaAdapter != null){
							mMediaAdapter.resetVideoUploaddInfeed();
							mMediaAdapter.notifyDataSetChanged();
						}
					}
				});
				showSimpleAlert(getString(R.string.error), getString(R.string.network_busy));
				break;
			}
			sending.dismiss();
		}

	}

	private void sendMessage(String textMessage) {
		image_count.setVisibility(View.GONE);
		attachment_panel.setVisibility(View.INVISIBLE);
		((ImageView)findViewById(R.id.attachement)).setImageResource(R.drawable.attachment_unsel) ;
		if (mMainList != null)
			mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		if (textMessage != null && textMessage.trim().length() <= 0
				&& (mPicturePath == null || mPicturePath.size() <= 0)
				&& (mVoicePath == null || mVoicePath.length() <= 0)
				&& (mVideoPath == null || mVideoPath.length() <= 0)) {
			return;
		}
		Entry entrys = new CommunityFeed.Entry();
		entrys.messageId = "" + System.currentTimeMillis();
		entrys.senderId = BusinessProxy.sSelf.getUserId();
		entrys.firstName = SettingData.sSelf.getFirstName();
		entrys.lastName = SettingData.sSelf.getLastName();

		if (replyToSender) {
			entrys.replyTo = replyTo;
			entrys.replyToFirstName = replyToFirstName;
			entrys.replyToLastName = replyToLastName;
		}
		entrys.createdDate = Utilities.getCurrentdate();
		entrys.senderName = SettingData.sSelf.getUserName();
		entrys.media = new Vector<String>();
		if(mVoicePath != null && mVoicePath.length() > 0 && mVoicePath.contains("content://media")){
			mVoicePath = getRealPathFromURI(getApplicationContext(),Uri.parse(mVoicePath));
		}


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
			entrys.media.add("local");
			entrys.media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
			entrys.media.add("video");
			entrys.media.add(mVideoPath);
			entrys.media.add("thumb");
		}
		//Then Add Picture
		if (mPicturePath != null && mPicturePath.size() > 0) {
			entrys.imgUrl = "~n~p";
			for (int i = 0; i < mPicturePath.size(); i++) 
			{
				//Format to send 
				//mediaurl, "image", imageurl, normal/local, mediaurl, "image", imageurl, "thumb"
				entrys.imgUrl = entrys.imgUrl + mPicturePath.elementAt(i) + "~";
				entrys.imgThumbUrl = entrys.imgThumbUrl + mPicturePath.elementAt(i) + "~";
				//				if (mPicturePathId.size() > i)
				//					entrys.media.add(mPicturePathId.get(i));

				entrys.media.add(RandomUtils.nextInt(9999));
				entrys.media.add("image");
				entrys.media.add((mPicturePath.elementAt(i)));
				entrys.media.add("local");

				//				if (mPicturePathId.size() > i)
				//					entrys.media.add(mPicturePathId.get(i));

				entrys.media.add(RandomUtils.nextInt(9999));
				entrys.media.add("image");
				entrys.media.add((mPicturePath.elementAt(i)));
				entrys.media.add("local");
			}
		}
		entrys.messageText = Base64.encodeToString(textMessage.getBytes(), Base64.DEFAULT);

		//		if (mMediaAdapter == null) {
		//			mMediaAdapter = new CommunityChatAdapter(this);
		//			mMainList.setAdapter(mMediaAdapter);
		//		}
		//		new Thread(new Runnable() {
		//
		//			@Override
		//			public void run() {
		//
		//				runOnUiThread(new Runnable() {
		//
		//					@Override
		//					public void run() {
		//						hideShowNoMessageView();
		//						// mMainList.setSelection(feed.entry.size() );
		//						mMainList.setSelection(feed.entry.size() - 1);
		//						mMediaAdapter.notifyDataSetChanged();
		//						// mMediaAdapter.notifyDataSetInvalidated();
		//
		//						// mMainList.fullScroll(ScrollView.FOCUS_DOWN);
		//						// mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		//						mMainList.setStackFromBottom(true);
		//						mMainList.invalidateViews();
		//						// for (int j = 0; j < 20; j++)
		//						// {
		//						// mMainList.scrollTo(mMainList.getScrollX(),mMainList.getScrollY()+j);
		//						// mMainList.invalidate();
		//						// }
		//					}
		//				});
		//
		//			}
		//		}).start();
		image_count.setVisibility(View.GONE);
		// mMainList.setSelection(feed.entry.size());

		if (feed == null) {
			feed = new CommunityFeed();
		}
		if (feed.entry == null)
			feed.entry = new Vector<CommunityFeed.Entry>();

		OutboxTblObject newRequest = new OutboxTblObject(1, Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_VOICE_NEW);
		if (null != mVoicePath) {
			newRequest.mPayloadData.mVoiceType[0] = VoiceTypes.PCM_FORMAT;
			newRequest.mPayloadData.mVoice[0] = mVoicePath.getBytes();
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VOICE;
		}
		if (null != mVideoPath && mVideoPath.length() > 0) {
			newRequest.mPayloadData.mVideoType[0] = VideoTypes.VIDEO_3GP_FORMAT;
			newRequest.mPayloadData.mVideo[0] = mVideoPath.getBytes();
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_VIDEO;
			entrys.isMediaUploading = true;
		}
		if (!"".equals(textMessage)) {
			newRequest.mPayloadData.mText[0] = textMessage.getBytes();
			newRequest.mPayloadData.mTextType[0] = 1;
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
		}
		if (null != mPicturePath && mPicturePath.size() > 0) {
			newRequest.mPayloadData.mPicture = new byte[mPicturePath.size()][];
			newRequest.mPayloadData.mPictureType = new byte[mPicturePath.size()];
			newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_PICTURE;
			for (int i = 0; i < mPicturePath.size(); i++) {
				CompressImage compressImage = new CompressImage(this);
				compressImage.checkRotation(false);
				String path = compressImage.compressImage(mPicturePath.get(i));
				newRequest.mPayloadData.mPicture[i] = ((String) path).getBytes();
				newRequest.mPayloadData.mPictureType[i] = PictureTypes.PICS_JPEG;
			}
		}
		if(entry != null)
			newRequest.mDestContacts = new String[] { (entry.groupName + "<g:"+ entry.groupName + ">") };
		if (replyToSender) {
			newRequest.mMessageId = replyMessageId;
			newRequest.mMessageOp = Constants.MSG_OP_REPLY;
		}
		vector.add(entrys);
		feed.entry.add(0, entrys);
		//Add this Entry to senderEntry so that, a check can be made at receiver side
		//		senderEntry = entrys;
		if((null != mVideoPath && mVideoPath.length() > 0)){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(viewHolder != null && viewHolder.progress_bar !=null ){
						viewHolder.progress_bar.setProgress(0);
						viewHolder.progress_bar.setSecondaryProgress(100);
						viewHolder.progress_bar.setVisibility(View.VISIBLE);
					}
					if(viewHolder != null && viewHolder.download_circular_progress_percentage != null){
						viewHolder.download_circular_progress_percentage.setText("0%");
						viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
					}
				}
			});
		}
		hideShowNoMessageView();
		mMainList.setSelection(feed.entry.size() - 1);
		mMediaAdapter.notifyDataSetChanged();
		mMainList.setStackFromBottom(true);
		mMainList.invalidateViews();

		/*int ret = BusinessProxy.sSelf.sendToTransport(this, newRequest);
		switch (ret) {
		case Constants.ERROR_NONE:
			//			CHAT_REFRESH_TIME = CHAT_MAX_REFRESH_TIME;
			//			if((null != mVideoPath && mVideoPath.length() > 0)){
			//				runOnUiThread(new Runnable() {
			//					
			//					@Override
			//					public void run() {
			//						// TODO Auto-generated method stub
			//						viewHolder.progress_bar.setProgress(0);
			//						viewHolder.progress_bar.setSecondaryProgress(100);
			//						viewHolder.progress_bar.setVisibility(View.VISIBLE);
			//						viewHolder.download_circular_progress_percentage.setText("0%");
			//						viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
			//					}
			//				});
			//			}
			stopTimerToRefresh();
			((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox)).setText("");
			mVoicePath = null;
			mVideoPath = null;
			mPicturePath.clear();
			mPicturePathId.clear();
			replyMessageId = null;
			if (mMediaAdapter != null)
				mMediaAdapter.notifyDataSetChanged();
			if (mMainList != null)
				getContent(mMainList.getFirstVisiblePosition());
			break;
		case Constants.ERROR_OUTQUEUE_FULL:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mMediaAdapter != null){
						mMediaAdapter.resetVideoUploaddInfeed();
						mMediaAdapter.notifyDataSetChanged();
					}
				}
			});
			showSimpleAlert(getString(R.string.error), "Outbox full. Please try after some time!");
			return;
		default:
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mMediaAdapter != null){
						mMediaAdapter.resetVideoUploaddInfeed();
						mMediaAdapter.notifyDataSetChanged();
					}
				}
			});
			showSimpleAlert(getString(R.string.error), getString(R.string.network_busy));
			return;
		}*/
	}

	public void processSendMessageResponse(ResponseObject res_obj){
		if(res_obj != null){
			switch(res_obj.getError()){
			case Constants.ERROR_NONE:
				switch (res_obj.getResponseCode()) {
				case Constants.RES_TYPE_SUCCESS:
					Log.w(TAG, "processSendMessageResponse :	: RES_TYPE_SUCCESS  : MSG_OP = "+res_obj.getSentOp());
					switch(res_obj.getSentOp()){
					case Constants.MSG_OP_VOICE_NEW:
					case Constants.MSG_OP_REPLY:
						Log.w(TAG, "processSendMessageResponse :: RES_TYPE_SUCCESS : MSG_OP = MSG_OP_VOICE_NEW");
						break;
					default:
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(mMediaAdapter != null){
									mMediaAdapter.resetVideoUploaddInfeed();
									mMediaAdapter.notifyDataSetChanged();
								}
							}
						});
						//							CHAT_REFRESH_TIME = CHAT_MIN_REFRESH_TIME;
						startTimerToRefresh();
						Log.w(TAG, "processSendMessageResponse :: RES_TYPE_SUCCESS : MSG_OP = "+res_obj.getSentOp());
						break;
					}
					break;
				default:
					Log.w(TAG, "processSendMessageResponse :: res_obj.getResponseCode() : "+res_obj.getResponseCode());
					break;
				}
				break;
			case Constants.ERROR_NET:
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(mMediaAdapter != null){
							mMediaAdapter.resetVideoUploaddInfeed();
							mMediaAdapter.notifyDataSetChanged();
						}
					}
				});
				switch(res_obj.getSentOp()){
				case Constants.MSG_OP_VOICE_NEW:
				case Constants.MSG_OP_REPLY:
					Log.w(TAG, "processSendMessageResponse :: res_obj.getError() : "+res_obj.getError());
					break;
				default:
					Log.w(TAG, "processSendMessageResponse :: res_obj.getError() : "+res_obj.getError());
					Log.w(TAG, "processSendMessageResponse :: res_obj.getSentOp() : "+res_obj.getSentOp());
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(ChannelReportActivity.this, getString(R.string.no_internet_connection),  Toast.LENGTH_SHORT).show();
						}
					});
					break;
				}
				break;
			default:
				Log.w(TAG, "processSendMessageResponse :: res_obj.getError() : "+res_obj.getError());
				break;
			}
		}
	}

	public void networkDataLoadChange(int data) {
		// TODO Auto-generated method stub
		uploadingPercentage = data;
		Log.i(TAG, "networkDataChange :: % data : "+uploadingPercentage);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewHolder.progress_bar.setVisibility(View.VISIBLE);
				viewHolder.progress_bar.setProgress(uploadingPercentage);
				viewHolder.progress_bar.setSecondaryProgress(100);
				viewHolder.download_circular_progress_percentage.setVisibility(View.VISIBLE);
				viewHolder.download_circular_progress_percentage.setText(uploadingPercentage+"%");
			}
		});
	}

	public void resetProgress() {
		if(bar != null){
			bar.setVisibility(View.VISIBLE);
			bar.setProgress(0);
			bar.invalidate();
		}
	}

	public void setEmoticon(String s) {
		EditText textbox = (com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox);
		textbox.setText(textbox.getText() + s);
		textbox.setSelection(textbox.getText().toString().length());
	}

	public void notificationFromTransport(ResponseObject response) {
	}

	class CProgressDialog extends ProgressDialog {
		public CProgressDialog(Context context) {
			super(context);
			lodingCanceled = false;
		}

		public void onBackPressed() {
			return;
		}
	}

	protected void showLoggingDialog(String aString) {
		mProgressDialog = new CProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.please_wait_while_loadin)
				+ aString + "..");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	public void run() {
		while (mIsRunning) {
			try {
				synchronized (this) {
					// STATE = READY;
					wait();
					// STATE = BUSY;
				}
				cancelOperation = false;
				//				BusinessProxy.sSelf.writeLogsToFile(TAG +" Entry run loop");
				if (null != mPostURL && 0 < mPostURL.trim().length()) {
					if(log)
						Log.i(TAG, "run :: --------chating mPostURL-------------------------->"+ mPostURL);
					helper = new HttpConnectionHelper(mPostURL);
					helper.setHeader("RT-APP-KEY", "" + iLoggedUserID);
					//					helper.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
					mCallBackTimer = new Timer();
					responseCode = helper.getResponseCode();
					if(log)
						Log.i(TAG, "run :: ---------community chat------------responseCode---->"+ responseCode);

					if (cancelOperation)
						responseCode = -999;
					//					BusinessProxy.sSelf.writeLogsToFile(TAG +" HTTP responseCode  "+responseCode);
					switch (responseCode) {
					case HttpURLConnection.HTTP_OK:
						String contentEncoding = helper
						.getHttpHeader("Content-Encoding");
						InputStream inputStream = null;
						if (null == contentEncoding) {
							inputStream = helper.getInputStream();
						} else if (contentEncoding.equals("gzip")) {
							inputStream = new GZIPInputStream(
									helper.getInputStream());
						}
						ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
						byte[] chunk = new byte[CHUNK_LENGTH];
						int len;
						while (-1 != (len = inputStream.read(chunk))) {
							buffer.append(chunk, 0, len);
							if (cancelOperation)
								break;
						}
						switch (requestType) {
						case ACTUAL_REFRESH_DATA_CALLBACK:
							this.mResponseDataRefresh = buffer.toByteArray();
							ChannelReportActivity.this.mResponseData = null;
							break;
						default:
							ChannelReportActivity.this.mResponseData = buffer.toByteArray();
							ChannelReportActivity.this.mResponseDataRefresh = null;
							if (requestType == ACTUAL_IMAGE_DATA_CALLBACK
									|| requestType == ACTUAL_AUDIO_DATA_CALLBACK) {
								cache.put(mPostURL, ChannelReportActivity.this.mResponseData);
							}
						}
						buffer = null;
						break;
					default:
						if (lDialogProgress != null
						&& lDialogProgress.isShowing()) {
							lDialogProgress.dismiss();
						}
						break;
					}
					if (lDialogProgress != null && lDialogProgress.isShowing()) {
						lDialogProgress.dismiss();
					}
					if (log)
						Log.i(TAG, "run :: chating responseCode : "+ responseCode);
					mCallBackTimer.cancel();
					mCallBackTimer = new Timer();
					mCallBackTimer.schedule(new OtsSchedularTask(this, requestType, (byte) 0), 0);
				}
			} catch (final Exception ex) {
				mCallBackTimer = new Timer();
				mCallBackTimer.schedule(new OtsSchedularTask(this,
						ERROR_CALLBACK, (byte) 0), 0);
			}
			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
			}
		}
	}

	private String refreshUrl;
	TimerTask refresh = new TimerTask() {

		@Override
		public void run() {
			while (startTimerToRefreshBool) {
				try {
					try {
						if(log)
							Log.v(TAG, "TimerTask :: refresh : run() : Waiting for --> "+ CHAT_REFRESH_TIME/1000+" seconds");
						Thread.sleep(CHAT_REFRESH_TIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
					String url = feed.socketedMessageUrl;
					url = url.replace("$maxCount$", ""+CHANNEL_MSG_MAX_COUNT);//Earlier it was 3
					url = url.replace("$order$", "ASC");
					if(url.indexOf("$messageId$") != -1 && getMessageIdRefresh() != null)
						url = url.replace("$messageId$", getMessageIdRefresh());
					else
						url = url.replace("$messageId$", "1");
					refreshUrl = url + "&textMode=base64&" + BIG_THUMB_INFO;
					if (null != refreshUrl && 0 < refreshUrl.trim().length()) {
						if(log)
							Log.v(TAG, "TimerTask :: refresh : run :: Channel Post refreshUrl --> "+ refreshUrl);
						HttpConnectionHelper helper = new HttpConnectionHelper(refreshUrl);
						helper.setHeader("RT-APP-KEY", "" + iLoggedUserID);
						helper.setHeader("RT-DEV-KEY","" + BusinessProxy.sSelf.getClientId());
						responseCode = helper.getResponseCode();
						if(log)
							Log.v(TAG, "TimerTask :: refresh : run :: Channel Post responseCode -->"+ responseCode);
						switch (responseCode) {
						case HttpURLConnection.HTTP_OK:
							String contentEncoding = helper.getHttpHeader("Content-Encoding");
							//							String contentType = helper.getHttpHeader("Content-Type");
							InputStream inputStream = null;
							if (null == contentEncoding) {
								inputStream = helper.getInputStream();
							} else if (contentEncoding.equals("gzip")) {
								inputStream = new GZIPInputStream(helper.getInputStream());
							}
							ByteArrayBuffer buffer = new ByteArrayBuffer(CHUNK_LENGTH);
							byte[] chunk = new byte[CHUNK_LENGTH];
							int len;
							while (-1 != (len = inputStream.read(chunk))) {
								buffer.append(chunk, 0, len);
								//								if (!startTimerToRefreshBool){
								//									Log.w(TAG, "startTimerToRefreshBool : false, so break without any response data");
								//									return;
								//								}
							}
							parseXMLData(buffer.toByteArray(), ACTUAL_REFRESH_DATA_CALLBACK);
							// this.mResponseDataRefresh = buffer.toByteArray();
							buffer = null;
							break;
						default:
							break;
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	};


	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			int maxCount = view.getLastVisiblePosition()
					- view.getFirstVisiblePosition() + 1;
			startThumbnailThread(maxCount);
			// mMediaAdapter.notifyDataSetChanged();
			// mMediaAdapter.notifyDataSetInvalidated();
			// mMainList.invalidateViews();
		}
		if (scrollState != 0) {
			// if(fillMedia !=null && !fillMedia.isCancelled())
			// fillMedia.cancel(true);
			// viewHolderStack = new Stack();
			mMediaAdapter.isScrolling = true;
			mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
		} else {
			// viewHolderStack = new Stack();
			mMediaAdapter.isScrolling = false;
			// mMediaAdapter.notifyDataSetChanged();
			// mMediaAdapter.notifyDataSetInvalidated();
		}
	}

	ImageDownloader imageManager = new ImageDownloader(2);

	private void startThumbnailThread(int maxCount) {
		try {

			// mImageLoader.setRequestType(ImageLoader.LAOD_IMAGES_FORM_RT);
			// if (maxCount > mMediaAdapter.getCount())
			// maxCount = mMediaAdapter.getCount();
			// int start = mMainList.getFirstVisiblePosition();
			// String[] names = new String[maxCount];
			// int[] indexs = new int[maxCount];
			// Entry entry = null;
			// for (int i = 0; i < maxCount; ++i) {
			// int pos = getActualPosition((start + i));
			// if (pos > feed.entry.size() - 1) {
			// continue;
			// }
			// entry = feed.entry.elementAt(pos);
			// if (null != entry) {
			// names[i] = entry.senderName;
			// indexs[i] = start + i;
			// }
			// }
			// mImageLoader.cleanAndPutRequest(names, indexs);
			// getContent(start);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	// http://schemas.rocketalk.com/2010#collection.mediaurl
	// video
	// http://akm.onetouchstar.com/2012/09/03/13/4618105332001780736.mp4
	// normal
	// http://schemas.rocketalk.com/2010#collection.mediaurl
	// video
	// http://akm.onetouchstar.com/2012/09/03/13/2063902070959660032.jpg
	// thumb
	//
	// -http://schemas.rocketalk.com/2010#collection.mediaurl
	// -video
	// http://akm.onetouchstar.com/2012/09/03/13/4618105332001780736.mp4
	// normal
	// http://schemas.rocketalk.com/2010#collection.mediaurl
	// video
	// http://akm.onetouchstar.com/2012/09/03/13/2063902070959660032.jpg
	// thumb

	public void getContent(int start) {
		if (1 == 1)
			return;
		Vector<String> ursl = new Vector<String>();
		if (currentLoadMedia != null) {
			// currentLoadMedia.cancel(true);
			currentLoadMedia.ursl.clear();
		}
		try {
			// System.out.println("-------------getContent---------------"+start);
			// System.out
			// .println("-------------getCount-----------"
			// + mMainList.getCount());
			// System.out
			// .println("-------------getContent----getFirstVisiblePosition-----------"
			// + mMainList.getFirstVisiblePosition());
			// System.out
			// .println("-------------gmMainList.getLastVisiblePosition()---------------"
			// + mMainList.getLastVisiblePosition());
			int ii = mMainList.getFirstVisiblePosition();
			int iii = mMainList.getLastVisiblePosition();
			// if(iii >= feed.entry.size())
			// feed.entry.size() ;
			for (int i = ii; i <= iii; i++) {
				// System.out.println("-------------inside loop---------------"
				// + (feed.entry.size() - (i+1)));
				Entry entry = feed.entry.get(feed.entry.size() - (i + 1));
				for (int j = 0; j < (entry.media.size()); j++) {
					// System.out.println("-------------gmedia---------------"
					// + entry.media.elementAt(j));
				}
				// System.out.println("---------------------------------");
				// if(currentLoadMedia !=null)
				// currentLoadMedia.cancel(true);
				// if(currentLoadMedia==null /*||
				// currentLoadMedia.isCancelled()*/)
				// {
				boolean audio = false;
				for (int j = 0; j < (entry.media.size()); j += 8) {
					if (audio) {
						j = j - 4;
						audio = false;
					}
					if (((String) entry.media.elementAt(j + 1))
							.equalsIgnoreCase("audio"))
						audio = true;

					if (entry.media.size() >= j + 6)
						ursl.add((String) entry.media.elementAt(j + 6));
					// System.out.println("-------------loading------media---------------"+entry.media.elementAt(j+6));
				}

				// }
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		// if(currentLoadMedia !=null )
		// System.out.println("------currentLoadMedia.getStatus()------------"+currentLoadMedia.getStatus());
		if (currentLoadMedia == null || currentLoadMedia.isCancelled()) {
			currentLoadMedia = new LoadMedia(ursl);
			currentLoadMedia.execute("");// new execute null
		} else {
			currentLoadMedia.ursl = ursl;
		}
	}

	private LoadMedia currentLoadMedia = null;

	class LoadMedia extends AsyncTask<Object, Void, String> {
		Vector<String> ursl = new Vector<String>();

		public LoadMedia(Vector ursl) {
			this.ursl = ursl;
		}

		@Override
		protected String doInBackground(Object... paramsl) {
			currentLoadMedia = this;
			try {
				// System.out
				// .println("--------------url indide doInBackground---");
				// System.out.println("--------------url indide ursl.size()---"
				// + ursl.size());
				// System.out.println("--------------url isCancelled()---"
				// + isCancelled());
				while (!isCancelled()) {
					for (int i = 0; i < ursl.size(); i++) {
						String s = ursl.get(i);
						String videoThumb = null;
						// if(isCancelled())return null ;
						// System.out
						// .println("--------------url to load image from internate-"
						// + s);
						String path = s.substring(s.lastIndexOf("/") + 1,
								s.length());
						// System.out.println("---path-" + path);
						InputStream is = null;
						try {
							is = openFileInput(path);
						} catch (Exception e) {
							is = null;
						}

						if (is == null) {
							is = (InputStream) new URL(s).getContent();

							// if(isCancelled())return null ;
							byte[] data = Utilities.readBytes(is);// new
							// byte[is.available()]
							// ;
							is.read(data);
							is.close();
							// System.out
							// .println("--------------url---data--------"
							// + data.length);
							// if(isCancelled())return null ;
							FileOutputStream fout = openFileOutput(path,
									Context.MODE_PRIVATE);

							fout.write(data);
							fout.flush();
							fout.close();
						} else {
							// System.out
							// .println("--------------url---already cache-"
							// + s);
						}
						// if(isCancelled())return null ;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mMediaAdapter.isScrolling = false;
								mMediaAdapter.notifyDataSetChanged();
								// mMediaAdapter.notifyDataSetInvalidated();
							}
						});

					}
					// System.out.println("-------------list size-" +
					// ursl.size());
					if (currentLoadMedia != null
							&& currentLoadMedia.getStatus() != Status.RUNNING)
						currentLoadMedia.cancel(true);
				}
				return null;

			} catch (Exception e) {
				// e.printStackTrace();
				return null;
			} catch (OutOfMemoryError e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String imageView) {
			// System.out.println("--------------url---onPostExecute-");
		}
	}

	Stack viewHolderStack = new Stack();
	private FillMedia fillMedia = null;

	class FillMedia extends AsyncTask<Object, Void, String> {
		// Stack viewHolderStack = new Stack();
		ViewHolder viewHolder;

		public FillMedia(Stack ursl) {
			// System.out.println("-----fill meedis----");
			// this.viewHolderStack=ursl;
		}

		public FillMedia(ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}

		@Override
		protected String doInBackground(Object... paramsl) {
			fillMedia = this;

			if (viewHolder.inboxLayout_imageLayout_image != null)
				runOnUiThread(new Runnable() {

					public void run() {
						if (viewHolder.inboxLayout_imageLayout_video != null) {
							// System.out.println("-------inboxLayout_imageLayout_video()----"+viewHolder.inboxLayout_imageLayout_video.getChildCount());
							for (int i = 0; i < viewHolder.inboxLayout_imageLayout_video
									.getChildCount(); i++) {
								ImageView imageview = (ImageView) viewHolder.inboxLayout_imageLayout_video
										.getChildAt(i);
								// System.out.println("--------video thumb tag url---"+imageview.getTag());

								// imageview.setImageResource(R.drawable.seekthumb);
								if (imageview.getTag() != null) {
									String s = (String) imageview.getTag();
									String path = s.substring(
											s.lastIndexOf("/") + 1, s.length());
									// System.out.println("---fill media-path-"
									// + path);
									InputStream is = null;
									try {
										is = openFileInput(path);
										byte[] data1 = Utilities
												.inputStreamToByteArray(is);
										if (data1 != null) {
											// System.out
											// .println("---got image---");
											Bitmap mBitmap = BitmapFactory
													.decodeByteArray(data1, 0,
															data1.length);
											imageview.setImageBitmap(mBitmap);
										}
										// else
										// System.out
										// .println("---got not image---");
									} catch (Exception e) {
										e.fillInStackTrace();
									}
								}
								imageview.invalidate();
							}
						}
						if (viewHolder.inboxLayout_imageLayout_image != null) {
							// System.out.println("-------viewHolder.inboxLayout_imageLayout_image.getChildCount()----"+viewHolder.inboxLayout_imageLayout_image.getChildCount());
							for (int i = 0; i < viewHolder.inboxLayout_imageLayout_image
									.getChildCount(); i++) {
								ImageView imageview = (ImageView) viewHolder.inboxLayout_imageLayout_image
										.getChildAt(i);
								// System.out
								// .println("--------image thumb tag url---"
								// + imageview.getTag());
								if (imageview.getTag() != null) {
									String s = (String) imageview.getTag();
									String path = s.substring(
											s.lastIndexOf("/") + 1, s.length());
									// System.out.println("---fill media-path-"
									// + path);
									InputStream is = null;
									try {
										is = openFileInput(path);
										byte[] data1 = Utilities
												.inputStreamToByteArray(is);
										if (data1 != null) {
											// System.out
											// .println("---got image---");
											Bitmap mBitmap = BitmapFactory
													.decodeByteArray(data1, 0,
															data1.length);
											imageview.setImageBitmap(mBitmap);
										}
										// else
										// System.out
										// .println("---got not image---");
									} catch (Exception e) {
										e.fillInStackTrace();
									}
								}
								imageview.invalidate();
							}
						}
						// if(viewHolder.inboxLayout_imageLayout_voice != null){
						// for (int i = 0; i <
						// viewHolder.inboxLayout_imageLayout_voice.getChildCount();
						// i++) {
						// ImageView imageview =
						// (ImageView)viewHolder.inboxLayout_imageLayout_voice.getChildAt(i);
						// imageview.setImageResource(R.drawable.seekthumb);
						// imageview.invalidate();
						// }
						// }
					}
				});

			return "";
		}

		@Override
		protected void onPostExecute(String imageView) {
			super.onPostExecute(imageView);
			{
			}
			// fillMedia.cancel(true);
			// if(fillMedia !=null && fillMedia.isCancelled())
			// viewHolderStack = new Stack();
			// System.out.println("-----fill meedis-onPostExecute---"+viewHolderStack.size());
			fillMedia = null;
			if (viewHolderStack.size() > 0) {
				fillMedia = new FillMedia((ViewHolder) viewHolderStack.pop());
				// fillMedia.execute(null);
			}
		}
	}

	public void onTaskCallback(Object parameter, byte req) {
		if (activityStop)
			return;
		byte task = ((Byte) parameter).byteValue();
		switch (task) {
		case HTTP_TIMEOUT:
			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
			}
			helper.close();
			showSimpleAlert("Error", "Unable to retrieve\n" + mCurrentScreen);
			break;
		case XML_DATA_CALLBACK:
			feedLoadedFirstTime = true;
			//			startRefresh();//Old refresh, so commented - New is active now
			if (responseCode == -1) {
				mIsRunning = true;
				return;
			}
			mIsRunning = false;
			if (null != mResponseData) {
				parseXMLData(mResponseData, task);
				if(feed != null)
					cancelLoading();
				resetLoading();
				//				new Thread(new Runnable() {
				//
				//					@Override
				//					public void run() {
				//						try {
				//							Thread.sleep(3000);
				//						} catch (InterruptedException e) {
				//							// TODO Auto-generated catch block
				//							e.printStackTrace();
				//						}
				//						if (mMainList != null)
				//							getContent(10);
				//					}
				//				}).start();
			} else {
				cancelLoading();
				feed = oldFeed;
				resetLoading();
				showSimpleAlert("Error:", "Unable to retrieve\n"+ mCurrentScreen);
			}
			requestednextPage = false;
			break;
		case ACTUAL_REFRESH_DATA_CALLBACK:
			if (lodingCanceled) {
				lodingCanceled = false;
				return;
			}
			mIsRunning = false;
			if (null != mResponseDataRefresh) {
				parseXMLData(mResponseDataRefresh, task);
			}
			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
			}
			//			startRefresh();//Old refresh, so commented - New is active now
			break;
		case ACTUAL_AUDIO_DATA_CALLBACK:
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						parseForAttachment();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					if (lDialogProgress != null && lDialogProgress.isShowing()) {
						lDialogProgress.dismiss();
					}
					if (payloadData == null || payloadData.mVoice == null) {
						return;
					}
					mVoiceMedia.startPlaying(payloadData.mVoice[0], "npnp");
					mResponseData = null;
				}
			});
			helper.close();
			break;
		case ACTUAL_VIDEO_DATA_CALLBACK:
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						parseForAttachment();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					if (lDialogProgress != null && lDialogProgress.isShowing()) {
						lDialogProgress.dismiss();
					}
					mResponseData = null;
					playVideoAtPosition();
				}
			});
			helper.close();
			break;
		case ACTUAL_IMAGE_DATA_CALLBACK:
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						parseForAttachment();
					} catch (Throwable e) {
						e.printStackTrace();
					}
					if (lDialogProgress != null && lDialogProgress.isShowing()) {
						lDialogProgress.dismiss();
					}
					mResponseData = null;
					multiView();
				}
			});
			helper.close();
			break;
		case ERROR_CALLBACK:
			mIsRunning = false;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
			}
			resetLoading();
			// showSimpleAlert("Error",
			// "Unable to connect.\nPlease try after some time.");
			break;
		}
		lodingCanceled = false;
		STATE = READY;
	}

	private void playVideoAtPosition() {
		if (payloadData == null || payloadData.mVideo == null) {
			return;
		}
		Intent intent = new Intent(ChannelReportActivity.this,
				VideoPlayer.class);
		intent.putExtra("BACK", "INBOX");
		intent.putExtra("MODE", "file");
		DataModel.sSelf.storeObject(DMKeys.VIDEO_DATA_TYPE,
				payloadData.mVideoType[0]);
		intent.putExtra("VIDEO_PATH", new String(payloadData.mVideo[0]));
		startActivity(intent);
	}

	public void resetLoading() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (loading != null) {
					// ProgressBar progressBar = ((ProgressBar)
					// loading.findViewById(R.id.progressbar));
					// progressBar.setVisibility(View.GONE);
					// TextView textView = ((TextView)
					// loading.findViewById(R.id.loadingtext));
					loadingView.setText(R.string.load_more);
				}
			}
		});
	}

	int firstVisiblePosition = 0;
	int lastVisiblePosition = 0;
	int existing_feed_count = 0;
	private synchronized void parseXMLData(byte[] mResponseData, final int DATA_CALLBACK) {
		try {
			if (mMainList != null)
				mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
			needToScroll = false;
			firstVisiblePosition = 0;
			lastVisiblePosition = 0;
			existing_feed_count = 0;
			int current_playing_pos = -1;
			if (feed != null && feed.entry != null && feed.entry.size() > 0) {
				if(voiceIsPlaying && mMediaAdapter != null)
					current_playing_pos = mMediaAdapter.getCurrentPlayingPosition();
				needToScroll = (mMainList.getLastVisiblePosition() >= feed.entry.size() - 1);
				if(log){
					Log.w(TAG, "parseXMLData :: ------------------------------------------------  "+new String(mResponseData));
					existing_feed_count = feed.entry.size();
					Log.w(TAG, "parseXMLData :: existing_feed_count : "+existing_feed_count);
					firstVisiblePosition = mMainList.getFirstVisiblePosition();
					Log.w(TAG, "parseXMLData :: mMainList.getFirstVisiblePosition() : "+firstVisiblePosition);
					lastVisiblePosition = mMainList.getLastVisiblePosition();
					Log.w(TAG, "parseXMLData :: mMainList.getLastVisiblePosition() : "+lastVisiblePosition);
				}else{
					//					Log.w(TAG, "parseXMLData :: ------------------------------------------------  "+new String(mResponseData));
					existing_feed_count = feed.entry.size();
					firstVisiblePosition = mMainList.getFirstVisiblePosition();
					lastVisiblePosition = mMainList.getLastVisiblePosition();
				}
			}
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			final CommunityFeedParser myXMLHandler = new CommunityFeedParser();
			xr.setContentHandler(myXMLHandler);
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(mResponseData);// xml.getBytes("UTF-8"));
			try
			{
				xr.parse(new InputSource(arrayInputStream));
				handler.post(new Runnable() {

					@Override
					public void run() {
						mMediaAdapter.notifyDataSetChanged();
						if (!mMediaAdapter.isScrolling && !voiceIsPlaying) {
							mMainList.invalidate();
							// Toast.makeText(CommunityChatActivity.this, "th",  Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				Log.e(TAG, "parseXMLData :: Exception in Parsing ==> "+new String(mResponseData));
			}finally{
				arrayInputStream.close();
			}
			final int new_feed_count = myXMLHandler.feed.entry.size();
			if(log)
				Log.w(TAG, "parseXMLData :: new_feed_count : "+new_feed_count);
			if (DATA_CALLBACK == ACTUAL_REFRESH_DATA_CALLBACK && new_feed_count <= 0) {
				feed.adminState = myXMLHandler.feed.adminState;
				feed.isAdmin =  myXMLHandler.feed.isAdmin;
				feed.isOwner =  myXMLHandler.feed.isOwner;
				if(entry != null)
					entry.adminState = myXMLHandler.feed.adminState;
				if(feed.adminState != null && feed.adminState.trim().length() > 0 && feed.isOwner == 0 && feed.isAdmin == 0
						&& !entry.ownerUsername.equalsIgnoreCase(SettingData.sSelf.getUserName()))
				{
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							setToBroadcast(feed.adminState);
						}
					});
					//Update in DB
					setChannelAdminState(feed.adminState);
				}
				return;
			}
			if (new_feed_count > 0) {
				if(DATA_CALLBACK == ACTUAL_REFRESH_DATA_CALLBACK){
					if(log)
						Log.i(TAG, "parseXMLData :: Channel refresh Data : "+new String(mResponseData));
					if(DATA_CALLBACK == CHAT_MIN_REFRESH_TIME)
						CHAT_REFRESH_TIME = CHAT_NORMAL_REFRESH_TIME;
					//					if (myXMLHandler.feed.entry.get(0).senderId == BusinessProxy.sSelf.getUserId())
					//					{
					//						if(myXMLHandler.feed.entry.get(0) != null && senderEntry != null  && senderEntry.media != null && senderEntry.media.size() > 0) 
					//							myXMLHandler.feed.entry.get(0).media = senderEntry.media;
					//					}
					//Check here if Voice is playing already, then get the position and update.
					for (int i = 0; i < new_feed_count; i++) {
						Log.w(TAG, "ParseXML :: messageId received at " + i+" : "+myXMLHandler.feed.entry.get(i).messageId);
						CCB.saveConversitionList(myXMLHandler.feed.entry.elementAt(i), myXMLHandler.feed.nexturl);
						//feed.entry.add(0,myXMLHandler.feed.entry.elementAt(i));
					}

					String[] mediaUrls = null;
					for (int i = myXMLHandler.feed.entry.size() - 1; i >= 0; i--) {
						mediaUrls = new String[myXMLHandler.feed.entry.get(i).media.size() / 8 + 1];
						for (int j = 0, k = 0; j < (myXMLHandler.feed.entry.get(i).media.size()); j += 8, k++) {
							String s = (String) myXMLHandler.feed.entry.get(i).media.elementAt(j + 1);
							if (s.equalsIgnoreCase("audio")){
								mediaUrls[k] = (String) myXMLHandler.feed.entry.get(i).media.elementAt(j + 2);
							}
						}
					}
					if(feed.adminState.equalsIgnoreCase("Mute") && myXMLHandler.feed.entry.get(0).senderId != BusinessProxy.sSelf.getUserId())
						voiceAutoPlay = true;
					else
						voiceAutoPlay = false;
					if(log)
						Log.w(TAG, "parseXML:: voiceAutoPlay : "+voiceAutoPlay);

					//Start Downloading Audio
					FileDownloader fd = new FileDownloader();
					if(mediaUrls != null && mediaUrls[0] != null){
						fd.downloadFile(null, mediaUrls, ChannelReportActivity.this);
						mediaUrls = null;
					}

					//update message id - Note that in case of multiple message '0' will have greater message ID
					//					messageId= FeedRequester.lastChannelRefreshMessageID = myXMLHandler.feed.entry.get(0).messageId;
					//					Utilities.setString(this, Constants.CH_TOP_ID, FeedRequester.lastChannelRefreshMessageID);

					messageId = myXMLHandler.feed.entry.get(0).messageId;
					//Update this message ID in Channel Message Refresh
					if(groupID!=null  && !groupID.equals("")){
						getCommunityChat(groupID, false);
					}
					if(log)
						Log.w(TAG, "parseXML:: total feed count : "+feed.entry.size());
					if(voiceAutoPlay && !voiceIsPlaying)
					{	
						if(mMediaAdapter != null)
							mMediaAdapter.setAutoPlayAtPos(feed.entry.size() - 1, true);
						if(log)
							Log.w(TAG, "parseXML:: Voice Auto Play set at : "+(feed.entry.size() - 1));
					}
					else if(voiceIsPlaying && current_playing_pos != -1){
						//Set the current voice playing position, so that this can be updated in getView
						if(log)
							Log.w(TAG, "parseXML:: New Message Added, so re-setting play postion : "+current_playing_pos);
						if(mMediaAdapter != null)
							mMediaAdapter.setPlayAtPos(current_playing_pos, true);
						current_playing_pos = -1;
					}
					//					senderEntry = null;
				}
				else if(DATA_CALLBACK == XML_DATA_CALLBACK){
					if(log)
						Log.i(TAG, "parseXMLData :: Channel First time or Load more case : "+new String(mResponseData));
					if(myXMLHandler!=null && myXMLHandler.feed!=null && myXMLHandler.feed.entry!=null)
						for (int i = 0; i < myXMLHandler.feed.entry.size(); i++) {
							//	CommunityChatDB CCB = new CommunityChatDB(this);
							CCB.saveConversitionList(myXMLHandler.feed.entry.elementAt(i), myXMLHandler.feed.nexturl);
							//feed.entry.add(0,myXMLHandler.feed.entry.elementAt(i));
						}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(groupID!=null  && !groupID.equals(""))
								getCommunityChat(groupID, false);
							mMediaAdapter.notifyDataSetChanged();
						}
					});
				}
			}
			mResponseData = null;
			//			if(new_feed_count > 0){
			//				runOnUiThread(new Runnable() {
			//					
			//					@Override
			//					public void run() {
			//						// TODO Auto-generated method stub
			//						lastListSelectedIndex = lastListSelectedIndex + new_feed_count;
			//						if(feed.adminState != null && feed.adminState.equalsIgnoreCase("Mute"))
			//						{
			//							mMainList.setSelection(lastListSelectedIndex > 10 ? (lastListSelectedIndex + 1) : lastListSelectedIndex);
			//						}
			//						else{
			//							mMainList.setSelection(lastListSelectedIndex);
			//						}
			//						mMainList.invalidate();
			//						Log.w(TAG, "parseXMLData :: After parse lastListSelectedIndex : "+lastListSelectedIndex);
			//						}
			//				});
			//			}
			if (feed != null) {
				if (vector.size() > 0) {
					int index = 0;
					Vector<Entry> vectorS = new Vector<Entry>();
					Vector<Entry> vectorL = new Vector<Entry>();
					for (int i = myXMLHandler.feed.entry.size() - 1; i >= 0
							&& index < vector.size(); i--) {
						Entry entryServer = myXMLHandler.feed.entry
								.elementAt(i);
						Entry entryLocal = vector.elementAt(index);
						if (entryServer.senderId == entryLocal.senderId) {
							vectorS.add(entryServer);
							vectorL.add(entryLocal);
							index++;
						}
					}
					for (int i = 0; i < vectorL.size(); i++)
						for (int j = 0; j < feed.entry.size(); j++) {
							Entry entryServer = feed.entry.elementAt(j);
							Entry entryLocal = vector.elementAt(i);
							if (entryLocal.messageId.equalsIgnoreCase(entryServer.messageId)) {
								Entry entryLocal2 = vectorS.elementAt(i);
								entryServer.messageId = entryLocal2.messageId;
								entryServer.createdDate = entryLocal2.createdDate;
								entryServer.messageText = entryLocal2.messageText;
								entryServer.media = entryLocal2.media;
								break;
							}
						}
					for (int i = 0; i < vectorS.size(); i++) {
						myXMLHandler.feed.entry.remove(vectorS.get(i));
					}
					for (int i = 0; i < vectorL.size(); i++) {
						vector.remove(vectorL.get(i));
					}
				}
				if (requestednextPage)
					feed.nexturl = myXMLHandler.feed.nexturl;
				feed.link = myXMLHandler.feed.link;
				feed.links = myXMLHandler.feed.links;
				feed.adminState = myXMLHandler.feed.adminState;
				if(feed.adminState != null && feed.adminState.trim().length() > 0 && feed.isOwner == 0 && feed.isAdmin == 0
						&& !entry.ownerUsername.equalsIgnoreCase(SettingData.sSelf.getUserName()))
				{
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							setToBroadcast(feed.adminState);
						}
					});
					//Update in DB
					setChannelAdminState(feed.adminState);
				}
			} else {
				feed = myXMLHandler.feed;
			}
			// requestednextPage = false;
			if (feed.entry.size() <= 0) {
				hideShowNoMessageView();
				return;
			}
			if(new_feed_count > 0){
				runOnUiThread(new Runnable() {
					public void run() {
						if (needToScroll) {
							mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
							mMainList.setSelection(feed.entry.size() - 1);
						} else {
							if(firstVisiblePosition == 0 && new_feed_count > 0)//Case of Load More - 20 or lesser messages added
								mMainList.setSelection(new_feed_count);//new_feed_count + 1
							else if(lastVisiblePosition < feed.entry.size() - 1)//New message 1, 2, 3 came in refresh 
								mMainList.setSelection(firstVisiblePosition + new_feed_count);
							else
								mMainList.setSelection(feed.entry.size() - 1);
						}
						//						mMediaAdapter.notifyDataSetChanged();
						hideShowNoMessageView();
						//					if (justin) {
						//						Log.w(TAG, "parseXMLData :: lastListSelectedIndex : "+lastListSelectedIndex);
						//						mMainList.setSelection(feed.entry.size() - 1);
						//						justin = false;
						//					}
					}
				});
			}

			//			handler.postDelayed(new Runnable() {
			//
			//				@Override
			//				public void run() {
			//					if (null != feed && null != feed.entry) {
			//						if (feed.entry.size() > 10) {
			//							startThumbnailThread(10);
			//						} else {
			//							startThumbnailThread(feed.entry.size());
			//						}
			//					}
			//				}
			//			}, 2000);
		} catch (Exception ex) {
			ex.printStackTrace();
			resetLoading();
		}
	}

	private void resetNewMessageCount()
	{
		try
		{
			Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
					new String[] { ""+entry.groupName}, null, null, null);
			if(cursor.getCount() == 1)
			{
				ContentValues values = new ContentValues();
				values.put(CommunityTable.GROUP_NEW_MSG_COUNT, 0);

				int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
						new String[] {entry.groupName});
				if(rowUpdated == 1){
					UIActivityManager.refreshChannelList = true;
					if(log)
						Log.i(TAG, "resetNewMessageCount to 0");
				}
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void setChannelAdminState(String value)
	{
		try
		{
			Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
					new String[] { ""+entry.groupName}, null, null, null);
			if(cursor.getCount() == 1)
			{
				ContentValues values = new ContentValues();
				values.put(CommunityTable.ADMIN_STATE, value);

				int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
						new String[] {entry.groupName});
				if(rowUpdated == 1){
					if(log)
						Log.i(TAG, "setChannelAdminState : "+value);
				}
			}
			cursor.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onPause() {
		//Check if First time data is not laoded and loading progress is shown, then cancel that progressa and go in BG
		//Then on resume, check if data was not laoded last time, load that again.
		boolean ringing = checkPhoneState(0);
		Log.w(TAG, "onPause :: Phone State ringing : "+ringing);
		if(!feedLoadedFirstTime)
		{
			cancelLoading();
			resetLoading();
			Log.w(TAG, "onPause :: feedLoadedFirstTime = false");
		}
		wasAppInBG = true;
		//		if(!isRunningInForeground()){
		//			//Dp not need to stop Channle Message Refresh
		////			stopTimerToRefresh();
		//			wasAppInBG = true;
		//			Log.w(TAG, "onPause :: wasAppInBG : "+wasAppInBG);
		//		}
		YelatalkApplication.am.setSpeakerphoneOn(false);
		if(ringing)
			closePlayScreen();
		super.onPause();
		lastListSelectedIndex = mMainList.getSelectedItemPosition();
	}

	private Drawable LoadImageFromWebOperations(String url) {
		try {
			if (1 == 1)
				return null;

			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Exc=" + e);
			makeToast(e.toString());
			return null;
		} catch (OutOfMemoryError e) {
			makeToast(e.toString());
			return null;
		}
	}

	public void hideShowNoMessageView() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (feed != null && feed.entry != null && feed.entry.size() <= 0) {
					((TextView) findViewById(R.community_chat.noContent)).setVisibility(View.GONE);
					((ListView) findViewById(R.community_chat.chatList)).setVisibility(View.INVISIBLE);
				} else {
					((TextView) findViewById(R.community_chat.noContent)).setVisibility(View.GONE);
					((ListView) findViewById(R.community_chat.chatList)).setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private ImageView getNewImageView(int type) 
	{
		ImageView view = new ImageView(this);
		view.setFocusable(false);
		view.setClickable(true);
		view.setOnClickListener(this);
		switch (type) {
		case MEDIA_TYPE_VIDEO:
			view.setLayoutParams(IMAGE_DIMENSION);
			//view.setImageDrawable(Drawable.createFromStream((this.getResources().openRawResource(R.drawable.media22)), null));
			break;
		case MEDIA_TYPE_IMAGE:
			view.setScaleType(ScaleType.CENTER_CROP);
			view.setLayoutParams(IMAGE_DIMENSION);
			view.setBackgroundResource(R.drawable.border);
			break;
		case MEDIA_TYPE_AUDIO:
			view.setScaleType(ScaleType.FIT_CENTER);
			view.setPadding(0, 10, 0, 0);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			view.setImageResource(R.drawable.chat_audio);
			break;
		default:
			break;
		}
		return view;
	}

	class SetThumb implements Runnable {

		ImageView imageview;

		public SetThumb(ImageView imageview) {
			this.imageview = imageview;
		}

		@Override
		public void run() {
			imageview.setImageResource(R.drawable.icon);
			imageview.invalidate();
		}
	}

	private void playFromURL(String aURL) {
		try {
			if (log)
				Log.i(TAG, "playFromURL-------------------------------->" + aURL);
			String extension = MimeTypeMap.getFileExtensionFromUrl(aURL);
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			Uri uri = Uri.parse(aURL);
			Intent player = new Intent(Intent.ACTION_VIEW, uri);
			if (mimeType.equalsIgnoreCase("3gp")) {
				player.setDataAndType(uri, "video/3gpp");
			} else {
				player.setDataAndType(uri, mimeType);
			}
			startActivity(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	boolean voiceIsPlaying;
	public boolean availableVoice = false;
	public boolean isStreamingAudio = false;

	private void playAvailableVoice(String file_path, boolean auto_play) {
		availableVoice = true;
		//		media_play_layout.setVisibility(View.VISIBLE);
		//		time_panel.setVisibility(View.VISIBLE);
		//		baradd = (SeekBar) media_play_layout.findViewById(R.id.mediavoicePlayingDialog_progressbar);
		//		baradd = (SeekBar) media_play_layout.findViewById(R.id.mediavoicePlayingDialog_progressbar);
		//		if (((ImageView) media_play_layout.findViewById(R.id.media_play)) != null)
		//			((ImageView) media_play_layout.findViewById(R.id.media_play)).setOnClickListener(playerClickEvent);
		//		total_autio_time.setVisibility(View.VISIBLE);
		//		played_autio_time.setVisibility(View.VISIBLE);
		//		total_autio_time.setText("00:00");
		//		played_autio_time.setText("00:00");
		//		openPlayScreen(file_path, auto_play, media_play_layout);
		//		openPlayScreen(file_path, auto_play, null);
		//	openPlayScreenUpdate(file_path, auto_play,mB);
	}
	boolean closePlay = false;
	public void closePlayScreen() {
		Log.w(TAG, "--------closePlayScreen-------------");
		closePlay = true;
		if(RTMediaPlayer.isPlaying()){
			RTMediaPlayer.reset();
			RTMediaPlayer.clear();
			resetProgress();
			for(int k = 0; k < feed.entry.size(); k++){
				CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
				playing_entry.isPlaying = false;
				playing_entry.isAutoPlay = false;
			}
		}
	}

	public void recycleImageView(LinearLayout view) {

		// if (view != null) {
		// for (int i = 0; i < view.getChildCount(); i++) {
		// try {
		// View v = view.getChildAt(i);
		// if (v instanceof ImageView) {
		// Drawable drawable = ((ImageView) v).getDrawable();
		// if (drawable instanceof BitmapDrawable) {
		// BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		// Bitmap bitmap = bitmapDrawable.getBitmap();
		// bitmap.recycle();
		// bitmapDrawable = null;
		// }
		// }
		// } catch (Exception e) {
		//
		// }
		// }
		//
		// }
	}

	public void myScreenName(CommunityFeed feed) {
		// System.out.println("------------media screen name-------------- : "+name);
		// BusinessProxy.sSelf.dynamicScreen = name;// "PBK" ;//LIST_BOX =
		// LIST_BOX_CONTACT;
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// BusinessProxy.sSelf.addPush.pushAdd(CommunityChatActivity.this);
		// }
		// }).start();

		String name = Feed.getAttrCode(feed.tracking);
		// System.out.println("------------media screen name-------------- : "+name);
		BusinessProxy.sSelf.dynamicScreen = name;
		BusinessProxy.sSelf.SEO = feed.seo;
		//		new Thread(new Runnable() {
		//			@Override
		//			public void run() {
		//				BusinessProxy.sSelf.addPush.noAddScreen = null;
		//				BusinessProxy.sSelf.addPush.pushAdd(CommunityChatActivity.this);
		//			}
		//		}).start();
	}

	public void openRowOptionRight(int type, final CommunityFeed feed,
			boolean menuFlag, HashMap<Integer, String> menuItems,
			final CommunityFeed.Entry entry) {// 1 right 2 left
		// HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox))
				.getWindowToken(), 0);

		if (menuFlag) {
			openActionSheet(menuItems, new OnMenuItemSelectedListener() {
				@Override
				public void MenuItemSelectedEvent(Integer selection) {

					switch (selection) {
					case 0:
						//						Intent intent = new Intent(CommunityChatActivity.this,
						//								BuddyActivity.class);
						//						intent.putExtra("from_community", true);
						//						if(entry != null)
						//							intent.putExtra("community_name", entry.groupName);
						//						intent.putExtra(BuddyActivity.SCREEN_MODE,
						//								BuddyActivity.MODE_ONLY_FRIENDS);
						//						startActivity(intent);
						break;
					case 1:
						if(entry != null){
							StringBuilder text = new StringBuilder("Leave::Name##");
							text.append(entry.groupName);
							if (BusinessProxy.sSelf.sendNewTextMessage(
									"Community manager<a:communitymgr>",
									text.toString(), true)) {
								showSimpleAlert(getString(R.string.info),
										getString(R.string.yourrequesthasbeensent));
							}
						}
						break;
					case 2:
						if (feed != null
						&& (feed.isOwner == 1 || feed.isAdmin == 1)) {
							deleteMessage(entry);
						}
						break;
					case 3:
						break;
					}
				}
			}, null);
		} else {
			openActionSheet(menuItems, new OnMenuItemSelectedListener() {
				@Override
				public void MenuItemSelectedEvent(Integer selection) {

					switch (selection) {
					case 0:
						//						Intent intent = new Intent(CommunityChatActivity.this,
						//								BuddyActivity.class);
						//						intent.putExtra("from_community", true);
						//						if(entry != null)
						//							intent.putExtra("community_name", entry.groupName);
						//						intent.putExtra(BuddyActivity.SCREEN_MODE,
						//								BuddyActivity.MODE_ONLY_FRIENDS);
						//						startActivity(intent);
						break;
					case 1:
						StringBuilder text = new StringBuilder("Leave::Name##");
						if(entry != null)
							text.append(entry.groupName);
						if (BusinessProxy.sSelf.sendNewTextMessage(
								"Community manager<a:communitymgr>",
								text.toString(), true)) {
							showSimpleAlert(getString(R.string.info),
									getString(R.string.yourrequesthasbeensent));
						}
						break;
					case 2:
						text = new StringBuilder("Report::grpN##");
						if(entry != null)
							text.append(entry.groupName);
						text.append("::gId##");
						text.append(feed.groupId);
						if (BusinessProxy.sSelf.sendNewTextMessage(
								"Community Manager<a:communitymgr>",
								text.toString(), true)) {

							new Thread(new Runnable() {
								public void run() {
									showSimpleAlert(
											getString(R.string.info),
											getString(R.string.yourrequesthasbeensent));
								}
							}).start();//
						}
						break;
					case 3:
						break;
					}
				}
			}, null);
		}
	}

	public void openRowOption(int type, final Entry entry, boolean menuFlag,
			HashMap<Integer, String> menuItems) {// 1 right 2 left
		// HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox))
				.getWindowToken(), 0);

		/*
		 * menuItems.put(0, "Bookmark message"); menuItems.put(1,
		 * "Delete message"); menuItems.put(2, "Cancel");
		 * 
		 * menuItems.put(0, "Bookmark message"); menuItems.put(1,
		 * "Delete message"); menuItems.put(2, "Report message");
		 * menuItems.put(3, "Report person"); menuItems.put(4, "Add as friend");
		 * menuItems.put(5, "Cancel");
		 */

		if (menuFlag) {
			openActionSheet(menuItems, new OnMenuItemSelectedListener() {
				@Override
				public void MenuItemSelectedEvent(Integer selection) {

					switch (selection) {
					case -1:

						break;
					case 0:
						findViewById(R.community_chat.replyLayout)
						.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.community_chat.replyName))
						.setText("@ " + entry.senderName);
						replyToSender = true;
						replyMessageId = entry.messageId;
						replyTo = entry.senderName;
						replyToFirstName = entry.firstName;
						replyToLastName = entry.lastName;
						break;
					case 1:

						//						DataModel.sSelf.storeObject(DMKeys.COMPOSE_MESSAGE_OP,
						//								Constants.MSG_OP_REPLY);
						//						DataModel.sSelf.storeObject(
						//								DMKeys.COMPOSE_REFERENCE_MESSAGE_ID,
						//								entry.messageId);
						//						// TODO Check with IPhone...
						//						DataModel.sSelf.storeObject(
						//								DMKeys.COMPOSE_INBOX_MESSAGE_CURRENT_STATUS, 0);
						//						// DataModel.sSelf.storeObject(DMKeys.COMPOSE_INBOX_MESSAGE_CURRENT_STATUS,
						//						// entry.messageState);
						//						String userName = "";
						//						if (entry.firstName == null
						//								|| entry.firstName.length() <= 0
						//								|| entry.lastName == null
						//								|| entry.lastName.length() <= 0)
						//							userName = entry.senderName + "<"
						//									+ entry.senderName + ">;";
						//						else
						//							userName = entry.firstName + " " + entry.lastName
						//							+ "<" + entry.senderName + ">;";
						//						DataModel.sSelf.storeObject(
						//								DMKeys.INBOX_REPLY_REPLYALL, userName);
						//						Intent intent = new Intent(CommunityChatActivity.this,
						//								ComposeActivity.class);
						//						intent.putExtra("PAGE", (byte) 1);
						//						startActivity(intent);
						break;
					case 2:// report message

						//						DataModel.sSelf.storeObject(DMKeys.XHTML_URL, null);
						//						DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, entry.senderName);
						//						intent = new Intent(CommunityChatActivity.this, WebviewActivity.class);
						//						intent.putExtra("from_community", true);
						//						intent.putExtra("PAGE", (byte) 1);
						//						startActivity(intent);
						break;
					case 3:
						if (feed != null
						&& (feed.isOwner == 1 || feed.isAdmin == 1)) {
							deleteMessage(entry);
						}

						break;
					case 4:// send friend request

						break;
					case 5:
						break;
					}

				}
			}, null);
		} else {
			openActionSheet(menuItems, new OnMenuItemSelectedListener() {
				@Override
				public void MenuItemSelectedEvent(Integer selection) {

					switch (selection) {
					case -1:
						Toast.makeText(ChannelReportActivity.this,
								"Comeing soon", Toast.LENGTH_SHORT).show();
						break;
					case 0:
						findViewById(R.community_chat.replyLayout)
						.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.community_chat.replyName))
						.setText("@ " + entry.senderName);
						replyToSender = true;
						replyMessageId = entry.messageId;
						replyTo = entry.senderName;
						replyToFirstName = entry.firstName;
						replyToLastName = entry.lastName;
						break;
					case 1:

						//						DataModel.sSelf.storeObject(DMKeys.COMPOSE_MESSAGE_OP,
						//								Constants.MSG_OP_REPLY);
						//						DataModel.sSelf.storeObject(
						//								DMKeys.COMPOSE_REFERENCE_MESSAGE_ID,
						//								entry.messageId);
						//						// TODO Check with IPhone...
						//						DataModel.sSelf.storeObject(
						//								DMKeys.COMPOSE_INBOX_MESSAGE_CURRENT_STATUS, 0);
						//						// DataModel.sSelf.storeObject(DMKeys.COMPOSE_INBOX_MESSAGE_CURRENT_STATUS,
						//						// entry.messageState);
						//						String userName = "";
						//						if (entry.firstName == null
						//								|| entry.firstName.length() <= 0
						//								|| entry.lastName == null
						//								|| entry.lastName.length() <= 0)
						//							userName = entry.senderName + "<"
						//									+ entry.senderName + ">;";
						//						else
						//							userName = entry.firstName + " " + entry.lastName
						//							+ "<" + entry.senderName + ">;";
						//						DataModel.sSelf.storeObject(
						//								DMKeys.INBOX_REPLY_REPLYALL, userName);
						//						Intent intent = new Intent(ChannelReportActivity.this,
						//								ComposeActivity.class);
						//						intent.putExtra("PAGE", (byte) 1);
						//						startActivity(intent);
						break;
					case 2:// report message
						//						DataModel.sSelf.storeObject(DMKeys.COMPOSE_MESSAGE_OP,
						//								Constants.MSG_OP_FWD);
						//						DataModel.sSelf.storeObject(
						//								DMKeys.COMPOSE_REFERENCE_MESSAGE_ID,
						//								entry.messageId);
						//						// TODO Check with IPhone...
						//						DataModel.sSelf.storeObject(
						//								DMKeys.COMPOSE_INBOX_MESSAGE_CURRENT_STATUS, 0);
						//						// DataModel.sSelf.storeObject(DMKeys.COMPOSE_INBOX_MESSAGE_CURRENT_STATUS,
						//						// entry.messageState);
						//						intent = new Intent(CommunityChatActivity.this,
						//								ComposeActivity.class);
						//						intent.putExtra("PAGE", (byte) 1);
						//						startActivity(intent);
						break;
					case 3:
						DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								StringBuilder text = new StringBuilder(
										"ymsg::msgid##");
								text.append(entry.messageId);
								text.append("::isMessageId##true");
								if (BusinessProxy.sSelf.sendNewTextMessage(
										"Report Abuse<a:rtmoderator>",
										text.toString(), true)) {
									new Thread(new Runnable() {
										public void run() {
											showSimpleAlert(
													getString(R.string.info),
													getString(R.string.yourrequesthasbeensent));
										}
									}).start();//

								}
							}
						};
						showAlertMessage(
								getString(R.string.confirm),
								getString(R.string.do_you_really_want_to_delete_this_community),
								new int[] { DialogInterface.BUTTON_POSITIVE,
									DialogInterface.BUTTON_NEGATIVE },
									new String[] { getString(R.string.yes),
									getString(R.string.no) },
									new DialogInterface.OnClickListener[] {
									exitHandler, null });

						break;
					case 4:// send friend request

						//						DataModel.sSelf.storeObject(DMKeys.XHTML_URL, null);
						//						DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
						//								entry.senderName);
						//						intent = new Intent(CommunityChatActivity.this,
						//								WebviewActivity.class);
						//						intent.putExtra("from_community", true);
						//						intent.putExtra("PAGE", (byte) 1);
						//						startActivity(intent);
						break;
					case 5:
						if (feed != null
						&& (feed.isOwner == 1 || feed.isAdmin == 1)) {
							deleteMessage(entry);
						}
						break;
					}

				}

			}, null);
		}

		// menuItems.d

	}

	private void deleteMessage(final Entry entry) {

		DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// blockmsg::msgid##2150089691::isMessageId##true::G##Y::OP##1
				StringBuilder text = new StringBuilder("blockmsg::msgid##");
				text.append(entry.messageId);
				text.append("::isMessageId##true::G##Y::OP##1");
				if (BusinessProxy.sSelf.sendNewTextMessage(
						"Report Abuse<a:rtmoderator>", text.toString(), true)) {
					new Thread(new Runnable() {
						public void run() {
							showSimpleAlert(getString(R.string.info),
									getString(R.string.yourrequesthasbeensent));
						}
					}).start();
				}
				feed.entry.removeElement(entry);
				mMediaAdapter.notifyDataSetChanged();
			}
		};

		showAlertMessage(
				getString(R.string.confirm),
				getString(R.string.do_you_really_want_to_delete_this_community),
				new int[] { DialogInterface.BUTTON_POSITIVE,
					DialogInterface.BUTTON_NEGATIVE }, new String[] {
					getString(R.string.yes), getString(R.string.no) },
					new DialogInterface.OnClickListener[] { exitHandler, null });
		// Toast.makeText(CommunityChatActivity.this, "wait...",
		// Toast.LENGTH_SHORT).show();
	}

	private void setEmojiconFragment(boolean useSystemDefault) {
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.emojicons,
				EmojiconsFragment.newInstance(useSystemDefault))
				.commit();
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment
		.backspace((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox));
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment
		.input((com.rockerhieu.emojicon.EmojiconEditText) findViewById(R.community_chat.msgBox),
				emojicon);

	}

	OnClickListener profileClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				stopTimerToRefresh();
				if(!feedLoadedFirstTime)
				{
					cancelLoading();
					resetLoading();
					switchedToScreenWhileLoading = true;
					Log.i(TAG, "profileClick : onClick() :: CommunityProfileNewActivity called.");
				}
				DataModel.sSelf.storeObject(DMKeys.ENTRY+ "COMM", entry);
				Intent intentC = new Intent(ChannelReportActivity.this, CommunityProfileNewActivity.class);
				intentC.putExtra("group_name", entry.groupName);
				intentC.putExtra("tags_name", entry.tags);
				intentC.putExtra("group_id", entry.groupId);
				if(entry.description != null)
					intentC.putExtra("group_desc", entry.description);
				else
					intentC.putExtra("group_desc", " ");
				if(entry.thumbUrl != null)
					intentC.putExtra("group_pic", entry.thumbUrl);
				else
					intentC.putExtra("group_pic", " ");
				Log.i(TAG, "onOptionsItemSelected :: ChannelName : "+entry.groupName+", ChannelID : "+entry.groupId);
				startActivity(intentC);
				//finish();

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	OnClickListener userProfileClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				String userName = (String) v.getTag();
				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, userName);
				Intent intent = new Intent(ChannelReportActivity.this, ProfileViewActivity.class);
				intent.putExtra("USERID", userName);
				intent.putExtra("CallFrom",1);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	public void joinLeaveCommunity(){
		StringBuilder buffer = new StringBuilder(
				"msgto:Community Manager<a:communitymgr>?text="+entry.joinOrLeave+"::Name##"+entry.groupName);
		clickHandler(buffer.toString(),false);

		if(entry.joinOrLeave.equalsIgnoreCase("join"))

			entry.joinOrLeave = "leave" ;

		else

			entry.joinOrLeave = "join" ;
		refershFollowUnfollow();

	}

	private void refershFollowUnfollow() {
		// TODO Auto-generated method stub
		if(entry.joinOrLeave.equalsIgnoreCase("join")){

			linearlyout_read.setVisibility(View.VISIBLE);
			linearlyout_write.setVisibility(View.GONE);
			//			findViewById(R.id.menu).setVisibility(View.GONE);
			((ImageView)findViewById(R.id.attachement)).setVisibility(View.GONE);

		}else
		{
			linearlyout_read.setVisibility(View.GONE);
			linearlyout_write.setVisibility(View.VISIBLE);
			//			findViewById(R.id.menu).setVisibility(View.VISIBLE);
			((ImageView)findViewById(R.id.attachement)).setVisibility(View.VISIBLE);
		}
	}
	private void setToBroadcast(String state) {
		// TODO Auto-generated method stub
		if(state != null){
			if(state.equalsIgnoreCase("mute")){
				layout_broadcast.setVisibility(View.VISIBLE);
				linearlyout_write.setVisibility(View.GONE);
				//			findViewById(R.id.menu).setVisibility(View.GONE);
				((ImageView)findViewById(R.id.attachement)).setVisibility(View.GONE);
				Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
				t.setScreenName("Channel Broadcast Screen");
				t.set("&uid",""+BusinessProxy.sSelf.getUserId());
				t.send(new HitBuilders.AppViewBuilder().build());
			}else if(state.equalsIgnoreCase("active")){
				layout_broadcast.setVisibility(View.GONE);
				linearlyout_write.setVisibility(View.VISIBLE);
				//			findViewById(R.id.menu).setVisibility(View.VISIBLE);
				((ImageView)findViewById(R.id.attachement)).setVisibility(View.VISIBLE);
			}
		}
	}

	public void onNotification(int requestCode, String sender, String msg)
	{
		switch(requestCode){
		case 9:
			showNewMessageAlert(sender, msg);
			break;
		}
	}

	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileDownloadResposne(View view, int[] type, String[] file_urls,
			String[] file_paths) {
		// TODO Auto-generated method stub
		for(int i = 0; i < file_urls.length; i++)
		{
			if(log)
				Log.i(TAG, "onFileDownloadResposne :: file downloaded from url at " + i + " : "+file_urls[i]+", filename : "+file_paths[i]);
			switch (type[i]) {
			case MEDIA_TYPE_VIDEO:
				break;
			case MEDIA_TYPE_IMAGE:
				getNewImageView(MEDIA_TYPE_IMAGE);
				break;
			case MEDIA_TYPE_AUDIO:
				if(getAutoplay() && voiceAutoPlay && entry.adminState.equalsIgnoreCase("mute"))
				{
					if(!voiceIsPlaying){
						if(file_paths[i] != null && file_paths[i].trim().length() > 0){
							if(wasAppInBG){
								//								playAvailableVoice(file_paths[i], true);
								if(log)
									Log.v(TAG, "onFileDownloadResposne :: App in BG mode, so playing from here!!");
								viewHolder.openPlayScreen(file_paths[i]);
							}
							voiceIsPlaying = true;
							availableVoice = true;
							//							for(int k = 0; k < feed.entry.size(); k++){
							//								CommunityFeed.Entry playing_entry = (CommunityFeed.Entry)feed.entry.elementAt(k);
							//								Log.v(TAG, "onFileDownloadResposne :: position : "+k+", isPlaying : "+playing_entry.isPlaying+", isAutoPlay : "+playing_entry.isAutoPlay);
							//							}
							//							if(mMediaAdapter != null)
							//								mMediaAdapter.setAutoPlayAtPos(feed.entry.size() - 1, true);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if(mMediaAdapter != null)
										mMediaAdapter.notifyDataSetChanged();
								}
							});
						}
						voiceAutoPlay = false;
					}
				}
				//				else{
				//					if(view != null)
				//						view.setTag(file_paths[i]);
				//				}
				break;
			default:
				break;
			}
		}
		runOnUiThread(new Runnable() {
			public void run() {
				hideShowNoMessageView();
				// int lastPos = feed.entry.size();
				// mMainList.setAdapter(mMediaAdapter);
				// if (DATA_CALLBACK != ACTUAL_REFRESH_DATA_CALLBACK)
				if (needToScroll) {
					mMainList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
					mMainList.setSelection(feed.entry.size());
				} else {
					mMainList.setSelection(lastListSelectedIndex);
				}
				if (justin) {

					mMainList.setSelection(feed.entry.size() - 1);
					justin = false;
				}

			}
		});
	}
	private boolean isFileExists(String file_name)
	{
		String dir = Environment.getExternalStorageDirectory().getPath();
		String fullPath = new StringBuffer(dir).append('/').append("YelaTalk").append('/').append(file_name).toString();
		return new File(fullPath).isFile();
	}
	private Uri getImageLocalPathUri(String file_name)
	{
		String dir = Environment.getExternalStorageDirectory().getPath();
		String fullPath =new StringBuffer(dir).append('/').append(file_name).toString();
		return Uri.parse(fullPath);
	}
	private String getImageLocalPath(String file_name)
	{
		String dir = Environment.getExternalStorageDirectory().getPath();
		String fullPath =new StringBuffer(dir).append('/').append("YelaTalk").append('/').append(file_name).toString();
		return fullPath;
	}
	//-------------------------------------------------------------------
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
	//	private final int RECORDING_TIME = 5 * 60;
	public void showAudioDialog(final byte TYPE){
		progress = 0;
		isCancelClick = false;
		dialog = new Dialog(ChannelReportActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.player_dialog);
		//dialog.setCancelable(true);
		final Runnable wheelThread = new Runnable() 
		{
			public void run() {
				wheelRunning = false;
				while(!wheelRunning)
				{
					if(progress < Constants.RECORDING_TIME)
					{
						isTimeOver = false;
						wheelProgress.incrementProgress(Constants.RECORDING_TIME);
						progress++;
						if(TYPE == PLAY_MODE)
							setPlayLeftTime(mVoiceMedia.getMediaDuration(), mVoiceMedia.getCurrentMediaTime());
						else if(TYPE == RECORDING_MODE){
							//							recordLeftTime((360 - progress)/3);
							recordLeftTime(Constants.RECORDING_TIME - progress);
						}
					}
					else
					{
						isTimeOver = true;
						wheelRunning = true;
						iCurrentState = UI_STATE_IDLE;
						ChannelReportActivity.this.runOnUiThread(new Runnable() {

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
				mVoiceMedia.stop();
				payloadData = null;
				RTMediaPlayer.reset();
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
				wheelProgress = null; 
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
			mVoiceMedia = new VoiceMedia(ChannelReportActivity.this, this);
		mVoiceMedia.startRecording(Constants.RECORDING_TIME);
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

	public void showDialogList(Vector abc){/*
		AlertDialog.Builder builder = new AlertDialog.Builder(ChannelReportActivity.this);
		//    builder.setTitle(R.string.option);
		builder.setItems(R.array.report_options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item){
				case 0:

					break;
				case 1:

					break;
				}

		}
	});
		AlertDialog alert = builder.create();
		alert.show();
	 */
		AlertDialog alertDialogStores;
		ArrayAdapter adapter = new ArrayAdapter(this, R.layout.reportlistrow, abc);
		// create a new ListView, set the adapter and item click listener
		ListView listViewItems = new ListView(this);
		listViewItems.setAdapter(adapter);
		//  listViewItems.setOnItemClickListener(new OnItemClickListenerListViewItem());
		/*   listViewItems.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, abc.userName);
						Intent intent = new Intent(ChannelReportActivity.this, ProfileViewActivity.class);
						intent.putExtra("USERID", participantInfo.userName);
						intent.putExtra("CallFrom",1);
						startActivity(intent);
					}
				});
		 */		           alertDialogStores = new AlertDialog.Builder(ChannelReportActivity.this)
		 .setView(listViewItems)
		 .setTitle("Reported by:")
		 .show();

	}
	// MANOJ 

	ProgressDialog reportDialog;
	public class ReportedRequest extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			reportDialog = ProgressDialog.show(ChannelReportActivity.this, "", getString(R.string.loading_dot), true);
			reportDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			String strData = postDataOnServer_res(url, entry.groupName, "",0);
			if (strData != null  && (strData.trim().indexOf("1") != -1)) {
			//	parserReportedMessage(strData);
				return strData;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.i(TAG, "DeleteRequest :: onPostExecute : result : "+result);
			if(result != null){
				//deleteTextLocalMessage(result);
				parserReportedMessage(result);
			}
			else
				Toast.makeText(ChannelReportActivity.this, getString(R.string.error),  Toast.LENGTH_SHORT).show();
			reportDialog.dismiss();
		}
	}
	private void parserReportedMessage(String strData) {
		if(feed!=null){
		}else
		{
			feed = new CommunityFeed();

		}

		try {
			JSONObject mainStr =  new JSONObject(strData);
			if(strData.contains("channelReportedMessageFeed"))
			{
				String channelReportedMessageFeed = mainStr.getString("channelReportedMessageFeed");
				JSONObject channelReportedMessageFeedObj =  new JSONObject(channelReportedMessageFeed);
				feed.groupId=channelReportedMessageFeedObj.getInt("groupId");
				feed.nexturl = null;
				if(channelReportedMessageFeedObj.has("nextUrl")){
				feed.nexturl = channelReportedMessageFeedObj.getString("nextUrl");
				loadingView.setVisibility(View.VISIBLE);
				}else
				{
					loadingView.setVisibility(View.GONE);
				}
				if(channelReportedMessageFeed.contains("channelReportedMessageList")){
					JSONArray channelReportedMessageListArray = channelReportedMessageFeedObj.getJSONArray("channelReportedMessageList");

					// looping through All entry
					for (int i = 0; i < channelReportedMessageListArray.length(); i++) {
						CommunityFeed.Entry entry = new CommunityFeed.Entry();
						JSONObject row = channelReportedMessageListArray.getJSONObject(i);
						entry.groupId = row.getInt("groupId");
						entry.groupName = row.getString("groupName");
						entry.messageId = row.getString("messageId");
						entry.createdDate = row.getString("createdDate");
						entry.senderId = row.getInt("senderId");
						entry.senderName = row.getString("senderUsername");
						entry.firstName = row.getString("senderFirstName");
						entry.lastName = row.getString("senderLastName");
						entry.reportCount = row.getInt("reportCount");
						entry.imgThumbUrl = row.getString("senderThumbUrl");
						entry.imgUrl = row.getString("senderProfileUrl");
						//entry.thumbUrl = group_pic;

						if(row.has("text")){
							entry.messageText = row.getString("text");
						}

						JSONArray mediaContentUrlListArray = row.getJSONArray("mediaContentUrlList");
						// looping through All entry
						Vector media = new Vector<String>();
						for (int j = 0; j < mediaContentUrlListArray.length(); j++) {
							JSONObject row_media = mediaContentUrlListArray.getJSONObject(j);

							media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
							String mediaType = row_media.getString("contentType");
							if(mediaType.equals("image")){
								media.add("image");
							}else if(mediaType.equals("video")){
								media.add("video");
							}
							else if(mediaType.equals("audio")){
								media.add("audio");
							}
							String contentUrl = row_media.getString("contentUrl");
							media.add(contentUrl);
							media.add("normal");

							if(mediaType.equals("image") || mediaType.equals("video"))
							{
								media.add("http://schemas.rocketalk.com/2010#collection.mediaurl");
								if(mediaType.equals("image")){
									media.add("image");
								}else if(mediaType.equals("video")){
									media.add("video");
								}
								media.add(row_media.getString("thumbUrl"));
								media.add("thumb");
							}
						}
						entry.media = media; 
						JSONArray reportedByListArray = row.getJSONArray("reportedByList");
						// looping through All entry
						String reportedby = "";
						Vector<ReportedByUser> ReportedByUser = new Vector<ReportedByUser>();
						for (int k = 0; k < reportedByListArray.length(); k++) {
							ReportedByUser RBU = new ReportedByUser();
							JSONObject row_reportedByList = reportedByListArray.getJSONObject(k);
							RBU.firstName=row_reportedByList.getString("firstName");
							RBU.lastName =row_reportedByList.getString("lastName");
							RBU.userId = row_reportedByList.getString("userId");
							RBU.userName = row_reportedByList.getString("userName");
							reportedby = reportedby + row_reportedByList.getString("firstName")+ row_reportedByList.getString("lastName") + ", ";
							ReportedByUser.add(RBU);
						}
						if(reportedby.endsWith(", ")){
							reportedby  = reportedby.substring(0, reportedby.length() - 2);
						}
						entry.reportedBy = reportedby;
						entry.ReportedByUser = ReportedByUser;
						feed.entry.add(entry);
					}
				}
				if(entry !=null){
					DataModel.sSelf.storeObject("FEED_REPORT", feed);

					//ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
					//if(mngr.)

					/*Intent intent = new Intent(.this, ChannelReportActivity.class);
					intent.putExtra("SYSTEM_MSG_VIEW_REPORT", true);
					UIActivityManager.systemMessageReportAction = true;
					if(deleteMessageAPI != null && deleteMessageAPI.length() > 0){
							intent.putExtra("DELETE_MESSAGE_URL", "");
							intent.putExtra("DELETE_MESSAGE_ID", -1);
					startActivity(intent);*/
					mMediaAdapter.notifyDataSetChanged();
					if (!mMediaAdapter.isScrolling && !voiceIsPlaying) {
						mMainList.invalidate();
					}
					 if (loadingView.getText().toString()
							 .equalsIgnoreCase(getString(R.string.loading_dot))) {
						// requestednextPage = true;
						 loadingView.setText(getString(R.string.load_more));
					 }
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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



}