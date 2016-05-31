package com.kainat.app.android.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;

import com.kainat.app.android.CommunityChatActivity;
import com.kainat.app.android.CommunityPostActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.bean.LeftMenu;
import com.kainat.app.android.engine.DBEngine;
import com.kainat.app.android.facebook.Facebook;
import com.kainat.app.android.helper.LandingPageDatabaseHelper;
import com.kainat.app.android.model.Buddy;
import com.kainat.app.android.model.IMData;
import com.kainat.app.android.model.InboxMessage;
import com.kainat.app.android.model.LoginData;
import com.kainat.app.android.model.OutboxMessage;
import com.kainat.app.android.model.PhoneContact;
import com.kainat.app.android.util.BuildInfo;
import com.kainat.app.android.util.ChannelRefreshInfo;
import com.kainat.app.android.util.Chat;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.DateFormatUtils;
import com.kainat.app.android.util.FrameEngine;
import com.kainat.app.android.util.HexStringConverter;
import com.kainat.app.android.util.InboxTblObject;
import com.kainat.app.android.util.Location;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.OnSchedularListener;
import com.kainat.app.android.util.OutboxTblObject;
import com.kainat.app.android.util.ResponseObject;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.VoiceMedia;
import com.kainat.app.android.util.format.Payload;
import com.kainat.app.android.util.format.SettingData;

public final class BusinessProxy implements Runnable, OnSchedularListener {

	private static final String TAG = BusinessProxy.class.getSimpleName();
//	private static final boolean CHAT_DEBUG = false;
	private boolean log = false;

	public static boolean testuser = true;
	public static BusinessProxy sSelf;
	public int newMessageCount = 0;
	public int newChatMessageCount = 0;
	private NetworkTransport mTransport;
	private int mTransactionId;
	private boolean mRegistered;
	private int mUserId;
	private boolean mUserIdUpdated;
	private int mClientId;
	public String mGroupTimeStamp;
	public String mInboxTimeStamp;
	private int mGroupCountOnServer;
	public String mBuddyTimeStamp;
	private int mBuddyCountOnServer;
	public IMData mMessengerData = IMData.getInstance();
	private String mTopMessageIdOnClient;
	public String mSrcNumber;
	public int mTotInboxMsgAtServer;
	public boolean loginWithFb=false;
	private int iIMCtr;
	public int iRefreshPeriod;
	private boolean iIsNetActivity;
	//private int mPollTime = 0;//This value should never be 0, make sure before changing that
	public UIActivityManager mUIActivityManager;
	public long iBuzzPlayTime;
	private DBEngine mDBEngine;
	public long iNextUpgradeDuration;
	public int iAgentUpdateInfo;
	public Map<String, String> mAgentTable = new HashMap<String, String>();
	public InboxTblObject mInboxData;
	public boolean mIsApplicationMute;
	public String textToVoice="";
	public final int[] iBuzzTime = new int[] { 60, 60 };
	public InboxTblObject mBuddyInfo;
	public InboxTblObject mGroupInfo;
	public InboxTblObject mIMBuddyInfo;
	public List<Buddy> listBuddy;//added by nag
	public Hashtable<String, String> onlineFriends;//added by nag
	public Hashtable<String, String> onlineOfflineFriends;//added by nag
	public Vector<String> buddy;//added by nag
	private boolean mRun = true;
	private boolean mIsRunning;
	private ArrayList<ResponseObject> iResponseQueue;
	public boolean messageSound=false;
	public boolean messagebluetooth=false;
//	public AdRequest adRequest;//new AdRequest() ;
	private Timer iTimer;
	public boolean rToolTipsFlag;
	// @ Variables for chat data
	public Map<String, List<Chat>> chatData = new HashMap<String, List<Chat>>();
	//    public String iCurrChatUserName;
	public InboxTblObject mSearchResult;
//	public DatabaseHandler db;
	public boolean iRecordStat;
	public String iPreviewMessageId;
	public int iOp;
	public String cameraPathUrl="";
	public String CameraSetting=null;
	public String iExtraCommand;
	public String iSaveDownload;
	public String iSelNumberText;
	public boolean iDownloadTophone;
	public String iCurrAgentName;
	public String iKeyForAgent;
	public boolean mContactUploadedToServer;
	public int leftmenuScrolIndex;
	public int textFontSize=14;
	public int textFontSizeHeading=14;
	public String bluetoothDeviceName=null;
	public boolean btStartFlag=false;
	public boolean animloadingFlag=false;
	//Notification
	public String iNotificationMessage = "No new notifications!";
	public Vector<Object> mCacheURLs = new Vector<Object>(10);
	public String[] emailList;
	public List<PhoneContact> mActualContactList = new ArrayList<PhoneContact>();
	public List<PhoneContact> mActualContactEmailList = new ArrayList<PhoneContact>();
	//REcord Stats
	// Stat control variables
	public boolean isCalling=false;
//	public parentFeed currentAnimData;
//	public Vector<RtAnimFeed> animValue;
	public boolean startRecording=false;
	public String animStateUrl=null;
	public boolean animPlay=false;
	private StringBuilder iRecordStatStr = new StringBuilder();
	private int mRecordCount;
	private boolean iStaticStatOn = true;
	private final int mUserTdleTime = 2 * 60 * 1000;
	private static boolean mIsAppIdle;
	public long mUsersLastActivityTime;
//	public  BluetoothChatService mChatService = null;
	public boolean mInvisible;
	public String[] openChatList;
	public com.kainat.app.android.service.ThumbnailFactory localThumbnailFactory ;
	private List<Activity> avtivityRegister = new ArrayList<Activity>();
	public HashMap<String, String> excludeAutoLoadId = new HashMap<String, String>();
	public byte[] migrate;
	public BuildInfo buildInfo ;//= BuildInfo.getInstance(this);
	public boolean exitClicked = false ;
//	public List<String> ImageList;
	public List<Bitmap> Imageview;
	public Bitmap i;
	public String cashDir = null ;
	private List<String> mImagesPath = new ArrayList<String>();
	private Hashtable<String,Location> mImagesPathLoc = new Hashtable<String,Location>();
	private List<String> mVideoPath = new ArrayList<String>();
	private VoiceMedia mVoiceMedia;
//	public List<AddBean> addData = new ArrayList<AddBean>() ;
	public static Facebook mFacebook;
	 public static String faceBookUserUID = null;
	 public static com.kainat.app.android.facebook.AsyncFacebookRunner mAsyncRunner;
	 public int MaxSizeData=2;
	 public boolean isSafeLogin = false ;
	 public String facebookLoginAstxt =null;
	 public String facebookLoginUserUrl =null;
	 public Boolean BtFreindRequestFlag=false;
	 public Boolean stopbuzzchatoperation=false;
	 public BluetoothAdapter mBluetoothAdapter=null;
	 public boolean leftMenuAdFlag=false;
	 public Map<String, String> cameraSettingMap = new HashMap<String,String>();
	 public static Vector<String> screenId = new Vector<String>();
		public HashMap<String, String> advertisementData = new HashMap<String, String>();
//		public static HashMap<String, String> advertisementDataForScreens = new HashMap<String, String>();
		public StringBuffer SEOData = new StringBuffer();
//		public AddPush addPush =new AddPush();
//		public static HashMap<String, xmlData> addData = new HashMap<String, xmlData>() ;
//		public static HashMap<String, xmlData> syncData = new HashMap<String, xmlData>() ;
//		public static HashMap<String, Vector<FormBean>> formDataForScreens = new HashMap<String, Vector<FormBean>>();
		public Vector <String>restrictedScreenIdVec ;
	public boolean isRegistrationForm=false;
	public String mediaAddString ;
	public String communityAddString ;
	public String SEO ;
	public String dynamicScreen = null ;
	Thread  sessionFetchTimer ;
	public int displaywidth,displayheight;
	
	public boolean dontCloseLoading = false ;
	public Vector<LeftMenu> gridMenu = null;// later will move from here  
	public Vector<LeftMenu> gridMenu2 = new Vector<LeftMenu>() ;// later will move from here  
	
	 public LandingPageDatabaseHelper database;
	 public SQLiteDatabase sqlDB ;
	 
	 //Animation List URL
//	 public String animationListURL;
//	 public String singleAnimationURL;
	 
	 //Media-Communities categories Data wiht the ID's
	 public String[] mediaCategories;
	 public String[] mediaCategoryIDs;
	 public String[] communityCategories;
	 
	 //Channel refresh Data
	 public ChannelRefreshInfo[] channelRefreshInfo;
	 
	 //Image thumb and Video Thumb
	 
