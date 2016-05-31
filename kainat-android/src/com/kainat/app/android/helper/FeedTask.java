package com.kainat.app.android.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.kainat.app.android.ConversationsActivity;
import com.kainat.app.android.KainatCommunityActivity;
import com.kainat.app.android.R;
import com.kainat.app.android.UIActivityManager;
import com.kainat.app.android.bean.ConversationList;
import com.kainat.app.android.bean.FeaturedUserBean;
import com.kainat.app.android.bean.GroupEventBean;
import com.kainat.app.android.bean.LandingPageBean;
import com.kainat.app.android.bean.LeftMenu;
import com.kainat.app.android.bean.MediaLikeDislike;
import com.kainat.app.android.bean.Message;
import com.kainat.app.android.bean.ParticipantInfo;
import com.kainat.app.android.bean.ShiftNewMessage;
import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.engine.MediaEngine;
import com.kainat.app.android.engine.RefreshService;
import com.kainat.app.android.helper.db.media.MediaContentProvider;
import com.kainat.app.android.inf.HttpSynchInf;
import com.kainat.app.android.uicontrol.CImageView;
import com.kainat.app.android.util.ChannelRefreshInfo;
import com.kainat.app.android.util.CommunityFeed;
import com.kainat.app.android.util.CommunityFeedParser;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.Downloader;
import com.kainat.app.android.util.Feed;
import com.kainat.app.android.util.Feed.Entry;
import com.kainat.app.android.util.CursorToObject;
import com.kainat.app.android.util.FeedRequester;
import com.kainat.app.android.util.HttpHeaderUtils;
import com.kainat.app.android.util.IOUtils;
import com.kainat.app.android.util.ImageDownloader;
import com.kainat.app.android.util.InboxTblObject;
import com.kainat.app.android.util.Logger;
import com.kainat.app.android.util.MyBase64;
import com.kainat.app.android.util.ThumbnailImage;
import com.kainat.app.android.util.ThumbnailReponseHandler;
import com.kainat.app.android.util.Urls;
import com.kainat.app.android.util.Utilities;
import com.kainat.app.android.util.XMLParserAndDataManager;
import com.kainat.app.android.util.XMLUtils;
import com.kainat.app.android.util.format.SettingData;

public class FeedTask extends AsyncTask<String, Void, String> {
	private HttpSynchInf httpSynchInf;

	private static HttpSynchInf httpSynchInfRefresh;
	private static HttpSynchInf httpSynchInfRefreshCureentActivity;
	private static HttpSynchInf httpSynchInfRefreshNotification;
	private static HttpSynchInf httpSynchChannelRefresh;

	public static Vector<ShiftNewMessage> ShiftNewMessageVec = new Vector<ShiftNewMessage>();
	private static final String TAG = FeedTask.class.getSimpleName();
	int requestForID = -1; 
	static Context context;
	int clientType = 1 ;
	public boolean more = false ;
	public boolean dontIncrementUnreadCount = false ;

	public long sortTime = 0 ;

	public boolean hiddenCall = false;
	public boolean requestDone = false;
	public boolean clearAll = false;
	public long clearBelowTImeStamp = -1;
	public boolean pullRefresh = false;// For landing page
	public String conversationID = null;
	public static Feed mediaFeed;

	private static boolean log = false; 
	public static String commnet_parentId = null  ;
	public static boolean updateParticipantInfoUI = false; ;
	public static boolean updateParticipantInfoUISUB = false; ;
	public static boolean refreshList = false; ;
	public static boolean UPDATE_ME = false; ;
	public static boolean KEEP_POSITION = false; ;
	public static boolean showBuzz = false; ;
	public static boolean showStstemMessage = false; ;
	public static String sub = ""; 
	public static String ERROR = "" ;
	public static Message message ;
	public static Message latestMessageCurrentCID ;
	public boolean refreshCancel = false; ;
	public int iRetriveTotalNoOfFeed = -1; 
	
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public FeedTask(Context context){
		this.context=context; 
	}

	public void setRequestId(int requestForID) {
		this.requestForID = requestForID;
		if(log)
			Log.i(TAG, "setRequestID :: requestForID :"+requestForID);
	}
	public void setHttpSynch(HttpSynchInf httpSynchInf) {
		this.httpSynchInf = httpSynchInf;
	}
	public void setHttpSynchRefresh(HttpSynchInf httpSynchInf) {
		this.httpSynchInfRefresh = httpSynchInf;
	}
	public HttpSynchInf getHttpSynch() {
		return this.httpSynchInf;
	}
	public static void setHttpSynchRefreshCurrentView(HttpSynchInf httpSynchInf) {
		httpSynchInfRefreshCureentActivity = httpSynchInf;
	}
	public static void setHttpSynchRefreshNotification(HttpSynchInf httpSynchInf) {
		httpSynchInfRefreshNotification = httpSynchInf;
	}
	public static void setHttpSynchRefreshForChannel(HttpSynchInf httpSynchInf) {
		httpSynchChannelRefresh = httpSynchInf;
	}

