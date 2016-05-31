package com.kainat.app.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.PowerManager.WakeLock;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.kainat.util.AppUtil;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.YelatalkApplication.TrackerName;
import com.kainat.app.android.adaptor.ConversationsAdapter;
import com.kainat.app.android.adaptor.ConversiotionAdaptorInf;
import com.kainat.app.android.adaptor.MessageViewHolder;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.MediaPost;
import com.kainat.app.android.bean.MediaPost.MediaContent;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.bean.ParticipantInfo;
import com.kainat.app.android.bean.ShiftNewMessage;
import com.kainat.app.android.bean.UserStatus;
import com.kainat.app.android.bean.VideoUrl;
import com.kainat.app.android.constanst.PictureTypes;
import com.kainat.app.android.constanst.VideoTypes;
import com.kainat.app.android.constanst.VoiceTypes;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.ComposeService;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.gif.GifDecoderView;
import com.kainat.app.android.gif.GifMovieView;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.LandingPageTable;
import com.kainat.app.android.helper.MediaPostTable;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.uicontrol.MultiPhotoViewer;
import com.kainat.app.android.uicontrol.StreemMultiPhotoViewer;
import com.kainat.app.android.util.CompressImage;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.FileDownloadResponseHandler;
import com.kainat.app.android.util.GalleryNavigator;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.InboxTblObject;
import com.kainat.app.android.util.KeyValue;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class ConversationsActivity extends UIActivityManager implements
		HttpSynchInf, ThumbnailReponseHandler, OnClickListener,
		OnItemClickListener, ConversiotionAdaptorInf, OnScrollListener,
		FileDownloadResponseHandler,
		EmojiconGridFragment.OnEmojiconClickedListener,
		EmojiconsFragment.OnEmojiconBackspaceClickedListener {

	public static String CONVERSATIONID = "";
	private static final String TAG = ConversationsActivity.class
			.getSimpleName();
	public Handler handler = new Handler(Looper.getMainLooper());
	private static Map<Integer, SoftReference<Spanned>> spannedContent;
	// private SoftReference<Spanned> spannedContent = new
	// SoftReference<Spanned>(bitmap);
	public static final int ADDCONVERSATIONS = 1000;
	private static final int CONTENT_ID_VOICE = 7000;
	private static final int CONTENT_ID_PICTURE = 8000;
	private static final int CONTENT_ID_VIDEO = 9000;
	private static final byte POSITION_PICTURE = 0;
	private static final byte POSITION_CAMERA_PICTURE = 1;
	private static final byte POSITION_VOICE = 2;
	private static final byte POSITION_VIDEO = 3;
	private static final byte POSITION_MULTI_SELECT_PICTURE = 4;
	private static final byte POSITION_PICTURE_RT_CANVAS = 5;
	boolean clickLeftRightCheck = false;
	private final byte MEDIA_TYPE_IMAGE = 1;
	private final byte MEDIA_TYPE_VIDEO = 2;
	public boolean availableVoice = false;
	private String cameraImagePath = null;
	private String mVoicePath, mVideoPath;
	private Vector<String> mPicturePath = new Vector<String>();
	private Vector<String> mPicturePathCanvas = new Vector<String>();
	private Vector<Integer> mPicturePathId = new Vector<Integer>();
	private Vector<Integer> mPicturePathId_int = new Vector<Integer>();
	private int[] imagePathPos;
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	// MyHorizontalScrollView scrollView;
	View menuLeft, menuRight;
	boolean frontCam, rearCam;
	private VoiceMedia mVoiceMedia;
	private DisplayImageOptions options;
	// int idsReport[] = new int[] { 1 };
	// String idsNameReport[] = new String[] {
	// "You can attach video of up to 10MB, about 20-25 seconds. For longer videos, please use a lower resolution setting."
	// };

	// View app;
	ImageView btnSlideLeft, btnSlideRight;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 200;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	boolean menuOut = false;
	public static ScrollView leftMenu;
	ListView listViewActivity;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	ConversationsAdapter activityAdapter;
	int idsOptions[] = new int[] { 1, 2, 3, 4 };
	String idsNameOptions[] = new String[] { "Users", "Community", "Media",
			"Cancle" };

	private static final byte ID_ADD_TO_CONVERSATION = 101;
	private static final byte ID_LEAVE_CONVERSATION = 102;
	private static final byte ID_UPDATE_SUBJECT = 103;
	private static final byte DELETE_CONVERSATION = 104;
	private static final int ID_CANCEL = 1004;
	// ,"Delete All"

	ProgressBar loadingChat;
	GalleryNavigator navi;
	// 9031160569
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
	LinearLayout media_play_layout;
	Dialog dialog;
int count_inx_globle = 0;
	SeekBar baradd;
	View chat_panel_more_option, chat_panel_more_option_emo;
	TextView total_autio_time, played_autio_time;
	public boolean isAudio = false;
	EmojiconTextView title;
	View load_prevoius_message;
	// FrameLayout conversations_screen_frame_layout
	ConversationList conversationList;
	ViewFlipper imageFrame;
	EmojiconEditText messageBox;
	private boolean isShowSmiley = false;
	FrameLayout smileyView = null;
	String actionUser = "";
	boolean nonFriendMessage = false;
	TextView new_message, new_message_info, load_prevoius_message2;
	View new_message_layout;
	boolean allLeft = false;
	private MessageAdapter mMessageAdapter;
	public ArrayList<String> animationFileName = new ArrayList<String>();
	public ArrayList<String> animationFileTrans = new ArrayList<String>();
	public ArrayList<String> animationSound = new ArrayList<String>();
	public ArrayList<String> animationImageView = new ArrayList<String>();
	public ArrayList<String> animationTagValue = new ArrayList<String>();
	// public parentFeed currentAnimData=null ;
	// MediaPlayer m = new MediaPlayer();
	GridView gridView;
	// private String mVoicePath;
	LinearLayout layoutMenu;
	// private VoiceMedia mVoiceMedia;
	public static String gifpath = "/RockeTalk/.GifImage/";
	Button play, select;
	boolean viewMenu = false;
	int currentIndexId = -1;
	private TextView image_count, image_count_dodle;
	private String titleName;
	final Pattern SUBJECT_PATTERN = Pattern
			.compile("^[a-zA-Z0-9@.#$%^&* _&\\\\]+$");
	// StringBuffer UnreadMsgCount = new StringBuffer();
	Hashtable<Integer, CImageView> images = new Hashtable<Integer, CImageView>();
	ProgressDialog rTDialog;
	// For MUC
	String mucFileId;
	String mucSubject;
	private List<String> mImagesPath = new ArrayList<String>();
	private static final byte MUC_CAMERA = 11;
	private static final byte MUC_PIC = 12;
	private static final byte MUC_PIC_CROP = 13;
	CImageView mucPic;
	ImageView mucFullPic;
	private String mucPicUrl = "http://" + Urls.TEJAS_HOST
			+ "/rtMediaServer/get/$MUC_FILE_ID$.jpg";
	private String mucUrlFromInbox;
	private ConversationsActivity mContext;
	private String mucGroupPic;
	ImageDownloader imageManager;

	public int measureCellWidth(Context context, View cell) {

		// We need a fake parent
		FrameLayout buffer = new FrameLayout(context);
		android.widget.AbsListView.LayoutParams layoutParams = new android.widget.AbsListView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		buffer.addView(cell, layoutParams);

		cell.forceLayout();
		cell.measure(1000, 1000);

		int width = cell.getMeasuredWidth();

		buffer.removeAllViews();

		return width;
	}

	// GestureDetector gestureDetector;
	// private LandingPageDatabaseHelper database;
	// private SQLiteDatabase sqlDB ;
	boolean fromOnCreate = false;
	PowerManager powerManager;
	WakeLock wakeLock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		// //Stop channel Refresh and set refresh and set time to 2 secs
		stopChannelRefresh();
		RefreshService.setRefreshTime(2000);

		fromOnCreate = true;
		options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.border_gray)
				.showImageForEmptyUri(R.drawable.border_gray).cacheInMemory()
				.cacheOnDisc().resetViewBeforeLoading().build();
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
//		if (BusinessProxy.sSelf == null || !BusinessProxy.sSelf.isRegistered()) {
//			// Logger.conversationLog("AnimFeed : ", "====Oncreate Call=====");
//			finish();
//			startActivity(new Intent(ConversationsActivity.this,
//					SplashActivity.class));
//			return;
//		}
		conversationList = (ConversationList) DataModel.sSelf
				.removeObject(DMKeys.CONVERSATION_SELECTED_LIST);
		removeMeIfILeft();

		if (conversationList != null
				&& conversationList.participantInfo != null
				&& conversationList.participantInfo.size() > 0) {
			for (int i = 0; i < conversationList.participantInfo.size(); i++) {
				ParticipantInfo participantInfo = conversationList.participantInfo
						.get(i);

				ImageView temp = new ImageView(ConversationsActivity.this);
				ImageDownloader imageManager = new ImageDownloader();
				imageManager.download(participantInfo.userName, temp,
						ConversationsActivity.this);
			}
		}

		// isFirendInList

		ParticipantInfo otherParticipantInfo = null;
		try {
			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				for (int i = 0; i < conversationList.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = conversationList.participantInfo
							.get(i);

					if (participantInfo.userName == null)
						continue;
					if (participantInfo.userName
							.equalsIgnoreCase(SettingData.sSelf.getUserName())) {

					} else {
						otherParticipantInfo = participantInfo;

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (conversationList != null
				&& 0 < (conversationList.notificationFlags & InboxTblObject.NOTI_NON_FRIEND_MSG)
				&& conversationList.isGroup == 0
				&& (otherParticipantInfo != null
						&& otherParticipantInfo.userName != null && !BusinessProxy.sSelf
							.isFirendInList(otherParticipantInfo.userName)))
			nonFriendMessage = true;
		checkAndAddMore();
		initFriendMessage();
		//Add Google Analytics
		Tracker t = ((YelatalkApplication) getApplication()).getTracker(TrackerName.APP_TRACKER);
        t.setScreenName("Conversations Screen");
        t.set("&uid",""+BusinessProxy.sSelf.getUserId());
        t.send(new HitBuilders.AppViewBuilder().build());
        
       
        
        powerManager = (PowerManager)ConversationsActivity.this.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");   
		
		    	
		
	}
	@Override
	protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        GoogleAnalytics.getInstance(ConversationsActivity.this).reportActivityStart(this);
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        GoogleAnalytics.getInstance(ConversationsActivity.this).reportActivityStop(this);
    }

	private void stopChannelRefresh() {
		// Stop Channel Refresh
		// Set refresh time to 10 secs.
		UIActivityManager.startChannelRefresh = false;
		RefreshService.setRefreshTime(10000);
	}

	private void startChannelRefresh() {
		if (!UIActivityManager.startChannelRefresh)
			UIActivityManager.startChannelRefresh = true;
	}

	@Override
	public void onBackPressed() {
		updateReadMessagereceiptToDBAndServer();
		if (attachment_panel.getVisibility() == View.VISIBLE) {
			attachment_panel.setVisibility(View.INVISIBLE);
			((ImageView) findViewById(R.id.attachement))
					.setImageResource(R.drawable.attachment_unsel);
			return;
		}
		if (!nonFriendMessage) {
			if (mPopupWindow != null && mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				return;
			}
			if (CustomMenu.hideRet())
				return;

			if (chat_panel_more_option_emo.getVisibility() == View.VISIBLE) {
				chat_panel_more_option_emo.setVisibility(View.GONE);
				myScreenName(Constants.SCRTEEN_NAME_THREAD_CHAT);
				return;
			}
		}
		setUnreadCount();
		if (FeedRequester.feedTaskConversationMessagesRefresh != null
				&& FeedRequester.feedTaskConversationMessagesRefresh
						.getStatus() == Status.RUNNING) {
			FeedRequester.feedTaskConversationMessagesRefresh.cancel(true);
			FeedRequester.feedTaskConversationMessagesRefresh = null;
		}
		if (FeedRequester.feedTaskConversationMessages != null
				&& FeedRequester.feedTaskConversationMessages.getStatus() == Status.RUNNING) {
			FeedRequester.feedTaskConversationMessages.cancel(true);
			FeedRequester.feedTaskConversationMessages = null;
		}
		FeedTask.ShiftNewMessageVec = new Vector<ShiftNewMessage>();
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
			DataModel.sSelf.removeObject(DMKeys.FROM_REGISTRATION);
			// Intent intent1 = new Intent(ConversationsActivity.this,
			// KainatHomeActivity.class);
			// startActivity(intent1);
			finish();
		} else {
			if (UIActivityManager.fromPushNot == 2) {
				UIActivityManager.fromPushNot = 0;
				Intent intent1 = new Intent(ConversationsActivity.this,
						KainatHomeActivity.class);
				startActivity(intent1);
			}
			finish();
		}
		// Start Channel Refresh here
		startChannelRefresh();
		// Set Refresh time to 5 secs
		RefreshService.setRefreshTime(5000);
		overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out); 
	}

	private void updateReadMessagereceiptToDBAndServer() {
		if (BusinessProxy.messageIDString != null
				&& BusinessProxy.messageIDString.toString().contains(";")) {
			sendReadReceipt("", BusinessProxy.messageIDString.toString());
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null, MessageConversationTable.CONVERSATION_ID + " =?",
					new String[] { conversationList.conversationId }, null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int read_msg_count = 0;
				int count = cursor
						.getInt(cursor
								.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT));
				ContentValues values = new ContentValues();
				if (BusinessProxy.messageIDString.toString().indexOf(';') != -1)
					read_msg_count = BusinessProxy.messageIDString.toString()
							.split(";").length;
				// Safe Check, that needs to be removed
				if (count > 0 && read_msg_count == 0)
					read_msg_count = count;

				values.put(
						MessageConversationTable.UNREAD_MESSAGE_COUNT,
						((count - read_msg_count) >= 0) ? (count - read_msg_count)
								: 0);

				int updatedRow = getContentResolver()
						.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								values,
								MessageConversationTable.CONVERSATION_ID + "=?",
								new String[] { conversationList.conversationId });
				cursor.close();
			}
			BusinessProxy.messageIDString = null;
		}
	}

	private void setUnreadCount() {
		if (readCount > 0) {
			Cursor cursor2 = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null,
					MessageConversationTable.CONVERSATION_ID + " = ? and "
							+ MessageConversationTable.USER_ID + " =? ",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							"" + BusinessProxy.sSelf.getUserId() }, null);
			if (cursor2.getCount() > 0) {
				cursor2.moveToFirst();
				int unreadMsgcount = cursor2
						.getInt(cursor2
								.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT));
				cursor2.close();
				unreadMsgcount = unreadMsgcount - readCount;
				readCount = 0;
				if (unreadMsgcount <= 0)
					unreadMsgcount = 0;
				ContentValues values = new ContentValues();
				values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT,
						unreadMsgcount);
				int updatedRow = getContentResolver()
						.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								values,
								MessageConversationTable.CONVERSATION_ID
										+ "= ? and "
										+ MessageConversationTable.USER_ID
										+ " =?",
								new String[] {
										getIntent().getStringExtra(
												Constants.CONVERSATION_ID),
										"" + BusinessProxy.sSelf.getUserId() });
				String id = "";
				for (int i = 0; i < readIds.size(); i++) {
					id = id + "'" + readIds.get(i) + "',";
				}
				// if(id.lastIndexOf(",")!= -1)
				if (id.length() > 0)
					id = id.substring(0, id.length() - 1);
				String sql = "UPDATE  " + MessageTable.TABLE + " set  "
						+ MessageTable.HAS_BEEN_VIEWED + "= 'true'  WHERE "
						+ MessageTable.MESSAGE_ID + " IN (" + id + ") and "
						+ MessageTable.CONVERSATION_ID + "='"
						+ getIntent().getStringExtra(Constants.CONVERSATION_ID)
						+ "'";
				BusinessProxy.sSelf.sqlDB.execSQL(sql);
				readIds.clear();
			} else
				cursor2.close();
		}
	}

	@Override
	protected void onResume() {
		// onCrahs();
		super.onResume();

		if (spannedContent == null) {
			spannedContent = new HashMap<Integer, SoftReference<Spanned>>();
		}
		if (!nonFriendMessage) {
			try {

				// //System.out.printlnprintln("---------resume-");
				// //System.out.printlnprintln("---------resume-  CONVERSATIONID : "+CONVERSATIONID);
				// //System.out.printlnprintln("---------resume-  intent CONVERSATIONID : "+getIntent().getStringExtra(Constants.CONVERSATION_ID));
				if (CONVERSATIONID != null
						&& !getIntent().getStringExtra(
								Constants.CONVERSATION_ID).equalsIgnoreCase(
								CONVERSATIONID)) {
					getIntent().putExtra(Constants.CONVERSATION_ID,
							CONVERSATIONID);
					// //System.out.printlnprintln("---------resume replacing cid-");

					if (!activityAdapter.getCursor().isClosed())
						if (activityAdapter != null) {
							activityAdapter.changeCursor(getCursor());
							activityAdapter.notifyDataSetChanged();
						}
					initParticipantInfo();
					((CImageView) findViewById(R.message_row.other_image_top_profile))
							.setVisibility(View.GONE);
					filltop();
					updateParticepentLabel();
					listViewActivity.invalidate();
				}

				// Write here code to send an auto welcome message in group, to
				// get the CID in reverse, so that Pic and subject can be
				// submitted.
				if (getIntent().getStringExtra("MUC_PIC_FILE_ID") != null) {
					mucFileId = getIntent().getStringExtra("MUC_PIC_FILE_ID");
					getIntent().removeExtra("MUC_PIC_FILE_ID");
				}
				if (getIntent().getStringExtra("MUC_SUB_NAME") != null) {
					mucSubject = getIntent().getStringExtra("MUC_SUB_NAME");
					getIntent().removeExtra("MUC_SUB_NAME");
					if (title != null)
						title.setText(mucSubject);
				}
				mucPic = ((CImageView) findViewById(R.message_row.other_image_top_profile));
				if (getIntent().getStringExtra(Constants.MUC_PIC) != null) {
					mucUrlFromInbox = getIntent().getStringExtra(
							Constants.MUC_PIC);
					imageManager = new ImageDownloader(2);
					mucPic.setImageUrl(mucUrlFromInbox);
					imageManager.download(mucUrlFromInbox, mucPic,
							ConversationsActivity.this,
							ImageDownloader.TYPE_THUMB_BUDDY);
				}
				if (getIntent().getByteExtra("START_CHAT_FROM_EXTERNAL",
						(byte) 0) == 1) {
					getIntent().putExtra("START_CHAT_FROM_EXTERNAL", (byte) 0);
					String destinations = DataModel.sSelf
							.removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS);
					addNewConversation(destinations, true);
				}
				getIntent().putExtra("START_CHAT_FROM_EXTERNAL", (byte) 0);

				title = ((EmojiconTextView) findViewById(R.conversation.title));

				// Hide Talking Pic , as now now its no supported in P2P

				updateParticepentLabel();

				// myScreenName(Constants.SCRTEEN_NAME_THREAD_CHAT);

				// //System.out.printlnprintln("-------update subject---1-------");
				// if(title.getText().toString()==null &&
				// title.getText().toString().trim().length()<=0)
				if (getIntent().getStringExtra("MUC_LOCAL_IMG") != null
						|| getIntent().getStringExtra("MUC_SUB_NAME") != null) {
					filltop();
				}

				title.setText(BusinessProxy.sSelf
						.parseNameInformation(getIntent().getStringExtra(
								Constants.TITLE)));
				if (title.getText().toString().equals("")) {
					// title.setText();
				}

				setTitleName(BusinessProxy.sSelf
						.parseNameInformation(getIntent().getStringExtra(
								Constants.TITLE)));

				load_prevoius_message = ((View) findViewById(R.conversation.load_prevoius_message));
				// load_prevoius_message.setBackgroundResource(R.drawable.chatmessagenotification);
				load_prevoius_message.setVisibility(View.GONE);
				if (FeedRequester.feedTaskConversationMessages != null
						&& FeedRequester.feedTaskConversationMessages
								.getStatus() != Status.FINISHED) {
					// load_prevoius_message.setText(getResources().getString(
					// R.string.refreshing_list));
					load_prevoius_message.setVisibility(View.VISIBLE);
					// //System.out.printlnprintln("--------load_prevoius_message-VISIBLE");
				}

				load_prevoius_message.setOnClickListener(this);

				chat_panel_more_option = findViewById(R.chat_panel.more_option);
				chat_panel_more_option_emo = findViewById(R.chat_panel.more_option_emo);

				addMultiImages();

				CONVERSATIONID = getIntent().getStringExtra(
						Constants.CONVERSATION_ID);

				if (getIntent().getStringExtra(Constants.CONVERSATION_ID)
						.startsWith("NP")) {
					load_prevoius_message.setVisibility(View.GONE);
					// //System.out.printlnprintln("--------load_prevoius_message-GONE");
				}
				image_count.setText("" + mPicturePath.size());
				if (mPicturePath != null && mPicturePath.size() <= 0)
					image_count.setVisibility(View.GONE);
				else
					image_count.setVisibility(View.VISIBLE);

				image_count_dodle.setText("" + mPicturePathCanvas.size());
				if (mPicturePathCanvas != null
						&& mPicturePathCanvas.size() <= 0)
					image_count_dodle.setVisibility(View.GONE);
				else
					image_count_dodle.setVisibility(View.VISIBLE);

				if (conversationList != null && conversationList.isLeft == 1)
				// blockInputPad() ;
				{
					// findViewById(R.chat_panel.more).setVisibility(View.GONE)
					// ;
					// findViewById(R.chat_panel.sendButton).setVisibility(View.GONE)
					// ;
					// ((EditText)findViewById(R.community_chat.msgBox))
					// .setText("You have left this group!");
					// ((EditText)findViewById(R.community_chat.msgBox))
					// .setEnabled(false) ;
					blockInputPad();
				}
				// ImageDownloader.thumbnailReponseHandler = this;

				if (conversationList != null
						&& (conversationList.subject == null || conversationList.subject
								.trim().length() <= 0)) {
					if (title == null)
						title = ((EmojiconTextView) findViewById(R.conversation.title));
					if (conversationList.isGroup == 1
							&& participantLable != null) {
						if (getIntent().getStringExtra("MUC_SUB_NAME") != null) {
							title.setText(getIntent().getStringExtra(
									"MUC_SUB_NAME"));
							setTitle(getIntent().getStringExtra("MUC_SUB_NAME"));
						} else
							title.setText(participantLable);
					} else {
						if (title == null)
							title = ((EmojiconTextView) findViewById(R.conversation.title));
						if (listViewActivity != null)
							title.setText(p2pParticipent);// ((TextView)findViewById(R.conversation.status)).getText());

					}
				}
				if (listViewActivity != null && activityAdapter != null
						&& activityAdapter.getCursor() != null) {
					if (fromOnCreate) {
						listViewActivity.post(new Runnable() {
							public void run() {
								try {
									listViewActivity.setSelection(activityAdapter.getCursor().getCount() - 1);
									Log.v("ConverstionsActivity","Manoj"+"825");
									listViewActivity.setStackFromBottom(true);
								} catch (Exception e) {
									// //System.out.printlnprintln(e.getLocalizedMessage());
								}
							}
						});

						listViewActivity
								.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
						listViewActivity.setStackFromBottom(true);
					}
					// //System.out.printlnprintln("-------getTranscriptMode------"+listViewActivity
					// .getTranscriptMode());

					fromOnCreate = false;
					FeedTask.setHttpSynchRefreshCurrentView(this);
					ComposeService.setHttpSynchRefreshCurrentView(this);
					Downloader.setHttpSynchRefreshCurrentView(this);
					VDownloader.setHttpSynchRefreshCurrentView(this);
				}
				if (mucFileId != null || mucSubject != null) {
					((EditText) findViewById(R.community_chat.msgBox))
							.setText(getString(R.string.muc_welcome_msg));
					((ImageView) findViewById(R.chat_panel.sendButton))
							.performClick();
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		if (conversationList != null
				&& conversationList.participantInfo != null
				&& conversationList.participantInfo.size() == 1
				&& conversationList.isGroup == 1) {
			allLeft = true;
		}
		if (allLeft) {
			blockInputPad();
			// showToast("All participent are left") ;
		}
		
		shareOuterData();
	}

	private void shareOuterData() {
		// TODO Auto-generated method stub
		 // Sharing from outside
		//TExt Sharing
		 if(UIActivityManager.sharedText != null){
			 messageBox.setText(UIActivityManager.sharedText);
				((ImageView) findViewById(R.chat_panel.sendButton))
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
					UIActivityManager.sharedImage = null;
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getString(R.string.multiimage_selection))
					   .setCancelable(false)
					   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					       public void onClick(DialogInterface dialog, int id) {
					    		
								for (int i = 0; i < 5; i++) {
									try {
										mPicturePath.add(s[i]);

									} catch (Exception e) {
										// TODO: handle exception
									}
								}
								UIActivityManager.sharedImage  = null;
								((ImageView)findViewById(R.chat_panel.sendButton)).performClick();
								mPicturePath = null;
							
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
							mPicturePath.add(s[i]);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					UIActivityManager.sharedImage  = null;
					((ImageView)findViewById(R.chat_panel.sendButton)).performClick();
					mPicturePath = null;
		       
				}
			}
	/*		// for Video Sharing
			if (UIActivityManager.sharedVideo!= null) {
			  	if (isSizeReachedMaximum(UIActivityManager.sharedVideo)) {
						showSimpleAlert("Error",
							"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
			}else
			{
				mVideoPath = UIActivityManager.sharedVideo;
				UIActivityManager.sharedVideo = null;
				((ImageView) findViewById(R.chat_panel.sendButton))
				.performClick();
			}}
			// for Audio Sharing
			if (UIActivityManager.sharedAudio!= null) {
				if(isInternetOn()){
					mVoicePath = UIActivityManager.sharedAudio;
					UIActivityManager.sharedAudio = null;
					((ImageView)findViewById(R.chat_panel.sendButton)).performClick();
				}
			  	}*/
			
			// for Video Sharing
			if (UIActivityManager.sharedVideo!= null) {

				final String[] s;
				s=UIActivityManager.sharedVideo.split(",");
				
				if(s.length > 1){
					UIActivityManager.sharedVideo = null;
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
								Toast.makeText(ConversationsActivity.this, getString(R.string.max_attachment_reached_update), Toast.LENGTH_LONG).show();
								mVideoPath = null;
								 UIActivityManager.sharedVideo = null;
							}else{
							UIActivityManager.sharedVideo = null;
							((ImageView) findViewById(R.chat_panel.sendButton))
							.performClick();
							}
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
							if (isSizeReachedMaximum(mVideoPath)) {
								Toast.makeText(ConversationsActivity.this, getString(R.string.max_attachment_reached_update), Toast.LENGTH_LONG).show();
								mVideoPath = null;
								 UIActivityManager.sharedVideo = null;
							}else{
							UIActivityManager.sharedVideo = null;
							((ImageView) findViewById(R.chat_panel.sendButton))
							.performClick();
							}
					}
				}
				
			
			  	}
			// for Audio Sharing
			if (UIActivityManager.sharedAudio!= null) {
				if(isInternetOn()){
					final String[] s;
					s=UIActivityManager.sharedAudio.split(",");
					if(s.length > 1){
						UIActivityManager.sharedAudio = null;
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
									if(mVoicePath.startsWith("file")){
										mVoicePath = mVoicePath.replaceFirst("file://", "");
									}
									UIActivityManager.sharedAudio = null;
									//sendMessage("");
									((ImageView) findViewById(R.chat_panel.sendButton))
									.performClick();
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
							if(mVoicePath.startsWith("file")){
								mVoicePath = mVoicePath.replaceFirst("file://", "");
							}
							UIActivityManager.sharedAudio = null;
							((ImageView) findViewById(R.chat_panel.sendButton))
							.performClick();
					}
					
					
					
					
					//=====================================================================
					
				}
			  	}


	}
	private void sendUpdateSubjectRequest(String value) {
		request = new Request(ID_UPDATE_SUBJECT, "url");
		if (value != null)
			request.subject = value;
		if (mucFileId != null)
			request.muc_file_id = mucFileId;
		if (!ConversationsActivity.this.checkInternetConnection()) {
			ConversationsActivity.this.networkLossAlert();
			return;
		}
		request.execute("LikeDislike");
	}

	public void setTitleName(String title) {
		this.titleName = title.trim();
	}

	public String getTitleName() {
		return this.titleName;
	}

	long touchDownTime;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDownTime = SystemClock.elapsedRealtime();
			break;

		case MotionEvent.ACTION_UP:
			// to avoid drag events
			if (SystemClock.elapsedRealtime() - touchDownTime <= 150) {

				if (isTouchInsideView(ev, listViewActivity))// ev.getRawX(),
															// ev.getRawY(),
															// field))
				{
					Utilities.closeSoftKeyBoard(messageBox,
							ConversationsActivity.this);
					break;
				}
			}
			break;
		}

		return super.dispatchTouchEvent(ev);

		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		//
		// if (imm.isAcceptingText()) {
		// //System.out.printlnprintln("Software Keyboard was shown");
		// } else {
		// //System.out.printlnprintln("Software Keyboard was not shown");
		// }
		//
		// //System.out.printlnprintln("---------------------1232dispatchTouchEvent : "+((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).isActive(messageBox));
		// View view = getCurrentFocus();
		// boolean ret = super.dispatchTouchEvent(event);

		// if (event.getAction() == MotionEvent.ACTION_DOWN ){
		// if(messageBox!=null)
		// Utilities.closeSoftKeyBoard(messageBox, ConversationsActivity.this);
		// }

		// if (view instanceof EditText) {
		// View w = getCurrentFocus();
		// int scrcoords[] = new int[2];
		// w.getLocationOnScreen(scrcoords);
		// float x = event.getRawX() + w.getLeft() - scrcoords[0];
		// float y = event.getRawY() + w.getTop() - scrcoords[1];
		//
		// if (event.getAction() == MotionEvent.ACTION_UP
		// && (x < w.getLeft() || x >= w.getRight()
		// || y < w.getTop() || y > w.getBottom()) ) {
		// InputMethodManager imm =
		// (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(),
		// 0);
		// }
		// }
		// return ret;
	}

	private boolean isTouchInsideView(final MotionEvent ev,
			final View currentFocus) {
		try {
			final int[] loc = new int[2];
			currentFocus.getLocationOnScreen(loc);
			return ev.getRawX() > loc[0] && ev.getRawY() > loc[1]
					&& ev.getRawX() < (loc[0] + currentFocus.getWidth())
					&& ev.getRawY() < (loc[1] + currentFocus.getHeight());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	// MyContentObserver contentObserver ;
	public void blockInputPad() {

		allLeft = false;
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		if (conversationList != null
				&& conversationList.participantInfo != null
				&& conversationList.participantInfo.size() == 1
				&& conversationList.isGroup == 1) {
			allLeft = true;
		}
		// //System.out.printlnprintln("-------------blockInputPad--:"+allLeft);

		// if(allLeft){
		// showToast("All participent are left") ;
		// }

		if (conversationList != null && conversationList.isGroup == 1
				&& (conversationList.isLeft == 1 || allLeft)) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// if(lDialog!=null)
					{
						findViewById(R.chat_panel.more)
								.setVisibility(View.GONE);
						findViewById(R.chat_panel.sendButton).setVisibility(
								View.GONE);
						((EditText) findViewById(R.community_chat.msgBox))
								.setVisibility(View.GONE);
						((TextView) findViewById(R.community_chat.left))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.community_chat.left))
								.setText(getString(R.string.you_left));
						if (allLeft && conversationList.isLeft == 0)
							((TextView) findViewById(R.community_chat.left))
									.setText(getString(R.string.all_left));
						((EditText) findViewById(R.community_chat.msgBox))
								.setEnabled(false);

						findViewById(R.id.chat_smiley).setVisibility(View.GONE);
						findViewById(R.id.voice_record)
								.setVisibility(View.GONE);

					}
				}
			});

		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					// if(lDialog!=null)
					{
						((TextView) findViewById(R.community_chat.left))
								.setVisibility(View.GONE);
						((EditText) findViewById(R.community_chat.msgBox))
								.setVisibility(View.VISIBLE);
						// findViewById(R.chat_panel.more).setVisibility(
						// View.VISIBLE);
						findViewById(R.chat_panel.sendButton).setVisibility(
								View.VISIBLE);
						// ((EditText)findViewById(R.community_chat.msgBox))
						// .setText("");
						((EditText) findViewById(R.community_chat.msgBox))
								.setEnabled(true);

					}
				}
			});
		}

	}

	public void startChatXXX() {
		String destinations = DataModel.sSelf
				.removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS);
		String destinationsUserName = DataModel.sSelf
				.removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS);

		// if (resultCode == ADDCONVERSATIONS)
		{

			request = new Request(ID_ADD_TO_CONVERSATION, "url");
			// String s = getParticipantInfo();
			// if(Utilities.split(new StringBuffer(s),
			// ";").length==2)
			if (conversationList != null
					&& conversationList.isGroup == 0
					&& conversationList.hasBeenViewed != null
					&& !conversationList.hasBeenViewed
							.equalsIgnoreCase("never"))// if(1==1)
			{
				request.p2p_2_group = true;
				addNewConversation(destinations, false);
				onBackPressed();
				return;
			} else {
				request.p2p_2_group = false;

				if (destinations != null && destinations.trim().length() > 0) {
					String destinationsUserNameArr[] = null;
					String dist[] = Utilities.split(new StringBuffer(
							destinations), ";");

					if (dist.length == 1
							&& BusinessProxy.sSelf
									.parseUsernameInformation(dist[0])
									.toLowerCase()
									.equalsIgnoreCase(
											SettingData.sSelf.getUserName()
													.toLowerCase()))

					{
						showToast("You can not add self!");
						return;
					}

					String actorUserid = "";
					for (int i = 0; i < dist.length; i++) {
						actorUserid = actorUserid + dist[i] + ";";
					}
					actorUserid = actorUserid.substring(0,
							actorUserid.length() - 1);

					String sp = getParticipantInfo();
					String dist2[] = Utilities.split(new StringBuffer(
							destinations), ";");
					destinationsUserNameArr = Utilities.split(new StringBuffer(
							destinations), ";");
					if (destinationsUserNameArr.length + dist2.length > 10) {
						showToast("You can not add more than 9 friends!");
						return;
					}

					// showOption(destinationsUserNameArr);
					request.destinationsUserNameArr = destinationsUserNameArr;
					// request.execute("LikeDislike");
				}

			}
		}

	}

	int count = 0;

	private class MyContentObserver extends ContentObserver {
		MyContentObserver(Handler handler) {
			super(handler);
		}

		public boolean deliverSelfNotifications() {
			return true;
		}

		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			if (1 == 1)
				return;
			onCrahs();
			try {
				if (activityAdapter == null
						|| activityAdapter.getCursor() == null)
					return;
				activityAdapter.getCursor().requery();
				activityAdapter.notifyDataSetChanged();
				if ((System.currentTimeMillis() - idelTime) > 2000)
					listViewActivity
							.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				else
					listViewActivity
							.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);

				if (FeedTask.updateParticipantInfoUI) {

					initParticipantInfo();
					updateParticepentLabel();
					blockInputPad();
					// findViewById(R.chat_panel.chat_panel).setVisibility(View.GONE)
					// ;
				}
				if (FeedTask.updateParticipantInfoUISUB) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								// Thread.sleep(3000);
								handler.post(new Runnable() {

									@Override
									public void run() {
										updateMUCSubject();
									}
								});

								FeedTask.updateParticipantInfoUISUB = false;
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
					}).start();

				}
				if (FeedTask.refreshList) {
					// //System.out.printlnprintln("---------refresh list-----");
				}
				if (!CONVERSATIONID.equalsIgnoreCase(getIntent()
						.getStringExtra(Constants.CONVERSATION_ID))) {
					getIntent().putExtra(Constants.CONVERSATION_ID,
							CONVERSATIONID);
					CONVERSATIONID = getIntent().getStringExtra(
							Constants.CONVERSATION_ID);

					// stopManagingCursor(activityAdapter.getCursor());

					// listViewActivity = (ListView)
					// findViewById(R.conversations.landing_discovery_activity_list);
					activityAdapter = new ConversationsAdapter(
							ConversationsActivity.this, getCursor(), true,
							ConversationsActivity.this);
					listViewActivity.setAdapter(activityAdapter);
					return;
				}

				int c = activityAdapter.getCursor().getCount();// getCursorCount();

				if (c > count) {

					if (count > 0) {
						if (FeedRequester.feedTaskConversationMessagesRefresh == null
								|| FeedRequester.feedTaskConversationMessagesRefresh
										.getStatus() == Status.FINISHED)
							listViewActivity.setSelection(count - 1);
						Log.v("ConverstionsActivity","Manoj"+"1420");
						listViewActivity.setStackFromBottom(true);
					}
				} else {
					// System.out
					// .println("Rocketalk --------MyContentObserver-only render------");
				}
				count = c;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// public void onChange(boolean selfChange, Uri uri)
		// {
		// //System.out.printlnprintln("Rocketalk --------MyContentObserver--------"+selfChange+"  : "+uri.toString());
		// }
	}

	private boolean isSizeReachedMaximum(long newSize) {

		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	private boolean isSizeReachedMaximum(Bitmap bm) {
		long newSize = 0;
		if (null != bm) {
			newSize = ImageUtils.getImageSize(bm);
		}
		// makeToast("Image Size: " + newSize + ", Total Attachment Size: " +
		// totalSize);
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	// ////////////////////
	private void calculateTotSize() {
		if (mPicturePath != null) {
			totalSize = 0;
			for (int i = 0; i < mPicturePath.size(); i++) {
				try {
					// totalSize = 0 ;
					long size = ImageUtils.getImageSize(mPicturePath.get(i));
					if (isSizeReachedMaximum(size)) {
					}
				} catch (Exception e) {
				}
			}
		}
	}

	// mPicturePathCanvas
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
		if (mPicturePathCanvas != null) {
			for (int i = 0; i < mPicturePathCanvas.size(); i++) {
				try {
					// totalSize = 0 ;
					long size = ImageUtils.getImageSize(mPicturePathCanvas
							.get(i));
					totalSize += size;
					if (isSizeReachedMaximum(size)) {
					}
				} catch (Exception e) {
				}
			}
		}
		if (mVideoPath != null) {
			long size = ImageUtils.getImageSize(mVideoPath);
			totalSize += size;
		}
	}

	private void addMultiImages() {
		String s[] = (String[]) DataModel.sSelf
				.removeObject(DMKeys.MULTI_IMAGES);
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

		Integer sid[] = (Integer[]) DataModel.sSelf
				.removeObject(DMKeys.MULTI_IMAGES + "ID");
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
		int sid_int[] = (int[]) DataModel.sSelf
				.removeObject(DMKeys.MULTI_IMAGES + "IDINT");
		if (sid_int != null) {
			mPicturePathId_int = new Vector<Integer>();
			for (int i = 0; i < sid_int.length; i++) {
				try {
					mPicturePathId_int.add(sid_int[i]);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		// quickSend();//change by akm
	}

	long MAX_IMAGE_ATTACH_SIZE = BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;

	private boolean isSizeReachedMaximum(String newPath) {
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
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}

	public void addNewConversation(String destinations, boolean addMe) {
		if (destinations == null || destinations.trim().length() <= 0)
			return;
		// //System.out.printlnprintln("----------met destinations 2: "+destinations);
		boolean meinthis = false;
		boolean selfMessage = false;
		if (destinations.toLowerCase().indexOf(
				"<" + SettingData.sSelf.getUserName().toLowerCase() + ">") != -1) {
			addMe = false;
			meinthis = true;
		}
		Vector<ConversationList> conversationListVec = new Vector<ConversationList>();
		ConversationList conversationList2 = new ConversationList();
		String dist[] = null;
		// if (destinations != null && destinations.trim().length() > 0) {
		// String destinationsUserNameArr[] = null;
		//
		// dist = Utilities.split(new StringBuffer(destinations), ";");
		// }
		// met destinations 2: amjda <amjda>;Amy Chadha<Amyhere>;
		// 05-07 23:38:00.796: I///System.out.println23818): ----------met
		// destinations
		// after 2: Amy Chadha<Amyhere>
		if (destinations != null && destinations.trim().length() > 0) {
			String destinationsUserNameArr[] = null;

			dist = Utilities.split(new StringBuffer(destinations), ";");
			if (dist != null && dist.length == 1 && meinthis) {
				selfMessage = true;
			}
			if (dist.length > 1) {
				destinations = "";
				for (int i = 0; i < dist.length; i++) {
					String userName = BusinessProxy.sSelf
							.parseUsernameInformation(dist[i]);
					if (userName.toLowerCase().equalsIgnoreCase(
							SettingData.sSelf.getUserName().toLowerCase())) {
						addMe = true;
						// //System.out.printlnprintln("----------met destinations continue: "+userName);
						continue;
					}
					destinations = destinations + dist[i] + ";";
				}
				if (destinations.lastIndexOf(";") != -1) {
					destinations = destinations.substring(0,
							destinations.length() - 1);
				}
			}
			// //System.out.printlnprintln("----------met destinations after 2: "+destinations);
			dist = Utilities.split(new StringBuffer(destinations), ";");
		}
		String s = "";
		if (dist.length == 1) {
			// Cursor cursor = getContentResolver().query(
			// MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
			// MessageConversationTable.USER_ID+" =? and "+MessageConversationTable.IS_GROUP+" =0",
			// new String[]{BusinessProxy.sSelf.getUserId()+""},
			// null);
			String where = /*
							 * MessageConversationTable.USER_ID+" ="+BusinessProxy
							 * .sSelf.getUserId()+" and "+
							 */MessageConversationTable.IS_GROUP
					+ " =0 and "
					+ MessageConversationTable.PARTICIPANT_INFO
					+ " like '%"
					+ BusinessProxy.sSelf.parseUsernameInformation(destinations
							.toLowerCase()) + "%'";
			// //System.out.printlnprintln("----------met where: "+where);
			// Cursor cursor = getContentResolver().query(
			// MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
			// where, null,
			// null);
			// Cursor cursor=
			// BusinessProxy.sSelf.sqlDB.query(MessageConversationTable.TABLE,
			// null, null,
			// null, null, null, null);

			destinations = destinations.replace(" ", "%");
			Cursor cursor = getContentResolver()
					.query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
							null,
							MessageConversationTable.USER_ID
									+ " =? and "
									+ MessageConversationTable.IS_GROUP
									+ " =0 and "
									+ MessageConversationTable.PARTICIPANT_INFO
									+ " like '%"
									+ BusinessProxy.sSelf
											.parseUsernameInformation(destinations)
									+ "%'",
							new String[] { BusinessProxy.sSelf.getUserId() + "" },
							null);

			String usernamesStr = "";
			// //System.out.printlnprintln("----------met--------------coursor count : "+cursor.getCount());
			if (cursor.getCount() > 0) {

				for (int ii = 0; ii < cursor.getCount(); ii++) {
					cursor.moveToPosition(ii);
					String pi[] = null;
					s = cursor
							.getString(cursor
									.getColumnIndex(MessageConversationTable.PARTICIPANT_INFO));
					if (s != null)
						pi = Utilities.split(new StringBuffer(s),
								Constants.ROW_SEPRETOR);
					String isGroup = cursor.getString(cursor
							.getColumnIndex(MessageConversationTable.IS_GROUP));
					if ((isGroup != null && isGroup.equalsIgnoreCase("1"))
							|| (pi != null && pi.length > 2)) {
						// //System.out.printlnprintln("----------met----continue1----------s : "+s);
						continue;
					}

					if (meinthis && (pi != null && pi.length >= 1)) {
						boolean yesIm = false;
						for (int i = 0; i < pi.length; i++) {
							String info[] = Utilities.split(new StringBuffer(
									pi[i]), Constants.COL_SEPRETOR);
							if (info[1].toLowerCase().equalsIgnoreCase(
									SettingData.sSelf.getUserName()
											.toLowerCase())) {
								yesIm = true;
							}
						}

						if (!yesIm) {
							// //System.out.printlnprintln("----------met----continue2----------s : "+s);
							continue;
						}
					}

					conversationList2 = new ConversationList();

					conversationList2.conversationId = cursor
							.getString(cursor
									.getColumnIndex(MessageConversationTable.CONVERSATION_ID));
					if (s != null) {
						// //System.out.printlnprintln("----------met--------------s : "+s);
						pi = Utilities.split(new StringBuffer(s),
								Constants.ROW_SEPRETOR);

						ParticipantInfo participantInfo = new ParticipantInfo();
						conversationList2.participantInfo = new Vector<ParticipantInfo>();
						try {
							for (int i = 0; i < pi.length; i++) {
								participantInfo = new ParticipantInfo();
								String info[] = Utilities.split(
										new StringBuffer(pi[i]),
										Constants.COL_SEPRETOR);
								// System.out.printlnprintln("----------met--------------info : "+info.length);
								if (!selfMessage && info != null
										&& info.length > 1) {
									// System.out.printlnprintln("----------met--------------if");
									participantInfo.isSender = Boolean
											.parseBoolean(info[0]);
									participantInfo.userName = info[1];
									if (info.length >= 3)
										participantInfo.firstName = info[2];
									if (info.length >= 4)
										participantInfo.lastName = info[3];
									// usernamesStr = usernamesStr
									// +participantInfo.userName +"~" ;
								} else if (selfMessage && info != null) {
									// System.out.printlnprintln("----------met--------------else");
									participantInfo.isSender = Boolean
											.parseBoolean(info[0]);
									participantInfo.userName = info[1];
									if (info.length >= 3)
										participantInfo.firstName = info[2];
									if (info.length >= 4)
										participantInfo.lastName = info[3];
									// usernamesStr = usernamesStr
									// +participantInfo.userName +"~" ;
								}

								conversationList2.participantInfo
										.add(participantInfo);
								conversationListVec.add(conversationList2);
							}
							if (selfMessage) {
								int ok = 0;
								for (int i = 0; i < conversationList2.participantInfo
										.size(); i++) {
									ParticipantInfo participantInfoT = (ParticipantInfo) conversationList2.participantInfo
											.get(i);
									if (participantInfoT.userName
											.trim()
											.toLowerCase()
											.equalsIgnoreCase(
													SettingData.sSelf
															.getUserName())) {
										ok++;
									}
								}
								if (ok != conversationList2.participantInfo
										.size())
									conversationListVec = new Vector<ConversationList>();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// userNameVec.add(usernamesStr) ;
				}
			} else {

			}
			cursor.close();
		}

		Long sysTime = System.currentTimeMillis();
		ConversationList conversationList = new ConversationList();
		conversationList.parent_id = "" + sysTime;
		conversationList.unreadMessageCount = -1;
		conversationList.contentBitMap = "0";
		conversationList.conversationId = "NP" + sysTime;
		if ((this.conversationList != null
				&& this.conversationList.isGroup == 1 && getIntent()
				.getStringExtra(Constants.CONVERSATION_ID).startsWith("NP"))) {
			// System.out.printlnprintln("--------------conversationList.conversationId current  : "+getIntent().getStringExtra(Constants.CONVERSATION_ID));
			getContentResolver().delete(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					MessageConversationTable.CONVERSATION_ID + " = ? ",
					new String[] { getIntent().getStringExtra(
							Constants.CONVERSATION_ID) });
			conversationList.conversationId = getIntent().getStringExtra(
					Constants.CONVERSATION_ID);
		}
		// System.out.printlnprintln("--------------CID conversationList.conversationId new  : "+conversationList.conversationId);
		conversationList.datetime = "" + sysTime;
		conversationList.drmFlags = 0;
		conversationList.messageId = "-1";
		conversationList.hasBeenViewed = "never";
		conversationList.hasBeenViewedBySIP = "never";
		conversationList.mfuId = "0";
		conversationList.msgOp = -1;
		conversationList.msgTxt = "";
		conversationList.notificationFlags = 0;
		conversationList.refMessageId = "-1";
		conversationList.senderUserId = "-1";
		conversationList.source = SettingData.sSelf.getUserName();

		if (SettingData.sSelf.getFirstName() != null
				&& SettingData.sSelf.getLastName() != null)
			conversationList.source = SettingData.sSelf.getFirstName() + " "
					+ SettingData.sSelf.getLastName();

		conversationList.targetUserId = "-1";
		conversationList.user_firstname = "";
		conversationList.user_lastname = "";
		conversationList.user_name = "";
		conversationList.user_uri = "";
		conversationList.inserTime = sysTime;
		conversationList.isGroup = 0;
		conversationList.subject = "";
		if (mucSubject != null)
			conversationList.subject = mucSubject;
		if (mucFileId != null) {
			conversationList.imageFileId = mucPicUrl.replace("$MUC_FILE_ID$",
					mucFileId);
			Log.i(TAG, "addNewConversation :: MUC File URL Saving : "
					+ mucPicUrl.replace("$MUC_FILE_ID$", mucFileId));
		}

		s = "";
		if (addMe)
			s += "" + "true" + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getUserName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getFirstName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getLastName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getUserName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getUserName() + Constants.ROW_SEPRETOR;

		int samePart = 0;
		boolean getId = false;
		if (dist != null && dist.length == 1) {
			if (dist.length > 2)
				conversationList.isGroup = 0;
			for (int i = 0; i < dist.length; i++) {
				String userName = BusinessProxy.sSelf
						.parseUsernameInformation(dist[i]);

				String user = BusinessProxy.sSelf.parseNameInformation(dist[i]);
				boolean isMe = false;
				if (userName.equalsIgnoreCase(SettingData.sSelf.getUserName()))
					isMe = true;
				s += "" + isMe + Constants.COL_SEPRETOR + userName
						+ Constants.COL_SEPRETOR + user
						+ Constants.COL_SEPRETOR + " " + Constants.COL_SEPRETOR
						+ userName + Constants.COL_SEPRETOR + userName
						+ Constants.ROW_SEPRETOR;

				if (conversationList.isGroup == 0) {
					for (int jj = 0; jj < conversationListVec.size(); jj++) {
						samePart = 0;
						conversationList2 = conversationListVec.get(jj);
						for (int j = 0; j < conversationList2.participantInfo
								.size(); j++) {
							ParticipantInfo info = conversationList2.participantInfo
									.get(j);
							if (info.userName.toLowerCase().toLowerCase()
									.equalsIgnoreCase(userName.toLowerCase())) {
								samePart++;
							}

						}

						if (samePart == 1) {
							getId = true;
							conversationList.conversationId = conversationList2.conversationId;
						}
					}
				}
			}
		} else {

			conversationList.isGroup = 1;
			if (meinthis && dist.length == 2)
				conversationList.isGroup = 0;
			for (int i = 0; i < dist.length; i++) {
				String userName = BusinessProxy.sSelf
						.parseUsernameInformation(dist[i]);
				String user = BusinessProxy.sSelf.parseNameInformation(dist[i]);
				s += "" + "false" + Constants.COL_SEPRETOR + userName
						+ Constants.COL_SEPRETOR + user
						+ Constants.COL_SEPRETOR + " " + Constants.COL_SEPRETOR
						+ userName + Constants.COL_SEPRETOR + userName
						+ Constants.ROW_SEPRETOR;
			}
		}

		// if( Utilities.split(new StringBuffer(destinations),
		// Constants.ROW_SEPRETOR).length>3)
		// conversationList.isGroup = 1;

		if (Utilities.split(new StringBuffer(destinations), ";").length < 2)
			conversationList.isGroup = 0;
		else
			conversationList.isGroup = 1;

		// //System.out.printlnprintln("----------met new destinations: "+s);
		// System.out.printlnprintln("--------------CID2 conversationList.conversationId  : "+conversationList.conversationId);
		conversationList.participantInfoStr = s;
		// if(!getId)
		if (!getId && conversationList.conversationId.startsWith("NP"))
			saveConversitionList(conversationList);

		getIntent().putExtra(Constants.CONVERSATION_ID,
				conversationList.conversationId);
		CONVERSATIONID = getIntent().getStringExtra(Constants.CONVERSATION_ID);
		// listViewActivity = (ListView)
		// findViewById(R.conversations.landing_discovery_activity_list);
		// if(conversationList.conversationId.ind)
		// stopManagingCursor(activityAdapter.getCursor());
		activityAdapter = new ConversationsAdapter(this, getCursor(), true,
				this);
		// if(count>0)

		getIntent().putExtra(Constants.TITLE, conversationList.subject);

		listViewActivity.setAdapter(activityAdapter);
		// //System.out.printlnprintln("-------update subject---4-------");
		if (title != null)
			title.setText(BusinessProxy.sSelf.parseNameInformation(getIntent()
					.getStringExtra(Constants.TITLE)));
		setTitleName(BusinessProxy.sSelf.parseNameInformation(getIntent()
				.getStringExtra(Constants.TITLE)));
		// title2.setText(BusinessProxy.sSelf.parseNameInformation(getIntent()
		// .getStringExtra(Constants.TITLE)));

		if (s != null) {
			String pi[] = Utilities.split(new StringBuffer(s),
					Constants.ROW_SEPRETOR);
			ParticipantInfo participantInfo = new ParticipantInfo();
			conversationList.participantInfo = new Vector<ParticipantInfo>();
			try {
				for (int i = 0; i < pi.length; i++) {
					participantInfo = new ParticipantInfo();
					String info[] = Utilities.split(new StringBuffer(pi[i]),
							Constants.COL_SEPRETOR);

					participantInfo.isSender = Boolean.parseBoolean(info[0]);
					participantInfo.userName = info[1];
					participantInfo.firstName = info[2];
					participantInfo.lastName = info[3];
					// participantInfo.profileImageUrl = info[4];
					// participantInfo.profileUrl = info[5];

					conversationList.participantInfo.add(participantInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.conversationList = conversationList;
		if (conversationList != null && conversationList.isGroup == 0)
			fillP2P();
		else if (conversationList != null && conversationList.isGroup == 1)
			fillGroup();

		updateParticepentLabel();

	}

	public void addUserConversation(String destinations, boolean addMe) {
		if (destinations == null || destinations.trim().length() <= 0)
			return;
		// //System.out.printlnprintln("----------met destinations 2: "+destinations);

		if (destinations.toLowerCase().indexOf(
				"<" + SettingData.sSelf.getUserName().toLowerCase() + ">") != -1) {
			addMe = false;
		}
		Vector<ConversationList> conversationListVec = new Vector<ConversationList>();
		ConversationList conversationList2 = new ConversationList();
		String dist[] = null;
		if (destinations != null && destinations.trim().length() > 0) {
			// String destinationsUserNameArr[] = null;

			dist = Utilities.split(new StringBuffer(destinations), ";");
		}
		String s = "";
		if (dist.length == 1) {
			Cursor cursor = getContentResolver()
					.query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
							null,
							MessageConversationTable.USER_ID + " =?",
							new String[] { BusinessProxy.sSelf.getUserId() + "" },
							null);

			String usernamesStr = "";

			if (cursor.getCount() > 0) {

				for (int ii = 0; ii < cursor.getCount(); ii++) {
					cursor.moveToPosition(ii);
					s = cursor
							.getString(cursor
									.getColumnIndex(MessageConversationTable.PARTICIPANT_INFO));
					String isGroup = cursor.getString(cursor
							.getColumnIndex(MessageConversationTable.IS_GROUP));
					if (isGroup != null && isGroup.equalsIgnoreCase("1"))
						continue;

					conversationList2 = new ConversationList();

					conversationList2.conversationId = cursor
							.getString(cursor
									.getColumnIndex(MessageConversationTable.CONVERSATION_ID));
					if (s != null) {
						String pi[] = Utilities.split(new StringBuffer(s),
								Constants.ROW_SEPRETOR);
						ParticipantInfo participantInfo = new ParticipantInfo();
						conversationList2.participantInfo = new Vector<ParticipantInfo>();
						try {
							for (int i = 0; i < pi.length; i++) {
								participantInfo = new ParticipantInfo();
								String info[] = Utilities.split(
										new StringBuffer(pi[i]),
										Constants.COL_SEPRETOR);
								if (info != null && info.length > 1) {

									participantInfo.isSender = Boolean
											.parseBoolean(info[0]);
									participantInfo.userName = info[1];
									if (info.length >= 3)
										participantInfo.firstName = info[2];
									if (info.length >= 4)
										participantInfo.lastName = info[3];
									// usernamesStr = usernamesStr
									// +participantInfo.userName +"~" ;
								}

								conversationList2.participantInfo
										.add(participantInfo);
								conversationListVec.add(conversationList2);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// userNameVec.add(usernamesStr) ;
				}
			} else {

			}
			cursor.close();
		}

		Long sysTime = System.currentTimeMillis();
		ConversationList conversationList = new ConversationList();

		s = "";
		if (addMe)
			s += "" + "true" + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getUserName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getFirstName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getLastName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getUserName() + Constants.COL_SEPRETOR
					+ SettingData.sSelf.getUserName() + Constants.ROW_SEPRETOR;

		int samePart = 0;
		boolean getId = false;
		if (dist != null && dist.length == 1) {
			if (dist.length > 2)
				conversationList.isGroup = 0;
			for (int i = 0; i < dist.length; i++) {
				String userName = BusinessProxy.sSelf
						.parseUsernameInformation(dist[i]);
				String user = BusinessProxy.sSelf.parseNameInformation(dist[i]);
				boolean isMe = false;
				if (userName.equalsIgnoreCase(SettingData.sSelf.getUserName()))
					isMe = true;
				s += "" + isMe + Constants.COL_SEPRETOR + userName
						+ Constants.COL_SEPRETOR + user
						+ Constants.COL_SEPRETOR + " " + Constants.COL_SEPRETOR
						+ user + Constants.COL_SEPRETOR + user
						+ Constants.ROW_SEPRETOR;

				if (conversationList.isGroup == 0) {
					for (int jj = 0; jj < conversationListVec.size(); jj++) {
						samePart = 0;
						conversationList2 = conversationListVec.get(jj);
						for (int j = 0; j < conversationList2.participantInfo
								.size(); j++) {
							ParticipantInfo info = conversationList2.participantInfo
									.get(j);
							if (info.userName.toLowerCase().toLowerCase()
									.equalsIgnoreCase(userName.toLowerCase())) {
								samePart++;
							}

						}

						if (samePart == 1) {
							getId = true;
							conversationList.conversationId = conversationList2.conversationId;
						}
					}
				}
			}
		} else {
			conversationList.isGroup = 1;
			for (int i = 0; i < dist.length; i++) {
				String userName = BusinessProxy.sSelf
						.parseUsernameInformation(dist[i]);
				// //System.out.printlnprintln("----------met new destinations dist[i]: "+dist[i]);
				String user = BusinessProxy.sSelf.parseNameInformation(dist[i]);

				s += "" + "false" + Constants.COL_SEPRETOR + userName
						+ Constants.COL_SEPRETOR + user
						+ Constants.COL_SEPRETOR + " " + Constants.COL_SEPRETOR
						+ user + Constants.COL_SEPRETOR + user
						+ Constants.ROW_SEPRETOR;
			}
		}

		if (Utilities.split(new StringBuffer(destinations),
				Constants.ROW_SEPRETOR).length > 3)
			conversationList.isGroup = 1;
		// //System.out.printlnprintln("----------met new destinations: "+s);
		// //System.out.printlnprintln("-------------- new participant  : "+s);
		conversationList.participantInfoStr = s;

		// getIntent().putExtra(Constants.CONVERSATION_ID,
		// conversationList.conversationId);
		// CONVERSATIONID =
		// getIntent().getStringExtra(Constants.CONVERSATION_ID);
		// listViewActivity = (ListView)
		// findViewById(R.conversations.landing_discovery_activity_list);
		// if(conversationList.conversationId.ind)
		// activityAdapter = new ConversationsAdapter(this, getCursor(), true,
		// this);
		// if(count>0)

		// getIntent().putExtra(Constants.TITLE, conversationList.subject);

		// listViewActivity.setAdapter(activityAdapter);
		// title.setText(BusinessProxy.sSelf.parseNameInformation(getIntent()
		// .getStringExtra(Constants.TITLE)));
		// title2.setText(BusinessProxy.sSelf.parseNameInformation(getIntent()
		// .getStringExtra(Constants.TITLE)));

		if (s != null) {
			String pi[] = Utilities.split(new StringBuffer(s),
					Constants.ROW_SEPRETOR);
			ParticipantInfo participantInfo = new ParticipantInfo();
			conversationList.participantInfo = new Vector<ParticipantInfo>();
			try {
				for (int i = 0; i < pi.length; i++) {
					participantInfo = new ParticipantInfo();
					String info[] = Utilities.split(new StringBuffer(pi[i]),
							Constants.COL_SEPRETOR);

					participantInfo.isSender = Boolean.parseBoolean(info[0]);
					participantInfo.userName = info[1];
					participantInfo.firstName = info[2];
					participantInfo.lastName = info[3];
					participantInfo.profileImageUrl = info[4];
					participantInfo.profileUrl = info[5];

					conversationList.participantInfo.add(participantInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ContentValues values = new ContentValues();
		values.put(MessageConversationTable.PARTICIPANT_INFO,
				conversationList.participantInfoStr);
		int updatedRow = getContentResolver().update(
				MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
				values,
				MessageConversationTable.CONVERSATION_ID + "= ? and "
						+ MessageConversationTable.USER_ID + " =?",
				new String[] { CONVERSATIONID,
						"" + BusinessProxy.sSelf.getUserId() });

		s = conversationList.participantInfoStr;
		if (s != null) {
			String pi[] = Utilities.split(new StringBuffer(s),
					Constants.ROW_SEPRETOR);

			// //System.out.printlnprintln("------------pi :"+s);
			ParticipantInfo participantInfo = new ParticipantInfo();
			this.conversationList.participantInfo = new Vector<ParticipantInfo>();
			// sender username firstname lastname profileImageUrl profileUrl
			int i = 0;
			try {

				for (i = 0; i < pi.length; i++) {
					participantInfo = new ParticipantInfo();
					String info[] = Utilities.split(new StringBuffer(pi[i]),
							Constants.COL_SEPRETOR);
					participantInfo.isSender = Boolean.parseBoolean(info[0]);
					participantInfo.userName = info[1];
					if (info.length >= 3)
						participantInfo.firstName = info[2];
					if (info.length >= 4)
						participantInfo.lastName = info[3];
					// participantInfo.profileImageUrl=info[4];
					// participantInfo.profileUrl=info[5];
					this.conversationList.participantInfo.add(participantInfo);
				}
			} catch (Exception e) {
				e.printStackTrace();

				message.participantInfo.add(participantInfo);
			}
		}
		this.conversationList.participantInfoStr = conversationList.participantInfoStr;
		if (this.conversationList != null && this.conversationList.isGroup == 0)
			fillP2P();
		else if (this.conversationList != null
				&& this.conversationList.isGroup == 1)
			fillGroup();
		updateParticepentLabel();
	}

	// if (getIntent().getByteExtra("START_CHAT_FROM_EXTERNAL", (byte) 0) == 1)
	// {
	// getIntent().putExtra("START_CHAT_FROM_EXTERNAL", (byte) 0);
	// String destinations = DataModel.sSelf
	// .removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS);
	// addNewConversation(destinations);
	// }
	int imId = 100;
	String addParticipentAfterResponse = "";

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String destinations = DataModel.sSelf
				.removeString(DMKeys.COMPOSE_DESTINATION_CONTACTS);
		if (resultCode == ADDCONVERSATIONS) {
			String dist2[] = Utilities.split(new StringBuffer(destinations),
					";");
			String s[] = null;
			String sp = getParticipantInfoWithUserName();
			s = Utilities.split(new StringBuffer(sp), ";");
			sp = "";
			if (dist2.length == 1) {
				for (int i = 0; i < s.length; i++) {
					if (BusinessProxy.sSelf
							.parseUsernameInformation(s[i])
							.trim()
							.toLowerCase()
							.equalsIgnoreCase(
									BusinessProxy.sSelf
											.parseUsernameInformation(dist2[0])
											.trim().toLowerCase())) {
						if (BusinessProxy.sSelf
								.parseUsernameInformation(s[i])
								.trim()
								.toLowerCase()
								.equalsIgnoreCase(
										SettingData.sSelf.getUserName().trim()
												.toLowerCase()))
							showSimpleAlert("Message",
									"You are already in group chat!");
						else
							showSimpleAlert("Message",
									"Friend(s) already in group chat!");
						return;
					}
				}
			}
			destinations = "";
			int met = 0;
			for (int i = 0; i < dist2.length; i++) {
				boolean m = false;
				for (int j = 0; j < s.length; j++) {
					if (BusinessProxy.sSelf
							.parseUsernameInformation(s[j])
							.trim()
							.toLowerCase()
							.equalsIgnoreCase(
									BusinessProxy.sSelf
											.parseUsernameInformation(dist2[i])
											.trim().toLowerCase())) {
						met++;
						m = true;
					}
				}
				if (destinations.indexOf(dist2[i]) == -1 && !m)
					destinations = destinations + dist2[i] + ";";
			}
			if (met == dist2.length) {
				showToast("Friend(s) already in group chat!");
				return;
			}
			request = new Request(ID_ADD_TO_CONVERSATION, "url");
			// if ((conversationList != null && conversationList.isGroup == 1 &&
			// getIntent().getStringExtra(Constants.CONVERSATION_ID).startsWith("NP")))
			// {
			// //System.out.printlnprintln("--------------conversationList.conversationId current  : "+getIntent().getStringExtra(Constants.CONVERSATION_ID));
			// }
			if ((conversationList != null && conversationList.isGroup == 0
					&& conversationList.hasBeenViewed != null && !conversationList.hasBeenViewed
						.equalsIgnoreCase("never"))
					|| getIntent().getStringExtra(Constants.CONVERSATION_ID)
							.startsWith("NP")
					|| (conversationList != null && conversationList.isGroup == 0))// if(1==1)
			{
				request.p2p_2_group = true;
				sp = getParticipantInfoWithUserName();
				s = Utilities.split(new StringBuffer(sp), ";");
				sp = "";
				for (int i = 0; i < s.length; i++) {
					if (!BusinessProxy.sSelf
							.parseUsernameInformation(s[i])
							.trim()
							.toLowerCase()
							.equalsIgnoreCase(
									SettingData.sSelf.getUserName().trim()
											.toLowerCase()))
						sp = sp + s[i].trim().toLowerCase() + ";";
				}
				addNewConversation(sp + destinations, true);
				initParticipantInfo();
				updateParticepentLabel();
				if (lDialogInfo != null && lDialogInfo.isShowing()) {
					lDialogInfo.dismiss();
					flagAnimNotPlay = false;
				}
				// onBackPressed();
				return;
			} else {
				request.p2p_2_group = false;
				if (destinations != null && destinations.trim().length() > 0) {
					String destinationsUserNameArr[] = null;
					String dist[] = Utilities.split(new StringBuffer(
							destinations), ";");
					if (dist.length == 1
							&& BusinessProxy.sSelf
									.parseUsernameInformation(dist[0])
									.toLowerCase()
									.equalsIgnoreCase(
											SettingData.sSelf.getUserName()
													.toLowerCase())) {
						showToast("You can not add self!");
						return;
					}
					String actorUserid = "";
					for (int i = 0; i < dist.length; i++) {
						actorUserid = actorUserid + dist[i] + ";";
					}
					actorUserid = actorUserid.substring(0,
							actorUserid.length() - 1);
					// String sp = getParticipantInfo();
					dist2 = Utilities
							.split(new StringBuffer(destinations), ";");
					destinationsUserNameArr = Utilities.split(new StringBuffer(
							destinations), ";");
					if (destinationsUserNameArr.length + dist2.length > 10) {
						showToast("You can not add more than 9 friends!");
						return;
					}
					sp = getParticipantInfoWithUserName();
					s = Utilities.split(new StringBuffer(sp), ";");
					sp = "";
					for (int i = 0; i < s.length; i++) {
						if (!BusinessProxy.sSelf
								.parseUsernameInformation(s[i])
								.trim()
								.toLowerCase()
								.equalsIgnoreCase(
										SettingData.sSelf.getUserName().trim()
												.toLowerCase()))
							sp = sp + s[i].trim().toLowerCase() + ";";
					}
					addParticipentAfterResponse = sp + destinations;
					// addUserConversation( sp+destinations, true);
					request.destinationsUserNameArr = destinationsUserNameArr;
					if (!ConversationsActivity.this.checkInternetConnection()) {
						ConversationsActivity.this.networkLossAlert();
						return;
					}
					request.execute("LikeDislike");
				}
			}
		} else if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case MUC_PIC_CROP:
				if (data != null && data.getData() != null) {
					Uri uri = data.getData();
					AppUtil.capturedPath1 = AppUtil.getPath(uri, mContext);
				}
				CompressImage compressImage = new CompressImage(mContext);
				AppUtil.capturedPath1 = compressImage
						.compressImage(AppUtil.capturedPath1);
				mImagesPath.add(mucGroupPic = AppUtil.capturedPath1);
				mucFullPic.setImageURI(Uri.parse(AppUtil.capturedPath1));
				break;
			case MUC_CAMERA:
				if (lDialogInfo != null && lDialogInfo.isShowing()) {
					if (data != null && data.getData() != null) {
						Uri uri = data.getData();
						AppUtil.capturedPath1 = AppUtil.getPath(uri, mContext);
					}
					compressImage = new CompressImage(mContext);
					AppUtil.capturedPath1 = compressImage
							.compressImage(AppUtil.capturedPath1);
					mImagesPath.add(mucGroupPic = AppUtil.capturedPath1);
					mucFullPic.setImageURI(Uri.parse(AppUtil.capturedPath1));
					// performCrop(MUC_PIC_CROP);
				}
				break;
			case MUC_PIC:
				if (lDialogInfo != null && lDialogInfo.isShowing()) {
					if (data != null && data.getData() != null) {
						Uri uri = data.getData();
						AppUtil.capturedPath1 = AppUtil.getPath(uri, mContext);
						BitmapFactory.Options o = new BitmapFactory.Options();
						o.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(getAbsolutePath(uri), o);
						int imageHeight = o.outHeight;
						int imageWidth = o.outWidth;
						if (imageHeight < Constants.CROP_DIMEN
								&& imageWidth < Constants.CROP_DIMEN) {
							mImagesPath.clear();
							mImagesPath.add(AppUtil.capturedPath1);
							Bitmap selectedImage = BitmapFactory
									.decodeFile(AppUtil.capturedPath1);
							mucFullPic.setImageBitmap(selectedImage);
						} else {
							compressImage = new CompressImage(mContext);
							AppUtil.capturedPath1 = compressImage
									.compressImage(AppUtil.capturedPath1);
							mucFullPic.setImageURI(Uri
									.parse(AppUtil.capturedPath1));
						}
						mucGroupPic = AppUtil.capturedPath1;
					}
					mImagesPath.clear();
					mImagesPath.add(mucGroupPic = AppUtil.capturedPath1);
					// performCrop(MUC_PIC_CROP);
					return;
				}
				break;
			case POSITION_VOICE:
				mVoicePath = getPath(data.getData());
				attachment_panel.setVisibility(View.INVISIBLE);
				String textMessage = messageBox.getText().toString();
				if (null == mVoicePath && null == mPicturePath
						&& 0 == textMessage.trim().length()) {
					showSimpleAlert("Error",
							"Please add content to send message");
					return;
				}
				if (!ConversationsActivity.this.checkInternetConnection()) {
					ConversationsActivity.this.networkLossAlert();
					return;
				}
				Vector<String> mPicturePathCanvasLocal = new Vector<String>();
				Vector<String> mPicturePathLocal = new Vector<String>();
				if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0)
					for (String ss : mPicturePathCanvas) {
						mPicturePathCanvasLocal.add(ss);
					}
				if (mPicturePath != null && mPicturePath.size() > 0) {
					for (String ss : mPicturePath) {
						mPicturePathLocal.add(ss);
					}
				}
				mPicturePathCanvas.clear();
				mPicturePath.clear();
				UpdateUi task = new UpdateUi();
				task.mPicturePath = mPicturePath;
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						new String[] { SEND_MESSAGE });

				if (mPicturePathLocal != null && mPicturePathLocal.size() > 0)
					for (String ss : mPicturePathLocal) {
						mPicturePath.clear();
						task = new UpdateUi();
						task.mPicturePath.add(ss);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
								new String[] { SEND_MESSAGE });
					}
				if (mPicturePathCanvasLocal != null
						&& mPicturePathCanvasLocal.size() > 0)
					for (String ss : mPicturePathCanvasLocal) {
						mPicturePath.clear();
						task = new UpdateUi();
						task.mPicturePath.add(ss);
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
								new String[] { SEND_MESSAGE });
					}
				mPicturePathCanvas.clear();
				mPicturePath.clear();
				((ImageView) findViewById(R.id.attachement))
						.setImageResource(R.drawable.attachment_unsel);
				break;
			case POSITION_MULTI_SELECT_PICTURE:
				// String[] all_path = data.getStringArrayExtra("all_path");
				// imagePathPos = (int[])
				// DataModel.sSelf.removeObject(DMKeys.MULTI_IMAGES+"IDINT");
				String s[] = (String[]) DataModel.sSelf
						.removeObject(DMKeys.MULTI_IMAGES);
				if (s != null) {
					mPicturePath = new Vector<String>();
					for (int i = 0; i < s.length; i++) {
						try {
							mPicturePath.add(s[i]);

						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					s = null;
				}
				mVoicePath = (String) DataModel.sSelf
						.removeObject(DMKeys.VOICE_PATH);
				((ImageView) findViewById(R.chat_panel.sendButton))
						.performClick();
				break;
			case POSITION_CAMERA_PICTURE:
				if (mPicturePath.contains(cameraImagePath)) {
					showSimpleAlert("Error",
							"This image has already been attached!");
					return;
				}
				Bitmap bm = null;
				compressImage = new CompressImage(ConversationsActivity.this);
				cameraImagePath = compressImage.compressImage(cameraImagePath);
				if ((frontCam && rearCam) || (!frontCam && rearCam)) {
					BitmapFactory.Options bfo = new BitmapFactory.Options();
					bfo.inDither = false; // Disable Dithering mode
					bfo.inPurgeable = true;
					bm = BitmapFactory.decodeFile(cameraImagePath, bfo);
					// bm =ImageUtils.rotate(bm,90,cameraImagePath);
					// bm = ImageUtils.imageRotateafterClick(bm,
					// cameraImagePath);
				}
				if (frontCam && !rearCam) {
					// bm = ImageUtils.getImageFor(cameraImagePath, 4);
					Bitmap bmp = BitmapFactory.decodeFile(cameraImagePath);
					// ImageUtils.fixOrientation(cameraImagePath,bmp);
					// ImageUtils.rotateImage(cameraImagePath,bmp);
					bmp.recycle();
					// ImageUtils.rotateAndSaveImage(cameraImagePath);
					bm = ImageUtils.getImageFor(cameraImagePath, 4);
				}
				// Bitmap bm = ImageUtils.getImageFor(cameraImagePath, 4);
				// Bitmap bm = BitmapFactory.decodeFile(cameraImagePath);
				// bm =ImageUtils.rotate(bm,90,cameraImagePath);
				// bm=ImageUtils.imageRotateafterClick(bm,cameraImagePath);
				// //System.out.printlnprintln("newSize==="+ImageUtils.getImageSize(bm));
				// ImageUtils.fixOrientation(cameraImagePath, bm);
				if (isSizeReachedMaximum(cameraImagePath)) {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached));
					return;
				}
				mPicturePath.add(cameraImagePath);
				((ImageView) findViewById(R.chat_panel.sendButton))
						.performClick();

				// LinearLayout layout = (LinearLayout)
				// findViewById(R.id.composeScreen_attachedImageLayout);
				// image_count.setText("" + mPicturePath.size());
				// if (mPicturePath != null && mPicturePath.size() <= 0)
				// image_count.setVisibility(View.GONE);
				// else
				// image_count.setVisibility(View.VISIBLE);
				// imId = -1;
				// if (mPicturePathId != null)
				// mPicturePathId.add(imId);
				// else {
				// mPicturePathId = new Vector<Integer>();
				// mPicturePathId.add(imId);
				// }
				break;
			case POSITION_PICTURE_RT_CANVAS:
				String capturedPath = getPath(data.getData());
				if (mPicturePathCanvas.contains(capturedPath)) {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached));
					return;
				}
				Bitmap bm1 = ImageUtils.getImageFor(capturedPath, 4);
				if (isSizeReachedMaximum(bm1)) {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached));
					return;
				}
				if (mPicturePath.size() + mPicturePathCanvas.size() > 20) {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached));
				}
				mPicturePathCanvas.add(capturedPath);
				((ImageView) findViewById(R.chat_panel.sendButton))
						.performClick();

				// layout = (LinearLayout)
				// findViewById(R.id.composeScreen_attachedImageLayout);
				// image_count_dodle.setText("" + mPicturePathCanvas.size());
				// if (mPicturePathCanvas != null
				// && mPicturePathCanvas.size() <= 0)
				// image_count_dodle.setVisibility(View.GONE);
				// else
				// image_count_dodle.setVisibility(View.VISIBLE);
				// imId = -1;
				// if (mPicturePathId != null)
				// mPicturePathId.add(imId);
				// else {
				// mPicturePathId = new Vector<Integer>();
				// mPicturePathId.add(imId);
				// }

				break;
			case POSITION_PICTURE:
				capturedPath = getPath(data.getData());
				// if(mPicturePath!=null)
				// mPicturePath = new Vector<String>() ;
				if (mPicturePath.contains(capturedPath)) {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached));
					return;
				}
				bm1 = ImageUtils.getImageFor(capturedPath, 4);
				if (isSizeReachedMaximum(bm1)) {
					// if (BusinessProxy.sSelf.MaxSizeData == 2) {
					// showSimpleAlert(
					// "Error",
					// "You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
					// } else {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached));
					// }
					return;
				}
				mPicturePath.add(capturedPath);

				LinearLayout layout = (LinearLayout) findViewById(R.id.composeScreen_attachedImageLayout);
				image_count.setText("" + mPicturePath.size());
				if (mPicturePath != null && mPicturePath.size() <= 0)
					image_count.setVisibility(View.GONE);
				else
					image_count.setVisibility(View.VISIBLE);
				imId = -1;
				if (mPicturePathId != null)
					mPicturePathId.add(imId);
				else {
					mPicturePathId = new Vector<Integer>();
					mPicturePathId.add(imId);
				}
				// quickSend();//change by akm
				// addImageToList(capturedPath, layout, MEDIA_TYPE_IMAGE, bm1);
				break;
			case POSITION_VIDEO:
				try {
					capturedPath = getPath(data.getData());
				} catch (Exception e) {
					capturedPath = cameraImagePath;// getPath(data.getData());
				}
				if (Utilities.getFileInputStream(capturedPath) == null)
					capturedPath = Utilities.getVideoLastVideoFile(this);
				if (mVideoPath != null && mVideoPath.contains(capturedPath)) {
					showSimpleAlert("Error",
							getString(R.string.max_attachment_reached_update));
					return;
				}
				if (isSizeReachedMaximum(capturedPath)) {
					if (BusinessProxy.sSelf.MaxSizeData == 2) {
						showSimpleAlert(
								"Error",
								"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
					} else {
						showSimpleAlert(
								"Error",
								getString(R.string.max_attachment_reached_update));
					}
					return;
				}
				mVideoPath = capturedPath;
				((ImageView) findViewById(R.chat_panel.sendButton))
						.performClick();

				// layout = (LinearLayout)
				// findViewById(R.id.composeScreen_attachedVideoLayout);
				// showHideVideoCount();
				break;
			}
		}
		calculateTotSizeAfterClear();
	}

	public String getPath(Uri uri) {

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			// Cursor cursor = managedQuery(uri, projection, null, null, null);
			Cursor cursor = getContentResolver().query(uri, projection, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();
		}

	}

	public Cursor getCursor() {
		try {
			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
					MessageTable.TABLE,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ? and "
							+ MessageTable.MESSAGE_TYPE + " !=?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "",
							"" + Message.MESSAGE_TYPE_SYSTEM_MESSAGE }, null,
					null,
					// MessageTable.INSERT_TIME + " ASC"
					MessageTable.DIRECTION + " DESC, " + MessageTable.SORT_TIME
							+ " ASC"
			// order by dir desc, time desc
					);
			CONVERSATIONID = getIntent().getStringExtra(
					Constants.CONVERSATION_ID);
			return cursor;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	// public int getCursorCount() {
	// Cursor cursor = getContentResolver().query(
	// MediaContentProvider.CONTENT_URI_INBOX,
	// null,
	// MessageTable.CONVERSATION_ID + " = ? and "+MessageTable.USER_ID
	// +" = ? and "+MessageTable.MESSAGE_TYPE +" !=?",
	// new String[] { getIntent().getStringExtra(
	// Constants.CONVERSATION_ID),BusinessProxy.sSelf.getUserId()+"",""+Message.MESSAGE_TYPE_SYSTEM_MESSAGE
	// }, null);
	// int count = cursor.getCount();
	// cursor.close();
	//
	// return count;
	// }
	String usersForthisConversition = "";

	public String getParticipantInfo() {
		try {

			String usersForthisConversition = "";
			Message message = new Message();
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null,
					MessageTable.CONVERSATION_ID + " = ?  and "
							+ MessageTable.USER_ID + " = ?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "" }, null);
			cursor.moveToFirst();

			String s = cursor.getString(cursor
					.getColumnIndex(MessageTable.PARTICIPANT_INFO));
			if (s != null) {
				String pi[] = Utilities.split(new StringBuffer(s),
						Constants.ROW_SEPRETOR);
				ParticipantInfo participantInfo = new ParticipantInfo();
				message.participantInfo = new Vector<ParticipantInfo>();
				// System.out
				// .println("------------usersForthisConversition----rraw------: "
				// + s);
				try {
					for (int i = 0; i < pi.length; i++) {
						participantInfo = new ParticipantInfo();
						String info[] = Utilities
								.split(new StringBuffer(pi[i]),
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
			// s =
			// cursor.getString(cursor.getColumnIndex(MessageTable.MSG_TXT));
			// System.out
			// .println("------------usersForthisConversition------txt----: "
			// + s);
			// s = cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
			// System.out
			// .println("------------usersForthisConversition------MFU_ID----: "
			// + s);
			// s = cursor
			// .getString(cursor.getColumnIndex(MessageTable.MESSAGE_ID));
			// System.out
			// .println("------------usersForthisConversition------MESSAGE_ID----: "
			// + s);
			// //System.out.printlnprintln("------------usersForthisConversition----------: "
			// + usersForthisConversition);
			cursor.close();
			if (usersForthisConversition.indexOf(";;") != -1) {
				usersForthisConversition = usersForthisConversition.replaceAll(
						";;", ";");
			}
			if (usersForthisConversition != null
					&& usersForthisConversition.trim().length() > 0)
				this.usersForthisConversition = usersForthisConversition;

			return usersForthisConversition;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.printlnprintln("Rocketalk--------dist is null : "
			// + e.getMessage());
		}
		return "";
	}

	public void updateMUCSubject() {
		try {
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.MFU_ID + " !=?  and "
							+ MessageTable.USER_ID + " = ?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID), "-1",
							BusinessProxy.sSelf.getUserId() + "" }, null);
			cursor.moveToLast();
			if (cursor.getCount() > 0) {
				conversationList.subject = cursor.getString(cursor
						.getColumnIndex(MessageTable.SUBJECT));
				conversationList.imageFileId = cursor.getString(cursor
						.getColumnIndex(MessageTable.GROUP_PIC));
			}
			cursor.close();
			if (conversationList.subject != null
					|| conversationList.subject.trim().length() > 0) {
				if (title == null)
					title = ((EmojiconTextView) findViewById(R.conversation.title));
				title.setText(conversationList.subject);
				getIntent().putExtra(Constants.TITLE, conversationList.subject);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void initParticipantInfo() {
		try {

			String usersForthisConversition = "";
			Message message = new Message();
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "" }, null);
			cursor.moveToLast();

			String sub = cursor.getString(cursor
					.getColumnIndex(MessageTable.SUBJECT));
			String muc_pic = cursor.getString(cursor
					.getColumnIndex(MessageTable.GROUP_PIC));
			String s = cursor.getString(cursor
					.getColumnIndex(MessageTable.PARTICIPANT_INFO));
			try {
				conversationList.isGroup = cursor.getInt(cursor
						.getColumnIndex(MessageConversationTable.IS_GROUP));
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				conversationList.isLeft = cursor.getInt(cursor
						.getColumnIndex(MessageConversationTable.IS_LEFT));
			} catch (Exception e) {
				// TODO: handle exception
			}
			cursor.close();
			if (s != null) {

				if (s.indexOf(Constants.ROW_SEPRETOR + "false���") != -1) {
					s = s.replaceAll(Constants.ROW_SEPRETOR + "false���",
							"");
				}

				String pi[] = Utilities.split(new StringBuffer(s),
						Constants.ROW_SEPRETOR);
				ParticipantInfo participantInfo = new ParticipantInfo();
				if (sub != null)
					conversationList.subject = sub;
				if (muc_pic != null)
					conversationList.imageFileId = muc_pic;
				conversationList.participantInfo = new Vector<ParticipantInfo>();
				try {
					ImageView temp = new ImageView(ConversationsActivity.this);
					for (int i = 0; i < pi.length; i++) {
						participantInfo = new ParticipantInfo();
						String info[] = Utilities
								.split(new StringBuffer(pi[i]),
										Constants.COL_SEPRETOR);
						if (info != null && info.length > 1) {

							participantInfo.isSender = Boolean
									.parseBoolean(info[0]);
							participantInfo.userName = info[1];
							if (info.length >= 3)
								participantInfo.firstName = info[2];
							if (info.length >= 4)
								participantInfo.lastName = info[3];

							ImageDownloader imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									temp, ConversationsActivity.this);

						}

						conversationList.participantInfo.add(participantInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public String getParticipantInfoWithUserName() {
		try {

			String usersForthisConversition = "";
			Message message = new Message();
			Cursor cursor = getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "" }, null);
			cursor.moveToLast();

			String s = cursor.getString(cursor
					.getColumnIndex(MessageTable.PARTICIPANT_INFO));
			if (s != null) {
				String pi[] = Utilities.split(new StringBuffer(s),
						Constants.ROW_SEPRETOR);
				ParticipantInfo participantInfo = new ParticipantInfo();
				message.participantInfo = new Vector<ParticipantInfo>();
				// System.out
				// .println("------------usersForthisConversition----rraw------: "
				// + s);
				try {
					for (int i = 0; i < pi.length; i++) {
						participantInfo = new ParticipantInfo();
						System.out
								.println("----------met------------pi[i]----------: "
										+ pi[i]);
						String info[] = Utilities
								.split(new StringBuffer(pi[i]),
										Constants.COL_SEPRETOR);
						// participantInfo.isSender=Boolean.parseBoolean(info[0]);
						// participantInfo.userName=info[1];
						// message.participantInfo.add(participantInfo);
						if (info.length > 3)
							usersForthisConversition = usersForthisConversition
									+ info[2] + " " + info[3] + "<" + info[1]
									+ ">;";
						else if (info.length > 2)
							usersForthisConversition = usersForthisConversition
									+ info[2] + "<" + info[1] + ">;";
						else if (info.length > 1)
							usersForthisConversition = usersForthisConversition
									+ "<" + info[1] + ">;";

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// s =
			// cursor.getString(cursor.getColumnIndex(MessageTable.MSG_TXT));
			// System.out
			// .println("------------usersForthisConversition------txt----: "
			// + s);
			// s = cursor.getString(cursor.getColumnIndex(MessageTable.MFU_ID));
			// System.out
			// .println("------------usersForthisConversition------MFU_ID----: "
			// + s);
			// s = cursor
			// .getString(cursor.getColumnIndex(MessageTable.MESSAGE_ID));
			// System.out
			// .println("------------usersForthisConversition------MESSAGE_ID----: "
			// + s);
			// //System.out.printlnprintln("----------met------------usersForthisConversition----------: "
			// + usersForthisConversition);
			cursor.close();
			if (usersForthisConversition.indexOf(";;") != -1) {
				usersForthisConversition = usersForthisConversition.replaceAll(
						";;", ";");
			}
			return usersForthisConversition;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
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

			View retView = inflater.inflate(R.layout.left_menu_grid, parent,
					false);

			return retView;
		}
	}

	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onThumbnailResponse(final ThumbnailImage value,
			final byte[] data) {
		ImageDownloader downloader = new ImageDownloader();

		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (lDialogInfo != null && lDialogInfo.isShowing()) {
					// CImageView p1_image, p2_image, p3_image, p4_image,
					// p5_image,
					// p6_image,
					// p7_image, p8_image, p9_image, p10_image;
					try {

						ParticipantInfo participantInfo = (ParticipantInfo) p1_image
								.getTag();
						ImageDownloader imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p1_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						UserStatus userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline1))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline1))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p2_image.getTag();

						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p2_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline2))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline2))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p3_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p3_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline3))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline3))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p4_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p4_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline4))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline4))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p5_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p5_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline5))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline5))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p6_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p6_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline6))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline6))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p7_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p7_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline7))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline7))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p8_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p8_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline8))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline8))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p9_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p9_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline9))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline9))
									.setImageResource(R.drawable.offline_icon_chat);
						}

						participantInfo = (ParticipantInfo) p10_image.getTag();
						imageManager = new ImageDownloader();
						imageManager.download(participantInfo.userName,
								p10_image, ConversationsActivity.this,
								ImageDownloader.TYPE_THUMB_BUDDY);
						userStatus = ImageDownloader
								.getUserInfo(participantInfo.userName);
						if (userStatus != null && userStatus.onLineOffline == 1) {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline10))
									.setImageResource(R.drawable.online_icon_chat);
						} else {
							((CImageView) lDialogInfo
									.findViewById(R.conversation_option_panel.online_offline10))
									.setImageResource(R.drawable.offline_icon_chat);
						}
					} catch (Exception e) {

					}

				} else {
					try {

						String url = new String(data);
						if (images.containsKey(url.hashCode())) {
							((CImageView) images.get(url.hashCode()))
									.setImageBitmap(value.mBitmap);
							((CImageView) images.get(url.hashCode()))
									.setBackgroundDrawable(null);
							images.remove(url.hashCode());
							return;
						}
						// if (activityAdapter != null) {
						// activityAdapter.notifyDataSetChanged();
						// activityAdapter.notifyDataSetInvalidated();
						// }
						// if (listViewActivity != null)
						// listViewActivity.invalidateViews();
					} catch (Exception e) {
						// TODO: handle exception
					}
					// UpdateUi task = new UpdateUi();
					// task.execute(new String[] { INVALIDATE_LIST });
				}
			}
		});
	}

	public void refreshList() {
		if (activityAdapter != null)
			activityAdapter.notifyDataSetChanged();
	}

	Message message;
	ParticipantInfo infoMessage;

	@Override
	public void onClick(final View v) {
		// //System.out.printlnprintln("---------------------1232onClick");
		// if(v.getId()!=R.chat_panel.sendButton)
		// Utilities.closeSoftKeyBoard(messageBox,
		// this);

		// showToast("ID==="+v.getId());
		closeCopy();
		// if(listViewActivity!=null)
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
		Object obj = v.getTag();
		if (obj instanceof Message)
			message = (Message) obj;
		if (obj instanceof ParticipantInfo)
			infoMessage = (ParticipantInfo) obj;
		// if(message.messageId!=null)

		super.onClick(v);
		switch (v.getId()) {
		case R.id.chat_smiley:
			if (isShowSmiley) {
				smileyView.setVisibility(View.GONE);
				showSoftKeyboard(messageBox);

			} else {
				hideSoftKeyboard(messageBox);
				smileyView.setVisibility(View.VISIBLE);
			}
			isShowSmiley = !isShowSmiley;
			break;
		case R.community_chat.msgBox:
			smileyView.setVisibility(View.GONE);
			showSoftKeyboard(v);
			isShowSmiley = false;
			break;
		case R.conversation.load_prevoius_message2:

			cidRefresh.remove(getIntent().getStringExtra(
					Constants.CONVERSATION_ID));
			FeedRequester.feedTaskConversationMessages = null;
			CheckNewDataOnServer task2 = new CheckNewDataOnServer();
			task2.execute(new String[] { "load" });
			break;
		case R.chat_panel.info_new_message:
		case R.conversation.new_message:
			// showToast("moving") ;
			// listViewActivity.post(new Runnable(){
			// public void run() {
			// listViewActivity.setSelection(activityAdapter.getCursor().getCount()
			// - 1);
			// }});
			//
			listViewActivity
					.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			listViewActivity.setSelection(activityAdapter.getCursor()
					.getCount() - 1);
			Log.v("ConverstionsActivity","Manoj"+"3404");
			new_message.setText("");
			new_message.setVisibility(View.GONE);
			new_message_layout.setVisibility(View.GONE);
			break;
		case R.message_row_right.notification:
		case R.message_row.notification:

			// if (FeedRequester.feedTaskConversationMessagesRefresh == null
			// || FeedRequester.feedTaskConversationMessagesRefresh
			// .getStatus() == Status.FINISHED) {
			// FeedRequester.requestConversationMessagesRefresh(
			// ConversationsActivity.this, getIntent()
			// .getStringExtra(Constants.CONVERSATION_ID),this);
			//
			// }
			// if (FeedRequester.feedTaskConversationMessagesRefresh != null)
			// FeedRequester.feedTaskConversationMessagesRefresh
			// .setHttpSynch(ConversationsActivity.this);

			if (FeedRequester.feedTaskConversationMessagesRefresh == null
					|| FeedRequester.feedTaskConversationMessagesRefresh
							.getStatus() == Status.FINISHED) {
				FeedRequester.requestConversationMessagesRefresh(
						ConversationsActivity.this,
						getIntent().getStringExtra(Constants.CONVERSATION_ID),
						this);
				if (FeedRequester.feedTaskConversationMessagesRefresh != null) {
					((TextView) v).setText(getResources().getString(
							R.string.wait_loading));
				} else {
					getContentResolver().delete(
							MediaContentProvider.CONTENT_URI_INBOX,
							MessageTable.MESSAGE_ID + " = '-999' ", null);
					if (activityAdapter != null) {
						activityAdapter.getCursor().requery();
						activityAdapter.notifyDataSetChanged();
					}
					listViewActivity.invalidate();
				}
			}
			if (FeedRequester.feedTaskConversationMessagesRefresh != null)
				FeedRequester.feedTaskConversationMessagesRefresh
						.setHttpSynch(ConversationsActivity.this);
			break;

		case R.id.coverpage_view_this_message:
			initFriendMessage();
			break;
		case R.id.coverpage_view_profile:

			// msg = (InboxMessage) mMessage;
			// sender = msg.mSenderName ;
			// msg = null ;
			// if (sender.indexOf('<') != -1 && sender.indexOf('>') != -1)
			// sender = sender.substring(sender.indexOf('<') + 1,
			// sender.lastIndexOf('>'));
			// String userName = "";
			// if (infoMessage != null)
			// userName = infoMessage.userName;
			// DataModel.sSelf.storeObject(DMKeys.USER_PROFILE, userName);
			// Intent intent = new Intent(this, WebviewActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
			// Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// intent.putExtra("ActivityForResult", true);
			// startActivityForResult(intent, 100);// (intent);
			break;
		case R.id.coverpage_block:
			DialogInterface.OnClickListener blockHandler = null;
			// if (inboxElement.mSenderName.indexOf(';') != -1)
			// inboxElement.mSenderName = inboxElement.mSenderName.substring(0,
			// inboxElement.mSenderName.indexOf(';'));
			String userNameCover = "";
			if (infoMessage != null)
				userNameCover = infoMessage.userName;
			final String user = userNameCover;
			// final String user = inboxElement.mSenderName;
			blockHandler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					StringBuilder text = new StringBuilder("BlockUser::User##");
					text.append(user);
					if (BusinessProxy.sSelf.sendNewTextMessage(
							"User Manager<a:usermgr>", text.toString(), true)) {
						StringBuffer buff = new StringBuffer(user);
						buff.append(" has been blocked.");
						buff.append("\n");
						buff.append("To unblock friend, view the 'Blocked users' list from the options menu and select 'Unblock'!");
						showAlertMessage("Info", buff.toString(), null, null,
								null);
						buff = null;
					}
				}
			};
			showAlertMessage(
					"Confirm",
					"Do you want to block this user?",
					new int[] { DialogInterface.BUTTON_POSITIVE,
							DialogInterface.BUTTON_NEGATIVE },
					new String[] { "Yes", "No" },
					new DialogInterface.OnClickListener[] { blockHandler, null });
			break;
		case 1420:
//			RtAnimFeed feed = (RtAnimFeed) v.getTag();
//			openDialogFullScreen(0, feed);

//			BusinessProxy.sSelf.addPush.pushAdd(BusinessProxy.sSelf.addPush.mContext);
			break;
		case R.id.coverpage_mark_safe:
			/*
			 * msg = (InboxMessage) mMessage; sender = msg.mSenderName ; msg =
			 * null ; msg = (InboxMessage) mMessage; sender = msg.mSenderName ;
			 * ((ScrollView)
			 * findViewById(R.id.coverpage)).setVisibility(View.GONE); if
			 * (inboxElement.mSenderName.indexOf(';') != -1)
			 * inboxElement.mSenderName = inboxElement.mSenderName.substring(0,
			 * inboxElement.mSenderName.indexOf(';')); String user2 =
			 * inboxElement.mSenderName;
			 */
			String UserNameMake = "";
			if (infoMessage != null)
				UserNameMake = infoMessage.userName;
			StringBuilder text = new StringBuilder("CreateBkmrk::User##");
			text.append(UserNameMake);
			if (BusinessProxy.sSelf.sendNewTextMessage(
					"Friend Manager<a:friendmgr>", text.toString(), true)) {
				StringBuffer buff = new StringBuffer("");
				// buff.append(" has been blocked.");
				// buff.append("\n");
				buff.append("Your request has been sent! Your friend list will be updated shortly.");
				showAlertMessage("Info", buff.toString(), null, null, null);
				buff = null;
			}
			initFriendMessage();
			break;
		case R.id.coverpage_privicy:
			// Intent intentPrivacy = new Intent(this, SettingUIActivity.class);
			// intentPrivacy.putExtra("PAGE_ID_TO_SHOW", (byte) 5);
			// intentPrivacy.putExtra("ActivityForResult", true);
			// startActivityForResult(intentPrivacy, 100);// (intent);
			break;
		// case R.message_row.other_image_top_profile:
		case R.message_row_right.other_image:
		case R.message_row.me_image:
		case 10001:
			try {
				ParticipantInfo participantInfo = (ParticipantInfo) v.getTag();
				DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
						participantInfo.userName);
				Intent intent = new Intent(ConversationsActivity.this,
						ProfileViewActivity.class);
				intent.putExtra("USERID", participantInfo.userName);
				intent.putExtra("CallFrom", 1);
				startActivity(intent);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		// case R.message_row_right.other_image:
		// showToast("my profile");
		// participantInfo = (ParticipantInfo)v.getTag() ;
		// break;
		// case R.message_row.video:
		case 100012:
			// if(longPressFlag)
			// return;
			if (voiceIsPlaying) {
				showSimpleAlert("Info",
						"Other media is running please stop first!");
				return;
			}
			listViewActivity
					.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);

			VideoUrl videoUrl = (VideoUrl) v.getTag();
			try {
				playFromURLMain(VDownloader.getInstance().getPlayUrl(
						videoUrl.playUrl));// (String)v.getTag()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		// case R.message_row_right.video:
		// playFromURLMain(message.video_content_urls);
		// break;
		case 1000:
			// if(longPressFlag)
			// return;
			if (voiceIsPlaying) {
				showSimpleAlert("Info",
						"Other media is running please stop first!");
				return;
			}
			ArrayList<String> imagesPathl = new ArrayList<String>();
			ArrayList<String> imagesPathlocal = new ArrayList<String>();
			Feed.Entry entry = new Feed().new Entry();// feed.entry.get(Integer.parseInt(index));
			entry.media = new Vector<String>();

			String s[] = Utilities.split(new StringBuffer(
					message.image_content_urls), Constants.COL_SEPRETOR);

			for (int i = 0; i < s.length; i++) {
				imagesPathl.add(s[i]);
			}

			s = Utilities.split(new StringBuffer(message.image_content_urls),
					Constants.COL_SEPRETOR);
			boolean isLocal = true;
			for (int i = 0; i < s.length; i++) {
				imagesPathlocal.add(s[i]);
				if (s[i].startsWith("http"))
					isLocal = false;
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

			Utilities.setInt(ConversationsActivity.this, "chatlistpos",
					listViewActivity.getFirstVisiblePosition());
			int top = (v == null) ? 0 : v.getTop();
			Utilities.setInt(ConversationsActivity.this, "chatlisttop", top);

			if (isLocal) {
				// Intent intentpics = new Intent(ConversationsActivity.this,
				// StreemMultiPhotoViewer.class);
				// startActivityForResult(intentpics, Constants.ResultCode);
				Intent intent = new Intent(this, MultiPhotoViewer.class);
				intent.putStringArrayListExtra("DATA",
						(ArrayList<String>) imagesPathlocal);
				startActivityForResult(intent, Constants.ResultCode);
			} else {
				Intent intentpics = new Intent(ConversationsActivity.this,
						StreemMultiPhotoViewer.class);
				startActivityForResult(intentpics, Constants.ResultCode);
			}

			break;
		// case R.message_row_right.voice:
		// media_play_layout.setVisibility(View.VISIBLE);
		// baradd = (SeekBar) media_play_layout
		// .findViewById(R.id.mediavoicePlayingDialog_progressbar);
		// if (((ImageView) media_play_layout.findViewById(R.id.media_play)) !=
		// null)
		// ((ImageView) media_play_layout.findViewById(R.id.media_play))
		// .setOnClickListener(playerClickEvent);
		//
		// total_autio_time = ((TextView) media_play_layout
		// .findViewById(R.id.audio_counter_max));
		// played_autio_time = ((TextView) media_play_layout
		// .findViewById(R.id.audio_counter_time));
		// total_autio_time.setText("00:00)");
		// played_autio_time.setText("(00:00 of ");
		// openPlayScreen(
		// Downloader.getInstance().getPlayUrl(
		// message.voice_content_urls), false,
		// media_play_layout);
		// v.invalidate();
		// // this.handler.post(new Runnable() {
		// // //
		// //
		// http://media-rtd4.onetouchstar.com/2012/09/24/05/1195675151493550080.mp3
		// // @Override
		// // public void run() {
		// //
		// //
		// // }
		// // });
		// break;
		case R.message_row_right.voice_play:
		case R.message_row_right.voice:
		case R.message_row.voice:
		case R.message_row.voice_play:

			if (Downloader.getInstance().check(message.voice_content_urls)
					|| !message.voice_content_urls.startsWith("http")) {
				media_play_layout.setVisibility(View.VISIBLE);
				wakeLock.acquire();
				baradd = (SeekBar) media_play_layout
						.findViewById(R.id.mediavoicePlayingDialog_progressbar);
				if (((ImageView) media_play_layout
						.findViewById(R.id.media_play)) != null)
					((ImageView) media_play_layout
							.findViewById(R.id.media_play))
							.setOnClickListener(playerClickEvent);

				total_autio_time = ((TextView) media_play_layout
						.findViewById(R.id.audio_counter_max));
				played_autio_time = ((TextView) media_play_layout
						.findViewById(R.id.audio_counter_time));
				// total_autio_time.setText("00:00)");
				// played_autio_time.setText("(00:00 of ");
				total_autio_time.setText("");
				played_autio_time.setText("");
				openPlayScreen(Downloader.getInstance().getPlayUrl(message.voice_content_urls), false, media_play_layout);
				v.invalidate();
			} else {
				if (!ConversationsActivity.this.checkInternetConnection()) {
					ConversationsActivity.this.networkLossAlert();
					return;
				}
				ContentValues values = new ContentValues();
				values.put(MessageTable.AUDIO_DOWNLOAD_STATUE, 0);// int
				int updatedRow = getContentResolver().update(
						MediaContentProvider.CONTENT_URI_INBOX,
						values,
						MessageTable.AUDIO_ID + "=?",
						new String[] { ""
								+ message.voice_content_urls.hashCode() });

				Downloader downloader = Downloader.getInstance();
				downloader.download(message.voice_content_urls, 2, this);
				if (activityAdapter != null) {
					activityAdapter.getCursor().requery();
					activityAdapter.notifyDataSetChanged();
				}
				listViewActivity.invalidateViews();
			}
			listViewActivity
					.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
			// this.handler.post(new Runnable() {
			// //
			// http://media-rtd4.onetouchstar.com/2012/09/24/05/1195675151493550080.mp3
			// @Override
			// public void run() {
			//
			//
			// }
			// });
			break;

		case R.message_row_right.sending_statud:
			if (((TextView) v)
					.getText()
					.toString()
					.equalsIgnoreCase(
							getResources().getString(R.string.message_retry))) {
				setRetry(Long.parseLong((String) v.getTag()));
				if (Logger.CHEAT)
					;
				showToast("Message Resending");
				if (activityAdapter != null) {
					activityAdapter.getCursor().requery();
					activityAdapter.notifyDataSetChanged();
				}
				listViewActivity.invalidateViews();
			}
			break;
		case 100011:

			listViewActivity
					.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);

			videoUrl = (VideoUrl) v.getTag();
			if (videoUrl.acaton == 1) {
				VDownloader.getInstance().cancel();
				listViewActivity.invalidateViews();
				activityAdapter.notifyDataSetChanged();
				((Button) videoUrl.view).setText(getResources().getString(
						R.string.get_video));
				return;
			}
			if (!VDownloader.isRunning()) {

				// if(Downloader.getInstance().check(
				// message.voice_content_urls)){
				if (!ConversationsActivity.this.checkInternetConnection()) {
					ConversationsActivity.this.networkLossAlert();
					return;
				}
				// //System.out.printlnprintln("VDOWNLOAD----------voice url :"
				// + videoUrl.url);
				VDownloader downloader = VDownloader.getInstance();
				downloader.download(videoUrl, 2, this);
				listViewActivity.invalidateViews();
				activityAdapter.notifyDataSetChanged();
				((Button) videoUrl.view).setText(getResources().getString(
						R.string.reciving_video));
				// }

				if (activityAdapter != null) {
					activityAdapter.getCursor().requery();
					activityAdapter.notifyDataSetChanged();
				}
				listViewActivity.invalidateViews();
			} else
				showToast("Other video already downloading please try after that");

			break;
		case R.message_row.receving_voice:
		case R.message_row_right.receving_voice:

			listViewActivity
					.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);

			// if(Downloader.getInstance().check(
			// message.voice_content_urls)){
			// //System.out.printlnprintln("DOWNLOAD----------voice url :"
			// + message.voice_content_urls);
			Downloader vdownloader = Downloader.getInstance();
			vdownloader.download(message.voice_content_urls, 2, this);
			// listViewActivity.invalidateViews();
			if (activityAdapter != null) {
				activityAdapter.getCursor().requery();
				activityAdapter.notifyDataSetChanged();
			}
			listViewActivity.invalidate();
			// }
			((TextView) v).setText(getResources().getString(
					R.string.reciving_voice));

			break;
		// case R.message_row.receving_voice:
		// //System.out.printlnprintln("DOWNLOAD----------voice url :"+message.voice_content_urls);
		// downloader = Downloader.getInstance();
		// downloader.download(message.voice_content_urls, 2, this);
		// // activityAdapter.notifyDataSetChanged();
		// // activityAdapter.notifyDataSetInvalidated() ;
		//
		// listViewActivity.invalidateViews();
		// break;
		// case R.message_row_right.view_rtml:
		// // showToast("System clicked");
		// // iPayloadData = aInboxdata.mPayload;
		// InboxMessage mMessage = new InboxMessage();
		// mMessage.mPayload = new Payload();
		// mMessage.mPayload.mRTML = new byte[1][1];
		// message.msgTxt=getRtmlText(message.messageId) ;
		// mMessage.mPayload.mRTML[0] = message.msgTxt.getBytes();
		// //
		// //System.out.printlnprintln("--------------message.msgTxt ------ :"
		// // + message.msgTxt);
		// mMessage.mSenderName = message.source;//"Qts";
		// DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
		// (InboxMessage) mMessage);
		// intent = new Intent(ConversationsActivity.this,
		// CustomViewDemo.class);
		// intent.putExtra("PAGE", (byte) 1);
		// // intent.putExtra("force_message", forceMessage);
		// startActivityForResult(intent, 0);
		// break;

		// RTML case commented by Mahesh
		// case R.message_row_right.view_rtml:
		// case R.message_row.view_rtml:
		// // showToast("System clicked");
		// // iPayloadData = aInboxdata.mPayload;
		// try {
		// DataModel.sSelf.storeObject(DMKeys.FROM_REGISTRATION, new
		// Boolean(false));
		// InboxMessage mMessage = new InboxMessage();
		// mMessage.mPayload = new Payload();
		// mMessage.mPayload.mRTML = new byte[1][1];
		// message.msgTxt = getRtmlText(message.messageId);
		// mMessage.mPayload.mRTML[0] = message.msgTxt.getBytes();
		// // //System.out.printlnprintln("---rtml : "+message.msgTxt);
		// // Get All Thumb URL's
		// String thumbURLs[] = null;
		//
		// if (message.image_content_thumb_urls != null) {
		// thumbURLs = Utilities.split(new StringBuffer(
		// message.image_content_thumb_urls),
		// Constants.COL_SEPRETOR);
		// }
		// // Get All Images URL's
		// String imageURLs[] = null;
		// if (message.image_content_urls != null)
		// imageURLs = Utilities
		// .split(new StringBuffer(message.image_content_urls),
		// Constants.COL_SEPRETOR);
		//
		// mMessage.mSenderName = message.source;// "Qts" ;
		// if (imageURLs != null)
		// mMessage.mPayload.mPicture = new byte[thumbURLs.length][];
		// if (imageURLs != null)
		// for (int i = 0; i < imageURLs.length; i++)
		// mMessage.mPayload.mPicture[i] = thumbURLs[i].getBytes();
		// if (imageURLs != null && imageURLs.length > 0)
		// mMessage.mPayload.mSlideShowURLs = new String[imageURLs.length];
		// if (imageURLs != null)
		// for (int i = 0; i < imageURLs.length; i++)
		// mMessage.mPayload.mSlideShowURLs[i] = imageURLs[i];
		//
		// // Add Voice url
		// if (message.voice_content_urls != null) {
		// String voiceURLs1[] = Utilities
		// .split(new StringBuffer(message.voice_content_urls),
		// Constants.COL_SEPRETOR);
		// mMessage.mPayload.mVoice = new byte[voiceURLs1.length][];
		// for (int i = 0; i < voiceURLs1.length; i++)
		// mMessage.mPayload.mVoice[i] = voiceURLs1[i].getBytes();
		// }
		//
		// DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
		// (InboxMessage) mMessage);
		// intent = new Intent(ConversationsActivity.this,
		// CustomViewDemo.class);
		// intent.putExtra("PAGE", (byte) 1);
		// // intent.putExtra("force_message", forceMessage);
		// startActivityForResult(intent, 0);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// break;
//		case R.message_row.inbox_accept:
			// showToast("accept clicked");
//			Utilities.jsonParserEngine(message.msgTxt);
//			String acceptsource = Utilities.accept();
//			String acceptdestination = "";
//			if (acceptsource.indexOf("?") != -1) {
//				acceptdestination = acceptsource.substring(0,
//						acceptsource.indexOf("?"));
//				acceptsource = acceptsource.substring(
//						acceptsource.indexOf("=") + 1, acceptsource.length());
//			}
//
//			if (BusinessProxy.sSelf.sendNewTextMessage(acceptdestination,
//					acceptsource, true)) {
//				String alert = (String) getString(R.string.acceptAlert);
//				// showSimpleAlert(
//				// "Info",
//				// String.format(alert,
//				// (inboxElement.mSenderName.indexOf(';') != -1) ?
//				// inboxElement.mSenderName.subSequence(0,
//				// inboxElement.mSenderName.indexOf(';')) :
//				// inboxElement.mSenderName));
//
//				DialogInterface.OnClickListener blockHandlerNew = null;
//
//				blockHandlerNew = new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface dialog, int which) {
//						final Object deleteElement;
//
//						deleteElement = message.messageId;
//
//						List<Object> list = new ArrayList<Object>();
//						list.add(deleteElement);
//
//						BusinessProxy.sSelf.deleteForIDs(list,
//								DBEngine.INBOX_TABLE);
//						// sendDeleteMessageRequest(deleteElement);
//
//						// finish();
//					}
//				};
//				showAlertMessage(getString(R.string.confirm), alert,
//						new int[] { DialogInterface.BUTTON_POSITIVE },
//						new String[] { getString(R.string.ok) },
//						new DialogInterface.OnClickListener[] {
//								blockHandlerNew, null });
//			}
//			break;
//		case R.message_row.inbox_decline:
//			showToast("decline clicked");
//			break;
//		case R.message_row.inbox_ignore:
//			showToast("ignore clicked");
//			break;
		case R.conversation.option:
			// action(idsMenuOptions, idsMenuNameOptions, (byte)
			// 1,idsMenuOptionsIcon);
			// quickAction.show(v);
			// if (lDialog != null && lDialog.isShowing())
			// openRowOption(1);
			// else
			Utilities.closeSoftKeyBoard(messageBox, ConversationsActivity.this);
			showOption(null);
			// option

			// conversations_screen_frame_layout.setVisibility(View.GONE);
			// conversations_option_frame_layout.setVisibility(View.VISIBLE);
			//
			// ((EditText)findViewById(R.conversation_option_panel.subject)).setText("");
			// ((TextView) findViewById(R.conversation.allParticipent))
			// .setText(getParticipantInfo()+" topMsgid"+FeedRequester.lastMessageId);
			// if (conversationList != null
			// && conversationList.isGroup == 0)
			// fillP2P();
			// else if (conversationList != null
			// && conversationList.isGroup == 1)
			// fillGroup();
			//
			// conversations_option_frame_layout.requestLayout() ;
			// conversations_option_frame_layout.invalidate();

			break;
		// case R.conversation.load_prevoius_message:
		// if (load_prevoius_message
		// .getText()
		// .toString()
		// .trim()
		// .equalsIgnoreCase(
		// getResources().getString(
		// R.string.load_prevoius_message))) {
		// if (FeedRequester.feedTaskConversationMessagesRefresh == null
		// || FeedRequester.feedTaskConversationMessagesRefresh
		// .getStatus() == Status.FINISHED) {
		// FeedRequester.requestConversationMessagesRefresh(
		// ConversationsActivity.this, getIntent()
		// .getStringExtra(Constants.CONVERSATION_ID),this);
		//
		// }
		// if (FeedRequester.feedTaskConversationMessagesRefresh != null)
		// FeedRequester.feedTaskConversationMessagesRefresh
		// .setHttpSynch(ConversationsActivity.this);
		// load_prevoius_message.setText(getResources().getString(
		// R.string.refreshing_list));
		// // listViewActivity
		// // .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
		// }
		// break;
		case R.message_row_right.menu:
			// action(idsRowMenuOptions, idsRowMenuNameOptions, (byte) 1,
			// idsRowMenuOptionsIcon);
			// quickAction.show(v);
			clickLeftRightCheck = true;
			openRowOption(1, v);

			// showCopy(v);
			break;
		case R.message_row.menu:
			// action(idsRowMenuOptions, idsRowMenuNameOptions, (byte) 1,
			// idsRowMenuOptionsIcon);
			// quickAction.show(v);
			clickLeftRightCheck = false;
			openRowOption(2, v);

			break;
		case R.id.discovery_messageDisplay_FeatureUserName:
			// FeaturedUserBean bean = (FeaturedUserBean) v.getTag();
			//
			// DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
			// bean.displayName);
			// DataModel.sSelf.storeObject(DMKeys.XHTML_URL, bean.profileUrl);
			// intent = new Intent(this, WebviewActivity.class);
			// intent.putExtra("PAGE", (byte) 1);
			// startActivity(intent);
			break;
		case R.chat_panel.more:

			if (dialogAnimation != null && dialogAnimation.isShowing()) {
				dialogAnimation.dismiss();
				flagAnimNotPlay = false;
			}

			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// if(BusinessProxy.sSelf.db==null)
					// BusinessProxy.sSelf.db=new
					// DatabaseHandler(BusinessProxy.sSelf.addPush.mContext);
					// dgvgvfd

					if (chat_panel_more_option != null) {
						if (chat_panel_more_option.getVisibility() == View.GONE) {

							// findViewById(R.chat_panel.more)
							// .setBackgroundResource(R.drawable.chat_down);
							chat_panel_more_option.setVisibility(View.VISIBLE);
							// chat_panel_more_option_emo.setVisibility(View.VISIBLE);
							chat_panel_more_option_emo.setVisibility(View.GONE);
						} else {
							findViewById(R.chat_panel.more)
									.setBackgroundResource(R.drawable.chat_up);

							chat_panel_more_option.setVisibility(View.GONE);
							chat_panel_more_option_emo.setVisibility(View.GONE);
						}
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								((EditText) findViewById(R.community_chat.msgBox))
										.getWindowToken(), 0);
					}
				}
			});

			/*
			 * new Thread(new Runnable() {
			 * 
			 * @Override public void run() { // TODO Auto-generated method stub
			 * InputMethodManager imm = (InputMethodManager)
			 * getSystemService(Context.INPUT_METHOD_SERVICE);
			 * imm.hideSoftInputFromWindow
			 * (((EditText)findViewById(R.community_chat
			 * .msgBox)).getWindowToken(), 0); } });
			 */
			break;
		case R.chat_panel.sendButton:
			attachment_panel.setVisibility(View.INVISIBLE);
			if (dialogAnimation != null && dialogAnimation.isShowing()) {
				dialogAnimation.dismiss();
				flagAnimNotPlay = false;
			}
			String textMessage = // ((EditText)
			// findViewById(R.community_chat.msgBox))
			messageBox.getText().toString();
			if (null == mVoicePath && null == mPicturePath
					&& 0 == textMessage.trim().length()) {
				showSimpleAlert("Error", "Please add content to send message");
				return;
			}

			// request = new Request(1000, "url");
			if (!ConversationsActivity.this.checkInternetConnection()) {
				ConversationsActivity.this.networkLossAlert();
				return;
			}
			// request.execute("LikeDislike");
			// quickSend(null,null);

			// findViewById(R.id.voice_count).setVisibility(View.GONE) ;

			Vector<String> mPicturePathCanvasLocal = new Vector<String>();
			Vector<String> mPicturePathLocal = new Vector<String>();
			if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0)
				for (String ss : mPicturePathCanvas) {
					mPicturePathCanvasLocal.add(ss);
				}

			if (mPicturePath != null && mPicturePath.size() > 0) {
				for (String ss : mPicturePath) {
					mPicturePathLocal.add(ss);
				}

			}
			if(mPicturePathCanvas != null)
			mPicturePathCanvas.clear();
			if(mPicturePath != null)
			mPicturePath.clear();
			UpdateUi task = new UpdateUi();
			task.mPicturePath = mPicturePath;
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					new String[] { SEND_MESSAGE });

			if (mPicturePathLocal != null && mPicturePathLocal.size() > 0)
				for (String ss : mPicturePathLocal) {
					mPicturePath.clear();
					// mPicturePath.add(ss) ;
					task = new UpdateUi();
					task.mPicturePath.add(ss);
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							new String[] { SEND_MESSAGE });
				}
			if (mPicturePathCanvasLocal != null
					&& mPicturePathCanvasLocal.size() > 0)
				for (String ss : mPicturePathCanvasLocal) {
					mPicturePath.clear();
					// mPicturePath.add(ss) ;
					task = new UpdateUi();
					task.mPicturePath.add(ss);
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							new String[] { SEND_MESSAGE });
				}

			mPicturePathCanvas.clear();
			if(mPicturePath != null)
			mPicturePath.clear();
			((ImageView) findViewById(R.id.attachement))
					.setImageResource(R.drawable.attachment_unsel);

			// Utilities.closeSoftKeyBoard(messageBox, this);
			break;
		case R.id.discoveryRow_FeatureUserIcon:
			// bean = (FeaturedUserBean) v.getTag();
			//
			// if (bean.displayName != null)
			// DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
			// bean.displayName);
			// if (bean.profileUrl != null)
			// DataModel.sSelf.storeObject(DMKeys.XHTML_URL, bean.profileUrl);
			// intent = new Intent(this, WebviewActivity.class);
			// intent.putExtra("PAGE", (byte) 1);
			// startActivity(intent);
			break;

		case R.id.photo_gallary:
			openThumbnailsToAttach(POSITION_PICTURE);
			break;
		case R.id.camera:
			openCamera();
			// if (dialogAnimation != null && dialogAnimation.isShowing()) {
			// dialogAnimation.dismiss();
			// flagAnimNotPlay = false;
			// }
			// Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
			// this);
			// Utilities.closeSoftKeyBoard(findViewById(R.community_chat.msgBox),
			// this);
			// if (mPicturePath != null
			// && mPicturePath.size() ==
			// Constants.COMPOSE_MAX_IMAGE_ATTACH_COUNT) {
			// showSimpleAlert("Error",
			// "Maximum number of images already added. Can not add more image!");
			// return;
			// }
			//
			// if (mPicturePath != null && mPicturePath.size() > 0) {
			//
			// HashMap menuItems = new HashMap<Integer, String>();
			// menuItems.put(0, getString(R.string.preview));
			// menuItems.put(1, getString(R.string.clear));
			// menuItems.put(2, getString(R.string.add_more));
			// menuItems.put(3, getString(R.string.click_more));
			// menuItems.put(4, getString(R.string.cancel));
			// openActionSheet(menuItems, new OnMenuItemSelectedListener() {
			// @Override
			// public void MenuItemSelectedEvent(Integer selection) {
			// // "";// mImagesPath.get(id);
			// switch (selection) {
			// case 0:
			//
			// List<String> imagesPath = new ArrayList<String>();
			// String path;
			// for (int i = 0; i < mPicturePath.size(); i++) {
			// imagesPath.add(mPicturePath.elementAt(i));
			// }
			//
			// Intent intent = new
			// Intent(ConversationsActivity.this,MultiPhotoViewer.class);
			// intent.putStringArrayListExtra("DATA",(ArrayList<String>)
			// imagesPath);
			// startActivity(intent);
			// closeActionSheet();
			// break;
			// case 1:
			// mPicturePathId.clear();
			// mPicturePath.clear();
			// showHidePictureCount();
			// closeActionSheet();
			// calculateTotSizeAfterClear();
			// break;
			// case 2:
			// openThumbnailsToAttach(POSITION_PICTURE);
			// break;
			// case 3:
			// openCamera();
			// break;
			// default:
			// closeActionSheet();
			// break;
			// }
			// }
			// }, null);
			//
			// return;
			// }
			//
			// String arr[] = {
			// getString(R.string.choose_from_library),
			// getString(R.string.take_photo)
			// } ;
			// // final Integer val[] = {1,2} ;
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.select_choice);
			// builder.setItems(arr, new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// switch (which) {
			// case 0:
			// openThumbnailsToAttach(POSITION_PICTURE);
			// break;
			// case 1:
			// saveMessageContents();
			// openCamera();
			// break;
			// }
			// }
			// });
			// builder.show() ;
			// if(1==1)return ;
			//
			// final Dialog dialogVideo = new Dialog(ConversationsActivity.this,
			// android.R.style.Theme_Translucent_NoTitleBar);
			// dialogVideo.requestWindowFeature(Window.FEATURE_NO_TITLE);
			//
			//
			// dialogVideo.setContentView(R.layout.open_choice_dialog);
			// dialogVideo.setCancelable(true);
			//
			// TextView text1 = (TextView) dialogVideo
			// .findViewById(R.open_choice.message);
			// text1.setText("");//
			// String.format("Max count: 20 within max size: 4 MB. Current attached size: %s MB",
			// // (totalSize / 1024.0 / 1024.0)));
			//
			// // TextView text1 = (TextView)
			// // dialogVideo.findViewById(R.open_choice.message);
			// // text1.setText(msg);
			//
			// Button button1 = (Button) dialogVideo
			// .findViewById(R.open_choice.button1);
			// button1.setText(getString(R.string.choose_from_library));
			// button1.setOnClickListener(new OnClickListener() {
			// public void onClick(View arg0) {
			// dialogVideo.dismiss();
			// openThumbnailsToAttach(POSITION_PICTURE);
			// }
			// });
			//
			// Button button2 = (Button) dialogVideo
			// .findViewById(R.open_choice.button2);
			// button2.setText(getString(R.string.take_photo));
			// button2.setOnClickListener(new OnClickListener() {
			// public void onClick(View arg0) {
			// saveMessageContents();
			// dialogVideo.dismiss();
			// openCamera();
			// }
			// });
			// /*
			// * Button button3 = (Button) dialogVideo
			// * .findViewById(R.open_choice.rt_canvas);
			// * button3.setText("RT Canvas"); if (Constants.DISPLAY_RT_CANVAS)
			// * button3.setVisibility(View.VISIBLE);
			// * button3.setOnClickListener(new OnClickListener() { public void
			// * onClick(View arg0) { dialogVideo.dismiss();
			// * openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS); } });
			// */
			// Button cancelButton = (Button) dialogVideo
			// .findViewById(R.open_choice.cancelButton);
			// cancelButton.setOnClickListener(new OnClickListener() {
			// public void onClick(View arg0) {
			// dialogVideo.dismiss();
			// // openCameraForVedioRecording();
			// }
			// });
			// dialogVideo.show();
			break;
		case R.id.chatScreen_doodle:
			if (dialogAnimation != null && dialogAnimation.isShowing()) {
				dialogAnimation.dismiss();
				flagAnimNotPlay = false;
			}

			if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0) {

				// menuItems.put(2, getString(R.string.add_more));
				// menuItems.put(3, getString(R.string.click_more));
				HashMap menuItems = new HashMap<Integer, String>();
				menuItems.put(0, getString(R.string.preview));
				menuItems.put(1, getString(R.string.clear));
				menuItems.put(2, getString(R.string.add_more));
				menuItems.put(3, getString(R.string.cancel));
				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {
						// "";// mImagesPath.get(id);
						switch (selection) {
						case 0:
							/*
							 * Intent intent = new Intent(CommentActivity.this,
							 * VideoPlayer.class); intent.putExtra("MODE",
							 * "file");
							 * ////System.out.printlnprintln("------path-" +
							 * path); intent.putExtra("VIDEO_PATH", path);
							 * intent.putExtra("BACK", "COMPOSE");
							 * startActivity(intent);
							 */
							List<String> imagesPath = new ArrayList<String>();
							String path;
							for (int i = 0; i < mPicturePathCanvas.size(); i++) {
								imagesPath.add(mPicturePathCanvas.elementAt(i));
							}

							/*
							 * try{ for (int i = 0; i < mPicturePath.size();
							 * i++) { try { FileOutputStream fout = new
							 * FileOutputStream(path =
							 * String.format("%s%sFile%d.jpg", getCacheDir(),
							 * File.separator, i)); fout.write(mPicturePath.);
							 * fout.flush(); fout.close(); imagesPath.add(path);
							 * } catch (Exception ex) { } }}catch (Exception e)
							 * { onClickMessageList(index); return ; }
							 */

							Intent intent = new Intent(
									ConversationsActivity.this,
									MultiPhotoViewer.class);
							intent.putStringArrayListExtra("DATA",
									(ArrayList<String>) imagesPath);
							// intent.putExtra("FROM_SERVER", true);
							startActivity(intent);
							closeActionSheet();
							break;
						case 1:
							mPicturePathId.clear();
							mPicturePathCanvas.clear();
							showHidePictureDoddleCount();
							closeActionSheet();
							calculateTotSizeAfterClear();
							break;
						case 2:
							openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS);
							break;

						default:
							closeActionSheet();
							break;
						}
					}
				}, null);

				return;
			}
			openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS);
			break;
		// case R.id.chatScreen_animation:
		// handler.post(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// openDialogAnimation();
		// }
		// });

		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * openDialogAnimation(); } }).start();
		 */
		// //startActivity(new Intent(ConversationsActivity.this,
		// GridFunnyGif.class));
		// break;
		case R.id.voice_record:
			wakeLock.acquire();
			if (dialogAnimation != null && dialogAnimation.isShowing()) {
				dialogAnimation.dismiss();
				flagAnimNotPlay = false;
			}
			// RTMediaPlayer.reset();
			closePlayScreen();
			// mVoiceMedia.startRecording("Done", "Cancel", null,
			// Constants.MAX_AUDIO_RECORD_TIME_REST);
			if (null == mVoicePath) {
				// mVoiceMedia.startRecording(getString(R.string.done),
				// getString(R.string.cancel), null,
				// Constants.MAX_AUDIO_RECORD_TIME_REST);
				showAudioDialog(RECORDING_MODE);
			} else {

				HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
				menuItems.put(0, getString(R.string.play_audio));
				menuItems.put(1, getString(R.string.clear_audio));
				menuItems.put(2, getString(R.string.re_record));
				menuItems.put(3, getString(R.string.cancel));
				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {
						switch (selection) {
						case 0:
							playAvailableVoice();
							break;
						case 1:
							File file = new File(mVoicePath);
							if (file.exists()) {
								file.delete();
							}
							mVoicePath = null;
							showHideRecCount();
							break;
						case 2:
							file = new File(mVoicePath);
							if (file.exists()) {
								file.delete();
							}
							mVoicePath = null;
							showHideRecCount();
							mVoiceMedia.startRecording("Done", "Cancel", null,
									Constants.MAX_AUDIO_RECORD_TIME_REST);
							break;
						}

					}
				}, null);
			}
			break;
		case R.id.talking_pic:
			openThumbnailsToAttach(POSITION_VOICE);
			break;
		case R.id.audio_file:
			openThumbnailsToAttach(POSITION_PICTURE_RT_CANVAS);
			break;
		case R.id.video_record:
			if (dialogAnimation != null && dialogAnimation.isShowing()) {
				dialogAnimation.dismiss();
				flagAnimNotPlay = false;
			}
			if (mVideoPath != null) {
				HashMap menuItems = new HashMap<Integer, String>();
				menuItems.put(0, getString(R.string.preview));
				menuItems.put(1, getString(R.string.clear));
				menuItems.put(2, getString(R.string.cancel));
				openActionSheet(menuItems, new OnMenuItemSelectedListener() {
					@Override
					public void MenuItemSelectedEvent(Integer selection) {
						String path = mVideoPath;// "";// mImagesPath.get(id);
						switch (selection) {
						case 0:
							Intent intent = new Intent(
									ConversationsActivity.this,
									VideoPlayer.class);
							intent.putExtra("MODE", "file");
							// //System.out.printlnprintln("------path-" +
							// path);
							intent.putExtra("VIDEO_PATH", path);
							intent.putExtra("BACK", "COMPOSE");
							startActivity(intent);
							closeActionSheet();
							break;
						case 1:
							mVideoPath = null;
							showHideVideoCount();
							closeActionSheet();
							calculateTotSizeAfterClear();
							break;
						default:
							closeActionSheet();
							break;
						}
					}
				}, null);

				return;
			}

			String arr[] = new String[] {
					getString(R.string.choose_from_library),
					getString(R.string.record_with_camera) };
			// val = {1,2} ;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
			if (1 == 1)
				return;
			String msg = getMaxSizeTextVideo();
			final Dialog dialog = new Dialog(ConversationsActivity.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.open_choice_dialog);
			dialog.setCancelable(true);

			TextView text2 = (TextView) dialog
					.findViewById(R.open_choice.message);
			text2.setText(msg);

			Button button3 = (Button) dialog
					.findViewById(R.open_choice.button1);
			button3.setText(getString(R.string.choose_from_library));
			button3.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					saveMessageContents();
					dialog.dismiss();
					openThumbnailsToAttach(POSITION_VIDEO);
				}
			});

			Button button4 = (Button) dialog
					.findViewById(R.open_choice.button2);
			button4.setText(getString(R.string.record_with_camera));
			button4.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					saveMessageContents();
					dialog.dismiss();
					// action(idsReport, idsNameReport, (byte) 1);
					// quickAction.show(v);
					/*
					 * if (BusinessProxy.sSelf.rToolTipsFlag) { Timer timer =
					 * new Timer();
					 * 
					 * timer.schedule(new TimerTask() { public void run() {
					 * openCameraForVedioRecording();
					 * BusinessProxy.sSelf.rToolTipsFlag = false; } }, 5000); }
					 * else { openCameraForVedioRecording(); }
					 */
					openCameraForVedioRecording();

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
		if (!isCancelClick) {
			if (dialog.isShowing())
				dialog.dismiss();
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
			// showHideRecCount();
			// Specific Demand By Rajiv for Quick Send of Voice Messages direct
			// from Chat Screen
			((ImageView) findViewById(R.chat_panel.sendButton)).performClick();
		} else
			mVoicePath = null;
	}

	private void sendMessage() {

		image_count.setVisibility(View.GONE);
		attachment_panel.setVisibility(View.INVISIBLE);
		((ImageView) findViewById(R.id.attachement))
				.setImageResource(R.drawable.attachment_unsel);

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

		ContentValues values2 = new ContentValues();
		values.put(MessageTable.SENDING_STATE, Constants.MESSAGE_STATUS_SENDING);
		values.put(MessageTable.USER_ID, BusinessProxy.sSelf.getUserId());// int
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
		values2.put(MessageTable.MESSAGE_ID, message.messageId);
		values.put(MessageTable.MFU_ID, message.mfuId);
		values.put(MessageTable.MSG_OP, message.msgOp);// int
		values.put(MessageTable.MSG_TXT, message.msgTxt);
		values2.put(MessageTable.MSG_TXT, message.msgTxt);
		values.put(MessageTable.NOTIFICATION_FLAGS, message.notificationFlags);
		values.put(MessageTable.REFMESSAGE_ID, message.refMessageId);
		values.put(MessageTable.SENDERUSER_ID, message.senderUserId);
		values.put(MessageTable.SOURCE, message.source);
		values.put(MessageTable.TARGETUSER_ID, message.targetUserId);
		values.put(MessageTable.USER_FIRSTNAME, message.user_firstname);
		values.put(MessageTable.USER_LASTNAME, message.user_lastname);
		values.put(MessageTable.USER_NAME, message.user_name);
		values.put(MessageTable.USER_URI, message.user_uri);

		// values.put(MessageTable.INSERT_TIME, System.currentTimeMillis());

		values.put(MessageTable.AUDIO_DOWNLOAD_STATUE,
				message.audio_download_statue);
		values.put(MessageTable.ANI_TYPE, message.aniType);
		values.put(MessageTable.ANI_VALUE, message.aniValue);
		// //System.out.printlnprintln("message.aniType==save="+message.aniType+"==message.aniValue===="+message.aniValue);
		// Cursor cursor = getCursor();
		// cursor.moveToLast();

		long l = System.currentTimeMillis();
		// if (cursor.getCount() > 0) {
		// l = cursor.getLong(cursor
		// .getColumnIndex(LandingPageTable.INSERT_TIME));
		// }
		// cursor.close();
		values.put(MessageTable.INSERT_TIME, l + 1000 * 1);
		values2.put(MessageTable.INSERT_TIME, l + 1000 * 1);
		FeedTask.ShiftNewMessageVec.add(new ShiftNewMessage(
				FeedTask.insMoreTime, message.conversationId));
		values.put(MessageTable.SORT_TIME, FeedTask.insMoreTime++);// System.currentTimeMillis());//System.currentTimeMillis());//values2.put(MessageTable.SORT_TIME,
		// System.currentTimeMillis());
		values2.put(MessageTable.SORT_TIME, FeedTask.insMoreTime);
		values2.put(MessageTable.DIRECTION, "0");
		values.put(MessageTable.DIRECTION, "0");

		// //sender username firstname lastname profileImageUrl profileUrl
		// values.put(MessageTable.PARTICIPANT_INFO,
		// "true�"+SettingData.sSelf.getUserName()+"�"+SettingData.sSelf.getFullName()+"�"+SettingData.sSelf.getUserName()+"�");
		// participantInfo.isSender=Boolean.parseBoolean(info[0]);
		// participantInfo.userName=info[1];
		// participantInfo.firstName=info[2];
		// participantInfo.lastName=info[3];
		// participantInfo.profileImageUrl=info[4];
		// participantInfo.profileUrl=info[5];
		values.put(MessageTable.PARTICIPANT_INFO, "true"
				+ Constants.COL_SEPRETOR + SettingData.sSelf.getUserName()
				+ Constants.COL_SEPRETOR + SettingData.sSelf.getFirstName()
				+ Constants.COL_SEPRETOR + SettingData.sSelf.getLastName()
				+ Constants.COL_SEPRETOR + SettingData.sSelf.getLastName()
				+ Constants.COL_SEPRETOR + SettingData.sSelf.getLastName()
				+ Constants.ROW_SEPRETOR);
		// values.put(MessageTable.DIRECTION, message.);
		// values.put(MessageTable.INSERT_TIME, System.currentTimeMillis());

		if (message.voice_content_urls != null) {

			// message.video_content_thumb_urls = mVoicePath ;
			values.put(MessageTable.VOICE_CONTENT_URLS,
					message.voice_content_urls);
			values.put(MessageTable.AUDIO_ID,
					message.voice_content_urls.hashCode());
		}

		values2.put(MessageTable.VOICE_CONTENT_URLS, "");
		values2.put(MessageTable.AUDIO_ID, "");
		if (message.voice_content_urls != null) {

			// message.video_content_thumb_urls = mVoicePath ;
			values2.put(MessageTable.VOICE_CONTENT_URLS,
					message.voice_content_urls);
			values2.put(MessageTable.AUDIO_ID,
					message.voice_content_urls.hashCode());
		}

		values.put(MessageTable.MESSAGE_TYPE, message.messageType);
		values.put(MessageTable.IMAGE_CONTENT_THUMB_URLS,
				message.image_content_thumb_urls);
		values.put(MessageTable.IMAGE_CONTENT_URLS, message.image_content_urls);

		values.put(MessageTable.VIDEO_CONTENT_THUMB_URLS,
				message.video_content_thumb_urls);
		values.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);

		values2.put(MessageTable.MESSAGE_TYPE, message.messageType);
		values2.put(MessageTable.IMAGE_CONTENT_THUMB_URLS,
				message.image_content_thumb_urls);
		values2.put(MessageTable.IMAGE_CONTENT_URLS, message.image_content_urls);

		values2.put(MessageTable.VIDEO_CONTENT_THUMB_URLS,
				message.video_content_thumb_urls);
		values2.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);

		values2.put(MessageTable.ANI_TYPE, message.aniType);
		values2.put(MessageTable.ANI_VALUE, message.aniValue);

		values2.put(MessageTable.MFU_ID, "-1");

		// values.put(MessageTable.MORE, System.currentTimeMillis());
		Uri u = getContentResolver().insert(
				MediaContentProvider.CONTENT_URI_INBOX, values);

		values.remove(MessageTable.SUBJECT);
		values.remove(MessageTable.GROUP_PIC);
		values.remove(MessageTable.PARTICIPANT_INFO);
		int updatedRow = getContentResolver().update(
				MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
				values2,
				MessageConversationTable.CONVERSATION_ID + "= ? and "
						+ MessageConversationTable.USER_ID + " =?",
				new String[] { message.conversationId,
						"" + BusinessProxy.sSelf.getUserId() });

		// count = getCursorCount();

		// final int oldPos = listViewActivity
		// .getFirstVisiblePosition();
		// activityAdapter = new ConversationsAdapter(
		// ConversationsActivity.this, getCursor(), true,
		// ConversationsActivity.this);
		// listViewActivity.setAdapter(activityAdapter);
		//
		// if (oldPos > 1)
		// listViewActivity.setSelection(oldPos);
		activityAdapter.notifyDataSetChanged();
		if (count > 0) {
			listViewActivity.setSelection(count - 1);
			Log.v("ConverstionsActivity","Manoj"+" 4882");
			listViewActivity.setStackFromBottom(true);
		}
		Utilities.setLong(this, "insMoreTime", FeedTask.insMoreTime);
	}

	private void openCamera() {

		// if ((frontCam && rearCam) || (!frontCam && rearCam)) {
		// cameraImagePath = null;
		// File file = new File(Environment.getExternalStorageDirectory(),
		// getRandomNumber() + ".jpg");
		// cameraImagePath = file.getPath();
		// Uri outputFileUri = Uri.fromFile(file);
		// // Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		//
		// //
		// //System.out.printlnprintln("cameraImagePath===on"+cameraImagePath);
		// Intent i = new Intent(ConversationsActivity.this,
		// DgCamActivity.class);
		// i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		// i.putExtra("urlpath", cameraImagePath);
		//
		// this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		// }
		// if (frontCam && !rearCam) {
		// cameraImagePath = null;
		// File file = new File(Environment.getExternalStorageDirectory(),
		// getRandomNumber() + ".jpg");
		// cameraImagePath = file.getPath();
		// Uri outputFileUri = Uri.fromFile(file);
		// Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
		// i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		// this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
		// }
		/*
		 * if ((frontCam && rearCam) || (!frontCam && rearCam)) {
		 * cameraImagePath = null; File file = new
		 * File(Environment.getExternalStorageDirectory(), getRandomNumber() +
		 * ".jpg"); cameraImagePath = file.getPath(); Uri outputFileUri =
		 * Uri.fromFile(file); // Intent i = new
		 * Intent("android.media.action.IMAGE_CAPTURE");
		 * 
		 * //
		 * //System.out.printlnprintln("cameraImagePath===on"+cameraImagePath);
		 * Intent i = new Intent(ConversationsActivity.this,
		 * DgCamActivity.class); i.putExtra(MediaStore.EXTRA_OUTPUT,
		 * outputFileUri); i.putExtra("urlpath", cameraImagePath);
		 * 
		 * this.startActivityForResult(i, POSITION_CAMERA_PICTURE); } if
		 * (frontCam && !rearCam) {
		 */
		cameraImagePath = null; // File file = new File(Utilities.
		// getPicPath(), getRandomNumber() + // ".jpg");
		File file = new File(Environment.getExternalStorageDirectory(),
				getRandomNumber() + ".jpg");
		cameraImagePath = file.getPath();
		Uri outputFileUri = Uri.fromFile(file);

		// Create parameters for Intent with filename
		// ContentValues values = new ContentValues();
		// values.put(MediaStore.Images.Media.TITLE, cameraImagePath);
		// values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by Yelatalk App");
		// Uri outputFileUri =
		// getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// values);

		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		this.startActivityForResult(i, POSITION_CAMERA_PICTURE);
	}

	private void openThumbnailsToAttach(byte which) {
		if (attachment_panel != null)
			attachment_panel.setVisibility(View.INVISIBLE);
		Intent intent = new Intent();
		switch (which) {
		case POSITION_VOICE:
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
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
			if (mPicturePathId_int != null && mPicturePathId_int.size() > 0) {
				int[] dataIdint = new int[mPicturePathId_int.size()];
				for (int i = 0; i < mPicturePathId_int.size(); i++) {
					dataIdint[i] = mPicturePathId_int.get(i);
				}
				DataModel.sSelf.storeObject("3333" + "int", dataIdint);
			}

			// intent = new Intent(this, RocketalkMultipleSelectImage.class);
			if (imagePathPos != null && imagePathPos.length > 0) {
				DataModel.sSelf.storeObject("SELECTED_IMAGES", imagePathPos);
				imagePathPos = null;
			}
			intent = new Intent(this, KainatMultiImageSelection.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
			// Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

			// open Gallery in Nexus plus All Google based ROMs
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
						"com.cooliris.media.Gallery");

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
		calculateTotSizeAfterClear();
		double sizeInMB = totalSize / 1024.0 / 1024.0;
		String text = Double.toString(sizeInMB);
		int index = text.indexOf('.');
		if (-1 != index && index + 5 < text.length()) {
			text = text.substring(0, index + 5);
		}
		// return String.format("Max size: " + BusinessProxy.sSelf.MaxSizeData
		// + " MB. Current attached size: %s MB", text);
		return "";
		// return ("Max count: 10.");// Current attached size: " +
		// onMbKbReturnResult(totalSize));
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
		if (BusinessProxy.sSelf.buildInfo.MODEL
				.equalsIgnoreCase(Constants.VIDEO_REC_PROB_ON_DEVICE2))
			videofile = file;
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

	boolean longPressFlag = false;
	OnLongClickListener onLongClickListenerShowCopy = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// showCopy(v);
			Object obj = v.getTag();
			if (obj instanceof Message)
				message = (Message) obj;
			if (obj instanceof ParticipantInfo)
				infoMessage = (ParticipantInfo) obj;
			clickLeftRightCheck = true;
			if (message.source == null) {
				// This is to copy sent messages that has no message info.
				message.source = SettingData.sSelf.getUserName();
			}
			if (message != null && message.source != null)
				if (message.source.equalsIgnoreCase(SettingData.sSelf
						.getUserName())) {
					openRowOption(1, v);
				} else
					openRowOption(2, v);
			longPressFlag = true;

			return true;
		}
	};
	String dist = "";

	@Override
	public void viewMessage(View convertView, Context context, Message message) {
		MessageViewHolder messageViewHolder = (MessageViewHolder) convertView
				.getTag();
		if (message.lastMsg && message.comments != null
				&& message.comments.trim().equalsIgnoreCase("chat")) {
			if (conversationList != null)
				conversationList.comments = "chat";
		}
		checkIsView(message);
		dist = "";
		if (FeedRequester.feedTaskConversationMessages != null
				&& FeedRequester.feedTaskConversationMessages.getStatus() == Status.FINISHED) {
			load_prevoius_message.setVisibility(View.GONE);
		}
		messageViewHolder.message = message;
		convertView.setTag(messageViewHolder);
		messageViewHolder.menuLeft.setOnClickListener(this);
		messageViewHolder.menuLeft.setTag(message);
		messageViewHolder.menuRight.setOnClickListener(this);
		messageViewHolder.menuRight.setTag(message);

//		messageViewHolder.inbox_accept.setTag(message);
//		messageViewHolder.inbox_accept.setOnClickListener(this);
//		messageViewHolder.inbox_decline.setTag(message);
//		messageViewHolder.inbox_decline.setOnClickListener(this);
//		messageViewHolder.inbox_ignore.setTag(message);
//		messageViewHolder.inbox_ignore.setOnClickListener(this);

		// messageViewHolder.view_rtml.setVisibility(View.GONE);

//		convertView.findViewById(R.message_row_right.friend_invite).setVisibility(View.GONE);
		ParticipantInfo senderParticipantInfo = null;
		boolean isSender = false;
		if (messageViewHolder.message.participantInfo != null
				&& messageViewHolder.message.participantInfo.size() > 0) {
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
		// //System.out.printlnprintln("------------mfuId 2 :" + message.mfuId);
		messageViewHolder.left.setVisibility(View.VISIBLE);
		messageViewHolder.right.setVisibility(View.VISIBLE);
		messageViewHolder.left.setTag(messageViewHolder.message);
		messageViewHolder.right.setTag(messageViewHolder.message);
		messageViewHolder.left
				.setOnLongClickListener(onLongClickListenerShowCopy);
		messageViewHolder.right
				.setOnLongClickListener(onLongClickListenerShowCopy);
		if (conversationList != null && conversationList.isGroup == 0) {
			messageViewHolder.other_image_layout.setVisibility(View.GONE);
			messageViewHolder.me_image_layout.setVisibility(View.GONE);// if
																		// same
																		// p2p
																		// then
																		// dont
																		// dispay
		} else {
			messageViewHolder.other_image_layout.setVisibility(View.GONE);
		}
		if (conversationList.isGroup == 0
				&& (messageViewHolder.message.mfuId == null || !messageViewHolder.message.mfuId
						.equalsIgnoreCase("-1"))
				&& conversationList.participantInfo.size() == 1) {
			isSender = false;
		}
		if (isSender) {
			renderRight(convertView, context, message, messageViewHolder,
					senderParticipantInfo);
			messageViewHolder.left.setVisibility(View.GONE);
			messageViewHolder.menuRight.setVisibility(View.INVISIBLE);
		} else {
			renderLeft(convertView, context, message, messageViewHolder,
					senderParticipantInfo);
			messageViewHolder.right.setVisibility(View.GONE);
		}
		messageViewHolder.menuLeft.setVisibility(View.VISIBLE);

		if (messageViewHolder.message.mfuId == null
				|| messageViewHolder.message.mfuId.equalsIgnoreCase("-1")
				|| messageViewHolder.message.mfuId.trim().length() <= 0)
			if (messageViewHolder.message.mfuId.equalsIgnoreCase("-1")) {
				messageViewHolder.menuLeft.setVisibility(View.GONE);
				messageViewHolder.menuRight.setVisibility(View.INVISIBLE);
			}
		if (messageViewHolder.message.aniType != null
				&& messageViewHolder.message.aniType
						.equalsIgnoreCase("animation")) {
			messageViewHolder.menuRight.setVisibility(View.INVISIBLE);
		}

		messageViewHolder.menuLeft.setVisibility(View.GONE);
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

	boolean flagAnimNotPlay = false;

	// ////////////////////////////////////////////////////////////////////////////////
	// //
	// //
	// //
	// Left Side //
	// //
	// //
	// ////////////////////////////////////////////////////////////////////////////////
	public void renderLeft(View convertView, Context context,
			final Message message, MessageViewHolder messageViewHolder,
			ParticipantInfo participantInfo) {

		message.participantInfoClazz = participantInfo;
		messageViewHolder.viewAll.setVisibility(View.VISIBLE);
		messageViewHolder.notification.setVisibility(View.GONE);

		// if (message.datetime != null) {
		messageViewHolder.datetime.setVisibility(View.VISIBLE);
		// messageViewHolder.datetime.setText(message.datetime);
		messageViewHolder.datetime.setText(Utilities.compareDate(
				message.inserTime, ConversationsActivity.this));
		// } else {
		// messageViewHolder.datetime.setVisibility(View.GONE);
		// }
		if (mPopupWindow != null && mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		messageViewHolder.notification.setOnClickListener(this);
		messageViewHolder.notification.setTextColor(getResources().getColor(
				R.color.sub_heading));
		// This is manually added to show loading...
		if ((message.messageId.equalsIgnoreCase("-999") && message.mfuId
				.equalsIgnoreCase("-999"))) {
			messageViewHolder.datetime.setVisibility(View.GONE);
			messageViewHolder.viewAll.setVisibility(View.GONE);
			messageViewHolder.notification.setVisibility(View.VISIBLE);
			messageViewHolder.notification
					.setBackgroundResource(R.drawable.loadmore);
			messageViewHolder.notification.setText(getResources().getString(
					R.string.load_prevoius_message));
			messageViewHolder.notification.setOnClickListener(this);
			messageViewHolder.notification.setTag("LOADING");
			messageViewHolder.left.setOnLongClickListener(null);
			messageViewHolder.notification.setTextColor(getResources()
					.getColor(R.color.titleredend));
			if (FeedRequester.feedTaskConversationMessagesRefresh != null
					&& FeedRequester.feedTaskConversationMessagesRefresh
							.getStatus() != Status.FINISHED) {
				messageViewHolder.notification
						.setBackgroundResource(R.drawable.loadmore);
				messageViewHolder.notification.setText(getResources()
						.getString(R.string.wait_loading));
			}
			return;
		}

		if (message.messageType == Message.MESSAGE_TYPE_UPDATE_SUBJECT
				|| message.messageType == Message.MESSAGE_TYPE_LEAVE_CONVERSATION
				|| message.messageType == Message.MESSAGE_TYPE_ADD_TO_CONVERSATION
				|| message.messageType == Message.MESSAGE_TYPE_ADD_TO_CONVERSATION_ALREADY) {
			messageViewHolder.viewAll.setVisibility(View.GONE);
			messageViewHolder.notification.setVisibility(View.VISIBLE);
			messageViewHolder.notification
					.setBackgroundResource(R.drawable.chatmessagenotification);
			messageViewHolder.notification.setText(message.msgTxt);
			messageViewHolder.left.setOnLongClickListener(null);
			messageViewHolder.notification.setOnClickListener(null);
			return;
		}

		String txt = message.msgTxt;
		messageViewHolder.view_rtml.setVisibility(View.GONE);
		if (txt != null) {
			messageViewHolder.messageView.setVisibility(View.VISIBLE);
			if (message.contentBitMap != null
					&& message.contentBitMap.indexOf("RTML") != -1) {
				txt = "System RTML Message";
				messageViewHolder.view_rtml.setVisibility(View.VISIBLE);
				messageViewHolder.view_rtml.setTag(message);
				messageViewHolder.view_rtml.setOnClickListener(this);
			}

			// will remove this login this temp because m not geting
			// notification flag in feed
			if (0 < (message.notificationFlags & InboxTblObject.NOTI_FRIEND_INVITATION_MSG)) {
				Utilities.jsonParserEngine(txt);
				if (Utilities.accept() != null) {
					txt = "";// "This is Friend Request";
//					convertView.findViewById(R.message_row_right.friend_invite).setVisibility(View.VISIBLE);
				}
			}
			if (participantInfo != null) {
				messageViewHolder.messageView.setText(txt);
			} else {
				messageViewHolder.messageView.setText(txt);
				participantInfo = new ParticipantInfo();
				participantInfo.userName = SettingData.sSelf.getUserName();
				participantInfo.profileUrl = SettingData.sSelf.getUserName();
			}
			messageViewHolder.messageView
					.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			messageViewHolder.messageView.setVisibility(View.GONE);
		}
		if (0 < (message.notificationFlags & InboxTblObject.NOTI_BUZZ_AUTOPLAY)) {
			messageViewHolder.messageView.setVisibility(View.VISIBLE);
			messageViewHolder.messageView.setText("BUZZzzzzz");
		}
		if (message.conversationId != null && conversationList.isGroup == 1) {
			messageViewHolder.conversation_id.setVisibility(View.VISIBLE);
			if ((participantInfo.firstName != null && participantInfo.firstName
					.length() > 0)
					&& (participantInfo.lastName != null && participantInfo.lastName
							.length() > 0))
				messageViewHolder.conversation_id
						.setText(participantInfo.firstName + " "
								+ participantInfo.lastName);
			else if (participantInfo.firstName != null
					&& participantInfo.firstName.length() > 0) {
				messageViewHolder.conversation_id
						.setText(participantInfo.firstName);
			} else if (participantInfo.lastName != null
					&& participantInfo.lastName.length() > 0) {
				messageViewHolder.conversation_id
						.setText(participantInfo.lastName);
			} else
				messageViewHolder.conversation_id
						.setText(participantInfo.userName);

			messageViewHolder.conversation_id
					.setTextSize(BusinessProxy.sSelf.textFontSize);
		} else {
			messageViewHolder.conversation_id.setVisibility(View.GONE);
		}

		message.participantInfoClazz = participantInfo;

		// /////////////
		messageViewHolder.voice.setVisibility(View.GONE);
		messageViewHolder.voicePlay.setVisibility(View.GONE);
		messageViewHolder.receving_voiceLeft.setVisibility(View.GONE);// gone
		messageViewHolder.receving_voiceLeft.setText(getResources().getString(
				R.string.reciving_voice));
		messageViewHolder.receving_voiceLeft.setOnClickListener(null);
		messageViewHolder.voice.setOnClickListener(this);
		messageViewHolder.voice.setTag(message);
		messageViewHolder.voicePlay.setOnClickListener(this);
		messageViewHolder.voicePlay.setTag(message);
		if (message.voice_content_urls != null) {
			if (message.audio_download_statue != 1) {
				if ((System.currentTimeMillis() - message.inserTime) < 1000 * 30) {
					messageViewHolder.voice.setVisibility(View.VISIBLE);
					Downloader downloader = Downloader.getInstance();
					downloader.download(message.voice_content_urls, 2, this);
					messageViewHolder.receving_voiceRight
							.setText(getResources().getString(
									R.string.reciving_voice));
					messageViewHolder.receving_voiceLeft
							.setVisibility(View.VISIBLE);
				} else if (Downloader.links
						.contains(message.voice_content_urls)) {
					messageViewHolder.receving_voiceLeft.setText(getResources()
							.getString(R.string.reciving_voice));
					messageViewHolder.receving_voiceLeft
							.setVisibility(View.VISIBLE);
				} else {
					if (Downloader.getInstance().getUrl() != null
							&& Downloader
									.getInstance()
									.getUrl()
									.equalsIgnoreCase(
											message.voice_content_urls)) {
						messageViewHolder.receving_voiceRight
								.setText(getResources().getString(
										R.string.reciving_voice));
					} else {
						messageViewHolder.voice.setVisibility(View.VISIBLE);
						// messageViewHolder.receving_voiceLeft.setText(getResources().getString(R.string.reciving_voice));
						messageViewHolder.receving_voiceLeft
								.setText(getResources().getString(
										R.string.get_voice));
					}
					messageViewHolder.receving_voiceLeft
							.setOnClickListener(this);
					messageViewHolder.receving_voiceLeft.setTag(message);
					messageViewHolder.receving_voiceLeft
							.setVisibility(View.VISIBLE);
				}
			} else {
				messageViewHolder.voice.setImageResource(R.drawable.chat_audio);
				messageViewHolder.voice.setVisibility(View.VISIBLE);
				messageViewHolder.voicePlay.setVisibility(View.VISIBLE);
			}
		}
		// /////////
		ImageDownloader imageManager = null;
		if (conversationList.isGroup == 1) {
			imageManager = new ImageDownloader();
			imageManager.download(participantInfo.userName,
					messageViewHolder.me_image, ConversationsActivity.this);
			messageViewHolder.me_image.setImageUrl(participantInfo.userName);
			messageViewHolder.me_image.setVisibility(View.VISIBLE);
			messageViewHolder.me_image.setOnClickListener(this);
			messageViewHolder.me_image.setTag(participantInfo);
		} else
			messageViewHolder.me_image.setVisibility(View.GONE);
		// messageViewHolder.me_image.setVisibility(View.GONE);
		//
		// messageViewHolder.images.setVisibility(View.GONE);
		//
		messageViewHolder.images.removeAllViews();
		float sizeInDipWeidth = 200f;
		float sizeInDipHeight = 200f;
		int heightPX = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDipHeight, getResources()
						.getDisplayMetrics());
		int weigthPX = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDipWeidth, getResources()
						.getDisplayMetrics());
		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
				weigthPX, heightPX);
		parms.setMargins(5, 0, 5, 0);
		if (message.image_content_thumb_urls != null
				&& message.image_content_thumb_urls.trim().length() > 0) {
			messageViewHolder.images.setVisibility(View.VISIBLE);
			s = Utilities.split(new StringBuffer(
					message.image_content_thumb_urls), Constants.COL_SEPRETOR);
			for (int j = 0; j < s.length; j++) {
				CImageView b = new CImageView(ConversationsActivity.this);
				// b.setId(Integer.parseInt(message.messageId));
				b.setScaleType(ScaleType.CENTER_CROP);
				parms.setMargins(5, 0, 5, 0);
				// b.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
				b.setBackgroundResource(R.drawable.border);
				// LinearLayout frame = new
				// LinearLayout(ConversationsActivity.this);
				// parms.setMargins(5, 5, 5, 5);
				// frame.setLayoutParams(parms);
				b.setLayoutParams(parms);
				// b.setImageUrl(s[j]);
				// b.setBackgroundResource(R.drawable.photo_frame);
				// frame.setBackgroundResource(R.drawable.photo_frame);//nage
				// tl.addView(frame);
				ImageView iv = b;

				imageLoader.displayImage(s[j], iv, options);
				/*
				 * imageLoader.displayImage(s[j], iv, options, new
				 * SimpleImageLoadingListener() {
				 * 
				 * @Override public void onLoadingComplete(Bitmap loadedImage) {
				 * // Animation anim =
				 * AnimationUtils.loadAnimation(ConversationsActivity.this,
				 * R.anim.fade_in); // imageView.setAnimation(anim); //
				 * anim.start(); } });
				 */

				/*
				 * imageManager = new ImageDownloader();
				 * imageManager.download(s[j], b, ConversationsActivity.this,
				 * ImageDownloader.TYPE_IMAGE);
				 */
				images.put(s[j].hashCode(), b);

				// b.setTag(message);
				// b.setOnClickListener(this);
				b.setOnLongClickListener(onLongClickListenerShowCopy);
				b.setTag(messageViewHolder.message);
				messageViewHolder.images.addView(b);
				// b.setId(1000);
				b.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (voiceIsPlaying) {
							showSimpleAlert("Info",
									"Other media is running please stop first!");
							return;
						}
						ArrayList<String> imagesPathl = new ArrayList<String>();
						ArrayList<String> imagesPathlocal = new ArrayList<String>();
						Feed.Entry entry = new Feed().new Entry();// feed.entry.get(Integer.parseInt(index));
						entry.media = new Vector<String>();

						String s[] = Utilities.split(new StringBuffer(
								message.image_content_urls),
								Constants.COL_SEPRETOR);

						for (int i = 0; i < s.length; i++) {
							imagesPathl.add(s[i]);
						}

						s = Utilities.split(new StringBuffer(
								message.image_content_urls),
								Constants.COL_SEPRETOR);
						boolean isLocal = true;
						for (int i = 0; i < s.length; i++) {
							imagesPathlocal.add(s[i]);
							if (s[i].startsWith("http"))
								isLocal = false;
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
						DataModel.sSelf.storeObject(DMKeys.ENTRY + "COMM",
								entry);

						Utilities.setInt(ConversationsActivity.this,
								"chatlistpos",
								listViewActivity.getFirstVisiblePosition());
						int top = (v == null) ? 0 : v.getTop();
						Utilities.setInt(ConversationsActivity.this,
								"chatlisttop", top);

						if (isLocal) {
							Intent intent = new Intent(
									ConversationsActivity.this,
									MultiPhotoViewer.class);
							intent.putStringArrayListExtra("DATA",
									(ArrayList<String>) imagesPathlocal);
							startActivityForResult(intent, Constants.ResultCode);
						} else {
							Intent intentpics = new Intent(
									ConversationsActivity.this,
									StreemMultiPhotoViewer.class);
							startActivityForResult(intentpics,
									Constants.ResultCode);
						}
					}
				});
				// if(message.inserTime<)

				// openDialogFullScreen(0,feed);
			}
		}
		messageViewHolder.animLayoutLeftImage.removeAllViews();
		if (message.aniValue != null
				&& message.aniType.equalsIgnoreCase("animation")) {
			// if(BusinessProxy.sSelf.db==null)
			// BusinessProxy.sSelf.db=BusinessProxy.sSelf.db=new
			// DatabaseHandler(BusinessProxy.sSelf.addPush.mContext);
			// //System.out.printlnprintln("message.aniValue====="+message.aniValue);
			// RtAnimFeed
			// feed=BusinessProxy.sSelf.db.getAnimFeedByAnimTag(message.aniValue.trim());
//			RtAnimFeed feed = null;

//			if (BusinessProxy.sSelf.animValue != null) {
//
//				for (RtAnimFeed valueData : BusinessProxy.sSelf.animValue) {
//					if (valueData.getAnimTag().equalsIgnoreCase(
//							message.aniValue)) {
//						feed = valueData;
//					}
//				}
//			}
//			if (feed != null) {
//				messageViewHolder.messageView.setVisibility(View.GONE);
//				messageViewHolder.notification.setVisibility(View.GONE);
//				CImageView b = new CImageView(ConversationsActivity.this);
//				parms.setMargins(5, 0, 5, 0);
//				Bitmap drawImage = null;
//				if (feed.getStatus().equalsIgnoreCase("DELETE"))
//					drawImage = decodeFile(feed.getThumbCaption() + "_"
//							+ feed.getSetId() + "_temp" + ".png");
//				else
//					drawImage = decodeFile(feed.getThumbCaption() + "_"
//							+ feed.getSetId() + ".png");
//				if (drawImage != null)
//					b.setImageBitmap(drawImage);
//				else {
//					b.setBackgroundResource(R.drawable.placeholder);
//				}
//				// b.setBackgroundColor(R.drawable.b_buzz);
//				b.setTag(feed);
//				b.setOnClickListener(this);
//				// b.setOnLongClickListener(onLongClickListenerShowCopy);
//				b.setTag(feed);
//				messageViewHolder.animLayoutLeftImage.addView(b);
//				// 05-15 17:47:26.805: I///System.out.println23098):
//				// message.inserTime===0 secs
//				// agomessage.inserTime===1368620247000
//
//				b.setId(1420);
//				// if(Utilities.CompareTime(message.inserTime)<5){
//				// if ((System.currentTimeMillis() - message.inserTime) < 1000 *
//				// 30 && !playedAniId.contains(message.messageId)) {
//				if (message.lastMsg
//						&& message.hasBeenViewed.equalsIgnoreCase("false")
//						&& !playedAniId.contains(message.messageId)) {
//					// //System.out.printlnprintln("conversation render bug");
//
//					if (!flagAnimNotPlay) {
//
//						playedAniId.add(message.messageId);
//						openDialogFullScreen(0, feed);
//					}
//				}
//				// //System.out.printlnprintln("message.inserTime==="+Utilities.compareDate(message.inserTime)+"message.inserTime==="+message.inserTime);
//				// 05-15 17:43:38.752: I///System.out.println22665):
//				// message.inserTime===0 secs ago
//
//				// openDialogFullScreen(feed.getSetId());
//			} 
//			else {
//				// LoadAnim(message.aniValue);
//				CImageView b = new CImageView(ConversationsActivity.this);
//				b.setBackgroundResource(R.drawable.placeholder);
//				// parms.setMargins(5, 0, 5, 0);
//				// b.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
//				// b.setBackgroundResource(R.drawable.border);
//				// LinearLayout frame = new
//				// LinearLayout(ConversationsActivity.this);
//				// parms.setMargins(5, 5, 5, 5);
//				// frame.setLayoutParams(parms);
//				// b.setLayoutParams(parms);
//
//				// b.setBackgroundResource(R.drawable.photo_frame);
//				// frame.setBackgroundResource(R.drawable.photo_frame);//nage
//				// tl.addView(frame);
//				// imageManager = new ImageDownloader();
//				// imageManager.download(feed.getThumbUrl(), b,
//				// ConversationsActivity.this,ImageDownloader.TYPE_IMAGE);
//				// b.setBackgroundColor(R.drawable.b_buzz);
//				// b.setTag(feed);
//				// b.setOnClickListener(this);
//				// b.setOnLongClickListener(onLongClickListenerShowCopy);
//				// b.setTag(feed);
//				messageViewHolder.animLayoutLeftImage.addView(b);
//				// 05-15 17:47:26.805: I///System.out.println23098):
//				// message.inserTime===0 secs
//				// agomessage.inserTime===1368620247000
//
//				// b.setId(1420);
//			}
		}
		messageViewHolder.voice
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showCopy(v);
						return false;
					}
				});
		messageViewHolder.images
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showCopy(v);
						return false;
					}
				});
		// parms = new LinearLayout.LayoutParams(getThumbwidht(500),
		// getThumbHeight(450));
		// parms.setMargins(5, 0, 5, 0);
		messageViewHolder.video
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showCopy(v);
						return false;
					}
				});
		messageViewHolder.video.setVisibility(View.GONE);
		// messageViewHolder.video.removeAllViews();
		if (message.video_content_thumb_urls != null
				&& message.video_content_thumb_urls.trim().length() > 0) {
			messageViewHolder.video.setVisibility(View.VISIBLE);
			s = Utilities.split(new StringBuffer(
					message.video_content_thumb_urls), Constants.COL_SEPRETOR);
			String[] ss = Utilities.split(new StringBuffer(
					message.video_content_urls), Constants.COL_SEPRETOR);
			if (message.video_size == null)
				message.video_size = "";

			String[] size = Utilities.split(
					new StringBuffer(message.video_size),
					Constants.COL_SEPRETOR);
			int j = 0;
			// for (int j = 0; j < s.length; j++)
			{
				sizeInDipWeidth = 130f;
				sizeInDipHeight = 130f;
				heightPX = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, sizeInDipHeight,
						getResources().getDisplayMetrics());
				weigthPX = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, sizeInDipWeidth,
						getResources().getDisplayMetrics());

				parms = new LinearLayout.LayoutParams(weigthPX, heightPX);

				LinearLayout l1 = null;//
				if (messageViewHolder.video.getChildCount() <= 0) {
					l1 = new LinearLayout(this);
					// l1.setLayoutParams(parms);
					l1.setOrientation(LinearLayout.VERTICAL);
					// l1.setBackgroundColor(getResources().getColor(R.color.white))
					// ;
					parms.setMargins(5, 0, 5, 0);
					l1.setLayoutParams(parms);
				} else
					l1 = (LinearLayout) messageViewHolder.video.getChildAt(0);

				Button btn = null;
				CImageView b = null;// new
									// CImageView(ConversationsActivity.this);
				if (l1.getChildCount() <= 0) {
					b = new CImageView(ConversationsActivity.this);
					btn = new Button(this);
					b.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					btn.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, 20));
				} else {
					b = (CImageView) l1.getChildAt(1);
					btn = (Button) l1.getChildAt(0);
				}

				// b.setBackgroundResource(R.drawable.border);

				b.setImageUrl(s[j]);
				// //System.out.printlnprintln("------video--left-url---"+s[j]);

				imageManager = new ImageDownloader();
				b.setImageUrl(s[j]);
				imageManager.download(s[j], b, ConversationsActivity.this,
						ImageDownloader.TYPE_VIDEO);
				b.setTag(message);
				b.setScaleType(ScaleType.CENTER_CROP);
				b.setOnClickListener(null);
				b.setOnLongClickListener(onLongClickListenerShowCopy);
				b.setTag(messageViewHolder.message);

				VideoUrl videoUrl = new VideoUrl();
				videoUrl.url = ss[j];
				videoUrl.index = (j + 1);
				btn.setVisibility(View.INVISIBLE);
				if (s[j].trim().startsWith("http"))
					btn.setText(getResources().getString(R.string.get_video)
							+ "["
							+ Utilities.humanReadableByteCount(
									Long.parseLong(size[j]), true) + "]");
				videoUrl.acaton = 0;
				b.setId(100011);
				if (VDownloader.getInstance().getUrl() != null
						&& VDownloader.getInstance().getUrl()
								.equalsIgnoreCase(ss[j])) {
					btn.setText(getResources().getString(
							R.string.reciving_video));
					videoUrl.acaton = 1;
					btn.setVisibility(View.VISIBLE);
				} else if (VDownloader.getInstance().check(ss[j])) {
					b.setOnClickListener(this);
					b.setId(100012);
					// b.setTag(ss[j]) ;
					videoUrl.playUrl = ss[j];
					btn.setVisibility(View.INVISIBLE);
					parms.setMargins(5, 0, 5, 0);
					// l1.setLayoutParams(parms);
				}
				btn.setBackgroundResource(R.drawable.trans);
				btn.setTextColor(Color.WHITE);
				btn.setGravity(Gravity.RIGHT);
				if (l1.getChildCount() <= 0) {
					l1.addView(btn);
					l1.addView(b);
				}

				b.setOnClickListener(this);
				btn.setTextSize(9f);
				videoUrl.view = btn;

				b.setTag(videoUrl);
				l1.setBackgroundResource(R.drawable.video_overlay);
				if (messageViewHolder.video.getChildCount() <= 0)
					messageViewHolder.video.addView(l1);
			}
		}
		// ////System.out.printlnprintln("message.video_content_thumb_urls=="+message.video_content_thumb_urls);
		// if (message.video_content_thumb_urls != null
		// && message.video_content_thumb_urls.trim().length() > 0) {
		// messageViewHolder.images.setVisibility(View.VISIBLE);
		// imageManager = new ImageDownloader();
		// imageManager.download(message.video_content_thumb_urls,
		// messageViewHolder.video, ConversationsActivity.this);
		// messageViewHolder.video.setLayoutParams(parms);
		// messageViewHolder.video.setVisibility(View.VISIBLE);
		// messageViewHolder.video.setTag(message);
		// messageViewHolder.video.setOnClickListener(this);
		// }
		// convertView.invalidate();
	}

	public Vector<String> playedAniId = new Vector<String>();

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

	// ////////////////////////////////////////////////////////////////////////////////
	// //
	// //
	// //
	// Right Side //
	// //
	// //
	// //
	// ////////////////////////////////////////////////////////////////////////////////
	public void renderRight(View convertView, Context context, Message message,
			MessageViewHolder messageViewHolder, ParticipantInfo participantInfo) {

		// if (message.datetime != null) {
		// messageViewHolder.datetimeRight.setVisibility(View.VISIBLE);
		// // messageViewHolder.datetime.setText(message.datetime);
		// messageViewHolder.datetimeRight.setText(Utilities.compareDate(message.inserTime));
		// } else {
		// messageViewHolder.datetimeRight.setVisibility(View.GONE);
		// }
		if (mPopupWindow != null && mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		messageViewHolder.viewAllRight.setVisibility(View.VISIBLE);
		messageViewHolder.notificationRight.setVisibility(View.GONE);

		messageViewHolder.notification.setOnClickListener(null);
		messageViewHolder.viewAllRight.setVisibility(View.VISIBLE);
		messageViewHolder.notificationRight.setVisibility(View.GONE);
		messageViewHolder.notificationRight.setOnClickListener(this);
		messageViewHolder.notificationRight.setTextColor(getResources()
				.getColor(R.color.sub_heading));
		// //System.out.printlnprintln("-----------------message.messageId: "+message.messageId);
		// //System.out.printlnprintln("-----------------message.mfuId: "+message.mfuId);
		if (message.messageId.equalsIgnoreCase("-999")
				&& message.mfuId.equalsIgnoreCase("-999")) {

			messageViewHolder.datetimeRight.setVisibility(View.GONE);
			messageViewHolder.viewAllRight.setVisibility(View.GONE);
			messageViewHolder.notificationRight.setVisibility(View.VISIBLE);
			messageViewHolder.notification
					.setBackgroundResource(R.drawable.loadmore);
			messageViewHolder.notificationRight.setText(getResources()
					.getString(R.string.load_prevoius_message));
			messageViewHolder.notificationRight.setOnClickListener(this);
			messageViewHolder.notificationRight.setTag("LOADING");
			messageViewHolder.left.setOnLongClickListener(null);
			messageViewHolder.notificationRight.setTextColor(getResources()
					.getColor(R.color.titleredend));

			if (FeedRequester.feedTaskConversationMessagesRefresh != null
					&& FeedRequester.feedTaskConversationMessagesRefresh
							.getStatus() != Status.FINISHED) {
				messageViewHolder.notification
						.setBackgroundResource(R.drawable.loadmore);
				messageViewHolder.notificationRight.setText(getResources()
						.getString(R.string.wait_loading));
				// getResources().getString(
				// R.string.wait_loading)

			}
			return;
		}

		if (message.messageType == message.MESSAGE_TYPE_UPDATE_SUBJECT
				|| message.messageType == message.MESSAGE_TYPE_LEAVE_CONVERSATION
				|| message.messageType == message.MESSAGE_TYPE_ADD_TO_CONVERSATION
				|| message.messageType == message.MESSAGE_TYPE_ADD_TO_CONVERSATION_ALREADY) {
			messageViewHolder.notification
					.setBackgroundResource(R.drawable.chatmessagenotification);
			messageViewHolder.viewAllRight.setVisibility(View.GONE);
			messageViewHolder.notificationRight.setVisibility(View.VISIBLE);
			messageViewHolder.notificationRight.setText(message.msgTxt);
			messageViewHolder.left.setOnLongClickListener(null);
			messageViewHolder.notification.setOnClickListener(null);
			messageViewHolder.datetimeRight.setVisibility(View.VISIBLE);
			if (message.mfuId.equalsIgnoreCase("-1")) {
				if (message.datetime.indexOf(":") == -1)
					messageViewHolder.datetimeRight.setText(Utilities
							.compareDate(Long.parseLong(message.datetime),
									ConversationsActivity.this));
				else
					messageViewHolder.datetimeRight.setText(message.datetime);
			} else
				messageViewHolder.datetimeRight.setText(Utilities.compareDate(
						message.inserTime, ConversationsActivity.this));

			return;
		}

		message.participantInfoClazz = participantInfo;
		messageViewHolder.sending_status_right.setVisibility(View.GONE);
		// if (message.inserTime != null)
		// {
		messageViewHolder.datetimeRight.setVisibility(View.VISIBLE);
		// if(message.mfuId.equalsIgnoreCase("-1")){
		// if(message.datetime.indexOf(":")==-1)
		// messageViewHolder.datetimeRight.setText(Utilities.compareDate(Long.parseLong(message.datetime)));
		// else
		// messageViewHolder.datetimeRight.setText(message.datetime);
		// }
		// else
		messageViewHolder.datetimeRight.setText(Utilities.compareDate(
				message.inserTime, ConversationsActivity.this));
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
		messageViewHolder.special.setVisibility(View.GONE);
		if (message.drmFlags == -100) {
			messageViewHolder.special.setVisibility(View.VISIBLE);
			messageViewHolder.special.setText("This is a forwarded message");
		}
		if (message.drmFlags == -200) {
			messageViewHolder.special.setVisibility(View.VISIBLE);
			messageViewHolder.special.setText("This is a media share message");
		}
		messageViewHolder.view_rtml_right.setVisibility(View.GONE);
		String txt = message.msgTxt;// +":"+message.aniValue;
		if (0 < (message.notificationFlags & InboxTblObject.NOTI_BUZZ_AUTOPLAY)) {
			messageViewHolder.messageViewRight.setText("BUZZ");
		} else if (txt != null) {
			messageViewHolder.messageViewRight.setVisibility(View.VISIBLE);
			if (message.contentBitMap != null
					&& message.contentBitMap.indexOf("RTML") != -1)// txt.indexOf("<?xml version=")
			// != -1)
			{
				txt = "";// "This is System Message";
				messageViewHolder.view_rtml_right.setVisibility(View.VISIBLE);
				messageViewHolder.view_rtml_right.setTag(message);
				messageViewHolder.view_rtml_right.setOnClickListener(this);
			}

			// will remove this login this temp because m not geting
			// notification flag in feed
			if (0 < (message.notificationFlags & InboxTblObject.NOTI_FRIEND_INVITATION_MSG)) {
				Utilities.jsonParserEngine(txt);
				if (Utilities.accept() != null) {
					txt = "This is Friend Request";
//					convertView.findViewById(R.message_row_right.friend_invite).setVisibility(View.VISIBLE);
				}
			}
			// if(message.drmFlags==-100){
			// messageViewHolder.messageViewRight.setVisibility(View.VISIBLE);
			// messageViewHolder.messageViewRight.setText("This is a forwarded message");
			// }else
			{
				// Spanned spannedContent = getSmiledText(this, txt);
				// messageViewHolder.messageViewRight.setText(spannedContent,
				// BufferType.SPANNABLE);

				if (txt != null && txt.length() < 50 && isContainEmoticon(txt)) {
					Spanned spannedContent = null;

					SoftReference<Spanned> span = (SoftReference<Spanned>) this.spannedContent
							.get(txt.hashCode());

					if (span != null && span.get() != null) {

						spannedContent = span.get();
						// if(spannedContent==null)
						// //System.out.printlnprintln("---------spaned incache new spaned right :"+txt.hashCode()+"  but null");
						// else
						// //System.out.printlnprintln("---------spaned incache new spaned right :"+txt.hashCode()+" : "+txt);
					}
					if (spannedContent == null) {

						spannedContent = getSmiledText(this, txt);
						span = new SoftReference<Spanned>(spannedContent);
						if (span != null) {

							this.spannedContent.put(txt.hashCode(), span);
							// //System.out.printlnprintln("---------spaned creating and cacheing new spaned right :"+txt);
							// //System.out.printlnprintln("---------spaned creating and cacheing new spaned right spannedContent :"+spannedContent.toString());
						}
					}
					// else
					// //System.out.printlnprintln("---------spaned getting from cache right :");

					messageViewHolder.messageViewRight.setText(spannedContent,
							BufferType.SPANNABLE);
				} else
					messageViewHolder.messageViewRight.setText(txt);
			}
			// messageViewHolder.messageViewRight.setText(txt);//+" : "+message.voice_content_urls);
			// messageViewHolder.messageViewRight.setText(txt+" : "+message.sending_state);//
			// +":"+participantInfo.userName+":"+":"+participantInfo.firstName+":"+participantInfo.lastName+":"+participantInfo.profileImageUrl+":"+participantInfo.profileUrl);
			messageViewHolder.messageViewRight
					.setTextSize(BusinessProxy.sSelf.textFontSize);

			if (0 < (message.notificationFlags & InboxTblObject.NOTI_BUZZ_AUTOPLAY)) {
				messageViewHolder.messageViewRight.setText("BUZZ");
			}
		} else {
			messageViewHolder.messageViewRight.setVisibility(View.GONE);
		}

		// if (message.conversationId != null) {
		// messageViewHolder.conversation_idRight.setVisibility(View.VISIBLE);
		// // if (Logger.CHEAT)
		// // messageViewHolder.conversation_idRight.setText(message.drmFlags
		// // + ":" + message.notificationFlags + ":" + message.mfuId
		// // + " : " + message.messageId + " : " + message.more
		// // + " : " + message.hasBeenViewed + " : " + " :in "
		// // + message.isNext + ":" + message.sortTime + ":"
		// // + message.direction + ":" + message.lastMsg);
		// // else
		// if ((participantInfo.firstName != null && participantInfo.firstName
		// .length() > 0)
		// && (participantInfo.lastName != null && participantInfo.lastName
		// .length() > 0))
		// messageViewHolder.conversation_idRight
		// .setText(participantInfo.firstName + " "
		// + participantInfo.lastName);
		// else if(participantInfo.firstName != null &&
		// participantInfo.firstName.length()>0){
		// messageViewHolder.conversation_idRight
		// .setText(participantInfo.firstName);
		// }
		// else
		// messageViewHolder.conversation_idRight
		// .setText(participantInfo.userName);
		// messageViewHolder.conversation_idRight
		// .setTextSize(BusinessProxy.sSelf.textFontSize);
		// } else {
		messageViewHolder.conversation_idRight.setVisibility(View.GONE);
		// }

		messageViewHolder.voiceRight.setVisibility(View.GONE);
		messageViewHolder.voiceRightPlay.setVisibility(View.GONE);
		messageViewHolder.receving_voiceRight.setVisibility(View.GONE);
		messageViewHolder.receving_voiceRight.setText(getResources().getString(
				R.string.reciving_voice));
		messageViewHolder.receving_voiceRight.setOnClickListener(null);
		messageViewHolder.voiceRight.setOnClickListener(this);
		messageViewHolder.voiceRight.setTag(message);
		messageViewHolder.voiceRightPlay.setOnClickListener(this);
		messageViewHolder.voiceRightPlay.setTag(message);
		if (message.voice_content_urls != null) {
			if (message.audio_download_statue != 1) {
				// //System.out.printlnprintln("-----------time diff-- :"+(System.currentTimeMillis()
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
						.setImageResource(R.drawable.chat_audio);
				messageViewHolder.voiceRight.setVisibility(View.VISIBLE);
				messageViewHolder.voiceRightPlay.setVisibility(View.VISIBLE);
			}
		}
		ImageDownloader imageManager = new ImageDownloader();
		messageViewHolder.otherImageRight.setImageUrl(participantInfo.userName);
		imageManager.download(participantInfo.userName,
				messageViewHolder.otherImageRight, ConversationsActivity.this);
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

		float sizeInDipWeidth = 200f;
		float sizeInDipHeight = 200f;

		// float sizeInDipWeidth = 80f;
		// float sizeInDipHeight = 60f;
		// if (Utilities.getScreenType(this) ==
		// Constants.SCREENLAYOUT_SIZE_X_LARGE) {
		// sizeInDipWeidth = 120f;
		// sizeInDipHeight = 90f;
		// } else if (Utilities.getScreenType(this) ==
		// Constants.SCREENLAYOUT_SIZE_LARGE) {
		// sizeInDipWeidth = 100f;
		// sizeInDipHeight = 75f;
		// } else if (Utilities.getScreenType(this) ==
		// Constants.SCREENLAYOUT_SIZE_MEDIUM) {
		// sizeInDipWeidth = 80f;
		// sizeInDipHeight = 60f;
		// } else if (Utilities.getScreenType(this) ==
		// Constants.SCREENLAYOUT_SIZE_SMALL) {
		// sizeInDipWeidth = 80f;
		// sizeInDipHeight = 60f;
		// }
		int heightPX = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDipHeight, getResources()
						.getDisplayMetrics());
		int weigthPX = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, sizeInDipWeidth, getResources()
						.getDisplayMetrics());

		// //System.out.printlnprintln("weigthPX===="+weigthPX
		// +"heightPX=="+heightPX);
		LinearLayout.LayoutParams parms = null;// new LinearLayout.LayoutParams(
		// width, heightPX);
		parms = new LinearLayout.LayoutParams(weigthPX, heightPX);
		parms.setMargins(5, 0, 5, 0);

		messageViewHolder.imagesRight.removeAllViews();
		messageViewHolder.imagesRight.setVisibility(View.GONE);
		if (message.image_content_thumb_urls != null
				&& message.image_content_thumb_urls.trim().length() > 0) {
			messageViewHolder.imagesRight.setVisibility(View.VISIBLE);

			messageViewHolder.imagesRight.removeAllViews();

			// parms.setMargins(5, 5, 5, 5);
			// messageViewHolder.videoRight.setLayoutParams(parms);
			s = Utilities.split(new StringBuffer(
					message.image_content_thumb_urls), Constants.COL_SEPRETOR);
			String ss[] = Utilities.split(new StringBuffer(
					message.image_content_urls), Constants.COL_SEPRETOR);
			// for (int j = 0; j < s.length; j++) {
			// if (!s[j].trim().startsWith("http")) {
			// //System.out.printlnprintln("----s----: "+s[j]);
			// //System.out.printlnprintln("----ss----: "+ss[j]);
			// }
			// }

			for (int j = 0; j < s.length; j++) {
				CImageView b = new CImageView(ConversationsActivity.this);
				// b.setId(Integer.parseInt(message.messageId));
				b.setScaleType(ScaleType.CENTER_CROP);
				parms.setMargins(5, 0, 5, 0);
				b.setLayoutParams(parms);
				if (s[j].trim().startsWith("http")) {
					b.setBackgroundResource(R.drawable.border);
					ImageView iv = b;
					/*
					 * imageLoader.displayImage(s[j], iv, options, new
					 * SimpleImageLoadingListener() {
					 * 
					 * @Override public void onLoadingComplete(Bitmap
					 * loadedImage) { // Animation anim =
					 * AnimationUtils.loadAnimation(ConversationsActivity.this,
					 * R.anim.fade_in); // imageView.setAnimation(anim); //
					 * anim.start(); } });
					 */
					imageLoader.displayImage(s[j], iv, options);
					images.put(s[j].hashCode(), b);
				} else {
					if (s[j].indexOf("/") == -1 && !s[j].startsWith("/")
							&& Integer.parseInt(s[j]) != -1) {
						b.setScaleType(ScaleType.CENTER_CROP);
						parms.setMargins(5, 0, 5, 0);
						b.setLayoutParams(parms);
						b.setImageBitmap(ImageUtils.getImageFileThumb(
								ConversationsActivity.this,
								Integer.parseInt(s[j])));
					} else {
						Bitmap bm = ImageUtils.getImageFileThumb(
								ConversationsActivity.this, ImageUtils
										.getImagesFileId(
												ConversationsActivity.this,
												ss[j]));
						if (bm != null) {
							BitmapDrawable bmd = new BitmapDrawable(bm);
							b.setScaleType(ScaleType.CENTER_CROP);
							parms.setMargins(5, 0, 5, 0);
							b.setLayoutParams(parms);
							b.setImageDrawable(bmd);
						} else {
							if (bm == null) {
								BitmapFactory.Options bfo = new BitmapFactory.Options();
								bfo.inSampleSize = 2;
								bm = BitmapFactory.decodeFile(ss[j], bfo);
								bm = ImageUtils.rotateImage(ss[j], bm);
							}
							if (bm != null) {
								// bm = Bitmap.createScaledBitmap(bm, 200, 200,
								// true);
								b.setScaleType(ScaleType.CENTER_CROP);
								parms.setMargins(5, 0, 5, 0);
								b.setLayoutParams(parms);
								b.setImageBitmap(bm);
							}
						}
					}
				}
				messageViewHolder.imagesRight.addView(b);
				b.setTag(message);
				b.setOnClickListener(this);
				b.setOnLongClickListener(onLongClickListenerShowCopy);
				b.setTag(messageViewHolder.message);
				b.setId(1000);
			}
		}
		messageViewHolder.animLayoutRightImage.removeAllViews();
//		if (message.aniValue != null && message.aniType.equalsIgnoreCase("animation")) {
//			// if(BusinessProxy.sSelf.db==null)
//			// BusinessProxy.sSelf.db=BusinessProxy.sSelf.db=new
//			// DatabaseHandler(BusinessProxy.sSelf.addPush.mContext);
//			// RtAnimFeed
//			// feed=BusinessProxy.sSelf.db.getAnimFeedByAnimTag(message.aniValue.trim());
//			messageViewHolder.imagesRight.setVisibility(View.VISIBLE);
//			RtAnimFeed feed = null;
//			if (BusinessProxy.sSelf.animValue != null) {
//				for (RtAnimFeed valueData : BusinessProxy.sSelf.animValue) {
//					if (valueData.getAnimTag().equalsIgnoreCase(
//							message.aniValue)) {
//						feed = valueData;
//					}
//				}
//			}
//			if (feed != null) {
//				messageViewHolder.imagesRight.setVisibility(View.GONE);
//				messageViewHolder.messageViewRight.setVisibility(View.GONE);
//				messageViewHolder.notificationRight.setVisibility(View.GONE);
//				CImageView b = new CImageView(ConversationsActivity.this);
//				parms.setMargins(5, 0, 5, 0);
//				// b.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
//				// b.setBackgroundResource(R.drawable.border);
//				// LinearLayout frame = new
//				// LinearLayout(ConversationsActivity.this);
//				// parms.setMargins(5, 5, 5, 5);
//				// frame.setLayoutParams(parms);
//				// b.setLayoutParams(parms);
//
//				b.setBackgroundResource(R.drawable.photo_frame);
//				// frame.setBackgroundResource(R.drawable.photo_frame);//nage
//				// tl.addView(frame);
//				// feed.getThumbCaption()+"_"+feed.getSetId()+".png"
//				/*
//				 * if(feed.getStatus().equalsIgnoreCase("DELETE")){
//				 * 
//				 * File file =new
//				 * File(Environment.getExternalStorageDirectory()+gifpath,
//				 * feed.getGifCaption()+"_"+feed.getSetId()+"_temp"+".gif");
//				 * if(file.exists() && file.length()==0){ file.delete();
//				 * ImageDownloadZeroSize task = new
//				 * ImageDownloadZeroSize(feed.getGifCaption
//				 * ()+"_"+feed.getSetId()+"_temp"); task.execute(new String[] {
//				 * feed.getGifUrl()}); } file =new
//				 * File(Environment.getExternalStorageDirectory()+gifpath,
//				 * feed.getThumbCaption()+"_"+feed.getSetId()+"_temp"+".png");
//				 * if(file.exists() && file.length()==0){ file.delete(); //
//				 * System
//				 * .out.println("getGifCaption========"+feed.getThumbUrl());
//				 * ImageDownloadZeroSize task = new
//				 * ImageDownloadZeroSize(feed.getThumbCaption
//				 * ()+"_"+feed.getSetId()+"_temp"); task.execute(new String[] {
//				 * feed.getThumbUrl()}); } file =new
//				 * File(Environment.getExternalStorageDirectory()+gifpath,
//				 * feed.getAudioCaption()+"_"+feed.getSetId()+"_temp"+".mp3");
//				 * if(file.exists() && file.length()==0){
//				 * 
//				 * file.delete();
//				 * 
//				 * ImageDownloadZeroSize task = new
//				 * ImageDownloadZeroSize(feed.getAudioCaption
//				 * ()+"_"+feed.getSetId()+"_temp"); task.execute(new String[] {
//				 * feed.getAudioUrl()}); } }else{ File file =new
//				 * File(Environment.getExternalStorageDirectory()+gifpath,
//				 * feed.getGifCaption()+"_"+feed.getSetId()+".gif");
//				 * if(file.exists() && file.length()==0){ file.delete();
//				 * ImageDownloadZeroSize task = new
//				 * ImageDownloadZeroSize(feed.getGifCaption
//				 * ()+"_"+feed.getSetId()); task.execute(new String[] {
//				 * feed.getGifUrl()}); } file =new
//				 * File(Environment.getExternalStorageDirectory()+gifpath,
//				 * feed.getThumbCaption()+"_"+feed.getSetId()+".png");
//				 * if(file.exists() && file.length()==0){ file.delete(); //
//				 * System
//				 * .out.println("getGifCaption========"+feed.getThumbUrl());
//				 * ImageDownloadZeroSize task = new
//				 * ImageDownloadZeroSize(feed.getThumbCaption
//				 * ()+"_"+feed.getSetId()); task.execute(new String[] {
//				 * feed.getThumbUrl()}); } file =new
//				 * File(Environment.getExternalStorageDirectory()+gifpath,
//				 * feed.getAudioCaption()+"_"+feed.getSetId()+".mp3");
//				 * if(file.exists() && file.length()==0){
//				 * 
//				 * file.delete();
//				 * 
//				 * ImageDownloadZeroSize task = new
//				 * ImageDownloadZeroSize(feed.getAudioCaption
//				 * ()+"_"+feed.getSetId()); task.execute(new String[] {
//				 * feed.getAudioUrl()}); } }
//				 */
//				/*
//				 * Drawable drawImage = null; if
//				 * (feed.getStatus().equalsIgnoreCase("DELETE")) drawImage =
//				 * getPicsFromSdcard(feed.getThumbCaption() + "_" +
//				 * feed.getSetId() + "_temp" + ".png"); else drawImage =
//				 * getPicsFromSdcard(feed.getThumbCaption() + "_" +
//				 * feed.getSetId() + ".png"); if (drawImage != null)
//				 * b.setBackgroundDrawable(drawImage); else {
//				 * b.setBackgroundResource(R.drawable.placeholder);
//				 */
//
//				Bitmap drawImage = null;
//				if (feed.getStatus().equalsIgnoreCase("DELETE"))
//					drawImage = decodeFile(feed.getThumbCaption() + "_"
//							+ feed.getSetId() + "_temp" + ".png");
//				else
//					drawImage = decodeFile(feed.getThumbCaption() + "_"
//							+ feed.getSetId() + ".png");
//				if (drawImage != null)
//					b.setImageBitmap(drawImage);
//				else {
//					b.setBackgroundResource(R.drawable.placeholder);
//
//				}
//
//				// ImageDownloader imageManage1 = new ImageDownloader();
//				// imageManage1.download(feed.getThumbUrl(), b,
//				// ConversationsActivity.this,imageManage1.TYPE_IMAGE);
//				// b.setBackgroundColor(R.drawable.b_buzz);
//				b.setTag(feed);
//				b.setOnClickListener(this);
//				// b.setOnLongClickListener(onLongClickListenerShowCopy);
//				b.setTag(feed);
//				// messageViewHolder.images.addView(b);
//				b.setId(1420);
//				// openDialogFullScreen(feed.getSetId());
//				messageViewHolder.animLayoutRightImage.addView(b);
//			} else {
//
//				// LoadAnim(message.aniValue);
//				CImageView b = new CImageView(ConversationsActivity.this);
//				b.setBackgroundResource(R.drawable.placeholder);
//				// parms.setMargins(5, 0, 5, 0);
//				// b.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
//				// b.setBackgroundResource(R.drawable.border);
//				// LinearLayout frame = new
//				// LinearLayout(ConversationsActivity.this);
//				// parms.setMargins(5, 5, 5, 5);
//				// frame.setLayoutParams(parms);
//				// b.setLayoutParams(parms);
//
//				// b.setBackgroundResource(R.drawable.photo_frame);
//				// frame.setBackgroundResource(R.drawable.photo_frame);//nage
//				// tl.addView(frame);
//				// imageManager = new ImageDownloader();
//				// imageManager.download(feed.getThumbUrl(), b,
//				// ConversationsActivity.this,ImageDownloader.TYPE_IMAGE);
//				// b.setBackgroundColor(R.drawable.b_buzz);
//				// b.setTag(feed);
//				// b.setOnClickListener(this);
//				// b.setOnLongClickListener(onLongClickListenerShowCopy);
//				// b.setTag(feed);
//				messageViewHolder.animLayoutRightImage.addView(b);
//				// 05-15 17:47:26.805: I///System.out.println23098):
//				// message.inserTime===0 secs
//				// agomessage.inserTime===1368620247000
//
//				// b.setId(1420);
//
//			}
//		}
		messageViewHolder.voiceRight
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showCopy(v);
						return false;
					}
				});
		messageViewHolder.imagesRight
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showCopy(v);
						return false;
					}
				});
		// parms = new LinearLayout.LayoutParams(getThumbwidht(500),
		// getThumbHeight(450));
		// parms.setMargins(5, 0, 5, 0);
		messageViewHolder.videoRight
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						showCopy(v);
						return false;
					}
				});
		// LinearLayout.LayoutParams parmsvid = new LinearLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, heightPX);
		// parmsvid.setMargins(5, 0, 5, 0);
		messageViewHolder.videoRight.setVisibility(View.GONE);
		// messageViewHolder.videoRight.removeAllViews();
		// if (message.video_content_thumb_urls != null
		// && message.video_content_thumb_urls.trim().length() > 0) {
		// messageViewHolder.imagesRight.setVisibility(View.VISIBLE);
		// ////System.out.printlnprintln("---------------message.video_content_thumb_urls : "+message.video_content_thumb_urls);
		// if (message.video_content_thumb_urls.trim().startsWith("http")) {//
		// local
		// imageManager = new ImageDownloader();
		// imageManager.download(message.video_content_thumb_urls,
		// messageViewHolder.videoRight,
		// ConversationsActivity.this);
		// } else {
		// Bitmap bm = ImageUtils.getVideoFileThumb(
		// ConversationsActivity.this, ImageUtils.getVideoFileId(
		// ConversationsActivity.this,
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
			// //System.out.printlnprintln("----------- ideo thumb : "+message.video_content_thumb_urls);
			s = Utilities.split(new StringBuffer(
					message.video_content_thumb_urls), Constants.COL_SEPRETOR);
			String[] ss = Utilities.split(new StringBuffer(
					message.video_content_urls), Constants.COL_SEPRETOR);
			if (message.video_size == null)
				message.video_size = "";
			String[] size = Utilities.split(
					new StringBuffer(message.video_size),
					Constants.COL_SEPRETOR);

			int j = 0;
			// for (int j = 0; j < ss.length; j++)
			{
				sizeInDipWeidth = 130f;
				sizeInDipHeight = 130f;
				heightPX = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, sizeInDipHeight,
						getResources().getDisplayMetrics());
				weigthPX = (int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_DIP, sizeInDipWeidth,
						getResources().getDisplayMetrics());

				parms = new LinearLayout.LayoutParams(weigthPX, heightPX);

				LinearLayout l1 = null;//
				if (messageViewHolder.videoRight.getChildCount() <= 0) {
					l1 = new LinearLayout(this);
					// l1.setLayoutParams(parms);
					l1.setOrientation(LinearLayout.VERTICAL);
					// l1.setBackgroundColor(getResources().getColor(R.color.red))
					// ;
					parms.setMargins(5, 0, 5, 0);
					l1.setLayoutParams(parms);
				} else
					l1 = (LinearLayout) messageViewHolder.videoRight
							.getChildAt(0);

				Button btn = null;
				CImageView b = null;// new
									// CImageView(ConversationsActivity.this);
				if (l1.getChildCount() <= 0) {
					b = new CImageView(ConversationsActivity.this);
					btn = new Button(this);
					b.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
					btn.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, 20));
				} else {
					b = (CImageView) l1.getChildAt(1);
					btn = (Button) l1.getChildAt(0);
				}

				// LinearLayout l1 = new LinearLayout(this);
				parms.setMargins(5, 0, 5, 0);
				// l1.setLayoutParams(parms);// new
				// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT))
				// ;
				parms.gravity = Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL;
				// l1.setBackgroundResource(R.color.red);
				l1.setOrientation(LinearLayout.VERTICAL);
				// CImageView b = new CImageView(ConversationsActivity.this);

				// parms.setMargins(5, 5, 5, 5);
				// b.setLayoutParams(new
				// LinearLayout.LayoutParams(getThumbwidht(500),
				// getThumbHeight(320)));

				// b.setBackgroundResource(R.drawable.border);
				// LinearLayout frame = new
				// LinearLayout(ConversationsActivity.this);
				// parms.setMargins(5, 5, 5, 5);
				// frame.setLayoutParams(parms);

				// b.setBackgroundResource(R.drawable.photo_frame);
				// frame.setBackgroundResource(R.drawable.photo_frame);//nage
				// tl.addView(frame);
				if (s[j].trim().startsWith("http")) {
					imageManager = new ImageDownloader();
					b.setImageUrl(s[j]);
					// System.out.printlnprintln("------video-url---"+s[j]);
					imageManager.download(s[j], b, ConversationsActivity.this,
							ImageDownloader.TYPE_IMAGE);
				} else {
					Bitmap bm = ImageUtils.getVideoFileThumb(
							ConversationsActivity.this, ImageUtils
									.getVideoFileId(ConversationsActivity.this,
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
					// l1.setLayoutParams(parms);
				}
				b.setTag(message);
				b.setScaleType(ScaleType.FIT_XY);
				b.setOnClickListener(null);
				b.setOnLongClickListener(onLongClickListenerShowCopy);
				if (otherParticipantInfo != null
						&& otherParticipantInfo.userName != null) {
					messageViewHolder.message.source = otherParticipantInfo.userName;
				}
				b.setTag(messageViewHolder.message);

				// Button btn = new Button(this);

				VideoUrl videoUrl = new VideoUrl();
				videoUrl.url = ss[j];
				videoUrl.index = (j + 1);
				if (s[j].trim().startsWith("http"))
					btn.setText(getResources().getString(R.string.get_video)
							+ "["
							+ Utilities.humanReadableByteCount(
									Long.parseLong(size[j]), true) + "]");
				videoUrl.acaton = 0;
				b.setId(100011);

				btn.setVisibility(View.INVISIBLE);
				if (s[j].trim().startsWith("http")) {
					if (VDownloader.getInstance().getUrl() != null
							&& VDownloader.getInstance().getUrl()
									.equalsIgnoreCase(ss[j])) {
						btn.setText(getResources().getString(
								R.string.reciving_video));
						videoUrl.acaton = 1;
						btn.setVisibility(View.VISIBLE);
					} else if (VDownloader.getInstance().check(ss[j])) {
						b.setOnClickListener(this);
						b.setId(100012);
						b.setTag(ss[j]);
						videoUrl.playUrl = ss[j];
						btn.setVisibility(View.GONE);
						// parms =new
						// LinearLayout.LayoutParams(getThumbwidht(500),
						// getThumbHeight(320));
						// parms.setMargins(5, 0, 5, 0);
						// l1.setLayoutParams(parms);

					}
				} else {
					b.setOnClickListener(this);
					b.setId(100012);
					// b.setTag(ss[j]) ;
					// btn.setVisibility(View.GONE) ;
					videoUrl.playUrl = ss[j];
					btn.setVisibility(View.INVISIBLE);
					// parms =new LinearLayout.LayoutParams(getThumbwidht(500),
					// getThumbHeight(320));
					// parms.setMargins(5, 0, 5, 0);
					// l1.setLayoutParams(parms);
				}
				btn.setBackgroundResource(R.drawable.trans);
				btn.setTextColor(Color.WHITE);
				btn.setGravity(Gravity.RIGHT);

				if (l1.getChildCount() <= 0) {
					l1.addView(btn);
					l1.addView(b);
				}

				btn.setTextSize(9f);
				// b.setId(100011);
				b.setOnClickListener(this);
				videoUrl.view = btn;
				b.setTag(videoUrl);

				// b.setImageResource(R.drawable.video_overlay);
				l1.setBackgroundResource(R.drawable.video_overlay);
				if (messageViewHolder.videoRight.getChildCount() <= 0) {
					messageViewHolder.videoRight.addView(l1);
				}
				// b.setId(1000*3049);
			}
		}

	}

	int totalNew = 0;
	// 2973305 2150203437

	String responseStr;
	int requestForID;
	public static Vector<String> cidRefresh = new Vector<String>();

	@Override
	public void onResponseSucess(final String responseStr,
			final int requestForID) {
		this.responseStr = responseStr;
		this.requestForID = requestForID;

		if (Constants.FEED_GET_CONVERSATION_MESSAGES == requestForID) {
			if (FeedRequester.feedTaskConversationMessagesRefresh != null
					&& FeedRequester.feedTaskConversationMessagesRefresh.hiddenCall
					&& FeedRequester.feedTaskConversationMessagesRefresh.requestDone) {
				if (!cidRefresh.contains(getIntent().getStringExtra(Constants.CONVERSATION_ID)))
					cidRefresh.add(getIntent().getStringExtra(Constants.CONVERSATION_ID));
				// //System.out.printlnprintln("------ all requested competed----------1 : "+FeedRequester.feedTaskConversationMessagesRefresh.requestDone);
			}
		}

		// //System.out.printlnprintln("------onResponseSucess: requestForID: "+requestForID
		// );
		// //System.out.printlnprintln("------onResponseSucess responseStr: "+responseStr
		// );
		if (FeedRequester.feedTaskConversationMessages != null) {
			// //System.out.printlnprintln("------onResponseSucess-feedTaskConversationMessages.iRetriveTotalNoOfFeed----------"+FeedRequester.feedTaskConversationMessages.iRetriveTotalNoOfFeed);
		}

		if (responseStr != null
				&& responseStr.equalsIgnoreCase(Constants.CHECKING_DONE)) {
			UpdateUi task = new UpdateUi();
			if(activityAdapter != null && (activityAdapter.getCursor().getCount()) == 0){
				task.execute(new String[] { LISTBOTTOM });
			}else
			{
			task.execute(new String[] { "0" });
			}
			//task.execute(new String[] { LISTBOTTOM });
			// if (Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID) {
			// if(!cidRefresh.contains(getIntent().getStringExtra(
			// Constants.CONVERSATION_ID)))
			// cidRefresh.add(getIntent().getStringExtra(
			// Constants.CONVERSATION_ID));
			//
			// ////System.out.printlnprintln(" on post execute inserted:"+cidRefresh.contains(getIntent().getStringExtra(
			// // Constants.CONVERSATION_ID)));
			// //if(FeedRequester.feedTaskConversationMessages!=null &&
			// FeedRequester.feedTaskConversationMessages.iRetriveTotalNoOfFeed>=50)
			// //load_prevoius_message.setVisibility(View.GONE) ;
			//
			// }

			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
					MessageTable.TABLE,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ? and "
							+ MessageTable.MESSAGE_TYPE + " !=? and "
							+ MessageTable.MESSAGE_ID + " ='-999'",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "",
							"" + Message.MESSAGE_TYPE_SYSTEM_MESSAGE }, null,
					null, MessageTable.INSERT_TIME + " ASC");
			int cnt = cursor.getCount();
			cursor.close();
			// //System.out.printlnprintln("------onResponseSucess: cnt : "+cnt
			// );
			// hitting another request if server return less tha 50 or zero for
			// new message
			if (FeedRequester.feedTaskConversationMessages != null
					&& FeedRequester.feedTaskConversationMessages.iRetriveTotalNoOfFeed < 50
					&& FeedRequester.feedTaskConversationMessages.iRetriveTotalNoOfFeed > -1
					&& cnt <= 0) {
				if (FeedRequester.feedTaskConversationMessagesRefresh == null
						|| FeedRequester.feedTaskConversationMessagesRefresh
								.getStatus() == Status.FINISHED) {
					// //System.out.printlnprintln("------onResponseSucess: hitting another request if server return less tha 50 or zero for new message"
					// );
					FeedRequester.requestConversationMessagesRefresh(
							ConversationsActivity.this, getIntent()
									.getStringExtra(Constants.CONVERSATION_ID),
							this);
					if (FeedRequester.feedTaskConversationMessagesRefresh != null) {
						if (load_prevoius_message.getVisibility() == View.GONE)
							handler.post(new Runnable() {
								@Override
								public void run() {

									// load_prevoius_message.setBackgroundResource(R.drawable.loadmore);
									load_prevoius_message
											.setVisibility(View.VISIBLE);
								}
							});
					} else {

					}
				}
				if (FeedRequester.feedTaskConversationMessagesRefresh != null) {
					FeedRequester.feedTaskConversationMessagesRefresh
							.setHttpSynch(ConversationsActivity.this);
					FeedRequester.feedTaskConversationMessagesRefresh.hiddenCall = true;
				}
				FeedRequester.feedTaskConversationMessages.iRetriveTotalNoOfFeed = -1;
			} else if (Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID
					&& FeedRequester.feedTaskConversationMessages != null
					&& FeedRequester.feedTaskConversationMessages.requestDone) {
				if (!cidRefresh.contains(getIntent().getStringExtra(
						Constants.CONVERSATION_ID)))
					cidRefresh.add(getIntent().getStringExtra(
							Constants.CONVERSATION_ID));
				// //System.out.printlnprintln("------ all requested competed----------2"+FeedRequester.feedTaskConversationMessages.requestDone);
			}
			return;
		}
		else{
			//Restart Messaage refresh service
			Log.i(TAG, "onResponseSucess :: Restart Messaage refresh service");
			restartMessageRefresh();
		}

		// UpdateUi task = new UpdateUi();
		// task.execute(new String[] { UPDATE_ON_RESPONCE });

		// //System.out.printlnprintln("------onResponseSucess: "+FeedRequester.feedTaskConversationMessages.getStatus()
		// );
		// FeedTask.refreshList=false;
		// activityAdapter.getCursor().requery() ;
		// activityAdapter.notifyDataSetInvalidated();
		// // if((System.currentTimeMillis()-idelTime)>2000)
		// // listViewActivity
		// //
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		// listViewActivity.post(new Runnable(){
		// public void run() {
		// listViewActivity.setSelection(activityAdapter.getCursor().getCount()
		// - 1);
		// }});
		//

		// UpdateUi task = new UpdateUi();
		// task.execute(new String[] { BLOCK_PAD });

		// //System.out.printlnprintln("------onResponseSucess FeedTask.updateParticipantInfoUI : "+FeedTask.updateParticipantInfoUI);
		// //System.out.printlnprintln("------onResponseSucess");
		// //System.out.printlnprintln("------onResponseSucess CONVERSATIONID"+CONVERSATIONID);
		// //System.out.printlnprintln("------onResponseSucess getIntent().getStringExtra( Constants.CONVERSATION_ID)"+getIntent().getStringExtra(
		// Constants.CONVERSATION_ID));

		// //System.out.printlnprintln("------onResponseSucess switiching list --"+responseStr+"   :refreshList "
		// +FeedTask.refreshList
		// +" : UPDATE_ME"+FeedTask.UPDATE_ME
		// +" : KEEP_POSITION"+FeedTask.KEEP_POSITION
		// );

		handler.post(new Runnable() {
			@Override
			public void run() {
				try {

					if (!CONVERSATIONID.equalsIgnoreCase(getIntent()
							.getStringExtra(Constants.CONVERSATION_ID))/*
																		 * && !
																		 * responseStr
																		 * .
																		 * equalsIgnoreCase
																		 * (
																		 * "REFRESH VIEW FEOM COMPOSE"
																		 * )
																		 */) {
						getIntent().putExtra(Constants.CONVERSATION_ID,
								CONVERSATIONID);
						CONVERSATIONID = getIntent().getStringExtra(
								Constants.CONVERSATION_ID);
						initParticipantInfo();
						updateParticepentLabel();

						// stopManagingCursor(activityAdapter.getCursor());

						// listViewActivity = (ListView)
						// findViewById(R.conversations.landing_discovery_activity_list);
						// Send update subject request - File ID and Suject
						if (mucFileId != null || mucSubject != null)
							sendUpdateSubjectRequest(mucSubject);
						activityAdapter = new ConversationsAdapter(
								ConversationsActivity.this, getCursor(), true,
								ConversationsActivity.this);
						listViewActivity.setAdapter(activityAdapter);
						return;
					}

					if (((System.currentTimeMillis() - idelTime) > 2000
							&& responseStr != null && responseStr
							.indexOf("REFRESH") != -1)
							|| FeedTask.UPDATE_ME
							|| ComposeService.refreshList)
					// if(FeedTask.UPDATE_ME || ComposeService.refreshList)
					{
						// //System.out.printlnprintln("------onResponseSucess--"+responseStr+"   :f "+FeedTask.refreshList);
						// //System.out.printlnprintln("------onResponseSucess--"+responseStr+"   :c "+ComposeService.refreshList);
						int oldCount = activityAdapter.getCount();
						// //System.out.printlnprintln("------onResponseSucess--old count:"+activityAdapter.getCount());
						activityAdapter.getCursor().requery();
						// //System.out.printlnprintln("------------loading sursor 1-------------");
						int newCount = activityAdapter.getCount();
						// //System.out.printlnprintln("------onResponseSucess--new count:"+activityAdapter.getCount());
						// activityAdapter.getCursor().moveToLast();

						int old = activityAdapter.getCount();
						activityAdapter.notifyDataSetInvalidated();
						if (idelTime > 0
								&& requestForID != Constants.FEED_GET_CONVERSATION_MESSAGES) {
							FeedTask.KEEP_POSITION = true;
							totalNew = totalNew + (newCount - oldCount);
							// new_message.setText(""+totalNew) ; // change by
							// akm
							if (FeedTask.latestMessageCurrentCID != null) {
								String s = "";
								if (FeedTask.latestMessageCurrentCID.msgTxt != null)
									s = FeedTask.latestMessageCurrentCID.msgTxt;
								new_message_info
										.setText(FeedTask.latestMessageCurrentCID.msgTxt);

								new_message.setVisibility(View.VISIBLE);
								new_message_layout.setVisibility(View.VISIBLE);
								if (FeedTask.latestMessageCurrentCID.image_content_urls != null
										&& FeedTask.latestMessageCurrentCID.image_content_urls
												.trim().length() > 0) {
									findViewById(R.chat_panel.image)
											.setVisibility(View.VISIBLE);
								} else
									findViewById(R.chat_panel.image)
											.setVisibility(View.GONE);

								if (FeedTask.latestMessageCurrentCID.video_content_urls != null
										&& FeedTask.latestMessageCurrentCID.video_content_urls
												.trim().length() > 0) {
									findViewById(R.chat_panel.video)
											.setVisibility(View.VISIBLE);
								} else
									findViewById(R.chat_panel.video)
											.setVisibility(View.GONE);

								if (FeedTask.latestMessageCurrentCID.voice_content_urls != null
										&& FeedTask.latestMessageCurrentCID.voice_content_urls
												.trim().length() > 0) {
									findViewById(R.chat_panel.voice)
											.setVisibility(View.VISIBLE);
								} else
									findViewById(R.chat_panel.voice)
											.setVisibility(View.GONE);

								if (FeedTask.latestMessageCurrentCID.aniType != null
										&& FeedTask.latestMessageCurrentCID.aniType
												.trim().length() > 0) {
									findViewById(R.chat_panel.animicon)
											.setVisibility(View.GONE);// kainat
								} else
									findViewById(R.chat_panel.animicon)
											.setVisibility(View.GONE);
							}
							FeedTask.latestMessageCurrentCID = null;
						}

						if (FeedTask.UPDATE_ME && !FeedTask.KEEP_POSITION
								&& listViewActivity != null)
							listViewActivity
									.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
						if (FeedTask.KEEP_POSITION && listViewActivity != null)
							listViewActivity
									.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
						FeedTask.KEEP_POSITION = false;

						if (activityAdapter != null) {
							// activityAdapter.getCursor().requery() ;
							// //System.out.printlnprintln("------------loading sursor 2-------------");
							activityAdapter.notifyDataSetChanged();
						}
						// listViewActivity.invalidate();//nag
					}
					if (FeedTask.updateParticipantInfoUI) {
						initParticipantInfo();
						updateParticepentLabel();
						blockInputPad();
					}
					if (FeedTask.updateParticipantInfoUISUB) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									handler.post(new Runnable() {

										@Override
										public void run() {
											updateMUCSubject();
										}
									});

									FeedTask.updateParticipantInfoUISUB = false;
								} catch (Exception e) {
								}
							}
						}).start();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (Constants.FEED_GET_CONVERSATION_MESSAGES == requestForID) {
					load_prevoius_message.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onResponseSucess(Object responseStr, final int requestForID) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (Constants.FEED_GET_CONVERSATION_MESSAGES == requestForID) {
					// listViewActivity.onRefreshComplete();
					final int oldPos = listViewActivity
							.getFirstVisiblePosition();
					// listViewActivity
					// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
					// activityAdapter = new ConversationsAdapter(
					// ConversationsActivity.this, getCursor(), true,
					// ConversationsActivity.this);
					// listViewActivity.setAdapter(activityAdapter);

					if (oldPos > 1) {
						listViewActivity.setSelection(oldPos);
						Log.v("ConverstionsActivity","Manoj"+" 7186");
						listViewActivity.setStackFromBottom(true);
					}
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
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, true,
				name.length);

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
//							Intent intent = new Intent(
//									ConversationsActivity.this,
//									BuddyActivity.class);
//							intent.putExtra(BuddyActivity.SCREEN_MODE,
//									BuddyActivity.MODE_ONLY_FRIENDS);
//							intent.putExtra("MULTISELECTED", true);
//							intent.putExtra("ADDCONVERSATIONS", true);
//							startActivityForResult(intent, 1);
							break;
						case ID_LEAVE_CONVERSATION:
							request = new Request(ID_LEAVE_CONVERSATION, "url");
							if (!ConversationsActivity.this
									.checkInternetConnection()) {
								ConversationsActivity.this.networkLossAlert();
								return;
							}
							request.execute("LikeDislike");
							break;
						case ID_UPDATE_SUBJECT:
							// final InboxMessage element = inboxElement;
							dialog = new Dialog(
									ConversationsActivity.this,
									android.R.style.Theme_Translucent_NoTitleBar);
							dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
							dialog.setContentView(R.layout.save_msg_dialog);
							dialog.setCancelable(true);
							((TextView) dialog
									.findViewById(R.password_dialog.message)).setText(ConversationsActivity.this
									.getString(R.string.add_conversation_subject));
							Button okButton = (Button) dialog
									.findViewById(R.id.save);
							okButton.setText(ConversationsActivity.this
									.getString(R.string.update));
							okButton.setOnClickListener(new OnClickListener() {
								public void onClick(View view) {
									Utilities.closeSoftKeyBoard(view,
											ConversationsActivity.this);
									dialog.dismiss();
									String value = ((EditText) dialog
											.findViewById(R.id.tag)).getText()
											.toString();
									request = new Request(ID_UPDATE_SUBJECT,
											"url");
									request.subject = value;
									if (mucFileId != null)
										request.muc_file_id = mucFileId;
									if (!ConversationsActivity.this
											.checkInternetConnection()) {
										ConversationsActivity.this
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
											ConversationsActivity.this);
									dialog.dismiss();
								}
							});
							dialog.show();
							break;
						case DELETE_CONVERSATION:
							request = new Request(DELETE_CONVERSATION, "url");
							if (!ConversationsActivity.this
									.checkInternetConnection()) {
								ConversationsActivity.this.networkLossAlert();
								return;
							}
							request.execute("LikeDislike");

							break;
						case ID_DELETE_MESSAGE:

							// DialogInterface.OnClickListener deleteHandler =
							// new DialogInterface.OnClickListener() {
							//
							// public void onClick(DialogInterface dialog, int
							// which) {
							// request = new Request(ID_DELETE_MESSAGE, "url");
							// request.execute("LikeDislike");
							// }
							// };
							//
							// showAlertMessage("Confirm",
							// "Do you really want to delete this message?", new
							// int[] { DialogInterface.BUTTON_POSITIVE,
							// DialogInterface.BUTTON_NEGATIVE }, new String[] {
							// "Yes",
							// "No" }, new DialogInterface.OnClickListener[] {
							// deleteHandler, null });
							//
							//

							break;
						case ID_SAVE_MESSAGE:
							dialog = new Dialog(
									ConversationsActivity.this,
									android.R.style.Theme_Translucent_NoTitleBar);
							dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
							dialog.setContentView(R.layout.save_msg_dialog);
							dialog.setCancelable(true);

							// ((TextView)
							// dialog.findViewById(R.password_dialog.message)).setText(ConversationsActivity.this.getString(R.string.add_conversation_subject))
							// ;
							okButton = (Button) dialog.findViewById(R.id.save);
							// okButton.setText(ConversationsActivity.this.getString(R.string.update));
							okButton.setOnClickListener(new OnClickListener() {
								public void onClick(View view) {
									Utilities.closeSoftKeyBoard(view,
											ConversationsActivity.this);
									dialog.dismiss();
									String value = ((EditText) dialog
											.findViewById(R.id.tag)).getText()
											.toString();

									if (value != null && value.length() > 0) {
										request = new Request(ID_SAVE_MESSAGE,
												"url");
										request.subject = value;
										if (mucFileId != null)
											request.muc_file_id = mucFileId;
										if (!ConversationsActivity.this
												.checkInternetConnection()) {
											ConversationsActivity.this
													.networkLossAlert();
											return;
										}
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
											ConversationsActivity.this);
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

	// private Vector<String> mPicturePath = new Vector<String>();
	// private Vector<String> mPicturePathCanvas = new Vector<String>();
	// private Vector<Integer> mPicturePathId = new Vector<Integer>();
	// http://http://124.153.95.174:28080/tejas/rest/rtmessaging/postMessage
	synchronized public String quickSend(String animTag, String animImageUrl,
			Vector<String> mPicturePath) {
		String d = ((EditText) findViewById(R.community_chat.msgBox)).getText()
				.toString();
		// //System.out.printlnprintln("--------------quicksend--------");

		Utilities.setInt(this, "listpos", 0);
		Utilities.setInt(this, "top", 0);

		// chat_panel_more_option_emo.setVisibility(View.GONE);
		// chat_panel_more_option.setVisibility(View.GONE);
		boolean noContent = true;
		// ///////////
		if (d != null && d.trim().length() > 0)
			noContent = false;
		if (animTag != null && animTag.length() > 0) {
			noContent = false;
		}

		int attachemtnt = 0;
		long attachemtntsize = d.length();
		// if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0) {
		// for (int i = 0; i < mPicturePathCanvas.size(); i++) {
		// mPicturePath.add(mPicturePathCanvas.get(i));
		// }
		// mPicturePathCanvas.clear();
		// ((TextView) findViewById(R.id.image_count_dodle)).setText("");
		//
		// }
		if (mPicturePath != null && mPicturePath.size() > 0) {

			attachemtnt = attachemtnt + mPicturePath.size();
			noContent = false;
		}

		if (mVideoPath != null && mVideoPath.length() > 0) {

			attachemtnt = attachemtnt + 1;
			noContent = false;
		}

		if (mVoicePath != null) {

			attachemtnt = attachemtnt + 1;
			noContent = false;
		}
		findViewById(R.chat_panel.more).setBackgroundResource(
				R.drawable.chat_up);
		// chat_panel_more_option.setVisibility(View.GONE);
		// chat_panel_more_option_emo.setVisibility(View.GONE);
		// image_count.setVisibility(View.GONE);
		// ((TextView) findViewById(R.id.video_count)).setText("");
		// ((TextView) findViewById(R.id.voice_count)).setText("");
		// ((TextView) findViewById(R.id.video_count)).setVisibility(View.GONE);
		// ((TextView) findViewById(R.id.voice_count)).setVisibility(View.GONE);
		// ((TextView) findViewById(R.id.image_count_dodle))
		// .setVisibility(View.GONE);
		// //System.out.printlnprintln("-------conversationList.comments-----"+conversationList.comments);
		ContentValues values = new ContentValues();
		long id = ((System.currentTimeMillis() - 10000));
		if (animTag != null && animTag.length() > 0) {
			noContent = false;
			values.put(MediaPostTable.COMMAND_TYPE, "" + animTag);
			values.put(MediaPostTable.COMMAND, "2");
		}
		values.put(MediaPostTable.DATE, id);
		values.put(MediaPostTable.COMMENT, conversationList.comments);
		values.put(MediaPostTable.ATTACHMENT, attachemtnt);
		// values.put(MediaPostTable.TAG, tag);
		values.put(MediaPostTable.MEDIS_STAUTS, MediaPostTable.STATUS_QUEUE);
		values.put(MediaPostTable.ABOUT, d.trim());
		// values.put(MediaPostTable.MODE, mode);
		values.put(MediaPostTable.TRY, 0);

		// values.put(MediaPostTable.CATEGORY, catId);
		String imageUrls = "";
		String imageThumbId = "";
		String videoUrls = "";
		if (mPicturePath != null && mPicturePath.size() > 0) {
			if (mPicturePath != null && mPicturePath.size() > 0) {
				for (int i = 0; i < mPicturePath.size(); i++) {
					attachemtntsize = attachemtntsize
							+ ImageUtils.getImageSize(mPicturePath.get(i));
					values.put("IMAGE_" + (i + 1), mPicturePath.get(i));
					imageUrls = imageUrls + mPicturePath.get(i)
							+ Constants.COL_SEPRETOR;
					if (mPicturePathId != null && mPicturePathId.size() > 0) {
						// if(mPicturePathId.get(i)!= -1)
						// imageThumbId = imageThumbId + mPicturePathId.get(i)
						// + Constants.COL_SEPRETOR;
						// else
						imageThumbId = imageThumbId
								+ ImageUtils.getImagesFileId(
										ConversationsActivity.this,
										mPicturePath.get(i))
								+ Constants.COL_SEPRETOR;
						;
					} else
						imageThumbId = imageThumbId + -1
								+ Constants.COL_SEPRETOR;
				}
			}
		}
		// for canvas
		if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0) {
			if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0) {
				for (int i = 0; i < mPicturePathCanvas.size(); i++) {
					attachemtntsize = attachemtntsize
							+ ImageUtils
									.getImageSize(mPicturePathCanvas.get(i));
					values.put("IMAGE_" + (i + 1), mPicturePathCanvas.get(i));
					imageUrls = imageUrls + mPicturePathCanvas.get(i)
							+ Constants.COL_SEPRETOR;
					// if (mPicturePathId != null && mPicturePathId.size() > 0)
					// imageThumbId = imageThumbId + mPicturePathId.get(i)
					// + Constants.COL_SEPRETOR;
					// else
					imageThumbId = imageThumbId + -1 + Constants.COL_SEPRETOR;
					noContent = false;
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
					videoUrls = mVideoPath;
				}
			}
		}

		if (mVoicePath != null) {
			values.put(MediaPostTable.AUDIO, mVoicePath);
		}

		if (noContent) {
			// showToast("Nothing to send");
			return null;
		}
		BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf
				.getTransactionId() + 1);
		long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
		// ;

		System.out.println("------s---- : imageUrls : " + imageUrls);
		System.out.println("------ss---- : imageThumbId : " + imageThumbId);
		values.put(MediaPostTable.CLTTXNID, cltTxnId);

		values.put(MediaPostTable.ATTACHMENT_SIZE, attachemtntsize);
		// values.put(MediaPostTable.CLTTXNID, BusinessProxy.sSelf.getUserId());
		values.put(MediaPostTable.USER_ID, BusinessProxy.sSelf.getUserId());
		values.put(MediaPostTable.USER_PASS, SettingData.sSelf.getPassword());
		values.put(MediaPostTable.CLIENT_ID, BusinessProxy.sSelf.getClientId());
		values.put(MediaPostTable.USER_NAME, SettingData.sSelf.getUserName());
		values.put(MediaPostTable.CONVERSATION_ID,
				getIntent().getStringExtra(Constants.CONVERSATION_ID));

		dist = getParticipantInfo();
		Logger.conversationLog("quick  dist1 : ", "" + dist);
		if (dist == null || dist.trim().length() <= 2
				|| dist.trim().length() <= 2) {
			dist = usersForthisConversition;
			Logger.conversationLog("quick  dist2 : ", "" + dist);
			// //System.out.printlnprintln("Rocketalk--------dist is null : " +
			// dist);
		} else
			usersForthisConversition = dist;
		Logger.conversationLog("quick  dist3 : ", "" + dist);
		Logger.conversationLog("quick  usersForthisConversition : ", ""
				+ usersForthisConversition);
		dist = Utilities.replace(dist, SettingData.sSelf.getUserName() + ";",
				" ");// dist.replaceAll(SettingData.sSelf.getUserName()+";",
		// "") ;
		// //System.out.printlnprintln("---------------------dist : "+dist);
		//
		// String dd [] = Utilities.split(new StringBuffer(dist), ";");
		//
		// //System.out.printlnprintln("---------------------dist : "+dist);
		dist = Utilities.replace(dist, SettingData.sSelf.getUserName()
				.toLowerCase() + ";", " ");
		values.put(MediaPostTable.DIST, dist);

		values.put(MediaPostTable.MSG_TYPE, "1");
		values.put(MediaPostTable.MSG_OP, "7");
		values.put(MediaPostTable.REQ_ID, dist);
		values.put(MediaPostTable.REQ_ID, System.currentTimeMillis());
		values.put(MediaPostTable.REQ_TYPE, "2_7");

		//
		// mpEntity.addPart("msgType", new StringBody("1"));
		// mpEntity.addPart("msgOp", new StringBody("7"));
		// mpEntity.addPart("reqId", new StringBody("8950819552855773454"));
		// mpEntity.addPart("reqType", new StringBody("2_7"));

		// public static final String IMAGE_1 = "IMAGE_1";
		// public static final String IMAGE_1_STATUS = "IMAGE_1_STATUS";

		Message message = new Message();

		if (animTag != null && animTag.length() > 0) {
			message.aniType = "animation";
			message.aniValue = animTag;
			// //System.out.printlnprintln("message.aniType==="+message.aniType+"==message.aniValue=="+message.aniValue);
		}

		message.msgTxt = d;

		message.messageId = "" + id;
		message.mfuId = "-1";
		message.datetime = "" + System.currentTimeMillis();// + (new
		// Date()).toString();
		message.clientTransactionId = "" + (new Date()).toString();
		message.conversationId = getIntent().getStringExtra(
				Constants.CONVERSATION_ID);

		getContentResolver().insert(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, values);
		// Cursor cursor = getContentResolver().query(
		// MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, null, null,
		// null, null);
		//
		// System.out
		// .println("Feed Synch--------------inbox compose cursor count----------"
		// + cursor.getCount());
		// //////////////
		// ((EditText) findViewById(R.community_chat.msgBox)).post(new
		// Runnable() {
		//
		// @Override
		// public void run() {
		// ((EditText) findViewById(R.community_chat.msgBox)).setText("");
		// }
		// });
		((EditText) findViewById(R.community_chat.msgBox)).setText("");
		String textMessage = ((EditText) findViewById(R.community_chat.msgBox))
				.getText().toString();

		if (mVoicePath != null) {

			message.voice_content_urls = mVoicePath;
			message.audio_download_statue = 1;

		}
		if (videoUrls != null && videoUrls.trim().length() > 0) {
			message.video_content_thumb_urls = videoUrls;
			message.video_content_urls = videoUrls;
		}
		if (imageUrls != null && imageUrls.trim().length() > 0) {
			message.image_content_thumb_urls = imageThumbId;
			message.image_content_urls = imageUrls;
		}
		if (animImageUrl != null && animImageUrl.trim().length() > 0) {
			// message.image_content_thumb_urls =animImageUrl;
			// message.image_content_thumb_urls = animImageUrl;
			// message.image_content_urls = animImageUrl;
		}
		message.cltTxnId = cltTxnId;

		saveMessage(message);

		// ((TextView) findViewById(R.conversation.allParticipent))
		// .setText(getParticipantInfo());
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		// checkAndSend();

		// reset
		cameraImagePath = null;
		mVoicePath = null;
		mVideoPath = null;
		mPicturePath = new Vector<String>();
		mPicturePathCanvas = new Vector<String>();
		mPicturePathId = new Vector<Integer>();
		totalSize = 0;

		// Intent i = new Intent(this, C2DMBroadcastReceiver.class);
		// i.setAction("com.kainat.app.android.engine.ComposeService");
		// sendBroadcast(i);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Utilities.startComposeService(ConversationsActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();

		// listViewActivity.setSelection(getCursorCount());
		if (activityAdapter != null) {
			activityAdapter.getCursor().requery();
			activityAdapter.notifyDataSetChanged();
		}
		listViewActivity
				.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listViewActivity.setStackFromBottom(true);

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					listViewActivity
							.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();

		Utilities.manageDB(this,
				getIntent().getStringExtra(Constants.CONVERSATION_ID));
		mVoicePath = null;
		mPicturePath = new Vector<String>();
		mVideoPath = null;
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_TEXT_MESSAGE, "");
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_VOICE_MESSAGE, mVoicePath);
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_PICTURE_MESSAGE,
				mPicturePath);
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_VIDEO_MESSAGE, mVideoPath);
		return "fail";
	}

	public String quickSendForAnim(String animTag, String animImageUrl) {
		String d = ((EditText) findViewById(R.community_chat.msgBox)).getText()
				.toString();

		Utilities.setInt(this, "listpos", 0);
		Utilities.setInt(this, "top", 0);

		// chat_panel_more_option_emo.setVisibility(View.GONE);
		// chat_panel_more_option.setVisibility(View.GONE);
		boolean noContent = true;
		// ///////////
		if (d != null && d.trim().length() > 0)
			noContent = false;
		if (animTag != null && animTag.length() > 0) {
			noContent = false;
		}

		int attachemtnt = 0;
		long attachemtntsize = d.length();
		// String mPicturePathCanvasNotAnim =mPicturePathCanvas;

		/*
		 * if(mPicturePathCanvas!=null && mPicturePathCanvas.size()>0){ for(int
		 * i=0;i<mPicturePathCanvas.size();i++){
		 * mPicturePath.add(mPicturePathCanvas.get(i)); }
		 * mPicturePathCanvas.clear();
		 * ((TextView)findViewById(R.id.image_count_dodle)).setText("");
		 * 
		 * } if (mPicturePath != null && mPicturePath.size() > 0) {
		 * 
		 * attachemtnt = attachemtnt + mPicturePath.size(); noContent = false; }
		 * 
		 * if (mVideoPath != null && mVideoPath.length() > 0) {
		 * 
		 * attachemtnt = attachemtnt + 1; noContent = false; }
		 * 
		 * if (mVoicePath != null) {
		 * 
		 * attachemtnt = attachemtnt + 1; noContent = false; }
		 */

		// chat_panel_more_option.setVisibility(View.GONE);
		// image_count.setVisibility(View.GONE);
		// ((TextView)findViewById(R.id.video_count)).setText("");
		// ((TextView)findViewById(R.id.voice_count)).setText("");
		// ((TextView)findViewById(R.id.video_count)).setVisibility(View.GONE);
		// ((TextView)findViewById(R.id.voice_count)).setVisibility(View.GONE);

		ContentValues values = new ContentValues();
		long id = ((System.currentTimeMillis() - 10000));
		if (animTag != null && animTag.length() > 0) {
			noContent = false;
			values.put(MediaPostTable.COMMAND_TYPE, "" + animTag);
			values.put(MediaPostTable.COMMAND, "2");
		}
		values.put(MediaPostTable.DATE, id);
		values.put(MediaPostTable.ATTACHMENT, attachemtnt);
		// values.put(MediaPostTable.TAG, tag);
		values.put(MediaPostTable.MEDIS_STAUTS, MediaPostTable.STATUS_QUEUE);
		values.put(MediaPostTable.ABOUT, d.trim());
		// values.put(MediaPostTable.MODE, mode);
		values.put(MediaPostTable.TRY, 0);

		// values.put(MediaPostTable.CATEGORY, catId);
		String imageUrls = "";
		String imageThumbId = "";
		String videoUrls = "";
		/*
		 * if (mPicturePath != null && mPicturePath.size() > 0) { if
		 * (mPicturePath != null && mPicturePath.size() > 0) { for (int i = 0; i
		 * < mPicturePath.size(); i++) { attachemtntsize = attachemtntsize +
		 * ImageUtils.getImageSize(mPicturePath.get(i)); values.put("IMAGE_" +
		 * (i + 1), mPicturePath.get(i)); imageUrls = imageUrls +
		 * mPicturePath.get(i) + Constants.COL_SEPRETOR; if (mPicturePathId !=
		 * null && mPicturePathId.size() > 0) imageThumbId = imageThumbId +
		 * mPicturePathId.get(i) + Constants.COL_SEPRETOR; else imageThumbId =
		 * imageThumbId + -1 + Constants.COL_SEPRETOR; } } }
		 */
		/*
		 * if (mVideoPath != null && mVideoPath.length() > 0) { if (mVideoPath
		 * != null && mVideoPath.length() > 0) { // for (int i = 0; i <
		 * mVideoPath.length(); i++) { attachemtntsize = attachemtntsize +
		 * ImageUtils.getImageSize(mVideoPath); values.put(MediaPostTable.VIDEO,
		 * mVideoPath); videoUrls = mVideoPath; } } }
		 */

		/*
		 * if (mVoicePath != null) { values.put(MediaPostTable.AUDIO,
		 * mVoicePath); }
		 */

		if (noContent) {
			// showToast("Nothing to send");
			return null;
		}
		BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf
				.getTransactionId() + 1);
		long cltTxnId = BusinessProxy.sSelf.getTransactionId();// System.currentTimeMillis()
		// ;
		// System.out
		// .println("Rocketalk--------cltTxnId : compose : "+cltTxnId);
		values.put(MediaPostTable.CLTTXNID, cltTxnId);

		values.put(MediaPostTable.ATTACHMENT_SIZE, attachemtntsize);
		// values.put(MediaPostTable.CLTTXNID, BusinessProxy.sSelf.getUserId());
		values.put(MediaPostTable.USER_ID, BusinessProxy.sSelf.getUserId());
		values.put(MediaPostTable.USER_PASS, SettingData.sSelf.getPassword());
		values.put(MediaPostTable.CLIENT_ID, BusinessProxy.sSelf.getClientId());
		values.put(MediaPostTable.USER_NAME, SettingData.sSelf.getUserName());
		values.put(MediaPostTable.CONVERSATION_ID,
				getIntent().getStringExtra(Constants.CONVERSATION_ID));

		dist = getParticipantInfo();

		dist = Utilities.replace(dist, SettingData.sSelf.getUserName() + ";",
				" ");// dist.replaceAll(SettingData.sSelf.getUserName()+";",
		// "") ;
		dist = Utilities.replace(dist, SettingData.sSelf.getUserName()
				.toLowerCase() + ";", " ");
		values.put(MediaPostTable.DIST, dist);

		values.put(MediaPostTable.MSG_TYPE, "1");
		values.put(MediaPostTable.MSG_OP, "7");
		values.put(MediaPostTable.REQ_ID, dist);
		values.put(MediaPostTable.REQ_ID, System.currentTimeMillis());
		values.put(MediaPostTable.REQ_TYPE, "2_7");

		//
		// mpEntity.addPart("msgType", new StringBody("1"));
		// mpEntity.addPart("msgOp", new StringBody("7"));
		// mpEntity.addPart("reqId", new StringBody("8950819552855773454"));
		// mpEntity.addPart("reqType", new StringBody("2_7"));

		// public static final String IMAGE_1 = "IMAGE_1";
		// public static final String IMAGE_1_STATUS = "IMAGE_1_STATUS";

		Message message = new Message();

		if (animTag != null && animTag.length() > 0) {
			message.aniType = "animation";
			message.aniValue = animTag;
			// //System.out.printlnprintln("message.aniType===" +
			// message.aniType
			// + "==message.aniValue==" + message.aniValue);
		}

		message.msgTxt = "";

		message.messageId = "" + id;
		message.mfuId = "-1";
		message.datetime = "" + System.currentTimeMillis();// + (new
		// Date()).toString();
		message.clientTransactionId = "" + (new Date()).toString();
		message.conversationId = getIntent().getStringExtra(
				Constants.CONVERSATION_ID);

		getContentResolver().insert(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, values);
		// Cursor cursor = getContentResolver().query(
		// MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, null, null,
		// null, null);
		//
		// System.out
		// .println("Feed Synch--------------inbox compose cursor count----------"
		// + cursor.getCount());
		// //////////////
		// ((EditText) findViewById(R.community_chat.msgBox)).post(new
		// Runnable() {
		//
		// @Override
		// public void run() {
		// ((EditText) findViewById(R.community_chat.msgBox)).setText("");
		// }
		// });
		// ((EditText) findViewById(R.community_chat.msgBox)).setText("");
		// String textMessage = ((EditText)
		// findViewById(R.community_chat.msgBox))
		// .getText().toString();

		/*
		 * if (mVoicePath != null) {
		 * 
		 * message.voice_content_urls = mVoicePath;
		 * message.audio_download_statue = 1;
		 * 
		 * } if (videoUrls != null && videoUrls.trim().length() > 0) {
		 * message.video_content_thumb_urls = videoUrls;
		 * message.video_content_urls = videoUrls; } if (imageUrls != null &&
		 * imageUrls.trim().length() > 0) { message.image_content_thumb_urls =
		 * imageThumbId; message.image_content_urls = imageUrls; }
		 */
		if (animImageUrl != null && animImageUrl.trim().length() > 0) {
			// message.image_content_thumb_urls =animImageUrl;
			// message.image_content_thumb_urls = animImageUrl;
			// message.image_content_urls = animImageUrl;
		}
		message.cltTxnId = cltTxnId;

		saveMessage(message);
		// ((TextView) findViewById(R.conversation.allParticipent))
		// .setText(getParticipantInfo());
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		// checkAndSend();

		// reset
		// cameraImagePath = null;
		// mVoicePath = null;
		// mVideoPath = null;
		// mPicturePath = new Vector<String>();
		// mPicturePathCanvas =new Vector<String>();
		// mPicturePathId = new Vector<Integer>();
		// totalSize = 0;
		Utilities.startComposeService(this);
		// listViewActivity.setSelection(getCursorCount());
		if (activityAdapter != null) {
			activityAdapter.getCursor().requery();
			activityAdapter.notifyDataSetChanged();
		}
		listViewActivity
				.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		listViewActivity.setStackFromBottom(true);

		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(2000);
		// listViewActivity
		// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// }
		// }).start();

		Utilities.manageDB(this,
				getIntent().getStringExtra(Constants.CONVERSATION_ID));
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
			mpEntity.addPart("reqId", new StringBody("8950819552855773454"));
			mpEntity.addPart("reqType", new StringBody("2_7"));
			// if(dist==null || dist.length()<=0){
			// mpEntity.addPart("dest", new StringBody("qts"));
			// }
			// else{
			dist = getParticipantInfo();

			dist = Utilities.replace(dist, SettingData.sSelf.getUserName()
					+ ";", " ");// dist.replaceAll(SettingData.sSelf.getUserName()+";",
			// "") ;
			dist = Utilities.replace(dist, SettingData.sSelf.getUserName()
					.toLowerCase() + ";", " ");
			mpEntity.addPart("dest", new StringBody(dist));
			// }

			mpEntity.addPart("msgTxt", new StringBody("1"));
			// mpEntity.addPart("cltTxnId", new
			// StringBody(message.clientTransactionId));
			mpEntity.addPart("msgTxt", new StringBody(mediaPost.mediaText));
			mpEntity.addPart("Vndr", new StringBody("TOMO"));

			// mpEntity.addPart("fwdMsgId", new StringBody("1"));
			mpEntity.addPart("convId", new StringBody(getIntent()
					.getStringExtra(Constants.CONVERSATION_ID)));

			if (mediaPost.mVoicePath != null
					&& mediaPost.mVoicePath.contentPath != null) {
				File file = new File(mediaPost.mVoicePath.contentPath);
				String extension = MimeTypeMap
						.getFileExtensionFromUrl(mediaPost.mVoicePath.contentPath);

				mpEntity.addPart("file", new FileBody(file, "audio/"
						+ extension));
			}
			if (mediaPost.mVideoPath != null && mediaPost.mVideoPath.size() > 0) {
				for (int i = 0; i < mediaPost.mVideoPath.size(); i++) {
					MediaPost.MediaContent mc = mediaPost.mVideoPath.get(i);
					File file = new File(mc.contentPath);
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(mc.contentPath);

					mpEntity.addPart("file", new FileBody(file, "image/"
							+ extension));
				}
			}
			if (mediaPost.mImagesPath != null
					&& mediaPost.mImagesPath.size() > 0) {
				for (int i = 0; i < mediaPost.mImagesPath.size(); i++) {
					MediaPost.MediaContent mc = mediaPost.mImagesPath.get(i);
					File file = new File(mc.contentPath);
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(mc.contentPath);

					mpEntity.addPart("file", new FileBody(file, "image/"
							+ extension));
				}
			}
			// String extension =
			// MimeTypeMap.getFileExtensionFromUrl("/mnt/sdcard/ok.jpg");
			// //System.out.printlnprintln("-----------------extension----------"+extension);
			// File file = new File("/mnt/sdcard/ok.jpg");
			// mpEntity.addPart("file", new FileBody(file,
			// "image/jpg"));
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
			// ContentValues mUpdateValues = new ContentValues();
			//
			// mUpdateValues.put(MediaPostTable.MEDIS_STAUTS,
			// MediaPostTable.STATUS_SENT);
			// mUpdateValues.put(MediaPostTable.SENT_DATE,
			// System.currentTimeMillis());
			// mUpdateValues.put(MediaPostTable.ERROR_MSG, "None");
			// int mRowsUpdated = getContentResolver().update(
			// MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
			// mUpdateValues, // the
			// // columns
			// // to
			// // update
			// MediaPostTable.COLUMN_ID + "=" + mediaPost.DB_ID, // the
			// // column
			// // to
			// // select
			// // on
			// null // the value to compare to
			// );
			// checkAndSend();
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
			// //System.out.printlnprintln("-----------PROGRESS_CANCEL_HANDLER_SUB-------------");
			if (request != null && !request.isCancelled()) {
				request.cancel(true);
			}
		}
	};

	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

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
			// //System.out.printlnprintln("----------httpget------"+httpget);
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
			// //System.out.printlnprintln("----------httpget------"+httpget);
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
		String muc_file_id = "";
		String destinationsUserNameArr[] = null;
		boolean p2p_2_group = false;
		String startMessageIdIndex = "";
		String endMessageIdIndex = "";

		public Request(int type, String url) {
			this.type = type;
			this.url = url;
		}

		@Override
		protected void onPreExecute() {
			if (type != 1000)
				// showAnimationDialogNonUIThread("",
				// getString(R.string.sending_request_wait), true,
				// PROGRESS_CANCEL_HANDLER_SUB);
				rTDialog = ProgressDialog.show(ConversationsActivity.this, "",
						getString(R.string.sending_request_wait), true);
		}

		protected void onPostExecute(String result) {

			// System.out.printlnprintln(result);
			DialogInterface.OnClickListener handler = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (lDialog != null && lDialog.isShowing())
						lDialog.dismiss();
					if (rTDialog != null && rTDialog.isShowing())
						rTDialog.dismiss();
					if (lDialogInfo != null && lDialogInfo.isShowing()) {
						lDialogInfo.dismiss();
						flagAnimNotPlay = false;
					}
					if (type == ID_LEAVE_CONVERSATION
							|| type == DELETE_CONVERSATION)
						onBackPressed();// got on message box after delete ofr
				}
			};
			if (rTDialog != null && rTDialog.isShowing())
				rTDialog.dismiss();
			// //System.out.printlnprintln("--------------result : "+result);
			if (result != null
					&& result.trim().equalsIgnoreCase("1")
					&& (this.type == DELETE_CONVERSATION || this.type == ID_LEAVE_CONVERSATION)) {
				dismissAnimationDialog();

				if (this.type == DELETE_CONVERSATION)
					result = getString(R.string.you_have_successfully_deleted_this_conversation);// "You have successfully deleted this conversation";
				if (this.type == ID_LEAVE_CONVERSATION) {
					result = getString(R.string.you_have_successfully_left_this_conversation);
					String sst = null;
					try {
						Cursor cursor = getContentResolver()
								.query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
										null,
										MessageTable.CONVERSATION_ID
												+ " = ? and "
												+ MessageTable.USER_ID + " = ?",
										new String[] {
												getIntent()
														.getStringExtra(
																Constants.CONVERSATION_ID),
												BusinessProxy.sSelf.getUserId()
														+ "" }, null);
						cursor.moveToFirst();

						sst = cursor.getString(cursor
								.getColumnIndex(MessageTable.PARTICIPANT_INFO));
						cursor.close();

						String userNAme = SettingData.sSelf.getUserName();
						String searchs1 = ("false" + Constants.COL_SEPRETOR + userNAme)
								.toLowerCase();
						String searchs2 = ("true" + Constants.COL_SEPRETOR + userNAme)
								.toLowerCase();
						// String sst
						// ="false�amita�Amita�Tandon�false�forgot�For�Got�true�mamtgupta�Mamt�Gupta�false�tariqfzb�Mohammad�Tariq�false�spider�Spi�Der�false�zeenat�Zeenat�Khan�false�adgdgj�adg�dgj�false�aniljha�anil�jha�false�wq���"
						// ;

						String oriS = sst;
						sst = sst.toLowerCase();

						if (sst.indexOf(searchs1) != -1) {
							// //System.out.printlnprintln("----search 1------");
							// //System.out.printlnprintln(sst.indexOf(searchs1));
							String part1 = sst.substring(0,
									sst.indexOf(searchs1));
							// //System.out.printlnprintln(part1);
							String part2 = sst.substring(sst.indexOf(searchs1),
									sst.length());
							// //System.out.printlnprintln(part2);
							String part3 = part2.substring(
									part2.indexOf(Constants.ROW_SEPRETOR) + 1,
									part2.length());
							// //System.out.printlnprintln(part3);

							// //System.out.printlnprintln(sst);
							// //System.out.printlnprintln(part1+part3);
						} else if (sst.indexOf(searchs2) != -1) {
							// //System.out.printlnprintln("----search 2------");
							// //System.out.printlnprintln(sst.indexOf(searchs2));
							String part1 = oriS.substring(0,
									sst.indexOf(searchs2));
							// //System.out.printlnprintln(part1);
							String part2 = oriS.substring(
									sst.indexOf(searchs2), sst.length());
							// //System.out.printlnprintln(part2);
							String part3 = part2.substring(
									part2.indexOf(Constants.ROW_SEPRETOR) + 1,
									part2.length());
							// //System.out.printlnprintln(part3);

							// //System.out.printlnprintln(sst);
							// //System.out.printlnprintln(part1+part3);
							sst = part1 + part3;
						}

					} catch (Exception e) {
						// TODO: handle exception
					}

					// //System.out.printlnprintln("------------sst--- : "+sst);

					ContentValues values = new ContentValues();
					values.put(MessageConversationTable.IS_LEFT, 1);
					if (sst != null)
						values.put(MessageConversationTable.PARTICIPANT_INFO,
								sst);
					int updatedRow = getContentResolver()
							.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
									values,
									MessageConversationTable.CONVERSATION_ID
											+ "= ? and "
											+ MessageConversationTable.USER_ID
											+ " =?",
									new String[] {
											CONVERSATIONID,
											""
													+ BusinessProxy.sSelf
															.getUserId() });
					conversationList.isLeft = 1;
				}

				// showAlertMessage("RockeTalk", result,
				// new int[] { DialogInterface.BUTTON_POSITIVE },
				// new String[] { getString(R.string.ok) },
				// new DialogInterface.OnClickListener[] { handler });

				new AlertDialog.Builder(ConversationsActivity.this)
						.setTitle(getString(R.string.confirm))
						.setMessage(result)
						.setPositiveButton(R.string.ok, handler).show();
				// showToast(result);
				return;
			} else if (this.type == ID_DELETE_MESSAGE) {
				dismissAnimationDialog();
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject != null
							&& jsonObject.has("status")
							&& jsonObject.getString("status").equalsIgnoreCase(
									"1")) {
						result = getString(R.string.you_have_successfully_deleted_this_message);
						// showToast(result);
						// showAlertMessage(
						// getString(R.string.information),
						// result,
						// new int[] { DialogInterface.BUTTON_POSITIVE },
						// new String[] {getString(R.string.ok) },
						// new DialogInterface.OnClickListener[] { handler });
						return;

					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				// //System.out.printlnprintln("result===="+result);
				if (result != null && !result.trim().equalsIgnoreCase("1")) {
					Hashtable<String, String> err = Utilities
							.parseError(result);
					if (err.containsKey("desc"))
						result = err.get("desc");
				}

				// //System.out.printlnprintln("result===="+result);
			}
			if (type != 1000) {
				dismissAnimationDialog();

				if (result != null) {
					if (result != null && result.trim().equalsIgnoreCase("1")) {

						switch (this.type) {
						case ID_DELETE_MESSAGE:
							// result =
							// getString(R.string.you_have_successfully_deleted_this_message);
							// break;
							result = getString(R.string.you_have_successfully_deleted_this_message);
							// showToast(result);
							return;
						case ID_ADD_TO_CONVERSATION:
							if (rTDialog.isShowing())
								rTDialog.dismiss();
							result = getString(R.string.buddy_added_conversation);
							addUserConversation(addParticipentAfterResponse,
									true);
							break;
						case ID_LEAVE_CONVERSATION:
							// result =
							// "You have successfully left this conversation";
							result = getString(R.string.you_have_successfully_left_this_conversation);
							String sst = null;
							try {
								Cursor cursor = getContentResolver()
										.query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
												null,
												MessageTable.CONVERSATION_ID
														+ " = ? and "
														+ MessageTable.USER_ID
														+ " = ?",
												new String[] {
														getIntent()
																.getStringExtra(
																		Constants.CONVERSATION_ID),
														BusinessProxy.sSelf
																.getUserId()
																+ "" }, null);
								cursor.moveToFirst();
								sst = cursor
										.getString(cursor
												.getColumnIndex(MessageTable.PARTICIPANT_INFO));
								cursor.close();
								String userNAme = SettingData.sSelf
										.getUserName();
								String searchs1 = ("false"
										+ Constants.COL_SEPRETOR + userNAme)
										.toLowerCase();
								String searchs2 = ("true"
										+ Constants.COL_SEPRETOR + userNAme)
										.toLowerCase();
								// String sst
								// ="false�amita�Amita�Tandon�false�forgot�For�Got�true�mamtgupta�Mamt�Gupta�false�tariqfzb�Mohammad�Tariq�false�spider�Spi�Der�false�zeenat�Zeenat�Khan�false�adgdgj�adg�dgj�false�aniljha�anil�jha�false�wq���"
								// ;

								String oriS = sst;
								sst = sst.toLowerCase();

								if (sst.indexOf(searchs1) != -1) {
									// //System.out.printlnprintln("----search 1------");
									// //System.out.printlnprintln(sst.indexOf(searchs1));
									String part1 = sst.substring(0,
											sst.indexOf(searchs1));
									// //System.out.printlnprintln(part1);
									String part2 = sst
											.substring(sst.indexOf(searchs1),
													sst.length());
									// //System.out.printlnprintln(part2);
									String part3 = part2
											.substring(
													part2.indexOf(Constants.ROW_SEPRETOR) + 1,
													part2.length());
									// //System.out.printlnprintln(part3);

									// //System.out.printlnprintln(sst);
									// //System.out.printlnprintln(part1+part3);
								} else if (sst.indexOf(searchs2) != -1) {
									// //System.out.printlnprintln("----search 2------");
									// //System.out.printlnprintln(sst.indexOf(searchs2));
									String part1 = oriS.substring(0,
											sst.indexOf(searchs2));
									// //System.out.printlnprintln(part1);
									String part2 = oriS
											.substring(sst.indexOf(searchs2),
													sst.length());
									// //System.out.printlnprintln(part2);
									String part3 = part2
											.substring(
													part2.indexOf(Constants.ROW_SEPRETOR) + 1,
													part2.length());
									// //System.out.printlnprintln(part3);

									// //System.out.printlnprintln(sst);
									// //System.out.printlnprintln(part1+part3);
									sst = part1 + part3;
								}

							} catch (Exception e) {
								// TODO: handle exception
							}

							// //System.out.printlnprintln("------------sst--- : "+sst);

							ContentValues values = new ContentValues();
							values.put(MessageConversationTable.IS_LEFT, 1);
							if (sst != null)
								values.put(
										MessageConversationTable.PARTICIPANT_INFO,
										sst);
							int updatedRow = getContentResolver()
									.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
											values,
											MessageConversationTable.CONVERSATION_ID
													+ "= ? and "
													+ MessageConversationTable.USER_ID
													+ " =?",
											new String[] {
													CONVERSATIONID,
													""
															+ BusinessProxy.sSelf
																	.getUserId() });
							conversationList.isLeft = 1;
							// blockInputPad();

							// lDialogInfo.findViewById(
							// R.conversation_option_panel.update_subject)
							// .setVisibility(View.GONE);
							lDialogInfo.findViewById(
									R.conversation_option_panel.subject)
									.setVisibility(View.GONE);
							lDialogInfo.findViewById(
									R.conversation_option_panel.leave)
									.setVisibility(View.GONE);

							lDialogInfo.findViewById(
									R.conversation_option_panel.add_recipant)
									.setVisibility(View.GONE);
							lDialogInfo
									.findViewById(
											R.conversation_option_panel.delete_conversation)
									.setVisibility(View.GONE);

							break;
						case ID_UPDATE_SUBJECT:
							Log.i(TAG,
									"Request :: onPostExecute :: Response for Subject Update : "
											+ result);
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									title.setText(StringEscapeUtils
											.unescapeXml(subject));
								}
							});
							if (lDialogInfo != null && lDialogInfo.isShowing()) {
								((EditText) lDialogInfo
										.findViewById(R.conversation_option_panel.subject))
										.setText("");
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.title))
										.setText(StringEscapeUtils
												.unescapeXml(subject));
							}
							values = new ContentValues();
							// Update subject and Pic to DB
							values.put(MessageConversationTable.SUBJECT,
									StringEscapeUtils.unescapeXml(subject));
							if (mucFileId != null) {
								values.put(MessageConversationTable.GROUP_PIC,
										mucPicUrl.replace("$MUC_FILE_ID$",
												mucFileId));
								Log.i(TAG,
										"Request :: onPostExecute :: MUC File URL Saving : "
												+ mucPicUrl.replace(
														"$MUC_FILE_ID$",
														mucFileId));
							}
							updatedRow = getContentResolver()
									.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
											values,
											MessageConversationTable.CONVERSATION_ID
													+ "= ? and "
													+ MessageConversationTable.USER_ID
													+ " =?",
											new String[] {
													CONVERSATIONID,
													""
															+ BusinessProxy.sSelf
																	.getUserId() });

							// Nullify the subject and file ID, if first time
							// group is created.
							mucSubject = null;
							mucFileId = null;
							mImagesPath.clear();
							if (lDialogInfo != null && lDialogInfo.isShowing()) {
								lDialogInfo.dismiss();
								flagAnimNotPlay = false;
							}
							if (rTDialog != null)
								rTDialog.dismiss();
							result = getString(R.string.you_successfully_changed_add_subject_this_conversation);
							break;
						case DELETE_CONVERSATION:
							// result =
							// "You have successfully deleted this conversation";
							result = getString(R.string.you_have_successfully_deleted_this_conversation);
							// if
							// (conversations_option_frame_layout.getVisibility()
							// == View.VISIBLE) {
							// conversations_screen_frame_layout.setVisibility(View.VISIBLE);
							// conversations_option_frame_layout.setVisibility(View.GONE);
							//
							// }
							onBackPressed();
							return;

						case ID_SAVE_MESSAGE:
							// result =
							// "The message has been successfully bookmarked";
							// getString(R.string.you_have_successfully_deleted_this_conversation);
							break;
						default:
							break;
						}
						// result = result+ " sucsessfully performed!";
					} else {

					}

					if (result == null
							|| (result != null && result.trim().length() == 0)) {
						// showSimpleAlert("Error", "Please try again!!!");
						showToast(getString(R.string.something_went_wrong));
					} else
						showToast(result);
					// new
					// AlertDialog.Builder(ConversationsActivity.this).setMessage(result)
					// .setPositiveButton(R.string.ok, handler).show();

				} else
					// showSimpleAlert("Error", "Please try after sometime!!!");
					showToast(getString(R.string.something_went_wrong));
			}
		}

		public void checkAndShift(String messageID) {
			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
					MessageTable.TABLE,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "" }, null, null,
					MessageTable.INSERT_TIME + " ASC");

			Message message2 = new Message();
			cursor.moveToLast();
			message2 = CursorToObject.conversationObject(cursor,
					ConversationsActivity.this);

			// //System.out.printlnprintln("---------moved deleteing mid: "+message2.messageId);
			// //System.out.printlnprintln("---------moved deleteing messageID: "+messageID);
			if (message2.messageId.equalsIgnoreCase(messageID)) {
				try {
					cursor.moveToPosition(cursor.getCount() - 2);
					message2 = null;
					message2 = CursorToObject.conversationObject(cursor,
							ConversationsActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
				}
				// message2 = CursorToObject.conversationObject(cursor) ;
				// //System.out.printlnprintln("---------moved deleteing 1 mid: "+message2.messageId);
				// //System.out.printlnprintln("---------moved deleteing 1 messageID: "+messageID);
				ContentValues values2 = new ContentValues();
				if (message2 != null) {
					values2.put(MessageTable.MESSAGE_ID, message2.messageId);
					values2.put(MessageTable.MSG_TXT, message2.msgTxt);
					values2.put(MessageTable.VOICE_CONTENT_URLS,
							message2.voice_content_urls);
					values2.put(MessageTable.AUDIO_ID, "");
					values2.put(MessageTable.MESSAGE_TYPE, message2.messageType);
					values2.put(MessageTable.IMAGE_CONTENT_THUMB_URLS,
							message2.image_content_thumb_urls);
					values2.put(MessageTable.IMAGE_CONTENT_URLS,
							message2.image_content_urls);

					values2.put(MessageTable.VIDEO_CONTENT_THUMB_URLS,
							message2.video_content_thumb_urls);
					values2.put(MessageTable.VIDEO_CONTENT_URLS,
							message2.video_content_urls);

					values2.put(MessageTable.ANI_TYPE, message2.aniType);
					values2.put(MessageTable.ANI_VALUE, message2.aniValue);

				} else {
					values2.put(MessageTable.MESSAGE_ID, "");
					values2.put(MessageTable.MSG_TXT, "");
					// values2.put(MessageTable.VOICE_CONTENT_URLS,message2.voice_content_urls);
					values2.put(MessageTable.AUDIO_ID, "");
					values2.put(MessageTable.MESSAGE_TYPE, "");
					values2.put(MessageTable.IMAGE_CONTENT_THUMB_URLS, "");
					values2.put(MessageTable.IMAGE_CONTENT_URLS, "");

					values2.put(MessageTable.VIDEO_CONTENT_THUMB_URLS, "");
					values2.put(MessageTable.VIDEO_CONTENT_URLS, "");

					values2.put(MessageTable.ANI_TYPE, "");
					values2.put(MessageTable.ANI_VALUE, "");
				}
				// //System.out.printlnprintln("---------moved deleteing text: "+message2.msgTxt);
				// //System.out.printlnprintln("---------moved deleteing mid: "+message2.messageId);
				// //System.out.printlnprintln("---------moved 13: "+message2.msgTxt);
				int updatedRow = getContentResolver()
						.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								values2,
								MessageConversationTable.CONVERSATION_ID
										+ "= ? and "
										+ MessageConversationTable.USER_ID
										+ " =?",
								new String[] { message.conversationId,
										"" + BusinessProxy.sSelf.getUserId() });

				// //System.out.printlnprintln("---------moved : "+updatedRow);
			}
			cursor.close();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				closeOption();
				switch (type) {
				case 1000:
					break;
				case ID_ADD_TO_CONVERSATION:

					URLConnection connection = new URL("http://"
							+ Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/addToConversation")
							.openConnection();
					connection.setDoOutput(true);
					String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationParticipationRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId><invitedUsers>#ACTORUSERID#</invitedUsers></conversationParticipationRequest>";
					content = content.replace("#USERID#", ""
							+ BusinessProxy.sSelf.getUserId());
					String formate = "";
					for (int i = 0; i < destinationsUserNameArr.length; i++) {
						formate = formate
								+ "<invitedUsers>"
								+ BusinessProxy.sSelf
										.parseUsernameInformation(destinationsUserNameArr[i])
								+ "</invitedUsers>";
					}

					content = content.replace("#ACTORUSERID#", formate);
					if (p2p_2_group) {
						// content = content.replace("#CONVERSATIONID#",
						// "null");
					} else {
						content = content.replace(
								"#CONVERSATIONID#",
								getIntent().getStringExtra(
										Constants.CONVERSATION_ID));
					}
					connection.setRequestProperty("Content-Type",
							"application/xml");
					connection.setRequestProperty("password",
							HttpHeaderUtils.encriptPass(

							SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					connection.setRequestProperty("locale", KeyValue.getString(
							ConversationsActivity.this, KeyValue.LANGUAGE));
					OutputStream output = connection.getOutputStream();
					// System.out.printlnprintln("-------content----- : " +
					// content);
					// System.out.printlnprintln("-------url----- : " + url);
					output.write(content.getBytes());
					output.close();
					return Utilities.readStream(connection.getInputStream());

				case ID_LEAVE_CONVERSATION:
					connection = new URL("http://" + Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/leaveConversation")
							.openConnection();
					connection.setDoOutput(true);
					content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationParticipationRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId></conversationParticipationRequest>";
					content = content.replace("#USERID#", ""
							+ BusinessProxy.sSelf.getUserId());
					content = content.replace("#CONVERSATIONID#", getIntent()
							.getStringExtra(Constants.CONVERSATION_ID));
					// System.out.printlnprintln("-------content----- : " +
					// content);
					// System.out.printlnprintln("-------url----- : " + url);
					connection.setRequestProperty("Content-Type",
							"application/xml");
					connection.setRequestProperty("password",
							HttpHeaderUtils.encriptPass(

							SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					connection.setRequestProperty("locale", KeyValue.getString(
							ConversationsActivity.this, KeyValue.LANGUAGE));
					output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					return Utilities.readStream(connection.getInputStream());

				case ID_UPDATE_SUBJECT:
					// subject = URLEncoder.encode(subject);
					if (mucSubject != null)
						subject = mucSubject;
					subject = StringEscapeUtils.escapeXml(subject);
					// XMLUtils.parseStringToXML(xml)
					connection = new URL("http://" + Urls.TEJAS_HOST
							+ "/tejas/rest/rtmessaging/updateSubject")
							.openConnection();
					connection.setDoOutput(true);
					if (mucFileId != null)
						content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationSubjectRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId><subject>#SUBJECT#</subject><imageFileId>#MUCPIC#</imageFileId></conversationSubjectRequest>";
					else
						content = "<?xml version=\"1.0\" encoding=\"utf-8\"?><conversationSubjectRequest><conversationId>#CONVERSATIONID#</conversationId><userId>#USERID#</userId><subject>#SUBJECT#</subject></conversationSubjectRequest>";
					content = content.replace("#USERID#", ""
							+ BusinessProxy.sSelf.getUserId());

					content = content.replace("#SUBJECT#", subject);
					if (mucFileId != null)
						content = content.replace("#MUCPIC#", mucFileId);
					content = content.replace("#CONVERSATIONID#", getIntent()
							.getStringExtra(Constants.CONVERSATION_ID));
					// System.out.printlnprintln("-------content----- : " +
					// content);
					// System.out.printlnprintln("-------url----- : " + url);

					Log.i(TAG, "Request :: doInBackground :: Subject Update : "
							+ content);
					connection.setRequestProperty("Content-Type",
							"application/xml");
					connection.setRequestProperty("Content-Type",
							"application/xml");
					connection.setRequestProperty("password",
							HttpHeaderUtils.encriptPass(

							SettingData.sSelf.getPassword()));
					connection.setRequestProperty("locale", KeyValue.getString(
							ConversationsActivity.this, KeyValue.LANGUAGE));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					return Utilities.readStream(connection.getInputStream());

				case DELETE_CONVERSATION:
					content = "<deleteRangeMessageRequest>"
							+ "<userId>" + BusinessProxy.sSelf.getUserId() + "</userId>"
							+ "<conversationId>" + getIntent().getStringExtra(Constants.CONVERSATION_ID) + "</conversationId>"
//							 +"<startMessageIdIndex>"+startMessageIdIndex+"</startMessageIdIndex>"
							+ "<endMessageIdIndex>" + endMessageIdIndex + "</endMessageIdIndex>"
							+ "</deleteRangeMessageRequest>";

					Log.i(TAG, "DELETE_CONVERSATION ==> "+content);
					String url = "http://" + Urls.TEJAS_HOST + "/tejas/rest/rtmessaging/deleteMessagesInRange";
					connection = new URL(url).openConnection();
					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Type", "application/xml");
					connection.setRequestProperty("password", HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
					connection.setRequestProperty("authkey", "RTAPP@#$%!@");
					connection.setRequestProperty("locale", KeyValue.getString(ConversationsActivity.this, KeyValue.LANGUAGE));
					output = connection.getOutputStream();
					output.write(content.getBytes());
					output.close();
					String s = Utilities.readStream(connection.getInputStream());
					if (s != null && s.trim().equalsIgnoreCase("1")) {
						getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
										MessageConversationTable.CONVERSATION_ID
												+ " = ? ",
										new String[] { getIntent()
												.getStringExtra(Constants.CONVERSATION_ID) });

						getContentResolver().delete(MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.CONVERSATION_ID + " = ? ",
								new String[] { getIntent().getStringExtra(
										Constants.CONVERSATION_ID) });
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
					// System.out.printlnprintln("-------content----- : " +
					// content);
					// System.out.printlnprintln("-------url----- : " +
					// "http://"
					// + Urls.TEJAS_HOST
					// + "/tejas/rest/rtmessaging/bookmarkMessage");
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
					connection.setRequestProperty("locale", KeyValue.getString(
							ConversationsActivity.this, KeyValue.LANGUAGE));
					output = connection.getOutputStream();
					// //System.out.printlnprintln("-------content----- : " +
					// content);
					output.write(content.getBytes());
					output.close();
					s = Utilities.readStream(connection.getInputStream());
					if (s != null && s.trim().equalsIgnoreCase("1")) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								showToast("Message saved !");
							}
						});

					}
					return s;

				case ID_DELETE_MESSAGE:
					if (message.mfuId.equalsIgnoreCase("-1")) {

						checkAndShift(message.messageId);

						getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = ? ",
								new String[] { message.messageId });

						handler.post(new Runnable() {
							@Override
							public void run() {
								if (activityAdapter != null) {
									activityAdapter.getCursor().requery();
									activityAdapter.notifyDataSetChanged();
								}
								listViewActivity.invalidate();
								activityAdapter.notifyDataSetChanged();
							}
						});

						return "1";
					}
					content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
							+ "<deleteMessageRequest>"
							+ " <isDeleteAll>false</isDeleteAll>"
							+ " <userId>" + BusinessProxy.sSelf.getUserId()
							+ "</userId>" + " <messageIds>"
							+ " <messageIds>" + message.messageId
							+ "</messageIds>" + " </messageIds>"
							+ "</deleteMessageRequest>";
					// //System.out.printlnprintln("-------content----- : " +
					// content);
					// //System.out.printlnprintln("-------url----- : " +
					// "http://" + Urls.TEJAS_HOST +
					// "/tejas/rest/rtmessaging/deleteMessages");
					// connection = new URL("http://"+ Urls.TEJAS_HOST +
					// "/tejas/rest/rtmessaging/deleteMessages").openConnection();
					// http://"+Urls.TEJAS_HOST+"/tejas/feeds/message/deletemfubyid?id=
					url = "http://" + Urls.TEJAS_HOST
							+ "/tejas/feeds/message/deletemfubyid?id="
							+ message.mfuId;
					if (message.mfuId.equals("")) {

						checkAndShift(message.messageId);
						getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = ? ",
								new String[] { message.messageId });
						s = "{\"status\":\"1\",\"desc\":\"Message deleted\"}";
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (activityAdapter != null) {
									activityAdapter.getCursor().requery();
									activityAdapter.notifyDataSetChanged();
								}
								listViewActivity.invalidate();
								activityAdapter.notifyDataSetChanged();
							}
						});

						return "Message Deleted";
					}
					// connection = new URL(url).openConnection();
					// Log.i(TAG, "Final Delete URL, with messages - "+url);
					// // connection.setDoOutput(true);
					// //
					// connection.setRequestProperty("Content-Type","application/xml");
					// //
					// connection.setRequestProperty("password",HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
					// // connection.setRequestProperty("locale",
					// KeyValue.getString(ConversationsActivity.this,
					// KeyValue.LANGUAGE));
					// // connection.setRequestProperty("authkey",
					// "RTAPP@#$%!@");
					// connection.setRequestProperty("RT-APP-KEY",HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),
					// SettingData.sSelf.getPassword()));
					// output = connection.getOutputStream();
					// output.write(content.getBytes());
					// output.close();
					// s = Utilities.readStream(connection.getInputStream());

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
					// BufferedReader reader = new BufferedReader(new
					// InputStreamReader(is, "UTF-8") );
					// String data = null;
					// s = "";
					// while ((data = reader.readLine()) != null){
					// s += data;
					// }
					// if(s !=null)
					// s = s.trim();
					Log.i(TAG, "Response Data - " + s);
					JSONObject jmain = new JSONObject(s);
					String Status = jmain.getString("status");
					if (s != null && (s.trim().indexOf("1") != -1)) {
						checkAndShift(message.messageId);
						getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = ? ",
								new String[] { message.messageId });
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (activityAdapter != null) {
									activityAdapter.getCursor().requery();
									activityAdapter.notifyDataSetChanged();
								}
								listViewActivity.invalidate();
								activityAdapter.notifyDataSetChanged();
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
			return err;
		}

	}

	/**
	 * Reads the response from the input stream and returns it as a string.
	 */
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

	String err = "Some error occured on server!";
	boolean voiceIsPlaying;

	private void openPlayScreen(final String url, boolean autoPlay, final LinearLayout ln) {
		availableVoice = false;
		TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
		tv.setText("Please wait while getting audio...");
		voiceIsPlaying = true;
		RTMediaPlayer.setUrl(null);
		tv.setTextColor(getResources().getColor(R.color.sub_heading));
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
				ConversationsActivity.this.handler.post(new Runnable() {

					@Override
					public void run() {
						// closePlayScreen();
						// //System.out.printlnprintln("-----voicePlayCompleted-----");
						try {
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
					if (fullDialog != null && fullDialog.isShowing()) {
						fullDialog.dismiss();
					}
					// //System.out.printlnprintln("onerroe=======i");
					TextView tv = (TextView) ln.findViewById(R.id.streemStatus);
					tv.setTextColor(getResources().getColor(R.color.red));
					voiceIsPlaying = false;
					tv.setText("Unable to play please try later!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onDureationchanged(final long total, final long current) {
				ConversationsActivity.this.handler.post(new Runnable() {

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
									tv.setTextColor(getResources().getColor(
											R.color.sub_heading));
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

	// public void checkAndSend() {
	//
	// Cursor cursor = getContentResolver().query(
	// MediaContentProvider.CONTENT_URI_INBOX_COMPOSE,
	// null,
	// MediaPostTable.MEDIS_STAUTS + " != ?  and "
	// + MediaPostTable.TRY + " < ? and "
	// + MediaPostTable.USER_ID + " =?",
	// new String[] { MediaPostTable.STATUS_SENT, "4",
	// "" + BusinessProxy.sSelf.getUserId() }, null);
	//
	// if (cursor.getCount() > 0) {
	// m = mediaPostObject(cursor);
	// // cursor.moveToFirst();
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// send(m);
	// }
	// }).start();
	//
	// } else {
	// System.out
	// .println("-----------nothng to send stoping post service");
	//
	// }
	// }

	public static MediaPost mediaPostObject(Cursor cursor) {

		// ContentValues values = new ContentValues();
		// values.put(MediaPostTable.DATE, System.currentTimeMillis());
		// values.put(MediaPostTable.TAG, tag);
		// values.put(MediaPostTable.ABOUT,
		// composeScreen_msgBox.getText().toString());
		// values.put(MediaPostTable.MODE, mode);
		// values.put(MediaPostTable.CATEGORY, catId);
		// 03-02 15:45:40.839: I///System.out.println17944):
		// ----------post------ori
		// fburl---{"message":"One or more input parameters are incorrect. Only values allowed for cat range from 49-57 both inclusive","status":"error"}

		cursor.moveToFirst();
		MediaPost mediaPost = new MediaPost();
		mediaPost.DB_ID = cursor.getInt(cursor
				.getColumnIndex(MediaPostTable.COLUMN_ID));
		// //System.out.printlnprintln("---------mediaPost.DB_ID : " +
		// mediaPost.DB_ID);
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

		// //System.out.printlnprintln("--------------featuredUserBean :  "+featuredUserBean.toString());
		return mediaPost;
	}

	PopupWindow mPopupWindow;

	public void closeCopy() {
		if (mPopupWindow != null && mPopupWindow.isShowing())
			mPopupWindow.dismiss();
	}

	public void showCopy(View anchor) {
		// if(1==1)return;

		try {
			message = (Message) anchor.getTag();
			if (mPopupWindow != null && mPopupWindow.isShowing())
				mPopupWindow.dismiss();

			Display display = ((WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE))
					.getDefaultDisplay();
			View mRootView = ((Activity) this).getLayoutInflater().inflate(
					R.layout.copy_panel, null);
			// //System.out.printlnprintln("message.msgTxt==="+message.msgTxt+"message.mfuId=="+message.mfuId);
			if (message.mfuId.equalsIgnoreCase("-1")) {
				if (message.msgTxt != null && message.msgTxt.length() > 0) {
					mRootView.findViewById(R.bubble_long_press.forward)
							.setVisibility(View.GONE);
					// mRootView.findViewById(R.bubble_long_press.share).setVisibility(View.GONE);
					mRootView.findViewById(R.bubble_long_press.copy)
							.setBackgroundResource(
									R.drawable.dialpad_btn_call_normal);
				}
				if (message.msgTxt == null) {
					mRootView.findViewById(R.bubble_long_press.forward)
							.setVisibility(View.GONE);
					// mRootView.findViewById(R.bubble_long_press.share).setVisibility(View.GONE);
					mRootView.findViewById(R.bubble_long_press.copy)
							.setVisibility(View.GONE);
					return;
				} else {

					mRootView.findViewById(R.bubble_long_press.forward)
							.setVisibility(View.GONE);
					// mRootView.findViewById(R.bubble_long_press.share).setVisibility(View.GONE);
					mRootView.findViewById(R.bubble_long_press.copy)
							.setVisibility(View.GONE);
					return;
				}
			} else if (message.msgTxt != null && message.msgTxt.length() == 0
					&& !message.mfuId.equalsIgnoreCase("-1")) {
				// //System.out.printlnprintln("message.msgTxt==="+message.msgTxt);
				mRootView.findViewById(R.bubble_long_press.forward)
						.setVisibility(View.VISIBLE);
				// mRootView.findViewById(R.bubble_long_press.share).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.bubble_long_press.forward)
						.setBackgroundResource(
								R.drawable.dialpad_btn_call_normal);
				mRootView.findViewById(R.bubble_long_press.copy).setVisibility(
						View.GONE);// .setBackgroundResource(R.drawable.dialpad_btn_call_normal);
			}
			if ((message.aniType != null
					&& message.aniType.equalsIgnoreCase("animation") && message.aniValue != null)) {
				mRootView.findViewById(R.bubble_long_press.forward)
						.setVisibility(View.GONE);
				// mRootView.findViewById(R.bubble_long_press.share).setVisibility(View.GONE);
				mRootView.findViewById(R.bubble_long_press.copy).setVisibility(
						View.GONE);
				;// .
			}
			if (((message.drmFlags & InboxTblObject.DRM_DONOTFORWARD) > 0)) {
				mRootView.findViewById(R.bubble_long_press.forward)
						.setVisibility(View.GONE);
				// mRootView.findViewById(R.bubble_long_press.share).setVisibility(View.GONE);
				mRootView.findViewById(R.bubble_long_press.copy).setVisibility(
						View.GONE);
				;// .setBackgroundResource(R.drawable.dialpad_btn_call_normal);
				showToast("Access denied!");
			}
			mRootView.findViewById(R.bubble_long_press.copy)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mPopupWindow != null
									&& mPopupWindow.isShowing())
								mPopupWindow.dismiss();
							if (message.msgTxt != null
									&& message.msgTxt.trim().length() > 0) {
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
			mRootView.findViewById(R.bubble_long_press.forward)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mPopupWindow != null
									&& mPopupWindow.isShowing())
								mPopupWindow.dismiss();

							if (message.mfuId.equalsIgnoreCase("-1")) {
								showToast("You can not forward this message!");
								return;
							}
							DataModel.sSelf.storeObject(
									DMKeys.COMPOSE_MESSAGE_OP,
									Constants.MSG_OP_FWD);
							DataModel.sSelf.storeObject(
									DMKeys.COMPOSE_REFERENCE_MESSAGE_ID,
									message.messageId);
							if (message.msgTxt != null
									&& message.msgTxt.trim().length() > 0) {

							}
							showToast(getString(R.string.comeing_soon));
							// Intent intent = new
							// Intent(ConversationsActivity.this,
							// ComposeActivity.class);
							// intent.putExtra(Constants.ACTIVITY_FOR_RESULT,
							// true);
							// startActivityForResult(intent,
							// Constants.ResultCode);

						}
					});
			// mRootView.findViewById(R.bubble_long_press.share).setOnClickListener(
			// new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if (mPopupWindow != null && mPopupWindow.isShowing())
			// mPopupWindow.dismiss();
			// if(message.mfuId.equalsIgnoreCase("-1")){
			// showToast("You can not share this message!");
			// return;
			// }
			// openShareOption();
			// }
			// });

			mPopupWindow = new PopupWindow(mRootView,
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);

			/*
			 * mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
			 * 
			 * @Override public boolean onTouch(View v, MotionEvent event) { //
			 * TODO Auto-generated method stub if (event.getAction() ==
			 * MotionEvent.ACTION_DOWN) {
			 * ////System.out.printlnprintln("close action");
			 * mPopupWindow.dismiss(); } return true; } });
			 */
			mPopupWindow.setAnimationStyle(R.style.Animations_PopUpMenu);

			// mPopupWindow.setWidth(display.getWidth());
			// Utilities.startAnimition(this, mRootView, R.anim.rail);
			int rootHeight = 0;
			int[] location = new int[2];
			int xPos, yPos, arrowPos;
			anchor.getLocationOnScreen(location);
			mWindowManager = (WindowManager) this
					.getSystemService(Context.WINDOW_SERVICE);
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

				arrowPos = anchorRect.centerX() - xPos;

			} else {
				if (anchor.getWidth() > rootWidth) {
					xPos = anchorRect.centerX() - (rootWidth / 2);
				} else {
					xPos = anchorRect.left;
				}

				arrowPos = anchorRect.centerX() - xPos;
			}

			int dyTop = anchorRect.top;
			int dyBottom = screenHeight - anchorRect.bottom;

			boolean onTop = (dyTop > dyBottom) ? true : false;

			if (onTop) {
				if (rootHeight > dyTop) {
					yPos = 15;
					// LayoutParams l = mScroller.getLayoutParams();
					// l.height = dyTop - anchor.getHeight();
				} else {
					yPos = anchorRect.top - rootHeight;
				}
			} else {
				yPos = anchorRect.bottom;

				if (rootHeight > dyBottom) {
					// LayoutParams l = mScroller.getLayoutParams();
					// l.height = dyBottom;
				}
			}
			mPopupWindow
					.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
							: R.style.Animations_PopDownMenu_Center);
			// System.out.printlnprintln("anchorRect.top : "+anchorRect.top);
			mPopupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY,
					(screenWidth / 2) - (rootWidth / 2),
					anchorRect.top <= 0 ? 150 : anchorRect.top);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void closeOption() {
		if (lDialog != null && lDialog.isShowing())
			lDialog.dismiss();
	}

	Dialog lDialog;
	Dialog lDialogInfo;

	public void showOption(String[] parti) {
		flagAnimNotPlay = true;
		lDialogInfo = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar) {
			public void onBackPressed() {
				if (lDialogInfo != null && lDialogInfo.isShowing()) {
					lDialogInfo.dismiss();
					flagAnimNotPlay = false;
				}
			};
		};
		lDialogInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
		lDialogInfo.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		if (conversationList != null && conversationList.isGroup == 1) {
			lDialogInfo.setContentView(R.layout.conversation_muc_option_panel);
		} else
		{
			lDialogInfo.setContentView(R.layout.conversation_option_panel);
			((TextView) lDialogInfo.findViewById(R.id.update_subject)).setVisibility(View.GONE);
		}
		myScreenName(Constants.SCRTEEN_NAME_THREAD_CHAT_INFO);
		// @+conversation_option_panel/

		lDialogInfo.findViewById(R.id.prev).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (lDialogInfo != null && lDialogInfo.isShowing()) {
							lDialogInfo.dismiss();
							flagAnimNotPlay = false;
						}
					}
				});
		if (conversationList.isGroup == 1) {
			mucFullPic = (ImageView) lDialogInfo
					.findViewById(R.conversation_option_panel.other_image);
			if (mucUrlFromInbox != null) {
				if (imageManager == null)
					imageManager = new ImageDownloader();
				imageManager.download(mucUrlFromInbox, mucFullPic,
						ConversationsActivity.this,
						ImageDownloader.TYPE_THUMB_COMMUNITY);
			}
			// if(conversationList.imageFileId != null)
			// mucFullPic.setImageURI(Uri.parse(conversationList.imageFileId));
			((TextView) lDialogInfo
					.findViewById(R.conversation_option_panel.subject))
					.setText(title.getText().toString());
			lDialogInfo.findViewById(R.id.attach_photo_id).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							onCreateDialogPricture().show();
						}
					});
			mucFullPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					if ((mucUrlFromInbox != null && mucUrlFromInbox
							.startsWith("http://"))) {
						intent.setDataAndType(Uri.parse(mucUrlFromInbox),
								"image/*");
					} else if (mucGroupPic != null
							&& !mucGroupPic.trim().equals("")) {
						intent.setDataAndType(
								Uri.parse("file://" + mucGroupPic), "image/*");
					}
					startActivity(intent);
				}
			});
		}

		((TextView) lDialogInfo.findViewById(R.conversation_option_panel.title))
				.setText(title.getText().toString());

		if (lDialogInfo != null) {
			lDialogInfo.findViewById(R.conversation_option_panel.leave)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							DialogInterface.OnClickListener deleteHandler = new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									request = new Request(
											ID_LEAVE_CONVERSATION, "url");
									if (!ConversationsActivity.this
											.checkInternetConnection()) {
										ConversationsActivity.this
												.networkLossAlert();
										return;
									}
									request.execute("LikeDislike");
								}
							};

							// showAlertMessage(
							// getString(R.string.confirm),
							// getString(R.string.leave_group_confiramation_message),
							// new int[] {
							// DialogInterface.BUTTON_POSITIVE,
							// DialogInterface.BUTTON_NEGATIVE },
							// new String[] { getString(R.string.yes),
							// getString(R.string.no) },
							// new DialogInterface.OnClickListener[] {
							// deleteHandler, null });

							new AlertDialog.Builder(ConversationsActivity.this)
									.setTitle(getString(R.string.confirm))
									.setMessage(
											getString(R.string.leave_group_confiramation_message))
									.setPositiveButton(R.string.yes,
											deleteHandler)
									.setNegativeButton(R.string.no, null)
									.show();

						}
					});
			lDialogInfo.findViewById(
					R.conversation_option_panel.delete_conversation)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								String lastMsgId = "0";
								String lastMfuId = "0";
								String[] columns = new String[] {
										MessageTable.MESSAGE_ID,
										MessageTable.MFU_ID };
								Cursor cursor = getContentResolver()
										.query(MediaContentProvider.CONTENT_URI_INBOX,
												columns,
												MessageTable.CONVERSATION_ID
														+ "= ? and "
														+ MessageTable.USER_ID
														+ " =? and "
														+ MessageTable.MFU_ID
														+ " != ?",
												new String[] {
														getIntent()
																.getStringExtra(
																		Constants.CONVERSATION_ID),
														""
																+ BusinessProxy.sSelf
																		.getUserId(),
														"-1" },
												MessageTable.INSERT_TIME
														+ " ASC");

								if (cursor.getCount() > 0) {
									cursor.moveToLast();
									int dataColumnIndex = cursor
											.getColumnIndex(MessageTable.MESSAGE_ID);
									lastMsgId = cursor
											.getString(dataColumnIndex);
									lastMfuId = cursor.getString(cursor
											.getColumnIndex(MessageTable.MESSAGE_ID));
								}
								// request = new Request(DELETE_CONVERSATION,
								// url); String url =
								// "http://"+UIActivityManager.TEJAS_HOST+"/tejas/rest/rtmessaging/deleteMessagesInRange"
								// ;
								request = new Request(DELETE_CONVERSATION,
										"url");
								// //System.out.printlnprintln("-----------max lastMsgId : "+lastMsgId);
								// //System.out.printlnprintln("-----------max lastMfuId : "+lastMfuId);
								request.startMessageIdIndex = lastMsgId;
								// showToast("max : " + lastMsgId);
								int totMsg = cursor.getCount();
								if (cursor.getCount() > 0) {
									cursor.moveToFirst();
									int dataColumnIndex = cursor
											.getColumnIndex(MessageTable.MESSAGE_ID);
									lastMsgId = cursor
											.getString(dataColumnIndex);
									lastMfuId = cursor.getString(cursor
											.getColumnIndex(MessageTable.MESSAGE_ID));
								}
								cursor.close();
								request.endMessageIdIndex = lastMsgId;
								// //System.out.printlnprintln("-----------min lastMsgId : "+lastMsgId);
								// //System.out.printlnprintln("-----------min lastMfuId : "+lastMfuId);

								// showToast("min : "+lastMsgId);
								DialogInterface.OnClickListener deleteHandlerNegative = new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
										// deleteMode = false;
										listViewActivity.invalidateViews();
										// ((Button)view).setText(" Delete ");
										// setUnCheckAll();
									}
								};
								DialogInterface.OnClickListener deleteHandler = new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {

										if (getIntent().getStringExtra(
												Constants.CONVERSATION_ID)
												.startsWith("NP")
												|| (request.startMessageIdIndex
														.equalsIgnoreCase("0") && request.endMessageIdIndex
														.equalsIgnoreCase("0"))) {

											getContentResolver()
													.delete(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
															MessageConversationTable.CONVERSATION_ID
																	+ " = ? ",
															new String[] { getIntent()
																	.getStringExtra(
																			Constants.CONVERSATION_ID) });
											// if
											// (conversations_option_frame_layout.getVisibility()
											// == View.VISIBLE) {
											// conversations_screen_frame_layout.setVisibility(View.VISIBLE);
											// conversations_option_frame_layout.setVisibility(View.GONE);
											//

											// }
											activityAdapter.getCursor()
													.requery();
											activityAdapter
													.notifyDataSetChanged();
											listViewActivity.invalidate();
											onBackPressed();
										} else {
											// String url =
											// "http://"+UIActivityManager.TEJAS_HOST+"/tejas/rest/rtmessaging/deleteMessagesInRange"
											// ;
											// request = new
											// Request(DELETE_CONVERSATION,
											// "url");
											// url = url.replace("#USERID#",
											// ""+BusinessProxy.sSelf.getUserId())
											// ;
											// url =
											// url.replace("#CONVERSATIONID#",
											// ""+conversationList.conversationId);

											if (request.startMessageIdIndex != null
													&& request.startMessageIdIndex
															.length() > 0
													&& request.endMessageIdIndex != null
													&& request.endMessageIdIndex
															.length() > 0) {

												if (!ConversationsActivity.this
														.checkInternetConnection()) {
													ConversationsActivity.this
															.networkLossAlert();
													return;
												}
												request.execute("LikeDislike");
											}
											//
											// DialogInterface.OnClickListener
											// deleteHandler = new
											// DialogInterface.OnClickListener()
											// {
											//
											// public void
											// onClick(DialogInterface dialog,
											// int which) {
											// // deleteMode = false;
											// request.execute("");
											//
											// }
											// };
											DialogInterface.OnClickListener deleteHandlerNegative = new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface dialog,
														int which) {
													// deleteMode = false;
													listViewActivity
															.invalidateViews();
													// ((Button)view).setText(" Delete ");
													// setUnCheckAll();
												}
											};

											// if(request.startMessageIdIndex!=null
											// &&
											// request.startMessageIdIndex.length()>0
											// &&
											// request.endMessageIdIndex!=null
											// &&
											// request.endMessageIdIndex.length()>0)
											// showAlertMessage("Confirm",
											// "Do you really want to delete ("+totMsg+") messages from conversation?",
											// new int[] {
											// DialogInterface.BUTTON_POSITIVE,
											// DialogInterface.BUTTON_NEGATIVE
											// }, new String[] { "Yes",
											// "No" }, new
											// DialogInterface.OnClickListener[]
											// { deleteHandler,
											// deleteHandlerNegative });

										}
									}
								};
								if (request.startMessageIdIndex != null
										&& request.startMessageIdIndex.length() > 0
										&& request.endMessageIdIndex != null
										&& request.endMessageIdIndex.length() > 0)
									if (totMsg > 0) {
										// showAlertMessage(
										// getString(R.string.confirm),
										// getString(R.string.conversation_delete),
										// new int[] {
										// DialogInterface.BUTTON_POSITIVE,
										// DialogInterface.BUTTON_NEGATIVE },
										// new String[] {
										// getString(R.string.yes),
										// getString(R.string.no)},
										// new DialogInterface.OnClickListener[]
										// {
										// deleteHandler,
										// deleteHandlerNegative });

										new AlertDialog.Builder(
												ConversationsActivity.this)
												.setTitle(
														getString(R.string.confirm))
												.setMessage(
														getString(R.string.conversation_delete))
												.setPositiveButton(
														R.string.yes,
														deleteHandler)
												.setNegativeButton(R.string.no,
														null).show();

									} else {
										// showAlertMessage(
										// "Confirm",
										// getString(R.string.conversation_delete),
										// new int[] {
										// DialogInterface.BUTTON_POSITIVE,
										// DialogInterface.BUTTON_NEGATIVE },
										// new String[] {
										// getString(R.string.yes),
										// getString(R.string.no)},
										// new DialogInterface.OnClickListener[]
										// {
										// deleteHandler,
										// deleteHandlerNegative });

										new AlertDialog.Builder(
												ConversationsActivity.this)
												.setTitle(
														getString(R.string.confirm))
												.setMessage(
														getString(R.string.conversation_delete))
												.setPositiveButton(
														R.string.yes,
														deleteHandler)
												.setNegativeButton(R.string.no,
														null).show();
									}

								// showAlertMessage(
								// "Confirm",
								// "Do you really want to delete this conversation?",
								// new int[] { DialogInterface.BUTTON_POSITIVE,
								// DialogInterface.BUTTON_NEGATIVE },
								// new String[] { "Yes", "No" },
								// new DialogInterface.OnClickListener[] {
								// deleteHandler, null });
							} catch (Exception e) {
								// TODO: handle exception
							}
						}

					});
			lDialogInfo.findViewById(R.conversation_option_panel.block)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							DialogInterface.OnClickListener blockHandlerBlock = null;
							final String blockUser = actionUser;
							blockHandlerBlock = new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									StringBuilder text = new StringBuilder(
											"BlockUser::User##");
									text.append(blockUser);
									if (BusinessProxy.sSelf.sendNewTextMessage(
											"User Manager<a:usermgr>",
											text.toString(), true)) {
										showSimpleAlertOutUIRun(
												"Info",
												String.format(
														"%s has been blocked.\nTo unblock friend, view the 'Blocked users' list from the options menu and select 'Unblock'!",
														blockUser));
									}
								}
							};
							showAlertMessage("Confirm", "Do you want to block "
									+ blockUser + "?", new int[] {
									DialogInterface.BUTTON_POSITIVE,
									DialogInterface.BUTTON_NEGATIVE },
									new String[] { "Yes", "No" },
									new DialogInterface.OnClickListener[] {
											blockHandlerBlock, null });
						}

					});
			lDialogInfo.findViewById(R.conversation_option_panel.delete_user)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							// TODO Auto-generated method stub
							DialogInterface.OnClickListener blockHandlerBlock = null;
							final String blockUser = actionUser;
							blockHandlerBlock = new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									StringBuilder text = new StringBuilder(
											"DeleteFriend::User##");
									text.append(blockUser);
									if (BusinessProxy.sSelf.sendNewTextMessage(
											"Friend Manager<a:friendmgr>",
											text.toString(), true)) {
										showSimpleAlertOutUIRun(
												"Info",
												String.format(
														"%s has been deleted.\n To add this friend again, you can use the 'Friend Invite' option!",
														blockUser));
									}
								}
							};
							showAlertMessage("Confirm",
									"Do you want to delete " + blockUser + "?",
									new int[] {
											DialogInterface.BUTTON_POSITIVE,
											DialogInterface.BUTTON_NEGATIVE },
									new String[] { "Yes", "No" },
									new DialogInterface.OnClickListener[] {
											blockHandlerBlock, null });
						}

					});
			lDialogInfo.findViewById(R.conversation_option_panel.add_recipant)
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (conversationList.participantInfo.size() > 9) {
								showSimpleAlert("Error",
										"You can not add more than 10 friend!");
							} else {
//								Intent intent = new Intent(
//										ConversationsActivity.this,
//										KainatContactActivity.class);
//								intent.putExtra("HEADER_TITLE",
//										getString(R.string.friend));
//								intent.putExtra(BuddyActivity.SCREEN_MODE,
//										BuddyActivity.MODE_ONLY_FRIENDS);// BuddyActivity
//								intent.putExtra("group", true);
//								intent.putExtra("MULTISELECTED", true);
//								intent.putExtra("ADDCONVERSATIONS", true);
//								intent.putExtra("ADDPARTICIPANT",
//										"ADDPARTICIPENT");
//								startActivityForResult(intent, 1);
							}

						}
					});

			((EditText) lDialogInfo
					.findViewById(R.conversation_option_panel.subject))
					.setOnEditorActionListener(new OnEditorActionListener() {
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE
									|| event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
								String value = ((EditText) lDialogInfo
										.findViewById(R.conversation_option_panel.subject))
										.getText().toString();
								if (value == null || value.trim().length() <= 0) {
									showToast(getString(R.string.enter_subject_text));
									return false;
								}
								if (!SUBJECT_PATTERN.matcher(value).matches()) {
									showToast("Charcter Not allowed");
									return false;
								}
								request = new Request(ID_UPDATE_SUBJECT, "url");
								request.subject = value;
								if (!ConversationsActivity.this
										.checkInternetConnection()) {
									ConversationsActivity.this
											.networkLossAlert();
									return false;
								}
								hideSoftKeyPad(((EditText) lDialogInfo
										.findViewById(R.conversation_option_panel.subject)));
								request.execute("LikeDislike");
								return true;
							}
							return false;
						}
					});
			((TextView) lDialogInfo.findViewById(R.id.update_subject))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mucSubject = ((EditText) lDialogInfo
									.findViewById(R.conversation_option_panel.subject))
									.getText().toString();

							if (mucSubject == null
									|| mucSubject.trim().length() <= 0) {
								showToast(getString(R.string.enter_subject_text));
								return;
							}
							if (mucGroupPic != null) {
								new UploadPicTask().execute("");
							} else {
								request = new Request(ID_UPDATE_SUBJECT, "url");
								request.subject = mucSubject;
								if (mucFileId != null)
									request.muc_file_id = mucFileId;
								if (!ConversationsActivity.this
										.checkInternetConnection()) {
									ConversationsActivity.this
											.networkLossAlert();
									return;
								}
								request.execute("LikeDislike");
							}
						}
					});
			/*
			 * findViewById(R.conversation_option_panel.option).setOnClickListener
			 * ( new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) {
			 * openConversationOption(1); } });
			 */

			/*
			 * ((ToggleButton)
			 * findViewById(R.conversation_option_panel.notification))
			 * .setOnCheckedChangeListener(new OnCheckedChangeListener() {
			 * 
			 * @Override public void onCheckedChanged(CompoundButton buttonView,
			 * boolean isChecked) {
			 * 
			 * if (isChecked) {
			 * 
			 * } else {
			 * 
			 * }
			 * 
			 * } });
			 */
		}

		p1_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p1_image);
		p2_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p2_image);
		p3_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p3_image);
		p4_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p4_image);
		p5_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p5_image);
		p6_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p6_image);
		p7_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p7_image);
		p8_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p8_image);
		p9_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p9_image);
		p10_image = (CImageView) lDialogInfo
				.findViewById(R.conversation_option_panel.p10_image);

		p1_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p1_username);
		p2_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p2_username);
		p3_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p3_username);
		p4_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p4_username);
		p5_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p5_username);
		p6_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p6_username);
		p7_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p7_username);
		p8_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p8_username);
		p9_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p9_username);
		p10_username = (TextView) lDialogInfo
				.findViewById(R.conversation_option_panel.p10_username);

		if (conversationList != null && conversationList.isGroup == 0)
			fillP2PNew();
		else if (conversationList != null && conversationList.isGroup == 1)
			fillGroupNew();
		if (allLeft) {
			lDialogInfo.findViewById(R.conversation_option_panel.leave)
					.setVisibility(View.GONE);
		}
		if (conversationList != null && conversationList.isLeft == 1) {
			// //System.out.printlnprintln("----------------going to hide");
			// lDialogInfo
			// .findViewById(R.conversation_option_panel.update_subject)
			// .setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.subject)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.leave)
					.setVisibility(View.GONE);

			lDialogInfo.findViewById(R.conversation_option_panel.add_recipant)
					.setVisibility(View.GONE);
			// lDialogInfo.findViewById(R.conversation_option_panel.delete_conversation).setVisibility(
			// View.GONE);
		}
		// //System.out.printlnprintln("----------------going to show");
		lDialogInfo.show();
		/*
		 * lDialog.findViewById(R.conversation_option_panel.subject).requestFocus
		 * ();
		 * lDialog.findViewById(R.conversation_option_panel.subject).postDelayed
		 * (new Runnable() {
		 * 
		 * @Override public void run() { InputMethodManager keyboard =
		 * (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 * keyboard
		 * .showSoftInput(lDialog.findViewById(R.conversation_option_panel
		 * .subject), 0); } },200);
		 */
		/*
		 * final EditText ed =(EditText)
		 * lDialog.findViewById(R.conversation_option_panel.subject);
		 * ed.setFocusable(false); ed.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub ed.setFocusable(true); } });
		 */

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromInputMethod(((EditText)
		// lDialog.findViewById(R.conversation_option_panel.subject)),
		// InputMethodManager.SHOW_IMPLICIT);
		imm.hideSoftInputFromInputMethod(((EditText) lDialogInfo
				.findViewById(R.conversation_option_panel.subject))
				.getWindowToken(), 0);
		lDialogInfo.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
	}

	ParticipantInfo ownerParticipantInfo = null;
	ParticipantInfo otherParticipantInfo = null;

	public void openRowOption(int type, final View v) {// 1 right 2 left
		HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				((EditText) findViewById(R.community_chat.msgBox))
						.getWindowToken(), 0);
		HashMap<Integer, Integer> drawableIcon = new HashMap<Integer, Integer>();
		// for(int iTemp=0;iTemp<draw.length;iTemp++){
		// Drawable
		// draw[]={getResources().getDrawable(R.drawable.notification_slide_count),getResources().getDrawable(R.drawable.notification_slide_count),null,null,null,null};
		// }
		// ActionItem action = new ActionItem(0], null, draw[i],0);
		if (clickLeftRightCheck) {
			/*
			 * if(type != 1) {
			 */
			drawableIcon.put(0, -1);
			// }
			drawableIcon.put(1, -1);
			drawableIcon.put(2, -1);
			// if(type != 1)
			// {
			menuItems.put(0, getString(R.string.delete_message));
			// }
			menuItems.put(1, getString(R.string.copy));
			menuItems.put(2, getString(R.string.cancel));
		} else {
			// Drawable
			// draw={getResources().getDrawable(R.drawable.notification_slide_count),getResources().getDrawable(R.drawable.notification_slide_count),null,null,null,null};
			drawableIcon.put(0, R.drawable.bookmarkmenu);
			drawableIcon.put(1, -1);
			drawableIcon.put(2, R.drawable.reportmessagemenu);
			drawableIcon.put(3, R.drawable.reportpersonmenu);
			drawableIcon.put(4, R.drawable.addasafriendmenu);
			drawableIcon.put(5, R.drawable.canclemenu);
			menuItems.put(0, "Bookmark message");
			menuItems.put(1, "Delete message");
			menuItems.put(2, "Report message");
			menuItems.put(3, "Report person");
			menuItems.put(4, "Add as friend");
			menuItems.put(5, "Cancel");
		}
		// if(message.aniType!=null && message.aniValue!=null &&
		// message.aniType.equalsIgnoreCase("animation")){
		// menuItems.remove(0) ;
		// }
		if ((message.drmFlags & InboxTblObject.DRM_DONOTSAVECONTENTS) > 0) {
			// menuItems.remove(0);
			// drawableIcon.remove(0);
		}
		if (message.participantInfoClazz != null
				&& BusinessProxy.sSelf
						.isInBuddyList(message.participantInfoClazz.userName)) {
			// //System.out.printlnprintln("---------removing friend list option");
			menuItems.remove(4);
			drawableIcon.remove(4);
		}
		if (message.mfuId.equalsIgnoreCase("-1") || message.aniType != null
				&& message.aniValue != null
				&& message.aniType.equalsIgnoreCase("animation")) {
			// //System.out.printlnprintln("---------removing friend list option");
			// menuItems.remove(0);
			// drawableIcon.remove(0);
		}
		// menuItems.remove(1);
		// drawableIcon.remove(1);
		// menuItems.d
		openActionSheet(menuItems, new OnMenuItemSelectedListener() {
			@Override
			public void MenuItemSelectedEvent(Integer selection) {
				switch (selection) {
				case 1:

					// DialogInterface.OnClickListener deleteHandlerbookmark =
					// new DialogInterface.OnClickListener() {
					//
					// public void onClick(DialogInterface dialog, int which) {
					// request = new Request(ID_SAVE_MESSAGE, "url");
					// // request.subject = value;
					// if (!ConversationsActivity.this
					// .checkInternetConnection()) {
					// ConversationsActivity.this.networkLossAlert();
					// return;
					// }
					// request.execute("LikeDislike");
					// }
					// };
					//
					// showAlertMessage("Confirm",
					// "Do you really want to bookmark this message?",
					// new int[] { DialogInterface.BUTTON_POSITIVE,
					// DialogInterface.BUTTON_NEGATIVE },
					// new String[] { "Yes", "No" },
					// new DialogInterface.OnClickListener[] {
					// deleteHandlerbookmark, null });
					if (message.msgTxt != null
							&& message.msgTxt.trim().length() > 0) {
						ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(message.msgTxt);
						showToast(getString(R.string.text_copied));
					} else
						showToast(getString(R.string.nothing_to_copy));
					break;
				case 0:
					// request = new Request(ID_DELETE_MESSAGE, "url");
					// request.execute("LikeDislike");

					// DialogInterface.OnClickListener deleteHandler = new
					// DialogInterface.OnClickListener() {
					//
					// public void onClick(DialogInterface dialog, int which) {
					// if (!ConversationsActivity.this
					// .checkInternetConnection()) {
					// ConversationsActivity.this.networkLossAlert();
					// return;
					// }
					// request = new Request(ID_DELETE_MESSAGE, "url");
					// request.execute("LikeDislike");
					// }
					// };

					// showAlertMessage(getString(R.string.confirm),
					// getString(R.string.delete_confirmation),
					// new int[] { DialogInterface.BUTTON_POSITIVE,
					// DialogInterface.BUTTON_NEGATIVE },
					// new String[] { getString(R.string.yes),
					// getString(R.string.no) },
					// new DialogInterface.OnClickListener[] {
					// deleteHandler, null });

					new AlertDialog.Builder(ConversationsActivity.this)
							.setMessage(getString(R.string.delete_confirmation))
							.setPositiveButton(R.string.yes,
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											if (!ConversationsActivity.this
													.checkInternetConnection()) {
												ConversationsActivity.this
														.networkLossAlert();
												return;
											}
											request = new Request(
													ID_DELETE_MESSAGE, "url");
											request.execute("LikeDislike");
										}
									}).setNegativeButton(R.string.no, null)
							.show();

					break;
				case 2:// report message
					if (!clickLeftRightCheck)
						reportMessage(message);
					break;
				case 3:

					reportUser(message);

					break;
				case 4:// send friend request
					sendInvite(message);
					break;
				}

			}
		}, drawableIcon);
	}

	String displayName = null;

	public void reportUser(Message message) {
		if (!ConversationsActivity.this.checkInternetConnection()) {
			ConversationsActivity.this.networkLossAlert();
			return;
		}
		boolean isSender = false;
		if (message.participantInfo != null
				&& conversationList.participantInfo.size() > 0) {
			for (int i = 0; i < message.participantInfo.size(); i++) {
				ParticipantInfo participantInfo = message.participantInfo
						.get(i);
				// if
				// (participantInfo.userName.equalsIgnoreCase(SettingData.sSelf
				// .getUserName()))
				// ownerParticipantInfo = participantInfo;
				// else
				// otherParticipantInfo = participantInfo;
				if (participantInfo.isSender) {
					otherParticipantInfo = participantInfo;
					break;
				}

			}
		}
		displayName = otherParticipantInfo.userName;
		if ((otherParticipantInfo.lastName != null && otherParticipantInfo.lastName
				.length() > 0))
			displayName = otherParticipantInfo.lastName;

		if ((otherParticipantInfo.firstName != null && otherParticipantInfo.firstName
				.length() > 0))
			displayName = otherParticipantInfo.firstName;

		if ((otherParticipantInfo.firstName != null && otherParticipantInfo.firstName
				.length() > 0)
				&& (otherParticipantInfo.lastName != null && otherParticipantInfo.lastName
						.length() > 0))
			displayName = otherParticipantInfo.firstName + " "
					+ otherParticipantInfo.lastName;

		DialogInterface.OnClickListener deleteHandler = null;
		// final String user = "element.name";
		deleteHandler = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				StringBuilder text = new StringBuilder(
						"reportProfile::reportee##");
				text.append(otherParticipantInfo.userName);
				if (BusinessProxy.sSelf.sendNewTextMessage(
						"Yank Profile<a:yankprofile>", text.toString(), true)) {
					StringBuilder buff = new StringBuilder(displayName);
					buff.append(" has been deleted.");
					buff.append("\n");
					buff.append("Your Request has been sent to moderator!");
					showAlertMessage("Info", buff.toString(), null, null, null);
				}
			}
		};
		showAlertMessage("Confirm", "Do you really want to report "
				+ displayName + "?", new int[] {
				DialogInterface.BUTTON_POSITIVE,
				DialogInterface.BUTTON_NEGATIVE },
				new String[] { "Yes", "No" },
				new DialogInterface.OnClickListener[] { deleteHandler, null });

	}

	public void sendInvite(Message message) {
		if (!ConversationsActivity.this.checkInternetConnection()) {
			ConversationsActivity.this.networkLossAlert();
			return;
		}
		StringBuffer text = new StringBuffer("Invite::User##");
		boolean isSender = false;
		if (message.participantInfo != null
				&& conversationList.participantInfo.size() > 0) {
			for (int i = 0; i < message.participantInfo.size(); i++) {
				ParticipantInfo participantInfo = message.participantInfo
						.get(i);
				// if
				// (participantInfo.userName.equalsIgnoreCase(SettingData.sSelf
				// .getUserName()))
				// ownerParticipantInfo = participantInfo;
				// else
				// otherParticipantInfo = participantInfo;
				if (participantInfo.isSender) {
					otherParticipantInfo = participantInfo;
					break;
				}

			}
		}
		String displayName = otherParticipantInfo.userName;
		if ((otherParticipantInfo.firstName != null && otherParticipantInfo.firstName
				.length() > 0)
				&& (otherParticipantInfo.lastName != null && otherParticipantInfo.lastName
						.length() > 0))
			displayName = otherParticipantInfo.firstName + " "
					+ otherParticipantInfo.lastName;

		String n = otherParticipantInfo.userName;

		n = BusinessProxy.sSelf.parseUsernameInformation(n);
		text.append(n);
		if (BusinessProxy.sSelf.sendNewTextMessage(
				"Friend Manager<a:friendmgr>", text.toString(), true)) {
			showSimpleAlert(
					"Info",
					String.format(
							"Friend invite has been sent to %s\nYou will get confirmation in your System Messages!",
							displayName));
		}
	}

	public void reportMessage(Message message) {
		if (!ConversationsActivity.this.checkInternetConnection()) {
			ConversationsActivity.this.networkLossAlert();
			return;
		}
		boolean isSender = false;
		if (message.participantInfo != null
				&& conversationList.participantInfo.size() > 0) {
			for (int i = 0; i < message.participantInfo.size(); i++) {
				ParticipantInfo participantInfo = message.participantInfo
						.get(i);
				// if
				// (participantInfo.userName.equalsIgnoreCase(SettingData.sSelf
				// .getUserName()))
				// ownerParticipantInfo = participantInfo;
				// else
				// otherParticipantInfo = participantInfo;
				if (participantInfo.isSender) {
					otherParticipantInfo = participantInfo;
					break;
				}

			}
		}
		String displayName = otherParticipantInfo.userName;
		if ((otherParticipantInfo.firstName != null && otherParticipantInfo.firstName
				.length() > 0)
				&& (otherParticipantInfo.lastName != null && otherParticipantInfo.lastName
						.length() > 0))
			displayName = otherParticipantInfo.firstName + " "
					+ otherParticipantInfo.lastName;
		DialogInterface.OnClickListener reportHandler = null;
		final String messageID = message.mfuId;
		reportHandler = new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// if (user.contains("a:") || user.contains("g:") ||
				// user.contains("m:")) {
				// showSimpleAlert("Error", "Can't report to this message.");
				// return;
				// }
				if (BusinessProxy.sSelf.sendNewTextMessage(
						Constants.AG_FLAGCONTENT,
						String.format("ymsg::msgid##%s", messageID), true)) {
					showSimpleAlert("Info",
							String.format("This message has been reported."));
				}
			}
		};
		showAlertMessage("Confirm", "Do you want to report this message?",
				new int[] { DialogInterface.BUTTON_POSITIVE,
						DialogInterface.BUTTON_NEGATIVE }, new String[] {
						"Yes", "No" }, new DialogInterface.OnClickListener[] {
						reportHandler, null });
		/*
		 * DialogInterface.OnClickListener deleteHandler = null; // final String
		 * user = "element.name"; deleteHandler = new
		 * DialogInterface.OnClickListener() {
		 * 
		 * public void onClick(DialogInterface dialog, int which) {
		 * StringBuilder text = new StringBuilder("Reportuser::User##");
		 * text.append(ownerParticipantInfo.userName); if
		 * (BusinessProxy.sSelf.sendNewTextMessage(
		 * "Friend Manager<a:friendmgr>", text.toString(), true)) {
		 * StringBuilder buff = new StringBuilder(
		 * ownerParticipantInfo.userName); buff.append(" has been deleted.");
		 * buff.append("\n"); buff.append(
		 * "To add this friend again, you can use the 'Friend Invite' option!");
		 * showAlertMessage("Info", buff.toString(), null, null, null); } } };
		 * showAlertMessage("Confirm",
		 * "Do you really want to report this message?", new int[] {
		 * DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE },
		 * new String[] { "Yes", "No" }, new DialogInterface.OnClickListener[] {
		 * deleteHandler, null });
		 */

	}

	public void openConversationOption(int type) {// 1 right 2 left
		HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
		menuItems.put(0, "Delete Conversiotion");
		menuItems.put(1, "Report Person");
		menuItems.put(2, "Cancel");
		openActionSheetPos(menuItems, new OnMenuItemSelectedListener() {
			@Override
			public void MenuItemSelectedEvent(Integer selection) {
				switch (selection) {
				case 0:
					request = new Request(DELETE_CONVERSATION, "url");
					if (!ConversationsActivity.this.checkInternetConnection()) {
						ConversationsActivity.this.networkLossAlert();
						return;
					}
					request.execute("LikeDislike");
					break;
				case 1:
					showToast("Wait few days ! :)");
					break;
				}

			}
		}, 0, null);
	}

	public void openShareOption() {// 1 right 2 left
		HashMap<Integer, String> menuItems = new HashMap<Integer, String>();
		// menuItems.put(0, "Rocketalk Community");
		menuItems.put(0, "Rocketalk Gallery");
		// menuItems.put(2, "Followers");
		// menuItems.put(3, "Facebook");
		// menuItems.put(4, "Twitter");
		menuItems.put(1, "Cancel");
		openActionSheetPos(menuItems, new OnMenuItemSelectedListener() {
			@Override
			public void MenuItemSelectedEvent(Integer selection) {
				switch (selection) {

				default:
					showToast("Wait few days ! :)");
					break;
				}

			}
		}, 0, null);
	}

	public void filltop() {
		try {
			ParticipantInfo ownerParticipantInfo = null;
			ParticipantInfo otherParticipantInfo = null;
			boolean isSender = false;
			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				for (int i = 0; i < conversationList.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = conversationList.participantInfo
							.get(i);
					if (participantInfo.userName
							.equalsIgnoreCase(SettingData.sSelf.getUserName()))
						ownerParticipantInfo = participantInfo;
					else
						otherParticipantInfo = participantInfo;
				}
			}
			ImageDownloader imageManager = new ImageDownloader();
			if (conversationList != null && conversationList.isGroup == 0) {
				// ImageDownloader imageManager = new ImageDownloader();
				imageManager.buddyThumb = true;
				imageManager.thumbnailReponseHandler = this;
				((CImageView) findViewById(R.message_row.other_image_top_profile))
						.setVisibility(View.VISIBLE);
				imageManager
						.download(
								otherParticipantInfo.userName.toLowerCase(),
								(CImageView) findViewById(R.message_row.other_image_top_profile),
								ConversationsActivity.this,
								imageManager.TYPE_THUMB_BUDDY, true);
				// findViewById(R.message_row.other_image_top_profile).setOnClickListener(profileClick);
				findViewById(R.message_row.other_image_top_profile).setTag(
						otherParticipantInfo);
			} else {
				// MUC - Group chat
				if (getIntent().getStringExtra("MUC_LOCAL_IMG") != null)
					((CImageView) findViewById(R.message_row.other_image_top_profile))
							.setImageURI(Uri.parse(getIntent().getStringExtra(
									"MUC_LOCAL_IMG")));
				else
					((CImageView) findViewById(R.message_row.other_image_top_profile))
							.setBackgroundResource(R.drawable.groupicon);
			}
			// Profile photo click will go back - P2P/MUC both
			findViewById(R.message_row.other_image_top_profile)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							onBackPressed();
						}
					});
			// conversation_option_panel

			// imageManager = new ImageDownloader();
			// imageManager
			// .download(
			// otherParticipantInfo.userName,
			// (ImageView)
			// findViewById(R.conversation_option_panel.other_image_top),
			// ConversationsActivity.this);

			// findViewById(R.conversation_option_panel.other_image_top)
			// .setOnClickListener(this);
			// findViewById(R.conversation_option_panel.other_image_top).setTag(
			// otherParticipantInfo);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void fillP2P() {

	}

	public void fillP2PNew() {
		try {

			boolean same = false;
			ParticipantInfo ownerParticipantInfo = null;
			ParticipantInfo otherParticipantInfo = null;
			actionUser = null;
			boolean isSender = false;
			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				for (int i = 0; i < conversationList.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = conversationList.participantInfo
							.get(i);

					if (participantInfo.userName
							.equalsIgnoreCase(SettingData.sSelf.getUserName()))
						ownerParticipantInfo = participantInfo;
					else {
						otherParticipantInfo = participantInfo;
						actionUser = participantInfo.userName;
					}

				}
			}
			if (otherParticipantInfo == null) {
				otherParticipantInfo = ownerParticipantInfo;
				same = true;
			}
			// lDialogInfo
			// .findViewById(R.conversation_option_panel.update_subject)
			// .setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.subject)
					.setVisibility(View.GONE);
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.hideSoftInputFromWindow(lDialogInfo.findViewById(R.conversation_option_panel.subject).getWindowToken(),
			// 0);
			lDialogInfo.findViewById(R.conversation_option_panel.other_image)
					.setVisibility(View.GONE);
			CImageView other_image = (CImageView) lDialogInfo
					.findViewById(R.conversation_option_panel.owner_image);
			ImageDownloader imageManager = new ImageDownloader();
			imageManager.download(otherParticipantInfo.userName, other_image,
					ConversationsActivity.this, imageManager.TYPE_IMAGE, true);
			if ((otherParticipantInfo.firstName != null && otherParticipantInfo.firstName
					.length() > 0)
					&& (otherParticipantInfo.lastName != null && otherParticipantInfo.lastName
							.length() > 0))
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.owner_name))
						.setText(otherParticipantInfo.firstName + " "
								+ otherParticipantInfo.lastName);
			else if ((otherParticipantInfo.firstName != null && otherParticipantInfo.firstName
					.length() > 0))
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.owner_name))
						.setText(otherParticipantInfo.firstName);
			else if ((otherParticipantInfo.lastName != null && otherParticipantInfo.lastName
					.length() > 0))
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.owner_name))
						.setText(otherParticipantInfo.lastName);
			else
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.owner_name))
						.setText(otherParticipantInfo.userName);
			if (ImageDownloader.getUserInfo(otherParticipantInfo.userName) != null) {
				// UserStatus userStatus =
				// ImageDownloader.buddyInfo.get(value.mName.hashCode()) ;
				// /
				// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
				//
				UserStatus userStatus = ImageDownloader
						.getUserInfo(otherParticipantInfo.userName);
				if (userStatus.onLineOffline == 1) {
					// //System.out.printlnprintln("userStatus.onLineOffline==="+userStatus.onLineOffline);
					((CImageView) lDialogInfo
							.findViewById(R.conversation_option_panel.online_offline0))
							.setImageResource(R.drawable.online_icon_chat);

				} else {
					// //System.out.printlnprintln("userStatus.onLineOffline==="+userStatus.onLineOffline);
					((CImageView) lDialogInfo
							.findViewById(R.conversation_option_panel.online_offline0))
							.setImageResource(R.drawable.offline_icon_chat);
				}
				// if(userStatus!=null)
				// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
			}
			other_image.setTag(otherParticipantInfo);
			other_image.setId(10001);
			other_image.setOnClickListener(profileClick);
			if (BusinessProxy.sSelf
					.isBookMarkFriend(otherParticipantInfo.userName)
					|| !BusinessProxy.sSelf
							.isFirendInList(otherParticipantInfo.userName)) {
				// ((TextView) lDialogInfo
				// .findViewById(R.conversation_option_panel.bookmark))
				// .setVisibility(View.VISIBLE);
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.bookmark))
						.setOnClickListener(sendFriendRequestListner);
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.bookmark))
						.setTag(otherParticipantInfo.userName);
			} else {
				((TextView) lDialogInfo
						.findViewById(R.conversation_option_panel.bookmark))
						.setVisibility(View.GONE);
			}
			lDialogInfo.findViewById(R.conversation_option_panel.owner_name)
					.setOnClickListener(this);
			lDialogInfo.findViewById(R.conversation_option_panel.owner_name)
					.setTag(otherParticipantInfo);
			((TextView) lDialogInfo
					.findViewById(R.conversation_option_panel.owner_image_heading))
					.setText("Conversation with");
			lDialogInfo.findViewById(R.conversation_option_panel.sep1)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p1)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.other_heading)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.leave)
					.setVisibility(View.GONE);

			lDialogInfo.findViewById(R.conversation_option_panel.p1)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p2)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p3)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p4)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p5)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p6)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p7)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p8)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p9)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p10)
					.setVisibility(View.GONE);

			if (same) {
				lDialogInfo.findViewById(
						R.conversation_option_panel.delete_user).setVisibility(
						View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.block)
						.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	CImageView p1_image, p2_image, p3_image, p4_image, p5_image, p6_image,
			p7_image, p8_image, p9_image, p10_image;
	TextView p1_username, p2_username, p3_username, p4_username, p5_username,
			p6_username, p7_username, p8_username, p9_username, p10_username,
			owner_image_heading;

	public void fillGroup() {

	}

	public void fillGroupNew() {
		try {

			// //System.out.printlnprintln("render is coming============");
			// //System.out.printlnprintln("000000000fillGroup0000000");
			// lDialogInfo
			// .findViewById(R.conversation_option_panel.update_subject)
			// .setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.subject)
					.setVisibility(View.VISIBLE);

			if (conversationList != null
					&& (conversationList.subject == null || conversationList.subject
							.trim().length() <= 0)) {
				((EditText) lDialogInfo
						.findViewById(R.conversation_option_panel.subject))
						.setHint(getString(R.string.name_this_group));
			} else
				((EditText) lDialogInfo
						.findViewById(R.conversation_option_panel.subject))
						.setHint(getString(R.string.change_group_name));
			ParticipantInfo ownerParticipantInfo = null;
			ParticipantInfo otherParticipantInfo = null;
			boolean isSender = false;
			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				for (int i = 0; i < conversationList.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = conversationList.participantInfo
							.get(i);
					if (participantInfo.userName
							.equalsIgnoreCase(SettingData.sSelf.getUserName()))
						ownerParticipantInfo = participantInfo;
					else
						otherParticipantInfo = participantInfo;

				}
			}
			lDialogInfo.findViewById(R.conversation_option_panel.other_image)
					.setVisibility(View.VISIBLE);
			lDialogInfo.findViewById(R.conversation_option_panel.p0)
					.setVisibility(View.GONE);
			// conversation_option_panel/p0
			ImageDownloader imageManager = new ImageDownloader();
			// imageManager
			// .download(
			// ownerParticipantInfo.userName,
			// (ImageView)
			// findViewById(R.conversation_option_panel.owner_image),
			// ConversationsActivity.this);

			// //System.out.printlnprintln("-------going "+CONVERSATIONID);

			// if (conversationList!=null&&conversationList.hasBeenViewed !=
			// null
			// && conversationList.hasBeenViewed.equalsIgnoreCase("never")) {
			if (CONVERSATIONID.startsWith("NP")) {
				// //System.out.printlnprintln("-------going to hide");
				// lDialogInfo.findViewById(
				// R.conversation_option_panel.update_subject)
				// .setVisibility(View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.subject)
						.setVisibility(View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.leave)
						.setVisibility(View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.block)
						.setVisibility(View.GONE);
				lDialogInfo.findViewById(
						R.conversation_option_panel.delete_user).setVisibility(
						View.GONE);
			} else {
				// //System.out.printlnprintln("-------going to show");
				// lDialogInfo.findViewById(
				// R.conversation_option_panel.update_subject)
				// .setVisibility(View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.subject)
						.setVisibility(View.VISIBLE);
				lDialogInfo.findViewById(R.conversation_option_panel.leave)
						.setVisibility(View.VISIBLE);
				lDialogInfo.findViewById(R.conversation_option_panel.block)
						.setVisibility(View.GONE);
				lDialogInfo.findViewById(
						R.conversation_option_panel.delete_user).setVisibility(
						View.GONE);
			}
			lDialogInfo.findViewById(R.conversation_option_panel.owner_image)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.sep1)
					.setVisibility(View.GONE);
			((TextView) lDialogInfo
					.findViewById(R.conversation_option_panel.owner_name))
					.setVisibility(View.GONE);
			// .setText(ownerParticipantInfo.userName);
			// findViewById(R.conversation_option_panel.owner_name)
			// .setOnClickListener(this);
			// findViewById(R.conversation_option_panel.owner_name).setTag(
			// ownerParticipantInfo);

			lDialogInfo.findViewById(R.conversation_option_panel.p1)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p2)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p3)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p4)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p5)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p6)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p7)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p8)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p9)
					.setVisibility(View.GONE);
			lDialogInfo.findViewById(R.conversation_option_panel.p10)
					.setVisibility(View.GONE);
			// InputMethodManager imm = (InputMethodManager)
			// getSystemService(Context.INPUT_METHOD_SERVICE);
			// imm.hideSoftInputFromWindow(lDialogInfo.findViewById(R.conversation_option_panel.subject).getWindowToken(),
			// 0);
			CImageView cImageView;
			boolean hide = true;
			int ind = 0;

			TextView p = (TextView) lDialogInfo
					.findViewById(R.conversation_option_panel.other_heading);

			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				p.setText(p.getText() + " ("
						+ conversationList.participantInfo.size() + "/10)");

				Collections.sort(conversationList.participantInfo,
						new Comparator<ParticipantInfo>() {

							public int compare(ParticipantInfo o1,
									ParticipantInfo o2) {
								return o1.getName().compareTo(o2.getName());
							}
						});

				for (int i = 0; i < conversationList.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = conversationList.participantInfo
							.get(i);
					// if (participantInfo.userName
					// .equalsIgnoreCase(SettingData.sSelf.getUserName())) {
					//
					// } else
					{
						if (ind == 0) {
							// //System.out.printlnprintln("render is coming============1");
							hide = false;
							lDialogInfo.findViewById(
									R.conversation_option_panel.p1)
									.setVisibility(View.VISIBLE);
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p1_image);
							p1_image.setTag(participantInfo);
							p1_image.setId(10001);
							p1_image.setOnClickListener(profileClick);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p1_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p1_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p1_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p1_username.setText(participantInfo.lastName);
							else
								p1_username.setText(participantInfo.userName);

							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline1))
											.setImageResource(R.drawable.online_icon_chat);

								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline1))
											.setImageResource(R.drawable.offline_icon_chat);
								}
								// if(userStatus!=null)
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
							}

							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark1))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark1))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark1))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark1))
										.setVisibility(View.GONE);
							}

						} else if (ind == 1) {
							// //System.out.printlnprintln("render is coming============2");
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p2_image);
							p2_image.setTag(participantInfo);
							p2_image.setId(10001);
							p2_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p2)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p2_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p2_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p2_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p2_username.setText(participantInfo.lastName);
							else
								p2_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline2))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline2))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline2)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline2)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark2))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark2))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark2))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark2))
										.setVisibility(View.GONE);
							}
						} else if (ind == 2) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p3_image);
							p3_image.setTag(participantInfo);
							p3_image.setId(10001);
							p3_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p3)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p3_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p3_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p3_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p3_username.setText(participantInfo.lastName);
							else
								p3_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline3))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline3))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline3)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline3)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark3))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark3))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark3))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark3))
										.setVisibility(View.GONE);
							}
						} else if (ind == 3) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p4_image);
							p4_image.setTag(participantInfo);
							p4_image.setId(10001);
							p4_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p4)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p4_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p4_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p4_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p4_username.setText(participantInfo.lastName);
							else
								p4_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline4))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline4))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline4)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline4)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark4))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark4))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark4))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark4))
										.setVisibility(View.GONE);
							}
						} else if (ind == 4) {
							hide = false;
							// p5_image = (CImageView)
							// findViewById(R.conversation_option_panel.p5_image);
							p5_image.setTag(participantInfo);
							p5_image.setId(10001);
							p5_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p5)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p5_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p5_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p5_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p5_username.setText(participantInfo.firstName);
							else
								p5_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline5))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline5))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline5)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline5)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark5))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark5))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark5))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark5))
										.setVisibility(View.GONE);
							}
						} else if (ind == 5) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p6_image);
							p6_image.setTag(participantInfo);
							p6_image.setId(10001);
							p6_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p6)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p6_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p6_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p6_username.setText(participantInfo.firstName);
							else
								p6_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline6))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline6))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline6)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline6)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark6))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark6))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark6))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark6))
										.setVisibility(View.GONE);
							}
						} else if (ind == 6) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p7_image);
							p7_image.setTag(participantInfo);
							p7_image.setId(10001);
							p7_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p7)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p7_image, ConversationsActivity.this);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p7_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p7_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p7_username.setText(participantInfo.lastName);
							else
								p7_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline7))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline7))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline7)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline7)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark7))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark7))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark7))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark7))
										.setVisibility(View.GONE);
							}
						} else if (ind == 7) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p8_image);
							p8_image.setTag(participantInfo);
							p8_image.setId(10001);
							p8_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p8)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p8_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p8_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p8_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p8_username.setText(participantInfo.lastName);
							else
								p8_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline8))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline8))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline8)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline8)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark8))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark8))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark8))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark8))
										.setVisibility(View.GONE);
							}
						} else if (ind == 8) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p4_image);
							p9_image.setTag(participantInfo);
							p9_image.setId(10001);
							p9_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p9)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p9_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							// ((TextView)
							// findViewById(R.conversation_option_panel.p9_username))
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p9_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p9_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p9_username.setText(participantInfo.lastName);
							else
								p9_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline9))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline9))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline9)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline9)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark9))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark9))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark9))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark9))
										.setVisibility(View.GONE);
							}

						} else if (ind == 9) {
							hide = false;
							// cImageView = (CImageView)
							// findViewById(R.conversation_option_panel.p4_image);
							p10_image.setTag(participantInfo);
							p10_image.setId(10001);
							p10_image.setOnClickListener(profileClick);
							lDialogInfo.findViewById(
									R.conversation_option_panel.p10)
									.setVisibility(View.VISIBLE);
							imageManager = new ImageDownloader();
							imageManager.download(participantInfo.userName,
									p10_image, ConversationsActivity.this,
									ImageDownloader.TYPE_THUMB_BUDDY);
							// ((TextView)
							// findViewById(R.conversation_option_panel.p9_username))
							if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0)
									&& (participantInfo.lastName != null && participantInfo.lastName
											.length() > 0))
								p10_username.setText(participantInfo.firstName
										+ " " + participantInfo.lastName);
							else if ((participantInfo.firstName != null && participantInfo.firstName
									.length() > 0))
								p10_username.setText(participantInfo.firstName);
							else if ((participantInfo.lastName != null && participantInfo.lastName
									.length() > 0))
								p10_username.setText(participantInfo.lastName);
							else
								p10_username.setText(participantInfo.userName);
							if (ImageDownloader
									.getUserInfo(participantInfo.userName) != null) {
								// // UserStatus userStatus =
								// ImageDownloader.buddyInfo.get(value.mName.hashCode())
								// ;
								// //
								// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
								//
								UserStatus userStatus = ImageDownloader
										.getUserInfo(participantInfo.userName);
								if (userStatus != null
										&& userStatus.onLineOffline == 1) {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline10))
											.setImageResource(R.drawable.online_icon_chat);
								} else {
									((CImageView) lDialogInfo
											.findViewById(R.conversation_option_panel.online_offline10))
											.setImageResource(R.drawable.offline_icon_chat);
								}
							}
							/*
							 * if(BusinessProxy.sSelf.isFriendaOnline(
							 * participantInfo.userName)){
							 * ((CImageView)findViewById
							 * (R.conversation_option_panel
							 * .online_offline10)).setImageResource
							 * (R.drawable.online_icon_chat) ;
							 * 
							 * }else{
							 * ((CImageView)findViewById(R.conversation_option_panel
							 * .online_offline10)).setImageResource(R.drawable.
							 * offline_icon_chat) ; }
							 */
							if (BusinessProxy.sSelf
									.isBookMarkFriend(participantInfo.userName)
									|| !BusinessProxy.sSelf
											.isFirendInList(participantInfo.userName)) {
								// ((TextView) lDialogInfo
								// .findViewById(R.conversation_option_panel.bookmark10))
								// .setVisibility(View.VISIBLE);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark10))
										.setOnClickListener(sendFriendRequestListner);
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark10))
										.setTag(participantInfo.userName);
							} else {
								((TextView) lDialogInfo
										.findViewById(R.conversation_option_panel.bookmark10))
										.setVisibility(View.GONE);
							}
						}
						/*
						 * else if (ind == 10) { hide = false; // cImageView =
						 * (CImageView) //
						 * findViewById(R.conversation_option_panel.p4_image);
						 * p10_image.setTag(participantInfo);
						 * p10_image.setId(10001);
						 * p10_image.setOnClickListener(this);
						 * findViewById(R.conversation_option_panel.p10)
						 * .setVisibility(View.VISIBLE); imageManager = new
						 * ImageDownloader();
						 * imageManager.download(participantInfo.userName,
						 * p10_image, ConversationsActivity.this); //
						 * ((TextView) //
						 * findViewById(R.conversation_option_panel
						 * .p9_username)) if((participantInfo.firstName!=null &&
						 * participantInfo.firstName.length()>0 ) &&
						 * (participantInfo.lastName!=null &&
						 * participantInfo.lastName.length()>0))
						 * p10_username.setText(participantInfo.firstName+" "+
						 * participantInfo.lastName); else
						 * p10_username.setText(participantInfo.userName); }
						 */
						ind++;
					}
				}
			}
			if (hide)
				owner_image_heading.setVisibility(View.GONE);

			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() >= 10) {
				lDialogInfo.findViewById(
						R.conversation_option_panel.add_recipant)
						.setVisibility(View.GONE);
			}

			// //System.out.printlnprintln("-----------conversationList.participantInfo.size() /; "+conversationList.participantInfo.size());
			if (conversationList != null && conversationList.isLeft == 1) {
				// lDialogInfo.findViewById(
				// R.conversation_option_panel.update_subject)
				// .setVisibility(View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.subject)
						.setVisibility(View.GONE);
				lDialogInfo.findViewById(R.conversation_option_panel.leave)
						.setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setRetry(long idold) {

		ContentValues mUpdateValues = new ContentValues();
		mUpdateValues.put(MediaPostTable.TRY, 0);
		long id = ((System.currentTimeMillis() - 10000));
		mUpdateValues.put(MediaPostTable.DATE, id);
		// mUpdateValues.put(MediaPostTable.SENT_DATE,
		// System.currentTimeMillis());
		mUpdateValues.put(MediaPostTable.ERROR_MSG, "None");
		int mRowsUpdated = getContentResolver().update(
				MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, mUpdateValues, // the
				MediaPostTable.DATE + "=" + idold, null // the value to compare
				// to
				);

		mUpdateValues = new ContentValues();

		mUpdateValues.put(MessageTable.SENDING_STATE,
				Constants.MESSAGE_STATUS_SENDING);
		mUpdateValues.put(MessageTable.MESSAGE_ID, id);

		Cursor cursor = getCursor();
		cursor.moveToLast();
		long l = cursor.getLong(cursor
				.getColumnIndex(LandingPageTable.INSERT_TIME));
		cursor.close();
		// mUpdateValues.put(MessageTable.INSERT_TIME, l + 1000 * 1);

		mRowsUpdated = getContentResolver().update(
				MediaContentProvider.CONTENT_URI_INBOX, mUpdateValues, // the
				MessageTable.MESSAGE_ID + "=" + idold, // the
				// column
				// to
				// select
				// on
				null // the value to compare to
				);
		new Thread(new Runnable() {

			@Override
			public void run() {
				Utilities.startComposeService(ConversationsActivity.this);
			}
		}).start();

	}

	// mEmoticons.put(":)", R.drawable.);
	// mEmoticons.put(":-)", R.drawable.);
	// mEmoticons.put(";)", R.drawable.);
	// mEmoticons.put(";-)", R.drawable.androidwink);
	// mEmoticons.put(":(", R.drawable.);
	// mEmoticons.put(":-(", R.drawable.androidsad);
	// mEmoticons.put("B-)", R.drawable.);
	// mEmoticons.put(":-*", R.drawable.);
	// mEmoticons.put(":-P", R.drawable.);
	// mEmoticons.put(":P", R.drawable.tongue);
	// mEmoticons.put("X(", R.drawable.);
	// mEmoticons.put("X-(", R.drawable.angry);
	// mEmoticons.put(":D", R.drawable.);
	// mEmoticons.put(":-D", R.drawable.grin);
	// mEmoticons.put(":((", R.drawable.);
	// mEmoticons.put(":-O", R.drawable.);
	// mEmoticons.put(":O", R.drawable.);

	// public Integer[] imageIDs2 = { R.drawable.emo_1, R.drawable.emo_2,
	// R.drawable.emo_3, R.drawable.emo_4, R.drawable.emo_5,
	// R.drawable.emo_6, R.drawable.emo_7, R.drawable.emo_8,
	// R.drawable.emo_9, R.drawable.emo_10, R.drawable.emo_11,
	// R.drawable.emo_12, R.drawable.emo_13
	//
	// };

	class ImageAdapter extends BaseAdapter {
		private Context context;
		public String[][] imageIDs;

		public ImageAdapter(Context c, String[][] imageIDResults) {
			context = c;
			imageIDs = imageIDResults;
		}

		public int getCount() {
			return imageIDs.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// ImageView imageView;
			// if (convertView == null) {
			// imageView = new ImageView(context);
			// imageView.setPadding(0, 0, 0, 0);;//imageView.setLayoutParams(new
			// GridView.LayoutParams(100, 100));
			// // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			// } else {
			// imageView = (ImageView) convertView;
			// }
			// imageView.setImageResource(imageIDs[position]);
			// return imageView;

			LayoutInflater inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View listItemView = inflator.inflate(R.layout.emoticons_layout2,
					null);
			ImageView imageView = (ImageView) listItemView
					.findViewById(R.emoticon.icon);
			imageView.setImageResource(Integer.parseInt(imageIDs[position][1]));
			listItemView.setTag(imageIDs[position][0]);
			return listItemView;
		}
	}

	private Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(500);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}

	private Animation outToRightAnimation() {
		Animation outtoRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.2f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}

	class MyGestureDetector extends SimpleOnGestureListener {
		@SuppressWarnings("static-access")
		// @Override
		// public boolean onSingleTapConfirmed(MotionEvent e) {
		//
		// //System.out.printlnprintln("Rocketalk---conversationactivity-------onSingleTapConfirmed");
		// return false;
		// }
		//
		// @SuppressWarnings("static-access")
		// @Override
		// public boolean onSingleTapUp(MotionEvent e) {
		// //System.out.printlnprintln("Rocketalk------conversationactivity----onSingleTapUp");
		// return true;
		// }
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// //System.out.printlnprintln("Rocketalk-------conversationactivity---onFling");
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					imageFrame.setInAnimation(inFromRightAnimation());
					imageFrame.setOutAnimation(outToLeftAnimation());
					imageFrame.showNext();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					imageFrame.setInAnimation(inFromLeftAnimation());
					imageFrame.setOutAnimation(outToRightAnimation());
					imageFrame.showPrevious();
				}
				navi.setPosition(imageFrame.getDisplayedChild());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

		messageBox.setText(messageBox.getText().toString()
				+ (String) view.getTag());
		Spanned spannedContent = getSmiledText(this, messageBox.getText()
				.toString());
		messageBox.setText(spannedContent, BufferType.SPANNABLE);
		messageBox.setSelection(messageBox.getText().toString().length());
		Log.v("ConverstionsActivity","Manoj"+" 12228");
		chat_panel_more_option_emo.setVisibility(View.GONE);

	}

	private void playAvailableVoice(final int postion, final String pathValue) {
		try {

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					availableVoice = true;
					// AssetFileDescriptor descriptor =
					// getAssets().openFd(animationSound.get(postion));

					// m.setDataSource(descriptor.getFileDescriptor(),
					// descriptor.getStartOffset(), descriptor.getLength());
					// String path="assets/"+animationSound.get(postion);
					// mVoicePath
					// =path;//descriptor.getFileDescriptor().toString();

					// //System.out.printlnprintln("mVoicePath=="+mVoicePath+"descriptor=="+descriptor);
					// descriptor.close();
					String path = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + gifpath;
					File file = null;
					if (pathValue == null)
						file = new File(path + animationSound.get(postion));
					else
						file = new File(path + pathValue);

					// //System.out.printlnprintln("postion====="+postion
					// +"pathValue==="+pathValue
					// +"file====="+file.getAbsolutePath());
					// ReadWriteUtill.readBytesFromFile(file);
					// InputStream input =
					// getAssets().open(animationSound.get(postion));

//					try {
//						mVoiceMedia.startPlayingWithByte(ReadWriteUtill.readBytesFromFile(file));
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					// mVoiceMedia.startNewPlaying(mVoicePath, null,false);
				}
			}).start();

		} catch (Exception e) {
			// //System.out.printlnprintln("onerroe=======3");
			e.printStackTrace();
			if (fullDialog != null && fullDialog.isShowing()) {
				fullDialog.dismiss();
			}

		}

		// //System.out.printlnprintln("playing.......");
	}

	public int stateValue = 0;

	@Override
	public void voicePlayStarted() {

	}

	@Override
	public void voicePlayCompleted() {
		// imageFrameView.setVisibility(View.VISIBLE);
		try {

			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					BusinessProxy.sSelf.animPlay = false;
					if (fullDialog != null && fullDialog.isShowing()) {
						fullDialog.dismiss();
						// System.gc();
						// Runtime.getRuntime().gc();
						/*
						 * if(gifView!=null){ gifView.clearColorFilter();
						 * gifView.clearAnimation(); gifView.recycleView(); }
						 */

					}
				}
			});

//			if (stateValue > 0) {
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//
//						// TODO Auto-generated method stub
//						if (BusinessProxy.sSelf.currentAnimData != null
//								&& BusinessProxy.sSelf.currentAnimData
//										.getFeedData() != null) {
//
//							RtAnimFeed feed = BusinessProxy.sSelf.currentAnimData
//									.getFeedData().elementAt(stateValue);
//							// //System.out.printlnprintln("===view====onitem"+feed.getSetId());
//							sendStatAnim(null, "" + feed.getSetId(), null);
//						}
//					}
//				}).start();
//			}
		} catch (Exception e) {
			// //System.out.printlnprintln("onerroe=======2");
			// TODO: handle exception
			e.printStackTrace();
			BusinessProxy.sSelf.animPlay = false;
			if (fullDialog != null && fullDialog.isShowing()) {
				fullDialog.dismiss();
			}

		}

	}

	@Override
	public void onError(int i) {
		// TODO Auto-generated method stub
		// System.out.printlnprintln("-------------onError------------");
		BusinessProxy.sSelf.animPlay = false;
		if (fullDialog != null && fullDialog.isShowing()) {
			fullDialog.dismiss();
		}
		// //System.out.printlnprintln("onerroe=======i");

	}

	@Override
	public void onError(String i, final int requestForID) {
		// System.out.printlnprintln("-------------onError------------" +
		// requestForID
		// + " reason :" + i);
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (Constants.FEED_GET_CONVERSATION_MESSAGES == requestForID
						|| Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID) {
					if (load_prevoius_message == null)
						load_prevoius_message = ((View) findViewById(R.conversation.load_prevoius_message));
					if (load_prevoius_message != null
							&& load_prevoius_message.getVisibility() == View.VISIBLE) {

						loadingChat.setVisibility(View.GONE);
						load_prevoius_message2.setOnClickListener(null);
						load_prevoius_message2
								.setText(getResources().getString(
										R.string.no_conversation_started_yet));
						// load_prevoius_message2.setGravity(Gravity.CENTER);

						cidRefresh.remove(getIntent().getStringExtra(
								Constants.CONVERSATION_ID));

					}
					// load_prevoius_message.setText(getResources().getString(
					// R.string.load_prevoius_message));
					// load_prevoius_message.setVisibility(View.GONE) ;
					// //System.out.printlnprintln("--------load_prevoius_message-VISIBLE");
					// listViewActivity

					// .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
					// activityAdapter = new
					// ConversationsAdapter(ConversationsActivity.this,
					// getCursor(), true,
					// ConversationsActivity.this);
					// // if(count>0)
					// listViewActivity.setAdapter(activityAdapter);
				}
			}
		});
	}

	public byte[] convertInputStreamToByteArray(InputStream inputStream) {
		byte[] bytes = null;

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			byte data[] = new byte[1024];
			int count;

			while ((count = inputStream.read(data)) != -1) {
				bos.write(data, 0, count);
			}

			bos.flush();
			bos.close();
			inputStream.close();

			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	int animDataBaseCounter = 0;

	private class MessageAdapter extends BaseAdapter {
		/*
		 * public Integer[] mThumbIds = { 1, 2, 3, 4, 5, 6, 7, 8,
		 * 9,10,11,12,13,14,15,16,17,18 };
		 */

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return animationFileName.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("null")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// url from server
			// String
			// gifUrl="http://74.208.228.56/akm/android/akm/gif/th_20100117.gif";
			// LinearLayout rowView=null ;
			try {

				String path = null;
				LayoutInflater inflater = LayoutInflater
						.from(getApplicationContext());
				TextView gif_view_heading, gif_view_des;
				// if (convertView == null) { // if it's not recycled,
				// initialize some attributes
				// = (LayoutInflater)
				// getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// //System.out.printlnprintln("===getview if convertView==null  ====");

				// rowView.setBackgroundColor(Color.GRAY);

				// server image only
				// gifView = new GifDecoderView(getApplicationContext());
				// gifView = (GifDecoderView)
				// rowView;//.findViewById(R.id.gif_view);

				// Rect displayRectangle = new Rect();

				// imageFrameView.setBackgroundResource()
				try {
					// get input stream
					// InputStream ims =
					// getAssets().open(animationImageView.get(position));
					// load image as Drawable
					rowView = (LinearLayout) inflater.inflate(
							R.layout.animatedlist_row, parent, false);
					// rowView.setLayoutParams(new
					// GridView.LayoutParams(LayoutParams.FILL_PARENT,
					// (int)(displayRectangle.height() * 0.9f)));
					imageFrameView = (ImageView) rowView
							.findViewById(R.id.imageFrame);
					rowView.setBackgroundResource(R.drawable.bluetoothbac);
					path = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + gifpath;
					File file = new File(path
							+ animationImageView.get(position));
					// //System.out.printlnprintln("file==="+file.getPath());
					// file=""/mnt/sdcard/RockeTalk/.GifImage/img69_1_11_K_I_I2_dgwlyftv6n_14.png
					// imageFrameView.setBackgroundResource(R.id.)
					// byte byteData[]=ReadWriteUtill.readBytesFromFile(file);
					// ByteArrayInputStream bis = new
					// ByteArrayInputStream(byteData);
					// Drawable d = Drawable.createFromStream(bis, null);
					// byteData=null;
					// set image to ImageView
					/*
					 * Drawable d = getPicsFromSdcard(file); //change
					 * 
					 * imageFrameView.setImageDrawable(d); //change
					 */
					Bitmap d = decodeFile(file);
					imageFrameView.setImageBitmap(d);
					d = null;
					// imageFrameView.setImageBitmap(b);
					// imageFrameView.setId(1000);
				} catch (Exception ex) {

				}
				// animationImageView.add()
				/*
				 * gifView = new gifView = new GifMovieView
				 * (getApplicationContext(),stream);(getApplicationContext());
				 * gifView.setLayoutParams(new
				 * GridView.LayoutParams(LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT)); //
				 * gifView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				 * gifView.setPadding(8, 8, 8, 8); gifView.setId(100);
				 */
				/*
				 * InputStream stream = null; try {
				 * ////System.out.printlnprintln
				 * ("animationFileTrans.get(position)=="
				 * +animationFileTrans.get(0)); stream =
				 * getAssets().open(animationFileTrans.get(position)); } catch
				 * (IOException e) { e.printStackTrace(); } //
				 * if(currentIndexId!=position) gifView.playGifOneFrame(stream);
				 */

				// gifView.setId(position);
				// server image only
				// ImageDownload task = new ImageDownload(gifView,null);
				// task.execute(new String[] { gifUrl
				// });//"http://psdhunter.com/psds/5554-thumb-gray-image-slider-psd-file-psdhome.png"
				// });
				rowView.setGravity(Gravity.CENTER);

				// rowView.addView(gifView);

				layoutMenu = (LinearLayout) inflater.inflate(
						R.layout.animation_play_select, parent, false);// (LinearLayout)rowView.findViewById(R.layout.animation_play_select);

				if (currentIndexId != position)
					layoutMenu.setVisibility(View.INVISIBLE);
				// rowView.setId(position);
				// rowView.setTag(layoutMenu);
				rowView.setId(position);
				rowView.setTag(layoutMenu);

				// rowView.setOnClickListener(setonclickmenu);
				// //System.out.printlnprintln("===view====onitem"+layoutMenu.getId());

				play = (Button) layoutMenu.findViewById(R.id.messageplay);
				play.setTag(rowView);
				play.setId(position);
				play.setOnClickListener(playView);
				// select.setTag(path);
				select = (Button) layoutMenu.findViewById(R.id.messageselect);
				select.setId(position);
				select.setOnClickListener(salectView);

				// rowView.setId(position);
				rowView.addView(layoutMenu);
				// layoutMenu
				// =(LinearLayout)inflater.inflate(R.layout.animlayoutdivider,
				// parent, false);
				// rowView.addView(layoutMenu);
				// rowView.addView(messagePlay);
				// rowView.addView(messageSelect);
				// gif_view_heading =(TextView)
				// rowView.findViewById(R.id.gif_view_heading);
				/*
				 * gif_view_heading =new TextView(getApplicationContext());
				 * gif_view_heading.setGravity(Gravity.CENTER);
				 * gif_view_heading.setText("playboy");
				 * rowView.addView(gif_view_heading); gif_view_des =new
				 * TextView(getApplicationContext());//(TextView)
				 * rowView.findViewById(R.id.gif_view_dis);
				 * gif_view_heading.setGravity(Gravity.CENTER);
				 * gif_view_des.setText("Ajay Kumar Maurya");
				 * gif_view_heading.setGravity(Gravity.CENTER);
				 * rowView.addView(gif_view_des);
				 */

				// } else {
				// //System.out.printlnprintln("===getview if convertView not null  ====");
				// rowView = (LinearLayout) convertView;
				// }

				if (BusinessProxy.sSelf.animloadingFlag) {
					if (gridView != null) {
						// if(dialogAnimation!=null &&
						// dialogAnimation.isShowing()){
						// dialogAnimation.dismiss();
						// appendAllAnimantinFile();
						// if(animDataBaseCounter<BusinessProxy.sSelf.currentAnimData.FeedData.size()){
						// appendAllAnimantinFile();
						openDialogAnimation();
						// mMessageAdapter
						// mMessageAdapter.notifyDataSetChanged();
						// }
						BusinessProxy.sSelf.animloadingFlag = false;
					}
				}
				// }
			} catch (OutOfMemoryError e) {
				Logger.conversationLog("get view ani : ", e.toString());
			}
			return rowView;

		}

	}

	// private Bitmap decodeFile(File f) {
	// FileOutputStream os = null;
	// Bitmap b = null;
	// try {
	// // Decode image size
	// BitmapFactory.Options o = new BitmapFactory.Options();
	// o.inJustDecodeBounds = true;
	//
	// FileInputStream fis = new FileInputStream(f);
	// BitmapFactory.decodeStream(fis, null, o);
	// fis.close();
	//
	// int scale = 1;
	// // if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE)
	// // {
	// // scale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE
	// // / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
	// // }
	//
	// // Decode with inSampleSize
	// BitmapFactory.Options o2 = new BitmapFactory.Options();
	// o2.inSampleSize = scale;
	// fis = new FileInputStream(f);
	// try {
	//
	// b = BitmapFactory.decodeStream(fis, null, o2);
	//
	// } catch (OutOfMemoryError e) {
	// // Log.d("hai","filename"+f);
	// System.gc();
	// }
	// fis.close();
	// } catch (IOException e) {
	// }
	// return b;
	// }

	OnItemClickListener setonclick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {

			// if(1==1)
			// return;
			layoutMenu = (LinearLayout) view.getTag();
			// layoutMenu.setVisibility(View.GONE);
			if (layoutMenu != null) {
				if (layoutMenu.getVisibility() == View.INVISIBLE) {
					// if(1==1)
					// return;
					layoutMenu.setVisibility(View.VISIBLE);

				} else
					layoutMenu.setVisibility(View.INVISIBLE);

				currentIndexId = view.getId();
				viewMenu = true;
				layoutMenu.invalidate();

			}
			// gridView.invalidateViews();
			mMessageAdapter.notifyDataSetChanged();
			// gridView.setOnItemClickListener(setonclick);
			// view.invalidate();
			/*
			 * Intent i = new Intent(getApplicationContext(),
			 * FullImageActivity.class); // passing array index i.putExtra("id",
			 * position); startActivity(i);
			 */
		}
	};
	OnClickListener setonclickmenu = new OnClickListener() {
		public void onClick(View view) {
			if (layoutMenu != null) {
				// //System.out.printlnprintln("===view====onitem"+view.getId());
				layoutMenu = (LinearLayout) view.getTag();
				// layoutMenu.setVisibility(View.GONE);

				if (layoutMenu.getVisibility() == View.INVISIBLE)
					layoutMenu.setVisibility(View.VISIBLE);
				else
					layoutMenu.setVisibility(View.INVISIBLE);
				// gridView.invalidate();
				currentIndexId = view.getId();
				viewMenu = true;
				// layoutMenu.invalidate();
				// mMessageAdapter = new MessageAdapter();
				// mMessageAdapter.notifyDataSetChanged();
				// gridView.setAdapter(mMessageAdapter);
				// gridView.invalidate();
				// gridView.removeView(layoutMenu);
				// gridView.invalidateViews();
				// mMessageAdapter.notifyDataSetChanged();
				// gridView.invalidateViews();
				// adapter.notifyDataChanged();
				// grid.setAdapter(adapter);
				// gridView.invalidate();
			}
		}
	};
	OnClickListener playView = new OnClickListener() {
		public void onClick(final View view) {

			/*
			 * LinearLayout layout=(LinearLayout)view.getTag(); imageFrameView
			 * =(ImageView)layout.findViewById(R.id.imageFrame);
			 * imageFrameView.setVisibility(View.GONE); //InputStream stream =
			 * null; ByteArrayInputStream stream =null; try { String path =
			 * Environment.getExternalStorageDirectory() .getAbsolutePath() +
			 * gifpath; File file =new
			 * File(path+animationFileName.get(view.getId())); byte
			 * byteData[]=ReadWriteUtill.readBytesFromFile(file); stream = new
			 * ByteArrayInputStream(byteData); // stream =
			 * getAssets().open(animationFileTrans.get(view.getId())); } catch
			 * (IOException e) { e.printStackTrace(); }
			 * 
			 * 
			 * gifView =(GifDecoderView)layout.findViewById(100);
			 * gifView.playGifLoopCountOne(stream);
			 * 
			 * playAvailableVoice(view.getId());
			 */

			try {
//				handler.post(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						openDialogFullScreen(view.getId(), null);
//					}
//				});
				/*
				 * new Thread(new Runnable() {
				 * 
				 * @Override public void run() {
				 * 
				 * // TODO Auto-generated method stub
				 * if(BusinessProxy.sSelf.currentAnimData!=null &&
				 * BusinessProxy.sSelf.currentAnimData.getFeedData()!=null){
				 * 
				 * RtAnimFeed
				 * feed=BusinessProxy.sSelf.currentAnimData.getFeedData
				 * ().elementAt(view.getId());
				 * //System.out.printlnprintln("===view====onitem"
				 * +feed.getSetId());
				 * sendStatAnim(null,""+feed.getSetId(),null); } } }).start();
				 */

			} catch (Exception e) {
				// TODO: handle exception

			}

		}
	};

	/*
	 * public void playBeep(int position) { try {
	 * 
	 * if (m.isPlaying()) { m.stop(); m.release(); m = new MediaPlayer(); }
	 * AssetFileDescriptor descriptor =
	 * getAssets().openFd(animationSound.get(position));
	 * m.setDataSource(descriptor.getFileDescriptor(),
	 * descriptor.getStartOffset(), descriptor.getLength()); descriptor.close();
	 * 
	 * m.prepare(); m.setVolume(1f, 1f); m.setLooping(true); m.start(); } catch
	 * (Exception e) { } }
	 */
	OnClickListener salectView = new OnClickListener() {
		public void onClick(View view) {
			try {
				// for(int
				// iTemp=0;iTemp<BusinessProxy.sSelf.currentAnimData.getFeedData().size();iTemp++){
				// RtAnimFeed
				// feed=BusinessProxy.sSelf.currentAnimData.getFeedData().elementAt(view.getId());
				// String xxx=getParticipantInfo();
				// //System.out.printlnprintln("feed===="+feed.getAnimTag()+"xxx=="+feed.getThumbUrl()+"feed status===="+feed.getStatus()+"feed==id="+feed.getSetId());
				// }
				// Vector<RtAnimFeed> animVlaue =
				// BusinessProxy.sSelf.db.getAllAnimationFeed();
				// animationImageView.add(feed.getThumbCaption()+"_"+feed.getSetId()+".png");
				// animationTagValue.add(feed.getAnimTag());
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + gifpath;
				File file = new File(path
						+ animationImageView.get(view.getId()));
				/*
				 * if(BusinessProxy.sSelf.currentAnimData!=null &&
				 * BusinessProxy.sSelf.currentAnimData.getFeedData()!=null){
				 * 
				 * //for(int
				 * iTemp=0;iTemp<BusinessProxy.sSelf.currentAnimData.getFeedData
				 * ().size();iTemp++){ RtAnimFeed
				 * feed=BusinessProxy.sSelf.currentAnimData
				 * .getFeedData().elementAt(view.getId()); //String
				 * xxx=getParticipantInfo();
				 * //System.out.printlnprintln("feed===="+feed.getAnimTag
				 * ()+"xxx=="+feed.getThumbUrl() +"viewid=="+view.getId());
				 * //sendAnimMessage if(dialogAnimation!=null &&
				 * dialogAnimation.isShowing()) dialogAnimation.dismiss();
				 * //if(!feed.getStatus().equalsIgnoreCase("DELETE"))
				 * //quickSend(feed.getAnimTag(),feed.getThumbUrl());
				 * 
				 * 
				 * // } }
				 */
				if (dialogAnimation != null && dialogAnimation.isShowing()) {
					dialogAnimation.dismiss();
					flagAnimNotPlay = false;
				}
				quickSendForAnim(animationTagValue.get(view.getId()),
						path.toString());
				// BusinessProxy.sSelf.currentAnimData
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	private class ImageDownload extends AsyncTask<String, Void, String> {
		View imgeView;
		Bitmap bitmap;
		Object object;

		public ImageDownload(View imgeView, Object object) {
			this.imgeView = imgeView;
			this.object = object;
			// //System.out.printlnprintln("----------------------gif view------------");
		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				// //System.out.printlnprintln("----------------------gif view------------");
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();
					// if( imgeView instanceof GifDecoderView){

					((GifDecoderView) imgeView).playGif(content);
					imgeView.invalidate();// idated();

					// }
				} catch (Exception e) {

				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

//	Vector<RtAnimFeed> animVlaue;

	GifMovieView gifView;
	LinearLayout rowView = null;
	ImageView imageFrameView;

//	public void appendAllAnimantinFile() {
//		// DatabaseHandler db = new
//		// DatabaseHandler(BusinessProxy.sSelf.addPush.mContext);
//		/*
//		 * if(animationFileName!=null){ animationFileName.clear(); }
//		 * if(animationSound!=null){ animationSound.clear(); }
//		 * if(animationImageView!=null){ animationImageView.clear(); }
//		 */
//
//		if (BusinessProxy.sSelf.db == null) {
//			BusinessProxy.sSelf.db = new DatabaseHandler(
//					BusinessProxy.sSelf.addPush.mContext);
//		}
//		// Reading all contacts
//		// Log.d("Reading: ", "Reading all contacts..");
//		try {
//			animVlaue = BusinessProxy.sSelf.db.getAllAnimationFeed();
//			BusinessProxy.sSelf.currentAnimData = null;
//			if (BusinessProxy.sSelf.currentAnimData == null
//					&& animVlaue != null) {
//				BusinessProxy.sSelf.currentAnimData = new parentFeed();
//				for (RtAnimFeed valueData : animVlaue) {
//					if (!valueData.getStatus().equalsIgnoreCase("DELETE"))
//						BusinessProxy.sSelf.currentAnimData
//								.setFeedData(animVlaue);
//					String log = "getStatus: " + valueData.getStatus()
//							+ "getSetId(): " + valueData.getSetId()
//							+ " ,getGifUrl(): " + valueData.getGifUrl()
//							+ " ,getAudioUrl(): " + valueData.getAudioUrl();
//					// Writing Contacts to log
//					// //System.out.printlnprintln("log===="+valueData.getStatus()
//					// +
//					// "Tag==="+valueData.getAnimTag() +
//					// "tagid===="+valueData.getAnimTag());
//					// Log.d("Name: ", log);
//
//				}
//				animDataBaseCounter = BusinessProxy.sSelf.currentAnimData.FeedData
//						.size();
//				if (BusinessProxy.sSelf.db != null) {
//					try {
//						BusinessProxy.sSelf.db.close();
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		if (BusinessProxy.sSelf.currentAnimData != null
//				&& BusinessProxy.sSelf.currentAnimData.getFeedData() != null) {
//
//			for (int iTemp = 0; iTemp < BusinessProxy.sSelf.currentAnimData
//					.getFeedData().size(); iTemp++) {
//				RtAnimFeed feed = BusinessProxy.sSelf.currentAnimData
//						.getFeedData().elementAt(iTemp);
//				if (!feed.getStatus().equalsIgnoreCase("DELETE")) {
//
//					File file = new File(
//							Environment.getExternalStorageDirectory() + gifpath,
//							feed.getGifCaption() + "_" + feed.getSetId()
//									+ ".gif");
//					if ((file.exists() && file.length() == 0) || !file.exists()) {
//						file.delete();
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getGifCaption() + "_" + feed.getSetId());
//						task.execute(new String[] { feed.getGifUrl() });
//					}
//					file = new File(Environment.getExternalStorageDirectory()
//							+ gifpath, feed.getThumbCaption() + "_"
//							+ feed.getSetId() + ".png");
//					// //System.out.printlnprintln("getGifCaption==download======"+feed.getThumbUrl());
//					if ((file.exists() && file.length() == 0) || !file.exists()) {
//						file.delete();
//						// //System.out.printlnprintln("getGifCaption===notdownload====="+feed.getThumbUrl());
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getThumbCaption() + "_" + feed.getSetId());
//						task.execute(new String[] { feed.getThumbUrl() });
//					}
//					file = new File(Environment.getExternalStorageDirectory()
//							+ gifpath, feed.getAudioCaption() + "_"
//							+ feed.getSetId() + ".mp3");
//					if ((file.exists() && file.length() == 0) || !file.exists()) {
//
//						file.delete();
//
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getAudioCaption() + "_" + feed.getSetId());
//						task.execute(new String[] { feed.getAudioUrl() });
//					}
//					if (animationTagValue != null
//							&& !animationTagValue.contains(feed.getAnimTag())) {
//						animationFileName.add(feed.getGifCaption() + "_"
//								+ feed.getSetId() + ".gif");
//						animationSound.add(feed.getAudioCaption() + "_"
//								+ feed.getSetId() + ".mp3");
//						animationImageView.add(feed.getThumbCaption() + "_"
//								+ feed.getSetId() + ".png");
//						animationTagValue.add(feed.getAnimTag());
//					}
//				}
//
//			}
//		}
//
//	}

	private class ImageDownloadZeroSize extends AsyncTask<String, Void, String> {
		// View imgeView;
		// Bitmap bitmap ;
		// Object object;
		String FileId;

		public ImageDownloadZeroSize(String FileId) {
			this.FileId = FileId;

		}

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();
					byte[] bytes = toByteArray(content);
					File file = null;
					if (url.endsWith(".gif") || url.endsWith(".GIF"))
						file = new File(
								Environment.getExternalStorageDirectory()
										+ gifpath, FileId + ".gif");
					else if (url.endsWith(".mp3") || url.endsWith(".MP3"))
						file = new File(
								Environment.getExternalStorageDirectory()
										+ gifpath, FileId + ".mp3");
					else if (url.endsWith(".png") || url.endsWith(".PNG"))
						file = new File(
								Environment.getExternalStorageDirectory()
										+ gifpath, FileId + ".png");
					// if(url.endsWith(".jpeg")|| url.endsWith(".JPEG"))
					// file =new
					// File(Environment.getExternalStorageDirectory()+gifpath,
					// FileId+".jpeg");
					// String path=file.getPath();
					String path = Environment.getExternalStorageDirectory()
							.getAbsolutePath() + gifpath;
					// //System.out.printlnprintln("pathzero=="+file.getPath());
					boolean exists = (new File(path)).exists();
					if (!exists) {
						new File(path).mkdirs();
					}
					// if(!file.exists()){
					// //System.out.printlnprintln("writeBytesToFile=="+file.getPath());
//					ReadWriteUtill.writeBytesToFile(file, bytes);
					// }
				} catch (Exception e) {

				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

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

	Dialog dialogAnimation = null;
	Dialog fullDialog = null;
	ProgressBar progressWheele;

	public void openDialogAnimation() {
		try {
			flagAnimNotPlay = true;
			// DatabaseHandler db = new
			// DatabaseHandler(BusinessProxy.sSelf.addPush.mContext);
			// Vector<RtAnimFeed> animVlaue = db.getAllAnimationFeed();
			// //System.out.printlnprintln("BusinessProxy.sSelf.currentAnimData==="+BusinessProxy.sSelf.currentAnimData);
			// appendAllAnimantinFile();
			// System.gc();
			LayoutInflater li = LayoutInflater.from(ConversationsActivity.this);
			final View promptsView = li.inflate(R.layout.animated_listview,
					null);
			if (dialogAnimation != null && dialogAnimation.isShowing()) {
				dialogAnimation.dismiss();
				flagAnimNotPlay = false;
				if (gridView != null) {
					gridView.destroyDrawingCache();
					gridView = null;
				}
				if (mMessageAdapter != null) {
					mMessageAdapter = null;
				}
			}
			// //System.out.printlnprint("=====================done============================");
			// final Dialog dialog = new
			// Dialog(BusinessProxy.sSelf.addPush.mContext, R.style.Dialog);
			// //this is a reference to the style above
			// dialog.setContentView(R.layout.animated_listview); //I saved the
			// xml file above as yesnomessage.xml
			dialogAnimation = new Dialog(ConversationsActivity.this,
					R.style.Dialog) {
				public void onBackPressed() {
					if (dialogAnimation != null && dialogAnimation.isShowing()) {
						dialogAnimation.dismiss();
						flagAnimNotPlay = false;
						if (gridView != null) {
							gridView.destroyDrawingCache();
							gridView = null;
						}
						if (mMessageAdapter != null) {
							mMessageAdapter = null;
						}
					}
				}
			};

//			if (BusinessProxy.sSelf.currentAnimData == null
//					|| (BusinessProxy.sSelf.currentAnimData != null
//							&& BusinessProxy.sSelf.currentAnimData.FeedData != null && BusinessProxy.sSelf.currentAnimData.FeedData
//							.size() == 0))
//				return;
			dialogAnimation.setCancelable(true);
			Window window = dialogAnimation.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			window.setBackgroundDrawable(new ColorDrawable(
					android.graphics.Color.TRANSPARENT));
			window.setGravity(Gravity.TOP);
			window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
					WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
			dialogAnimation.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
							| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			// window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
			// WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

			// LayoutInflater inflater =
			// (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			Rect displayRectangle = new Rect();
			DisplayMetrics displaymetrics = new DisplayMetrics();
			window.getDecorView()
					.getWindowVisibleDisplayFrame(displayRectangle);
			LinearLayout.LayoutParams labelLayoutParams1 = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			promptsView.setLayoutParams(labelLayoutParams1);
			// promptsView.setMinimumWidth(LayoutParams.FILL_PARENT);
			// promptsView.setMinimumHeight(LayoutParams.FILL_PARENT);
			// to set the message

			((Button) promptsView.findViewById(R.id.animatedloadmore))
					.setOnClickListener(loadMoreOnClick);
			progressWheele = ((ProgressBar) promptsView
					.findViewById(R.id.progressBar1));
			progressWheele.setVisibility(View.GONE);
			// animatedloadmore
			// this.setContentView(promptsView);
			dialogAnimation.setContentView(promptsView);
			dialogAnimation.show();
			final LinearLayout gridLayout = (LinearLayout) promptsView
					.findViewById(R.animated.grideviewanimated);
			final LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT,
					(int) (displayRectangle.height() * 0.68f));
			/*
			 * if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_X_LARGE
			 * ) { labelLayoutParams = new LinearLayout.LayoutParams(
			 * LayoutParams.FILL_PARENT, (int)(displayRectangle.height() *
			 * 0.69f)); } else
			 * if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_LARGE
			 * ) { labelLayoutParams = new LinearLayout.LayoutParams(
			 * LayoutParams.FILL_PARENT, (int)(displayRectangle.height() *
			 * 0.69f)); }else
			 * if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_NORMAL
			 * ) { labelLayoutParams = new LinearLayout.LayoutParams(
			 * LayoutParams.FILL_PARENT, (int)(displayRectangle.height() *
			 * 0.69f)); } else
			 * if(Utilities.getScreenType(this)==Constants.SCREENLAYOUT_SIZE_SMALL
			 * ) { labelLayoutParams = new LinearLayout.LayoutParams(
			 * LayoutParams.FILL_PARENT, (int)(displayRectangle.height() *
			 * 0.69f)); }else{ labelLayoutParams = new
			 * LinearLayout.LayoutParams( LayoutParams.FILL_PARENT,
			 * (int)(displayRectangle.height() * 0.69f)); }
			 */

			// TODO Auto-generated method stub
			gridLayout.setLayoutParams(labelLayoutParams);
			gridView = (GridView) gridLayout.findViewById(R.animated.grideview);

			// gridView.set
			// LinearLayout.LayoutParams labelLayoutParams = new
			// LinearLayout.LayoutParams(
			// LayoutParams.FILL_PARENT,
			// LayoutParams.FILL_PARENT);//(int)(displayRectangle.height() *
			// 0.8f));
			// gridView.setLayoutParams(labelLayoutParams);

			mMessageAdapter = new MessageAdapter();

			gridView.setAdapter(mMessageAdapter);

			gridView.setOnItemClickListener(setonclick);
			// Button btnLoadMore = new Button(this);
			// btnLoadMore.setText("Load More");

			// Adding Load More button to lisview at bottom
			// gridView.addView(btnLoadMore,mMessageAdapter.getCount() );
			if (mVoiceMedia == null)
				mVoiceMedia = new VoiceMedia(ConversationsActivity.this,
						ConversationsActivity.this);

			// message.setText(otherUserRequest+" have send you friend request on RockeTalk");
			((TextView) promptsView.findViewById(R.id.animtextclose))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (dialogAnimation != null
									&& dialogAnimation.isShowing()) {
								dialogAnimation.dismiss();
								flagAnimNotPlay = false;
								if (gridView != null) {
									gridView.destroyDrawingCache();
									gridView = null;
								}
								if (mMessageAdapter != null) {
									mMessageAdapter = null;
								}
							}
						}
					});

			// add some action to the buttons
			// String num = "0123456789"
			// overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	OnClickListener loadMoreOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// //System.out.printlnprintln("Load more Url");
			LoadMoreCall();

		}
	};

	public void LoadMoreCall() {
		handler.post(new Runnable() {//
			@Override
			public void run() {
				try {
					// TODO Auto-generated method stub
//					RtAnimationEngine connectionEngine = RtAnimationEngine
//							.getInstance();
//					BusinessProxy.sSelf.animloadingFlag = false;
//					progressWheele.setVisibility(View.VISIBLE);
//					// String
//					// url="http://124.153.95.166:28080/tejas/feeds/api/animation/getAnimationList";
//					String url = "http://" + Urls.TEJAS_HOST
//							+ "/tejas/feeds/api/animation/getAnimationList";// "http://"+TEJAS_HOST+"/tejas/feeds/api/animation/getAnimationList";//"http://74.208.228.56/Android/xmlData/animXmlFromMySide.xml";
//					connectionEngine.getRtAnimation(url);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});
	}

	// public void LoadAnim(final String animValue) {
	// handler.post(new Runnable() {//
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	// try {
	// LoadAnimation task = new LoadAnimation(animValue);
	// task.execute(new String[] { "" });
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	// });
	// }

	// private class LoadAnimation extends AsyncTask<String, Void, String> {
	//
	// String animValue;
	//
	// public LoadAnimation(String animValue) {
	// // TODO Auto-generated constructor stub
	// this.animValue = animValue;
	// }
	//
	// @Override
	// protected String doInBackground(String... urls) {
	// try {
	// RtAnimationEngine connectionEngine = RtAnimationEngine
	// .getInstance();
	// String url = "http://" + Urls.TEJAS_HOST
	// + "/tejas/feeds/api/animation/getAnimation?animTag="
	// + animValue;
	//
	// // String
	// //
	// url="http://124.153.95.166:28080/tejas/feeds/api/animation/getAnimation?tagId=";
	//
	// // String
	// //
	// url="http://124.153.95.166:28080/tejas/feeds/api/animation/getAnimationList";//"http://"+TEJAS_HOST+"/tejas/feeds/api/animation/getAnimationList";//"http://74.208.228.56/Android/xmlData/animXmlFromMySide.xml";
	// if (BusinessProxy.sSelf.singleAnimationURL != null
	// && BusinessProxy.sSelf.singleAnimationURL.length() > 0)
	// url = BusinessProxy.sSelf.singleAnimationURL + animValue;
	// // //System.out.printlnprintln("LoadAnim  url============="+url);
	// connectionEngine.getRtAnimation(url);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "";
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	//
	// }
	// }

	/*
	 * private class UpdateUi extends AsyncTask<String, Void, String> {
	 * 
	 * @Override protected String doInBackground(String... urls) { String
	 * response = ""; if(urls!=null && urls.length>0) response= urls[0]; return
	 * response ; }
	 * 
	 * @Override protected void onPostExecute(String result) { try{
	 * if(result.equalsIgnoreCase(LISTBOTTOM)){
	 * //System.out.printlnprintln("-----------------update ui : "+result);
	 * listViewActivity
	 * .setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	 * if(activityAdapter!=null){ activityAdapter.getCursor().requery() ;
	 * activityAdapter.notifyDataSetChanged(); } listViewActivity.invalidate();
	 * } }catch (Exception e) {
	 * 
	 * } } }
	 */

	public void sendStatAnim(final String tag, final String tagvalue,
			final String AllViewid) {
		handler.post(new Runnable() {//
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					// //System.out.printlnprintln("sendStatAnim  url========g====="+tagvalue);
					SendState task = new SendState(tagvalue);
					task.execute(new String[] { "" });
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
	}

	private class SendState extends AsyncTask<String, Void, String> {

		String tagvalue;

		public SendState(final String tagvalue) {
			// TODO Auto-generated constructor stub
			this.tagvalue = tagvalue;
			// //System.out.printlnprintln("sendStatAnim  url========h====="+tagvalue);
		}

		@Override
		protected String doInBackground(String... urls) {
			try {
//				RtAnimationEngine connectionEngine = RtAnimationEngine.getInstance();
				// http://124.153.95.165:28080/tejas/feeds/api/animation/animationStats
				String url = "http://" + Urls.TEJAS_HOST
						+ "/tejas/feeds/api/animation/animationStats?tagId=";

				// String
				// url="http://124.153.95.166:28080/tejas/feeds/api/animation/getAnimation?tagId=";

				// String
				// url="http://124.153.95.166:28080/tejas/feeds/api/animation/getAnimationList";//"http://"+TEJAS_HOST+"/tejas/feeds/api/animation/getAnimationList";//"http://74.208.228.56/Android/xmlData/animXmlFromMySide.xml";
				if (BusinessProxy.sSelf.animStateUrl != null
						&& BusinessProxy.sSelf.animStateUrl.length() > 0)
					url = BusinessProxy.sSelf.animStateUrl;
				// //System.out.printlnprintln("sendStatAnim  url============="+url+tagvalue);
//				connectionEngine.getRtAnimationStat(url + tagvalue);
			} catch (Exception e) {

			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

//	public void openDialogFullScreen(final int Position, RtAnimFeed feed) {
//
//		if (BusinessProxy.sSelf.startRecording)
//			return;
//		/*
//		 * final Dialog dialog = new
//		 * Dialog(BusinessProxy.sSelf.addPush.mContext, R.style.Dialog); //this
//		 * is a reference to the style above
//		 * dialog.setContentView(R.layout.animationfullview); //I saved the xml
//		 * file above as yesnomessage.xml
//		 * overridePendingTransition(R.anim.slide_left_in
//		 * ,R.anim.slide_left_out); dialog.setCancelable(true);
//		 * 
//		 * Window window = dialog.getWindow();
//		 * window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		 * window.setBackgroundDrawable(new
//		 * ColorDrawable(android.graphics.Color.TRANSPARENT));
//		 */
//		LayoutInflater li = LayoutInflater.from(ConversationsActivity.this);
//		final View promptsView = li.inflate(R.layout.animationfullview, null);
//		LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(
//				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		labelLayoutParams.gravity = Gravity.CENTER;
//		// labelLayoutParams.setMargins(0, -100, 0, 0);
//
//		promptsView.setLayoutParams(labelLayoutParams);
//		if (fullDialog != null && fullDialog.isShowing()) {
//			fullDialog.dismiss();
//		}
//		// //System.out.printlnprint("=====================done============================");
//		// final Dialog dialog = new
//		// Dialog(BusinessProxy.sSelf.addPush.mContext, R.style.Dialog); //this
//		// is a reference to the style above
//		// dialog.setContentView(R.layout.animated_listview); //I saved the xml
//		// file above as yesnomessage.xml
//		fullDialog = new Dialog(ConversationsActivity.this, R.style.Dialog) {
//			public void onBackPressed() {
//				if (fullDialog != null && fullDialog.isShowing()) {
//					fullDialog.dismiss();
//				}
//			}
//		};
//
//		fullDialog.setCancelable(true);
//		Window window = fullDialog.getWindow();
//		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		window.setBackgroundDrawable(new ColorDrawable(
//				android.graphics.Color.TRANSPARENT));
//		window.setGravity(Gravity.CENTER);
//
//		LinearLayout layoutAnimFullView = (LinearLayout) promptsView
//				.findViewById(R.id.gifViewForFull);
//
//		// gifView =(GifDecoderView)promptsView.find;
//
//		ByteArrayInputStream stream = null;
//		if (feed == null) {
//			// ByteArrayInputStream stream =null;
//			try {
//
//				String path = Environment.getExternalStorageDirectory()
//						.getAbsolutePath() + gifpath;
//				File file = new File(path + animationFileName.get(Position));
//				// file.
//				// 05-23 00:26:44.613: I///System.out.println18133):
//				// file====/mnt/sdcard/RockeTalk/.GifImage/img54747_1_11_K_I_I1_dgvy03bwg8_32.gif
//				// file="/mnt/sdcard/RockeTalk/.GifImage/img54747_1_11_K_I_I1_dgvy03bwg8_32.gif";
//				if (file.exists() && file.length() > 0) {
////					byte byteData[] = ReadWriteUtill.readBytesFromFile(file);
//					stream = new ByteArrayInputStream(byteData);
//					// stream =
//					// getAssets().open(animationFileTrans.get(view.getId()));
//					// gifView.playGifLoopCountOne(stream);
//					stateValue = Position;
//					gifView = new GifMovieView(getApplicationContext(), stream);
//
//					// gifView.setLayoutParams(new
//					// GridView.LayoutParams(LayoutParams.WRAP_CONTENT,
//					// LayoutParams.WRAP_CONTENT));
//					// gifView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//					// gifView.setPadding(0, 0, 0, 0);
//					layoutAnimFullView.addView(gifView);
//
//					// playBeep(view.getId());
//					playAvailableVoice(Position, null);
//				} else {
//					if (fullDialog != null) {
//						fullDialog.dismiss();
//					}
//				}
//			} catch (Exception e) {
//				System.gc();
//				e.printStackTrace();
//			}
//		} else {
//			// ByteArrayInputStream stream =null;
//			try {
//
//				if (feed.getStatus().equalsIgnoreCase("DELETE")) {
//
//					File file = new File(
//							Environment.getExternalStorageDirectory() + gifpath,
//							feed.getGifCaption() + "_" + feed.getSetId()
//									+ "_temp" + ".gif");
//					if ((file.exists() && file.length() == 0) || !file.exists()) {
//						file.delete();
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getGifCaption() + "_" + feed.getSetId()
//										+ "_temp");
//						task.execute(new String[] { feed.getGifUrl() });
//						showAlertMessage("Info",
//								"Please wait content is downloading!", null,
//								null, null);
//						return;
//					}
//					file = new File(Environment.getExternalStorageDirectory()
//							+ gifpath, feed.getThumbCaption() + "_"
//							+ feed.getSetId() + "_temp" + ".png");
//					if ((file.exists() && file.length() == 0) || !file.exists()) {
//						file.delete();
//						// //System.out.printlnprintln("getGifCaption========"+feed.getThumbUrl());
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getThumbCaption() + "_" + feed.getSetId()
//										+ "_temp");
//						task.execute(new String[] { feed.getThumbUrl() });
//						showAlertMessage("Info",
//								"Please wait content is downloading!", null,
//								null, null);
//						return;
//					}
//					file = new File(Environment.getExternalStorageDirectory()
//							+ gifpath, feed.getAudioCaption() + "_"
//							+ feed.getSetId() + "_temp" + ".mp3");
//					if ((file.exists() && file.length() == 0) || !file.exists()) {
//
//						file.delete();
//
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getAudioCaption() + "_" + feed.getSetId()
//										+ "_temp");
//						task.execute(new String[] { feed.getAudioUrl() });
//						showAlertMessage("Info",
//								"Please wait content is downloading!", null,
//								null, null);
//						return;
//					}
//				} else {
//					File file = new File(
//							Environment.getExternalStorageDirectory() + gifpath,
//							feed.getGifCaption() + "_" + feed.getSetId()
//									+ ".gif");
//					if (file.exists() && file.length() == 0) {
//						file.delete();
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getGifCaption() + "_" + feed.getSetId());
//						task.execute(new String[] { feed.getGifUrl() });
//						showAlertMessage("Info",
//								"Please wait content is downloading!", null,
//								null, null);
//						return;
//					}
//					file = new File(Environment.getExternalStorageDirectory()
//							+ gifpath, feed.getThumbCaption() + "_"
//							+ feed.getSetId() + ".png");
//					if (file.exists() && file.length() == 0) {
//						file.delete();
//						// //System.out.printlnprintln("getGifCaption========"+feed.getThumbUrl());
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getThumbCaption() + "_" + feed.getSetId());
//						task.execute(new String[] { feed.getThumbUrl() });
//						showAlertMessage("Info",
//								"Please wait content is downloading!", null,
//								null, null);
//						return;
//					}
//					file = new File(Environment.getExternalStorageDirectory()
//							+ gifpath, feed.getAudioCaption() + "_"
//							+ feed.getSetId() + ".mp3");
//					if (file.exists() && file.length() == 0) {
//
//						file.delete();
//
//						ImageDownloadZeroSize task = new ImageDownloadZeroSize(
//								feed.getAudioCaption() + "_" + feed.getSetId());
//						task.execute(new String[] { feed.getAudioUrl() });
//						showAlertMessage("Info",
//								"Please wait content is downloading!", null,
//								null, null);
//						return;
//					}
//				}
//				// animationFileName.add(feed.getGifCaption()+"_"+feed.getSetId()+".gif");
//				// animationSound.add(feed.getAudioCaption()+"_"+feed.getSetId()+".mp3");
//				// animationImageView.add(feed.getThumbCaption()+"_"+feed.getSetId()+".png");
//
//				String path = Environment.getExternalStorageDirectory()
//						.getAbsolutePath() + gifpath;
//				File file = null;
//				if (feed.getStatus().equalsIgnoreCase("DELETE")) {
//
//					file = new File(path + feed.getGifCaption() + "_"
//							+ feed.getSetId() + "_temp" + ".gif");
//				} else {
//					file = new File(path + feed.getGifCaption() + "_"
//							+ feed.getSetId() + ".gif");
//				}
//
//				// if(file.)
//				stateValue = 0;
//				// //System.out.printlnprintln("file===="+file.toString());
//				if (file.exists() && file.length() > 0) {
//					byte byteData[] = ReadWriteUtill.readBytesFromFile(file);
//					stream = new ByteArrayInputStream(byteData);
//					// stream =
//					// getAssets().open(animationFileTrans.get(view.getId()));
//					// gifView.playGifLoopCountOne(stream);
//
//					gifView = new GifMovieView(getApplicationContext(), stream);
//					layoutAnimFullView.addView(gifView);
//					if (feed.getStatus().equalsIgnoreCase("DELETE")) {
//						playAvailableVoice(Position, feed.getAudioCaption()
//								+ "_" + feed.getSetId() + "_temp" + ".mp3");
//					} else {
//						playAvailableVoice(Position, feed.getAudioCaption()
//								+ "_" + feed.getSetId() + ".mp3");
//					}
//				} else {
//					if (fullDialog != null) {
//						fullDialog.dismiss();
//					}
//				}
//				// playBeep(view.getId());
//
//			} catch (Exception e) {
//				System.gc();
//				e.printStackTrace();
//			}
//
//			sendStatAnim(null, "" + feed.getSetId(), null);
//		}
//		// View gifdata=view.getTag();
//		// rowView.removeViewAt(view.getId());
//
//		BusinessProxy.sSelf.animPlay = true;
//		fullDialog.setContentView(promptsView);
//		// overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
//		fullDialog.show();
//
//		/*
//		 * if(feed==null) new Thread(new Runnable() {
//		 * 
//		 * @Override public void run() {
//		 * 
//		 * // TODO Auto-generated method stub
//		 * if(BusinessProxy.sSelf.currentAnimData!=null &&
//		 * BusinessProxy.sSelf.currentAnimData.getFeedData()!=null){
//		 * 
//		 * RtAnimFeed
//		 * feed=BusinessProxy.sSelf.currentAnimData.getFeedData().elementAt
//		 * (Position);
//		 * //System.out.printlnprintln("===view====onitem"+feed.getSetId());
//		 * sendStatAnim(null,""+feed.getSetId(),null); } } }).start();
//		 */
//
//	}

	public Drawable getPicsFromSdcard(String imageUrl) {
		Drawable mDrawable = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					+ gifpath, imageUrl);

			if (file.exists()) {

				mDrawable = Drawable.createFromPath(file.getAbsolutePath());

			}
		} catch (OutOfMemoryError e) {
			// TODO: handle exception

			System.gc();
			return null;
		}
		return mDrawable;
	}

	private Bitmap decodeFile(File f) {
		try {
			// Decode image size

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	private Bitmap decodeFile(String imageUrl) {
		try {
			// Decode image size

			File f = new File(Environment.getExternalStorageDirectory()
					+ gifpath, imageUrl);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 70;

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {

		}
		return null;
	}

	public Drawable getPicsFromSdcard(File file) {

		// File file = //new
		// File(Environment.getExternalStorageDirectory()+gifpath, imageUrl);
		Drawable mDrawable = null;
		if (file.exists()) {
			// //System.out.printlnprintln("  file : "+file.getAbsolutePath() );
			mDrawable = Drawable.createFromPath(file.getAbsolutePath());// file.getAbsolutePath());

		}
		return mDrawable;

	}

	Vector<String> readIds = new Vector<String>();
	int readCount = 0;
	Thread checkThreah;

	public void checkIsView(Message message) {
		if (message.hasBeenViewed != null
				&& message.hasBeenViewed.equalsIgnoreCase("false")
				&& !message.mfuId.equalsIgnoreCase("-1")
				&& !message.mfuId.equalsIgnoreCase("-999")) {

			if (!readIds.contains((message.messageId))) {
				readCount++;
				readIds.add(message.messageId);
				// setValuesForReadMessage(message.mfuId);
				// ---------------------Old code
				setValuesForReadMessage(message.messageId); // ---------------------New
															// Code
			}

			// if(readIds.size()>30){
			// if(checkThreah==null || !checkThreah.isAlive()){
			// checkThreah= new Thread(new Runnable() {
			//
			// @Override
			// public void run() {
			// setUnreadCount();
			// }
			// });
			// checkThreah.start();
			// }
			// }
		}

		// //System.out.printlnprintln("message.hasBeenViewed"+message.hasBeenViewed);
		// //System.out.printlnprintln("message.hasBeenViewedBySIP"+message.hasBeenViewedBySIP);
		//
		// if(message.hasBeenViewed!= null &&
		// message.hasBeenViewed.equalsIgnoreCase("true")){
		// ContentValues values = new ContentValues();
		// values.put(MessageConversationTable.HAS_BEEN_VIEWED, "true");// int
		// int updatedRow = getContentResolver().update(
		// MediaContentProvider.CONTENT_URI_INBOX,
		// values,
		// MessageConversationTable.CONVERSATION_ID + "=? and " +
		// MessageConversationTable.MESSAGE_ID+" =?",
		// new String[] { getIntent().getStringExtra(
		// Constants.CONVERSATION_ID),message.messageId });
		//
		// Cursor cursor2 =
		// getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
		// null,
		// MessageConversationTable.CONVERSATION_ID+" = ? ", new String[]
		// {message.conversationId}, null);
		// int ci =
		// cursor2.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT)
		// ;
		//
		// if(ci!= -1 && cursor2.getCount()>0){
		// cursor2.moveToFirst() ;
		// int unreadMsgcount = cursor2.getInt(ci);
		// unreadMsgcount = unreadMsgcount -1 ;
		// if(unreadMsgcount<0)
		// unreadMsgcount = 0 ;
		// ////
		// //System.out.printlnprintln("Feed Synch -----unreadMsgcount :"+unreadMsgcount
		// +" : message.conversationId :"+message.conversationId);
		// cursor2.close() ;
		// values = new ContentValues();
		// values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT,
		// unreadMsgcount);
		// updatedRow =
		// getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
		// values,MessageConversationTable.CONVERSATION_ID
		// +"= ? and "+MessageConversationTable.USER_ID+" =?",new String[]
		// {message.conversationId,""+BusinessProxy.sSelf.getUserId()});
		// setValuesForReadMessage(message.messageId) ;
		// }
		// cursor2.close() ;
		// }
	}

	public void sendReadMessageRequest() {
		if (BusinessProxy.messageIDString == null
				|| BusinessProxy.messageIDString.length() <= 0) {
			return;
		}
		if (Logger.ENABLE)
			Logger.debug(TAG, "--sendReadMessageRequest()::[FINE] Entry");
		try {
			String readMessageString = null;
			readMessageString = BusinessProxy.messageIDString.toString();
			// //System.out.printlnprintln("--------------BusinessProxy.messageIDString. SENT :"+readMessageString);
			OutboxTblObject reqObj = new OutboxTblObject(1,
					Constants.FRAME_TYPE_INBOX_REFRESH, Constants.MSG_OP_SET);
			if (readMessageString.length() > 120) {
				readMessageString = readMessageString.substring(0, 120);
				readMessageString = readMessageString.substring(0,
						readMessageString.lastIndexOf(';'));
			}

			reqObj.mMessageIDString = readMessageString;
			int ret = BusinessProxy.sSelf.sendToTransport(reqObj);
			if (ret == Constants.ERROR_NONE) {
				// method_MSG_READ_call("",BusinessProxy.messageIDString.toString());
				BusinessProxy.messageIDString = null;
				// BusinessProxy.iRefreshPeriod = Constants.IM_REFRESH_TIME;
			} else if (ret != Constants.ERROR_NONE) {
				if (Logger.ENABLE)
					Logger.warning(
							TAG,
							"--sendReadMessageRequest()--[WARNING] -- ****ERROR SENDING REQUEST****. RETURN VALUE = "
									+ ret);
			}
		} catch (Exception ex) {
			if (Logger.ENABLE)
				Logger.error(TAG, "--sendReadMessageRequest()::[ERROR] --  "
						+ ex.toString(), ex);
		}
	}

	// Manoj Singh
	// Date 17-06-2015
	// [--Start
	private String postDataOnServer(String url, String msgId) {
		// url =
		// "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings";
		// http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/pushnotif/settings?groupName=pkrpriv&op={TURNOFF
		// or TURNON}
		// Creating HTTP client
		HttpClient httpClient = new DefaultHttpClient();
		// Creating HTTP Post
		HttpPost httpPost = new HttpPost(url);
		// key and value pair
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		msgId = msgId.replaceAll(";", ",");
		nameValuePair.add(new BasicNameValuePair("messageIds", msgId));
		// nameValuePair.add(new BasicNameValuePair("op", op));//{TURNOFF or
		// TURNON}
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
			if (responceCode == 200) {
				// InputStream ins = response.getEntity().getContent();
				String strResponse = EntityUtils.toString(response.getEntity());
				// //System.out.printlnprintln("DATA = "+IOUtils.read(ins));
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

	public void sendReadReceipt(final String url, final String msgID) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final String responce = postDataOnServer("http://"
						+ Urls.TEJAS_HOST + "/tejas/feeds/message/markread",
						msgID);
				Log.w(TAG, "sendReadReceipt :: msgID : " + msgID);
				ConversationsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (responce != null && responce.contains("success")) {
							try {
								JSONObject jobj = new JSONObject(responce);
								if (responce.contains("desc")) {
									String desc = jobj.optString("desc")
											.toString();
									// showSimpleAlert(getString(R.string.app_name),
									// desc);
									// if(desc != null && desc.length() > 0)
									// Toast.makeText(ConversationsActivity.this,
									// desc, Toast.LENGTH_SHORT).show();
								}
							} catch (Exception e) {
							}
						}

					}
				});
			}
		}).start();
	}

	// --END]

	private void setValuesForReadMessage(String messageId) {
		// Send Values For read message.
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
			if (BusinessProxy.messageIDString.toString().split(";").length > 10) {
				// sendReadMessageRequest();//Old
				updateReadMessagereceiptToDBAndServer();// New
			}
			currentMessageId = null;
		}
	}

	long idelTime = 0;
	int lastVisibleItem = 0;
	boolean isScrollingDown = false;

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		idelTime = System.currentTimeMillis();
		if ((arg0.getCount() - 2) <= arg0.getLastVisiblePosition()) {
			totalNew = 0;
			idelTime = 0;
			new_message.setText("");
			new_message.setVisibility(View.GONE);
			new_message_layout.setVisibility(View.GONE);
		}

		if (visibleItemCount == 0)
			ImageDownloader.idel = true;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			idel = true;
			ImageDownloader.idel = true;
			// System.out
			// .println("----onScrollStateChanged----idel-idel :" + idel);

			if (idel) {
				if (listViewActivity != null) {
					listViewActivity.postDelayed(new Runnable() {

						@Override
						public void run() {
							if (idel)
								fetchImage();
						}
					}, 500);
				}
			}
		} else {
			ImageDownloader.idel = false;
			idel = false;
			// System.out
			// .println("----onScrollStateChanged----idel-idel :" + idel);
		}
	}

	private void fetchImage() {

		// if(1==1)return ;
		boolean s = false;
		if (listViewActivity != null && idel) {
			int firstPosition = listViewActivity.getFirstVisiblePosition()
					- listViewActivity.getHeaderViewsCount(); // This is the
																// same as child
																// #0
			for (int i = 0; i <= firstPosition + visibleItemCount; i++) {
				if (listViewActivity != null) {
					View convertView = listViewActivity.getChildAt(i);
					if (convertView != null) {
						MessageViewHolder messageViewHolder = (MessageViewHolder) convertView
								.getTag();

						if (messageViewHolder != null
								&& messageViewHolder.message != null) {
							int chdount = messageViewHolder.images
									.getChildCount();
							if (chdount > 0) {
								for (int j = 0; j < messageViewHolder.images
										.getChildCount(); j++) {
									if (messageViewHolder.images.getChildAt(j) instanceof ImageView) {
										CImageView cImageView = (CImageView) messageViewHolder.images
												.getChildAt(j);
										ImageDownloader imageManager = new ImageDownloader(
												2);
										imageManager.handler = handler;
										imageManager.download(
												cImageView.getImageUrl(),
												cImageView,
												ConversationsActivity.this,
												ImageDownloader.TYPE_IMAGE);
									}
								}
							}
							chdount = messageViewHolder.imagesRight
									.getChildCount();
							if (chdount > 0) {
								for (int j = 0; j < messageViewHolder.imagesRight
										.getChildCount(); j++) {
									if (messageViewHolder.imagesRight
											.getChildAt(j) instanceof ImageView) {
										CImageView cImageView = (CImageView) messageViewHolder.imagesRight
												.getChildAt(j);
										if (cImageView.getImageUrl() != null
												&& cImageView.getImageUrl()
														.startsWith("http")) {
											ImageDownloader imageManager = new ImageDownloader(
													2);
											imageManager.handler = handler;
											imageManager.download(
													cImageView.getImageUrl(),
													cImageView,
													ConversationsActivity.this,
													ImageDownloader.TYPE_IMAGE);
										}
									}
								}
							}
							// //
							if (messageViewHolder.me_image.getImageUrl() != null) {
								ImageDownloader imageManager = new ImageDownloader(
										2);
								imageManager.handler = handler;
								try {
									if (messageViewHolder.message.participantInfoClazz != null
											&& messageViewHolder.message.participantInfoClazz.userName != null)
										imageManager
												.download(
														messageViewHolder.message.participantInfoClazz.userName,
														messageViewHolder.me_image,
														ConversationsActivity.this,
														ImageDownloader.TYPE_THUMB_BUDDY);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							//
							if (messageViewHolder.otherImageRight.getImageUrl() != null) {
								try {
									ImageDownloader imageManager = new ImageDownloader(
											2);
									imageManager.handler = handler;
									if (messageViewHolder.message.participantInfoClazz != null
											&& messageViewHolder.message.participantInfoClazz.userName != null)
										imageManager
												.download(
														messageViewHolder.message.participantInfoClazz.userName,
														messageViewHolder.me_image,
														ConversationsActivity.this,
														ImageDownloader.TYPE_THUMB_BUDDY);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							// /
							chdount = messageViewHolder.video.getChildCount();
							if (chdount > 0) {
								for (int j = 0; j < messageViewHolder.video
										.getChildCount(); j++) {
									if (messageViewHolder.video.getChildAt(j) instanceof LinearLayout) {
										LinearLayout ln = (LinearLayout) messageViewHolder.video
												.getChildAt(j);
										for (int k = 0; k < ln.getChildCount(); k++) {
											if (ln.getChildAt(k) instanceof CImageView) {
												CImageView cImageView = (CImageView) ln
														.getChildAt(k);
												if (cImageView.getImageUrl() != null
														&& cImageView
																.getImageUrl()
																.startsWith(
																		"http")) {
													ImageDownloader imageManager = new ImageDownloader(
															2);
													imageManager.handler = handler;
													// System.out.printlnprintln("-------videoLeft.getImageUrl()-----"+cImageView.getImageUrl());
													imageManager
															.download(
																	cImageView
																			.getImageUrl(),
																	cImageView,
																	ConversationsActivity.this,
																	ImageDownloader.TYPE_VIDEO);
												}
											}
										}

									}
								}
							}
							// //
							// /
							chdount = messageViewHolder.videoRight
									.getChildCount();
							if (chdount > 0) {
								for (int j = 0; j < messageViewHolder.videoRight
										.getChildCount(); j++) {
									if (messageViewHolder.videoRight
											.getChildAt(j) instanceof LinearLayout) {
										LinearLayout ln = (LinearLayout) messageViewHolder.videoRight
												.getChildAt(j);
										for (int k = 0; k < ln.getChildCount(); k++) {
											if (ln.getChildAt(k) instanceof CImageView) {
												CImageView cImageView = (CImageView) ln
														.getChildAt(k);
												if (cImageView.getImageUrl() != null
														&& cImageView
																.getImageUrl()
																.startsWith(
																		"http")) {
													ImageDownloader imageManager = new ImageDownloader(
															2);
													imageManager.handler = handler;
													// System.out.printlnprintln("-------videoRight.getImageUrl()-----"+cImageView.getImageUrl());
													imageManager
															.download(
																	cImageView
																			.getImageUrl(),
																	cImageView,
																	ConversationsActivity.this,
																	ImageDownloader.TYPE_VIDEO);
												}
											}
										}

									}
								}
							}
							// //
						}
					}
				}
			}

		}
	}

	View attachment_panel;

	public void initFriendMessage() {
		// //System.out.printlnprintln("-----initFriendMessage");
		setContentView(R.layout.conversations_screen);
		LinearLayout d = ((LinearLayout) findViewById(R.id.media_play_layout));
		d.setVisibility(View.GONE);
		//wakeLock.release();
		attachment_panel = findViewById(R.id.attachment_panel);
		attachment_panel.setVisibility(View.INVISIBLE);
		findViewById(R.id.prev).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		findViewById(R.id.attachement).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (attachment_panel.getVisibility() != View.VISIBLE) {
							attachment_panel.setVisibility(View.VISIBLE);
							// For talking pic layout shifting
							((RelativeLayout) findViewById(R.id.relativeLayout22))
									.setVisibility(View.INVISIBLE);
							((ImageView) findViewById(R.id.audio_file))
									.setBackgroundResource(R.drawable.icon_doodle);
							((TextView) findViewById(R.id.name_audio))
									.setText(getResources().getString(
											R.string.doodle));
							((ImageView) findViewById(R.id.talking_pic))
									.setBackgroundResource(R.drawable.icon_audio);
							((TextView) findViewById(R.id.name_talking_pic))
									.setText(getResources().getString(
											R.string.audio));
							// ///////////

							Utilities.startAnimition(
									attachment_panel.getContext(),
									attachment_panel, R.anim.grow_from_top);
							((ImageView) v)
									.setImageResource(R.drawable.ic_action_attach_2);
						} else {
							attachment_panel.setVisibility(View.INVISIBLE);
							Utilities.startAnimition(
									attachment_panel.getContext(),
									attachment_panel, R.anim.shrink_from_bottom);
							((ImageView) v)
									.setImageResource(R.drawable.attachment_unsel);
						}
					}
				});

		messageBox = (EmojiconEditText) findViewById(R.community_chat.msgBox);
		if ((getIntent().getStringExtra("MessageText") != null)) {
			messageBox.setText(getIntent().getStringExtra("MessageText"));
		}
		smileyView = (FrameLayout) findViewById(R.id.emojicons);
		// messageBox.addTextChangedListener(new TextWatcherAdapter() {
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		// mTxtEmojicon.setText(s);
		// }
		// });

		setEmojiconFragment(false);

		nonFriendMessage = false;
		// GridView gridView = (GridView) findViewById(R.id.grid);
		// gridView.setAdapter(new ImageAdapter(this, imageIDs));
		// gridView.setNumColumns(imageIDs.length);
		PackageManager pm = getPackageManager();
		frontCam = pm.hasSystemFeature("android.hardware.camera.front");

		loadingChat = (ProgressBar) findViewById(R.conversation.progressBar);
		load_prevoius_message2 = (TextView) findViewById(R.conversation.load_prevoius_message2);
		new_message = (TextView) findViewById(R.conversation.new_message);
		new_message_layout = findViewById(R.chat_panel.info_new_message);
		new_message_info = (TextView) findViewById(R.chat_panel.new_message_info);

		// info_new_message
		new_message.setOnClickListener(this);
		new_message_layout.setOnClickListener(this);
		rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
		imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				return gestureDetector.onTouchEvent(event);
			}
		};
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View emoticon_screen = inflator.inflate(R.layout.emoticon_screen, null);
		GridView gridView = (GridView) emoticon_screen.findViewById(R.id.grid);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(new ImageAdapter(this, Utilities.imageIDs1));
		// gridView.setOnTouchListener(gestureListener);
		imageFrame.addView(emoticon_screen, 0);

		/*
		 * emoticon_screen = inflator.inflate(R.layout.emoticon_screen, null);
		 * gridView = (GridView) emoticon_screen.findViewById(R.id.grid);
		 * gridView.setOnItemClickListener(this); gridView.setAdapter(new
		 * ImageAdapter(this, Utilities.imageIDs2));
		 * gridView.setOnTouchListener(gestureListener);
		 * imageFrame.addView(emoticon_screen, 1);
		 * 
		 * emoticon_screen = inflator.inflate(R.layout.emoticon_screen, null);
		 * gridView = (GridView) emoticon_screen.findViewById(R.id.grid);
		 * gridView.setOnItemClickListener(this); gridView.setAdapter(new
		 * ImageAdapter(this, Utilities.imageIDs3));
		 * gridView.setOnTouchListener(gestureListener);
		 * imageFrame.addView(emoticon_screen, 2);
		 */
		messageBox.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (dialogAnimation != null && dialogAnimation.isShowing()) {
					dialogAnimation.dismiss();
					flagAnimNotPlay = false;
				}
				return false; // consume touch even
			}
		});

		// imageFrame.setOnClickListener(this);
		// imageFrame.setOnTouchListener(gestureListener);
		// scrollView.setOnTouchListener(gestureListener);
		// emoticon_screen = inflator.inflate(R.layout.emoticon_screen, null);
		// gridView = (GridView)emoticon_screen. findViewById(R.id.grid);
		// gridView.setAdapter(new ImageAdapter(this, imageIDs));
		// gridView.setNumColumns(imageIDs.length);
		// imageFrame.addView(emoticon_screen, 1);

		navi = (GalleryNavigator) findViewById(R.id.count);
		navi.setSize(3);
		navi.setPosition(0);
		navi.setLoadedSize(0);
		navi.invalidate();

		// conversations_screen_frame_layout = (FrameLayout)
		// findViewById(R.id.conversations_screen_frame_layout);
		// conversations_option_frame_layout = (FrameLayout)
		// findViewById(R.id.conversations_option_frame_layout);

		media_play_layout = (LinearLayout) findViewById(R.id.media_play_layout);
		listViewActivity = (ListView) findViewById(R.conversations.landing_discovery_activity_list);
		// listViewActivity.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// //System.out.printlnprintln("list onTouch");
		// return false;
		// }
		// });
		activityAdapter = new ConversationsAdapter(this, getCursor(), true,
				this);
		// if(count>0)
		listViewActivity.setOnScrollListener(this);
		listViewActivity.setAdapter(activityAdapter);
		image_count = ((TextView) findViewById(R.id.image_count));
		image_count_dodle = ((TextView) findViewById(R.id.image_count_dodle));
		updateParticepentLabel();
		TextView imgStop = (TextView) findViewById(R.id.media_stop_play);
		imgStop.setOnClickListener(playerClickEvent);
		mVoiceMedia = new VoiceMedia(this, this);

		CheckNewDataOnServer task = new CheckNewDataOnServer();
		task.execute(new String[] { "load" });

		findViewById(R.chat_panel.more_emo_prev).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						imageFrame.setInAnimation(inFromRightAnimation());
						imageFrame.setOutAnimation(outToLeftAnimation());
						imageFrame.showPrevious();
						navi.setPosition(imageFrame.getDisplayedChild());
					}
				});
		findViewById(R.chat_panel.more_emo_next).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {

						imageFrame.setInAnimation(inFromRightAnimation());
						imageFrame.setOutAnimation(outToLeftAnimation());
						imageFrame.showNext();
						navi.setPosition(imageFrame.getDisplayedChild());
					}
				});
		findViewById(R.chat_panel.smiley).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (dialogAnimation != null
								&& dialogAnimation.isShowing()) {
							dialogAnimation.dismiss();
							flagAnimNotPlay = false;
						}
						if (chat_panel_more_option_emo.getVisibility() == View.GONE) {
							chat_panel_more_option_emo
									.setVisibility(View.VISIBLE);
						} else {
							chat_panel_more_option_emo.setVisibility(View.GONE);
						}
					}
				});

		// appendAllAnimantinFile();
		// this.onResume();
	}

	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(messageBox, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	public void hideSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(messageBox.getWindowToken(), 0);
		}
	}

	private void setEmojiconFragment(boolean useSystemDefault) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.emojicons,
						EmojiconsFragment.newInstance(useSystemDefault))
				.commit();
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		EmojiconsFragment.input(messageBox, emojicon);
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		EmojiconsFragment.backspace(messageBox);
	}

	public void removeMeIfILeft() {
		if (conversationList != null && conversationList.isLeft == 1) {
			String sst = null;
			try {
				// Cursor query(Uri uri, String[] projection, String selection,
				// String[] selectionArgs, String sortOrder)
				Cursor cursor = getContentResolver()
						.query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								null,
								MessageTable.CONVERSATION_ID + " = ? and "
										+ MessageTable.USER_ID + " = ?",
								new String[] {
										getIntent().getStringExtra(
												Constants.CONVERSATION_ID),
										BusinessProxy.sSelf.getUserId() + "" },
								null);
				cursor.moveToFirst();

				sst = cursor.getString(cursor
						.getColumnIndex(MessageTable.PARTICIPANT_INFO));
				cursor.close();

				String userNAme = SettingData.sSelf.getUserName();
				String searchs1 = ("false" + Constants.COL_SEPRETOR + userNAme)
						.toLowerCase();
				String searchs2 = ("true" + Constants.COL_SEPRETOR + userNAme)
						.toLowerCase();
				// String sst
				// ="false�amita�Amita�Tandon�false�forgot�For�Got�true�mamtgupta�Mamt�Gupta�false�tariqfzb�Mohammad�Tariq�false�spider�Spi�Der�false�zeenat�Zeenat�Khan�false�adgdgj�adg�dgj�false�aniljha�anil�jha�false�wq���"
				// ;

				String oriS = sst;
				sst = sst.toLowerCase();

				if (sst.indexOf(searchs1) != -1) {
					// //System.out.printlnprintln("----search 1------");
					// //System.out.printlnprintln(sst.indexOf(searchs1));
					String part1 = sst.substring(0, sst.indexOf(searchs1));
					// //System.out.printlnprintln(part1);
					String part2 = sst.substring(sst.indexOf(searchs1),
							sst.length());
					// //System.out.printlnprintln(part2);
					String part3 = part2.substring(
							part2.indexOf(Constants.ROW_SEPRETOR) + 1,
							part2.length());
					// //System.out.printlnprintln(part3);
					sst = part1 + part3;
					// //System.out.printlnprintln(sst);
					// //System.out.printlnprintln(part1+part3);
				} else if (sst.indexOf(searchs2) != -1) {
					// //System.out.printlnprintln("----search 2------");
					// //System.out.printlnprintln(sst.indexOf(searchs2));
					String part1 = oriS.substring(0, sst.indexOf(searchs2));
					// //System.out.printlnprintln(part1);
					String part2 = oriS.substring(sst.indexOf(searchs2),
							sst.length());
					// //System.out.printlnprintln(part2);
					String part3 = part2.substring(
							part2.indexOf(Constants.ROW_SEPRETOR) + 1,
							part2.length());
					// //System.out.printlnprintln(part3);

					// //System.out.printlnprintln(sst);
					// //System.out.printlnprintln(part1+part3);
					sst = part1 + part3;
				}

				// //System.out.printlnprintln("------------sst--- : "+sst);

				ContentValues values = new ContentValues();
				values.put(MessageConversationTable.IS_LEFT, 1);
				if (sst != null)
					values.put(MessageConversationTable.PARTICIPANT_INFO, sst);
				int updatedRow = getContentResolver()
						.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								values,
								MessageConversationTable.CONVERSATION_ID
										+ "= ? and "
										+ MessageConversationTable.USER_ID
										+ " =?",
								new String[] {
										getIntent().getStringExtra(
												Constants.CONVERSATION_ID),
										"" + BusinessProxy.sSelf.getUserId() });
				// //System.out.printlnprintln("------------sst---updated rod : "+updatedRow);
				cursor = getContentResolver()
						.query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								null,
								MessageTable.CONVERSATION_ID + " = ? and "
										+ MessageTable.USER_ID + " = ?",
								new String[] {
										getIntent().getStringExtra(
												Constants.CONVERSATION_ID),
										BusinessProxy.sSelf.getUserId() + "" },
								null);
				cursor.moveToLast();
				conversationList = CursorToObject.conversationListObject(
						cursor, ConversationsActivity.this);

				cursor.close();

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	boolean force = false;
	private class CheckNewDataOnServer extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			try {
//				Thread.sleep(1000);
				force = false;
				if (((FeedRequester.feedTaskConversationMessages == null || FeedRequester.feedTaskConversationMessages
						.getStatus() == Status.FINISHED) && !getIntent()
						.getStringExtra(Constants.CONVERSATION_ID).startsWith("NP"))
						&& !cidRefresh.contains(getIntent().getStringExtra(Constants.CONVERSATION_ID))) {
					handler.post(new Runnable() {

						@Override
						public void run() {
							
							try{
								loadingChat.setVisibility(View.VISIBLE);
								load_prevoius_message2.setOnClickListener(ConversationsActivity.this);
							load_prevoius_message2.setText(getResources().getString(R.string.checking_conversation));
							load_prevoius_message.setVisibility(View.VISIBLE);
							}catch(Exception e){
								
							}
							
						}
					});
					boolean notRunnuming = false;
					if (FeedRequester.feedTaskMessageList != null) {
						FeedRequester.feedTaskMessageList.cancel(true);
						FeedRequester.feedTaskMessageList.refreshCancel = true;
						FeedRequester.feedTaskMessageList = null;
					}
					notRunnuming = true;
					handler.post(new Runnable() {
						@Override
						public void run() {
							FeedRequester.requestConversationMessages(ConversationsActivity.this, 
									getIntent().getStringExtra(Constants.CONVERSATION_ID), ConversationsActivity.this, force);
						}
					});
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				Thread.sleep(1000);
				if (FeedRequester.feedTaskConversationMessages != null)
					FeedRequester.feedTaskConversationMessages.setHttpSynch(ConversationsActivity.this);
				if (FeedRequester.feedTaskConversationMessages != null
						&& FeedRequester.feedTaskConversationMessages.getStatus() != Status.FINISHED) {
					load_prevoius_message.setVisibility(View.VISIBLE);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class CheckMoreDataOnServer extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			try {
				// Thread.sleep(1000) ;

				boolean notRunnuming = false;
				do {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						// TODO: handle exception
					}

					if ((FeedRequester.feedTaskMessageList != null && FeedRequester.feedTaskMessageList
							.getStatus() == Status.RUNNING)) {
						FeedRequester.feedTaskMessageList.cancel(true);
						notRunnuming = true;
						// //System.out.printlnprintln("singalton-----2-----------feedTaskMessageList running-");
					} else {
						notRunnuming = false;
						// //System.out.printlnprintln("singalton------2----------feedTaskMessageList not running-");
					}
				} while (notRunnuming);
				handler.post(new Runnable() {

					@Override
					public void run() {
						FeedRequester.requestConversationMessages(
								ConversationsActivity.this,
								getIntent().getStringExtra(
										Constants.CONVERSATION_ID),
								ConversationsActivity.this, force);
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

	private class UpdateUi extends AsyncTask<String, Void, String> {

		public Vector<String> mPicturePath = new Vector<String>();
		private Vector<String> mPicturePathCanvas = new Vector<String>();

		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			if (urls != null && urls.length > 0)
				response = urls[0];
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (result.equalsIgnoreCase(LISTBOTTOM)) {
					// //System.out.printlnprintln("-----------------update ui : "+result);
					listViewActivity
							.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
					if (activityAdapter != null) {
						activityAdapter.getCursor().requery();
						activityAdapter.notifyDataSetChanged();
						// Manoj change for scrolling
						listViewActivity.post(new Runnable() {
							@Override
							public void run() {
								
								int idx = activityAdapter.getCursor()
										.getCount();
								listViewActivity.setSelection(idx);
								Log.v("ConverstionsActivity","Manoj"+"14744");
								listViewActivity.setStackFromBottom(true);
							}
						});
					}
					listViewActivity.invalidate();
					final Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
					    @Override
					    public void run() {
					        // Do something after 5s = 5000ms
					      //  buttons[inew][jnew].setBackgroundColor(Color.BLACK);
					    	//Method for handle outside data here
					    	shareOuterData();
					    }
					}, 200);
					
			         
					
				}
				if (result.equalsIgnoreCase(INVALIDATE_LIST)) {

					// if(inageViewVec != null && inageViewVec.size()>0)
					// {
					// //System.out.printlnprintln("-------invalidat size "+inageViewVec.size());
					// for (int i = 0; i < inageViewVec.size(); i++) {
					// try{
					// //System.out.printlnprintln("-------invalidat "+i);
					// inageViewVec.get(i).invalidate() ;
					// }catch (Exception e) {
					// e.printStackTrace() ;
					// }
					// }
					// }
					// //System.out.printlnprintln("-----------------update ui : "+result);

					// int pos = listViewActivity.getScrollY();
					// int top = listViewActivity.getSelectedItemPosition();

					// if (activityAdapter != null) {
					// activityAdapter.getCursor().requery();
					// activityAdapter.notifyDataSetChanged();
					// }
					listViewActivity.invalidate();

					// //System.out.printlnprintln("------------pos top"+pos+":"+top);
					// if(listViewActivity!=null)
					// {
					// listViewActivity.setSelection(pos) ;
					// listViewActivity.setSelectionFromTop(pos, top);
					// }
					return;
				}
				if (result.equalsIgnoreCase(SEND_MESSAGE)) {
					// //System.out.printlnprintln("-----------------update ui : "+result);
					quickSend(null, null, mPicturePath);
				}

				if (result.equalsIgnoreCase(UPDATE_ON_RESPONCE)) {

					if (responseStr != null
							&& responseStr
									.equalsIgnoreCase(Constants.CHECKING_DONE)) {
						UpdateUi task = new UpdateUi();
						task.execute(new String[] { LISTBOTTOM });
						if (Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID) {
							// if(!cidRefresh.contains(getIntent().getStringExtra(
							// Constants.CONVERSATION_ID)))
							// cidRefresh.add(getIntent().getStringExtra(
							// Constants.CONVERSATION_ID));

							// //System.out.printlnprintln(" on post execute inserted:"+cidRefresh.contains(getIntent().getStringExtra(
							// Constants.CONVERSATION_ID)));
							load_prevoius_message.setVisibility(View.GONE);

						}
						return;
					}

					// handler.post(new Runnable() {
					// @Override
					// public void run() {
					try {

						if (!CONVERSATIONID.equalsIgnoreCase(getIntent()
								.getStringExtra(Constants.CONVERSATION_ID))/*
																			 * &&
																			 * !
																			 * responseStr
																			 * .
																			 * equalsIgnoreCase
																			 * (
																			 * "REFRESH VIEW FEOM COMPOSE"
																			 * )
																			 */) {
							getIntent().putExtra(Constants.CONVERSATION_ID,
									CONVERSATIONID);
							CONVERSATIONID = getIntent().getStringExtra(
									Constants.CONVERSATION_ID);
							initParticipantInfo();
							updateParticepentLabel();
							// stopManagingCursor(activityAdapter.getCursor());

							// listViewActivity = (ListView)
							// findViewById(R.conversations.landing_discovery_activity_list);
							activityAdapter = new ConversationsAdapter(
									ConversationsActivity.this, getCursor(),
									true, ConversationsActivity.this);
							listViewActivity.setAdapter(activityAdapter);
							return;
						}

						if (((System.currentTimeMillis() - idelTime) > 2000
								&& responseStr != null && responseStr
								.indexOf("REFRESH") != -1)
								|| FeedTask.UPDATE_ME
								|| ComposeService.refreshList)
						// if(FeedTask.UPDATE_ME || ComposeService.refreshList)
						{
							// //System.out.printlnprintln("------onResponseSucess--"+responseStr+"   :f "+FeedTask.refreshList);
							// //System.out.printlnprintln("------onResponseSucess--"+responseStr+"   :c "+ComposeService.refreshList);
							int oldCount = activityAdapter.getCount();
							// //System.out.printlnprintln("------onResponseSucess--old count:"+activityAdapter.getCount());
							activityAdapter.getCursor().requery();
							int newCount = activityAdapter.getCount();
							// //System.out.printlnprintln("------onResponseSucess--new count:"+activityAdapter.getCount());
							// activityAdapter.getCursor().moveToLast();

							int old = activityAdapter.getCount();
							activityAdapter.notifyDataSetInvalidated();
							if (idelTime > 0
									&& requestForID != Constants.FEED_GET_CONVERSATION_MESSAGES) {
								FeedTask.KEEP_POSITION = true;
								totalNew = totalNew + (newCount - oldCount);
								if (totalNew > 0) {
									new_message.setText("" + totalNew);
									if (FeedTask.latestMessageCurrentCID != null) {
										String s = "";
										if (FeedTask.latestMessageCurrentCID.msgTxt != null)
											s = FeedTask.latestMessageCurrentCID.msgTxt;
										new_message_info
												.setText(FeedTask.latestMessageCurrentCID.msgTxt);

										new_message.setVisibility(View.VISIBLE);
										new_message_layout
												.setVisibility(View.VISIBLE);
										if (FeedTask.latestMessageCurrentCID.image_content_urls != null
												&& FeedTask.latestMessageCurrentCID.image_content_urls
														.trim().length() > 0) {
											findViewById(R.chat_panel.image)
													.setVisibility(View.VISIBLE);
										} else
											findViewById(R.chat_panel.image)
													.setVisibility(View.GONE);

										if (FeedTask.latestMessageCurrentCID.video_content_urls != null
												&& FeedTask.latestMessageCurrentCID.video_content_urls
														.trim().length() > 0) {
											findViewById(R.chat_panel.video)
													.setVisibility(View.VISIBLE);
										} else
											findViewById(R.chat_panel.video)
													.setVisibility(View.GONE);

										if (FeedTask.latestMessageCurrentCID.voice_content_urls != null
												&& FeedTask.latestMessageCurrentCID.voice_content_urls
														.trim().length() > 0) {
											findViewById(R.chat_panel.voice)
													.setVisibility(View.VISIBLE);
										} else
											findViewById(R.chat_panel.voice)
													.setVisibility(View.GONE);

										if (FeedTask.latestMessageCurrentCID.aniType != null
												&& FeedTask.latestMessageCurrentCID.aniType
														.trim().length() > 0) {
											findViewById(R.chat_panel.animicon)
													.setVisibility(View.VISIBLE);// kainat
										} else
											findViewById(R.chat_panel.animicon)
													.setVisibility(View.GONE);
									}
									FeedTask.latestMessageCurrentCID = null;
								}
							}

							if (FeedTask.UPDATE_ME && !FeedTask.KEEP_POSITION)
								listViewActivity
										.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_ALWAYS_SCROLL);
							if (FeedTask.KEEP_POSITION)
								listViewActivity
										.setTranscriptMode(listViewActivity.TRANSCRIPT_MODE_DISABLED);
							FeedTask.KEEP_POSITION = false;

							if (activityAdapter != null) {
								activityAdapter.getCursor().requery();
								activityAdapter.notifyDataSetChanged();
							}
							listViewActivity.invalidate();
						}
						if (FeedTask.updateParticipantInfoUI) {
							initParticipantInfo();
							updateParticepentLabel();
							blockInputPad();
						}
						if (FeedTask.updateParticipantInfoUISUB) {
							// new Thread(new Runnable() {
							// @Override
							// public void run() {
							// try{
							// handler.post(new Runnable() {
							//
							// @Override
							// public void run() {
							// updtaeSubject();
							// }
							// });
							//
							// FeedTask.updateParticipantInfoUISUB=false ;
							// }catch (Exception e) {
							// }
							// }
							// }).start();
							updateMUCSubject();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (Constants.FEED_GET_CONVERSATION_MESSAGES == requestForID) {
						load_prevoius_message.setVisibility(View.GONE);
					}
					// }
					// });
				}
			} catch (Exception e) {

			}
		}
	}

	public boolean isPhoneNumber(String sPhoneNumber) {

		Pattern pattern = Pattern.compile("\\d{3}-\\d{7}");
		Matcher matcher = pattern.matcher(sPhoneNumber);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^-?\\d+\\.?\\d*$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isEmailId(String email) {

		String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(email);

		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	String LISTBOTTOM = "1";
	String BLOCK_PAD = "1";
	String SEND_MESSAGE = "2";
	String UPDATE_ON_RESPONCE = "3";
	String INVALIDATE_LIST = "4";
	String participantLable = "";
	String p2pParticipent = "";

	private void updateParticepentLabel() {
		try {
			participantLable = "";
			ParticipantInfo otherParticipantInfo = null;
			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				boolean youAdded = false;
				for (int i = 0; i < conversationList.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = conversationList.participantInfo
							.get(i);
					// //System.out.printlnprintln("lock i screen===d= "+participantInfo.userName);
					if (participantInfo.userName.equalsIgnoreCase("a:help")
							|| isEmailId(participantInfo.userName)
							|| (participantInfo.userName.startsWith("+"))
							|| isPhoneNumber(participantInfo.userName)
							|| isNumber(participantInfo.userName)) {
						// //System.out.printlnprintln("lock i screen==== "+participantInfo.userName);
						(findViewById(R.conversation.option))
								.setVisibility(View.VISIBLE);
						// conversation.option
					}
					if (participantInfo.userName == null)
						continue;
					if (participantInfo.userName
							.equalsIgnoreCase(SettingData.sSelf.getUserName())) {
						if (!youAdded) {
							ownerParticipantInfo = participantInfo;
							participantLable = participantLable + "you,";
						}
						youAdded = true;
					} else {
						otherParticipantInfo = participantInfo;
						if (participantInfo.firstName != null
								&& participantInfo.lastName != null)
							participantLable = (participantLable
									+ participantInfo.firstName + " " + participantInfo.lastName)
									.trim() + ",";
						else if (participantInfo.firstName != null
								&& participantInfo.firstName.length() > 0)
							participantLable = (participantLable + participantInfo.firstName)
									.trim() + ",";
						else if (participantInfo.lastName != null
								&& participantInfo.lastName.length() > 0)
							participantLable = (participantLable + participantInfo.lastName)
									.trim() + ",";
						else
							participantLable = participantLable
									+ participantInfo.userName + ",";
					}
				}
			}
			if (otherParticipantInfo == null) {
				otherParticipantInfo = ownerParticipantInfo;
			}
			if (title != null) {
				title.setTag(otherParticipantInfo);
				title.setOnClickListener(profileClick);
			}
			if (participantLable != null) {
				// if(participantLable.endsWith(", ")){
				if (participantLable.length() > 1)
					participantLable = participantLable.substring(0,
							participantLable.length() - 1);
				// }
			}
			participantLable = participantLable.toLowerCase();
			String dist[] = Utilities.split(new StringBuffer(participantLable),
					",");

			Collections.sort(Arrays.asList(dist));

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < dist.length; i++) {
				String intValue = dist[i];
				builder.append(Utilities.capitalizeString(intValue));
				if (i < dist.length - 1) {
					builder.append(", ");
				}
			}
			participantLable = builder.toString();
			// //System.out.printlnprintln("-------------participantLable : "+participantLable);
			((TextView) findViewById(R.conversation.status))
					.setText(participantLable);
			LinearLayout layoutbuddy = (LinearLayout) findViewById(R.conversation_option_panel.p0xx);
			// //System.out.printlnprintln("otherParticipantInfo.userName==="+otherParticipantInfo.userName);
			if (conversationList != null
					&& conversationList.participantInfo != null
					&& conversationList.participantInfo.size() > 0) {
				if (conversationList != null && conversationList.isGroup == 0) {
					if (conversationList.isGroup == 0)// conversationList.participantInfo.size()==1)
					{
						if (ImageDownloader
								.getUserInfo(otherParticipantInfo.userName) != null) {
							// // UserStatus userStatus =
							// ImageDownloader.buddyInfo.get(value.mName.hashCode())
							// ;
							// //
							// //System.out.printlnprintln("--------------userStatus:"+userStatus.toString());
							//
							UserStatus userStatus = ImageDownloader
									.getUserInfo(otherParticipantInfo.userName);
							// //System.out.printlnprintln("--------------userStatus top:"+userStatus.toString());
							// if(userStatus.statusText!=null)
							{
								((TextView) findViewById(R.conversation.info))
										.setText(userStatus.statusText);

							}

							if (userStatus.onLineOffline == 1) {
								// //System.out.printlnprintln("userStatus.onLineOffline==="+userStatus.onLineOffline);
								((CImageView) findViewById(R.conversation_option_panel.online_offline00))
										.setImageResource(R.drawable.online_icon_chat);
								((CImageView) findViewById(R.conversation_option_panel.online_offlineheader))
										.setImageResource(R.drawable.online_icon_chat);

							} else {
								// //System.out.printlnprintln("userStatus.onLineOffline==="+userStatus.onLineOffline);
								((CImageView) findViewById(R.conversation_option_panel.online_offline00))
										.setImageResource(R.drawable.offline_icon_chat);
								((CImageView) findViewById(R.conversation_option_panel.online_offlineheader))
										.setImageResource(R.drawable.offline_icon_chat);
							}

							// if(userStatus!=null)
							// //System.out.printlnprintln("--------------conversationList.participantInfo.get(0).userName:"+conversationList.participantInfo.get(0).userName);
						}

						if (otherParticipantInfo.firstName != null
								&& otherParticipantInfo.lastName != null)
							p2pParticipent = (otherParticipantInfo.firstName
									+ " " + otherParticipantInfo.lastName);
						else if (otherParticipantInfo.firstName != null
								&& otherParticipantInfo.firstName.length() > 0)
							p2pParticipent = (otherParticipantInfo.firstName);
						else
							p2pParticipent = (otherParticipantInfo.userName);
						// //System.out.printlnprintln("p2pParticipent==="+p2pParticipent);
					}
					((LinearLayout) findViewById(R.activity_list_row.activity_LeftLayout))
							.setVisibility(View.GONE);
					layoutbuddy.setVisibility(View.VISIBLE);
					ImageDownloader imageManager = new ImageDownloader();
					// // imageManager.handler=ha
					// ((CImageView)
					// findViewById(R.conversation_option_panel.owner_image_one)).setVisibility(View.VISIBLE);
					// imageManager.download(otherParticipantInfo.userName,
					// ((CImageView)
					// findViewById(R.conversation_option_panel.owner_image_one)),
					// ConversationsActivity.this,ImageDownloader.TYPE_THUMB_BUDDY);
					// new addedn in nonfriend message type
					if (conversationList != null
							&& (conversationList.subject == null || conversationList.subject
									.trim().length() <= 0)) {
						if (title == null)
							title = ((EmojiconTextView) findViewById(R.conversation.title));
						if (conversationList.isGroup == 1
								&& participantLable != null) {
							// //System.out.printlnprintln("-------update subject---2-------");
							title.setText(participantLable);// ((TextView)findViewById(R.conversation.status)).getText());
						} else {
							if (title == null)
								title = ((EmojiconTextView) findViewById(R.conversation.title));
							if (listViewActivity != null)
								title.setText(p2pParticipent);// ((TextView)findViewById(R.conversation.status)).getText());
							// ((CImageView)
							// findViewById(R.conversation_option_panel.online_offlineheader))
							// .setVisibility(View.VISIBLE);

							imageManager.buddyThumb = true;
							imageManager.thumbnailReponseHandler = this;
							imageManager
									.download(
											otherParticipantInfo.userName
													.toLowerCase(),
											(CImageView) findViewById(R.message_row.other_image_top_profile),
											ConversationsActivity.this,
											imageManager.TYPE_THUMB_BUDDY, true);
							// findViewById(R.message_row.other_image_top_profile).setOnClickListener(profileClick);
							findViewById(R.message_row.other_image_top_profile)
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											onBackPressed();
										}
									});
							findViewById(R.message_row.other_image_top_profile)
									.setTag(otherParticipantInfo);
							UserStatus userStatus = ImageDownloader
									.getUserInfo(otherParticipantInfo.userName);
							// //System.out.printlnprintln("--------------userStatus top:"+userStatus.toString());
							// if(userStatus.statusText!=null)
							if (userStatus == null)
								userStatus = new UserStatus();
							{
								if (userStatus.statusText != null)
									((TextView) findViewById(R.conversation.info))
											.setText(userStatus.statusText);

							}

							if (userStatus.onLineOffline == 1) {
								// //System.out.printlnprintln("userStatus.onLineOffline==="+userStatus.onLineOffline);
								((CImageView) findViewById(R.conversation_option_panel.online_offline00))
										.setImageResource(R.drawable.online_icon_chat);
								((CImageView) findViewById(R.conversation_option_panel.online_offlineheader))
										.setImageResource(R.drawable.online_icon_chat);

							} else {
								// //System.out.printlnprintln("userStatus.onLineOffline==="+userStatus.onLineOffline);
								((CImageView) findViewById(R.conversation_option_panel.online_offline00))
										.setImageResource(R.drawable.offline_icon_chat);
								((CImageView) findViewById(R.conversation_option_panel.online_offlineheader))
										.setImageResource(R.drawable.offline_icon_chat);
							}
						}
					}

				} else {

					if (conversationList != null
							&& conversationList.isGroup == 1) {

						if (conversationList != null
								&& (conversationList.subject == null || conversationList.subject
										.trim().length() <= 0)) {
							if (title == null)
								title = ((EmojiconTextView) findViewById(R.conversation.title));
							if (conversationList.isGroup == 1
									&& participantLable != null) {
								// //System.out.printlnprintln("-------update subject---2-------");
								title.setText(participantLable);// ((TextView)findViewById(R.conversation.status)).getText());
							}
						}
						((LinearLayout) findViewById(R.activity_list_row.activity_LeftLayout))
								.setVisibility(View.VISIBLE);
						layoutbuddy.setVisibility(View.GONE);
						((CImageView) findViewById(R.message_row.other_image_top_profile))
								.setVisibility(View.VISIBLE);
						((CImageView) findViewById(R.message_row.other_image_top_profile))
								.setBackgroundResource(R.drawable.groupicon);
						((CImageView) findViewById(R.conversation_option_panel.online_offlineheader))
								.setVisibility(View.GONE);
					}
					findViewById(R.message_row.other_image_top_profile)
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									onBackPressed();
								}
							});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// op();
	}

	public void op() {

		// by using BackgroundColorSpan.
		// However, I want a rounded corners background (like `new
		// PaintDrawable(Color.GREEN).setCornerRadius(8)' ).
		//
		// this is the text we'll be operating on
		SpannableString text = new SpannableString("Lorem ipsum dolor sit amet");

		// make "Lorem" (characters 0 to 5) red
		text.setSpan(new BackgroundColorSpan(Color.LTGRAY), 0, 5, 0);

		// make "ipsum" (characters 6 to 11) one and a half time bigger than the
		// textbox
		// text.setSpan(new RelativeSizeSpan(1.5f), 6, 11, 0);
		text.setSpan(new BackgroundColorSpan(Color.LTGRAY), 6, 11, 0);

		// make "dolor" (characters 12 to 17) display a toast message when
		// touched
		final Context context = this;
		// ClickableSpan clickableSpan = new ClickableSpan() {
		// @Override
		// public void onClick(View view) {
		// Toast.makeText(context, "dolor", Toast.LENGTH_LONG).show();
		// }
		// };
		// text.setSpan(clickableSpan, 12, 17, 0);
		text.setSpan(new BackgroundColorSpan(Color.LTGRAY), 12, 17, 0);

		// make "sit" (characters 18 to 21) struck through
		// text.setSpan(new StrikethroughSpan(), 18, 21, 0);
		text.setSpan(new BackgroundColorSpan(Color.LTGRAY), 18, 21, 0);

		// make "amet" (characters 22 to 26) twice as big, green and a link to
		// this site.
		// it's important to set the color after the URLSpan or the standard
		// link color will override it.
		// text.setSpan(new RelativeSizeSpan(12f), 22, 26, 0);
		// text.setSpan(new URLSpan("http://www.chrisumbel.com"), 22, 26, 0);
		text.setSpan(new BackgroundColorSpan(Color.LTGRAY), 22, 26, 0);
		text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 22,
				26, 0);
		// make our ClickableSpans and URLSpans work
		((TextView) findViewById(R.conversation.status))
				.setMovementMethod(LinkMovementMethod.getInstance());

		// shove our styled text into the TextView
		((TextView) findViewById(R.conversation.status)).setText(text,
				BufferType.SPANNABLE);
	}

	Comparator<String> comparator = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			return o2.compareTo(o1);
		}
	};

	public String[] shortingNameAlphaB(String array[]) {

		int k = array.length;
		String temp = new String();
		String names[] = new String[k + 1];
		for (int i = 0; i < k; i++) {
			names[i] = array[i];
		}
		for (int i = 0; i < k; i++)
			for (int j = i + 1; j < k; j++) {
				if (names[i].compareTo(names[j]) < 0) {
					temp = names[i];
					names[i] = names[j];
					names[j] = temp;
				}
			}
		// //System.out.printlnprintln("Sorted order is");
		// for(int i=0;i<k;i++)
		// {
		// //System.out.printlnprintln(names[i]);
		// }

		return names;
	}

	OnClickListener profileClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			try {
				if ((conversationList != null && conversationList.isGroup == 0)
						|| (lDialogInfo != null && lDialogInfo.isShowing())) {
					ParticipantInfo participantInfo = (ParticipantInfo) v
							.getTag();
					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
							participantInfo.userName);
					Intent intent = new Intent(ConversationsActivity.this,
							ProfileViewActivity.class);
					intent.putExtra("USERID", participantInfo.userName);
					intent.putExtra("CallFrom", 1);
					startActivity(intent);
				} else {
					Utilities.closeSoftKeyBoard(messageBox,
							ConversationsActivity.this);
					showOption(null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	};

	public void showHideVideoCount() {
		TextView count = ((TextView) findViewById(R.id.video_count));
		count.setText("1");
		if (mVideoPath != null)
			count.setVisibility(View.VISIBLE);
		else
			count.setVisibility(View.GONE);
	}

	public void showHidePictureCount() {
		TextView count = ((TextView) findViewById(R.id.image_count));
		count.setText("1");
		if (mPicturePath != null && mPicturePath.size() > 0)
			count.setVisibility(View.VISIBLE);
		else
			count.setVisibility(View.GONE);
	}

	public void showHidePictureDoddleCount() {
		TextView count = ((TextView) findViewById(R.id.image_count_dodle));
		count.setText("1");
		if (mPicturePathCanvas != null && mPicturePathCanvas.size() > 0)
			count.setVisibility(View.VISIBLE);
		else
			count.setVisibility(View.GONE);
	}

	public void showHideRecCount() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				TextView count = ((TextView) findViewById(R.id.voice_count));
				count.setText("1");
				if (mVoicePath != null)
					count.setVisibility(View.VISIBLE);
				else
					count.setVisibility(View.GONE);
			}
		});

	}

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
		openPlayScreen(Downloader.getInstance().getPlayUrl(mVoicePath), false,
				media_play_layout);
		wakeLock.release();
	}

	OnClickListener sendFriendRequestListner = new OnClickListener() {
		public void onClick(View v) {

			addAsFriendRequest(v.getTag().toString());
		};
	};

	public void addAsFriendRequest(String UserName) {
		if (!ConversationsActivity.this.checkInternetConnection()) {
			ConversationsActivity.this.networkLossAlert();
			return;
		}
		StringBuffer text = new StringBuffer("Invite::User##");
		// //System.out.printlnprintln("UserName==="+UserName);
		UserName = BusinessProxy.sSelf.parseUsernameInformation(UserName);

		text.append(UserName);
		if (BusinessProxy.sSelf.sendNewTextMessage(
				"Friend Manager<a:friendmgr>", text.toString(), true)) {
			showSimpleAlert(
					"Info",
					String.format(
							"Friend invite has been sent to %s\nYou will get confirmation in your System Messages!",
							UserName));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// if(contentObserver!=null)
		// getContentResolver().unregisterContentObserver(contentObserver);
		// setUnreadCount();
		// Do not send read message request as there is not new API and old one
		// is not working...
		// sendReadMessageRequest();
	}

	@Override
	protected void onDestroy() {
		// //System.out.printlnprintln("-------------on stop conversation activity--------");
		// stopManagingCursor(activityAdapter.getCursor());
		try {
			if (activityAdapter != null) {
				if (!activityAdapter.getCursor().isClosed())
					activityAdapter.getCursor().close();
			}
			Utilities.removeMoreFromInbox(this,
					getIntent().getStringExtra(Constants.CONVERSATION_ID));
			Utilities.manageDB(this,
					getIntent().getStringExtra(Constants.CONVERSATION_ID));

			DataModel.sSelf.storeObject(DMKeys.COMPOSE_TEXT_MESSAGE, null);
			DataModel.sSelf.storeObject(DMKeys.COMPOSE_VOICE_MESSAGE, null);
			DataModel.sSelf.storeObject(DMKeys.COMPOSE_PICTURE_MESSAGE, null);
			DataModel.sSelf.storeObject(DMKeys.COMPOSE_VIDEO_MESSAGE, null);

			// (Context context, String cid)
			// sqlDB.close(); database.close();logger
		} catch (Exception e) {
			// TODO: handle exception
		}
		FeedTask.insMoreTime2 = 500000;// Long.MIN_VALUE ;
		FeedTask.totLodedMore = 0;// Long.MIN_VALUE ;
		super.onDestroy();

		if (listViewActivity != null) {
			listViewActivity.destroyDrawingCache();
			listViewActivity = null;
			// //System.out.printlnprintln("Catch Clear:::::===== listViewActivity");
		}

		if (gifView != null) {
			gifView.destroyDrawingCache();
			gifView = null;
			// //System.out.printlnprintln("Catch Clear:::::===== gifView");
		}
		if (layoutMenu != null) {
			layoutMenu.destroyDrawingCache();
			layoutMenu = null;
			// //System.out.printlnprintln("Catch Clear:::::===== layoutMenu");
		}

		if (rowView != null) {
			rowView.destroyDrawingCache();
			rowView = null;
			// //System.out.printlnprintln("Catch Clear:::::===== rowView");
		}
		if (imageFrameView != null) {
			imageFrameView.destroyDrawingCache();
			imageFrameView = null;
			// //System.out.printlnprintln("Catch Clear:::::===== imageFrameView");

			if (animationFileName != null) {
				animationFileName = null;
			}
			if (animationFileTrans != null) {
				animationFileTrans = null;
			}
			if (animationSound != null) {
				animationSound = null;
			}
			if (animationImageView != null) {
				animationImageView = null;
			}
			if (animationTagValue != null) {
				animationTagValue = null;
			}
			if (app != null) {
				app.destroyDrawingCache();
				app = null;
			}
		}
		// System.gc();

	}

	/*
	 * private void sendAnimMessage(final String recordedVoicePath, String
	 * username) {
	 * 
	 * ContentValues values = new ContentValues(); long id =
	 * ((System.currentTimeMillis() - 10000)); values.put(MediaPostTable.DATE,
	 * id); long attachemtntsize = 0; values.put(MediaPostTable.COMMAND_TYPE,
	 * "2"); values.put(MediaPostTable.COMMAND, ""+username);
	 * 
	 * if (null != recordedVoicePath) { if (mRecordType == RECORDING_BUZZ) {
	 * 
	 * values.put(MediaPostTable.AUDIO, recordedVoicePath); } int attachemtnt =
	 * 1 ;
	 * 
	 * values.put(MediaPostTable.AUDIO, recordedVoicePath); attachemtntsize =
	 * attachemtntsize + ImageUtils.getImageSize(recordedVoicePath);
	 * values.put(MediaPostTable.ATTACHMENT,attachemtnt); }
	 * values.put(MediaPostTable.ATTACHMENT_SIZE, attachemtntsize);
	 * 
	 * values.put(MediaPostTable.MSG_TYPE, "2");
	 * values.put(MediaPostTable.MSG_OP, ""+Constants.MSG_OP_VOICE_NEW);
	 * values.put(MediaPostTable.USER_ID, BusinessProxy.sSelf.getUserId());
	 * values.put(MediaPostTable.USER_PASS, SettingData.sSelf.getPassword());
	 * values.put(MediaPostTable.CLIENT_ID, BusinessProxy.sSelf.getClientId());
	 * values.put(MediaPostTable.USER_NAME, SettingData.sSelf.getUserName());
	 * values.put(MediaPostTable.REQ_ID, System.currentTimeMillis());
	 * values.put(MediaPostTable.REQ_TYPE, "2_"+Constants.MSG_OP_VOICE_NEW);
	 * values.put(MediaPostTable.TRY, 0);
	 * values.put(MediaPostTable.MEDIS_STAUTS, MediaPostTable.STATUS_QUEUE);
	 * String name = username;//(String) mVoiceMediaProcessor.getTag();
	 * 
	 * if (Logger.ENABLE) { Logger.info(TAG,
	 * "voiceRecordingCompleted-sendMessage -- Sending message  to = " + name);
	 * } values.put(MediaPostTable.DIST, name);
	 * 
	 * getContentResolver().insert(
	 * MediaContentProvider.CONTENT_URI_INBOX_COMPOSE, values);
	 * Utilities.startComposeService(this);
	 * 
	 * // newRequest.mDestContacts = new String[] { name }; int ret =
	 * Constants.ERROR_NONE;//BusinessProxy.sSelf.sendToTransport(newRequest);
	 * switch (ret) { case Constants.ERROR_NONE: //mRecordType = -1;
	 * MediaEngine.getMediaEngineInstance().playResource(R.raw.rocketalert);
	 * break; case Constants.ERROR_OUTQUEUE_FULL: showSimpleAlert("Error",
	 * "Outbox full. Please try after some time!"); return; default: if
	 * (Logger.ENABLE) { Logger.error(TAG,
	 * "voice Recording Completed - sendMessage -- Error " + ret, null); }
	 * showSimpleAlert("Error", getString(R.string.network_busy)); return; } }
	 */

	public void checkAndAddMore() {
		try {
			String lastMsgId = "0";
			String lastMfuId = "0";
			int isNext = 0;
			String[] columns = new String[] { MessageTable.MESSAGE_ID,
					MessageTable.MFU_ID, MessageTable.IS_NEXT };
			// Cursor cursor = getContentResolver().query(
			// MediaContentProvider.CONTENT_URI_INBOX, columns,
			// MessageTable.CONVERSATION_ID + "= ? and "+MessageTable.USER_ID +
			// " =?  "
			// , new String[]{getIntent()
			// .getStringExtra(Constants.CONVERSATION_ID),""+BusinessProxy.sSelf.getUserId()},
			// MessageTable.INSERT_TIME + " DESC");

			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(
					MessageTable.TABLE,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ? and "
							+ MessageTable.MESSAGE_TYPE + " !=?",
					new String[] {
							getIntent().getStringExtra(
									Constants.CONVERSATION_ID),
							BusinessProxy.sSelf.getUserId() + "",
							"" + Message.MESSAGE_TYPE_SYSTEM_MESSAGE }, null,
					null,
					// MessageTable.INSERT_TIME + " ASC"
					MessageTable.DIRECTION + " DESC, " + MessageTable.SORT_TIME
							+ " ASC"
			// order by dir desc, time desc
					);

			if (cursor.getCount() > 0) {
				cursor.moveToLast();
				int dataColumnIndex = cursor
						.getColumnIndex(MessageTable.MESSAGE_ID);
				lastMsgId = cursor.getString(dataColumnIndex);
				lastMfuId = cursor.getString(cursor
						.getColumnIndex(MessageTable.MFU_ID));
				isNext = cursor.getInt(cursor
						.getColumnIndex(MessageTable.IS_NEXT));

				// //System.out.printlnprintln("------------adding more----lastMsgId-:"+lastMsgId);
				// //System.out.printlnprintln("------------adding more----lastMfuId-:"+lastMfuId);
				// //System.out.printlnprintln("------------adding more----isNext-:"+isNext);
				cursor.close();

				if (isNext == 1
						&& (!lastMsgId.equalsIgnoreCase("-999") && !lastMfuId
								.equalsIgnoreCase("-999")/*
														 * && !lastMfuId.
														 * equalsIgnoreCase
														 * ("-1")
														 */)) {
					getContentResolver().delete(
							MediaContentProvider.CONTENT_URI_INBOX,
							MessageTable.MESSAGE_ID + " = '-999' ", null);
					Message message = new Message();
					message.messageId = "-999";
					message.user_id = "" + BusinessProxy.sSelf.getUserId();
					message.conversationId = getIntent().getStringExtra(
							Constants.CONVERSATION_ID);
					message.mfuId = "-999";
					message.inserTime = 0;// Long.MAX_VALUE ;
					FeedTask f = new FeedTask(this);
					f.more = true;
					f.saveMessage(message);
					// //System.out.printlnprintln("------------adding more---added--");
				} else {
					getContentResolver().delete(
							MediaContentProvider.CONTENT_URI_INBOX,
							MessageTable.MESSAGE_ID + " = '-999' ", null);
				}
				if (cursor.getCount() == 1) {
					if ((lastMsgId.equalsIgnoreCase("-999") && lastMfuId
							.equalsIgnoreCase("-999")))
						getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX,
								MessageTable.MESSAGE_ID + " = '-999' ", null);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
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
				.getString(cursor.getColumnIndex(MessageTable.MSG_TXT));
		cursor.close();
		return s;
	}

	// --------------------------------------------------------------------------------
	public Dialog onCreateDialogPricture() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_picture).setItems(
				R.array.image_choose01, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							AppUtil.openCamera(mContext, AppUtil.capturedPath1,
									MUC_CAMERA);
							break;
						case 1:
							AppUtil.openImageGallery(mContext, MUC_PIC);
							break;
						}
					}
				});
		return builder.create();
	}

	private void performCrop(byte resultCode) {
		try {
			mImagesPath.clear();
			mImagesPath.add(AppUtil.capturedPath1);
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("outputX", Constants.CROP_DIMENSION_X);
			cropIntent.putExtra("outputY", Constants.CROP_DIMENSION_Y);
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("scale", true);
			try {
				cropIntent.putExtra("return-data", true);
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						AppUtil.getTempUri());
				// cropIntent.putExtra("outputFormat",
				// Bitmap.CompressFormat.JPEG);
				startActivityForResult(cropIntent, resultCode);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	public String getAbsolutePath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	private class UploadPicTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			rTDialog = ProgressDialog.show(ConversationsActivity.this, "",
					getString(R.string.loading_dot), true);
		}

		@Override
		protected String doInBackground(String... urls) {
			String responseStr = null;
			try {
				if (mImagesPath != null && mImagesPath.size() > 0)
					responseStr = Utilities.createMediaID(mImagesPath.get(0),
							Constants.ID_FOR_UPDATE_PROFILE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return responseStr;
		}

		@Override
		protected void onPostExecute(String response) {
			Log.i(TAG, "UploadPicTask :: onPostExecute :: response : "
					+ response);
			rTDialog.dismiss();
			if (response != null && response.contains("\"status\":\"error\"")) {
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(response);
					if (jsonObject.has("status")
							&& jsonObject.getString("status").equalsIgnoreCase(
									"error")) {
						if (jsonObject.has("message"))
							Toast.makeText(ConversationsActivity.this,
									jsonObject.getString("message"),
									Toast.LENGTH_LONG).show();
						mImagesPath.clear();
						return;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			runOnUiThread(new Runnable() {
				public void run() {
					mucPic.setImageURI(Uri.parse(mucGroupPic));
				}
			});
			if (response != null)
				mucFileId = response;
			sendUpdateSubjectRequest(mucSubject);
			// if(response != null &&
			// !response.contains("\"status\":\"error\"")){
			// runOnUiThread(new Runnable() {
			// public void run() {
			// mucPic.setImageURI(Uri.parse(mucGroupPic));
			// }
			// });
			// mucFileId = response;
			// sendUpdateSubjectRequest(mucSubject);
			// }
		}
	}

	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFileDownloadResposne(final View view, int[] type,
			String[] file_urls, final String[] file_paths) {
		// TODO Auto-generated method stub
		for (int i = 0; i < file_urls.length; i++) {
			Log.i(TAG, "onFileDownloadResposne :: file downloaded from url at "
					+ i + " : " + file_urls[i] + ", filename : "
					+ file_paths[i]);
			switch (type[i]) {
			case 2:
				runOnUiThread(new Runnable() {
					public void run() {
						if (view instanceof ImageView)
							((ImageView) view).setImageURI(Uri
									.parse(file_paths[0]));
					}
				});
				break;

			default:
				break;
			}
		}
	}

	// ------------------------------------------------------------------------------------------------
	private boolean isCancelClick = false;
	AudioProgressWheel wheelProgress;
	private String mRecordedVoicePath;
	static boolean wheelRunning;
	private int progress = 0;
	// private ImageView voiceBt;
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
	private final int RECORDING_TIME = 6 * 60;

	public void showAudioDialog(final byte TYPE) {
		progress = 0;
		isCancelClick = false;
		dialog = new Dialog(ConversationsActivity.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.player_dialog);
		// dialog.setCancelable(true);
		final Runnable wheelThread = new Runnable() {
			public void run() {
				wheelRunning = false;
				while (!wheelRunning) {
					if (progress < RECORDING_TIME) {
						isTimeOver = false;
						wheelProgress.incrementProgress(RECORDING_TIME);
						progress++;
						if (TYPE == PLAY_MODE)
							setPlayLeftTime(mVoiceMedia.getMediaDuration(),
									mVoiceMedia.getCurrentMediaTime());
						else if (TYPE == RECORDING_MODE) {
							recordLeftTime(RECORDING_TIME - progress);
						}
					} else {
						isTimeOver = true;
						wheelRunning = true;
						iCurrentState = UI_STATE_IDLE;
						ConversationsActivity.this
								.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										stopRecording();
									}
								});
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				wheelRunning = true;
			}
		};

		wheelProgress = (AudioProgressWheel) dialog
				.findViewById(R.id.player_wheel);
		wheelProgress.setWheelType(AudioProgressWheel.RECORD_WHEEL);

		if (mRecordedVoicePath != null && TYPE == PLAY_MODE) {
			wheelProgress.resetCount();
			startPlaying(null);
			Thread s = new Thread(wheelThread);
			s.start();
			if (wheelProgress != null)
				wheelProgress.setBackgroundResource(R.drawable.profile_listen);
		} else {
			iCurrentState = UI_STATE_IDLE;
			wheelProgress.resetCount();
			startRecording(null);
			Thread s = new Thread(wheelThread);
			s.start();
			if (wheelProgress != null)
				wheelProgress.setBackgroundResource(R.drawable.profile_audio);
		}
		leftTimeView = (TextView) dialog.findViewById(R.id.left_time);

		Button cancelBt = (Button) dialog.findViewById(R.id.button_01);
		cancelBt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isCancelClick = true;
				dialog.dismiss();
				wakeLock.release();
			}
		});

		final Button doneReset = (Button) dialog.findViewById(R.id.button_02);
		if (TYPE == RECORDING_MODE) {
			doneReset.setText(getString(R.string.done));
		} else if (TYPE == PLAY_MODE) {
			doneReset.setText(getString(R.string.reset));
		}
		doneReset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isCancelClick = false;
				progress = 0;
				// TODO Auto-generated method stub
				if (doneReset.getText().toString()
						.equalsIgnoreCase(getString(R.string.done))) {
					wheelRunning = true;
					stopRecording();
					if (wheelProgress != null) {
						wheelProgress
								.setBackgroundResource(R.drawable.profile_audio);
						wheelProgress.resetCount();
					}
					// voiceBt.setImageResource(R.drawable.profile_listen);

					dialog.dismiss();
				} else if (doneReset.getText().toString()
						.equalsIgnoreCase(getString(R.string.reset))) {// if(TYPE
																		// ==
																		// PLAY_MODE){
					dialog.dismiss();
					wheelRunning = true;
					stopPlaying(v);
					if (wheelProgress != null)
						wheelProgress
								.setBackgroundResource(R.drawable.profile_listen);
					// voiceBt.setImageResource(R.drawable.profile_listen);
					File file = new File(mRecordedVoicePath);
					if (file.exists()) {
						file.delete();
					}
					// voiceBt.setImageResource(R.drawable.profile_audio);
					mRecordedVoicePath = null;
				}
				wakeLock.release();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (isCancelClick) {
					progress = 0;
					if (TYPE == RECORDING_MODE) {
						wheelRunning = true;
						stopRecording();
						if (wheelProgress != null)
							wheelProgress
									.setBackgroundResource(R.drawable.profile_audio);
						mRecordedVoicePath = null;
						// voiceBt.setImageResource(R.drawable.profile_audio);
						dialog.dismiss();
					} else if (TYPE == PLAY_MODE) {
						wheelRunning = true;
						stopPlaying(null);
						if (wheelProgress != null)
							wheelProgress
									.setBackgroundResource(R.drawable.profile_listen);
						dialog.dismiss();
					}
				}
			}
		});
		dialog.show();
	}

	public void setPlayLeftTime(final int total, final int time) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				leftTimeView
						.setText(Utilities.converMiliSecond(time) + " secs");
			}
		});
	}

	public void recordLeftTime(final int time) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				leftTimeView.setText(String.format("%02d:%02d", (time / 60),
						(time % 60)) + " secs");
				// leftTimeView.setText(time+" secs");
			}
		});
	}

	private void startRecording(View v) {
		if (mVoiceMedia == null)
			mVoiceMedia = new VoiceMedia(ConversationsActivity.this, this);
		mVoiceMedia.startRecording(RECORDING_TIME);
		iCurrentState = Constants.UI_STATE_RECORDING;
		Log.d(TAG, "startRecording()");
	}

	private void stopRecording() {
		if (mVoiceMedia != null)
			mVoiceMedia.stopRec();
		iCurrentState = Constants.UI_STATE_IDLE;
		Log.i(TAG, "stopRecording()");
	}

	private void startPlaying(View v) {
		if (mRecordedVoicePath != null) {
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
}