	 public static String thumbInfo = null;
	 public static String thumbInfoSmall = "iThumbFormat=100x100&vThumbFormat=100x100&iFormat=100x100";
	 public int imageWidth;
	 public int imageHeight;
	 public static String chatBit = null;
	 public boolean testFlag=false;
	 CommunityChatActivity communityChatRef;
	private BusinessProxy(Context context) {
		sSelf = this;
		
		//Initiate the categories
    	mediaCategories 	=  Constants.values;//context.getResources().getStringArray(R.array.category);
    	mediaCategoryIDs 	= Constants.valuesId;//new String[]{ "49", "50", "51", "52", "53", "54", "55", "56", "57"};
    	communityCategories = context.getResources().getStringArray(R.array.community_category);
		cashDir 			= context.getCacheDir().getAbsolutePath(); 
		buildInfo 			= BuildInfo.getInstance(context);
		localThumbnailFactory = com.kainat.app.android.service.ThumbnailFactory.getDefault(context);
		DataModel.createInstance();
		FrameEngine.createInstance();
		mDBEngine = new DBEngine(context);
		SettingData.createInstance(context);
		mTransport = NetworkTransport.getInstance();
		this.mTransactionId = 1;
		
		iResponseQueue = new ArrayList<ResponseObject>();
	
//		adRequest = new AdRequest() ;
		initOpenchatList();
//		adRequest.
		Thread th = new Thread(this);
		th.start();
	}
	
	public static void initialise(Context context) {
		if (null == sSelf) {
			new BusinessProxy(context);
		}
	}

	public void setInboxdata(InboxTblObject aData) {
		mInboxData = aData;
	}

	public void setStatusInvisible(boolean aBool) {
		mInvisible = aBool;
	}

	public boolean getInvisibleStatus() {
		return mInvisible;
	}

	public byte[] getFrameForAutoInboxLoad(OutboxTblObject newRequest)
	{
		int frameRet = 0;
		newRequest.mIdList[0] = ++mTransactionId;
		frameRet = FrameEngine.sSelf.createInboxFrame(newRequest);
		return mTransport.putRequestforAutoInboxLoad(newRequest, (byte)2);
	}
	public synchronized int sendToTransport(CommunityChatActivity activity, OutboxTblObject newRequest) {
		int ret = Constants.ERROR_NONE;
		communityChatRef = activity;
		try {
			newRequest.mIdList[0] = ++mTransactionId;
			if (log)
				Log.i(TAG, "sendToTransport : : NEXT TRANSID = " + mTransactionId + "  OPERATION = " + newRequest.mOp[0]);
			newRequest.mRetryCount[0] = 0;
			int frameRet = 0;
			switch (newRequest.mOp[0]) {
			case Constants.FRAME_TYPE_VTOV:
				frameRet = FrameEngine.sSelf.createVtovFrame(newRequest);
				break;
			default:
				break;
			}
			if (frameRet != Constants.ERROR_NONE) {
				return Constants.ERROR_CREATE_FRAME;
			}
			ret = newRequest.addRecords(DBEngine.OUTBOX_TABLE);
			if (Constants.ERROR_NONE != ret)
				return ret;
			wakeMe();
		} catch (OutOfMemoryError me) {
		} catch (Throwable ex) {
			if (log)
				Log.e(TAG, "sendToTransport :: ERROR SENDIG TO TRANSPORT .. " + ex.toString(), ex);
			ret = Constants.ERROR_CREATE_FRAME;
		}
		return ret;
	}
	
	public synchronized int sendToTransport(CommunityPostActivity activity, OutboxTblObject newRequest) {
		int ret = Constants.ERROR_NONE;
	//	communityChatRef = activity;
		try {
			newRequest.mIdList[0] = ++mTransactionId;
			if (log)
				Log.i(TAG, "sendToTransport : : NEXT TRANSID = " + mTransactionId + "  OPERATION = " + newRequest.mOp[0]);
			newRequest.mRetryCount[0] = 0;
			int frameRet = 0;
			switch (newRequest.mOp[0]) {
			case Constants.FRAME_TYPE_VTOV:
				frameRet = FrameEngine.sSelf.createVtovFrame(newRequest);
				break;
			default:
				break;
			}
			if (frameRet != Constants.ERROR_NONE) {
				return Constants.ERROR_CREATE_FRAME;
			}
			ret = newRequest.addRecords(DBEngine.OUTBOX_TABLE);
			if (Constants.ERROR_NONE != ret)
				return ret;
			wakeMe();
		} catch (OutOfMemoryError me) {
		} catch (Throwable ex) {
			if (log)
				Log.e(TAG, "sendToTransport :: ERROR SENDIG TO TRANSPORT .. " + ex.toString(), ex);
			ret = Constants.ERROR_CREATE_FRAME;
		}
		return ret;
	}
	public synchronized int sendToTransport(OutboxTblObject newRequest) {
		int ret = Constants.ERROR_NONE;
		try {
			newRequest.mIdList[0] = ++mTransactionId;
			// FIXME - Is this variable required?
			// mMessageOpForServer = newRequest.mMessageOp;
			if (log)
				Log.i(TAG, "sendToTransport : : NEXT TRANSID = " + mTransactionId + "  OPERATION = " + newRequest.mOp[0]);
			newRequest.mRetryCount[0] = 0;
			int frameRet = 0;
			boolean isIcu = false;

			switch (newRequest.mOp[0]) {
			case Constants.FRAME_TYPE_GET_PROFILE:
			case Constants.FRAME_TYPE_GET_PREFERENCES:
			case Constants.FRAME_TYPE_GET_ACC_SETTING:
			case Constants.FRAME_TYPE_COMM_MSG_STATUS:
			case Constants.FRAME_TYPE_GET_EXTENDED_PROFILE:
			case Constants.FRAME_TYPE_UPDATE_PROFILE:
			case Constants.FRAME_TYPE_LOGIN:
			case Constants.FRAME_TYPE_LOGOFF:
				if (!mRegistered) {
					if (newRequest.mOp[0] != Constants.FRAME_TYPE_LOGIN)
						return Constants.ERROR_CLIENT_NOT_LOGIN_YET;
				}
			case Constants.FRAME_TYPE_SIGNUP:
			case Constants.FRAME_TYPE_FORGOT_PASSWORD:
			case Constants.FRAME_TYPE_CHECK_AVAILABILITY:
			case Constants.FRAME_TYPE_ACTIVATE_CODE:
				frameRet = FrameEngine.sSelf.createSettingFrame(newRequest);
				isIcu = true;
				break;
			case Constants.FRAME_TYPE_VTOV:
				// case CommonConstants.FRAME_TYPE_GROUP_CREATE_NEW:// added
				// by mahesh on 7th december for add community.
				frameRet = FrameEngine.sSelf.createVtovFrame(newRequest);
				break;
			case Constants.FRAME_TYPE_SEARCH_OTS_BUDDY:
			case Constants.FRAME_TYPE_PHCONTACTS_INVITE:
			case Constants.FRAME_TYPE_INBOX_PLAY:
			case Constants.FRAME_TYPE_INBOX_SAVE_VOICE:
			case Constants.FRAME_TYPE_INBOX_MORE:
			case Constants.FRAME_TYPE_GET_GROUP_DETAILS:
			case Constants.FRAME_TYPE_INBOX_GET_PROFILE:
			case Constants.FRAME_TYPE_VIEW_GROUP_PROFILE:
			case Constants.FRAME_TYPE_GET_IM_SETTING: // forIM Settings.
			case Constants.FRAME_TYPE_GET_IM_OFFLINE:
			case Constants.FRAME_TYPE_BUDDY_GET_OFFLINE:
			case Constants.FRAME_TYPE_IM_SIGN_OUT:
			case Constants.FRAME_TYPE_CHECK_UPGRADE:
			case Constants.FRAME_TYPE_CHECK_AVAIL_COMM:
			case Constants.FRAME_TYPE_PREVIEW_DEFAULT_BUZZ:
			case Constants.FRAME_TYPE_INBOX_CHECK_NEW:
				if (!mRegistered)
					return Constants.ERROR_CLIENT_NOT_LOGIN_YET;
				isIcu = true;
				frameRet = FrameEngine.sSelf.createInboxFrame(newRequest);
				break;
            	
			case Constants.FRAME_TYPE_INBOX_REFRESH:
			case Constants.FRAME_TYPE_INBOX_DEL:
			case Constants.FRAME_TYPE_INBOX_DEL_ALL:
				frameRet = FrameEngine.sSelf.createInboxFrame(newRequest);
				break;
			case Constants.FRAME_TYPE_BOOKMARK:
				if (!mRegistered)
					return Constants.ERROR_CLIENT_NOT_LOGIN_YET;
				isIcu = true;
				frameRet = FrameEngine.sSelf.createBookmarkFrame(newRequest);
				break;
            
			default:
				break;
			}
			if (frameRet != Constants.ERROR_NONE) {
				return Constants.ERROR_CREATE_FRAME;
			}
			// check request type is ICU or normal
			
			if (isIcu) {
				// give request to transport
				//if (mTransport.isBusy()) {
					// cancel operation
//					mTransport.cancelOperation();
				//}
				// ** CHANGE ** remove wake me.....DONE
				mTransport.setSyncCall(isIcu);
				Thread.sleep(200);
				mTransport.putRequest(newRequest, (byte)2);
				Thread.sleep(200);
				
				return mTransport.sendRequest(true);// ,newRequest
			} else {
				ret = newRequest.addRecords(DBEngine.OUTBOX_TABLE);
				if (Constants.ERROR_NONE != ret)
					return ret;
			}
			wakeMe();
		} catch (OutOfMemoryError me) {
			System.gc();
		} catch (Throwable ex) {
			if (log)
				Log.e(TAG, "sendToTransport :: ERROR SENDIG TO TRANSPORT .. " + ex.toString(), ex);
			ret = Constants.ERROR_CREATE_FRAME;
		}
		return ret;
	}