	public String getFormatedlink(String s,String id)
	{
		String toReplace = "roctetalk://myactivity?url=" ;
		Pattern pattern = Pattern.compile("<a\\s+href=\"(.+?)\"");
		//		String s = "<a href=\"http://iphone.rocketalk.com/mypage/defaultpage/vishal954\">Vishal</a> posted a media <a href=\"goto:screen/RTAPI/MEDIA?href=http://tejas.rocketalk.com/tejas/feeds/api/message/2800389144,textbox=The_Sword_Of_Tippu\">The_Sword_Of_Tippu</a>" ;
		Pattern p = Pattern.compile("<a\\s+href=\"(.+?)\"", Pattern.MULTILINE);
		Matcher m = p.matcher(s); 
		Vector v = new Vector<String>();
		while(m.find()){
			if(log)
				Log.i(TAG, "getFormatedlink:: "+m.start()+" : "+m.group(1));
			//	    	System.out.println("--------------------getformated link converted : "+m.group(1));
			v.add(m.group(1)) ;
		}
		for (int i = 0; i < v.size(); i++) {
			s = Utilities.replace(s, (String)v.get(i), toReplace+(MyBase64.encode(((String)v.get(i)).getBytes())+Constants.COL_SEPRETOR+id));
		}
		// s = "<a href=\"http://google.com\">Google</a>"+s;
		if(log)
			Log.i(TAG, "getFormatedlink:: getformated link : "+s);
		return s; 
	}
	int totMsg = 0;
	StringBuffer leftmenuData = null;
	private void addLeftMenuLogs(StringBuffer buString, String text)
	{
		buString.append("--");
		buString.append((new Date()).toLocaleString());
		buString.append(text+"\n");
	}
	@Override
	protected String doInBackground(String... arg) 
	{
		iRetriveTotalNoOfFeed=-1;
		totMsg = 0;
		String resp = "OK";
		//commnet_parentId = null ;
		String url = arg[0];
		UPDATE_ME = false;
		if(log)
		{
			if(requestForID == Constants.FEED_GET_MESSAGE)
				Log.i(TAG, "doInBackground:: requestForID = FEED_GET_MESSAGE = "+requestForID+", URL : "+url);
			if(requestForID == Constants.FEED_CONVERSATION_LIST)
				Log.i(TAG, "doInBackground:: requestForID = FEED_CONVERSATION_LIST = "+requestForID);
			else if(requestForID == Constants.FEED_GET_MESSAGE)
				Log.i(TAG, "doInBackground:: requestForID = FEED_GET_MESSAGE = "+requestForID);
			else if(requestForID == Constants.FEED_GET_CHANNEL_REFRESH)
				Log.i(TAG, "doInBackground:: requestForID = FEED_GET_CHANNEL_REFRESH = "+requestForID);
			else if(requestForID == Constants.FEED_GET_CONVERSATION_MESSAGES)
				Log.i(TAG, "doInBackground:: requestForID = FEED_GET_CONVERSATION_MESSAGES = "+requestForID);
			else if(requestForID == Constants.FEED_GET_CONVERSATION_MESSAGES_REFRESH)
				Log.i(TAG, "doInBackground:: requestForID = FEED_GET_CONVERSATION_MESSAGES_REFRESH = "+requestForID);
			else if(requestForID == Constants.FEED_GET_CONVERSATION_MESSAGES2)
				Log.i(TAG, "doInBackground:: requestForID = FEED_GET_CONVERSATION_MESSAGES2 = "+requestForID);
			else
				Log.i(TAG, "doInBackground:: requestForID - "+requestForID);
			
			Log.i(TAG, "doInBackground:: URL to hit : "+url);
			Log.i(TAG, "doInBackground:: ClientType : "+clientType);
		}

		try {		
			HttpClient client = new DefaultHttpClient();
			HttpGet helper = new HttpGet(url);
			int connectionTry = 0;
			int statusCode = 200;
			HttpResponse response=null;
			InputStream is = null;
			if(requestForID != Constants.FEED_INITIAL_LEFT_MENU)
			{
				do{
					if(connectionTry == 1)
					{
						if(log)
							Log.i(TAG, "doInBackground:: connectionTry == 1, Sleep for 2 secs.. ");
						Thread.sleep(2000);
					}
					if (clientType == 0) 
					{
						helper.setHeader("RT-APP-KEY", "" + BusinessProxy.sSelf.getUserId());		
						if(log)
							Log.i(TAG, "doInBackground:: RT-APP-KEY - "+BusinessProxy.sSelf.getUserId());
					} 
					else 
					{
						helper.setHeader("RT-APP-KEY", HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), 
								SettingData.sSelf.getPassword()));
						helper.setHeader("password", HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
						helper.setHeader("authkey","RTAPP@#$%!@");
						if(log)
						{
//							Log.i(TAG, "doInBackground:: RT-APP-KEY  : "+HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));
//							Log.i(TAG, "doInBackground:: Password  : "+HttpHeaderUtils.encriptPass(SettingData.sSelf.getPassword()));
//							Log.i(TAG, "doInBackground:: Authkey : RTAPP@#$%!@");
//							Log.i(TAG, "doInBackground:: Synch-CRT-APP-KEY  : "+HttpHeaderUtils.createRTAppKeyHeader(BusinessProxy.sSelf.getUserId(), SettingData.sSelf.getPassword()));				
						}
					}
					helper.setHeader("RT-DEV-KEY", "" + BusinessProxy.sSelf.getClientId());
					if(log)
						Log.i(TAG, "doInBackground:: RT-DEV-KEY : "+BusinessProxy.sSelf.getClientId());
					response = client.execute(helper);
					StatusLine statusLine = response.getStatusLine();
					statusCode = statusLine.getStatusCode();
					if(log )
					{
						Log.i(TAG, "doInBackground:: Response Code : "+statusCode);
						Log.i(TAG, "doInBackground:: connectionTry : "+connectionTry);
					}
					connectionTry++;
				}while(statusCode !=200 && connectionTry < 2);

			}else
				statusCode = 200 ;//leftmenu reading from local file

			if (statusCode == 200) 
			{		
				if(requestForID != Constants.FEED_INITIAL_LEFT_MENU)
					is = response.getEntity().getContent();
				if("media".hashCode() == requestForID)//Media
				{
					try {
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						XMLReader xr = sp.getXMLReader();
						XMLParserAndDataManager myXMLHandler = new XMLParserAndDataManager();
						xr.setContentHandler(myXMLHandler);
						if(log)
							Log.i(TAG, "doInBackground :: Response Data : "+new String(IOUtils.read(is)));
						ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(IOUtils.read(is).getBytes());
						xr.parse(new InputSource(arrayInputStream));
						if(myXMLHandler.feed!=null&&myXMLHandler.feed.entry.size() > 0)
						{
							mediaFeed = myXMLHandler.feed;
							int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_MEDIA, null, null);					
							if(log)
								Log.i(TAG, "doInBackground :: Delete row count media : "+mRowsUpdated);
							saveMedia(myXMLHandler.feed);
						}
						else
						{
							if(log)
								Log.i(TAG, "doInBackground :: -- feed size is zero -- ");
						}
					}catch (Exception e) {
						e.printStackTrace() ;
					}					
				}
				else if(Constants.FEED_MEDIA_DETAILS == requestForID)//media	
				{
					if(log)
						Log.i(TAG, "doInBackground :: -- Media Details -- ");
					try {
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						XMLReader xr = sp.getXMLReader();
						XMLParserAndDataManager myXMLHandler = new XMLParserAndDataManager();
						xr.setContentHandler(myXMLHandler);
						if(log)
							Log.i(TAG, "doInBackground :: Response Data : "+new String(IOUtils.read(is)));
						ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
								IOUtils.read(is).getBytes());
						xr.parse(new InputSource(arrayInputStream));
						if(myXMLHandler.feed != null&&myXMLHandler.feed.entry.size() > 0)
						{
							httpSynchInf.onResponseSucess(myXMLHandler.feed, requestForID) ;
							httpSynchInf = null;
						}
						else if(myXMLHandler.feed != null && myXMLHandler.feed.errorMsg != null 
								&& myXMLHandler.feed.errorMsg.trim().length() > 0)
						{
							httpSynchInf.onError(myXMLHandler.feed.errorMsg, requestForID);
							httpSynchInf = null;
						}
						else
						{
							if(log)
								Log.i(TAG, "doInBackground :: -- feed size is zero -- ");
						}
					}catch (Exception e) {
						e.printStackTrace() ;
					}					
				}
				else if(Constants.FEED_MEDIA_COMMENT == requestForID)//media	
				{
					if(log)
						Log.i(TAG, "doInBackground :: Discovery-media feed get media comment");
					try 
					{
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						XMLReader xr = sp.getXMLReader();
						XMLParserAndDataManager myXMLHandler = new XMLParserAndDataManager();
						myXMLHandler.setStreemTag("url_type");
						xr.setContentHandler(myXMLHandler);
						if(log)
							Log.i(TAG, "doInBackground :: Response Data : "+new String(IOUtils.read(is)));
						ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
								IOUtils.read(is).getBytes()/*s.getBytes()*/);
						xr.parse(new InputSource(arrayInputStream));
						if(myXMLHandler.feed!=null)
							commnet_parentId = myXMLHandler.feed.object_id ;
						if(log)
							Log.i(TAG, "doInBackground :: Discovery-media feed get media commnet_parentId- : "+commnet_parentId);

						if(myXMLHandler.feed != null && myXMLHandler.feed.entry.size() > 0)
						{
							//mediaFeed = myXMLHandler.feed ;
							int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_MEDIA_COMMENT,
											null, null);					
							if(log)
								Log.i(TAG, "doInBackground :: delete row coutn media comment-----------"+mRowsUpdated);
							saveMediaComment(myXMLHandler.feed)	;
							httpSynchInf.onResponseSucess(myXMLHandler.feed, requestForID) ;
							httpSynchInf = null;
						}
						else{
							if(log)
								Log.i(TAG, "doInBackground :: -- feed size is zero -- ");
							httpSynchInf.onResponseSucess(null, requestForID) ;
							httpSynchInf = null;
						}
					}catch (Exception e) {
						e.printStackTrace() ;
					}					
				}
				else if("community".hashCode() == requestForID)//community
				{
					if(log)
						Log.i(TAG, "doInBackground :: Discovery-community feed get community");
					try {
						if(log)
							Log.i(TAG, "doInBackground :: Response Data : "+new String(IOUtils.read(is)));
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						XMLReader xr = sp.getXMLReader();
						CommunityFeedParser myXMLHandler = new CommunityFeedParser();
						xr.setContentHandler(myXMLHandler);
						ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(IOUtils.read(is).getBytes());
						xr.parse(new InputSource(arrayInputStream));
						if(myXMLHandler.feed != null && myXMLHandler.feed.entry.size() > 0)
						{
							int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_COMMUNITY,
											null, null);
							if(log)
								Log.i(TAG, "doInBackground :: delete row coutn media comment-----------"+mRowsUpdated);
							saveCommunity(myXMLHandler.feed);
						}
						else
						{
							if(log)
								Log.i(TAG, "doInBackground :: -- feed size is zero -- ");
						}
					}catch (Exception e) {
						e.printStackTrace() ;
					}					
				}
				else if("user".hashCode() == requestForID)//user
				{
					if(log)
						Log.i(TAG, "doInBackground :: Response Data : "+new String(IOUtils.read(is)));
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							IOUtils.read(is).getBytes("UTF-8"));
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					//if(Constants.FEED_ACTIVITY==requestForID)
					{
						String totalCount = null ;
						NodeList nodeGeneric = doc.getElementsByTagName("feed");
						String trackingCode = null ;
						String tabTitle = null;
						String next = null ;
						String prev = null ;
						NodeList link = doc.getElementsByTagName("link");
						NodeList tracking = doc.getElementsByTagName("tracking");
						for (int j = 0; j < tracking.getLength(); j++){
							Element e = (Element) tracking.item(j);					
							trackingCode = e.getAttribute("code") ;
						}
						for (int j = 0; j < link.getLength(); j++) {
							Element e = (Element) link.item(j);
							if(e.getAttribute("rel").equalsIgnoreCase("next"))
								next = e.getAttribute("href");
							if(e.getAttribute("rel").equalsIgnoreCase("prev"))
								prev = e.getAttribute("href");					
						}
						if(log)
						{
							Log.i(TAG, "doInBackground :: entry count in feature user list--- : "+trackingCode);
							Log.i(TAG, "doInBackground :: entry count in feature user  next--- : "+next);
							Log.i(TAG, "doInBackground :: entry count in feature user  prev--- : "+prev);
						}
						try
						{
							Element e = (Element) nodeGeneric.item(0);
							totalCount = XMLUtils.getValue(e, "totalCount") ;
						}
						catch (Exception e) {
						}
						if((/*totalCount!=null&&(Integer.parseInt(totalCount)) > 20 || */next == null))
						{
							int mRowsUpdated =
									context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_FEATUREDUSER,
											FeatureUserTable.STORY_ID+"='-1'", null);

							if(log)
								Log.i(TAG, "doInBackground :: Discovery user, remove row Featured User : " + mRowsUpdated);
						}
						NodeList entry = doc.getElementsByTagName("entry");
						if(!more)
						{
							int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_FEATUREDUSER,
											null, null);
							if(log)		
								Log.i(TAG, "doInBackground :: Discovery user, remove row Featured User : " + mRowsUpdated);
						}
						FeaturedUserBean featuredUserBean = null;
						for (int j = 0; j < entry.getLength(); j++) 
						{
							Element e = (Element) entry.item(j);
							featuredUserBean = new FeaturedUserBean() ;
							featuredUserBean.prevUrl =prev ;
							featuredUserBean.nextUrl =next ;
							featuredUserBean.tracking =trackingCode ;
							//featuredUserBean.prevUrl =prev ;
							//featuredUserBean.title = title ;
							featuredUserBean.totalCount = totalCount ;
							featuredUserBean.displayName = XMLUtils.getValue(e, "rt:displayName");
							featuredUserBean.mediaPosts = XMLUtils.getValue(e, "rt:mediaPosts");
							featuredUserBean.mediaFollowers = XMLUtils.getValue(e, "rt:mediaFollowers");
							featuredUserBean.communitiesMember = XMLUtils.getValue(e, "rt:communitiesMember");
							featuredUserBean.thumbUrl = XMLUtils.getValue(e, "rt:thumbUrl");
							featuredUserBean.profileUrl = XMLUtils.getValue(e, "rt:profileUrl");
							featuredUserBean.id = XMLUtils.getValue(e, "rt:profileUrl");
							//featuredUserBean.displayName = XMLUtils.getValue(e, "rt:displayName");
							//featuredUserBean.displayName = XMLUtils.getValue(e, "rt:displayName");		
							if(more)
								featuredUserBean.more = 1 ;
							else
								featuredUserBean.more = 0 ;
							saveFeaturedUser(featuredUserBean);
							if(log)
								Log.i(TAG, "doInBackground :: Discovery user, featuredUserBean :  "+featuredUserBean.toString());
						}
						try{
							if(/*totalCount!=null&&(Integer.parseInt(totalCount)) > 20&&*/next != null)
							{
								//							 int mRowsUpdated =
								//							 context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_FEATUREDUSER,
								//				FeatureUserTable.STORY_ID+"='-1'", null);
								//						 
								//						 if(log)
								//				System.out.println("Feed Synch Discovery user--------------remove row Featured User----------" + mRowsUpdated);
								//				
								if(featuredUserBean==null)
									featuredUserBean = new FeaturedUserBean();
								featuredUserBean.id = "-1" ;
								featuredUserBean.displayName = "loading..." ;
								int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_FEATUREDUSER,
												FeatureUserTable.STORY_ID+"='-1'", null);
								saveFeaturedUser(featuredUserBean);
							}
							else
							{
								int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_FEATUREDUSER,
												FeatureUserTable.STORY_ID+"='-1'", null);
							}
						}catch (Exception e) {
							if(log)
								Log.e(TAG, "doInBackground :: feature user totalCount----"+totalCount);
							e.printStackTrace();
						}	
					}
				}
				else if("groupevent".hashCode() == requestForID)//groupevent
				{	
					if(log)
						Log.i(TAG, "doInBackground :: Response Data : "+new String(IOUtils.read(is)));
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							IOUtils.read(is).getBytes("UTF-8"));
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					//if(Constants.FEED_ACTIVITY==requestForID)
					{
						NodeList nodeGeneric = doc.getElementsByTagName("feed");
						String totalCount = null ;
						String trackingCode = null ;
						String tabTitle = null;
						String next = null ;
						String prev = null ;
						NodeList link = doc.getElementsByTagName("link");
						NodeList tracking = doc.getElementsByTagName("tracking");
						for (int j = 0; j < tracking.getLength(); j++){
							Element e = (Element) tracking.item(j);					
							trackingCode = e.getAttribute("code") ;
						}
						for (int j = 0; j < link.getLength(); j++) {
							Element e = (Element) link.item(j);
							if(e.getAttribute("rel").equalsIgnoreCase("next"))
								next = e.getAttribute("href");
							if(e.getAttribute("rel").equalsIgnoreCase("prev"))
								prev = e.getAttribute("href");					
						}
						if(log)
						{
							Log.i(TAG, "doInBackground :: entry count in feature user list--- : "+trackingCode);
							Log.i(TAG, "doInBackground :: entry count in feature user  next--- : "+next);
							Log.i(TAG, "doInBackground :: entry count in feature user  prev--- : "+prev);
						}
						try
						{
							Element e = (Element) nodeGeneric.item(0);
							totalCount = XMLUtils.getValue(e, "totalCount") ;
							tabTitle = XMLUtils.getValue(e, "title") ;
							//leftMenu2.userProfileUrl = XMLUtils.getValue(e, "userProfileUrl") ;
							//leftMenu2.userDisplayName = XMLUtils.getValue(e, "userDisplayName") ;
							//leftMenu2.userStatus = XMLUtils.getValue(e, "userStatus") ;
						}
						catch (Exception e) 
						{
						}
						if((/*totalCount!=null&&(Integer.parseInt(totalCount)) > 20 || */next == null))
						{
							int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_GROUPEVENT,
											GroupEventTable.ID+"='-1'", null);	    
							if(log)
								Log.i(TAG, "doInBackground :: Discovery user, remove row group event----------" + mRowsUpdated);
						}
						NodeList entry = doc.getElementsByTagName("entry");
						//String tabTitle = null;
						//String title =  XMLUtils.getValue(nodeGeneric.item(0), "title");
						if(!more)
						{
							int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_GROUPEVENT,
											null, null);	    
							if(log)
								Log.i(TAG, "doInBackground :: Discovery user, remove row group event----------" + mRowsUpdated);
						}
						GroupEventBean groupEventBean = null ;
						for (int j = 0; j < entry.getLength(); j++) {
							Element e = (Element) entry.item(j);
							groupEventBean = new GroupEventBean() ;
							//featuredUserBean.title = title ;
							groupEventBean.title = tabTitle;
							groupEventBean.totalCount = totalCount;
							groupEventBean.nextUrl = next;
							groupEventBean.prevUrl = prev;
							groupEventBean.tracking = trackingCode;
							groupEventBean.id = XMLUtils.getValue(e, "id");
							groupEventBean.groupname = XMLUtils.getValue(e, "groupname");
							groupEventBean.comProfileUrl = XMLUtils.getValue(e, "comProfileUrl");
							groupEventBean.hosterUsername = XMLUtils.getValue(e, "hosterUsername");
							groupEventBean.hosterThumbUr = XMLUtils.getValue(e, "hosterThumbUrl");
							groupEventBean.hosterProfileUrl = XMLUtils.getValue(e, "hosterProfileUrl");
							groupEventBean.hosterDisplayName = XMLUtils.getValue(e, "hosterDisplayName");
							groupEventBean.title = XMLUtils.getValue(e, "title");
							groupEventBean.description = XMLUtils.getValue(e, "description");
							groupEventBean.startDate = XMLUtils.getValue(e, "startDate");
							groupEventBean.endDate = XMLUtils.getValue(e, "endDate");
							groupEventBean.pictureUrl = XMLUtils.getValue(e, "pictureUrl");
							groupEventBean.thumbPictureUrl = XMLUtils.getValue(e, "thumbPictureUrl");
							groupEventBean.audioUrl = XMLUtils.getValue(e, "audioUrl");

							groupEventBean.messageInfo1 = XMLUtils.getValue(e, "messageInfo1");
							groupEventBean.messageInfo2 = XMLUtils.getValue(e, "messageInfo2");
							groupEventBean.messageAlert1 = XMLUtils.getValue(e, "messageAlert1");
							groupEventBean.messageAlert2 = XMLUtils.getValue(e, "messageAlert2");
							groupEventBean.button_name = XMLUtils.getValue(e, "button");

							groupEventBean.ownerUsername = XMLUtils.getValue(e, "ownerUsername");
							groupEventBean.ownerThumbUrl = XMLUtils.getValue(e, "ownerThumbUrl");
							groupEventBean.ownerProfileUrl = XMLUtils.getValue(e, "ownerProfileUrl");
							groupEventBean.ownerDisplayName = XMLUtils.getValue(e, "ownerDisplayName");

							//groupEventBean.button_action = e.getAttribute("action");
							try{
								NodeList story = e.getElementsByTagName("button");
								Element e4 = (Element) story.item(0);
								groupEventBean.button_action = e4.getAttribute("action");
							}catch (Exception ee) {
								// TODO: handle exception
							}
							//NodeList ac = entry.getElementsByTagName("tracking");

							/*for (int jj = 0; jj < tracking.getLength(); jj++){
							Element ee = (Element) tracking.item(jj);					
							groupEventBean.button_action = ee.getAttribute("action") ;
						}*/
							groupEventBean.startTime =  XMLUtils.getValue(e, "startTime");
							groupEventBean.endTime =  XMLUtils.getValue(e, "startTime");
							groupEventBean.insertTime = System.currentTimeMillis() ;
							if(more)
								groupEventBean.more = 1 ;
							if(log)
								Log.i(TAG, "doInBackground :: Discovery user group Event Bean : "+groupEventBean.toString());
							saveGroupEvent(groupEventBean) ;						
						}
						try{
							if(next!=null)
							{
								if(groupEventBean==null)
									groupEventBean = new GroupEventBean();
								groupEventBean.id = "-1" ;
								groupEventBean.insertTime = System.currentTimeMillis() ;
								groupEventBean.groupname = "loading..." ;
								saveGroupEvent(groupEventBean);
								if(log)
									Log.i(TAG, "doInBackground ::  Discovery user group Event Bean, next url is available");

							}else{
								int mRowsUpdated = context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_GROUPEVENT,
												GroupEventTable.ID+"=-1", null);	    
								if(log)
									Log.i(TAG, "doInBackground :: Discovery user group Event Bean, next url deleting : "+mRowsUpdated);
							}
						}catch (Exception e) {
							if(log)
								Log.e(TAG, "doInBackground :: ----------------feature user totalCount----"+totalCount);
							e.printStackTrace();
						}
					}				
				}
				else 	if(Constants.FEED_ACTIVITY == requestForID)
				{
					//				String s = IOUtils.read(is) ;
					//			if(log)
					//				System.out.println("Feed Synch Activity feed--url--------------"+url);
					//					System.out.println("Feed Synch Activity feed----------------"+s);

					LandingPageBean landingPageBean = null;
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							IOUtils.read(is).getBytes()/*s.getBytes("UTF-8")*/);
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));

					String trackingCode = null ;
					String tabTitle = null;
					String next = null ;
					String prev = null ;

					NodeList nodeGeneric = doc.getElementsByTagName("feed");
					NodeList link = doc.getElementsByTagName("link");
					NodeList tracking = doc.getElementsByTagName("tracking");

					for (int j = 0; j < tracking.getLength(); j++){
						Element e = (Element) tracking.item(j);					
						trackingCode = e.getAttribute("code") ;
					}
					for (int j = 0; j < link.getLength(); j++) {
						Element e = (Element) link.item(j);
						if(e.getAttribute("rel").equalsIgnoreCase("next"))
							next = e.getAttribute("href");
						if(e.getAttribute("rel").equalsIgnoreCase("prev"))
							prev = e.getAttribute("href");					
					}

					NodeList hasmore = doc.getElementsByTagName("hasMoreContent");

					if(hasmore.getLength()>0)
						next = "true" ;
					else
						next = "false" ;
					//				/<flushData>true or false</flushData>
					Element e = (Element) nodeGeneric.item(0);		
					String flush = XMLUtils.getValue(e,
							"flushData");
					if(log){
						Log.i(TAG, "doInBackground:: activity list should flush--- : "+flush);						
					}
					if(flush!=null && flush.trim().equalsIgnoreCase("true")){

						pullRefresh = false ;
						context. getContentResolver().delete(LandingPageContentProvider.CONTENT_URI,
								null, null);
					}
					if(log){
						Log.i(TAG, "doInBackground:: entry count in activity list--- : "+trackingCode);
						Log.i(TAG, "doInBackground:: entry count in activity next--- : "+next);
						Log.i(TAG, "doInBackground:: entry count in activity prev--- : "+prev);
					}
					for (int j = 0; j < nodeGeneric.getLength(); j++) {
						e = (Element) nodeGeneric.item(j);
						tabTitle = XMLUtils.getValue(e, "title");
						NodeList stories = e.getElementsByTagName("entry");
						int totFeed = stories.getLength() ;
						if(log)
							Log.i(TAG, "doInBackground:: entry count in activity list---"+stories.getLength());
						for (int n = 0; n < stories.getLength(); n++) {
							Element e3 = (Element) stories.item(n);
							//						NodeList story = e3.getElementsByTagName("story");
							//						for (int p = 0; p < story.getLength(); p++) 
							//						{
							landingPageBean = new LandingPageBean();
							landingPageBean.prev_url = prev ;
							landingPageBean.next_url = next ;
							landingPageBean.tracking = trackingCode ;
							NodeList pthumb = e3.getElementsByTagName("pthumb");
							if(log)
								Log.i(TAG, "doInBackground:: pthumb lenght--"+pthumb.getLength());
							for (int ppp = 0; ppp < pthumb.getLength(); ppp++) {
								Element e5e = (Element) pthumb.item(ppp);
								NodeList content = e5e
										.getElementsByTagName("content");
								for (int npn = 0; npn < content.getLength(); npn++) {
									Element e6 = (Element) content.item(npn); //
									String value = XMLUtils.getValue(e6,
											"contentType");
									if (value != null
											&& value.equalsIgnoreCase(LandingPageBean.CONTENT_TYPE_PICTURE)) {
										landingPageBean.userthumb_url = XMLUtils
												.getValue(e6, "contentUrl");
										landingPageBean.userthumb_profile_url = XMLUtils
												.getValue(e6, "clickUrl");
									}
								}
							}

							pthumb = e3.getElementsByTagName("sthumb");
							if(log)
								Log.i(TAG, "doInBackground:: pthumb lenght--"+pthumb.getLength());
							for (int ppp = 0; ppp < pthumb.getLength(); ppp++) {
								Element e5e = (Element) pthumb.item(ppp);
								NodeList content = e5e
										.getElementsByTagName("content");
								for (int npn = 0; npn < content.getLength(); npn++) {
									Element e6 = (Element) content.item(npn); //
									String value = XMLUtils.getValue(e6,
											"contentType");
									if (value != null
											&& value.equalsIgnoreCase(LandingPageBean.CONTENT_TYPE_PICTURE)) {
										landingPageBean.other_user_thumb_url = XMLUtils
												.getValue(e6, "contentUrl");
										landingPageBean.other_user_profile_url = XMLUtils
												.getValue(e6, "clickUrl");
									}
								}
							}


							landingPageBean.story_type = "activity";
							//							Element e4 = (Element) story.item(n);
							String id = XMLUtils
									.getValue(e3, "id");//"1";//e4.getAttribute("id");
							try {
								landingPageBean.story_id = id;

								//										Integer.parseInt(id/*
								//																			 * XMLUtils.
								//																			 * getValue
								//																			 * (e4,
								//																			 * "story-id"
								//																			 * )
								//																			 */);
							} catch (Exception e2) {
								System.out
								.println("--------------------------INVALID ID FOR THIS STORIE----------");
								continue;
							}
							id = XMLUtils
									.getValue(e3, "opType");//"1";//e4.getAttribute("id");
							try {
								landingPageBean.opType = Integer.parseInt(id/*
								 * XMLUtils.
								 * getValue
								 * (e4,
								 * "story-id"
								 * )
								 */);
							} catch (Exception e2) {
								System.out
								.println("--------------------------INVALID ID FOR THIS STORIE----------");
								continue;
							}
							landingPageBean.desc = XMLUtils.getValue(e3, "formattedText");
							landingPageBean.descOri = XMLUtils.getValue(e3, "formattedText");
							landingPageBean.desc = getFormatedlink(landingPageBean.desc,landingPageBean.story_id) ;
							landingPageBean.published = XMLUtils.getValue(e3,
									"date");
							// landingPageBean.story_type = XMLUtils.getValue(e4,
							// "story-type ") ;
							NodeList thumblist = e3
									.getElementsByTagName("contentlist");
							if(log)
								Log.i(TAG, "doInBackground:: Media Text--- : "+ XMLUtils.getValue(e3,
										"mediaText"));
							landingPageBean.mediaText = XMLUtils.getValue(e3,
									"mediaText");	
							if(log)
								Log.i(TAG, "doInBackground:: contentlist size---- : "
										+ thumblist.getLength());
							for (int np = 0; np < thumblist.getLength(); np++) {
								Element e5 = (Element) thumblist.item(np);


								NodeList thumb = e5.getElementsByTagName("content");
								if(log)
									Log.i(TAG, "doInBackground:: content size---- : "
											+ thumb.getLength());
								for (int npn = 0; npn < thumb.getLength(); npn++) {
									Element e5e = (Element) thumb.item(npn);
									//									NodeList content = e5e
									//											.getElementsByTagName("content");
									//									for (int np1n = 0; np1n < content.getLength(); np1n++) 
									{
										//										Element e6 = (Element) content.item(np1n); //
										String value = XMLUtils.getValue(e5e,
												"contentType");
										if (value != null
												&& value.equalsIgnoreCase(LandingPageBean.CONTENT_TYPE_PICTURE)) {
											if(landingPageBean.image_content_id==null)
												landingPageBean.image_content_id = "" ;
											landingPageBean.image_content_id += XMLUtils
													.getValue(e5e, "contentId")+landingPageBean.SEPERATOR;

											if(landingPageBean.image_content_urls==null)
												landingPageBean.image_content_urls = "" ;											
											landingPageBean.image_content_urls += XMLUtils
													.getValue(e5e, "contentUrl")+landingPageBean.SEPERATOR;

											if(landingPageBean.image_content_thumb_urls==null)
												landingPageBean.image_content_thumb_urls = "" ;
											landingPageBean.image_content_thumb_urls += XMLUtils
													.getValue(e5e, "thumbUrl")+landingPageBean.SEPERATOR;

											if(landingPageBean.image_content_click_action==null)
												landingPageBean.image_content_click_action = "" ;
											landingPageBean.image_content_click_action += XMLUtils
													.getValue(e5e, "clickUrl")+landingPageBean.SEPERATOR;
										} else if (value != null
												&& value.equalsIgnoreCase(LandingPageBean.CONTENT_TYPE_VIDEO)) {
											landingPageBean.video_content_id = XMLUtils
													.getValue(e5e, "contentId");
											landingPageBean.video_content_urls = XMLUtils
													.getValue(e5e, "contentUrl");
											landingPageBean.video_content_thumb_urls = XMLUtils
													.getValue(e5e, "thumbUrl");
											landingPageBean.video_content_click_action = XMLUtils
													.getValue(e5e, "contentUrl");
										} else if (value != null
												&& value.equalsIgnoreCase(LandingPageBean.CONTENT_TYPE_AUDIO)) {
											landingPageBean.voice_content_id = XMLUtils
													.getValue(e5e, "contentId");
											landingPageBean.voice_content_urls = XMLUtils
													.getValue(e5e, "contentUrl");
											landingPageBean.voice_content_thumb_urls = XMLUtils
													.getValue(e5e, "thumbUrl");
											landingPageBean.voice_content_click_action = XMLUtils
													.getValue(e5e, "contentUrl");
										}
										else if (value != null
												&& value.equalsIgnoreCase(LandingPageBean.CONTENT_TYPE_TEXT)) {

											landingPageBean.desc2 = XMLUtils
													.getValue(e5e, "contenttext");
										}
									}
								}
							}
							if (landingPageBean != null) {
								if(log)
									Log.i(TAG, "doInBackground:: activity page value : "
											+ landingPageBean.toString());
								if(more && !pullRefresh){
									landingPageBean.direction = 2 ;
								}
								if(!more && pullRefresh){
									landingPageBean.direction = 0 ;
								}
								landingPageBean.insertDate = Utilities.dateToLong(landingPageBean.published);
								saveStory(landingPageBean);
							}
							//						}//empty
						}
						if(httpSynchInf!=null){
							if(more)
								httpSynchInf.onResponseSucess("", requestForID,2,totFeed) ;
							else if(pullRefresh)
								httpSynchInf.onResponseSucess("", requestForID,0,totFeed) ;
							else if(flush!=null && flush.trim().equalsIgnoreCase("true"))
							{
								httpSynchInf.onResponseSucess("", requestForID,0,totFeed) ;
							}
						}

						Cursor cursor = context.getContentResolver().query(
								LandingPageContentProvider.CONTENT_URI, null, null, null, null);
						if(log){
							Log.i(TAG, "doInBackground:: cursor count after saving activity feed ----------"+cursor.getCount());
							Log.i(TAG, "doInBackground:: activity feed ----------"+next);
						}
						if(next.equalsIgnoreCase("true")){
							if(log)
								Log.i(TAG, "doInBackground:: inserting load more ui ----------"+cursor.getCount());

							if(landingPageBean==null)
								landingPageBean = new LandingPageBean();
							landingPageBean.insertDate =  0;//Long.MAX_VALUE;//System.currentTimeMillis() ;
							landingPageBean.story_id = "0" ;
							landingPageBean.direction = 3;
							landingPageBean.desc = "loading..." ;
							saveStory(landingPageBean);
						}else{
							if(more){

								int mRowsUpdated =
										context. getContentResolver().delete(LandingPageContentProvider.CONTENT_URI,
												LandingPageTable.DIRECTION+"="+ 3, null);
								if(log)
									Log.i(TAG, "doInBackground:: delete row count activity feed -----3-----"+mRowsUpdated);
							}
						}
						//add loading view
					}
					arrayInputStream.close();
				}
				else if(Constants.FEED_INITIAL_LEFT_MENU == requestForID)
				{
					//String username = null;
					String s = new String(MediaEngine.loadResToByteArray(R.raw.leftmenu, context)) ;//IOUtils.read(is) ;
//					writeToFile(s);
					if(log)
						Log.i(TAG, "doInBackground :: Feed Synch --------------LEFT MENU-----------"+s);

					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							/*IOUtils.read(is)*/s.getBytes("UTF-8")/*IOUtils.read(is).getBytes()*/);
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					NodeList leftMenu = doc.getElementsByTagName("leftMenu");
					Element e = (Element) leftMenu.item(0);
					LeftMenu leftMenu2 = new LeftMenu() ;
					leftMenu2.isUserDetails = true ;
					//				if(BusinessProxy.testuser)
					//				{
					leftMenu2.userThumbUrl = XMLUtils.getValue(e, "userThumbUrl") ;
					loadLeftProfile(leftMenu2.userThumbUrl);
					leftMenu2.userProfileUrl = XMLUtils.getValue(e, "userProfileUrl") ;
					BusinessProxy.testuser = false;
					//				}
					//				else
					//				{
					//					leftMenu2.userThumbUrl = "http://media.rocketalk.com/get/43_1_5_E_I_I3_ddzd9mbk2q.jpg?height=130&amp;width=130";
					//					loadLeftProfile(leftMenu2.userThumbUrl);
					//					leftMenu2.userProfileUrl = "http://iphone.rocketalk.com/mypage/defaultpage/parampreet1265";
					//				}
					/*if(leftMenu2.userProfileUrl !=null && leftMenu2.userProfileUrl.length() > 0)
				{
					//Test if user information has not changed
					//http://iphone.rocketalk.com/mypage/defaultpage/akmmaurya
					if(leftMenu2.userProfileUrl.lastIndexOf('/') != -1)
						username = leftMenu2.userProfileUrl.substring(leftMenu2.userProfileUrl.lastIndexOf('/') + 1);
				}*/
					leftMenu2.userDisplayName = XMLUtils.getValue(e, "userDisplayName") ;
					leftMenu2.userStatus = XMLUtils.getValue(e, "userStatus") ;

					//				if(BusinessProxy.sSelf.gridMenu == null && username.equalsIgnoreCase(SettingData.sSelf.getUserName()))
					BusinessProxy.sSelf.gridMenu = new Vector<LeftMenu>();

					//				if(BusinessProxy.sSelf.gridMenu != null && username.equalsIgnoreCase(SettingData.sSelf.getUserName()))
					//				{
					BusinessProxy.sSelf.gridMenu = new Vector<LeftMenu>();
					BusinessProxy.sSelf.gridMenu.add(leftMenu2) ;
					//				}
					//				else
					//				{
					//					addLeftMenuLogs(leftmenuData, s);
					//mail to us for wrong user 
					//					System.out.println("Left Menu Data --->> "+leftmenuData.toString());
					//					BusinessProxy.sSelf.sendNewTextMessage("mahesh@rocketalk.com;pushpesh.rajwanshi@onetouchstar.com;tariq@onetouchstar.com;nagendra@onetouchstar.com", leftmenuData.toString(), false);
					//					return null;
					//				}
					if(log){

						Log.i(TAG, "doInBackground:: left menu userThumbUrl---- : "+XMLUtils.getValue(e, "userThumbUrl"));
						Log.i(TAG, "doInBackground:: left menu userProfileUrl---- : "+XMLUtils.getValue(e, "userProfileUrl"));
						Log.i(TAG, "doInBackground:: left menu userDisplayName---- : "+XMLUtils.getValue(e, "userDisplayName"));
						Log.i(TAG, "doInBackground:: left menu userStatus---- : "+XMLUtils.getValue(e, "userStatus"));
					}

					for (int x = 0; x < e.getElementsByTagName("*").getLength(); x++) {
						Element e2 = (Element) e.getElementsByTagName("*").item(x);
						String strValue = e2.getTagName();
						String parentName = e2.getParentNode().getNodeName();
						leftMenu2 = new LeftMenu() ;
						leftMenu2 = new LeftMenu() ;
						if(strValue.trim().equalsIgnoreCase("grid")&&parentName.trim().equalsIgnoreCase("leftMenu")){

							NodeList grow = e.getElementsByTagName("grow");
							for (int np = 0; np < grow.getLength(); np++) {
								leftMenu2 = new LeftMenu() ;
								leftMenu2.isgrid = true ;
								Element	ee = (Element) grow.item(np);
								NodeList entry = ee.getElementsByTagName("entry");

								for (int p = 0; p < entry.getLength(); p++) {
									LeftMenu leftMenuTenp = new LeftMenu() ;
									Element eee = (Element) entry.item(p);
									leftMenuTenp.caption = XMLUtils.getValue(eee, "caption");
									leftMenuTenp.action = XMLUtils.getValue(eee, "action");
									leftMenuTenp.thumb = XMLUtils.getValue(eee, "thumb");
									if(leftMenu2.gridMenu==null)
										leftMenu2.gridMenu = new Vector<LeftMenu>() ;
									leftMenu2.gridMenu.add(leftMenuTenp) ;
								}
								if(BusinessProxy.sSelf.gridMenu!=null/* && username.equalsIgnoreCase(SettingData.sSelf.getUserName())*/)
								{
									BusinessProxy.sSelf.gridMenu.add(leftMenu2) ;
								}
								//						if(leftMenu2!=null)
								//							System.out.println("Feed Synch ------------left menu grid ---- : "+leftMenu2.toString());
							}
						}
						if(strValue.trim().equalsIgnoreCase("entry")&&parentName.trim().equalsIgnoreCase("leftMenu")){					
							leftMenu2 = new LeftMenu() ;
							leftMenu2.caption = XMLUtils.getValue(e2, "caption");
							leftMenu2.action = XMLUtils.getValue(e2, "action");
							leftMenu2.thumb = XMLUtils.getValue(e2, "thumb");
							if(BusinessProxy.sSelf.gridMenu!=null /*&& username.equalsIgnoreCase(SettingData.sSelf.getUserName())*/)
							{
								BusinessProxy.sSelf.gridMenu.add(leftMenu2) ;

							}						
						}					
					}
					if(BusinessProxy.sSelf.gridMenu!=null){
						BusinessProxy.sSelf.gridMenu2 = BusinessProxy.sSelf.gridMenu;
						if(log)
							Log.i(TAG, "doInBackground:: left menu BusinessProxy---- : "+BusinessProxy.sSelf.gridMenu.toString());
					}
					arrayInputStream.close();
					leftmenuData = null;
					//				username = null;
				
				}
				else if(Constants.FEED_CONVERSATION_LIST == requestForID)
				{
					ByteArrayInputStream arrayInputStream = null;
					String s = null;
					if(log)
					{
						Log.i(TAG, "doInBackground :: PARSING----START---FEED_CONVERSATION_LIST");
						Log.i(TAG, "doInBackground :: swaping id--1-conversationList.isLastMessage insert time lastMessageIdTime22--"+(FeedRequester.lastMessageIdTime));
					}
					if(log)
					{
						s = IOUtils.read(is);
						Log.i(TAG, "doInBackground :: PARSING FEED_CONVERSATION_LIST DATA : "+s);
						arrayInputStream = new ByteArrayInputStream(s.getBytes());
					}
					else{
						arrayInputStream = new ByteArrayInputStream(
								/*s.getBytes("UTF-8")*/IOUtils.read(is).getBytes());
					}
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					NodeList hasmore = doc.getElementsByTagName("hasNext");
					boolean hasNext = false ;
					Element e2;
					try{
						e2 = (Element) hasmore.item(0);
						String str =e2.getFirstChild().getNodeValue() ;

						if(str!=null)
							hasNext = Boolean.parseBoolean(str) ;
					}catch (Exception e) {
						// TODO: handle exception
					}
					NodeList nodeGeneric = doc.getElementsByTagName("getConversationsResponse");
					NodeList conversationInfos = doc.getElementsByTagName("conversationInfos");
					//hasNext=true;
					//System.out.println("Feed Synch Discovery user-----conversationInfos.getLength()---------------"+conversationInfos.getLength());
					boolean assigned =false ;
					String mfuid=null;
					ConversationList conversationList=null;
					for (int j = 0; j < conversationInfos.getLength(); j++) 
					{
						conversationList = new ConversationList() ;
						if(hasNext)
							conversationList.isNext = 1 ;
						Element e = (Element) conversationInfos.item(j);
						NodeList stories = e.getElementsByTagName("lastMessage");
						conversationList.conversationId = XMLUtils.getValue(e, "conversationId");
						conversationList.unreadMessageCount = Integer.parseInt(XMLUtils.getValue(e, "unreadMessageCount"));
						String outerPartiInfo = getParticepent(e.getElementsByTagName("participants"), 0);
						try{
							conversationList.isGroup= Integer.parseInt(XMLUtils.getValue(e, "isGroup"));
						}catch (Exception ex) {
							// TODO: handle exception
						}
						String isInactive  = XMLUtils.getValue(e, "isInactive");
						if(isInactive!=null && isInactive.equalsIgnoreCase("1")){
							conversationList.isLeft = 1 ;
						}
						conversationList.subject= XMLUtils.getValue(e, "subject");
						//New change added to support group pic
						conversationList.imageFileId= XMLUtils.getValue(e, "imageFileId");
						//conversationList.subject ="ddd" ;
						NodeList n = e.getElementsByTagName("subject");
						if(n.getLength() > 1)
							conversationList.subject= XMLUtils.getElementValue(n.item(1)).trim();
						//					<firstName>akm</firstName>
						//					<lastName>akm</lastName>
						//					<sender>false</sender>
						//					<userName>akm</userName>
						for (int np = 0; np < stories.getLength(); np++) {
							e2 = (Element) stories.item(np);
							//conversationList.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
							try{
								conversationList.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
							}catch (Exception es2) {
								// TODO: handle exception
							}
							NodeList participants = e2.getElementsByTagName("specialParameter");
							for (int ij = 0; ij < participants.getLength(); ij++) {
								Element eParticipantInfo = (Element) participants.item(ij);
								conversationList.aniType = XMLUtils.getValue(eParticipantInfo, "type");
								conversationList.aniValue = XMLUtils.getValue(eParticipantInfo, "value");
							}
							participants = e2.getElementsByTagName("participants");
							String participantInfoStr = "" ;
							Vector<String> p = new Vector<String>() ;
							String willReplace = "" ;
							boolean willReplaceBool = false;
							boolean im = false;
							for (int i = 0; i < participants.getLength(); i++) {
								Element eParticipants = (Element) participants.item(i);
								NodeList participantInfo = eParticipants.getElementsByTagName("participantInfo");
								for (int ij = 0; ij < participantInfo.getLength(); ij++) {
									Element eParticipantInfo = (Element) participantInfo.item(ij);
									if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
										im = true;
									if((conversationList.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION 
											|| conversationList.messageType==Message.MESSAGE_TYPE_UPDATE_SUBJECT 
											|| conversationList.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION) 
											&& XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
									{
										if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()))
											if(conversationList.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION )
												conversationList.isLeft = 1 ;
										updateParticipantInfoUI = true ;
										continue ;
									}
									if(conversationList.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION && XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) )//&& XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
									{
										conversationList.isLeft = 0 ;
										updateParticipantInfoUI = true ;
									}
									if(p.contains(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase())){
										willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
										willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
										willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
										willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
										//04-25 12:42:18.182: I/System.out(18561): ---------participantInfoStr 2: false§qts§Qts§Sµ
										if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true")){
											try{
												String willReplace2 = willReplace.replaceFirst("true", "false") ;
												//System.out.println("---------participantInfoStr 2willReplace  : "+willReplace);
												//System.out.println("---------participantInfoStr 2willReplace2  : "+willReplace2);
												//System.out.println("---------participantInfoStr 2participantInfoStr  : "+participantInfoStr);
												if(log)
													Log.i(TAG, "doInBackground :: participantInfoStr : "+participantInfoStr.indexOf(willReplace2));
												participantInfoStr = participantInfoStr.toLowerCase() ;
												willReplace2 = willReplace2.toLowerCase() ;
												willReplace = willReplace.toLowerCase() ;
												participantInfoStr = participantInfoStr.replaceFirst(willReplace2, willReplace) ;
											}catch (Exception e3) {

											}
										}
										//willReplaceBool = true ;
										//System.out.println("---------participantInfoStr 2 p participantInfoStr: "+participantInfoStr);
										//System.out.println("---------participantInfoStr 2 p willReplace: "+willReplace);
										continue;
									}
									p.add(XMLUtils.getValue(eParticipantInfo, "userName"));
									participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
									participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
									participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
									participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
									//participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userId")+Constants.COL_SEPRETOR;
									//participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userName")+Constants.COL_SEPRETOR;
									//participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileImageUrl")+Constants.COL_SEPRETOR;
									//participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileUrl")+Constants.ROW_SEPRETOR;
									//participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "sender")+Constants.ROW_SEPRETOR;
								}
							}
							if(participantInfoStr.indexOf("false§§§")!= -1){
								participantInfoStr =participantInfoStr.replaceAll("false§§§", "");
							}
							conversationList.participantInfoStr = participantInfoStr ;
							if(conversationList.isGroup!= 1)
								conversationList.participantInfoStr = outerPartiInfo ;
							String urls ="";
							String turls= "";
							String size = "" ;
							NodeList images = e2.getElementsByTagName("images");
							for (int i = 0; i < images.getLength(); i++) {
								Element rTMediasE = (Element) images.item(i);
								NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

								for (int ij = 0; ij < rTMedias.getLength(); ij++) {
									Element eParticipantInfo = (Element) rTMedias.item(ij);
									urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
									turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
									size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
								}
							}
							if(urls!=null && urls.trim().length()>0){
								conversationList.image_content_thumb_urls = turls ;
								conversationList.image_content_urls = urls;//message.image_content_thumb_urls ;
								conversationList.image_size = urls;//message.image_content_thumb_urls ;
							}
							urls = "";
							turls= "";
							size= "";
							NodeList videos = e2.getElementsByTagName("videos");
							for (int i = 0; i < videos.getLength(); i++) {
								Element rTMediasE = (Element) videos.item(i);
								NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

								for (int ij = 0; ij < rTMedias.getLength(); ij++) {
									Element eParticipantInfo = (Element) rTMedias.item(ij);
									urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
									turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
									size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
								}
							}
							if(urls!=null && urls.trim().length()>0){
								conversationList.video_content_thumb_urls =  turls.substring(0, turls.length()-1);  ;//"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
								conversationList.video_content_urls =  urls.substring(0, urls.length()-1);   ;
								conversationList.video_size =   size.substring(0, size.length()-1);   ;;//message.image_content_thumb_urls ;
							}
							urls =size= "";
							NodeList audios = e2.getElementsByTagName("audios");
							for (int i = 0; i < audios.getLength(); i++) {
								Element rTMediasE = (Element) audios.item(i);
								NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");
								for (int ij = 0; ij < rTMedias.getLength(); ij++) {
									Element eParticipantInfo = (Element) rTMedias.item(ij);
									//								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
									urls =  XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
									size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
								}
							}
							if(urls!=null && urls.trim().length()>0){
								conversationList.voice_content_urls = urls ;
								conversationList.audio_size = size ;
							}
							//drmFlags
							conversationList.clientTransactionId = XMLUtils.getValue(e2, "clientTransactionId");
							conversationList.contentBitMap = XMLUtils.getValue(e2, "contentBitMap");
							//conversationList.conversationId = XMLUtils.getValue(e2, "conversationId");
							conversationList.datetime = XMLUtils.getValue(e2, "datetime");
							conversationList.inserTime = Long.parseLong(XMLUtils.getValue(e2, "datetimeInMillis"));
							conversationList.comments= XMLUtils.getValue(e2, "comments");
							String d =  XMLUtils.getValue(e2, "drmFlags"); 
							if(d!=null && d.trim().length()>0)
								conversationList.drmFlags = Integer.parseInt(d) ;
							d =  XMLUtils.getValue(e2, "notificationFlags"); 
							if(d!=null && d.trim().length()>0)
								conversationList.notificationFlags = Integer.parseInt(d) ;
							conversationList.hasBeenViewed = XMLUtils.getValue(e2, "hasBeenViewed");
							conversationList.hasBeenViewedBySIP = XMLUtils.getValue(e2, "hasBeenViewedBySIP");
							conversationList.messageId = XMLUtils.getValue(e2, "messageId");
							conversationList.mfuId = XMLUtils.getValue(e2, "mfuId");
							if(conversationList.mfuId!=null && conversationList.mfuId.trim().length()>0)
								mfuid= conversationList.mfuId;
							conversationList.messageAttribute = XMLUtils.getValue(e2, "messageAttribute");
							//conversationList.msgOp = XMLUtils.getValue(e2, "msgOp");
							conversationList.msgTxt = XMLUtils.getValue(e2, "msgTxt");

							/*if(conversationList.messageType==conversationList.MESSAGE_TYPE_UPDATE_SUBJECT && im  && conversationList.msgTxt.indexOf("has")!= -1)
							conversationList.msgTxt="You have "+conversationList.msgTxt.substring(conversationList.msgTxt.indexOf("has")+4, conversationList.msgTxt.length());
						if(conversationList.messageType==conversationList.MESSAGE_TYPE_ADD_TO_CONVERSATION && im  && conversationList.msgTxt.indexOf("has")!= -1)
							conversationList.msgTxt="You have "+conversationList.msgTxt.substring(conversationList.msgTxt.indexOf("has")+4, conversationList.msgTxt.length());
						if(conversationList.messageType==conversationList.MESSAGE_TYPE_LEAVE_CONVERSATION && im  && conversationList.msgTxt.indexOf("has")!= -1)
							conversationList.msgTxt="You have "+conversationList.msgTxt.substring(conversationList.msgTxt.indexOf("has")+4, conversationList.msgTxt.length());
							 */

							conversationList.refMessageId = XMLUtils.getValue(e2, "refMessageId");
							conversationList.senderUserId = XMLUtils.getValue(e2, "senderUserId");
							conversationList.source = XMLUtils.getValue(e2, "source");
							conversationList.targetUserId = XMLUtils.getValue(e2, "targetUserId");
							conversationList.usmId= XMLUtils.getValue(e2, "usmId");
							conversationList.isLastMessage= XMLUtils.getValue(e2, "isLastMessage");

							//						System.out.println("---------swaping id--1-conversationList.conversationId--"+conversationList.conversationId);
							//						System.out.println("---------swaping id--1-conversationList.mfuid--"+conversationList.mfuId);
							//						System.out.println("---------swaping id--1-conversationList.isLastMessage--"+conversationList.isLastMessage);
							//						System.out.println("---------swaping id--1-conversationList.isLastMessage insert time--"+(conversationList.inserTime ));
							//						System.out.println("---------swaping id--1-conversationList.isLastMessage insert time lastMessageIdTime--"+(FeedRequester.lastMessageIdTime));
							//						System.out.println("---------swaping id--1-conversationList.isLastMessage insert time--"+(conversationList.inserTime > FeedRequester.lastMessageIdTime));
							if(conversationList.isLastMessage==null || conversationList.isLastMessage.trim().length()<= 0)
								conversationList.isLastMessage="false" ;
							//else

							//						{
							//							System.out.println("---------swaping id---swaping top message id--------4-----"+FeedRequester.lastMessageIdTime);
							//							if(conversationList!=null && conversationList.mfuId!=null && conversationList.inserTime > FeedRequester.lastMessageIdTime
							////									&& conversationList.isLastMessage.equalsIgnoreCase("false")
							//									){
							//								FeedRequester.lastMessageId = conversationList.mfuId ;
							//								FeedRequester.lastMessageIdTime = conversationList.inserTime ;
							//								System.out.println("---------swaping id-----1id :"+conversationList.mfuId);
							//							}
							//						}
							//						Logger.conversationLog("MFUID CONVERSATIONLIST  : ", conversationList.mfuId);

							//if(j==0)
							try{
								if(conversationList.mfuId != null && conversationList.mfuId.trim().length() > 0 && !assigned)
									//						if(conversationList.mfuId!=null && Long.parseLong(FeedRequester.lastMessageId)<Long.parseLong(conversationList.mfuId))// && !RefreshService.CONVERSIOTION_LIST_OLD)
								{
									assigned=true;
									Logger.conversationLog("SAVING TOP MFUID ID IN CONVERSATIONLIST : ", conversationList.mfuId);
									FeedRequester.lastMessageId = conversationList.mfuId ;
									FeedRequester.lastMessageIdTime = conversationList.inserTime ;
									//							Toast.makeText(context, "Top mfuid will be :"+FeedRequester.lastMessageId, Toast.LENGTH_LONG).show() ;
									//							System.out.println("SAVING TOP MFUID ID IN CONVERSATIONLIST-------------"+conversationList.mfuId);
								}

								if(conversationList.mfuId!=null && conversationList.mfuId.trim().length()>0)
								{
									Utilities.setString(context, Constants.CONVERSATION_LIST_MFUID_FOR_MORE, conversationList.mfuId) ;
									if(log)
										Log.i(TAG, "doInBackground :: Discovery user---FeedRequester.lastMessageId-FOR LOAD MORE------------"+conversationList.mfuId);
								}
							}catch (Exception edk) {
								//							Logger.conversationLog("SAVING TOP MFUID ID IN CONVERSATIONLIST ERROR : ", edk.getMessage());
								// TODO: handle exception
								edk.printStackTrace();
							}
							//						System.out.println("Feed Synch Conversiotion list----"+conversationList.toString());
							//						System.out.println("Feed Synch Discovery user---lastMessage-------------"+s3);
							saveConversitionList(conversationList) ;
							//						 System.out.println("Feed Synch Discovery user-----conversationInfos.getLength()-J-2-------------"+j);
						}

						//					lastMessage
						//					if(log)
						//					System.out.println("Feed Synch Discovery user----------------"+featuredUserBean.toString());
					}

					if(!assigned)
					{
//						Toast.makeText(context, "Top mfuid will ben :"+FeedRequester.lastMessageId, Toast.LENGTH_LONG).show() ;
//						Logger.conversationLog("NOT GET TOP MFUID ID IN CONVERSATIONLIST OLD TOP ID IS: ", FeedRequester.lastMessageId);
					}
					if(mfuid!=null)
						Utilities.setString(context, Constants.LAST_MFUID_FOR_CONVERSATION_LIST,mfuid);//, FeedTask.insMoreTime) ;
					if(hasNext && mfuid != null)
					{
						if(conversationList==null)
							conversationList = new ConversationList() ;
						context.getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								MessageTable.MESSAGE_ID + " = '-999' ",
								null);
						conversationList.conversationId="0" ;
						//						conversationList.conversationId="0" ;
						conversationList.messageId="-999" ;
						//						if(mfuid!=null)
						conversationList.mfuId=mfuid ;
						conversationList.msgTxt="LOADING..." ;
						conversationList.drmFlags=0 ;
						conversationList.notificationFlags=0 ;
						conversationList.inserTime=0;//Long.MAX_VALUE ;
						saveConversitionList(conversationList) ;
					}
					else
					{
						context.getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								MessageTable.MESSAGE_ID + " = '-999' ",
								null);
					}
					//					if(log)
					requestDone=true;
					RefreshService.PARSE_REFRESH_CONVERSIOTION_LIST = true;
					//					MediaEngine.getMediaEngineInstance().playResource(R.raw.beep);
					if(log)
						Log.i(TAG, "doInBackground :: PARSING----END---FEED_CONVERSATION_LIST");
					arrayInputStream.close();
				}
				else if(Constants.FEED_GET_CHANNEL_REFRESH == requestForID)
				{
					String s = IOUtils.read(is);
					if(log)
						Log.i(TAG, "doInBackground :: PARSING----START---FEED_GET_CHANNEL_REFRESH - "+s);
					//Parse JSON Data here
					JSONObject jsonObject = new JSONObject(s);
					if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("success"))
					{
						if(jsonObject.has("topMessageId"))
						{
							if(log)
								Log.i(TAG, "doInBackground :: lastChannelRefreshMessageID in Shared Pref : "+FeedRequester.lastChannelRefreshMessageID);
							FeedRequester.lastChannelRefreshMessageID = jsonObject.getString("topMessageId");
							if(log)
								Log.i(TAG, "doInBackground :: FEED_GET_CHANNEL_REFRESH - lastChannelRefreshMessageID"+s);
							//Update sharedPreference for Channel Top Message ID
							if(FeedRequester.lastChannelRefreshMessageID != null)
							{
								Utilities.setString(context, Constants.CH_TOP_ID, FeedRequester.lastChannelRefreshMessageID);
								if(log)
									Log.i(TAG, "doInBackground :: FEED_GET_CHANNEL_REFRESH - updated lastChannelRefreshMessageID in Shared Pref : "
										+FeedRequester.lastChannelRefreshMessageID);
							}
						}
						//Parse JSON Array for channel notification
						if (jsonObject.has("groupnameCountList")) 
						{
							JSONArray jSONArray = jsonObject.getJSONArray("groupnameCountList");
							if(log)
								Log.i(TAG, "doInBackground :: FEED_GET_CHANNEL_REFRESH - Length : "+jSONArray.length());
							BusinessProxy.sSelf.channelRefreshInfo = new ChannelRefreshInfo[jSONArray .length()];
							for (int i = 0; i < jSONArray.length(); i++) 
							{
								BusinessProxy.sSelf.channelRefreshInfo[i] = new ChannelRefreshInfo();
								JSONObject nameObjectw = jSONArray.getJSONObject(i);
								if (nameObjectw.has("groupname")) {
									BusinessProxy.sSelf.channelRefreshInfo[i].setChannelName(nameObjectw.getString("groupname"));
								}
								if (nameObjectw.has("groupId")) {
									BusinessProxy.sSelf.channelRefreshInfo[i].setChannelID(nameObjectw.getString("groupId"));
								}
								if (nameObjectw.has("cnt")) {
									BusinessProxy.sSelf.channelRefreshInfo[i].setNewMessageCount(Integer.parseInt(nameObjectw.getString("cnt")));
								}
							}
						}
						if(BusinessProxy.sSelf.channelRefreshInfo != null && BusinessProxy.sSelf.channelRefreshInfo.length > 0)
							updateCommunityInfo(requestForID, BusinessProxy.sSelf.channelRefreshInfo);
					}
					
				}
				else if(Constants.FEED_GET_MESSAGE == requestForID)
				{
//					BusinessProxy.sSelf.writeLogsToFile(TAG +":doInBackground - FEED_GET_MESSAGE - requestForID - "+requestForID);
					String s = IOUtils.read(is);
					StringBuffer responseMsg = new StringBuffer();
					String senderName = null;
					if(log)
						Log.i(TAG, "doInBackground :: PARSING----START---FEED_GET_MESSAGE - "+s);
					if(refreshCancel){
						return "CANCEL REQUEST";
					}
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(s.getBytes("UTF-8"));
					if(refreshCancel){
						return "CANCEL REQUEST";
					}
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(arrayInputStream));
					if(refreshCancel){
						return "CANCEL REQUEST";
					}
					NodeList hasmore = doc.getElementsByTagName("hasNext");
					boolean hasNext = false ;
					Element e2;
					try{
						e2 = (Element) hasmore.item(0);
						String str =e2.getFirstChild().getNodeValue();
						if(str!=null)
							hasNext = Boolean.parseBoolean(str) ;
					}catch (Exception e) {
						// TODO: handle exception
					}
					NodeList rTMessage = doc.getElementsByTagName("rTMessage");
					Message message=null;
					//if(rTMessage.getLength()>0)
					FeedRequester.latestMessage = new Vector<Message>();
					for (int np = 0; np < rTMessage.getLength(); np++) 
					{
						if(refreshCancel){
							return "CANCEL REQUEST";
						}
						e2 = (Element) rTMessage.item(np);
						message = new Message() ;
						if(hasNext)
							message.isNext = 1 ;
						//						if(XMLUtils.getValue(e2, "messageType")!=null)


						try{
							message.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
						}catch (Exception e) {
							// TODO: handle exception
						}
						message.msgTxt = XMLUtils.getValue(e2, "msgTxt");
						String willReplace = "" ;
						boolean willReplaceBool = false ;
						boolean im = false ;

						NodeList participants = e2.getElementsByTagName("specialParameter");
						for (int ij = 0; ij < participants.getLength(); ij++) {
							Element eParticipantInfo = (Element) participants.item(ij);
							message.aniType = XMLUtils.getValue(eParticipantInfo, "type");
							message.aniValue = XMLUtils.getValue(eParticipantInfo, "value");
						}

						participants = e2.getElementsByTagName("participants");
						String participantInfoStr = "" ;
						if(message.messageType==Message.MESSAGE_TYPE_BUDDY_CAN_NOT_ADD_TO_CONVERSATION){
							updateParticipantInfoUI = true ;
						}
						Vector<String> p = new Vector<String>() ;
						for (int i = 0; i < participants.getLength(); i++) {
							Element eParticipants = (Element) participants.item(i);
							NodeList participantInfo = eParticipants.getElementsByTagName("participantInfo");
							for (int ij = 0; ij < participantInfo.getLength(); ij++) {
								Element eParticipantInfo = (Element) participantInfo.item(ij);
								if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
									im= true;

								if((message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION 
										|| message.messageType==Message.MESSAGE_TYPE_UPDATE_SUBJECT 
										|| message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION)  && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
								{
									if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()))
										if(message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION )
											message.isLeft = 1 ;
									updateParticipantInfoUI = true ;
									continue ;
								}

								if(message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION && XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()))// && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
								{
									message.isLeft = 0 ;
									updateParticipantInfoUI = true ;
								}
								if(p.contains(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase())){
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;

									if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
									{
										String willReplace2 = null;
										try{
											willReplace2 = willReplace.replaceFirst("true", "false");
											if(log)
												Log.i(TAG, "doInBackground:: participantInfo : "+participantInfoStr.indexOf(willReplace2));
											participantInfoStr = participantInfoStr.toLowerCase() ;
											willReplace2 = willReplace2.toLowerCase() ;
											willReplace = willReplace.toLowerCase() ;
											participantInfoStr = participantInfoStr.replaceFirst(willReplace2, willReplace) ;
										}
										catch (Exception e) 
										{
											e.printStackTrace();
										}
									}
										continue;
								}
								//								if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("false"))
								p.add(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase());

								//								System.out.println("---------participantInfoStr 2 p willReplace: "+willReplace);
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
								if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
								{
									senderName = XMLUtils.getValue(eParticipantInfo, "firstName") 
													+ XMLUtils.getValue(eParticipantInfo, "lastName");
								}
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userId")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileImageUrl")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileUrl")+Constants.ROW_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "sender")+Constants.ROW_SEPRETOR;
							}
						}
						if(log)
							Log.i(TAG, "doInBackground :: message text : "+message.msgTxt);
						if(message.messageType == Message.MESSAGE_TYPE_UPDATE_SUBJECT && im && message.msgTxt.indexOf("has")!= -1)
							message.msgTxt="You have "+message.msgTxt.substring(message.msgTxt.indexOf("has")+4, message.msgTxt.length());
						if(message.messageType == Message.MESSAGE_TYPE_ADD_TO_CONVERSATION && im  && message.msgTxt.indexOf("has")!= -1 )
							message.msgTxt="You have "+message.msgTxt.substring(message.msgTxt.indexOf("has")+4, message.msgTxt.length());
						if(message.messageType == Message.MESSAGE_TYPE_LEAVE_CONVERSATION && im  && message.msgTxt.indexOf("has")!= -1)
							message.msgTxt="You have "+message.msgTxt.substring(message.msgTxt.indexOf("has")+4, message.msgTxt.length());

						String urls = "";
						String turls = "";
						String size = "";
						NodeList images = e2.getElementsByTagName("images");
						for (int i = 0; i < images.getLength(); i++) {
							Element rTMediasE = (Element) images.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.image_content_thumb_urls = turls.substring(0, turls.length()-1);  
							message.image_content_urls =  urls.substring(0, urls.length()-1);  ;//message.image_content_thumb_urls ;
							message.image_size =  size.substring(0, size.length()-1);  ;//message.image_content_thumb_urls ;
						}
						urls =turls = size="";
						NodeList videos = e2.getElementsByTagName("videos");
						for (int i = 0; i < videos.getLength(); i++) {
							Element rTMediasE = (Element) videos.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR;; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}

						if(urls!=null && urls.trim().length()>0){
							message.video_content_thumb_urls = turls.substring(0, turls.length()-1);//"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
							message.video_content_urls = urls.substring(0, urls.length()-1); 
							message.video_size = size.substring(0, size.length()-1); 
						}
						urls =turls =size= "";
						NodeList audios = e2.getElementsByTagName("audios");
						for (int i = 0; i < audios.getLength(); i++) {
							Element rTMediasE = (Element) audios.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");
							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								//								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
								urls =  XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.voice_content_urls = urls ;
							message.audio_size = size ;
						}
						if(log)
							Log.i(TAG, "doInBackground :: ParticipantInfoStr : "+participantInfoStr);
						message.participantInfoStr = participantInfoStr ;
						message.clientTransactionId = XMLUtils.getValue(e2, "clientTransactionId");
						message.contentBitMap = XMLUtils.getValue(e2, "contentBitMap");
						message.conversationId = XMLUtils.getValue(e2, "conversationId");
						message.datetime = XMLUtils.getValue(e2, "datetime");
