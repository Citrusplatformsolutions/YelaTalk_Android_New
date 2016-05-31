package com.kainat.app.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kainat.util.AppUtil;
import com.kainat.app.android.CustomMenu.OnMenuItemSelectedListener;
import com.kainat.app.android.MyHorizontalScrollView.SizeCallback;
import com.kainat.app.android.adaptor.NavDrawerListAdapter;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.LandingPageBean;
import com.kainat.app.android.bean.LeftMenu;
import com.kainat.app.android.bean.NavDrawerItem;
import com.kainat.app.android.bean.ParticipantInfo;
import com.kainat.app.android.bean.User;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.engine.HttpSynch;
import com.kainat.app.android.engine.MediaEngine;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.engine.VoiceMediaHandler;
import com.kainat.app.android.facebook.AsyncFacebookRunner;
import com.kainat.app.android.facebook.Facebook;
import com.kainat.app.android.facebook.Login;
import com.kainat.app.android.facebook.SessionEvents;
import com.kainat.app.android.facebook.SessionEvents.AuthListener;
import com.kainat.app.android.facebook.SessionEvents.LogoutListener;
import com.kainat.app.android.facebook.SessionStore;
import com.kainat.app.android.helper.FeedTask;
import com.kainat.app.android.helper.HttpConnectionHelper;
import com.kainat.app.android.helper.MessageConversationTable;
import com.kainat.app.android.helper.MessageTable;
import com.kainat.app.android.helper.SettingTable;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.ClientProperty;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.EmoticonInf;
import com.kainat.app.android.util.FeedManager;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.InboxTblObject;
import com.kainat.app.android.util.KeyValue;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OtsSchedularTask;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.RTMediaPlayer;
import com.kainat.app.android.util.RequestType;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VoiceMedia;
import com.kainat.app.android.util.XMLUtils;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;
import com.navdrawer.SimpleSideDrawer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.flurry.android.FlurryAgent;
//import com.example.kainat.SplashActivity;
//import java.lang.Thread.UncaughtExceptionHandler;

public abstract class UIActivityManager extends FragmentActivity implements
OnClickListener, VoiceMediaHandler, TextToSpeech.OnInitListener,
HttpSynchInf {

	protected YelatalkApplication mMyApp;
	public static int SuffelNeed = 1;
	Handler handler = new Handler();
	boolean select  = false;
	ImageButton menuNew;
	static int showDiscoverCounter = 0;
	public DrawerLayout mDrawerLayout;
	public ListView mDrawerList;
	public ActionBarDrawerToggle mDrawerToggle;
	ImageLoader imageLoader = ImageLoader.getInstance();
	public static String sharedText;
	public static String sharedImage;
	public static String sharedVideo;
	public static String sharedAudio;
	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisc(true).
			resetViewBeforeLoading(true)
			.showImageForEmptyUri(null)
			.showImageOnFail(null)
			.showImageOnLoading(null).build();
	DisplayImageOptions option_mailicon = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisc(true).
			resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.male_icon)
			.showImageOnFail(R.drawable.male_icon)
			.showImageOnLoading(R.drawable.male_icon).build();
	// slide menu items
	public String[] navMenuTitles;
	public TypedArray navMenuIcons;
	public ArrayList<NavDrawerItem> navDrawerItems;
	public NavDrawerListAdapter adapter;
	public static String selectedCategoryName = null;
	public static int picselected = 0;
	public static boolean refreshChannelList = false;
	public static boolean startChannelRefresh = false;
	protected static final String HELP_NO = "+919765266777";
	protected static boolean ContactSelfChange =  false;
	protected static final String M_DOT = "m.rocketalk.com";
	//public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static Context mContext;
	private static final String TAG = UIActivityManager.class.getSimpleName();
	int totalItemCount, visibleItemCount, firstVisibleItem;
	boolean idel = true;
	public static final int SCREEN_SPLASH = 0;
	public static final int SCREEN_ABOUT = 1;
	public static final int SCREEN_ACCOUNT_SETTINGS = 2;
	public static final int SCREEN_ACCOUNT_SETUP = 3;
	public static final int SCREEN_BUDDY = 4;
	public static final int SCREEN_COMPOSE = 5;
	public static final int SCREEN_HOME = 6;
	public static final int SCREEN_INBOX = 7;
	public static final int SCREEN_LOGIN = 8;
	public static final int SCREEN_REGISTRATION = 9;
	public static final int SCREEN_RT_ACCOUNT_SETTING = 10;
	public static final int SCREEN_SEARCH_RESULT = 11;
	public static final int SCREEN_SETTING = 12;
	public static final int SCREEN_GROUP = 13;
	public static final int SCREEN_CHAT = 14;
	public static final int SCREEN_RESET_PASSWORD = 15;
	public static final int SCREEN_COMMUNITY = 16;
	public static final int SCREEN_COMMUNITY_SEARCH = 17;
	public static final int SCREEN_COMMUNITY_CREATE = 18;
	public static final int SCREEN_INVITE = 19;
	public static final int SCREEN_ANS_SEQ = 20;
	public static final int SCREEN_POST = 21;
	public static final int SCREEN_STATUS = 22;
	public static final int SCREEN_WEB_PROF = 23;
	public static final int SCREEN_MEDIA = 24;
	public static final int SCREEN_COMMUNITY_CHAT = 25;
	public static final int SCREEN_MULTIPHOTOVIEW = 26;
	public static final int SCREEN_COMMNITY_MEMBER = 27;
	public static final int SCREEN_RTML = 28;
	public static final int SCREEN_MESSAGE_DETAIL = 29;
	public static final int SCREEN_REGISTRATION_WELCOME = 30;
	public static final int SCREEN_CHAT_LIST = 31;
	public static final int SCREEN_MEDIA_DETAILS = 32;
	public static final int SCREEN_SLIDE_SHOW = 33;
	public static final int SCREEN_ACTIVIITY = 34;
	public static final int SCREEN_DISCOVERY = 35;
	public static final int SCREEN_LANDING_PAGE_MESSAGE_DETAILS = 36;
	public static final int SCREEN_RT_PAGE_MESSAGE_DETAILS = 37;
	public static final int SCREEN_RT_PAGE_LIST = 38;
	public static final int SCREEN_RT_PAGE_FEATURE_LIST = 39;
	public static final int SCREEN_RT_PAGE_FEATURE_DETAILS = 40;
	public static final int SCREEN_RT_PAGE_BLUETOOTH_LIST = 41;

	public static final int SCREEN_VIEW_PROFILE = 42;
	public static final int SCREEN_EDIT_PROFILE = 43;
	public static final int SCREEN_ADD_INTEREST = 44;
	public static final int SCREEN_EMAI_VERIFICATION = 45;

	// for bluetooth
	// **************************************************************
	// private static final String TAG = "BluetoothChat";
	private static final boolean D = true;

	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;

	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	public static final int REQUEST_CONNECT_DEVICE = 11;
	public static final int REQUEST_ENABLE_BT = 22;

	public TextView mTitle;
	public ListView mConversationView;
	public EditText mOutEditText;
	public Button mSendButton;

	public String mConnectedDeviceName = null;
	public static int counter_hitoutsidedata = 0;

	public ArrayAdapter<String> mConversationArrayAdapter;

	public StringBuffer mOutStringBuffer;

	// public static BluetoothAdapter mBluetoothAdapter = null;

	// for bluetooth
	// **************************************************************

	// public static final int SCREEN_COMMUNITY_PAGE_MESSAGE_DETAILS = 36;
	public static boolean sIsRunningInBackGround = false;
	private static final String NEW_MESSAGE_ALERT1 = "You have new message from ";
	// private static final String NEW_MESSAGE_ALERT2 =
	// ". Please click on the message to view!";

	// public static Hashtable iAgentTable = new Hashtable();
	//	private final RockeTalkExceptionHandler UNCAUGHT_EX_HANDLER = new RockeTalkExceptionHandler();
	public static int sScreenNumber;
	private static final int VERIFICATION_REQUEST = 1;
	private static final int UPGRADE_REQUEST = 5;

	public static byte buddyUpdated = 0;// because buddy list displayed updated
	// alert only once

	public static Stack<Class<?>> sScreenStack = new Stack<Class<?>>();
	protected ProgressDialog mProgressDialog;
	private Toast mNotificationToast;
	private VoiceMedia mVoiceMediaProcessor;
	Payload rtAddData;
	private static final byte SMALL_PHONE = 0;
	private static final byte MEDIUM_PHONE = 1;
	private static final byte LARGE_PHONE = 2;
	private static final byte XLARGE_PHONE = 3;
	byte phoneType = MEDIUM_PHONE;
	final protected DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			BusinessProxy.sSelf.cancelOperation();
			dismissAnimationDialog();
			cancelOperation();
		}
	};
	public String iChatFrom;
	public String iBuzzFrom;
	public String iBuzzCommunityFrom;
	public int[] iBuzzTime = new int[] { 60, 60 };// {inTime, OutTime}
	public int[] iRocketTime = new int[] { 60, 60 };// {inTime, OutTime}

	public String[] URLFromServer = { ClientProperty.SERVER_MAIN_ADDRESS,
			ClientProperty.SERVER_FALLBACK_ADDRESS };
	public boolean fromRegistration = false;
	public long iBuzzPlayTime = 0;
	// public int iFullScreenHeight;
	public long iBuzzSentTime;
	public String iUniqueID;
	// public Hashtable iFrndBuzzTimes;
	public long iRocketSentTime;
	public boolean iIsBuzzPlaying;
	public byte[] iBuzzData;
	public static final String PROFILE_VOICE = "Pro_voice.amr";
	private VoiceMedia mVoiceMedia;
	private String mVoicePath;
	public int mCurrentState = Constants.UI_STATE_IDLE;
	public int mPreviousState = Constants.UI_STATE_IDLE;
	public static final byte TAB_CHAT = 0;
	public static final byte TAB_COMMUNITY = 1;
	public static byte PushNotification = 1;
	public static int fromPushNot = 0;
	public static byte TabValue = 1;
	public static int Push_CONVERSATION_ID = 1;
	public static byte mCurrentSelectedTab = TAB_CHAT;
	Random rand = new Random(19580312);
	public static String cacheDir = "";
	//Tobe changed in future
	public static Bundle pushData;
	public static String myProfilePicURL;
	// private TextToSpeech tts;
	public static String featuredTags;
	public static String trendingTags;
	public static boolean directFromCreateChannel;
	public static boolean backFromUpdateChannel;
	public static int selfSentMessageCount;
	public static boolean launchedWithNoNetwork;
	public String SharedPrefrenceName = "SPN"; 
	public String AUTOPLAYSTR = "AUTOPLAYSTR";
	public String AUTOPLAYBOOL = "AUTOPLAYBOOL";
	public static int sharingFromOutside = 0;
	public PhoneStateListener phoneStateListener;
	public static boolean systemMessageReportAction;
	public static int NOTIFICATION_COUNT = 0;
	protected static final DialogInterface.OnKeyListener SEARCH_KEY_HANDLER = new DialogInterface.OnKeyListener() {

		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_SEARCH:
				return true;
			}
			return false;
		}
	};

	public boolean onSearchRequested() {
		return true;// super.onSearchRequested();
	}

	public synchronized void showAnimition(final String title,
			final String message) {
		// showAnimationDialog(title, message, true);

		showProgressDialog1(title, message, null, null,
				new DialogInterface.OnClickListener[] { null, null });
	}
	public synchronized void showAnimitionFullScreen(final String title, final String message, boolean noBG) {
		// showAnimationDialog(title, message, true);
		showProgressFull(title, message, null, null, new DialogInterface.OnClickListener[] { null, null }, noBG);
	}

	protected synchronized void showAnimationDialog(final String title, final String message) {
		// showAnimationDialog(title, message, true);

		showProgressDialog1(title, message, null, null, new DialogInterface.OnClickListener[] { null, null });
	}

	public synchronized void showAnimationDialogFromAdd(final String title,
			final String message, final boolean withButton,
			DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER) {

		if (withButton) {
			showProgressDialog1(title, message,
					new int[] { DialogInterface.BUTTON_POSITIVE },
					new String[] { "Cancel" },
					new DialogInterface.OnClickListener[] {
					PROGRESS_CANCEL_HANDLER, null });
		} else {
			showProgressDialog1(title, message, null, null, null);
		}
	}

	protected synchronized void showAnimationDialog(final String title,
			final String message, final boolean withButton,
			DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER) {

		if (withButton) {
			showProgressDialog1(title, message,
					new int[] { DialogInterface.BUTTON_POSITIVE },
					new String[] { getString(R.string.cancel) },
					new DialogInterface.OnClickListener[] {
					PROGRESS_CANCEL_HANDLER, null });
		} else {
			showProgressDialog1(title, message, null, null, null);
		}
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

		if (status == TextToSpeech.SUCCESS) {

			// int result = tts.setLanguage(Locale.US);

			// tts.setPitch(5); // set pitch level

			// tts.setSpeechRate(2); // set speech speed rate

			// if (result == TextToSpeech.LANG_MISSING_DATA
			// || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			// Log.e("TTS", "Language is not supported");
			// } else {
			// // btnSpeak.setEnabled(true);
			// BusinessProxy.sSelf.messageSound = SettingData.sSelf
			// .getMessageSound();
			// if (Logger.MESSAGEVOICE) {
			// if (BusinessProxy.sSelf.messageSound) {
			// speakOut(BusinessProxy.sSelf.textToVoice);
			// }
			// }
			// BusinessProxy.sSelf.textToVoice = "";
			// }

		} else {
			Log.e("TTS", "Initilization Failed");
		}

	}

	@Override
	protected void onDestroy() {
		// Don't forget to shutdown!

		try {
			// if (tts != null) {
			// tts.stop();
			// tts.shutdown();
			// }
			super.onDestroy();
			// if (mChatService != null) mChatService.stop();
			//			if (BusinessProxy.sSelf.addPush != null) {
			//				BusinessProxy.sSelf.addPush.destroyView();
			//			}
			unbindDrawables(findViewById(R.layout.horz_scroll_menu_left));
			if (app != null) {
				app.destroyDrawingCache();
				app = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		// System.gc();
	}

	private void unbindDrawables(View view) {
		if (view != null && view.getBackground() != null
				&& view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view != null) {
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}
				((ViewGroup) view).removeAllViews();
			}
		}
		if (view != null) {
			view = null;
		}

	}

	public void speakOut(String txtText) {

		String text = txtText.toString();
		BusinessProxy.sSelf.messageSound = SettingData.sSelf.getMessageSound();
		// if (Logger.MESSAGEVOICE) {
		// if (BusinessProxy.sSelf.messageSound) {
		// tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		// }
		// }
	}
	/*public UIActivityManager()
{

}*/
	protected synchronized void showAnimationDialog(final String title,
			final String message, final boolean withButton) {
		/*
		 * runOnUiThread(new Runnable() { public void run() { mProgressDialog =
		 * new ProgressDialog(UIActivityManager.this);
		 * mProgressDialog.setTitle(title); mProgressDialog.setMessage(message);
		 * mProgressDialog.setCancelable(false); if (withButton) {
		 * mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
		 * PROGRESS_CANCEL_HANDLER); } mProgressDialog.show(); } });
		 */

		if (withButton) {
			showProgressDialog1(title, message,
					new int[] { DialogInterface.BUTTON_POSITIVE },
					new String[] { "Cancel" },
					new DialogInterface.OnClickListener[] {
					PROGRESS_CANCEL_HANDLER, null });
		} else {
			showProgressDialog1(title, message, null, null, null);
		}

	}

	protected synchronized void showAnimationDialogNonUIThread(
			final String title, final String message, final boolean withButton,
			DialogInterface.OnClickListener PROGRESS_CANCEL_HANDLER) {
		if (withButton) {
			// showProgressDialog1(title, message, new int[] {
			// DialogInterface.BUTTON_POSITIVE },
			// new String[] { "Cancel" }, new DialogInterface.OnClickListener[]
			// { PROGRESS_CANCEL_HANDLER, null });
			// if(PROGRESS_CANCEL_HANDLER==null)
			// this.PROGRESS_CANCEL_HANDLER =PROGRESS_CANCEL_HANDLER ;
			showProgressDialog(title, message,
					new int[] { DialogInterface.BUTTON_POSITIVE },
					new String[] { getString(R.string.cancel) },
					new DialogInterface.OnClickListener[] {
					PROGRESS_CANCEL_HANDLER, null }, false);
		} else {

			showProgressDialog(title, message, null, null, null, false);
		}
	}

	@Override
	protected void onStop() {

		//		FlurryAgent.onEndSession(this);
		// FlurryAgent.setUserId("" + LoginInformation.getInstance().USERID);

		// if(contentObserver!=null)
		// getContentResolver().unregisterContentObserver(contentObserver);

		if (mVoiceMedia != null
				&& mVoiceMedia.getMediaState() == Constants.UI_STATE_PLAYING)
			mVoiceMedia.stopVoicePlaying();
		try {
			super.onStop();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onPause() 
	{
		Log.i(TAG, "onPause :: App set in background");
		//Here we need to Stop the refresh service as, Messages will be automatically received via GCM
		ImageDownloader.idel = true;
		if (leftMenuShow == true && menu != null) 
		{
			try 
			{
				menuOut = false;
				leftMenuShow = false;
				BusinessProxy.sSelf.leftMenuAdFlag = false;
				transparentBar.setVisibility(View.GONE);
				int menuWidth = menu.getMeasuredWidth();
				meniW = menuWidth;
				int right = meniW;
				scrollView.smoothScrollTo(right, 0);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// RocketalkApplication.am.setMode(AudioManager.STREAM_MUSIC);
		YelatalkApplication.proximityChanged = true;
		sIsRunningInBackGround = true;
		if (quickAction != null && quickAction.isShowing())
			quickAction.dismiss();
		super.onPause();
	}

	public long getRandomNumber() {
		return System.currentTimeMillis();// rand.nextInt();
	}

	public void onMenuButtonClickCall(View v) {
	}

	@Override
	public void onUserInteraction() {
		if (BusinessProxy.sSelf.isAppIdle()) {
			BusinessProxy.sSelf.setRefreshPeriod(Constants.IM_REFRESH_TIME);
			BusinessProxy.sSelf.startRefresh();
			BusinessProxy.sSelf.setAppStatus(false);
		}
		BusinessProxy.sSelf.mUsersLastActivityTime = System.currentTimeMillis();
	}

	public boolean shouldStopMedia = true;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyApp = (YelatalkApplication)this.getApplicationContext();
		phoneType = (byte) Utilities.getScreenType(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		setScreenNumber();

		if (cacheDir == null) {

			cacheDir = Environment.getExternalStorageDirectory().toString();
		}
		if (shouldStopMedia)
			stopMedia();
	}

	protected void onResume() {
		String lan = KeyValue.getString(this, KeyValue.LANGUAGE);
		if(lan!=null)
			setLocale(lan);
		//		if (BusinessProxy.sSelf != null || BusinessProxy.sSelf.addPush != null)
		//			BusinessProxy.sSelf.addPush.mContext = this;

		super.onResume();
		mMyApp.setCurrentActivity(this);

		if (BusinessProxy.thumbInfo == null) 
		{
			int m = 1;
			float sizeInDipWeidth = 80f * m;
			float sizeInDipHeight = 60f * m;
			int thumbW = 60, thumbH = 60;
			if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_X_LARGE) {
				sizeInDipWeidth = 240f * m;
				sizeInDipHeight = 180f * m;
				thumbW = thumbH = 150;
			} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_LARGE) {
				sizeInDipWeidth = 240f * m;
				sizeInDipHeight = 180f * m;
				thumbW = thumbH = 120;
			} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_MEDIUM) {
				// sizeInDipWeidth = 120f*m;
				// sizeInDipHeight = 90f*m;
				// thumbW = thumbH = 60;
			} else if (Utilities.getScreenType(this) == Constants.SCREENLAYOUT_SIZE_SMALL) {
				// sizeInDipWeidth = 80f*m;
				// sizeInDipHeight = 60f*m;
				// thumbW = thumbH = 60;
				sizeInDipWeidth = 240f * m;
				sizeInDipHeight = 180f * m;
				thumbW = thumbH = 120;
			}
			int weigthPX = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, sizeInDipWeidth,
					getResources().getDisplayMetrics());
			int heightPX = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, sizeInDipHeight,
					getResources().getDisplayMetrics());
			if (weigthPX > 0 && heightPX > 0) {
				BusinessProxy.thumbInfo = "iThumbFormat=" + thumbW + "x"
						+ thumbH + "&" + "vThumbFormat=" + weigthPX + "x"
						+ heightPX + "&iFormat=" + weigthPX + "x" + heightPX;
				BusinessProxy.sSelf.imageWidth = weigthPX;
				BusinessProxy.sSelf.imageHeight = heightPX;
				// BusinessProxy.sSelf.thumbInfo =
				// "iThumbFormat=100x100&vThumbFormat=120x90&iFormat=400x350";
			} else {
				BusinessProxy.thumbInfo = "iThumbFormat=60x60&vThumbFormat=120x90&iFormat=120x90";
				BusinessProxy.sSelf.imageWidth = 120;
				BusinessProxy.sSelf.imageHeight = 90;
			}
			// BusinessProxy.sSelf.thumbInfo =
			// "iThumbFormat=100x100&vThumbFormat=120x90&iFormat=400x350";
			BusinessProxy.thumbInfo = "iThumbFormat=300x300&vThumbFormat=300x300&iFormat=300x300";

		}
		if (quickAction != null)
			quickAction.dismiss();
		sIsRunningInBackGround = false;
		//		setScreenNumber();
		BusinessProxy.sSelf.mUIActivityManager = this;
		onNotification(Constants.FEED_NOTIFICATION_COUNT, null, null);
		FeedTask.setHttpSynchRefreshNotification(this);
		FeedTask.setHttpSynchRefreshForChannel(this);
	}

	/*
	 * private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { String
	 * action = intent.getAction(); if
	 * (action.equals(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)) {
	 * setSummaryToName(); } else if
	 * (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED) &&
	 * (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
	 * == BluetoothAdapter.STATE_ON)) { setSummaryToName(); } } };
	 */
	// private LocalBluetoothManager mLocalManager;
	/*
	 * private void setSummaryToName() { // mLocalManager =
	 * LocalBluetoothManager.getInstance(BusinessProxy.sSelf.addPush.mContext);
	 * //BusinessProxy.sSelf.mBluetoothAdapter; if
	 * (BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()) {
	 * setSummary(BusinessProxy.sSelf.mBluetoothAdapter.getName()); } }
	 */

	public void initForAdModule() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					// Hit the below URL and authenticate the client and then
					// get the advt URL, AdvtID, session key etc
					String authURL = Urls.ADD_AUTH;// "http://"+TEJAS_HOST+"/tejas/feeds/api/advt/getAuth?";
					// String authURL =
					// "http://advt.rocketalk.com/tejas/feeds/api/advt/getAuth?";
					StringBuffer headerData = new StringBuffer(
							YelatalkApplication.clientProperty
							.getProperty(ClientProperty.CLIENT_VERSION))
					.append(':');
					DefaultHttpClient client = new DefaultHttpClient();
					// System.out.println("------Main Url---"+authURL+headerData);
					HttpGet httpget = new HttpGet(authURL);
					headerData.append(HttpHeaderUtils.encryptData(Urls.ADD_KEY));
					// System.out.println("Encrypted Header Data--"+headerData.toString());
					httpget.setHeader("ADVT-CLIENT-ID", headerData.toString());
					httpget.setHeader("AD_PARAM", ClientProperty.RT_PARAMS);
					// System.out.println("---AD_PARAM--"+headerData.toString());
					// httpget.setHeader("AD_PARAM", ClientProperty.RT_PARAMS);
					HttpResponse response = client.execute(httpget);
					String responseStr = EntityUtils.toString(response
							.getEntity());

					// System.out.println("------responseStr---"+responseStr);
					// Parse the received XML and get the values -
					Document doc = null;
					try {
						doc = XMLUtils.parseStringToXML(responseStr);
						NodeList nodeGeneric = doc.getElementsByTagName("feed");
						Element genericElement = (Element) nodeGeneric.item(0);
						BusinessProxy.sSelf.advertisementData.clear();
						// System.out.println("------advtUrl---"+XMLUtils.getValue(genericElement,
						// "advtUrl"));
						// System.out.println("------advtSessionKey---"+XMLUtils.getValue(genericElement,
						// "advtSessionKey"));
						// System.out.println("------advtKey---"+XMLUtils.getValue(genericElement,
						// "advtKey"));
						// Check for the vaules and replace if already there.
						if (!BusinessProxy.sSelf.advertisementData.isEmpty()) {
							BusinessProxy.sSelf.advertisementData
							.remove("advtUrl");
							BusinessProxy.sSelf.advertisementData
							.remove("advtSessionKey");
							BusinessProxy.sSelf.advertisementData
							.remove("advtKey");
						}
						BusinessProxy.sSelf.advertisementData.put("advtUrl",
								XMLUtils.getValue(genericElement, "advtUrl"));
						BusinessProxy.sSelf.advertisementData.put(
								"advtSessionKey", XMLUtils.getValue(
										genericElement, "advtSessionKey"));
						BusinessProxy.sSelf.advertisementData.put("advtKey",
								XMLUtils.getValue(genericElement, "advtKey"));
					} catch (SAXException sex) {
						sex.printStackTrace();
					} catch (ParserConfigurationException px) {
					}
				} catch (ClientProtocolException cpe) {

				} catch (IOException ioe) {

				}
			}
		}).start();

	}

	public void onCrahs() {

		if (BusinessProxy.sSelf.getUserStatusString().indexOf("(Connecting)") != -1) {

			// System.out.println("-------------is safe login-----"+Utilities.isSafeLoin(this));
			// System.out.println("-------------is safe logout-----"+Utilities.isSafeLogout(this));
			if (Utilities.isSafeLogout(this) && !Utilities.isSafeLoin(this)) {
				FeedManager.clearAll();
				FeedManager.clearAll();
				finish();
				System.exit(-1);
			}
			// System.out.println("---########################## SYSTEM RESTART ON CRAHS ##########################"+BusinessProxy.sSelf.isSafeLogin);
			// if (sScreenNumber != SCREEN_ABOUT && sScreenNumber !=
			// SCREEN_RESET_PASSWORD &&sScreenNumber != SCREEN_ANS_SEQ &&
			// sScreenNumber != SCREEN_LOGIN && sScreenNumber != SCREEN_SPLASH
			// && sScreenNumber != SCREEN_REGISTRATION && (BusinessProxy.sSelf
			// == null || !BusinessProxy.sSelf.isRegistered()))
			{
				finish();
				FeedManager.clearAll();
				FeedManager.clearAll();
				//				Intent intent = new Intent(this, LoginActivity.class);
				//				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				//						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//				startActivity(intent);
				killApp(true) ;
				return;
			}
		}
	}

	public boolean onBackPressedCheck() {
		return false;
	}

	@Override
	public void onBackPressed() {
		try{
			if (quickAction != null && quickAction.isShowing()) {
				quickAction.dismiss();
				return;
			}
			if (getIntent().getBooleanExtra(Constants.ACTIVITY_FOR_RESULT, false))
				finish();
			else
				popScreen();
		}catch(Exception e){

		}
		// overridePendingTransition(android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right);
	}

	/**
	 * 
	 */
	/*
	 * private void setScreenNumber() {

		final String className = this.getClass().getSimpleName();
		// System.out.println("-------------setScreenNumber-className--"+className);//
//		if (className.equals(SplashActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_SPLASH;
//		} else if (className.equals(RegistrationMessageActivity.class
//				.getSimpleName())) {
//			sScreenNumber = SCREEN_REGISTRATION_WELCOME;
//		} else 
			if (className.equals(AboutActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_ABOUT;
		} else if (className.equals(BuddyActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_BUDDY;
		} else if (className.equals(ComposeActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_COMPOSE;
		} else if (className.equals(KainatHomeActivity.class.getSimpleName())) {

			if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
				sScreenStack = new Stack<Class<?>>();

			sScreenNumber = SCREEN_HOME;
		} else if (className.equals(KainatHomeActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_INBOX;
		} else if (className.equals(LoginActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_LOGIN;
		} 
//		else if (className.equals(RegistrationActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_REGISTRATION;
//		} 
//		else if (className.equals(SearchResultActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_SEARCH_RESULT;
//		} else if (className.equals(SettingUIActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_SETTING;
//		} 
//		else if (className
//				.equals(ResetPasswordActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RESET_PASSWORD;
//		} 
		else if (className.equals(KainatHomeActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_COMMUNITY;
		} else if (className.equals(SearchCommunityActivity.class
				.getSimpleName())) {
			sScreenNumber = SCREEN_COMMUNITY_SEARCH;
		} else if (className.equals(CreateCommunityActivity.class
				.getSimpleName())) {
			sScreenNumber = SCREEN_COMMUNITY_CREATE;
		} else if (className.equals(InviteActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_INVITE;
		} 
//		else if (className.equals(AnswerSecurityQuestionActivity.class
//				.getSimpleName())) {
//			sScreenNumber = SCREEN_ANS_SEQ;
//		}
		else if (className.equals(PostActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_POST;
		} else if (className.equals(MyStatus.class.getSimpleName())) {
			sScreenNumber = SCREEN_STATUS;
		}
		// else if (className.equals(ChatListActivity.class.getSimpleName())) {
		// sScreenNumber = SCREEN_CHAT_LIST;
		// }
//		else if (className.equals(WebviewActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_WEB_PROF;
//		}
		// else if (className.equals(LandingPageBean.class.getSimpleName())) {
		// sScreenNumber = SCREEN_ACTIVIITY;
		// }
//		else if (className.equals(LandingPageMessageDetail.class
//				.getSimpleName())) {
//			sScreenNumber = SCREEN_LANDING_PAGE_MESSAGE_DETAILS;
//		} else if (className.equals(DiscoveryFeaturedRtLive.class
//				.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_MESSAGE_DETAILS;
//		} else if (className.equals(RtLiveListActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_LIST;
//		} else if (className.equals(FeaturedUserListActivity.class
//				.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_FEATURE_LIST;
//		} else if (className.equals(DiscoveryFeaturedUsersDetails.class
//				.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_MESSAGE_DETAILS;
//		} 
//		else if (className.equals(DeviceListActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_BLUETOOTH_LIST;
//		}

		else if (className.equals(AbstractMedia.class.getSimpleName())
				|| className
				.equals(MediaActivityExternal.class.getSimpleName())
				|| className
				.equals(MediaActivityFeatured.class.getSimpleName())
				|| className.equals(MediaActivityHotPic.class.getSimpleName())
				|| className.equals(MediaActivityMore.class.getSimpleName())
				|| className.equals(MediaActivityMyAlbum.class.getSimpleName())
				|| className.equals(MediaActivitySearch.class.getSimpleName())
				|| className
				.equals(MediaActivityToFollow.class.getSimpleName())
				|| className.equals(MediaActivityUser.class.getSimpleName())
				|| className.equals(MediaActivityUserFollowering.class
						.getSimpleName())
						|| className.equals(MediaActivityVideo.class.getSimpleName())) {
			sScreenNumber = SCREEN_MEDIA;
		} else if (className
				.equals(CommunityChatActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_COMMUNITY_CHAT;
		}
		// else if (className.equals(MediaActivity.class.getSimpleName())) {
		// sScreenNumber = SCREEN_STATUS;
		// }
		else if (className.equals(MultiPhotoViewer.class.getSimpleName())) {
			sScreenNumber = SCREEN_MULTIPHOTOVIEW;
		}
		// else if (className.equals(ChatActivity.class.getSimpleName())) {
		// sScreenNumber = SCREEN_CHAT;
		// }
		else if (className.equals(CommunityMemberActivity.class.getSimpleName())) {
			sScreenNumber = SCREEN_COMMNITY_MEMBER;
		} 
//		else if (className.equals(CustomViewDemo.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RTML;
//		} 
//		else if (className.equals(PostActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_POST;
//		} 
//		else if (className.equals(SearchResultActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_SEARCH_RESULT;
//		} 
//		else if (className.equals(MessageActivity.class.getSimpleName())) {
//			sScreenNumber = SCREEN_MESSAGE_DETAIL;
//		} 
		else if (className.equals(MediaVideoPlayer.class.getSimpleName())) {
			sScreenNumber = SCREEN_MEDIA_DETAILS;
		} else if (className.equals(StreemMultiPhotoViewer.class
				.getSimpleName())
				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
			sScreenNumber = SCREEN_SLIDE_SHOW;
		}

		else if (className.equals(KainatHomeActivity.class.getSimpleName())
				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
			sScreenNumber = SCREEN_ACTIVIITY;
		} else if (className.equals(KainatHomeActivity.class.getSimpleName())
				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
			sScreenNumber = SCREEN_DISCOVERY;
		} 
//		else if (className.equals(LandingPageMessageDetail.class
//				.getSimpleName())
//				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
//			sScreenNumber = SCREEN_LANDING_PAGE_MESSAGE_DETAILS;
//		} else if (className.equals(DiscoveryFeaturedRtLive.class
//				.getSimpleName())
//				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_MESSAGE_DETAILS;
//		} else if (className.equals(RtLiveListActivity.class.getSimpleName())
//				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_LIST;
//		}
//
//		else if (className.equals(FeaturedUserListActivity.class
//				.getSimpleName())
//				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
//			sScreenNumber = SCREEN_RT_PAGE_FEATURE_LIST;
//		} 
		else if (className.equals(KainatProfileActivity.class.getSimpleName())
				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
			sScreenNumber = SCREEN_EDIT_PROFILE;
		} else if (className.equals(ProfileViewActivity.class.getSimpleName())
				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
			sScreenNumber = SCREEN_VIEW_PROFILE;
		} 
//		else if (className.equals(EmailVerifyActivity.class.getSimpleName())
//				|| className.equals(MultiPhotoViewer.class.getSimpleName())) {
//			sScreenNumber = SCREEN_EMAI_VERIFICATION;
//		}

		// BusinessProxy.sSelf.addPush.closeMethod();

		BusinessProxy.sSelf.mUIActivityManager = this;
	}

	public String getCurrentScreen(int aScreenNumber) {
		// System.out.println("--------------aScreenNumber---"+aScreenNumber);
		String screenName = null;
		switch (aScreenNumber) {
		case SCREEN_LOGIN:
			screenName = "LOGIN";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_REGISTRATION_WELCOME:
			screenName = "REG_WEL";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_REGISTRATION:
			screenName = "REG";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_HOME:
			screenName = defaultScreen;
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_INBOX:
			screenName = "IBX";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_BUDDY:
			// screenName = "BUD";
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_WEB_PROF:
			screenName = defaultScreen;
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_COMMUNITY:
			screenName = defaultScreen;
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_MEDIA:
			screenName = defaultScreen;// "GLY";
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_INVITE:
			screenName = "INVRT";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_SEARCH_RESULT:
			// screenName = "SEARCH_HOME";
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_SETTING:
			screenName = defaultScreen;// "SETT";
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_CHAT:
			screenName = "CHAT";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_COMPOSE:
			screenName = "CMPS";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_ABOUT:
			screenName = "ABT";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_COMMNITY_MEMBER:
			// screenName = "COM_MEM";
			// BusinessProxy.sSelf.dynamicScreen = null ;
			break;
		case SCREEN_COMMUNITY_CHAT:
			screenName = "COM_MSG";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_COMMUNITY_SEARCH:
			screenName = "GRP_SCH";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_STATUS:
			screenName = "STTS_UPDT";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_RTML:
			screenName = "SCREEN_RTML";// SCREEN_RTML
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_MESSAGE_DETAIL:
			screenName = "OPN";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_POST:
			screenName = "PST";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_CHAT_LIST:
			screenName = "CHLIST";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_MEDIA_DETAILS:
			screenName = "GLY_DTL";// if ut chaning here pls channge also in
			// ADDPush class
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_SLIDE_SHOW:
			screenName = "SLIDE_SHOW";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_ACTIVIITY:
			screenName = "LANDING";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_DISCOVERY:
			screenName = "LANDING";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_LANDING_PAGE_MESSAGE_DETAILS:// ////////////////////
			screenName = "MEDIA_DETAIL_DIS";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_RT_PAGE_MESSAGE_DETAILS:// /////////////////
			screenName = "EVENT_DETAIL_DIS";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_RT_PAGE_LIST:// ////////////////////
			screenName = defaultScreen;
			// BusinessProxy.sSelf.dynamicScreen = null ;

			break;
		case SCREEN_RT_PAGE_FEATURE_LIST:// //////////////////
			screenName = defaultScreen;
			// BusinessProxy.sSelf.dynamicScreen = null ;

			break;
		case SCREEN_RT_PAGE_FEATURE_DETAILS:
			screenName = "FEATURE_USER_DETAILS";
			BusinessProxy.sSelf.dynamicScreen = null;

			break;
		case SCREEN_RT_PAGE_BLUETOOTH_LIST:
			screenName = "BLUETOOTH_LIST";
			BusinessProxy.sSelf.dynamicScreen = null;
			break;

		case SCREEN_EDIT_PROFILE:
			screenName = Constants.SCRTEEN_NAME_EDIT_PROFILE;
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_VIEW_PROFILE:
			screenName = Constants.SCRTEEN_NAME_VIEW_PROFILE;
			BusinessProxy.sSelf.dynamicScreen = null;
			break;
		case SCREEN_EMAI_VERIFICATION:
			screenName = Constants.SCRTEEN_NAME_EMAIL_VERIFICATION;
			BusinessProxy.sSelf.dynamicScreen = null;
			break;

		default:
			screenName = defaultScreen;
			break;
		}

		return screenName;
	}

	 */

	public String defaultScreen = "NA";

	protected void pushNewScreen(Class<?> clazz, boolean clearCurrentScreen) {
		// mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
		// if(mBluetoothAdapter!=null ){
		// mBluetoothAdapter.disable();
		// }
		Intent intent = new Intent(UIActivityManager.this, clazz);
		if (clazz.getSimpleName().equals(
				KainatHomeActivity.class.getSimpleName())) {
			while (!sScreenStack.empty()) {
				sScreenStack.pop();
			}

		} 
		else if (clazz.getSimpleName().equals(
				KainatHomeActivity.class.getSimpleName())) {
			while (!sScreenStack.empty()) {
				sScreenStack.pop();
			}
		} else if (clazz.getSimpleName().equals(
				KainatHomeActivity.class.getSimpleName())) {
			while (!sScreenStack.empty()) {
				sScreenStack.pop();
			}
		} 
		//		else if (clazz.getSimpleName().equals(
		//				InviteActivity.class.getSimpleName())) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		} 
		//		else if (clazz.getSimpleName().equals(MyStatus.class.getSimpleName())) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		} 
		//		else if (clazz.getSimpleName().equals(
		//				RtLiveListActivity.class.getSimpleName())
		//				&& sScreenStack.size() < 1) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		}
		finish();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		/*
		 * if(clazz == BuddyActivity)
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		 * Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 */
		if (clazz == KainatHomeActivity.class) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		sScreenStack.push(clazz);
	}

	protected void pushNewScreen(Class<?> clazz, boolean clearCurrentScreen,
			boolean extra) {
		// mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
		// if(mBluetoothAdapter!=null ){
		// mBluetoothAdapter.disable();
		// }
		Intent intent = new Intent(UIActivityManager.this, clazz);
		if (clazz.getSimpleName().equals(
				KainatHomeActivity.class.getSimpleName())) {
			while (!sScreenStack.empty()) {
				sScreenStack.pop();
			}

		} 
		//		else if (clazz.getSimpleName().equals(
		//				SettingUIActivity.class.getSimpleName())) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		}
		// else if
		// (clazz.getSimpleName().equals(ChatListActivity.class.getSimpleName())&&sScreenStack.size()<1)
		// {
		// while (!sScreenStack.empty()) {
		// sScreenStack.pop();
		// }
		// }
		//		else if (clazz.getSimpleName().equals(
		//				MediaActivity.class.getSimpleName())) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		} 
		//		else if (clazz.getSimpleName().equals(
		//				SearchResultActivity.class.getSimpleName())) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		} 
		//		else if (clazz.getSimpleName().equals(
		//				BuddyActivity.class.getSimpleName())) {
		//			while (!sScreenStack.empty()) {
		//				sScreenStack.pop();
		//			}
		//		} 
		else if (clazz.getSimpleName().equals(
				KainatHomeActivity.class.getSimpleName())) {
			while (!sScreenStack.empty()) {
				sScreenStack.pop();
			}
		} else if (clazz.getSimpleName().equals(
				KainatHomeActivity.class.getSimpleName())) {
			while (!sScreenStack.empty()) {
				sScreenStack.pop();
			}
		} 
		finish();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("HOME", extra);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		startActivity(intent);
		sScreenStack.push(clazz);
	}

	protected void pushNewScreen(Intent intent, Class<?> clazz,
			boolean clearCurrentScreen) {
		finish();
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
		startActivity(intent);
		sScreenStack.push(clazz);
	}

	protected final void popScreen() {
		if (sScreenStack.size() > 1)
			sScreenStack.pop();
		if (!sScreenStack.isEmpty()) {
			Class<?> clazz = sScreenStack.peek();
			//			if (clazz.getSimpleName().equals(
			//					SettingUIActivity.class.getSimpleName())) {
			//
			//				while (!sScreenStack.empty()) {
			//					sScreenStack.pop();
			//				}
			//				pushNewScreen(KainatHomeActivity.class, false);
			//			}
			// else if
			// (clazz.getSimpleName().equals(ChatListActivity.class.getSimpleName())&&sScreenStack.size()<1)
			// {
			// while (!sScreenStack.empty()) {
			// sScreenStack.pop();
			// }
			// pushNewScreen(KainatHomeActivity.class, false);
			// }
			//			else

			//				if (clazz.getSimpleName().equals(MediaActivity.class.getSimpleName())) {
			//				while (!sScreenStack.empty()) {
			//					sScreenStack.pop();
			//				}
			//				pushNewScreen(KainatHomeActivity.class, false);
			//			} 
			//			else if (clazz.getSimpleName().equals(
			//					SearchResultActivity.class.getSimpleName())) {
			//
			//				while (!sScreenStack.empty()) {
			//					sScreenStack.pop();
			//				}
			//
			//				pushNewScreen(KainatHomeActivity.class, false);
			//
			//			} 
			//			else if (clazz.getSimpleName().equals(
			//					BuddyActivity.class.getSimpleName())) {
			//				while (!sScreenStack.empty()) {
			//					sScreenStack.pop();
			//				}
			//				pushNewScreen(KainatHomeActivity.class, false);
			//			} 
			//			else
			if (clazz.getSimpleName().equals(
					KainatHomeActivity.class.getSimpleName())) {
				while (!sScreenStack.empty()) {
					sScreenStack.pop();
				}
				pushNewScreen(KainatHomeActivity.class, false);
			} else if (clazz.getSimpleName().equals(
					KainatHomeActivity.class.getSimpleName())
					&& sScreenStack.size() < 1) {
				while (!sScreenStack.empty()) {
					sScreenStack.pop();
				}
				pushNewScreen(KainatHomeActivity.class, false);
			} 
			else {
				Intent intent = new Intent(UIActivityManager.this, clazz);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_left_in,
						R.anim.slide_left_out);
				finish();
			}
		}
	}

	public final void makeToast(String msg) {
		// Toast.makeText(UIActivityManager.this, msg,
		// Toast.LENGTH_LONG).show();
	}

	public final void someThingWentWrong(final String msg) {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(UIActivityManager.this, msg, Toast.LENGTH_LONG)
				.show();
			}
		});

	}

	public final void havePatient() {
		// Toast.makeText(UIActivityManager.this,
		// "Please have patient, We shall come with this feature soon!",
		// 3000).show();
	}

	public final void showToast(String msg) {
		Toast.makeText(UIActivityManager.this, msg, 3000).show();
	}

	AlertDialog alertDialog;

	public synchronized final void showSimpleAlert(final String title,final String message) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showSimpleAlert(title, message, null);
			}
		});

	}

	public synchronized final void showSimpleAlertOutUIRun(final String title,
			final String message) {
		showSimpleAlert(title, message, null);
	}

	public synchronized final void showSimpleAlert(final String title,
			final String message, final DialogInterface.OnClickListener listener) {
		if (null == message) {
			return;
		}
		if (lDialog != null && lDialog.isShowing()) {
			return;
		}
		/*
		 * runOnUiThread(new Runnable() { public void run() { if
		 * (!isFinishing()) { alertDialog = new
		 * AlertDialog.Builder(UIActivityManager.this).create(); if (null !=
		 * title) { alertDialog.setTitle(title); }
		 * alertDialog.setMessage(message);
		 * alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
		 * listener); alertDialog.setOnKeyListener(SEARCH_KEY_HANDLER);
		 * alertDialog.setCancelable(false); alertDialog.show(); } } });
		 */
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showCustomMessage(title, message, null, null, null);
			}
		});

	}

	public Dialog lDialog = null;

	public void showCustomMessage(String pTitle, final String pMsg,
			final int[] buttonTypes, final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners) {
		if (pTitle != null && pTitle.trim().equalsIgnoreCase("Error")) {
			pTitle = "Alert";
		}
		boolean space = false;
		if (pTitle.equalsIgnoreCase("Alert")) {
			pTitle = "";
			space = true;
		}
		if (pTitle.equalsIgnoreCase("RockeTalk")) {
			pTitle = getString(R.string.app_name);
			space = true;
		}
		if (pTitle.equalsIgnoreCase("Information")) {
			pTitle = getString(R.string.information);

		}

		//		""
		// System.out.println("------showCustomMessage");
		try {

			if (lDialog != null && lDialog.isShowing()) {
				lDialog.dismiss();
				lDialog = null;
			}

			//			lDialog = new Dialog(BusinessProxy.sSelf.addPush.mContext,
			//					android.R.style.Theme_Translucent_NoTitleBar) {
			//				public void onBackPressed() {
			//					if (null == buttonTypes)
			//						if (lDialog == null && lDialog.isShowing())
			//							lDialog.dismiss();
			//				};
			//			};
			// lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			lDialog.setContentView(R.layout.r_okcanceldialogview);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

			WindowManager.LayoutParams lp = lDialog.getWindow().getAttributes();
			lp.dimAmount = Constants.dimamount;
			lDialog.getWindow().setAttributes(lp);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			lDialog.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);

			((TextView) lDialog.findViewById(R.id.dialog_title))
			.setText(pTitle);

			if (pTitle == null || pTitle.length() <= 0)
				lDialog.findViewById(R.id.dialog_title)
				.setVisibility(View.GONE);

			((TextView) lDialog.findViewById(R.id.dialog_message))
			.setText(pMsg);

			if (space) {
				// ((TextView)
				// lDialog.findViewById(R.id.dialog_message)).setMaxHeight(100)
				// ;
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ((TextView) lDialog
						.findViewById(R.id.dialog_message)).getLayoutParams();
				layoutParams.setMargins(0, 50, 0, 50);
			}
			LinearLayout buttonContainer = ((LinearLayout) lDialog
					.findViewById(R.id.buttonContainer));// .setText(pMsg);
			LinearLayout con = ((LinearLayout) lDialog.findViewById(R.id.con));
			// View convertView =
			// LayoutInflater.from(this).inflate(R.layout.button, null);
			// Button b = ((Button)
			// convertView.findViewById(R.id.button));//.setText(pMsg);.findViewById(R.id.dialog_message)).setText(pMsg);
			//
			// android:layout_width="0dip"
			// android:background="@drawable/custom_button1"
			// android:layout_weight="0.5"
			// android:layout_gravity="left"
			// android:textColor = "@color/white"
			// android:text="Cancel"
			// android:textStyle="bold"
			// android:layout_marginLeft="10dip"
			// android:id="@+id/cancel"

			android.widget.LinearLayout.LayoutParams param = new android.widget.LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT,
					(int) Utilities.convertDpToPixel(40, this), 0.5f);
			// param.leftMargin = (int) Utilities.convertDpToPixel(5, this);
			// param.rightMargin = (int) Utilities.convertDpToPixel(5, this);
			/*
			 * Button button = new Button(this); button.setLayoutParams(param) ;
			 * button.setText("sdfsdf") ; button.setTextColor(0xffffffff);
			 * button.setHeight(25) ;
			 * 
			 * button.setBackgroundResource(R.drawable.custom_button1) ;
			 * buttonContainer.addView(button);
			 * 
			 * button = new Button(this); button.setLayoutParams(param) ;
			 * button.setText("sdfsdf") ; button.setTextColor(0xffffffff);
			 * button.setHeight(25) ;
			 * button.setBackgroundResource(R.drawable.custom_button1) ;
			 * buttonContainer.addView(button);
			 */

			if (null != buttonTypes) {

				for (int i = 0; i < buttonTypes.length; i++) {
					// lDialog.setButton(buttonTypes[i], buttonTexts[i],
					// buttonListeners[i]);

					Button button = new Button(this);
					// View view =new View(this);

					button.setLayoutParams(param);
					// view.setLayoutParams(param);
					button.setText(buttonTexts[i]);
					button.setTextColor(0xf1f1f1f1);
					button.setId(i);
					// button.setPadding((int) Utilities.convertDpToPixel(10,
					// this), 0, 0, 0);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							runOnUiThread(new Runnable() {
								public void run() {
									cancelAlert();
									lDialog.dismiss();
									int btnIndex = v.getId();
									try {
										// if(buttonTypes[v.getId()] != 0)
										btnIndex = buttonTypes[v.getId()];
									} catch (Exception e) {
										// TODO: handle exception
									}
									if (buttonListeners[v.getId()] != null)
										buttonListeners[v.getId()].onClick(
												null, btnIndex);

								}
							});

						}
					});
					if (buttonTexts != null && buttonTexts.length > 1) {
						if (buttonTexts[1] != null
								&& buttonTexts[0]
										.equalsIgnoreCase("GPS Setting")
										&& buttonTexts[1].equalsIgnoreCase("Cancel")) {
							if (buttonTexts[1].equalsIgnoreCase("Cancel")) {
								button.setBackgroundResource(R.drawable.alertbuttonno);
							}
							if (buttonTexts[0].equalsIgnoreCase("GPS Setting")) {
								button.setBackgroundResource(R.drawable.alertbuttonyes);
							}
						} else {
							if (buttonTexts[1] != null
									&& (buttonTexts[i].equalsIgnoreCase("No") || buttonTexts[1]
											.equalsIgnoreCase("Cancel"))) {
								/*
								 * view.setBackgroundColor(0xf1f1f1f1);
								 * android.widget.LinearLayout.LayoutParams
								 * paramxx = new
								 * android.widget.LinearLayout.LayoutParams( 1,
								 * (int) Utilities.convertDpToPixel(40, this),
								 * 0.5f); view.setLayoutParams(paramxx);
								 * buttonContainer.addView(view);
								 */
								button.setBackgroundResource(R.drawable.alertbuttonno);
							} else if (buttonTexts[0] != null
									&& (buttonTexts[0]
											.equalsIgnoreCase("Cancel") || buttonTexts[0]
													.equalsIgnoreCase("Cancel"))) {

								button.setBackgroundResource(R.drawable.alertbuttonyes);
							} else if (buttonTexts[1] != null
									&& (buttonTexts[1].equalsIgnoreCase("Yes") || buttonTexts[1]
											.equalsIgnoreCase("Yes"))) {

								button.setBackgroundResource(R.drawable.alertbuttonno);
							} else {
								button.setBackgroundResource(R.drawable.alertbuttonyes);
							}
						}
					} else
						button.setBackgroundResource(R.drawable.alertbuttonyes);
					buttonContainer.addView(button);
				}
			} else {

				Button button = new Button(this);
				button.setLayoutParams(param);
				button.setText(getString(R.string.ok));
				button.setTextColor(0xffffffff);
				// button.setHeight(Utilities.convertDpToPixel(40, this)) ;
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// buttonListeners[v.getId()].onClick(null, v.getId());
						cancelAlert();
						lDialog.dismiss();
					}
				});
				button.setBackgroundResource(R.drawable.transbuttion);
				buttonContainer.addView(button);

			}

			//			Utilities.startAnimition(BusinessProxy.sSelf.addPush.mContext, con,
			//					R.anim.grow_from_midddle);
			//			if (!((Activity) BusinessProxy.sSelf.addPush.mContext)
			//					.isFinishing())
			lDialog.show();
		} catch (Exception e) {
			System.out
			.println("##################### EXCEPTION CATCHED ############################");
			e.printStackTrace();
		}

	}

	public void showCustomMessageInsideWithoutRun(String pTitle,
			final String pMsg, final int[] buttonTypes,
			final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners) {
		if (pTitle != null && pTitle.trim().equalsIgnoreCase("Error")) {
			pTitle = "Alert";
		}
		// System.out.println("------showCustomMessage");
		try {

			if (lDialog != null && lDialog.isShowing()) {
				lDialog.dismiss();
				lDialog = null;
			}

			lDialog = new Dialog(this,
					android.R.style.Theme_Translucent_NoTitleBar) {
				public void onBackPressed() {
					if (null == buttonTypes)
						if (lDialog == null && lDialog.isShowing())
							lDialog.dismiss();
				};
			};
			// lDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			lDialog.setContentView(R.layout.r_okcanceldialogview);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

			WindowManager.LayoutParams lp = lDialog.getWindow().getAttributes();
			lp.dimAmount = Constants.dimamount;
			lDialog.getWindow().setAttributes(lp);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			lDialog.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);

			((TextView) lDialog.findViewById(R.id.dialog_title))
			.setText(pTitle);
			((TextView) lDialog.findViewById(R.id.dialog_message))
			.setText(pMsg);
			LinearLayout buttonContainer = ((LinearLayout) lDialog
					.findViewById(R.id.buttonContainer));// .setText(pMsg);
			LinearLayout con = ((LinearLayout) lDialog.findViewById(R.id.con));
			// View convertView =
			// LayoutInflater.from(this).inflate(R.layout.button, null);
			// Button b = ((Button)
			// convertView.findViewById(R.id.button));//.setText(pMsg);.findViewById(R.id.dialog_message)).setText(pMsg);
			//
			// android:layout_width="0dip"
			// android:background="@drawable/custom_button1"
			// android:layout_weight="0.5"
			// android:layout_gravity="left"
			// android:textColor = "@color/white"
			// android:text="Cancel"
			// android:textStyle="bold"
			// android:layout_marginLeft="10dip"
			// android:id="@+id/cancel"

			android.widget.LinearLayout.LayoutParams param = new android.widget.LinearLayout.LayoutParams(
					0, (int) Utilities.convertDpToPixel(40, this), 0.5f);
			// param.leftMargin = (int) Utilities.convertDpToPixel(5, this);
			// param.rightMargin = (int) Utilities.convertDpToPixel(5, this);
			/*
			 * Button button = new Button(this); button.setLayoutParams(param) ;
			 * button.setText("sdfsdf") ; button.setTextColor(0xffffffff);
			 * button.setHeight(25) ;
			 * 
			 * button.setBackgroundResource(R.drawable.custom_button1) ;
			 * buttonContainer.addView(button);
			 * 
			 * button = new Button(this); button.setLayoutParams(param) ;
			 * button.setText("sdfsdf") ; button.setTextColor(0xffffffff);
			 * button.setHeight(25) ;
			 * button.setBackgroundResource(R.drawable.custom_button1) ;
			 * buttonContainer.addView(button);
			 */

			if (null != buttonTypes) {
				for (int i = 0; i < buttonTypes.length; i++) {
					// lDialog.setButton(buttonTypes[i], buttonTexts[i],
					// buttonListeners[i]);

					Button button = new Button(this);
					button.setLayoutParams(param);
					button.setText(buttonTexts[i]);
					button.setTextColor(0xffffffff);
					button.setId(i);
					// button.setPadding((int) Utilities.convertDpToPixel(10,
					// this), 0, 0, 0);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							runOnUiThread(new Runnable() {
								public void run() {
									cancelAlert();
									lDialog.dismiss();
									int btnIndex = v.getId();
									try {
										// if(buttonTypes[v.getId()] != 0)
										btnIndex = buttonTypes[v.getId()];
									} catch (Exception e) {
										// TODO: handle exception
									}
									if (buttonListeners[v.getId()] != null)
										buttonListeners[v.getId()].onClick(
												null, btnIndex);

								}
							});

						}
					});
					button.setBackgroundResource(R.drawable.alertbuttonyes);
					if (buttonTexts[i] != null
							&& (buttonTexts[i].equalsIgnoreCase("No") || buttonTexts[i]
									.equalsIgnoreCase("Cancel"))) {
						/*
						 * view.setBackgroundColor(0xf1f1f1f1);
						 * android.widget.LinearLayout.LayoutParams paramxx =
						 * new android.widget.LinearLayout.LayoutParams( 1,
						 * (int) Utilities.convertDpToPixel(40, this), 0.5f);
						 * view.setLayoutParams(paramxx);
						 * buttonContainer.addView(view);
						 */
						button.setBackgroundResource(R.drawable.alertbuttonno);
					}
					buttonContainer.addView(button);
				}
			} else {
				Button button = new Button(this);
				button.setLayoutParams(param);
				button.setText("Ok");
				button.setTextColor(0xffffffff);
				// button.setHeight(Utilities.convertDpToPixel(40, this)) ;
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// buttonListeners[v.getId()].onClick(null, v.getId());
						cancelAlert();
						lDialog.dismiss();
					}
				});
				button.setBackgroundResource(R.drawable.transbuttion);
				buttonContainer.addView(button);

			}

			//			Utilities.startAnimition(this, con, R.anim.grow_from_midddle);
			//			if (!((Activity) BusinessProxy.sSelf.addPush.mContext)
			//					.isFinishing())
			lDialog.show();
		} catch (Exception e) {
			System.out
			.println("##################### EXCEPTION CATCHED ############################");
			e.printStackTrace();
		}

	}

	public void showProgressDialog1(final String pTitle, final String pMsg,
			final int[] buttonTypes, final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showProgressDialog(pTitle, pMsg, buttonTypes, buttonTexts,
						buttonListeners, false);
			}
		});
	}
	public void showProgressFull(final String pTitle, final String pMsg,
			final int[] buttonTypes, final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners, final boolean noBG) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showProgressDialog(pTitle, pMsg, buttonTypes, buttonTexts, buttonListeners, noBG);
			}
		});
	}

	public Dialog lDialogProgress;

	public void showProgressDialog(String pTitle, final String pMsg,
			final int[] buttonTypes, final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners, boolean noBG) {
		try {

			if (lDialogProgress != null && lDialogProgress.isShowing()) {
				lDialogProgress.dismiss();
				// lDialogProgress.
				lDialogProgress = null;
			}

			// if(lDialogProgress == null)
			{
				lDialogProgress = new Dialog(this,
						android.R.style.Theme_Translucent_NoTitleBar) {
					public void onBackPressed() {
					};
				};

				lDialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
				// LayoutInflater inflator =
				// (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				// View listItemView =
				// inflator.inflate(R.layout.r_okcanceldialogview_dialog, null);
				lDialogProgress.setContentView(R.layout.r_okcanceldialogview_dialog);
				if(noBG){
					((LinearLayout)lDialogProgress.findViewById(R.id.con)).setBackgroundResource(0);//ackgroundDrawable(null);
				}
				/*
				 * if(buttonTypes!=null && buttonTypes.length==0){
				 * 
				 * ((LinearLayout)lDialogProgress.findViewById(R.id.con)).
				 * setBackgroundResource(R.drawable.diloag_bg); }else{
				 * 
				 * ((LinearLayout)lDialogProgress.findViewById(R.id.con)).
				 * setBackgroundResource(R.drawable.diloag_bg); }
				 * if(buttonTypes==null)
				 */
				// ((LinearLayout)lDialogProgress.findViewById(R.id.con)).setBackgroundResource(R.drawable.diloag_bg);
			}
			((TextView) lDialogProgress.findViewById(R.id.dialog_title))
			.setText(pTitle);
			if (pTitle == null || pTitle.length() <= 0)
				lDialogProgress.findViewById(R.id.dialog_title).setVisibility(
						View.GONE);

			WindowManager.LayoutParams lp = lDialogProgress.getWindow()
					.getAttributes();
			lp.dimAmount = Constants.dimamount;
			lDialogProgress.getWindow().setAttributes(lp);
			// lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			lDialogProgress.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);

			ProgressBar pg = ((ProgressBar) lDialogProgress
					.findViewById(R.id.progressBar1));
			pg.setProgress(0);
			if(noBG)
			{
				MarginLayoutParams params = (MarginLayoutParams) ((LinearLayout) lDialogProgress.findViewById(R.id.dialog_message_layout)).getLayoutParams();
				params.topMargin = 200;
				((LinearLayout) lDialogProgress.findViewById(R.id.dialog_message_layout)).setLayoutParams(params);
			}
			// pg.setin
			((TextView) lDialogProgress.findViewById(R.id.dialog_message))
			.setText(pMsg);
			LinearLayout buttonContainer = ((LinearLayout) lDialogProgress
					.findViewById(R.id.buttonContainer));// .setText(pMsg);
			LinearLayout con = ((LinearLayout) lDialogProgress
					.findViewById(R.id.con));
			buttonContainer.removeAllViews();
			android.widget.LinearLayout.LayoutParams param = new android.widget.LinearLayout.LayoutParams(
					0, (int) Utilities.convertDpToPixel(40, this), 0.5f);
			param.leftMargin = (int) Utilities.convertDpToPixel(5, this);
			param.rightMargin = (int) Utilities.convertDpToPixel(5, this);

			if (null != buttonTypes) {
				// if(buttonTypes.length==2)
				// ((TextView)lDialogProgress.findViewById(R.id.divider)).setVisibility(View.VISIBLE);
				// else
				// ((TextView)lDialogProgress.findViewById(R.id.divider)).setVisibility(View.GONE);
				for (int i = 0; i < buttonTypes.length; i++) {
					Button button = new Button(this);
					button.setLayoutParams(param);
					button.setText(buttonTexts[i]);
					button.setTextColor(0xf1f1f1f1);
					button.setId(i);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(final View v) {
							runOnUiThread(new Runnable() {
								public void run() {
									int btnIndex = v.getId();
									try {
										btnIndex = buttonTypes[v.getId()];
									} catch (Exception e) {

									}
									if (buttonListeners[v.getId()] != null)
										buttonListeners[v.getId()].onClick(
												null, btnIndex);
									if (lDialogProgress != null
											&& lDialogProgress.isShowing())
										lDialogProgress.dismiss();
								}
							});
							// 000647780
						}
					});
					button.setBackgroundResource(R.drawable.transbuttion);
					buttonContainer.addView(button);
				}
			} else {
				(lDialogProgress.findViewById(R.id.loading_div1))
				.setVisibility(View.GONE);
				(lDialogProgress.findViewById(R.id.loading_div2))
				.setVisibility(View.GONE);
				// ((TextView)lDialogProgress.findViewById(R.id.divider)).setVisibility(View.GONE);
			}

			Utilities.startAnimition(this, con, R.anim.grow_from_midddle);
			lDialogProgress.show();
		} catch (Exception e) {
			System.out
			.println("##################### EXCEPTION CATCHED ############################");
			e.printStackTrace();
		}
	}

	AlertDialog chatAlertDialog;

	protected final void showChatAlertMessage(final String title,
			final String message, final int[] buttonTypes,
			final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners) {
		// if (null == message || (alertDialog != null
		// &&alertDialog.isShowing())) {
		if (chatAlertDialog != null && chatAlertDialog.isShowing()) {
			chatAlertDialog.cancel();
		}
		if (null == message) {
			return;
		}
		if ((null != buttonTypes && (null == buttonTexts || null == buttonListeners))
				|| (null != buttonTexts && (null == buttonTypes || null == buttonListeners))
				|| (null != buttonListeners && (null == buttonTexts || null == buttonTypes))) {
			return;
		}
		if ((null != buttonTypes && null != buttonTexts && null != buttonListeners)
				&& ((buttonTypes.length != buttonTexts.length) || (buttonListeners.length != buttonTexts.length))) {
			return;
		}
		runOnUiThread(new Runnable() {

			public void run() {
				try {
					chatAlertDialog = new AlertDialog.Builder(
							UIActivityManager.this).create();
					if (null != title) {
						chatAlertDialog.setTitle(title);
					}
					chatAlertDialog.setMessage(message);
					if (null != buttonTypes) {
						for (int i = 0; i < buttonTypes.length; i++) {
							chatAlertDialog.setButton(buttonTypes[i],
									buttonTexts[i], buttonListeners[i]);
						}
					} else {
						chatAlertDialog.setButton(
								DialogInterface.BUTTON_NEUTRAL, "OK",
								(DialogInterface.OnClickListener) null);
					}
					chatAlertDialog.setOnKeyListener(SEARCH_KEY_HANDLER);
					chatAlertDialog.setCancelable(false);
					chatAlertDialog.show();
				} catch (Exception e) {
					System.out
					.println("##################### EXCEPTION CATCHED ############################");
					e.printStackTrace();
				}
			}
		});
	}

	public final void showAlertMessage(final String title,

			final String message, final int[] buttonTypes, final String[] buttonTexts,
			final DialogInterface.OnClickListener[] buttonListeners) {
		Constants.ERROR = null;
		/*
		 * if (null == message) { return; } if ((null != buttonTypes && (null ==
		 * buttonTexts || null == buttonListeners)) || (null != buttonTexts &&
		 * (null == buttonTypes || null == buttonListeners)) || (null !=
		 * buttonListeners && (null == buttonTexts || null == buttonTypes))) {
		 * return; } if ((null != buttonTypes && null != buttonTexts && null !=
		 * buttonListeners) && ((buttonTypes.length != buttonTexts.length) ||
		 * (buttonListeners.length != buttonTexts.length))) { return; } if
		 * (alertDialog != null && alertDialog.isShowing()) { return; }
		 * runOnUiThread(new Runnable() { public void run() { alertDialog = new
		 * AlertDialog.Builder(UIActivityManager.this).create(); if (null !=
		 * title) { alertDialog.setTitle(title); }
		 * alertDialog.setMessage(message); if (null != buttonTypes) { for (int
		 * i = 0; i < buttonTypes.length; i++) {
		 * alertDialog.setButton(buttonTypes[i], buttonTexts[i],
		 * buttonListeners[i]); } } else {
		 * alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK",
		 * (DialogInterface.OnClickListener) null); }
		 * alertDialog.setOnKeyListener(SEARCH_KEY_HANDLER);
		 * alertDialog.setCancelable(false); alertDialog.show(); } });
		 */
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showCustomMessage(title, message, buttonTypes, buttonTexts,
						buttonListeners);
			}
		});

	}

	// @ Add notification to the notification bar
	Notification mNotification;
	public static NotificationManager mNotificationManager;
	boolean toggle;
	int HELLO_ID = 1;

	public void addNotification(final String aTickerText,
			final String aContentText) {
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				showNotification(aTickerText, aContentText);
			}
		}).start();
	}

	public void addNotificationLocal(final String aTickerText,
			final String aContentText) {
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				showNotificationNoBackBround(aTickerText, aContentText);
			}
		}).start();
	}

	public void cancelNotification() {
		if (mNotificationManager != null)
			mNotificationManager.cancelAll();// cancel(HELLO_ID);
	}

	public void showNotification(String aTickerText, String aContentText) {
		if (!sIsRunningInBackGround)
			return;
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
			return;
		}
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		try {
			CharSequence contentTitle = "RockeTalk"; // expanded message title
			// CharSequence tickerText =
			// "You have new message in RockeTalk Inbox! Please click on the message to view!!";
			// // ticker-text
			// CharSequence contentText =
			// "You have new message in RockeTalk Inbox! Please click on the message to view!!";
			// // expanded message text
			int icon = R.drawable.pushicon; // icon from resources
			long when = System.currentTimeMillis(); // notification time - When
			// to notify
			Context context = getApplicationContext(); // application Context
			if (mNotificationManager != null)
				mNotificationManager.cancel(HELLO_ID);
			if (toggle) {
				HELLO_ID = 2;
				toggle = false;
			} else {
				HELLO_ID = 1;
				toggle = true;
			}
			// --------------------------Notification
			// Test------------------------------
			// the next two lines initialize the Notification, using the
			// configurations and different flags to set
			if (mNotification == null) {
				mNotification = new Notification(icon, aTickerText, when);
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotification.defaults |= Notification.DEFAULT_LIGHTS;
			}
			mNotification.number = BusinessProxy.sSelf.newMessageCount;
			mNotification.tickerText = aTickerText;
			// Default
			/*
			 * Intent notificationIntent = new Intent(this,
			 * InboxActivity.class);//(UIActivityManager.this,
			 * ComposeActivity.class);
			 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			 * Intent.FLAG_ACTIVITY_SINGLE_TOP); PendingIntent contentIntent =
			 * PendingIntent.getActivity(this, 0, notificationIntent, 0);
			 * mNotification.setLatestEventInfo(context, contentTitle,
			 * aContentText, contentIntent); //if(mNotificationManager == null)
			 * mNotificationManager = (NotificationManager)
			 * getSystemService(Context.NOTIFICATION_SERVICE);
			 * mNotificationManager.notify(HELLO_ID, mNotification);
			 */

			// Custom
			Intent notificationIntent = new Intent(this,
					KainatHomeActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			mNotification.contentIntent = contentIntent;
			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.noti_alert);
			contentView.setImageViewResource(R.id.image, R.drawable.pushicon);
			contentView.setTextViewText(R.id.text, aContentText);// "Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
			mNotification.contentView = contentView;

			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(HELLO_ID, mNotification);
			// /
		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println("Error - " + ex.toString());
		}

	}

	public void showNotificationNoBackBround(String aTickerText,
			String aContentText) {
		// if (!sIsRunningInBackGround)
		// return;
		if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
			return;
		}
		if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
			return;
		}
		try {
			CharSequence contentTitle = "RockeTalk"; // expanded message title
			// CharSequence tickerText =
			// "You have new message in RockeTalk Inbox! Please click on the message to view!!";
			// // ticker-text
			// CharSequence contentText =
			// "You have new message in RockeTalk Inbox! Please click on the message to view!!";
			// // expanded message text
			int icon = R.drawable.pushicon; // icon from resources
			long when = System.currentTimeMillis(); // notification time - When
			// to notify
			Context context = getApplicationContext(); // application Context
			if (mNotificationManager != null)
				mNotificationManager.cancel(HELLO_ID);
			if (toggle) {
				HELLO_ID = 2;
				toggle = false;
			} else {
				HELLO_ID = 1;
				toggle = true;
			}
			// --------------------------Notification
			// Test------------------------------
			// the next two lines initialize the Notification, using the
			// configurations and different flags to set
			if (mNotification == null) {
				mNotification = new Notification(icon, aTickerText, when);
				mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
				mNotification.defaults |= Notification.DEFAULT_LIGHTS;
			}
			mNotification.number = BusinessProxy.sSelf.newMessageCount;
			mNotification.tickerText = aTickerText;
			// Default
			/*
			 * Intent notificationIntent = new Intent(this,
			 * InboxActivity.class);//(UIActivityManager.this,
			 * ComposeActivity.class);
			 * notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			 * Intent.FLAG_ACTIVITY_SINGLE_TOP); PendingIntent contentIntent =
			 * PendingIntent.getActivity(this, 0, notificationIntent, 0);
			 * mNotification.setLatestEventInfo(context, contentTitle,
			 * aContentText, contentIntent); //if(mNotificationManager == null)
			 * mNotificationManager = (NotificationManager)
			 * getSystemService(Context.NOTIFICATION_SERVICE);
			 * mNotificationManager.notify(HELLO_ID, mNotification);
			 */

			// Custom
			Intent notificationIntent = new Intent(this,
					KainatHomeActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, 0);
			mNotification.contentIntent = contentIntent;
			RemoteViews contentView = new RemoteViews(getPackageName(),
					R.layout.noti_alert);
			contentView.setImageViewResource(R.id.image, R.drawable.pushicon);
			contentView.setTextViewText(R.id.text, aContentText);// "Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
			mNotification.contentView = contentView;

			mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(HELLO_ID, mNotification);
			// /
		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println("Error - " + ex.toString());
		}

	}

	public void removeAllNotification() {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}

	public final String ALERT_USER_PROF = "This profile will be delivered to your system messages shortly. Meanwhile, please feel free to browse!";
	public final String ALERT_MEDIA_MORE = "This page will be delivered to your system messages shortly. Meanwhile, please feel free to browse!";
	public final String ALERT_MEDIA_CLICK = "This media will be delivered to your system messages shortly. Meanwhile, please feel free to browse!";
	public final String ALERT_DWNLOAD_FUN = "This download will be delivered to your system messages shortly. Meanwhile, please feel free to browse!";
	public final String ALERT_POST_CLICK = "Your media will be posted in the relevant category shortly. Please be patient, this may take upto 24 hours!";
	public final String ALERT_REFRESH = "You will get a notification in your system messages when this page is refreshed!";
	public final String ALERT_FRND_INVITE_HELLO = "Your message has been sent to the user(s). Their response will be delivered in your system messages shortly!";
	public final String ALERT_SAVE_MSG = "This message has been saved. You can get it again in your inbox by clicking on 'Inbox options' and 'Get saved messages'.";
	public final String ALERT_BOOKMRK_CLICK = "This user has been added to your friend's list. You can now see their online/offline status!";
	public final String ALERT_DEFAULT = "Your request has been sent. You will receive a response shortly in your system messages.";
	public final String ALERT_SUBMITT = "Thank you for submitting this information. We will get in touch with you shortly!";
	public final String ALERT_VERIFICATION = "Your opinion has been submitted. You will get a poll result soon in your system messages. Thank you for your participation!";
	public final String ALERT_FRND_INVITE = "Your invite has been sent to this user. His/Her response will be delivered in your system messages shortly!";
	public final String RECV_SHORTLY = "Your request will be sent to your system messages shortly. Meanwhile feel free to browse around!";

	public void showAppropriateAlert(int aIndex) {
		String alertText;
		switch (aIndex) {
		case 1:
			alertText = ALERT_REFRESH;
			break;
		case 2:
			alertText = ALERT_USER_PROF;
			break;
		case 3:
			alertText = ALERT_MEDIA_MORE;
			break;
		case 4:
			alertText = ALERT_MEDIA_CLICK;
			break;
		case 5:
			alertText = ALERT_DWNLOAD_FUN;
			break;
		case 6:
			alertText = ALERT_POST_CLICK;
			break;
		case 7:
			// alertText = TextData.ALERT_FRND_INVITE_HELLO;
			alertText = ALERT_FRND_INVITE;
			break;
		case 8:
			alertText = ALERT_SAVE_MSG;
			break;
		case 9:
			alertText = ALERT_BOOKMRK_CLICK;
			break;
		case 10:
			alertText = ALERT_DEFAULT;
			break;
		case 11:
			alertText = ALERT_SUBMITT;
			break;
		case 12:
			alertText = ALERT_VERIFICATION;
			break;
		case 13:
			alertText = ALERT_FRND_INVITE;
			break;
		default:
			alertText = RECV_SHORTLY;
			break;
		}
		showAlertMessage("Info", alertText, null, null, null);
		alertText = null;
	}

	public final void TransportNotification(ResponseObject aResponse) {
		// System.out.println("notificationFromTransport-- Response Code= " +
		// aResponse.getResponseCode() + "  Sent Op= " + aResponse.getSentOp());
		if (loginRes)
			return;
		//		if (Logger.ENABLE)
		//			Logger.debug(TAG, "TransportNotification-- Entry");
		//		try {
		//			InboxTblObject inboxData = (InboxTblObject) aResponse.getData();
		//			// addNotification(NEW_MESSAGE_ALERT1 + "nage1111",
		//			// NEW_MESSAGE_ALERT1 + "2222nage");
		//			if (aResponse.getError() == Constants.ERROR_NONE) {
		//				switch (aResponse.getResponseCode()) {
		//				case Constants.RES_TYPE_NO_VOICE:
		//				case Constants.RES_TYPE_SUCCESS:
		//				case Constants.RES_TYPE_HARD_UPGRADE:
		//				case Constants.RES_TYPE_SOFT_UPGRADE:
		//				case Constants.RES_TYPE_ACTIVATION_NOT_DONE:
		//				case Constants.RES_TYPE_SOFT_UPGRADE_WITH_VERIFICATION:
		//				case Constants.RES_TYPE_USER_ID_AVAILABLE:
		//					switch (aResponse.getSentOp()) {
		//					case Constants.FRAME_TYPE_UPDATE_PROFILE:
		//						break;
		//					case Constants.FRAME_TYPE_INBOX_PLAY:
		//						if (BusinessProxy.sSelf.addPush != null
		//						&& BusinessProxy.sSelf.addPush
		//						.isAddDisplaying()) {
		//							// System.out.println("lDialogProgress=="+lDialogProgress);
		//							if (lDialogProgress != null
		//									&& lDialogProgress.isShowing()) {
		//								lDialogProgress.dismiss();
		//								// break;
		//							}
		//							InboxTblObject inboxDataT = (InboxTblObject) aResponse
		//									.getData();
		//							switch (aResponse.getSentOp()) {
		//							case Constants.FRAME_TYPE_INBOX_PLAY:
		//								BusinessProxy.sSelf.addPush.closeAdd();
		//								// System.out.println("-----------------------sending sunch req from add sent op code -----------"+aResponse.getSentOp());
		//								if (inboxData == null
		//										|| inboxData.getItemCount() == 0) {
		//									showAlertMessage("Error",
		//											"Sorry! No content available!!",
		//											null, null, null);
		//								} else {
		//									InboxMessage mInboxData = new InboxMessage();
		//									mInboxData.mId = (String) inboxData.mMessageId
		//											.get(0);
		//									mInboxData.mSenderName = inboxData.mName
		//											.get(0);
		//									mInboxData.mRecepients = inboxData.mPhoneNumber
		//											.get(0);
		//									mInboxData.mTime = inboxData.mReceivingTime
		//											.get(0);
		//									mInboxData.mBitmap = inboxData.mBitmap.get(
		//											0).intValue();
		//									mInboxData.mPayload = inboxData.mPayload
		//											.get(0);
		//									if ((mInboxData.mBitmap & Payload.PAYLOAD_BITMAP_RTML) > 0) {
		//
		//										// Check if size is 50 the remove first
		//										// element
		//										if (Proxy.sSelf.iCardNameStr.size() == 50) {
		//											Proxy.sSelf.iSyncCallData
		//											.remove((String) Proxy.sSelf.iCardNameStr
		//													.elementAt(0));
		//											Proxy.sSelf.iCardNameStr
		//											.removeElementAt(0);
		//
		//										}
		//
		//										Proxy.sSelf.iCardNameStr
		//										.addElement(Proxy.sSelf.iCurrDisplayName);
		//										try {
		//											if (Proxy.sSelf.iSyncCallData == null)
		//												Proxy.sSelf.iSyncCallData = new Hashtable<Object, Object>(
		//														50);// to cash rtml data
		//											Proxy.sSelf.iSyncCallData
		//											.put(Proxy.sSelf.iCurrDisplayName,
		//													Proxy.sSelf.iCurrPayloadData);
		//										} catch (Exception e) {
		//											// TODO: handle exception
		//										}
		//										// showCustomData(mInboxData);
		//										if (Proxy.sSelf.iCurrCardName != null)
		//											Proxy.sSelf.iCurrDisplayName = Proxy.sSelf.iCurrCardName;
		//										if (BusinessProxy.sSelf.iKeyForAgent != null)
		//											Proxy.sSelf.iCurrDisplayName = BusinessProxy.sSelf.iKeyForAgent;
		//										Proxy.sSelf.iCurrPayloadData = mInboxData;
		//										DataModel.sSelf.storeObject(MessageActivity.MESSAGE_DATA,mInboxData);
		//										Intent intent = new Intent(this, MessageActivity.class);
		//										intent.putExtra("force_message_play",
		//												true);
		//										startActivity(intent);
		//									} else if ((0 < (mInboxData.mBitmap & Payload.PAYLOAD_BITMAP_PICTURE))
		//											&& (0 < (mInboxData.mBitmap & Payload.PAYLOAD_BITMAP_TEXT))
		//											&& (0 < (mInboxData.mBitmap & Payload.PAYLOAD_BITMAP_VOICE))) {
		//										// open message activity
		//										DataModel.sSelf.storeObject(MessageActivity.MESSAGE_DATA, mInboxData);
		//										Intent intent = new Intent(this, MessageActivity.class);
		//										intent.putExtra("force_message_play",
		//												true);
		//										startActivity(intent);
		//									} else if ((0 < (mInboxData.mBitmap & Payload.PAYLOAD_BITMAP_VIDEO))) {
		//										Intent intent = new Intent(
		//												UIActivityManager.this,
		//												VideoPlayer.class);
		//										intent.putExtra("MODE", "buffer");
		//										DataModel.sSelf
		//										.storeObject(
		//												DMKeys.VIDEO_DATA_TYPE,
		//												mInboxData.mPayload.mVideoType[0]);
		//										// intent.putExtra("DATA",
		//										// mInboxData.mPayload.mVideo[0]);
		//										DataModel.sSelf.storeObject(
		//												DMKeys.VIDEO_DATA,
		//												mInboxData.mPayload.mVideo[0]);
		//										startActivity(intent);
		//										return;
		//									}
		//								}
		//								break;
		//							default:
		//								break;
		//							}
		//
		//							return;
		//						}
		//						break;
		//					case Constants.FRAME_TYPE_SIGNUP:
		//					case Constants.FRAME_TYPE_LOGIN:
		//						// BusinessProxy.sSelf.iNotificationMessage =
		//						// "Hi this is mahesh!";
		//						// if(!LoginActivity.twoUser)
		//						// {
		//						if (aResponse.getResponseCode() != Constants.RES_TYPE_USER_ID_AVAILABLE)
		//						{
		////							handleLoginResponse(aResponse);
		//						}
		//						if (null != SettingData.sSelf
		//								&& SettingData.sSelf.isRemember()) {
		//							if (null != aResponse.getBuddyObject()) {
		//								BusinessProxy.sSelf.mBuddyInfo = new InboxTblObject(
		//										InboxTblObject.BUDDY_LIST);
		//								BusinessProxy.sSelf.mBuddyInfo
		//								.addSpecificFields(aResponse
		//										.getBuddyObject());
		//							}
		//							if (null != aResponse.getGroupObject()) {
		//								BusinessProxy.sSelf.mGroupInfo = new InboxTblObject(
		//										InboxTblObject.GROUP_LIST);
		//								BusinessProxy.sSelf.mGroupInfo
		//								.addSpecificFields(aResponse
		//										.getGroupObject());
		//							}
		//							if (null != aResponse.getIMBuddyObject()) {
		//								BusinessProxy.sSelf.mIMBuddyInfo = new InboxTblObject(
		//										InboxTblObject.IM_LIST);
		//								BusinessProxy.sSelf.mIMBuddyInfo
		//								.addSpecificFields(aResponse
		//										.getIMBuddyObject());
		//							}
		//							BusinessProxy.sSelf.startRefresh();
		//						}
		//						// }
		//						break;
		//						// case Constants.FRAME_TYPE_LOGOFF:
		//
		//					case Constants.FRAME_TYPE_SEARCH_OTS_BUDDY:
		//					case Constants.FRAME_TYPE_PHCONTACTS_INVITE:
		//						// iInboxData = inboxData;
		//						break;
		//					case Constants.FRAME_TYPE_INBOX_MORE:
		//						updateBuddyAndGroup(aResponse);
		//						if (aResponse.getClientControl() != null
		//								&& aResponse.getClientControl().length() > 0) {
		//							if (Logger.ENABLE)
		//								Logger.info(TAG,
		//										"TransportNotification -- Got Stat control in FRAME_TYPE_INBOX_MORE request "
		//												+ aResponse.getClientControl());
		//							parseAndSetStat(aResponse.getClientControl());
		//						}
		//						break;
		//					case Constants.FRAME_TYPE_INBOX_DEL:
		//					case Constants.FRAME_TYPE_INBOX_DEL_ALL:
		//						// case Constants.FRAME_TYPE_DEL_OTS_BUDDY:
		//						// case Constants.FRAME_TYPE_DEL_ALL_OTS_BUDDY:
		//					case Constants.FRAME_TYPE_VTOV:
		//					case Constants.FRAME_TYPE_INBOX_CHECK_NEW:
		//					case Constants.FRAME_TYPE_INBOX_REFRESH:
		//						// case Constants.FRAME_TYPE_GROUP_CREATE_NEW:
		//						// case Constants.FRAME_TYPE_GROUP_DEL:
		//						// case Constants.FRAME_TYPE_GROUP_ADD_MEM:
		//						// case Constants.FRAME_TYPE_GROUP_DETAILS:
		//						// case Constants.FRAME_TYPE_GROUP_DEL_MEM:
		//						// case
		//						// Constants.FRAME_TYPE_GROUP_UPDATE_MEM_STATUS:
		//						String sender = null;
		//						if (BusinessProxy.sSelf.isRegistered()) {
		//							if (aResponse.getMessageCount() > 0) {
		//								if (Logger.ENABLE)
		//									Logger.debug(
		//											TAG,
		//											"TransportNotification--ADD MESSAGES. COUNT = "
		//													+ aResponse
		//													.getMessageCount());
		//								if (inboxData != null
		//										&& inboxData.getItemCount() > 0) {
		//									String msgId;
		//									int drmForSelMsg;
		//									int notification = 0;
		//									// int bitmap = 0;
		//									int itemCount = inboxData.getItemCount();
		//									// int topMsgId = 0 ;
		//									for (int pos = 0; pos < itemCount; pos++) {
		//										msgId = (String) inboxData
		//												.getField(
		//														pos,
		//														InboxTblObject.INBOX_FIELD_MSG_ID);
		//										if (msgId != null) {
		//											BusinessProxy.sSelf
		//											.setTopMessageIdOnClient(msgId);
		//										}
		//										drmForSelMsg = ((Integer) inboxData
		//												.getField(
		//														pos,
		//														InboxTblObject.INBOX_FIELD_MSG_DRM))
		//														.intValue();
		//										// drmForSelMsg = 16;
		//										sender = ((String) inboxData
		//												.getField(
		//														pos,
		//														InboxTblObject.INBOX_FIELD_NAME));
		//										if (sender.indexOf(SettingData.sSelf
		//												.getUserName()) != -1) {
		//											// inboxData =rtAddData ;
		//											// rtadd(inboxData, pos);
		//										}
		//										if (-1 < sender.indexOf(';')) {
		//											sender = sender.substring(0,
		//													sender.indexOf(";")).trim();
		//										}
		//
		//										if (Logger.ENABLE) {
		//											Logger.debug(TAG,
		//													"TransportNotification()--[WARNING] - drmForSelMsg - "
		//															+ drmForSelMsg);
		//										}
		//										if (0 < (InboxTblObject.DRM_SYSTEM_MSG & drmForSelMsg)) {
		//											if (Logger.ENABLE) {
		//												Logger.debug(TAG,
		//														"--TransportNotification()--[WARNING] - DRM_SYSTEM_MSG - ");
		//											}
		//											if (sender.indexOf("a:tickermgr") != -1) {
		//												if (!InboxTblObject
		//														.isMessageIdExistInDB(
		//																DBEngine.INBOX_TABLE,
		//																((String) inboxData
		//																		.getField(
		//																				pos,
		//																				InboxTblObject.INBOX_FIELD_MSG_ID)))) {
		//													BusinessProxy.sSelf
		//													.setTopMessageIdOnClient((String) inboxData
		//															.getField(
		//																	pos,
		//																	InboxTblObject.INBOX_FIELD_MSG_ID));
		//												}
		//												getTickerMessageAndUpdate(
		//														inboxData, pos);
		//											} else {
		//												// 1 For agent Table
		//												inboxData.addAgentMessage(
		//														aResponse, 1, false,
		//														pos, 1);
		//												BusinessProxy.sSelf
		//												.setInboxdata(inboxData);
		//												updateInfoForBlinkOnGrid(sender);
		//											}
		//											if (sender
		//													.indexOf(SettingData.sSelf
		//															.getUserName()) != -1) {
		//												// inboxData =rtAddData ;
		//												// rtadd(inboxData, pos);
		//											}
		//										} else if (0 < (InboxTblObject.DRM_AUTOPLAY & drmForSelMsg)) {
		//											// Right here code to open RTML
		//											// message
		//											switch (sScreenNumber) {
		//											case SCREEN_BUDDY:
		//											case SCREEN_INBOX:
		//											case SCREEN_HOME:
		//											case SCREEN_INVITE:
		//											case SCREEN_SEARCH_RESULT:
		//											case SCREEN_SETTING:
		//												InboxMessage message = new InboxMessage();
		//												message.mId = (String) inboxData.mMessageId
		//														.get(0);
		//												message.mSenderName = inboxData.mName
		//														.get(0);
		//												message.mRecepients = inboxData.mPhoneNumber
		//														.get(0);
		//												message.mTime = inboxData.mReceivingTime
		//														.get(0);
		//												message.mBitmap = inboxData.mBitmap
		//														.get(0).intValue();
		//												message.mPayload = inboxData.mPayload
		//														.get(0);
		//												DataModel.sSelf
		//												.storeObject(
		//														MessageActivity.MESSAGE_DATA,
		//														message);
		//												Intent intent = new Intent(
		//														this,
		//														MessageActivity.class);
		//												intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
		//														| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//												DataModel.sSelf
		//												.storeObject(
		//														DMKeys.NEW_INTENT,
		//														true);
		//												startActivityForResult(intent,
		//														999);
		//											}
		//										} else {
		//											switch (sScreenNumber) {
		//											case SCREEN_LOGIN:
		//												break;
		//											default:
		//												notification = ((Integer) inboxData
		//														.getField(
		//																pos,
		//																InboxTblObject.INBOX_FIELD_MSG_NOTIFICATION))
		//																.intValue();
		//												if (Logger.ENABLE) {
		//													Logger.info(
		//															TAG,
		//															"TransportNotification-- Curr Ui = "
		//																	+ sScreenNumber
		//																	+ ", and DRM for message at pos - "
		//																	+ pos
		//																	+ " is = "
		//																	+ drmForSelMsg);
		//												}
		//												Integer buzzTime = DataModel.sSelf
		//														.getInt(DMKeys.BUZZ_TIME);
		//												if (Logger.ENABLE) {
		//													Logger.debug(
		//															TAG,
		//															"TransportNotification(): buzz autoplay time = "
		//																	+ (null == buzzTime ? 0
		//																			: buzzTime
		//																			.intValue())
		//																			+ ", and time diff = "
		//																			+ (null == buzzTime ? 0
		//																					: buzzTime
		//																					.intValue()));
		//												}
		//												if (!((InboxTblObject.NOTI_BUZZ_AUTOPLAY & notification) > 0)
		//														&& !((InboxTblObject.NOTI_ROCKET_BUZZ & notification) > 0 || (InboxTblObject.NOTI_ROCKET_BUZZ_REPLY & notification) > 0)) {
		//													if ((InboxTblObject.NOTI_CHAT_MSG & notification) > 0) {
		//														// bitmap = ((Integer)
		//														// inboxData.getField(pos,
		//														// InboxTblObject.INBOX_FIELD_PAYLOAD_BITMAP)).intValue();
		//														String currentChatter = "";// DataModel.sSelf.getString(DMKeys.CURRENT_CHAT_USER_NAME);
		//														if ("".equals(currentChatter)) {
		//															currentChatter = sender;
		//															DataModel.sSelf
		//															.storeObject(
		//																	DMKeys.CURRENT_CHAT_USER_NAME,
		//																	currentChatter);
		//														}
		//														if (!BusinessProxy.sSelf
		//																.isMyChatOpen(sender
		//																		.trim())) {
		//															final Payload payload = inboxData.mPayload
		//																	.get(pos);
		//															final String rTime = inboxData.mReceivingTime
		//																	.get(pos);
		//															// Show a popup
		//															// Screen showing
		//															// that
		//															final String name = sender
		//																	.trim();
		//															DialogInterface.OnClickListener blockHandler = new DialogInterface.OnClickListener() {
		//
		//																public void onClick(
		//																		DialogInterface dialog,
		//																		int which) {
		//																	/*
		//																	 * List<Chat>
		//																	 * chatDataList
		//																	 * = new
		//																	 * ArrayList
		//																	 * <Chat>();
		//																	 * BusinessProxy
		//																	 * .sSelf.
		//																	 * chatData
		//																	 * .put
		//																	 * (name,
		//																	 * chatDataList
		//																	 * ); Chat
		//																	 * chat =
		//																	 * new
		//																	 * Chat();
		//																	 * chat
		//																	 * .payload
		//																	 * =
		//																	 * payload;
		//																	 * chat
		//																	 * .userName
		//																	 * = name;
		//																	 * chat
		//																	 * .timestamp
		//																	 * = rTime ;
		//																	 * chatDataList
		//																	 * .
		//																	 * add(chat)
		//																	 * ;
		//																	 * DataModel
		//																	 * .sSelf.
		//																	 * storeObject
		//																	 * (DMKeys.
		//																	 * CHAT_SCREEN_USER_NAME
		//																	 * , name);
		//																	 * pushNewScreen
		//																	 * (
		//																	 * ChatActivity
		//																	 * .class,
		//																	 * false);
		//																	 */
		//																	InboxMessage msg = new InboxMessage();
		//																	msg.mTime = rTime;
		//																	if (BusinessProxy.sSelf.chatData
		//																			.get(name) == null) {
		//																		List<Chat> mCurrentUserChatData = Utilities
		//																				.getChatData(SettingData.sSelf
		//																						.getUserName()
		//																						+ "_"
		//																						+ name);
		//																		if (mCurrentUserChatData != null)
		//																			BusinessProxy.sSelf.chatData
		//																			.put(name,
		//																					mCurrentUserChatData);
		//																		if (Logger.ENABLE)
		//																			Logger.debug(
		//																					TAG,
		//																					"loding data from file system......");
		//																	}
		//																	int insertAt = BusinessProxy.sSelf
		//																			.insertChat(
		//																					name,
		//																					payload,
		//																					msg);
		//																	//
		//																	BusinessProxy.sSelf
		//																	.initOpenchatList();
		//																	dismissAnimationDialog();
		//																	DataModel.sSelf
		//																	.storeObject(
		//																			DMKeys.CHAT_SCREEN_USER_NAME,
		//																			name);
		//																	DataModel.sSelf
		//																	.storeObject(
		//																			DMKeys.CHAT_INSERT_AT,
		//																			insertAt);
		//																	// pushNewScreen(
		//																	// ChatActivity.class,
		//																	// false);
		//
		//																}
		//															};
		//															// BusinessProxy.sSelf.isMyChatOpen(name);
		//															ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		//															String clazzName = am
		//																	.getRunningTasks(
		//																			1)
		//																			.get(0).topActivity
		//																			.getShortClassName();
		//															if (!clazzName
		//																	.equalsIgnoreCase("VerificationActivity")
		//																	&& !BusinessProxy.sSelf.addPush
		//																	.isAddDisplaying()
		//																	&& !BusinessProxy.sSelf.addPush.isFormDisplay) {
		//																showChatAlertMessage(
		//																		"Confirmation",
		//																		"You have received a chat message from "
		//																				+ name
		//																				+ ". Do you want to chat with "
		//																				+ sender
		//																				+ "?",
		//																				new int[] {
		//																				DialogInterface.BUTTON_POSITIVE,
		//																				DialogInterface.BUTTON_NEGATIVE },
		//																				new String[] {
		//																				"Yes",
		//																		"No" },
		//																		new DialogInterface.OnClickListener[] {
		//																				blockHandler,
		//																				null });
		//															}
		//														} else {
		//															Payload payload = inboxData.mPayload
		//																	.get(pos);
		//															String rTime = inboxData.mReceivingTime
		//																	.get(pos);
		//															List<Chat> chatDataList = BusinessProxy.sSelf.chatData
		//																	.get(currentChatter);
		//															if (chatDataList == null) {
		//																chatDataList = new ArrayList<Chat>();
		//																BusinessProxy.sSelf.chatData
		//																.put(currentChatter,
		//																		chatDataList);
		//															}
		//															Chat chat = new Chat();
		//															chat.payload = payload;
		//															chat.userName = currentChatter;
		//															chat.timestamp = rTime;
		//															Utilities
		//															.saveChatData(
		//																	chat,
		//																	currentChatter);
		//															chatDataList
		//															.add(chat);
		//														}
		//													}
		//												}
		//												if (((InboxTblObject.NOTI_BUZZ_AUTOPLAY & notification) > 0 && (BusinessProxy.sSelf.iBuzzPlayTime == 0 || (System
		//														.currentTimeMillis() - BusinessProxy.sSelf.iBuzzPlayTime) >= BusinessProxy.sSelf.iBuzzTime[1] * 1000))
		//														&& BusinessProxy.sSelf.addPush
		//														.isAddDisplaying()
		//														&& !BusinessProxy.sSelf.addPush.isFormDisplay) {
		//													String buzzFrom = ((String) inboxData
		//															.getField(
		//																	pos,
		//																	InboxTblObject.INBOX_FIELD_NAME));
		//													if (buzzFrom.indexOf(";") != -1)
		//														buzzFrom = buzzFrom
		//														.substring(
		//																0,
		//																buzzFrom.indexOf(";"))
		//																.trim();
		//													if (buzzFrom.indexOf("a:") != -1) {
		//														buzzFrom = BusinessProxy.sSelf
		//																.parseNameInformation(buzzFrom);
		//													}
		//													final String from = buzzFrom;
		//													String buzzCommunityFrom = ((String) inboxData
		//															.getField(
		//																	pos,
		//																	InboxTblObject.INBOX_FIELD_NUMBER));
		//													DataModel.sSelf
		//													.storeObject(
		//															DMKeys.BUZZ_FROM,
		//															buzzFrom);
		//													DataModel.sSelf
		//													.storeObject(
		//															DMKeys.BUZZ_COMMUNITY_FROM,
		//															buzzCommunityFrom);
		//													if (Logger.ENABLE)
		//														Logger.debug(
		//																TAG,
		//																"TransportNotification(): Going to play buzz from user = "
		//																		+ buzzFrom);
		//													writeProfilePayload(inboxData.mPayload
		//															.get(pos));
		//													// Autoplay Voice here.
		//													ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		//													String clazzName = am
		//															.getRunningTasks(1)
		//															.get(0).topActivity
		//															.getShortClassName();
		//													if (!clazzName
		//															.equalsIgnoreCase("VerificationActivity")) {
		//														runOnUiThread(new Runnable() {
		//															public void run() {
		//																boolean b = BusinessProxy.sSelf.mIsApplicationMute;
		//																mVoiceMedia = new VoiceMedia(
		//																		UIActivityManager.this,
		//																		null);
		//																if (sScreenNumber != SCREEN_COMPOSE
		//																		&& sScreenNumber != SCREEN_SETTING
		//																		&& sScreenNumber != SCREEN_POST
		//																		&& sScreenNumber != SCREEN_STATUS)
		//																	mVoiceMedia
		//																	.startPlayingForMute(
		//																			mVoicePath,
		//																			"Playing - Buzz From "
		//																					+ from);
		//																BusinessProxy.sSelf.mIsApplicationMute = b;
		//															}
		//														});
		//													}
		//												} else {
		//													if (mCurrentState == Constants.UI_STATE_IDLE) {
		//														MediaEngine mMediaEngine = MediaEngine
		//																.getMediaEngineInstance();
		//														if ((InboxTblObject.NOTI_ROCKET_BUZZ & notification) > 0
		//																|| (InboxTblObject.NOTI_ROCKET_BUZZ_REPLY & notification) > 0) {
		//															if (sScreenNumber != SCREEN_COMPOSE
		//																	&& sScreenNumber != SCREEN_SETTING
		//																	&& sScreenNumber != SCREEN_POST
		//																	&& sScreenNumber != SCREEN_STATUS)
		//																mMediaEngine
		//																.playResource(R.raw.rocketalert);
		//														} else if ((InboxTblObject.NOTI_CHAT_MSG & notification) > 0) {
		//															if (sScreenNumber != SCREEN_COMPOSE
		//																	&& sScreenNumber != SCREEN_SETTING
		//																	&& sScreenNumber != SCREEN_POST
		//																	&& sScreenNumber != SCREEN_STATUS)
		//																mMediaEngine
		//																.playResource(R.raw.chimeup);
		//														} else {
		//															if (mCurrentState == Constants.UI_STATE_IDLE) {
		//																if (inboxData
		//																		.getItemCount() > 1
		//																		&& (pos + 1) == inboxData
		//																		.getItemCount()) {
		//																	if (sScreenNumber != SCREEN_COMPOSE
		//																			&& sScreenNumber != SCREEN_SETTING
		//																			&& sScreenNumber != SCREEN_POST
		//																			&& sScreenNumber != SCREEN_STATUS)
		//																		mMediaEngine
		//																		.playResource(getSelectedAlertID());
		//																} else if (inboxData
		//																		.getItemCount() == 1) {
		//																	if (sScreenNumber != SCREEN_COMPOSE
		//																			&& sScreenNumber != SCREEN_SETTING
		//																			&& sScreenNumber != SCREEN_POST
		//																			&& sScreenNumber != SCREEN_STATUS)
		//																		mMediaEngine
		//																		.playResource(getSelectedAlertID());
		//																}
		//															}
		//														}
		//													}
		//													BusinessProxy.sSelf.iAgentUpdateInfo |= Constants.INBOX;
		//												}
		//												break;
		//											}
		//											if (!InboxTblObject
		//													.isMessageIdExistInDB(
		//															DBEngine.INBOX_TABLE,
		//															msgId)) {
		//												inboxData.addRecords(aResponse,
		//														DBEngine.INBOX_TABLE,
		//														pos, 1);
		//												// FIXME - Check this for
		//												// android.
		//												// resetNewReplyFwdDataForNewMessage(1);
		//												BusinessProxy.sSelf.mTotInboxMsgAtServer += 1;
		//												BusinessProxy.sSelf.iAgentUpdateInfo |= Constants.INBOX;
		//												if ((InboxTblObject.NOTI_CHAT_MSG & notification) > 0) {
		//													if (BusinessProxy.sSelf
		//															.isMyChatOpen(sender
		//																	.trim()))
		//														++BusinessProxy.sSelf.newChatMessageCount;
		//													// System.out.println("Chat message="+BusinessProxy.sSelf.newMessageCount);
		//												} else
		//													++BusinessProxy.sSelf.newMessageCount;
		//
		//												// showSimpleAlert("new message",
		//												// ""+(BusinessProxy.sSelf.newMessageCount++));
		//											} // END IF MESSAGE DOES NOT EXISTS
		//											// IN DATABASE
		//										}// END IF SYSTEM MESSAGE IS NOT THERE
		//									}// ALL MESSAGES PARSING DONE; LOOP ENDS
		//									// HERE
		//									inboxData = null;
		//									// BusinessProxy.sSelf.setTopMessageIdOnClient(topMsgId);//
		//									// top message id changed nagendra
		//								}
		//								if (BusinessProxy.sSelf.newMessageCount > 0) {
		//									if (sIsRunningInBackGround) {
		//										String s = sender;
		//										if (s.indexOf("<") != -1
		//												&& s.indexOf(">") != -1)
		//											s = s.substring(0, s.indexOf("<"));
		//
		//										// if (sender.indexOf("a:tickermgr") ==
		//										// -1)
		//										// addNotification(NEW_MESSAGE_ALERT1
		//										// + s, NEW_MESSAGE_ALERT1 + s);
		//									} else if (SCREEN_INBOX != sScreenNumber) {
		//										showNewMessageAlert(null);
		//									}
		//								}
		//								if (Logger.ENABLE)
		//									Logger.debug(TAG,
		//											"TransportNotification():ADDED MESSAGES");
		//							}
		//							updateBuddyAndGroup(aResponse);
		//							if (aResponse.getBuddyObject() != null) {
		//								if (buddyUpdated == 0) {
		//									//									runOnUiThread(new Runnable() {
		//									//
		//									//										@Override
		//									//										public void run() {
		//									//											showNewMessageAlert("Your friend list updated.");
		//									//										}
		//									//									});
		//
		//									buddyUpdated = 1;
		//								}
		//
		//								if (Logger.ENABLE)
		//									Logger.info(TAG,
		//											"TransportNotification():UPDATE BUDDY ON REFRESH");
		//								if (sScreenNumber == SCREEN_BUDDY) {
		//									BusinessProxy.sSelf.mBuddyInfo = new InboxTblObject(
		//											InboxTblObject.BUDDY_LIST);
		//									BusinessProxy.sSelf.mBuddyInfo
		//									.addSpecificFields(aResponse
		//											.getBuddyObject());
		//								}
		//							}
		//							if (aResponse.getGroupObject() != null) {
		//								if (Logger.ENABLE)
		//									Logger.info(TAG,
		//											"TransportNotification-- UPDATE GROUP ON REFRESH");
		//								if (SCREEN_GROUP == sScreenNumber) {
		//									BusinessProxy.sSelf.mGroupInfo = new InboxTblObject(
		//											InboxTblObject.GROUP_LIST);
		//									BusinessProxy.sSelf.mGroupInfo
		//									.addSpecificFields(aResponse
		//											.getGroupObject());
		//								}
		//							}
		//							if (aResponse.getIMBuddyObject() != null) {
		//								if (Logger.ENABLE)
		//									Logger.info(TAG,
		//											"TransportNotification -- UPDATE MESSENGER BUDDIES ON REFRESH");
		//								if (SCREEN_CHAT == sScreenNumber) {
		//									BusinessProxy.sSelf.mIMBuddyInfo = new InboxTblObject(
		//											InboxTblObject.IM_LIST);
		//									BusinessProxy.sSelf.mIMBuddyInfo
		//									.addSpecificFields(aResponse
		//											.getIMBuddyObject());
		//								}
		//							}
		//							if (aResponse.getClientControl() != null
		//									&& aResponse.getClientControl().length() > 0) {
		//								if (Logger.ENABLE)
		//									Logger.info(
		//											TAG,
		//											"TransportNotification -- Got Stat control from server. - "
		//													+ aResponse
		//													.getClientControl());
		//								// FIXME - Check this for android.
		//								parseAndSetStat(aResponse.getClientControl());
		//							}
		//							if (aResponse.getiSpecialValues() > 0)// new changed
		//								// for emer
		//							{
		//								processSpecialResponse(aResponse);
		//							}
		//							// else
		//							// {
		//							// iCurrUiScreen.TransportNotification(aResponse);
		//							// displayUpgradeScreen(aResponse);
		//							// }
		//							if (BusinessProxy.sSelf.mMessengerData
		//									.isStatusChangedForTicker()) {
		//								// FIXME - Check for android
		//								// startTicker(iCurrUiScreen,
		//								// CoreController.iSelf.IMDATA.getTickerText(),
		//								// 1, 0);
		//								BusinessProxy.sSelf.iAgentUpdateInfo |= Constants.MESSENGER;
		//							}
		//						}
		//						break;
		//					}
		//					break;
		//				case Constants.RES_TYPE_INVALIDUSER:
		////					if (SCREEN_LOGIN != sScreenNumber) {
		////						SettingData.sSelf.setRemember(false);
		////						BusinessProxy.sSelf.setRegistered(false);
		////						pushNewScreen(LoginActivity.class, false);
		////					}
		//					break;
		//				}
		//			} else// adding in advertise build
		//			{
		//				// System.out.println("-----------------getResponseCode---- : "+aResponse.getResponseCode());
		//				// System.out.println("-----------------dend op---- : "+aResponse.getError());
		//				if (BusinessProxy.sSelf.addPush != null
		//						&& BusinessProxy.sSelf.addPush.isAddDisplaying()) {
		//					if (lDialogProgress != null && lDialogProgress.isShowing()) {
		//						lDialogProgress.dismiss();
		//					}
		//				}
		//			}
		//			notificationFromTransport(aResponse);
		//			if (Logger.ENABLE)
		//				Logger.debug(TAG, "TransportNotification():[INFO] UI NOTIFIED");
		//			if (aResponse.getUpgradeType() != 0)
		//			{
		//				//Commented By mahesh on 27th Feb-2015
		//				displayUpgradeScreen(aResponse);
		//			}
		//			if (aResponse.getiSpecialValues() > 0)// new changed for emer
		//			{
		//				processSpecialResponse(aResponse);
		//			}
		//			if (Logger.ENABLE)
		//				Logger.debug(TAG, "TransportNotification -- Exit");
		//		} catch (Throwable ex) {
		//			ex.printStackTrace();
		//			if (Logger.ENABLE)
		//				Logger.error(TAG, "TransportNotification-- ERROR " + ex, ex);
		//		}
	}

	private void processSpecialResponse(ResponseObject aResp)// new changed for
	// emer
	{
		String alert = "For security reasons you have been logged out. Please relogin and enjoy RockeTalk.";
		if (aResp.getSuggestedNames() != null)
			alert = aResp.getSuggestedNames();
		switch (aResp.getiSpecialValues()) {
		case 1:
			if (aResp.getSuggestedNames() != null)
				showSimpleAlert("RockeTalk", aResp.getSuggestedNames());
			break;
		case 2:// Log off
			/*
			 * resetDataAndClearMemory(); nextScreen(SPLASH_UI_ID, new
			 * Integer(1)); AllDisplays.iSelf.showConfirmation(alert, "",
			 * TextData.OK, getWidth() - 20);
			 */
			SettingData.sSelf.setLogOffFromserver(true);
			BusinessProxy.sSelf.stopNetworkActivity();
			BusinessProxy.sSelf.setRegistered(false);
			BusinessProxy.sSelf.iNotificationMessage = null;
			BusinessProxy.sSelf.mContactUploadedToServer = false;
			BusinessProxy.sSelf.newMessageCount = 0;
			if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
				DataModel.sSelf.removeObject(DMKeys.FROM_REGISTRATION);
			pushNewScreen(LoginActivity.class, true);
			showSimpleAlert("RockeTalk", alert);
			break;
		case 3:// Log off and clear all tabless
			/*
			 * resetDataAndClearMemory(); iReplyFwdFlag = new
			 * byte[MAX_REPLY_FWD_BUFFER]; InboxTblObject.iInboxTableId.clear();
			 * InboxTblObject.iBuddyTableId.clear();
			 * InboxTblObject.iGroupTableId.clear();
			 * InboxTblObject.iIMTableId.clear();
			 * CoreController.iSelf.deleteAllRecords(); iSettingInfo.iAlertMode
			 * = 1; iSettingInfo.iRefresh = 120;//300 iSettingInfo.iVolumeLevel
			 * = 10; nextScreen(SPLASH_UI_ID, new Integer(1));
			 * AllDisplays.iSelf.showConfirmation(alert, "", TextData.OK,
			 * getWidth() - 20);
			 */

			BusinessProxy.sSelf.stopNetworkActivity();
			BusinessProxy.sSelf.setRegistered(false);
			BusinessProxy.sSelf.iNotificationMessage = null;
			BusinessProxy.sSelf.mContactUploadedToServer = false;
			BusinessProxy.sSelf.newMessageCount = 0;
			if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
				DataModel.sSelf.removeObject(DMKeys.FROM_REGISTRATION);
			BusinessProxy.sSelf.deleteAllTables();
			SettingData.sSelf.setAlertMode((byte) 1);
			SettingData.sSelf.setRefreshPeriod(120);
			SettingData.sSelf.setVolumeLevel(10);
			SettingData.sSelf.setLogOffFromserver(true);
			pushNewScreen(LoginActivity.class, true);
			showSimpleAlert("RockeTalk", alert);
			break;
		case 4:// reset top message ID
			BusinessProxy.sSelf.setTopMessageIdOnClient(null);
			break;
		case 13:// Show an alert to user and block him - means only exit app.
			if (aResp.getSuggestedNames() != null) {
				/*
				 * AllDisplays.iSelf.showConfirmation(aResp.getSuggestedNames(),
				 * null, TextData.EXIT, getWidth() - 20);
				 */
				// showSimpleAlert("RockeTalk",aResp.getSuggestedNames());

				DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						YelatalkApplication.killApplication();
					}
				};
				BusinessProxy.sSelf.stopNetworkActivity();
				BusinessProxy.sSelf.setRegistered(false);
				BusinessProxy.sSelf.iNotificationMessage = null;
				BusinessProxy.sSelf.mContactUploadedToServer = false;
				BusinessProxy.sSelf.newMessageCount = 0;
				if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
					DataModel.sSelf.removeObject(DMKeys.FROM_REGISTRATION);
				showAlertMessage("RockeTalk", aResp.getSuggestedNames(),
						new int[] { DialogInterface.BUTTON_POSITIVE },
						new String[] { "Ok" },
						new DialogInterface.OnClickListener[] { exitHandler });
			}
			break;
		default:
			break;
		}
	}

	// -------------------------------------------------------------------------------
	private void writeProfilePayload(Payload payload) {
		StringBuilder buff = new StringBuilder();
		if (null != payload.mVoice) {
			buff.append(getCacheDir());
			buff.append(File.separator);
			buff.append(PROFILE_VOICE);
			try {
				FileOutputStream fout = new FileOutputStream(buff.toString());
				fout.write(payload.mVoice[0]);
				fout.flush();
				fout.close();
				mVoicePath = buff.toString();
			} catch (IOException io) {
				if (Logger.ENABLE) {
					Logger.error(
							TAG,
							"writeProfilePhoto::UIActivityManager()-- OHH file write error. Why??",
							io);
				}
			}
		}
	}

	public boolean isForeOrBack() {
		return sIsRunningInBackGround;
	}

	// --------------------------------------------------------------------------
	private void showNewMessageAlertXXX(final String aStr) {
		if (sIsRunningInBackGround /*
		 * || mCurrentState !=
		 * Constants.UI_STATE_IDLE
		 */) {
			return;
		}
		runOnUiThread(new Runnable() {

			public void run() {
				try {
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater
							.inflate(
									R.layout.notification,
									(ViewGroup) findViewById(R.id.notification_toast_layout));
					TextView text = (TextView) layout.findViewById(R.id.text);
					// layout.findViewById(R.id.goto_inbox_arrow).setVisibility(View.VISIBLE);
					if (aStr == null)
						text.setText("You have "
								+ BusinessProxy.sSelf.newMessageCount
								+ " message(s) in your inbox.");
					else
						text.setText(aStr);
					mNotificationToast = new Toast(UIActivityManager.this);
					mNotificationToast.setGravity(Gravity.TOP
							| Gravity.FILL_HORIZONTAL, 0, 25);
					mNotificationToast.setDuration(Toast.LENGTH_LONG);
					mNotificationToast.setView(layout);
					Utilities.startAnimition(UIActivityManager.this, layout,
							R.anim.grow_from_top);
					mNotificationToast.show();
				} catch (Exception e) {
					System.out
					.println("##################### EXCEPTION CATCHED ############################");
					e.printStackTrace();
				}
			}
		});
	}

	public void showNewMessageAlert(final String sender, final String aStr) 
	{
		if (sIsRunningInBackGround) 
		{
			return;
		}
		if (quickAction != null && quickAction.isShowing())
			quickAction.dismiss();
		if (!BusinessProxy.sSelf.leftMenuAdFlag) 
		{
			//			if (aStr == null) 
			//			{
			//				String extra = "";
			//				if (FeedRequester.systemMessage > 1)
			//					extra = "s";
			//				if (FeedRequester.systemMessage > 0
			//						&& FeedRequester.latestMessage.size() > 0) {
			//					showConnentAlert(null, "You have new message in your message box and "
			//									+ FeedRequester.systemMessage
			//									+ " System message" + extra,
			//									true,
			//									com.kainat.app.android.bean.Message.MESSAGE_TYPE_BUDDY);
			//				} else if (FeedRequester.systemMessage > 0) {
			//					showConnentAlert(null, 
			//							"You have " + FeedRequester.systemMessage
			//							+ " System message" + extra,
			//							true,
			//							com.kainat.app.android.bean.Message.MESSAGE_TYPE_SYSTEM_MESSAGE);
			//				} else if (FeedRequester.latestMessage.size() > 0)
			//					showConnentAlert(null, 
			//							"You have new message in your message box.",
			//							true,
			//							com.kainat.app.android.bean.Message.MESSAGE_TYPE_BUDDY);
			//				FeedRequester.latestMessage.clear();
			//
			//			} else
			if(sender != null && sender.trim().length() > 0 && aStr != null)
				showConnentAlert(sender, aStr, false, (byte) -1);
		}
	}

	public void showThreadMessgeNotification(
			Vector<com.kainat.app.android.bean.Message> latestMessage) {
		// if (!sIsRunningInBackGround)
		// return;
		// if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
		// return;
		// }
		// if (DataModel.sSelf.getBoolean(DMKeys.IS_VERIFIED)) {
		// return;
		// }
		try {
			for (int i = 0; i < latestMessage.size(); i++) {

				com.kainat.app.android.bean.Message message = latestMessage
						.get(i);
				CharSequence contentTitle = message.source; // expanded message
				// title
				int icon = R.drawable.pushicon; // icon from resources
				long when = System.currentTimeMillis(); // notification time -
				// When
				// to notify
				Context context = getApplicationContext(); // application
				// Context
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
					mNotification = new Notification(icon, message.msgTxt, when);
					mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
					mNotification.defaults |= Notification.DEFAULT_LIGHTS;
				}
				mNotification.number = BusinessProxy.sSelf.newMessageCount;
				mNotification.tickerText = "aTickerText";

				// Custom
				Intent notificationIntent = new Intent(this,
						KainatHomeActivity.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this,
						0, notificationIntent, 0);
				mNotification.contentIntent = contentIntent;
				RemoteViews contentView = new RemoteViews(getPackageName(),
						R.layout.noti_alert);
				contentView.setImageViewResource(R.id.image,
						R.drawable.pushicon);
				contentView.setTextViewText(R.id.text, "aContentText");// "Hello, this is the custom expanded view. Here we can display the recieved text message. And if we cwant we can show any information regarding the notification or the application.");
				mNotification.contentView = contentView;

				mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(HELLO_ID, mNotification);
				// /
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println("Error - " + ex.toString());
		}

	}

	public int getSelectedAlertID() {
		int resourceId = -1;
		switch (SettingData.sSelf.getAlertMode()) {
		case Constants.ALERT_NO_SOUND:
			break;
		case Constants.ALERT_BEEP:
			resourceId = R.raw.beep;
			break;
		case Constants.ALERT_CHIMEUP:
			resourceId = R.raw.chimeup;
			break;
		case Constants.ALERT_KNOCK:
			resourceId = R.raw.knock;
			break;
		case Constants.ALERT_MOO:
			resourceId = R.raw.moo;
			break;
		case Constants.ALERT_NEWALERT:
			resourceId = R.raw.newalert;
			break;
		case Constants.ALERT_RINGING:
			resourceId = R.raw.ringing;
			break;
		}
		return resourceId;
	}

	// -----------------------------------------------------------------------------------------------------------
	private void parseAndSetStat(String aStr) {
		// "ST##0::ACK##CC_DONE@@1"
		final String[] stats = new String[] { "ST", "ACK", "BUZZIN##",
				"BUZZOUT##", "ROCKETIN##", "ROCKETOUT##", "NFY##", "USEIP1##",
		"USEIP2##" };
		String tempStr;
		// aStr =
		// "BUZZIN##60::BUZZOUT##60::USEIP1##http://wap.indiatimes.com/qarocketalk/::USEIP2##http://test02.rocketalk-justfortest.com/rocketalk/im";
		String[] statClt = aStr.split("::");
		for (int i = 0; i < statClt.length; i++) {
			if (statClt[i].indexOf(stats[0]) != -1)// ST
			{
				tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
				if (tempStr.equals("0"))
					BusinessProxy.sSelf.setStatState(false);
				else
					BusinessProxy.sSelf.setStatState(true);
			} else if (statClt[i].indexOf(stats[1]) != -1)// ACK
			{
				tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
				if (BusinessProxy.sSelf.getStatString().length() > 0)
					BusinessProxy.sSelf.addStatData("^");
				BusinessProxy.sSelf.addStatData("::" + tempStr);
				if (BusinessProxy.sSelf.sendNewTextMessage(
						"STAT<a:userstatistics>",
						BusinessProxy.sSelf.getStatString(), false))
					BusinessProxy.sSelf.clearStatData();
				else {
					// if(Flags.LOG_ENABLE)
					// OtsLogger.LogTime("sendStatRequest()__[WARNING] -- ****ERROR SENDING REQUEST****. RETURN VALUE = ",
					// OtsLogger.SEVERE);
				}
			} else if (statClt[i].indexOf(stats[2]) != -1)// BUZZIN
			{
				tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
				iBuzzTime[0] = Integer.parseInt(tempStr);
			} else if (statClt[i].indexOf(stats[3]) != -1)// BUZZOUT
			{
				tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
				iBuzzTime[1] = Integer.parseInt(tempStr);
			} else if (statClt[i].indexOf(stats[4]) != -1)// ROCKETIN
			{
				tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
				iRocketTime[0] = Integer.parseInt(tempStr);
			} else if (statClt[i].indexOf(stats[5]) != -1)// ROCKETOUT
			{
				tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
				iRocketTime[1] = Integer.parseInt(tempStr);
			} else if (statClt[i].indexOf(stats[6]) != -1
					&& statClt[i].length() > 0)// NFY
			{
				BusinessProxy.sSelf.iNotificationMessage = statClt[i]
						.substring(statClt[i].indexOf("##") + 2);
				// @Check here if current UI is Home Screen then update the
				// Notification message
			}
			// else if(statClt[i].indexOf(stats[7]) != -1)//USEIP1
			// {
			// tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
			// URLFromServer[0] = (tempStr != null && tempStr.length() > 0) ?
			// tempStr :
			// URLFromServer[0];
			// HttpEngine.getInstance().changePrimaryURL(URLFromServer[0]);
			// }
			// else if(statClt[i].indexOf(stats[8]) != -1)//USEIP2
			// {
			// tempStr = statClt[i].substring(statClt[i].indexOf("##") + 2);
			// URLFromServer[1] = (tempStr != null && tempStr.length() > 0) ?
			// tempStr :
			// URLFromServer[1];
			// HttpEngine.getInstance().changeSecondaryURL(URLFromServer[1]);
			// }
		}
	}

	public void getFeeds() {

		// int mRowsUpdated =
		// getContentResolver().delete(LandingPageContentProvider.CONTENT_URI,
		// LandingPageTable.MORE+"=1", null);
		// System.out.println("Feed Synch --------------delete row for loading activity feed ----------"+mRowsUpdated);

		// System.out.println("---------------onresuen home left menu url : "+Utilities.getString(this,
		// Constants.LEFT_MENU_URL));
		runOnUiThread(new Runnable() {
			public void run() {
				FeedRequester.requestLeftMenu(UIActivityManager.this);
				FeedRequester.feedTaskLeftMenu
				.setHttpSynch(UIActivityManager.this);
			}
		});

		/*
		 * FeedRequester.requestActivityFeed(this) ;
		 * FeedRequester.requestFeatureUserFeed(this) ;
		 * FeedRequester.requestMediaFeed(this) ;
		 * FeedRequester.requestCommnunity(this) ;
		 * FeedRequester.requestGroupevent(this) ;
		 */

		/*
		 * Hashtable<String, DiscoveryUrl> discovery =
		 * Utilities.getDiscoveryUrl(this);
		 * System.out.println("---------------discovery url : "
		 * +discovery.toString()); feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("user".hashCode()); feedTask.setClientType(1) ;
		 * feedTask.execute(((DiscoveryUrl)discovery.get("user")).url) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("media".hashCode()); feedTask.setClientType(0)
		 * ; feedTask.execute(((DiscoveryUrl)discovery.get("media")).url) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("community".hashCode());
		 * feedTask.setClientType(0) ;
		 * feedTask.execute(((DiscoveryUrl)discovery.get("community")).url) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("groupevent".hashCode());
		 * feedTask.setClientType(1) ;
		 * feedTask.execute(((DiscoveryUrl)discovery.get("groupevent")).url) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId(Constants.FEED_ACTIVITY);
		 * feedTask.setClientType(1) ;
		 * feedTask.execute(Utilities.getString(UIActivityManager.this,
		 * Constants.ACTIVITY_FEED_URL) ) ;
		 */
		// feedTask = new FeedTask(this) ;
		// feedTask.setRequestId(Constants.FEED_ACTIVITY);
		// feedTask.setClientType(1) ;
		// feedTask.execute(((DiscoveryUrl)discovery.get("user")).url) ;

		/*
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("user".hashCode()); feedTask.setClientType(1) ;
		 * feedTask.execute(
		 * "http://124.153.95.168:28080/tejas/feeds/user/featureduser?type=featured"
		 * ) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("media".hashCode()); feedTask.setClientType(0)
		 * ; feedTask.execute(
		 * "http://124.153.95.168:28080/tejas/feeds/api/list/attributed/featured"
		 * ) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("community".hashCode());
		 * feedTask.setClientType(0) ; feedTask.execute(
		 * "http://124.153.95.168:28080/tejas/feeds/api/group/attributed/featured"
		 * ) ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId("groupevent".hashCode());
		 * feedTask.setClientType(1) ; feedTask.execute(
		 * "http://124.153.95.168:28080/tejas/feeds/group/groupevent") ;
		 * 
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId(Constants.FEED_ACTIVITY);
		 * feedTask.setClientType(1) ; feedTask.execute(
		 * "http://124.153.95.168:28080/tejas/feeds/user/usernotification"
		 * "http://74.208.228.56/Javaclient/Android/landingpage/sample_schema_for_landing_screen.xml"
		 * ) ;
		 */
		/*
		 * feedTask = new FeedTask(this) ;
		 * feedTask.setRequestId(Constants.FEED_MEDIA_COMMENT);
		 * feedTask.setClientType(0) ; feedTask.execute(
		 * "http://124.153.95.168:28080/tejas/feeds/comments?comment_type=media&object_id=2150004490&start_page=1&num_comments=20"
		 * ) ;
		 */
	}

	// --------------------------------------------------------------------------------------------------------------------------
	boolean urlloading = false;
	boolean loginRes = false;

	//	private void handleLoginResponse(ResponseObject aResponse) throws Throwable {
	//
	//		ImageDownloader imageManager = new ImageDownloader();
	//
	//		imageManager.download(SettingData.sSelf.getUserName(), new ImageView(
	//				this), new ThumbnailReponseHandler() {
	//
	//			@Override
	//			public void onThumbnailResponse(ThumbnailImage value, byte[] data) {
	//			}
	//		}, ImageDownloader.TYPE_THUMB_BUDDY);
	//
	//		Utilities.setBoolean(this, Constants.REFRESH_CONVERSIOTION_LIST, false);
	//		Utilities.setInt(this, Constants.FIRST_LOGIN,
	//				(Utilities.getInt(this, Constants.FIRST_LOGIN) + 1));
	//		loginRes = true;
	//
	//		Cursor cursor = getContentResolver().query(
	//				MediaContentProvider.CONTENT_URI_SETTING, null, null, null,
	//				null);
	//		if (cursor.getCount() <= 0) {
	//			ContentValues values = new ContentValues();
	//			values.put(SettingTable.INSMORE_TIME, 0);
	//			getContentResolver().insert(
	//					MediaContentProvider.CONTENT_URI_SETTING, values);
	//		}
	//		Utilities.cleanOnLogin(this);
	//		runOnUiThread(new Runnable() {
	//			@Override
	//			public void run() {
	//				try {
	//					((TextView) findViewById(R.id.notyou))
	//					.setText(getResources().getString(
	//							R.string.loading_interface));
	//					((TextView) findViewById(R.id.notyou))
	//					.setTextColor(getResources().getColor(
	//							R.color.sub_heading));
	//				} catch (Exception e) {
	//					// TODO: handle exception
	//				}
	//
	//			}
	//		});
	//		Utilities.cleanTemp();
	//		if (!Utilities.getBoolean(this, Constants.REGISTRATION))
	//			Utilities.resetLandingPageSetting(UIActivityManager.this);
	//		loginRes = false;
	//		if (aResponse.getUpgradeType() != 1) {
	//			if (!BusinessProxy.sSelf.advertisementData.isEmpty()) {
	//				try {
	//					final String fetchURL = BusinessProxy.sSelf.advertisementData
	//							.get("advtUrl");
	//					if (fetchURL != null && fetchURL.length() > 5)
	//						new Thread(new Runnable() {
	//							public void run() {
	//								BusinessProxy.addData = BusinessProxy.sSelf.addPush.advertisementEngine
	//										.getAdd(fetchURL,
	//												getCurrentScreen(sScreenNumber));
	//							}
	//						}).start();
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				}
	//			}
	//
	//			// 22-Get Animations
	//			// Start Loading Animation here
	//			new Thread(new Runnable() {//
	//				@Override
	//				public void run() {
	//					// TODO Auto-generated method stub
	//					try {
	//						//								RtAnimationEngine connectionEngine = RtAnimationEngine
	//						//										.getInstance();
	//						//								String url = "http://"
	//						//										+ Urls.TEJAS_HOST
	//						//										+ "/tejas/feeds/api/animation/getAnimationList";// "http://74.208.228.56/Android/xmlData/animXmlFromMySide.xml";
	//						//								if (BusinessProxy.sSelf.animationListURL != null
	//						//										&& BusinessProxy.sSelf.animationListURL
	//						//												.trim().length() > 0)
	//						//									url = BusinessProxy.sSelf.animationListURL;
	//						//								connectionEngine.getRtAnimation(url);
	//					} catch (Exception e) {
	//						// TODO: handle exception
	//					}
	//				}
	//			}).start();
	//		}
	//		if (BusinessProxy.sSelf != null) {
	//			int updatedRow = BusinessProxy.sSelf.resetOutboxSatus();
	//			if (Logger.ENABLE)
	//				Logger.debug(TAG, "total row updated in outbox for"
	//						+ updatedRow);
	//			if (BusinessProxy.sSelf.onlineFriends != null)
	//				BusinessProxy.sSelf.onlineFriends.clear();
	//			if (BusinessProxy.sSelf.onlineOfflineFriends != null)
	//				BusinessProxy.sSelf.onlineOfflineFriends.clear();
	//			if (BusinessProxy.sSelf.buddy != null)
	//				BusinessProxy.sSelf.buddy.clear();
	//			SettingData.sSelf.setLogOffFromserver(false);
	//		}
	//		Utilities.setSafeLogin(this);
	//		cancelNotification();
	//		if (iFrndBuzzTimes == null)
	//			iFrndBuzzTimes = new Hashtable<String, Long>(3);
	//		String tempInt;
	//		String topIdInBuff;
	//		InboxTblObject inboxData = (InboxTblObject) aResponse.getData();
	//		boolean fromRegistration = DataModel.sSelf
	//				.getBoolean(DMKeys.FROM_REGISTRATION);
	//		if (!fromRegistration)
	//			BusinessProxy.sSelf.mTotInboxMsgAtServer = aResponse
	//			.getMessageCount();
	//		if (Logger.ENABLE)
	//			Logger.debug(TAG, "--TransportNotification():UPDATING SETTING::");
	//
	//		// if(fromRegistration){
	//		SettingData.sSelf.setRemember(true);
	//		// }
	//
	//		String previousUser = DataModel.sSelf
	//				.getString(DMKeys.PREVIOUS_USERNAME);
	//		boolean sameUser = true;
	//		if (Logger.ENABLE)
	//			Logger.debug(TAG, "updateSetting()::--iPrevUserId = "
	//					+ previousUser + "::iSettingInfo.iUserName="
	//					+ SettingData.sSelf.getUserName());
	//		if (previousUser == null
	//				|| (previousUser != null && !previousUser
	//				.equalsIgnoreCase(SettingData.sSelf.getUserName())))
	//			sameUser = false;
	//
	//		if (!sameUser)
	//			Utilities.cleanDB(this);
	//		updateSetting();
	//
	//		// FIXME - Check for android
	//		// sendAgentRequest(); // Fun Stuff Contents
	//
	//		BusinessProxy.sSelf.setRegistered(true);
	//
	//		if (Logger.ENABLE)
	//			Logger.debug(TAG, "TransportNotification():SETTING::UPDATED");
	//
	//		BusinessProxy.sSelf.setTransactionId(BusinessProxy.sSelf
	//				.getTopId(DBEngine.OUTBOX_TABLE));
	//		if (BusinessProxy.sSelf.getTransactionId() < aResponse
	//				.getLastTransactionId()) {
	//			BusinessProxy.sSelf.setTransactionId(aResponse
	//					.getLastTransactionId());
	//		}
	//		if (inboxData != null) {
	//			if (Logger.ENABLE)
	//				Logger.debug(TAG,
	//						"--TransportNotification():Getting Topid from db::");
	//			tempInt = BusinessProxy.sSelf.getTopInboxId();
	//			topIdInBuff = (String) inboxData.getField(
	//					inboxData.getItemCount() - 1,
	//					InboxTblObject.INBOX_FIELD_MSG_ID);
	//			if (Logger.ENABLE)
	//				Logger.debug(TAG, "--TransportNotification():GOT DB TOP ID:: "
	//						+ tempInt + "::Top Id In Buffer = " + topIdInBuff);
	//			if (!topIdInBuff.equalsIgnoreCase(tempInt)
	//					|| inboxData.getItemCount() > 0) {
	//				// if (topIdInBuff != tempInt || inboxData.getItemCount() >
	//				// BusinessProxy.sSelf.getRecordCount(DBEngine.INBOX_TABLE)) {
	//				if (Logger.ENABLE)
	//					Logger.info(TAG, "--TransportNotification(): DELETE INBOX");
	//				// FIXME - Check This for android
	//				// iReplyFwdFlag = new byte[MAX_REPLY_FWD_BUFFER];
	//				// inboxData.deleteAllMessagePayload();
	//				// InboxTblObject.iInboxTableId.clear();
	//				if (!sameUser)
	//					BusinessProxy.sSelf.deleteTable(DBEngine.INBOX_TABLE);
	//				if (!fromRegistration) {
	//					/*
	//					 * ////// UPDATE DB ContentValues values = new
	//					 * ContentValues(); byte[] data =
	//					 * InboxTblObject.writeAttachments(payloadData); if
	//					 * (data.length < Constants.MAX_DB_DATA_STORE_SIZE) {
	//					 * values.put(DBEngine.INBOX_ATTACHMENTS, data); }else{
	//					 * System.out.println(mExtraMessage.mId+
	//					 * " : id *************** can not save this payload coz it too big"
	//					 * +(data.length/1024)+" KB"
	//					 * +" we allow "+(Constants.MAX_DB_DATA_STORE_SIZE/1024)
	//					 * +" KB data to save");
	//					 * BusinessProxy.sSelf.excludeAutoLoadId
	//					 * .put(messageId,messageId); }
	//					 * values.put(DBEngine.INBOX_STATUS, 0);
	//					 * values.put(DBEngine.INBOX_NOTIFICATION,
	//					 * (Integer)inboxData.mNotificationBitmap.get(0));
	//					 * BusinessProxy
	//					 * .sSelf.updateTableValues(DBEngine.INBOX_TABLE,
	//					 * messageIdmSelectedMessageId, values);in
	//					 */
	//					// System.out.println("----------50 inbox msg---------------");
	//					List<InboxMessage> list = BusinessProxy.sSelf
	//							.getAllInboxIdPayload();
	//					BusinessProxy.sSelf.deleteTable(DBEngine.INBOX_TABLE);
	//					// System.out.println("---------to be migrate--------------"+list.size());
	//					inboxData.addRecords(aResponse, DBEngine.INBOX_TABLE, 0,
	//							inboxData.getItemCount());
	//					for (int i = 0; i < list.size(); i++) {
	//						InboxMessage msg = list.get(i);
	//						// System.out.println("----------migrating---------------"+msg.mId);
	//						ContentValues values = new ContentValues();
	//						values.put(DBEngine.INBOX_ATTACHMENTS,
	//								msg.mPayload.mVideoType);
	//						values.put(DBEngine.INBOX_DRM, msg.mDRM);
	//						values.put(DBEngine.INBOX_NOTIFICATION,
	//								msg.mNotification);
	//						BusinessProxy.sSelf.updateTableValues(
	//								DBEngine.INBOX_TABLE, msg.mId, values);
	//						msg.mPayload.mVideoType = null;
	//						msg.mPayload = null;
	//						msg = null;
	//					}
	//				}
	//			}
	//			// top message id changed nagendra
	//			BusinessProxy.sSelf.setTopMessageIdOnClient(BusinessProxy.sSelf
	//					.getTopInboxId());
	//			// int val =
	//			// BusinessProxy.sSelf.getTopMessageIdFromAgents(DBEngine.AGENT_TABLE);
	//			// if (BusinessProxy.sSelf.getTopMessageIdOnClient() < val)
	//			// BusinessProxy.sSelf.setTopMessageIdOnClient(val);
	//			if (Logger.ENABLE)
	//				Logger.info(TAG, "TransportNotification():INBOX DB UPDATED = "
	//						+ BusinessProxy.sSelf.getTopMessageIdOnClient());
	//		}
	//
	//		// Mahesh - Commented
	//		// if (aResponse.getBuddyObject() != null) {
	//		// if (Logger.ENABLE)
	//		// Logger.info(TAG, "TransportNotification():UPDATE BUDDY");
	//		// // FIXME - Check This for android
	//		// // InboxTblObject.iBuddyTableId.clear();
	//		// BusinessProxy.sSelf.deleteTable(DBEngine.BUDDY_TABLE);
	//		// aResponse.getBuddyObject().addRecordsBuddy(aResponse,
	//		// DBEngine.BUDDY_TABLE, 0,
	//		// aResponse.getBuddyObject().getItemCount());
	//		// // if(buddyUpdated == 1 && BusinessProxy.sSelf.onlineOfflineFriends
	//		// // != null ){
	//		// // Enumeration<String> en =
	//		// // BusinessProxy.sSelf.onlineOfflineFriends.keys() ;
	//		// // String onl = null ;
	//		// // String offl = null ;
	//		// // while(en.hasMoreElements()){
	//		// // String k = en.nextElement() ;
	//		// // String v = BusinessProxy.sSelf.onlineOfflineFriends.get(k) ;
	//		// // if(v.equalsIgnoreCase("0")){
	//		// // onl += k +", ";
	//		// // }else
	//		// // {
	//		// // offl += k +", ";
	//		// // }
	//		// // }
	//		// // BusinessProxy.sSelf.onlineOfflineFriends.clear() ;
	//		// // if(onl != null && offl != null)
	//		// // showNewMessageAlert(onl+" are online now and "+offl
	//		// // +" got offline!");
	//		// // else if(onl != null && offl == null)
	//		// // showNewMessageAlert(onl+" are online now!");
	//		// // else if(onl == null && offl != null)
	//		// // showNewMessageAlert(onl+" are got ffline!");
	//		// // }
	//		// if (Logger.ENABLE)
	//		// Logger.info(TAG, "--TransportNotification():BUDDY UPDATED");
	//		// } else {
	//		// BusinessProxy.sSelf.mBuddyInfo = null;
	//		// }
	//
	//		// if (aResponse.getGroupObject() != null) {
	//		// if (Logger.ENABLE)
	//		// Logger.info(TAG, "TransportNotification():UPDATE GROUP");
	//		// // FIXME - Check This for android
	//		// // InboxTblObject.iGroupTableId.clear();
	//		// BusinessProxy.sSelf.deleteTable(DBEngine.GROUP_TABLE);
	//		// aResponse.getGroupObject().addRecords(aResponse,
	//		// DBEngine.GROUP_TABLE, 0,
	//		// aResponse.getGroupObject().getItemCount());
	//		// if (Logger.ENABLE)
	//		// Logger.info(TAG, "TransportNotification():GROUP UPDATED");
	//		// } else {
	//		// BusinessProxy.sSelf.mBuddyInfo = null;
	//		// }
	//
	//		// FIXME - Check This for android
	//		// InboxTblObject.iIMTableId.clear();
	//		// BusinessProxy.sSelf.deleteTable(DBEngine.MESSENGER_TABLE);
	//
	//		startService(new Intent(this, PostService.class));
	//
	//		// if (aResponse.getIMBuddyObject() != null) {
	//		// if (Logger.ENABLE)
	//		// Logger.info(TAG,
	//		// "TransportNotification():UPDATE MESSENGER BUDDY");
	//		// aResponse.getIMBuddyObject().addRecords(aResponse,
	//		// DBEngine.MESSENGER_TABLE, 0,
	//		// aResponse.getIMBuddyObject().getItemCount());
	//		// if (Logger.ENABLE)
	//		// Logger.info(TAG,
	//		// "TransportNotification():MESSENGER LIST UPDATED");
	//		// } else {
	//		// BusinessProxy.sSelf.mIMBuddyInfo = null;
	//		// }
	//
	//		Utilities.setLoginCount(this);
	//
	//		// Get ads if server has send the data to retrieve ads
	//		// "http://74.208.228.56/Javabuilds/android/akm/addfeed_attache.xml";//
	//
	//		if (aResponse.getUpgradeType() != 1) {
	//			int waitTill = 0;
	//			// 33- Get Landing Page Data
	//			fetchLandingInitialInfoFeed(Constants.FEED_INITIAL_LANDING_PAGE,
	//					"http://" + Urls.TEJAS_HOST + "/tejas/feeds/landingscreen");
	//			try {
	//				while (!urlloading) {
	//					waitTill += 200;
	//					Thread.sleep(200);
	//					// System.out.println("-----------------initial url loding : "+urlloading
	//					// + " : "+waitTill); ;
	//					if (waitTill > 1000 * 60) {
	//						urlloading = true;
	//						RocketalkApplication.killApplication();
	//					}
	//				}
	//			} catch (InterruptedException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			getFeeds();
	//		}
	//
	//		Logger.conversationLog("LOGIN : ", "" + BusinessProxy.sSelf.getUserId()
	//				+ " : " + SettingData.sSelf.getUserName());
	//
	//		if (aResponse.getUpgradeType() != 1) {
	//			// 44 - Start Notification Timer
	//			BusinessProxy.sSelf.startNotificationTimer();
	//			// wait for db init
	//			try {
	//				do {
	//					Thread.sleep(1000);
	//				} while (BusinessProxy.sSelf.sqlDB == null);
	//			} catch (Exception e) {
	//				// TODO: handle exception
	//			}
	//			Utilities.initLastMsgId(this);
	//			Intent service = new Intent(this, RefreshService.class);
	//			stopService(service);
	//
	//			service = new Intent(this, RefreshService.class);
	//			if (!isRefreshServiceRunning(this))
	//				startService(service);
	//			else
	//				System.out
	//				.println("----------in alaram revicer service already running-------");
	//		}
	//		Utilities.checkAndShift(this);
	//
	//	}

	public void loadLeftProfile() {
		try {

			Vector<LeftMenu> data = Utilities.getLeftMenu(this);

			if (data == null || data.size() <= 0) {
				data = Utilities.getLeftMenu2(this);
			}
			for (int iTemp = 0; iTemp < data.size(); iTemp++) {
				LeftMenu value = data.get(iTemp);
				if (value.userThumbUrl != null) {

					ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

						@Override
						public void onThumbnailResponse(ThumbnailImage value,
								byte[] data) {
							// System.out.println("------thumb loaded----");
						}
					};
					ImageDownloader imageManager = new ImageDownloader();
					// System.out.println("------thumb loading...----"+value.userThumbUrl);
					imageManager.download(value.userThumbUrl, new CImageView(
							UIActivityManager.this), handler,
							ImageDownloader.TYPE_THUMB_BUDDY);
					break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void handleLoginResponseNew(ResponseObject aResponse)
			throws Throwable {
		BusinessProxy.sSelf.firstHit = true;
		//Send Google Token to server
		if(isInternetOn())
			sendPushKeyToServer();
		Utilities.deleteTemp(this);
		Utilities.deletePostTemp(this);
		leftMenuShow = true;
		Utilities.setBoolean(this, Constants.REFRESH_CONVERSIOTION_LIST, false);
		Utilities.setInt(this, Constants.FIRST_LOGIN, (Utilities.getInt(this, Constants.FIRST_LOGIN) + 1));
		loginRes = true;
		Cursor cursor = getContentResolver().query(MediaContentProvider.CONTENT_URI_SETTING, null, null, null, null);
		if (cursor.getCount() <= 0) {
			ContentValues values = new ContentValues();
			values.put(SettingTable.INSMORE_TIME, 0);
			getContentResolver().insert(MediaContentProvider.CONTENT_URI_SETTING, values);
			cursor.close();
		}
		loginRes = false;

		if (BusinessProxy.sSelf != null) 
		{
			int updatedRow = BusinessProxy.sSelf.resetOutboxSatus();
			if (Logger.ENABLE)
				Logger.debug(TAG, "handleLoginResponseNew :: total row updated in outbox for"+ updatedRow);
		}
		Utilities.setSafeLogin(this);
		cancelNotification();
		SettingData.sSelf.setRemember(true);

		//Get Channel top ID from Shared Preference
		if(Utilities.getString(this, Constants.CH_TOP_ID) != null)
		{
			FeedRequester.lastChannelRefreshMessageID = Utilities.getString(this, Constants.CH_TOP_ID);
			Log.i(TAG, "handleLoginResponseNew :: get lastChannelRefreshMessageID from Shared Pref : "+FeedRequester.lastChannelRefreshMessageID);
		}
		updateSetting();
		//			SettingData.sSelf.updateSetting();
		BusinessProxy.sSelf.setRegistered(true);
		if (Logger.ENABLE)
			Logger.debug(TAG, "handleLoginResponseNew :: SETTING::UPDATED");
		if(isInternetOn())
			startService(new Intent(this, PostService.class));

		BusinessProxy.sSelf.startNotificationTimer();
		Utilities.initLastMsgId(this);
		stopService(new Intent(this, RefreshService.class));
		if (!isRefreshServiceRunning(this) && isInternetOn())
			startService(new Intent(this, RefreshService.class));
		else
			Log.i(TAG, "handleLoginResponseNew :: Refresh service already running or NO INTERNET");
		Utilities.checkAndShift(this);
		BusinessProxy.sSelf.displaywidth = getWindowManager().getDefaultDisplay().getWidth();
		BusinessProxy.sSelf.displayheight = getWindowManager().getDefaultDisplay().getHeight();
		//			Auth.getInstance().mFacebook = null;
		refreshChannelList = true;

		//Get tags
		if(isInternetOn())
			new GetTags().execute("") ;
	}

	public void restartMessageRefresh(){
		stopService(new Intent(this, RefreshService.class));
		if (!isRefreshServiceRunning(this))
			startService(new Intent(this, RefreshService.class));

	}
	public void restartServices(){
		startService(new Intent(this, PostService.class));
		if (!isRefreshServiceRunning(this))
			startService(new Intent(this, RefreshService.class));

	}
	public void stopServices(){
		stopService(new Intent(this, PostService.class));
		stopService(new Intent(this, RefreshService.class));
	}

	public class GetTags extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {

			try {				
				String url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/api/group/hashtag";
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("RT-APP-KEY", HttpHeaderUtils
						.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),
								SettingData.sSelf.getPassword()));
				try {
					HttpResponse responseHttp = client.execute(httpget);
					if (responseHttp != null && responseHttp.getStatusLine().getStatusCode() == 200) 
					{
						//							hashTags = EntityUtils.toString(responseHttp.getEntity());
						JSONObject jsonObject = new JSONObject(EntityUtils.toString(responseHttp.getEntity()));
						if (jsonObject.has("trendingTags")) 
						{
							trendingTags = jsonObject.getString("trendingTags");
						}
						if (jsonObject.has("featuredTags")) 
						{
							featuredTags = jsonObject.getString("featuredTags");
						}

					}
					return featuredTags;
				} catch (ClientProtocolException e) {
					e.printStackTrace();

				} catch (IOException e) {
					e.printStackTrace();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null ;
		}
		@Override
		protected void onPostExecute(String response) {						
		}
	}

	public boolean isRefreshServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						context.getApplicationContext().ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.kainat.app.android.engine.RefreshService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void updateSetting() {
		String previousUser = DataModel.sSelf
				.removeString(DMKeys.PREVIOUS_USERNAME);

		// System.out.println("-----------previeous user:"+previousUser);
		if (Logger.ENABLE)
			Logger.debug(TAG, "updateSetting()::--iPrevUserId = "
					+ previousUser + "::iSettingInfo.iUserName="
					+ SettingData.sSelf.getUserName());
		if (previousUser == null
				|| (previousUser != null && !previousUser
				.equalsIgnoreCase(SettingData.sSelf.getUserName()))) {
			if (Logger.ENABLE)
				Logger.debug(TAG, "updateSetting()::--DELETE RECORDS");
			// System.out.println("-----------previeous user reset");
			// FIXME - Check for android
			// iReplyFwdFlag = new byte[MAX_REPLY_FWD_BUFFER];
			// InboxTblObject.iInboxTableId.clear();
			// InboxTblObject.iBuddyTableId.clear();
			// InboxTblObject.iGroupTableId.clear();
			// InboxTblObject.iIMTableId.clear();
			// Utilities.deleteDraftPostData(this);
			BusinessProxy.sSelf.deleteAllTables();
			//			SettingData.sSelf.setAlertMode((byte) 1);
			//			SettingData.sSelf.setRefreshPeriod(120);
			//			SettingData.sSelf.setVolumeLevel(10);
			//			Utilities.cleanDB(this);
		}
		try {
			System.out.println("-----------previeous user exception ");
			SettingData.sSelf.updateSetting();
		} catch (Exception e) {
			// System.out.println("-----------previeous user exception "+e.getMessage());
			if (Logger.ENABLE)
				Logger.error(TAG, "UpdateSetting " + e.getMessage(), e);
		}
	}

	private void updateBuddyAndGroup(ResponseObject aResponse) {
		int tempInt = 0;
		int topIdInBuff = 0;
		try {
			if (null != aResponse.getBuddyObject()) {
				// System.out.println("ENTRY - UITRANSTPORT- updateBuddyAndGroup");
				if (Logger.ENABLE)
					Logger.info(TAG,
							"updateBuddyAndGroup()[INFO]-UPDATE BUDDY ON REFRESH");
				if (BusinessProxy.sSelf.mBuddyInfo != null) {
					tempInt = BusinessProxy.sSelf.mBuddyInfo.getTopId();
					Integer intObj = (Integer) BusinessProxy.sSelf.mBuddyInfo
							.getField(0, InboxTblObject.INBOX_FIELD_MSG_ID);
					topIdInBuff = 0;
					if (intObj != null)
						topIdInBuff = intObj.intValue();
				}
				// if (aResponse.getSentOp() != Constants.FRAME_TYPE_INBOX_MORE
				// && (topIdInBuff == tempInt || BusinessProxy.sSelf.mBuddyInfo
				// == null)) {
				// FIXME - Check this for android
				// InboxTblObject.iBuddyTableId.clear();
				BusinessProxy.sSelf.deleteTable(DBEngine.BUDDY_TABLE);
				aResponse.getBuddyObject().addRecordsBuddy(aResponse,
						DBEngine.BUDDY_TABLE, 0,
						aResponse.getBuddyObject().getItemCount());

				if (buddyUpdated == 1
						&& BusinessProxy.sSelf.onlineOfflineFriends != null) {
					Enumeration<String> en = BusinessProxy.sSelf.onlineOfflineFriends
							.keys();
					StringBuffer onlsb = new StringBuffer();
					StringBuffer offlsb = new StringBuffer();

					int onlineCount = 0;
					int offlineCount = 0;
					while (en.hasMoreElements()) {
						String k = en.nextElement();
						String v = BusinessProxy.sSelf.onlineOfflineFriends
								.get(k);
						if (v.equalsIgnoreCase("1")) {
							if (k.indexOf("a:") != -1)
								continue;
							onlsb.append(BusinessProxy.sSelf
									.parseNameInformation(k) + ", ");
							onlineCount++;
						} else {
							if (k.indexOf("a:") != -1)
								continue;
							offlsb.append(BusinessProxy.sSelf
									.parseNameInformation(k) + ", ");// offl +=
							// k
							// +", ";
							offlineCount++;
						}
					}
					String onl = onlsb.toString();
					String offl = offlsb.toString();
					onlsb = null;
					offlsb = null;
					if (onl.indexOf(",") != -1)
						onl = onl.substring(0, onl.lastIndexOf(","));

					if (offl.indexOf(",") != -1)
						offl = offl.substring(0, offl.lastIndexOf(","));

					String isare1 = "is";
					String isare0 = "is";
					if (onlineCount > 1)
						isare1 = "are";
					if (offlineCount > 1)
						isare0 = "are";
					BusinessProxy.sSelf.onlineOfflineFriends.clear();
					if (onl.trim().length() > 0 && offl.trim().length() > 0) {
						showNewMessageAlert(null, onl + " " + isare1
								+ " online now and " + offl + " " + isare0
								+ "  gone offline!");
					} else if (onl.trim().length() > 0
							&& offl.trim().length() >= 0)
						showNewMessageAlert(null, onl + " " + isare1
								+ "  online now!");
					else if (onl.trim().length() >= 0
							&& offl.trim().length() > 0)
						showNewMessageAlert(null, offl + " " + isare0
								+ " gone offline!");
				}

				// BusinessProxy.sSelf.listBuddy =
				// BusinessProxy.sSelf.getAllBuddyInformation1();//added by nag

				// }
				if (Logger.ENABLE)
					Logger.info(TAG,
							"updateBuddyAndGroup()[INFO]-BUDDY UPDATED");
			} else if (null == aResponse.getBuddyObject()
					&& 0 == BusinessProxy.sSelf.getBuddyCountOnServer()) {
				// FIXME - Check this for android
				// InboxTblObject.iBuddyTableId.clear();
				// BusinessProxy.sSelf.deleteTable(DBEngine.BUDDY_TABLE);//commented
				// by nage
				// BusinessProxy.sSelf.listBuddy =
				// BusinessProxy.sSelf.getAllBuddyInformation1();//added by nag

			}
			if (aResponse.getGroupObject() != null) {
				if (Logger.ENABLE)
					Logger.info(TAG,
							"updateBuddyAndGroup()[INFO]-UPDATE GROUP ON REFRESH");
				if (BusinessProxy.sSelf.mGroupInfo != null) {
					tempInt = BusinessProxy.sSelf.mGroupInfo.getTopId();
					Integer intObj = (Integer) BusinessProxy.sSelf.mGroupInfo
							.getField(0, InboxTblObject.INBOX_FIELD_MSG_ID);
					topIdInBuff = 0;
					if (intObj != null)
						topIdInBuff = intObj.intValue();
				}
				if (aResponse.getSentOp() != Constants.FRAME_TYPE_INBOX_MORE
						&& (topIdInBuff == tempInt || BusinessProxy.sSelf.mGroupInfo == null)) {
					// FIXME - Check this for android.
					// InboxTblObject.iGroupTableId.clear();
					BusinessProxy.sSelf.deleteTable(DBEngine.GROUP_TABLE);
					aResponse.getGroupObject().addRecords(aResponse,
							DBEngine.GROUP_TABLE, 0,
							aResponse.getGroupObject().getItemCount());
				}
				if (Logger.ENABLE)
					Logger.info(TAG,
							"updateBuddyAndGroup()[INFO]-GROUP UPDATED");
			} else if (null == aResponse.getGroupObject()
					&& 0 == BusinessProxy.sSelf.getGroupCountOnServer()) {
				BusinessProxy.sSelf.mGroupInfo = null;
				// FIXME - Check this for android.
				// InboxTblObject.iGroupTableId.clear();
				BusinessProxy.sSelf.deleteTable(DBEngine.GROUP_TABLE);
			}
			if (null != aResponse.getIMBuddyObject()) {
				if (Logger.ENABLE)
					Logger.info(TAG,
							"updateBuddyAndGroup()[INFO]-UPDATE MESSENGER BUDDIES ON REFRESH");
				if (aResponse.getIMBuddyObject() != null) {
					tempInt = aResponse.getIMBuddyObject().getTopId();
					Integer intObj = (Integer) aResponse.getIMBuddyObject()
							.getField(0, InboxTblObject.INBOX_FIELD_MSG_ID);
					topIdInBuff = 0;
					if (intObj != null)
						topIdInBuff = intObj.intValue();
				}
				// if(topIdInBuff != tempInt || iIMBuddyInfo == null)
				{
					// FIXME - Check this for android.
					// InboxTblObject.iIMTableId.clear();
					BusinessProxy.sSelf.deleteTable(DBEngine.MESSENGER_TABLE);
					aResponse.getIMBuddyObject().addRecords(aResponse,
							DBEngine.MESSENGER_TABLE, 0,
							aResponse.getIMBuddyObject().getItemCount());
				}
				if (Logger.ENABLE)
					Logger.info(TAG,
							"updateBuddyAndGroup()[INFO]-MESSENGER BUDDY UPDATED");
			} else
				BusinessProxy.sSelf.mIMBuddyInfo = null;

		} catch (Throwable th) {
			if (Logger.ENABLE)
				Logger.error(TAG,
						"updateBuddyAndGroup()__[ERROR] - Buddy refresh failed "
								+ th.getMessage(), th);
		}
	}

	protected void showLoadingDialog() {
		showAnimationDialog("", getString(R.string.please_wait_while_loadin));
	}

	public void verifyUser(ResponseObject response) {
		if (null != this.mProgressDialog) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (Constants.FRAME_TYPE_LOGIN == response.getSentOp()) {
			if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
				DataModel.sSelf.removeObject(DMKeys.FROM_REGISTRATION);
			}
		}
		BusinessProxy.sSelf.mUsersLastActivityTime = System.currentTimeMillis();
		Intent intent = new Intent(UIActivityManager.this,
				VerificationActivity.class);
		intent.putExtra("mobile_number", SettingData.sSelf.getNumber());
		startActivityForResult(intent, VERIFICATION_REQUEST);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
	}

	public void verifyUser(boolean fromlogin) {
		if (null != this.mProgressDialog && this.mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		if (fromlogin) {
			if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
				DataModel.sSelf.removeObject(DMKeys.FROM_REGISTRATION);
			}
		}
		BusinessProxy.sSelf.mUsersLastActivityTime = System.currentTimeMillis();
		Intent intent = new Intent(UIActivityManager.this,
				VerificationActivity.class);
		intent.putExtra("mobile_number", SettingData.sSelf.getNumber());
		startActivityForResult(intent, VERIFICATION_REQUEST);
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
	}


	public void displayUpgradeScreen(final ResponseObject aResponse) {
		// String urltohit = aResponse.getUpgradeURL();
		// @ TODO - here Now we shall get the complete RTML message from server.
		// Need to display the complete message in messageActivity.
		//		String upgradeMessage = aResponse.getUpgradeURL();
		//		// TODO Open RTML and show the hard and soft upgrade
		//		if (null == upgradeMessage || 0 == upgradeMessage.trim().length())
		//			upgradeMessage = "http://" + M_DOT;// Default
		//		// URL;//"http://m.rocketalk.com";//
		//		// Default URL
		//		final String url = upgradeMessage.trim();
		//		final int upType = aResponse.getUpgradeType();
		//		if (!url.startsWith("http")) {
		//			DataModel.sSelf.storeObject(CustomViewDemo.RTML_MESSAGE_DATA,
		//					aResponse);
		//			final int type = aResponse.getUpgradeType();
		//			if (type == 1)
		//				finish();// new added by nag
		//			Intent intent = new Intent(UIActivityManager.this,
		//					CustomViewDemo.class);
		//			if (DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION)) {
		//				intent.putExtra("PAGE", (byte) 101);
		//			} else {
		//				intent.putExtra("PAGE", (byte) 1);
		//			}
		//			startActivityForResult(intent, UPGRADE_REQUEST);
		//		} else {
		//			DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
		//				public void onClick(DialogInterface dialog, int which) {
		//					switch (which) {
		//					case 0:
		//					case DialogInterface.BUTTON_POSITIVE:
		//						Intent view = new Intent(Intent.ACTION_VIEW);
		//						view.setData(Uri.parse(url));
		//						startActivity(view);
		//						break;
		//					case 1:
		//					case DialogInterface.BUTTON_NEGATIVE:
		//						if (upType == 1) {
		//							finish();
		//							RocketalkApplication.killApplication();
		//						} else if (sScreenNumber == SCREEN_LOGIN
		//								|| sScreenNumber == SCREEN_REGISTRATION
		//								|| sScreenNumber == SCREEN_RESET_PASSWORD)
		//							if (aResponse.getResponseCode() == Constants.RES_TYPE_SOFT_UPGRADE_WITH_VERIFICATION) {
		//								verifyUser(aResponse);
		//							} else {
		//								pushNewScreen(KainatHomeActivity.class, false);
		//							}
		//						break;
		//					}
		//				}
		//			};
		//			if (aResponse.getUpgradeType() == 1)// Hard Upgrade
		//				showAlertMessage("RockeTalk",
		//						"New version is available. Do you want to upgrade?",
		//						new int[] { DialogInterface.BUTTON_POSITIVE,
		//						DialogInterface.BUTTON_NEGATIVE },
		//						new String[] { "OK", "Exit" },
		//						new DialogInterface.OnClickListener[] { exitHandler,
		//						exitHandler });
		//			else
		//				showAlertMessage("RockeTalk",
		//						"New version is available. Do you want to upgrade?",
		//						new int[] { DialogInterface.BUTTON_POSITIVE,
		//						DialogInterface.BUTTON_NEGATIVE },
		//						new String[] { "Yes", "No" },
		//						new DialogInterface.OnClickListener[] { exitHandler,
		//						exitHandler });
		//		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// System.out.println("resultonactivity====== call in uiactivity");
		// super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == RESULT_OK) {
			// System.out.println("UiActivityManager::onActivityResult requestCode - "+requestCode);
			switch (requestCode) {
			case UPGRADE_REQUEST:
				//				if (sScreenNumber == SCREEN_LOGIN
				//				|| sScreenNumber == SCREEN_REGISTRATION
				//				|| sScreenNumber == SCREEN_RESET_PASSWORD) {
				//					Object data = DataModel.sSelf.removeObject(CustomViewDemo.RTML_MESSAGE_DATA);
				//					ResponseObject response = (ResponseObject) data;
				//					if (response.getResponseCode() == Constants.RES_TYPE_SOFT_UPGRADE_WITH_VERIFICATION) {
				//						verifyUser(response);
				//					} else {
				//						pushNewScreen(KainatHomeActivity.class, false);
				//					}
				//				}
				return;
			case REQUEST_CONNECT_DEVICE:
				// try {
				// if (resultCode == Activity.RESULT_OK) {
				//
				// String address = intent.getExtras().getString(
				// DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				//
				// BluetoothDevice device =
				// BusinessProxy.sSelf.mBluetoothAdapter
				// .getRemoteDevice(address);
				// if (BusinessProxy.sSelf.mChatService != null) {
				// switch (BusinessProxy.sSelf.mChatService.getState()) {
				// case BluetoothChatService.STATE_CONNECTED:
				// break;
				// case BluetoothChatService.STATE_LISTEN:
				// break;
				// default:
				// break;
				// }
				//
				// BusinessProxy.sSelf.mChatService.connect(device);
				// }
				// // System.out.println("=================a");
				// }
				// } catch (Exception e) {
				// // TODO: handle exception
				// }
				break;
			case REQUEST_ENABLE_BT:

				if (resultCode == Activity.RESULT_OK) {
					// if(mBluetoothAdapter!=null
					// &&BusinessProxy.sSelf.bluetoothDeviceName.length()<2 )
					// System.out.println("UIActivityManager::REQUEST_ENABLE_BT");
					if (BusinessProxy.sSelf.bluetoothDeviceName == null)
						BusinessProxy.sSelf.bluetoothDeviceName = BusinessProxy.sSelf.mBluetoothAdapter
						.getName();

					ChangeDeviceName();

					//					searchDevice();
					//					if (BusinessProxy.sSelf.mChatService == null) {
					//						BusinessProxy.sSelf.mChatService = new BluetoothChatService(UIActivityManager.this, mHandler);
					//						mOutStringBuffer = new StringBuffer("");
					//					}

					// System.out.println("============b");
					// setupChat();
				} else {

					// Log.d(TAG, "BT not enabled");
					Toast.makeText(this, R.string.bt_not_enabled_leaving,
							Toast.LENGTH_SHORT).show();
					// finish();
				}
				break;
			}

		} else if (resultCode == RESULT_CANCELED) {

		}
		//		if (BusinessProxy.sSelf.addPush != null
		//				&& BusinessProxy.sSelf.addPush.isAddDisplaying())
		//			BusinessProxy.sSelf.addPush.onActivityResult(requestCode,
		//					resultCode, intent);
	}

	private void updateInfoForBlinkOnGrid(String from) {

	}

	/**
	 * @param inboxData
	 * @param pos
	 */
	public void getTickerMessageAndUpdate(InboxTblObject aInboxData, int pos) {
		// TODO Auto-generated method stub
		Payload payLoad = null;
		try {
			int bitmap = ((Integer) aInboxData.getField(pos,
					InboxTblObject.INBOX_FIELD_PAYLOAD_BITMAP)).intValue();
			// Now get the ticker message
			if ((bitmap & Constants.TEXT_PAYLOAD_BITMAP) > 0) {
				payLoad = (Payload) aInboxData.mPayload.get(pos);
				DataModel.sSelf.storeObject(DMKeys.TICKER_MESSAGE, new String(
						payLoad.mText[0], 0, payLoad.mText[0].length,
						Constants.ENC));
			}
		} catch (Exception ex) {
		}
	}

	public void dismissAnimationDialog() {
		// if(lDialogProgress!=null)
		// System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
		try{
			runOnUiThread(new Runnable() {
				public void run() {
					// System.out.println("biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
					if (lDialogProgress != null && lDialogProgress.isShowing()
							&& !BusinessProxy.sSelf.dontCloseLoading) {
						lDialogProgress.dismiss();
						lDialogProgress = null;
						// System.out.println("siiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
					}
				}
			});
		}catch(Exception e){

		}

	}

	public abstract void notificationFromTransport(ResponseObject response);

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notification_toast_layout:
			mNotificationToast.cancel();
			mNotificationToast = null;
			break;
			// case R.id.goto_inbox_arrow:
			// pushNewScreen(InboxActivity.class, false);
			// break;

		default:

			break;
		}
		return;
	}

	EmoticonInf emoticonInf;
	Dialog confirmationDialog;


	// ----------------------------------------------------------------------------------------------------

	// mEmoticons.put(":)", R.drawable.android_smile);
	// mEmoticons.put("", R.drawable.android_smile);
	// mEmoticons.put("", R.drawable.androidwink);
	// mEmoticons.put("", R.drawable.androidwink);
	// mEmoticons.put("", R.drawable.androidsad);
	// mEmoticons.put("", R.drawable.androidsad);
	// mEmoticons.put("", R.drawable.cool);
	// mEmoticons.put("", R.drawable.kiss);
	// mEmoticons.put("", R.drawable.tongue);
	// mEmoticons.put("", R.drawable.tongue);
	// mEmoticons.put("", R.drawable.angry);
	// mEmoticons.put("", R.drawable.angry);
	// mEmoticons.put("", R.drawable.grin);
	// mEmoticons.put("", R.drawable.grin);
	// mEmoticons.put("", R.drawable.androidcry);
	// mEmoticons.put("", R.drawable.surprised);
	// mEmoticons.put("", R.drawable.surprised);
	public static boolean isContainEmoticon(String text) {
		if (!text.contains(":)") && !text.contains(":-)")
				&& !text.contains(";)") && !text.contains(";-)")
				&& !text.contains(":(") && !text.contains(":-(")
				&& !text.contains("B-)") && !text.contains(":-*")
				&& !text.contains(":-P") && !text.contains(":P")
				&& !text.contains("X(") && !text.contains("X-(")
				&& !text.contains(":D") && !text.contains(":-D")
				&& !text.contains(":'(") && !text.contains(":-O")
				&& !text.contains(":O")

				)
			return false;
		else
			return true;
	}

	public static Spannable getSmiledText(Context context, String text) {
		try {

			SpannableStringBuilder builder = new SpannableStringBuilder(text);

			if (!isContainEmoticon(text))
				return builder;

			int index;
			for (index = 0; index < builder.length(); index++) {
				for (Entry<String, Integer> entry : Utilities.mEmoticons
						.entrySet()) {
					int length = entry.getKey().length();
					if (index + length > builder.length())
						continue;
					if (builder.subSequence(index, index + length).toString()
							.equals(entry.getKey())) {
						builder.setSpan(
								new ImageSpan(context, entry.getValue()),
								index, index + length,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						index += length - 1;
						break;
					}
				}
			}
			return builder;
		} catch (Exception e) {
			return new SpannableStringBuilder("");
		}
	}

	// ------------------------------------------------------------------------------------------------------------
	public void sendRTMLRequest(String aDestination, String aCommand,
			String aAlert) {
		try {
			if (aDestination.startsWith("\"") && aDestination.endsWith("\""))
				aDestination = aDestination.substring(
						aDestination.indexOf("\"") + 1,
						aDestination.lastIndexOf("\""));
			if (aCommand.startsWith("\"") && aCommand.endsWith("\""))
				aCommand = aCommand.substring(aCommand.indexOf("\"") + 1,
						aCommand.lastIndexOf("\""));
			if (aAlert != null && aAlert.startsWith("\"")
					&& aAlert.endsWith("\"")
					&& !aAlert.equalsIgnoreCase("COMMENT"))
				aAlert = aAlert.substring(aAlert.indexOf("\"") + 1,
						aAlert.lastIndexOf("\""));
			OutboxTblObject newRequest = new OutboxTblObject(1,
					Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_REPLY);
			newRequest.mPayloadData.mText[0] = aCommand.getBytes(Constants.ENC);
			newRequest.mPayloadData.mTextType[0] = Constants.TEXT_TXT;
			newRequest.mPayloadData.mPayloadTypeBitmap |= Constants.TEXT_PAYLOAD_BITMAP;
			newRequest.mDestContacts = new String[] { aDestination };
			int ret = BusinessProxy.sSelf.sendToTransport(newRequest);
			if (ret == Constants.ERROR_NONE) {
				if (aAlert != null && !aAlert.equalsIgnoreCase("COMMENT"))
					showSimpleAlert(
							"Information",
							(aAlert != null) ? aAlert
									: getString(R.string.yourrequesthasbeensent));
			} else if (ret == Constants.ERROR_OUTQUEUE_FULL) {
				showSimpleAlert("Information",
						"Your outbox is full. Please try after sometime!");
			} else {
				showSimpleAlert("Information", "Network busy!");
			}

		} catch (Exception ex) {
			showSimpleAlert("Information", "Action Failed!");
		}

	}

	// ------------------------------------------------------------------------------------------------------------
	public void sendRTMLRequestWitoutAlert(String aDestination, String aCommand) {
		try {
			if (aDestination.startsWith("\"") && aDestination.endsWith("\""))
				aDestination = aDestination.substring(
						aDestination.indexOf("\"") + 1,
						aDestination.lastIndexOf("\""));
			if (aCommand.startsWith("\"") && aCommand.endsWith("\""))
				aCommand = aCommand.substring(aCommand.indexOf("\"") + 1,
						aCommand.lastIndexOf("\""));
			// if (aAlert != null && aAlert.startsWith("\"")
			// && aAlert.endsWith("\"") && !aAlert.equalsIgnoreCase("COMMENT"))
			// aAlert = aAlert.substring(aAlert.indexOf("\"") + 1,
			// aAlert.lastIndexOf("\""));
			OutboxTblObject newRequest = new OutboxTblObject(1,
					Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_REPLY);
			newRequest.mPayloadData.mText[0] = aCommand.getBytes(Constants.ENC);
			newRequest.mPayloadData.mTextType[0] = Constants.TEXT_TXT;
			newRequest.mPayloadData.mPayloadTypeBitmap |= Constants.TEXT_PAYLOAD_BITMAP;
			newRequest.mDestContacts = new String[] { aDestination };
			int ret = BusinessProxy.sSelf.sendToTransport(newRequest);
			if (ret == Constants.ERROR_NONE) {
				// if(aAlert != null && !aAlert.equalsIgnoreCase("COMMENT"))
				// showSimpleAlert(
				// "Information",
				// (aAlert != null) ? aAlert
				// :
				// "Your request has been sent! you will receive a response shortly");
			} else if (ret == Constants.ERROR_OUTQUEUE_FULL) {
				showSimpleAlert("Information",
						"Your outbox is full. Please try after sometime!");
			} else {
				showSimpleAlert("Information", "Network busy!");
			}

		} catch (Exception ex) {
			showSimpleAlert("Information", "Action Failed!");
		}

	}

	// -------------------get And set the States of the
	// machine------------------
	public void setCurrentState(int aNewState) {
		mPreviousState = mCurrentState;
		mCurrentState = aNewState;
	}

	public int getCurrentState() {
		return mCurrentState;
	}

	public int getPreviousState() {
		return mPreviousState;
	}

	// -----------------------------------------------------------------------------------
	public static void killApp(boolean killSafely) {
		if (killSafely) {
			/*
			 * Notify the system to finalize and collect all objects of the app
			 * on exit so that the virtual machine running the app can be killed
			 * by the system without causing issues. NOTE: If this is set to
			 * true then the virtual machine will not be killed until all of its
			 * threads have closed.
			 */
			System.runFinalizersOnExit(true);

			/*
			 * Force the system to close the app down completely instead of
			 * retaining it in the background. The virtual machine that runs the
			 * app will be killed. The app will be completely created as a new
			 * app in a new virtual machine running in a new process if the user
			 * starts the app again.
			 */
			System.exit(0);
		} else {
			/*
			 * Alternatively the process that runs the virtual machine could be
			 * abruptly killed. This is the quickest way to remove the app from
			 * the device but it could cause problems since resources will not
			 * be finalized first. For example, all threads running under the
			 * process will be abruptly killed when the process is abruptly
			 * killed. If one of those threads was making multiple related
			 * changes to the database, then it may have committed some of those
			 * changes but not all of those changes when it was abruptly killed.
			 */
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}

	public void cancelPostnotification() {
		try {
			stopService(new Intent(this, PostService.class));
			PostService.cancelallnotification();
		} catch (Exception e) {

		}
	}

	// ---------------------------------------------------------------------------------
	//	private class RockeTalkExceptionHandler implements UncaughtExceptionHandler {
	//		public void uncaughtException(Thread thread, Throwable throwable) {
	//			cancelPostnotification();
	//			RTMediaPlayer.setStreemSystem();
	//			FeedManager.clearAll();
	//
	//			dismissAnimationDialog();
	//			throwable.printStackTrace();
	//			final Writer result = new StringWriter();
	//			final PrintWriter printWriter = new PrintWriter(result);
	//			throwable.printStackTrace(printWriter);
	//			String stacktrace = result.toString();
	//			printWriter.close();
	//			writeToFile(stacktrace);
	//
	//			if (Logger.ENABLE)
	//				Logger.debug("Debug",
	//						"Caught exception: " + throwable.getClass().getName()
	//						+ ": " + throwable.getMessage());
	//			//
	//			// This would use the default platform exception handler:
	//			//
	//			// oldHandler.uncaughtException(thread, throwable);
	//			final DialogInterface.OnClickListener exitHandler = new DialogInterface.OnClickListener() {
	//
	//				public void onClick(DialogInterface dialog, int which) {
	//					switch (which) {
	//					case DialogInterface.BUTTON_POSITIVE:
	//						// AlarmManager mgr = (AlarmManager)
	//						// getSystemService(Context.ALARM_SERVICE);
	//						// mgr.set(AlarmManager.RTC, System.currentTimeMillis()
	//						// + 2500, intent);
	//						// System.exit(2);
	//						// Reset the network
	//
	//						BusinessProxy.sSelf.stopNetworkActivity();
	//						BusinessProxy.sSelf.iNotificationMessage = null;
	//						BusinessProxy.sSelf.mContactUploadedToServer = false;
	//						BusinessProxy.sSelf.newMessageCount = 0;
	//						Utilities.saveChatData();
	//						// Restart the application
	//						finish();
	//						Intent i = getBaseContext().getPackageManager()
	//								.getLaunchIntentForPackage(
	//										"com.kainat.app.android");
	//						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//						startActivity(i);
	//						break;
	//					default:
	//						finish();
	//						Intent intent = new Intent(Intent.ACTION_MAIN);
	//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	//						intent.addCategory(Intent.CATEGORY_HOME);
	//						startActivity(intent);
	//						RocketalkApplication.killApplication();
	//						break;
	//					}
	//				}
	//			};
	//			// showAlertMessage("Error!",
	//			// "Some unexpected exception caught. \nPlease exit the application or Re-launch the application!!",
	//			// new int[] { DialogInterface.BUTTON_POSITIVE,
	//			// DialogInterface.BUTTON_NEGATIVE }, new String[] { "Re-launch",
	//			// "Exit" }, new DialogInterface.OnClickListener[] { exitHandler,
	//			// exitHandler });
	//		}
	//	}

	private static void writeToFile(String stacktrace) {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		File file = new File(path + "/kainat/crahs.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					true));
			Date d = new Date();
			writer.write(":::::::::::::::::::::::::::: Date :" + d.toString());
			writer.write("--------totalMemory------------" + ""
					+ Runtime.getRuntime().totalMemory() / 1024 + " KB");
			writer.write("--------freeMemory------------" + ""
					+ Runtime.getRuntime().freeMemory() / 1024 + " KB");
			writer.write(stacktrace);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static int messageCounter = 0;
	public static final int VALUE_TO_EMAIL = 5;
	public void writeLogsToFile(String message) {
		String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kainat/logs.txt";
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
					true));
			Date d = new Date();
			writer.write(":::::::::::::::::::::::::::: Date :" + d.toString()+"->");
			writer.write(message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			messageCounter++;
			if(messageCounter == VALUE_TO_EMAIL)
			{
				BufferedReader br = null;
				String sCurrentLine;
				StringBuffer buffer = new StringBuffer();
				try {
					br = new BufferedReader(new FileReader(fileName));
					while ((sCurrentLine = br.readLine()) != null) {
						//						System.out.println(sCurrentLine);
						buffer.append(sCurrentLine);
						buffer.append("\n");
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
						//Send email
						messageCounter = 0;

					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}


	public void sendEmail(String aSubject, String aMessage) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { "mahesh@citrusplatform.com"});
		i.putExtra(Intent.EXTRA_SUBJECT, aSubject);
		i.putExtra(Intent.EXTRA_TEXT, aMessage);
		try {
			startActivity(Intent.createChooser(i,
					"Crash - Please send email to us for better experience"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(UIActivityManager.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}


	public void hideSoftKeyPad(EditText to) {

		// to = (EditText) findViewById(R.id.composeScreen_msgBox);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(to.getWindowToken(), 0);
	}

	public static Hashtable<String, Long> iFrndBuzzTimes;

	public boolean checkFrndBuzzTimes(String afrnd) {
		/*
		 * if(iFrndBuzzTimes == null) iFrndBuzzTimes = new Hashtable(3);
		 */
		boolean retVal = false;
		if (iFrndBuzzTimes.containsKey(afrnd)) {
			Long time = (Long) iFrndBuzzTimes.get(afrnd);
			long time2 = System.currentTimeMillis() / 1000;
			boolean timeDiff = (((time2 - time.longValue()) / 60) >= iBuzzTime[1] / 60);
			if (timeDiff) {
				deleteFrndBuzzTimes(afrnd);
				retVal = true;
			} else
				retVal = false;
		} else {
			retVal = true;
		}

		return retVal;
	}

	public void addFrndBuzzTimes(String afrnd) {
		Long time = new Long(System.currentTimeMillis() / 1000);

		if (!iFrndBuzzTimes.containsKey(afrnd))
			iFrndBuzzTimes.put(afrnd, time);
	}

	public void deleteFrndBuzzTimes(String afrnd) {
		if (iFrndBuzzTimes.containsKey(afrnd))
			iFrndBuzzTimes.remove(afrnd);
	}

	public boolean checkEmail(String emailstring) {
		if (emailstring == null)
			return false;
		String pattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+";
		Pattern emailPattern = Pattern.compile(pattern);
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}

	public boolean checkPhone(String phone) {
		String pattern = "^[+]?[0-9]{7,13}$";
		Pattern emailPattern = Pattern.compile(pattern);
		Matcher emailMatcher = emailPattern.matcher(phone);
		return emailMatcher.matches();
	}

	// public String prepareProperUrl(String username, String email,
	// String urlFragment) {
	// StringBuffer buffer = new StringBuffer();
	// buffer.append(urlFragment);
	// int index = username.indexOf(" ");
	// if (-1 != index) {
	// String firstName = username.substring(0, index);
	// String lastName = username.substring(index + 1);
	// buffer.append("firstname=")
	// .append(URLEncoder.encode(firstName.trim()))
	// .append("&lastname=")
	// .append(URLEncoder.encode(lastName.trim()))
	// .append("&emailormobile=")
	// .append(URLEncoder.encode(email.trim()));
	// } else {
	// buffer.append("username=").append(
	// URLEncoder.encode(username.trim()));
	// }
	// return buffer.toString();
	// }
	public String prepareProperUrl(String username, String email,
			String urlFragment) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(urlFragment);
		if (username != null && username.trim().length() > 0) {

			int index = username.indexOf(" ");
			if (-1 != index) {
				String firstName = username.substring(0, index);
				String lastName = username.substring(index + 1);
				buffer.append("firstname=")
				.append(URLEncoder.encode(firstName.trim()))
				.append("&lastname=")
				.append(URLEncoder.encode(lastName.trim()))
				.append("&emailormobile=")
				.append(URLEncoder.encode(email.trim()));
			} else {
				buffer.append("username=").append(
						URLEncoder.encode(username.trim()));
			}
		} else {
			buffer.append("emailormobile=").append(
					URLEncoder.encode(email.trim()));
		}
		return buffer.toString();
	}

	public void onClick() {

		try {
			// custom dialog
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.custom);
			dialog.setTitle("Title...");

			// set the custom dialog components - text, image and button
			TextView text = (TextView) dialog.findViewById(R.id.text);
			text.setText("Android custom dialog example!");
			ImageView image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.icon);

			Button dialogButton = (Button) dialog
					.findViewById(R.id.dialogButtonOK);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			// dialog.
			dialog.show();
		} catch (Exception e) {
			System.out
			.println("##################### EXCEPTION CATCHED ############################");
			e.printStackTrace();
		}
	}

	/*
	 * public void getPermission() { try{
	 * 
	 * PackageManager p = this.getPackageManager(); //
	 * p.checkPermission(Manifest.permission.C2D_MESSAGE,
	 * "com.kainat.app.android") ; PermissionInfo[]
	 * z=p.getPackageInfo("com.kainat.app.android"
	 * ,PackageManager.GET_PERMISSIONS).permissions;
	 * if(Logger.ENABLE)Logger.debug(TAG,
	 * "-----------------------------------------------getPermission----------------------------------: "
	 * +z.length ); for (int i = 0; i < z.length; i++) {
	 * if(Logger.ENABLE)Logger.debug(TAG, z[i].name );
	 * if(Logger.ENABLE)Logger.debug(TAG, z[i].packageName ); } } catch
	 * (Exception e) { e.printStackTrace() ; } }
	 */

	Dialog mDialog;
	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};
	Dialog mAddDialog;

	SeekBar bar;

	public void openPlayScreen(String url) {
		TextView tv = (TextView) mAddDialog.findViewById(R.id.streemStatus);
		tv.setText("Please wait while getting audio...");
		tv.setTextColor(0xff6AABB4);
		RTMediaPlayer.setProgressBar(bar);
		RTMediaPlayer.setMediaHandler(new VoiceMediaHandler() {

			@Override
			public void voiceRecordingStarted(String recordingPath) {
				// TODO Auto-generated method stub

			}

			@Override
			public void voiceRecordingCompleted(String recordedVoicePath) {
				// TODO Auto-generated method stub

			}

			public void voicePlayStarted() {
				mAddDialog.findViewById(R.id.streemStatus).setVisibility(
						View.GONE);
				LinearLayout d = ((LinearLayout) mAddDialog
						.findViewById(R.id.media_play_layout));
				d.setVisibility(View.VISIBLE);
				ImageView imageView1 = (ImageView) mAddDialog
						.findViewById(R.id.media_close);// media_play
				// imageView1.setImageResource(R.drawable.pause);
				imageView1.setVisibility(View.INVISIBLE);
				imageView1.setOnClickListener(UIActivityManager.this);
				imageView1.setTag("PAUSE");

				imageView1 = (ImageView) mAddDialog
						.findViewById(R.id.media_play);
				imageView1.setOnClickListener(UIActivityManager.this);
				imageView1.setTag("PLAY");
				imageView1.setImageResource(R.drawable.pause);
				imageView1.setTag("PAUSE");
				imageView1.setVisibility(View.VISIBLE);
			}

			public void voicePlayCompleted() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// closePlayScreen();
						// System.out.println("-----voicePlayCompleted-----");
						ImageView imageView1 = (ImageView) mAddDialog
								.findViewById(R.id.media_play);
						imageView1.setImageResource(R.drawable.play);
						imageView1.setTag("PLAY");
						// resetProgress();

					}
				});

			}

			public void onDureationchanged(long total, long current) {
			}

			public void onError(int i) {
				TextView tv = (TextView) mAddDialog
						.findViewById(R.id.streemStatus);
				tv.setTextColor(0xffff0000);
				tv.setText("Unable to play please try later!");
			}
		});
		mAddDialog.findViewById(R.id.streemStatus).setVisibility(View.VISIBLE);
		RTMediaPlayer._startPlay(url);
		mAddDialog.findViewById(R.id.media_play_layout).setVisibility(
				View.VISIBLE);
	}

	public void closePlayScreen() {
		if (findViewById(R.id.media_play_layout).getVisibility() == View.VISIBLE) {
			findViewById(R.id.media_play_layout).setVisibility(View.GONE);
			if (RTMediaPlayer.isPlaying())
				RTMediaPlayer.reset();
		}
	}

	public void memory() {
		if (Logger.ENABLE)
			Logger.debug(TAG, "--------totalMemory------------" + ""
					+ Runtime.getRuntime().totalMemory() / 1024 + " KB");
		if (Logger.ENABLE)
			Logger.debug(TAG, "--------freeMemory------------" + ""
					+ Runtime.getRuntime().freeMemory() / 1024 + " KB");
		// android.os.Debug.MemoryInfo info = android.os.Debug.MemoryInfo
		// long freeMemory = (Runtime.getRuntime().maxMemory()) -
		// (Debug.getNativeHeapAllocatedSize());
		// Toast.makeText(this, "freeMemory : "+(freeMemory / 1024)+ " KB",
		// Toast.LENGTH_SHORT).show();
		// Toast.makeText(this, "t: "+Runtime.getRuntime().totalMemory() / 1024
		// + " KB f: "+" : "+Runtime.getRuntime().freeMemory() / 1024 +
		// " KB all :"
		// +" : "+Debug.getNativeHeapAllocatedSize() / 1024 + " KB"
		// , Toast.LENGTH_SHORT).show();
	}

	public void openBrowser(String url) {
		try {
			// System.out.println("--------------openBrowser---------------" +
			// url);
			Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri
					.parse(url));
			startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void voiceRecordingStarted(final String recordingPath) {
	}

	public void voiceRecordingCompleted(final String recordedVoicePath) {
	}

	public void voicePlayStarted() {
	}

	public void voicePlayCompleted() {
	}

	public void closeActionSheet() {
		CustomMenu.hide();
	}

	public QuickAction quickAction, quickActionUserStstus;
	private static final int ID_SEND_MESSAGE = 1;
	private static final int ID_START_CHAT = 2;
	private static final int ID_CLEAR = 3;
	private static final int ID_CANCEL = 4;
	// protected PopupWindow mWindow;
	protected WindowManager mWindowManager;
	Timer mCallBackTimer;

	public void showMyStatus(final String msg, final boolean isArrow,
			final Bitmap icon) {

		if (mCallBackTimer != null)
			mCallBackTimer.cancel();

		mCallBackTimer = new Timer();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {
					int ids[] = { 1 };
					String[] name = { msg };
					if (quickActionUserStstus != null
							&& quickActionUserStstus.isShowing()) {
						quickActionUserStstus.dismiss();
						quickActionUserStstus = null;
					}
					quickActionUserStstus = new QuickAction(
							UIActivityManager.this, QuickAction.VERTICAL, true,
							false, name.length);
					quickActionUserStstus.addAlertText(null, msg);

					quickActionUserStstus.hideAarrow();
					quickActionUserStstus.showThumb();
					if (icon != null)
						quickActionUserStstus.addThumb(icon);
					/*
					 * for (int i = 0; i < name.length; i++) { ActionItem
					 * nextItem = new ActionItem(ids[i], name[i], (isArrow ==
					 * true) ?
					 * getResources().getDrawable(R.drawable.nextarrow):null);
					 * quickAction.addActionItem(nextItem); }
					 */

					quickActionUserStstus
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
						@Override
						public void onItemClick(QuickAction source,
								int pos, int actionId) {
							ActionItem actionItem = quickActionUserStstus
									.getActionItem(pos);
						}
					});

					quickActionUserStstus
					.setOnDismissListener(new QuickAction.OnDismissListener() {
						@Override
						public void onDismiss() {
						}
					});

					quickActionUserStstus
					.show2(findViewById(R.id.notificationdiloag));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// mCallBackTimer.schedule(new OtsSchedularTask(popListner, 1, (byte)
		// 0), 10000);
	}

	public void NotificationDissmis() {

	}

	public void showConnentAlert(final String sender, final String msg, final boolean isArrow,
			final byte activityType) {

		if (mCallBackTimer != null)
			mCallBackTimer.cancel();

		mCallBackTimer = new Timer();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				try {

					int ids[] = { 1 };
					String[] name = { msg };
					if (quickAction != null && quickAction.isShowing())
						quickAction.dismiss();
					quickAction = new QuickAction(UIActivityManager.this,
							QuickAction.VERTICAL, true, true, name.length);
					quickAction.addAlertText(sender, msg);
					if (isArrow == true) {
						OnClickListener clickListener = new OnClickListener() {

							@Override
							public void onClick(View v) {
								if (quickAction != null
										&& quickAction.isShowing())
									quickAction.dismiss();
								cancelNotification();

								Intent intent = null;
								if (activityType == com.kainat.app.android.bean.Message.MESSAGE_TYPE_SYSTEM_MESSAGE) {
									//									BusinessProxy.sSelf.addPush.closeAdd();
									intent = new Intent(UIActivityManager.this,
											OtherMessageActivity.class);
									intent.putExtra("What", 1);
									intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
									//									startActivityForResult(intent, "OtherMessageActivity".hashCode());// (intent);c
									startActivityForResult(intent, 111);// (intent);c
									//									MediaEngine.getMediaEngineInstance().playResource(R.raw.beep);
								} else {
									intent = new Intent(UIActivityManager.this,
											KainatHomeActivity.class);
									intent.putExtra(
											Constants.ACTIVITY_FOR_RESULT, true);
									startActivityForResult(intent,
											111);
								}

								// pushNewScreen(InboxActivity.class, false);
							}
						};
						quickAction.arrowListener(clickListener);
						MediaEngine.getMediaEngineInstance().playResource(R.raw.ring1);
					} else
						quickAction.hideAarrow();
					/*
					 * for (int i = 0; i < name.length; i++) { ActionItem
					 * nextItem = new ActionItem(ids[i], name[i], (isArrow ==
					 * true) ?
					 * getResources().getDrawable(R.drawable.nextarrow):null);
					 * quickAction.addActionItem(nextItem); }
					 */

					quickAction
					.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
						@Override
						public void onItemClick(QuickAction source,
								int pos, int actionId) {

							ActionItem actionItem = quickAction
									.getActionItem(pos);
						}
					});

					quickAction
					.setOnDismissListener(new QuickAction.OnDismissListener() {
						@Override
						public void onDismiss() {
						}
					});
					try {
						if (findViewById(R.id.notificationdiloag) != null)// nagendra
							// stop
							// popup
							quickAction
							.show2(findViewById(R.id.notificationdiloag));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		});
		mCallBackTimer.schedule(new OtsSchedularTask(popListner, 1, (byte) 0),
				5000);
	}

	OnSchedularListener popListner = new OnSchedularListener() {

		@Override
		public void onTaskCallback(Object parameter, byte mRequestObjNo) {

			try {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						try {
							if (quickAction != null && quickAction.isShowing())
								quickAction.hide();
							if (quickActionUserStstus != null) {
								quickActionUserStstus.hide();
								quickActionUserStstus = null;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};
	Spinner option_spinner ;
	public void openActionSheet(HashMap<Integer, String> menuItems,
			final OnMenuItemSelectedListener onMenuItemSelectedListener,
			HashMap<Integer, Integer> drableIcon) {

		//		CharSequence colors[] = new CharSequence[] {"red", "green", "blue", "black"};

		Set key =  menuItems.keySet() ;
		String arr[] = new String[key.size()]  ;
		final Integer val[] = new Integer[key.size()]  ;
		int i =0 ;
		for (Entry<Integer, String> entry : menuItems.entrySet()) {
			arr[i] = entry.getValue() ;
			val[i++] = entry.getKey();
		}




		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.options);
		builder.setItems(arr, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(onMenuItemSelectedListener!=null){
					onMenuItemSelectedListener.MenuItemSelectedEvent(val[which]) ;
				}
			}
		});
		builder.show();
		//		if(1==1)return ;
		//		String[] some_array = getResources().getStringArray(R.array.language);
		//		option_spinner = (Spinner) findViewById(R.id.option_spinner) ;
		//		option_spinner.setAdapter(new ArrayAdapter<String>(this,
		//				R.layout.language_testview_screen, some_array));
		//
		//		option_spinner.performClick() ;
		//		option_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		//			@Override
		//			public void onItemSelected(AdapterView<?> parentView, View arg1,
		//					int arg2, long arg3) {
		//				
		////				try{
		////				((TextView)parentView.getChildAt(0)).setBackgroundColor(getResources().getColor(R.color.list_background));
		////				((TextView)parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.sub_heading));
		////				}catch (Exception e) {
		////					// TODO: handle exception
		////				}
		//
		////				if (select) {
		////					switch (arg2) {
		////					case 1:
		////						setLocale("en");
		////						finish();
		////						Intent intent = new Intent(LanguageActivity.this,
		////								LoginActivity.class);
		////						startActivity(intent);
		////						break;
		////					case 2:
		////						setLocale("ar");
		////						finish();
		////						intent = new Intent(LanguageActivity.this,
		////								LoginActivity.class);
		////						startActivity(intent);
		////						break;
		////					case 3:
		////						setLocale("ar");
		////						break;
		////					default:
		////						break;
		////					}
		////					
		////				}
		//			}
		//
		//			//
		//			@Override
		//			public void onNothingSelected(AdapterView<?> arg0) {
		//				System.out.println("onNothingSelected");
		//			}
		//		});
		////		
		//		CustomMenu
		//				.show(this, menuItems, onMenuItemSelectedListener, drableIcon);
	}

	public void openActionSheet(Activity context,
			HashMap<Integer, String> menuItems,
			OnMenuItemSelectedListener onMenuItemSelectedListener,
			HashMap<Integer, Integer> drableIcon) {
		CustomMenu.show(context, menuItems, onMenuItemSelectedListener,
				drableIcon);
	}

	public void openActionSheetPos(HashMap<Integer, String> menuItems,
			OnMenuItemSelectedListener onMenuItemSelectedListener,
			int bottomPos, HashMap<Integer, Integer> drableIcon) {

		CustomMenu.showPos(this, menuItems, onMenuItemSelectedListener,
				bottomPos, drableIcon);
	}

	class popupWindows extends PopupWindows {
		LayoutInflater mInflater;

		public popupWindows(Context context) {
			super(context);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			setRootViewId(R.layout.action_item_vertical);

		}

		public void setRootViewId(int id) {
			mRootView = (ViewGroup) mInflater.inflate(id, null);
			/*
			 * mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
			 * 
			 * mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
			 * mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
			 * 
			 * mScroller = (ScrollView) mRootView.findViewById(R.id.scroller);
			 */

			// This was previously defined on show() method, moved here to
			// prevent force close that occured
			// when tapping fastly on a view to show quickaction dialog.
			// Thanx to zammbi (github.com/zammbi)
			mRootView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			setContentView(mRootView);
		}
	}

	private View mRootView;

	public void show(View anchor) {

		PopupWindows popupWindows = new popupWindows(this);

		int xPos, yPos, arrowPos;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		// mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT));

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();
		int rootWidth = 0;
		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

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
		yPos = anchorRect.top - rootHeight;
		/*
		 * if (onTop) { if (rootHeight > dyTop) { yPos = 15; LayoutParams l =
		 * mScroller.getLayoutParams(); l.height = dyTop - anchor.getHeight(); }
		 * else { yPos = anchorRect.top - rootHeight; } } else { yPos =
		 * anchorRect.bottom;
		 * 
		 * if (rootHeight > dyBottom) { LayoutParams l =
		 * mScroller.getLayoutParams(); l.height = dyBottom; } }
		 * 
		 * showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);
		 * 
		 * setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		 */
		popupWindows.mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos,
				yPos);
	}

	public void cancelOperation() {

	}

	public void cancelAlert() {

	}

	private HttpResponse getResponse(final String url)
			throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		// httpget.setHeader("RT-APP-KEY",
		// HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(),
		// SettingData.sSelf.getPassword()));
		return client.execute(httpget);
	}

	public void onError(int i) {
	}

	public void onDureationchanged(long total, long current) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			YelatalkApplication.am.adjustStreamVolume(
					AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
					AudioManager.FLAG_SHOW_UI);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			YelatalkApplication.am.adjustStreamVolume(
					AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
					AudioManager.FLAG_SHOW_UI);
			return true;
		default:

			// return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onAuthSucceed() {

	}

	public void onAuthFail(String error) {

	}

	public void onLogoutBegin() {
		// mText.setText("Logging out...");
	}

	public void onLogoutFinish() {

	}

	public AsyncFacebookRunner mAsyncRunner;

	public void initFB() {
		try {
			Login login = new Login(this);
			if (BusinessProxy.sSelf.mFacebook == null)
				BusinessProxy.sSelf.mFacebook = new Facebook(
						Constants.FACEBOOK_APP_ID);

			mAsyncRunner = new AsyncFacebookRunner(
					BusinessProxy.sSelf.mFacebook);

			// SessionStore.restore(mFacebook,mFacebookContext);
			SessionEvents.addAuthListener(new SampleAuthConfermListener());
			SessionEvents.addLogoutListener(new SampleLogoutListener());
			login.init(this, BusinessProxy.sSelf.mFacebook);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// SessionStore.restore(mFacebook, MediaVideoPlayer.this);
	}

	public class SampleAuthConfermListener implements AuthListener {

		public void onAuthSucceed() {
			try {
				// System.out.println("fburlflag======s");
				UIActivityManager.this.onAuthSucceed();
			} catch (Exception e) {
				// System.out.println("fburlflag======e");
				// System.out.println("�xception in facebook dailog==" + e);
			}
		}

		@Override
		public void onAuthFail(String error) {
			// System.out.println("fburlflag======f");
			UIActivityManager.this.onAuthFail(error);
		}

	}

	public class SampleLogoutListener implements LogoutListener {
		public void onLogoutBegin() {
			UIActivityManager.this.onLogoutBegin();
		}

		public void onLogoutFinish() {
			UIActivityManager.this.onLogoutFinish();
			SessionStore.clear(UIActivityManager.this);
		}
	}

	protected String onMbKbReturnResult(Long value) {
		String size;
		if (value / 1024.0 < 1024.0) {

			size = "" + value / 1024 + "KB";
		} else {
			double sizeInMB = value / 1024.0 / 1024.0;
			String text = Double.toString(sizeInMB);
			int index = text.indexOf('.');
			if (-1 != index && index + 5 < text.length()) {
				text = text.substring(0, index + 5);
			}
			size = "" + text + "MB";
		}

		return size;
	}

	public boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.kainat.app.android.PostService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void myScreenName(String sName) {
		// String name = Feed.getAttrCode(feed.tracking) ;
		// System.out.println("------------media screen name-------------- : "+sName);
		BusinessProxy.sSelf.dynamicScreen = sName;
		// BusinessProxy.sSelf.SEO = feed.seo;
		//		new Thread(new Runnable() {
		//			@Override
		//			public void run() {
		//				BusinessProxy.sSelf.addPush.noAddScreen = null;
		//				BusinessProxy.sSelf.addPush.pushAdd(UIActivityManager.this);
		//			}
		//		}).start();

	}

	public boolean checkInternetConnection() {
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null

				&& conMgr.getActiveNetworkInfo().isAvailable()

				&& conMgr.getActiveNetworkInfo().isConnected()) {
			// System.out.println("================network avilable==========================");
			return true;

		} else {
			// System.out.println("================network not avilable==========================");
			return false;

		}
	}

	public int headerHeight() {
		// System.out.println("--------------some error in showing add-------"+this.getClass().getSimpleName());
		String charse = this.getText(R.dimen.header_height).toString();
		String str = charse.substring(0, charse.indexOf(".0dip"));
		Integer data = Integer.parseInt(str.toString());
		// System.out.println("height==========================="+data);
		Utilities.convertDpToPixel(data, this);
		float value = Utilities.convertDpToPixel(data, this);
		int height = (int) value;
		// System.out.println("height==========================="+height);
		return height;
	}

	// 02-08 00:52:06.228: I/System.out(19150): ------------on error---
	// Received the response code 403 from the URL
	// http://124.153.95.168:28080/tejas/feeds/discovery

	public void fetchLandingInitialInfoFeed(final int requestForFeed,
			final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpSynch.getInstance().setHttpSynch(UIActivityManager.this);
				HttpSynch.getInstance().request(url, HttpSynch.METHOD_POST,
						requestForFeed, 1);
			}
		}).start();
	}

	@Override
	public void onError(String err, int requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponseSucess(Object responseStr, int requestForID) {
		// System.out.println("--------onResponseSucess uiactivity---");
		// if(!UIActivityManager.this.getClass().getSimpleName().equalsIgnoreCase("ConversationsActivity"))
		// if(FeedTask.showBuzz)
		// showBuzz();
		// if(FeedTask.showStstemMessage)
		// showStstemMessage() ;
		//
		// //
		// System.out.println("--------running activity---"+UIActivityManager.this.getClass().getSimpleName());
		// if(RefreshService.PARSE_REFRESH_CONVERSIOTION_LIST)
		// if(!UIActivityManager.this.getClass().getSimpleName().equalsIgnoreCase("InboxAvtivity")
		// &&
		// !UIActivityManager.this.getClass().getSimpleName().equalsIgnoreCase("ConversationsActivity"))
		// {
		// System.out.println("---FeedRequester.systemMessage:"+FeedRequester.systemMessage);
		// onNotification(-1) ;
		// }
	}

	@Override
	public void onResponseSucess(String respons, int requestForID, int subType,
			int totNewFeed) {
		// System.out.println("--------- : "+requestForID);
	}

	@Override
	public void onResponseSucess(String responseStr, int requestForID) {
		if (responseStr == null)
			return;
		// if(SettingData.sSelf.isIm())
		// System.out.println("--------- : "+responseStr);
		long sTime = System.currentTimeMillis();
		LandingPageBean landingPageBean;
		try {
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
					responseStr.getBytes("UTF-8"));
			Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
					arrayInputStream));
			if (requestForID == Constants.FEED_INITIAL_LANDING_PAGE) {
				NodeList nodeGeneric = doc.getElementsByTagName("feed");
				NodeList leftMenu = doc.getElementsByTagName("leftMenu");

				NodeList grid = doc.getElementsByTagName("grid");
				Element e3 = (Element) leftMenu.item(0);
				Utilities.setString(UIActivityManager.this,
						Constants.LEFT_MENU_URL, XMLUtils.getValue(e3, "url"));
				NodeList main = doc.getElementsByTagName("main");
				for (int j = 0; j < main.getLength(); j++) {
					Element e = (Element) main.item(j);
					NodeList tab = e.getElementsByTagName("tab");
					for (int n = 0; n < tab.getLength(); n++) {
						e3 = (Element) tab.item(n);
						NodeList discovery = e3
								.getElementsByTagName("discovery");
						String selected = null;
						// for (int jj = 0; jj < discovery.getLength(); jj++){
						// Element ee = (Element) discovery.item(jj);
						// selected = ee.getAttribute("selected") ;
						// }
						// if(selected!=null&&selected.trim().equalsIgnoreCase("true"))
						// Utilities.setString(UIActivityManager.this,
						// Constants.SELECTED_TAB, Constants.TAB_DISCOVERY) ;

						NodeList activity = e3.getElementsByTagName("activity");

						selected = null;
						// for (int jj = 0; jj < activity.getLength(); jj++){
						// Element ee = (Element) activity.item(jj);
						// selected = ee.getAttribute("selected") ;
						// }
						// if(selected!=null&&selected.trim().equalsIgnoreCase("true"))
						// Utilities.setString(UIActivityManager.this,
						// Constants.SELECTED_TAB, Constants.TAB_ACTIVITY) ;

						if (discovery.getLength() > 0) {
							selected = e3.getAttribute("selected");
							if (selected != null
									&& selected.trim().equalsIgnoreCase("true"))
								Utilities.setString(UIActivityManager.this,
										Constants.SELECTED_TAB,
										Constants.TAB_DISCOVERY);

							e3 = (Element) discovery.item(0);
							Utilities.setString(UIActivityManager.this,
									Constants.DISCOVERY_FEED_URL,
									XMLUtils.getValue(e3, "url"));

						}
						if (activity.getLength() > 0) {

							selected = e3.getAttribute("selected");
							if (selected != null
									&& selected.trim().equalsIgnoreCase("true"))
								Utilities.setString(UIActivityManager.this,
										Constants.SELECTED_TAB,
										Constants.TAB_ACTIVITY);
							e3 = (Element) activity.item(0);
							Utilities.setString(UIActivityManager.this,
									Constants.ACTIVITY_FEED_URL,
									XMLUtils.getValue(e3, "url"));
						}
					}
				}
				fetchLandingInitialInfoFeed(Constants.FEED_INITIAL_DISCOVERY,
						Utilities.getString(UIActivityManager.this,
								Constants.DISCOVERY_FEED_URL));
			} else if (requestForID == Constants.FEED_INITIAL_LEFT_MENU) {
				// System.out.println("------------left menu itmes--- : "+responseStr);
				NodeList leftMenu = doc.getElementsByTagName("leftMenu");
				/*
				 * // NodeList items = doc.getElementsByTagName("items");
				 * 
				 * Element e = (Element) leftMenu.item(0);
				 * 
				 * for (int x = 0; x < e.getElementsByTagName("*").getLength();
				 * x++) { Element e2 = (Element)
				 * e.getElementsByTagName("*").item(x); String strValue =
				 * e2.getTagName(); LeftMenu leftMenu2 = new LeftMenu() ;
				 * leftMenu2 = new LeftMenu() ; System.out.println(
				 * "-----------contentNode leftmenu================" +
				 * strValue); if(strValue.trim().equalsIgnoreCase("grid")){
				 * System
				 * .out.println("------------------------node is grid------------"
				 * );
				 * 
				 * leftMenu2.isgrid = true ; NodeList grow =
				 * e.getElementsByTagName("grow"); for (int np = 0; np <
				 * grow.getLength(); np++) { e = (Element) grow.item(np);
				 * NodeList entry = e.getElementsByTagName("entry");
				 * 
				 * for (int p = 0; p < entry.getLength(); p++) { LeftMenu
				 * leftMenuTenp = new LeftMenu() ; e = (Element) entry.item(p);
				 * leftMenuTenp.caption = XMLUtils.getValue(e, "caption");
				 * leftMenuTenp.action = XMLUtils.getValue(e, "action");
				 * leftMenuTenp.thumb = XMLUtils.getValue(e, "thumb"); //
				 * System.
				 * out.println("------------caption : "+XMLUtils.getValue(e,
				 * "caption")); //
				 * System.out.println("------------action : "+XMLUtils
				 * .getValue(e, "action")); //
				 * System.out.println("------------thumb : "
				 * +XMLUtils.getValue(e, "thumb")); if(leftMenu2.gridMenu==null)
				 * leftMenu2.gridMenu = new Vector<LeftMenu>() ;
				 * leftMenu2.gridMenu.add(leftMenuTenp) ; } } }
				 * if(strValue.trim().equalsIgnoreCase("entry")){
				 * System.out.println
				 * ("------------------------node is entry------------");
				 * NodeList item = e.getElementsByTagName("entry"); for (int p =
				 * 0; p < item.getLength(); p++) { e = (Element) item.item(p);
				 * //
				 * System.out.println("------------caption : "+XMLUtils.getValue
				 * (e, "caption")); //
				 * System.out.println("------------action : "
				 * +XMLUtils.getValue(e, "action")); //
				 * System.out.println("------------thumb : "
				 * +XMLUtils.getValue(e, "thumb")); leftMenu2.caption =
				 * XMLUtils.getValue(e, "caption"); leftMenu2.action =
				 * XMLUtils.getValue(e, "action"); leftMenu2.thumb =
				 * XMLUtils.getValue(e, "thumb"); } } if(leftMenu2!=null)
				 * System.
				 * out.println("------------left menu---- : "+leftMenu2.toString
				 * ()); }
				 */

				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < leftMenu.getLength(); j++) {
					Element e = (Element) leftMenu.item(j);
					NodeList item = e.getElementsByTagName("entry");
					for (int p = 0; p < item.getLength(); p++) {
						e = (Element) item.item(p);
						sb.append(XMLUtils.getValue(e, "caption")
								+ Constants.COL_SEPRETOR
								+ XMLUtils.getValue(e, "action")
								+ Constants.ROW_SEPRETOR);
					}
				}
				// System.out.println("------------left menu itmes--- : "+sb.toString());
				Utilities.setString(UIActivityManager.this,
						Constants.LEFT_MENU_URL_ITEM, sb.toString());
				sb = null;
				loadLeftProfile();
				fetchLandingInitialInfoFeed(Constants.FEED_INITIAL_DISCOVERY,
						Utilities.getString(UIActivityManager.this,
								Constants.DISCOVERY_FEED_URL));
			} else if (requestForID == Constants.FEED_INITIAL_DISCOVERY) {
				StringBuffer sb = new StringBuffer();
				{
					NodeList node = doc.getElementsByTagName("media");
					if (node.getLength() > 0) {
						for (int p = 0; p < node.getLength(); p++) {
							Element e = (Element) node.item(p);
							sb.append(e.getNodeName() + Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "subtitle")
									+ Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "feedurl")
									+ Constants.ROW_SEPRETOR);
						}
					}
					node = doc.getElementsByTagName("community");
					if (node.getLength() > 0) {
						for (int p = 0; p < node.getLength(); p++) {
							Element e = (Element) node.item(p);
							sb.append(e.getNodeName() + Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "subtitle")
									+ Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "feedurl")
									+ Constants.ROW_SEPRETOR);
						}
					}
					node = doc.getElementsByTagName("user");
					if (node.getLength() > 0) {
						for (int p = 0; p < node.getLength(); p++) {
							Element e = (Element) node.item(p);
							sb.append(e.getNodeName() + Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "subtitle")
									+ Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "feedurl")
									+ Constants.ROW_SEPRETOR);
						}
					}
					node = doc.getElementsByTagName("groupevent");
					if (node.getLength() > 0) {
						for (int p = 0; p < node.getLength(); p++) {
							Element e = (Element) node.item(p);
							sb.append(e.getNodeName() + Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "subtitle")
									+ Constants.COL_SEPRETOR
									+ XMLUtils.getValue(e, "feedurl")
									+ Constants.ROW_SEPRETOR);
						}
					}
					// System.out.println("---------------DISCOVERY_MENU_URL_ITEM-- : "+sb.toString());
					Utilities.setString(UIActivityManager.this,
							Constants.DISCOVERY_MENU_URL_ITEM, sb.toString());
					sb = null;
					Utilities.getDiscoveryBasicInfo(UIActivityManager.this);
					urlloading = true;

				}
			}
			if (requestForID == Constants.FEED_INITIAL_LEFT_MENU) {
				// runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// RenderLeftMenu(leftMenuView) ;
				// }
				// });
				loadLeftProfile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		long diff = System.currentTimeMillis() - sTime;
		// System.out
		// .println("--------------------time kaken to parse and save in data base : "
		// + (diff) + " Ms");
	}

	@Override
	public void onError(String err) {
		// System.out.println("------------on error---" + err);
	}

	int counter = 0;
	// final String
	// url="http://media.rocketalk.com/get/5_1_1_A_I_I3_dbrt5blgzh.jpg?height=75&amp;width=75";
	public static boolean leftMenuShow = false;

	// public void RenderLeftMenu(LinearLayout view) {
	//
	// }
	// LinearLayout leftMenuView;

	CImageView imgAvatar;

	public void RenderLeftMenu(LinearLayout view) {

		// LinearLayout view = new LinearLayout(this);

		// System.out.println("--------RenderLeftMenu---1");
		// System.out.println("--------Chat counter---===="+BusinessProxy.sSelf.newChatMessageCount);
		BusinessProxy.sSelf.leftMenuAdFlag = true;
		//		BusinessProxy.sSelf.addPush.closeAdd();// closeAddOnCloseClick();
		leftMenuShow = true;
		Vector<LeftMenu> data = Utilities.getLeftMenu(this);
		LayoutInflater inflater = LayoutInflater.from(this);

		if (data == null || data.size() <= 0) {
			data = Utilities.getLeftMenu2(this);
		}
		view.removeAllViews();
		// System.out.println("data==="+data);
		if (data != null && data.size() > 0) {
			try {

				for (int iTemp = 0; iTemp < data.size(); iTemp++) {
					LeftMenu value = data.get(iTemp);
					if (value.userThumbUrl != null) {
						final View userView = inflater.inflate(
								R.layout.user_profile_left_menu, null, false);
						imgAvatar = (CImageView) userView
								.findViewById(R.id.UserIcon);
						imgAvatar.setBackgroundDrawable(ImageDownloader.def2);
						imgAvatar.setTag(value.userThumbUrl);
						ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

							@Override
							public void onThumbnailResponse(
									ThumbnailImage value, byte[] data) {
								try {
									if (userView.getTag().equals(
											new String(data))) {
										BitmapDrawable background = new BitmapDrawable(
												value.mBitmap);
										imgAvatar.setImageDrawable(background);
									}
								} catch (Exception e) {
								}
							}
						};
						ImageDownloader imageManager = new ImageDownloader();

						// imageManager.download(value.userThumbUrl, imgAvatar,
						// handler, ImageDownloader.TYPE_THUMB_BUDDY);

						// imageManager.download(SettingData.sSelf.getUserName(),
						// imgAvatar,
						// handler, ImageDownloader.TYPE_THUMB_BUDDY);

						imageManager.download(SettingData.sSelf.getUserName(),
								imgAvatar, handler,
								ImageDownloader.TYPE_THUMB_BUDDY);

						imgAvatar.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// BusinessProxy.sSelf.leftMenuAdFlag = false;
								//								BusinessProxy.sSelf.addPush.closeAdd();
								//								if (BusinessProxy.sSelf.addPush != null) {
								//									BusinessProxy.sSelf.addPush.destroyView();
								//								}

								try {

									// System.out.println("----leftMenuShow:"+leftMenuShow);
									if (leftMenuShow == true) {
										try {
											menuOut = false;
											leftMenuShow = false;
											BusinessProxy.sSelf.leftMenuAdFlag = false;
											transparentBar
											.setVisibility(View.GONE);
											int menuWidth = menu
													.getMeasuredWidth();
											meniW = menuWidth;
											int right = meniW;// meniW;
											scrollView.smoothScrollTo(right, 0);
											// System.out.println("----leftMenuShow closing :"+leftMenuShow+"    right: "+right);
										} catch (Exception e) {
											// TODO: handle exception
										}
									}

								} catch (Exception e) {

								}

								DataModel.sSelf.storeObject(
										DMKeys.USER_PROFILE, DMKeys.MY_PAGE);
								// pushNewScreen(WebviewActivity.class, false);

								DataModel.sSelf.storeObject(DMKeys.NEW_INTENT,
										true);
								Intent i = new Intent(UIActivityManager.this,
										ProfileViewActivity.class);
								// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
								// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("USERID", SettingData.sSelf
										.getUserName().toLowerCase());
								// i.putExtra("USERID",postURL.substring(postURL.lastIndexOf("/")+1,
								// postURL.length())) ;
								startActivityForResult(i, 34234);

							}
						});

						ImageView verify = (ImageView) userView
								.findViewById(R.id.verify);
						if (!SettingData.sSelf.isEmailVerified())
							verify.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// System.out.println("-----verify me----"+meniW);

									//									Intent intent = new Intent(
									//											UIActivityManager.this,
									//											EmailVerifyActivity.class);
									//									startActivityForResult(intent, 1000);
									//
									//									try {
									//										leftMenuShow = true;
									//										// BusinessProxy.sSelf.leftMenuAdFlag =
									//										// false;
									//										transparentBar.setVisibility(View.GONE);
									//										int right = meniW;
									//										scrollView.smoothScrollTo(right, 0);
									//										getMenuwidth();
									//									} catch (Exception e) {
									//
									//									}
								}
							});
						else
							verify.setVisibility(View.INVISIBLE);

						TextView dataView = (TextView) userView
								.findViewById(R.id.user_Name);
						if (value.userDisplayName != null) {
							if (Logger.CHEAT)
								dataView.setText(value.userDisplayName + " id:"
										+ BusinessProxy.sSelf.getUserId());
							else
								dataView.setText(value.userDisplayName);

							if (SettingData.sSelf.getFirstName() != null
									&& SettingData.sSelf.getFirstName().trim()
									.length() > 0
									&& SettingData.sSelf.getLastName() != null
									&& SettingData.sSelf.getLastName().trim()
									.length() > 0)
								dataView.setText(SettingData.sSelf
										.getFirstName()
										+ " "
										+ SettingData.sSelf.getLastName());
							else if (SettingData.sSelf.getFirstName() != null
									&& SettingData.sSelf.getFirstName().trim()
									.length() > 0)
								dataView.setText(SettingData.sSelf
										.getFirstName());
							else if (SettingData.sSelf.getLastName() != null
									&& SettingData.sSelf.getLastName().trim()
									.length() > 0)
								dataView.setText(SettingData.sSelf
										.getLastName());
							else
								dataView.setText(SettingData.sSelf
										.getUserName());
						}
						dataView = (TextView) userView
								.findViewById(R.id.wats_thinking);
						if (value.userStatus != null)
							dataView.setText(value.userStatus);
						else
							dataView.setText("What's i am thinking?");

						dataView.setTag("goto:watsIam");

						dataView.setOnClickListener(addClickMenu);
						view.addView(userView);
						view.invalidate();
					}
					if (value.isgrid) {
						TableLayout tblLayout = new TableLayout(this);
						TableRow tr = new TableRow(this);
						tblLayout.setLayoutParams(new TableLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
						tblLayout.setPadding(3, 3, 3, 3);

						for (int jTemp = 0; jTemp < value.gridMenu.size(); jTemp++) {
							LeftMenu gridMenu = value.gridMenu.get(jTemp);
							counter = counter + 1;
							View retViewGrid = inflater.inflate(
									R.layout.left_menu_grid, null, false);
							TextView text = (TextView) retViewGrid
									.findViewById(R.id.textOne);
							text.setVisibility(View.VISIBLE);
							text.setPadding(2, 2, 2, 2);

							if (gridMenu.thumb != null) {
								final CImageView b = (CImageView) retViewGrid
										.findViewById(R.id.left_ImageGrid);
								b.setVisibility(View.VISIBLE);
								if (gridMenu.action
										.equalsIgnoreCase("goto:Friends")) {
									b.setImageDrawable(ImageDownloader.friend_slide);// Resource(R.drawable.friend_slide);
								}
								if (gridMenu.action
										.equalsIgnoreCase("goto:Inbox")) {

									if (BusinessProxy.sSelf.newMessageCount > 0) {
										CImageView noti = (CImageView) retViewGrid
												.findViewById(R.id.home_notification_grid);
										noti.setVisibility(View.VISIBLE);
										TextView textList = (TextView) retViewGrid
												.findViewById(R.id.home_notification_count_grid);
										textList.setVisibility(View.VISIBLE);
										String msg = ((BusinessProxy.sSelf.newMessageCount < 100) ? ""
												+ BusinessProxy.sSelf.newMessageCount
												: "100+");
										textList.setText(msg);// Inbox
										// notification
									}

									b.setImageDrawable(ImageDownloader.inbox_slide);// Resource(R.drawable.inbox_slide);
								}
								if (gridMenu.action
										.equalsIgnoreCase("goto:Communities")) {
									b.setImageDrawable(ImageDownloader.community_slide);// Resource(R.drawable.community_slide);
								}
								if (gridMenu.action
										.equalsIgnoreCase("goto:Media")) {
									b.setImageDrawable(ImageDownloader.media);// Resource(R.drawable.media);
								}
								if (gridMenu.action
										.equalsIgnoreCase("goto:Rtlive")) {
									b.setImageDrawable(ImageDownloader.left_rtlive);// Resource(R.drawable.left_rtlive);
								}

								LinearLayout frame = (LinearLayout) retViewGrid
										.findViewById(R.id.LinearLayout_tableView);// new
								// LinearLayout(LandingPageActivity.this);
								LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT);
								parms.gravity = Gravity.CENTER;
								parms.setMargins(5, 0, 5, 0);
								frame.setLayoutParams(parms);
								if (gridMenu.action != null) {
									b.setTag(gridMenu.action);
									b.setOnClickListener(addClickMenu);
								}
								frame.setBackgroundDrawable(ImageDownloader.leftmenugridbackground);// Resource(R.drawable.leftmenugridbackground);
								frame.setTag(gridMenu.action);
								frame.setOnClickListener(addClickMenu);
							}
							tr.setPadding(5, 0, 5, 0);
							text.setText(gridMenu.caption);
							tr.addView(retViewGrid);

						}
						tblLayout.addView(tr);
						view.addView(tblLayout);
						view.invalidate();
					} else {
						if (value.caption != null) {
							View retView = inflater.inflate(
									R.layout.left_menu_grid, null, false);

							LinearLayout frame = (LinearLayout) retView
									.findViewById(R.id.LinearLayout_main);// new
							// LinearLayout(LandingPageActivity.this);
							TextView textList = (TextView) retView
									.findViewById(R.id.textOnly);
							textList.setText(value.caption);
							textList.setPadding(0, 1, 0, 1);
							textList.setVisibility(View.VISIBLE);
							if (value.thumb != null) {
								final CImageView b = (CImageView) retView
										.findViewById(R.id.left_ImageView);
								b.setVisibility(View.VISIBLE);
								if (value.action
										.equalsIgnoreCase("goto:Rtevents")) {

									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.rtevent_slide);

									b.setImageDrawable(ImageDownloader.rtevent_slide);// Bitmap(icon)
									// ;;//setBackgroundResource(R.drawable.rtevent_slide);
								}
								if (value.action
										.equalsIgnoreCase("goto:Search")) {
									// retView.setPadding(0, 30, 0, 0);
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.search_slide);

									b.setImageDrawable(ImageDownloader.search_slide);// itmap(icon)
									// ;;//setBackgroundResource(R.drawable.rtevent_slide);
									// b.setBackgroundResource(R.drawable.search_slide);
								}

								if (value.action
										.equalsIgnoreCase("goto:MyProfile")) {
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.profile_left_new);

									b.setImageDrawable(ImageDownloader.profile_left_new);// Bitmap(icon)
									// ;
								}
								if (value.action
										.equalsIgnoreCase("goto:Communities")) {
									// retView.setPadding(0, 30, 0, 0);
									// b.setBackgroundResource(R.drawable.community_slide);

									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.community_slide);

									b.setImageDrawable(ImageDownloader.community_slide);// Bitmap(icon)
									// ;
								}

								if (value.action.equalsIgnoreCase("goto:chat")) {
									if (BusinessProxy.sSelf.newChatMessageCount > 0) {
										CImageView noti = (CImageView) retView
												.findViewById(R.id.home_notification);
										noti.setVisibility(View.VISIBLE);
										textList = (TextView) retView
												.findViewById(R.id.home_notification_count);
										textList.setVisibility(View.VISIBLE);
										String msg = ((BusinessProxy.sSelf.newChatMessageCount < 100) ? ""
												+ BusinessProxy.sSelf.newChatMessageCount
												: "100+");
										textList.setText(msg);
									}

									b.setImageDrawable(ImageDownloader.chat_slide);// Resource(R.drawable.chat_slide);
								}
								if (value.action
										.equalsIgnoreCase("goto:Settings")) {
									// b.setBackgroundResource(R.drawable.setting_slide);

									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.setting_slide);

									b.setImageDrawable(ImageDownloader.setting_slide);// Bitmap(icon)
									// ;
								}
								if (value.action
										.equalsIgnoreCase("goto:Settings")) {
									// b.setBackgroundResource(R.drawable.setting_slide);
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.setting_slide);

									b.setImageDrawable(ImageDownloader.setting_slide);
								}
								if (value.action
										.equalsIgnoreCase("goto:Invite")) {
									// b.setBackgroundResource(R.drawable.left_itinvite);
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.left_itinvite);
									b.setImageDrawable(ImageDownloader.left_itinvite);// /Bitmap(icon)
									// ;
								}
								if (value.action
										.equalsIgnoreCase("goto:Rtlive")) {
									retView.setPadding(0, 30, 0, 0);
									// b.setBackgroundResource(R.drawable.left_rtlive);
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.left_rtlive);

									b.setImageDrawable(ImageDownloader.left_rtlive);// Bitmap(icon)
									// ;
								}
								if (value.action
										.equalsIgnoreCase("goto:SendFeedback")) {
									// b.setBackgroundResource(R.drawable.leftmenu_compose);
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.leftmenu_compose);

									b.setImageDrawable(ImageDownloader.leftmenu_feedback);// Bitmap(icon)
									// ;
								}
								// if
								// (value.action.equalsIgnoreCase("goto:SendFeedback"))
								// {
								// //
								// b.setBackgroundResource(R.drawable.leftmenu_compose);
								// // Bitmap icon =
								// BitmapFactory.decodeResource(getResources(),
								// // R.drawable.leftmenu_compose);
								//
								// b.setImageDrawable(ImageDownloader.leftmenu_compose);//Bitmap(icon)
								// ;
								// }
								if (value.action
										.equalsIgnoreCase("goto:PostMedia")) {
									// b.setBackgroundResource(R.drawable.left_menu_post);
									// Bitmap icon =
									// BitmapFactory.decodeResource(getResources(),
									// R.drawable.left_menu_post);

									b.setImageDrawable(ImageDownloader.left_menu_post);// Bitmap(icon)
									// ;
								}
							}
							if (value.action != null) {
								retView.setTag(value.action);
								retView.setOnClickListener(addClickMenu);
							}
							view.addView(retView);
							view.invalidate();
							View dividerView = inflater.inflate(
									R.layout.leftt_menu_divider, null, false);
							textList = (TextView) dividerView
									.findViewById(R.id.line_divider);
							view.addView(dividerView);
							view.invalidate();

						}
					}

				}
				// TextView t = new TextView(this);
				// t.setText("Recent access community") ;
				// t.setTypeface(Typeface.DEFAULT_BOLD);
				// t.setTextSize(16);
				// t.setTextColor(getResources().getColor(R.color.red));
				// view.addView(t);
				// for (int jTemp = 0; jTemp < 10; jTemp++) {
				// t = new TextView(this);
				// t.setText("Comm xxx") ;
				// t.setTypeface(Typeface.DEFAULT);
				// t.setTextSize(16);
				// t.setTextColor(getResources().getColor(R.color.black));
				// MarginLayoutParams params = new
				// MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT,
				// MarginLayoutParams.WRAP_CONTENT);
				// params.setMargins(10, 10, 0, 0);
				// t.setLayoutParams(params) ;
				// // ((M /arginLayoutParams) t.getLayoutParams()).leftMargin =
				// 20;
				// // LinearLayout.LayoutParams layoutParams =
				// (LinearLayout.LayoutParams)t.getLayoutParams();
				// // layoutParams.setMargins(0, 20, 0, 0);
				// view.addView(t,params);
				//
				// }
			} catch (Exception e) {
				e.fillInStackTrace();
				e.printStackTrace();
			}
			leftMenuNotLoaded = false;
		} else {
			leftMenuNotLoaded = true;
			TextView loading = new TextView(this);
			loading.setText("Loading...");
			loading.setTextColor(getResources().getColor(R.color.nag_green));
			loading.setGravity(Gravity.CENTER);
			view.addView(loading);
			if (FeedRequester.feedTaskLeftMenu != null)
				FeedRequester.feedTaskLeftMenu
				.setHttpSynch(UIActivityManager.this);
		}
		view.refreshDrawableState();
	}

	public static boolean leftMenuNotLoaded = true;
	public View.OnClickListener addClickMenu = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			BusinessProxy.sSelf.leftMenuAdFlag = false;
			if (!leftMenuShow)
				return;
			leftMenuShow = false;
			String getTagValue = (String) v.getTag();
			String ActionType = null;
			String ActionValue = null;
			if (getTagValue.indexOf("goto:") != -1
					|| getTagValue.indexOf("GOTO:") != -1) {
				ActionType = getTagValue.trim().substring(0, 5);
				ActionValue = getTagValue.trim().substring(5,
						getTagValue.length());
			}
			if (ActionValue != null && ActionType != null
					&& ActionType.equalsIgnoreCase("goto:")) {
				if (ActionValue.equalsIgnoreCase("inbox")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					cancelNotification();

					// pushNewScreen(KainatHomeActivity.class, false);

					finish();
					Intent intent1 = new Intent(UIActivityManager.this,
							KainatHomeActivity.class);

					startActivity(intent1);
					overridePendingTransition(R.anim.slide_left_in,
							R.anim.slide_left_out);
				} else if (ActionValue.equalsIgnoreCase("mypage")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					//
					//					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
					//							DMKeys.MY_PAGE);
					//					pushNewScreen(WebviewActivity.class, false);
				} else if (ActionValue.equalsIgnoreCase("invite")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					// pushNewScreen(InviteActivity.class, false);

					//					Intent intent = new Intent(UIActivityManager.this,
					//							BuddyActivity.class);
					//					intent.putExtra(BuddyActivity.SCREEN_MODE,
					//							BuddyActivity.MODE_ONLY_CONTACT);
					//
					//					pushNewScreen(intent, BuddyActivity.class, true);
					//					overridePendingTransition(R.anim.slide_left_in,
					//							R.anim.slide_left_out);
					//					// intent.putExtra("FOR_RESULT", true);
					//					// startActivityForResult(intent, 0);
					//					leftMenuShow = true;
				} else if (ActionValue.equalsIgnoreCase("Friends")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					//					pushNewScreen(BuddyActivity.class, false, true);
				} else if (ActionValue.equalsIgnoreCase("chat")) {
					// BusinessProxy.sSelf.addPush.closeAdd();
					// pushNewScreen(ChatListActivity.class, false);
					// BusinessProxy.sSelf.addPush.closeAdd();
					// pushNewScreen(KainatHomeActivity.class, false);
					// finish();
					// Intent intent1 = new Intent(UIActivityManager.this,
					// KainatHomeActivity.class);
					// startActivity(intent1);
					leftMenuShow = true;
				} else if (ActionValue.equalsIgnoreCase("media")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					//					pushNewScreen(MediaActivity.class, false);
				} else if (ActionValue.equalsIgnoreCase("Communities")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					//					DataModel.sSelf.removeString(CommunityActivity.MEDIA_URL);
					//					pushNewScreen(KainatHomeActivity.class, false);
				} 
				else if (ActionValue.equalsIgnoreCase("home")) {
					// BusinessProxy.sSelf.addPush.closeAdd();
					// pushNewScreen(KainatHomeActivity.class, false);
				} 
				//				else if (ActionValue.equalsIgnoreCase("watsIam")) {
				////					BusinessProxy.sSelf.addPush.closeAdd();
				//					pushNewScreen(MyStatus.class, false);
				//				} 
				else if (ActionValue.equalsIgnoreCase("Rtlive")) {
					Utilities.setInt(UIActivityManager.this, Utilities.homeTab, R.landingpage.activity);
					pushNewScreen(KainatHomeActivity.class, false);
				} 
				else if (ActionValue.equalsIgnoreCase("MyProfile")) {
					try {
						leftMenuShow = true;
						if (leftMenuShow == true) {
							try {
								menuOut = false;
								leftMenuShow = false;
								// BusinessProxy.sSelf.leftMenuAdFlag = false;
								transparentBar.setVisibility(View.GONE);
								int menuWidth = menu.getMeasuredWidth();
								meniW = menuWidth;
								int right = meniW;// meniW;
								scrollView.smoothScrollTo(right, 0);
								// System.out.println("----leftMenuShow closing :"+leftMenuShow+"    right: "+right);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}

					} catch (Exception e) {

					}

					DataModel.sSelf.storeObject(DMKeys.USER_PROFILE,
							DMKeys.MY_PAGE);
					// pushNewScreen(WebviewActivity.class, false);

					DataModel.sSelf.storeObject(DMKeys.NEW_INTENT, true);
					Intent i = new Intent(UIActivityManager.this,
							ProfileViewActivity.class);
					// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("USERID", SettingData.sSelf.getUserName()
							.toLowerCase());
					// i.putExtra("USERID",postURL.substring(postURL.lastIndexOf("/")+1,
					// postURL.length())) ;

					startActivityForResult(i, 34234);
					overridePendingTransition(R.anim.slide_left_in,
							R.anim.slide_left_out);
				} else if (ActionValue.equalsIgnoreCase("PostMedia")) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					Utilities.setInt(UIActivityManager.this, Utilities.homeTab,
							R.landingpage.activity);

					leftMenuShow = true;

					if (leftMenuShow == true) {
						try {
							menuOut = false;
							leftMenuShow = false;
							// BusinessProxy.sSelf.leftMenuAdFlag = false;
							transparentBar.setVisibility(View.GONE);
							int menuWidth = menu.getMeasuredWidth();
							meniW = menuWidth;
							int right = meniW;// meniW;
							scrollView.smoothScrollTo(right, 0);
							// System.out.println("----leftMenuShow closing :"+leftMenuShow+"    right: "+right);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}
	};

	public static int meniW = 0;
	View menu;

	class ClickListenerForScrollingLeftTransparent implements OnClickListener {
		HorizontalScrollView scrollView;

		// LinearLayout
		// leftLayout=(LinearLayout)leftMenu.findViewById(R.id.list_layout);

		// RenderLeftMenu(leftLayout);

		public ClickListenerForScrollingLeftTransparent(
				HorizontalScrollView scrollView, View menul) {
			super();

			this.scrollView = scrollView;
			menu = menul;
		}

		@Override
		public void onClick(View v) {
			Context context = menu.getContext();
			String msg = "Slide " + new Date();
			// Toast.makeText(context, msg, 1000).show();
			// System.out.println(msg);
			// leftMenu.setAdapter(leftMenuAdaptor);
			// LinearLayout
			// leftLayout=(LinearLayout)leftMenu.findViewById(R.id.list_layout);

			// RenderLeftMenu(leftLayout);
			menuOut = false;
			int menuWidth = menu.getMeasuredWidth();

			// Ensure menu is visible

			// Scroll to menuWidth so menu isn't on screen.
			transparentBar.setVisibility(View.GONE);
			int right = menuWidth;
			// meniW=menuWidth;
			// System.out.println("-----verify me2----"+meniW);

			scrollView.smoothScrollTo(right, 0);
			getMenuwidth();

		}
	}

	public static int menuWidth = 0;

	public void getMenuwidth() {
		if (menu != null)
			menuWidth = scrollView.getScrollX();// .getMeasuredWidth();

	}

	// boolean menuOut = false;
	class ClickListenerForScrollingLeft implements OnClickListener {
		HorizontalScrollView scrollView;
		View menu;

		// LinearLayout
		// leftLayout=(LinearLayout)leftMenu.findViewById(R.id.list_layout);

		// RenderLeftMenu(leftLayout);

		public ClickListenerForScrollingLeft(HorizontalScrollView scrollView,
				View menu) {
			super();

			this.scrollView = scrollView;
			this.menu = menu;
		}

		@Override
		public void onClick(View v) {
			//			BusinessProxy.sSelf.addPush.closeAdd();
			BusinessProxy.sSelf.leftMenuAdFlag = true;
			// System.out.println("<<<Left menu Tapped - "+BusinessProxy.sSelf.leftMenuAdFlag);
			try {
				if (CustomMenu.isShowing())
					CustomMenu.hideRet();
				if (quickAction != null)
					quickAction.hide();
			} catch (Exception e) {
				// TODO: handle exception
			}

			menuLeft.setVisibility(View.VISIBLE);
			Context context = menu.getContext();
			String msg = "Slide " + new Date();
			// Toast.makeText(context, msg, 1000).show();
			// System.out.println(msg);
			// leftMenu.setAdapter(leftMenuAdaptor);
			// LinearLayout
			// leftLayout=(LinearLayout)leftMenu.findViewById(R.id.list_layout);
			final LinearLayout leftLayout = (LinearLayout) leftMenu
					.findViewById(R.id.list_layout);
			//			if (BusinessProxy.sSelf.addPush != null) {
			//				BusinessProxy.sSelf.addPush.destroyView();
			//			}
			RenderLeftMenu(leftLayout);
			// RenderLeftMenu(leftLayout);

			// System.out.println("------checking profile updation---1");
			// hitting server for check updation
			if (((System.currentTimeMillis() - Utilities.getLong(
					UIActivityManager.this, Constants.MENU_RFRESH_TIME))) > Constants.MENU_RFRESH_TIME_INTERVAL) {
				// System.out.println("------checking profile updation---");
				if (imgAvatar == null)
					imgAvatar = new CImageView(UIActivityManager.this);
				ImageDownloader imageManager = new ImageDownloader();
				imageManager.download(SettingData.sSelf.getUserName(),
						imgAvatar, new ThumbnailReponseHandler() {
					@Override
					public void onThumbnailResponse(
							ThumbnailImage value, byte[] data) {
					}
				}, ImageDownloader.TYPE_THUMB_BUDDY);
				Utilities.setLong(UIActivityManager.this,
						Constants.MENU_RFRESH_TIME, System.currentTimeMillis());
			}

			int menuWidth = menu.getMeasuredWidth();
			meniW = menuWidth;
			// System.out.println("-----verify me33----"+meniW);
			// Ensure menu is visible
			menu.setVisibility(View.VISIBLE);

			if (!menuOut) {
				// Scroll to 0 to reveal menu
				int right = 0;
				transparentBar.setVisibility(View.VISIBLE);
				//				BusinessProxy.sSelf.addPush.closeAdd();// AddOnCloseClick();
				//				if (BusinessProxy.sSelf.addPush != null) {
				//					BusinessProxy.sSelf.addPush.destroyView();
				//				}
				BusinessProxy.sSelf.leftMenuAdFlag = true;
				// meniW=right;
				// System.out.println("-----verify me4----"+meniW);
				scrollView.smoothScrollTo(right, 0);
				getMenuwidth();
				// System.out.println("----leftMenuShow closing close :"+leftMenuShow+"    right: "+right);
			} else {
				// Scroll to menuWidth so menu isn't on screen.
				//				BusinessProxy.sSelf.addPush.closeAdd();// closeAddOnCloseClick();
				//				if (BusinessProxy.sSelf.addPush != null) {
				//					BusinessProxy.sSelf.addPush.destroyView();
				//				}
				BusinessProxy.sSelf.leftMenuAdFlag = false;
				transparentBar.setVisibility(View.GONE);
				int right = menuWidth;

				meniW = right;
				// System.out.println("-----verify me3----"+meniW);
				// System.out.println("----leftMenuShow closing open :"+leftMenuShow+"    right: "+right);
				scrollView.smoothScrollTo(right, 0);
				getMenuwidth();
			}
			menuOut = !menuOut;
		}
	}

	private boolean bellMenuOpen = false;

	public class ClickListenerForScrollingRight implements OnClickListener {
		public void onClick(View v) {
			try {

				if (bellMenuOpen) {
					quickAction.dismiss();
					bellMenuOpen = false;
					return;
				}

				int idsOptions[] = new int[] { 1, 2, 3 };
				int value[] = new int[] { 0, 0, 0 };// new int[3];
				// Drawable draw[]=new Drawable[3];

				String idsNameOptions[] = new String[] { "Activity Updates",
						"Notifications", "Messages" };/* "Friend Request", */
				// "Friend Request" };

				// Cursor cursor = getContentResolver().query(
				// MediaContentProvider.CONTENT_URI_INBOX, null,
				// MessageTable.MESSAGE_TYPE + " = ?", new String[] {
				// ""+com.kainat.app.android.bean.Message.MESSAGE_TYPE_FRIEND_REQUEST
				// },
				// MessageTable.INSERT_TIME+" ASC");//LandingPageTable.STORY_ID
				// + " ASC" DESC
				// if(cursor.getCount()>0){
				// idsNameOptions[2] =
				// idsNameOptions[2]+"("+cursor.getCount()+")" ;
				// }
				// Cursor cursor = getContentResolver().query(
				// MediaContentProvider.CONTENT_URI_INBOX, null,
				// // MessageTable.MESSAGE_TYPE +
				// " = ? or "+MessageTable.MESSAGE_TYPE + " =? ",
				// MessageTable.MESSAGE_TYPE +
				// " = ? and "+MessageTable.USER_ID+" =? and "+MessageTable.HAS_BEEN_VIEWED+" ='false'",
				// new String[] {
				// ""+com.kainat.app.android.bean.Message.MESSAGE_TYPE_SYSTEM_MESSAGE,BusinessProxy.sSelf.getUserId()+""
				// },
				// null);

				Cursor cursor = BusinessProxy.sSelf.sqlDB
						.query(MessageTable.TABLE,
								null,
								MessageTable.USER_ID + " = ? and "
										+ MessageTable.MESSAGE_TYPE
										+ " =? and "
										+ MessageTable.HAS_BEEN_VIEWED + " =?",
										new String[] {
								BusinessProxy.sSelf.getUserId() + "",
								""
										+ com.kainat.app.android.bean.Message.MESSAGE_TYPE_SYSTEM_MESSAGE,
						"false" }, null, null,
						MessageTable.INSERT_TIME + " DESC");

				// LandingPageTable.STORY_ID + " ASC" DESC*/
				int sysCoutn = cursor.getCount();
				cursor.close();

				if (sysCoutn > 0) {// cursor.getCount()>0){
					idsNameOptions[1] = idsNameOptions[1];// +"("+cursor.getCount()+")"
					// ;
					value[1] = sysCoutn;// cursor.getCount();
				}
				//				final String s[] = Utilities.getActivityCount(BusinessProxy.sSelf.addPush.mContext);
				//				try {
				//					int feedData = Integer.parseInt(s[0]);
				//					value[0] = feedData;// cursor.getCount();
				//				} catch (Exception e) {
				//					// TODO: handle exception
				//				}

				// value[2]=cursor.getCount();
				if (newMessageFlag) {
					value[2] = 1;
					Drawable draw[] = {
							getResources().getDrawable(
									R.drawable.notification_slide_count),
									getResources().getDrawable(
											R.drawable.notification_slide_count),
											getResources().getDrawable(
													R.drawable.notification_slide_count) };
					actionForNotification(idsOptions, idsNameOptions, (byte) 3,
							draw, value);
				} else {
					Drawable draw[] = {
							getResources().getDrawable(
									R.drawable.notification_slide_count),
									getResources().getDrawable(
											R.drawable.notification_slide_count), null };
					actionForNotification(idsOptions, idsNameOptions, (byte) 3,
							draw, value);
				}

				// System.out.println("is showing()"+quickAction.isShowing());

				/*
				 * if(quickAction!=null && quickAction.isShowing()){
				 * quickAction.hide(); quickAction.dismiss();
				 * quickAction.onDismiss();
				 * 
				 * System.out.println("is dismiss()"); }else{
				 */
				quickAction.show(v);
				bellMenuOpen = true;

				// }

				// RocketalkApplication.killApplication();
				// try{
				// final String s[] =
				// Utilities.getActivityCount(UIActivityManager.this) ;
				// if(s!=null&&s[0]!=null && s.length > 0 && s[0]!=null &&
				// s[1]!=null&&Integer.parseInt(s[0].trim())>0)
				// {
				// Utilities.setInt(UIActivityManager.this, Utilities.homeTab,
				// R.landingpage.activity);
				// Utilities.setString(UIActivityManager.this,
				// Constants.NOTIFICATIO_COUNT, "") ;
				// Utilities.setBoolean(UIActivityManager.this,
				// Constants.CLICK_ON_ACTIVITY_NOTIFICATION, true) ;
				// BusinessProxy.sSelf.addPush.closeAdd();
				//
				// Intent intent = new Intent(UIActivityManager.this,
				// KainatHomeActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				// | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// pushNewScreen(KainatHomeActivity.class, false);
				// }
				// else
				// {
				// showToast("No new activity") ;
				// }
				// }catch (Exception e) {
				// showToast("No new activity") ;
				// }
			} catch (Exception e) {
				// TODO: handle exception
				// System.out.println("Exception==="+e);
				e.printStackTrace();
			}
		}
	}

	public void actionForNotification(int ids[], String[] name,
			final byte type, Drawable drawImg[], int value[]) {
		try {
			quickAction = new QuickAction(this, QuickAction.VERTICAL, false,
					true, name.length);

			for (int i = 0; i < name.length; i++) {
				ActionItem nextItem = new ActionItem(ids[i], name[i],
						drawImg[i], value[i]);
				quickAction.addActionItem(nextItem);
			}

			quickAction
			.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
				@Override
				public void onItemClick(QuickAction source, int pos,
						int actionId) {

					if (actionId == 1) {// notification

						try {
							final String s[] = Utilities
									.getActivityCount(UIActivityManager.this);
							if (s != null
									&& s[0] != null
									&& s.length > 0
									&& s[0] != null
									&& s[1] != null
									&& Integer.parseInt(s[0].trim()) > 0) {
								Utilities.setInt(
										UIActivityManager.this,
										Utilities.homeTab,
										R.landingpage.activity);
								Utilities
								.setString(
										UIActivityManager.this,
										Constants.NOTIFICATIO_COUNT,
										"");
								Utilities
								.setBoolean(
										UIActivityManager.this,
										Constants.CLICK_ON_ACTIVITY_NOTIFICATION,
										true);
								//								BusinessProxy.sSelf.addPush.closeAdd();
								//								if (BusinessProxy.sSelf.addPush != null) {
								//									BusinessProxy.sSelf.addPush
								//									.destroyView();
								//								}
								Intent intent = new Intent(
										UIActivityManager.this,
										KainatHomeActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
								pushNewScreen(KainatHomeActivity.class,
										false);
							} else {
								// showToast("No new activity");
								Intent intent = new Intent(
										UIActivityManager.this,
										KainatHomeActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
										| Intent.FLAG_ACTIVITY_CLEAR_TOP);
								pushNewScreen(KainatHomeActivity.class,
										false);
							}
						} catch (Exception e) {
							// showToast("No new activity");
							Intent intent = new Intent(
									UIActivityManager.this,
									KainatHomeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
									| Intent.FLAG_ACTIVITY_CLEAR_TOP);
							pushNewScreen(KainatHomeActivity.class,
									false);
						}
					} else if (actionId == 2) {
						if (!UIActivityManager.this
								.getClass()
								.getSimpleName()
								.equalsIgnoreCase(
										"OtherMessageActivity")) {
							// System.out.println("--------running activity---"+UIActivityManager.this.getClass().getSimpleName());
							//							BusinessProxy.sSelf.addPush.closeAdd();
							Intent intent = new Intent(
									UIActivityManager.this,
									OtherMessageActivity.class);
							intent.putExtra("What", 1);
							FeedRequester.systemMessage = 0;

							intent.putExtra(
									Constants.ACTIVITY_FOR_RESULT, true);

							startActivityForResult(intent,
									111);// (intent);c
							onNotification(-1, null, null);
						}
					} else if (actionId == 3) {
						/*
						 * if(!UIActivityManager.this.getClass().
						 * getSimpleName
						 * ().equalsIgnoreCase("OtherMessageActivity")){
						 * /
						 * /System.out.println("--------running activity---"
						 * +
						 * UIActivityManager.this.getClass().getSimpleName
						 * ()); Intent intent = new
						 * Intent(UIActivityManager.this,
						 * OtherMessageActivity.class);
						 * intent.putExtra("What", 2);
						 * FeedRequester.systemMessage=0;
						 * intent.putExtra
						 * (Constants.ACTIVITY_FOR_RESULT, true);
						 * startActivityForResult(intent,
						 * "OtherMessageActivity"
						 * .hashCode());//(intent);c }
						 */
						//						BusinessProxy.sSelf.addPush.closeAdd();
						Intent intent = new Intent(
								UIActivityManager.this,
								KainatHomeActivity.class);
						// intent.putExtra("What", 2);
						// FeedRequester.systemMessage=0;
						// intent.putExtra(Constants.ACTIVITY_FOR_RESULT,
						// true);
						startActivity(intent);// (intent);c
					} else if (actionId == 4) {

					}
				}
			});

			quickAction
			.setOnDismissListener(new QuickAction.OnDismissListener() {
				@Override
				public void onDismiss() {
					// Toast.makeText(getApplicationContext(),
					// "Dismissed",
					// Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void action(int ids[], String[] name, final byte type) {
		quickAction = new QuickAction(this, QuickAction.VERTICAL, false, true,
				name.length);

		for (int i = 0; i < name.length; i++) {
			ActionItem nextItem = new ActionItem(ids[i], name[i], null, 0);
			quickAction.addActionItem(nextItem);
		}

		quickAction
		.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(QuickAction source, int pos,
					int actionId) {

				if (actionId == 1) {// notification

					try {

						final String s[] = Utilities
								.getActivityCount(UIActivityManager.this);
						if (s != null && s[0] != null && s.length > 0
								&& s[0] != null && s[1] != null
								&& Integer.parseInt(s[0].trim()) > 0) {
							Utilities.setInt(UIActivityManager.this,
									Utilities.homeTab,
									R.landingpage.activity);
							Utilities.setString(UIActivityManager.this,
									Constants.NOTIFICATIO_COUNT, "");
							Utilities
							.setBoolean(
									UIActivityManager.this,
									Constants.CLICK_ON_ACTIVITY_NOTIFICATION,
									true);
							//							BusinessProxy.sSelf.addPush.closeAdd();
							//							if (BusinessProxy.sSelf.addPush != null) {
							//								BusinessProxy.sSelf.addPush
							//								.destroyView();
							//							}
							Intent intent = new Intent(
									UIActivityManager.this,
									KainatHomeActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
									| Intent.FLAG_ACTIVITY_CLEAR_TOP);
							pushNewScreen(KainatHomeActivity.class,
									false);
						} else {
							showToast("No new activity");
						}
					} catch (Exception e) {
						showToast("No new activity");
					}
				} else if (actionId == 2) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					Intent intent = new Intent(UIActivityManager.this,
							OtherMessageActivity.class);
					intent.putExtra("What", 1);
					intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
					startActivityForResult(intent,
							111);// (intent);c
				} else if (actionId == 3) {
					//					BusinessProxy.sSelf.addPush.closeAdd();
					Intent intent = new Intent(UIActivityManager.this,
							OtherMessageActivity.class);
					intent.putExtra("What", 2);
					intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);

					startActivityForResult(intent,
							111);// (intent);c

				} else if (actionId == 4) {

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

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 200;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	public class MyGestureDetector extends SimpleOnGestureListener {

		HorizontalScrollView scrollView;
		View menuLeft;
		Context context;

		public MyGestureDetector(HorizontalScrollView scrollView,
				View menuLeft, Context context) {
			this.scrollView = scrollView;
			this.menuLeft = menuLeft;
			this.context = context;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				menuLeft.setVisibility(View.VISIBLE);
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					int menuWidth = menuLeft.getMeasuredWidth();
					int right = menuWidth;
					scrollView.smoothScrollTo(right, 0);
					getMenuwidth();
					// new ViewMover(menuLeft,right);
					// listViewActivity.setOnTouchListener(null);
					// Ensure menu is visible
					//					BusinessProxy.sSelf.addPush.closeAdd();// closeAddOnCloseClick();
					//					if (BusinessProxy.sSelf.addPush != null) {
					//						BusinessProxy.sSelf.addPush.destroyView();
					//					}
					BusinessProxy.sSelf.leftMenuAdFlag = false;
					menuOut = false;
					transparentBar.setVisibility(View.GONE);
					// Toast.makeText(context, "Left Swipe",
					// Toast.LENGTH_SHORT).show();
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// Toast.makeText(LandingPageActivity.this, "Right Swipe",
					// Toast.LENGTH_SHORT).show();
					// leftMenu.setAdapter(leftMenuAdaptor);

					final LinearLayout leftLayout = (LinearLayout) leftMenu
							.findViewById(R.id.list_layout);
					transparentBar.setVisibility(View.VISIBLE);
					//					BusinessProxy.sSelf.addPush.closeAdd();// closeAddOnCloseClick();
					//					if (BusinessProxy.sSelf.addPush != null) {
					//						BusinessProxy.sSelf.addPush.destroyView();
					//					}
					BusinessProxy.sSelf.leftMenuAdFlag = true;
					RenderLeftMenu(leftLayout);
					menuLeft.setVisibility(View.VISIBLE);
					int right = 0;
					// listViewActivity.setOnTouchListener(gestureListener);
					scrollView.smoothScrollTo(right, 0);
					getMenuwidth();
					// new ViewMover(menuLeft,right);
					// Toast.makeText(context, "right Swipe",
					// Toast.LENGTH_SHORT).show();
					// if (!menuOut) {
					// Scroll to 0 to reveal menu

					// } else {
					// Scroll to menuWidth so menu isn't on screen.

					// }
					// menuOut = !menuOut;
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}

	// please dont delete for future use
	/*
	 * static class ClickListenerForScrollingRight implements OnClickListener {
	 * HorizontalScrollView scrollView; View menu;
	 * 
	 * boolean menuOut = false;
	 * 
	 * public ClickListenerForScrollingRight(HorizontalScrollView scrollView,
	 * View menu) { super(); this.scrollView = scrollView; this.menu = menu; }
	 * 
	 * @Override public void onClick(View v) { Context context =
	 * menu.getContext(); String msg = "Slide " + new Date(); //
	 * Toast.makeText(context, msg, 1000).show(); System.out.println(msg); //
	 * System.out.println("app width==="+app.getMeasuredWidth()); int menuWidth
	 * = menu.getMeasuredWidth(); //
	 * System.out.println("menu  width==="+menuWidth); // Ensure menu is visible
	 * menu.setVisibility(View.VISIBLE);
	 * 
	 * if (!menuOut) { // Scroll to 0 to reveal menu int left = 2 * menuWidth -
	 * 2 * btnSlideRight.getMeasuredWidth();
	 * 
	 * scrollView.smoothScrollTo(left, 0); } else {
	 * 
	 * // Scroll to menuWidth so menu isn't on screen. int left = menuWidth -
	 * btnSlideRight.getMeasuredWidth(); scrollView.smoothScrollTo(left, 0); }
	 * menuOut = !menuOut; } }
	 */

	public class SizeCallbackForMenu implements SizeCallback {
		int btnWidth;
		View btnSlide;

		public SizeCallbackForMenu(View btnSlide) {
			super();
			this.btnSlide = btnSlide;
		}

		@Override
		public void onGlobalLayout() {
			btnWidth = btnSlide.getMeasuredWidth();
			// System.out.println("btnWidth=" + btnWidth);
		}

		@Override
		public void getViewSize(int idx, int w, int h, int[] dims) {
			dims[0] = w;
			dims[1] = h;
			final int menuIdx = 0;
			if (idx == menuIdx) {
				switch (phoneType) {
				case MEDIUM_PHONE:
					// dims[0] = w - 3 / 2 * btnWidth;
					dims[0] = w - btnWidth;
					break;
				case LARGE_PHONE:
				case XLARGE_PHONE:
					// dims[0] = w - 5 / 2 * btnWidth;
					dims[0] = w - btnWidth;
					break;
				default:
					dims[0] = w - btnWidth;
					break;
				}
			}
		}
	}

	MyHorizontalScrollView scrollView;
	View menuLeft, menuRight;
	View app;
	ImageView btnSlideLeft, btnSlideRight;

	boolean menuOut = false;
	public ScrollView leftMenu;
	private GestureDetector gestureDetector1;
	View.OnTouchListener gestureListener1;
	TextView transparentBar;

	public void addEvent() {
		if (nativeSlide)
			return;
		// this.app=app;
		btnSlideRight = (ImageView) app
				.findViewById(R.id.landing_discovery_BtnSlideRight);
		btnSlideRight.setOnClickListener(new ClickListenerForScrollingRight());

		transparentBar = ((TextView) app.findViewById(R.id.transparent_image));
		transparentBar
		.setOnClickListener(new ClickListenerForScrollingLeftTransparent(
				scrollView, menuLeft));

		btnSlideLeft = (ImageView) app
				.findViewById(R.id.landing_discovery_BtnSlideLeft);
		btnSlideLeft.setOnClickListener(new ClickListenerForScrollingLeft(
				scrollView, menuLeft));
		// transparentBar.setVisibility(View.GONE);
	}

	SimpleSideDrawer slide_me;

	boolean nativeSlide = false;

	public View screenSlide(Context mContext, int id) {
		try {

			if(1==1){
				LayoutInflater inflater1 = LayoutInflater.from(mContext);

				//				app = inflater1.inflate(R.layout.drawarer_new_nagendra, null);

				app  = inflater1.inflate(id, null);

				try{
					app.findViewById(R.id.landing_discovery_tabBar).setVisibility(View.GONE);

					menuNew = (ImageButton)app.findViewById(R.id.menu) ;		
					if(menuNew!=null)
						menuNew.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								mDrawerLayout.openDrawer(mDrawerList) ;	
							}
						});

				}catch (Exception e) {
					e.printStackTrace();
				}

				//				((FrameLayout)findViewById(R.id.frame_container)).addView(app) ;
				setContentView(app);
				return app;
			}
			LayoutInflater inflater = LayoutInflater.from(mContext);
			scrollView = (MyHorizontalScrollView) inflater.inflate(
					R.layout.horz_scroll_with_list_menu, null);

			menuLeft = inflater.inflate(R.layout.horz_scroll_menu_left, null);

			app = inflater.inflate(id, null);

			// setContentView(app);

			if ((Utilities.getBoolean(this, Constants.REGISTRATION))) {
				((LinearLayout) app.findViewById(R.id.landing_discovery_tabBar))
				.setVisibility(View.GONE);

			}
			leftMenu = (ScrollView) menuLeft.findViewById(R.id.list);

			btnSlideRight = (ImageView) app
					.findViewById(R.id.landing_discovery_BtnSlideRight);
			btnSlideRight
			.setOnClickListener(new ClickListenerForScrollingRight());

			transparentBar = ((TextView) app
					.findViewById(R.id.transparent_image));
			transparentBar
			.setOnClickListener(new ClickListenerForScrollingLeftTransparent(
					scrollView, menuLeft));

			btnSlideLeft = (ImageView) app
					.findViewById(R.id.landing_discovery_BtnSlideLeft);
			btnSlideLeft.setOnClickListener(new ClickListenerForScrollingLeft(
					scrollView, menuLeft));

			final View[] children = new View[] { menuLeft, app };

			int scrollToViewIdx = 1;
			final LinearLayout leftLayout = (LinearLayout) leftMenu.findViewById(R.id.list_layout);
			scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu(btnSlideLeft));
			menuLeft.setVisibility(View.GONE);
			gestureDetector = new GestureDetector(new MyGestureDetector(scrollView, menuLeft, mContext));
			gestureListener = new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					return gestureDetector.onTouchEvent(event);
				}
			};
			transparentBar.setOnTouchListener(gestureListener);
			btnSlideLeft.setOnTouchListener(gestureListener);
			setContentView(scrollView);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return app;
	}

	public void shift(View menu) {
		int menuWidth = menu.getMeasuredWidth();

		// Ensure menu is visible

		// Scroll to menuWidth so menu isn't on screen.
		transparentBar.setVisibility(View.GONE);
		int right = menuWidth;
		scrollView.smoothScrollTo(-right, 0);
		getMenuwidth();

	}

	@Override
	public void onNotificationThreadInbox(int requestId, String sender, String msg) {
		try {
			if (FeedTask.showBuzz) {
				if (BusinessProxy.sSelf.startRecording)
					return;
				showBuzz();
			}
			//			if (FeedTask.showStstemMessage)
			//				showStstemMessage();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	int sysNotCount = 0;

	boolean newMessageFlag = false;

	public void onNotification(int requestCode, String sender, String msg) 
	{
		try {
			if (Utilities.getBoolean(UIActivityManager.this, Constants.REGISTRATION))
				return;
			newMessageFlag = (FeedRequester.latestMessage.size() > 0) ? true : false;

			if (Constants.FEED_NOTIFICATION_COUNT != requestCode && (FeedRequester.latestMessage != null
					&& FeedRequester.latestMessage.size() > 0 || (FeedRequester.systemMessage > 0))) 
			{
				if (UIActivityManager.this.getClass().getSimpleName().equalsIgnoreCase("ConversationsActivity")
						&& (FeedRequester.latestMessage != null
						&& FeedRequester.latestMessage.size() > 0 || (FeedRequester.systemMessage > 0))) 
				{
					boolean dontShow = false;
					for (int i = 0; i < FeedRequester.latestMessage.size(); i++) 
					{
						com.kainat.app.android.bean.Message message = FeedRequester.latestMessage.get(i);
						if (message.conversationId.equalsIgnoreCase(ConversationsActivity.CONVERSATIONID)) {
							dontShow = true;
							break;
						}
					}
					if (!dontShow)
						showNewMessageAlert(null, null);
				} else{
					if(msg.startsWith("{\"") && msg.contains("\"deleteMessageAPI\":")){
						KainatHomeActivity.refreshNotificationCounter();
					}
					else
						showNewMessageAlert(sender, msg);
				}
				FeedRequester.systemMessage = 0;
			}

			Cursor cursor = BusinessProxy.sSelf.sqlDB.query(MessageTable.TABLE, null,
					MessageTable.USER_ID + " = ? and " + MessageTable.MESSAGE_TYPE + " =? and "
							+ MessageTable.HAS_BEEN_VIEWED + " =?",
							new String[] {
					BusinessProxy.sSelf.getUserId() + "", "" + com.kainat.app.android.bean.Message.MESSAGE_TYPE_SYSTEM_MESSAGE,
			"false" }, null, null,
			MessageTable.INSERT_TIME + " DESC");

			FeedRequester.systemMessage = cursor.getCount();

			cursor.close();
			// FeedRequester.systemMessage = 2 ;
			final int scount = FeedRequester.systemMessage;
			//if(Constants.FEED_NOTIFICATION_COUNT == requestCode||FeedRequester.systemMessage > 0)
			//			{
			//				final String s[] = Utilities.getActivityCount(this);
			//				if (app != null)
			//					if ((s != null && s.length > 1 && s[0] != null
			//					&& s.length > 0 && s[0] != null && s[1] != null && Integer.parseInt(s[0].trim()) > 0)
			//					|| FeedRequester.systemMessage > 0) 
			//					{
			//						runOnUiThread(new Runnable() {
			//
			//							@Override
			//							public void run() {
			//								try {
			//									// Thread.sleep(1000);
			//									TextView tv = (TextView) app.findViewById(R.id.landing_discovery_activity_notification);
			//									tv.setTextColor(Color.WHITE);
			//									tv.setTextSize(10);
			//									int totalCount = 0;// Integer.parseInt(s[0].trim());
			//									if (s != null && s.length > 0 &&  s[0] != null && s[0].length() > 0)
			//										totalCount = Integer.parseInt(s[0].trim());
			//									totalCount = totalCount + scount;
			//									FeedRequester.systemMessage = 0;
			//									if (totalCount > 50)
			//										tv.setText("" + 50 + "+");
			//									else
			//										tv.setText("" + totalCount);
			//									tv.setVisibility(View.VISIBLE);
			//										tv = KainatHomeActivity.counter;
			//										tv.setTextColor(Color.WHITE);
			//										tv.setTextSize(10);
			//										totalCount = 0;// Integer.parseInt(s[0].trim());
			//										if (s != null && s.length > 0 && s[0].length() > 0)
			//											totalCount = Integer.parseInt(s[0].trim());
			//										totalCount = totalCount + scount;
			//										FeedRequester.systemMessage = 0;
			//										if (totalCount > 50)
			//											tv.setText("" + 50 + "+");
			//										else
			//											tv.setText("" + totalCount);
			//										tv.setVisibility(View.VISIBLE);
			//										tv.invalidate();
			//								} catch (Exception e) {
			//									e.printStackTrace();
			//								}
			//							}
			//						});
			//
			//					} 
			//					else 
			//					{
			//						if (newMessageFlag || app.findViewById(R.id.landing_discovery_activity_notification).getVisibility() == View.VISIBLE)
			//							runOnUiThread(new Runnable() {
			//
			//								@Override
			//								public void run() {
			//									try {
			//										if (newMessageFlag) 
			//										{
			//											app.findViewById(R.id.landing_discovery_activity_notification).setVisibility(View.VISIBLE);
			//											app.findViewById(R.id.landing_discovery_activity_notification).setBackgroundResource(R.drawable.notification_slide_count);
			//											((TextView) app.findViewById(R.id.landing_discovery_activity_notification)).setText("1");
			//										} 
			//										else 
			//										{
			//											app.findViewById(R.id.landing_discovery_activity_notification).setVisibility(View.GONE);
			//										}
			//									} catch (Exception e) {
			//									}
			//								}
			//							});
			//					}
			//			}
		} catch (Exception e) {
			// e.printStackTrace();
			FeedRequester.systemMessage = 0;
		}
	}

	public void activityForResult(Class<?> clazz) {
		Intent intent = new Intent(this, clazz);
		intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
		startActivityForResult(intent, Constants.ACTIVITY_FOR_RESULT_INT);
	}

	public void stopMedia() {
		try {
			RTMediaPlayer.reset();
			RTMediaPlayer.clear();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void networkLossAlert() {

		// runOnUiThread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // Toast t = Toast.makeText(UIActivityManager.this,
		// // "Please check network connection!", Toast.LENGTH_LONG);
		// // t.setGravity(Gravity.CENTER, 0, 0);
		// // t.show();
		//
		// showAlertMessage("Alert",
		// "Please check network connection!", null,
		// null, null);
		// }
		// });

		showAlertMessage("Alert", getString(R.string.check_net_connection), null,
				null, null);
	}

	public void bluetoothInit() {

		// mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// ensureDiscoverable();
		if (BusinessProxy.sSelf.mBluetoothAdapter != null
				&& BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()) {
			// if(BusinessProxy.sSelf.bluetoothDeviceName.length()<2)
			if (BusinessProxy.sSelf.bluetoothDeviceName == null)
				BusinessProxy.sSelf.bluetoothDeviceName = BusinessProxy.sSelf.mBluetoothAdapter
				.getName();
			if (BusinessProxy.sSelf.bluetoothDeviceName != null
					&& BusinessProxy.sSelf.bluetoothDeviceName.indexOf(" (") != -1
					&& BusinessProxy.sSelf.bluetoothDeviceName.indexOf(")") != -1)
				BusinessProxy.sSelf.bluetoothDeviceName = BusinessProxy.sSelf.bluetoothDeviceName
				.substring(BusinessProxy.sSelf.bluetoothDeviceName
						.indexOf(" (") + 2,
						BusinessProxy.sSelf.bluetoothDeviceName
						.indexOf(")"));
			else if (BusinessProxy.sSelf.bluetoothDeviceName != null
					&& BusinessProxy.sSelf.bluetoothDeviceName.indexOf(" (") != -1)
				BusinessProxy.sSelf.bluetoothDeviceName = BusinessProxy.sSelf.bluetoothDeviceName
				.substring(BusinessProxy.sSelf.bluetoothDeviceName
						.indexOf(" (") + 2);
			ChangeDeviceName();
			//			searchDevice();
		} else if (BusinessProxy.sSelf.mBluetoothAdapter != null
				&& !BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()) {
			// if(BusinessProxy.sSelf.bluetoothDeviceName.length()<2)
			// BusinessProxy.sSelf.bluetoothDeviceName=mBluetoothAdapter.getName();
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		if (BusinessProxy.sSelf.mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			// finish();
			return;
		}
	}

	public void ChangeDeviceName() {
		// Log.i(LOG,
		// "localdevicename : "+bluetoothAdapter.getName()+" localdeviceAddress : "+bluetoothAdapter.getAddress());
		if (BusinessProxy.sSelf.mBluetoothAdapter != null)
			// BusinessProxy.sSelf.mBluetoothAdapter.setName("RockeTalk-"+SettingData.sSelf.getUserName());
			BusinessProxy.sSelf.mBluetoothAdapter.setName(SettingData.sSelf
					.getUserName()
					+ " ("
					+ BusinessProxy.sSelf.bluetoothDeviceName + ")");

		// Log.i(LOG,
		// "localdevicename : "+bluetoothAdapter.getName()+" localdeviceAddress : "+bluetoothAdapter.getAddress());
	}

	/*
	 * public void rechangeDeviceName(String name){ //Log.i(LOG,
	 * "localdevicename : "
	 * +bluetoothAdapter.getName()+" localdeviceAddress : "+bluetoothAdapter
	 * .getAddress()); if(BusinessProxy.sSelf.mBluetoothAdapter!=null &&
	 * name!=null) BusinessProxy.sSelf.mBluetoothAdapter.setName(name);
	 * //Log.i(LOG,
	 * "localdevicename : "+bluetoothAdapter.getName()+" localdeviceAddress : "
	 * +bluetoothAdapter.getAddress()); }
	 */

	@Override
	protected void onStart() {
		super.onStart();

		// FlurryAgent.setUserId("" + LoginInformation.getInstance().USERID);
		//		FlurryAgent.onStartSession(this, Constants.FLURRY_API_KEY);

		// if(D) Log.e(TAG, "++ ON START ++");
		/*
		 * if(BusinessProxy.sSelf.mBluetoothAdapter!=null){
		 * 
		 * if (!BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()) { //
		 * mBluetoothAdapter.enable(); // Intent enableIntent = new
		 * Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		 * //startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		 * 
		 * } else { if (mChatService == null){ //setupChat(); mChatService = new
		 * BluetoothChatService(UIActivityManager.this, mHandler);
		 * 
		 * mOutStringBuffer = new StringBuffer("");
		 * //System.out.println("MchatServics ==== call"); }
		 * //System.out.println("MchatServics ==== not call"); } }
		 */
		// System.out.println("MchatServics==== not call");
	}

	/*
	 * @Override public void onDestroy() { super.onDestroy();
	 * 
	 * if (mChatService != null) mChatService.stop(); // if(D) Log.e(TAG,
	 * "--- ON DESTROY ---"); }
	 */

	public void ensureDiscoverablebluetooth() {
		// if(D) Log.d(TAG, "ensure discoverable");
		if (BusinessProxy.sSelf.mBluetoothAdapter == null)
			BusinessProxy.sSelf.mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
		if (BusinessProxy.sSelf.mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	//	public void searchDevice() {
	//		Intent serverIntent = new Intent(this, DeviceListActivity.class);
	//		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	//	}
	//
	//	public void sendFriendRequest(String message) {
	//
	//		if (BusinessProxy.sSelf.mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	//			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
	//			.show();
	//			return;
	//		}
	//
	//		if (message.length() > 0) {
	//
	//			byte[] send = message.getBytes();
	//			BusinessProxy.sSelf.mChatService.write(send);
	//
	//			mOutStringBuffer.setLength(0);
	//			// mOutEditText.setText(mOutStringBuffer);
	//		}
	//	}

	boolean sopFlag = false;
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (sopFlag)
				System.out
				.println("UIActivityManager::Handler::handleMessage msg -"
						+ msg.toString());
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				// if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				if (sopFlag)
					System.out
					.println("UIActivityManager::Handler::handleMessage MESSAGE_STATE_CHANGE -"
							+ msg.arg1);
				//				switch (msg.arg1) {
				//				case BluetoothChatService.STATE_CONNECTED:
				//					// mTitle.setText(R.string.title_connected_to);
				//					// mTitle.append(mConnectedDeviceName);
				//					// mConversationArrayAdapter.clear();
				//					break;
				//				case BluetoothChatService.STATE_CONNECTING:
				//					// mTitle.setText(R.string.title_connecting);
				//					if (sopFlag)
				//						System.out
				//						.println("UIActivityManager::Handler::handleMessage STATE_CONNECTING");
				//					break;
				//				case BluetoothChatService.STATE_LISTEN:
				//				case BluetoothChatService.STATE_NONE:
				//					// mTitle.setText(R.string.title_not_connected);
				//					break;
				//				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;

				String writeMessage = new String(writeBuf);
				if (sopFlag)
					System.out
					.println("UIActivityManager::Handler::handleMessage :: case: MESSAGE_WRITE -"
							+ writeMessage);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				// mConversationArrayAdapter.add(mConnectedDeviceName+":  " +
				// readMessage);

				String n = readMessage.toString();
				if (sopFlag)
					System.out
					.println("UIActivityManager::Handler::handleMessage :: case  MESSAGE_READ -"
							+ n);
				if (n.indexOf("" + SettingData.sSelf.getUserName() + "-") == -1
						&& n.indexOf("Request") != -1) {
					// if(n.equalsIgnoreCase("Request")){
					// System.out.println("Request==="+n.toString());
					// Qts-Request

					final String userdata = n.substring(0, n.indexOf("-"));
					// System.out.println("userdata==="+userdata+" "+SettingData.sSelf.getUserName());
					if (!userdata.equalsIgnoreCase(SettingData.sSelf
							.getUserName())) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								//								openFriendRequestDialog(userdata);
							}
						});

					}
					// }
				} else if (n
						.indexOf("" + SettingData.sSelf.getUserName() + "-") == -1
						&& n.indexOf("Decline") != -1) {
					try {
						//						if (sopFlag)
						//							System.out.println("UIActivityManager::Handler::handleMessage - User Declined !!!!!!!");
						//						if (BusinessProxy.sSelf.mChatService != null) {
						//							BusinessProxy.sSelf.mChatService.stop();
						//						}
						//						if (BusinessProxy.sSelf.mChatService != null) {
						//							BusinessProxy.sSelf.mChatService = new BluetoothChatService(BusinessProxy.sSelf.addPush.mContext, mHandler);
						//							mOutStringBuffer = new StringBuffer("");
						//							BusinessProxy.sSelf.mChatService.start();
						//						}
						// if(mChatService!=null)
						// mChatService.connectionDisconnectSocket();
						// sendFriendRequest(SettingData.sSelf.getUserName()+"-Decline");
						// if(BusinessProxy.sSelf.mBluetoothAdapter!=null &&
						// BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()){
						// if (BusinessProxy.sSelf.mChatService != null) {
						//
						// // if (mChatService.getState() ==
						// BluetoothChatService.STATE_NONE) {
						// //mChatService = new
						// BluetoothChatService(UIActivityManager.this,
						// mHandler);
						// //System.out.println("==================mchatservice   on");
						// //mOutStringBuffer = new StringBuffer("");
						// BusinessProxy.sSelf.mChatService = new
						// BluetoothChatService(BusinessProxy.sSelf.addPush.mContext,
						// mHandler);
						// mOutStringBuffer = new StringBuffer("");
						// //BusinessProxy.sSelf.mChatService.start();
						// // }
						// }
						// if (BusinessProxy.sSelf.mChatService == null){
						// //setupChat();
						// BusinessProxy.sSelf.mChatService = new
						// BluetoothChatService(BusinessProxy.sSelf.addPush.mContext,
						// mHandler);
						// //System.out.println("===================mchatservice   off");
						// mOutStringBuffer = new StringBuffer("");
						// }
						// }else{
						// //System.out.println("===================bluetooth ");
						// }
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				} else if (n.indexOf("success") != -1) {
					if (sopFlag)
						System.out
						.println("UIActivityManager::Handler::handleMessage - User Accepted !!!!!!!");
					// Write your code here to invoke YES event
					String username = n.substring(7, n.length());
					// System.out.println("username==="+username.toString());
					// Toast.makeText(getApplicationContext(),
					// "You clicked on YES", Toast.LENGTH_SHORT).show();
					StringBuffer text = new StringBuffer("Invite::User##");
					n = BusinessProxy.sSelf.parseUsernameInformation(username);
					text.append(n);
					if (BusinessProxy.sSelf.mBluetoothAdapter != null
							&& BusinessProxy.sSelf.mBluetoothAdapter
							.isEnabled()) {

						// openDialogBluetooth();
						// BusinessProxy.sSelf.mBluetoothAdapter.disable();

					}
					if (BusinessProxy.sSelf.sendNewTextMessage(
							"Friend Manager<a:friendmgr>", text.toString(),
							true)) {
						//						BusinessProxy.sSelf.mChatService.stop();
						showSimpleAlert(
								"Info",
								"Your friend request have been sent. You will receive a confirmation in your system messages shortly.");// String.format("Friend invite has been sent to %s\nYou will get confirmation in your inbox!",
						// n));
					}
					// Add here same code

					if (BusinessProxy.sSelf.mBluetoothAdapter != null
							&& BusinessProxy.sSelf.mBluetoothAdapter
							.isEnabled()) {
						//						if (BusinessProxy.sSelf.mChatService != null) {
						//
						//							// if (mChatService.getState() ==
						//							// BluetoothChatService.STATE_NONE) {
						//							// mChatService = new
						//							// BluetoothChatService(UIActivityManager.this,
						//							// mHandler);
						//							// mOutStringBuffer = new StringBuffer("");
						//							BusinessProxy.sSelf.mChatService = new BluetoothChatService(
						//									BusinessProxy.sSelf.addPush.mContext,
						//									mHandler);
						//							mOutStringBuffer = new StringBuffer("");
						//							BusinessProxy.sSelf.mChatService.start();
						//							// }
						//						}
						//						if (BusinessProxy.sSelf.mChatService == null) { // setupChat();
						//							// System.out.println("99999999");
						//							BusinessProxy.sSelf.mChatService = new BluetoothChatService(
						//									BusinessProxy.sSelf.addPush.mContext,
						//									mHandler);
						//							// System.out.println("===================mchatservice   off");
						//							mOutStringBuffer = new StringBuffer("");
						//						}
					} else {
						// System.out.println("===================bluetooth ");
						// System.out.println("000000");
					}
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);

				// Toast.makeText(getApplicationContext(), "Connected to "
				// + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				if (sopFlag)
					System.out
					.println("UIActivityManager::Handler::handleMessage - case: MESSAGE_DEVICE_NAME - Send friend Request - "
							+ mConnectedDeviceName);
				//				if (BusinessProxy.sSelf.BtFreindRequestFlag)
				//					sendFriendRequest(SettingData.sSelf.getUserName()+ "-Request");
				//				BusinessProxy.sSelf.BtFreindRequestFlag = false;
				// sendFriendRequest(SettingData.sSelf.getUserName());
				// mChatService=null;
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public void bluetoothForOn() {
		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	}

	//	public void openFriendRequestDialog(final String otherUserRequest) {
	//
	//		try {
	//
	//			final Dialog dialog = new Dialog(
	//					BusinessProxy.sSelf.addPush.mContext, R.style.Dialog); // this
	//			// is
	//			// a
	//			// reference
	//			// to
	//			// the
	//			// style
	//			// above
	//			dialog.setContentView(R.layout.bluetoothacceptdecline); // I saved
	//			// the xml
	//			// file
	//			// above as
	//			// yesnomessage.xml
	//			dialog.setCancelable(true);
	//			Window window = dialog.getWindow();
	//			window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	//			window.setBackgroundDrawable(new ColorDrawable(
	//					android.graphics.Color.TRANSPARENT));
	//			// to set the message
	//			TextView message = (TextView) dialog
	//					.findViewById(R.id.userfriendrequestmessage);
	//			message.setText(otherUserRequest
	//					+ " have send you friend request on RockeTalk");
	//
	//			// add some action to the buttons
	//			// String num = "0123456789"
	//			TextView accept = (Button) dialog
	//					.findViewById(R.id.bmessageDialogAccept);
	//			accept.setOnClickListener(new OnClickListener() {
	//
	//				public void onClick(View v) {
	////					sendFriendRequest("success"
	////							+ SettingData.sSelf.getUserName());
	//
	//					// Write your code here to invoke YES event
	//					// String username=n.substring(7,n.length());
	//					// System.out.println("username==="+otherUserRequest.toString());
	//					// Toast.makeText(BusinessProxy.sSelf.addPush.mContext,
	//					// "You clicked on YES", Toast.LENGTH_SHORT).show();
	//					StringBuffer text = new StringBuffer("Invite::User##");
	//					String n = BusinessProxy.sSelf
	//							.parseUsernameInformation(otherUserRequest);
	//					text.append(n);
	//					if (BusinessProxy.sSelf.mBluetoothAdapter != null
	//							&& BusinessProxy.sSelf.mBluetoothAdapter
	//							.isEnabled()) {
	//
	//						// openDialogBluetooth();
	//						// BusinessProxy.sSelf.mBluetoothAdapter.disable();
	//
	//					}
	//					if (BusinessProxy.sSelf.sendNewTextMessage(
	//							"Friend Manager<a:friendmgr>", text.toString(),
	//							true)) {
	//
	//						// TODO Auto-generated method stub
	//						// BusinessProxy.sSelf.mChatService.disconnectSocket();
	////						BusinessProxy.sSelf.mChatService.stop();
	//						showSimpleAlert(
	//								"Info",
	//								"You have accepted the friend request. You will receive a confirmation in your system messages shortly.");// String.format("Friend invite has been sent to %s\nYou will get confirmation in your inbox!",
	//						// n));
	//
	//						if (BusinessProxy.sSelf.mBluetoothAdapter != null
	//								&& BusinessProxy.sSelf.mBluetoothAdapter
	//								.isEnabled()) {
	////							if (BusinessProxy.sSelf.mChatService != null) {
	////
	////								// if (mChatService.getState() ==
	////								// BluetoothChatService.STATE_NONE) {
	////								// mChatService = new
	////								// BluetoothChatService(UIActivityManager.this,
	////								// mHandler);
	////								// mOutStringBuffer = new StringBuffer("");
	////								BusinessProxy.sSelf.mChatService = new BluetoothChatService(
	////										BusinessProxy.sSelf.addPush.mContext,
	////										mHandler);
	////								mOutStringBuffer = new StringBuffer("");
	////								BusinessProxy.sSelf.mChatService.start();
	////								// }
	////							}
	////							if (BusinessProxy.sSelf.mChatService == null) { // setupChat();
	////								// System.out.println("4444444");
	////								BusinessProxy.sSelf.mChatService = new BluetoothChatService(
	////										BusinessProxy.sSelf.addPush.mContext,
	////										mHandler);
	////								// System.out.println("===================mchatservice   off");
	////								mOutStringBuffer = new StringBuffer("");
	////							}
	//						} else {
	//							// System.out.println("===================bluetooth ");
	//							// System.out.println("55555555");
	//						}
	//
	//						// new Thread(new Runnable() {
	//						// public void run() {
	//						// showSimpleAlert("Info",
	//						// "You have accepted the friend request. You will receive a confirmation in your inbox shortly.");//String.format("Friend invite has been sent to %s\nYou will get confirmation in your inbox!",
	//						// n));
	//						// }
	//						// }).start();
	//						// showSimpleAlert("Info",
	//						// "You have accepted the friend request. You will receive a confirmation in your inbox shortly.");//String.format("Friend invite has been sent to %s\nYou will get confirmation in your inbox!",
	//						// n));
	//						// BluetoothChatService.STATE_NONE;
	//
	//						// if(BusinessProxy.sSelf.mBluetoothAdapter!=null&&
	//						// BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()){
	//
	//						// BusinessProxy.sSelf.mBluetoothAdapter.disable();
	//
	//						// }
	//						// if(BusinessProxy.sSelf.bluetoothDeviceName!=null)
	//						// rechangeDeviceName(BusinessProxy.sSelf.bluetoothDeviceName);
	//					}
	//					dialog.dismiss();
	//
	//				}
	//			});
	//
	//			TextView decline = (Button) dialog
	//					.findViewById(R.id.bmessageDialogDecline);
	//			decline.setOnClickListener(new OnClickListener() {
	//
	//				public void onClick(View v) {
	//					// TODO Auto-generated method stub
	//					BusinessProxy.sSelf.BtFreindRequestFlag = false;
	//					// System.out.println("====decline======");
	//					try {
	////						sendFriendRequest(SettingData.sSelf.getUserName()
	////								+ "-Decline");
	////						if (BusinessProxy.sSelf.mChatService != null) {
	////
	////							BusinessProxy.sSelf.mChatService.stop();
	////						}
	//						// if(mChatService!=null)
	//						// mChatService.connectionDisconnectSocket();
	//
	//						if (BusinessProxy.sSelf.mBluetoothAdapter != null
	//								&& BusinessProxy.sSelf.mBluetoothAdapter
	//								.isEnabled()) {
	////							if (BusinessProxy.sSelf.mChatService != null) {
	////
	////								// if (mChatService.getState() ==
	////								// BluetoothChatService.STATE_NONE) {
	////								// mChatService = new
	////								// BluetoothChatService(UIActivityManager.this,
	////								// mHandler);
	////								// mOutStringBuffer = new StringBuffer("");
	////								BusinessProxy.sSelf.mChatService = new BluetoothChatService(
	////										BusinessProxy.sSelf.addPush.mContext,
	////										mHandler);
	////								mOutStringBuffer = new StringBuffer("");
	////								BusinessProxy.sSelf.mChatService.start();
	////								// }
	////							}
	////							if (BusinessProxy.sSelf.mChatService == null) { // setupChat();
	////								// System.out.println("2222222");
	////								BusinessProxy.sSelf.mChatService = new BluetoothChatService(
	////										BusinessProxy.sSelf.addPush.mContext,
	////										mHandler);
	////								// System.out.println("===================mchatservice   off");
	////								mOutStringBuffer = new StringBuffer("");
	////							}
	//						} else {
	//							// System.out.println("===================bluetooth ");
	//							// System.out.println("33333333");
	//						}
	//					} catch (Exception e) {
	//						// TODO: handle exception
	//						e.printStackTrace();
	//					}
	//					dialog.dismiss();
	//				}
	//			});
	//
	//			dialog.show();
	//		} catch (Exception e) {
	//			// TODO: handle exception
	//			// System.out.println("message====="+e.getMessage());
	//		}
	//
	//	}

	//	public void openDialogBluetooth() {
	//
	//		final Dialog dialog = new Dialog(BusinessProxy.sSelf.addPush.mContext,
	//				R.style.Dialog); // this is a reference to the style above
	//		dialog.setContentView(R.layout.bluetooth_on_off); // I saved the xml
	//		// file above as
	//		// yesnomessage.xml
	//		dialog.setCancelable(true);
	//		Window window = dialog.getWindow();
	//		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	//		window.setBackgroundDrawable(new ColorDrawable(
	//				android.graphics.Color.TRANSPARENT));
	//		// to set the message
	//		TextView message = (TextView) dialog
	//				.findViewById(R.id.userfriendrequestmessage);
	//		// message.setText(otherUserRequest+" have send you friend request on RockeTalk");
	//
	//		// add some action to the buttons
	//		// String num = "0123456789"
	//		TextView accept = (Button) dialog.findViewById(R.id.bmessageDialogYes);
	//		accept.setOnClickListener(new OnClickListener() {
	//
	//			public void onClick(View v) {
	//				// if(BusinessProxy.sSelf.bluetoothDeviceName!=null)
	//				// rechangeDeviceName(BusinessProxy.sSelf.bluetoothDeviceName);
	//				if (BusinessProxy.sSelf.mBluetoothAdapter != null
	//						&& BusinessProxy.sSelf.mBluetoothAdapter.isEnabled()) {
	//
	//					BusinessProxy.sSelf.mBluetoothAdapter.disable();
	//
	//				}
	//				dialog.dismiss();
	//
	//			}
	//		});
	//
	//		TextView decline = (Button) dialog.findViewById(R.id.bmessageDialogNo);
	//		decline.setOnClickListener(new OnClickListener() {
	//
	//			public void onClick(View v) {
	//				// TODO Auto-generated method stub
	//				BusinessProxy.sSelf.BtFreindRequestFlag = false;
	//				dialog.dismiss();
	//			}
	//		});
	//
	//		dialog.show();
	//
	//	}

	public void bluetoothInstance() {
		if (BusinessProxy.sSelf.mBluetoothAdapter == null)
			BusinessProxy.sSelf.mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	}

	/*
	 * public void onActivityResult(int requestCode, int resultCode, Intent
	 * data) { // if(D) Log.d(TAG, "onActivityResult " + resultCode); switch
	 * (requestCode) { case REQUEST_CONNECT_DEVICE:
	 * 
	 * if (resultCode == Activity.RESULT_OK) {
	 * 
	 * String address = data.getExtras()
	 * .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	 * 
	 * BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
	 * 
	 * mChatService.connect(device); } break; case REQUEST_ENABLE_BT:
	 * 
	 * if (resultCode == Activity.RESULT_OK) { mChatService = new
	 * BluetoothChatService(this, mHandler);
	 * 
	 * 
	 * mOutStringBuffer = new StringBuffer(""); //setupChat(); } else {
	 * 
	 * Log.d(TAG, "BT not enabled"); Toast.makeText(this,
	 * R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show(); // finish();
	 * } } }
	 */
	public void playFromURLMain(String aURL) {
		try {
			// showToast(aURL) ;
			boolean TESTING = false;
			if (TESTING) {
				Intent intent = new Intent(this, Player.class);
				intent.putExtra("url", aURL);
				this.startActivity(intent);
			} else {

				if (!aURL.startsWith("http")) {
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(aURL);
					String mimeType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(extension);
					File file = new File(aURL);
					// Uri uri = Uri.parse(aURL);
					// videoPlayer(uri,true);
					Intent player = new Intent(Intent.ACTION_VIEW,
							Uri.fromFile(file));
					// Toast.makeText(this,
					// "extension: " + extension + " mimeType: " + mimeType,
					// Toast.LENGTH_LONG).show();
					// player.setDataAndType(uri, "video/3gpp");
					if (mimeType != null && mimeType.equalsIgnoreCase("3gp")) {
						player.setDataAndType(Uri.fromFile(file), "video/3gpp");
					} else {
						player.setDataAndType(Uri.fromFile(file), mimeType);
					}
					startActivity(player);
				} else {
					String extension = MimeTypeMap
							.getFileExtensionFromUrl(aURL);
					String mimeType = MimeTypeMap.getSingleton()
							.getMimeTypeFromExtension(extension);
					// File file = new File(aURL);
					Uri uri = Uri.parse(aURL);
					// videoPlayer(uri,true);
					Intent player = new Intent(Intent.ACTION_VIEW, uri);
					// Toast.makeText(this,
					// "extension: " + extension + " mimeType: " + mimeType,
					// Toast.LENGTH_LONG).show();
					// player.setDataAndType(uri, "video/3gpp");
					if (mimeType != null && mimeType.equalsIgnoreCase("3gp")) {
						player.setDataAndType(uri, "video/3gpp");
					} else {
						player.setDataAndType(uri, mimeType);
					}
					startActivity(player);
				}
			}
		} catch (Exception ex) {
			// System.out.println("ERROR IN PLAYING STREAMING - " +
			// ex.toString());
			ex.printStackTrace();
		}
	}

	// ////////

	public String addNewConversationGloble(String destinations, boolean addMe) {
		boolean meinthis = false;
		if (destinations == null || destinations.trim().length() <= 0)
			return null;
		// System.out.println("----------met destinations 2: "+destinations);

		if (destinations.toLowerCase().indexOf(
				"<" + SettingData.sSelf.getUserName().toLowerCase() + ">") != -1) {
			addMe = false;
			meinthis = true;
		}
		Vector<ConversationList> conversationListVec = new Vector<ConversationList>();
		ConversationList conversationList2 = new ConversationList();
		String dist[] = null;
		if (destinations != null && destinations.trim().length() > 0) {
			String destinationsUserNameArr[] = null;

			dist = Utilities.split(new StringBuffer(destinations), ";");
			if (dist.length > 1) {
				destinations = "";

				for (int i = 0; i < dist.length; i++) {
					String userName = BusinessProxy.sSelf
							.parseUsernameInformation(dist[i]);
					if (userName.toLowerCase().equalsIgnoreCase(
							SettingData.sSelf.getUserName().toLowerCase())) {
						addMe = true;
						continue;
					}
					destinations = destinations + dist[i] + ";";
				}
				if (destinations.lastIndexOf(";") != -1) {
					destinations = destinations.substring(0,
							destinations.length() - 1);
				}
			}
			// System.out.println("----------met destinations after 2: "+destinations);
			dist = Utilities.split(new StringBuffer(destinations), ";");
		}
		String s = "";

		// System.out.println("----------met destinations 2 dist.length: "+dist.length);
		// System.out.println("----------met destinations 2 like: "+BusinessProxy.sSelf.parseUsernameInformation(destinations));

		if (dist.length == 1) {
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

			// String where = "MessageConversationTable.USER_ID+"
			// +BusinessProxy.sSelf.parseUsernameInformation(destinations)+" and "
			// +MessageConversationTable.IS_GROUP+" =0 and "+
			// MessageConversationTable.PARTICIPANT_INFO+" like '%"+BusinessProxy.sSelf.parseUsernameInformation(destinations)+"%'"
			// ;
			// Cursor cursor=
			// BusinessProxy.sSelf.sqlDB.query(MessageConversationTable.TABLE,
			// null, null,
			// null, null, null, null);

			String usernamesStr = "";

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
					// if(isGroup!=null && isGroup.equalsIgnoreCase("1")
					// &&pi!=null && pi.length>2)
					// if((isGroup!=null && isGroup.equalsIgnoreCase("1"))
					// ||(pi!=null && pi.length>2))
					// continue ;
					//
					// if(meinthis && (pi!=null && pi.length>1))
					// continue ;

					if ((isGroup != null && isGroup.equalsIgnoreCase("1"))
							|| (pi != null && pi.length > 2))
						continue;

					if (meinthis && (pi != null && pi.length > 1))
						continue;

					// 06-05 16:51:10.625: I/System.out(15637): ----------met
					// pass true�qts�qts�s�
					// 06-05 16:51:10.630: I/System.out(15637): ----------met
					// pass true�qts�qts�s�
					// 06-05 16:51:10.630: I/System.out(15637): ----------met
					// pass true�guriya�Gur�iya�false�qts�Qts�S�
					// 06-05 16:51:10.630: I/System.out(15637): ----------met
					// pass
					// true�qts�Qts�S�false�tulikasharma�Tulika�Sharma�
					// 06-05 16:51:10.630: I/System.out(15637): ----------met
					// pass true�qts�Qts�S�false�rraj�Raj�Raj�
					// 06-05 16:51:10.630: I/System.out(15637): ----------met
					// pass false�momdad�Mom�Dad�true�qts�Qts�S�

					// System.out.println("----------met pass "+s+" len: "+pi.length+" meinthis: "+meinthis);

					conversationList2 = new ConversationList();

					conversationList2.conversationId = cursor
							.getString(cursor
									.getColumnIndex(MessageConversationTable.CONVERSATION_ID));
					if (s != null) {
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
								// System.out.println("----------met new destinations: addding info pi[i] "+pi[i]);
								// System.out.println("----------met new destinations: addding info info len "+info.length);

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
								// System.out.println("----------met new destinations: addding info "+participantInfo.toString());
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
		conversationList.parent_id = "" + sysTime;
		conversationList.unreadMessageCount = -1;
		conversationList.contentBitMap = "0";
		conversationList.conversationId = "NP" + sysTime;
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
							// System.out.println("----------met new destinations: samePart j "+j);
							ParticipantInfo info = conversationList2.participantInfo
									.get(j);
							// System.out.println("----------met new destinations: samePart info.userName "+info.userName);
							// System.out.println("----------met new destinations: samePart userName "+userName);
							if (info.userName != null
									&& info.userName
									.toLowerCase()
									.toLowerCase()
									.equalsIgnoreCase(
											userName.toLowerCase())) {
								samePart++;
							}
							// System.out.println("----------met new destinations: samePart "+samePart);
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
				String user = BusinessProxy.sSelf.parseNameInformation(dist[i]);
				s += "" + "false" + Constants.COL_SEPRETOR + userName
						+ Constants.COL_SEPRETOR + user
						+ Constants.COL_SEPRETOR + " " + Constants.COL_SEPRETOR
						+ userName + Constants.COL_SEPRETOR + userName
						+ Constants.ROW_SEPRETOR;
			}
		}
		// System.out.println("----------met new destinations:  destinations "+destinations);
		// if( Utilities.split(new StringBuffer(destinations),
		// Constants.ROW_SEPRETOR).length>3)
		// conversationList.isGroup = 1;

		if (Utilities.split(new StringBuffer(destinations), ";").length < 2)
			conversationList.isGroup = 0;
		else
			conversationList.isGroup = 1;

		// System.out.println("----------met new destinations: isgroup "+conversationList.isGroup);

		// System.out.println("----------met new destinations: "+s);
		// System.out.println("-------------- new participant  : "+s);
		conversationList.participantInfoStr = s;
		// System.out.println("----------met new conversationId: "+conversationList.conversationId);
		// System.out.println("----------met new getId: "+getId);
		if (!getId && conversationList.conversationId.startsWith("NP"))
			saveConversitionList(conversationList);

		// if (s != null) {
		// String pi[] = Utilities.split(new StringBuffer(s),
		// Constants.ROW_SEPRETOR);
		// ParticipantInfo participantInfo = new ParticipantInfo();
		// conversationList.participantInfo = new Vector<ParticipantInfo>();
		// try {
		// for (int i = 0; i < pi.length; i++) {
		// participantInfo = new ParticipantInfo();
		// String info[] = Utilities.split(new StringBuffer(pi[i]),
		// Constants.COL_SEPRETOR);
		//
		// participantInfo.isSender = Boolean.parseBoolean(info[0]);
		// participantInfo.userName = info[1];
		// participantInfo.firstName = info[2];
		// participantInfo.lastName = info[3];
		// participantInfo.profileImageUrl = info[4];
		// participantInfo.profileUrl = info[5];
		//
		// conversationList.participantInfo.add(participantInfo);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		return conversationList.conversationId;
	}

	public void saveConversitionList(ConversationList conversationList) {

		// System.out.println("----------met new saveConversitionList: "
		// + conversationList.conversationId);

		ContentValues values = new ContentValues();
		values.put(MessageConversationTable.USER_ID,
				BusinessProxy.sSelf.getUserId());// int
		values.put(MessageConversationTable.PARENT_ID,
				conversationList.parent_id);
		values.put(MessageConversationTable.CLIENTTRANSACTION_ID,
				conversationList.clientTransactionId);
		values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT,
				conversationList.unreadMessageCount);
		values.put(MessageConversationTable.CONTENT_BITMAP,
				conversationList.contentBitMap);
		values.put(MessageConversationTable.CONVERSATION_ID,
				conversationList.conversationId);
		values.put(MessageConversationTable.DATE_TIME,
				conversationList.datetime);
		values.put(MessageConversationTable.DRM_FLAGS,
				conversationList.drmFlags);// int
		values.put(MessageConversationTable.HAS_BEEN_VIEWED,
				conversationList.hasBeenViewed);
		values.put(MessageConversationTable.HAS_BEEN_VIEWED_BY_SIP,
				conversationList.hasBeenViewedBySIP);
		values.put(MessageConversationTable.MESSAGE_ID,
				conversationList.messageId);
		values.put(MessageConversationTable.USMId, conversationList.usmId);
		values.put(MessageConversationTable.MFU_ID, conversationList.mfuId);
		values.put(MessageConversationTable.MSG_OP, conversationList.msgOp);// int
		values.put(MessageConversationTable.MSG_TXT, conversationList.msgTxt);
		values.put(MessageConversationTable.NOTIFICATION_FLAGS,
				conversationList.notificationFlags);
		values.put(MessageConversationTable.REFMESSAGE_ID,
				conversationList.refMessageId);
		values.put(MessageConversationTable.SENDERUSER_ID,
				conversationList.senderUserId);
		values.put(MessageConversationTable.SOURCE, conversationList.source);
		values.put(MessageConversationTable.TARGETUSER_ID,
				conversationList.targetUserId);
		values.put(MessageConversationTable.USER_FIRSTNAME,
				conversationList.user_firstname);
		values.put(MessageConversationTable.USER_LASTNAME,
				conversationList.user_lastname);
		values.put(MessageConversationTable.USER_NAME,
				conversationList.user_name);
		values.put(MessageConversationTable.USER_URI, conversationList.user_uri);
		values.put(MessageConversationTable.INSERT_TIME,
				System.currentTimeMillis());

		values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, 0);

		values.put(MessageConversationTable.IS_GROUP, conversationList.isGroup);
		values.put(MessageConversationTable.SUBJECT, conversationList.subject);
		values.put(MessageConversationTable.GROUP_PIC, conversationList.imageFileId);

		values.put(MessageTable.PARTICIPANT_INFO,
				conversationList.participantInfoStr);

		Uri u = getContentResolver().insert(
				MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
				values);
		//		System.out.println("UIActivityManager:: u - "+u.toString());
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
				// media_play_layout.setVisibility(View.GONE);
				RTMediaPlayer.reset();
				RTMediaPlayer.clear();
				if (media_play_layout != null && media_play_layout.isShowing()) {
					media_play_layout.dismiss();
					// return;
					media_play_layout = null;
				}
				break;
			}
		}
	};

	// showStstemMessage
	//	public void showStstemMessage() {
	//
	//		FeedTask.showStstemMessage = false;
	//		// if(DataModel.sSelf.getBoolean(DMKeys.FROM_REGISTRATION))
	//		// return ;
	//		runOnUiThread(new Runnable() {
	//
	//			@Override
	//			public void run() {
	//				try {
	//					// showToast("System message ");
	//					InboxMessage mMessage = new InboxMessage();
	//					mMessage.mPayload = new Payload();
	//					mMessage.mPayload.mRTML = new byte[1][1];
	//					mMessage.mPayload.mRTML[0] = FeedTask.message.msgTxt
	//							.getBytes();
	//					// -------------------------------------------
	//					// Get All Thumb URL's
	//					String thumbURLs[] = null;
	//
	//					if (FeedTask.message.image_content_thumb_urls != null) {
	//						thumbURLs = Utilities.split(new StringBuffer(
	//								FeedTask.message.image_content_thumb_urls),
	//								Constants.COL_SEPRETOR);
	//					}
	//					// Get All Images URL's
	//					String imageURLs[] = null;
	//					if (FeedTask.message.image_content_urls != null)
	//						imageURLs = Utilities.split(new StringBuffer(
	//								FeedTask.message.image_content_urls),
	//								Constants.COL_SEPRETOR);
	//					mMessage.mSenderName = FeedTask.message.source;// "Qts" ;
	//					if (imageURLs != null)
	//						mMessage.mPayload.mPicture = new byte[thumbURLs.length][];
	//					if (imageURLs != null)
	//						for (int i = 0; i < imageURLs.length; i++)
	//							mMessage.mPayload.mPicture[i] = thumbURLs[i]
	//									.getBytes();
	//					if (imageURLs != null && imageURLs.length > 0)
	//						mMessage.mPayload.mSlideShowURLs = new String[imageURLs.length];
	//					if (imageURLs != null)
	//						for (int i = 0; i < imageURLs.length; i++)
	//							mMessage.mPayload.mSlideShowURLs[i] = imageURLs[i];
	//
	//					// Add Voice url
	//					if (FeedTask.message.voice_content_urls != null) {
	//						String voiceURLs1[] = Utilities.split(new StringBuffer(
	//								FeedTask.message.voice_content_urls),
	//								Constants.COL_SEPRETOR);
	//						mMessage.mPayload.mVoice = new byte[voiceURLs1.length][];
	//						for (int i = 0; i < voiceURLs1.length; i++)
	//							mMessage.mPayload.mVoice[i] = voiceURLs1[i]
	//									.getBytes();
	//					}
	//
	//					// -------------------------------------------
	//					// System.out.println("--------------message.msgTxt ------ :"
	//					// + FeedTask.message.msgTxt);
	//					mMessage.mSenderName = FeedTask.message.source;// "Qts";
	//					DataModel.sSelf.storeObject(
	//							CustomViewDemo.RTML_MESSAGE_DATA,
	//							(InboxMessage) mMessage);
	//					Intent intent = new Intent(UIActivityManager.this,
	//							CustomViewDemo.class);
	//					intent.putExtra("PAGE", (byte) 1);
	//					// intent.putExtra("force_message", forceMessage);
	//					startActivityForResult(intent, 0);
	//				} catch (Exception e) {
	//					// TODO: handle exception
	//				}
	//			}
	//		});
	//	}

	Dialog media_play_layout;

	public void showBuzz() {
		try {
			if (Utilities.isSilentMode(this)) {
				FeedTask.showBuzz = false;
				return;
			}
			FeedTask.showBuzz = false;
			// FeedTask.message = new
			// com.kainat.app.android.bean.Message() ;
			// FeedTask.message.voice_content_urls =
			// "http://robtowns.com/music/blind_willie.mp3";
			if (media_play_layout != null && media_play_layout.isShowing()) {
				// media_play_layout.dismiss() ;
				return;
			}

			String s = FeedTask.message.participantInfoStr;// cursor.getString(cursor.getColumnIndex(MessageTable.PARTICIPANT_INFO));
			if (s != null) {
				String pi[] = Utilities.split(new StringBuffer(s),
						Constants.ROW_SEPRETOR);

				// System.out.println("------------message.video_size :"+message.video_size);
				ParticipantInfo participantInfo = new ParticipantInfo();
				FeedTask.message.participantInfo = new Vector<ParticipantInfo>();
				// sender username firstname lastname profileImageUrl profileUrl
				int i = 0;
				try {

					for (i = 0; i < pi.length; i++) {
						participantInfo = new ParticipantInfo();
						String info[] = Utilities
								.split(new StringBuffer(pi[i]),
										Constants.COL_SEPRETOR);
						participantInfo.isSender = Boolean
								.parseBoolean(info[0]);
						participantInfo.userName = info[1];
						if (info.length >= 3)
							participantInfo.firstName = info[2];
						if (info.length >= 4)
							participantInfo.lastName = info[3];
						// participantInfo.profileImageUrl=info[4];
						// participantInfo.profileUrl=info[5];
						FeedTask.message.participantInfo.add(participantInfo);
					}
				} catch (Exception e) {
					e.printStackTrace();
					FeedTask.message.participantInfo.add(participantInfo);
				}
			}

			ParticipantInfo senderParticipantInfo = null;
			boolean isSender = false;
			if (FeedTask.message.participantInfo != null
					&& FeedTask.message.participantInfo.size() > 0) {
				// System.out.println("---------participantInfoStr view: "+messageViewHolder.message.participantInfo);
				// System.out.println("-------------user name : "+SettingData.sSelf
				// .getUserName());
				for (int i = 0; i < FeedTask.message.participantInfo.size(); i++) {
					ParticipantInfo participantInfo = FeedTask.message.participantInfo
							.get(i);

					if (participantInfo.isSender)
						senderParticipantInfo = participantInfo;

				}
			}
			if (Downloader.getInstance().check(
					FeedTask.message.voice_content_urls)
					|| !FeedTask.message.voice_content_urls.startsWith("http")) {
				String tu = Downloader.getInstance().getPlayUrl(
						FeedTask.message.voice_content_urls);
				if (tu != null)
					FeedTask.message.voice_content_urls = tu;
			}

			// ActivityManager am = (ActivityManager)
			// this.getSystemService(Activity.ACTIVITY_SERVICE);
			// String packageName =
			// am.getRunningTasks(1).get(0).topActivity.getPackageName();
			// String className =
			// am.getRunningTasks(1).get(0).topActivity.getClassName();

			//			media_play_layout = new Dialog(
			//					BusinessProxy.sSelf.addPush.mContext,
			//					android.R.style.Theme_Translucent_NoTitleBar);
			media_play_layout.getWindow().requestFeature(
					Window.FEATURE_NO_TITLE);
			media_play_layout.setContentView(R.layout.buzz_play_screen);
			media_play_layout.setCancelable(false);
			// mDialog.setOnKeyListener(this);
			media_play_layout.findViewById(R.id.media_play_layout)
			.setVisibility(View.VISIBLE);
			if (senderParticipantInfo != null)
				if ((senderParticipantInfo.firstName != null && senderParticipantInfo.firstName
				.length() > 0)
				&& (senderParticipantInfo.lastName != null && senderParticipantInfo.lastName
				.length() > 0))
					((TextView) media_play_layout.findViewById(R.id.name))
					.setText(senderParticipantInfo.firstName + " "
							+ senderParticipantInfo.lastName);
				else
					((TextView) media_play_layout.findViewById(R.id.name))
					.setText(senderParticipantInfo.userName);

			Utilities.vibrate(this);

			// ((TextView)
			// media_play_layout.findViewById(R.id.name)).setText(FeedTask.message.source);
			TextView tv = (TextView) media_play_layout
					.findViewById(R.id.streemStatus);
			tv.setText("Please wait while getting audio...");
			RTMediaPlayer.setUrl(null);
			tv.setTextColor(0xff6AABB4);
			baradd = (SeekBar) media_play_layout
					.findViewById(R.id.mediavoicePlayingDialog_progressbar);
			TextView imgStop = (TextView) media_play_layout
					.findViewById(R.id.media_stop_play);
			imgStop.setOnClickListener(playerClickEvent);

			if (true) {
				ImageView imageView1 = (ImageView) media_play_layout
						.findViewById(R.id.media_play);
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
					// if(availableVoice)return ;
					try {
						media_play_layout.findViewById(R.id.streemStatus)
						.setVisibility(View.GONE);
						LinearLayout d = ((LinearLayout) media_play_layout
								.findViewById(R.id.media_play_layout));
						d.setVisibility(View.VISIBLE);
						ImageView imageView1 = (ImageView) media_play_layout
								.findViewById(R.id.media_close);// media_play
						// imageView1.setImageResource(R.drawable.pause);
						if (imageView1 != null) {
							imageView1.setVisibility(View.INVISIBLE);
							imageView1.setOnClickListener(playerClickEvent);
							imageView1.setTag("PAUSE");
						}
						imageView1 = (ImageView) media_play_layout
								.findViewById(R.id.media_play);

						// imageView1.setTag(clickId+"^"+url);
						if (imageView1 != null) {
							imageView1.setOnClickListener(playerClickEvent);
							imageView1.setTag("PLAY");
							imageView1
							.setBackgroundResource(R.drawable.addpause);
							imageView1.setTag("PAUSE");
							imageView1.setVisibility(View.VISIBLE);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				public void voicePlayCompleted() {
					// availableVoice = true;
					UIActivityManager.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// closePlayScreen();
							// System.out.println("-----voicePlayCompleted-----");
							try {
								ImageView imageView1 = (ImageView) media_play_layout
										.findViewById(R.id.media_play);
								imageView1
								.setBackgroundResource(R.drawable.addplay);
								imageView1.setTag("PLAY");
								// RTMediaPlayer.reset();
								// media_play_layout.setVisibility(View.GONE);
								// RTMediaPlayer.clear();
								// if(media_play_layout != null &&
								// media_play_layout.isShowing()){
								// media_play_layout.dismiss() ;
								// media_play_layout = null ;
								// // return;
								// }
								resetProgress();

							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					});
				}

				@Override
				public void onError(int i) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDureationchanged(long total, long current) {
					// TODO Auto-generated method stub

				}
			});
			RTMediaPlayer._startPlay(FeedTask.message.voice_content_urls);
			media_play_layout.show();
		} catch (WindowManager.BadTokenException e) {
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	SeekBar baradd;

	public void resetProgress() {
		// ProgressBar progressBar = (ProgressBar)
		// findViewById(R.id.progressbar);
		baradd.setVisibility(View.VISIBLE);
		baradd.setProgress(0);
		baradd.invalidate();
	}

	public void startConversation(String destinations) {
		DataModel.sSelf.storeObject(DMKeys.COMPOSE_DESTINATION_CONTACTS,
				destinations);
		// DataModel.sSelf.storeObject(DMKeys.COMPOSE_MESSAGE_OP,
		// Constants.MSG_OP_FWD);
		if (destinations != null && destinations.trim().length() > 0) {
			Intent intent = new Intent(this, ConversationsActivity.class);
			intent.putExtra(Constants.CONVERSATION_ID, "-1");
			intent.putExtra("START_CHAT_FROM_EXTERNAL", (byte) 1);
			intent.putExtra("PAGE", (byte) 1);
			startActivity(intent);
		}
		finish();
	}

	public static File cacheDirImage;

	public File getCacheDirectoryImage() {

		if (cacheDirImage != null)
			return cacheDirImage;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDirImage = new File(sdDir, Constants.imageTempPath);
		}

		//		 if (!cacheDirImage.exists())
		//		 cacheDirImage.mkdirs();
		//		 return cacheDirImage;
		else
			cacheDirImage = getCacheDir();

		if (!cacheDirImage.exists())
			cacheDirImage.mkdirs();
		return cacheDirImage;
	}

	public File getCacheDirectoryImagePrivate() {
		return getCacheDirectoryImage();
		//		if(1==1)return ;
		// if (cacheDirImage != null)
		// return cacheDirImage;
		// String sdState = android.os.Environment.getExternalStorageState();
		// if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
		// File sdDir = android.os.Environment.getExternalStorageDirectory();
		// cacheDirImage = new File(sdDir, Constants.imageTempPath);
		// }

		//		cacheDirImage = getCacheDir();
		//
		//		if (!cacheDirImage.exists())
		//			cacheDirImage.mkdirs();
		//		return cacheDirImage;
	}

	public void initVerificationScreen() {
		//		C2DMBroadcastReceiver.pushScreen = -1;
		//		finish();
		//		Intent intent = new Intent(this, LoginActivity.class);
		//		intent.putExtra("verifyscreen", true);
		//		intent.putExtra("password", SettingData.sSelf.getPassword());
		//		intent.putExtra("email", SettingData.sSelf.getEmail());
		//		intent.putExtra("username", SettingData.sSelf.getUserName());
		//		startActivityForResult(intent, 10);
	}

	public void reloadeViewAfterChangeLanguag(){
		try{
			if(KeyValue.getBoolean(this, KeyValue.LANGUAGE_CHANGE)==true){
				KeyValue.setBoolean(this, KeyValue.LANGUAGE_CHANGE,false);
				finish();
				Intent intent = new Intent(this,
						getClass());
				startActivity(intent);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	CImageView imgAvatarLeftMenu;
	@SuppressLint("NewApi")
	public void initLeftMenu() {
		try{
			ImageDownloader imageManager = new ImageDownloader();
			try {
				mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
				mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
				// load slide menu items
				navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

				LayoutInflater	 mInflater = (LayoutInflater)
						getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				View convertView = mInflater.inflate(R.layout.left_menu_profile, null);

				((TextView)convertView.findViewById(R.id.name)).setText(SettingData.sSelf.getDisplayName()+"");

				((TextView)convertView.findViewById(R.id.location)).setText(SettingData.sSelf.getCountry()+"");
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent	intent = new Intent(UIActivityManager.this,
								ProfileViewActivity.class);
						intent.putExtra("USERID", SettingData.sSelf.getUserName().toLowerCase()) ;
						startActivity(intent);
						mDrawerLayout.closeDrawers();
					}
				});

				imgAvatarLeftMenu = (CImageView)convertView.findViewById(R.id.thumb) ;
				imgAvatarLeftMenu.setGender(SettingData.sSelf.getGender() + 1);
				ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

					@Override
					public void onThumbnailResponse(
							ThumbnailImage value, byte[] data) {
						try {
							BitmapDrawable background = new BitmapDrawable(value.mBitmap);
							imgAvatarLeftMenu.setImageDrawable(background);
							imgAvatarLeftMenu.invalidate();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				if(myProfilePicURL != null)
				{
					imageLoader.displayImage(myProfilePicURL, imgAvatarLeftMenu, option_mailicon);
			}
			else{
				imageLoader.displayImage(SettingData.sSelf.getUserName(), imgAvatarLeftMenu, option_mailicon);
			}
			/*	if(myProfilePicURL != null)
					imageManager.download(myProfilePicURL, imgAvatarLeftMenu, handler,ImageDownloader.TYPE_THUMB_BUDDY);
				else
					imageManager.download(SettingData.sSelf.getUserName(), imgAvatarLeftMenu, handler,ImageDownloader.TYPE_THUMB_BUDDY);*/

				mDrawerList.addHeaderView(convertView);

				// nav drawer icons from resources
				navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

				navDrawerItems = new ArrayList<NavDrawerItem>();

				// adding nav drawer items to array

				// Home
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
						.getResourceId(0, -1)));
				// Notification
				if (FeedRequester.systemMessage > 0) {
					navDrawerItems.add(1,new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),true,""+FeedRequester.systemMessage));
					
				}else
				{
					navDrawerItems.add(1,new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
				}
				
				/*navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
						.getResourceId(1, -1)));*/
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
						.getResourceId(6, -1)))*/;
				// Invite
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons
						.getResourceId(7, -1)));

				//			navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				//					.getResourceId(4, -1)));
				//			// What's hot, We will add a counter here
				//			navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				//					.getResourceId(5, -1), true, "50+"));

				// Recycle the typed array
				navMenuIcons.recycle();

				mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

				// setting the nav drawer list adapter
				adapter = new NavDrawerListAdapter(getApplicationContext(),
						navDrawerItems);
				mDrawerList.setAdapter(adapter);
				mDrawerList.invalidateViews();
				// enabling action bar app icon and behaving it as toggle button
				//		 getActionBar().setDisplayHomeAsUpEnabled(true);
				//		 getActionBar().setHomeButtonEnabled(true);
				//		 getActionBar().setIcon(R.drawable.action_menu) ;
			} catch (Exception e) {
				e.printStackTrace();
			}

			//		 @drawable/
			// navDrawerItems = new ArrayList<NavDrawerItem>();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mDrawerLayout.openDrawer(mDrawerList) ;
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
			case 1://home
				//				Intent intent = new Intent(UIActivityManager.this,
				//						KainatContactActivity.class);
				//				startActivity(intent);
				//				mDrawerLayout.closeDrawers();

				Intent intent = new Intent(UIActivityManager.this, KainatHomeActivity.class);
				if(SettingData.sSelf.getUserName() != null)
					intent.putExtra("USERID", SettingData.sSelf.getUserName().toLowerCase()) ;
				startActivity(intent);	
				if(mDrawerLayout != null)
					mDrawerLayout.closeDrawers();

				break;
			case 2://notification


				intent = new Intent(UIActivityManager.this,
						OtherMessageActivity.class);
				intent.putExtra("What", 1);
				intent.putExtra(Constants.ACTIVITY_FOR_RESULT, true);
				//startActivityForResult(intent, "OtherMessageActivity".hashCode());
				startActivity(intent);
				mDrawerLayout.closeDrawers();

				break;
			case 3://chat

				Intent intenta = new Intent(UIActivityManager.this,KainatInboxAvtivity.class);
				intenta.putExtra("TYPE", 2);
				startActivity(intenta);
				mDrawerLayout.closeDrawers();

				break;
			case 4://contact


				Intent intentc = new Intent(UIActivityManager.this,
						KainatContactActivity.class);
				intentc.putExtra("HEADER_TITLE", getString(R.string.friend));
				startActivity(intentc);
				mDrawerLayout.closeDrawers();

				break;
			case 5://search

				Intent intentaa = new Intent(UIActivityManager.this,SearchCommunityActivity.class);
				intentaa.putExtra("TYPE", 2);
				startActivity(intentaa);
				mDrawerLayout.closeDrawers();

				break;
			case 6://Invite
				intent = new Intent(UIActivityManager.this,KainatInviteSelection.class);
				startActivity(intent);
				mDrawerLayout.closeDrawers();
				break;

			case 7://Discover

			/*	intent = new Intent(UIActivityManager.this,
						KainatDiscoverActivity.class);*/
				intent = new Intent(UIActivityManager.this,KainatSettings.class);
				startActivity(intent);
				mDrawerLayout.closeDrawers();
				break;
			case 8: // Setting

				// User.getInstance().clean();
				// SettingData.sSelf.deleteSettings();
				//	intent = new Intent(KainatHomeActivity.this,ContactActivity.class);
				intent = new Intent(UIActivityManager.this,KainatSettings.class);
				startActivity(intent);
				//	Toast.makeText(KainatHomeActivity.this, getString(R.string.comeing_soon), 3000).show();
				mDrawerLayout.closeDrawers();
				break;
			}
		}

	}

	public void joinLeaveCommunity(View v){
		KeyValue.setBoolean(this, KeyValue.CREATE_COMMUNITY,true);
		CommunityFeed.Entry  entry = (CommunityFeed.Entry)v.getTag() ;
		//msgto:Community Manager&lt;a:communitymgr&gt;?text=Join::Name##Test Kainat
		//		AppUtil.showTost(UIActivityManager.this, "joinLeaveCommunity");



		StringBuilder buffer = new StringBuilder(
				"msgto:Community Manager<a:communitymgr>?text="+entry.joinOrLeave+"::Name##"+entry.groupName);
		clickHandler(buffer.toString(),false);

		if(entry.joinOrLeave.equalsIgnoreCase("join"))

			entry.joinOrLeave = "leave";

		else

			entry.joinOrLeave = "join";

		refreshChannelList = true;
	}
	public int clickHandler(String aString,boolean showAlert) {
		String destination = "msgto:";
		String rtmlRow = aString;
		String command;
		int index;
		index = rtmlRow.indexOf(destination);
		if (index != Constants.ERROR_NOT_FOUND) {
			rtmlRow = rtmlRow
					.substring(index + destination.toString().length());
			index = rtmlRow.indexOf('?');
			if (index == Constants.ERROR_NOT_FOUND)
				return Constants.ERROR_NOT_FOUND;
			destination = rtmlRow.substring(0, index);
			rtmlRow = rtmlRow.substring(index + 1);
			command = "text=";
			index = rtmlRow.indexOf(command);
			if (index == Constants.ERROR_NOT_FOUND)
				return Constants.ERROR_NOT_FOUND;
			String alert = ",alert=";
			int alertIndex = rtmlRow.indexOf(alert);
			if (alertIndex == Constants.ERROR_NOT_FOUND) {
				command = rtmlRow.substring(index + command.length());
				rtmlRow = rtmlRow.substring(index + command.length());
			} else {
				rtmlRow = rtmlRow.substring(index + command.length());
				alertIndex = rtmlRow.indexOf(alert);
				command = rtmlRow.substring(0, alertIndex);
				rtmlRow = rtmlRow.substring(alertIndex + alert.length());
			}
			if(showAlert)
				sendRTMLRequest(destination, command, getString(R.string.yourrequesthasbeensent));
			else
				sendRTMLRequest(destination, command,null);

		}

		return Constants.ERROR_NONE;
	}
	public static boolean registerPusKey;
	public void sendPushKeyToServer() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {	                
				try {	    
					if(Utilities.sRegId==null || Utilities.sRegId.trim().length() <= 10){
						return null;
					}
					else{
						String url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/push/updatetoken?token="+Utilities.sRegId+"&deviceType=android";
						if(Utilities.getPhoneIMEINumber() != null && Utilities.getPhoneIMEINumber().length() > 0)
							url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/push/updatetoken?token="+Utilities.sRegId+"&deviceType=android&imei="+Utilities.getPhoneIMEINumber();
						Log.i(TAG, "sendPushKeyToServer : doInBackground :: URL ==> " + url); 
						Log.i(TAG, "sendPushKeyToServer : doInBackground :: PUSH KEY ==> " + Utilities.sRegId); 
						HttpConnectionHelper 	helper = new HttpConnectionHelper(url);
						helper.setHeader(
								"RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(
										BusinessProxy.sSelf.getUserId(),
										SettingData.sSelf.getPassword()));

						mCallBackTimer = new Timer();
						int responseCode = helper.getResponseCode();
						String response = "" ;
						switch (responseCode) {
						case HttpURLConnection.HTTP_OK:
							String contentEncoding = helper.getHttpHeader("Content-Encoding");
							InputStream inputStream = null;
							if (null == contentEncoding) {
								inputStream = helper.getInputStream();
							} else if (contentEncoding.equals("gzip")) {
								inputStream = new GZIPInputStream(helper.getInputStream());
							}
							ByteArrayBuffer buffer = new ByteArrayBuffer(Constants.CHUNK_LENGTH);
							byte[] chunk = new byte[Constants.CHUNK_LENGTH];
							int len;
							while ( -1 != (len = inputStream.read(chunk))) {
								buffer.append(chunk, 0, len);
							}
							response = new String( buffer.toByteArray());
							break ;
						}

						//	                	String response = 
						//	    						AdConnectionManager.getInstance()
						//	    						.uploadByteDataBody("http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/push/updatetoken?token=Utilities.sRegId", Utilities.sRegId, null, null);
						//						Log.i(TAG, "registerInBackground : doInBackground ::#### PUSH  REGISTER ####" + response);
						//	                    registerPusKey = true ;
						return response;
					}
				} catch (Exception ex) {
					ex.printStackTrace();               
					Log.i(TAG, "sendPushKeyToServer : doInBackground ::#### PUSH  REGISTER ####" + ex.getLocalizedMessage());
				}

				return null;
			}

			@Override
			protected void onPostExecute(String msg) {
				if(msg!=null){
					Log.i(TAG, "sendPushKeyToServer : onPostExecute : response message ==> "+msg);
					registerPusKey = true;
					//					AppUtil.showTost(UIActivityManager.this, "Push key registered \n\n "+Utilities.sRegId);
					//					SplashActivity.testPushMessage(Utilities.sRegId);
				}else
				{
					//	            		AppUtil.showTost(UIActivityManager.this, "Push key not registered ");
				}
			}
		}.execute(null, null, null);
	}

	public void setLocale(String lang) {

		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);		        
	}
	public String getLocale() {
		String locale = "en";
		try
		{
			//		locale = BusinessProxy.sSelf.addPush.mContext.getResources().getConfiguration().locale.getLanguage();
			//		System.out.println("My Locale --- "+locale);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return locale;
	}
	//--------------------

	/**
	 * Method that checks, if app is running in background or not
	 * @return true or false
	 */
	public boolean isRunningInForeground() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
		if (tasks.isEmpty()) {
			return false;
		}
		String topActivityName = tasks.get(0).topActivity.getPackageName();
		return topActivityName.equalsIgnoreCase(getPackageName());
	}
	/**
	 * Method to get Current activity name
	 * @return current activity name
	 */
	public static String getCurrentActivityName()
	{
		ActivityManager am = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		return am.getRunningTasks(1).get(0).topActivity.getClassName();
	}
	public String getCurrentPackageName()
	{
		ActivityManager am = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		return am.getRunningTasks(1).get(0).topActivity.getPackageName();
	}
	private void clearReferences(){
		Activity currActivity = mMyApp.getCurrentActivity();
		if (currActivity != null && currActivity.equals(this))
			mMyApp.setCurrentActivity(null);
	}
	public Activity getCurrentActivity()
	{
		if(mMyApp != null)
			return mMyApp.getCurrentActivity();
		return null;
	}
	//---------------------------------
	public  boolean isInternetOn() {
		//DATE :19-08-2015
		//MANOJ SINGH
		try{
			ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting())
				return true;
			else
				return false;
		}
		catch(Exception e){
			return true;

		}
		// OLD CODE throwing error on tab
		//DATE :19-08-2015
		//MANOJ SINGH

		/*    // get Connectivity Manager object to check connection
		        ConnectivityManager connec = (ConnectivityManager)getSystemService(UIActivityManager.CONNECTIVITY_SERVICE);

		           // Check for network connections
		            if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
		                 connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
		                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
		                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

		                // if connected with internet

		             //   Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
		                return true;

		            } else if ( 
		              connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
		              connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

		                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
		                return false;
		            }
		          return false;*/
	}
	//-------------------------------------------------------------------------------------------
	public void gotoHomeScreenAndSendRequest(){
		try {
			//		if(isInternetOn()){
			//			new Thread(new Runnable() {
			//				
			//				@Override
			//				public void run() {
			//					// TODO Auto-generated method stub
			//					sendLoginRequest();
			//				}
			//			}).start();
			//		}
			//		else
			{
				BusinessProxy.sSelf.setUserId(SettingData.sSelf.getUserID());
				mCurrentSelectedTab = TAB_COMMUNITY;
				Intent intent = new Intent(this, KainatHomeActivity.class);
				startActivity(intent);
				handleLoginResponseNew(null);
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendLoginRequest() {
		try {
			Hashtable<String, String> headerParam = new Hashtable<String, String>();
			headerParam.put("RT-Params", ClientProperty.RT_PARAMS);
			Hashtable<String, String> postParam = new Hashtable<String, String>();
			{
				User.getInstance().userName  = SettingData.sSelf.getUserName() ;
				User.getInstance().password  = SettingData.sSelf.getPassword();
				User.getInstance().email  = SettingData.sSelf.getEmail();
				Log.i(TAG, "sendLoginRequest :: ############## PUSH  client_param ###################" +  ClientProperty.CLIENT_PARAMS + "::requesttype##" + RequestType.LOGIN_FB_AUTO);
				postParam.put("mode", User.getInstance().LOGIN_ROCKETALK_SHORT_NAME);// mode=fb
				// rt
				if(SettingData.sSelf.getAppkey() != null)
					postParam.put("appkey", SettingData.sSelf.getAppkey());

				if (SettingData.sSelf.getPartneruserkey() != null)
					postParam.put("partneruserkey", SettingData.sSelf.getPartneruserkey());// fb

				if (User.getInstance().userName != null && !checkEmail(User.getInstance().userName) && !checkPhone(User.getInstance().userName)) 
				{
					postParam.put("username", User.getInstance().userName);
					if (User.getInstance().userName != null && User.getInstance().userName.indexOf("~") != -1) {

						String un[] = User.getInstance().userName.split("~");
						postParam.put("fname", un[0]);
						postParam.put("lname", un[1]);
						postParam.put("username", un[0] + " " + un[1]);
					}
				}
				if (User.getInstance().userName != null && checkEmail(User.getInstance().userName) && !checkPhone(User.getInstance().userName))
					postParam.put("emailid", User.getInstance().userName);
				if (User.getInstance().userName != null && !checkEmail(User.getInstance().userName) && checkPhone(User.getInstance().userName))
					postParam.put("phone", User.getInstance().userName);
				if (User.getInstance().password != null)
					postParam.put("password", User.getInstance().password);
				if (User.getInstance().multipleAccountOnRT && (User.getInstance().userName != null && User.getInstance().userName.indexOf("~") != -1)) 
				{
					String un[] = User.getInstance().userName.split("~");
					postParam.put("fname", un[0]);
					postParam.put("lname", un[1]);
					postParam.put("username", un[0] + " " + un[1]);
				}
				String urlStr = "http://" + Urls.TEJAS_HOST + "/tejas/feeds/api/user/login";
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(urlStr);
				MultipartEntity mpEntity = new MultipartEntity();
				for (Iterator<String> iterator = postParam.keySet().iterator(); iterator.hasNext();) 
				{
					String key = iterator.next();
					String value = postParam.get(key);
					mpEntity.addPart(key, new StringBody(value));
				}
				httppost.setHeader("RT-Params", ClientProperty.RT_PARAMS);				
				httppost.setHeader("client_param", ClientProperty.CLIENT_PARAMS + "::requesttype##" + RequestType.LOGIN_FB_AUTO);
				httppost.setEntity(mpEntity);
				HttpResponse response;
				response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
				String responseString = EntityUtils.toString(r_entity);
				try {
					handleLoginResponseNew(null);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d(TAG, "sendLoginRequest :: responseString : "+responseString);
			}
		} catch (final Exception e) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AppUtil.showTost(UIActivityManager.this, getString(R.string.check_net_connection)) ;
				}
			});
			finish() ;
			e.printStackTrace();
		}
	}
	//------------------ Manoj -------------------

	// For AUTOPLAY In Shared Prefrence 
	public void saveAutoplay(boolean bool)
	{
		SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPrefrenceName, MODE_PRIVATE); 
		Editor editor = pref.edit(); 
		editor.putBoolean(AUTOPLAYBOOL, bool);           // Saving boolean - true/false
		//editor.putString(AUTOPLAYSTR, val);            // Saving string
		// Save the changes in SharedPreferences
		editor.commit(); // commit changes
		// HERE POP UP
		if(bool){
			showSingleButtonDialog(getString(R.string.autoplay_on));
		}
		else{
			showSingleButtonDialog(getString(R.string.autoplay_off));
		}
	}
	public boolean getAutoplay()
	{
		SharedPreferences pref = getApplicationContext().getSharedPreferences(SharedPrefrenceName, MODE_PRIVATE); 
		return  pref.getBoolean(AUTOPLAYBOOL, true);      
	}

	public void removeNotification(){
		try
		{
			NotificationManager mNotificationManager = (NotificationManager)
					this.getSystemService(Context.NOTIFICATION_SERVICE);
			for(Integer in :  GcmIntentService.notificationID){
				mNotificationManager.cancel(in);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void showSingleButtonDialog(String Text){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("");
		builder.setMessage(Text);

		builder.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog diag = builder.create();

		//Display the message!
		diag.show();
	}
	//--------- Phone State Receiver ---------------
	static boolean phoneRinging = false;
	public boolean checkPhoneState(int type){
		phoneStateListener = new PhoneStateListener() {  
			@Override  
			public void onCellInfoChanged(List<CellInfo> cellInfo) {  
				int i = 0;  
				super.onCellInfoChanged(cellInfo);  
				//                  TextView call_info = (TextView)findViewById(R.id.cellInfo);  
				//                  if (cellInfo != null) {  
				//                       for (i = 0; i < cellInfo.size(); i++); 
				//                       call_info.setText(i + "Cell Info Available");  
				//                  } else {  
				//                       call_info.setText("No Cell Info Available");  
				//                  }  
			}  
			@Override  
			public void onDataActivity(int direction) {  
				super.onDataActivity(direction);  
				//                   TextView data_activity = (TextView)findViewById(R.id.dataActivity);  
				switch (direction) {  
				case TelephonyManager.DATA_ACTIVITY_NONE:  
					//                             data_activity.setText("No Data Activity");       
					break;  
				case TelephonyManager.DATA_ACTIVITY_IN:  
					//                             data_activity.setText("Incoming Data Activity");       
					break;  
				case TelephonyManager.DATA_ACTIVITY_OUT:  
					//                             data_activity.setText("Outgoing Data Activity");       
					break;  
				case TelephonyManager.DATA_ACTIVITY_INOUT:  
					//                             data_activity.setText("Bi-directional Data Activity");       
					break;  
				case TelephonyManager.DATA_ACTIVITY_DORMANT:  
					//                             data_activity.setText("Dormant Data Activity");       
					break;  
				default:  
					break;  
				}  
			}  
			@Override  
			public void onServiceStateChanged(ServiceState serviceState) {  
				super.onServiceStateChanged(serviceState);  
				//                   TextView service_state = (TextView)findViewById(R.id.serviceState);  
				switch (serviceState.getState()) {  
				case ServiceState.STATE_IN_SERVICE:  
					//                             service_state.setText("Phone in service");  
					break;  
				case ServiceState.STATE_OUT_OF_SERVICE:  
					//                             service_state.setText("Phone out of service");  
					break;  
				case ServiceState.STATE_EMERGENCY_ONLY:  
					//                             service_state.setText("Emergency service only");  
					break;  
				case ServiceState.STATE_POWER_OFF:  
					//                             service_state.setText("Powered Off");  
					break;  
				}  
			}  
			@Override  
			public void onCallStateChanged(int state, String incomingNumber) {  
				super.onCallStateChanged(state, incomingNumber);  
				//                   TextView callState = (TextView)findViewById(R.id.callState);  
				switch (state) {  
				case TelephonyManager.CALL_STATE_IDLE:  
					//                             callState.setText("Call State is IDLE");  
					break;  
				case TelephonyManager.CALL_STATE_RINGING:  
					//                             callState.setText("Call State is RINGING"); 
					phoneRinging = true;
					break;  
				case TelephonyManager.CALL_STATE_OFFHOOK:  
					//                             callState.setText("Call State is OFFHOOK");  
					break;  
				default:  
					break;  
				}  
			}  
			@Override  
			public void onCellLocationChanged(CellLocation location) {  
				super.onCellLocationChanged(location);  
				//                   TextView cell_location = (TextView)findViewById(R.id.cellLocation);  
				//                   cell_location.setText(location.toString());  
			}  
			@Override  
			public void onCallForwardingIndicatorChanged(boolean cfi) {  
				super.onCallForwardingIndicatorChanged(cfi);  
				//                   TextView call_forwarding = (TextView)findViewById(R.id.callForwarding);  
				//                   if (cfi)  
				//                        call_forwarding.setText("Call forward ON");  
				//                   else  
				//                        call_forwarding.setText("Call forward OFF");  
			}  
			@Override  
			public void onMessageWaitingIndicatorChanged(boolean mwi) {  
				super.onMessageWaitingIndicatorChanged(mwi);  
				//                   TextView message_waiting = (TextView)findViewById(R.id.messageWaiting);  
				//                   if (mwi)  
				//                        message_waiting.setText("Call forward ON");  
				//                   else  
				//                        message_waiting.setText("Call forward OFF");  
			}  
		};  
		TelephonyManager mFlags = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);  
		mFlags.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |  
				PhoneStateListener.LISTEN_CALL_STATE |  
				//                      PhoneStateListener.LISTEN_CELL_INFO |  
				PhoneStateListener.LISTEN_CELL_LOCATION |  
				PhoneStateListener.LISTEN_DATA_ACTIVITY |  
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |  
				PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |  
				PhoneStateListener.LISTEN_SERVICE_STATE |  
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);    
		return phoneRinging;
	}
}