	public String getUserStatusString() {
		String tempStatusString;
		if (!isRegistered()) {
			if (3 < mTransport.iRetryCount)
				tempStatusString = "(Offline)";
			else {
				switch (mTransport.iNetState[0]) {
				default:
				case NetworkTransport.INIT:
				case NetworkTransport.CONFIGURE:
				case NetworkTransport.CONNECTED:
				case NetworkTransport.STARTSESSION:
					tempStatusString = "(Connecting)";
					break;
				case NetworkTransport.DISCONNECTED:
					tempStatusString = "(Offline)";
					break;
				case NetworkTransport.EXECUTESPEECH:
					tempStatusString = "(Logging)";
					break;
				}
			}
		} else {
			tempStatusString = "(Online)";
		}
		return tempStatusString;
	}

	/**
	 * Gets the value of mRegistered
	 * 
	 * @return The mRegistered
	 */
	public final boolean isRegistered() {
		return mRegistered;
	}

	/**
	 * Sets the value of registered
	 * 
	 * @param registered
	 *            The mRegistered to set
	 */
	public final void setRegistered(boolean registered) {
		mRegistered = registered;
	}

	/**
	 * Sets the value of mUserId
	 * 
	 * @param mUserId
	 *            The mUserId to set
	 */
	public void setUserId(int mUserId) {
		if(/*!mUserIdUpdated && */mUserId != 0)
		{
			this.mUserId = mUserId;
			this.mUserIdUpdated = true;
		}
		else if(isRegistered() && this.mUserId != mUserId)//Shoot an email that user id is getting changed some how due to bug
		{
//			String message = "Error!! Logged in userID - "+this.mUserId+ " is trying to change with new ID - "+mUserId;
//			sendNewTextMessage("mahesh@rocketalk.com,tariq@onetouchstar.com", message, false);
		}
	}
	
	public void resetUserId(int mUserId) {
		this.mUserId = mUserId;
		this.mUserIdUpdated = false;
	}
	
	/**
	 * Gets the value of mUserId
	 * 
	 * @return The mUserId
	 */
	public int getUserId() {
		return mUserId;
	}

	/**
	 * Sets the value of mClientId
	 * 
	 * @param mClientId
	 *            The mClientId to set
	 */
	public void setClientId(int mClientId) {
		this.mClientId = mClientId;
	}

	/**
	 * Gets the value of mClientId
	 * 
	 * @return The mClientId
	 */
	public int getClientId() {
		return mClientId;
	}

	/**
	 * Sets the value of groupCountOnServer
	 * 
	 * @param groupCountOnServer
	 *            The groupCountOnServer to set
	 */
	public void setGroupCountOnServer(int groupCountOnServer) {
		this.mGroupCountOnServer = groupCountOnServer;
	}

	/**
	 * Gets the value of groupCountOnServer
	 * 
	 * @return The groupCountOnServer
	 */
	public int getGroupCountOnServer() {
		return mGroupCountOnServer;
	}

	/**
	 * Sets the value of mBuddyCountOnServer
	 * 
	 * @param mBuddyCountOnServer
	 *            The mBuddyCountOnServer to set
	 */
	public void setBuddyCountOnServer(int mBuddyCountOnServer) {
		this.mBuddyCountOnServer = mBuddyCountOnServer;
	}

	/**
	 * Gets the value of mBuddyCountOnServer
	 * 
	 * @return The mBuddyCountOnServer
	 */
	public int getBuddyCountOnServer() {
		return mBuddyCountOnServer;
	}

	/**
	 * Sets the value of mTopMessageIdOnClient
	 * 
	 * @param mTopMessageIdOnClient
	 *            The mTopMessageIdOnClient to set
	 */
	public void setTopMessageIdOnClient(String mTopMessageIdOnClient) {
		this.mTopMessageIdOnClient = mTopMessageIdOnClient;
	}

	/**
	 * Gets the value of mTopMessageIdOnClient
	 * 
	 * @return The mTopMessageIdOnClient
	 */
	public String getTopMessageIdOnClient() {
		return mTopMessageIdOnClient;
	}

	public void run() {
		while (this.mRun) {
			try {
				int outboxRecCount = BusinessProxy.sSelf.getRecordCount(DBEngine.OUTBOX_TABLE);
				synchronized (this) {
					
					// it checks for response queue and sending queue...if both
					// are empty then it will go in wait mode.
					// iTransport == null is checked for the first time when it
					// is invoked
					if ((iResponseQueue.size() == 0 && outboxRecCount == 0) || !isRegistered()) {
						mIsRunning = false;
						wait();
						mIsRunning = true;
					}
				}
				// Check response queue and handle it if queue is not empty
				int err = processResponse();

				boolean isProcessError = true;
				if (log && mTransport != null)
					Log.i(TAG, "run() :: ERROR STARTING REFRESH .. mTransport.isBusy() mTransport.getSyncCall() iIsNetActivity "+" : "+mTransport.isBusy() +" , "+ mTransport.getSyncCall()+" , "+iIsNetActivity);
				if (err != Constants.ERROR_NONE) {
					if (!isRegistered())
						isProcessError = false;
				}
				if (err == Constants.ERROR_PARSE_ERROR && isProcessError) {
					if (log)
						Log.i(TAG, "run() :: ERROR IN PROCESSING RESPONSE - CODE = " + err);
				} else if (err == Constants.ERROR_NET && isProcessError) {
					iIMCtr = 0;
					// This in case of net error... to retry after some time
					iRefreshPeriod = Constants.IM_REFRESH_TIME;
					try {
						startRefresh();
					} catch (Exception ex) {
						if (log)
							Log.e(TAG, "run() ::  ERROR STARTING REFRESH .. " + ex.toString());
					}
				} else if (mTransport!=null && mTransport.isBusy() == false && !mTransport.getSyncCall() && iIsNetActivity) {
					// actual request
					
					int ret = Constants.ERROR_OUTQUEUE_EMPTY;
					outboxRecCount = BusinessProxy.sSelf.getRecordCount(DBEngine.OUTBOX_TABLE);
					if(outboxRecCount > 0)
						ret = mTransport.sendRequest(false);
					if (log)
						Log.i(TAG, "run() :: TRANSPORT not busy, and netactivity ret outboxRecCount= " + iIsNetActivity+" , "+ret+" , "+outboxRecCount);
					// FROM HERE REFRESH STARTS
					//Commenting, this code is of no use as nothing will come in this refresh.
//					if (ret == Constants.ERROR_OUTQUEUE_EMPTY && isRegistered()) {
//						if (iIMCtr >= SettingData.sSelf.getRefreshPeriod()/* iParent.iPollTime */) {
//							iIMCtr = 0;
//							iRefreshPeriod = SettingData.sSelf.getRefreshPeriod();
//							if (messageIDString != null && messageIDString.length() > 1) {
//								// if refresh time == poll time then send read
//								// message request.
//								if (log)
//									Log.i(TAG, "run() :: Now going to Send read message request.");
//								sendReadMessageRequest();
//							}
//						}
//						try {
//							startRefresh();
//						} catch (Exception ex) {
//							if (log)
//								Log.e(TAG, "run() ::  ERROR STARTING REFRESH AFTER DATA SENT.. " + ex.toString(), ex);
//						}
//						if (log)
//							Log.i(TAG, "run() :: REFRESH TIMER STARTED:: iIMCtr = " + iIMCtr + ", And iRefreshPeriod = " + iRefreshPeriod);
//					} else if (ret != Constants.ERROR_NONE && isRegistered()) {
//						if (log)
//							Log.w(TAG, "run() ::  return value - " + ret);
//						// FIXME - Check for android
//						// AllDisplays.iSelf.showConfirmation(TextData.MSG_NOT_SENT, null, TextData.OK,
//						// UiController.iUiController.getWidth() - 60);
//						startRefresh();
//					}
				}
				else
				Thread.sleep(500);
			} catch (Exception ex) {
				if (log)
					Log.e(TAG, "run() :: ERROR : " + ex.toString(), ex);
			}
		}

	}

	public void setRefreshPeriod(int aNew) {
		iRefreshPeriod = Constants.IM_REFRESH_TIME;
	}