//						message.drmFlags = Integer.parseInt(XMLUtils.getValue(e2, "drmFlags"));
						message.hasBeenViewed = XMLUtils.getValue(e2, "hasBeenViewed");
						message.hasBeenViewedBySIP = XMLUtils.getValue(e2, "hasBeenViewedBySIP");
						message.messageId = XMLUtils.getValue(e2, "messageId");
						message.mfuId = XMLUtils.getValue(e2, "mfuId");
						//						conversationList.msgOp = XMLUtils.getValue(e2, "msgOp");
						message.messageAttribute = XMLUtils.getValue(e2, "messageAttribute");
						message.refMessageId = XMLUtils.getValue(e2, "refMessageId");
						message.senderUserId = XMLUtils.getValue(e2, "senderUserId");
						message.source = XMLUtils.getValue(e2, "source");
						message.targetUserId = XMLUtils.getValue(e2, "targetUserId");
						message.subject = XMLUtils.getValue(e2, "subject");
						message.tag = XMLUtils.getValue(e2, "tag");
						message.inserTime = Long.parseLong(XMLUtils.getValue(e2, "datetimeInMillis"));
						message.comments= XMLUtils.getValue(e2, "comments");
						message.usmId= XMLUtils.getValue(e2, "usmId");
						//						message.cltTxnId= Integer.parseInt(XMLUtils.getValue(e2, "cltTxnId"));

						//Add here for top-notification data - Name, message
						//http://"+Urls.TEJAS_HOST+"/tejas/feeds/user/pic?format=150x150&user=mmmmm68484
						try
						{
							if(responseMsg == null)
								responseMsg = new StringBuffer();
							if(senderName != null)
								responseMsg.append(resp).append("##").append(senderName).append('<').append(message.source).append('>').append("##");
							else
								responseMsg.append(resp).append("##").append(message.source).append('<').append(message.source).append('>').append("##");
							if(message.contentBitMap != null && message.contentBitMap.contains("TEXT"))
								responseMsg.append(message.msgTxt);
							else
								responseMsg.append("Media message");
						}catch(NullPointerException ex)
						{
							ex.printStackTrace();
						}
						
						//Add DRM Value
						String d =  XMLUtils.getValue(e2, "drmFlags"); 
						if(d != null && d.trim().length() > 0)
						{
							message.drmFlags = Integer.parseInt(d);
							responseMsg.append("##").append(message.drmFlags);
						}
						resp = responseMsg.toString();
						senderName = null;
						responseMsg = null;
						
						//Add Notification Value
						d =  XMLUtils.getValue(e2, "notificationFlags"); 
						if(d != null && d.trim().length() > 0)
							message.notificationFlags = Integer.parseInt(d);

						//Check for DRM_SYSTEM_MSG
						if (0 < (message.drmFlags & InboxTblObject.DRM_SYSTEM_MSG)){
							FeedRequester.systemMessage++;
							MediaEngine.getMediaEngineInstance().playResource(R.raw.ring1);
						}
						
						if((message.notificationFlags  & InboxTblObject.NOTI_BUZZ_AUTOPLAY) > 0 
								&& RefreshService.PARSE_REFRESH_REFRESH_FIRST)// && (System.currentTimeMillis()-message.inserTime)<5000)
						{
							this.message = message;
							showBuzz = true;
						}
						
						//Check for DRM_AUTOPLAY
						if((message.drmFlags  & InboxTblObject.DRM_AUTOPLAY) > 0 && 
								(message.drmFlags  & InboxTblObject.DRM_SYSTEM_MSG) > 0 
								&& (System.currentTimeMillis()-message.inserTime) < 5000)
						{
							this.message = message;
							showStstemMessage = true;
						}

						if(!isSystemMessagess(message.drmFlags, message.notificationFlags))
							FeedRequester.latestMessage.add(message);
						if(ConversationsActivity.CONVERSATIONID.equals(message.conversationId)){
							refreshList = true ;
							UPDATE_ME= true ;
							latestMessageCurrentCID = message;
						}
						
						//Save Message to DB
						saveMessage(message);
						
						if(ConversationsActivity.CONVERSATIONID.equals(message.conversationId)){
							refreshList = true;
							UPDATE_ME = true;
							KEEP_POSITION = false;
						}
						if(log){
							Log.i(TAG, "doInBackground:: CONVERSATIONID 11: "+ConversationsActivity.CONVERSATIONID);
							Log.i(TAG, "doInBackground:: CONVERSATIONID 22: "+message.conversationId);
							Log.i(TAG, "doInBackground:: CONVERSATIONID KEEP_POSITION22: "+KEEP_POSITION);
						}
						Utilities.manageDB( context, message.conversationId);

						if(updateParticipantInfoUI || message.messageType == Message.MESSAGE_TYPE_ADD_TO_CONVERSATION
								|| message.messageType == Message.MESSAGE_TYPE_LEAVE_CONVERSATION
								|| message.messageType == Message.MESSAGE_TYPE_UPDATE_SUBJECT)
						{
							refreshList = true ;
							updateParticipantInfoUI=true;
							if(httpSynchInfRefreshCureentActivity!=null)
								httpSynchInfRefreshCureentActivity.onResponseSucess("REFRESH VIEW", requestForID);
						}
					}
					