	private int processResponse() {
		int retErr = 0;
		try {
			ResponseObject resObj = null;
			if (this.iResponseQueue.size() > 0) {
				
				resObj = (ResponseObject) this.iResponseQueue.get(0);
				if(resObj.iHttpEngineIndex == 2)
					mTransport.setSyncCall(false);
				moveToSentFolder(resObj);
				if (log)
					Log.i(TAG, "processResponse() :: resObj.getError() = " + resObj.getError());
				if (resObj.getError() == Constants.ERROR_NET) {
					Logger.NET_ERROR = true ;
				}
				if (resObj.getError() == Constants.ERROR_NONE) {
					int ret = FrameEngine.sSelf.parse(resObj);
					// //////////////////////
					if (ret != Constants.ERROR_NONE) {
						resObj.setError(Constants.ERROR_NOT_FOUND);
						resObj.setResponseCode(Constants.RES_TYPE_ERROR);
						// return CommonConstants.ERROR_PARSE_ERROR;
					}
					if (resObj.getSentOp() == Constants.FRAME_TYPE_VTOV || resObj.getMessageCount() > 0) {
						if (log)
							Log.i(TAG, "processResponse :: Either FRAME_TYPE_VTOV or Got new message so iIMCtr RESET and Refresh time = " + iRefreshPeriod);
						iIMCtr = 0;
						iRefreshPeriod = Constants.IM_REFRESH_TIME;
					} else if (iRefreshPeriod == SettingData.sSelf.getRefreshPeriod())
						iRefreshPeriod = SettingData.sSelf.getRefreshPeriod();
					else
						iRefreshPeriod = Constants.IM_REFRESH_TIME;
					if (log)
						Log.i(TAG, "processResponse() :: REFRESH TIME = " + iRefreshPeriod);
					switch (resObj.getSentOp()) {
					case Constants.FRAME_TYPE_LOGIN:
						if (UIActivityManager.sScreenNumber != UIActivityManager.SCREEN_SPLASH) {
							switch (resObj.getResponseCode()) {
							case Constants.RES_TYPE_SUCCESS:
							case Constants.RES_TYPE_SOFT_UPGRADE:
							case Constants.RES_TYPE_ACTIVATION_NOT_DONE:
							case Constants.RES_TYPE_BAD_NAME:
							case Constants.RES_TYPE_HARD_UPGRADE:
							case Constants.RES_TYPE_NO_VOICE:
							case Constants.RES_TYPE_DEST_NOT_SUPPORTED:
							case Constants.RES_TYPE_INVALIDUSER:
							case Constants.RES_TYPE_SOFT_UPGRADE_WITH_VERIFICATION:
							case Constants.RES_TYPE_USER_ID_AVAILABLE:
								break;
							default:
								retErr = resObj.getResponseCode();
								iResponseQueue.remove(resObj);
//								mUIActivityManager.TransportNotification(resObj);
//								return retErr;
							}
						}
						// DO NOT USE BREAK CHECK CONDITION FOR
						// LOGIN, SIGNUP AND GUEST
					default:
						mTransport.putRequest(null,resObj.iHttpEngineIndex);
						break;
					}
					// END ]]
					if (log)
						Log.i(TAG, "processResponse :: Server Response code = " + resObj.getResponseCode());
					if (Constants.RES_TYPE_ERROR == resObj.getResponseCode() && Constants.FRAME_TYPE_VTOV == resObj.getSentOp()) {
						if (log)
							Log.w(TAG, "processResponse() :: MSG_NOT_SENT, response code = " + resObj.getResponseCode());
						// FIXME - Check this for android
						// AllDisplays.iSelf.showConfirmation(TextData.MSG_NOT_SENT, null, TextData.OK,
						// UiController.iUiController.getWidth() - 60);
					}
//					mUIActivityManager.TransportNotification(resObj);
					if(communityChatRef != null)
						communityChatRef.processSendMessageResponse(resObj);
					if (Constants.FRAME_TYPE_LOGIN == resObj.getSentOp()) {
						this.mTransactionId = resObj.getLastTransactionId();
						OutboxTblObject outbox = new OutboxTblObject(Constants.OUTBOX_MAX_REC_SIZE);
						int val = outbox.updateAllTransactionIds(this.mTransactionId, DBEngine.OUTBOX_TABLE);
						if (val > 0)
							this.mTransactionId = val;
					}
				} else {
					if (log)
						Log.i(TAG, "processResponse ::ELSE part error case = " + iRefreshPeriod);
					switch (resObj.getSentOp()) {
					case Constants.FRAME_TYPE_LOGIN:
						if (UIActivityManager.sScreenNumber != UIActivityManager.SCREEN_SPLASH) {
							if (Constants.ERROR_NONE != resObj.getError()) {
								retErr = resObj.getError();
								iResponseQueue.remove(resObj);
								mTransport.putRequest(null, resObj.iHttpEngineIndex);
							}
						}
						break;
					default:
						mTransport.putRequest(null, resObj.iHttpEngineIndex);
						if (resObj.getSentOp() == Constants.FRAME_TYPE_VTOV) {
							if (log)
								Log.w(TAG, "processResponse :: ERROR CASE - Either FRAME_TYPE_VTOV or Got new message so iIMCtr RESET and Refresh time = " + iRefreshPeriod);
							iIMCtr = 0;
							iRefreshPeriod = Constants.IM_REFRESH_TIME;
						} else if (iRefreshPeriod == SettingData.sSelf.getRefreshPeriod())
							iRefreshPeriod = SettingData.sSelf.getRefreshPeriod();
						else
							iRefreshPeriod = Constants.IM_REFRESH_TIME;
						break;
					}
					if(communityChatRef != null)
						communityChatRef.processSendMessageResponse(resObj);
//					mUIActivityManager.TransportNotification(resObj);
				}
				if (log)
					Log.i(TAG, "processResponse :: RESPONSE IS BEING DEQUEUE");
				mTransport.putRequest(null, resObj.iHttpEngineIndex);
				iResponseQueue.remove(resObj);
				retErr = resObj.getError();
				resObj.mResponseData = null;
				resObj.setData(null);
				resObj = null;
			}
			
		} catch (Exception ex) {
			if (log)
				Log.e(TAG, "--processResponse--[ERROR]--- " + ex.toString(), ex);
			retErr = Constants.ERROR_PARSE_ERROR;
		}
		return retErr;
	}