//					BusinessProxy.sSelf.writeLogsToFile(TAG +":doInBackground - After the for loop - NodeList");

					try{
						if(message != null && message.mfuId != null)// && Long.parseLong(FeedRequester.lastMessageId)<Long.parseLong(message.mfuId))// && !RefreshService.CONVERSIOTION_LIST_OLD)
						{
//							Logger.conversationLog("SAVING TOP MFUID ID IN REFRESH : ", message.mfuId);
							FeedRequester.lastMessageId = message.mfuId ;
							FeedRequester.lastMessageIdTime = message.inserTime ;
							if(log)
								Log.i(TAG, "doInBackground :: Discovery user---FeedRequester.lastMessageId-------------"+FeedRequester.lastMessageId);
						}
					}catch (Exception edk) {
						// TODO: handle exception
						edk.printStackTrace();
					}

					//					lastMessage
					//					if(log)
					//					System.out.println("Feed Synch Discovery user----------------"+featuredUserBean.toString());
					//				}
					RefreshService.PARSE_REFRESH_REFRESH_FIRST = true;
					if(log)
						Log.i(TAG, "doInBackground :: PARSING----END---FEED_GET_MESSAGE");
					//				saveMessage(new Message()) ;
					arrayInputStream.close();
				}
				else if(Constants.FEED_GET_CONVERSATION_MESSAGES == requestForID || Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID)
				{
//					BusinessProxy.sSelf.writeLogsToFile(TAG +":doInBackground - FEED_GET_CONVERSATION_MESSAGES - requestForID - "+requestForID);
					//				is = readRawTextFile(context, R.raw.get_messages) ;
					//				String s = IOUtils.read(is);
					//				if(isCancelled())
					//					return null ;
					//				if(log)
					//					
					////					Thread.sleep(5000);
					//				System.out.println("Feed Synch Discovery user-----FEED_GET_CONVERSATION_MESSAGES---------------"+s);
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							IOUtils.read(is).getBytes()/*s.getBytes("UTF-8")*//*IOUtils.read(is).getBytes()*/);
					if(isCancelled())
						return null ;
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					if(isCancelled())
						return null ;
					NodeList rTMessage = doc.getElementsByTagName("rTMessages");
					NodeList hasmore = doc.getElementsByTagName("hasNext");
					boolean hasNext = false ;
					Element e2;
					try{
						e2 = (Element) hasmore.item(0);
						String str =e2.getFirstChild().getNodeValue() ;

						if(str!=null)
							hasNext = Boolean.parseBoolean(str) ;
					}catch (Exception e) {
						// TODO: handle exception
					}
					if(log)
						Log.i(TAG, "doInBackground :: ----sort time--"+sortTime);
//					BusinessProxy.sSelf.writeLogsToFile(TAG +":doInBackground - FEED_GET_CONVERSATION_MESSAGES - Data read in - arrayInputStream");
					boolean moreOri = more ;
					Message message=null;
					if(rTMessage.getLength()>0 && (clearAll || hasNext) && !more){
						int deletedRows = context.getContentResolver()
								.delete(MediaContentProvider.CONTENT_URI_INBOX,
										MessageTable.CONVERSATION_ID
										+ " = '"+conversationID+"'",
										null);
						more=false;
					}
					if(rTMessage.getLength()>0 && clearBelowTImeStamp>0){
						int deletedRows = context.getContentResolver()
								.delete(MediaContentProvider.CONTENT_URI_INBOX,
										MessageTable.CONVERSATION_ID
										+ " = ? and "+MessageTable.INSERT_TIME +" < "+clearBelowTImeStamp,
										new String[] {conversationID});
						if(log)
							Log.i(TAG, "doInBackground :: ----manageDB--row deletedRows--"+deletedRows);
					}
					if(rTMessage.getLength() > 0 && sortTime > 0){
						//						int deletedRows = context.getContentResolver()
						//								.delete(MediaContentProvider.CONTENT_URI_INBOX,
						//										MessageTable.CONVERSATION_ID
						//												+ " = ? and "+MessageTable.SORT_TIME +" > "+sortTime,
						//										new String[] {conversationID});

						int deletedRows = BusinessProxy.sSelf.sqlDB
								.delete(MessageTable.TABLE,
										MessageTable.CONVERSATION_ID
										+ " = ? and "+MessageTable.SORT_TIME +" > "+sortTime,
										new String[] {conversationID});

						if(log)
							Log.i(TAG, "doInBackground :: ----manageDB--row deletedRows--"+deletedRows);

						//						Logger.conversationLog("FeedRequester remove sorttime greater than  tot row removed : ", ""+deletedRows);

					}
					iRetriveTotalNoOfFeed = rTMessage.getLength() ;
					for (int np = 0; np < rTMessage.getLength(); np++) {
						if(isCancelled())
							return null ;

						e2 = (Element) rTMessage.item(np);
						message = new Message() ;
						if(hasNext){
							message.isNext = 1 ;
							iRetriveTotalNoOfFeed=50;
						}
						//						if(hasNext){
						//							int deletedRows = context.getContentResolver()
						//									.delete(MediaContentProvider.CONTENT_URI_INBOX,
						//											MessageTable.CONVERSATION_ID
						//											+ " = '"+conversationID+"'",
						//											null);
						//							
						//						}
						//						
						NodeList participants = e2.getElementsByTagName("specialParameter");
						for (int ij = 0; ij < participants.getLength(); ij++) {
							Element eParticipantInfo = (Element) participants.item(ij);
							message.aniType = XMLUtils.getValue(eParticipantInfo, "type");
							message.aniValue = XMLUtils.getValue(eParticipantInfo, "value");
						}

						participants = e2.getElementsByTagName("participants");
						//						if(XMLUtils.getValue(e2, "messageType")!=null)
						try{
							message.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
						}catch (Exception e) {
							// TODO: handle exception
						}
						//						message.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
						String participantInfoStr = "" ;
						String willReplace = "" ;
						boolean willReplaceBool = false ;
						boolean im = false ;
						Vector<String> p = new Vector<String>() ;
						for (int i = 0; i < participants.getLength(); i++) {
							Element eParticipants = (Element) participants.item(i);
							NodeList participantInfo = eParticipants.getElementsByTagName("participantInfo");

							for (int ij = 0; ij < participantInfo.getLength(); ij++) {
								Element eParticipantInfo = (Element) participantInfo.item(ij);

								if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
									im= true;
								//
								if((message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION 
										|| message.messageType==Message.MESSAGE_TYPE_UPDATE_SUBJECT 
										|| message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION)  && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
								{
									if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toLowerCase()))
										if(message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION )
											message.isLeft = 1 ;
									updateParticipantInfoUI = true ;
									continue ;
								}

								if(message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION && XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()))// && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
								{
									message.isLeft = 0 ;
									updateParticipantInfoUI = true ;
								}
								//								System.out.println("---------participantInfoStr 2 p : "+p.toString());

								if(p.contains(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase())){
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
									willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
									//									04-25 12:42:18.182: I/System.out(18561): ---------participantInfoStr 2: false§qts§Qts§Sµ

									if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true")){
										try{
											String willReplace2 = willReplace.replaceFirst("true", "false") ;
											//										System.out.println("---------participantInfoStr 2willReplace  : "+willReplace);
											//										System.out.println("---------participantInfoStr 2willReplace2  : "+willReplace2);
											//										System.out.println("---------participantInfoStr 2participantInfoStr  : "+participantInfoStr);
											if(log)
												Log.i(TAG, "doInBackground :: participantInfoStr : "+participantInfoStr.indexOf(willReplace2));

											participantInfoStr = participantInfoStr.toLowerCase() ;
											willReplace2 = willReplace2.toLowerCase() ;
											willReplace = willReplace.toLowerCase() ;

											participantInfoStr = participantInfoStr.replaceFirst(willReplace2, willReplace) ;
										}catch (Exception e) {
											// TODO: handle exception
										}
									}
									//	willReplaceBool = true ;

									//									System.out.println("---------participantInfoStr 2 p participantInfoStr: "+participantInfoStr);
									//									System.out.println("---------participantInfoStr 2 p willReplace: "+willReplace);

									continue;
								}
								//								if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("false"))
								p.add(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase());

								//								System.out.println("---------participantInfoStr 2 p willReplace: "+willReplace);
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userId")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileImageUrl")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileUrl")+Constants.ROW_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "sender")+Constants.ROW_SEPRETOR;
							}
						}
						//						for (int i = 0; i < participants.getLength(); i++) {
						//							Element eParticipants = (Element) participants.item(i);
						//							NodeList participantInfo = eParticipants.getElementsByTagName("participantInfo");
						//							
						//							for (int ij = 0; ij < participantInfo.getLength(); ij++) {
						//								Element eParticipantInfo = (Element) participantInfo.item(ij);
						//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
						//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
						//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
						//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
						////								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userId")+Constants.COL_SEPRETOR;
						////								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userName")+Constants.COL_SEPRETOR;
						////								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileImageUrl")+Constants.COL_SEPRETOR;
						////								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileUrl")+Constants.ROW_SEPRETOR;
						////								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "sender")+Constants.ROW_SEPRETOR;
						//							}
						//						}
						String urls = "";
						String turls = "";
						String size = "";
						NodeList images = e2.getElementsByTagName("images");
						for (int i = 0; i < images.getLength(); i++) {
							Element rTMediasE = (Element) images.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.image_content_thumb_urls = turls.substring(0, turls.length()-1);  
							message.image_content_urls =  urls.substring(0, urls.length()-1);  ;//message.image_content_thumb_urls ;
							message.image_size =  size.substring(0, size.length()-1);  ;//message.image_content_thumb_urls ;
						}
						urls =turls = size="";
						NodeList videos = e2.getElementsByTagName("videos");
						for (int i = 0; i < videos.getLength(); i++) {
							Element rTMediasE = (Element) videos.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR;; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}

						if(urls!=null && urls.trim().length()>0){
							message.video_content_thumb_urls = turls.substring(0, turls.length()-1);//"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
							message.video_content_urls = urls.substring(0, urls.length()-1); 
							message.video_size = size.substring(0, size.length()-1); 
						}
						urls =turls =size= "";
						NodeList audios = e2.getElementsByTagName("audios");
						for (int i = 0; i < audios.getLength(); i++) {
							Element rTMediasE = (Element) audios.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");
							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								//								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
								urls =  XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.voice_content_urls = urls ;
							message.audio_size = size ;
						}
						//						System.out.println("---------participantInfoStr 1: "+participantInfoStr);
						message.participantInfoStr = participantInfoStr ;

						message.clientTransactionId = XMLUtils.getValue(e2, "clientTransactionId");
						message.contentBitMap = XMLUtils.getValue(e2, "contentBitMap");
						message.conversationId = XMLUtils.getValue(e2, "conversationId");
						message.datetime = XMLUtils.getValue(e2, "datetime");
						String d =  XMLUtils.getValue(e2, "drmFlags"); 

						if(d!=null && d.trim().length()>0)
							message.drmFlags = Integer.parseInt(d) ;
						d =  XMLUtils.getValue(e2, "notificationFlags"); 
						if(d!=null && d.trim().length()>0)
							message.notificationFlags = Integer.parseInt(d) ;

						message.hasBeenViewed = XMLUtils.getValue(e2, "hasBeenViewed");
						message.hasBeenViewedBySIP = XMLUtils.getValue(e2, "hasBeenViewedBySIP");
						message.messageId = XMLUtils.getValue(e2, "messageId");
						message.mfuId = XMLUtils.getValue(e2, "mfuId");
						//						conversationList.msgOp = XMLUtils.getValue(e2, "msgOp");
						message.msgTxt = XMLUtils.getValue(e2, "msgTxt");
						if(message.messageType == Message.MESSAGE_TYPE_UPDATE_SUBJECT && im  && message.msgTxt.indexOf("has")!= -1 )
							message.msgTxt="You have "+message.msgTxt.substring(message.msgTxt.indexOf("has")+4, message.msgTxt.length());
						if(message.messageType == Message.MESSAGE_TYPE_ADD_TO_CONVERSATION && im  && message.msgTxt.indexOf("has")!= -1)
							message.msgTxt="You have "+message.msgTxt.substring(message.msgTxt.indexOf("has")+4, message.msgTxt.length());
						if(message.messageType == Message.MESSAGE_TYPE_LEAVE_CONVERSATION&& im  && message.msgTxt.indexOf("has")!= -1)
							message.msgTxt="You have "+message.msgTxt.substring(message.msgTxt.indexOf("has")+4, message.msgTxt.length());
						message.messageAttribute = XMLUtils.getValue(e2, "messageAttribute");
						message.refMessageId = XMLUtils.getValue(e2, "refMessageId");
						message.senderUserId = XMLUtils.getValue(e2, "senderUserId");
						message.source = XMLUtils.getValue(e2, "source");
						message.targetUserId = XMLUtils.getValue(e2, "targetUserId");
						message.subject = XMLUtils.getValue(e2, "subject");
						message.tag = XMLUtils.getValue(e2, "tag");
						message.inserTime = Long.parseLong(XMLUtils.getValue(e2, "datetimeInMillis"));
						message.comments= XMLUtils.getValue(e2, "comments");
						message.usmId= XMLUtils.getValue(e2, "usmId");

						saveMessage(message) ;

						if(ConversationsActivity.CONVERSATIONID.equals(message.conversationId)){
							refreshList = true ;
							UPDATE_ME= true ;
							KEEP_POSITION=false;
							if(moreOri){
								UPDATE_ME =false ;
								KEEP_POSITION=true;
							}

						}
						if(log)
						{
							Log.i(TAG, "doInBackground :: ---------CONVERSATIONID 1: "+ConversationsActivity.CONVERSATIONID);
							Log.i(TAG, "doInBackground :: CONVERSATIONID 2: "+message.conversationId);
							Log.i(TAG, "doInBackground :: CONVERSATIONID KEEP_POSITION: "+KEEP_POSITION);
						}
						if(hasNext){
							context.getContentResolver().delete(
									MediaContentProvider.CONTENT_URI_INBOX,
									MessageTable.MESSAGE_ID + " = '-999' ",
									null);
							message.messageId="-999" ;
							message.mfuId="-999" ;
							message.inserTime=0;//Long.MAX_VALUE ;
							saveMessage(message) ;

							refreshList = true ;
						}else{
							context.getContentResolver().delete(
									MediaContentProvider.CONTENT_URI_INBOX,
									MessageTable.MESSAGE_ID + " = '-999' ",
									null);
						}

						//						if(updateParticipantInfoUI){
						//							refreshList = true ;
						//							if(httpSynchInfRefreshCureentActivity!=null)
						//								httpSynchInfRefreshCureentActivity.onResponseSucess("REFRESH VIEW", requestForID);
						//						}
						//						System.out.println("Feed Synch Discovery user---lastMessage-------------"+s3);
					}
//					BusinessProxy.sSelf.writeLogsToFile(TAG +":doInBackground - FEED_GET_CONVERSATION_MESSAGES - After for loop....");

					//					FeedTask.ShiftNewMessageVec.add(new ShiftNewMessage(FeedTask.insMoreTime, message.conversationId)) ;

					try{
						Vector<ShiftNewMessage> toBeRemovedShiftNewMessageVec = new Vector<ShiftNewMessage>() ;
						for (int i = 0; i < ShiftNewMessageVec.size(); i++) {
							if(ShiftNewMessageVec.get(i).cid.equalsIgnoreCase(message.conversationId)){
								toBeRemovedShiftNewMessageVec.add(ShiftNewMessageVec.get(i));
								ContentValues values = new ContentValues() ;
								values.put(MessageTable.SORT_TIME, insMoreTime++) ;

								int updatedRow = context.getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX,
										values,MessageTable.SORT_TIME+" ="+ShiftNewMessageVec.get(i).timeId,null);
								//						Logger.conversationLog("shifting------------message.conversationId :",message.conversationId);
//								System.out.println("-------------updatedRow ShiftNewMessage-----"+updatedRow+" :insMoreTime : "+insMoreTime);	
							}
						}
						for (int i = 0; i < toBeRemovedShiftNewMessageVec.size(); i++) {
							ShiftNewMessageVec.remove(toBeRemovedShiftNewMessageVec.get(i)) ;
						}
					}catch (Exception e) {
						// TODO: handle exception
					}
					refreshList = true ;
					//					lastMessage
					//					if(log)
					//					System.out.println("Feed Synch Discovery user----------------"+featuredUserBean.toString());
					//				}
					requestDone=true;
					//				saveMessage(new Message()) ;
					arrayInputStream.close();
				}
				else if(Constants.FEED_GET_BOOKMARK_MESSAGES == requestForID || Constants.FEED_GET_BOOKMARK_MESSAGES_MORE == requestForID){
					//				is = readRawTextFile(context, R.raw.get_messages) ;
					if(isCancelled())
						return null ;
					//				String s = IOUtils.read(is);
					//				if(isCancelled())
					//					return null ;
					//				if(s.indexOf("<errorCode>")!= -1)
					//					ERROR = "Something wrong in bookmark feed "+s ;
					//				if(log)
					//				System.out.println("Feed Synch Discovery user-----FEED_GET_BOOKMARK_MESSAGES---------------"+s);
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							/*s.getBytes("UTF-8")*/IOUtils.read(is).getBytes());
					if(isCancelled())
						return null ;
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					if(isCancelled())
						return null ;
					NodeList rTMessage = doc.getElementsByTagName("rTBookmarkMessage");

					NodeList hasmore = doc.getElementsByTagName("hasNext");
					boolean hasNext = false ;
					Element e2;
					try{
						e2 = (Element) hasmore.item(0);
						String str =e2.getFirstChild().getNodeValue() ;

						if(str!=null)
							hasNext = Boolean.parseBoolean(str) ;
					}catch (Exception e) {
						// TODO: handle exception
					}
					//				if(e2.getFirstChild().getNodeValue().trim().equalsIgnoreCase("true"))
					//					hasNext= true ;
					//				if(log)
					//				System.out.println("--------hasNext bookmark------ : "+hasNext+" : "+str);

					Message message=null;
					if(rTMessage.getLength()>0 && !more){
						Utilities.cleanBookmark(context) ;
					}
					if(isCancelled())
						return null ;
					for (int np = 0; np < rTMessage.getLength(); np++) {
						if(isCancelled())
							return null ;
						e2 = (Element) rTMessage.item(np);
						message = new Message() ;
						if(hasNext)
							message.isNext = 1 ;
						String source = null ;
						NodeList participants = e2.getElementsByTagName("specialParameter");
						for (int ij = 0; ij < participants.getLength(); ij++) {
							Element eParticipantInfo = (Element) participants.item(ij);
							message.aniType = XMLUtils.getValue(eParticipantInfo, "type");
							message.aniValue = XMLUtils.getValue(eParticipantInfo, "value");
						}
						participants = e2.getElementsByTagName("participants");
						String participantInfoStr = "" ;
						for (int i = 0; i < participants.getLength(); i++) {
							Element eParticipants = (Element) participants.item(i);
							NodeList participantInfo = eParticipants.getElementsByTagName("participantInfo");

							for (int ij = 0; ij < participantInfo.getLength(); ij++) {
								Element eParticipantInfo = (Element) participantInfo.item(ij);

								if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true")){
									source = XMLUtils.getValue(eParticipantInfo, "firstName")+" "+
											XMLUtils.getValue(eParticipantInfo, "userName")+" "+"<"+
											XMLUtils.getValue(eParticipantInfo, "lastName")+">";
								}
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userId")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userName")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileImageUrl")+Constants.COL_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileUrl")+Constants.ROW_SEPRETOR;
								//								participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "sender")+Constants.ROW_SEPRETOR;
							}
						}
						String urls = "";
						String turls = "";
						String size = "";
						NodeList images = e2.getElementsByTagName("images");
						for (int i = 0; i < images.getLength(); i++) {
							Element rTMediasE = (Element) images.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.image_content_thumb_urls =  turls.substring(0, turls.length()-1);   ;
							message.image_content_urls =  urls.substring(0, urls.length()-1);  ;//message.image_content_thumb_urls ;
						}
						urls = "";
						turls = "";
						size = "";
						images = e2.getElementsByTagName("images");
						for (int i = 0; i < images.getLength(); i++) {
							Element rTMediasE = (Element) images.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
								size =  turls + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.image_content_thumb_urls = turls.substring(0, turls.length()-1);  
							message.image_content_urls =  urls.substring(0, urls.length()-1);  ;//message.image_content_thumb_urls ;
							message.image_size =  size.substring(0, size.length()-1);  ;//message.image_content_thumb_urls ;
						}
						urls =turls = size="";
						NodeList videos = e2.getElementsByTagName("videos");
						for (int i = 0; i < videos.getLength(); i++) {
							Element rTMediasE = (Element) videos.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");

							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl")+Constants.COL_SEPRETOR;; 
								turls =  turls + XMLUtils.getValue(eParticipantInfo, "thumbnailUrl")+Constants.COL_SEPRETOR; 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}

						if(urls!=null && urls.trim().length()>0){
							message.video_content_thumb_urls = turls.substring(0, turls.length()-1);//"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
							message.video_content_urls = urls.substring(0, urls.length()-1); 
							message.video_size = size.substring(0, size.length()-1); 
						}
						urls =turls =size= "";
						NodeList audios = e2.getElementsByTagName("audios");
						for (int i = 0; i < audios.getLength(); i++) {
							Element rTMediasE = (Element) audios.item(i);
							NodeList rTMedias = rTMediasE.getElementsByTagName("rTMedias");
							for (int ij = 0; ij < rTMedias.getLength(); ij++) {
								Element eParticipantInfo = (Element) rTMedias.item(ij);
								//								urls =  urls + XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
								urls =  XMLUtils.getValue(eParticipantInfo, "mediaUrl"); 
								size =  size + XMLUtils.getValue(eParticipantInfo, "mediaSize")+Constants.COL_SEPRETOR; 
							}
						}
						if(urls!=null && urls.trim().length()>0){
							message.voice_content_urls = urls ;
							message.audio_size = size ;
						}
						//						System.out.println("---------participantInfoStr 1: "+participantInfoStr);
						message.participantInfoStr = participantInfoStr ;

						message.clientTransactionId = XMLUtils.getValue(e2, "clientTransactionId");
						message.contentBitMap = XMLUtils.getValue(e2, "contentBitMap");
						message.conversationId = XMLUtils.getValue(e2, "conversationId");
						message.datetime = XMLUtils.getValue(e2, "datetime");
						//						conversationList.drmFlags = XMLUtils.getValue(e2, "drmFlags");
						message.hasBeenViewed = XMLUtils.getValue(e2, "hasBeenViewed");
						message.hasBeenViewedBySIP = XMLUtils.getValue(e2, "hasBeenViewedBySIP");
						message.messageId = XMLUtils.getValue(e2, "messageId");
						message.mfuId = XMLUtils.getValue(e2, "mfuId");
						//						conversationList.msgOp = XMLUtils.getValue(e2, "msgOp");
						message.msgTxt = XMLUtils.getValue(e2, "msgTxt");
						message.refMessageId = XMLUtils.getValue(e2, "refMessageId");
						message.senderUserId = XMLUtils.getValue(e2, "senderUserId");

						if(source==null)
							message.source = XMLUtils.getValue(e2, "source");
						else
							message.source = source;

						message.targetUserId = XMLUtils.getValue(e2, "targetUserId");
						message.inserTime = Long.parseLong(XMLUtils.getValue(e2, "datetimeInMillis"));
						message.comments= XMLUtils.getValue(e2, "comments");
						message.subject = XMLUtils.getValue(e2, "subject");
						message.tag = XMLUtils.getValue(e2, "tag");
						message.messageAttribute = XMLUtils.getValue(e2, "messageAttribute");
						//						message.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
						try{
							message.messageType= Integer.parseInt(XMLUtils.getValue(e2, "messageType"));
						}catch (Exception e) {
							// TODO: handle exception
						}
						message.usmId= XMLUtils.getValue(e2, "bookmarkId");
						//						message.cltTxnId= Integer.parseInt(XMLUtils.getValue(e2, "cltTxnId"));
						//						System.out.println("Feed Synch message list----"+message.toString());

						//						message.image_content_thumb_urls = "http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash3/572860_100000665125941_1173252510_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-prn1/572658_100000431658511_1470386684_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash4/211623_1250359870_2139397439_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-sphotos-g-a.akamaihd.net/hphotos-ak-ash4/381821_469242199814603_1765457441_n.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-ash4/373297_58762883453_691832530_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/276675_240755355970062_1295775176_q.jpg"+Constants.COL_SEPRETOR+
						//								"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/274160_100001570279923_1957553652_q.jpg" ;
						//						message.image_content_urls = message.image_content_thumb_urls ;
						//						message.video_content_thumb_urls = "http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
						//						message.video_content_urls = "http://124.153.95.131/tejas/video/resource/repository/2011/01/31/10/3959016690211624960.mp4";//"" ;
						saveBookmarkMessage(message) ;
						//						System.out.println("Feed Synch Discovery user---lastMessage-------------"+s3);
					}
					if(hasNext){
						context.getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_BOOKMARK,
								MessageTable.MESSAGE_ID + " = '-999' ",
								null);
						message.messageId="-999" ;
						message.mfuId="-999" ;
						message.inserTime=0;//Long.MAX_VALUE ;
						saveBookmarkMessage(message) ;
					}else{
						context.getContentResolver().delete(
								MediaContentProvider.CONTENT_URI_BOOKMARK,
								MessageTable.MESSAGE_ID + " = '-999' ",
								null);
					}
					//					lastMessage
					//					if(log)
					//					System.out.println("Feed Synch Discovery user----------------"+featuredUserBean.toString());
					//				}

					//				saveMessage(new Message()) ;
					arrayInputStream.close();
				}
				else if(Constants.FEED_GET_LIKE_USE==requestForID){
					if(log)
						System.out.println("-------------------PARSING----START---FEED_GET_MESSAGE");
					//					is = readRawTextFile(context, R.raw.like) ;
					//					String s = IOUtils.read(is);
					//					if(s.indexOf("<errorCode>")!= -1)
					//						ERROR = "Something wrong in Refresh "+s ;
					////					if(log)
					//					System.out.println("Feed Synch Discovery user-----FEED_GET_LIKE_USE---------------"+s);
					if(refreshCancel){
						return "CANCEL REQUEST";
					}
					ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
							IOUtils.read(is).getBytes()/*s.getBytes("UTF-8")/*IOUtils.read(is).getBytes()*/);
					if(refreshCancel){
						return "CANCEL REQUEST";
					}
					Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
							arrayInputStream));
					if(refreshCancel){
						return "CANCEL REQUEST";
					}
					NodeList nodeGeneric = doc.getElementsByTagName("feed");
					Element ele = (Element) nodeGeneric.item(0);
					String baseUrl =  XMLUtils.getValue(ele, "baseUrl") ;
					String	nextUrl =  XMLUtils.getValue(ele, "nextUrl") ;
					String	prevUrl =  XMLUtils.getValue(ele, "prevUrl") ;
					NodeList rTMessage = doc.getElementsByTagName("entry");
					Message message = null;
					MediaLikeDislike mediaLikeDislike = new MediaLikeDislike() ;
					FeedRequester.latestMessage = new Vector<Message>();
					for (int np = 0; np < rTMessage.getLength(); np++) {
						if(refreshCancel){
							return "CANCEL REQUEST";
						}
						Element e2 = (Element) rTMessage.item(np);
						mediaLikeDislike = new MediaLikeDislike() ;
						try{
							mediaLikeDislike.userId= XMLUtils.getValue(e2, "userId");
							mediaLikeDislike.userName= XMLUtils.getValue(e2, "userName");
							mediaLikeDislike.userThumbUrl= XMLUtils.getValue(e2, "userThumbUrl");
							mediaLikeDislike.userDisplayName= XMLUtils.getValue(e2, "userDisplayName");
							mediaLikeDislike.userThumbUrl= baseUrl+XMLUtils.getValue(e2, "userThumbUrl");
							//								mediaLikeDislike.profileUrl= XMLUtils.getValue(e2, "profileUrl");
							mediaLikeDislike.timestamp= Long.parseLong(XMLUtils.getValue(e2, "opDateTimeInMillis"));
							mediaLikeDislike.nextUrl= nextUrl;
							mediaLikeDislike.prevUrl= prevUrl;

						}catch (Exception e) {
							// TODO: handle exception
						}

						saveMediaUserLike(mediaLikeDislike);
						if(log)
							Log.i(TAG, "doInBackground :: mediaLikeDislike : "+mediaLikeDislike.toString());
					}
					if(nextUrl!=null && nextUrl.trim().length()>0){
						mediaLikeDislike.userId ="-1" ;
						mediaLikeDislike.nextUrl= nextUrl;
						mediaLikeDislike.prevUrl= prevUrl;
						saveMediaUserLike(mediaLikeDislike);
					}
				}
			}else{
				RefreshService.PARSE_REFRESH_CONVERSIOTION_LIST = true ;
				//				MediaEngine.getMediaEngineInstance().playResource(R.raw.beep);
				return "error on creation connection status code is : "+statusCode;
			}

		} catch (ClientProtocolException e) {
			//			if(log)
			Log.e(TAG, "doInBackground :: Discovery user---------ERROR 1");
			e.printStackTrace();
			return  e.getMessage();
		} catch (IOException e) {
			//			if(log)
			Log.e(TAG, "doInBackground :: Discovery user---------ERROR 2");
			e.printStackTrace();
			return e.getMessage();
		}
		catch (Exception e) {
			//			if(log)
			Log.e(TAG, "doInBackground :: Discovery user---------ERROR 3");
			e.printStackTrace();
			return e.getMessage();
		}
		catch (OutOfMemoryError e) {
//			Logger.conversationLog("FeedTask dobackground ",e.getMessage());
			//			if(log)
			Log.e(TAG, "doInBackground :: Discovery user---------ERROR 4");
			e.printStackTrace();
			return e.getMessage();
		}
//		BusinessProxy.sSelf.writeLogsToFile(TAG +":Exit doInBackground");
		return resp;
	}
	
	public void updateInsTime()
	{
		try{
			ContentValues values = new ContentValues() ;
			values.put(SettingTable.INSMORE_TIME, insMoreTime) ;
			int updatedRow = context.getContentResolver().update(MediaContentProvider.CONTENT_URI_SETTING,
					values,null,null);
			if(log)
				Log.i(TAG, "updateInsTime :: updatedRow-----"+updatedRow+" :insMoreTime : "+insMoreTime);	
		}
		catch (SQLiteException e) 
		{
			e.printStackTrace();
//			Logger.conversationLog("FeedTask updateInsTime : ", e.getMessage());
		}
	}
	@Override
	protected void onPostExecute(String result) 
	{
		updateInsTime();
		String[] res = null;
//		BusinessProxy.sSelf.writeLogsToFile(TAG +":Entry onPostExecute - result - "+result);
		//Formatted Result - OK##mahesh##How are you
		if(result.contains("##"))
		{
			if(log)
				Log.i(TAG, "onPostExecute :: result - "+result);
			res = result.split("##");
			if(res.length > 1)
				result = res[0];
		}
		if(result != null && !result.equalsIgnoreCase("OK"))
		{

			if(httpSynchInfRefreshCureentActivity!=null && totMsg > 0 && Constants.FEED_GET_CONVERSATION_MESSAGES2!=requestForID)
				httpSynchInfRefreshCureentActivity.onError(result, requestForID);

			if(httpSynchInfRefreshCureentActivity!=null && Constants.FEED_GET_CONVERSATION_MESSAGES2==requestForID)
				httpSynchInfRefreshCureentActivity.onError(result, requestForID);

			if(httpSynchInfRefreshNotification!=null && totMsg > 0)
				httpSynchInfRefreshNotification.onError(result, requestForID);
			if(httpSynchInfRefreshNotification!=null)
				httpSynchInfRefreshNotification.onError(result, requestForID);

		}
		if(log)
			Log.i(TAG, "onPostExecute :: Feed TASK iscancel---------"+isCancelled()+" : "+requestForID);
		
		if(httpSynchInfRefreshCureentActivity != null && Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID)
		{
			httpSynchInfRefreshCureentActivity.onResponseSucess(Constants.CHECKING_DONE, requestForID);
			return ;
		}
		if(Constants.FEED_CONVERSATION_LIST == requestForID)
		{
			//RefreshService.PARSE_REFRESH_CONVERSIOTION_LIST=true ;
			//MediaEngine.getMediaEngineInstance().playResource(R.raw.beep);
			if(log)
				Log.i(TAG, "onPostExecute :: --------insMoreTime- onpost"+insMoreTime);
		}
		if(log)
			Log.i(TAG, "onPostExecute :: Feed Synch onPostExecute---------"+result);
		
		if(result!=null && !result.equalsIgnoreCase("OK"))
		{
			if(httpSynchInf!=null)
				httpSynchInf.onError(result, requestForID);
			if(Constants.FEED_GET_MESSAGE == requestForID || Constants.FEED_CONVERSATION_LIST == requestForID)
			{
				if(httpSynchInfRefresh!=null)
					httpSynchInfRefresh.onError(result, requestForID);
			}
			if(httpSynchInfRefreshCureentActivity != null && totMsg > 0 && Constants.FEED_GET_CONVERSATION_MESSAGES2 != requestForID)
				httpSynchInfRefreshCureentActivity.onError(result, requestForID);

			if(httpSynchInfRefreshCureentActivity != null && Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID)
				httpSynchInfRefreshCureentActivity.onError(result, requestForID);

			if(httpSynchInfRefreshNotification != null && totMsg > 0)
				httpSynchInfRefreshNotification.onError(result, requestForID);
			if(httpSynchInfRefreshNotification != null)
				httpSynchInfRefreshNotification.onError(result, requestForID);
		}
		else
		{
			if(httpSynchInf!=null)
				httpSynchInf.onResponseSucess(null, requestForID) ;
			if(Constants.FEED_GET_MESSAGE == requestForID  || Constants.FEED_CONVERSATION_LIST == requestForID)
			{
				//This calls stops the service and starts again after refresh time :)
				if(httpSynchInfRefresh != null)
					httpSynchInfRefresh.onResponseSucess(null, requestForID) ;
			}
		}
		if(httpSynchInfRefreshCureentActivity != null && totMsg > 0 && Constants.FEED_GET_CONVERSATION_MESSAGES2 != requestForID)
		{
			//This call is to refresh the conversation list and shuffle depending upon recent messages
			httpSynchInfRefreshCureentActivity.onResponseSucess("REFRESH VIEW", requestForID);
		}

		if(httpSynchInfRefreshCureentActivity != null && Constants.FEED_GET_MESSAGE == requestForID)
		{
			//This call Will go to respective chat thread.
			httpSynchInfRefreshCureentActivity.onResponseSucess(Constants.CHECKING_DONE, requestForID);
		}
		if(httpSynchInfRefreshCureentActivity != null && Constants.FEED_GET_CONVERSATION_MESSAGES2 == requestForID)
		{
			//This call Will go to respective chat thread.
			httpSynchInfRefreshCureentActivity.onResponseSucess(Constants.CHECKING_DONE, requestForID);
		}
		if(httpSynchInfRefreshNotification != null && totMsg > 0)
		{
			//This call is to show the notification on the UI for new messages
			if(res != null && res.length == 4 && res[3] != null && res[3].length() > 0){
				if(res[2].startsWith("{\"") && res[2].contains("\"deleteMessageAPI\":"))
					httpSynchInfRefreshNotification.onNotification(Constants.CHANNEL_REPORT_MESSAGE, res[1], res[2]);
				else
					httpSynchInfRefreshNotification.onNotification(Constants.CHANNEL_YOU_BECOME_MEMBER, res[1], res[2]);
			}
			else if(res != null && res[1] != null && res[1].length() > 0){
				httpSynchInfRefreshNotification.onNotification(requestForID, res[1], res[2]);
				MediaEngine.getMediaEngineInstance().playResource(R.raw.ring1);
			}
			else
				httpSynchInfRefreshNotification.onNotification(requestForID, null, null);
			
		}
		if(httpSynchInfRefreshNotification != null)
		{
			//This call is show the notification on the UI, when user is in chat thread
			httpSynchInfRefreshNotification.onNotificationThreadInbox(requestForID, "", "");
		}
	}

	public void saveMedia(Feed feed)
	{
		try{
			if(log)
				Log.i(TAG, "saveMedia :: SAVING MEDIA FEED ");
			long sTime = System.currentTimeMillis();
			if(feed.entry.size() > 0 )
			{
				if(log)
					Log.i(TAG, "saveMedia :: Feed Count : " +feed.entry.size());
			}
			for (int j = 0; j < feed.entry.size(); j++) 
			{
				Entry entry = feed.entry.get(j) ;
				ContentValues values = new ContentValues();
				values.put(MediaTable.PARENT_ID, 0);
				values.put(MediaTable.STORY_ID, entry.id);
				values.put(MediaTable.TRACKING, "TRACKING");//
				values.put(MediaTable.SUE, "entry.sue");//
				values.put(MediaTable.TITLE, entry.title);
				values.put(MediaTable.TYPE, "0");//
				values.put(MediaTable.PUBLISHED, entry.published);//
				String link3 = null;
				int n=0;
				for (n = 0; n < entry.category.size(); n++) {
					if(link3 == null)
						link3="" ;
					String type = Feed.getAttrScheme(entry.category.elementAt(n));
					link3 += type +Constants.COL_SEPRETOR;
					type = Feed.getAttrTerm(entry.category.elementAt(n));
					link3 += type +Constants.COL_SEPRETOR;
					type = Feed.getAttrLabel(entry.category.elementAt(n));
					link3 += type +Constants.ROW_SEPRETOR;
					if(log)
						Log.i(TAG, "saveMedia :: Entry category : "+link3);

				}
				if(log)
					Log.i(TAG, "saveMedia :: category : "+link3);
				values.put(MediaTable.CATEGORY, link3);//
				link3 = null;
				for ( n = 0; n < entry.link.size(); n++) 
				{
					if(link3 == null)
						link3="" ;
					String type = Feed.getAttrRel(entry.link.elementAt(n));
					link3 += type +Constants.COL_SEPRETOR;
					type = Feed.getAttrType(entry.link.elementAt(n));
					link3 += type +Constants.COL_SEPRETOR;
					type = Feed.getAttrHref(entry.link.elementAt(n));
					link3 += type +Constants.ROW_SEPRETOR;
					if(log)
						Log.i(TAG, "saveMedia :: Entry link other : "+link3);
				}
				values.put(MediaTable.LINK_OTHER, link3);//
				link3 = null;
//				if(log)
//					System.out.println();
				for (int np = 0; np < entry.media.size(); np+=4) {
					link3 += entry.media.get(np) +Constants.COL_SEPRETOR;
					link3 += entry.media.get(np+1) +Constants.COL_SEPRETOR;
					link3 += entry.media.get(np+2) +Constants.COL_SEPRETOR;
					link3 += entry.media.get(np+3) +Constants.ROW_SEPRETOR;				
					if(log)
						Log.i(TAG, "saveMedia :: Entry link media : "+link3);
				}
				values.put(MediaTable.LINK_MEDIA, link3);//			
				values.put(MediaTable.AUTHOR_FIRSTNAME, entry.author.firstName);
				values.put(MediaTable.AUTHOR_LASTNAME, entry.author.lastName);
				values.put(MediaTable.AUTHOR_NAME, entry.author.name);
				values.put(MediaTable.AUTHOR_URI, entry.author.uri);
				if(log)
					Log.i(TAG, "saveMedia :: author uri: "+ entry.author.uri);
				values.put(MediaTable.COMMENT_COUNT, entry.comment_count);
				values.put(MediaTable.FBURL, entry.fburl);
				values.put(MediaTable.MEDIASTATSURL, entry.mediastatsurl);
				values.put(MediaTable.COMMENT_URL, entry.comment_url);
				values.put(MediaTable.MEDAICOUNT, Feed.getAttrViewcount(entry.rt_statistics));
				values.put(MediaTable.VOTE_UP_COUNT, Feed.getAttrmMedaicount(entry.rt_statistics));		
				values.put(MediaTable.VOTE_UP_COUNT, Feed.getAttrVoteupcount(entry.rt_voting));		
				values.put(MediaTable.VOTE_DOWN_COUNT, Feed.getAttrVotedowncount(entry.rt_voting));
				values.put(MediaTable.CONTENT_VIEW, Feed.getAttrViewcount(entry.rt_statistics));	

				values.put(MediaTable.CONTENT_PICTURE, Feed.getAttrSrc(entry.content));
				values.put(MediaTable.SUMMARY, entry.summary);
				values.put(MediaTable.LOCATION, entry.location);
				values.put(MediaTable.INSERT_DATE, ""+(new Date()).getTime());	

				int mRowsUpdated =
						context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_MEDIA,
								MediaTable.STORY_ID+"='"+ entry.id+"'", null);
				if(log)	 
					Log.i(TAG, "saveMedia :: --------------media delete row count media----------"+mRowsUpdated);			
				context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_MEDIA,values);			
				//			Cursor cursor = context.getContentResolver().query(
				//					MediaContentProvider.CONTENT_URI_MEDIA, null, null, null, null);
				//			if(log)
				//			System.out
				//					.println("Feed Synch--------------media cursor count----------" + cursor.getCount());			
			}

			long diff = System.currentTimeMillis() - sTime;
			if(log)
				Log.i(TAG, "saveMedia :: ----------time kaken to save media in data base : " + (diff) + " Ms");
		}catch (Exception e) {
//			System.out.println("Feed Synch ERROR in SAVING MEDIA"+e.getMessage());
			e.printStackTrace();
		}
	}
	public void saveMediaComment(Feed feed){
		try{
			if(log){
				Log.i(TAG, "saveMediaComment :: SAVING MEDIA COMMENT FEED####################### feed.object_id : "+feed.object_id);
			}
			long sTime = System.currentTimeMillis();

			if(feed.entry.size() > 0 )
			{
				if(log)
					Log.i(TAG, "saveMediaComment :: Feed Count : " +feed.entry.size());
			}
			for (int j = 0; j < feed.entry.size(); j++) {
				Entry entry = feed.entry.get(j) ;

				ContentValues values = new ContentValues();
				values.put(CommentTable.PARENT_ID, feed.object_id);
				values.put(CommentTable.STORY_ID, entry.id);
				values.put(CommentTable.TRACKING, "TRACKING");//
				values.put(CommentTable.SUE, "entry.sue");//
				values.put(CommentTable.TEXT, entry.text);
				//			values.put(CommentTable.TYPE, "0");//
				values.put(CommentTable.PUBLISHED, entry.published);//
				values.put(CommentTable.USERNAME,entry.username);//
				values.put(CommentTable.FIRSTNAME,  entry.firstname);//
				values.put(CommentTable.LASTNAME, entry.lastname);//
				if(log){
					Log.i(TAG, "saveMediaComment :: CommentTable.FIRSTNAME : "+entry.lastname + ", CommentTable.LASTNAME : "+entry.username);
				}
				values.put(CommentTable.VID_THUMB_URL, entry.video_url);//
				values.put(CommentTable.VID_URL, entry.video_url);//
				values.put(CommentTable.LIKES, entry.likes);//
				values.put(CommentTable.DISLIKES, entry.dislikes);//	
				values.put(CommentTable.LIKES_VALUE, entry.like_value);//	
				values.put(CommentTable.PREV_URL, feed.prevurl);//
				values.put(CommentTable.NEXT_URL, feed.nexturl);//
				values.put(CommentTable.TOTAL_COMMENT, feed.comment_count);//
				values.put(CommentTable.INSERT_TIME, Utilities.dateToLong(entry.published));

				if(log){
					Log.i(TAG, "saveMediaComment :: comment insertion time: "+Utilities.dateToLong(entry.published));
				}

				String link3 = null;
				int n=0;

				link3 = null;
				for ( n = 0; n < entry.link.size(); n++) {
					String type = Feed.getAttrRel(entry.link.elementAt(n));
					link3 += type +Constants.COL_SEPRETOR;
					type = Feed.getAttrType(entry.link.elementAt(n));
					link3 += type +Constants.COL_SEPRETOR;
					type = Feed.getAttrHref(entry.link.elementAt(n));
					link3 += type +Constants.ROW_SEPRETOR;
					if(log)
						Log.i(TAG, "saveMediaComment :: Entry media comment link other : "+link3);
				}
				values.put(CommentTable.LINK_OTHER, link3);//
				link3 = "";
				if(log)
					Log.i(TAG, "saveMediaComment :: Entry media comment media link other : "+entry.media.toString());
				for (int np = 0; np < entry.media.size(); np+=4) {
					link3 += entry.media.get(np) +Constants.COL_SEPRETOR;
					link3 += entry.media.get(np+1) +Constants.COL_SEPRETOR;
					link3 += entry.media.get(np+2) +Constants.COL_SEPRETOR;
					link3 += entry.media.get(np+3) +Constants.ROW_SEPRETOR;				
					if(log)
						Log.i(TAG, "saveMediaComment :: Entry link comment media : "+link3);
				}
				values.put(CommentTable.LINK_MEDIA, link3);//			
				int mRowsUpdated =
						context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_MEDIA_COMMENT,
								MediaTable.STORY_ID+"='"+ entry.id+"'", null);
				if(log)	 
					Log.i(TAG, "saveMediaComment :: media comment delete row count media----------"+mRowsUpdated);			
				context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_MEDIA_COMMENT,
						values);			
				//			Cursor cursor = context.getContentResolver().query(
				//					MediaContentProvider.CONTENT_URI_MEDIA_COMMENT, null, null, null, null);
				//			if(log)
				//			System.out
				//					.println("Feed Synch--------------media comment cursor count----------" + cursor.getCount());			
			}
			long diff = System.currentTimeMillis() - sTime;
			if(log)
				Log.i(TAG, "saveMediaComment :: time kaken to save media comment in data base : "
						+ (diff) + " Ms");
		}catch (Exception e) {
//			System.out.println("Feed Synch ERROR in SAVING MEDIA COMMENT"+e.getMessage());
			e.printStackTrace();
		}
	}