	public boolean sendSMS(String aTo, String aMessage, Activity context) {
		try {
//			SmsManager sms = SmsManager.getDefault();
//			sms.sendTextMessage(aTo, null, aMessage, null, null);
			
			Uri uri = Uri.parse("smsto:"+aTo);   
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);   
			it.putExtra("sms_body",aMessage);  
//			it.setClassName("com.android.mms", "com.android.mms.ui.ConversationList");
			context.startActivityForResult(it,12); 
			
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	public boolean sendSMS(String aTo, String aMessage, Context context) {
		try {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(aTo, null, aMessage, null, null);
			
//			Uri uri = Uri.parse("smsto:"+aTo);   
//			Intent it = new Intent(Intent.ACTION_SENDTO, uri);   
//			it.putExtra("sms_body",aMessage);  
////			it.setClassName("com.android.mms", "com.android.mms.ui.ConversationList");
//			context.startActivityForResult(it,12); 
			
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	private void moveToSentFolder(ResponseObject aResObj) {
		OutboxTblObject outboxObj = new OutboxTblObject(1);
		if (null == aResObj)
			return;
		try {
			if(log)
				if (Logger.MULTICHANEL){
					Log.i(TAG,"moveToSentFolder :: ");
			 }
			switch (aResObj.getSentOp()) {
			case Constants.FRAME_TYPE_VTOV:// move from outbox to sent folder
				outboxObj.getRecords(false, false, aResObj.getSentTransactionId(), (byte)-1, DBEngine.OUTBOX_TABLE);
				if (aResObj.getError() == Constants.ERROR_NONE) {
					if (Constants.RES_TYPE_ERROR == aResObj.getResponseCode()) {

						outboxObj.mRetryCount[0] = OutboxTblObject.MAX_RETRY_COUNT + 1;
						if (log)
							Log.i(TAG, "moveToSentFolder :: response" + aResObj.getResponseCode());
						// FIXME - Check this for android
						// AllDisplays.iSelf.showConfirmation(TextData.MSG_NOT_SENT,
						// null, TextData.OK,
						// UiController.iUiController.getWidth() - 60);
					} else {
						String dest = outboxObj.getDestinations(0).trim();
						if(log)
							if (Logger.MULTICHANEL){
								Log.i(TAG,"moveToSentFolder :: 1-mIdList--"+outboxObj.mIdList[0]);
						 }
						
						if (dest != null && dest.length() > 0 && -1 == dest.indexOf("g:") && -1 == dest.indexOf("a:") && -1 == dest.indexOf("I:")/* && outboxObj.mBitmap[0] != 0 */) {
							outboxObj.addRecords(DBEngine.SENTBOX_TABLE);
							if(log)
								if (Logger.MULTICHANEL){
									Log.i(TAG,"moveToSentFolder :: 2--mIdList--"+outboxObj.mIdList[0]);
							 }
						}
					}
				} else if (OutboxTblObject.MAX_RETRY_COUNT < outboxObj.mRetryCount[0]) {
					if (log)
						Log.w(TAG, "moveToSentFolder--[WARNING]--- ****COULD NOT SENT MESSAGE SO SENDING TO OUTBOX****");
					outboxObj.mIdList[0] = aResObj.getSentTransactionId();
					outboxObj.deleteRecordWithId(outboxObj.mIdList[0]);
					mTransport.putRequest(null, outboxObj.httpEngineIndex);
					if (log)
						Log.i(TAG, "moveToSentFolder --- retry - " + outboxObj.mRetryCount[0]);
					// FIXME - Check this for android.
					// AllDisplays.iSelf.showConfirmation(TextData.MSG_NOT_SENT,
					// null, TextData.OK, UiController.iUiController.getWidth()
					// - 60);
				}
			case Constants.FRAME_TYPE_INBOX_DEL:
			case Constants.FRAME_TYPE_INBOX_DEL_ALL:
			case Constants.FRAME_TYPE_INBOX_REFRESH:
				if (aResObj.getError() == Constants.ERROR_NONE) {
					outboxObj.mIdList[0] = aResObj.getSentTransactionId();
					outboxObj.deleteRecordWithId(outboxObj.mIdList[0]);
				}
				break;
			default:
				break;
			}
		} catch (Throwable e) {
			if (log)
				Log.e(TAG, "moveToSentFolder--- SENDING DATA TO SENT FOLDER .. " + e.toString(), e);
		}
		outboxObj = null;
	}

	public String parseNameInformation(String nameStr) {
		try{
		String displayName;
		if (nameStr.indexOf("<") != -1) {
			displayName = nameStr.substring(0, nameStr.indexOf("<"));
		} else {
			displayName = nameStr;
		}
		return displayName;
		}catch (Exception e) {
			return "" ;
		}
	}

	public String parseUsernameInformation(String nameStr) {
		String userName;
		if (nameStr.indexOf("<") != -1 && nameStr.indexOf(">") != -1) {
			userName = nameStr.substring(nameStr.indexOf("<") + 1, nameStr.indexOf(">"));
		} else {
			userName = nameStr;
		}
		return userName;
	}
	
	
	public String getDisplayName(String nameStr) {
		try{
			String displayName;
			if (nameStr.indexOf("<") != -1) {
				displayName = nameStr.substring(0, nameStr.indexOf("<"));
			} else {
				return parseUsernameInformation(nameStr);//displayName = nameStr;
			}
			return displayName;
			}catch (Exception e) {
				return "" ;
			}
	}
	
	public String[] parseNameInformationFNLN(String nameStr) {
		
		return Utilities.split(new StringBuffer(nameStr), " ");
	}
	//String str="mahseh chandra<mahi123>;ajay maurya<akm420>;sunny<leaone>;nagendra singh<nag>";
	public String parseMultiName(String str) {
		String strArray[] = str.split(";");
		StringBuffer buffer = new StringBuffer();
		for (int iTemp = 0; iTemp < strArray.length; iTemp++) {
			buffer.append(parseNameInformation(strArray[iTemp]));
			if (iTemp < strArray.length - 1)
				buffer.append(';');
		}
		return buffer.toString();
	}
	public static int messageCounter = 0;
	public static final int VALUE_TO_EMAIL = 500;
	public static final boolean WRITE_LOGS = false;
	public void writeLogsToFile(String message) {
		if(WRITE_LOGS){
		String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/kainat/kainat_logs.txt";
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
			writer.write(d.toString()+"->");
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
						messageCounter = 0;
						//Send email
						mUIActivityManager.sendEmail("Log message", buffer.toString());
						new File(fileName).delete();
						
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
	}


	public void sendReadMessageRequest() {
		if (log)
			Log.i(TAG, "--sendReadMessageRequest()::[FINE] Entry");
		try {
			String readMessageString = null;
			readMessageString = messageIDString.toString();
			//System.out.println("-----------readMessageString-----:"+readMessageString);
			OutboxTblObject reqObj = new OutboxTblObject(1, Constants.FRAME_TYPE_INBOX_REFRESH, Constants.MSG_OP_SET);
			if (readMessageString.length() > 120) {
				readMessageString = readMessageString.substring(0, 120);
				readMessageString = readMessageString.substring(0, readMessageString.lastIndexOf(';'));
			}
			reqObj.mMessageIDString = readMessageString;
			int ret = sendToTransport(reqObj);
			if (ret == Constants.ERROR_NONE) {
				messageIDString = null;
				iRefreshPeriod = Constants.IM_REFRESH_TIME;
			} else if (ret != Constants.ERROR_NONE) {
				if (log)
					Log.w(TAG, "--sendReadMessageRequest()--[WARNING] -- ****ERROR SENDING REQUEST****. RETURN VALUE = " + ret);
			}
		} catch (Exception ex) {
			if (log)
				Log.e(TAG, "--sendReadMessageRequest()::[ERROR] --  " + ex.toString(), ex);
		}
	}

	public boolean sendNewTextMessage(String aDestination, String aMessage, boolean aShowErrorAlert) {
		boolean ret = false;
		
		OutboxTblObject newRequest = new OutboxTblObject(1, Constants.FRAME_TYPE_VTOV, Constants.MSG_OP_VOICE_NEW);
		newRequest.mPayloadData.mText[0] = aMessage.getBytes();
		newRequest.mPayloadData.mTextType[0] = 1;// 1 is always for Text
		newRequest.mPayloadData.mPayloadTypeBitmap |= Payload.PAYLOAD_BITMAP_TEXT;
		newRequest.mDestContacts = new String[] { aDestination };
		int response = sendToTransport(newRequest);

		if (response == Constants.ERROR_NONE) {
			ret = true;
		}
		if (!ret && aShowErrorAlert) {
			switch (response) {
			case Constants.ERROR_OUTQUEUE_FULL:
				mUIActivityManager.showSimpleAlert("Error", "Outbox full. Please try after some time!");
				return false;

			default:
				mUIActivityManager.showSimpleAlert("Error", "Network busy! Please try after some time!");
				return false;
			}
		}
		return ret;
	}
	public boolean firstHit = true;
	public synchronized void startRefresh() {
		//Commenting, as this ode is on not use - As nothing will come in this refresh
//		if (SettingData.sSelf != null && SettingData.sSelf.getRefreshPeriod() != 0) {
//			if (log)
//				Log.i(TAG, "--startRefresh--[FINE] ENTRY");
//			cancelTimer();
//			iTimer = new Timer();
//			long sleep = 10 * 1000 ;
//			if(firstHit)
//				sleep = 1*1000 ;
//			firstHit = false ;
//			iTimer.schedule(new OtsSchedularTask(this, null, (byte)0), /*iRefreshPeriod*/sleep);
//		}
	}

	public void cancelTimer() {
		if (iTimer != null) {
			iTimer.cancel();
			iTimer = null;
		}
	}

	private void wakeMe() {
		synchronized (this) {
			if (!this.mIsRunning) {
				notify();
			}
		}
	}
	public static StringBuffer messageIDString;

	public synchronized void sendRefresh() throws Exception {
		if (log)
			Log.i(TAG, "--sendRefresh() ::[INFO] Entry ");
		int outboxSize = getRecordCount(DBEngine.OUTBOX_TABLE);
		if (outboxSize == 0) {
//			if (iRefreshPeriod == Constants.IM_REFRESH_TIME) {
//				iIMCtr += Constants.IM_REFRESH_TIME;
//			}
//			OutboxTblObject reqObj = new OutboxTblObject(1,
//					Constants.FRAME_TYPE_INBOX_REFRESH, Constants.MSG_OP_NEWMSG);
//			reqObj.mOp[0] = Constants.FRAME_TYPE_INBOX_REFRESH;
//
//			long diff = (System.currentTimeMillis() - iNextUpgradeDuration)
//					/ (1000 * 60 * 60);
//			if (messageIDString != null && messageIDString.length() > 1) {
//				String readMessageString;
//				if (Logger.ENABLE)
//					Logger.info(TAG,
//							"--sendRefresh::SENDING READ MESSASE REQUEST TO SERVER.");
//				readMessageString = messageIDString.toString();
//				// System.out.println("sending read message id--------"+readMessageString);
//				reqObj.mMessageOp = Constants.MSG_OP_SET; // MESSAGE OP =
//				// 2 SET For
//				// Read
//				// Messages.
//				if (readMessageString.length() > 120) {
//					readMessageString = readMessageString.substring(0, 120);
//					readMessageString = readMessageString.substring(0,
//							readMessageString.lastIndexOf(';'));
//				}
//				reqObj.mMessageIDString = readMessageString;
//				messageIDString = null;
//			} else if (diff >= 12) {
//				if (Logger.ENABLE)
//					Logger.debug(TAG,
//							"--sendRefresh--[INFO] SENDING CHECK UPGRADE::diff= "
//									+ diff);
//				iNextUpgradeDuration = System.currentTimeMillis();
//				reqObj.mMessageOp = Constants.MSG_OP_CHECK_UPGRADE;
//			}
//			try {
//
//			} catch (Exception e) {
//				if (Logger.ENABLE)
//					Logger.debug(TAG,
//							"--sendRefresh()--[ERROR] --  " + e.toString());
//			}
//			sendToTransport(reqObj);
		} else {
			if (log)
				Log.i(TAG, "--sendRefresh--[INFO] Pending message in out queue ! ");
			wakeMe();
		}
		if (log)
			Log.i(TAG, "--sendRefresh() ::[INFO] -- EXIT");
	}

	public void onTaskCallback(Object parameter, byte req) {
		if (log)
			Log.i(TAG, "--onTaskCallback--[INFO] RefreshTimerCB");
		try {
			cancelTimer();
			if (isRegistered()) {
				if (mUsersLastActivityTime > 0 && (System.currentTimeMillis() - mUsersLastActivityTime) > mUserTdleTime) {
					if (getRecordCount(DBEngine.OUTBOX_TABLE) == 0) {
						stopRefresh();
						iIMCtr = 0;
						mIsAppIdle = true;
						mUsersLastActivityTime = 0;
					} else
						sendRefresh();
				} else
					sendRefresh();
			}
		} catch (Throwable ex) {
			if (log)
				Log.e(TAG, "--executeTask():[ERROR] -- " + ex, ex);
		}
	}

	/**
	 * @param newState
	 */
	public void setNetActive(boolean newState) {
		synchronized (this) {
			iIsNetActivity = newState;
		}
	}

	public void enqueueResponse(ResponseObject res) {
		iResponseQueue.add(res);
		wakeMe();
	}

	public void networkStateChanged(int netState, int sentOpCode) {

	}
	public void networkDataLoadChange(int data) {
		// TODO Auto-generated method stub
		if(communityChatRef != null)
			communityChatRef.networkDataLoadChange(data);
		
	}

	public boolean isAppIdle() {
		return mIsAppIdle;
	}

	public void setAppStatus(boolean aBool) {
		mIsAppIdle = aBool;
	}

	public int getRecordCount(String tableName) {
		return mDBEngine.getRowCount(tableName);
	}
	public int resetOutboxSatus() {
		return mDBEngine.resetOutboxSatus();
	}
	public List<Integer> getAllIdsForTable(String tableName) {
		return mDBEngine.getAllIdsForTable(tableName);
	}

	public List<String> getAllIdsForInboxTable() {
		return mDBEngine.getAllIdsForInboxTable();
	}

	public void insertOrUpdateLoginData(String userName, ContentValues values) {
		mDBEngine.insertOrUpdateLoginData(userName, values);
	}

	public LoginData getLoginDataFor(String userName) {
		return mDBEngine.getLoginDataForUser(userName);
	}

	public String getPushRegId(String tableName) {
		return mDBEngine.getPushRegId(tableName);
	}
	public String getPushImeiId(String tableName) {
		return mDBEngine.getPushImeiId(tableName);
	}
	public void deleteForIDs(List<?> idList, String table) {
		mDBEngine.deleteContentForId(table, idList);
	}

	public void deleteTable(String table) {
		mDBEngine.deleteTableContents(table);
	}

	public void insertValuesInTable(String table, List<ContentValues> contents) {
		mDBEngine.insertValues(table, contents);
	}

	public Payload getPayloadForInboxMessage(String id) {
		return mDBEngine.getPayloadForInboxMessage(id);
	}

	public List<InboxMessage> getAllInboxMessages() {
		return mDBEngine.getAllInboxMessages();
	}
	public List<InboxMessage> getAllInboxIdPayload() {
		return mDBEngine.getAllInboxIdPayload();
	}
	public void migrateInboxOnlogin(String id, ContentValues updateValues) {
		mDBEngine.migrateInboxOnlogin(id, updateValues);
	}
	public ContentValues getOutboxTblObjectDetails(int transactionId, byte mRequestNo, String tableName) {
		return this.mDBEngine.getOutboxTblObjectDetails(tableName, transactionId, mRequestNo);
	}

	public List<OutboxMessage> getAllOutboxMessages() {
		return this.mDBEngine.getAllOutBoxMessages(DBEngine.OUTBOX_TABLE);
	}

	public List<OutboxMessage> getAllSentboxMessages() {
		return this.mDBEngine.getAllOutBoxMessages(DBEngine.SENTBOX_TABLE);
	}
	public List<String> getRecepients() {
		return this.mDBEngine.getRecepients(DBEngine.RECEPIENTS_TABLE);
	}
	public List<LoginData> getAllLoginData() {
		return this.mDBEngine.getAllLoginData(DBEngine.LOGIN_TABLE);
	}

	public List<OutboxMessage> getAllBookmarkboxMessages() {
		List<OutboxMessage> bookmark = new ArrayList<OutboxMessage>();
		return bookmark;
	}

	public List<Buddy> getAllBuddyInformation() {
		//		return mDBEngine.getAllBuddyInformation();//return BusinessProxy.sSelf.listBuddy;
		return BusinessProxy.sSelf.listBuddy;
	}

	public List<Buddy> getAllBuddyInformation1() {
		return mDBEngine.getAllBuddyInformation();
	}
//	public List<Buddy> getAllBuddyInformation1() {
//		return mDBEngine.getAllBuddyInformation();
//	}
	public void updateTableValues(String table, String id, ContentValues updatedValues) {
		long count = this.mDBEngine.updateOutboxValues(table, id, updatedValues);
		if (log)
			Log.i(TAG, "updateTableValues -- TABLE = " + table + "  Update count = " + count);
	}

	public void deletePayloadForIDs(List<String> ids) {
		this.mDBEngine.deletePayloads(DBEngine.INBOX_TABLE, ids);
	}

	/**
	 * Gets the value of mTransactionId
	 * 
	 * @return The mTransactionId
	 */
	public final int getTransactionId() {
//		System.out.println("------------getTransactionId---- :"+mTransactionId);
		if(mTransactionId<=0)
			mTransactionId = 1 ;
		return mTransactionId;
	}

	/**
	 * Sets the value of transactionId
	 * 
	 * @param transactionId
	 *            The mTransactionId to set
	 */
	public final void setTransactionId(int transactionId) {
//		System.out.println("---------setTransactionId------- :"+mTransactionId);
		mTransactionId = transactionId;
	}

	public void deleteAllTables() {
		this.mDBEngine.deleteTableContents(DBEngine.INBOX_TABLE);
		this.mDBEngine.deleteTableContents(DBEngine.OUTBOX_TABLE);
		this.mDBEngine.deleteTableContents(DBEngine.GROUP_TABLE);
		this.mDBEngine.deleteTableContents(DBEngine.BUDDY_TABLE);
		this.mDBEngine.deleteTableContents(DBEngine.MESSENGER_TABLE);
		// FIXME - Uncomment when implementation done for below tables
		this.mDBEngine.deleteTableContents(DBEngine.AGENT_TABLE);
		this.mDBEngine.deleteTableContents(DBEngine.SENTBOX_TABLE);

	}

	public void deleteAllInbox() {
		this.mDBEngine.deleteTableContents(DBEngine.INBOX_TABLE);
		

	}
	public void deleteAllSentBox() {
		this.mDBEngine.deleteTableContents(DBEngine.SENTBOX_TABLE);
		

	}
	public int getTopId(String table) {
		List<Integer> idList = this.mDBEngine.getAllIdsForTable(table);
		if (idList.isEmpty())
			return 0;
		return idList.get(idList.size() - 1).intValue();
	}

	public String getTopInboxId() {
		List<String> idList = this.mDBEngine.getAllIdsForInboxTable();
		if (idList.isEmpty())
			return "";
		return idList.get(idList.size() - 1);
	}

	/**
	 * @param agentTable
	 * @return
	 */
	public int getTopMessageIdFromAgents(String agentTable) {
		return 0;
	}

	/**
     * 
     */
	public synchronized void cancelOperation() {
		mTransport.cancelOperation();
	}

	/**
     * 
     */
	public synchronized void stopNetworkActivity() {
		setNetActive(false);
		stopRefresh();
		cancelOperation();
		for(byte i = 0; i<mTransport.HTTP_ENGINES; i++)
			mTransport.putRequest(null,i);
	}

	public synchronized void stopRefresh() {
		if (log)
			Log.i(TAG, "stopRefresh-- Stopping refesh");
		cancelTimer();
	}

	public int getColor(int r, int g, int b) {
		return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	}

	public String replaceString(StringBuffer string, String searchStr, String replaceStr, int fromIndex) {
		StringBuffer strBuff = new StringBuffer();
		// String str = string.toString();
		int index = string.toString().intern().indexOf(searchStr, fromIndex);
		if (index != -1) {
			strBuff.append(string.toString().intern().substring(0, string.toString().intern().indexOf(searchStr)));
			strBuff.append(replaceStr);
			strBuff.append(string.toString().intern().substring(string.toString().intern().indexOf(searchStr) + searchStr.length()));
			fromIndex = index;
			return replaceString(strBuff, searchStr.intern(), replaceStr.intern(), fromIndex);
			//return retStr;
		} else
			return string.toString();
	}

	//----------------------------------------------------------------------------
//	public void recordScreenStatistics(String aStr, boolean aSep, boolean aStatic) {
//		if (aStatic && !iStaticStatOn)
//			return;
//		if (log)
//			Log.i(TAG, "recordScreenStatistics()::[FINE] Entry");
//		if (iRecordStatStr == null || aStr == null || (aStr != null && aStr.length() == 0))
//			return;
//		if (aSep && iRecordStatStr.length() > 0) {
//			if (mRecordCount == Constants.STAT_COUNT) {
//				if (BusinessProxy.sSelf.sendNewTextMessage("STAT<a:userstatistics>", iRecordStatStr.toString(), false)) {
//					mRecordCount = 0;
//					iRecordStatStr.delete(0, iRecordStatStr.length());
//				} else {
//					if (log)
//						Log.w(TAG, "recordScreenStatistics****ERROR SENDING REQUEST****. RETURN VALUE =");
//				}
//			}
//			if (iRecordStatStr.length() > 0)
//				iRecordStatStr.append("^");
//		}
//		iRecordStatStr.append(aStr);
//		mRecordCount++;
//		//System.out.println("Stat -> " + iRecordStatStr.toString() + " and rec count - " + mRecordCount);
//	}

	public void setStatState(boolean aBool) {
		iStaticStatOn = aBool;
	}

	public boolean getStatState() {
		return iStaticStatOn;
	}

	public void addStatData(String aStr) {
		iRecordStatStr.append(aStr);
	}

	public String getStatString() {
		return iRecordStatStr.toString();
	}

	public void clearStatData() {
		iRecordStatStr.delete(0, iRecordStatStr.length());
	}
	
	public void removeSameName(String showName){	
		try{
			Collection s = BusinessProxy.sSelf.mAgentTable.values() ;
			Vector<String> vec = new Vector<String>() ;
			
			Map<String, String> agent = BusinessProxy.sSelf.mAgentTable;
			Iterator<String> keyIterator = agent.keySet().iterator();
			Iterator<String> valueIterator = agent.values().iterator();
			while (valueIterator.hasNext()) {
				String key = keyIterator.next();
				String value = valueIterator.next();
				
				if(value.equalsIgnoreCase(showName))
				vec.add(key);
			}
			for (int i = 0; i < vec.size(); i++) {
				BusinessProxy.sSelf.mAgentTable.remove(vec.elementAt(i));
			}
		} catch (Exception e) {

		}

	}

	public boolean isFirend(String s)
	{
		if(log)
			Log.i(TAG, "isFriend :: Entry s : "+s);
		boolean b = false ;
		for (int i = 0; i < listBuddy.size(); i++) {
			Buddy buddy = listBuddy.get(i);//BKMARKONLINE online offline BKMARKOFFLINE
			if(log)
				Log.i(TAG, "isFriend :: buddy.status : "+buddy.status);
			if(buddy.realName.equalsIgnoreCase(s))
			{
				b=true;
				break;
			}
		}
		if(log)
			Log.i(TAG, "isFriend :: Exit b : "+b);
		return b;
		
	}
	
	
	public boolean isFirendInList(String  username)
	{
		if(log)
			Log.i(TAG, "isFirendInList :: username : "+username);
		boolean b = false ;
		if(username.equalsIgnoreCase(SettingData.sSelf.getUserName()))
			return true;
			if(listBuddy!=null){
			for (int i = 0; i < listBuddy.size(); i++) {
				Buddy buddy = listBuddy.get(i);//BKMARKONLINE online offline BKMARKOFFLINE
				if(log)
					Log.i(TAG, "isFirendInList :: buddy.status : "+buddy.status);
				if((parseUsernameInformation(buddy.name).equalsIgnoreCase(username)))
				{
					
					b=true;
					break;
				}
				
			}
			}
			
			if(log)
				Log.i(TAG, "isFirendInList :: b : "+b);
			return b;
		
	}
	
public boolean isBookMarkFriend(String username){
		if(log)
			Log.i(TAG, "isBookMarkFriend :: username : "+username);
		boolean b = false ;
		if(username.equalsIgnoreCase(SettingData.sSelf.getUserName()))
			return b;
			if(listBuddy!=null){
			for (int i = 0; i < listBuddy.size(); i++) {
				Buddy buddy = listBuddy.get(i);//BKMARKONLINE online offline BKMARKOFFLINE
				if(log)
					Log.i(TAG, "isBookMarkFriend :: buddy.status : "+buddy.status);
				if((parseUsernameInformation(buddy.name).equalsIgnoreCase(username) && buddy.status.equalsIgnoreCase("BKMARKONLINE")) ||(parseUsernameInformation(buddy.name).equalsIgnoreCase(username) && buddy.status.equalsIgnoreCase("BKMARKOFFLINE")) )
				{
					
					b=true;
					break;
				}
				
			}
			}
			
			if(log)
				Log.i(TAG, "isBookMarkFriend : b : "+b);
			return b;
	}
	public boolean isFriendaOnline(String s) {
		if(s.equalsIgnoreCase(SettingData.sSelf.getUserName()))
			return true ;
		
		boolean ret = false ;
		try {
			if (s.indexOf("<") != -1 && s.indexOf(">") != -1) {
				s = s.substring(s.indexOf("<") + 1, s.indexOf(">"));
			}
			ret= BusinessProxy.sSelf.onlineFriends.containsKey(s.toLowerCase());
			return ret ;
		} catch (Exception e) {
			return ret;
		}
	}
	
	public boolean isFriendaOffline(String s) {
		if(s.equalsIgnoreCase(SettingData.sSelf.getUserName()))
			return true ;
		
		boolean ret = false ;
		try {
			if (s.indexOf("<") != -1 && s.indexOf(">") != -1) {
				s = s.substring(s.indexOf("<") + 1, s.indexOf(">"));
			}
			ret= BusinessProxy.sSelf.onlineOfflineFriends.containsKey(s.toLowerCase());
			return ret ;
		} catch (Exception e) {
//			e.printStackTrace();
			return ret;
		}
	}

	public boolean isInBuddyList(String username){
		username = username.toLowerCase() ;
		//System.out.println("isFriendaOnline------isInBuddyList--------username---------------------->" + username);
		//System.out.println("isFriendaOnline------isInBuddyList--------buddy.tostring---------------------->" + buddy.toString());
//		boolean yes = buddy.contains(username);
		
		//System.out.println("isFriendaOnline------isInBuddyList--------username-----------ret----------->" + yes);
		return false ;//yes ;
	}
	public int insertChat(String name, Payload payload, InboxMessage msg) {
		int insertAt = -1 ;
		try{
		Iterator<String> keyIterator = BusinessProxy.sSelf.chatData.keySet().iterator();
	/*	while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			System.out.println("key=" + key);
		}*/
		
		if (null == BusinessProxy.sSelf.chatData.get(name))
		{
			if(log)
				Log.i(TAG, "insertChat :: ");
			List<Chat> chatDataList = new ArrayList<Chat>();
			BusinessProxy.sSelf.chatData.put(name, chatDataList);
			Chat chat = new Chat();
			chat.payload = payload;
			chat.userName = name;
			chat.timestamp = msg.mTime;
			Utilities.saveChatData(chat,name);			
			chatDataList.add(chat);
			insertAt = 0 ;
		} 
		else if (BusinessProxy.sSelf.chatData.get(name) != null)
		{			
			Date past = null ;
			Date savedDate = null ;
			SimpleDateFormat sdf = null;
			String newtime = msg.mTime;
			if (newtime.indexOf("-") != -1) {
				newtime = DateFormatUtils.convertGMTDateToCurrentGMTDate(newtime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			} else {
				sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}
			
//			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				past = sdf.parse(newtime);
				if(Logger.ENABLE)Logger.debug(TAG, "chat time stamp "+past.getTime());
			} catch (ParseException e) {
//				if(Logger.ENABLE)Logger.debug(TAG, "chat time stamp "+past.getTime());
				e.printStackTrace();
			}
			long tempT = 0 ;
//			int insertAt = -1 ;
			if(log)
				Log.i(TAG, "insertChat :: BusinessProxy.sSelf.chatData.get(name) : "+BusinessProxy.sSelf.chatData.get(name));
			List<Chat> chatDataList = BusinessProxy.sSelf.chatData.get(name);
			for (int i = 0; i < chatDataList.size(); i++) {
				String stime =chatDataList.get(i).timestamp ;
				if(stime == null)stime = Utilities.getCurrentdate() ;
				if(stime.equalsIgnoreCase(msg.mTime)){
					if(log)
						Log.i(TAG, stime+"...same id found exiting without inserting... "+msg.mTime);
					return i;
				}
				try {
//					savedDate = sdf.parse(chatDataList.get(i).timestamp);
					
					if (stime.indexOf("-") != -1) {
						stime = DateFormatUtils.convertGMTDateToCurrentGMTDate(chatDataList.get(i).timestamp, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
						sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					} else {
						sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					}
					savedDate = sdf.parse(stime);
//					if(Logger.ENABLE)Logger.debug(TAG, "savedDate time stamp "+savedDate.getTime());
				} catch (ParseException e) {
//					if(Logger.ENABLE)Logger.debug(TAG, "chat time stamp "+past.getTime());
					e.printStackTrace();
				}
				if(log)
					Log.i(TAG, past.getTime()+":"+savedDate.getTime());
				if(savedDate.getTime() >= past.getTime() && (tempT >= savedDate.getTime() || tempT == 0 )){
					insertAt = i ;
					tempT = savedDate.getTime();
				}
				if(log)
					Log.i(TAG, "-----t----------------------insertAt "+insertAt);
				
			}
			if(log)
				Log.i(TAG, "----------------------------insertAt "+insertAt);
			
			int tempInsertAt = insertAt ;
			BusinessProxy.sSelf.chatData.put(name, chatDataList);
			Chat chat = new Chat();
			chat.payload = payload;
			chat.userName = name;
			chat.timestamp = msg.mTime;
			Utilities.saveChatData(chat,name);
			if(tempInsertAt > -1)
			{
//				Chat c= null ;
				do{
					if(tempInsertAt < chatDataList.size())
					chat =chatDataList.set(tempInsertAt, chat);
					else{
						chatDataList.add(chat);
						break ;
					}
					tempInsertAt++ ;
				}while(chat!=null);
			}
			else
			chatDataList.add(chat);
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return insertAt ;
	}

	public void initOpenchatList() {
		try{
			if(log)
				Log.i(TAG, "#initOpenchatList#") ;
		String s[] = Utilities.getChatList();
		ArrayList<String> t = new ArrayList<String>();
		for (int i = 0; i < s.length; i++) {
//			String currentChatter = HexStringConverter.getHexStringConverterInstance().hexToString(s[i].substring(0, s[i].indexOf('.')));//(SettingData.sSelf.getUserName() + "_" + key) ;;//key;		
			String currentChatter = HexStringConverter.getHexStringConverterInstance().hexToString(s[i]);//(SettingData.sSelf.getUserName() + "_" + key) ;;//key;
			if (!currentChatter.startsWith(SettingData.sSelf.getUserName()))
				continue;

			String userw = currentChatter.substring(currentChatter.indexOf(SettingData.sSelf.getUserName()) + SettingData.sSelf.getUserName().length() + 1, currentChatter.length());
			t.add(userw);
			if(log)
				Log.i(TAG, "----open chat---"+userw) ;
		}
		// store nagendra kumar<nagendra1020>
//		openChatList
		openChatList = new String[t.size()];
		t.toArray(openChatList);
		}catch (Exception e) {
			if(log)
				Log.i(TAG, "Total Email available "+e.getMessage()) ;
		}
	} 
	public boolean isMyChatOpen(String userName)
	{
		userName = parseUsernameInformation(userName);
		if(log)
			Log.i(TAG, "----isMyChatOpen---"+userName) ;
		for (int i = 0; i < openChatList.length; i++) {
			if(userName.equals(parseUsernameInformation(openChatList[i]))){
				if(log)
					Log.i(TAG, "----yes My Chat  is Open---"+userName) ;
				return true;
			}
		}
		if(log)
			Log.i(TAG, "----no My Chat  is Open---"+userName) ;
		return false;
	}
	public void registerActivity(Activity activity)
	{
		avtivityRegister.add(activity);
	}
	public void unRegisterActivity(Activity activity)
	{
		boolean res = avtivityRegister.remove(activity);
		if(res)activity.finish();
//		System.out.println("Un register activity :: "+res +activity.getLocalClassName());
	}
	public void unRegisterAllActivity()
	{
		for (int i = 0; i < avtivityRegister.size(); i++) {
			Activity activity = avtivityRegister.get(i) ;
//			System.out.println("Un register activity :: "+activity.getLocalClassName());
			activity.finish();
		}
		avtivityRegister = new ArrayList<Activity>();
	}
	public void initChatIfAvail(String name){
		if (BusinessProxy.sSelf.chatData.get(name) == null) {
			List<Chat> mCurrentUserChatData = Utilities.getChatData(SettingData.sSelf.getUserName() + "_" + name);
			if (mCurrentUserChatData != null)
				BusinessProxy.sSelf.chatData.put(name, mCurrentUserChatData);
			if (log)
				Log.i(TAG, "loding data from file system......");
			
		}
		List<Chat> chatData = BusinessProxy.sSelf.chatData.get(name);
		if (null == chatData) {
			BusinessProxy.sSelf.chatData.put(name, new ArrayList<Chat>());
		}
		BusinessProxy.sSelf.initOpenchatList();
	}
	
	public void cancelSessionFetchTimer() {
		if (sessionFetchTimer != null) {
			sessionFetchTimer.stop();
			sessionFetchTimer = null;
		}
	}
	
	public void initializeDatabase()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(database==null){
					database = new LandingPageDatabaseHelper(mUIActivityManager);
				}
				if(sqlDB==null){
					sqlDB = database.getWritableDatabase();
				}
			}
		}).start();
	}
	public void startNotificationTimer(){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(database==null){
					database = new LandingPageDatabaseHelper(mUIActivityManager);
					//System.out.println("-------------data base iniit 1 --------");
				}
					
						if(sqlDB==null){
					   sqlDB = database.getWritableDatabase();
					 //  System.out.println("-------------data base iniit 2 --------");
						}
						
				try{
					
					Thread.sleep(1000) ;
				}catch (Exception e) {
					// TODO: handle exception
				}
//				System.out.println("-----------------usernotificationcount : "+ isRegistered());
//		while(isRegistered()){
//			try{
//				
//				if(!Utilities.forgroundApp(mUIActivityManager)){
////						System.out.println("RefreshService---------in extra wating wating startNotificationTimer-------");
//					Thread.sleep(20000) ;
//					continue ;
//				}
//				
//				Thread.sleep(Constants.NOTIFICATIO_REFRESH_INTERVAL) ;
//				Utilities.checkAndStartRefresh(mUIActivityManager);
//				String url = "http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/usernotificationcount?id=" ; 
//				
//				String[] columns = new String[] { LandingPageTable.STORY_ID };
////				Cursor cursor = mUIActivityManager.getContentResolver().query(
////						LandingPageContentProvider.CONTENT_URI, columns,
////						LandingPageTable.TYPE + " = ?", new String[] { "activity" },
////						LandingPageTable.INSERT_TIME+" DESC");//LandingPageTable.STORY_ID + " ASC"   DESC
//				
//				Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(LandingPageTable.TABLE_LANDING_PAGE, columns, LandingPageTable.TYPE + " = ?",
//						 new String[] { "activity" }, null, null,
//						 LandingPageTable.INSERT_TIME+" DESC","2"
//								);
//				
//				if(cursor.getCount()>0){
//					cursor.moveToPosition(0);
//					int dataColumnIndex = cursor.getColumnIndex(LandingPageTable.STORY_ID);
//					url = url + cursor.getString(dataColumnIndex);
//				}else
//					continue;
////				System.out.println("--------------------landingPageBean : activity count url "+cursor.getColumnCount());
////				System.out.println("--------------------landingPageBean : activity count url "+url);
////				HttpConnectionHelper helper = new HttpConnectionHelper(url);
////				InputStream is = null;
////				helper.setReadTimeout(30);
////				helper.setHeader("RT-APP-KEY", HttpHeaderUtils
////						.createRTAppKeyHeader(
////								BusinessProxy.sSelf.getUserId(),
////								SettingData.sSelf.getPassword()));
////				helper.setHeader("RT-DEV-KEY",
////						"" + BusinessProxy.sSelf.getClientId());
//				int responseCode = 4040 ;//helper.getResponseCode();
//				if (responseCode != 200) {
//					continue ;
//				}
////				is = helper.getInputStream();
////				String s = IOUtils.read(is) ;
////				System.out.println("-----------------usernotificationcount : "+ s);
//				
////				mUIActivityManager.runonResponseSucess(responseStr, requestForID) ;
////{"status":"1","count":13,"desc":"You have 13 new stories"}
////				JSONObject myjson = null;
////
////				String desc = null;
////				
////					myjson = new JSONObject(s);
////					if(myjson.has("status"))
////					s = myjson.getString("status");
////					if(s.equalsIgnoreCase("1")){
////						if(myjson.has("count"))
////						s = myjson.getString("count")+Constants.COL_SEPRETOR;
////						if(myjson.has("desc"))
////							s = s + myjson.getString("desc");
////					}
////					Utilities.setString(mUIActivityManager, Constants.NOTIFICATIO_COUNT, s) ;
////					mUIActivityManager.onNotification(Constants.FEED_NOTIFICATION_COUNT);
//						
//			}catch (Exception e) {		
//				e.printStackTrace() ;
//			}
//			catch (OutOfMemoryError e) {		
//				e.printStackTrace() ;
//			}
//		}
//			try{
//				sqlDB.close(); 
//				database.close();
//				database = null;
//				sqlDB = null;
//			}catch (Exception e) {
//				e.printStackTrace() ;
//				
//			}
		}
		}).start();
	}
	
	public boolean isSynchcall(){
		return mTransport.getSyncCall();
	}
	
//	public String getAllAnimTagId(){
//		try{
//		
//		Vector<RtAnimFeed> animVlaue=null;
//		if(BusinessProxy.sSelf.db==null){
//    		BusinessProxy.sSelf.db=new DatabaseHandler(BusinessProxy.sSelf.addPush.mContext);
//    	}
//		
//		StringBuffer buff=new StringBuffer("tagIds##"); 
//		if(BusinessProxy.sSelf.db!=null)
//		 animVlaue = BusinessProxy.sSelf.db.getAllAnimationFeed();  
//	       int iTemp=0;
//	        if(animVlaue!=null){
//	       // BusinessProxy.sSelf.currentAnimData =new parentFeed();
//	        for (RtAnimFeed valueData : animVlaue) {
//	        	if(!valueData.getStatus().equalsIgnoreCase("DELETE")){
//	        	buff.append(valueData.getTagId()); 
//	        	}
//	        	if(iTemp<animVlaue.size()-1){
//	        		if(!valueData.getStatus().equalsIgnoreCase("DELETE")){
//	        	      buff.append(","); 
//	        		}
//	        	 
//	        	}
//	        	  iTemp++;
//	        	  //System.out.println("valueData.getTagId()=="+valueData.getTagId());
//	        	//BusinessProxy.sSelf.currentAnimData.setFeedData(animVlaue);
//	           // String log = "getSetId(): "+valueData.getSetId()+" ,getGifUrl(): " + valueData.getGifUrl() + " ,getAudioUrl(): " + valueData.getAudioUrl();
//	                // Writing Contacts to log
//	               //Log.d("Name: ", log);
//	        
//	        }
//	        } 
//	          //System.out.print("buff.toString()==="+buff.toString());
//		return buff.toString();
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			return null;
//		}
//	}
}