//	public static void saveCommunity(CommunityFeed feed){				//COMMUNITY
//		try{
//			if(log)
//				Log.i(TAG, "saveCommunity :: SAVING MEDIA COMMUNITY feed count : "+feed.entry.size());
//			long sTime = System.currentTimeMillis();
//
//			for (int j = 0; j < feed.entry.size(); j++) {
//				CommunityFeed.Entry entry = feed.entry.get(j) ;
//				ContentValues values = new ContentValues();
//				//			values.put(CommunityTable.COLUMN_ID, 0);
//				values.put(CommunityTable.PARENT_ID, entry.groupId);
//				values.put(CommunityTable.GROUPID, "TRACKING");//
//				values.put(CommunityTable.TRACKING, "trackingg");//
//				values.put(CommunityTable.SUE, "sue");
//				values.put(CommunityTable.TYPE, "0");//
//				values.put(CommunityTable.GROUP_NAME, entry.groupName);//
//				values.put(CommunityTable.CATEGORY, entry.category);//
//				values.put(CommunityTable.DESCRIPTION, entry.description);//
//				values.put(CommunityTable.IS_MODERATED, entry.moderated);//
//				values.put(CommunityTable.AUTOA_CCEPTUSER, entry.autoAcceptUser);//
//				values.put(CommunityTable.IS_PUBLIC, entry.publicCommunity);//
//				values.put(CommunityTable.LAST_UPDATED, entry.lastUpdated);//
//				values.put(CommunityTable.TIME_OF_CREATION, entry.createdOn);//
//				values.put(CommunityTable.NUMBER_OF_MEMBERS, entry.noOfMember);//
//				values.put(CommunityTable.NUMBER_OF_MESSAGES, entry.noOfMessage);//
//				values.put(CommunityTable.OWNERUSER_ID, entry.ownerId);//
//				values.put(CommunityTable.WELCOME_MSG_ID, entry.welcomeMsgId);//
//				values.put(CommunityTable.MSG_ID, entry.messageId);//
//				values.put(CommunityTable.VENDOR_NAME, entry.vendorName);//
//				values.put(CommunityTable.NUMBEROF_ONLINE_MEMBERS, entry.noOfOnlineMember);//
//				values.put(CommunityTable.COMM_ON_FRIENDS_COUNT, entry.commonFriendCount);//
//				values.put(CommunityTable.ACTIVE_MEMBERS, entry.activeMember);//
//				values.put(CommunityTable.MEMBER_PERMISSION, entry.memberPermission);//
//				values.put(CommunityTable.IS_ADMIN, entry.admin);//		
//
//				try{
//					if(entry.media != null && entry.media.size() > 0)
//						for (int np = 0; np < entry.media.size(); np+=4) {
//							//				link3 += entry.media.get(np) +Constants.COL_SEPRETOR;
//							//				link3 += entry.media.get(np+1) +Constants.COL_SEPRETOR;
//							//				link3 += entry.media.get(np+2) +Constants.COL_SEPRETOR;
//							//				link3 += entry.media.get(np+3) +Constants.ROW_SEPRETOR;		
//
//							entry.thumbUrl= (String)entry.media.get(np+2);
//
//						}
//				}catch (Exception e) {
//					// TODO: handle exception
//				}
//
//				values.put(CommunityTable.THUMB_URL, entry.thumbUrl);//
//				values.put(CommunityTable.MESSAGE_URL, entry.messageUrl);//
//				values.put(CommunityTable.PROFILE_URL, entry.profileUrl);//			
//				values.put(CommunityTable.SEARCH_MEMBER_URL, entry.searchMemberUrl);//
//				values.put(CommunityTable.NEXT_URL, "not maped");//
//				values.put(CommunityTable.PREV_URL, "not maped");//
//
//				values.put(CommunityTable.OWNER_DISPLAY_NAME, entry.ownerDisplayName);//
//				values.put(CommunityTable.OWNER_PROFILE_URL, entry.ownerProfileUrl);//
//				values.put(CommunityTable.OWNER_THUMB_URL, entry.ownerThumbUrl);//
//				values.put(CommunityTable.OWNER_USERNAME, entry.ownerUsername);//
//				String link3;
//				int n=0;		
//				//context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_COMMUNITY,	CommunityTable.GROUPID+"='"+ entry.groupId+"'", null);
//				//			if(log)	 
//				//			System.out.println("Feed Synch--------------community delete row count media----------"+mRowsUpdated);			
//				context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_COMMUNITY, values);			
//				//			Cursor cursor = context.getContentResolver().query(
//				//					MediaContentProvider.CONTENT_URI_COMMUNITY, null, null, null, null);
//				//			if(log)
//				//			System.out
//				//					.println("Feed Synch--------------community cursor count----------" + cursor.getCount());	
//			}
//			long diff = System.currentTimeMillis() - sTime;
//			if(log)
//				Log.i(TAG, "saveCommunity :: time kaken to save community in data base : "+ (diff) + " Ms");
//		}catch (Exception e) {
////			if(log)
////				System.out.println("Feed Synch ERROR in SAVING COMMUNITY---- "+e.getMessage());
//			e.printStackTrace();
//		}
//	}
	public static void deleteAll(){
		int rowsDeleted = BusinessProxy.sSelf.sqlDB.delete(CommunityTable.TABLE, null, null);
        Log.e("saveCommunity :: Community Table ", "deleted number of rows : "+rowsDeleted);
	}
	
	public static void saveCommunity(CommunityFeed feed)
	{    //COMMUNITY
		  try{
			   if(log)
			    Log.i(TAG, "saveCommunity :: SAVING MEDIA COMMUNITY feed count : "+feed.entry.size());
			   
			   //Delete all the rows, only if the user has 0 channels, else update 
			   if(Utilities.getBoolean(context, Constants.NO_CHANNLE_JOINED))
			   {
				   int rowsDeleted = BusinessProxy.sSelf.sqlDB.delete(CommunityTable.TABLE, null, null);
		            Log.e("saveCommunity :: Community Table ", "deleted number of rows : "+rowsDeleted);
		            Log.e("saveCommunity :: Community Table ", "Going to insert no of rows : "+feed.entry.size());
		            
		          //Save value in Shared pref, that user has now joined some channel.
		            Log.i(TAG, "parseXMLData :: NO_CHANNLE_JOINED == false");
					Utilities.setBoolean(context, Constants.NO_CHANNLE_JOINED, false);
			   }
	
			   for (int j = feed.entry.size(); j > 0; j--) 
			   {
				    CommunityFeed.Entry entry = feed.entry.get(j - 1) ;
				    ContentValues values = new ContentValues();
				    //   values.put(CommunityTable.COLUMN_ID, 0);
				    values.put(CommunityTable.PARENT_ID, entry.groupId);
				    values.put(CommunityTable.GROUPID, entry.groupId);//
				    values.put(CommunityTable.TRACKING, "trackingg");//
				    values.put(CommunityTable.SUE, "sue");
				    values.put(CommunityTable.TYPE, "0");//
				    values.put(CommunityTable.GROUP_NAME, entry.groupName);//
				    values.put(CommunityTable.CATEGORY, entry.category);//
				    values.put(CommunityTable.DESCRIPTION, entry.description);//
				    values.put(CommunityTable.IS_MODERATED, entry.moderated);//
				    values.put(CommunityTable.AUTOA_CCEPTUSER, entry.autoAcceptUser);//
				    values.put(CommunityTable.IS_PUBLIC, entry.publicCommunity);//
				    values.put(CommunityTable.LAST_UPDATED, entry.lastUpdated);//
				    values.put(CommunityTable.TIME_OF_CREATION, entry.createdOn);//
				    values.put(CommunityTable.NUMBER_OF_MEMBERS, entry.noOfMember);//
				    values.put(CommunityTable.NUMBER_OF_MESSAGES, entry.noOfMessage);//
				    values.put(CommunityTable.OWNERUSER_ID, entry.ownerId);//
				    values.put(CommunityTable.WELCOME_MSG_ID, entry.welcomeMsgId);//
				    values.put(CommunityTable.MSG_ID, entry.messageId);//
				    values.put(CommunityTable.VENDOR_NAME, entry.vendorName);//
				    values.put(CommunityTable.NUMBEROF_ONLINE_MEMBERS, entry.noOfOnlineMember);//
				    values.put(CommunityTable.COMM_ON_FRIENDS_COUNT, entry.commonFriendCount);//
				    values.put(CommunityTable.ACTIVE_MEMBERS, entry.activeMember);//
				    values.put(CommunityTable.MEMBER_PERMISSION, entry.memberPermission);//
				    values.put(CommunityTable.IS_ADMIN, entry.admin);//  
				    values.put(CommunityTable.FEATURED, entry.featured);
		
				    try{
				     if(entry.media!=null&&entry.media.size() > 0)
				      for (int np = 0; np < entry.media.size(); np+=4) {
				       //    link3 += entry.media.get(np) +Constants.COL_SEPRETOR;
				       //    link3 += entry.media.get(np+1) +Constants.COL_SEPRETOR;
				       //    link3 += entry.media.get(np+2) +Constants.COL_SEPRETOR;
				       //    link3 += entry.media.get(np+3) +Constants.ROW_SEPRETOR;  
		
				       entry.thumbUrl= (String)entry.media.get(np+2);
		
				      }
				    }catch (Exception e) {
				     // TODO: handle exception
				    }
		
				    values.put(CommunityTable.THUMB_URL, entry.thumbUrl);//
				    values.put(CommunityTable.MESSAGE_URL, entry.messageUrl);//
				    values.put(CommunityTable.PROFILE_URL, entry.profileUrl);//   
				    values.put(CommunityTable.SEARCH_MEMBER_URL, entry.searchMemberUrl);//
				    values.put(CommunityTable.NEXT_URL, "not maped");//
				    values.put(CommunityTable.PREV_URL, "not maped");//
		
				    values.put(CommunityTable.OWNER_DISPLAY_NAME, entry.ownerDisplayName);//
				    values.put(CommunityTable.OWNER_PROFILE_URL, entry.ownerProfileUrl);//
				    values.put(CommunityTable.OWNER_THUMB_URL, entry.ownerThumbUrl);//
				    values.put(CommunityTable.OWNER_USERNAME, entry.ownerUsername);//
				    values.put(CommunityTable.INSERT_TIME, System.currentTimeMillis());
				    values.put(CommunityTable.ADMIN_STATE, entry.adminState);//  
				    values.put(CommunityTable.FEATURED, entry.featured);//
		
				    Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
							new String[] { ""+entry.groupName}, null, null, null);

				    if(cursor.getCount() == 1)
				    {
				    	//Check if already any value in DB for new message counter
				    	cursor.moveToFirst();
				    	CommunityFeed.Entry e = CursorToObject.communityEntryObject(cursor);
				    	if(e != null && e.newMessageCount > 0)
				    		values.put(CommunityTable.GROUP_NEW_MSG_COUNT, entry.newMessageCount + e.newMessageCount);
				    	else
				    		values.put(CommunityTable.GROUP_NEW_MSG_COUNT, entry.newMessageCount);
				    	if(log)	
				    		Log.i(TAG, "saveCommunity :: ContentValues Update : "+values.toString());
				    	// MANOJ SINGH
				    	// DATE 17-08-2015
				    	// Counter blank for same channel where we chating in community
				    	// START
				    	String Groupid_last_vistied = Utilities.getString(context, Constants.LAST_VISITED_GROUPID);
				    	if(Groupid_last_vistied != null){
				    		if(Groupid_last_vistied.equals(entry.groupId)){
				    			//break;
				    			Log.v("Groupid_last_vistied","Groupid_last_vistied  --->"+ entry.groupId);
				    			//Utilities.setString(context, Constants.LAST_VISITED_GROUPID, null);
				    		}
				    		else
				    		{
				    			int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
										new String[] {entry.groupName});
				    		}
				    	}else
				    	{
				    	int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
								new String[] {entry.groupName});
				    	}
				    	//
				    	//END
				    }
				    else
				    {
				    	//Here we need to write the code, to check if no entry for that community in DB then, either we need to get the profile info
				    	//and update in the DB
				    	//OR
				    	//Refresh the whole channel list and maintain the new message counter
//				    	Log.i(TAG, "updateCommunityInfo :: ContentValues Insert : "+values.toString());
				    	context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_COMMUNITY,values);   
				    }
				    cursor.close();
			   }
		  }catch (Exception e) {
		   e.printStackTrace();
		  }
		 }
	public static void updateCommunityInfo(int requestForID, ChannelRefreshInfo[] feed)
	{  
		boolean isUpdate = false;
		boolean isMessageNewCommunity = false;
		try{
			if(log)
				Log.i(TAG, "updateCommunityInfo :: SAVING MEDIA COMMUNITY feed count : "+feed.length);
			
			for (int j = feed.length; j > 0; j--) 
			{
				ChannelRefreshInfo entry = feed[j - 1];
				ContentValues values = new ContentValues();
				
				 Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(CommunityTable.TABLE, null, CommunityTable.GROUP_NAME + " = ? ",
							new String[] { ""+entry.getChannelName()}, null, null, null);
				    
				    if(cursor.getCount() == 1)
				    {
				    	cursor.moveToFirst();
				    	CommunityFeed.Entry entryOld = CursorToObject.communityEntryObject(cursor);
				    	
				    	values.put(CommunityTable.GROUP_NAME, entry.getChannelName());
				    	values.put(CommunityTable.GROUPID, entry.getChannelID());
				    	values.put(CommunityTable.INSERT_TIME, System.currentTimeMillis());
				    	values.put(CommunityTable.GROUP_NEW_MSG_COUNT, entry.getNewMessageCount() + entryOld.newMessageCount);
				    	if(log)
				    		Log.i(TAG, "updateCommunityInfo["+(j - 1)+"] :: ContentValues Update : "+values.toString());
				    	
				    	
				    	// MANOJ SINGH
				    	// DATE 17-08-2015
				    	// Counter blank for same channel where we chating in community
				    	// START
				    	String Groupid_last_vistied = Utilities.getString(context, Constants.LAST_VISITED_GROUPID);
				    	if(Groupid_last_vistied != null){
				    		if(Groupid_last_vistied.equals(entry.getChannelID())){
				    			//break;
				    			Log.v("Groupid_last_vistied","Groupid_last_vistied  --->"+ entry.getChannelID());
				    			Utilities.setString(context, Constants.LAST_VISITED_GROUPID, null);
				    			values.put(CommunityTable.GROUP_NEW_MSG_COUNT, 0);
				    			int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
										new String[] {entry.getChannelName()});
				    		}
				    		else
				    		{
				    			int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
										new String[] {entry.getChannelName()});
				    		}
				    	}else
				    	{
				    	int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
								new String[] {entry.getChannelName()});
				    	}
				    	//
				    	//END
				    	
				    	
				    /*	int rowUpdated = BusinessProxy.sSelf.sqlDB.update(CommunityTable.TABLE, values, CommunityTable.GROUP_NAME +"= ? ",
								new String[] {entry.getChannelName()});*/
						isUpdate = true;
				    }
					else
					{
						//Community doesn't exists in DB then get the data from server and update entry.
						isMessageNewCommunity = true;
//						context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_COMMUNITY,values);
					}
				    UIActivityManager.refreshChannelList = true;
				    cursor.close();
			}
			if(isUpdate)
			{
				MediaEngine.getMediaEngineInstance().playResource(R.raw.ring1);
//				BusinessProxy.sSelf.mUIActivityManager.showNewMessageAlert("Yelatalk", "Yelatalk");
				httpSynchChannelRefresh.onNotification(Constants.CHANNEL_NEW_COUNTER_FOR_EXISTING, null, null);
			}
			if(isMessageNewCommunity)
			{
				MediaEngine.getMediaEngineInstance().playResource(R.raw.ring1);
//				BusinessProxy.sSelf.mUIActivityManager.showNewMessageAlert("Yelatalk", "Yelatalk");
				httpSynchChannelRefresh.onNotification(Constants.CHANNEL_NEW_COUNTER_FOR_NEW, null, null);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFeaturedUser(FeaturedUserBean featuredUserBean) {
		if(log)
			Log.i(TAG, "saveFeaturedUser :: SAVING FEATURED USER FEED");
		//		System.out.println("Feed Synch Discovery user--------------remove row Featured User----------" + mRowsUpdated);
		try{
			ContentValues values = new ContentValues();
			values.put(FeatureUserTable.STORY_ID, featuredUserBean.id);
			values.put(FeatureUserTable.TOTAL_COUNT, featuredUserBean.totalCount);
			values.put(FeatureUserTable.DISPLAY_NAME, featuredUserBean.displayName);
			values.put(FeatureUserTable.MEDIA_POSTS, featuredUserBean.mediaPosts);
			values.put(FeatureUserTable.MEDIA_FOLLOWERS, featuredUserBean.mediaFollowers);
			values.put(FeatureUserTable.COMMUNITIES_MEMBER, featuredUserBean.communitiesMember);
			values.put(FeatureUserTable.THUMB_URL, featuredUserBean.thumbUrl);
			values.put(FeatureUserTable.PROFIL_EURL, featuredUserBean.profileUrl);
			//		values.put(FeatureUserTable.PROFIL_EURL, featuredUserBean.prevUrl);
			values.put(FeatureUserTable.PREV_URL, featuredUserBean.prevUrl);
			values.put(FeatureUserTable.NEXT_URL, featuredUserBean.nextUrl);
			values.put(FeatureUserTable.TITLE, featuredUserBean.title);
			values.put(FeatureUserTable.TRACKING, featuredUserBean.tracking);
			values.put(FeatureUserTable.SUE, featuredUserBean.seo);
			values.put(FeatureUserTable.INSERT_TIME, featuredUserBean.insertTime);
			values.put(FeatureUserTable.MORE, featuredUserBean.more);


			//				 int mRowsUpdated =
			//					 context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_FEATUREDUSER,
			//		FeatureUserTable.DISPLAY_NAME+"='"+ featuredUserBean.displayName+"'", null);
			context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_FEATUREDUSER,values);
			//				 if(log)
			/*System.out.println("Feed Synch Discovery user--------------remove row Featured User----------" + mRowsUpdated);
			 */
			//		Cursor cursor = context.getContentResolver().query(MediaContentProvider.CONTENT_URI_FEATUREDUSER, null, null, null, null);
			//		if(log)
			//		System.out.println("Feed Synch Discovery user--------------cursor count Featured User----------" + cursor.getCount());
		}catch (Exception e) {
//			System.out.println("Feed Synch ERROR in SAVING FEATUREDUSER"+e.getMessage());
			e.printStackTrace();
		}
	}
	public void saveGroupEvent(GroupEventBean groupEventBean) {
		try{
			if(log)
				Log.i(TAG, "saveGroupEvent :: SAVING GROUPEEVENT FEED");
			ContentValues values = new ContentValues();
			values.put(GroupEventTable.ID, groupEventBean.id);
			values.put(GroupEventTable.PARENT_ID, "");
			values.put(GroupEventTable.TITLE, groupEventBean.title);
			values.put(GroupEventTable.TOTAL_COUNT, groupEventBean.totalCount);
			values.put(GroupEventTable.GROUP_NAME, groupEventBean.groupname);
			values.put(GroupEventTable.COM_PROFILE_URL, groupEventBean.comProfileUrl);
			values.put(GroupEventTable.HOSTER_USER_NAME, groupEventBean.hosterUsername);
			values.put(GroupEventTable.HOSTER_THUMBUR, groupEventBean.hosterThumbUr);
			values.put(GroupEventTable.HOSTER_PROFILE_URL, groupEventBean.hosterProfileUrl);
			values.put(GroupEventTable.HOSTER_DISPLAY_NAME, groupEventBean.hosterDisplayName);		
			values.put(GroupEventTable.DESCRIPTION, groupEventBean.description);
			values.put(GroupEventTable.START_DATE, groupEventBean.startDate);
			values.put(GroupEventTable.END_DATE, groupEventBean.endDate);
			values.put(GroupEventTable.PICTURE_URL, groupEventBean.pictureUrl);		
			values.put(GroupEventTable.THUMB_PICTURE_URL, groupEventBean.thumbPictureUrl);
			values.put(GroupEventTable.AUDIO_URL, groupEventBean.audioUrl);
			values.put(GroupEventTable.NEXT_URL, groupEventBean.nextUrl);
			values.put(GroupEventTable.PREV_URL, groupEventBean.prevUrl);
			values.put(GroupEventTable.TRACKING, groupEventBean.tracking);
			values.put(GroupEventTable.SEO, groupEventBean.seo);
			values.put(GroupEventTable.NEXT_URL, groupEventBean.nextUrl);
			values.put(GroupEventTable.PREV_URL, groupEventBean.prevUrl);
			values.put(GroupEventTable.INSERT_TIME, groupEventBean.insertTime);
			values.put(GroupEventTable.MORE, groupEventBean.insertTime);

			values.put(GroupEventTable.MESSAGE_INFO1, groupEventBean.messageInfo1);
			values.put(GroupEventTable.MESSAGE_INFO2, groupEventBean.messageInfo2);
			values.put(GroupEventTable.MESSAGE_ALERT1, groupEventBean.messageAlert1);
			values.put(GroupEventTable.MESSAGE_ALERT2, groupEventBean.messageAlert2);
			values.put(GroupEventTable.BUTTON_NAME, groupEventBean.button_name);
			values.put(GroupEventTable.BUTTON_ACTION, groupEventBean.button_action);
			values.put(GroupEventTable.START_TIME, groupEventBean.startTime);
			values.put(GroupEventTable.END_TIME, groupEventBean.endTime);

			values.put(GroupEventTable.OWNER_DISPLAY_NAME, groupEventBean.ownerDisplayName);
			values.put(GroupEventTable.OWNER_THUMB_URL, groupEventBean.ownerThumbUrl);
			values.put(GroupEventTable.OWNER_USER_NAME, groupEventBean.ownerUsername);
			values.put(GroupEventTable.OWNERP_ROFILE_URL, groupEventBean.ownerProfileUrl);


			if(log)
				Log.i(TAG, "saveGroupEvent :: Group event time ins : "+groupEventBean.insertTime + " : "+groupEventBean.title);
			//	    int mRowsUpdated =
			//		context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_GROUPEVENT,
			//				GroupEventTable.ID+"='"+ groupEventBean.id+"'", null);	    
			//	    if(log)
			//	    System.out.println("Feed Synch Discovery user--------------remove row group event----------" + mRowsUpdated);

			context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_GROUPEVENT,values);
			//		Cursor cursor = context.getContentResolver().query(MediaContentProvider.CONTENT_URI_GROUPEVENT, null, null, null, null);
			//		if(log)
			//		System.out.println("Feed Synch Discovery user--------------cursor count Groupe event----------" + cursor.getCount());
		}catch (Exception e) {
//			System.out.println("Feed Synch ERROR in SAVING GOUPE EVENT"+e.getMessage());
			e.printStackTrace();
		}
	}
	public void saveStory(LandingPageBean landingPageBean) {
		try{
			//			if(1==1)return;
			if(log)
				Log.i(TAG, "saveStory :: SAVING ACTIVITY FEED landingPageBean id : "+landingPageBean.story_id );
			ContentValues values = new ContentValues();
			values.put(LandingPageTable.PARENT_ID, landingPageBean.parent_id);
			values.put(LandingPageTable.STORY_ID, landingPageBean.story_id);
			values.put(LandingPageTable.TRACKING, landingPageBean.tracking);
			values.put(LandingPageTable.SUE, landingPageBean.sue);
			values.put(LandingPageTable.TITLE, landingPageBean.title);
			values.put(LandingPageTable.TYPE, landingPageBean.story_type);
			values.put(LandingPageTable.USERTHUMB_PROFILE_URL, landingPageBean.userthumb_profile_url);

			values.put(LandingPageTable.IMAGE_CONTENT_ID,
					landingPageBean.image_content_id);
			values.put(LandingPageTable.IMAGE_CONTENT_THUMB_URLS,
					landingPageBean.image_content_thumb_urls);
			values.put(LandingPageTable.IMAGE_CONTENT_URLS,
					landingPageBean.image_content_urls);
			values.put(LandingPageTable.IMAGE_CONTENT_CLICK_URL,
					landingPageBean.image_content_click_action);
			values.put(LandingPageTable.DESC, landingPageBean.desc);
			values.put(LandingPageTable.DESC_ORI, landingPageBean.descOri);
			values.put(LandingPageTable.PUBLISHED, landingPageBean.published);
			values.put(LandingPageTable.DATE, landingPageBean.date);
			values.put(LandingPageTable.LIKE, landingPageBean.like);
			values.put(LandingPageTable.DISLIKE, landingPageBean.dislike);
			values.put(LandingPageTable.USER_THUMB_URL,
					landingPageBean.userthumb_url);
			values.put(LandingPageTable.COMMENT_URL, landingPageBean.comment_url);
			values.put(LandingPageTable.SHARE_URL, landingPageBean.share_url);
			values.put(LandingPageTable.PREV_URL, landingPageBean.prev_url);
			values.put(LandingPageTable.NEXT_URL, landingPageBean.next_url);
			values.put(LandingPageTable.FIRSTNAME, landingPageBean.firstname);
			values.put(LandingPageTable.LASTNAME, landingPageBean.lastname);
			values.put(LandingPageTable.USERNAME, landingPageBean.username);
			values.put(LandingPageTable.MESSAGE_STATE,
					landingPageBean.message_state);
			values.put(LandingPageTable.DRM, landingPageBean.drm);
			values.put(LandingPageTable.ACTION, landingPageBean.action);
			values.put(LandingPageTable.LOGIN_USER, landingPageBean.loginUser);
			values.put(LandingPageTable.TAB, landingPageBean.tab);
			values.put(LandingPageTable.DESC2, landingPageBean.desc2);
			values.put(LandingPageTable.VIDEO_CONTENT_ID,
					landingPageBean.video_content_id);
			values.put(LandingPageTable.VIDEO_CONTENT_THUMB_URLS,
					landingPageBean.video_content_thumb_urls);
			values.put(LandingPageTable.VIDEO_CONTENT_URLS,
					landingPageBean.video_content_urls);
			values.put(LandingPageTable.VIDEO_CONTENT_CLICK_URL,
					landingPageBean.video_content_click_action);		
			values.put(LandingPageTable.VOICE_CONTENT_ID,
					landingPageBean.voice_content_id);		
			values.put(LandingPageTable.VOICE_CONTENT_THUMB_URLS,
					landingPageBean.voice_content_thumb_urls);
			values.put(LandingPageTable.VOICE_CONTENT_URLS,
					landingPageBean.voice_content_urls);
			values.put(LandingPageTable.VOICE_CONTENT_CLICK_URL,
					landingPageBean.voice_content_click_action);
			values.put(LandingPageTable.OTHER_USER_THUMB_URL,
					landingPageBean.other_user_thumb_url);
			values.put(LandingPageTable.OTHER_USER_FIRSTNAME,
					landingPageBean.other_user_firstname);
			values.put(LandingPageTable.OTHER_USER_LASTNAME,
					landingPageBean.other_user_lastname);
			values.put(LandingPageTable.OTHER_USER_USERNAME,
					landingPageBean.other_user_username);
			values.put(LandingPageTable.OTHER_USER_PROFILE_URL,
					landingPageBean.other_user_profile_url);
			values.put(LandingPageTable.MEDIA_TEXT,
					landingPageBean.mediaText);
			values.put(LandingPageTable.DIRECTION,
					landingPageBean.direction);
			values.put(LandingPageTable.INSERT_TIME,
					landingPageBean.insertDate);
			values.put(LandingPageTable.OP_TYPE,
					landingPageBean.opType);
			if(more){
				values.put(LandingPageTable.MORE, 1);
				values.put(LandingPageTable.DIRECTION,
						landingPageBean.direction);
			}

			//		 System.out.println("Feed Synch --------------activity direction----------"+landingPageBean.direction);

			int mRowsUpdated =
					context. getContentResolver().delete(LandingPageContentProvider.CONTENT_URI,
							LandingPageTable.STORY_ID+"= '"+ landingPageBean.story_id+"'", null);
			if(log)
				Log.i(TAG, "saveStory :: delete row count activity feed ----------"+mRowsUpdated);

			Uri u =  context.getContentResolver().insert(LandingPageContentProvider.CONTENT_URI,
					values);
			if(log)
				Log.i(TAG, "saveStory :: landingPageBean saving:"+ landingPageBean.toString());
			if(log)
				Log.i(TAG, "saveStory :: uri after insert activity feed : "+u.toString());
			//		Cursor cursor = context.getContentResolver().query(
			//				LandingPageContentProvider.CONTENT_URI, null, null, null, null);
			//		System.out
			//				.println("Feed Synch --------------cursor count activity page----------" + cursor.getCount());
		}catch (Exception e) {
//			System.out.println("Feed Synch ERROR in SAVING ACTIVITY FEED"+e.getMessage());
			e.printStackTrace();
		}
	}


	public void saveConversitionList(ConversationList conversationList){
		totMsg++;
		//		message.inserTime =message.inserTime-18000000l ;
		conversationList.inserTime =gettimemillis(conversationList.datetime);//message.inserTime-18000000l ;
		if(log){
			Log.i(TAG, "saveConversitionList :: conversationId--2---- : "+conversationList.conversationId);
			Log.i(TAG, "saveConversitionList :: SAVING MESSAGE text--2---messageType- : "+conversationList.msgTxt);
			Log.i(TAG, "saveConversitionList :: SAVING MESSAGE TYPE--2--messageType-- : "+conversationList.messageType);
			Log.i(TAG, "saveConversitionList :: SAVING MESSAGE TYPE--2---unreadMessageCount- : "+conversationList.unreadMessageCount);
			Log.i(TAG, "saveConversitionList :: SAVING MESSAGE conversationId--2----## : "+conversationList.conversationId);
		}
		//		if(conversationList!=null && conversationList.mfuId!=null && conversationList.inserTime > FeedRequester.lastMessageIdTime && !conversationList.isLastMessage.equalsIgnoreCase("false"))
		//		{
		//			
		//			System.out.println("Feed Synch Discovery user---swaping top message id--------2-----"+FeedRequester.latestMessage.size());
		//			FeedRequester.lastMessageId = conversationList.mfuId ;
		//			FeedRequester.lastMessageIdTime = conversationList.inserTime ;
		//		}
		String s ="" ;
		boolean dontsave = false;
		String id ="" ;
		try{
			Cursor cursor=  BusinessProxy.sSelf.sqlDB.query(MessageTable.TABLE, null, MessageTable.CONVERSATION_ID + " = ? and "+MessageTable.USER_ID +" = ?  ",
					new String[] { conversationList.conversationId,BusinessProxy.sSelf.getUserId()+"" }, null, null, MessageTable.INSERT_TIME + " ASC");
			cursor.moveToLast();

			s =cursor.getString(cursor.getColumnIndex(MessageConversationTable.PARTICIPANT_INFO) );
			id =cursor.getString(cursor.getColumnIndex(MessageConversationTable.MESSAGE_ID) );
			String  msg =cursor.getString(cursor.getColumnIndex(MessageConversationTable.MSG_TXT) );
			cursor.close() ;
			if(log)
				Log.i(TAG, "saveConversitionList :: partiinfo:"+s);


			if(s!=null){
				String pi[] = Utilities.split(new StringBuffer(s), Constants.ROW_SEPRETOR) ;

				//				System.out.println("------------message.video_size :"+message.video_size);
				ParticipantInfo participantInfo = new ParticipantInfo() ;
				//				message.participantInfo = new Vector<ParticipantInfo>() ;
				//sender username firstname lastname profileImageUrl profileUrl
				int i = 0;
				try{

					for ( i = 0; i < pi.length; i++) {
						participantInfo = new ParticipantInfo() ;
						String info[] = Utilities.split(new StringBuffer(pi[i]), Constants.COL_SEPRETOR) ;
						participantInfo.isSender=Boolean.parseBoolean(info[0]);
						participantInfo.userName=info[1];

						if(participantInfo.userName.toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) 
								&& participantInfo.isSender){
							dontsave = true;
							break;
						}

					}
				}catch (Exception e) {
					e.printStackTrace();
//					if(log)
//						System.out.println(pi[i]);
					//					message.participantInfo.add(participantInfo);
				}
				//				 System.out.println("-----------dontsave:"+dontsave+" : "+conversationList.conversationId+" : "+id+" : "+msg);
			}
		}
		catch (Exception e) {

		}
		//		 if(!dontsave)
		if(isSystemMessagess(conversationList.drmFlags,conversationList.notificationFlags))
			saveMessage(conversationList) ;
		if(isSystemMessagess(conversationList.drmFlags,conversationList.notificationFlags)){
			return;
		}

		ContentValues values = new ContentValues();
		values.put(MessageTable.COMMENT, conversationList.comments);//int
		values.put(MessageConversationTable.USER_ID, BusinessProxy.sSelf.getUserId());//int
		values.put(MessageConversationTable.PARENT_ID, conversationList.parent_id);
		values.put(MessageConversationTable.CLIENTTRANSACTION_ID, conversationList.clientTransactionId);
		values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, conversationList.unreadMessageCount);
		values.put(MessageConversationTable.CONTENT_BITMAP, conversationList.contentBitMap);
		values.put(MessageConversationTable.CONVERSATION_ID, conversationList.conversationId);
		values.put(MessageConversationTable.DATE_TIME, conversationList.datetime);
		values.put(MessageConversationTable.DRM_FLAGS, conversationList.drmFlags);//int
		values.put(MessageConversationTable.HAS_BEEN_VIEWED, conversationList.hasBeenViewed);
		values.put(MessageConversationTable.HAS_BEEN_VIEWED_BY_SIP, conversationList.hasBeenViewedBySIP);
		values.put(MessageConversationTable.MESSAGE_ID, conversationList.messageId);
		values.put(MessageConversationTable.USMId, conversationList.usmId);
		values.put(MessageConversationTable.MFU_ID, conversationList.mfuId);
		values.put(MessageConversationTable.MSG_OP, conversationList.msgOp);//int
		values.put(MessageConversationTable.MSG_TXT, conversationList.msgTxt);
		values.put(MessageConversationTable.NOTIFICATION_FLAGS, conversationList.notificationFlags);
		values.put(MessageConversationTable.REFMESSAGE_ID, conversationList.refMessageId);
		values.put(MessageConversationTable.SENDERUSER_ID, conversationList.senderUserId);
		values.put(MessageConversationTable.SOURCE, conversationList.source);
		values.put(MessageConversationTable.TARGETUSER_ID, conversationList.targetUserId);
		values.put(MessageConversationTable.USER_FIRSTNAME, conversationList.user_firstname);
		values.put(MessageConversationTable.USER_LASTNAME, conversationList.user_lastname);
		values.put(MessageConversationTable.USER_NAME, conversationList.user_name);
		values.put(MessageConversationTable.USER_URI, conversationList.user_uri);
		values.put(MessageConversationTable.INSERT_TIME, conversationList.inserTime);
		values.put(MessageConversationTable.SORT_TIME, conversationList.inserTime);//System.currentTimeMillis());
		values.put(MessageConversationTable.IS_LAST_MESSAGE, conversationList.isLastMessage);
		//		System.out.println("Feed Synch Discovery user---conversationList.isLastMessage-----1--------"+conversationList.isLastMessage);
		values.put(MessageConversationTable.IS_GROUP, conversationList.isGroup);
		values.put(MessageConversationTable.SUBJECT, conversationList.subject);
		//NEW CHANGE ADDED FOR GROUP PIC
		values.put(MessageConversationTable.GROUP_PIC, conversationList.imageFileId);
		values.put(MessageTable.IS_LEFT, conversationList.isLeft);
		values.put(MessageTable.IS_NEXT, conversationList.isNext);
		values.put(MessageTable.ANI_TYPE, conversationList.aniType);
		values.put(MessageTable.ANI_VALUE, conversationList.aniValue);
		//		if(log)
		if(conversationList.conversationId.equalsIgnoreCase("100483"))
		{
			Log.i(TAG, "saveConversitionList :: participantInfoStr 3 subject: "+conversationList.subject);
			Log.i(TAG, "saveConversitionList :: participantInfoStr 3 saveConversitionList: "+conversationList.participantInfoStr);
		}
		values.put(MessageTable.PARTICIPANT_INFO, conversationList.participantInfoStr);



		Cursor cursor = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, 
				null, MessageConversationTable.CONVERSATION_ID+" = ? and "+MessageConversationTable.USER_ID+" =?", new String[] {conversationList.conversationId,""+BusinessProxy.sSelf.getUserId()}, null);
		int count = cursor.getCount() ;
		String conversitionId =null; 
		if(log)
			Log.i(TAG, "saveConversitionList :: cursor count message list is message id available----------"+count);

		if(count <=0){
			cursor.close();
			Uri u =  context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					values);

			if(dontsave)
			{
				int updatedRow = context.getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX,
						values,MessageConversationTable.CONVERSATION_ID
						+"= ? and "+MessageConversationTable.USER_ID+" =? and "+MessageConversationTable.MESSAGE_ID+" =?",new String[] {conversationList.conversationId,BusinessProxy.sSelf.getUserId()+"",id});
			}

		}
		else
		{
			cursor.moveToFirst();
			try{
				conversitionId = cursor.getString(cursor.getColumnIndex(MessageConversationTable.CONVERSATION_ID));
				cursor.close();
				int updatedRow = context.getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
						values,MessageConversationTable.CONVERSATION_ID

						+"=? and "+MessageConversationTable.USER_ID+" =?",new String[] {conversitionId,""+BusinessProxy.sSelf.getUserId()});


				if(log)
					Log.i(TAG, "saveConversitionList :: conversiotion list updated row count ----------"+updatedRow);

			}catch (Exception e) {
				// TODO: handle exception
			}

		}

		////		System.out.println("-------------uri after insert activity feed : "+u.toString());
		//	    cursor = context.getContentResolver().query(
		//				MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null, null, null, null);
		//	    if(log)
		//		System.out
		//				.println("Feed Synch --------------cursor count conversition list----------" + cursor.getCount());
		//		cursor.close();
	}

	String a[] = {};
	public static int getRandomNumberBetween(int min, int max) {
		min = min-1;
		max = max+1;
		if(log)
			Log.i(TAG, "getRandomNumberBetween :: -min : "+min+" : max : "+max);
		Random foo = new Random();
		int randomNumber = foo.nextInt(max - min) + min;
		if(randomNumber == min) {
			//        	System.out.println("-----------------randomNumber : "+min + 1);
			return min + 1;
		}
		else {
			//        	System.out.println("-----------------randomNumber : "+min + 1);
			return randomNumber;
		}

	}
	public static String LAST_MFUID_FOR_CONVERSATION_LIST = "0";//Long.MIN_VALUE ;
	public static long insMoreTime = 0;//Long.MIN_VALUE ;
	public static long insMoreTime2 = 500000;//Long.MIN_VALUE ;
	public static long totLodedMore = 0;//Long.MIN_VALUE ;

	public void saveMessage(Message message){

		if(message.datetime != null)
			message.inserTime =gettimemillis(message.datetime);//message.inserTime-18000000l ;

		//please remove
		//		message.messageType = Message.MESSAGE_TYPE_BUDDY_CAN_NOT_ADD_TO_CONVERSATION ;

		if(isAvoidableSource(message.source))
			return; 

		BusinessProxy.sSelf.chatBit = message.comments ;
		totMsg++;
		long st = System.currentTimeMillis() ;
		ContentValues values = new ContentValues();

		if((message.notificationFlags  & InboxTblObject.NOTI_BUZZ_AUTOPLAY)>0)
			message.msgTxt = "BUZZ" ;

		values.put(MessageTable.COMMENT, message.comments);//int
		values.put(MessageTable.USER_ID, BusinessProxy.sSelf.getUserId());//int
		values.put(MessageTable.PARENT_ID, message.parent_id);
		values.put(MessageTable.CLIENTTRANSACTION_ID, message.clientTransactionId);
		values.put(MessageTable.CONTENT_BITMAP, message.contentBitMap);
		values.put(MessageTable.CONVERSATION_ID, message.conversationId);
		values.put(MessageTable.DATE_TIME, message.datetime);
		values.put(MessageTable.DRM_FLAGS, message.drmFlags);//int
		values.put(MessageTable.HAS_BEEN_VIEWED, message.hasBeenViewed);
		values.put(MessageTable.HAS_BEEN_VIEWED_BY_SIP, message.hasBeenViewedBySIP);
		values.put(MessageTable.MESSAGE_ID, message.messageId);
		values.put(MessageTable.USMId, message.usmId);
		values.put(MessageTable.MFU_ID, message.mfuId);
		values.put(MessageTable.MSG_OP, message.msgOp);//int
		values.put(MessageTable.CLTTXNID, message.cltTxnId);//int



		if (message.contentBitMap!=null && message.contentBitMap.indexOf("RTML")!= -1 && isSystemMessagess(message.drmFlags, message.notificationFlags))
			values.put(MessageTable.VIDEO_ID_8, message.msgTxt);
		else
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
		values.put(MessageTable.IS_BOOKMARK, message.isBookmark);
		//		values.put(MessageTable.SUBJECT, "message.subject");
		values.put(MessageTable.TAG, message.tag);
		values.put(MessageTable.IS_LEFT, message.isLeft);
		values.put(MessageTable.IS_NEXT, message.isNext);
		values.put(MessageTable.MESSAGE_ATTRIBUTE, message.messageAttribute);

		values.put(MessageTable.SORT_TIME, insMoreTime++);//System.currentTimeMillis());//System.currentTimeMillis());

		values.put(MessageTable.ANI_TYPE, message.aniType);
		values.put(MessageTable.ANI_VALUE, message.aniValue);
		values.put(MessageTable.DIRECTION, "0");
		if(more || message.messageId.equalsIgnoreCase("-999") ){
			totLodedMore++;
			values.put(MessageTable.SORT_TIME, insMoreTime2++);//System.currentTimeMillis());//System.currentTimeMillis());

			values.put(MessageTable.MORE, "true");
			values.put(MessageTable.DIRECTION, "2");
			if(message.messageId.equalsIgnoreCase("-999"))
				values.put(MessageTable.SORT_TIME, Long.MIN_VALUE);//System.currentTimeMillis());
		}

		if(log)
			Log.i(TAG, "getRandomNumberBetween :: SAVING MESSAGE TYPE--1---- : "+message.messageType);

		if(message.messageType == Message.MESSAGE_TYPE_EMAIL_VERIFY){
			SettingData.sSelf.setEmailVerified(true) ;
		}
		if(message.messageType == Message.MESSAGE_TYPE_UPDATE_SUBJECT){
//			values.put(MessageTable.SUBJECT, message.subject=message.msgTxt.substring(message.msgTxt.indexOf("to")+2,message.msgTxt.length()));
			values.put(MessageTable.SUBJECT, message.subject);
			values.put(MessageTable.GROUP_PIC, message.imageFileId);
			Log.w(TAG, "saveMessage :: message.messageType == Message.MESSAGE_TYPE_UPDATE_SUBJECT");
			String url = "http://"+Urls.TEJAS_HOST+"/tejas/rest/rtmessaging/getConversationInfo/$USERID$/$CID$"; 
			if(url != null){
				url = url.replace("$USERID$", ""+BusinessProxy.sSelf.getUserId());
				url = url.replace("$CID$", message.conversationId);
			}
			new GetMUCData().execute(url);
		}
		//		if (message.msgTxt.indexOf("<?xml version=") != -1 ||  isAvoidableSource(message.source)){
		//			message.messageType = Message.MESSAGE_TYPE_SYSTEM_MESSAGE ;
		//		}
		//		
		//		//will remove this login this temp because  m not geting notification flag in feed
		//		if(message.msgTxt.startsWith("{")){
		//		Utilities.jsonParserEngine(message.msgTxt);
		//		if( Utilities.accept()!=null){
		//			message.messageType = Message.MESSAGE_TYPE_FRIEND_REQUEST ;
		//		}
		//		}
		//		if(message.drmFlags !=null  && message.drmFlags.trim().length()>0)


		if(isSystemMessagess(message.drmFlags, message.notificationFlags)) 
		{
			message.messageType = Message.MESSAGE_TYPE_SYSTEM_MESSAGE ;
		}else{
			if(message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION ||
					message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION ||
					message.messageType==Message.MESSAGE_TYPE_BUDDY_CAN_NOT_ADD_TO_CONVERSATION ||
					message.messageType==Message.MESSAGE_TYPE_UPDATE_SUBJECT ||
					message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION_ALREADY 
					){}else
					{
						message.messageType=Message.MESSAGE_TYPE_BUDDY ;
					}

		}
		//		if(isFriendRequest(message.drmFlags, message.notificationFlags)) 
		//		{
		//			message.messageType = Message.MESSAGE_TYPE_FRIEND_REQUEST ;
		//		}
		if(log){
			Log.i(TAG, "saveMessage :: SAVING MESSAGE conversationId--2---- : "+message.conversationId);
			Log.i(TAG, "saveMessage :: SAVING MESSAGE text--2---- : "+message.msgTxt);
			Log.i(TAG, "saveMessage :: SAVING MESSAGE TYPE--2---- : "+message.messageType);
		}

		//		message.image_content_thumb_urls =	"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg"+Constants.COL_SEPRETOR+"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
		//		message.image_content_urls =	"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg"+Constants.COL_SEPRETOR+"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
		//		

		if(message.image_content_thumb_urls !=null && message.image_content_thumb_urls.endsWith(Constants.ROW_SEPRETOR))
			message.image_content_thumb_urls = message.image_content_thumb_urls.substring(0, message.image_content_thumb_urls.length()-1) ;
		if(message.image_content_urls !=null && message.image_content_urls.endsWith(Constants.ROW_SEPRETOR))
			message.image_content_urls = message.image_content_urls.substring(0, message.image_content_urls.length()-1) ;

		if(message.image_size !=null && message.image_size.endsWith(Constants.ROW_SEPRETOR))
			message.image_size = message.image_size.substring(0, message.image_size.length()-1) ;

		if(message.audio_size !=null && message.audio_size.endsWith(Constants.ROW_SEPRETOR))
			message.audio_size = message.audio_size.substring(0, message.audio_size.length()-1) ;


		values.put(MessageTable.MESSAGE_TYPE, message.messageType);
		values.put(MessageTable.IMAGE_CONTENT_THUMB_URLS, message.image_content_thumb_urls);
		values.put(MessageTable.IMAGE_CONTENT_URLS, message.image_content_urls);
		values.put(MessageTable.IMAGE_SIZE, message.image_size);

		//ROW_SEPRETOR
		if(message.video_content_thumb_urls !=null && message.video_content_thumb_urls.endsWith(Constants.ROW_SEPRETOR))
			message.video_content_thumb_urls = message.video_content_thumb_urls.substring(0, message.video_content_thumb_urls.length()-1) ;
		if(message.video_content_urls !=null && message.video_content_urls.endsWith(Constants.ROW_SEPRETOR))
			message.video_content_urls = message.video_content_urls.substring(0, message.video_content_urls.length()-1) ;

		if(message.video_content_thumb_urls !=null && message.video_content_thumb_urls.endsWith(Constants.COL_SEPRETOR))
			message.video_content_thumb_urls = message.video_content_thumb_urls.substring(0, message.video_content_thumb_urls.length()-1) ;
		if(message.video_content_urls !=null && message.video_content_urls.endsWith(Constants.COL_SEPRETOR))
			message.video_content_urls = message.video_content_urls.substring(0, message.video_content_urls.length()-1) ;

		if(message.video_size !=null && message.video_size.endsWith(Constants.COL_SEPRETOR))
			message.video_size = message.video_size.substring(0, message.video_size.length()-1) ;
		if(message.video_content_urls !=null && message.video_content_urls.endsWith(Constants.COL_SEPRETOR))
			message.video_content_urls = message.video_content_urls.substring(0, message.video_content_urls.length()-1) ;

		//		message.video_content_urls =	"http://124.153.95.131/tejas/video/resource/repository/2011/01/31/10/3959016690211624960.mp4"+Constants.COL_SEPRETOR+"http://124.153.95.131/tejas/video/resource/repository/2011/01/31/10/3959016690211624960.mp4" ;
		//		message.video_content_thumb_urls =	"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg"+Constants.COL_SEPRETOR+"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;



		if(message.conversationId!=null&&ConversationsActivity.CONVERSATIONID!=null&& ConversationsActivity.CONVERSATIONID.equalsIgnoreCase(message.conversationId) && message.MESSAGE_TYPE_UPDATE_SUBJECT==message.messageType){
			updateParticipantInfoUISUB = true ;
			sub=message.subject;
		}

		values.put(MessageTable.VIDEO_CONTENT_THUMB_URLS, message.video_content_thumb_urls);
		values.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);
		values.put(MessageTable.VIDEO_SIZE, message.video_size);

		//		values.put(MessageTable.DIRECTION, message.);
		values.put(MessageTable.INSERT_TIME, message.inserTime);//System.currentTimeMillis());

		//		System.out.println("------------date saveMessage datetime : " + message.datetime);
		//		 System.out.println("------------date saveMessage inserTime : " + message.inserTime);

		//		if(more)
		//			values.put(MessageTable.SORT_TIME, message.inserTime-1000);//System.currentTimeMillis());
		//		int ind = getRandomNumberBetween(0, a.length-1) ;

		if(message.voice_content_urls !=null && message.voice_content_urls.endsWith(Constants.ROW_SEPRETOR))
			message.voice_content_urls = message.voice_content_urls.substring(0, message.voice_content_urls.length()-1) ;

		values.put(MessageTable.VOICE_CONTENT_URLS, message.voice_content_urls);
		if(message.voice_content_urls!=null){
			values.put(MessageTable.VOICE_CONTENT_URLS, message.voice_content_urls);
			values.put(MessageTable.AUDIO_ID, message.voice_content_urls.hashCode());
			values.put(MessageTable.AUDIO_SIZE, message.audio_size);
			if(Downloader.getInstance().check(
					message.voice_content_urls)){
				values.put(MessageTable.AUDIO_DOWNLOAD_STATUE, 1);
			}
		}
		if(message.video_content_urls!=null){
			values.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);
			String s[] = Utilities.split(new StringBuffer(message.video_content_urls), Constants.COL_SEPRETOR) ;

			if (message.video_content_urls != null) {
				values.put(MessageTable.VIDEO_CONTENT_URLS,
						message.video_content_urls);
				for (int i = 0; i < s.length; i++) {
					values.put("VIDEO_ID_"+(i+1), s[i].hashCode());
					if (Downloader.getInstance().check(s[i])) {
						values.put("VIDEO_DOWNLOAD_STATUE_"+(i+1), 1);
					}
				}
			}}

		if(log)
			Log.i(TAG, "saveMessage :: participantInfoStr 3 save message: "+message.participantInfoStr);
		values.put(MessageTable.PARTICIPANT_INFO, message.participantInfoStr);

		String pi[] = null;
		if(message.participantInfoStr!=null)
			pi = Utilities.split(new StringBuffer(message.participantInfoStr), Constants.ROW_SEPRETOR) ;

		if(message.messageId==null){
			return;
		}

		//		System.out.println("Feed Synch --------------cursor count message list is message id MESSAGE_ID----------"+message.messageId);

		Cursor cursor = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX, null, MessageTable.MESSAGE_ID+" = ? and "+MessageTable.USER_ID+" =?", new String[] {message.messageId,""+BusinessProxy.sSelf.getUserId()}, null);
		int count = cursor.getCount() ;

		if(log)
			Log.i(TAG, "saveMessage :: cursor count message list is message id available----------"+count);
		cursor.close();
		if(count <=0)
		{

			Uri u =  context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_INBOX,
					values);
			//		if(!isAvoidableSource(message.source)){
			//					Cursor cursor2 = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
			//							MessageConversationTable.CONVERSATION_ID+" = ? and  "+MessageConversationTable.USER_ID+" =?", new String[] {message.conversationId,"" +BusinessProxy.sSelf.getUserId()}, null);
			//					cursor2.close();
			//		}

		}else
		{
			if(log)
				Log.i(TAG, "saveMessage :: returing because message id is already present in DB------ :"+message.messageId );
			return ;
		}

		if(more|| message.messageId.equalsIgnoreCase("-999") || dontIncrementUnreadCount)return;
		Cursor cursor2 = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
				MessageConversationTable.CONVERSATION_ID+" = ? and "+MessageConversationTable.USER_ID+" =? ", new String[] {message.conversationId,"" +BusinessProxy.sSelf.getUserId()}, null);
		if(cursor2.getCount()>0)
		{

			cursor2.moveToFirst(); 
			int ci = cursor2.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT) ;
			long inserTimeL =cursor2.getLong(cursor2.getColumnIndex(MessageTable.INSERT_TIME));
			if(log)
				Log.i(TAG, "saveMessage :: coolumn index for -unreadMsgcount ---------- :"+ci);
			if(ci!= -1){
				int unreadMsgcount = cursor2.getInt(ci);
				if(log)
					Log.i(TAG, "saveMessage :: unreadMsgcount :"+unreadMsgcount +" : message.conversationId :"+message.conversationId);
				if(!cursor2.isClosed())
					cursor2.close() ;
				if(inserTimeL<message.inserTime)
					values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, ++unreadMsgcount);

				if(message.subject == null || message.subject.trim().length() <= 0)
					values.remove(MessageConversationTable.SUBJECT) ;
				if(message.imageFileId == null || message.imageFileId.trim().length() <= 0)
					values.remove(MessageConversationTable.GROUP_PIC) ;
				//				else
				//					if(ConversationsActivity.CONVERSATIONID.equalsIgnoreCase(message.conversationId) && message.MESSAGE_TYPE_UPDATE_SUBJECT==message.messageType){
				//						System.out.println("-------FeedTask.updateParticipantInfoUISUB message.message:-"+message.subject);
				//						updateParticipantInfoUISUB = true ;
				//						sub=message.subject;
				//					}
				//updateing conversioton table
				//if(message.messageType!=92)
				if(message.isBookmark==0)
				{
					if(pi != null && pi.length>2)
						values.put(MessageConversationTable.IS_GROUP, 1);

					if((message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION || message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION )
							&& requestForID==Constants.FEED_GET_MESSAGE){
						if(log)
							Log.i(TAG, "saveMessage :: PARTICIPENT INFI UPDATED");

					}else if(( message.messageType==Message.MESSAGE_TYPE_BUDDY_CAN_NOT_ADD_TO_CONVERSATION )
							&& requestForID==Constants.FEED_GET_MESSAGE){
						if(log)
							Log.i(TAG, "saveMessage :: PARTICIPENT INFI UPDATED");

						String newPartiIng = removeAddeBuddy(message.comments,message.conversationId);
						if(newPartiIng!=null){
							values.put(MessageConversationTable.PARTICIPANT_INFO,newPartiIng);

							String pi3[] = null;
							if(newPartiIng!=null)
								pi3 = Utilities.split(new StringBuffer(newPartiIng), Constants.ROW_SEPRETOR) ;
							if(pi3 != null && pi.length<2)
								values.put(MessageConversationTable.IS_GROUP, 0);
						}



					}else{
						if(log)
							Log.i(TAG, "saveMessage :: PARTICIPENT INFI NOT UPDATED");
						values.remove(MessageConversationTable.PARTICIPANT_INFO);
					}

					if(message.messageType!=Message.MESSAGE_TYPE_SYSTEM_MESSAGE ){
						if(message.messageType==Message.MESSAGE_TYPE_BUDDY )
							values.remove(MessageTable.IS_LEFT) ;
						int updatedRow = context.getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								values,MessageConversationTable.CONVERSATION_ID
								+"= ? and "+MessageConversationTable.USER_ID+" =?",new String[] {message.conversationId,""+BusinessProxy.sSelf.getUserId()});
						if(log)
							Log.i(TAG, "saveMessage :: conversiotion list updated row count ----------"+updatedRow);
					}}
			}
		}else
		{
			if(/*message.messageType==Message.MESSAGE_TYPE_BUDDY && */message.messageType!=Message.MESSAGE_TYPE_SYSTEM_MESSAGE){
				values.put(MessageConversationTable.IS_LAST_MESSAGE, "true");
				Uri u =  context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
						values);
				if(log)
					Log.i(TAG, "saveMessage :: insert into CONTENT_URI_INBOX_CONVERSATION_LIST ----------"+u.toString());
				if(cursor2!=null)
					cursor2.close() ;
				cursor2 = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
						MessageConversationTable.CONVERSATION_ID+" = ? and "+MessageConversationTable.USER_ID+" =?", new String[] {message.conversationId,""+BusinessProxy.sSelf.getUserId()}, null);
				if(cursor2.getCount()>0)
				{
					cursor2.moveToFirst();
					int ci = cursor2.getColumnIndex(MessageConversationTable.UNREAD_MESSAGE_COUNT) ;
					if(log)
						Log.i(TAG, "saveMessage :: coolumn index for -unreadMsgcount ---------- :"+ci);
					if(ci!= -1){
						int unreadMsgcount = cursor2.getInt(ci);
						if(log)
							Log.i(TAG, "saveMessage :: unreadMsgcount :"+unreadMsgcount +" : message.conversationId :"+message.conversationId);
						cursor2.close() ;
						values.put(MessageConversationTable.UNREAD_MESSAGE_COUNT, ++unreadMsgcount);

						if(message.subject==null || message.subject.trim().length()<=0)
							values.remove(MessageConversationTable.SUBJECT) ;

						//if(message.messageType!=92)
						if(message.isBookmark==0)
						{
							//updateing conversioton table
							if(pi != null && pi.length>2)
								values.put(MessageConversationTable.IS_GROUP, 1);

							if((message.messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION || message.messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION )
									&& requestForID==Constants.FEED_GET_MESSAGE){
								if(log)
									Log.i(TAG, "saveMessage :: PARTICIPENT INFI UPDATED");

								String newPartiIng = removeAddeBuddy(message.comments,message.conversationId);
								if(newPartiIng!=null)
									values.put(MessageConversationTable.PARTICIPANT_INFO,newPartiIng);

								String pi3[] = null;
								if(newPartiIng!=null){
									pi3 = Utilities.split(new StringBuffer(newPartiIng), Constants.ROW_SEPRETOR) ;
									if(pi3 != null && pi.length<2)
										values.put(MessageConversationTable.IS_GROUP, 0);
								}


							}else if(( message.messageType==Message.MESSAGE_TYPE_BUDDY_CAN_NOT_ADD_TO_CONVERSATION )
									&& requestForID==Constants.FEED_GET_MESSAGE){
								if(log)
									Log.i(TAG, "saveMessage :: PARTICIPENT INFI UPDATED");

								String newPartiIng = removeAddeBuddy(message.comments,message.conversationId);
								if(newPartiIng!=null)
									values.put(MessageConversationTable.PARTICIPANT_INFO,newPartiIng);	

								String pi3[] = null;
								if(message.participantInfoStr!=null)
									pi3 = Utilities.split(new StringBuffer(message.participantInfoStr), Constants.ROW_SEPRETOR) ;
								if(pi3 != null && pi.length<2)
									values.put(MessageConversationTable.IS_GROUP, 0);
							}
							else{
								if(log)
									Log.i(TAG, "saveMessage :: PARTICIPENT INFI NOT UPDATED");
								values.remove(MessageConversationTable.PARTICIPANT_INFO);
							}
							if(message.messageType!=Message.MESSAGE_TYPE_SYSTEM_MESSAGE ){
								if(message.messageType==Message.MESSAGE_TYPE_BUDDY )
									values.remove(MessageTable.IS_LEFT) ;
								int updatedRow = context.getContentResolver().update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
										values,MessageConversationTable.CONVERSATION_ID
										+"= ? and "+MessageConversationTable.USER_ID+" =?",new String[] {message.conversationId,BusinessProxy.sSelf.getUserId()+""});
								if(log)
									Log.i(TAG, "saveMessage :: conversiotion list updated row count ----------"+updatedRow);
							}
						}
					}
				}
				cursor2.close() ;
			}else{
				if(log){
					Log.i(TAG, "saveMessage :: we cant insert in  MessageConversationTable because it is not buddy message----------");
				}
			}
		}
		if(cursor2!=null && !cursor2.isClosed())
			cursor2.close();
		if(cursor!=null && !cursor.isClosed())
			cursor.close();
		//////////////////
		if(cursor2!=null && !cursor2.isClosed())
			cursor2.close();

		updateInsTime();
		//		Utilities.setLong(context, "insMoreTime", insMoreTime) ;
		if(log)
			Log.i(TAG, "saveMessage :: time taken to save message ----------"+(System.currentTimeMillis() - st));
	}
	private String removeAddeBuddy(String nonFriends, String conversationId) {
		try{
			//			System.out.println("Feed Synch ----removeAddeBuddy------------"+msgTxt);
			//			String rem1 = msgTxt.substring(msgTxt.indexOf(":")+1, msgTxt.length()) ;
			//			System.out.println("Rocketalk--removeAddeBuddy---"+rem1);
			//			String nonFriends = rem1.substring(0, rem1.indexOf(".")) ;
			//			System.out.println("Rocketalk--removeAddeBuddy---"+nonFriends.trim());
			Cursor infoCursor = context. getContentResolver().query(
					MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
					null,
					MessageTable.CONVERSATION_ID + " = ? and "
							+ MessageTable.USER_ID + " = ?",
							new String[] {
							conversationId,
							BusinessProxy.sSelf.getUserId() + "" }, null);
			infoCursor.moveToLast();


			String s = infoCursor.getString(infoCursor
					.getColumnIndex(MessageTable.PARTICIPANT_INFO));

			//		System.out.println("Rocketalk--removeAddeBuddy--stored buddy-"+s);

			infoCursor.close();

			String userNAmes =nonFriends;// "nonfreind5,nonfreind1,nonfreind2,nonfreind3" ;
			String sst =s;//"false§nonfreind1§Non§Freindµfalse§nonfreind2§Non§Freindµfalse§nonfreind3§Non§Freindµfalse§nonfreind4§Non§Freindµfalse§nonfreind5§Non§Freindµ";//"false§amita§Amita§Tandonµfalse§forgot§For§Gotµtrue§mamtgupta§Mamt§Guptaµfalse§tariqfzb§Mohammad§Tariqµfalse§spider§Spi§Derµfalse§zeenat§Zeenat§Khanµfalse§adgdgj§adg§dgjµfalse§aniljha§anil§jhaµfalse§wq§§µ" ;

			String ssss[] = userNAmes.split(",") ;
			for (int i = 0; i < ssss.length; i++) {

				String userNAme = ssss[i] ;
				String searchs1 = ("false"+Constants.COL_SEPRETOR+userNAme).toLowerCase();
				String searchs2 = ("true"+Constants.COL_SEPRETOR+userNAme).toLowerCase();

				String oriS = sst ;
				sst = sst.toLowerCase();

				if(sst.indexOf(searchs1)!= -1){
					//			System.out.println("Rocketalk--removeAddeBuddy----search 1------");
					//		System.out.println(sst.indexOf(searchs1));
					String part1 = sst.substring(0,sst.indexOf(searchs1)) ;
//					System.out.println(part1);
					String part2 =sst.substring(sst.indexOf(searchs1),sst.length()) ;
//					System.out.println(part2);
					String part3 =part2.substring(part2.indexOf(Constants.ROW_SEPRETOR)+1,part2.length()) ;
//					System.out.println(part3);

					//		System.out.println("sst--:"+sst);
					sst = part1+part3 ;

				}
			}
			//		System.out.println("Rocketalk--removeAddeBuddy---new parti--:"+sst);
			return sst ;
		}catch (Exception e) {
			// TODO: hanle exception
			e.printStackTrace();
			return null;
		}
		//		return nonFriends;
	}

	public void saveBookmarkMessage(Message message){
		totMsg++;
		//		if(isAvoidableSource(message.source))
		//			return;

		long st = System.currentTimeMillis() ;
		ContentValues values = new ContentValues();
		values.put(MessageTable.USER_ID, BusinessProxy.sSelf.getUserId());//int
		values.put(MessageTable.PARENT_ID, message.parent_id);
		values.put(MessageTable.CLIENTTRANSACTION_ID, message.clientTransactionId);
		values.put(MessageTable.CONTENT_BITMAP, message.contentBitMap);
		values.put(MessageTable.CONVERSATION_ID, message.conversationId);
		values.put(MessageTable.DATE_TIME, message.datetime);
		values.put(MessageTable.DRM_FLAGS, message.drmFlags);//int
		values.put(MessageTable.HAS_BEEN_VIEWED, message.hasBeenViewed);
		values.put(MessageTable.HAS_BEEN_VIEWED_BY_SIP, message.hasBeenViewedBySIP);
		values.put(MessageTable.MESSAGE_ID, message.messageId);
		values.put(MessageTable.MFU_ID, message.mfuId);
		values.put(MessageTable.MSG_OP, message.msgOp);//int
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
		values.put(MessageTable.SUBJECT, message.subject);
		values.put(MessageTable.GROUP_PIC, message.imageFileId);
		values.put(MessageTable.TAG, message.tag);
		values.put(MessageTable.USMId, message.usmId);
		values.put(MessageTable.IS_NEXT, message.isNext);
		values.put(MessageTable.INSERT_TIME, System.currentTimeMillis());
		values.put(MessageTable.SORT_TIME, message.inserTime);//System.currentTimeMillis());

		values.put(MessageTable.IMAGE_CONTENT_THUMB_URLS, message.image_content_thumb_urls);
		values.put(MessageTable.IMAGE_CONTENT_URLS, message.image_content_urls);

		values.put(MessageTable.VIDEO_CONTENT_THUMB_URLS, message.video_content_thumb_urls);
		//		values.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);

		values.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);
		values.put(MessageTable.ANI_TYPE, message.aniType);
		values.put(MessageTable.ANI_VALUE, message.aniValue);
		////
		if(message.image_content_thumb_urls !=null && message.image_content_thumb_urls.endsWith(Constants.ROW_SEPRETOR))
			message.image_content_thumb_urls = message.image_content_thumb_urls.substring(0, message.image_content_thumb_urls.length()-1) ;
		if(message.image_content_urls !=null && message.image_content_urls.endsWith(Constants.ROW_SEPRETOR))
			message.image_content_urls = message.image_content_urls.substring(0, message.image_content_urls.length()-1) ;

		if(message.image_size !=null && message.image_size.endsWith(Constants.ROW_SEPRETOR))
			message.image_size = message.image_size.substring(0, message.image_size.length()-1) ;

		if(message.audio_size !=null && message.audio_size.endsWith(Constants.ROW_SEPRETOR))
			message.audio_size = message.audio_size.substring(0, message.audio_size.length()-1) ;


		values.put(MessageTable.MESSAGE_TYPE, message.messageType);
		values.put(MessageTable.IMAGE_CONTENT_THUMB_URLS, message.image_content_thumb_urls);
		values.put(MessageTable.IMAGE_CONTENT_URLS, message.image_content_urls);
		values.put(MessageTable.IMAGE_SIZE, message.image_size);

		//ROW_SEPRETOR
		if(message.video_content_thumb_urls !=null && message.video_content_thumb_urls.endsWith(Constants.ROW_SEPRETOR))
			message.video_content_thumb_urls = message.video_content_thumb_urls.substring(0, message.video_content_thumb_urls.length()-1) ;
		if(message.video_content_urls !=null && message.video_content_urls.endsWith(Constants.ROW_SEPRETOR))
			message.video_content_urls = message.video_content_urls.substring(0, message.video_content_urls.length()-1) ;

		if(message.video_content_thumb_urls !=null && message.video_content_thumb_urls.endsWith(Constants.COL_SEPRETOR))
			message.video_content_thumb_urls = message.video_content_thumb_urls.substring(0, message.video_content_thumb_urls.length()-1) ;
		if(message.video_content_urls !=null && message.video_content_urls.endsWith(Constants.COL_SEPRETOR))
			message.video_content_urls = message.video_content_urls.substring(0, message.video_content_urls.length()-1) ;

		if(message.video_size !=null && message.video_size.endsWith(Constants.COL_SEPRETOR))
			message.video_size = message.video_size.substring(0, message.video_size.length()-1) ;
		if(message.video_content_urls !=null && message.video_content_urls.endsWith(Constants.COL_SEPRETOR))
			message.video_content_urls = message.video_content_urls.substring(0, message.video_content_urls.length()-1) ;

		//		message.video_content_urls =	"http://124.153.95.131/tejas/video/resource/repository/2011/01/31/10/3959016690211624960.mp4"+Constants.COL_SEPRETOR+"http://124.153.95.131/tejas/video/resource/repository/2011/01/31/10/3959016690211624960.mp4" ;
		//		message.video_content_thumb_urls =	"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg"+Constants.COL_SEPRETOR+"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/186254_776062717_1969485358_q.jpg" ;
		if(log)
			Log.i(TAG, "saveBookmarkMessage :: video thumb:"+message.video_content_thumb_urls);
		values.put(MessageTable.VIDEO_CONTENT_THUMB_URLS, message.video_content_thumb_urls);
		values.put(MessageTable.VIDEO_CONTENT_URLS, message.video_content_urls);
		values.put(MessageTable.VIDEO_SIZE, message.video_size);

		//		values.put(MessageTable.DIRECTION, message.);
		//		values.put(MessageTable.INSERT_TIME, message.inserTime);//System.currentTimeMillis());
		//		int ind = getRandomNumberBetween(0, a.length-1) ;

		if(message.voice_content_urls !=null && message.voice_content_urls.endsWith(Constants.ROW_SEPRETOR))
			message.voice_content_urls = message.voice_content_urls.substring(0, message.voice_content_urls.length()-1) ;

		values.put(MessageTable.VOICE_CONTENT_URLS, message.voice_content_urls);
		if(message.voice_content_urls!=null){
			values.put(MessageTable.VOICE_CONTENT_URLS, message.voice_content_urls);
			values.put(MessageTable.AUDIO_ID, message.voice_content_urls.hashCode());
			values.put(MessageTable.AUDIO_SIZE, message.audio_size);
			if(Downloader.getInstance().check(
					message.voice_content_urls)){
				values.put(MessageTable.AUDIO_DOWNLOAD_STATUE, 1);
			}
		}
		/////


		if (message.video_content_urls != null) {
			String s[] = Utilities.split(new StringBuffer(message.video_content_urls), Constants.COL_SEPRETOR) ;
			values.put(MessageTable.VIDEO_CONTENT_URLS,
					message.video_content_urls);
			for (int i = 0; i < s.length; i++) {
				values.put("VIDEO_ID_"+(i+1), s[i].hashCode());
				if (Downloader.getInstance().check(s[i])) {
					values.put("VIDEO_DOWNLOAD_STATUE_"+(i+1), 1);
				}
			}
		}

		values.put(MessageTable.IS_BOOKMARK, 1);

		//		values.put(MessageTable.DIRECTION, message.);
		//		values.put(MessageTable.INSERT_TIME, message.inserTime);
		int ind = getRandomNumberBetween(0, a.length-1) ;
		if(message.voice_content_urls!=null){
			values.put(MessageTable.VOICE_CONTENT_URLS, message.voice_content_urls);
			values.put(MessageTable.AUDIO_ID, message.voice_content_urls.hashCode());
		}
		if(log)
			Log.i(TAG, "saveBookmarkMessage :: participantInfoStr 389: "+message.participantInfoStr);
		values.put(MessageTable.PARTICIPANT_INFO, message.participantInfoStr);

		//		System.out.println("Feed Synch --------------message values----------"+message.toString());
		if(message.messageId==null){
			return;
		}
		Cursor cursor = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_BOOKMARK, null, MessageTable.MESSAGE_ID+" = ? ", new String[] {message.messageId}, null);
		int count = cursor.getCount() ;
		//		count=0;//cheate
		if(log)
			Log.i(TAG, "saveBookmarkMessage :: cursor count FEED_GET_BOOKMARK_MESSAGES message list is message id available----------"+count);
		cursor.close();
		if(count <=0)
		{
			//			System.out.println("----------------------values : "+ values.toString());
			Uri u =  context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_BOOKMARK,
					values);
			//			
			//			Cursor cursor2 = context. getContentResolver().query(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST, null,
			//					MessageConversationTable.CONVERSATION_ID+" = ? ", new String[] {message.conversationId}, null);
			//			cursor2.close();

		}else
		{
			if(log)
				Log.i(TAG, "saveBookmarkMessage :: returing because message id is already present in DB------ :"+message.messageId );
			return ;
		}

		if(log)
			Log.i(TAG, "saveBookmarkMessage :: cursor count message list----------" + cursor.getCount());
		cursor.close() ;
		if(log)
			Log.i(TAG, "saveBookmarkMessage :: time taken to save message ----------"+(System.currentTimeMillis()-st));
	}

	public static InputStream readRawTextFile(Context ctx, int resId)
	{
		InputStream inputStream = ctx.getResources().openRawResource(resId);
		return inputStream;
	}

	public boolean isAvoidableSource(String s){
		if(log)
			Log.i(TAG, "isAvoidableSource :: isAvoidableSource--"+s);
		if(s!=null && (s.indexOf("a:tickermgr")!=-1 ))
		{
			return  true;
		}
		return false ;
	}
	//	public static final byte DRM_DONOTFORWARD = 1 << 0;
	//	public static final byte DRM_DONOTREPLY = 1 << 1;
	//	public static final byte DRM_DONOTSAVECONTENTS = 1 << 2;
	//	public static final byte DRM_EXPIRES = 1 << 3;
	//	public static final byte DRM_AUTOPLAY = 1 << 4;
	//	public static final byte DRM_PRIVATE_MSG = 1 << 5;
	//	public static final byte DRM_SYSTEM_MSG = 1 << 6;
	//	public static final int DRM_DONOT_VIEW_PROFILE = 1 << 7;
	//	// ----------------------------------------------------------------
	//	public static final byte NOTI_MEDIA_MESSAGE = 1 << 0;
	//	// public static final byte NOTI_NOTIFYONVIEWING = 1 << 1;
	//	public static final int NOTI_ROCKET_BUZZ = 1 << 1;
	//	public static final int NOTI_ROCKET_BUZZ_REPLY = 1 << 2;
	//	// public static final byte NOTI_NOTIFYONDELETION = 1 << 2;
	//	public static final byte NOTI_NOTIFYONSAVINGOFCONTENT = 1 << 3;
	//	public static final byte NOTI_SAVEDMSG = 1 << 4;
	//	public static final int NOTI_AUTO_REPLY = 1 << 5;
	//	public static final int NOTI_BUZZ_AUTOPLAY = 1 << 11;
	//	// 1 << 12 is assigned to Icelib.
	//	public static final int NOTI_CHAT_MSG = 1 << 13;
	//	public static final int NOTI_NON_FRIEND_MSG = 1 << 14;//For Cover page disabling
	//	public static final int NOTI_FRIEND_INVITATION_MSG = 1 << 15;
	////	public static final int NOTI_FRIEND_INVITATION_MSG = 1 << 16;

	public boolean isFriendRequest(int drm,int notification){
		if(log)
			Log.i(TAG, "isFriendRequest :: SAVING MESSAGERocketalk-----isFriendRequest-------drm :"+drm+" notification"+notification);
		if (drm==7 && notification==32768) {
			if(log)
				Log.i(TAG, "isFriendRequest :: SAVING MESSAGERocketalk-----isFriendRequest-------YES");
			return true;
		}
		return false;
	}

	public boolean isSystemMessagess(int drm,int notification){
		if(log)
			Log.i(TAG, "isSystemMessagess :: SAVING MESSAGERocketalk-----isSystemMessahe-------drm :"+drm+" notification"+notification);
		if (0 < (drm & InboxTblObject.DRM_SYSTEM_MSG) ) {
			//			FeedRequester.systemMessage++ ;
			if(log)
				Log.i(TAG, "isSystemMessagess :: SAVING MESSAGERocketalk-----isSystemMessahe-------YES");
			return true;
		}
		return false;
	}

	public String getParticepent(NodeList participants, int messageType){



		//		 participants = e2.getElementsByTagName("participants");
		String participantInfoStr = "" ;
		Vector<String> p = new Vector<String>() ;
		String willReplace = "" ;
		boolean willReplaceBool = false ;
		boolean im = false ;
		//		for (int i = 0; i < participants.getLength(); i++) 
		{
			Element eParticipants = (Element) participants.item(participants.getLength()-1);
			NodeList participantInfo = eParticipants.getElementsByTagName("participantInfo");

			for (int ij = 0; ij < participantInfo.getLength(); ij++) {
				Element eParticipantInfo = (Element) participantInfo.item(ij);

				if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) && XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
					im= true;



				if((messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION 
						|| messageType==Message.MESSAGE_TYPE_UPDATE_SUBJECT 
						|| messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION) 
						&& XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
				{
					if(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()))
						if(messageType==Message.MESSAGE_TYPE_LEAVE_CONVERSATION )
							//						conversationList.isLeft = 1 ;
							updateParticipantInfoUI = true ;
					continue ;
				}

				if(messageType==Message.MESSAGE_TYPE_ADD_TO_CONVERSATION && XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase().equalsIgnoreCase(SettingData.sSelf.getUserName().toString()) )//&& XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true"))
				{
					//					conversationList.isLeft = 0 ;
					updateParticipantInfoUI = true ;
				}

				if(p.contains(XMLUtils.getValue(eParticipantInfo, "userName").toLowerCase())){
					//					willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
					//					willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
					//					willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
					//					willReplace = willReplace + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
					////					04-25 12:42:18.182: I/System.out(18561): ---------participantInfoStr 2: false§qts§Qts§Sµ
					//
					//					if(XMLUtils.getValue(eParticipantInfo, "sender").equalsIgnoreCase("true")){
					//						String willReplace2 = willReplace.replaceFirst("true", "false") ;
					////						System.out.println("---------participantInfoStr 2willReplace  : "+willReplace);
					////						System.out.println("---------participantInfoStr 2willReplace2  : "+willReplace2);
					////						System.out.println("---------participantInfoStr 2participantInfoStr  : "+participantInfoStr);
					//						if(log)
					//						System.out.println(participantInfoStr.indexOf(willReplace2));
					//						
					//						participantInfoStr = participantInfoStr.toLowerCase() ;
					//						willReplace2 = willReplace2.toLowerCase() ;
					//						willReplace = willReplace.toLowerCase() ;
					//						
					//						participantInfoStr = participantInfoStr.replaceFirst(willReplace2, willReplace) ;
					//					}
					//					//	willReplaceBool = true ;
					////					
					////					System.out.println("---------participantInfoStr 2 p participantInfoStr: "+participantInfoStr);
					////					System.out.println("---------participantInfoStr 2 p willReplace: "+willReplace);
					////					
					participantInfoStr = participantInfoStr + "true"+Constants.COL_SEPRETOR;
					participantInfoStr = participantInfoStr + SettingData.sSelf.getUserName()+Constants.COL_SEPRETOR;
					participantInfoStr = participantInfoStr + SettingData.sSelf.getFirstName()+Constants.COL_SEPRETOR;
					participantInfoStr = participantInfoStr + SettingData.sSelf.getLastName()+Constants.ROW_SEPRETOR;
					continue;
				}

				p.add(XMLUtils.getValue(eParticipantInfo, "userName"));

				participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "sender")+Constants.COL_SEPRETOR;
				participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "userName")+Constants.COL_SEPRETOR;
				participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "firstName")+Constants.COL_SEPRETOR;
				participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "lastName")+Constants.ROW_SEPRETOR;
				//				participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userId")+Constants.COL_SEPRETOR;
				//				participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "userName")+Constants.COL_SEPRETOR;
				//				participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileImageUrl")+Constants.COL_SEPRETOR;
				//				participantInfoStr = participantInfoStr + XMLUtils.getValue(eParticipantInfo, "profileUrl")+Constants.ROW_SEPRETOR;
				//				participantInfoStr = participantInfoStr + XMLUtils.getValue(e2, "sender")+Constants.ROW_SEPRETOR;
			}
		}
		if(participantInfoStr.indexOf("false§§§")!= -1){
			participantInfoStr =participantInfoStr.replaceAll("false§§§", "");
		}
		return participantInfoStr;
	}
	public static void writeToFile(String stacktrace) {
		//		File file = new File("/sdcard/left_menu2.txt");
		//		if (!file.exists()) {
		//			try {
		//				file.createNewFile();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		try {
		//			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
		//					true));
		//			writer.write((new Date()).toLocaleString());
		//			writer.write(stacktrace+"\n");
		//			writer.flush();
		//			writer.close();
		//		} catch (FileNotFoundException e) {
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}
	public static void writeToFileMessageList(String stacktrace) {
		//		System.out.println(stacktrace);
		//		File file = new File("/sdcard/messageThread.txt");
		//		if (!file.exists()) {
		//			try {
		//				file.createNewFile();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		try {
		//			BufferedWriter writer = new BufferedWriter(new FileWriter(file,
		//					true));
		//			writer.write((new Date()).toLocaleString());
		//			writer.write(stacktrace+"\n");
		//			writer.flush();
		//			writer.close();
		//		} catch (FileNotFoundException e) {
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
	}

	public void loadLeftProfile(String userThumbUrl){
		try{

			if(userThumbUrl==null)return ;
			ThumbnailReponseHandler handler = new ThumbnailReponseHandler() {

				@Override
				public void onThumbnailResponse(
						ThumbnailImage value, byte[] data) {
					//					System.out.println("------thumb loaded----");
				}
			};
			ImageDownloader imageManager = new ImageDownloader();
			//			System.out.println("------thumb loading...----"+userThumbUrl);
			imageManager.download(userThumbUrl, new CImageView(context),
					handler, ImageDownloader.TYPE_THUMB_BUDDY);

		}catch (Exception e) {
		}
	}

	public void saveMediaUserLike(MediaLikeDislike likeDislike){
		ContentValues values = new ContentValues();
		values.put(MediaLikeUserTable.USERID, likeDislike.userId);
		values.put(MediaLikeUserTable.USERDISPLAYNAME, likeDislike.userDisplayName);	
		values.put(MediaLikeUserTable.USERNAME, likeDislike.userName);	
		values.put(MediaLikeUserTable.USERTHUMBURL, likeDislike.userThumbUrl);	
		values.put(MediaLikeUserTable.PROFILEURL, likeDislike.profileUrl);
		values.put(MediaLikeUserTable.NEXT_URL, likeDislike.nextUrl);
		values.put(MediaLikeUserTable.PREV_URL, likeDislike.prevUrl);
		values.put(MediaLikeUserTable.TIMESTAMP, likeDislike.timestamp);

		int mRowsUpdated =
				context.getContentResolver().delete(MediaContentProvider.CONTENT_URI_LIKES,
						MediaLikeUserTable.USERID+"='"+ likeDislike.userId+"'", null);
		if(log)	 
			Log.i(TAG, "saveMediaUserLike :: media userlike delete row count media----------"+mRowsUpdated+"  likeDislike.userId: "+likeDislike.userId);			
		Uri uri =	 context.getContentResolver().insert(MediaContentProvider.CONTENT_URI_LIKES,
				values);			
		if(log)
			Log.i(TAG, "saveMediaUserLike :: media userlike uri row count media----------"+uri.toString());
	}

	public static long gettimemillis(String datetime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long convertedtime = TimeZone.getDefault().getRawOffset();
		if(datetime.indexOf('/') != -1)
		{
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//24/01/2015 00:38:52
			convertedtime = 0;
		}
		Date date = null;
		//2015-01-06T17:45:15-05:00, 2015-01-23 18:37:30
		if(datetime == null)
			return convertedtime;
		if(datetime.length() > 19)
		{
			if(datetime.lastIndexOf('-') != -1)
				datetime = datetime.substring(0, datetime.lastIndexOf('-'));
			else if(datetime.lastIndexOf('+') != -1)
				datetime = datetime.substring(0, datetime.lastIndexOf('+'));
			if(datetime.indexOf('T') != -1)
				datetime = datetime.replace('T', ' ');
		}

		try {
			date = sdf.parse(datetime);
//			System.out.println(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(date != null)
			convertedtime += date.getTime();
		return convertedtime;
	}
//------------------------------------------
	private class GetMUCData extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			String responseStr = null;
			String url = "http://"+Urls.TEJAS_HOST+"/tejas/rest/rtmessaging/getConversationInfo/$USERID$/$CID$?";
			if(urls != null && urls[0] != null)
				url = urls[0];
			DefaultHttpClient client = new DefaultHttpClient();
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params,3000);
			HttpGet httpget = new HttpGet(url);
			httpget.setHeader("authkey", "RTAPP@#$%!@");
			httpget.setHeader("password", "AAAABl26u1EIeTIz");

			try {
				HttpResponse responseHttp = client.execute(httpget);
				if (responseHttp != null) {
					responseStr = EntityUtils.toString(responseHttp.getEntity());
				}
			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}
			return responseStr;
		} 

		@Override
		protected void onPostExecute(String response) {
			Log.i(TAG, "GetMUCData :: onPostExecute :: response : "+response);
			String imageFileId = null;
			String con_id = null;
			String subject = null;
			//update Subject and Image in DB
			ContentValues values = new ContentValues();
			if(response != null && response.contains("<conversationId>")){
				con_id = response.substring(response.indexOf("<conversationId>") + "<conversationId>".length(), response.indexOf("</conversationId>"));
			}
			if(response != null && response.contains("<imageFileId>")){
				imageFileId = response.substring(response.indexOf("<imageFileId>") + "<imageFileId>".length(), response.indexOf("</imageFileId>"));
			}
			if(response != null && response.contains("<subject>")){
				subject = response.substring(response.indexOf("<subject>") + "<subject>".length(), response.indexOf("</subject>"));
			}
			//Update subject and Pic to DB
			if(subject != null)
				values.put(MessageConversationTable.SUBJECT, StringEscapeUtils.unescapeXml(subject));
			if(imageFileId != null)
				values.put(MessageConversationTable.GROUP_PIC,imageFileId);
			if(subject != null || imageFileId != null){
				Log.i(TAG, "GetMUCData :: onPostExecute :: Updating Values  : "+values.toString());
				int updatedRow = context.getContentResolver()
						.update(MediaContentProvider.CONTENT_URI_INBOX_CONVERSATION_LIST,
								values,
								MessageConversationTable.CONVERSATION_ID
								+ "= ? and "
								+ MessageConversationTable.USER_ID
								+ " =?",
								new String[] {
								con_id,
								""+ BusinessProxy.sSelf
										.getUserId() });
			}

			}
		}
}